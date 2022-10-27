package com.kcteam.features.addAttendence

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.MySingleton
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.LeaveTypeEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addAttendence.api.addattendenceapi.AddAttendenceRepoProvider
import com.kcteam.features.addAttendence.api.leavetytpeapi.LeaveTypeRepoProvider
import com.kcteam.features.addAttendence.model.*
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 05-Aug-20.
 */
class ApplyLeaveFragment : BaseFragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mContext: Context

    private lateinit var tv_show_date_range: AppCustomTextView
    private lateinit var rl_leave_type_header: RelativeLayout
    private lateinit var tv_leave_type: AppCustomTextView
    private lateinit var iv_leave_type_dropdown: ImageView
    private lateinit var ll_leave_type_list: LinearLayout
    private lateinit var rv_leave_type_list: RecyclerView
    private lateinit var et_leave_reason_text: AppCustomEditText
    private lateinit var tv_submit: AppCustomTextView
    private lateinit var rl_leave_main: RelativeLayout
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel

    private var startDate = ""
    private var endDate = ""
    private var leaveId = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_apply_leave, container, false)

        initView(view)
        initClickListener()

        return view
    }

    private fun initView(view: View) {
        tv_show_date_range = view.findViewById(R.id.tv_show_date_range)
        rl_leave_type_header = view.findViewById(R.id.rl_leave_type_header)
        tv_leave_type = view.findViewById(R.id.tv_leave_type)
        iv_leave_type_dropdown = view.findViewById(R.id.iv_leave_type_dropdown)
        ll_leave_type_list = view.findViewById(R.id.ll_leave_type_list)
        rv_leave_type_list = view.findViewById(R.id.rv_leave_type_list)
        rv_leave_type_list.layoutManager = LinearLayoutManager(mContext)
        et_leave_reason_text = view.findViewById(R.id.et_leave_reason_text)
        tv_submit = view.findViewById(R.id.tv_submit)
        rl_leave_main = view.findViewById(R.id.rl_leave_main)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        checkForLeaveTypeData()

        et_leave_reason_text.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }

            false
        })

        openDateRangeCalendar()

        if (Pref.willLeaveApprovalEnable)
            tv_submit.text = getString(R.string.send_for_approval)
        else
            tv_submit.text = getString(R.string.submit_button_text)
    }

    private fun openDateRangeCalendar() {
        val now = Calendar.getInstance(Locale.ENGLISH)
        now.add(Calendar.DAY_OF_MONTH, +1)
        val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.isAutoHighlight = true
        val tomorrowsDateLong = Calendar.getInstance(Locale.ENGLISH).timeInMillis + (1000 * 60 * 60 * 24)
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = tomorrowsDateLong
        dpd.minDate = cal
        dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
    }

    private fun checkForLeaveTypeData() {
        if (AppDatabase.getDBInstance()?.leaveTypeDao()?.getAll()!!.isEmpty())
            getLeaveTypeList()
        else {
            setLeaveTypeAdapter(AppDatabase.getDBInstance()?.leaveTypeDao()?.getAll() as ArrayList<LeaveTypeEntity>)
        }
    }

    private fun getLeaveTypeList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = LeaveTypeRepoProvider.leaveTypeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getLeaveTypeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as LeaveTypeResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.leave_type_list

                                if (list != null && list.size > 0) {
                                    doAsync {

                                        for (i in list.indices) {

                                            val leave = LeaveTypeEntity()
                                            leave.id = list[i].id?.toInt()!!
                                            leave.leave_type = list[i].type_name
                                            AppDatabase.getDBInstance()?.leaveTypeDao()?.insert(leave)
                                        }

                                        uiThread {
                                            setLeaveTypeAdapter(AppDatabase.getDBInstance()?.leaveTypeDao()?.getAll() as ArrayList<LeaveTypeEntity>)
                                            progress_wheel.stopSpinning()
                                        }
                                    }
                                } else
                                    progress_wheel.stopSpinning()
                            } else
                                progress_wheel.stopSpinning()
                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }

    private fun setLeaveTypeAdapter(leaveTypeList: ArrayList<LeaveTypeEntity>?) {
        rv_leave_type_list.adapter = LeaveTypeListAdapter(mContext, leaveTypeList!!, object : LeaveTypeListAdapter.OnLeaveTypeClickListener {
            override fun onLeaveTypeClick(leaveType: LeaveTypeEntity?, adapterPosition: Int) {
                tv_leave_type.text = leaveType?.leave_type
                (mContext as DashboardActivity).leaveType = leaveType?.leave_type!!
                leaveId = leaveType.id.toString()
                ll_leave_type_list.visibility = View.GONE
                iv_leave_type_dropdown.isSelected = false
            }
        })
    }

    private fun initClickListener() {
        tv_submit.setOnClickListener(this)
        rl_leave_type_header.setOnClickListener(this)
        rl_leave_main.setOnClickListener(null)
        tv_show_date_range.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {

            R.id.tv_submit -> {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                visibilityCheck()
            }

            R.id.rl_leave_type_header -> {
                if (iv_leave_type_dropdown.isSelected) {
                    iv_leave_type_dropdown.isSelected = false
                    ll_leave_type_list.visibility = View.GONE
                } else {
                    iv_leave_type_dropdown.isSelected = true
                    ll_leave_type_list.visibility = View.VISIBLE
                }
            }

            R.id.tv_show_date_range -> {
                openDateRangeCalendar()
            }
        }
    }

    private fun visibilityCheck() {
        if (TextUtils.isEmpty(leaveId))
            (mContext as DashboardActivity).showSnackMessage("Please select leave type")
        else if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate))
            (mContext as DashboardActivity).showSnackMessage("Please select date range")
        else if (Pref.willLeaveApprovalEnable && TextUtils.isEmpty(et_leave_reason_text.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage("Please enter reason")
        else{
            //callLeaveApprovalApi()
            calculateDaysForLeave()
            //callLeaveApiForUser()
        }
    }

    var dateList:ArrayList<String> = ArrayList()
    var count=0

    @SuppressLint("NewApi")
    private fun calculateDaysForLeave(){
        val stDate = LocalDate.parse(startDate)
        val enDate = LocalDate.parse(endDate)
        val diff = Period.between(stDate,enDate)


        dateList.add(startDate)
        var countDate=stDate

        for(i in 0..diff.days-1){
            countDate=countDate.plusDays(1)
            dateList.add(countDate.toString())
        }
        //for(j in 0..dateList.size-1){
            callLeaveApiForUser()
        //}
    }

    private fun callLeaveApiForUser(){

        var stDate=dateList.get(count).toString()
        var enDate=dateList.get(count).toString()

        var addAttendenceModel: AddAttendenceInpuModel = AddAttendenceInpuModel()
        addAttendenceModel.user_id=Pref.user_id.toString()
        addAttendenceModel.add_attendence_time=AppUtils.getCurrentTimeWithMeredian()
        addAttendenceModel.collection_taken="0"
        addAttendenceModel.distance=""
        addAttendenceModel.distributor_name=""
        addAttendenceModel.from_id=""
        addAttendenceModel.is_on_leave="true"
        addAttendenceModel.leave_from_date=stDate
        addAttendenceModel.leave_to_date=enDate
        //addAttendenceModel.work_date_time=AppUtils.getCurrentDateTime()
        addAttendenceModel.work_date_time=stDate + " "+AppUtils.getCurrentTime()

        var mLeaveReason=""
        if (!TextUtils.isEmpty(et_leave_reason_text.text.toString().trim()))
            mLeaveReason = et_leave_reason_text.text.toString().trim()

        addAttendenceModel.leave_reason=mLeaveReason
        addAttendenceModel.leave_type=leaveId
        addAttendenceModel.market_worked=""
        addAttendenceModel.new_shop_visit="0"
        addAttendenceModel.order_taken="0"

        addAttendenceModel.revisit_shop="0"
        addAttendenceModel.route=""
        addAttendenceModel.session_token=""
        addAttendenceModel.work_lat=Pref.current_latitude
        addAttendenceModel.work_long=Pref.current_longitude
        addAttendenceModel.beat_id = "0"

        val repository = AddAttendenceRepoProvider.addAttendenceRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.addAttendence(addAttendenceModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    progress_wheel.stopSpinning()
                    val response = result as BaseResponse
                    if (response.status == NetworkConstant.SUCCESS) {
                        if(count==(dateList.size-1)){
                            count=0
                        callLeaveApprovalApi()
                        }else{
                            count++
                            callLeaveApiForUser()
                        }
                    } else {
                        BaseActivity.isApiInitiated = false
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                    }
                    Log.e("ApprovalPend work attendance", "api work type")

                }, { error ->
                    XLog.d("AddAttendance Response Msg=========> " + error.message)
                    BaseActivity.isApiInitiated = false
                    progress_wheel.stopSpinning()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                })
        )

    }

    private fun callLeaveApprovalApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        var stDate=dateList.get(count).toString()
        var enDate=dateList.get(count).toString()

        val leaveApproval = SendLeaveApprovalInputParams()
        leaveApproval.session_token = Pref.session_token!!
        leaveApproval.user_id = Pref.user_id!!
        leaveApproval.leave_from_date = stDate
        leaveApproval.leave_to_date = enDate
        leaveApproval.leave_type = leaveId

        var tt=AppUtils.getCurrentDateTime()

        if (TextUtils.isEmpty(Pref.current_latitude))
            leaveApproval.leave_lat = "0.0"
        else
            leaveApproval.leave_lat = Pref.current_latitude

        if (TextUtils.isEmpty(Pref.current_longitude))
            leaveApproval.leave_long = "0.0"
        else
            leaveApproval.leave_long = Pref.current_longitude

        if (TextUtils.isEmpty(Pref.current_latitude))
            leaveApproval.leave_add = ""
        else
            leaveApproval.leave_add = LocationWizard.getLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())


        if (!TextUtils.isEmpty(et_leave_reason_text.text.toString().trim()))
            leaveApproval.leave_reason = et_leave_reason_text.text.toString().trim()

        XLog.d("=========Apply Leave Input Params==========")
        XLog.d("session_token======> " + leaveApproval.session_token)
        XLog.d("user_id========> " + leaveApproval.user_id)
        XLog.d("leave_from_date=======> " + leaveApproval.leave_from_date)
        XLog.d("leave_to_date=======> " + leaveApproval.leave_to_date)
        XLog.d("leave_type========> " + leaveApproval.leave_type)
        XLog.d("leave_lat========> " + leaveApproval.leave_lat)
        XLog.d("leave_long========> " + leaveApproval.leave_long)
        XLog.d("leave_add========> " + leaveApproval.leave_add)
        XLog.d("leave_reason========> " + leaveApproval.leave_reason)
        XLog.d("===============================================")

        val repository = AddAttendenceRepoProvider.leaveApprovalRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.sendLeaveApproval(leaveApproval)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse
                            XLog.d("Apply Leave Response Code========> " + response.status)
                            XLog.d("Apply Leave Response Msg=========> " + response.message)
                            BaseActivity.isApiInitiated = false

                            if (response.status == NetworkConstant.SUCCESS) {
                                if(count==(dateList.size-1)){
                                    count=0
                                    openPopupshowMessage(response.message!!)
                                }else{
                                    count++
                                    callLeaveApprovalApi()

                                }

//                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                //(mContext as DashboardActivity).onBackPressed()

                            } else {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            XLog.d("Apply Leave Response ERROR=========> " + error.message)
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun openPopupshowMessage(message:String) {
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_message)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
        val dialogBody = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
        val obBtn = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
        dialogHeader.text="Hi "+Pref.user_name+"!"
        dialogBody.text = message
        obBtn.setOnClickListener({ view ->
            simpleDialog.cancel()
            //(mContext as DashboardActivity).loadFragment(FragType.LeaveListFragment, false, "")
            Handler().postDelayed(Runnable {
                if(Pref.Leaveapprovalfromsupervisor){
                    getSupervisorIDInfo()
                }else{
                    (mContext as DashboardActivity).onBackPressed()
                }
            }, 500)

        })
        simpleDialog.show()

    }

    override fun onDateSet(datePickerDialog: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int,
                           dayOfMonthEnd: Int) {

        datePickerDialog?.minDate = Calendar.getInstance(Locale.ENGLISH)
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd
        if (dayOfMonth < 10)
            day = "0$dayOfMonth"
        if (dayOfMonthEnd < 10)
            dayEnd = "0$dayOfMonthEnd"
        var fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear + 1).toString() + "") + "-" + year
        var endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd + 1).toString() + "") + "-" + yearEnd
        if (AppUtils.getStrinTODate(endString).before(AppUtils.getStrinTODate(fronString))) {
            (mContext as DashboardActivity).showSnackMessage("Your end date is before start date.")
            return
        }
        val date = "Leave: From " + day + AppUtils.getDayNumberSuffix(day.toInt()) + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + " " + year + " To " + dayEnd + AppUtils.getDayNumberSuffix(dayEnd.toInt()) + FTStorageUtils.formatMonth((++monthOfYearEnd).toString() + "") + " " + yearEnd
        tv_show_date_range.visibility = View.VISIBLE
        tv_show_date_range.text = date

        startDate = AppUtils.convertFromRightToReverseFormat(fronString)
        endDate = AppUtils.convertFromRightToReverseFormat(endString)
    }


    private fun getSupervisorIDInfo(){
        try{
            val repository = AddAttendenceRepoProvider.addAttendenceRepo()
            BaseActivity.compositeDisposable.add(
                    repository.getReportToUserID(Pref.user_id.toString(),Pref.session_token.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as GetReportToResponse

                                if (response.status == NetworkConstant.SUCCESS) {
                                    getSupervisorFCMInfo(response.report_to_user_id!!)
                                }

                            }, { error ->
                                XLog.d("Apply Leave Response ERROR=========> " + error.message)
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }

    private fun getSupervisorFCMInfo(usrID:String){
        try{
            val repository = AddAttendenceRepoProvider.addAttendenceRepo()
            BaseActivity.compositeDisposable.add(
                    repository.getReportToFCMInfo(usrID,Pref.session_token.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as GetReportToFCMResponse

                                if (response.status == NetworkConstant.SUCCESS) {
                                    sendFCMNotiSupervisor(response.device_token!!)
                                }

                            }, { error ->
                                XLog.d("Apply Leave Response ERROR=========> " + error.message)
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }

    private fun sendFCMNotiSupervisor(superVisor_fcmToken:String){
        if (superVisor_fcmToken != "") {
            try {
                val jsonObject = JSONObject()
                val notificationBody = JSONObject()
                notificationBody.put("body","Leave applied by : "+Pref.user_name!!)
                notificationBody.put("flag", "flag")
                notificationBody.put("applied_user_id",Pref.user_id)
                notificationBody.put("leave_from_date",startDate)
                notificationBody.put("leave_to_date",endDate)
                notificationBody.put("leave_reason",et_leave_reason_text.text.toString().trim())
                notificationBody.put("leave_type",tv_leave_type)
                notificationBody.put("leave_type_id",leaveId)
                jsonObject.put("data", notificationBody)
                val jsonArray = JSONArray()
                jsonArray.put(0,superVisor_fcmToken)
                jsonObject.put("registration_ids", jsonArray)
                sendCustomNotification(jsonObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    fun sendCustomNotification(notification: JSONObject) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                object : Response.Listener<JSONObject?> {
                    override fun onResponse(response: JSONObject?) {
                        (mContext as DashboardActivity).onBackPressed()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {

                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getString(R.string.firebase_key)
                params["Content-Type"] = "application/json"
                return params
            }
        }

        MySingleton.getInstance(mContext)!!.addToRequestQueue(jsonObjectRequest)
    }

}
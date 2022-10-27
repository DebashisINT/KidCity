package com.kcteam.features.photoReg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.attendance.AttendanceRecyclerViewAdapter
import com.kcteam.features.attendance.api.AttendanceRepositoryProvider
import com.kcteam.features.attendance.model.AttendanceRequest
import com.kcteam.features.attendance.model.AttendanceResponse
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.UserLoginDataEntity
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.photoReg.model.UserListResponseModel
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

class TeamAttendanceFragment: BaseFragment(), CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private var radioButtonDateRangePicker: AppCompatRadioButton? = null
    private var attandence_radio_button_last_fifteen_days: AppCompatRadioButton? = null
    private var mAutoHighlight: Boolean = false
    // public lateinit var context: Context
    private var dateRangeTv: TextView? = null
    private var recyclerView: RecyclerView? = null
    private var adapter: AttendanceRecyclerViewAdapter? = null
    private var isChkChanged = false
    private lateinit var mContext: Context
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var tv_frag_attend_team_name: AppCustomTextView
    private var attendanceList: MutableList<UserLoginDataEntity> = ArrayList()
    //    internal var userLoginDataEntityArr: ArrayList<UserLoginDataEntity>? = null
    var userLoginDataEntityArr: MutableList<UserLoginDataEntity> = ArrayList()
    private var isDateRangeSelected = false
    private lateinit var noDataText: AppCustomTextView

    companion object{
        var user_uid: String = ""
        var team_user_name: String = ""
        fun getInstance(objects: Any): TeamAttendanceFragment {
            val teamAttendanceFragment = TeamAttendanceFragment()
            if (objects is UserListResponseModel) {
                user_uid = objects.user_id.toString()
                team_user_name = objects.user_name.toString()
            }
            return teamAttendanceFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_attendance_team, container, false)
        initView(view)
        getAttendanceList()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun getAttendanceList() {
        val attendanceList = AppDatabase.getDBInstance()!!.userAttendanceDataDao().all as ArrayList<UserLoginDataEntity>
        if (/*attendanceList.size <= 1 &&*/ AppUtils.isOnline(mContext)) {
            //if (attendanceList.size == 1 && attendanceList[0].logindate != AppUtils.changeAttendanceDateFormat(AppUtils.getCurrentISODateAtt())) {
            //AppDatabase.getDBInstance()!!.userAttendanceDataDao().delete()
            //}
            val attendanceReq = AttendanceRequest()
            attendanceReq.user_id = user_uid// Pref.user_id
            attendanceReq.session_token = Pref.session_token
            attendanceReq.start_date = ""
            attendanceReq.end_date = ""
            callAttendanceListApi(attendanceReq)
        } else {
            /*if (!AppUtils.isOnline(mContext))
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))*/

            val list = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getAllSortedList() as ArrayList<UserLoginDataEntity>

            if (list != null && list.size > 0)
                initAdapter(list)
            else {
                noDataText.visibility = View.VISIBLE
                recyclerView!!.visibility = View.GONE
            }
        }

//        userLoginDataEntityArr = AppDatabase.getDBInstance()!!.userAttendanceDataDao().all as ArrayList<UserLoginDataEntity>
////        initAdapter(userLoginDataEntityArr as ArrayList<UserLoginDataEntity>)
//        if (userLoginDataEntityArr.isNotEmpty() && userLoginDataEntityArr.size > 1) {
//            initAdapter(userLoginDataEntityArr as ArrayList<UserLoginDataEntity>)
//        } else {
//            if (AppUtils.isOnline(mContext)) {
//                var attendanceReq = AttendanceRequest()
//                attendanceReq.user_id = Pref.user_id
//                attendanceReq.session_token = Pref.session_token
//                attendanceReq.start_date = ""
//                attendanceReq.end_date = ""
//                callAttendanceListApi(attendanceReq)
//            } else {
//                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
//            }
//        }
    }

    private fun callAttendanceListApi(attendanceReq: AttendanceRequest) {
        val repository = AttendanceRepositoryProvider.provideAttendanceRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getAttendanceList(attendanceReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val attendanceList = result as AttendanceResponse
                            if (attendanceList.status == NetworkConstant.SUCCESS) {
                                convertAndInitAdapter(attendanceList, attendanceReq)
                                //progress_wheel.stopSpinning()
//                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")
                            } else if (attendanceList.status == NetworkConstant.SESSION_MISMATCH) {
                                progress_wheel.stopSpinning()
//                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else if (attendanceList.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()

                                /*if (!TextUtils.isEmpty(attendanceReq.start_date) && !TextUtils.isEmpty(attendanceReq.end_date)) {
                                    val list = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getAllSortedList() as ArrayList<UserLoginDataEntity>
                                    if (list != null && list.size > 0)
                                        initAdapter(list)
                                    else {
                                        noDataText.visibility = View.VISIBLE
                                        recyclerView!!.visibility = View.GONE
                                    }
                                } else {*/
                                noDataText.visibility = View.VISIBLE
                                recyclerView!!.visibility = View.GONE
                                //}
                                (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
//                                initAdapter(AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!,AppUtils.getCurrentDateChanged()) as ArrayList<UserLoginDataEntity>)
                            } else {
                                progress_wheel.stopSpinning()

                                if (TextUtils.isEmpty(attendanceReq.start_date) && TextUtils.isEmpty(attendanceReq.end_date)) {
                                    val list = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getAllSortedList() as ArrayList<UserLoginDataEntity>
                                    if (list != null && list.size > 0)
                                        initAdapter(list)
                                    else {
                                        //(mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
                                        noDataText.visibility = View.VISIBLE
                                        recyclerView!!.visibility = View.GONE
                                    }
                                } else {
                                    //(mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
                                    noDataText.visibility = View.VISIBLE
                                    recyclerView!!.visibility = View.GONE
                                }

                                (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
                            }
//
                        }, { error ->
                            progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                            error.printStackTrace()

                            if (TextUtils.isEmpty(attendanceReq.start_date) && TextUtils.isEmpty(attendanceReq.end_date)) {
                                val list = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getAllSortedList() as ArrayList<UserLoginDataEntity>

                                if (list != null && list.size > 0)
                                    initAdapter(list)
                                else {
                                    //(mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                    noDataText.visibility = View.VISIBLE
                                    recyclerView!!.visibility = View.GONE
                                }
                            } else {
                                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                noDataText.visibility = View.VISIBLE
                                recyclerView!!.visibility = View.GONE
                            }

                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )

    }

    fun convertAndInitAdapter(attendanceList: AttendanceResponse, attendanceReq: AttendanceRequest) {
        doAsync {
            if (!isDateRangeSelected)
                AppDatabase.getDBInstance()!!.userAttendanceDataDao().delete()
            val result = runLongTask(attendanceList)
            uiThread {
                if (result == true) {
                    progress_wheel.stopSpinning()
//                    var v=result as
                    noDataText.visibility = View.GONE
                    recyclerView!!.visibility = View.VISIBLE
                    if (!isDateRangeSelected) {
                        userLoginDataEntityArr.clear()
                        userLoginDataEntityArr = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getAllSortedList() as ArrayList<UserLoginDataEntity>
                    }
                    initAdapter(userLoginDataEntityArr as ArrayList<UserLoginDataEntity>)
                }

            }
        }
    }

    private fun runLongTask(attendanceList: AttendanceResponse): Any {
        return convertToModelAndSave(attendanceList)

    }

    private fun convertToModelAndSave(attendanceList: AttendanceResponse): Boolean {
        var shoplist = attendanceList.shop_list
        var dbDataList = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getAllSortedList() as ArrayList<UserLoginDataEntity>
        userLoginDataEntityArr = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getAllSortedList() as ArrayList<UserLoginDataEntity>
        if (shoplist!!.isEmpty()) {
//            userLoginDataEntityArr = AppDatabase.getDBInstance()!!.userAttendanceDataDao().all as ArrayList<UserLoginDataEntity>
            if (dbDataList.isNotEmpty()) {
                initAdapter(dbDataList)
            }
            return true
        }

//        var userLoginDataEntityArr: MutableList<UserLoginDataEntity> = ArrayList()
        if (isDateRangeSelected) {
//            isDateRangeSelected=false
            userLoginDataEntityArr.clear()
            for (i in 0 until shoplist.size) {
                if (shoplist[i].login_date == AppUtils.getCurrentISODateAtt()) {
                    userLoginDataEntityArr = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!, AppUtils.getCurrentDateChanged()) as MutableList<UserLoginDataEntity>
                }
            }
        } else {
            userLoginDataEntityArr.clear()
            userLoginDataEntityArr = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!, AppUtils.getCurrentDateChanged()) as MutableList<UserLoginDataEntity>
        }

        for (i in 0 until shoplist.size) {
            val userLoginEntry = UserLoginDataEntity()
            if (shoplist[i].login_date == AppUtils.getCurrentISODateAtt()) {
                if (userLoginDataEntityArr.size > 0) {
                    for (j in 0 until userLoginDataEntityArr.size) {
                        if (userLoginDataEntityArr[j].logindate == AppUtils.getCurrentDateChanged()) {
                            userLoginDataEntityArr[j].logintime = AppUtils.convertTime(FTStorageUtils.getStringToDate(shoplist[i].login_time!!))
                            userLoginDataEntityArr[j].Isonleave = shoplist[i].Isonleave!!
                            AppDatabase.getDBInstance()!!.userAttendanceDataDao().updateLoginTime(AppUtils.convertTime(FTStorageUtils.getStringToDate(shoplist[i].login_time!!)), Pref.user_id!!, AppUtils.getCurrentDateChanged())
                            AppDatabase.getDBInstance()!!.userAttendanceDataDao().updateIsLeave(shoplist[i].Isonleave!!, Pref.user_id!!, AppUtils.getCurrentDateChanged())
                        }
                    }
                } else {
                    if (!isDateRangeSelected) {
                        userLoginEntry.logintime = AppUtils.convertTime(FTStorageUtils.getStringToDate(shoplist[i].login_time!!))
                        userLoginEntry.Isonleave = shoplist[i].Isonleave!!
                        userLoginEntry.logindate = AppUtils.changeAttendanceDateFormat(shoplist[i].login_date!!)
                        userLoginEntry.logindate_number = AppUtils.getTimeStampFromDateOnly(shoplist[i].login_date!!)
                        userLoginEntry.userId = Pref.user_id!!
                        if (!TextUtils.isEmpty(shoplist[i].logout_time))
                            userLoginEntry.logouttime = AppUtils.convertTime(FTStorageUtils.getStringToDate(shoplist[i].logout_time!!))
                        userLoginEntry.duration = shoplist[i].duration!!
                        AppDatabase.getDBInstance()!!.userAttendanceDataDao().insertAll(userLoginEntry)
                    }
                }
                continue
            }
            if (shoplist[i].login_time == null)
                continue
            userLoginEntry.logintime = AppUtils.convertTime(FTStorageUtils.getStringToDate(shoplist[i].login_time!!))
            if (shoplist[i].logout_time == null)
                continue
            userLoginEntry.logouttime = AppUtils.convertTime(FTStorageUtils.getStringToDate(shoplist[i].logout_time!!))
            userLoginEntry.userId = Pref.user_id!!
            userLoginEntry.duration = shoplist[i].duration!!
            if (shoplist[i].login_date == null)
                continue
            userLoginEntry.logindate = AppUtils.changeAttendanceDateFormat(shoplist[i].login_date!!)
            //userLoginEntry.logintime=shoplist[i].login_time!!
            //userLoginEntry.logouttime=shoplist[i].logout_time!!
            userLoginEntry.logindate_number = AppUtils.getTimeStampFromDateOnly(shoplist[i].login_date!!)

            userLoginEntry.Isonleave = shoplist[i].Isonleave!!

            userLoginDataEntityArr.add(userLoginEntry)
            if (!isDateRangeSelected)
                AppDatabase.getDBInstance()!!.userAttendanceDataDao().insertAll(userLoginEntry)

        }
        //initAdapter(userLoginDataEntityArr as ArrayList<UserLoginDataEntity>)
        //userLoginDataEntityArr.addAll(dbDataList)
        return true
        //userLoginDataEntityArr = AppDatabase.getDBInstance()!!.userAttendanceDataDao().all as ArrayList<UserLoginDataEntity>

    }

    private fun initAdapter(userLoginDataEntityArr: ArrayList<UserLoginDataEntity>) {
        recyclerView!!.visibility = View.VISIBLE

        if (userLoginDataEntityArr != null && userLoginDataEntityArr.size > 0)
            noDataText.visibility = View.GONE

        adapter = AttendanceRecyclerViewAdapter(mContext, userLoginDataEntityArr, AttendanceRecyclerViewAdapter.onScrollEndListener {
            val imageButton = view!!.findViewById<ProgressBar>(R.id.refresh_iv)
            imageButton.visibility = View.VISIBLE
            Handler().postDelayed({ imageButton.visibility = View.GONE }, 2000)
        })
        recyclerView!!.adapter = adapter
    }

    private fun initView(view: View) {
        progress_wheel = view.findViewById(R.id.progress_wheel)
        tv_frag_attend_team_name = view.findViewById(R.id.tv_frag_attend_team_name)
        tv_frag_attend_team_name.text= "Attendance Report for : "+team_user_name
        progress_wheel.stopSpinning()
        radioButtonDateRangePicker = view.findViewById(R.id.attendance_header_radio_button_date_range_picker)
        radioButtonDateRangePicker!!.setOnClickListener(this)
//        radioButtonDateRangePicker!!.setOnClickListener(this)
        dateRangeTv = view.findViewById(R.id.tv_show_date_range)
        recyclerView = view.findViewById(R.id.attendance_list_RCV)
        noDataText = view.findViewById(R.id.no_attendance_tv)
        noDataText.visibility = View.GONE
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        attandence_radio_button_last_fifteen_days = view.findViewById(R.id.attandence_radio_button_last_fifteen_days)
        attandence_radio_button_last_fifteen_days!!.setOnClickListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        mAutoHighlight = isChecked
        when (buttonView.id) {
            R.id.attendance_header_radio_button_date_range_picker -> {

                if (isChecked) {
                    isChkChanged = true
                    val now = Calendar.getInstance(Locale.ENGLISH)
                    val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                            this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.isAutoHighlight = mAutoHighlight
                    dpd.maxDate = Calendar.getInstance(Locale.ENGLISH)
                    dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
                }
            }
            R.id.attandence_radio_button_last_fifteen_days -> if (isChecked) {
//                if (adapter != null)
//                    adapter!!.notifyAdapter(userLoginDataEntityArr)
//                var count=AppDatabase.getDBInstance()!!.userAttendanceDataDao().deleteAll(AppUtils.getCurrentDateChanged())
                getAttendanceList()
                dateRangeTv!!.visibility = View.GONE

            }
        }
    }

    override fun onDateSet(datePickerDialog: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        datePickerDialog.maxDate = Calendar.getInstance(Locale.ENGLISH)
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth
        if (dayOfMonthEnd < 10)
            dayEnd = "0" + dayOfMonthEnd
        var fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear + 1).toString() + "") + "-" + year
        var endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd + 1).toString() + "") + "-" + yearEnd
        if (AppUtils.getStrinTODate(endString).before(AppUtils.getStrinTODate(fronString))) {
            (mContext as DashboardActivity).showSnackMessage("Your end date is before start date.")
            return
        }
        val date = "Attendance: From " + day + AppUtils.getDayNumberSuffix(day.toInt()) + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + " " + year + " To " + dayEnd + AppUtils.getDayNumberSuffix(dayEnd.toInt()) + FTStorageUtils.formatMonth((++monthOfYearEnd).toString() + "") + " " + yearEnd
        dateRangeTv!!.visibility = View.VISIBLE
        dateRangeTv!!.text = date
        isDateRangeSelected = true
        if (AppUtils.isOnline(mContext)) {
            val attendanceReq = AttendanceRequest()
            attendanceReq.user_id = Pref.user_id
            attendanceReq.session_token = Pref.session_token
            attendanceReq.start_date = AppUtils.changeLocalDateFormatToAtt(fronString)
            attendanceReq.end_date = AppUtils.changeLocalDateFormatToAtt(endString)
            callAttendanceListApi(attendanceReq)
        } else {
//            var count=AppDatabase.getDBInstance()!!.userAttendanceDataDao().deleteAll(AppUtils.getCurrentDateChanged())
//            initAdapter(AppDatabase.getDBInstance()!!.userAttendanceDataDao().all as ArrayList<UserLoginDataEntity>)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }


//        userLoginDataEntityArr?.let {
//            if (adapter != null){
//                var list=selectValueWithinFilter(it, FTStorageUtils.getStrinTODateType2(fronString), FTStorageUtils.getStrinTODateType2(endString))
//                if (list.isEmpty()){
//                    if (AppUtils.isOnline(mContext)){
//                        var attendanceReq=AttendanceRequest()
//                        attendanceReq.user_id=Pref.user_id
//                        attendanceReq.session_token=Pref.session_token
//                        attendanceReq.start_date=AppUtils.changeLocalDateFormatToAtt(fronString)
//                        attendanceReq.end_date=AppUtils.changeLocalDateFormatToAtt(endString)
//                        callAttendanceListApi(attendanceReq)
//                    }
//                }else{
//                    adapter!!.notifyAdapter(list)
//                }
//            }
//
//
//        }

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.attendance_header_radio_button_date_range_picker -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                if (!isChkChanged) {
                    val now = Calendar.getInstance(Locale.ENGLISH)
                    val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                            this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.isAutoHighlight = mAutoHighlight
                    dpd.maxDate = Calendar.getInstance(Locale.ENGLISH)
                    dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
                } else
                    isChkChanged = false
            }
            R.id.attandence_radio_button_last_fifteen_days -> if (!isChkChanged) {
//                if (adapter != null)
//                    adapter!!.notifyAdapter(userLoginDataEntityArr)
//                var count=AppDatabase.getDBInstance()!!.userAttendanceDataDao().deleteAll(AppUtils.getCurrentDateChanged())
                //getAttendanceList()

                val list = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getAllSortedList() as ArrayList<UserLoginDataEntity>

                if (list != null && list.size > 0)
                    initAdapter(list)
                else {
                    noDataText.visibility = View.VISIBLE
                    recyclerView!!.visibility = View.GONE
                }

                dateRangeTv!!.visibility = View.GONE

            } else
                isChkChanged = false
        }
    }


    fun selectValueWithinFilter(list: ArrayList<UserLoginDataEntity>, fromdate: Date, toDate: Date): ArrayList<UserLoginDataEntity> {
        var sortedlist: ArrayList<UserLoginDataEntity> = ArrayList<UserLoginDataEntity>()
        for (i in 0..list.size - 1) {

            var logintimeDate: Date = FTStorageUtils.getStrinTODateType2(list[i].logindate)
            if (logintimeDate >= fromdate && logintimeDate <= toDate) {

                sortedlist.add(list[i])
            }

        }

        return sortedlist

    }
}
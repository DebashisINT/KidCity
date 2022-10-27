package com.kcteam.features.member.presentation

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.PjpListEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.*
import com.kcteam.features.reimbursement.presentation.CustomerListDialog
import com.kcteam.features.reimbursement.presentation.DateAdapter
import com.kcteam.features.reimbursement.presentation.MonthListAdapter
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by Saikat on 31-Mar-20.
 */
class EditPJPFragment : BaseFragment(), DateAdapter.onPetSelectedListener, View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var rvDateList: RecyclerView
    private lateinit var select_date_tv: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var et_from_time_slot: AppCustomEditText
    private lateinit var tv_customer_dropdown: AppCustomTextView
    private lateinit var tv_supervisor_name: AppCustomTextView
    private lateinit var et_to_time_slot: AppCustomEditText
    private lateinit var et_location: AppCustomEditText
    private lateinit var et_remark: AppCustomEditText
    private lateinit var submit_button_TV: AppCustomTextView
    private lateinit var rl_customer: RelativeLayout
    private lateinit var rl_add_pjp_main: RelativeLayout
    private lateinit var iv_customer_cross_icon: AppCompatImageView
    private lateinit var iv_customer_dropdown_icon: ImageView
    private lateinit var iv_view_map: ImageView

    private var dateAdapter: DateAdapter? = null
    private var pjp: TeamPjpDataModel? = null
    private var selectedDate: Date? = null
    private var date = ""
    private var picker: TimePickerDialog? = null
    private var conveyancePopupWindow: PopupWindow? = null
    private var customerList: ArrayList<CustomerDataModel>? = null
    private var cust_id = ""
    private var lat = 0.0
    private var long = 0.0
    private var radius = ""

    private val cal: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    private val dateList by lazy {
        ArrayList<Date>()
    }

    companion object {

        fun newInstance(pjp: Any): EditPJPFragment {
            val fragment = EditPJPFragment()

            if (pjp is TeamPjpDataModel) {
                val bundle = Bundle()
                bundle.putSerializable("pjp", pjp)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        pjp = arguments?.getSerializable("pjp") as TeamPjpDataModel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_pjp, container, false)

        initView(view)
        setData()
        getPjpConfig()
        getCustomerList()

        return view
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(view: View) {
        view.apply {
            rvDateList = findViewById(R.id.rvDateList)
            select_date_tv = findViewById(R.id.select_date_tv)
            progress_wheel = findViewById(R.id.progress_wheel)
            et_from_time_slot = findViewById(R.id.et_from_time_slot)
            tv_customer_dropdown = findViewById(R.id.tv_customer_dropdown)
            tv_supervisor_name = findViewById(R.id.tv_supervisor_name)
            et_to_time_slot = findViewById(R.id.et_to_time_slot)
            et_location = findViewById(R.id.et_location)
            et_remark = findViewById(R.id.et_remark)
            submit_button_TV = findViewById(R.id.submit_button_TV)
            rl_customer = findViewById(R.id.rl_customer)
            rl_add_pjp_main = findViewById(R.id.rl_add_pjp_main)
            iv_customer_cross_icon = findViewById(R.id.iv_customer_cross_icon)
            iv_customer_dropdown_icon = findViewById(R.id.iv_customer_dropdown_icon)
            iv_view_map = findViewById(R.id.iv_view_map)
        }

        rvDateList.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        dateAdapter = DateAdapter(mContext, true, this)
        rvDateList.adapter = dateAdapter

        progress_wheel.stopSpinning()

        submit_button_TV.setOnClickListener(this)
        tv_customer_dropdown.setOnClickListener(this)
        rl_add_pjp_main.setOnClickListener(null)
        iv_customer_dropdown_icon.setOnClickListener(this)
        iv_customer_cross_icon.setOnClickListener(this)
        iv_view_map.setOnClickListener(this)

        et_from_time_slot.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                showTimer(et_from_time_slot)
                return@OnTouchListener true
            }
            false
        })


        et_to_time_slot.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                showTimer(et_to_time_slot)
                return@OnTouchListener true
            }
            false
        })

        et_remark.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }

            false
        })
    }

    private fun setData() {
        et_from_time_slot.setText(pjp?.from_time)
        et_to_time_slot.setText(pjp?.to_time)
        if (!TextUtils.isEmpty(pjp?.customer_name)) {
            tv_customer_dropdown.text = pjp?.customer_name
            iv_customer_cross_icon.visibility = View.VISIBLE
        } else
            iv_customer_cross_icon.visibility = View.GONE

        cust_id = pjp?.customer_id!!

        if (!TextUtils.isEmpty(pjp?.location))
            et_location.setText(pjp?.location)
        else
            et_location.setText("N.A.")

        if (!TextUtils.isEmpty(pjp?.remarks))
            et_remark.setText(pjp?.remarks)
        else
            et_remark.setText("N.A.")

        if (!TextUtils.isEmpty(pjp?.pjp_lat))
            lat = pjp?.pjp_lat?.toDouble()!!

        if (!TextUtils.isEmpty(pjp?.pjp_long))
            long = pjp?.pjp_long?.toDouble()!!

        if (!TextUtils.isEmpty(pjp?.pjp_radius))
            radius = pjp?.pjp_radius!!
    }

    private fun getPjpConfig() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.teamPjpConfig(pjp?.user_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamPjpConfigResponseModel
                            XLog.d("GET TEAM PJP CONFIG DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                tv_supervisor_name.text = response.supervisor_name
                                setDateData(response.pjp_past_days)
                            } else {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET TEAM PJP CONFIG DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun getCustomerList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.teamCustomerList(pjp?.user_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as CustomerResponseModel
                            XLog.d("GET TEAM CUSTOMER DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {

                                response.cust_list?.takeIf { it.size > 0 }?.let {
                                    customerList = it
                                }

                            } /*else {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }*/

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET TEAM CUSTOMER DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.submit_button_TV -> {
                checkValidation()
            }

            R.id.iv_customer_dropdown_icon -> {
                CustomerListDialog.newInstance(customerList, { customer ->

                    tv_customer_dropdown.text = customer.cust_name
                    cust_id = customer.cust_id

                    iv_customer_cross_icon.visibility = View.VISIBLE

                }).show(fragmentManager!!, "")
            }

            R.id.tv_customer_dropdown -> {
                /*conveyancePopupWindow?.takeIf { it.isShowing }?.dismiss() ?: let {
                    if (customerList != null)
                        callExpenseTypeDropDownPopUp(rl_customer, customerList!!, tv_customer_dropdown, resources.getDimensionPixelOffset(R.dimen._250sdp))
                }*/

                CustomerListDialog.newInstance(customerList, { customer ->

                    tv_customer_dropdown.text = customer.cust_name
                    cust_id = customer.cust_id
                    iv_customer_cross_icon.visibility = View.VISIBLE

                }).show(fragmentManager!!, "")
            }

            R.id.iv_customer_cross_icon -> {
                tv_customer_dropdown.text = ""
                cust_id = ""
                iv_customer_cross_icon.visibility = View.GONE
            }

            R.id.iv_view_map -> {
                if (lat != 0.0) {
                    val pjpMapData = PJPMapData(lat, long, radius, pjp?.location!!)
                    (mContext as DashboardActivity).loadFragment(FragType.AddPJPLocationFragment, true, pjpMapData)
                }
                else
                    (mContext as DashboardActivity).loadFragment(FragType.AddPJPLocationFragment, true, "")
            }
        }
    }

    private fun checkValidation() {
        AppUtils.hideSoftKeyboard((mContext as DashboardActivity))
        when {
            TextUtils.isEmpty(et_from_time_slot.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_from_time))
            TextUtils.isEmpty(et_to_time_slot.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_to_time))
        //TextUtils.isEmpty(tv_customer_dropdown.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_customer))
            AppUtils.convertTimeWithMeredianToLong(et_from_time_slot.text.toString().trim()) > AppUtils.convertTimeWithMeredianToLong(et_to_time_slot.text.toString().trim()) -> {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_from_time_must_be_greate))
            }
        //TextUtils.isEmpty(et_location.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_location))
            else -> {
                editPJP()
            }
        }
    }

    private fun editPJP() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        var remarks = ""
        remarks = if (et_remark.text.toString().trim().equals("N.A.", ignoreCase = true))
            ""
        else
            et_remark.text.toString().trim()

        var location = ""
        location = if (et_location.text.toString().trim().equals("N.A.", ignoreCase = true))
            ""
        else
            et_location.text.toString().trim()

        val editPjp = EditPjpInputParams(Pref.session_token!!, pjp?.user_id!!, Pref.user_id!!, date, et_from_time_slot.text.toString().trim(), et_to_time_slot.text.toString().trim(), cust_id, location, remarks, pjp?.id!!, lat.toString(), long.toString(), radius)

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.editPjp(editPjp)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("ADD PJP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            if (response.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).isAddedEdited = true
                                //task new updation
                                //AppDatabase.getDBInstance()?.pjpListDao()?.updatePjp(editPjp.date,editPjp.PJP_id)
                                CustomStatic.IsPJPAddEdited=true
                                getPjpListApi()
                                //(mContext as DashboardActivity).onBackPressed()
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("ADD PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun getPjpListApi() {
        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getUserPJPList(AppUtils.getCurrentDateForShopActi())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UserPjpResponseModel
                            XLog.d("GET USER PJP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.pjp_list != null && response.pjp_list.isNotEmpty()) {
                                    doAsync {
                                        AppDatabase.getDBInstance()?.pjpListDao()?.deleteAll()
                                        response.pjp_list.forEach {
                                            val pjpEntity = PjpListEntity()
                                            AppDatabase.getDBInstance()?.pjpListDao()?.insert(pjpEntity.apply {
                                                pjp_id = it.id
                                                from_time = it.from_time
                                                to_time = it.to_time
                                                customer_name = it.customer_name
                                                customer_id = it.customer_id
                                                location = it.location
                                                date = it.date
                                                remarks = it.remarks
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            (mContext as DashboardActivity).onBackPressed()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    AppDatabase.getDBInstance()?.pjpListDao()?.deleteAll()
                                    (mContext as DashboardActivity).onBackPressed()
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                AppDatabase.getDBInstance()?.pjpListDao()?.deleteAll()
                                (mContext as DashboardActivity).onBackPressed()
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET USER PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).onBackPressed()
                        })
        )
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callExpenseTypeDropDownPopUp(anchorView: View, arr_themes: ArrayList<*>, textView: AppCustomTextView, width: Int) {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Inflate the custom layout/view
        val customView = inflater!!.inflate(R.layout.dialog_months, null)

        conveyancePopupWindow = PopupWindow(customView, width, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val rv_member_no_list = customView.findViewById(R.id.rv_months) as RecyclerView
        rv_member_no_list.layoutManager = LinearLayoutManager(mContext)

        conveyancePopupWindow?.elevation = 200f

        rv_member_no_list.adapter = MonthListAdapter(mContext, arr_themes, object : MonthListAdapter.OnItemClickListener {
            override fun onItemClick(adapterPosition: Int) {
                conveyancePopupWindow?.dismiss()

                val genericObj = arr_themes[adapterPosition]

                when (genericObj) {

                    is CustomerDataModel -> {
                        textView.text = genericObj.cust_name
                        cust_id = genericObj.cust_id
                    }
                }
            }
        })

        conveyancePopupWindow?.takeIf { !it.isShowing }?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                it.showAsDropDown(anchorView, resources.getDimensionPixelOffset(R.dimen._25sdp), 0, Gravity.BOTTOM)
            } else {
                it.showAsDropDown(anchorView, anchorView.width - it.width, 0)
            }
        }
    }


    private fun showTimer(editText: AppCustomEditText) {
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minutes = cal.get(Calendar.MINUTE)
        // time picker dialog
        picker = TimePickerDialog(mContext, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

            var am_pm = ""

            val datetime = Calendar.getInstance(Locale.ENGLISH)
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            datetime.set(Calendar.MINUTE, minute)

            if (datetime.get(Calendar.AM_PM) === Calendar.AM)
                am_pm = "AM"
            else if (datetime.get(Calendar.AM_PM) === Calendar.PM)
                am_pm = "PM"

            val strHrsToShow = if (datetime.get(Calendar.HOUR) === 0) "12" else datetime.get(Calendar.HOUR).toString() + ""

            //editText.setText(hourOfDay.toString() + ":" + minute)

            if (datetime.get(Calendar.MINUTE).toString().length == 1)
                editText.setText(strHrsToShow + ":0" + datetime.get(Calendar.MINUTE) + " " + am_pm)
            else
                editText.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm)

        }, hour, minutes, false)

        picker?.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setDateData(pjp_past_days: String?) {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        //calendar.add(Calendar.DAY_OF_YEAR, 0)
        var todayDate: Date? = null /*= calendar.time*/

        if (!TextUtils.isEmpty(pjp?.date)) {
            todayDate = AppUtils.getDateFromDateString(pjp?.date!!)
        }
        calendar.time = todayDate!!
        dateList.add(todayDate)

        selectedDate = todayDate
        date = AppUtils.getFormattedDateForApi(selectedDate!!)

        val lastPastDay = pjp_past_days!!.toInt() - 1

        select_date_tv.text = "Select Date (You can apply for past $pjp_past_days days only)"

        for (i in 1..lastPastDay) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)

            val nextDate = calendar.time
            dateList.add(nextDate)
        }

        dateAdapter?.refreshAdapter(dateList)
    }

    override fun onDateItemClick(pos: Int) {
        selectedDate = dateList[pos]
        date = AppUtils.getFormattedDateForApi(selectedDate!!)
    }

    override fun onPause() {
        super.onPause()
        conveyancePopupWindow?.dismiss()
    }

    fun updateAddress(mLat: Double, mLng: Double, address: String, mRadius: String) {
        et_location.setText(address)
        lat = mLat
        long = mLng
        radius = mRadius
    }
}
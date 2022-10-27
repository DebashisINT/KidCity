package com.kcteam.features.timesheet.presentation

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.TimesheetListEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.reimbursement.presentation.FullImageDialog
import com.kcteam.features.timesheet.api.TimeSheetRepoProvider
import com.kcteam.features.timesheet.model.AddTimeSheetInputModel
import com.kcteam.features.timesheet.model.EditDeleteTimesheetResposneModel
import com.kcteam.features.timesheet.model.TimeSheetListResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 29-Apr-20.
 */
class TimeSheetListFragment : BaseFragment(), DatePickerListener, View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var datePicker: HorizontalPicker
    private lateinit var tv_user_name: AppCustomTextView
    private lateinit var tv_superviser_name: AppCustomTextView
    private lateinit var rv_timesheet_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var fab: FloatingActionButton
    private lateinit var tv_total_time: AppCustomTextView
    private lateinit var tv_pick_date: AppCustomTextView

    private var timesheetList: ArrayList<TimesheetListEntity>? = null

    private var specifiedDate = ""

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_timesheet_list, container, false)

        initView(view)
        specifiedDate = AppUtils.getCurrentDateForShopActi()
        getDataFromDb()

        return view
    }

    private fun initView(view: View) {

        view.apply {
            datePicker = findViewById(R.id.datePicker)
            tv_user_name = findViewById(R.id.tv_user_name)
            tv_superviser_name = findViewById(R.id.tv_superviser_name)
            rv_timesheet_list = findViewById(R.id.rv_timesheet_list)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
            progress_wheel = findViewById(R.id.progress_wheel)
            fab = findViewById(R.id.fab)
            tv_total_time = findViewById(R.id.tv_total_time)
            tv_pick_date = findViewById(R.id.tv_pick_date)
        }

        tv_pick_date.text = AppUtils.getFormattedDate(myCalendar.time)
        tv_user_name.text = Pref.user_name
        progress_wheel.stopSpinning()
        rv_timesheet_list.layoutManager = LinearLayoutManager(mContext)
        tv_superviser_name.text = getString(R.string.supervisor_name) + " " + Pref.supervisor_name
        tv_total_time.text = "(Total Hours: 00:00)"

        datePicker.let {
            it.setListener(this)
                    .setDays(30)
                    .setOffset(3)
                    .setDateSelectedColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//box color
                    .setDateSelectedTextColor(ContextCompat.getColor(mContext, R.color.white))
                    .setMonthAndYearTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//month color
                    .setTodayButtonTextColor(ContextCompat.getColor(mContext, R.color.date_selector_color))
                    .setTodayDateTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setTodayDateBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent))//
                    .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setDayOfWeekTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                    .showTodayButton(false)
                    .init()

            it.backgroundColor = Color.WHITE
            it.setDate(DateTime())
        }

        fab.also {
            rv_timesheet_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy < 0 && !it.isShown)
                        it.show()
                    else if (dy > 0 && it.isShown)
                        it.hide()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })

            it.setOnClickListener(this)
        }

        tv_pick_date.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_pick_date -> {
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                val cal = Calendar.getInstance(Locale.ENGLISH)
                cal.add(Calendar.DATE, -30)
                datePicker.datePicker.minDate = cal.timeInMillis
                datePicker.show()
            }

            R.id.fab -> {
                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else if (Pref.isOnLeave.equals("true", ignoreCase = true))
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_you_are_in_leave))
                else
                    (mContext as DashboardActivity).loadFragment(FragType.AddTimeSheetFragment, true, specifiedDate)
            }
        }
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        tv_pick_date.text = AppUtils.getFormattedDate(myCalendar.time)
        specifiedDate = AppUtils.getFormattedDateForApi(myCalendar.time)

        getDataFromDb()
    }

    override fun onDateSelected(dateSelected: DateTime) {
        val dateTime = dateSelected.toString()
        val dateFormat = dateTime.substring(0, dateTime.indexOf('T'))
        specifiedDate = dateFormat

        getDataFromDb()
    }

    private fun getDataFromDb() {
        val list = AppDatabase.getDBInstance()?.timesheetDao()?.getTimesheetDateWise(specifiedDate)

        timesheetList = list as ArrayList<TimesheetListEntity>?

        if (list != null && list.isNotEmpty())
            initAdapter(list)
        else {
            rv_timesheet_list.visibility = View.GONE
            tv_no_data_available.visibility = View.VISIBLE
            tv_total_time.text = "(Total Hours: 00:00)"
        }
    }

    private fun getTimeSheetList() {

        if (!AppUtils.isOnline(mContext)) {
            /*if (timesheetList == null || timesheetList?.size == 0)
                tv_no_data_available.visibility = View.VISIBLE*/
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.timeSheetList("")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TimeSheetListResponseModel
                            XLog.d("GET TIMESHEET DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            /*if (!TextUtils.isEmpty(response.superviser_name))
                                tv_superviser_name.text = getString(R.string.supervisor_name) + " " + response.superviser_name
                            else
                                tv_superviser_name.text = getString(R.string.supervisor_name) + " N.A."*/

                            /*if (!TextUtils.isEmpty(response.total_hrs))
                                tv_total_time.text = "(Total Hours: " + response.total_hrs + ")"
                            else
                                tv_total_time.text = "(Total Hours: 00:00)"*/

                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.timesheet_list != null && response.timesheet_list!!.size > 0) {
                                    //initAdapter(response.timesheet_list!!)

                                    AppDatabase.getDBInstance()?.timesheetDao()?.deleteAll()

                                    doAsync {
                                        response.timesheet_list?.forEach {
                                            val timeSheetEntity = TimesheetListEntity()
                                            AppDatabase.getDBInstance()?.timesheetDao()?.insertAll(timeSheetEntity.apply {
                                                timesheet_id = it.id
                                                date = it.date
                                                client_id = it.client_id
                                                project_id = it.project_id
                                                activity_id = it.activity_id
                                                product_id = it.product_id
                                                time = it.time
                                                comments = it.comments
                                                isUploaded = true
                                                status = it.timesheet_status
                                                client_name = it.client_name
                                                activity_name = it.activity_name
                                                project_name = it.project_name
                                                product_name = it.product_name
                                                image = it.image
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getDataFromDb()
                                        }
                                    }

                                } else {
                                    //if (timesheetList == null || timesheetList?.size == 0) {

                                    AppDatabase.getDBInstance()?.timesheetDao()?.deleteAll()

                                    progress_wheel.stopSpinning()
                                    rv_timesheet_list.visibility = View.GONE
                                    tv_no_data_available.visibility = View.VISIBLE
                                    tv_total_time.text = "(Total Hours: 00:00)"
                                    //}
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    fab.show()
                                }
                            }
                            else if (response.status == NetworkConstant.NO_DATA) {
                                AppDatabase.getDBInstance()?.timesheetDao()?.deleteAll()

                                progress_wheel.stopSpinning()
                                rv_timesheet_list.visibility = View.GONE
                                tv_no_data_available.visibility = View.VISIBLE
                                tv_total_time.text = "(Total Hours: 00:00)"

                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                fab.show()
                            }
                            else {
                                progress_wheel.stopSpinning()
                                if (timesheetList == null || timesheetList?.size == 0) {
                                    rv_timesheet_list.visibility = View.GONE
                                    tv_no_data_available.visibility = View.VISIBLE
                                    tv_total_time.text = "(Total Hours: 00:00)"
                                }
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET TIMESHEET DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))

                            if (timesheetList == null || timesheetList?.size == 0) {
                                rv_timesheet_list.visibility = View.GONE
                                tv_no_data_available.visibility = View.VISIBLE
                                tv_total_time.text = "(Total Hours: 00:00)"
                            }
                        })
        )
    }

    private fun initAdapter(timesheet_list: List<TimesheetListEntity>?) {
        rv_timesheet_list.visibility = View.VISIBLE
        tv_no_data_available.visibility = View.GONE

        var totalMins = 0
        timesheet_list?.forEach {
            val hrs = it.time!!.substring(0, it.time!!.indexOf(":")).toInt()
            val mins = it.time!!.substring(it.time!!.indexOf(":") + 1, it.time!!.length).toInt()

            totalMins += ((hrs * 60) + mins)
        }

        val hrs = totalMins / 60
        val mins = totalMins % 60

        var hrsString = ""
        var minString = ""

        hrsString = if (hrs.toString().trim().length == 1)
            "0" + hrs.toString().trim()
        else
            hrs.toString().trim()

        minString = if (mins.toString().trim().length == 1)
            "0" + mins.toString().trim()
        else
            mins.toString().trim()

        tv_total_time.text = "(Total Hours: $hrsString:$minString)"

        rv_timesheet_list.adapter = TimeSheetAdapter(mContext, timesheet_list as ArrayList<TimesheetListEntity>?, { timeSheet: TimesheetListEntity ->
            if (!Pref.isAddAttendence)
                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
            else if (Pref.isOnLeave.equals("true", ignoreCase = true))
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_you_are_in_leave))
            else
                (mContext as DashboardActivity).loadFragment(FragType.EditTimeSheetFragment, true, timeSheet)
        }, { timeSheet: TimesheetListEntity ->
            if (!Pref.isAddAttendence)
                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
            else if (Pref.isOnLeave.equals("true", ignoreCase = true))
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_you_are_in_leave))
            else
                showDeleterAlert(timeSheet)
        }, { timeSheet: TimesheetListEntity ->
            callAddTimeApi(timeSheet)
        }, {
            FullImageDialog.getInstance(it.image!!).show((mContext as DashboardActivity).supportFragmentManager, "")
        })

        rv_timesheet_list.smoothScrollToPosition(0)
        fab.show()
    }

    private fun callAddTimeApi(timeSheet: TimesheetListEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("==============Sync Single Timesheet Input Params (Timesheet List)====================")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("date=======> " + timeSheet.date)
        XLog.d("client_id=======> " + timeSheet.client_id)
        XLog.d("project_id=======> " + timeSheet.project_id)
        XLog.d("activity_id=======> " + timeSheet.activity_id)
        XLog.d("product_id=======> " + timeSheet.product_id)
        XLog.d("time=======> " + timeSheet.time)
        XLog.d("comments=======> " + timeSheet.comments)
        XLog.d("timesheet_id=======> " + timeSheet.timesheet_id)
        XLog.d("image=======> " + timeSheet.image)
        XLog.d("===================================================================================")

        val addIntput = AddTimeSheetInputModel(Pref.session_token!!, Pref.user_id!!, timeSheet.date!!, timeSheet.client_id!!,
                timeSheet.project_id!!, timeSheet.activity_id!!, timeSheet.product_id!!, timeSheet.time!!, timeSheet.comments!!,
                timeSheet.timesheet_id!!)


        progress_wheel.spin()

        if (TextUtils.isEmpty(timeSheet.image)) {
            val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.addTimeSheet(addIntput)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                XLog.d("ADD TIMESHEET: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.timesheetDao()?.updateIsUploaded(true, timeSheet.timesheet_id!!)
                                    getDataFromDb()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("ADD TIMESHEET: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
        else {
            val repository = TimeSheetRepoProvider.timeSheetImageRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.addTimesheetWithImage(addIntput, timeSheet.image!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                XLog.d("ADD TIMESHEET: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.timesheetDao()?.updateIsUploaded(true, timeSheet.timesheet_id!!)
                                    getDataFromDb()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("ADD TIMESHEET: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
    }

    private fun showDeleterAlert(timeSheet: TimesheetListEntity) {
        CommonDialog.getInstance("Delete Alert", "Do you really want to delete this Timesheet?", getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                if (timeSheet.isUploaded)
                    deleteTimesheet(timeSheet.timesheet_id!!)
                else {
                    (mContext as DashboardActivity).showSnackMessage("Timesheet deleted successfully")
                    AppDatabase.getDBInstance()?.timesheetDao()?.deleteSingleTimesheet(timeSheet.timesheet_id!!)
                    getDataFromDb()
                }
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun deleteTimesheet(id: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage("Timesheet already saved in server so delete only possible if you have internet connection")
            return
        }

        XLog.d("==============Delete Timesheet Input Params (Timesheet List)===============")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("timesheet_id=======> " + id)
        XLog.d("===========================================================================")

        progress_wheel.spin()
        val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.deleteTimeSheet(id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as EditDeleteTimesheetResposneModel
                            XLog.d("DELETE TIMESHEET : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.timesheetDao()?.deleteSingleTimesheet(id)
                                getDataFromDb()
                            } else if (response.status == "204") {
                                AppDatabase.getDBInstance()?.timesheetDao()?.updateStatus(response.timesheet_status, id)
                                getDataFromDb()
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("DELETE TIMESHEET : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    fun updateList() {
        //getTimeSheetList()
        getDataFromDb()
    }

    private var i = 0
    fun refreshList() {
        val list = AppDatabase.getDBInstance()?.timesheetDao()?.getTimesheetSyncWise(false)
        if (list == null || list.isEmpty())
            getTimeSheetList()
        else {
            i = 0
            callAddTimeSheetApiForAll(list[i], list)
        }
    }

    private fun callAddTimeSheetApiForAll(timeSheet: TimesheetListEntity, list: List<TimesheetListEntity>?) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("==============Sync Timesheet Input Params (Timesheet List)====================")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("date=======> " + timeSheet.date)
        XLog.d("client_id=======> " + timeSheet.client_id)
        XLog.d("project_id=======> " + timeSheet.project_id)
        XLog.d("activity_id=======> " + timeSheet.activity_id)
        XLog.d("product_id=======> " + timeSheet.product_id)
        XLog.d("time=======> " + timeSheet.time)
        XLog.d("comments=======> " + timeSheet.comments)
        XLog.d("timesheet_id=======> " + timeSheet.timesheet_id)
        XLog.d("image=======> " + timeSheet.image)
        XLog.d("===========================================================================")

        val addIntput = AddTimeSheetInputModel(Pref.session_token!!, Pref.user_id!!, timeSheet.date!!, timeSheet.client_id!!,
                timeSheet.project_id!!, timeSheet.activity_id!!, timeSheet.product_id!!, timeSheet.time!!, timeSheet.comments!!,
                timeSheet.timesheet_id!!)


        progress_wheel.spin()

        if (TextUtils.isEmpty(timeSheet.image)) {
            val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.addTimeSheet(addIntput)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                XLog.d("ADD TIMESHEET: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                                progress_wheel.stopSpinning()

                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.timesheetDao()?.updateIsUploaded(true, timeSheet.timesheet_id!!)

                                    i++
                                    if (i < list?.size!!)
                                        callAddTimeSheetApiForAll(list[i], list)
                                    else {
                                        i = 0
                                        getTimeSheetList()
                                    }
                                } else
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("ADD TIMESHEET: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
        else {
            val repository = TimeSheetRepoProvider.timeSheetImageRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.addTimesheetWithImage(addIntput, timeSheet.image!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                XLog.d("ADD TIMESHEET: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                                progress_wheel.stopSpinning()

                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.timesheetDao()?.updateIsUploaded(true, timeSheet.timesheet_id!!)

                                    i++
                                    if (i < list?.size!!)
                                        callAddTimeSheetApiForAll(list[i], list)
                                    else {
                                        i = 0
                                        getTimeSheetList()
                                    }
                                } else
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("ADD TIMESHEET: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
    }
}
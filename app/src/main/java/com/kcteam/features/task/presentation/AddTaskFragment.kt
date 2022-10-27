package com.kcteam.features.task.presentation

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import androidx.core.app.ActivityCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.TaskEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.task.api.TaskRepoProvider
import com.kcteam.features.task.model.AddTaskInputModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by Saikat on 12-Aug-20.
 */
class AddTaskFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var rl_add_task_main: RelativeLayout
    private lateinit var et_date: AppCustomEditText
    private lateinit var et_task: AppCustomEditText
    private lateinit var et_details: AppCustomEditText
    private lateinit var tv_submit_btn: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel

    private var selectedDate = ""
    private var permissionUtils: PermissionUtils? = null

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_task, container, false)

        initView(view)
        initClickListener()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            et_date = findViewById(R.id.et_date)
            et_task = findViewById(R.id.et_task)
            et_details = findViewById(R.id.et_details)
            tv_submit_btn = findViewById(R.id.tv_submit_btn)
            progress_wheel = findViewById(R.id.progress_wheel)
            rl_add_task_main = findViewById(R.id.rl_add_task_main)
        }

        progress_wheel.stopSpinning()
        et_date.setText(AppUtils.getFormattedDate(myCalendar.time))
        selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)

        et_details.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }

            false
        })
    }

    private fun initClickListener() {
        tv_submit_btn.setOnClickListener(this)
        et_date.setOnClickListener(this)
        rl_add_task_main.setOnClickListener(null)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_submit_btn -> {
                when {
                    TextUtils.isEmpty(selectedDate) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_date))
                    TextUtils.isEmpty(et_task.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_task))
                    else -> saveData()
                }
            }

            R.id.et_date -> {
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))

                /*val cal = Calendar.getInstance()
                cal.add(Calendar.DATE, -30)
                datePicker.datePicker.minDate = cal.timeInMillis

                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DATE, 30)
                datePicker.datePicker.maxDate = calendar.timeInMillis*/

                datePicker.show()
            }
        }
    }

    private fun saveData() {
        val task = TaskEntity()
        task.apply {
            task_id = Pref.user_id + "_task_" + System.currentTimeMillis()
            date = selectedDate
            task_name = et_task.text.toString().trim()
            details = et_details.text.toString().trim()
            isUploaded = false
            isCompleted = false
            isStatusUpdated = -1
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            initPermissionCheck(task)
        else
            addEventToCalender(task)
    }

    private fun initPermissionCheck(task: TaskEntity) {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                addEventToCalender(task)
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun addEventToCalender(task: TaskEntity) {
        val startTime = AppUtils.getTimeStampFromDateOnly(task.date)

        val calender = Calendar.getInstance(Locale.ENGLISH)
        calender.timeInMillis = startTime + (1000 * 60 * 60 * 24)

        val endTime = AppUtils.getTimeStampFromDateOnly(AppUtils.getFormattedDateForApi(calender.time))

        val cr = mContext.contentResolver
        val values = ContentValues()

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values.apply {
            put(CalendarContract.Events.DTSTART, startTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.TITLE, task.task_name)

            if (!TextUtils.isEmpty(task.details))
                put(CalendarContract.Events.DESCRIPTION, task.details)
            else
                put(CalendarContract.Events.DESCRIPTION, "")

            put(CalendarContract.Events.CALENDAR_ID, AppUtils.getCalendarId(mContext))
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        })
        task.eventId = uri?.lastPathSegment

        AppDatabase.getDBInstance()?.taskDao()?.insertAll(task)

        callAddTaskApi(task)
    }

    private fun callAddTaskApi(task: TaskEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage("Task added successfully")
            (mContext as DashboardActivity).onBackPressed()
            return
        }

        XLog.d("==============Add Task Input Params (Add Task)====================")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("date=======> " + task.date)
        XLog.d("task_id=======> " + task.task_id)
        XLog.d("task_name=======> " + task.task_name)
        XLog.d("details=======> " + task.details)
        XLog.d("isCompleted=======> " + task.isCompleted)
        XLog.d("eventId=======> " + task.eventId)
        XLog.d("===================================================================")

        val taskInput = AddTaskInputModel(Pref.session_token!!, Pref.user_id!!, task.task_id!!, task.date!!, task.task_name!!,
                task.details!!, task.isCompleted, task.eventId)

        progress_wheel.spin()
        val repository = TaskRepoProvider.taskRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.addTask(taskInput)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("ADD TASK: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.taskDao()?.updateIsUploaded(true, task.task_id!!)
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                (mContext as DashboardActivity).onBackPressed()
                            } else {
                                (mContext as DashboardActivity).showSnackMessage("Task added successfully")
                                (mContext as DashboardActivity).onBackPressed()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("ADD TASK: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage("Task added successfully")
                            (mContext as DashboardActivity).onBackPressed()
                        })
        )
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        et_date.setText(AppUtils.getFormattedDate(myCalendar.time))
        selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)
    }
}
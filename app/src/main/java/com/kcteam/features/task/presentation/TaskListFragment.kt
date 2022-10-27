package com.kcteam.features.task.presentation

import android.Manifest
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.TaskEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.task.api.TaskRepoProvider
import com.kcteam.features.task.model.AddTaskInputModel
import com.kcteam.features.task.model.TaskListResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by Saikat on 13-Aug-20.
 */
class TaskListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var tv_pick_date: AppCustomTextView
    private lateinit var ll_completed: LinearLayout
    private lateinit var tv_completed_count: AppCustomTextView
    private lateinit var tv_pending_count: AppCustomTextView
    private lateinit var rv_task_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var fab: FloatingActionButton
    private lateinit var ll_pending: LinearLayout
    private lateinit var tv_no_data_available: AppCustomTextView

    private var selectedDate = ""
    private var isCompleted = true
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
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        initView(view)
        initClickListener()

        val list = AppDatabase.getDBInstance()?.taskDao()?.getAll()
        if (list != null && list.isNotEmpty())
            initAdapter()
        else
            callTaskListApi()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            tv_pick_date = findViewById(R.id.tv_pick_date)
            ll_completed = findViewById(R.id.ll_completed)
            tv_completed_count = findViewById(R.id.tv_completed_count)
            tv_pending_count = findViewById(R.id.tv_pending_count)
            rv_task_list = findViewById(R.id.rv_task_list)
            progress_wheel = findViewById(R.id.progress_wheel)
            fab = findViewById(R.id.fab)
            ll_pending = findViewById(R.id.ll_pending)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
        }

        rv_task_list.layoutManager = LinearLayoutManager(mContext)
        progress_wheel.stopSpinning()
        tv_pick_date.text = AppUtils.getFormattedDate(myCalendar.time)
        selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)

    }

    private fun initClickListener() {
        tv_pick_date.setOnClickListener(this)
        ll_completed.setOnClickListener(this)
        fab.setOnClickListener(this)
        ll_pending.setOnClickListener(this)
    }

    private fun callTaskListApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = TaskRepoProvider.taskRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.taskList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TaskListResponseModel
                            XLog.d("TASK LIST: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.task_list != null && response.task_list!!.isNotEmpty()) {
                                    doAsync {
                                        response.task_list?.forEach {
                                            val task = TaskEntity()
                                            AppDatabase.getDBInstance()?.taskDao()?.insertAll(task.apply {
                                                task_id = it.id
                                                date = it.date
                                                task_name = it.task
                                                details = it.details
                                                isUploaded = true
                                                isCompleted = it.isCompleted
                                                isStatusUpdated = -1
                                                eventId = it.eventID
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            initAdapter()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("TASK LIST: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun initAdapter() {
        val completedList = AppDatabase.getDBInstance()?.taskDao()?.getTaskStatusDateWise(selectedDate, true)
        val pendingList = AppDatabase.getDBInstance()?.taskDao()?.getTaskStatusDateWise(selectedDate, false)

        if (completedList == null)
            tv_completed_count.text = "0"
        else
            tv_completed_count.text = completedList.size.toString()

        if (pendingList == null)
            tv_pending_count.text = "0"
        else
            tv_pending_count.text = pendingList.size.toString()


        var finalList: ArrayList<TaskEntity>? = null

        if (isCompleted) {
            finalList = completedList as ArrayList<TaskEntity>?
            tv_no_data_available.text = getString(R.string.no_completed_task_available)
        }
        else {
            finalList = pendingList as ArrayList<TaskEntity>?
            tv_no_data_available.text = getString(R.string.no_pending_task_available)
        }


        if (finalList == null || finalList.isEmpty()) {
            tv_no_data_available.visibility = View.VISIBLE
            rv_task_list.visibility = View.GONE
        } else {
            tv_no_data_available.visibility = View.GONE
            rv_task_list.visibility = View.VISIBLE
            rv_task_list.adapter = TaskAdapter(mContext, finalList, { task: TaskEntity ->
                (mContext as DashboardActivity).loadFragment(FragType.EditTaskFragment, true, task)
            }, { task: TaskEntity ->
                showDeleterAlert(task)
            }, { task: TaskEntity ->
                if (!task.isUploaded)
                    syncTask(task)
                else
                    syncStatusUpdateApi(task)
            }, { task: TaskEntity ->
                showStatusAlert(task)
            })
        }
    }

    private fun showDeleterAlert(task: TaskEntity) {
        CommonDialog.getInstance(AppUtils.hiFirstNameText()+"!", "Do you really want to delete this Task?", getString(R.string.no), getString(R.string.yes), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                if (task.isUploaded)
                    deleteTask(task)
                else {
                    (mContext as DashboardActivity).showSnackMessage("Task deleted successfully")
                    AppDatabase.getDBInstance()?.taskDao()?.deleteSingleItem(task.task_id!!)
                    initAdapter()

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheck(task)
                    else
                        deleteEventToCalender(task)
                }
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun initPermissionCheck(task: TaskEntity) {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                deleteEventToCalender(task)
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR))
    }

    private fun deleteEventToCalender(task: TaskEntity) {
        val cr = mContext.contentResolver
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, task.eventId?.toLong()!!)
        cr.delete(uri, null, null)
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun deleteTask(task: TaskEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage("Task already saved in server so delete only possible if you have internet connection")
            return
        }

        XLog.d("==============Delete Task Input Params (Task List)====================")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("task_id=======> " + task.task_id)
        XLog.d("===================================================================")

        progress_wheel.spin()
        val repository = TaskRepoProvider.taskRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.deleteTask(task.task_id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("DELETE TASK: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    initPermissionCheck(task)
                                else
                                    deleteEventToCalender(task)

                                AppDatabase.getDBInstance()?.taskDao()?.deleteSingleItem(task.task_id)
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                initAdapter()
                            } else {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("DELETE TASK: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun syncTask(task: TaskEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("==============Sync Task Input Params (Task List)====================")
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
                                initAdapter()
                            } else {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("ADD TASK: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showStatusAlert(task: TaskEntity) {
        CommonDialog.getInstance(AppUtils.hiFirstNameText()+"!", "Do you really want to change this Task's status?", getString(R.string.no), getString(R.string.yes), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                changeStatus(task)
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun changeStatus(task: TaskEntity) {
        if (task.isCompleted)
            AppDatabase.getDBInstance()?.taskDao()?.updateIsCompleted(false, task.task_id!!)
        else
            AppDatabase.getDBInstance()?.taskDao()?.updateIsCompleted(true, task.task_id!!)

        if (!task.isUploaded) {
            XLog.d("============UnSync Task at Status change time (Task List)===========")
            (mContext as DashboardActivity).showSnackMessage("Task status updated successfully")
            initAdapter()
            return
        }

        AppDatabase.getDBInstance()?.taskDao()?.updateIsStatus(0, task.task_id!!)

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage("Task status updated successfully")
            initAdapter()
            return
        }

        XLog.d("============Update Task Status Input Params (Task List)===========")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("task_id=======> " + task.task_id)
        XLog.d("isCompleted=======> " + task.isCompleted)
        XLog.d("===================================================================")

        progress_wheel.spin()
        val repository = TaskRepoProvider.taskRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.updateStatus(task.task_id, task.isCompleted)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("UPDATE TASK STATUS: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.taskDao()?.updateIsStatus(1, task.task_id!!)
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                initAdapter()
                            } else {
                                (mContext as DashboardActivity).showSnackMessage("Task status updated successfully")
                                initAdapter()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("UPDATE TASK STATUS: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage("Task status updated successfully")
                            initAdapter()
                        })
        )
    }

    private fun syncStatusUpdateApi(task: TaskEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("============Sync Update Task Status Input Params (Task List)===========")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("task_id=======> " + task.task_id)
        XLog.d("isCompleted=======> " + task.isCompleted)
        XLog.d("========================================================================")

        progress_wheel.spin()
        val repository = TaskRepoProvider.taskRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.updateStatus(task.task_id, task.isCompleted)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("UPDATE TASK STATUS: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.taskDao()?.updateIsStatus(1, task.task_id!!)
                                initAdapter()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("UPDATE TASK STATUS: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_pick_date -> {
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))

                datePicker.show()
            }

            R.id.ll_completed -> {
                isCompleted = true
                initAdapter()
            }

            R.id.ll_pending -> {
                isCompleted = false
                initAdapter()
            }

            R.id.fab -> {
                (mContext as DashboardActivity).loadFragment(FragType.AddTaskFragment, true, "")
            }
        }
    }


    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        tv_pick_date.text = AppUtils.getFormattedDate(myCalendar.time)
        selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)
        initAdapter()
    }

    fun updateList() {
        initAdapter()
    }

    fun showCalender() {
        /*val builder = CalendarContract.CONTENT_URI.buildUpon()
        builder.appendPath("time")
        ContentUris.appendId(builder, System.currentTimeMillis())
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = builder.build()
        startActivity(intent)*/

        (mContext as DashboardActivity).loadFragment(FragType.CalenderTaskFragment, true, "")
    }
}
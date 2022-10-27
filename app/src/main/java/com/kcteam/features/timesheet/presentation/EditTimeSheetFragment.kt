package com.kcteam.features.timesheet.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.ProcessImageUtils_v1
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.reimbursement.presentation.DateAdapter
import com.kcteam.features.timesheet.api.TimeSheetRepoProvider
import com.kcteam.features.timesheet.model.*
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*

/**
 * Created by Saikat on 29-Apr-20.
 */
class EditTimeSheetFragment : BaseFragment(), DateAdapter.onPetSelectedListener, View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var select_date_tv: AppCustomTextView
    private lateinit var rvDateList: RecyclerView
    private lateinit var tv_supervisor_name: AppCustomTextView
    private lateinit var tv_client_header: AppCustomTextView
    private lateinit var tv_client_dropdown: AppCustomTextView
    private lateinit var iv_client_dropdown_icon: ImageView
    private lateinit var tv_project_header: AppCustomTextView
    private lateinit var tv_project_dropdown: AppCustomTextView
    private lateinit var iv_project_dropdown_icon: ImageView
    private lateinit var tv_activity_header: AppCustomTextView
    private lateinit var tv_activity_dropdown: AppCustomTextView
    private lateinit var iv_activity_dropdown_icon: ImageView
    private lateinit var tv_product_header: AppCustomTextView
    private lateinit var tv_product_dropdown: AppCustomTextView
    private lateinit var iv_product_dropdown_icon: ImageView
    private lateinit var et_time: AppCustomEditText
    private lateinit var et_comment: AppCustomEditText
    private lateinit var submit_button_TV: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_add_timesheet_main: RelativeLayout
    private lateinit var iv_client_cross_icon: AppCompatImageView
    private lateinit var iv_project_cross_icon: AppCompatImageView
    private lateinit var iv_product_cross_icon: AppCompatImageView
    private lateinit var til_time: TextInputLayout
    private lateinit var til_comment: TextInputLayout
    private lateinit var tv_time: AppCustomTextView
    private lateinit var et_hrs: AppCustomEditText
    private lateinit var et_mins: AppCustomEditText
    private lateinit var iv_upload_photo: ImageView
    private var permissionUtils: PermissionUtils? = null
    private var imagePath = ""

    private var dateAdapter: DateAdapter? = null
    private var selectedDate: Date? = null
    private var date = ""
    private var picker: TimePickerDialog? = null
    private var mClient_list: ArrayList<ClientListEntity>? = null
    private var mProject_list: ArrayList<ProjectListEntity>? = null
    private var mProduct_list: ArrayList<TimesheetProductListEntity>? = null
    private var mActivity_list: ArrayList<ActivityListEntity>? = null
    private var clientId = ""
    private var projectId = ""
    private var productId = ""
    private var activityId = ""

    private val dateList by lazy {
        ArrayList<Date>()
    }

    companion object {

        private var timeSheet: TimesheetListEntity? = null

        fun newInstance(mTimeSheet: Any): EditTimeSheetFragment {
            val fragment = EditTimeSheetFragment()

            if (mTimeSheet is TimesheetListEntity) {
                timeSheet = mTimeSheet
            }

            return fragment
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_timesheet, container, false)

        initView(view)
        setDateData(Pref.timesheet_past_days)
        setData()

        mClient_list = AppDatabase.getDBInstance()?.clientDao()?.getAll() as ArrayList<ClientListEntity>?
        mProject_list = AppDatabase.getDBInstance()?.projectDao()?.getAll() as ArrayList<ProjectListEntity>?
        mProduct_list = AppDatabase.getDBInstance()?.productDao()?.getAll() as ArrayList<TimesheetProductListEntity>?
        mActivity_list = AppDatabase.getDBInstance()?.activityDao()?.getAll() as ArrayList<ActivityListEntity>?

        return view
    }

    private fun initView(view: View) {
        view.apply {
            select_date_tv = findViewById(R.id.select_date_tv)
            rvDateList = findViewById(R.id.rvDateList)
            tv_supervisor_name = findViewById(R.id.tv_supervisor_name)
            tv_client_header = findViewById(R.id.tv_client_header)
            tv_client_dropdown = findViewById(R.id.tv_client_dropdown)
            iv_client_dropdown_icon = findViewById(R.id.iv_client_dropdown_icon)
            tv_project_header = findViewById(R.id.tv_project_header)
            tv_project_dropdown = findViewById(R.id.tv_project_dropdown)
            iv_project_dropdown_icon = findViewById(R.id.iv_project_dropdown_icon)
            tv_activity_header = findViewById(R.id.tv_activity_header)
            tv_activity_dropdown = findViewById(R.id.tv_activity_dropdown)
            iv_activity_dropdown_icon = findViewById(R.id.iv_activity_dropdown_icon)
            tv_product_header = findViewById(R.id.tv_product_header)
            tv_product_dropdown = findViewById(R.id.tv_product_dropdown)
            iv_product_dropdown_icon = findViewById(R.id.iv_product_dropdown_icon)
            et_time = findViewById(R.id.et_time)
            et_comment = findViewById(R.id.et_comment)
            submit_button_TV = findViewById(R.id.submit_button_TV)
            progress_wheel = findViewById(R.id.progress_wheel)
            rl_add_timesheet_main = findViewById(R.id.rl_add_timesheet_main)
            iv_client_cross_icon = findViewById(R.id.iv_client_cross_icon)
            iv_project_cross_icon = findViewById(R.id.iv_project_cross_icon)
            iv_product_cross_icon = findViewById(R.id.iv_product_cross_icon)
            til_time = findViewById(R.id.til_time)
            til_comment = findViewById(R.id.til_comment)
            tv_time = findViewById(R.id.tv_time)
            et_hrs = findViewById(R.id.et_hrs)
            et_mins = findViewById(R.id.et_mins)
            iv_upload_photo = findViewById(R.id.iv_upload_photo)
        }

        progress_wheel.stopSpinning()

        rvDateList.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        dateAdapter = DateAdapter(mContext, true, this)
        rvDateList.adapter = dateAdapter


        clientId = timeSheet?.client_id!!
        productId = timeSheet?.product_id!!
        activityId = timeSheet?.activity_id!!
        projectId = timeSheet?.project_id!!

        if (!TextUtils.isEmpty(timeSheet?.client_name))
            tv_client_dropdown.text = timeSheet?.client_name

        if (!TextUtils.isEmpty(timeSheet?.project_name))
            tv_project_dropdown.text = timeSheet?.project_name

        if (!TextUtils.isEmpty(timeSheet?.activity_name))
            tv_activity_dropdown.text = timeSheet?.activity_name

        if (!TextUtils.isEmpty(timeSheet?.product_name))
            tv_product_dropdown.text = timeSheet?.product_name

        if (!TextUtils.isEmpty(timeSheet?.time)) {
            et_time.setText(timeSheet?.time)

            val hrs = timeSheet?.time!!.substring(0, timeSheet?.time!!.indexOf(":"))
            val mins = timeSheet?.time!!.substring(timeSheet?.time!!.indexOf(":") + 1, timeSheet?.time!!.length)

            et_hrs.setText(hrs)
            et_mins.setText(mins)

        } else {
            et_time.setText("N.A.")

            et_hrs.hint = "00"
            et_mins.hint = "00"
        }

        if (!TextUtils.isEmpty(timeSheet?.comments))
            et_comment.setText(timeSheet?.comments)
        else
            et_comment.setText("N.A.")

        if (timeSheet?.image != null) {
            imagePath = timeSheet?.image!!

            Glide.with(mContext)
                    .load(imagePath)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                    .into(iv_upload_photo)
        }

        rl_add_timesheet_main.setOnClickListener(null)
        submit_button_TV.setOnClickListener(this)
        tv_client_dropdown.setOnClickListener(this)
        iv_client_dropdown_icon.setOnClickListener(this)
        tv_project_dropdown.setOnClickListener(this)
        iv_project_dropdown_icon.setOnClickListener(this)
        iv_activity_dropdown_icon.setOnClickListener(this)
        tv_activity_dropdown.setOnClickListener(this)
        tv_product_dropdown.setOnClickListener(this)
        iv_product_dropdown_icon.setOnClickListener(this)
        iv_product_cross_icon.setOnClickListener(this)
        iv_client_cross_icon.setOnClickListener(this)
        iv_project_cross_icon.setOnClickListener(this)
        iv_upload_photo.setOnClickListener(this)

        /*et_time.setOnTouchListener(View.OnTouchListener { v, event ->
            if (event?.action == MotionEvent.ACTION_UP) {
                showTimer()
                return@OnTouchListener true
            }
            false
        })*/

        et_comment.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }

            false
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setDateData(timesheet_past_days: String?) {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        //calendar.add(Calendar.DAY_OF_YEAR, 1)
        val todayDate = calendar.time
        dateList.add(todayDate)

        selectedDate = todayDate
        date = timeSheet?.date!! //AppUtils.getFormattedDateForApi(selectedDate!!)

        val lastPastDay = timesheet_past_days!!.toInt() - 1

        select_date_tv.text = "Select Date (You can add Timesheet for $timesheet_past_days days only)"

        for (i in 1..lastPastDay) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)

            val nextDate = calendar.time
            dateList.add(nextDate)
        }

        var selectedIndex = -1
        for (i in dateList.indices) {
            if (AppUtils.getFormattedDateForApi(dateList[i]) == timeSheet?.date) {
                selectedIndex = i
                break
            }
        }

        dateAdapter?.refreshAdapter(dateList, selectedIndex)
    }

    private fun setData() {
        Pref.apply {
            tv_supervisor_name.text = supervisor_name
            tv_client_header.text = client_text
            tv_product_header.text = product_text
            tv_activity_header.text = activity_text
            tv_project_header.text = project_text
            til_time.hint = time_text
            til_comment.hint = comment_text
            tv_time.text = time_text
            submit_button_TV.text = submit_text
        }
    }

    override fun onDateItemClick(pos: Int) {
        selectedDate = dateList[pos]
        date = AppUtils.getFormattedDateForApi(selectedDate!!)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.submit_button_TV -> {
                checkValidation()
            }

            R.id.iv_client_dropdown_icon -> {
                if (mClient_list != null && mClient_list?.size!! > 0) {
                    ClientListDialog.newInstance(mClient_list, { client ->

                        tv_client_dropdown.text = client.client_name
                        clientId = client.client_id!!

                        iv_client_cross_icon.visibility = View.VISIBLE

                    }).show(fragmentManager!!, "")
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            }

            R.id.tv_client_dropdown -> {
                if (mClient_list != null && mClient_list?.size!! > 0) {
                    ClientListDialog.newInstance(mClient_list, { client ->

                        tv_client_dropdown.text = client.client_name
                        clientId = client.client_id!!

                        iv_client_cross_icon.visibility = View.VISIBLE

                    }).show(fragmentManager!!, "")
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            }

            R.id.iv_project_dropdown_icon -> {
                if (mProject_list != null && mProject_list?.size!! > 0) {
                    ProjectListDialog.newInstance(mProject_list, { project ->

                        tv_project_dropdown.text = project.project_name
                        projectId = project.project_id!!

                        iv_project_cross_icon.visibility = View.VISIBLE

                    }).show(fragmentManager!!, "")
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            }

            R.id.tv_project_dropdown -> {
                if (mProject_list != null && mProject_list?.size!! > 0) {
                    ProjectListDialog.newInstance(mProject_list, { project ->

                        tv_project_dropdown.text = project.project_name
                        projectId = project.project_id!!

                        iv_project_cross_icon.visibility = View.VISIBLE

                    }).show(fragmentManager!!, "")
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            }

            R.id.iv_activity_dropdown_icon -> {
                if (mActivity_list != null && mActivity_list?.size!! > 0) {
                    ActivityListDialog.newInstance(mActivity_list, { activity ->

                        tv_activity_dropdown.text = activity.activity_name
                        activityId = activity.activity_id!!

                        iv_project_cross_icon.visibility = View.VISIBLE

                    }).show(fragmentManager!!, "")
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            }

            R.id.tv_activity_dropdown -> {
                if (mActivity_list != null && mActivity_list?.size!! > 0) {
                    ActivityListDialog.newInstance(mActivity_list, { activity ->

                        tv_activity_dropdown.text = activity.activity_name
                        activityId = activity.activity_id!!

                    }).show(fragmentManager!!, "")
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            }

            R.id.iv_product_dropdown_icon -> {
                if (mProduct_list != null && mProduct_list?.size!! > 0) {
                    ProductListDialog.newInstance(mProduct_list, { product ->

                        tv_product_dropdown.text = product.product_name
                        productId = product.product_id!!

                        iv_product_cross_icon.visibility = View.VISIBLE

                    }).show(fragmentManager!!, "")
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            }

            R.id.tv_product_dropdown -> {
                if (mProduct_list != null && mProduct_list?.size!! > 0) {
                    ProductListDialog.newInstance(mProduct_list, { product ->

                        tv_product_dropdown.text = product.product_name
                        productId = product.product_id!!

                        iv_product_cross_icon.visibility = View.VISIBLE

                    }).show(fragmentManager!!, "")
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            }

            R.id.iv_project_cross_icon -> {
                tv_project_dropdown.text = ""
                projectId = ""
                iv_project_cross_icon.visibility = View.GONE
            }

            R.id.iv_client_cross_icon -> {
                tv_client_dropdown.text = ""
                clientId = ""
                iv_client_cross_icon.visibility = View.GONE
            }

            R.id.iv_product_cross_icon -> {
                tv_product_dropdown.text = ""
                productId = ""
                iv_product_cross_icon.visibility = View.GONE
            }

            R.id.iv_upload_photo -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck()
                else {
                    (mContext as DashboardActivity).captureImage()
                }
            }
        }
    }

    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                (mContext as DashboardActivity).captureImage()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setImage(filePath: String) {
        val file = File(filePath)
        var newFile: File? = null

        progress_wheel.spin()
        doAsync {

            val processImage = ProcessImageUtils_v1(mContext, file, 50)
            newFile = processImage.ProcessImage()

            uiThread {
                if (newFile != null) {
                    XLog.e("=========Image from new technique==========")
                    timesheetPic(newFile!!.length(), newFile?.absolutePath!!)
                } else {
                    // Image compression
                    val fileSize = AppUtils.getCompressImage(filePath)
                    timesheetPic(fileSize, filePath)
                }
            }
        }
    }

    private fun timesheetPic(fileSize: Long, filePath: String) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Add Timesheet", "image file size after compression=====> $fileSizeInKB KB")

        progress_wheel.stopSpinning()

        imagePath = filePath
        Glide.with(mContext)
                .load(filePath)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                .into(iv_upload_photo)
    }

    private fun checkValidation() {
        AppUtils.hideSoftKeyboard((mContext as DashboardActivity))

        if (TextUtils.isEmpty(activityId))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_activity))
        /*else if (TextUtils.isEmpty(et_time.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_time))
        else {
            if (!et_time.text.toString().trim().contains(".") && !et_time.text.toString().trim().contains(":")) {
                when {
                    et_time.text.toString().trim().toInt() == 0 -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_time))
                    et_time.text.toString().trim().toInt() > 5 -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_time))
                    else -> editTimeSheet()
                }
            } else if (et_time.text.toString().trim().contains(".")) {
                val hour = et_time.text.toString().trim().substring(0, et_time.text.toString().trim().indexOf("."))

                when {
                    hour.toInt() == 0 -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_time))
                    hour.toInt() > 5 -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_time))
                    else -> editTimeSheet()
                }
            } else if (et_time.text.toString().trim().contains(":")) {
                val hour = et_time.text.toString().trim().substring(0, et_time.text.toString().trim().indexOf(":"))

                when {
                    hour.toInt() == 0 -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_time))
                    hour.toInt() > 5 -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_time))
                    else -> editTimeSheet()
                }
            }
        }*/
        else if (TextUtils.isEmpty(et_hrs.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_hrs))
        else if (TextUtils.isEmpty(et_mins.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_mins))
        else if (et_hrs.text.toString().trim().toInt() > 23)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_hrs))
        else if (et_mins.text.toString().trim().toInt() > 59)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_mins))
        else {
            if (timeSheet?.isUploaded!!)
                editTimeSheet()
            else {
                saveDataToDb(getString(R.string.pending))
                (mContext as DashboardActivity).showSnackMessage("Timesheet edited successfully")
                (mContext as DashboardActivity).isTimesheetAddedEdited = true
                (mContext as DashboardActivity).onBackPressed()
            }
        }
    }

    private fun saveDataToDb(mStatus: String) {
        val mDate = date
        AppDatabase.getDBInstance()?.timesheetDao()?.updateTimesheet(timeSheet?.apply {
            date = mDate
            client_id = clientId
            project_id = projectId
            activity_id = activityId
            product_id = productId

            var hrs = ""
            var mins = ""

            hrs = if (et_hrs.text.toString().trim().length == 1)
                "0" + et_hrs.text.toString().trim()
            else
                et_hrs.text.toString().trim()

            mins = if (et_mins.text.toString().trim().length == 1)
                "0" + et_mins.text.toString().trim()
            else
                et_mins.text.toString().trim()

            time = hrs + ":" + mins

            comments = et_comment.text.toString().trim()
            status = mStatus
            client_name = tv_client_dropdown.text.toString().trim()
            activity_name = tv_activity_dropdown.text.toString().trim()
            project_name = tv_project_dropdown.text.toString().trim()
            product_name = tv_product_dropdown.text.toString().trim()
            image = imagePath
        }!!)
    }

    private fun editTimeSheet() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage("Timesheet already saved in server so edit only possible if you have internet connection")
            return
        }


        XLog.d("==============Edit Timesheet Input Params (Edit Timesheet)===============")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("date=======> $date")
        XLog.d("client_id=======> $clientId")
        XLog.d("project_id=======> $projectId")
        XLog.d("activity_id=======> $activityId")
        XLog.d("product_id=======> $productId")
        XLog.d("time=======> " + et_hrs.text.toString().trim() + ":" + et_mins.text.toString().trim())
        XLog.d("comments=======> " + et_comment.text.toString().trim())
        XLog.d("timesheet_id=======> " + timeSheet?.timesheet_id)
        XLog.d("image=======> $imagePath")
        XLog.d("===========================================================================")

        var comment = ""

        if (!TextUtils.isEmpty(et_comment.text.toString().trim())) {
            if (et_comment.text.toString().trim().equals("N.A.", ignoreCase = true))
                comment = ""
            else
                comment = et_comment.text.toString().trim()
        }

        val editInput = EditTimeSheetInputModel(Pref.session_token!!, Pref.user_id!!, date, clientId, projectId, activityId, productId,
                et_hrs.text.toString().trim() + ":" + et_mins.text.toString().trim(), comment, timeSheet?.timesheet_id!!)


        progress_wheel.spin()
        if (TextUtils.isEmpty(imagePath)) {
            val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.editTimeSheet(editInput)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as EditDeleteTimesheetResposneModel
                                XLog.d("EDIT TIMESHEET: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    saveDataToDb(response.timesheet_status)
                                    (mContext as DashboardActivity).isTimesheetAddedEdited = true
                                    Handler().postDelayed(Runnable {
                                        (mContext as DashboardActivity).onBackPressed()
                                    }, 500)
                                } else if (response.status == "204") {
                                    AppDatabase.getDBInstance()?.timesheetDao()?.updateStatus(response.timesheet_status, timeSheet?.timesheet_id!!)
                                    (mContext as DashboardActivity).isTimesheetAddedEdited = true
                                    Handler().postDelayed(Runnable {
                                        (mContext as DashboardActivity).onBackPressed()
                                    }, 500)
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("EDIT TIMESHEET: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
        else {
            val repository = TimeSheetRepoProvider.timeSheetImageRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.editTimesheetWithImage(editInput, imagePath, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as EditDeleteTimesheetResposneModel
                                XLog.d("EDIT TIMESHEET: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    saveDataToDb(response.timesheet_status)
                                    (mContext as DashboardActivity).isTimesheetAddedEdited = true
                                    Handler().postDelayed(Runnable {
                                        (mContext as DashboardActivity).onBackPressed()
                                    }, 500)
                                } else if (response.status == "204") {
                                    AppDatabase.getDBInstance()?.timesheetDao()?.updateStatus(response.timesheet_status, timeSheet?.timesheet_id!!)
                                    (mContext as DashboardActivity).isTimesheetAddedEdited = true
                                    Handler().postDelayed(Runnable {
                                        (mContext as DashboardActivity).onBackPressed()
                                    }, 500)
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("EDIT TIMESHEET: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
    }
}
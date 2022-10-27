package com.kcteam.features.myjobs.presentation

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.myjobs.api.MyJobRepoProvider
import com.kcteam.features.myjobs.model.CustomerDataModel
import com.kcteam.features.myjobs.model.WIPImageSubmit
import com.kcteam.features.myjobs.model.WorkCancelledInputParams
import com.kcteam.features.myjobs.model.WorkCompletedInputParams
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class WorkCancelledFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var tv_date: AppCustomTextView
    private lateinit var tv_time: AppCustomTextView
    private lateinit var et_attachment: AppCustomEditText
    private lateinit var et_photo: AppCustomEditText
    private lateinit var et_remarks: AppCustomEditText
    private lateinit var submit_button_TV: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_work_cancellation_main: RelativeLayout
    private lateinit var et_reason: AppCustomEditText
    private lateinit var rb_by_us: RadioButton
    private lateinit var rb_by_customer: RadioButton
    private lateinit var et_canceled_by: AppCustomEditText
    private lateinit var et_lat_lng: AppCustomEditText
    private lateinit var et_address: AppCustomEditText

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    private var dataPath = ""
    private var imagePath = ""
    private var dateMilis = 0L
    private var timeMilis = 0L
    private var permissionUtils: PermissionUtils? = null
    private var isAttachment = false
    private var selectedDate = ""
    private var customerdata: CustomerDataModel? = null
    private var latitude = ""
    private var longitude = ""
    private var user = ""

    companion object {
        fun newInstance(mcustomerdata: Any): WorkCancelledFragment {
            val fragment = WorkCancelledFragment()

            if (mcustomerdata is CustomerDataModel) {
                val bundle = Bundle()
                bundle.putSerializable("customer", mcustomerdata)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        customerdata = arguments?.getSerializable("customer") as CustomerDataModel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_work_cancelled, container, false)

        initView(view)
        initClickListener()

        return  view
    }

    private fun initView(view: View) {
        view.apply {
            tv_date = findViewById(R.id.tv_date)
            tv_time = findViewById(R.id.tv_time)
            et_attachment = findViewById(R.id.et_attachment)
            et_photo = findViewById(R.id.et_photo)
            et_remarks = findViewById(R.id.et_remarks)
            submit_button_TV = findViewById(R.id.submit_button_TV)
            progress_wheel = findViewById(R.id.progress_wheel)
            rl_work_cancellation_main = findViewById(R.id.rl_work_cancellation_main)
            et_reason = findViewById(R.id.et_reason)
            rb_by_us = findViewById(R.id.rb_by_us)
            rb_by_customer = findViewById(R.id.rb_by_customer)
            et_canceled_by = findViewById(R.id.et_canceled_by)
            et_lat_lng = findViewById(R.id.et_lat_lng)
            et_address = findViewById(R.id.et_address)
        }

        latitude = Pref.current_latitude
        longitude = Pref.current_longitude
        et_lat_lng.setText("$latitude, $longitude")
        et_address.setText(LocationWizard.getNewLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()))

        progress_wheel.stopSpinning()
    }

    private fun initClickListener() {
        submit_button_TV.setOnClickListener(this)
        tv_date.setOnClickListener(this)
        tv_time.setOnClickListener(this)
        et_attachment.setOnClickListener(this)
        et_photo.setOnClickListener(this)
        rb_by_us.setOnClickListener(this)
        rb_by_customer.setOnClickListener(this)
        rl_work_cancellation_main.setOnClickListener(null)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.submit_button_TV -> {
                checkValidation()
            }

            R.id.tv_date -> {
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))

                datePicker.show()
            }

            R.id.tv_time -> {
                val cal = Calendar.getInstance(Locale.ENGLISH)

                val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)

                    timeMilis = cal.timeInMillis
                    tv_time.text = SimpleDateFormat("hh:mm a").format(cal.time)
                }

                val timePicker = TimePickerDialog(mContext, R.style.DatePickerTheme, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
                timePicker.show()
            }

            R.id.et_attachment -> {
                isAttachment = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck()
                else {
                    showPictureDialog()
                }
            }

            R.id.et_photo -> {
                isAttachment = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck()
                else {
                    (mContext as DashboardActivity).captureImage()
                }
            }

            R.id.rb_by_us -> {
                user = rb_by_us.text.toString().trim()
            }
            R.id.rb_by_customer -> {
                user = rb_by_customer.text.toString().trim()
            }
        }
    }

    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                if (isAttachment)
                    showPictureDialog()
                else
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

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture Image", "Select file from file manager")
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> selectImageInAlbum()
                        1 -> {
                            //(mContext as DashboardActivity).openFileManager()
                            launchCamera()
                        }
                        2 -> {
                            (mContext as DashboardActivity).openFileManager()
                        }
                    }
                })
        pictureDialog.show()
    }

    private fun launchCamera() {
        (mContext as DashboardActivity).captureImage()
    }

    private fun selectImageInAlbum() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        (mContext as DashboardActivity).startActivityForResult(galleryIntent, PermissionHelper.REQUEST_CODE_STORAGE)
    }

    fun setImage(file: File) {
        if (isAttachment) {
            et_attachment.setText(file.name)
            dataPath = file.absolutePath
        }
        else {
            imagePath = file.absolutePath
            et_photo.setText(file.name)
        }
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)
        tv_date.text = AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time))
        dateMilis = myCalendar.timeInMillis
    }

    private fun checkValidation() {
        when {
            TextUtils.isEmpty(tv_date.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_date))
            TextUtils.isEmpty(tv_time.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_time))
            TextUtils.isEmpty(et_reason.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_reason_cancellation))
            TextUtils.isEmpty(et_canceled_by.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_canceled_by))
            else -> {
                submitWorkCompletedApi()
            }
        }
    }

    private fun submitWorkCompletedApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val workCancelledInput = WorkCancelledInputParams(Pref.session_token!!, Pref.user_id!!, customerdata?.id!!, selectedDate, tv_time.text.toString().trim(),
                et_reason.text.toString().trim(), et_remarks.text.toString().trim(), AppUtils.getCurrentISODateTime(), latitude,
                longitude, et_address.text.toString().trim(), et_canceled_by.text.toString().trim(), user)

        progress_wheel.spin()
        if (!TextUtils.isEmpty(et_attachment.text.toString().trim()) || !TextUtils.isEmpty(et_photo.text.toString().trim())) {
            val imgList = ArrayList<WIPImageSubmit>()

            if (!TextUtils.isEmpty(et_attachment.text.toString()))
                imgList.add(WIPImageSubmit(dataPath, "attachment"))

            if (!TextUtils.isEmpty(et_photo.text.toString()))
                imgList.add(WIPImageSubmit(imagePath, "image"))

            val repository = MyJobRepoProvider.jobMultipartRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.submitWorkCancelled(workCancelledInput, imgList, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    (mContext as DashboardActivity).isSubmit = true
                                    (mContext as DashboardActivity).onBackPressed()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
        else {
            val repository = MyJobRepoProvider.jobRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.submitWorkCancelled(workCancelledInput)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    (mContext as DashboardActivity).isSubmit = true
                                    (mContext as DashboardActivity).onBackPressed()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
    }
}
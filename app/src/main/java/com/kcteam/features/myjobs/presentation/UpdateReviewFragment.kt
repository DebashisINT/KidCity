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
import android.widget.RatingBar
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
import com.kcteam.features.myjobs.model.UpdateReviewInputParams
import com.kcteam.features.myjobs.model.WIPImageSubmit
import com.kcteam.features.myjobs.model.WIPSubmit
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class UpdateReviewFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var et_attachment: AppCustomEditText
    private lateinit var et_photo: AppCustomEditText
    private lateinit var submit_button_TV: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_update_review_main: RelativeLayout
    private lateinit var et_review: AppCustomEditText
    private lateinit var ratingBusiness: RatingBar

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    private var dataPath = ""
    private var imagePath = ""
    private var permissionUtils: PermissionUtils? = null
    private var isAttachment = false

    private var customerdata: CustomerDataModel? = null

    companion object {
        fun newInstance(mcustomerdata: Any): UpdateReviewFragment {
            val fragment = UpdateReviewFragment()

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
        val view = inflater.inflate(R.layout.fragment_update_review, container, false)

        initView(view)
        initClickListener()

        return  view
    }

    private fun initView(view: View) {
        view.apply {
            et_attachment = findViewById(R.id.et_attachment)
            et_photo = findViewById(R.id.et_photo)
            submit_button_TV = findViewById(R.id.submit_button_TV)
            progress_wheel = findViewById(R.id.progress_wheel)
            rl_update_review_main = findViewById(R.id.rl_update_review_main)
            et_review = findViewById(R.id.et_review)
            ratingBusiness = findViewById(R.id.ratingBusiness)
        }

        progress_wheel.stopSpinning()
    }

    private fun initClickListener() {
        submit_button_TV.setOnClickListener(this)
        et_attachment.setOnClickListener(this)
        et_photo.setOnClickListener(this)
        rl_update_review_main.setOnClickListener(null)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.submit_button_TV -> {
                checkValidation()
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
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
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
        }
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

    private fun checkValidation() {
        when {
            TextUtils.isEmpty(et_review.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_review))
            else -> {
                updatReviewApi()
            }
        }
    }

    private fun updatReviewApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val updateReviewInput = UpdateReviewInputParams(Pref.session_token!!, Pref.user_id!!, customerdata?.id!!, et_review.text.toString().trim(),
                ratingBusiness.rating.toString(), AppUtils.getCurrentISODateTime(), Pref.current_latitude, Pref.current_longitude, LocationWizard.getNewLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()))

        progress_wheel.spin()
        if (!TextUtils.isEmpty(et_attachment.text.toString().trim()) || !TextUtils.isEmpty(et_photo.text.toString().trim())) {
            val imgList = ArrayList<WIPImageSubmit>()

            if (!TextUtils.isEmpty(et_attachment.text.toString()))
                imgList.add(WIPImageSubmit(dataPath, "attachment"))

            if (!TextUtils.isEmpty(et_photo.text.toString()))
                imgList.add(WIPImageSubmit(imagePath, "image"))

            val repository = MyJobRepoProvider.jobMultipartRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.updateReview(updateReviewInput, imgList, mContext)
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
                    repository.updateReview(updateReviewInput)
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
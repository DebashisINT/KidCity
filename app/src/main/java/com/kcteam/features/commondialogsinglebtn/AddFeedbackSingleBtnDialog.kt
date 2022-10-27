package com.kcteam.features.commondialogsinglebtn

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.PopupWindow
import android.widget.RelativeLayout
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.ProspectEntity
import com.kcteam.app.domain.ShopVisitCompetetorModelEntity
import com.kcteam.app.domain.VisitRemarksEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.presentation.ProspectListDialog
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dashboard.presentation.MeetingTypeAdapter
import com.kcteam.features.dashboard.presentation.VisitRemarksTypeAdapter
import com.kcteam.features.nearbyshops.api.ShopListRepositoryProvider
import com.kcteam.features.nearbyshops.model.ProsListResponseModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.squareup.picasso.Picasso
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.billing_adapter.view.*
import kotlinx.android.synthetic.main.dialog_add_feedback_single_btn.*
import kotlinx.android.synthetic.main.fragment_add_shop.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 31-01-2020.
 */
class AddFeedbackSingleBtnDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var dialogHeader: AppCustomTextView
    private lateinit var et_feedback: AppCustomEditText
    private lateinit var dialogOk: AppCustomTextView
    private lateinit var iv_close_icon: AppCompatImageView
    private lateinit var et_next_visit_date: AppCustomEditText
    private lateinit var tv_visit_date_asterisk_mark: AppCustomTextView
    private lateinit var et_audio: AppCustomEditText
    private lateinit var rl_audio: RelativeLayout
    private lateinit var tv_remarks_dropdown: AppCustomTextView
    private lateinit var rl_remarks: RelativeLayout
    private lateinit var til_feedback: TextInputLayout
    private lateinit var ll_competitorImg: RelativeLayout

    private lateinit var rl_approxvalue_main: RelativeLayout

    private lateinit var rl_prospect_main: RelativeLayout
    private lateinit var iv_prospect_dropdownn: AppCustomTextView

    private lateinit var et_approxvalue_name: AppCustomEditText






    private lateinit var tv_visit_asterisk_mark: AppCustomTextView

    private var visitRemarksPopupWindow: PopupWindow? = null
    private  var audioFile: File? = null
    private var nextVisitDate = ""
    private var filePath = ""

    private var ProsId = ""

    private var shopType = ""



    private val myCalendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    companion object {
        private lateinit var mHeader: String
        private lateinit var mActionBtn: String
        private lateinit var mShopID: String
        //private lateinit var mRightBtn: String
        private lateinit var mListener: OnOkClickListener

        fun getInstance(header: String, actionBtn: String,shopID: String, listener: OnOkClickListener): AddFeedbackSingleBtnDialog {
            val cardFragment = AddFeedbackSingleBtnDialog()
            mHeader = header
            mActionBtn = actionBtn
            mShopID = shopID
            mListener = listener
            return cardFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_add_feedback_single_btn, container, false)
        initView(v)

        isCancelable = false

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(v: View) {
        dialogHeader = v.findViewById(R.id.dialog_header_TV)
        et_feedback = v.findViewById(R.id.et_feedback)
        dialogOk = v.findViewById(R.id.ok_TV)
        dialogOk.isSelected = false
        dialogOk.text = mActionBtn
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        et_next_visit_date = v.findViewById(R.id.et_next_visit_date)
        tv_visit_date_asterisk_mark = v.findViewById(R.id.tv_visit_date_asterisk_mark)
        tv_visit_asterisk_mark = v.findViewById(R.id.tv_visit_asterisk_mark)
        et_audio = v.findViewById(R.id.et_audio)
        rl_audio = v.findViewById(R.id.rl_audio)
        tv_remarks_dropdown = v.findViewById(R.id.tv_remarks_dropdown)
        rl_remarks = v.findViewById(R.id.rl_remarks)
        til_feedback = v.findViewById(R.id.til_feedback)
        ll_competitorImg = v.findViewById(R.id.rl_competitor_image)

        /*12-12-2021*/
        rl_approxvalue_main = v.findViewById(R.id.rl_approxvalue_main)
        rl_prospect_main = v.findViewById(R.id.rl_prospect_main)
        iv_prospect_dropdownn = v.findViewById(R.id.iv_prospect_dropdownn)
        et_approxvalue_name =  v.findViewById(R.id.et_approxvalue_name)

        /*13-12-2021*/
        shopType = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopType(mShopID).toString()

        dialogHeader.text = mHeader

        if (Pref.isNextVisitDateMandatory) {
            tv_visit_date_asterisk_mark.visibility = View.VISIBLE
            //iv_close_icon.visibility = View.GONE
        } else {
            tv_visit_date_asterisk_mark.visibility = View.GONE
            //iv_close_icon.visibility = View.VISIBLE
        }

        if (Pref.isRecordAudioEnable)
            rl_audio.visibility = View.VISIBLE
        else
            rl_audio.visibility = View.GONE

        if (Pref.isRecordAudioEnable || Pref.isNextVisitDateMandatory)
            iv_close_icon.visibility = View.GONE
        else
            iv_close_icon.visibility = View.VISIBLE

        if (Pref.isShowVisitRemarks) {
            rl_remarks.visibility = View.VISIBLE
            til_feedback.visibility = View.GONE
        }
        else {
            rl_remarks.visibility = View.GONE
            til_feedback.visibility = View.VISIBLE
        }

        if(AppUtils.getSharedPreferenceslogCompetitorImgEnable(mContext))
            ll_competitorImg.visibility=View.VISIBLE
        else
            ll_competitorImg.visibility=View.GONE


        if(Pref.IsnewleadtypeforRuby && shopType.equals("16")){
            rl_approxvalue_main.visibility = View.VISIBLE
            rl_prospect_main.visibility = View.VISIBLE
            ll_competitorImg.visibility=View.GONE
//            var list  = AppDatabase.getDBInstance()!!.shopActivityDao().getAll()
//            if(list.size==0){
//                Toaster.msgShort(mContext, "please wait,background data under snyc")
//                return
//            }
//            else{
                var shopActivityListToProsId = AppDatabase.getDBInstance()!!.shopActivityDao().getProsId(mShopID).toString()
                var prosNameByID=""
                if(shopActivityListToProsId!=null || !shopActivityListToProsId.equals("")){
                    prosNameByID = AppDatabase.getDBInstance()!!.prosDao().getProsNameByProsId(shopActivityListToProsId)
                }
                iv_prospect_dropdownn.text = prosNameByID // select pros name showing
                ProsId=shopActivityListToProsId


        }




        /*28-09-2021 For Gupta Power*/
        if (Pref.RevisitRemarksMandatory) {
            tv_visit_asterisk_mark.visibility = View.VISIBLE

        } else {
            tv_visit_asterisk_mark.visibility = View.GONE
        }

        dialogOk.setOnClickListener(this)
        iv_close_icon.setOnClickListener(this)
        et_next_visit_date.setOnClickListener(this)
        et_audio.setOnClickListener(this)
        tv_remarks_dropdown.setOnClickListener(this)

        ll_competitorImg.setOnClickListener(this)
        rl_prospect_main.setOnClickListener(this)
        rl_approxvalue_main.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
//        deSelectAll()
        when (p0!!.id) {
            R.id.ok_TV -> {
                iv_close_icon.isEnabled=true

                var msg = ""
                    if(TextUtils.isEmpty(tv_remarks_dropdown.text.toString().trim()) && Pref.isShowVisitRemarks)
                        msg =  "Please put the remarks"
                    else if(TextUtils.isEmpty(et_feedback.text.toString().trim()))
                        msg = "Please put the feedback"


                //if (Pref.RevisitRemarksMandatory && TextUtils.isEmpty(tv_remarks_dropdown.text.toString().trim()))
                if (Pref.RevisitRemarksMandatory && !msg.equals(""))
                    Toaster.msgShort(mContext, msg)
                else if (Pref.isNextVisitDateMandatory && TextUtils.isEmpty(nextVisitDate))
                    Toaster.msgShort(mContext, getString(R.string.error_message_next_visit_date))
                else if (Pref.isRecordAudioEnable && TextUtils.isEmpty(et_audio.text.toString().trim()))
                    Toaster.msgShort(mContext, getString(R.string.error_message_audio))
                else if (Pref.IsnewleadtypeforRuby && shopType.equals("16") && TextUtils.isEmpty(et_approxvalue_name.text.toString().trim()))
                    Toaster.msgShort(mContext, getString(R.string.error_message_approx))
                else {
                    dialogOk.isSelected = true
                    dismiss()
                    if (Pref.RevisitRemarksMandatory){
                        mListener.onOkClick(tv_remarks_dropdown.text.toString().trim(), nextVisitDate, filePath,et_approxvalue_name.text.toString(),ProsId)
                    }
                    else{
                        if (!Pref.isShowVisitRemarks)
                            mListener.onOkClick(et_feedback.text.toString().trim(), nextVisitDate, filePath,et_approxvalue_name.text.toString(),ProsId)
                        else
                            mListener.onOkClick(tv_remarks_dropdown.text.toString().trim(), nextVisitDate, filePath,et_approxvalue_name.text.toString(),ProsId)
                    }
                }
            }
            R.id.iv_close_icon -> {

                var msg = ""
                if(TextUtils.isEmpty(tv_remarks_dropdown.text.toString().trim()) && Pref.isShowVisitRemarks)
                    msg =  "Please put the remarks"
                else if(TextUtils.isEmpty(et_feedback.text.toString().trim()))
                    msg = "Please put the feedback"

                //if (Pref.RevisitRemarksMandatory && TextUtils.isEmpty(tv_remarks_dropdown.text.toString().trim()))
                if (Pref.RevisitRemarksMandatory && !msg.equals(""))
                    Toaster.msgShort(mContext, msg)
                else{
                    dismiss()
                    mListener.onCloseClick(tv_remarks_dropdown.text.toString().trim())
                }
            }

            R.id.et_next_visit_date -> {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val aniDatePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                aniDatePicker.datePicker.minDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis + (1000 * 60 * 60 * 24)
                aniDatePicker.show()
            }

            R.id.et_audio -> {
                val folderPath = FTStorageUtils.getFolderPath(mContext)
                audioFile = File("$folderPath/" + System.currentTimeMillis() + ".mp3")

                AndroidAudioRecorder.with(mContext as DashboardActivity)
                        // Required
                        .setFilePath(audioFile?.absolutePath)
                        .setColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                        .setRequestCode(PermissionHelper.REQUEST_CODE_AUDIO)
                        .setAutoStart(false)
                        .setKeepDisplayOn(true)

                        // Start recording
                        .record()
            }

            R.id.tv_remarks_dropdown -> {
                val list = AppDatabase.getDBInstance()?.visitRemarksDao()?.getAll()
                if (list == null || list.isEmpty())
                    Toaster.msgShort(mContext, getString(R.string.no_data_found))
                else {
                    if (visitRemarksPopupWindow != null && visitRemarksPopupWindow?.isShowing!!)
                        visitRemarksPopupWindow?.dismiss()

                    callMeetingTypeDropDownPopUp(list)
                }
            }
            R.id.rl_competitor_image -> {
                mListener.onClickCompetitorImg()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheckOne()
                else
                    showPictureDialog()
            }

            /*12-12-2021*/
            R.id.rl_prospect_main -> {
                val list = AppDatabase.getDBInstance()?.prosDao()?.getAll() as ArrayList<ProspectEntity>
                if (list == null || list.isEmpty())
                    getProspectApi()
                else
                    showProsDialog(list)
            }


            R.id.rl_approxvalue_main->{

            }

        }
    }

    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheckOne() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                showPictureDialog()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> selectImageInAlbum()
                1 -> launchCamera()
            }
        }
        pictureDialog.show()
    }

    fun selectImageInAlbum() {
        if (PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_STORAGE)

        }
    }

    fun launchCamera() {
        if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, (mContext as DashboardActivity).getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)*/

            (mContext as DashboardActivity).captureImage()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callMeetingTypeDropDownPopUp(list: List<VisitRemarksEntity>) {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Inflate the custom layout/view
        val customView = inflater!!.inflate(R.layout.popup_meeting_type, null)

        visitRemarksPopupWindow = PopupWindow(customView, resources.getDimensionPixelOffset(R.dimen._220sdp), RelativeLayout.LayoutParams.WRAP_CONTENT)
        val rv_meeting_type_list = customView.findViewById(R.id.rv_meeting_type_list) as RecyclerView
        rv_meeting_type_list.layoutManager = LinearLayoutManager(mContext)

        visitRemarksPopupWindow?.elevation = 200f
        visitRemarksPopupWindow?.isFocusable = true
        visitRemarksPopupWindow?.update()


        rv_meeting_type_list.adapter = VisitRemarksTypeAdapter(mContext, list as ArrayList<VisitRemarksEntity>, object : VisitRemarksTypeAdapter.OnItemClickListener {
            override fun onItemClick(adapterPosition: Int) {
                tv_remarks_dropdown.text = list[adapterPosition].name
                visitRemarksPopupWindow?.dismiss()
            }
        })

        if (visitRemarksPopupWindow != null && !visitRemarksPopupWindow?.isShowing!!) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                rl_remarks.post(Runnable {
                    visitRemarksPopupWindow?.showAsDropDown(tv_remarks_dropdown, resources.getDimensionPixelOffset(R.dimen._1sdp), resources.getDimensionPixelOffset(R.dimen._10sdp), Gravity.BOTTOM)
                })
            } else {
                visitRemarksPopupWindow?.showAsDropDown(tv_remarks_dropdown, tv_remarks_dropdown.width - visitRemarksPopupWindow?.width!!, 0)
            }
        }
    }

    val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        nextVisitDate = AppUtils.getFormattedDateForApi(myCalendar.time)
        et_next_visit_date.setText(AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time)))
    }

    private fun deSelectAll() {
        dialogOk.isSelected = false
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            //if (!dialog.isShowing) {
            val ft = manager?.beginTransaction()
            ft?.add(this, tag)
            ft?.commitAllowingStateLoss()
            //}
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    interface OnOkClickListener {
        fun onOkClick(feedback: String, nextVisitDate: String, filePath: String,approxValue:String,prosId:String)

        fun onCloseClick(mfeedback: String)

        fun onClickCompetitorImg()
    }

    fun setAudio(){
        filePath = audioFile?.absolutePath!!
        et_audio.setText(audioFile?.absolutePath)
    }

    fun setImage(imgRealPath: Uri, fileSizeInKB: Long) {
        val imagePathCompetitor = imgRealPath.toString()
        Picasso.get()
                .load(imgRealPath)
                .resize(500, 100)
                .into(iv_competitor_image_view_feedback)

        var obj: ShopVisitCompetetorModelEntity = ShopVisitCompetetorModelEntity()
        obj.session_token=Pref.session_token!!
        obj.shop_id=mShopID
        obj.user_id=Pref.user_id!!
        obj.shop_image=imagePathCompetitor
        obj.isUploaded=false
        obj.visited_date=AppUtils.getCurrentDateTime()
        AppDatabase.getDBInstance()!!.shopVisitCompetetorImageDao().insert(obj)

        iv_close_icon.isEnabled=false
    }

    private fun getProspectApi() {
        try {
            val list = AppDatabase.getDBInstance()?.prosDao()?.getAll()
            if (list!!.size == 0) {
                val repository = ShopListRepositoryProvider.provideShopListRepository()
                BaseActivity.compositeDisposable.add(
                        repository.getProsList()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val response = result as ProsListResponseModel
                                    XLog.d("GET PROS DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                                    if (response.status == NetworkConstant.SUCCESS) {
                                        if (response.Prospect_list != null && response.Prospect_list!!.isNotEmpty()) {
                                            doAsync {
                                                AppDatabase.getDBInstance()?.prosDao()?.insertAll(response.Prospect_list!!)
                                                uiThread {

                                                }
                                            }
                                        } else {

                                        }
                                    } else {

                                    }

                                }, { error ->


                                })
                )
            } else {

            }
        } catch (ex: Exception) {
            ex.printStackTrace()


        }

    }

    private fun showProsDialog(prosList: ArrayList<ProspectEntity>) {
        ProspectListDialog.newInstance(prosList) { pros: ProspectEntity ->
            iv_prospect_dropdownn.text = pros.pros_name
            ProsId = pros.pros_id!!
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }
}
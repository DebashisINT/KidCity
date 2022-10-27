package com.kcteam.features.survey

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.damageProduct.ShopDamageProductSubmitFrag
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.marketing.model.MarketingDetailImageData
import com.kcteam.features.photoReg.api.GetUserListPhotoRegProvider
import com.kcteam.features.photoReg.model.ImageResponse
import com.kcteam.features.survey.api.SurveyDataProvider
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList

class SurveyFrag: BaseFragment(), View.OnClickListener {
    private lateinit var mContext: Context
    lateinit var ll_root: LinearLayout
    lateinit var progress_wheel: ProgressWheel
    lateinit var tv_surveyFromType:AppCustomTextView
    lateinit var iv_arrow_dd:ImageView
    lateinit var btnSubmit:Button

    lateinit var picView:ImageView

    var survayID = ""
    private var adapterDropDD: AdapterSurveyDD? = null

    private var qList:ArrayList<Question_list> = ArrayList()
    private var finalSaveList:ArrayList<Question_list> = ArrayList()
    private var qaImgList:ArrayList<SurveyQAIMGModel> = ArrayList()
    private var qaImgObj:SurveyQAIMGModel = SurveyQAIMGModel()

    private var imagePath: String = ""
    private var toastFormat: String = "Please answer the question of "

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var mAddShopDataObj: AddShopDBModelEntity? = null
        var shop_id:String = ""
        fun getInstance(objects: Any): SurveyFrag {
            val fragment = SurveyFrag()
            if (!TextUtils.isEmpty(objects.toString())) {
                shop_id=objects.toString()
                mAddShopDataObj = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_survey, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View?) {
        ll_root=view?.findViewById(R.id.lll_root_qa) as LinearLayout
        progress_wheel=view?.findViewById(R.id.progress_wheel) as ProgressWheel
        tv_surveyFromType = view?.findViewById(R.id.survey_from_typeSpinner) as AppCustomTextView
        iv_arrow_dd =  view?.findViewById(R.id.iv_dropd) as ImageView
        btnSubmit =  view?.findViewById(R.id.btn_frag_survey_submit) as Button
        btnSubmit.visibility=View.GONE
        btnSubmit.setOnClickListener(this)

        initPermissionCheckFirstTime()
        getQuestion()
    }

    fun getQuestion(){
        qList=ArrayList()
        try{
            val repository = SurveyDataProvider.provideSurveyQ()
            BaseActivity.compositeDisposable.add(
                repository.provideSurveyQApi(Pref.session_token!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        progress_wheel.stopSpinning()
                        var response = result as QaListResponseModel
                        if (response.status == NetworkConstant.SUCCESS) {
                            doAsync {
                                if(response.Question_list!=null){
                                    qList=response.Question_list!! as ArrayList<Question_list>
                                }
                                uiThread {
                                    filterData()
                                }
                            }
                        }else {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_date_found))
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        error.printStackTrace()
                        (mContext as DashboardActivity).showSnackMessage("ERROR")
                    })
            )
        }catch (ex:Exception){
            progress_wheel.stopSpinning()
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage("ERROR")
        }
    }

    fun filterData(){
        var listWithID :ArrayList<Question_list> = ArrayList()
        listWithID= qList.filter { it.question_for_shoptype_id!!.toInt() == mAddShopDataObj!!.type.toInt() } as ArrayList<Question_list>
        var uniqGr = listWithID.map { it.group_name }.distinct()
        formSelectionDD(uniqGr!! as List<String>,listWithID)

    }

    fun formSelectionDD(uniqGr:List<String>,listWithID:ArrayList<Question_list>){
        if(uniqGr.size>1){
            tv_surveyFromType.setEnabled(true)
            iv_arrow_dd.visibility = View.VISIBLE
            tv_surveyFromType.setOnClickListener {
                SurveyFromListDialog.newInstance(uniqGr as ArrayList<String>) {

                    ll_root.removeAllViewsInLayout()

                    tv_surveyFromType.text = it
                    filterDataByGr(listWithID.filter { it.group_name.equals(tv_surveyFromType.text.toString()) })
                }.show((mContext as DashboardActivity).supportFragmentManager, "")
            }
        }
        else{
            iv_arrow_dd.visibility = View.GONE
            tv_surveyFromType.setEnabled(false)
            tv_surveyFromType.text = uniqGr.get(0).toString()
            filterDataByGr(listWithID.filter { it.group_name.equals(uniqGr.get(0)) })
        }
    }

    fun filterDataByGr(filteredList:List<Question_list>){
        finalSaveList=filteredList as ArrayList<Question_list>
        for(i in 0..filteredList.size-1){
            var obj=filteredList.get(i)
            if(obj.question_type.equals("Dropdown")){
                var qValueList :List<String> = obj.question_value!!.split(",")
                createDynaViewDropDown(obj.question_id.toString(),obj.question_desc!!,qValueList,false)
            }
            if(obj.question_type.equals("DropdownMulti")){
                var list1=obj.question_value!!.split("|").get(0).split(",")
                var list2=obj.question_value!!.split("|").get(1).split(",")
                createDynaViewTwoDropDown(obj.question_id.toString(),obj.question_desc!!,list1,list2,false)
            }
            if(obj.question_type.equals("Checkbox")){
                var qValueList :List<String> = obj.question_value!!.split(",")
                createDynaViewDropDown(obj.question_id.toString(),obj.question_desc!!,qValueList,true)
            }
            if(obj.question_type.equals("Text")){
                createDynaViewEdittext(obj.question_id.toString(),obj.question_desc!!,true,obj.question_value!!)
            }
            if(obj.question_type.equals("TextNumber")){
                createDynaViewEdittext(obj.question_id.toString(),obj.question_desc!!,false,obj.question_value!!)
            }
            if(obj.question_type.equals("TextImage")){
                createDynaViewEdittextWithIMG(obj.question_id.toString(),obj.question_desc!!,true,obj.question_value!!)
            }
        }
        btnSubmit.visibility=View.VISIBLE
    }



    var vi:LayoutInflater ? = null
    var viewC: View? = null
    private fun createDynaViewDropDown(qID:String,qDesc:String,valueList:List<String>,isMulti:Boolean){
        vi = mContext.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewC = vi!!.inflate(R.layout.item_survey_dropdown, null)
        ll_root.addView(viewC)

        var v:View = ll_root.getChildAt(ll_root.childCount - 1)
        var dropDText: TextView =(v.findViewById(R.id.tv_item_survey_dropdown) as TextView)
        var answerTV: TextView =(v.findViewById(R.id.tv_item_survey_dd_ans) as TextView)
        var rootV: LinearLayout =(v.findViewById(R.id.ll_item_survey_dropdown_root) as LinearLayout)
        dropDText.text=qDesc

        var list:ArrayList<CheckB> = ArrayList()
        for(i in 0..valueList.size-1){
            list.add(CheckB(false,valueList.get(i)))
        }

        answerTV.setOnClickListener {
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(true)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_qa_survey)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_qa_headerTV) as AppCustomTextView
            val  rv_QAList = simpleDialog.findViewById(R.id.rv_qa_list) as RecyclerView
            val dialog_qa_cancel =  simpleDialog.findViewById(R.id.dialog_qa_cancel) as AppCustomTextView
            val dialog_qa_ok =  simpleDialog.findViewById(R.id.dialog_qa_ok) as AppCustomTextView
            rv_QAList.layoutManager = LinearLayoutManager(mContext)
            dialogHeader.text=qDesc

            dialog_qa_cancel.setOnClickListener {
                simpleDialog.cancel()
            }
            dialog_qa_ok.setOnClickListener {
                simpleDialog.cancel()
            }

            adapterDropDD=AdapterSurveyDD(mContext,list,isMulti,object : AdapterSurveyDD.OnOkClick{
                override fun onCheckClick(_list: ArrayList<CheckB>) {
                    list=_list
                    var answ = ""
                    for(i in 0.._list.size-1){
                        if(_list.get(i).isChk){
                            answ=answ+_list.get(i).value+","
                        }
                    }
                    answerTV.text=answ.dropLast(1)
                }
            })
            rv_QAList.adapter = adapterDropDD
            simpleDialog.show()
        }

    }

    private fun createDynaViewTwoDropDown(qID:String,qDesc:String,valueList1:List<String>,valueList2:List<String>,isMulti:Boolean){
        vi = mContext.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewC = vi!!.inflate(R.layout.item_survey_dropdown_two, null)
        ll_root.addView(viewC)

        var v:View = ll_root.getChildAt(ll_root.childCount - 1)
        var dropDText: TextView =(v.findViewById(R.id.tv_item_survey_dropdown2) as TextView)
        var answerTV: TextView =(v.findViewById(R.id.tv_item_survey_dd_ans2) as TextView)
        var answerTV1: TextView =(v.findViewById(R.id.tv_item_survey_dd_ans2_1) as TextView)
        dropDText.text=qDesc

        var list:ArrayList<CheckB> = ArrayList()
        for(i in 0..valueList1.size-1){
            list.add(CheckB(false,valueList1.get(i)))
        }

        var list1:ArrayList<CheckB> = ArrayList()
        for(i in 0..valueList2.size-1){
            list1.add(CheckB(false,valueList2.get(i)))
        }

        answerTV.setOnClickListener {
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(true)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_qa_survey)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_qa_headerTV) as AppCustomTextView
            val  rv_QAList = simpleDialog.findViewById(R.id.rv_qa_list) as RecyclerView
            val dialog_qa_cancel =  simpleDialog.findViewById(R.id.dialog_qa_cancel) as AppCustomTextView
            val dialog_qa_ok =  simpleDialog.findViewById(R.id.dialog_qa_ok) as AppCustomTextView
            rv_QAList.layoutManager = LinearLayoutManager(mContext)
            dialogHeader.text=qDesc

            dialog_qa_cancel.setOnClickListener {
                simpleDialog.cancel()
            }
            dialog_qa_ok.setOnClickListener {
                simpleDialog.cancel()
            }

            adapterDropDD=AdapterSurveyDD(mContext,list,isMulti,object : AdapterSurveyDD.OnOkClick{
                override fun onCheckClick(_list: ArrayList<CheckB>) {
                    list=_list
                    var answ = ""
                    for(i in 0.._list.size-1){
                        if(_list.get(i).isChk){
                            answ=answ+_list.get(i).value+","
                        }
                    }
                    answerTV.text=answ.dropLast(1)
                }
            })
            rv_QAList.adapter = adapterDropDD
            simpleDialog.show()
        }
        answerTV1.setOnClickListener {
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(true)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_qa_survey)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_qa_headerTV) as AppCustomTextView
            val  rv_QAList = simpleDialog.findViewById(R.id.rv_qa_list) as RecyclerView
            val dialog_qa_cancel =  simpleDialog.findViewById(R.id.dialog_qa_cancel) as AppCustomTextView
            val dialog_qa_ok =  simpleDialog.findViewById(R.id.dialog_qa_ok) as AppCustomTextView
            rv_QAList.layoutManager = LinearLayoutManager(mContext)
            dialogHeader.text=qDesc

            dialog_qa_cancel.setOnClickListener {
                simpleDialog.cancel()
            }
            dialog_qa_ok.setOnClickListener {
                simpleDialog.cancel()
            }

            adapterDropDD=AdapterSurveyDD(mContext,list1,isMulti,object : AdapterSurveyDD.OnOkClick{
                override fun onCheckClick(_list: ArrayList<CheckB>) {
                    list1=_list
                    var answ = ""
                    for(i in 0.._list.size-1){
                        if(_list.get(i).isChk){
                            answ=answ+_list.get(i).value+","
                        }
                    }
                    answerTV1.text=answ.dropLast(1)
                }
            })
            rv_QAList.adapter = adapterDropDD
            simpleDialog.show()
        }

    }

    private fun createDynaViewEdittext(qID:String,qDesc:String,onlyText:Boolean,hint:String) {
        vi = mContext.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewC = vi!!.inflate(R.layout.item_survey_ettext, null)
        ll_root.addView(viewC)

        var v:View = ll_root.getChildAt(ll_root.childCount - 1)
        var tvQText: TextView =(v.findViewById(R.id.tv_item_survey_qtext) as TextView)
        var etAText: TextView =(v.findViewById(R.id.edt_item_survey_ettext) as EditText)

        if(onlyText){
            etAText.setInputType(InputType.TYPE_CLASS_TEXT)
        }else {
            etAText.setInputType(InputType.TYPE_CLASS_NUMBER)
        }

        tvQText.text=qDesc
        etAText.setHint(hint)
    }

    private fun createDynaViewEdittextWithIMG(qID:String,qDesc:String,onlyText:Boolean,hint:String) {
        vi = mContext.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewC = vi!!.inflate(R.layout.item_survey_ettext_img, null)
        ll_root.addView(viewC)

        var v:View = ll_root.getChildAt(ll_root.childCount - 1)
        var tvQText: TextView =(v.findViewById(R.id.tv_item_survey_qtext1) as TextView)
        var etAText: TextView =(v.findViewById(R.id.edt_item_survey_ettext1) as EditText)
        var imageV: ImageView =(v.findViewById(R.id.iv_upload_image_view_survey) as ImageView)

        if(onlyText){
            etAText.setInputType(InputType.TYPE_CLASS_TEXT)
        }else {
            etAText.setInputType(InputType.TYPE_CLASS_NUMBER)
        }
        imageV.setOnClickListener {
            qaImgObj= SurveyQAIMGModel(Pref.user_id.toString(),Pref.session_token!!,qID,"","")
            picView = imageV
            showPictureDialog()
        }

        tvQText.text=qDesc
        etAText.setHint(hint)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_frag_survey_submit -> {
                var saveQAModel = SaveQAModel()
                saveQAModel.user_id=Pref.user_id.toString()
                saveQAModel.shop_id= shop_id
                saveQAModel.survey_id= Pref.user_id+"_"+AppUtils.getCurrentDateTime().replace("-","").replace(" ","").replace(":","")
                saveQAModel.date_time=AppUtils.getCurrentDateTime()
                saveQAModel.group_name=tv_surveyFromType.text.toString()
                saveQAModel.question_for_shoptype_id= mAddShopDataObj!!.type
                saveQAModel.answer_list= ArrayList()

                survayID=saveQAModel.survey_id

                for(i in 0..ll_root.childCount-1){
                    var obj = SaveQAListModel()
                    obj.question_id=finalSaveList.get(i).question_id.toString()

                    var vii: View = ll_root.getChildAt(i)
                    var ansCapture=""

                    if(finalSaveList.get(i).question_type.equals("Dropdown") || finalSaveList.get(i).question_type.equals("Checkbox")){
                        var tv_ans = vii.findViewById(R.id.tv_item_survey_dd_ans) as TextView
                        ansCapture=tv_ans.text.toString()
                        if(finalSaveList.get(i).question_desc!!.contains("*") && ansCapture.equals("")){
                            (mContext as DashboardActivity).showSnackMessage("$toastFormat  '${finalSaveList.get(i).question_desc!!}'")
                            return
                        }
                    }else if(finalSaveList.get(i).question_type.equals("Text") || finalSaveList.get(i).question_type.equals("TextNumber")){
                        var et_ans = vii.findViewById(R.id.edt_item_survey_ettext) as EditText
                        ansCapture=et_ans.text.toString()
                        if(finalSaveList.get(i).question_desc!!.contains("*") && ansCapture.equals("")){
                            (mContext as DashboardActivity).showSnackMessage("$toastFormat  '${finalSaveList.get(i).question_desc!!}'")
                            return
                        }
                    }else if(finalSaveList.get(i).question_type.equals("DropdownMulti")){
                        var tv_ans1 = vii.findViewById(R.id.tv_item_survey_dd_ans2) as TextView
                        var tv_ans2 = vii.findViewById(R.id.tv_item_survey_dd_ans2_1) as TextView
                        ansCapture=tv_ans1.text.toString()+","+tv_ans2.text.toString()
                    }else if(finalSaveList.get(i).question_type.equals("TextImage")){
                        var et_ans = vii.findViewById(R.id.edt_item_survey_ettext1) as EditText
                        ansCapture=et_ans.text.toString()
                        if(finalSaveList.get(i).question_desc!!.contains("*") && ansCapture.equals("")){
                            (mContext as DashboardActivity).showSnackMessage("$toastFormat  '${finalSaveList.get(i).question_desc!!}'")
                            return
                        }
                        var imageObj = qaImgList.filter { it.question_id.equals(finalSaveList.get(i).question_id) }
                        if(imageObj.size==0 && finalSaveList.get(i).question_desc!!.contains("*")){
                            (mContext as DashboardActivity).showSnackMessage("Please attach the picture.")
                            return
                        }
                    }
                    obj.answer=ansCapture
                    saveQAModel.answer_list!!.add(obj)
                }
                getQuestionSubmit(saveQAModel)
            }
        }
    }

    fun getQuestionSubmit(saveQAModel: SaveQAModel){
        progress_wheel.spin()
        try{
            val repository = SurveyDataProvider.provideSurveyQ()
            BaseActivity.compositeDisposable.add(
                repository.provideSurveySubmitApi(saveQAModel)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        progress_wheel.stopSpinning()
                        var response = result as BaseResponse
                        if (response.status == NetworkConstant.SUCCESS) {
                            Handler().postDelayed(Runnable {
                                if(qaImgList.size==0){
                                    dialogOpen()
                                }else{
                                    if(qaImgList.size>0){
                                        for(i in 0..qaImgList.size-1){
                                            qaImgList.get(i).survey_id = survayID
                                        }
                                    }
                                    imgwithQuestionSubmit()
                                }
                            }, 300)
                        }else {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        error.printStackTrace()
                        (mContext as DashboardActivity).showSnackMessage("ERROR..")
                    })
            )
        }catch (ex:Exception){
            progress_wheel.stopSpinning()
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage("ERROR")
        }
    }



    private fun imgwithQuestionSubmit() {
        var obj = SurveyQAIMGModel()
        if(qaImgList.size>0){
            obj=qaImgList.get(0)
        }
        if(obj.survey_id.equals("")){
            dialogOpen()
        }else{
            progress_wheel.spin()
            var uniqNo = AppUtils.getCurrentDateTime().replace("-","").replace(" ","").replace(":","")
            val repository = SurveyDataProvider.provideSurveyQMultiP()
            BaseActivity.compositeDisposable.add(
                repository.addImgwithdata(obj,obj.img_link,uniqNo)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as ImageResponse
                        if(response.status== NetworkConstant.SUCCESS){
                            Handler(Looper.getMainLooper()).postDelayed({
                                progress_wheel.stopSpinning()
                                qaImgList.removeAt(0)
                                //if(qaImgList.filter { it.isUpload==false }.size>0){
                                imgwithQuestionSubmit()
                                //}else{
                                //(mContext as DashboardActivity).loadFragment(FragType.SurveyViewFrag, false, shop_id)
                                //}
                            }, 500)
                        }else{
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong1))
                        }
                    },{
                            error ->
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        progress_wheel.stopSpinning()
                    })
            )
        }


    }


    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheckFirstTime() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {

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
            (mContext as DashboardActivity).captureImage()
        }
    }

    fun setImage(file: File) {
        imagePath = file.absolutePath
        getBitmap(imagePath)
    }

    fun setImageFromPath(path: String) {
        val file = File(path)
        setImage(file)
    }

    fun getBitmap(path: String?) {
        var bitmap: Bitmap? = null
        try {
            val f = File(path)
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
            picView.setImageBitmap(bitmap)

            qaImgObj.img_link = path!!
            qaImgList.add(qaImgObj)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun dialogOpen() {
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_message)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
        dialog_yes_no_headerTV.text = "Congrats!"
        dialogHeader.text = "Hi ${Pref.user_name}! Your Survey for ${mAddShopDataObj!!.shopName} has been placed successfully. Survey number is  $survayID."
        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
            (mContext as DashboardActivity).onBackPressed()
        })
        simpleDialog.show()
    }


}

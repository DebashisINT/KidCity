package com.kcteam.features.photoReg

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder.with
import com.bumptech.glide.GenericTransitionOptions.with
import com.bumptech.glide.Glide.with
import com.kcteam.R
import com.kcteam.app.*
import com.kcteam.app.domain.OrderStatusRemarksModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.*
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.model.AddLogReqData
import com.kcteam.features.addshop.presentation.AddShopFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.ShopFeedbackEntity
import com.kcteam.features.login.model.ShopFeedbackResponseModel
import com.kcteam.features.myjobs.model.WIPImageSubmit
import com.kcteam.features.orderList.api.neworderlistapi.NewOrderListRepoProvider
import com.kcteam.features.photoReg.adapter.AdapterUserList
import com.kcteam.features.photoReg.adapter.PhotoRegUserListner
import com.kcteam.features.photoReg.api.GetUserListPhotoRegProvider
import com.kcteam.features.photoReg.model.*
import com.kcteam.features.reimbursement.presentation.FullImageDialog
import com.kcteam.features.shopdetail.presentation.ShopDetailFragment
import com.kcteam.features.shopdetail.presentation.api.EditShopRepoProvider
import com.kcteam.mappackage.SendBrod
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.elvishew.xlog.XLog
import com.hahnemann.features.commondialog.presentation.CommonDialogTripleBtn
import com.hahnemann.features.commondialog.presentation.CommonTripleDialogClickListener
import com.squareup.picasso.*
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.customnotification.view.*
import kotlinx.android.synthetic.main.fragment_photo_registration.*
import kotlinx.android.synthetic.main.row_user_list_face_regis.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileInputStream
import java.net.URLEncoder

class ProtoRegistrationFragment:BaseFragment(),View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mRv_userList: RecyclerView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    var userList: ArrayList<UserListResponseModel> = ArrayList()
    var userList_temp: ArrayList<UserListResponseModel> = ArrayList()
    private var adapter: AdapterUserList? = null
    private var str_aadhaarNo: String = ""

    private lateinit var et_attachment: AppCustomEditText
    private lateinit var et_photo: AppCustomEditText

    private var isAttachment = false
    private var dataPath = ""
    private var imagePath = ""

    private var aadhaarList: ArrayList<String> = ArrayList()

    private data class AadhaarListUser(var user_id: String, var RegisteredAadhaarNo: String)
    private var aadhaarListUserList:ArrayList<AadhaarListUser> = ArrayList()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var user_uid: String = ""
        fun getInstance(objects: Any): ProtoRegistrationFragment {
            val protoRegistrationFragment = ProtoRegistrationFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                user_uid = objects.toString()
            }
            return protoRegistrationFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_photo_registration, container, false)
        initView(view)


        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    userList?.let {
                        adapter?.refreshList(it)
                        tv_cust_no.text = "Total customer(s): " + it.size
                    }
                } else {
                    adapter?.filter?.filter(query)
                }
            }
        })

        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initView(view: View) {
        et_attachment = view!!.findViewById(R.id.et_attachment)
        et_photo = view!!.findViewById(R.id.et_photo)
        mRv_userList = view!!.findViewById(R.id.rv_frag_photo_reg)
        progress_wheel = view.findViewById(R.id.progress_wheel)

        mRv_userList.layoutManager = LinearLayoutManager(mContext)


        initPermissionCheck()

        progress_wheel.spin()
        Handler(Looper.getMainLooper()).postDelayed({
            callUSerListApi()
        }, 300)

    }



    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                /*if(SDK_INT >= 30){
                    if (!Environment.isExternalStorageManager()){
                        requestPermission()
                    }else{
                        callUSerListApi()
                    }
                }else{
                    callUSerListApi()
                }*/

                //callUSerListApi()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun callUSerListApi() {
        userList.clear()
        aadhaarList.clear()
        val repository = GetUserListPhotoRegProvider.provideUserListPhotoReg()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getUserListApi(Pref.user_id!!, Pref.session_token!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            var response = result as GetUserListResponse
                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.user_list!!.size > 0 && response.user_list!! != null) {

                                    doAsync {
                                        userList = response.user_list!!
                                        /*for(j in 0..userList.size-1){
                                            if(userList.get(j).IsAadhaarRegistered!!){
                                                aadhaarList.add(userList.get(j).RegisteredAadhaarNo!!)
                                            }
                                        }*/

                                        uiThread {
                                            callAllUserAadhaarDetailsApi()
                                            //setAdapter()
                                        }
                                    }

                                } else {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_date_found))
                                }
//
                            } else {
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_date_found))
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )

    }

    private fun callAllUserAadhaarDetailsApi() {
        aadhaarList.clear()
        aadhaarListUserList.clear()
        val repository = GetUserListPhotoRegProvider.provideUserListPhotoReg()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getAllAadhaar(Pref.session_token!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            var response = result as GetAllAadhaarResponse
                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.all_aadhaar_list != null && response.all_aadhaar_list!!.size > 0) {
                                    doAsync {
                                        var allAadhaarList = response.all_aadhaar_list
                                        /*for(j in 0..allAadhaarList!!.size-1){
                                            aadhaarList.add(allAadhaarList.get(j).RegisteredAadhaarNo)
                                        }*/

                                        for (l in 0..response.all_aadhaar_list!!.size - 1) {
                                            aadhaarList.add(allAadhaarList!!.get(l).RegisteredAadhaarNo)
                                            var obj: AadhaarListUser = AadhaarListUser("", "")
                                            obj.user_id = response.all_aadhaar_list!!.get(l).user_id.toString()
                                            obj.RegisteredAadhaarNo = response.all_aadhaar_list!!.get(l).RegisteredAadhaarNo
                                            aadhaarListUserList.add(obj)
                                        }

                                        uiThread {
                                            setAdapter()
                                        }
                                    }
                                } else {
                                    setAdapter()
                                }
                            } else {
                                setAdapter()
                                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.no_date_found))
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            setAdapter()
                        })
        )
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun getBytesForMemCache(percent: Int): Int {
        val mi: ActivityManager.MemoryInfo = ActivityManager.MemoryInfo()
        val activityManager: ActivityManager = context!!.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(mi)
        val availableMemory: Double = mi.availMem.toDouble()
        return (percent * availableMemory / 100).toInt()
    }

    private fun getCustomPicasso(): Picasso? {
        val builder = Picasso.Builder(mContext)
        //set 12% of available app memory for image cache
        builder.memoryCache(LruCache(getBytesForMemCache(12)))
        //set request transformer
        val requestTransformer = Picasso.RequestTransformer { request ->
            Log.d("image request", request.toString())
            request
        }
        builder.requestTransformer(requestTransformer)
        return builder.build()
    }


    private fun setAdapter() {

        //Toast.makeText(mContext,userList.size.toString(),Toast.LENGTH_SHORT).show()


        adapter = AdapterUserList(mContext, userList!!, object : PhotoRegUserListner {

            override fun getUserInfoOnLick(obj: UserListResponseModel) {
                (mContext as DashboardActivity).loadFragment(FragType.RegisTerFaceFragment, true, obj)
            }

            override fun getPhoneOnLick(phone: String) {
                IntentActionable.initiatePhoneCall(mContext, phone)
            }

            override fun getWhatsappOnLick(phone: String) {
                var phone = "+91" + phone
                sendWhats(phone)
                //openWhatsApp(phone)
            }

            override fun deletePicOnLick(obj: UserListResponseModel) {


                val simpleDialogg = Dialog(mContext)
                simpleDialogg.setCancelable(false)
                simpleDialogg.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialogg.setContentView(R.layout.dialog_yes_no)
                val dialogHeader = simpleDialogg.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                val dialogHeaderHeader = simpleDialogg.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                //dialogHeader.text = "Are you sure?"
                dialogHeader.text = "Wish to Delete Face Registration?"
                dialogHeaderHeader.text = "Hi " + Pref.user_name!! + "!"
                val dialogYes = simpleDialogg.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                val dialogNo = simpleDialogg.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView

                dialogYes.setOnClickListener({ view ->
                    simpleDialogg.cancel()

                    if (AppUtils.isOnline(mContext)) {
                        deletePicApi(obj.user_id.toString())
                    } else {
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    }


                })
                dialogNo.setOnClickListener({ view ->
                    simpleDialogg.cancel()
                })
                simpleDialogg.show()


            }

            override fun viewPicOnLick(img_link: String, name: String) {
                progress_wheel.spin()
                val simpleDialogg = Dialog(mContext)
                simpleDialogg.setCancelable(true)
                simpleDialogg.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialogg.setContentView(R.layout.view_face_img)


                val faceImg = simpleDialogg.findViewById(R.id.iv_face_img) as ImageView
                faceImg.setImageDrawable(null)
                faceImg.setBackgroundDrawable(null)
                faceImg.invalidate();
                faceImg.setImageBitmap(null);
                val faceName = simpleDialogg.findViewById(R.id.face_name) as AppCustomTextView
                val faceCanel = simpleDialogg.findViewById(R.id.iv_face_reg_cancel) as ImageView
                faceName.text = name

                //var ppiic=Picasso.setSingletonInstance(getCustomPicasso()!!)
                //Picasso.get().load(img_link).resize(500, 500).into(faceImg);


                /*          Glide.with(mContext)
                             .load(img_link)
                             .into(faceImg)
                                  .clearOnDetach()*/

                val picasso = Picasso.Builder(mContext)
                        .memoryCache(Cache.NONE)
                        .indicatorsEnabled(false)
                        .loggingEnabled(true) //add other settings as needed
                        .build()
                //Picasso.setSingletonInstance(picasso)
                picasso.load(Uri.parse(img_link))
                        .centerCrop()
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .resize(500, 500)
                        .into(faceImg)

                /*Picasso.get()
                        .load(Uri.parse(img_link))
                        .resize(500, 500)
                        .into(faceImg)*/

                /*  Picasso.get()
                          .load(img_link)
                          .resize(500, 500)
                          .into(faceImg)*/


                progress_wheel.stopSpinning()


                simpleDialogg.show()

                faceCanel.setOnClickListener({ view ->
                    simpleDialogg.dismiss()
                })

                simpleDialogg.setOnCancelListener({ view ->
                    simpleDialogg.dismiss()

                })
                simpleDialogg.setOnDismissListener({ view ->
                    simpleDialogg.dismiss()

                })
            }

            override fun getAadhaarOnLick(obj: UserListResponseModel) {
                OpenDialogForAdhaarReg(obj)
            }

            override fun updateTypeOnClick(obj: UserListResponseModel) {

            }
        }, {
            it
        })

        mRv_userList.adapter = adapter
    }


    override fun onClick(p0: View?) {

    }


    fun deletePicApi(usr_id: String) {

        val repository = GetUserListPhotoRegProvider.providePhotoReg()
        BaseActivity.compositeDisposable.add(
                repository.deleteUserPicApi(usr_id, Pref.session_token!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            var response = result as DeleteUserPicResponse
                            if (response.status == NetworkConstant.SUCCESS) {
                                callUSerListApi()

                            } else {
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )

    }

    /////////////////////////////////////////////

    private fun sendWhats(phone: String) {
        val packageManager: PackageManager = mContext.getPackageManager()
        val i = Intent(Intent.ACTION_VIEW)
        try {
            //val url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + URLEncoder.encode("", "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=" + phone + "&text=" + " "
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                this.startActivity(i)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openWhatsApp(num: String) {
        val isAppInstalled = appInstalledOrNot("com.whatsapp")
        if (isAppInstalled) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$num"))
            startActivity(intent)
        } else {
            // WhatsApp not installed show toast or dialog
        }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = requireActivity().packageManager
        return try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    lateinit var simpleDialog: Dialog
    lateinit var iv_takenImg: ImageView
    lateinit var dialogCameraclickCancel: ImageView
    lateinit var dialogDocclickCancel: ImageView
    lateinit var tv_docShow: TextView
    lateinit var tv_docUrl: TextView
    private fun OpenDialogForAdhaarReg(obj: UserListResponseModel) {
        simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(true)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_adhaar_reg)

        val headerName = simpleDialog.findViewById(R.id.dialog_adhaar_reg_adhharTV_header) as AppCustomTextView
        headerName.text = "Enter Aadhaar for " + obj.user_name

        val dialogEtCardNumber1 = simpleDialog.findViewById(R.id.dialog_adhaar_reg_et_no_et_1) as AppCustomEditText
        val dialogEtCardNumber2 = simpleDialog.findViewById(R.id.dialog_adhaar_reg_et_no_et_2) as AppCustomEditText
        val dialogEtCardNumber3 = simpleDialog.findViewById(R.id.dialog_adhaar_reg_et_no_et_3) as AppCustomEditText

        val dialogEtFeedback = simpleDialog.findViewById(R.id.tv_dialog_adhaar_reg_feedback) as AppCustomEditText
        val dialogCameraclick = simpleDialog.findViewById(R.id.tv_dialog_adhaar_reg_iv_camera) as ImageView
        dialogCameraclickCancel = simpleDialog.findViewById(R.id.iv_dialog_aadhaar_reg_cancel_pic) as ImageView
        dialogDocclickCancel = simpleDialog.findViewById(R.id.iv_dialog_aadhaar_reg_cancel_pic_doc) as ImageView
        iv_takenImg = simpleDialog.findViewById(R.id.iv_dialog_aadhaar_reg_pic) as ImageView
        tv_docShow = simpleDialog.findViewById(R.id.tv_dialog_aadhaar_reg_doc) as TextView
        tv_docUrl = simpleDialog.findViewById(R.id.tv_dialog_aadhaar_reg_doc_url) as TextView

        val dialogConfirm = simpleDialog.findViewById(R.id.tv_dialog_adhaar_reg_confirm) as AppCustomTextView
        val dialogCancel = simpleDialog.findViewById(R.id.tv_dialog_adhaar_reg_cancel) as AppCustomTextView

        dialogEtFeedback.setText(obj.aadhaar_remarks)


        dialogEtCardNumber1.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (dialogEtCardNumber1.text.toString().length == 4) {
                    dialogEtCardNumber2.setSelection(dialogEtCardNumber2.text.toString().length)
                    dialogEtCardNumber2.requestFocus()
                }
            }
        })
        dialogEtCardNumber2.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (dialogEtCardNumber2.text.toString().length == 4) {
                    dialogEtCardNumber3.setSelection(dialogEtCardNumber3.text.toString().length)
                    dialogEtCardNumber3.requestFocus()
                }
            }
        })

        /* val key: OnKeyListener = object : OnKeyListener {
             override fun onKey(v: View, keyCode: Int, event: KeyEvent?): Boolean {
                 if((v as EditText).length()==4){
                     if (!(v as EditText).toString().isEmpty()){
                         v.focusSearch(View.FOCUS_RIGHT).requestFocus()
                     }

                 }
                 return false
             }
         }*/

        //dialogEtCardNumber1.setOnKeyListener(key)
        //dialogEtCardNumber2.setOnKeyListener(key)
        //dialogEtCardNumber3.setOnKeyListener(key)

        /*  if(dialogEtCardNumber1.getText().toString().length==4)
          {
              if(dialogEtCardNumber1.text.toString().isNotEmpty())
              {
                  dialogEtCardNumber1.requestFocus()
              }
              else
              {

              }
          }*/


        if (obj.RegisteredAadhaarNo != null && obj.RegisteredAadhaarNo!!.length > 0) {
            dialogEtCardNumber1.setText(obj.RegisteredAadhaarNo!!.get(0).toString() + obj.RegisteredAadhaarNo!!.get(1).toString() +
                    obj.RegisteredAadhaarNo!!.get(2).toString() + obj.RegisteredAadhaarNo!!.get(3).toString())
            dialogEtCardNumber2.setText(obj.RegisteredAadhaarNo!!.get(4).toString() + obj.RegisteredAadhaarNo!!.get(5).toString() +
                    obj.RegisteredAadhaarNo!!.get(6).toString() + obj.RegisteredAadhaarNo!!.get(7).toString())
            dialogEtCardNumber3.setText(obj.RegisteredAadhaarNo!!.get(8).toString() + obj.RegisteredAadhaarNo!!.get(9).toString() +
                    obj.RegisteredAadhaarNo!!.get(10).toString() + obj.RegisteredAadhaarNo!!.get(11).toString())
        }
        if (obj.RegisteredAadhaarDocLink != null && obj.RegisteredAadhaarDocLink!!.length > 0 && obj.RegisteredAadhaarDocLink!!.contains("jpg")) {
            iv_takenImg.visibility = View.VISIBLE
            dialogCameraclickCancel.visibility = View.VISIBLE
            Picasso.get()
                    .load(obj.RegisteredAadhaarDocLink)
                    .resize(500, 500)
                    .into(iv_takenImg)
        } else if (obj.RegisteredAadhaarDocLink != null && obj.RegisteredAadhaarDocLink!!.length > 0) {
            //Toaster.msgLong(mContext,obj.RegisteredAadhaarDocLink!!.toString())
            iv_takenImg.visibility = View.GONE
            dialogCameraclickCancel.visibility = View.GONE


            if (obj.RegisteredAadhaarDocLink!!.contains("CommonFolder")) {
                tv_docShow.text = "Document Attached."
                tv_docUrl.text = obj.RegisteredAadhaarDocLink

            }

            // download document here
            tv_docUrl.setOnClickListener { view ->

                val file = File(obj.RegisteredAadhaarDocLink!!)
                var strFileName = ""
                if (!obj.RegisteredAadhaarDocLink!!.startsWith("http")!!) {
                    strFileName = file.name
                } else {
                    strFileName = obj.RegisteredAadhaarDocLink!!.substring(obj.RegisteredAadhaarDocLink!!.lastIndexOf("/")!! + 1)
                }

                //downloadFile(obj.RegisteredAadhaarDocLink,tv_docUrl.text.toString().trim())
                downloadFile(obj.RegisteredAadhaarDocLink, strFileName)
            }


        }

        dialogCameraclick.setOnClickListener { v: View? ->
            iv_takenImg.visibility = View.GONE
            dialogCameraclickCancel.visibility = View.GONE
            dialogDocclickCancel.visibility = View.GONE
            tv_docShow.visibility = View.GONE
            showPictureDialog()
        }
        dialogCameraclickCancel.setOnClickListener { v: View? ->

            iv_takenImg.setImageBitmap(null)
            dialogCameraclickCancel.visibility = View.GONE
            dataPath = ""
            imagePath = ""
        }
        dialogDocclickCancel.setOnClickListener { v: View? ->

            tv_docShow.visibility = View.GONE
            dialogDocclickCancel.visibility = View.GONE

            dataPath = ""
            imagePath = ""
        }
        dialogCancel.setOnClickListener { v: View? ->
            progress_wheel.stopSpinning()
            simpleDialog.cancel()
        }

        dialogConfirm.setOnClickListener({ view ->
            //simpleDialog.cancel()

            if (dialogEtCardNumber1.text.toString().length == 4) {
                if (dialogEtCardNumber2.text.toString().length == 4) {
                    if (dialogEtCardNumber3.text.toString().length == 4) {
                        ////////
                        val simpleDialogInner = Dialog(mContext)
                        simpleDialogInner.setCancelable(false)
                        simpleDialogInner.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialogInner.setContentView(R.layout.dialog_yes_no)
                        val dialogHeader = simpleDialogInner.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                        val dialogHeaderTTV = simpleDialogInner.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                        dialogHeader.text = "Are you sure?"
                        dialogHeaderTTV.text = "Hi " + Pref.user_name + "!"
                        val dialogYes = simpleDialogInner.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                        val dialogNo = simpleDialogInner.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView

                        dialogYes.setOnClickListener({ view ->
                            simpleDialogInner.cancel()
                            str_aadhaarNo = dialogEtCardNumber1.text.toString() + dialogEtCardNumber2.text.toString() + dialogEtCardNumber3.text.toString()
                            // check aadhar unique or not

                            var tagAadhaar = false


                            /*for (j in 0..aadhaarList.size - 1) {
                                if (str_aadhaarNo.equals(aadhaarList.get(j))) {
                                    tagAadhaar = true
                                }
                            }
*/
                            /* if (obj.IsAadhaarRegistered!!) {
                                tagAadhaar = false
                            }*/

                            if (aadhaarListUserList!!.size > 0 && aadhaarListUserList != null) {
                                for (p in 0..aadhaarListUserList!!.size - 1) {
                                    if (str_aadhaarNo.equals(aadhaarListUserList.get(p).RegisteredAadhaarNo)) {
                                        if (obj.user_id!!.toString().equals(aadhaarListUserList.get(p).user_id)) {
                                            tagAadhaar = false
                                            break
                                        } else {
                                            tagAadhaar = true
                                            break
                                        }
                                    }
                                }
                            }



                            if (tagAadhaar == false) {
                                simpleDialog.cancel()
                                submitAadhaarDetails(obj, dialogEtFeedback.text.toString())
                            }
                            else {

                                val simpleDialog = Dialog(mContext)
                                simpleDialog.setCancelable(false)
                                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                simpleDialog.setContentView(R.layout.dialog_message)
                                val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                                dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                                dialogHeader.text = "Duplicate Aadhaar Number.Please enter Unique for Current Person.Thanks."
                                val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                dialogYes.setOnClickListener({ view ->
                                    simpleDialog.cancel()
                                })
                                simpleDialog.show()

                                //Toaster.msgShort(mContext, "Duplicate Aadhaar Number.Please enter Unique for Current Person.Thanks.")
                                voiceAttendanceMsg("Duplicate Aadhaar Number.Please enter Unique for Current Person.")
                                //(mContext as DashboardActivity).showSnackMessage("Duplication Aadhaar Number.Please enter Unique for Current Person.Thanks.")
                            }


                        })
                        dialogNo.setOnClickListener({ view ->
                            simpleDialogInner.cancel()
                        })
                        simpleDialogInner.show()

                        ///////

                    } else {
                        dialogEtCardNumber3.setError("Please Enter Aadhaad No")
                        dialogEtCardNumber3.requestFocus()
                    }
                } else {
                    dialogEtCardNumber2.setError("Please Enter Aadhaad No")
                    dialogEtCardNumber2.requestFocus()
                }
            } else {
                dialogEtCardNumber1.setError("Please Enter Aadhaad No")
                dialogEtCardNumber1.requestFocus()
            }

        })
        simpleDialog.show()

    }


    private fun submitAadhaarDetails(obj: UserListResponseModel, feedBac: String) {
        progress_wheel.spin()
        var aadhaarSubmitData: AadhaarSubmitData = AadhaarSubmitData()
        aadhaarSubmitData.session_token = Pref.session_token.toString()
        aadhaarSubmitData.aadhaar_holder_user_id = obj.user_id.toString()
        aadhaarSubmitData.aadhaar_holder_user_contactid = obj.user_contactid.toString()
        aadhaarSubmitData.aadhaar_no = str_aadhaarNo
        aadhaarSubmitData.date = AppUtils.getCurrentDateForShopActi()
        aadhaarSubmitData.feedback = feedBac
        aadhaarSubmitData.address = ""

        val repository = GetUserListPhotoRegProvider.provideUserListPhotoReg()
        BaseActivity.compositeDisposable.add(
                repository.sendUserAadhaarApi(aadhaarSubmitData)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            progress_wheel.stopSpinning()
                            //(mContext as DashboardActivity).showSnackMessage(response.message!!)
                            //(mContext as DashboardActivity).showSnackMessage("Aadhaar registered successfully")

                            if (response.status == NetworkConstant.SUCCESS) {
                                //Toaster.msgShort(mContext,response.status.toString())
                                if (!TextUtils.isEmpty(et_attachment.text.toString().trim()) || !TextUtils.isEmpty(et_photo.text.toString().trim())) {
                                    val imgList = java.util.ArrayList<WIPImageSubmit>()

                                    if (!TextUtils.isEmpty(et_attachment.text.toString()))
                                        imgList.add(WIPImageSubmit(dataPath, "attachment"))

                                    if (!TextUtils.isEmpty(et_photo.text.toString()))
                                        imgList.add(WIPImageSubmit(imagePath, "image"))

                                    val repository = GetUserListPhotoRegProvider.jobMultipartRepoProvider()
                                    BaseActivity.compositeDisposable.add(
                                            repository.submitAadhaarDetails(aadhaarSubmitData, imgList, mContext)
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribeOn(Schedulers.io())
                                                    .subscribe({ result ->
                                                        val response = result as BaseResponse
                                                        progress_wheel.stopSpinning()
                                                        //(mContext as DashboardActivity).showSnackMessage(response.message!!)
                                                        //(mContext as DashboardActivity).showSnackMessage("Aadhar registered successfully")
                                                        if (response.status == NetworkConstant.SUCCESS) {
                                                            aadharSuccessDialogShow(obj)
                                                            //voiceAttendanceMsg("Aadhaar registered successfully")
                                                            /* Handler(Looper.getMainLooper()).postDelayed({
                                                                callUSerListApi()
                                                            }, 300)*/
                                                            //(mContext as DashboardActivity).loadFragment(FragType.ProtoRegistrationFragment, false, "")
                                                        }
                                                        else if (response.status ==NetworkConstant.SESSION_MISMATCH){
                                                            progress_wheel.stopSpinning()
                                                            (mContext as DashboardActivity).showSnackMessage(message = response.message.toString())
                                                        }

                                                    }, { error ->
                                                        progress_wheel.stopSpinning()
                                                        error.printStackTrace()
                                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                                    })
                                    )
                                } else {
                                    progress_wheel.stopSpinning()
                                    aadharSuccessDialogShow(obj)
                                    //voiceAttendanceMsg("Aadhaar registered successfully")
                                    //(mContext as DashboardActivity).loadFragment(FragType.ProtoRegistrationFragment, false, "")
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Duplicate Aadhaar Number.Please enter Unique for Current Person.Thanks.")
                                voiceAttendanceMsg("Duplicate Aadhaar Number.Please enter Unique for Current Person.")
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )


    }

    private fun aadharSuccessDialogShow(obj: UserListResponseModel) {
        val simpleDialogAdhhar = Dialog(mContext)
        simpleDialogAdhhar.setCancelable(false)
        simpleDialogAdhhar.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialogAdhhar.setContentView(R.layout.dialog_message)
        val dialogHeader = simpleDialogAdhhar.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
        val dialogHeaderTTV = simpleDialogAdhhar.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
        dialogHeader.text = "Aadhaar registered successfully for "+obj.user_name
        dialogHeaderTTV.text = "Hi " + Pref.user_name + "!"
        val tv_message_ok = simpleDialogAdhhar.findViewById(R.id.tv_message_ok) as AppCustomTextView

        tv_message_ok.setOnClickListener({ view ->
            simpleDialogAdhhar.cancel()
            voiceAttendanceMsg("Aadhaar registered successfully for " + obj.user_name)
        })
        simpleDialogAdhhar.show()
    }


    private fun voiceAttendanceMsg(msg: String) {
        if (Pref.isVoiceEnabledForAttendanceSubmit) {
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Add Day Start", "TTS error in converting Text to Speech!");
        }
        Handler(Looper.getMainLooper()).postDelayed({
            callUSerListApi()
        }, 300)
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        //val pictureDialogItems = arrayOf("Select photo from gallery", "Capture Image", "Select file from file manager")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture Image")
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> {
                            isAttachment = false
                            selectImageInAlbum()
                        }
                        1 -> {
                            isAttachment = false
                            launchCamera()
                        }
                        /*2 -> {
                            isAttachment = true
                            (mContext as DashboardActivity).openFileManager()
                        }*/
                    }
                })
        pictureDialog.show()
    }

    private fun selectImageInAlbum() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        (mContext as DashboardActivity).startActivityForResult(galleryIntent, PermissionHelper.REQUEST_CODE_STORAGE)
    }


    private fun launchCamera() {
        (mContext as DashboardActivity).captureImage()
    }


    /*fun setImage(file: File) {
        if (isAttachment) {
            et_attachment.setText(file.name)
            dataPath = file.absolutePath
        }
        else {
            imagePath = file.absolutePath
            et_photo.setText(file.name)
        }
    }*/

    fun setImage(filePath: String) {
        val file = File(filePath)
        var newFile: File? = null
        progress_wheel.spin()
        doAsync {
            val processImage = ProcessImageUtils_v1(mContext, file, 50)
            newFile = processImage.ProcessImageSelfie()
            uiThread {
                if (newFile != null) {
                    XLog.e("=========Image from new technique==========")
                    val fileSize = AppUtils.getCompressImage(filePath)
                    var tyy = filePath

                    if (isAttachment) {
                        et_attachment.setText(newFile!!.name)
                        dataPath = newFile!!.absolutePath
                    } else {
                        imagePath = newFile!!.absolutePath
                        et_photo.setText(newFile!!.name)

                        val f = File(newFile!!.absolutePath)
                        val options: BitmapFactory.Options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        var bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
                        iv_takenImg.setImageBitmap(bitmap)
                        var tt = "asd"

                    }
                    iv_takenImg.visibility = View.VISIBLE
                    dialogCameraclickCancel.visibility = View.VISIBLE
                    progress_wheel.stopSpinning()
                } else {
                    // Image compression
                    val fileSize = AppUtils.getCompressImage(filePath)
                    var tyy = filePath
                    progress_wheel.stopSpinning()
                    iv_takenImg.visibility = View.VISIBLE
                    dialogCameraclickCancel.visibility = View.VISIBLE
                }
            }
        }
    }

    fun setDoc(file: File) {
        if (isAttachment) {
            et_attachment.setText(file.name)
            dataPath = file.absolutePath
            iv_takenImg.visibility = View.GONE
            tv_docShow.text = "Document Attached."
            tv_docShow.visibility = View.VISIBLE
            dialogDocclickCancel.visibility = View.VISIBLE
        }
    }


    /* fun setImage(filePath: String) {

         val file = File(filePath)
         var newFile: File? = null

         progress_wheel.spin()
         doAsync {

             val processImage = ProcessImageUtils_v1(mContext, file, 50)
             newFile = processImage.ProcessImage()

             uiThread {
                 if (newFile != null) {
                     XLog.e("=========Image from new technique==========")
                     //reimbursementEditPic(newFile!!.length(), newFile?.absolutePath!!)
                 } else {
                     // Image compression
                     val fileSize = AppUtils.getCompressImage(filePath)
                     //reimbursementEditPic(fileSize, filePath)
                 }
             }
         }
     }*/


    private fun downloadFile(downloadUrl: String?, fileName: String) {
        try {
            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }

            progress_wheel.spin()

            val folder = File(FTStorageUtils.getFolderPath(mContext) + "/", fileName)
            if (folder.exists()) {
                folder.delete()
                if (folder.exists()) {
                    folder.canonicalFile.delete()
                    if (folder.exists()) {
                        mContext.deleteFile(folder.getName())
                    }
                }
            }

            PRDownloader.download(downloadUrl, FTStorageUtils.getFolderPath(mContext) + "/", fileName)
                    .build()
                    .setOnProgressListener {
                        Log.e("Aadhaar Details", "Attachment Download Progress======> $it")
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            progress_wheel.stopSpinning()
                            val file = File(FTStorageUtils.getFolderPath(mContext) + "/" + fileName)
                            openFile(file)
                        }

                        override fun onError(error: Error) {
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("Download failed")
                            Log.e("Aadhaar Details", "Attachment download error msg=======> " + error.serverErrorMessage)
                        }
                    })

        } catch (e: Exception) {
            (mContext as DashboardActivity).showSnackMessage("Download failed")
            progress_wheel.stopSpinning()
            e.printStackTrace()
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun openFile(file: File) {

        val mimeType = NewFileUtils.getMemeTypeFromFile(file.absolutePath + "." + NewFileUtils.getExtension(file))

        if (mimeType?.equals("application/pdf")!!) {
//            val path1 = Uri.fromFile(file)
            val path1 = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Pdf")
            }
        } else if (mimeType == "application/msword") {
            //val path1 = Uri.fromFile(file)
            val path1 = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/msword")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }
        } else if (mimeType == "application/vnd.ms-excel") {
            //val path1 = Uri.fromFile(file)
            val path1 = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }

        } else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.template") {
            //val path1 = Uri.fromFile(file)
            val path1 = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.openxmlformats-officedocument.wordprocessingml.template")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }
        } else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
            //val path1 = Uri.fromFile(file)
            val path1 = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }

        } else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") {
            //val path1 = Uri.fromFile(file)
            val path1 = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }
        } else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.template") {
            //val path1 = Uri.fromFile(file)
            val path1 = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }
        } else if (mimeType == "image/jpeg" || mimeType == "image/png") {
            FullImageDialog.getInstance(file.absolutePath).show((mContext as DashboardActivity).supportFragmentManager, "")
        }
    }


    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result: Int = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
            val result1: Int = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission() {
        /*if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent: Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(java.lang.String.format("package:%s", mContext.getApplicationContext().getPackageName()))
                startActivityForResult(intent, 2296)
            } catch (e: java.lang.Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
            }
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    callUSerListApi()
                } else {
                    Toast.makeText(mContext, "Allow permission for storage access!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
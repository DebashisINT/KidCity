package com.kcteam.features.nearbyshops.multipleattachImage

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.ProcessImageUtils_v1
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.model.assigntopplist.AddShopUploadImg
import com.kcteam.features.addshop.model.assigntopplist.AddshopImageMultiReqbody1
import com.kcteam.features.addshop.model.imageListResponse
import com.kcteam.features.beatCustom.BeatGetStatusModel
import com.kcteam.features.beatCustom.api.GetBeatRegProvider
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.marketing.model.MarketingDetailImageData
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.squareup.picasso.Cache
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileInputStream

class MultipleImageFragment: BaseFragment(),
    View.OnClickListener {
    private lateinit var mContext: Context
    private var str_imageView1: String = ""
    private var str_imageView2: String = ""
    private var str_imageView3: String = ""
    private var str_imageView4: String = ""
    private var imagePathupload: String = ""
    private var imagePathupload2: String = ""
    private var imagePathupload3: String = ""
    private var imagePathupload4: String = ""
    private lateinit var iv_upload_image_view: ImageView
    private lateinit var iv_upload_image_view_image1: ImageView
    private lateinit var iv_image_cross_icon_1: AppCompatImageView
    private lateinit var iv_image_cross_icon_2: AppCompatImageView
    private lateinit var iv_upload_image_view_image2: ImageView
    private lateinit var iv_upload_image_view_image3: ImageView
    private lateinit var iv_image_cross_icon_3: AppCompatImageView
    private lateinit var iv_image_cross_icon_4: AppCompatImageView
    private lateinit var tv_text1: TextView
    private lateinit var tv_text2: TextView
    private lateinit var tv_text3: TextView
    private lateinit var tv_text4: TextView

    private lateinit var upload_TV: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var iv_hand1: ImageView
    private lateinit var iv_hand2: ImageView
    private lateinit var iv_hand3: ImageView
    private lateinit var iv_hand4: ImageView

    private var isDocDegree = -1
    private var degreeImgLink = ""
    private var isAttachment = false



    companion object {
        var mobj: AddShopDBModelEntity? = null
        fun newInstance(objects: Any): MultipleImageFragment {
            val fragment = MultipleImageFragment()
            mobj = objects as AddShopDBModelEntity
            return fragment
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_mutliple_image_attach, container, false)
        initView(view)
        return view
    }

    @SuppressLint("RestrictedApi")
    private fun initView(view: View) {
        iv_upload_image_view =  view.findViewById(R.id.iv_upload_image_view)
        iv_upload_image_view_image1 =  view.findViewById(R.id.iv_upload_image_view_image1)
        iv_upload_image_view_image2=view.findViewById(R.id.iv_upload_image_view_image2)
        iv_upload_image_view_image3=view.findViewById(R.id.iv_upload_image_view_image3)

        iv_image_cross_icon_1 =  view.findViewById(R.id.iv_image_cross_icon_1)
        iv_image_cross_icon_2 =  view.findViewById(R.id.iv_image_cross_icon_2)
        iv_image_cross_icon_3 = view.findViewById(R.id.iv_image_cross_icon_3)
        iv_image_cross_icon_4 = view.findViewById(R.id.iv_image_cross_icon_4)

        tv_text1 = view.findViewById(R.id.tv_text1)
        tv_text2 = view.findViewById(R.id.tv_text2)
        tv_text3 = view.findViewById(R.id.tv_text3)
        tv_text4 = view.findViewById(R.id.tv_text4)

        upload_TV =  view.findViewById(R.id.upload_TV)
        iv_hand1 = view.findViewById(R.id.iv_hand1)
        iv_hand2 = view.findViewById(R.id.iv_hand2)
        iv_hand3 = view.findViewById(R.id.iv_hand3)
        iv_hand4 = view.findViewById(R.id.iv_hand4)
        progress_wheel = view.findViewById(R.id.progress_wheel)

        initPermissionCheck()
        upload_TV.visibility = View.GONE

        iv_upload_image_view.setOnClickListener (this)
        iv_upload_image_view_image1.setOnClickListener (this)
        iv_upload_image_view_image2.setOnClickListener (this)
        iv_upload_image_view_image3.setOnClickListener (this)
        iv_image_cross_icon_1.setOnClickListener (this)
        iv_image_cross_icon_2.setOnClickListener (this)
        iv_image_cross_icon_3.setOnClickListener (this)
        iv_image_cross_icon_4.setOnClickListener (this)
        upload_TV.setOnClickListener (this)

        if (!AppUtils.isOnline(mContext)) {
            (this as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }else{
            fetchShopwiseImageList()
        }

    }

    //from gallery
    fun setImageFromPath(filePath: String) {
        progress_wheel.spin()
        val file = File(filePath)
        var newFile: File? = null
        doAsync {
            val processImage = ProcessImageUtils_v1(mContext, file, 50)
            newFile = processImage.ProcessImage()
            uiThread {
                if (newFile != null) {
                    val fileSize = AppUtils.getCompressOldImage(filePath,mContext)
                    if (isDocDegree == 0) {
                        progress_wheel.stopSpinning()
                        imagePathupload = newFile?.absolutePath!!
                        tv_text1.setText(newFile?.absolutePath!!.split("/").last())
                        iv_image_cross_icon_1.visibility = View.VISIBLE
                        upload_TV.visibility = View.VISIBLE
                    } else if (isDocDegree == 1) {
                        progress_wheel.stopSpinning()
                        imagePathupload2 = newFile?.absolutePath!!.toString()
                        tv_text2.setText(newFile?.absolutePath!!.split("/").last())
                        iv_image_cross_icon_2.visibility = View.VISIBLE
                        upload_TV.visibility = View.VISIBLE
                    }
                    else if (isDocDegree == 2) {
                        progress_wheel.stopSpinning()
                        imagePathupload3 = newFile?.absolutePath!!.toString()
                        tv_text3.setText(newFile?.absolutePath!!.split("/").last())
                        iv_image_cross_icon_3.visibility = View.VISIBLE
                        upload_TV.visibility = View.VISIBLE
                    }
                    else if (isDocDegree == 3) {
                        progress_wheel.stopSpinning()
                        imagePathupload4 = newFile?.absolutePath!!.toString()
                        tv_text4.setText(newFile?.absolutePath!!.split("/").last())
                        iv_image_cross_icon_4.visibility = View.VISIBLE
                        upload_TV.visibility = View.VISIBLE
                    }
                    else {
                        upload_TV.visibility = View.VISIBLE
                        progress_wheel.stopSpinning()
                        if (fileSize <= 20 * 1024) {
                            degreeImgLink = newFile?.absolutePath!!.toString()
                        } else
                            (mContext as DashboardActivity).showSnackMessage("Image size can not be greater than 20 MB")
                    }
                }
            }
        }
    }


    // from camera
    fun setImagecapture(filePath: String) {
        progress_wheel.spin()
        val file = File(filePath)
        var newFile: File? = null
//        progress_wheel.spin()
        doAsync {
            val processImage = ProcessImageUtils_v1(mContext, file, 50,false)
            newFile = processImage.ProcessImageSelfie()
            val fileSize = AppUtils.getCompressOldImage(filePath,mContext)
            uiThread {
                if (newFile != null) {
                    XLog.e("=========Image from new technique==========")
                    if(isDocDegree == 0){
                        progress_wheel.stopSpinning()
                        tv_text1.setText(newFile!!.name)
                        iv_image_cross_icon_1.visibility = View.VISIBLE
                        upload_TV.visibility = View.VISIBLE
                        imagePathupload = newFile!!.absolutePath
                        val f = File(newFile!!.absolutePath)
                        val options: BitmapFactory.Options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        var bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
                    }else if (isDocDegree == 1){
                        progress_wheel.stopSpinning()
                        tv_text2.setText(newFile!!.name)
                        iv_image_cross_icon_2.visibility = View.VISIBLE
                        upload_TV.visibility = View.VISIBLE
                        imagePathupload2 = newFile!!.absolutePath
                        val f = File(newFile!!.absolutePath)
                        val options: BitmapFactory.Options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        var bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
                    }
                    else if (isDocDegree == 2){
                        progress_wheel.stopSpinning()
                        tv_text3.setText(newFile!!.name)
                        iv_image_cross_icon_3.visibility = View.VISIBLE
                        upload_TV.visibility = View.VISIBLE
                        imagePathupload3 = newFile!!.absolutePath
                        val f = File(newFile!!.absolutePath)
                        val options: BitmapFactory.Options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        var bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
                    }
                    else if (isDocDegree == 3){
                        progress_wheel.stopSpinning()
                        tv_text4.setText(newFile!!.name)
                        iv_image_cross_icon_4.visibility = View.VISIBLE
                        upload_TV.visibility = View.VISIBLE
                        imagePathupload4 = newFile!!.absolutePath
                        val f = File(newFile!!.absolutePath)
                        val options: BitmapFactory.Options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        var bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
                    }
                } else {
                    // Image compression
                    upload_TV.visibility = View.VISIBLE
                    progress_wheel.stopSpinning()
                        if (fileSize <= 20 * 1024) {
                            degreeImgLink = newFile?.absolutePath!!.toString()
                        } else
                            (mContext as DashboardActivity).showSnackMessage("Image size can not be greater than 20 MB")

                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.iv_upload_image_view->{
                if(!str_imageView1.equals("")){
                    viewPicOnLick(str_imageView1,"Attachment 1")
                }else{
                    isDocDegree = 0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheckOne()
                    else
                        showPictureDialog()
                }
            }
            R.id.iv_upload_image_view_image1->{
                if(!str_imageView2.equals("")){
                    viewPicOnLick(str_imageView2,"Attachment 2")
                }else{
                if(str_imageView1.equals("") && imagePathupload.equals("")){
                    openDialogPopup("Hi ${Pref.user_name} !","Please upload attachment1.")
                }else {
                    isDocDegree = 1
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheckOne()
                    else
                        showPictureDialog()
                }
                }
            }
            R.id.iv_upload_image_view_image2->{
                if(!str_imageView3.equals("")){
                    viewPicOnLick(str_imageView3,"Attachment 3")
                }else {
                    if (str_imageView2.equals("")&& imagePathupload2.equals("")) {
                        openDialogPopup("Hi ${Pref.user_name} !", "Please upload attachment2.")
                    } else {
                        isDocDegree = 2
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            initPermissionCheckOne()
                        else
                            showPictureDialog()
                    }
                }
            }
            R.id.iv_upload_image_view_image3-> {
                if(!str_imageView4.equals("")){
                    viewPicOnLick(str_imageView4,"Attachment 4")
                }else {
                    if (str_imageView3.equals("")&& imagePathupload3.equals("")) {
                        openDialogPopup("Hi ${Pref.user_name} !", "Please upload attachment3.")
                    } else {
                        isDocDegree = 3
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            initPermissionCheckOne()
                        else
                            showPictureDialog()
                    }
                }
            }
            R.id.iv_image_cross_icon_1 ->{
                imagePathupload = ""
                tv_text1.setText("")
                iv_image_cross_icon_1.visibility = View.GONE
            }

            R.id.iv_image_cross_icon_2 ->{
                imagePathupload2 = ""
                tv_text2.setText("")
                iv_image_cross_icon_2.visibility = View.GONE
            }
            R.id.iv_image_cross_icon_3 ->{
                imagePathupload3 = ""
                tv_text3.setText("")
                iv_image_cross_icon_3.visibility = View.GONE
            }
            R.id.iv_image_cross_icon_4 ->{
                imagePathupload4 = ""
                tv_text4.setText("")
                iv_image_cross_icon_4.visibility = View.GONE
            }
            R.id.upload_TV->{
                if (!AppUtils.isOnline(mContext)) {
                    (this as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }
                else if (str_imageView1.equals("")) {
                   addShopMutlipleUploadImg1(mobj!!.shop_id!!)
               }else if(str_imageView2.equals("")) {
                   addShopMutlipleUploadImg2(mobj!!.shop_id!!)
               }else if(str_imageView3.equals("")) {
                   addShopMutlipleUploadImg3(mobj!!.shop_id!!)
               }else if(str_imageView4.equals("")) {
                   addShopMutlipleUploadImg4(mobj!!.shop_id!!)
               }
                else{

               }

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

    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
//                showPictureDialog()
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
                0 -> {
                    isAttachment = false
                    selectImageInAlbum()
                }
                1 -> {
                    isAttachment = true
                    launchCamera()
                }
            }
        }
        pictureDialog.show()
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun selectImageInAlbum() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        (mContext as DashboardActivity).startActivityForResult(galleryIntent, PermissionHelper.REQUEST_CODE_STORAGE)
    }

    fun launchCamera() {
        if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
                (mContext as DashboardActivity).captureImage()
            }

        }

    private fun addShopMutlipleUploadImg1(shopId: String) {
        progress_wheel.spin()
        iv_upload_image_view.setColorFilter(ContextCompat.getColor(mContext,R.color.yellow))
        if(!str_imageView1.equals("")){
            addShopMutlipleUploadImg2(shopId)
        }else{
        var objCompetetor: AddshopImageMultiReqbody1 = AddshopImageMultiReqbody1()
        objCompetetor.session_token = Pref.session_token
        objCompetetor.shop_id = shopId
        objCompetetor.user_id = Pref.user_id

        val repository = AddShopRepositoryProvider.provideAddShopRepository()
        BaseActivity.compositeDisposable.add(
            repository.addShopWithImageuploadMultipleImg1(objCompetetor, imagePathupload, mContext)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as BaseResponse
                    if (response.status == NetworkConstant.SUCCESS) {
                        iv_upload_image_view.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                        progress_wheel.stopSpinning()
                        iv_image_cross_icon_1.visibility = View.GONE
                        if (!imagePathupload2.equals("")) {
                            addShopMutlipleUploadImg2(shopId)
                        }
                       else if (!imagePathupload3.equals("")) {
                            addShopMutlipleUploadImg3(shopId)
                        }
                        else if(!imagePathupload4.equals("")) {
                            addShopMutlipleUploadImg4(shopId)
                        }
                        else{
                            openDialogPopup("Hi ${Pref.user_name} !","upload 1 attcmentment successfully.")
                        }
                    }
                }, { error ->
                    progress_wheel.stopSpinning()
                    if (error != null) {
                        XLog.d("AddShop : Image Upload 1" + ", SHOP: " + shopId + ", ERROR: " + error.localizedMessage)
                    }
                })
        )
        }
    }

    private fun addShopMutlipleUploadImg2(shopId: String) {
        progress_wheel.spin()
        iv_upload_image_view_image1.setColorFilter(ContextCompat.getColor(mContext,R.color.yellow))
        if(!str_imageView2.equals("")){
            addShopMutlipleUploadImg3(shopId)
        }else{
        var objCompetetor: AddshopImageMultiReqbody1 = AddshopImageMultiReqbody1()
        objCompetetor.session_token = Pref.session_token
        objCompetetor.shop_id = shopId
        objCompetetor.user_id = Pref.user_id

        val repository = AddShopRepositoryProvider.provideAddShopRepository()
        BaseActivity.compositeDisposable.add(
            repository.addShopWithImageuploadMultipleImg2(objCompetetor, imagePathupload2, mContext)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as BaseResponse
                    if (response.status == NetworkConstant.SUCCESS) {
                        progress_wheel.stopSpinning()
                        iv_upload_image_view_image1.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                        iv_image_cross_icon_2.visibility = View.GONE
                             if (!imagePathupload3.equals("")) {
                                addShopMutlipleUploadImg3(shopId)
                            }
                            else if(!imagePathupload4.equals("")) {
                                addShopMutlipleUploadImg4(shopId)
                            }
                            else{
                                openDialogPopup("Hi ${Pref.user_name} !","upload 2 attcmentment successfully.")
                            }
                    }
                }, { error ->
                    if (error != null) {
                        progress_wheel.stopSpinning()
                        openDialogPopup("Hi ${Pref.user_name} !","upload 2 attcmentment not successfully.")
                        XLog.d("AddShop : Image Upload 1" + ", SHOP: " + shopId + ", ERROR: " + error.localizedMessage)
                    }
                })
        )}
    }

    private fun addShopMutlipleUploadImg3(shopId: String) {
        progress_wheel.spin()
        iv_upload_image_view_image2.setColorFilter(ContextCompat.getColor(mContext,R.color.yellow))
        if(!str_imageView3.equals("")){
            addShopMutlipleUploadImg4(shopId)
        }else{
        var objCompetetor: AddshopImageMultiReqbody1 = AddshopImageMultiReqbody1()
        objCompetetor.session_token = Pref.session_token
        objCompetetor.shop_id = shopId
        objCompetetor.user_id = Pref.user_id

        val repository = AddShopRepositoryProvider.provideAddShopRepository()
        BaseActivity.compositeDisposable.add(
            repository.addShopWithImageuploadMultipleImg3(objCompetetor, imagePathupload3, mContext)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as BaseResponse
                    if (response.status == NetworkConstant.SUCCESS) {
                        progress_wheel.stopSpinning()
                        iv_upload_image_view_image2.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                        iv_image_cross_icon_3.visibility = View.GONE
                        if(!imagePathupload4.equals("")) {
                                addShopMutlipleUploadImg4(shopId)
                            }
                            else{
                                openDialogPopup("Hi ${Pref.user_name} !","upload 3 attcmentment successfully.")
                            }
                        }

                }, { error ->
                    if (error != null) {
                        progress_wheel.stopSpinning()
                        openDialogPopup("Hi ${Pref.user_name} !","upload 3 attcmentment not successfully.")
                        XLog.d("AddShop : Image Upload 1" + ", SHOP: " + shopId + ", ERROR: " + error.localizedMessage)
                    }
                })
        )
        }
    }

    private fun addShopMutlipleUploadImg4(shopId: String) {
        progress_wheel.spin()
        iv_upload_image_view_image3.setColorFilter(ContextCompat.getColor(mContext,R.color.yellow))
        if(!str_imageView4.equals("")){
            openDialogPopup("Hi ${Pref.user_name} !","All attachments are uploaded.")
        }else{
        var objCompetetor: AddshopImageMultiReqbody1 = AddshopImageMultiReqbody1()
        objCompetetor.session_token = Pref.session_token
        objCompetetor.shop_id = shopId
        objCompetetor.user_id = Pref.user_id

        val repository = AddShopRepositoryProvider.provideAddShopRepository()
        BaseActivity.compositeDisposable.add(
            repository.addShopWithImageuploadMultipleImg4(objCompetetor, imagePathupload4, mContext)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as BaseResponse
                    if (response.status == NetworkConstant.SUCCESS) {
                        progress_wheel.stopSpinning()
                        iv_upload_image_view_image3.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                        iv_image_cross_icon_4.visibility = View.GONE
                        openDialogPopup("Hi ${Pref.user_name} !","upload 4 attcmentment successfully.")
                    }
                }, { error ->
                    if (error != null) {
                        progress_wheel.stopSpinning()
                        openDialogPopup("Hi ${Pref.user_name} !","upload 4 attcmentment not successfully.")
                        XLog.d("AddShop : Image Upload 4" + ", SHOP: " + shopId + ", ERROR: " + error.localizedMessage)
                    }
                })
        )
        }
    }

    private fun fetchShopwiseImageList() {
        progress_wheel.spin()
        try{
            val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                repository.fetchData(mobj!!.shop_id!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val viewResult = result as imageListResponse
                        if (viewResult!!.status == NetworkConstant.SUCCESS) {
                            progress_wheel.stopSpinning()
                            str_imageView1 = viewResult!!.image_list!!.get(0).attachment_image1
                            str_imageView2 = viewResult!!.image_list!!.get(0).attachment_image2
                            str_imageView3 = viewResult!!.image_list!!.get(0).attachment_image3
                            str_imageView4 = viewResult!!.image_list!!.get(0).attachment_image4
                            tv_text1.setText(str_imageView1.split("/").last())
                            tv_text2.setText(str_imageView2.split("/").last())
                            tv_text3.setText(str_imageView3.split("/").last())
                            tv_text4.setText(str_imageView4.split("/").last())
                            if(str_imageView1!=""){
                                if(str_imageView2!=""){
                                    if(str_imageView3!=""){
                                        if(str_imageView4!=""){
                                            iv_upload_image_view.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                            iv_upload_image_view_image1.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                            iv_upload_image_view_image2.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                            iv_upload_image_view_image3.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                            upload_TV.visibility = View.GONE
                                            iv_hand1.visibility = View.GONE
                                            iv_hand2.visibility = View.GONE
                                            iv_hand3.visibility = View.GONE
                                            iv_hand4.visibility = View.GONE
                                        }
                                        else{
                                            iv_upload_image_view.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                            iv_upload_image_view_image1.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                            iv_upload_image_view_image2.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                            upload_TV.visibility = View.VISIBLE
                                            iv_hand1.visibility = View.GONE
                                            iv_hand2.visibility = View.GONE
                                            iv_hand3.visibility = View.GONE
                                        }
                                    }
                                    else{
                                        iv_upload_image_view.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                        iv_upload_image_view_image1.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                        iv_hand1.visibility = View.GONE
                                        iv_hand2.visibility = View.GONE
                                        upload_TV.visibility = View.VISIBLE
                                    }
                                }
                                else{
                                    iv_upload_image_view.setColorFilter(ContextCompat.getColor(mContext,R.color.color_custom_green))
                                    iv_hand1.visibility = View.GONE
                                    upload_TV.visibility = View.VISIBLE
                                }
                            }

                        } else {
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong1))
                        }
                    }, { error ->
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    })
            )
        }
        catch (ex:Exception){
            ex.printStackTrace()
        }
    }

    fun openDialogPopup(header:String,text:String){
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_ok_imei)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_header) as AppCustomTextView
        val dialogBody = simpleDialog.findViewById(R.id.dialog_yes_body) as AppCustomTextView
        dialogHeader.text = header
        dialogBody.text = text
        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
            if(text.contains("successfully",ignoreCase = true)){
                fetchShopwiseImageList()
            }
        })
        simpleDialog.show()
    }


     fun viewPicOnLick(img_link: String, name: String) {
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

        val picasso = Picasso.Builder(mContext)
            .memoryCache(Cache.NONE)
            .indicatorsEnabled(false)
            .loggingEnabled(true)
            .build()

        picasso.load(Uri.parse(img_link))
            .centerCrop()
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .resize(500, 500)
            .into(faceImg)

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

    }



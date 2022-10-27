package com.kcteam.features.damageProduct

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.NewOrderColorEntity
import com.kcteam.app.domain.NewOrderProductEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.damageProduct.model.AddBreakageReqData
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.photoReg.RegisTerFaceFragment
import com.kcteam.features.photoReg.api.GetUserListPhotoRegProvider
import com.kcteam.features.photoReg.model.FaceRegResponse
import com.kcteam.features.photoReg.model.ImageResponse
import com.kcteam.features.photoReg.model.UserPhotoRegModel
import com.kcteam.features.viewAllOrder.presentation.ProductListNewOrderDialog
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_add_ta.*
import java.io.File
import java.io.FileInputStream
import java.util.*
import kotlin.collections.ArrayList

class ShopDamageProductSubmitFrag: BaseFragment(), View.OnClickListener {
    private lateinit var mContext: Context
    private lateinit var ll_product: LinearLayout
    private lateinit var iv_image: ImageView
    private lateinit var iv_cameraLogo: ImageView
    private lateinit var tv_cameraPic: TextView
    private lateinit var iv_submit: AppCustomTextView

    private lateinit var et_desc: EditText
    private lateinit var et_feedback: EditText
    private lateinit var et_remarks: EditText

    private var product_list: List<NewOrderProductEntity> = listOf()
    private lateinit var productSpinner: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private var productId: String = ""

    lateinit var imgUri:Uri
    private var imagePath: String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var mAddShopDataObj: AddShopDBModelEntity? = null
        var shop_id:String = ""
        fun getInstance(objects: Any): ShopDamageProductSubmitFrag {
            val fragment = ShopDamageProductSubmitFrag()
            if (!TextUtils.isEmpty(objects.toString())) {
                shop_id=objects.toString()
                mAddShopDataObj = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)
            }
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_shop_damage_submit, container, false)
        initView(view)
        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initView(view: View?) {
        ll_product = view!!.findViewById(R.id.ll_damage_scr_product)
        productSpinner = view!!.findViewById(R.id.ProductSpinner)
        iv_image = view!!.findViewById(R.id.iv_frag_damage_reg_pic)
        iv_cameraLogo = view!!.findViewById(R.id.iv_frag_damage_reg_camera_icon)
        tv_cameraPic = view!!.findViewById(R.id.tv_frag_damage_product_takephoto)
        iv_submit = view!!.findViewById(R.id.iv_frag_shop_damage_submit)
        et_desc = view!!.findViewById(R.id.et_frag_shop_damage_desc)
        et_feedback = view!!.findViewById(R.id.et_frag_shop_damage_feedback)
        et_remarks = view!!.findViewById(R.id.et_frag_shop_damage_remarks)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        ll_product.setOnClickListener(this)
        iv_image.setOnClickListener(this)
        iv_submit.setOnClickListener(this)

        initPermissionCheckFirstTime()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.ll_damage_scr_product ->{
                loadProduct()
            }
            R.id.iv_frag_damage_reg_pic -> {
                initPermissionCheck()
            }
            R.id.iv_frag_shop_damage_submit -> {
                val simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_yes_no)
                val dialogHeader =
                    simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTV =
                    simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                dialogHeader.text = "Would you like to save the document ?"
                val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                dialogYes.setOnClickListener({ view ->
                    simpleDialog.cancel()
                    if(imagePath.equals("")){
                        Toaster.msgShort(mContext,"Please capture image")
                    }else if(productId.equals("")){
                        Toaster.msgShort(mContext,"Please select a product")
                    }else if(et_desc.text.toString().length==0){
                        Toaster.msgShort(mContext,"Please enter Description of Breakage")
                    }else{
                        saveDataCallApi()
//                        Toaster.msgShort(mContext,"Success")
                    }
                })
                dialogNo.setOnClickListener({ view ->
                    simpleDialog.cancel()
                })
                simpleDialog.show()
            }
        }
    }

    private fun saveDataCallApi() {
        progress_wheel.spin()
        val random = Random()
        val m = random.nextInt(9 - 1) + 1
        var obj= AddBreakageReqData()
        obj.user_id= Pref.user_id
        obj.session_token=Pref.session_token
        obj.date_time= AppUtils.getCurrentDateTime()
        obj.breakage_number=Pref.user_id + AppUtils.getCurrentDateTime().replace(" ","").replace("-","")
            .replace(":","")+ m.toString()
        obj.product_id=productId
        obj.description_of_breakage=et_desc.text.toString()
        obj.customer_feedback=et_feedback.text.toString()
        obj.remarks=et_remarks.text.toString()
        obj.shop_id = shop_id

        val repository = GetUserListPhotoRegProvider.providePhotoSubmitReg()
        BaseActivity.compositeDisposable.add(
            repository.addImgwithdata(obj,imagePath,mContext,
                Pref.user_id+AppUtils.getCurrentDateTime().replace("-","").replace(" ","").replace(":",""))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as ImageResponse
                    if(response.status== NetworkConstant.SUCCESS){
//                        (mContext as DashboardActivity).showSnackMessage("Submit data Succesfully")
                        Handler(Looper.getMainLooper()).postDelayed({
                            progress_wheel.stopSpinning()

                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = "Congrats!"
                            //dialogHeader.text = "Distributor Visited..."
                            dialogHeader.text = "Hi ${Pref.user_name}! Your Breakage for ${mAddShopDataObj!!.shopName} has been placed successfully. Breakage number is  ${obj.breakage_number}."
                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                                (mContext as DashboardActivity).onBackPressed()
                            })
                            simpleDialog.show()

                        }, 500)
                    }else{
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong1))
                    }
                },{
                        error ->
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    if (error != null) {
                        XLog.d(error.localizedMessage)
                    }
                    progress_wheel.stopSpinning()
                })
        )
    }

    private fun loadProduct() {
        product_list= AppDatabase.getDBInstance()?.newOrderProductDao()?.getAllProduct()!!
        if (product_list != null && product_list.isNotEmpty()) {
            ProductListNewOrderDialog.newInstance(product_list as ArrayList<NewOrderProductEntity>) {
                productId = it.product_id!!.toString()
                productSpinner.text = it.product_name

            }.show((mContext as DashboardActivity).supportFragmentManager, "")
        } else {
            Toaster.msgShort(mContext, "No Product Found")
        }
    }

    private fun initPermissionCheckFirstTime() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {

            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }


    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                launchCamera()
            }
            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }
        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun launchCamera() {
        if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            (mContext as DashboardActivity).captureImage()
        }
    }
        fun setImage(imgRealPath: Uri, fileSizeInKB: Long) {
            imgUri=imgRealPath
            imagePath = imgRealPath.toString()
            iv_cameraLogo.visibility=View.GONE
            tv_cameraPic.visibility=View.GONE
            getBitmap(imgRealPath.path)

        }

    fun setImage(file: File) {
        imagePath = file.absolutePath
        iv_cameraLogo.visibility=View.GONE
        tv_cameraPic.visibility=View.GONE
        getBitmap(imagePath)
    }

    fun getBitmap(path: String?) {
        var bitmap: Bitmap? = null
        try {
            val f = File(path)
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            bitmap = BitmapFactory.decodeStream(FileInputStream(f), null, options)
            iv_image.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
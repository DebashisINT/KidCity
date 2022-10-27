package com.kcteam.features.shopdetail.presentation

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.NewFileUtils
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.CollectionDetailsEntity
import com.kcteam.app.domain.PaymentModeEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.features.DecimalDigitsInputFilter
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.newcollection.model.PaymentModeResponseModel
import com.kcteam.features.newcollection.newcollectionlistapi.NewCollectionListRepoProvider
import com.kcteam.features.reimbursement.presentation.FullImageDialog
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.elvishew.xlog.XLog
import com.google.android.material.textfield.TextInputLayout
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*

class AddCollectionWithOrderDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var et_collection: AppCustomEditText
    private lateinit var shop_name_TV: AppCustomTextView
    private lateinit var tvOrderAmt: AppCustomTextView
    private lateinit var tvInvoiceAmt: AppCustomTextView
    private lateinit var iv_close_icon: ImageView
    private lateinit var add_TV: AppCustomTextView
    private var shopName: String = ""
    private var isAdd: Boolean = false
    private lateinit var tv_collection_hint: AppCustomTextView
    private lateinit var dialog_header_TV: AppCustomTextView
    private lateinit var et_date: AppCustomEditText
    private lateinit var rl_payment_mode: RelativeLayout
    private lateinit var et_payment_mode: AppCustomEditText
    private lateinit var tl_instrument: TextInputLayout
    private lateinit var et_bank: AppCustomEditText
    private lateinit var iv_camera: ImageView
    private lateinit var et_feedback: AppCustomEditText
    private lateinit var et_link: AppCustomEditText
    private lateinit var et_instrument: AppCustomEditText
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var cv_collection_main: CardView
    private lateinit var iv_dropdown_icon: ImageView
    private lateinit var ll_image: LinearLayout
    private lateinit var rl_bank: RelativeLayout
    private lateinit var rl_instrument_no: RelativeLayout
    private lateinit var tl_payment: TextInputLayout
    private lateinit var et_patient: AppCustomEditText
    private lateinit var et_address: AppCustomEditText
    private lateinit var et_phone: AppCustomEditText
    private lateinit var rl_patient: RelativeLayout
    private lateinit var rl_address: RelativeLayout
    private lateinit var rl_phone: RelativeLayout

    private lateinit var rl_lab: RelativeLayout
    private lateinit var rl_emailaddress: RelativeLayout
    private lateinit var et_lab: AppCustomEditText
    private lateinit var et_emailaddress: AppCustomEditText


    private lateinit var ll_dialog_add_coll_order_amt: LinearLayout
    private lateinit var ll_dialog_add_coll_inv_amt: LinearLayout
    private lateinit var view_order_inv_amt: View

    private var dateString = ""
    private var amount = ""
    private var permissionUtils: PermissionUtils? = null
    private var dataPath = ""
    private var paymentId = ""
    private var paymentTypePopupWindow: PopupWindow? = null
    private var orderId = ""
    private var billId = ""

    private var orderAmt = "0"
    private var invoiceAmt = "0"
    private var pendingAmt = "0"

    private val myCalendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    companion object {

        private lateinit var addCollectionClickListener: AddCollectionClickLisneter
        private var mAddShopDBModelEntity: CollectionDetailsEntity? = null
        private var mAddShopEntity: AddShopDBModelEntity? = null

        private var ARG_SHOP_NAME = "shop_name"
        private var ARG_IS_ADD = "isAdd"
        private var ARG_DATE = "date"
        private var ARG_AMOUNT = "amount"
        private var ARG_ORDERID = "orderId"
        private var ARG_BILLID = "billId"

        fun getInstance(mObj: Any?, isAdd: Boolean, shop_name: String, date: String, amount: String, orderId: String,billID:String, listener: AddCollectionClickLisneter): AddCollectionWithOrderDialog {
            val mUpdateShopAddressDialog = AddCollectionWithOrderDialog()
            val bundle = Bundle()
            bundle.putString(ARG_SHOP_NAME, shop_name)
            bundle.putBoolean(ARG_IS_ADD, isAdd)
            bundle.putString(ARG_DATE, date)
            bundle.putString(ARG_AMOUNT, amount)
            bundle.putString(ARG_ORDERID, orderId)
            bundle.putString(ARG_ORDERID, orderId)
            bundle.putString(ARG_BILLID, billID)

            if (mObj != null) {
                if (mObj is CollectionDetailsEntity)
                    this.mAddShopDBModelEntity = mObj
                if(mObj is AddShopDBModelEntity)
                    this.mAddShopEntity = mObj

            }

            mUpdateShopAddressDialog.arguments = bundle
            addCollectionClickListener = listener
            return mUpdateShopAddressDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shopName = arguments?.getString(ARG_SHOP_NAME).toString()
        isAdd = arguments?.getBoolean(ARG_IS_ADD)!!
        dateString = arguments?.getString(ARG_DATE).toString()
        amount = arguments?.getString(ARG_AMOUNT).toString()
        orderId = arguments?.getString(ARG_ORDERID).toString()
        billId = arguments?.getString(ARG_BILLID).toString()


        if(Pref.IsCollectionEntryConsiderOrderOrInvoice){
            var shEnt=AppDatabase.getDBInstance()!!.orderDetailsListDao().getSingleOrder(orderId)
            var ordA= AppDatabase.getDBInstance()!!.orderDetailsListDao().getOrderAmt(orderId).toString()
            when{
                ordA == null -> orderAmt="0"
                ordA !=null -> orderAmt=ordA
            }

            var billDtls =AppDatabase.getDBInstance()!!.billingDao().getSingleBillData(billId.toString())
            if(billDtls!=null){
                invoiceAmt=billDtls.invoice_amount
            }else{
                invoiceAmt="0"
            }

             //var collA = AppDatabase.getDBInstance()!!.collectionDetailsDao().getCollectSumAmt(shEnt.shop_id.toString())
             var collA = AppDatabase.getDBInstance()!!.collectionDetailsDao().getCollectSumAmtByOrdID(orderId.toString())
             if(collA!=null){
                 try{
                     if(invoiceAmt.toDouble() > collA.toDouble()){
                         pendingAmt = (invoiceAmt.toDouble()-collA.toDouble()).toString()
                         pendingAmt = String.format("%.2f", pendingAmt.toDouble())
                     }
                 }catch (ex:Exception){
                     pendingAmt="0"
                 }
             }else{
                 if(invoiceAmt.toDouble().toInt()!=0){
                     pendingAmt=invoiceAmt
                     pendingAmt = String.format("%.2f", pendingAmt.toDouble())
                 }
                 else{
                     pendingAmt=orderAmt
                     pendingAmt = String.format("%.2f", pendingAmt.toDouble())
                 }
             }
        }else{
            var ordA= AppDatabase.getDBInstance()!!.orderDetailsListDao().getOrderAmt(orderId).toString()
            when{
                ordA == null -> orderAmt="0"
                ordA !=null -> orderAmt=ordA
            }
            var invA= AppDatabase.getDBInstance()!!.billingDao().getInvoiceSumAmt(orderId)
            when{
                invA == null -> invoiceAmt="0"
                invA != null -> invoiceAmt=invA
            }

            var totalCollectA= AppDatabase.getDBInstance()!!.collectionDetailsDao().getCollectSumAmtByOrdID(orderId)
            var totalCollectionAmount = "0"
            when{
                totalCollectA == null -> totalCollectionAmount="0"
                totalCollectA != null -> totalCollectionAmount=totalCollectA
            }

            if(Pref.IsCollectionEntryConsiderOrderOrInvoice){
                if(invoiceAmt.toDouble() > totalCollectionAmount.toDouble()){
                    pendingAmt = (invoiceAmt.toDouble()-totalCollectionAmount.toDouble()).toString()
                    pendingAmt = String.format("%.2f", pendingAmt.toDouble())
                }
            }else{
                if(orderAmt.toDouble() > totalCollectionAmount.toDouble()){
                    pendingAmt = (orderAmt.toDouble()-totalCollectionAmount.toDouble()).toString()
                    pendingAmt = String.format("%.2f", pendingAmt.toDouble())
                }
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater?.inflate(R.layout.dialog_add_coll_with_ord, container, false)
        //addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)
        isCancelable = false

        initView(v)
        initClickListener()

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    private fun initView(v: View?) {
        et_collection = v?.findViewById(R.id.et_collection)!!
        shop_name_TV = v.findViewById(R.id.shop_name_TV)
        tvOrderAmt = v.findViewById(R.id.tv_dialog_add_coll_order_amt)
        tvInvoiceAmt = v.findViewById(R.id.tv_dialog_add_coll_invoice_amt)
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        add_TV = v.findViewById(R.id.add_TV)
        tv_collection_hint = v.findViewById(R.id.tv_collection_hint)
        //tv_collection_hint.text = "Collection as on " + AppUtils.getCurrentDate()
        dialog_header_TV = v.findViewById(R.id.dialog_header_TV)
        et_date = v.findViewById(R.id.et_date)
        rl_payment_mode = v.findViewById(R.id.rl_payment_mode)
        et_payment_mode = v.findViewById(R.id.et_payment_mode)
        tl_instrument = v.findViewById(R.id.tl_instrument)
        et_bank = v.findViewById(R.id.et_bank)
        iv_camera = v.findViewById(R.id.iv_camera)
        et_feedback = v.findViewById(R.id.et_feedback)
        et_link = v.findViewById(R.id.et_link)
        et_instrument = v.findViewById(R.id.et_instrument)
        progress_wheel = v.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        cv_collection_main = v.findViewById(R.id.cv_collection_main)
        iv_dropdown_icon = v.findViewById(R.id.iv_dropdown_icon)
        ll_image = v.findViewById(R.id.ll_image)
        rl_instrument_no = v.findViewById(R.id.rl_instrument_no)
        rl_bank = v.findViewById(R.id.rl_bank)
        tl_payment = v.findViewById(R.id.tl_payment)
        et_patient = v.findViewById(R.id.et_patient)
        et_address = v.findViewById(R.id.et_address)
        et_phone = v.findViewById(R.id.et_phone)
        rl_patient = v.findViewById(R.id.rl_patient)
        rl_address = v.findViewById(R.id.rl_address)
        rl_phone = v.findViewById(R.id.rl_phone)

        rl_lab = v.findViewById(R.id.rl_lab)
        rl_emailaddress = v.findViewById(R.id.rl_emailaddress)
        et_lab = v.findViewById(R.id.et_lab)
        et_emailaddress = v.findViewById(R.id.et_emailaddress)

        ll_dialog_add_coll_order_amt = v.findViewById(R.id.ll_dialog_add_coll_order_amt)
        ll_dialog_add_coll_inv_amt = v.findViewById(R.id.ll_dialog_add_coll_inv_amt)
        view_order_inv_amt = v.findViewById(R.id.view_order_inv_amt)

        et_date.setText(AppUtils.convertToCommonFormat(AppUtils.getFormattedDateForApi(myCalendar.time)))

        shop_name_TV.text = shopName

        try {
            et_collection.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter(9, 2)))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }


        if (Pref.isPatientDetailsShowInCollection) {
            rl_patient.visibility = View.VISIBLE
            rl_address.visibility = View.VISIBLE
            rl_phone.visibility = View.VISIBLE

            rl_lab.visibility = View.VISIBLE
            rl_emailaddress.visibility = View.VISIBLE
        }
        else {
            rl_patient.visibility = View.GONE
            rl_address.visibility = View.GONE
            rl_phone.visibility = View.GONE

            rl_lab.visibility = View.GONE
            rl_emailaddress.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(orderId)) {
            val collectionList = AppDatabase.getDBInstance()?.collectionDetailsDao()?.getListOrderWise(orderId)
            amount = ""
            collectionList?.forEach {
                amount += it.collection
            }

            val order = AppDatabase.getDBInstance()!!.orderDetailsListDao().getSingleOrder(orderId)
            if (!TextUtils.isEmpty(order?.patient_name))
                et_patient.setText(order?.patient_name)

            if (!TextUtils.isEmpty(order?.patient_address))
                et_address.setText(order?.patient_address)

            if (!TextUtils.isEmpty(order?.patient_no))
                et_phone.setText(order?.patient_no)

            if (!TextUtils.isEmpty(order?.Hospital))
                et_lab.setText(order?.Hospital)

            if (!TextUtils.isEmpty(order?.Email_Address))
                et_emailaddress.setText(order?.Email_Address)
        }

        if (!isAdd) {
            add_TV.visibility = View.GONE
            iv_dropdown_icon.visibility = View.GONE
            et_collection.setText(mAddShopDBModelEntity?.collection)
            et_collection.isEnabled = false
            //tv_collection_hint.text = "Collection as on " + /*AppUtils.convertDateTimeToCommonFormat(*/mAddShopDBModelEntity?.date!!//)
            dialog_header_TV.text = getString(R.string.view_collection_text)
            et_date.isEnabled = false
            et_patient.isEnabled = false
            et_address.isEnabled = false
            et_phone.isEnabled = false
            et_lab.isEnabled = false
            et_emailaddress.isEnabled = false
            et_date.setText(mAddShopDBModelEntity?.date)

            if (!TextUtils.isEmpty(mAddShopDBModelEntity?.bank))
                et_bank.setText(mAddShopDBModelEntity?.bank)
            else
                et_bank.setText("N.A.")

            if (!TextUtils.isEmpty(mAddShopDBModelEntity?.feedback))
                et_feedback.setText(mAddShopDBModelEntity?.feedback)
            else
                et_feedback.setText("N.A.")

            if (!TextUtils.isEmpty(mAddShopDBModelEntity?.instrument_no))
                et_instrument.setText(mAddShopDBModelEntity?.instrument_no)
            else
                et_instrument.setText("N.A.")

            if (!TextUtils.isEmpty(mAddShopDBModelEntity?.patient_name))
                et_patient.setText(mAddShopDBModelEntity?.patient_name)
            else
                et_patient.setText("N.A.")

            if (!TextUtils.isEmpty(mAddShopDBModelEntity?.patient_address))
                et_address.setText(mAddShopDBModelEntity?.patient_address)
            else
                et_address.setText("N.A.")

            if (!TextUtils.isEmpty(mAddShopDBModelEntity?.patient_no))
                et_phone.setText(mAddShopDBModelEntity?.patient_no)
            else
                et_phone.setText("N.A.")
            /*06-01-2022*/
            if (!TextUtils.isEmpty(mAddShopDBModelEntity?.Hospital))
                et_lab.setText(mAddShopDBModelEntity?.Hospital)
            else
                et_lab.setText("N.A.")

            if (!TextUtils.isEmpty(mAddShopDBModelEntity?.Email_Address))
                et_emailaddress.setText(mAddShopDBModelEntity?.Email_Address)
            else
                et_emailaddress.setText("N.A.")

            val payment = AppDatabase.getDBInstance()?.paymenttDao()?.getSingleData(mAddShopDBModelEntity?.payment_id!!)
            if (payment != null && !TextUtils.isEmpty(payment.name))
                et_payment_mode.setText(payment.name)
            else
                et_payment_mode.setText("N.A.")

            tl_payment.isEnabled = false

            if (!TextUtils.isEmpty(mAddShopDBModelEntity?.file_path)) {
                val extension = NewFileUtils.getExtension(File(mAddShopDBModelEntity?.file_path!!))
                if (extension.contains("jpg") || extension.contains("jpeg") || extension.contains("png")) {
                    iv_camera.visibility = View.VISIBLE
                    et_link.visibility = View.GONE
                    Glide.with(mContext)
                        .load(dataPath)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_camera_pic).error(R.drawable.ic_camera_pic))
                        .into(iv_camera)
                }
                else {
                    iv_camera.visibility = View.GONE
                    et_link.visibility = View.VISIBLE
                    et_link.setText(mAddShopDBModelEntity?.file_path)
                }
            }
            else
                ll_image.visibility = View.GONE

            if (paymentId == "4") {
                rl_instrument_no.visibility = View.GONE
                rl_bank.visibility = View.GONE
            }
            else {
                rl_instrument_no.visibility = View.VISIBLE
                rl_bank.visibility = View.VISIBLE
            }
        }
        else
            dialog_header_TV.text = getString(R.string.add_collection_text)

        tvOrderAmt.text="Rs. " +orderAmt
//        tvInvoiceAmt.text="Rs. " +invoiceAmt
        tvInvoiceAmt.text="Rs. " +String.format("%.2f", invoiceAmt.toDouble())
        et_collection.setText(String.format("%.2f", pendingAmt.toDouble()).toString())

    }

    private fun initClickListener() {
        add_TV.setOnClickListener(this)
        iv_close_icon.setOnClickListener(this)
        et_date.setOnClickListener(this)
        et_payment_mode.setOnClickListener(this)
        iv_dropdown_icon.setOnClickListener(this)
        iv_camera.setOnClickListener(this)
        et_link.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.add_TV -> {
                when {
                    TextUtils.isEmpty(et_collection.text.toString().trim()/*.substring(1)*/) -> {
                        AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_collection)
                        Toaster.msgShort(mContext, getString(R.string.error_enter_order_collection))
                    }
                    /*et_collection.text.toString().trim() < "1" -> {
                        AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_collection)
                        Toaster.msgShort(mContext, getString(R.string.error_enter_valid_collection))
                    }*/
                    /*!TextUtils.isEmpty(amount) && et_collection.text.toString().trim().toFloat() > amount.toFloat() -> {
                        AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_collection)
                        Toaster.msgShort(mContext, getString(R.string.error_collection_cannot_more) + " balance amount " +
                                getString(R.string.rupee_symbol) + amount)
                    }*/
                    TextUtils.isEmpty(paymentId) -> {
                        AppUtils.removeSoftKeyboard(mContext as DashboardActivity, rl_payment_mode)
                        Toaster.msgShort(mContext, getString(R.string.error_enter_payment_mode))
                    }
                    et_collection.text.toString().trim().toString().toDouble() == 0.0 ->{
                        AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_collection)
                        Toaster.msgShort(mContext, getString(R.string.error_enter_order_collection))
                    }
                    Pref.IsCollectionEntryConsiderOrderOrInvoice == true && invoiceAmt.toDouble().toInt()!=0 && pendingAmt.toDouble() < et_collection.text.toString().toDouble() ->{
                        AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_collection)
                        Toaster.msgShort(mContext, getString(R.string.error_collection_cannot_more) + " balance amount " +
                                getString(R.string.rupee_symbol) + pendingAmt)
                    }
                    Pref.IsCollectionEntryConsiderOrderOrInvoice == false && orderAmt.toDouble().toInt()!=0 && pendingAmt.toDouble() < et_collection.text.toString().toDouble() ->{
                        AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_collection)
                        Toaster.msgShort(mContext, getString(R.string.error_collection_cannot_more) + " balance amount " + getString(
                            R.string.rupee_symbol) + pendingAmt)
                    }
                    else -> {
                        /*addCollectionClickListener.onClick(et_collection.text.toString().trim(), et_date.text.toString().trim(), paymentId,
                                et_instrument.text.toString().trim(), et_bank.text.toString().trim(), dataPath, et_feedback.text.toString().trim(),
                                et_patient.text.toString().trim(), et_address.text.toString().trim(), et_phone.text.toString().trim(),
                            et_lab.text.toString().trim(),et_emailaddress.text.toString().trim())*/
                        addCollectionClickListener.onClick(String.format("%.2f", (et_collection.text.toString().trim()).toDouble()), et_date.text.toString().trim(), paymentId,
                            et_instrument.text.toString().trim(), et_bank.text.toString().trim(), dataPath, et_feedback.text.toString().trim(),
                            et_patient.text.toString().trim(), et_address.text.toString().trim(), et_phone.text.toString().trim(),
                            et_lab.text.toString().trim(),et_emailaddress.text.toString().trim(),orderId)
                        dismiss()
                    }
                }
            }

            R.id.iv_close_icon -> {
                dismiss()
            }

            R.id.et_date -> {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                val aniDatePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH))
                aniDatePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis

                if (!TextUtils.isEmpty(dateString))
                    aniDatePicker.datePicker.minDate = AppUtils.getTimeStampFromDateOnly(dateString)
                aniDatePicker.show()
            }

            R.id.et_payment_mode, R.id.iv_dropdown_icon -> {
                val list = AppDatabase.getDBInstance()?.paymenttDao()?.getAll()
                if (list != null && list.isNotEmpty()) {
                    if (paymentTypePopupWindow != null && paymentTypePopupWindow?.isShowing!!)
                        paymentTypePopupWindow?.dismiss()
                    else
                        callMeetingTypeDropDownPopUp(list)
                }
                else
                    getPaymentApi()
            }

            R.id.iv_camera, R.id.et_link -> {
                if (isAdd) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheck()
                    else {
                        showPictureDialog()
                    }
                }
                else {
                    if (!TextUtils.isEmpty(mAddShopDBModelEntity?.file_path)) {
                        val file = File(mAddShopDBModelEntity?.file_path!!)
                        if (mAddShopDBModelEntity?.file_path?.startsWith("http")!!) {
                            val mimeType = NewFileUtils.getMemeTypeFromFile(file.absolutePath + "." + NewFileUtils.getExtension(file))

                            if (mimeType != "image/jpeg" || mimeType != "image/png")
                                downloadFile(mAddShopDBModelEntity?.file_path, file.name)
                            else
                                openFile(file)
                        }
                        else
                            openFile(file)
                    }
                }
            }
        }
    }

    private fun downloadFile(downloadUrl: String?, fileName: String) {
        try {
            if (!AppUtils.isOnline(mContext)){
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }

            progress_wheel.spin()

            PRDownloader.download(downloadUrl, FTStorageUtils.getFolderPath(mContext) + "/", fileName)
                .build()
                .setOnProgressListener {
                    Log.e("Collection Details", "Attachment Download Progress======> $it")
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {

                        doAsync {
                            AppDatabase.getDBInstance()!!.collectionDetailsDao().updateAttachment(
                                FTStorageUtils.getFolderPath(mContext) + "/" + fileName, mAddShopDBModelEntity?.collection_id!!)

                            uiThread {
                                progress_wheel.stopSpinning()
                                val file = File(FTStorageUtils.getFolderPath(mContext) + "/" + fileName)
                                openFile(file)
                            }
                        }
                    }

                    override fun onError(error: Error) {
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage("Download failed")
                        Log.e("Collection Details", "Attachment download error msg=======> " + error.serverErrorMessage)
                    }
                })

        } catch (e: Exception) {
            (mContext as DashboardActivity).showSnackMessage("Download failed")
            progress_wheel.stopSpinning()
            e.printStackTrace()
        }

    }


    private fun openFile(file: File) {

        val mimeType = NewFileUtils.getMemeTypeFromFile(file.absolutePath + "." + NewFileUtils.getExtension(file))

        if (mimeType?.equals("application/pdf")!!) {
            val path1 = Uri.fromFile(file)
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
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/msword")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }
        } else if (mimeType == "application/vnd.ms-excel") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }

        } else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.template") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.openxmlformats-officedocument.wordprocessingml.template")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }
        } else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }

        } else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }
        } else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.template") {
            val path1 = Uri.fromFile(file)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callMeetingTypeDropDownPopUp(list: List<PaymentModeEntity>) {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Inflate the custom layout/view
        val customView = inflater!!.inflate(R.layout.popup_meeting_type, null)

        paymentTypePopupWindow = PopupWindow(customView, resources.getDimensionPixelOffset(R.dimen._220sdp), RelativeLayout.LayoutParams.WRAP_CONTENT)
        val rv_meeting_type_list = customView.findViewById(R.id.rv_meeting_type_list) as RecyclerView
        rv_meeting_type_list.layoutManager = LinearLayoutManager(mContext)

        paymentTypePopupWindow?.elevation = 200f
        paymentTypePopupWindow?.isFocusable = true
        paymentTypePopupWindow?.update()


        rv_meeting_type_list.adapter = PaymentTypeAdapter(mContext, list) {
            et_payment_mode.setText(it.name)
            paymentId = it.payment_id!!
            paymentTypePopupWindow?.dismiss()

            if (paymentId == "4") {
                rl_instrument_no.visibility = View.GONE
                rl_bank.visibility = View.GONE
            }
            else {
                rl_instrument_no.visibility = View.VISIBLE
                rl_bank.visibility = View.VISIBLE
            }
        }

        if (paymentTypePopupWindow != null && !paymentTypePopupWindow?.isShowing!!) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                cv_collection_main.post(Runnable {
                    paymentTypePopupWindow?.showAsDropDown(rl_payment_mode, resources.getDimensionPixelOffset(
                        R.dimen._1sdp), resources.getDimensionPixelOffset(R.dimen._10sdp), Gravity.BOTTOM)
                })
            } else {
                paymentTypePopupWindow?.showAsDropDown(rl_payment_mode, rl_payment_mode.width - paymentTypePopupWindow?.width!!, 0)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getPaymentApi() {
        if (!AppUtils.isOnline(mContext)) {
            Toaster.msgShort(mContext, getString(R.string.no_internet))
            return
        }

        val repository = NewCollectionListRepoProvider.newCollectionListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.paymentModeList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as PaymentModeResponseModel
                    XLog.d("PAYMENT RESPONSE=======> " + response.status)

                    if (response.status == NetworkConstant.SUCCESS) {
                        if (response.paymemt_mode_list != null && response.paymemt_mode_list!!.size > 0) {
                            doAsync {
                                response.paymemt_mode_list?.forEach {
                                    val paymentMode = PaymentModeEntity()
                                    AppDatabase.getDBInstance()?.paymenttDao()?.insert(paymentMode.apply {
                                        payment_id = it.id
                                        name = it.name
                                    })
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()

                                    if (paymentTypePopupWindow != null && paymentTypePopupWindow?.isShowing!!)
                                        paymentTypePopupWindow?.dismiss()
                                    else
                                        callMeetingTypeDropDownPopUp(AppDatabase.getDBInstance()?.paymenttDao()?.getAll()!!)
                                }
                            }


                        } else {
                            progress_wheel.stopSpinning()
                            Toaster.msgShort(mContext, response.message)
                        }
                    } else {
                        progress_wheel.stopSpinning()
                        Toaster.msgShort(mContext, response.message)
                    }

                }, { error ->
                    error.printStackTrace()
                    progress_wheel.stopSpinning()
                    Toaster.msgShort(mContext, getString(R.string.something_went_wrong))
                    XLog.d("PAYMENT ERROR=======> " + error.localizedMessage)
                })
        )
    }

    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                showPictureDialog()
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

    val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        et_date.setText(AppUtils.convertToCommonFormat(AppUtils.getFormattedDateForApi(myCalendar.time)))
        //tv_collection_hint.text = "Collection as on " + et_date.text
    }

    fun setImage(file: File) {
        dataPath = file.absolutePath

        val extension = NewFileUtils.getExtension(file)
        if (extension.contains("jpg") || extension.contains("jpeg") || extension.contains("png")) {
            iv_camera.visibility = View.VISIBLE
            et_link.visibility = View.GONE
            Glide.with(mContext)
                .load(dataPath)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_camera_pic).error(R.drawable.ic_camera_pic))
                .into(iv_camera)
        }
        else {
            iv_camera.visibility = View.GONE
            et_link.visibility = View.VISIBLE
            et_link.setText(file.name)
        }
    }

    interface AddCollectionClickLisneter {
        /*fun onClick(collection: String, date: String, paymentId: String, instrument: String, bank: String, filePath: String, feedback: String,
                    patientName: String, patientAddress: String, patinetNo: String,hospital:String,emailAddress:String)*/
        fun onClick(collection: String, date: String, paymentId: String, instrument: String, bank: String, filePath: String, feedback: String,
                    patientName: String, patientAddress: String, patinetNo: String,hospital:String,emailAddress:String,order_id:String)
    }
}
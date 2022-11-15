package com.kcteam.features.orderList

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.NumberToWords
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.kcteam.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.addshop.model.AssignedToShopListResponseModel
import com.kcteam.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.kcteam.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.kcteam.features.averageshop.presentation.AverageShopListClickListener
import com.kcteam.features.billing.api.billinglistapi.BillingListRepoProvider
import com.kcteam.features.billing.model.BillingListResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.nearbyshops.presentation.QrCodeDialog
import com.kcteam.features.orderList.api.neworderlistapi.NewOrderListRepoProvider
import com.kcteam.features.orderList.model.NewOrderListResponseModel
import com.kcteam.features.shopdetail.presentation.AddCollectionDialog
import com.kcteam.features.shopdetail.presentation.AddCollectionWithOrderDialog
import com.kcteam.features.shopdetail.presentation.api.addcollection.AddCollectionRepoProvider
import com.kcteam.features.shopdetail.presentation.model.addcollection.AddCollectionInputParamsModel
import com.kcteam.features.viewAllOrder.api.addorder.AddOrderRepoProvider
import com.kcteam.features.viewAllOrder.model.AddOrderInputParamsModel
import com.kcteam.features.viewAllOrder.model.AddOrderInputProductList
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class NewOrderListFragment : BaseFragment() {

    private lateinit var mContext: Context
    private lateinit var rv_order_list: RecyclerView
    private lateinit var no_shop_tv: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_order_count: AppCustomTextView
    private lateinit var rl_main: RelativeLayout

    private var i = 0
    private var collectionDialog: AddCollectionDialog?= null
    private var collectionDialog1: AddCollectionWithOrderDialog?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_new_order_list, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        rl_main = view.findViewById(R.id.rl_main)
        rl_main.setOnClickListener(null)
        rv_order_list = view.findViewById(R.id.rv_order_list)
        rv_order_list.layoutManager = LinearLayoutManager(mContext)
        no_shop_tv = view.findViewById(R.id.no_shop_tv)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        tv_order_count = view.findViewById(R.id.tv_order_count)
        tv_order_count.visibility = View.VISIBLE

        val list = AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>

        if (list != null && list.isNotEmpty()) {
            no_shop_tv.visibility = View.GONE

            val list_ = AppDatabase.getDBInstance()!!.billingDao().getAll()
            if (list_ == null || list_.isEmpty())
                getBillListApi(list)
            else
                initAdapter(list)

        } else
            getOrderList(true)
    }

    private fun getBillListApi(list: ArrayList<OrderDetailsListEntity>) {

        if (!AppUtils.isOnline(mContext)) {
            initAdapter(list)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = BillingListRepoProvider.provideBillListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getBillList(Pref.session_token!!, Pref.user_id!!, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BillingListResponseModel
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {
                                val billing_list = response.billing_list

                                if (billing_list != null && billing_list.isNotEmpty()) {

                                    doAsync {

                                        for (i in billing_list.indices) {
                                            val billing = BillingEntity()
                                            billing.bill_id = Pref.user_id + "_bill_" + System.currentTimeMillis()
                                            billing.invoice_no = billing_list[i].invoice_no
                                            billing.invoice_date = billing_list[i].invoice_date
                                            billing.invoice_amount = billing_list[i].invoice_amount
                                            billing.remarks = billing_list[i].remarks
                                            billing.order_id = billing_list[i].order_id
                                            billing.patient_no = billing_list[i].patient_no
                                            billing.patient_name = billing_list[i].patient_name
                                            billing.patient_address = billing_list[i].patient_address
                                            billing.isUploaded = true

                                            AppDatabase.getDBInstance()!!.billingDao().insertAll(billing)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            initAdapter(list)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    initAdapter(list)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                initAdapter(list)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            initAdapter(list)
                        })
        )
    }


    private fun getOrderList(isFromInitView: Boolean) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }


        if (!isFromInitView)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.wait_msg), 1000)


        val repository = NewOrderListRepoProvider.provideOrderListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getOrderList(Pref.session_token!!, Pref.user_id!!, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as NewOrderListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val order_details_list = response.order_list

                                if (order_details_list != null && order_details_list.isNotEmpty()) {

                                    if (!isFromInitView) {
                                        AppDatabase.getDBInstance()!!.orderDetailsListDao().delete()
                                        AppDatabase.getDBInstance()!!.orderProductListDao().delete()
                                    }


                                    doAsync {

                                        for (i in order_details_list.indices) {
                                            val orderDetailList = OrderDetailsListEntity()
                                            orderDetailList.date = order_details_list[i].order_date_time //AppUtils.convertToCommonFormat(order_details_list[i].date!!)
                                            orderDetailList.only_date = AppUtils.convertDateTimeToCommonFormat(order_details_list[i].order_date_time!!)
                                            orderDetailList.shop_id = order_details_list[i].shop_id
                                            orderDetailList.description = ""
                                            /*if (order_details_list[i].amount?.contains(".")!!)
                                                orderDetailList.amount = order_details_list[i].amount?.toDouble()?.toInt().toString()
                                            else
                                                orderDetailList.amount = order_details_list[i].amount*/
                                            if (!TextUtils.isEmpty(order_details_list[i].order_amount)) {
                                                val finalAmount = String.format("%.2f", order_details_list[i].order_amount?.toFloat())
                                                orderDetailList.amount = finalAmount
                                            }

                                            orderDetailList.isUploaded = true
                                            orderDetailList.order_id = order_details_list[i].order_id
                                            orderDetailList.collection = ""

                                            if (!TextUtils.isEmpty(order_details_list[i].order_lat) && !TextUtils.isEmpty(order_details_list[i].order_long)) {
                                                orderDetailList.order_lat = order_details_list[i].order_lat
                                                orderDetailList.order_long = order_details_list[i].order_long
                                            } else {
                                                orderDetailList.order_lat = order_details_list[i].shop_lat
                                                orderDetailList.order_long = order_details_list[i].shop_long
                                            }

                                            orderDetailList.patient_no = order_details_list[i].patient_no
                                            orderDetailList.patient_name = order_details_list[i].patient_name
                                            orderDetailList.patient_address = order_details_list[i].patient_address

                                            orderDetailList.Hospital = order_details_list[i].Hospital
                                            orderDetailList.Email_Address = order_details_list[i].Email_Address

                                            if (order_details_list[i].product_list != null && order_details_list[i].product_list?.size!! > 0) {
                                                for (j in order_details_list[i].product_list?.indices!!) {
                                                    val productOrderList = OrderProductListEntity()
                                                    productOrderList.brand = order_details_list[i].product_list?.get(j)?.brand
                                                    //productOrderList.brand_id = order_details_list[i].product_list?.get(j)?.brand_id
                                                    //productOrderList.category_id = order_details_list[i].product_list?.get(j)?.category_id
                                                    productOrderList.watt = order_details_list[i].product_list?.get(j)?.watt
                                                    // productOrderList.watt_id = order_details_list[i].product_list?.get(j)?.watt_id
                                                    productOrderList.product_id = order_details_list[i].product_list?.get(j)?.id.toString()
                                                    productOrderList.category = order_details_list[i].product_list?.get(j)?.category
                                                    productOrderList.order_id = order_details_list[i].order_id
                                                    productOrderList.product_name = order_details_list[i].product_list?.get(j)?.product_name

                                                    /*06-01-2022*/
                                                    if (!TextUtils.isEmpty(order_details_list[i].product_list?.get(j)?.MRP)) {
                                                        val finalMRP = String.format("%.2f", order_details_list[i].product_list?.get(j)?.MRP?.toFloat())
                                                        productOrderList.MRP = finalMRP
                                                    }

                                                    /*if (order_details_list[i].product_list?.get(j)?.rate?.contains(".")!!)
                                                        productOrderList.rate = order_details_list[i].product_list?.get(j)?.rate?.toDouble()?.toInt().toString()
                                                    else*/
                                                    if (!TextUtils.isEmpty(order_details_list[i].product_list?.get(j)?.rate)) {
                                                        val finalRate = String.format("%.2f", order_details_list[i].product_list?.get(j)?.rate?.toFloat())
                                                        productOrderList.rate = finalRate
                                                    }

                                                    productOrderList.qty = order_details_list[i].product_list?.get(j)?.qty

                                                    /*if (order_details_list[i].product_list?.get(j)?.total_price?.contains(".")!!)
                                                        productOrderList.total_price = order_details_list[i].product_list?.get(j)?.total_price?.toDouble()?.toInt().toString()
                                                    else*/
                                                    if (!TextUtils.isEmpty(order_details_list[i].product_list?.get(j)?.total_price)) {
                                                        val finalTotalPrice = String.format("%.2f", order_details_list[i].product_list?.get(j)?.total_price?.toFloat())
                                                        productOrderList.total_price = finalTotalPrice
                                                    }
                                                    productOrderList.shop_id = order_details_list[i].shop_id

                                                    AppDatabase.getDBInstance()!!.orderProductListDao().insert(productOrderList)
                                                }
                                            }

                                            AppDatabase.getDBInstance()!!.orderDetailsListDao().insert(orderDetailList)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            no_shop_tv.visibility = View.GONE
                                            //initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)
                                            val list_ = AppDatabase.getDBInstance()!!.billingDao().getAll()
                                            if (list_ == null || list_.isEmpty())
                                                getBillListApi(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)
                                            else
                                                initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)

                                            if (!isFromInitView)
                                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.success_msg), 1000)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                    if (isFromInitView)
                                        no_shop_tv.visibility = View.VISIBLE
                                    else
                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                                }
                            } else {
                                progress_wheel.stopSpinning()

                                if (isFromInitView)
                                    no_shop_tv.visibility = View.VISIBLE
                                else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()

                            if (isFromInitView)
                                no_shop_tv.visibility = View.VISIBLE
                            else
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                        })
        )
    }

    private fun initAdapter(list: ArrayList<OrderDetailsListEntity>) {
        tv_order_count.text = "Total Order(s): " + list.size

        rv_order_list.adapter = NewDateWiseOrderListAdapter(mContext, list, object : AverageShopListClickListener {
            override fun onSyncClick(position: Int) {
                //syncShopActivity(ShopActivityEntityList[position].shopid!!)

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(list[position].shop_id)

                if (shop != null) {

                    if (shop.isUploaded) {

                        if (!list[position].isUploaded) {
                            addOrderApi(list[position].shop_id, list[position].order_id, list[position].amount!!, list[position].description!!, list[position].collection!!,
                                    list[position].date, list[position].order_lat, list[position].order_long, list[position].remarks,
                                    list[position].signature, list[position])
                        }

                    } else {
                        syncShop(position, shop, list)
                    }
                }
            }

            override fun OnItemClick(position: Int) {
                //(mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, orderList?.get(position)?.shop_id!!)
            }

            override fun OnMenuClick(position: Int, view: View) {
                // initiatePopupWindow(view, position)
            }

            override fun onQuestionnarieClick(shopId: String) {

            }

            override fun onReturnClick(position: Int) {

            }

            override fun onDamageClick(shop_id: String) {
                TODO("Not yet implemented")
            }

            override fun onSurveyClick(shop_id: String) {

            }

            override fun onMultipleImageClick(shop: Any, position: Int) {
                TODO("Not yet implemented")
            }
        }, { shopId: String, orderId: String ->
            val shopType = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopType(shopId)
            senOrderEmail(shopId, orderId, shopType)
        }, {

//            val heading = "SALES ORDER"
//            var pdfBody = "\n\n\n\nOrder No.: " + it.order_id + "\n\nOrder Date: " + AppUtils.convertDateTimeToCommonFormat(it.date!!) +
//                    "\n\nParty Name: "
//
//            val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(it.shop_id)
//
//            pdfBody = pdfBody + shop?.shopName + "\n\nAddress: " + shop?.address + "\n\nContact No.: " + shop?.ownerContactNumber +
//                    "\n\nSales Person: " + Pref.user_name + "\n\n\n"
//
//            if (Pref.isPatientDetailsShowInOrder) {
//                var patientName = "N.A."
//                if (!TextUtils.isEmpty(it.patient_name))
//                    patientName = it.patient_name!!
//
//                var patientNo = "N.A."
//                if (!TextUtils.isEmpty(it.patient_no))
//                    patientNo = it.patient_no!!
//
//                var patientAddress = "N.A."
//                if (!TextUtils.isEmpty(it.patient_address))
//                    patientAddress = it.patient_address!!
//
//                pdfBody = pdfBody + "Patient Name: " + patientName + "\n\nPatient Address: " + patientAddress + "\n\nPhone: " +
//                        patientNo + "\n\n\n"
//            }
//
//            val productList = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToOrderId(it.order_id!!)
//            productList?.forEach {it1 ->
//                pdfBody = pdfBody + "Item: " + it1.product_name + "\nQty: " + it1.qty + "  Rate: " +
//                        getString(R.string.rupee_symbol_with_space) + it1.rate + "  Amount: " + getString(R.string.rupee_symbol_with_space) +
//                        it1.total_price + "\n\n"
//            }
//
//            pdfBody = pdfBody + "Total Amount: " + getString(R.string.rupee_symbol_with_space) + it.amount
//
//            val image = BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher)
//
//            val path = FTStorageUtils.stringToPdf(pdfBody, mContext, "FTS_" + it.order_id + ".pdf", image, heading,
//                    2.7f)
//
//            /*var msg = ""
//            msg = if (status == 1)
//                "Pdf saved successfully."
//            else
//                "Pdf can not be saved."*/
//
//            if (!TextUtils.isEmpty(path)) {
//                try {
//                    val shareIntent = Intent(Intent.ACTION_SEND)
//                    val fileUrl = Uri.parse(path)
//
//                    val file = File(fileUrl.path)
//
//                    //val uri = Uri.fromFile(file)
//                    //27-09-2021
//                    val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
//                    shareIntent.type = "image/png"
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//                    startActivity(Intent.createChooser(shareIntent, "Share pdf using"));
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//            else
//                (mContext as DashboardActivity).showSnackMessage("Pdf can not be sent.")

            saveDataAsPdf(it)

        }, {
            try {

                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else {

                    val addShop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(it.shop_id)

                    var bID = ""
                    var bDtlList = AppDatabase.getDBInstance()!!.billingDao().getDataOrderIdWise(it.order_id.toString())
                    if(bDtlList!= null && bDtlList.size>0){
                        bID=bDtlList.get(0).bill_id
                    }


                    if(Pref.IsCollectionOrderWise){
                        collectionDialog1 = AddCollectionWithOrderDialog.getInstance(it, true, addShop?.shopName!!, AppUtils.getCurrentDateFormatInTa(it.only_date!!),
                            it.amount!!, it.order_id!!,bID, object : AddCollectionWithOrderDialog.AddCollectionClickLisneter {
                                override fun onClick(collection: String, date: String, paymentId: String, instrument: String, bank: String, filePath: String, feedback: String, patientName: String, patientAddress: String, patinetNo: String,
                                                     hospital: String, emailAddress: String,order_id:String) {

                                    //val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shop_id)
                                    if (addShop != null) {
                                        //if (addShop.isUploaded) {
                                        val order = it
                                        doAsync {

                                            val collectionDetails = CollectionDetailsEntity()
                                            collectionDetails.collection = collection/*.substring(1)*/

                                            val random = Random()
                                            val m = random.nextInt(9999 - 1000) + 1000

                                            //collectionDetails.collection_id = Pref.user_id + "_" + m /*+ "_" + System.currentTimeMillis().toString()*/
                                            collectionDetails.collection_id = Pref.user_id + "c" + m
                                            collectionDetails.shop_id = it.shop_id
                                            collectionDetails.date = date //AppUtils.getCurrentDate()
                                            collectionDetails.only_time = AppUtils.getCurrentTime()  //AppUtils.getCurrentDate()
                                            collectionDetails.bill_id = ""
                                            collectionDetails.order_id = it.order_id
                                            collectionDetails.payment_id = paymentId
                                            collectionDetails.bank = bank
                                            collectionDetails.instrument_no = instrument
                                            collectionDetails.file_path = filePath
                                            collectionDetails.feedback = feedback
                                            collectionDetails.patient_name = patientName
                                            collectionDetails.patient_address = patientAddress
                                            collectionDetails.patient_no = patinetNo
                                            /*06-01-2022*/
                                            collectionDetails.Hospital = hospital
                                            collectionDetails.Email_Address = emailAddress

                                            collectionDetails.order_id = order_id
                                            AppDatabase.getDBInstance()!!.collectionDetailsDao().insert(collectionDetails)

                                            val collectionDate = AppUtils.getCurrentDateForShopActi() + "T" + collectionDetails.only_time

                                            uiThread {

                                                if (AppUtils.isOnline(mContext)) {
                                                    if (addShop.isUploaded) {
                                                        if (order.isUploaded) {
                                                            addCollectionApi(collectionDetails.shop_id, collectionDetails.collection_id, "",
                                                                "", collection, collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                                        } else {
                                                            syncOrderForCollection(order, collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
                                                                collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                                        }
                                                    } else {
                                                        syncShopForCollection(addShop, collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
                                                            collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                                    }

                                                } else {
                                                    (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                                    voiceCollectionMsg()
                                                }
                                            }
                                        }
                                    }
                                }
                            })
                        collectionDialog1?.show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionWithOrderDialog")
                    }
                    else{
                        collectionDialog = AddCollectionDialog.getInstance(it, true, addShop?.shopName!!, AppUtils.getCurrentDateFormatInTa(it.only_date!!),
                            it.amount!!, it.order_id!!, object : AddCollectionDialog.AddCollectionClickLisneter {
                                override fun onClick(collection: String, date: String, paymentId: String, instrument: String, bank: String, filePath: String, feedback: String, patientName: String, patientAddress: String, patinetNo: String,
                                                     hospital: String, emailAddress: String,order_id:String) {

                                    //val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shop_id)
                                    if (addShop != null) {

                                        //if (addShop.isUploaded) {
                                        val order = it
                                        doAsync {

                                            val collectionDetails = CollectionDetailsEntity()
                                            collectionDetails.collection = collection/*.substring(1)*/

                                            val random = Random()
                                            val m = random.nextInt(9999 - 1000) + 1000

                                            //collectionDetails.collection_id = Pref.user_id + "_" + m /*+ "_" + System.currentTimeMillis().toString()*/
                                            collectionDetails.collection_id = Pref.user_id + "c" + m
                                            collectionDetails.shop_id = it.shop_id
                                            collectionDetails.date = date //AppUtils.getCurrentDate()
                                            collectionDetails.only_time = AppUtils.getCurrentTime()  //AppUtils.getCurrentDate()
                                            collectionDetails.bill_id = ""
                                            collectionDetails.order_id = it.order_id
                                            collectionDetails.payment_id = paymentId
                                            collectionDetails.bank = bank
                                            collectionDetails.instrument_no = instrument
                                            collectionDetails.file_path = filePath
                                            collectionDetails.feedback = feedback
                                            collectionDetails.patient_name = patientName
                                            collectionDetails.patient_address = patientAddress
                                            collectionDetails.patient_no = patinetNo
                                            /*06-01-2022*/
                                            collectionDetails.Hospital = hospital
                                            collectionDetails.Email_Address = emailAddress

                                            collectionDetails.order_id = order_id
                                            AppDatabase.getDBInstance()!!.collectionDetailsDao().insert(collectionDetails)

                                            val collectionDate = AppUtils.getCurrentDateForShopActi() + "T" + collectionDetails.only_time

                                            uiThread {

                                                if (AppUtils.isOnline(mContext)) {
                                                    if (addShop.isUploaded) {
                                                        if (order.isUploaded) {
                                                            addCollectionApi(collectionDetails.shop_id, collectionDetails.collection_id, "",
                                                                "", collection, collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                                        } else {
                                                            syncOrderForCollection(order, collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
                                                                collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                                        }
                                                    } else {
                                                        syncShopForCollection(addShop, collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
                                                            collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                                    }

                                                } else {
                                                    (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                                    voiceCollectionMsg()
                                                }
                                            }
                                        }
                                    }
                                }
                            })
                        collectionDialog?.show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionDialog")
                    }

                }

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }, {
            if (!TextUtils.isEmpty(it.order_lat) && !TextUtils.isEmpty(it.order_long))
                (mContext as DashboardActivity).openLocationMap(it, false)
            else
                (mContext as DashboardActivity).showSnackMessage("No order location available")
        }, {
            var pdfBody = "Order No.: " + it.order_id + "\n\nOrder Date: " + AppUtils.convertDateTimeToCommonFormat(it.date!!) +
                    "\n\nParty Name: "

            val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(it.shop_id)

            pdfBody = pdfBody + shop?.shopName + "\n\nAddress: " + shop?.address + "\n\nContact No.: " + shop?.ownerContactNumber +
                    "\n\nSales Person: " + Pref.user_name + "\n\n\n"

            val productList = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToOrderId(it.order_id!!)
            productList?.forEach { it1 ->
                pdfBody = pdfBody + "Item: " + it1.product_name + "\nQty: " + it1.qty + "  Rate: INR. " + it1.rate +
                        "  Amount: INR. " + it1.total_price + "\n\n"
            }

            pdfBody = pdfBody + "Total Amount: INR. " + it.amount
            val bitmap = AppUtils.createQrCode(pdfBody)

            if (bitmap != null)
                QrCodeDialog.newInstance(bitmap, it.shop_id!!, shop?.shopName!!, it.order_id!!, "Create QR of Order").show((mContext as DashboardActivity).supportFragmentManager, "")
        })
    }

    private fun saveDataAsPdf(obj: OrderDetailsListEntity) {
        /*21-04-2022 new pdf format*/
        var document: Document = Document()
        var fileName = "FTS"+ "_" + obj.order_id
        fileName = fileName.replace("/", "_")

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/kcteamApp/ORDERDETALIS/"

        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        try {
            PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))



            document.open()

            var font: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD)
            var fontBoldU: Font = Font(Font.FontFamily.HELVETICA, 12f, Font.UNDERLINE or Font.BOLD)
            var font1: Font = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL)
            val grayFront = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, BaseColor.GRAY)




            //image add
            val bm: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            val bitmap = Bitmap.createScaledBitmap(bm, 50, 50, true);
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            var img: Image? = null
            val byteArray: ByteArray = stream.toByteArray()
            try {
                img = Image.getInstance(byteArray)
                img.scaleToFit(90f, 90f)
                img.scalePercent(70f)
                img.alignment = Image.ALIGN_LEFT
            } catch (e: BadElementException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //document.add(img)


            val Heading = Paragraph("SALES ORDER ", fontBoldU)
            Heading.alignment = Element.ALIGN_CENTER
            Heading.spacingAfter = 2f
            //document.add(Heading)


/////////////////////////////

            val sp = Paragraph("", font)
            sp.spacingAfter = 50f
            document.add(sp)




            val h = Paragraph("SALES ORDER ", fontBoldU)
            h.alignment = Element.ALIGN_CENTER

            val pHead = Paragraph()
            pHead.add(Chunk(img, 0f, -30f))
            pHead.add(h)
            document.add(pHead)

            /*val paraHead = Paragraph()
            val glueHead = Chunk(VerticalPositionMark())
            val ph1Head = Phrase()
            val mainHead = Paragraph()
            ph1Head.add(Chunk(img, 0f,0f))
            ph1Head.add(glueHead) // Here I add special chunk to the same phrase.

            ph1Head.add(Chunk("SALES ORDER: " + "\u20B9" + obj.amount, font))
            paraHead.add(ph1Head)
            document.add(paraHead)*/
////////////////////////////////////


            
            val x = Paragraph("", font)
            x.spacingAfter = 20f
            document.add(x)

            val widthsOrder = floatArrayOf(0.50f, 0.50f)

            var tableHeaderOrder: PdfPTable = PdfPTable(widthsOrder)
            tableHeaderOrder.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
            tableHeaderOrder.setWidthPercentage(100f)

            var invoiceNo="."
            var invoiceDate="."
            try{
                invoiceNo = AppDatabase.getDBInstance()!!.billingDao().getInvoice(obj.order_id!!)
                if (invoiceNo==null){
                    invoiceNo=""
                }
            }catch (ex:Exception){
                invoiceNo=""
            }
            try{
                invoiceDate = AppDatabase.getDBInstance()!!.billingDao().getInvoiceDate(obj.order_id!!)
                if (invoiceDate==null){
                    invoiceDate=""
                }
            }catch (ex:Exception){
                invoiceDate=""
            }

            val cell11 = PdfPCell(Phrase("Order No       :     " + obj.order_id + "\n\n" + "Order Date    :     " + AppUtils.convertDateTimeToCommonFormat(obj.date!!), font))
            cell11.setHorizontalAlignment(Element.ALIGN_LEFT)
            cell11.borderColor = BaseColor.GRAY
            tableHeaderOrder.addCell(cell11)


            val cell222 = PdfPCell(Phrase("Invoice No     :     " + invoiceNo + "\n\n" + "Invoice Date  :     " + invoiceDate, font))
            cell222.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell222.borderColor = BaseColor.GRAY
            cell222.paddingBottom=10f
            tableHeaderOrder.addCell(cell222)

            document.add(tableHeaderOrder)


            var orderNoDate: String = ""
            var InvoicDate: String = ""
            val tableRows = PdfPTable(widthsOrder)
            tableRows.defaultCell.horizontalAlignment = Element.ALIGN_LEFT
            tableRows.setWidthPercentage(100f);

            var cellBodySl1 = PdfPCell(Phrase(orderNoDate + "Order Date: " + AppUtils.convertDateTimeToCommonFormat(obj.date!!), font))
            cellBodySl1.setHorizontalAlignment(Element.ALIGN_LEFT)
            cellBodySl1.borderColor = BaseColor.GRAY
//            tableRows.addCell(cellBodySl1)


            var cellBody22 = PdfPCell(Phrase(InvoicDate + invoiceNo + "Invoice Date: " + invoiceDate, font))
            cellBody22.setHorizontalAlignment(Element.ALIGN_LEFT)
            cellBody22.borderColor = BaseColor.GRAY
//            tableRows.addCell(cellBody22)

            document.add(tableRows)

            document.add(Paragraph())


            val xz = Paragraph("", font)
            xz.spacingAfter = 10f
            document.add(xz)

            val HeadingPartyDetls = Paragraph("Details of Party ", fontBoldU)
            HeadingPartyDetls.indentationLeft = 82f
//            HeadingPartyDetls.alignment = Element.ALIGN_LEFT
            HeadingPartyDetls.spacingAfter = 2f
            document.add(HeadingPartyDetls)

            val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(obj.shop_id)

            val Parties = Paragraph("Name                    :      " + shop?.shopName, font1)
            Parties.alignment = Element.ALIGN_LEFT
            Parties.spacingAfter = 2f
            document.add(Parties)

            val address = Paragraph("Address                :      " + shop?.address, font1)
            address.alignment = Element.ALIGN_LEFT
            address.spacingAfter = 2f
            document.add(address)


            val Contact = Paragraph("Contact No.          :      " + shop?.ownerContactNumber, font1)
            Contact.alignment = Element.ALIGN_LEFT
            Contact.spacingAfter = 2f
            document.add(Contact)


            if (Pref.isPatientDetailsShowInOrder) {
                val PatientName = Paragraph("Patient Name        :  " + obj.patient_name, font1)
                PatientName.alignment = Element.ALIGN_LEFT
                PatientName.spacingAfter = 2f
                document.add(PatientName)

                val PatientAddr = Paragraph("Patient Address     :  " + obj.patient_address, font1)
                PatientAddr.alignment = Element.ALIGN_LEFT
                PatientAddr.spacingAfter = 2f
                document.add(PatientAddr)

                val PatientPhone = Paragraph("Patient Phone        :  " + obj.patient_no, font1)
                PatientPhone.alignment = Element.ALIGN_LEFT
                PatientPhone.spacingAfter = 10f
                document.add(PatientPhone)

            }
            val xze = Paragraph("", font)
            xze.spacingAfter = 10f
            document.add(xze)

            // table header
            //val widths = floatArrayOf(0.55f, 0.05f, 0.2f, 0.2f)
            val widths = floatArrayOf(0.06f, 0.58f, 0.07f, 0.07f, 0.07f, 0.15f)

            var tableHeader: PdfPTable = PdfPTable(widths)
            tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT)
            tableHeader.setWidthPercentage(100f)

            val cell111 = PdfPCell(Phrase("SL. ", font))
            cell111.setHorizontalAlignment(Element.ALIGN_LEFT)
            cell111.borderColor = BaseColor.GRAY
            tableHeader.addCell(cell111);

            val cell1 = PdfPCell(Phrase("Item Description ", font))
            cell1.setHorizontalAlignment(Element.ALIGN_LEFT)
            cell1.borderColor = BaseColor.GRAY
            tableHeader.addCell(cell1);

            val cell2 = PdfPCell(Phrase("Qty ", font))
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.borderColor = BaseColor.GRAY
            tableHeader.addCell(cell2);

            val cell21 = PdfPCell(Phrase("Unit ", font))
            cell21.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell21.borderColor = BaseColor.GRAY
            tableHeader.addCell(cell21);

            val cell3 = PdfPCell(Phrase("Rate ", font))
            cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell3.borderColor = BaseColor.GRAY
            tableHeader.addCell(cell3);

            val cell4 = PdfPCell(Phrase("Amount ", font))
            cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell4.borderColor = BaseColor.GRAY
            tableHeader.addCell(cell4);

            document.add(tableHeader)

            //table body
            var srNo: String = ""
            var item: String = ""
            var qty: String = ""
            var unit: String = ""
            var rate: String = ""
            var amount: String = ""

            val productList = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToOrderId(obj.order_id!!)

            for (i in 0..productList.size-1) {
                srNo = (i+1).toString() +" "
                item = productList!!.get(i).product_name +  "       "
                qty = productList!!.get(i).qty +" "
                unit = "KG" +" "
                rate =   getString(R.string.rupee_symbol_with_space)+" "+productList !!.get(i).rate +" "
                amount = getString(R.string.rupee_symbol_with_space)+" "+productList!!.get(i).total_price +" "


                val tableRows = PdfPTable(widths)
                tableRows.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                tableRows.setWidthPercentage(100f);


                var cellBodySr = PdfPCell(Phrase(srNo, font1))
                cellBodySr.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellBodySr.borderColor = BaseColor.GRAY
                tableRows.addCell(cellBodySr)

                var cellBodySl = PdfPCell(Phrase(item, font1))
                cellBodySl.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellBodySl.borderColor = BaseColor.GRAY
                tableRows.addCell(cellBodySl)

                var cellBody2 = PdfPCell(Phrase(qty, font1))
                cellBody2.setHorizontalAlignment(Element.ALIGN_LEFT)
                cellBody2.borderColor = BaseColor.GRAY
                tableRows.addCell(cellBody2)


                var cellBody21 = PdfPCell(Phrase(unit, font1))
                cellBody21.setHorizontalAlignment(Element.ALIGN_LEFT)
                cellBody21.borderColor = BaseColor.GRAY
                tableRows.addCell(cellBody21)

                var cellBody3 = PdfPCell(Phrase(rate, font1))
                cellBody3.setHorizontalAlignment(Element.ALIGN_LEFT)
                cellBody3.borderColor = BaseColor.GRAY
                tableRows.addCell(cellBody3)

                var cellBody4 = PdfPCell(Phrase(amount, font1))
                cellBody4.setHorizontalAlignment(Element.ALIGN_LEFT)
                cellBody4.borderColor = BaseColor.GRAY
                tableRows.addCell(cellBody4)

                document.add(tableRows)

                document.add(Paragraph())
            }
            val xffx = Paragraph("", font)
            xffx.spacingAfter = 12f
            document.add(xffx)

//            val widthsamount = floatArrayOf(0.70f,0.30f)
//
//            var tableamountHeader: PdfPTable = PdfPTable(widthsamount)
//            tableamountHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT)
//            tableamountHeader.setWidthPercentage(100f)
//
//            val cellamount = PdfPCell(Phrase(convertIntoWords(obj.amount!!.toDouble(),"en","US")+"Only  "+"  "+"Total Amount: " + "\u20B9" + obj.amount, font))
//            cellamount.setHorizontalAlignment(Element.ALIGN_LEFT)
//            cellamount.borderColor = BaseColor.GRAY
//            tableamountHeader.addCell(cellamount)


//            document.add(tableamountHeader)

            val para = Paragraph()
            val glue = Chunk(VerticalPositionMark())
            val ph1 = Phrase()
            val main = Paragraph()
            //ph1.add(Chunk("Rupees " + convertIntoWords(obj.amount!!.toDouble(), "en", "US")!!.toUpperCase() + " Only  ", font))
            ph1.add(Chunk("Rupees " + NumberToWords.numberToWord(obj.amount!!.toDouble().toInt()!!)!!.toUpperCase() + " Only  ", font))
            ph1.add(glue) // Here I add special chunk to the same phrase.

            ph1.add(Chunk("Total Amount: " + "\u20B9" + obj.amount, font))
            para.add(ph1)
            document.add(para)

//            val TotalAmountword = Paragraph("" + "\u20B9" + convertIntoWords(obj.amount!!.toDouble(),"en","US"), font)
//            TotalAmountword.alignment = Element.ALIGN_LEFT
//            TotalAmountword.spacingAfter = 2f
//            document.add(TotalAmountword)
//
//
//            val TotalAmount = Paragraph("Total Amount:" + "\u20B9" + obj.amount, font)
//            TotalAmount.alignment = Element.ALIGN_RIGHT
//            TotalAmount.spacingAfter = 2f
//            document.add(TotalAmount)

            val xfx = Paragraph("", font)
            xfx.spacingAfter = 12f
            document.add(xfx)


            val widthsSalesPerson = floatArrayOf(1f)

            var tablewidthsSalesPersonHeader: PdfPTable = PdfPTable(widthsSalesPerson)
            tablewidthsSalesPersonHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT)
            tablewidthsSalesPersonHeader.setWidthPercentage(100f)

            val cellsales = PdfPCell(Phrase("Entered by: " + Pref.user_name, font1))
            cellsales.setHorizontalAlignment(Element.ALIGN_LEFT)
            cellsales.borderColor = BaseColor.GRAY
            tablewidthsSalesPersonHeader.addCell(cellsales)


            document.add(tablewidthsSalesPersonHeader)



//            val salesPerson = Paragraph("Entered by: " + Pref.user_name, font)
//            salesPerson.alignment = Element.ALIGN_LEFT
//            salesPerson.spacingAfter = 10f
//            document.add(salesPerson)

            document.close()

            var sendingPath = path + fileName + ".pdf"
            if (!TextUtils.isEmpty(sendingPath)) {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    val fileUrl = Uri.parse(sendingPath)
                    val file = File(fileUrl.path)
                    val uri: Uri = FileProvider.getUriForFile(mContext, mContext.applicationContext.packageName.toString() + ".provider", file)
                    shareIntent.type = "image/png"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(shareIntent, "Share pdf using"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong1))
                }
            }
        }
        catch (ex: Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }
    }

    private fun voiceCollectionMsg() {
        if (Pref.isVoiceEnabledForCollectionSaved) {
            val msg = "Hi, Collection saved successfully."
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Add Collection", "TTS error in converting Text to Speech!");

        }
    }

    private fun syncOrderForCollection(order: OrderDetailsListEntity, shop_id: String?, collection_id: String?, amount: String, desc: String, collection: String,
                                       currentDateForShopActi: String, billId: String?, orderId: String?, collectionDetails: CollectionDetailsEntity) {

        val addOrder = AddOrderInputParamsModel()
        addOrder.collection = ""
        addOrder.description = ""
        addOrder.order_amount = order.amount
        addOrder.order_date = order.date
        addOrder.order_id = order.order_id
        addOrder.shop_id = shop_id
        addOrder.session_token = Pref.session_token
        addOrder.user_id = Pref.user_id
        addOrder.latitude = order.order_lat
        addOrder.longitude = order.order_long

        if (order.scheme_amount != null)
            addOrder.scheme_amount = order.scheme_amount
        else
            addOrder.scheme_amount = ""

        if (order.remarks != null)
            addOrder.remarks = order.remarks
        else
            addOrder.remarks = ""

        if (order.patient_name != null)
            addOrder.patient_name = order.patient_name
        else
            addOrder.patient_name = ""

        if (order.patient_address != null)
            addOrder.patient_address = order.patient_address
        else
            addOrder.patient_address = ""

        if (order.patient_no != null)
            addOrder.patient_no = order.patient_no
        else
            addOrder.patient_no = ""

        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shop_id!!)
        if (shopActivity != null) {
            if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)

                if (!TextUtils.isEmpty(shopDetail.address))
                    addOrder.address = shopDetail.address
                else
                    addOrder.address = ""
            } else {
                if (!TextUtils.isEmpty(order.order_lat) && !TextUtils.isEmpty(order.order_long))
                    addOrder.address = LocationWizard.getLocationName(mContext, order.order_lat!!.toDouble(), order.order_long!!.toDouble())
                else
                    addOrder.address = ""
            }
        } else {
            if (!TextUtils.isEmpty(order.order_lat) && !TextUtils.isEmpty(order.order_long))
                addOrder.address = LocationWizard.getLocationName(mContext, order.order_lat!!.toDouble(), order.order_long!!.toDouble())
            else
                addOrder.address = ""
        }
        /*06-01-2022*/
        if (order.Hospital != null)
            addOrder.Hospital = order.Hospital
        else
            addOrder.Hospital = ""

        if (order.Email_Address != null)
            addOrder.Email_Address = order.Email_Address
        else
            addOrder.Email_Address = ""
        val list = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToShopAndOrderId(order.order_id!!, shop_id!!)
        val productList = java.util.ArrayList<AddOrderInputProductList>()

        for (i in list.indices) {
            val product = AddOrderInputProductList()
            product.id = list[i].product_id
            product.qty = list[i].qty
            product.rate = list[i].rate
            product.total_price = list[i].total_price
            product.product_name = list[i].product_name
            product.scheme_qty = list[i].scheme_qty
            product.scheme_rate = list[i].scheme_rate
            product.total_scheme_price = list[i].total_scheme_price
            product.MRP = list[i].MRP
            productList.add(product)
        }

        addOrder.product_list = productList

        progress_wheel.spin()

        if (TextUtils.isEmpty(order.signature)) {
            val repository = AddOrderRepoProvider.provideAddOrderRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addNewOrder(addOrder)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, order.order_id!!)

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")

                                    initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)
                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                            billId, orderId, collectionDetails)

                                } else
                                    (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                            })
            )
        }
        else {
            val repository = AddOrderRepoProvider.provideAddOrderImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addNewOrder(addOrder, order.signature!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, order.order_id!!)

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")

                                    initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)
                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                            billId, orderId, collectionDetails)

                                } else
                                    (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                            })
            )
        }
    }


    private fun syncShopForCollection(addShop: AddShopDBModelEntity, shop_id: String?, collection_id: String?, amount: String, desc: String, collection: String,
                                      currentDateForShopActi: String, billId: String?, orderId: String?, collectionDetails: CollectionDetailsEntity) {
        val addShopData = AddShopRequestData()
        val mAddShopDBModelEntity = addShop
        addShopData.session_token = Pref.session_token
        addShopData.address = mAddShopDBModelEntity.address
        addShopData.owner_contact_no = mAddShopDBModelEntity.ownerContactNumber
        addShopData.owner_email = mAddShopDBModelEntity.ownerEmailId
        addShopData.owner_name = mAddShopDBModelEntity.ownerName
        addShopData.pin_code = mAddShopDBModelEntity.pinCode
        addShopData.shop_lat = mAddShopDBModelEntity.shopLat.toString()
        addShopData.shop_long = mAddShopDBModelEntity.shopLong.toString()
        addShopData.shop_name = mAddShopDBModelEntity.shopName.toString()
        addShopData.type = mAddShopDBModelEntity.type.toString()
        addShopData.shop_id = mAddShopDBModelEntity.shop_id
        addShopData.user_id = Pref.user_id
        addShopData.assigned_to_pp_id = mAddShopDBModelEntity.assigned_to_pp_id
        addShopData.assigned_to_dd_id = mAddShopDBModelEntity.assigned_to_dd_id
        addShopData.added_date = mAddShopDBModelEntity.added_date
        addShopData.amount = mAddShopDBModelEntity.amount
        addShopData.area_id = mAddShopDBModelEntity.area_id
        addShopData.model_id = mAddShopDBModelEntity.model_id
        addShopData.primary_app_id = mAddShopDBModelEntity.primary_app_id
        addShopData.secondary_app_id = mAddShopDBModelEntity.secondary_app_id
        addShopData.lead_id = mAddShopDBModelEntity.lead_id
        addShopData.stage_id = mAddShopDBModelEntity.stage_id
        addShopData.funnel_stage_id = mAddShopDBModelEntity.funnel_stage_id
        addShopData.booking_amount = mAddShopDBModelEntity.booking_amount
        addShopData.type_id = mAddShopDBModelEntity.type_id

        addShopData.director_name = mAddShopDBModelEntity.director_name
        addShopData.key_person_name = mAddShopDBModelEntity.person_name
        addShopData.phone_no = mAddShopDBModelEntity.person_no

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.family_member_dob))
            addShopData.family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.family_member_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.add_dob))
            addShopData.addtional_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.add_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.add_doa))
            addShopData.addtional_doa = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.add_doa)

        addShopData.specialization = mAddShopDBModelEntity.specialization
        addShopData.category = mAddShopDBModelEntity.category
        addShopData.doc_address = mAddShopDBModelEntity.doc_address
        addShopData.doc_pincode = mAddShopDBModelEntity.doc_pincode
        addShopData.is_chamber_same_headquarter = mAddShopDBModelEntity.chamber_status.toString()
        addShopData.is_chamber_same_headquarter_remarks = mAddShopDBModelEntity.remarks
        addShopData.chemist_name = mAddShopDBModelEntity.chemist_name
        addShopData.chemist_address = mAddShopDBModelEntity.chemist_address
        addShopData.chemist_pincode = mAddShopDBModelEntity.chemist_pincode
        addShopData.assistant_contact_no = mAddShopDBModelEntity.assistant_no
        addShopData.average_patient_per_day = mAddShopDBModelEntity.patient_count
        addShopData.assistant_name = mAddShopDBModelEntity.assistant_name

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.doc_family_dob))
            addShopData.doc_family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.doc_family_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_dob))
            addShopData.assistant_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_doa))
            addShopData.assistant_doa = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_doa)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_family_dob))
            addShopData.assistant_family_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_family_dob)

        addShopData.entity_id = mAddShopDBModelEntity.entity_id
        addShopData.party_status_id = mAddShopDBModelEntity.party_status_id
        addShopData.retailer_id = mAddShopDBModelEntity.retailer_id
        addShopData.dealer_id = mAddShopDBModelEntity.dealer_id
        addShopData.beat_id = mAddShopDBModelEntity.beat_id
        addShopData.assigned_to_shop_id = mAddShopDBModelEntity.assigned_to_shop_id
        addShopData.actual_address = mAddShopDBModelEntity.actual_address

        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id!!, false)
        addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!

        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name


        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate

        addShopData.purpose=mAddShopDBModelEntity.purpose

        addShopData.GSTN_Number=mAddShopDBModelEntity.gstN_Number
        addShopData.ShopOwner_PAN=mAddShopDBModelEntity.shopOwner_PAN

        callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shop_id, collection_id, amount, collection,
                currentDateForShopActi, desc, billId, mAddShopDBModelEntity.doc_degree, orderId, collectionDetails)
    }

    private var isShopRegistrationInProcess = false
    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, shop_id: String?, collection_id: String?, amount: String, collection: String,
                               currentDateForShopActi: String, desc: String, billId: String?, degree_imgPath: String?, orderId: String?,
                               collectionDetails: CollectionDetailsEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        if (isShopRegistrationInProcess)
            return

        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("==============================SyncShop Input Params(Shop List)==============================")
        XLog.d("shop id=======> " + addShop.shop_id)
        val index = addShop.shop_id!!.indexOf("_")
        XLog.d("decoded shop id=======> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
        XLog.d("shop added date=======> " + addShop.added_date)
        XLog.d("shop address=======> " + addShop.address)
        XLog.d("assigned to dd id=======> " + addShop.assigned_to_dd_id)
        XLog.d("assigned to pp id=======> " + addShop.assigned_to_pp_id)
        XLog.d("date aniversery=======> " + addShop.date_aniversary)
        XLog.d("dob=======> " + addShop.dob)
        XLog.d("shop owner phn no=======> " + addShop.owner_contact_no)
        XLog.d("shop owner email=======> " + addShop.owner_email)
        XLog.d("shop owner name=======> " + addShop.owner_name)
        XLog.d("shop pincode=======> " + addShop.pin_code)
        XLog.d("session token=======> " + addShop.session_token)
        XLog.d("shop lat=======> " + addShop.shop_lat)
        XLog.d("shop long=======> " + addShop.shop_long)
        XLog.d("shop name=======> " + addShop.shop_name)
        XLog.d("shop type=======> " + addShop.type)
        XLog.d("user id=======> " + addShop.user_id)
        XLog.d("amount=======> " + addShop.amount)
        XLog.d("area id=======> " + addShop.area_id)
        XLog.d("model id=======> " + addShop.model_id)
        XLog.d("primary app id=======> " + addShop.primary_app_id)
        XLog.d("secondary app id=======> " + addShop.secondary_app_id)
        XLog.d("lead id=======> " + addShop.lead_id)
        XLog.d("stage id=======> " + addShop.stage_id)
        XLog.d("funnel stage id=======> " + addShop.funnel_stage_id)
        XLog.d("booking amount=======> " + addShop.booking_amount)
        XLog.d("type id=======> " + addShop.type_id)

        if (shop_imgPath != null)
            XLog.d("shop image path=======> $shop_imgPath")

        XLog.d("director name=======> " + addShop.director_name)
        XLog.d("family member dob=======> " + addShop.family_member_dob)
        XLog.d("key person's name=======> " + addShop.key_person_name)
        XLog.d("phone no=======> " + addShop.phone_no)
        XLog.d("additional dob=======> " + addShop.addtional_dob)
        XLog.d("additional doa=======> " + addShop.addtional_doa)
        XLog.d("doctor family member dob=======> " + addShop.doc_family_member_dob)
        XLog.d("specialization=======> " + addShop.specialization)
        XLog.d("average patient count per day=======> " + addShop.average_patient_per_day)
        XLog.d("category=======> " + addShop.category)
        XLog.d("doctor address=======> " + addShop.doc_address)
        XLog.d("doctor pincode=======> " + addShop.doc_pincode)
        XLog.d("chambers or hospital under same headquarter=======> " + addShop.is_chamber_same_headquarter)
        XLog.d("chamber related remarks=======> " + addShop.is_chamber_same_headquarter_remarks)
        XLog.d("chemist name=======> " + addShop.chemist_name)
        XLog.d("chemist name=======> " + addShop.chemist_address)
        XLog.d("chemist pincode=======> " + addShop.chemist_pincode)
        XLog.d("assistant name=======> " + addShop.assistant_name)
        XLog.d("assistant contact no=======> " + addShop.assistant_contact_no)
        XLog.d("assistant dob=======> " + addShop.assistant_dob)
        XLog.d("assistant date of anniversary=======> " + addShop.assistant_doa)
        XLog.d("assistant family dob=======> " + addShop.assistant_family_dob)
        XLog.d("entity id=======> " + addShop.entity_id)
        XLog.d("party status id=======> " + addShop.party_status_id)
        XLog.d("retailer id=======> " + addShop.retailer_id)
        XLog.d("dealer id=======> " + addShop.dealer_id)
        XLog.d("beat id=======> " + addShop.beat_id)
        XLog.d("assigned to shop id=======> " + addShop.assigned_to_shop_id)
        XLog.d("actual address=======> " + addShop.actual_address)

        if (degree_imgPath != null)
            XLog.d("doctor degree image path=======> $degree_imgPath")
        XLog.d("=================================================================================")

        if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(degree_imgPath)) {
            val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShop(addShop)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                when (addShopResult.status) {
                                    NetworkConstant.SUCCESS -> {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                        //(mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                        doAsync {
                                            val resultAs = runLongTask(addShop.shop_id)
                                            uiThread {
                                                if (resultAs == true) {
                                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetails)
                                                }
                                            }
                                        }
                                        progress_wheel.stopSpinning()
                                        isShopRegistrationInProcess = false

                                    }
                                    NetworkConstant.DUPLICATE_SHOP_ID -> {
                                        XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                            AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                            AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                        }
                                        doAsync {
                                            val resultAs = runLongTask(addShop.shop_id)
                                            uiThread {
                                                if (resultAs == true) {
                                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetails)
                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                                        isShopRegistrationInProcess = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                isShopRegistrationInProcess = false
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
        else {
            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShop, shop_imgPath, degree_imgPath, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                when (addShopResult.status) {
                                    NetworkConstant.SUCCESS -> {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                        //(mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                        doAsync {
                                            val resultAs = runLongTask(addShop.shop_id)
                                            uiThread {
                                                if (resultAs == true) {
                                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetails)
                                                }
                                            }
                                        }
                                        progress_wheel.stopSpinning()
                                        isShopRegistrationInProcess = false

                                    }
                                    NetworkConstant.DUPLICATE_SHOP_ID -> {
                                        XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                            AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                            AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                        }
                                        doAsync {
                                            val resultAs = runLongTask(addShop.shop_id)
                                            uiThread {
                                                if (resultAs == true) {
                                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetails)
                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                                        isShopRegistrationInProcess = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                isShopRegistrationInProcess = false
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }

    }

    private fun addCollectionApi(shop_id: String?, collection_id: String?, amount: String, desc: String, collection: String, date: String?,
                                 billId: String?, orderId: String?, collectionDetails: CollectionDetailsEntity) {

        val addCollection = AddCollectionInputParamsModel()
        addCollection.collection = collection
        addCollection.collection_date = date
        addCollection.collection_id = collection_id
        addCollection.session_token = Pref.session_token
        addCollection.user_id = Pref.user_id
        addCollection.shop_id = shop_id

        if (!TextUtils.isEmpty(billId))
            addCollection.bill_id = billId!!
        else
            addCollection.bill_id = ""

        if (!TextUtils.isEmpty(orderId))
            addCollection.order_id = orderId!!
        else
            addCollection.order_id = ""

        addCollection.payment_id = collectionDetails.payment_id!!

        if (collectionDetails.instrument_no != null)
            addCollection.instrument_no = collectionDetails.instrument_no!!

        if(collectionDetails.bank != null)
            addCollection.bank = collectionDetails.bank!!

        if (collectionDetails.feedback != null)
            addCollection.remarks = collectionDetails.feedback!!

        if (collectionDetails.patient_name != null)
            addCollection.patient_name = collectionDetails.patient_name!!

        if (collectionDetails.patient_address != null)
            addCollection.patient_address = collectionDetails.patient_address!!

        if (collectionDetails.patient_no != null)
            addCollection.patient_no = collectionDetails.patient_no!!

        /*06-01-2022*/
        if (collectionDetails.Hospital != null)
            addCollection.Hospital = collectionDetails.Hospital!!

        if (collectionDetails.Email_Address != null)
            addCollection.Email_Address = collectionDetails.Email_Address!!

        progress_wheel.spin()

        if (TextUtils.isEmpty(collectionDetails.file_path)) {
            val repository = AddCollectionRepoProvider.addCollectionRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addCollection(addCollection)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, collection_id!!)
                                }

                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                voiceCollectionMsg()

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                voiceCollectionMsg()
                            })
            )
        }
        else {
            val repository = AddCollectionRepoProvider.addCollectionMultipartRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addCollection(addCollection, collectionDetails.file_path, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, collection_id!!)
                                }

                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                voiceCollectionMsg()

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                voiceCollectionMsg()
                            })
            )
        }
    }

    private fun senOrderEmail(shopId: String, orderId: String, shopType: String?) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = NewOrderListRepoProvider.provideOrderListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.sendOrderEmail(shopId, orderId, shopType!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            if (response.status == NetworkConstant.SUCCESS) {

                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun addOrderApi(shop_id: String?, order_id: String?, amount: String, desc: String, collection: String, date: String?, order_lat: String?,
                            order_long: String?, remarks: String?, signature: String?, orderListDetails: OrderDetailsListEntity) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val addOrder = AddOrderInputParamsModel()
        addOrder.collection = ""
        addOrder.description = ""
        addOrder.order_amount = amount
        addOrder.order_date = date
        addOrder.order_id = order_id
        addOrder.shop_id = shop_id
        addOrder.session_token = Pref.session_token
        addOrder.user_id = Pref.user_id
        addOrder.latitude = order_lat
        addOrder.longitude = order_long

        if (remarks != null)
            addOrder.remarks = remarks
        else
            addOrder.remarks = ""

        if (orderListDetails.patient_name != null)
            addOrder.patient_name = orderListDetails.patient_name
        else
            addOrder.patient_name = ""

        if (orderListDetails.patient_address != null)
            addOrder.patient_address = orderListDetails.patient_address
        else
            addOrder.patient_address = ""

        if (orderListDetails.patient_no != null)
            addOrder.patient_no = orderListDetails.patient_no
        else
            addOrder.patient_no = ""

        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shop_id!!)

        if (shopActivity != null) {
            if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)

                if (!TextUtils.isEmpty(shopDetail.address))
                    addOrder.address = shopDetail.address
                else
                    addOrder.address = ""
            } else {
                if (!TextUtils.isEmpty(order_lat) && !TextUtils.isEmpty(order_long))
                    addOrder.address = LocationWizard.getLocationName(mContext, order_lat!!.toDouble(), order_long!!.toDouble())
                else
                    addOrder.address = ""
            }
        } else {
            if (!TextUtils.isEmpty(order_lat) && !TextUtils.isEmpty(order_long))
                addOrder.address = LocationWizard.getLocationName(mContext, order_lat!!.toDouble(), order_long!!.toDouble())
            else
                addOrder.address = ""
        }

        /*06-01-2022*/
        if (orderListDetails.Hospital != null)
            addOrder.Hospital = orderListDetails.Hospital
        else
            addOrder.Hospital = ""

        if (orderListDetails.Email_Address != null)
            addOrder.Email_Address = orderListDetails.Email_Address
        else
            addOrder.Email_Address = ""

        val list = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToShopAndOrderId(order_id!!, shop_id!!)
        val productList = ArrayList<AddOrderInputProductList>()

        for (i in list.indices) {
            val product = AddOrderInputProductList()
            product.id = list[i].product_id
            product.qty = list[i].qty
            product.rate = list[i].rate
            product.total_price = list[i].total_price
            product.product_name = list[i].product_name
            product.scheme_qty = list[i].scheme_qty
            product.scheme_rate = list[i].scheme_rate
            product.total_scheme_price = list[i].total_scheme_price
            product.MRP = list[i].MRP

            productList.add(product)
        }

        addOrder.product_list = productList

        progress_wheel.spin()

        if (TextUtils.isEmpty(signature)) {
            val repository = AddOrderRepoProvider.provideAddOrderRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addNewOrder(addOrder)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, order_id)

                                    val shopActivityList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shop_id, AppUtils.getCurrentDateForShopActi())

                                    /*if (shopActivityList[0].isVisited && shopActivityList[0].isDurationCalculated) {
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id, AppUtils.getCurrentDateForShopActi())
                                }*/

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                    initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)
                                } else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_sync_order))

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_sync_order))
                            })
            )
        }
        else {
            val repository = AddOrderRepoProvider.provideAddOrderImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addNewOrder(addOrder, signature!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, order_id)

                                    val shopActivityList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shop_id, AppUtils.getCurrentDateForShopActi())

                                    /*if (shopActivityList[0].isVisited && shopActivityList[0].isDurationCalculated) {
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id, AppUtils.getCurrentDateForShopActi())
                                }*/

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                    initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)
                                } else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_sync_order))

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_sync_order))
                            })
            )
        }
    }

    private fun syncShop(position: Int, shop: AddShopDBModelEntity, list: ArrayList<OrderDetailsListEntity>) {
        val addShopData = AddShopRequestData()
        //if (!shop.isUploaded) {
        addShopData.session_token = Pref.session_token
        addShopData.address = shop.address
        addShopData.owner_contact_no = shop.ownerContactNumber
        addShopData.owner_email = shop.ownerEmailId
        addShopData.owner_name = shop.ownerName
        addShopData.pin_code = shop.pinCode
        addShopData.shop_lat = shop.shopLat.toString()
        addShopData.shop_long = shop.shopLong.toString()
        addShopData.shop_name = shop.shopName.toString()
        addShopData.type = shop.type.toString()
        addShopData.shop_id = shop.shop_id
        addShopData.user_id = Pref.user_id

        if (!TextUtils.isEmpty(shop.dateOfBirth))
            addShopData.dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.dateOfBirth)

        if (!TextUtils.isEmpty(shop.dateOfAniversary))
            addShopData.date_aniversary = AppUtils.changeAttendanceDateFormatToCurrent(shop.dateOfAniversary)

        addShopData.assigned_to_dd_id = shop.assigned_to_dd_id
        addShopData.assigned_to_pp_id = shop.assigned_to_pp_id
        addShopData.added_date = shop.added_date
        addShopData.amount = shop.amount
        addShopData.area_id = shop.area_id
        addShopData.model_id = shop.model_id
        addShopData.primary_app_id = shop.primary_app_id
        addShopData.secondary_app_id = shop.secondary_app_id
        addShopData.lead_id = shop.lead_id
        addShopData.stage_id = shop.stage_id
        addShopData.funnel_stage_id = shop.funnel_stage_id
        addShopData.booking_amount = shop.booking_amount
        addShopData.type_id = shop.type_id

        addShopData.director_name = shop.director_name
        addShopData.key_person_name = shop.person_name
        addShopData.phone_no = shop.person_no

        if (!TextUtils.isEmpty(shop.family_member_dob))
            addShopData.family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.family_member_dob)

        if (!TextUtils.isEmpty(shop.add_dob))
            addShopData.addtional_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.add_dob)

        if (!TextUtils.isEmpty(shop.add_doa))
            addShopData.addtional_doa = AppUtils.changeAttendanceDateFormatToCurrent(shop.add_doa)

        addShopData.specialization = shop.specialization
        addShopData.category = shop.category
        addShopData.doc_address = shop.doc_address
        addShopData.doc_pincode = shop.doc_pincode
        addShopData.is_chamber_same_headquarter = shop.chamber_status.toString()
        addShopData.is_chamber_same_headquarter_remarks = shop.remarks
        addShopData.chemist_name = shop.chemist_name
        addShopData.chemist_address = shop.chemist_address
        addShopData.chemist_pincode = shop.chemist_pincode
        addShopData.assistant_contact_no = shop.assistant_no
        addShopData.average_patient_per_day = shop.patient_count
        addShopData.assistant_name = shop.assistant_name

        if (!TextUtils.isEmpty(shop.doc_family_dob))
            addShopData.doc_family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.doc_family_dob)

        if (!TextUtils.isEmpty(shop.assistant_dob))
            addShopData.assistant_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.assistant_dob)

        if (!TextUtils.isEmpty(shop.assistant_doa))
            addShopData.assistant_doa = AppUtils.changeAttendanceDateFormatToCurrent(shop.assistant_doa)

        if (!TextUtils.isEmpty(shop.assistant_family_dob))
            addShopData.assistant_family_dob = AppUtils.changeAttendanceDateFormatToCurrent(shop.assistant_family_dob)

        addShopData.entity_id = shop.entity_id
        addShopData.party_status_id = shop.party_status_id
        addShopData.retailer_id = shop.retailer_id
        addShopData.dealer_id = shop.dealer_id
        addShopData.beat_id = shop.beat_id
        addShopData.assigned_to_shop_id = shop.assigned_to_shop_id
        addShopData.actual_address = shop.actual_address

        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(shop.shop_id!!, false)
        addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!

        // duplicate shop api call
        addShopData.isShopDuplicate=shop.isShopDuplicate
        addShopData.purpose=shop.purpose

        addShopData.GSTN_Number=shop.gstN_Number
        addShopData.ShopOwner_PAN=shop.shopOwner_PAN


        callAddShopApi(addShopData, shop.shopImageLocalPath, position, list, shop.doc_degree)
        //}
    }

    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, position: Int, list: ArrayList<OrderDetailsListEntity>,
                               doc_degree: String?) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()


        XLog.d("==============SyncShop Input Params (Order List)====================")
        XLog.d("shop id=======> " + addShop.shop_id)
        val index = addShop.shop_id!!.indexOf("_")
        XLog.d("decoded shop id=======> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
        XLog.d("shop added date=======> " + addShop.added_date)
        XLog.d("shop address=======> " + addShop.address)
        XLog.d("assigned to dd id=======> " + addShop.assigned_to_dd_id)
        XLog.d("assigned to pp id=======> " + addShop.assigned_to_pp_id)
        XLog.d("date aniversery=======> " + addShop.date_aniversary)
        XLog.d("dob=======> " + addShop.dob)
        XLog.d("shop owner phn no=======> " + addShop.owner_contact_no)
        XLog.d("shop owner email=======> " + addShop.owner_email)
        XLog.d("shop owner name=======> " + addShop.owner_name)
        XLog.d("shop pincode=======> " + addShop.pin_code)
        XLog.d("session token=======> " + addShop.session_token)
        XLog.d("shop lat=======> " + addShop.shop_lat)
        XLog.d("shop long=======> " + addShop.shop_long)
        XLog.d("shop name=======> " + addShop.shop_name)
        XLog.d("shop type=======> " + addShop.type)
        XLog.d("user id=======> " + addShop.user_id)
        XLog.d("amount=======> " + addShop.amount)
        XLog.d("area id=======> " + addShop.area_id)
        XLog.d("model id=======> " + addShop.model_id)
        XLog.d("primary app id=======> " + addShop.primary_app_id)
        XLog.d("secondary app id=======> " + addShop.secondary_app_id)
        XLog.d("lead id=======> " + addShop.lead_id)
        XLog.d("stage id=======> " + addShop.stage_id)
        XLog.d("funnel stage id=======> " + addShop.funnel_stage_id)
        XLog.d("booking amount=======> " + addShop.booking_amount)
        XLog.d("type id=======> " + addShop.type_id)

        if (shop_imgPath != null)
            XLog.d("shop image path=======> $shop_imgPath")

        XLog.d("director name=======> " + addShop.director_name)
        XLog.d("family member dob=======> " + addShop.family_member_dob)
        XLog.d("key person's name=======> " + addShop.key_person_name)
        XLog.d("phone no=======> " + addShop.phone_no)
        XLog.d("additional dob=======> " + addShop.addtional_dob)
        XLog.d("additional doa=======> " + addShop.addtional_doa)
        XLog.d("family member dob=======> " + addShop.family_member_dob)
        XLog.d("key person's name=======> " + addShop.key_person_name)
        XLog.d("phone no=======> " + addShop.phone_no)
        XLog.d("additional dob=======> " + addShop.addtional_dob)
        XLog.d("additional doa=======> " + addShop.addtional_doa)
        XLog.d("doctor family member dob=======> " + addShop.doc_family_member_dob)
        XLog.d("specialization=======> " + addShop.specialization)
        XLog.d("average patient count per day=======> " + addShop.average_patient_per_day)
        XLog.d("category=======> " + addShop.category)
        XLog.d("doctor address=======> " + addShop.doc_address)
        XLog.d("doctor pincode=======> " + addShop.doc_pincode)
        XLog.d("chambers or hospital under same headquarter=======> " + addShop.is_chamber_same_headquarter)
        XLog.d("chamber related remarks=======> " + addShop.is_chamber_same_headquarter_remarks)
        XLog.d("chemist name=======> " + addShop.chemist_name)
        XLog.d("chemist name=======> " + addShop.chemist_address)
        XLog.d("chemist pincode=======> " + addShop.chemist_pincode)
        XLog.d("assistant name=======> " + addShop.assistant_name)
        XLog.d("assistant contact no=======> " + addShop.assistant_contact_no)
        XLog.d("assistant dob=======> " + addShop.assistant_dob)
        XLog.d("assistant date of anniversary=======> " + addShop.assistant_doa)
        XLog.d("assistant family dob=======> " + addShop.assistant_family_dob)
        XLog.d("entity id=======> " + addShop.entity_id)
        XLog.d("party status id=======> " + addShop.party_status_id)
        XLog.d("retailer id=======> " + addShop.retailer_id)
        XLog.d("dealer id=======> " + addShop.dealer_id)
        XLog.d("beat id=======> " + addShop.beat_id)
        XLog.d("assigned to shop id=======> " + addShop.assigned_to_shop_id)
        XLog.d("actual address=======> " + addShop.actual_address)

        if (doc_degree != null)
            XLog.d("doctor degree image path=======> $doc_degree")
        XLog.d("======================================================================")

        if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(doc_degree)) {
            val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShop(addShop)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    progress_wheel.stopSpinning()
                                    getAssignedPPListApi(addShop.shop_id, position, list, true)

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    getAssignedPPListApi(addShop.shop_id, position, list, true)
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                }


                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
        else {
            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShop, shop_imgPath, doc_degree, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    progress_wheel.stopSpinning()
                                    getAssignedPPListApi(addShop.shop_id, position, list, true)

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    getAssignedPPListApi(addShop.shop_id, position, list, true)
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                }


                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
    }

    private fun runLongTask(shop_id: String?): Any {
        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(shop_id!!, true, false)
        if (shopActivity != null)
            callShopActivitySubmit(shop_id)
        return true
    }

    private var shop_duration = ""
    private fun callShopActivitySubmit(shopId: String) {
        var list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())
        if (list.isEmpty())
            return

        var shopDataList: MutableList<ShopDurationRequestData> = java.util.ArrayList()
        var shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token

        if (!Pref.isMultipleVisitEnable) {
            var shopActivity = list[0]

            var shopDurationData = ShopDurationRequestData()
            shopDurationData.shop_id = shopActivity.shopid
            if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())

                shopDurationData.spent_duration = duration
            } else {
                shopDurationData.spent_duration = shopActivity.duration_spent
            }
            shopDurationData.visited_date = shopActivity.visited_date
            shopDurationData.visited_time = shopActivity.visited_date
            if (TextUtils.isEmpty(shopActivity.distance_travelled))
                shopActivity.distance_travelled = "0.0"
            shopDurationData.distance_travelled = shopActivity.distance_travelled
            var sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
            if (sList != null && sList.isNotEmpty())
                shopDurationData.total_visit_count = sList[0].totalVisitCount

            if (!TextUtils.isEmpty(shopActivity.feedback))
                shopDurationData.feedback = shopActivity.feedback
            else
                shopDurationData.feedback = ""

            shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
            shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc
            shopDurationData.next_visit_date = shopActivity.next_visit_date

            if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
            else
                shopDurationData.early_revisit_reason = ""

            shopDurationData.device_model = shopActivity.device_model
            shopDurationData.android_version = shopActivity.android_version
            shopDurationData.battery = shopActivity.battery
            shopDurationData.net_status = shopActivity.net_status
            shopDurationData.net_type = shopActivity.net_type
            shopDurationData.in_time = shopActivity.in_time
            shopDurationData.out_time = shopActivity.out_time
            shopDurationData.start_timestamp = shopActivity.startTimeStamp
            shopDurationData.in_location = shopActivity.in_loc
            shopDurationData.out_location = shopActivity.out_loc

            shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!

            /*10-12-2021*/
            shopDurationData.updated_by = Pref.user_id
            try {
                shopDurationData.updated_on = shopActivity.updated_on!!
            }catch (ex: Exception){
                shopDurationData.updated_on = ""
            }

            if (!TextUtils.isEmpty(shopActivity.pros_id!!))
                shopDurationData.pros_id = shopActivity.pros_id!!
            else
                shopDurationData.pros_id = ""

            if (!TextUtils.isEmpty(shopActivity.agency_name!!))
                shopDurationData.agency_name =shopActivity.agency_name!!
            else
                shopDurationData.agency_name = ""

            if (!TextUtils.isEmpty(shopActivity.approximate_1st_billing_value))
                shopDurationData.approximate_1st_billing_value = shopActivity.approximate_1st_billing_value!!
            else
                shopDurationData.approximate_1st_billing_value = ""
            //duration garbage fix
            try{
                if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                {
                    shopDurationData.spent_duration="00:00:10"
                }
            }catch (ex:Exception){
                shopDurationData.spent_duration="00:00:10"
            }
            shopDataList.add(shopDurationData)
        }
        else {
            for (i in list.indices) {
                var shopActivity = list[i]

                var shopDurationData = ShopDurationRequestData()
                shopDurationData.shop_id = shopActivity.shopid
                if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                    val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                    val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)

                    shopDurationData.spent_duration = duration
                } else {
                    shopDurationData.spent_duration = shopActivity.duration_spent
                }
                shopDurationData.visited_date = shopActivity.visited_date
                shopDurationData.visited_time = shopActivity.visited_date

                if (TextUtils.isEmpty(shopActivity.distance_travelled))
                    shopActivity.distance_travelled = "0.0"

                shopDurationData.distance_travelled = shopActivity.distance_travelled

                var sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
                if (sList != null && sList.isNotEmpty())
                    shopDurationData.total_visit_count = sList[0].totalVisitCount

                if (!TextUtils.isEmpty(shopActivity.feedback))
                    shopDurationData.feedback = shopActivity.feedback
                else
                    shopDurationData.feedback = ""

                shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
                shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc
                shopDurationData.next_visit_date = shopActivity.next_visit_date

                if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                    shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
                else
                    shopDurationData.early_revisit_reason = ""

                shopDurationData.device_model = shopActivity.device_model
                shopDurationData.android_version = shopActivity.android_version
                shopDurationData.battery = shopActivity.battery
                shopDurationData.net_status = shopActivity.net_status
                shopDurationData.net_type = shopActivity.net_type
                shopDurationData.in_time = shopActivity.in_time
                shopDurationData.out_time = shopActivity.out_time
                shopDurationData.start_timestamp = shopActivity.startTimeStamp
                shopDurationData.in_location = shopActivity.in_loc
                shopDurationData.out_location = shopActivity.out_loc

                shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!

                /*10-12-2021*/
                shopDurationData.updated_by = Pref.user_id
                try {
                    shopDurationData.updated_on = shopActivity.updated_on!!
                }catch (ex: Exception){
                    shopDurationData.updated_on =""
                }

                if (!TextUtils.isEmpty(shopActivity.pros_id!!))
                    shopDurationData.pros_id = shopActivity.pros_id!!
                else
                    shopDurationData.pros_id = ""

                if (!TextUtils.isEmpty(shopActivity.agency_name!!))
                    shopDurationData.agency_name =shopActivity.agency_name!!
                else
                    shopDurationData.agency_name = ""

                if (!TextUtils.isEmpty(shopActivity.approximate_1st_billing_value))
                    shopDurationData.approximate_1st_billing_value = shopActivity.approximate_1st_billing_value!!
                else
                    shopDurationData.approximate_1st_billing_value = ""
                //duration garbage fix
                try{
                    if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                    {
                        shopDurationData.spent_duration="00:00:10"
                    }
                }catch (ex:Exception){
                    shopDurationData.spent_duration="00:00:10"
                }
                shopDataList.add(shopDurationData)
            }
        }

        if (shopDataList.isEmpty()) {
            return
        }

        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            XLog.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + ", RESPONSE:" + result.message)
                            if (result.status == NetworkConstant.SUCCESS) {

                            }

                        }, { error ->
                            error.printStackTrace()
                            if (error != null)
                                XLog.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + error.localizedMessage)
//                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )

    }

    private fun getAssignedPPListApi(shop_id: String?, position: Int, list_: ArrayList<OrderDetailsListEntity>, isSyncFromList: Boolean) {

        val shopActivityList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shop_id!!, AppUtils.getCurrentDateForShopActi())

        if (!Pref.isMultipleVisitEnable) {
            if (shopActivityList[0].isVisited && shopActivityList[0].isDurationCalculated) {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id, AppUtils.getCurrentDateForShopActi())
                XLog.d("================sync locally shop visited (order list)===============")
            }
        }
        else {
            shopActivityList.forEach {
                if (it.isVisited && it.isDurationCalculated) {
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id, AppUtils.getCurrentDateForShopActi(), it.startTimeStamp)
                    XLog.d("================sync locally shop visited (order list)===============")
                }
            }
        }

        val repository = AssignToPPListRepoProvider.provideAssignPPListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToPPList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignToPPListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.assigned_to_pp_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                        if (assignPPList != null)
                                            AppDatabase.getDBInstance()?.ppListDao()?.delete()

                                        for (i in list.indices) {
                                            val assignToPP = AssignToPPEntity()
                                            assignToPP.pp_id = list[i].assigned_to_pp_id
                                            assignToPP.pp_name = list[i].assigned_to_pp_authorizer_name
                                            assignToPP.pp_phn_no = list[i].phn_no
                                            AppDatabase.getDBInstance()?.ppListDao()?.insert(assignToPP)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getAssignedDDListApi(shop_id, position, list_, isSyncFromList)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedDDListApi(shop_id, position, list_, isSyncFromList)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getAssignedDDListApi(shop_id, position, list_, isSyncFromList)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            getAssignedDDListApi(shop_id, position, list_, isSyncFromList)
                        })
        )
    }

    private fun getAssignedDDListApi(shop_id: String?, position: Int, list_: ArrayList<OrderDetailsListEntity>, isSyncFromList: Boolean) {
        val repository = AssignToDDListRepoProvider.provideAssignDDListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToDDList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignToDDListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.assigned_to_dd_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                                        if (assignDDList != null)
                                            AppDatabase.getDBInstance()?.ddListDao()?.delete()

                                        for (i in list.indices) {
                                            val assignToDD = AssignToDDEntity()
                                            assignToDD.dd_id = list[i].assigned_to_dd_id
                                            assignToDD.dd_name = list[i].assigned_to_dd_authorizer_name
                                            assignToDD.dd_phn_no = list[i].phn_no
                                            assignToDD.pp_id = list[i].assigned_to_pp_id
                                            assignToDD.type_id = list[i].type_id
                                            assignToDD.dd_latitude = list[i].dd_latitude
                                            assignToDD.dd_longitude = list[i].dd_longitude
                                            AppDatabase.getDBInstance()?.ddListDao()?.insert(assignToDD)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getAssignedToShopApi(shop_id, position, list_, isSyncFromList)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedToShopApi(shop_id, position, list_, isSyncFromList)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getAssignedToShopApi(shop_id, position, list_, isSyncFromList)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            getAssignedToShopApi(shop_id, position, list_, isSyncFromList)
                        })
        )
    }

    private fun getAssignedToShopApi(shop_id: String?, position: Int, list_: ArrayList<OrderDetailsListEntity>, isSyncFromList: Boolean) {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToShopList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignedToShopListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.shop_list

                                AppDatabase.getDBInstance()?.assignToShopDao()?.delete()

                                doAsync {
                                    list?.forEach {
                                        val shop = AssignToShopEntity()
                                        AppDatabase.getDBInstance()?.assignToShopDao()?.insert(shop.apply {
                                            assigned_to_shop_id = it.assigned_to_shop_id
                                            name = it.name
                                            phn_no = it.phn_no
                                            type_id = it.type_id
                                        })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        if (isSyncFromList) {
                                            if (!list_[position].isUploaded) {
                                                addOrderApi(list_[position].shop_id, list_[position].order_id, list_[position].amount!!, list_[position].description!!,
                                                        list_[position].collection!!, list_[position].date, list_[position].order_lat,
                                                        list_[position].order_long, list_[position].remarks, list_[position].signature, list_[position])
                                            } else {
                                                (mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                                initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)
                                            }
                                        } else {
                                            //syncAllOrder(list_[i], list_)
                                            refreshOrderList()
                                        }
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                if (isSyncFromList) {
                                    if (!list_[position].isUploaded) {
                                        addOrderApi(list_[position].shop_id, list_[position].order_id, list_[position].amount!!, list_[position].description!!,
                                                list_[position].collection!!, list_[position].date, list_[position].order_lat,
                                                list_[position].order_long, list_[position].remarks, list_[position].signature, list_[position])
                                    } else {
                                        (mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                        initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)
                                    }
                                } else {
                                    //syncAllOrder(list_[i], list_)
                                    refreshOrderList()
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (isSyncFromList) {
                                if (!list_[position].isUploaded) {
                                    addOrderApi(list_[position].shop_id, list_[position].order_id, list_[position].amount!!, list_[position].description!!,
                                            list_[position].collection!!, list_[position].date, list_[position].order_lat,
                                            list_[position].order_long, list_[position].remarks, list_[position].signature, list_[position])
                                } else {
                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                    initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>)
                                }
                            } else {
                                //syncAllOrder(list_[i], list_)
                                refreshOrderList()
                            }
                        })
        )
    }


    fun updateItem() {
        val list = AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as ArrayList<OrderDetailsListEntity>

        if (list != null && list.isNotEmpty()) {
            no_shop_tv.visibility = View.GONE
            initAdapter(list)
        } else
            no_shop_tv.visibility = View.VISIBLE
    }

    fun refreshOrderList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val orderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getUnsyncedData(false)
        val orderDetailsList = java.util.ArrayList<OrderDetailsListEntity>()
        val shopList = ArrayList<AddShopDBModelEntity>()

        if (orderList != null && orderList.isNotEmpty()) {

            for (i in orderList.indices) {
                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(orderList[i].shop_id)

                if (shop != null) {
                    if (shop.isUploaded)
                        orderDetailsList.add(orderList[i])
                    else
                        shopList.add(shop)
                }
            }

            if (shopList.size > 0) {
                i = 0
                syncAllShop(shopList[i], shopList, orderDetailsList)
            } else if (orderDetailsList.size > 0) {
                i = 0
                syncAllOrder(orderDetailsList[i], orderDetailsList)
            }


        } else
            getOrderList(false)

    }

    private fun syncAllShop(addShopDBModelEntity: AddShopDBModelEntity, shopList: ArrayList<AddShopDBModelEntity>, orderDetailsList: ArrayList<OrderDetailsListEntity>) {
        val addShopData = AddShopRequestData()
        val mAddShopDBModelEntity = addShopDBModelEntity
        addShopData.session_token = Pref.session_token
        addShopData.address = mAddShopDBModelEntity.address
        addShopData.owner_contact_no = mAddShopDBModelEntity.ownerContactNumber
        addShopData.owner_email = mAddShopDBModelEntity.ownerEmailId
        addShopData.owner_name = mAddShopDBModelEntity.ownerName
        addShopData.pin_code = mAddShopDBModelEntity.pinCode
        addShopData.shop_lat = mAddShopDBModelEntity.shopLat.toString()
        addShopData.shop_long = mAddShopDBModelEntity.shopLong.toString()
        addShopData.shop_name = mAddShopDBModelEntity.shopName.toString()
        addShopData.type = mAddShopDBModelEntity.type.toString()
        addShopData.shop_id = mAddShopDBModelEntity.shop_id
        addShopData.user_id = Pref.user_id
        addShopData.assigned_to_dd_id = mAddShopDBModelEntity.assigned_to_dd_id
        addShopData.assigned_to_pp_id = mAddShopDBModelEntity.assigned_to_pp_id
        addShopData.added_date = mAddShopDBModelEntity.added_date
        addShopData.amount = mAddShopDBModelEntity.amount
        addShopData.area_id = mAddShopDBModelEntity.area_id
        addShopData.model_id = mAddShopDBModelEntity.model_id
        addShopData.primary_app_id = mAddShopDBModelEntity.primary_app_id
        addShopData.secondary_app_id = mAddShopDBModelEntity.secondary_app_id
        addShopData.lead_id = mAddShopDBModelEntity.lead_id
        addShopData.stage_id = mAddShopDBModelEntity.stage_id
        addShopData.funnel_stage_id = mAddShopDBModelEntity.funnel_stage_id
        addShopData.booking_amount = mAddShopDBModelEntity.booking_amount
        addShopData.type_id = mAddShopDBModelEntity.type_id

        addShopData.director_name = mAddShopDBModelEntity.director_name
        addShopData.key_person_name = mAddShopDBModelEntity.person_name
        addShopData.phone_no = mAddShopDBModelEntity.person_no

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.family_member_dob))
            addShopData.family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.family_member_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.add_dob))
            addShopData.addtional_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.add_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.add_doa))
            addShopData.addtional_doa = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.add_doa)

        addShopData.specialization = mAddShopDBModelEntity.specialization
        addShopData.category = mAddShopDBModelEntity.category
        addShopData.doc_address = mAddShopDBModelEntity.doc_address
        addShopData.doc_pincode = mAddShopDBModelEntity.doc_pincode
        addShopData.is_chamber_same_headquarter = mAddShopDBModelEntity.chamber_status.toString()
        addShopData.is_chamber_same_headquarter_remarks = mAddShopDBModelEntity.remarks
        addShopData.chemist_name = mAddShopDBModelEntity.chemist_name
        addShopData.chemist_address = mAddShopDBModelEntity.chemist_address
        addShopData.chemist_pincode = mAddShopDBModelEntity.chemist_pincode
        addShopData.assistant_contact_no = mAddShopDBModelEntity.assistant_no
        addShopData.average_patient_per_day = mAddShopDBModelEntity.patient_count
        addShopData.assistant_name = mAddShopDBModelEntity.assistant_name

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.doc_family_dob))
            addShopData.doc_family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.doc_family_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_dob))
            addShopData.assistant_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_doa))
            addShopData.assistant_doa = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_doa)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_family_dob))
            addShopData.assistant_family_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_family_dob)

        addShopData.entity_id = mAddShopDBModelEntity.entity_id
        addShopData.party_status_id = mAddShopDBModelEntity.party_status_id
        addShopData.retailer_id = mAddShopDBModelEntity.retailer_id
        addShopData.dealer_id = mAddShopDBModelEntity.dealer_id
        addShopData.beat_id = mAddShopDBModelEntity.beat_id
        addShopData.assigned_to_shop_id = mAddShopDBModelEntity.assigned_to_shop_id
        addShopData.actual_address = mAddShopDBModelEntity.actual_address

        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id!!, false)
        addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!


        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name


        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate
        addShopData.purpose=mAddShopDBModelEntity.purpose

        addShopData.GSTN_Number=mAddShopDBModelEntity.gstN_Number
        addShopData.ShopOwner_PAN=mAddShopDBModelEntity.shopOwner_PAN

        callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shopList, orderDetailsList,
                mAddShopDBModelEntity.doc_degree)
    }

    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, shopList: ArrayList<AddShopDBModelEntity>,
                               list: ArrayList<OrderDetailsListEntity>, doc_degree: String?) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()


        XLog.d("==============SyncShop Input Params (Order List)====================")
        XLog.d("shop id=======> " + addShop.shop_id)
        val index = addShop.shop_id!!.indexOf("_")
        XLog.d("decoded shop id=======> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
        XLog.d("shop added date=======> " + addShop.added_date)
        XLog.d("shop address=======> " + addShop.address)
        XLog.d("assigned to dd id=======> " + addShop.assigned_to_dd_id)
        XLog.d("assigned to pp id=======> " + addShop.assigned_to_pp_id)
        XLog.d("date aniversery=======> " + addShop.date_aniversary)
        XLog.d("dob=======> " + addShop.dob)
        XLog.d("shop owner phn no=======> " + addShop.owner_contact_no)
        XLog.d("shop owner email=======> " + addShop.owner_email)
        XLog.d("shop owner name=======> " + addShop.owner_name)
        XLog.d("shop pincode=======> " + addShop.pin_code)
        XLog.d("session token=======> " + addShop.session_token)
        XLog.d("shop lat=======> " + addShop.shop_lat)
        XLog.d("shop long=======> " + addShop.shop_long)
        XLog.d("shop name=======> " + addShop.shop_name)
        XLog.d("shop type=======> " + addShop.type)
        XLog.d("user id=======> " + addShop.user_id)
        XLog.d("amount=======> " + addShop.amount)
        XLog.d("area id=======> " + addShop.area_id)
        XLog.d("model id=======> " + addShop.model_id)
        XLog.d("primary app id=======> " + addShop.primary_app_id)
        XLog.d("secondary app id=======> " + addShop.secondary_app_id)
        XLog.d("lead id=======> " + addShop.lead_id)
        XLog.d("stage id=======> " + addShop.stage_id)
        XLog.d("funnel stage id=======> " + addShop.funnel_stage_id)
        XLog.d("booking amount=======> " + addShop.booking_amount)
        XLog.d("type id=======> " + addShop.type_id)

        if (shop_imgPath != null)
            XLog.d("shop image path=======> $shop_imgPath")
        XLog.d("director name=======> " + addShop.director_name)
        XLog.d("family member dob=======> " + addShop.family_member_dob)
        XLog.d("key person's name=======> " + addShop.key_person_name)
        XLog.d("phone no=======> " + addShop.phone_no)
        XLog.d("additional dob=======> " + addShop.addtional_dob)
        XLog.d("additional doa=======> " + addShop.addtional_doa)
        XLog.d("family member dob=======> " + addShop.family_member_dob)
        XLog.d("key person's name=======> " + addShop.key_person_name)
        XLog.d("phone no=======> " + addShop.phone_no)
        XLog.d("additional dob=======> " + addShop.addtional_dob)
        XLog.d("additional doa=======> " + addShop.addtional_doa)
        XLog.d("doctor family member dob=======> " + addShop.doc_family_member_dob)
        XLog.d("specialization=======> " + addShop.specialization)
        XLog.d("average patient count per day=======> " + addShop.average_patient_per_day)
        XLog.d("category=======> " + addShop.category)
        XLog.d("doctor address=======> " + addShop.doc_address)
        XLog.d("doctor pincode=======> " + addShop.doc_pincode)
        XLog.d("chambers or hospital under same headquarter=======> " + addShop.is_chamber_same_headquarter)
        XLog.d("chamber related remarks=======> " + addShop.is_chamber_same_headquarter_remarks)
        XLog.d("chemist name=======> " + addShop.chemist_name)
        XLog.d("chemist name=======> " + addShop.chemist_address)
        XLog.d("chemist pincode=======> " + addShop.chemist_pincode)
        XLog.d("assistant name=======> " + addShop.assistant_name)
        XLog.d("assistant contact no=======> " + addShop.assistant_contact_no)
        XLog.d("assistant dob=======> " + addShop.assistant_dob)
        XLog.d("assistant date of anniversary=======> " + addShop.assistant_doa)
        XLog.d("assistant family dob=======> " + addShop.assistant_family_dob)
        XLog.d("entity id=======> " + addShop.entity_id)
        XLog.d("party status id=======> " + addShop.party_status_id)
        XLog.d("retailer id=======> " + addShop.retailer_id)
        XLog.d("dealer id=======> " + addShop.dealer_id)
        XLog.d("beat id=======> " + addShop.beat_id)
        XLog.d("assigned to shop id=======> " + addShop.assigned_to_shop_id)
        XLog.d("actual address=======> " + addShop.actual_address)

        if (doc_degree != null)
            XLog.d("doctor degree image path=======> $doc_degree")
        XLog.d("======================================================================")

        if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(doc_degree)) {
            val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShop(addShop)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                                i++
                                                if (i < shopList.size) {
                                                    syncAllShop(shopList[i], shopList, list)
                                                } else {
                                                    i = 0
                                                    progress_wheel.stopSpinning()
                                                    getAssignedPPListApi(addShop.shop_id, -1, list, false)
                                                }
                                            }
                                        }
                                    }


                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    //(mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {
                                                i++
                                                if (i < shopList.size) {
                                                    syncAllShop(shopList[i], shopList, list)
                                                } else {
                                                    i = 0
                                                    progress_wheel.stopSpinning()
                                                    getAssignedPPListApi(addShop.shop_id, -1, list, false)
                                                }
                                            }

                                        }
                                    }

                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                }


                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
        else {
            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShop, shop_imgPath, doc_degree, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                                i++
                                                if (i < shopList.size) {
                                                    syncAllShop(shopList[i], shopList, list)
                                                } else {
                                                    i = 0
                                                    progress_wheel.stopSpinning()
                                                    getAssignedPPListApi(addShop.shop_id, -1, list, false)
                                                }
                                            }
                                        }
                                    }


                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    //(mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {
                                                i++
                                                if (i < shopList.size) {
                                                    syncAllShop(shopList[i], shopList, list)
                                                } else {
                                                    i = 0
                                                    progress_wheel.stopSpinning()
                                                    getAssignedPPListApi(addShop.shop_id, -1, list, false)
                                                }
                                            }

                                        }
                                    }

                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                }


                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
    }


    private fun syncAllOrder(orderDetailsListEntity: OrderDetailsListEntity, orderList: ArrayList<OrderDetailsListEntity>) {


        val addOrder = AddOrderInputParamsModel()
        addOrder.collection = ""
        addOrder.description = ""
        addOrder.order_amount = orderDetailsListEntity.amount
        addOrder.order_date = orderDetailsListEntity.date
        addOrder.order_id = orderDetailsListEntity.order_id
        addOrder.shop_id = orderDetailsListEntity.shop_id
        addOrder.session_token = Pref.session_token
        addOrder.user_id = Pref.user_id
        addOrder.latitude = orderDetailsListEntity.order_lat
        addOrder.longitude = orderDetailsListEntity.order_long

        if (orderDetailsListEntity.scheme_amount != null)
            addOrder.scheme_amount = orderDetailsListEntity.scheme_amount
        else
            addOrder.scheme_amount = ""

        if (orderDetailsListEntity.remarks != null)
            addOrder.remarks = orderDetailsListEntity.remarks
        else
            addOrder.remarks = ""

        if (orderDetailsListEntity.patient_name != null)
            addOrder.patient_name = orderDetailsListEntity.patient_name
        else
            addOrder.patient_name = ""

        if (orderDetailsListEntity.patient_address != null)
            addOrder.patient_address = orderDetailsListEntity.patient_address
        else
            addOrder.patient_address = ""

        if (orderDetailsListEntity.patient_no != null)
            addOrder.patient_no = orderDetailsListEntity.patient_no
        else
            addOrder.patient_no = ""

        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(orderDetailsListEntity.shop_id!!)
        if (shopActivity != null) {
            if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(orderDetailsListEntity.shop_id)

                if (!TextUtils.isEmpty(shopDetail.address))
                    addOrder.address = shopDetail.address
                else
                    addOrder.address = ""
            } else {
                if (!TextUtils.isEmpty(orderDetailsListEntity.order_lat) && !TextUtils.isEmpty(orderDetailsListEntity.order_long))
                    addOrder.address = LocationWizard.getLocationName(mContext, orderDetailsListEntity.order_lat!!.toDouble(), orderDetailsListEntity.order_long!!.toDouble())
                else
                    addOrder.address = ""
            }
        } else {
            if (!TextUtils.isEmpty(orderDetailsListEntity.order_lat) && !TextUtils.isEmpty(orderDetailsListEntity.order_long))
                addOrder.address = LocationWizard.getLocationName(mContext, orderDetailsListEntity.order_lat!!.toDouble(), orderDetailsListEntity.order_long!!.toDouble())
            else
                addOrder.address = ""
        }

        /*06-01-2022*/
        if (orderDetailsListEntity.Hospital != null)
            addOrder.Hospital = orderDetailsListEntity.Hospital
        else
            addOrder.Hospital = ""

        if (orderDetailsListEntity.Email_Address != null)
            addOrder.Email_Address = orderDetailsListEntity.Email_Address
        else
            addOrder.Email_Address = ""

        val list = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToShopAndOrderId(orderDetailsListEntity.order_id!!, orderDetailsListEntity.shop_id!!)
        val productList = java.util.ArrayList<AddOrderInputProductList>()

        for (j in list.indices) {
            val product = AddOrderInputProductList()
            product.id = list[j].product_id
            product.qty = list[j].qty
            product.rate = list[j].rate
            product.total_price = list[j].total_price
            product.product_name = list[j].product_name
            product.scheme_qty = list[i].scheme_qty
            product.scheme_rate = list[i].scheme_rate
            product.total_scheme_price = list[i].total_scheme_price

            product.MRP = list[i].MRP
            productList.add(product)
        }

        addOrder.product_list = productList

        progress_wheel.spin()

        if (TextUtils.isEmpty(orderDetailsListEntity.remarks)) {
            val repository = AddOrderRepoProvider.provideAddOrderRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addNewOrder(addOrder)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderListResponse = result as BaseResponse
                                XLog.e("Add Order : \n" + ", SHOP ID===> " + orderList[i].shop_id + ", STATUS====> " + orderListResponse.status + ",RESPONSE MESSAGE:" + orderListResponse.message)
                                if (orderListResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, orderDetailsListEntity.order_id!!)

                                    i++
                                    if (i < orderList.size) {
                                        syncAllOrder(orderList[i], orderList)
                                    } else {
                                        i = 0
                                        progress_wheel.stopSpinning()
                                        getOrderList(false)
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(orderListResponse.message!!)
                                    /*i++
                                if (i < orderList.size) {
                                    syncAllOrder(orderList[i], orderList)
                                } else {
                                    progress_wheel.stopSpinning()
                                    i = 0
                                    getOrderList()
                                }*/
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
        else {
            val repository = AddOrderRepoProvider.provideAddOrderImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addNewOrder(addOrder, orderDetailsListEntity.remarks!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderListResponse = result as BaseResponse
                                XLog.e("Add Order : \n" + ", SHOP ID===> " + orderList[i].shop_id + ", STATUS====> " + orderListResponse.status + ",RESPONSE MESSAGE:" + orderListResponse.message)
                                if (orderListResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, orderDetailsListEntity.order_id!!)

                                    i++
                                    if (i < orderList.size) {
                                        syncAllOrder(orderList[i], orderList)
                                    } else {
                                        i = 0
                                        progress_wheel.stopSpinning()
                                        getOrderList(false)
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(orderListResponse.message!!)
                                    /*i++
                                if (i < orderList.size) {
                                    syncAllOrder(orderList[i], orderList)
                                } else {
                                    progress_wheel.stopSpinning()
                                    i = 0
                                    getOrderList()
                                }*/
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        collectionDialog?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setImage(file: File) {
        collectionDialog?.setImage(file)
    }

    /*private fun convertIntoWords(str: Double, language: String, Country: String): String? {
        val local = Locale(language, Country)
        val ruleBasedNumberFormat = RuleBasedNumberFormat(local, RuleBasedNumberFormat.SPELLOUT)
        return ruleBasedNumberFormat.format(str)
    }*/

}
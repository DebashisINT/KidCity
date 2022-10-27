package com.kcteam.features.newcollectionreport

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.CollectionDetailsEntity
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.billing.presentation.AddBillingFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.shopdetail.presentation.AddCollectionWithOrderDialog
import com.kcteam.features.shopdetail.presentation.api.addcollection.AddCollectionRepoProvider
import com.kcteam.features.shopdetail.presentation.model.addcollection.AddCollectionInputParamsModel
import com.kcteam.features.viewAllOrder.api.addorder.AddOrderRepoProvider
import com.kcteam.features.viewAllOrder.model.AddOrderInputParamsModel
import com.kcteam.features.viewAllOrder.model.AddOrderInputProductList
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList


class CollectionPendingDtlsFrag : BaseFragment(), View.OnClickListener {
    private lateinit var mContext: Context
    private lateinit var rv_CollectiondtlsList: RecyclerView
    private lateinit var adapter: CollectionPendingDtlsAdapter
    private var collectionDialog: AddCollectionWithOrderDialog?= null
    private lateinit var tv_noData: TextView
    private lateinit var progress_wheel: ProgressWheel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var mobj: PendingCollData? = null
        fun getInstance(objects: Any): CollectionPendingDtlsFrag {
            val collectionPendingDtlsFrag = CollectionPendingDtlsFrag()
            if (objects != null) {
                if (objects is PendingCollData)
                    this.mobj = objects
            }
            return collectionPendingDtlsFrag
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_collection_pending_dtls, container, false)
        initView(view)

        return view
    }

    private fun initView(view: View) {
        rv_CollectiondtlsList = view.findViewById(R.id.rv_frag_coll_pending_dtle_list)
        tv_noData = view.findViewById(R.id.tv_coll_pend_dtls_list_noData)


        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        getData()
    }

    private fun getData(){
        var objList: ArrayList<PendingCollDtlsData> = ArrayList()
        var orderList = AppDatabase.getDBInstance()?.orderDetailsListDao()?.getListAccordingToShopId(mobj!!.shop_id!!)
        var objPendingList: ArrayList<PendingCollDtlsData> = ArrayList()
        if (orderList != null && orderList.size > 0) {
            for (i in 0..orderList.size - 1) {
                var objPending: PendingCollDtlsData = PendingCollDtlsData("0", "0", "0", "0", "0", "0",
                    "0", "0", ArrayList<CollectionList>(), "","")
                objPending.shop_id = mobj!!.shop_id
                objPending.shop_name = mobj!!.shopName
                objPending.order_id = orderList.get(i).order_id.toString()
                objPending.order_date = orderList.get(i).only_date.toString()
                objPending.order_amt = orderList.get(i).amount.toString()

                var bDtlList = AppDatabase.getDBInstance()!!.billingDao().getDataOrderIdWise(objPending.order_id.toString())
                if(bDtlList!= null && bDtlList.size>0){
                    objPending.bill_id=bDtlList.get(0).bill_id
                }


                var invList = AppDatabase.getDBInstance()!!.billingDao().getDataOrderIdWise(orderList.get(i).order_id.toString()!!)
                if (invList != null && invList.size > 0) {
                    objPending.invoice_id = invList.get(0).invoice_no
                    objPending.invoice_date = invList.get(0).invoice_date
                    objPending.invoice_amt = invList.get(0).invoice_amount
                }

                var totalCollAmt=0.0
                var totalPendingAmt=objPending.order_amt

                var collList = AppDatabase.getDBInstance()?.collectionDetailsDao()?.getListOrderWise(orderList.get(i).order_id!!)
                if (collList != null && collList.size > 0) {
                    for (k in 0..collList.size - 1) {
                        var collectionObj: CollectionList = CollectionList("0", "0", "0")
                        collectionObj.coll_id = collList.get(k).collection_id.toString()
                        collectionObj.coll_amt = collList.get(k).collection.toString()
                        collectionObj.coll_date = collList.get(k).date.toString()
                        objPending.coll_list.add(collectionObj)
                        try{
                            totalCollAmt=totalCollAmt+collList.get(k).collection!!.toDouble()
                        }catch (ex:Exception){
                            totalCollAmt=totalCollAmt+0
                        }
                    }
                }

                totalPendingAmt=(objPending.order_amt.toDouble() - String.format("%.2f",totalCollAmt.toDouble()).toDouble()).toString()

                var totalInvAmt ="0"
                if(Pref.IsCollectionEntryConsiderOrderOrInvoice){
                    var ob=AppDatabase.getDBInstance()!!.billingDao().getInvoiceSumAmt(objPending.order_id.toString())
                    if(ob!=null)
                        totalInvAmt=(totalInvAmt.toDouble()+ob.toDouble()).toString()
                    else{
                        totalInvAmt="0"
                    }

                    totalPendingAmt=(totalInvAmt.toDouble()-totalCollAmt.toDouble()).toString()
                    if(totalPendingAmt.contains("-")){
                        totalPendingAmt="0"
                    }
                }
                totalPendingAmt=String.format("%.2f", totalPendingAmt.toDouble())

                objPending.pendingAmt=totalPendingAmt
                if(totalPendingAmt.equals("0.0") || totalPendingAmt.equals("0") || totalPendingAmt.equals("0.00")){

                }else{
                    objPendingList.add(objPending)
                }

            }
        }

        if (objPendingList.size > 0) {
            initAdapter(objPendingList)
            tv_noData.visibility = View.GONE
            rv_CollectiondtlsList.visibility = View.VISIBLE
        }
        else {
            tv_noData.visibility = View.VISIBLE
            rv_CollectiondtlsList.visibility = View.GONE
        }
    }

    private fun initAdapter(list: ArrayList<PendingCollDtlsData>) {
        CustomStatic.IsCollectionViewFromTeam = false
        adapter = CollectionPendingDtlsAdapter(mContext, list, object : PendingCollDtlsListner {
            override fun getInfoDtlsOnLick(obj: PendingCollDtlsData) {
                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else
                    openCollDialog(obj)
            }
        })

        rv_CollectiondtlsList.adapter = adapter
    }

    private fun openCollDialog(obj:PendingCollDtlsData){
        val mShop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(obj.shop_id)
        collectionDialog = AddCollectionWithOrderDialog.getInstance(obj, true, obj?.shop_name!!, AppUtils.getCurrentDateFormatInTa(obj.order_date!!),
            obj.order_amt!!, obj.order_id!!,obj.bill_id, object : AddCollectionWithOrderDialog.AddCollectionClickLisneter {
                override fun onClick(collection: String, date: String, paymentId: String, instrument: String, bank: String, filePath: String, feedback: String, patientName: String, patientAddress: String, patinetNo: String,
                                     hospital: String, emailAddress: String,order_id:String) {

                    if (mShop != null) {
                        doAsync {
                            val collectionDetails = CollectionDetailsEntity()
                            collectionDetails.collection = collection/*.substring(1)*/

                            val random = Random()
                            val m = random.nextInt(9999 - 1000) + 1000
                            collectionDetails.collection_id = Pref.user_id + "c" + m
                            collectionDetails.shop_id = obj.shop_id
                            collectionDetails.date = date //AppUtils.getCurrentDate()
                            collectionDetails.only_time = AppUtils.getCurrentTime()  //AppUtils.getCurrentDate()
                            collectionDetails.bill_id = ""
                            collectionDetails.order_id = obj.order_id
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
                                val order = AppDatabase.getDBInstance()!!.orderDetailsListDao().getSingleOrder(obj.order_id)
                                if (AppUtils.isOnline(mContext)) {
                                    if (mShop.isUploaded) {
                                        if (order.isUploaded) {
                                            addCollectionApi(collectionDetails.shop_id, collectionDetails.collection_id, "",
                                                "", collection, collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                        } else {
                                            syncOrderForCollection(order, collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
                                                collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                        }
                                    } else {
                                        (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                        /*syncShopForCollection(mShop, collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
                                            collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)*/
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
        collectionDialog?.show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionWithOrderDialog")
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

                            //initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as java.util.ArrayList<OrderDetailsListEntity>)
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

                            //initAdapter(AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll() as java.util.ArrayList<OrderDetailsListEntity>)
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

        val index = addShop.shop_id!!.indexOf("_")
        if (shop_imgPath != null)
            XLog.d("shop image path=======> $shop_imgPath")
        if (degree_imgPath != null)
            XLog.d("doctor degree image path=======> $degree_imgPath")

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

    private fun runLongTask(shop_id: String?): Any {
        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(shop_id!!, true, false)
        if (shopActivity != null){
            //callShopActivitySubmit(shop_id)
        }
        return true
    }

    private fun voiceCollectionMsg() {
        if (Pref.isVoiceEnabledForCollectionSaved) {
            val msg = "Hi, Collection saved successfully."
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Add Collection", "TTS error in converting Text to Speech!");

        }
        getData()
    }

    override fun onClick(p0: View?) {

    }
}
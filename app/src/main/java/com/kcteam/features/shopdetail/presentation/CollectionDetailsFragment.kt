package com.kcteam.features.shopdetail.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.CollectionDetailsEntity
import com.kcteam.app.domain.ViewAllOrderListEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.newcollection.model.NewCollectionListResponseModel
import com.kcteam.features.newcollection.newcollectionlistapi.NewCollectionListRepoProvider
import com.kcteam.features.shopdetail.presentation.api.addcollection.AddCollectionRepoProvider
import com.kcteam.features.shopdetail.presentation.model.addcollection.AddCollectionInputParamsModel
import com.kcteam.features.shopdetail.presentation.model.collectionlist.CollectionListDataModel
import com.kcteam.widgets.AppCustomTextView
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by Saikat on 26-10-2018.
 */
class CollectionDetailsFragment : BaseFragment(), View.OnClickListener {

    lateinit var ViewAllOrderListRecyclerViewAdapter: CollectionDetailsAdapter
    private lateinit var order_list_rv: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_address_TV: AppCustomTextView
    private lateinit var order_amount_tv: AppCustomTextView
    private lateinit var picker: HorizontalPicker
    private lateinit var ViewAllOrderListEntityList: ArrayList<ViewAllOrderListEntity>
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var add_order_tv: FloatingActionButton
    private lateinit var shop_detail_RL: RelativeLayout
    private lateinit var no_shop_tv: AppCustomTextView
    private lateinit var rl_view_all_order_main: RelativeLayout
    var i: Int = 0
    private var viewAllOrderList: ArrayList<CollectionDetailsEntity>? = null
    private lateinit var shop_IV: ImageView
    private var shopId = ""
    private var shopName = ""
    private lateinit var tv_contact_number: AppCustomTextView
    private lateinit var mContext: Context
    private lateinit var tv_view: AppCustomTextView

    companion object {
        var maddShopDataObj: AddShopDBModelEntity? = null

        fun getInstance(objects: Any): CollectionDetailsFragment {
            val mViewAllOrderListFragment = CollectionDetailsFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is AddShopDBModelEntity)
                    maddShopDataObj = objects
            }
            return mViewAllOrderListFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_view_all_order_list, container, false)
        initView(view)

        val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getAll()

        if (list == null || list.isEmpty())
            getCollectionListApi("")
        else
            setData()

        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    @SuppressLint("RestrictedApi")
    private fun initView(view: View) {
        add_order_tv = view.findViewById(R.id.add_order_tv)
        add_order_tv.visibility = View.GONE

        order_list_rv = view.findViewById(R.id.order_list_rv)
        ViewAllOrderListEntityList = ArrayList()
        myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
        myshop_address_TV = view.findViewById(R.id.myshop_address_TV)
        order_amount_tv = view.findViewById(R.id.order_amount_tv)
        no_shop_tv = view.findViewById(R.id.no_shop_tv)
        no_shop_tv.text = getString(R.string.no_collection)
        //order_amount_tv.text = "Total Order Amount : ₹10,000"
        shop_IV = view.findViewById(R.id.shop_IV)
        rl_view_all_order_main = view.findViewById(R.id.rl_view_all_order_main)
        tv_contact_number = view.findViewById(R.id.tv_contact_number)
        tv_view = view.findViewById(R.id.tv_view)
        tv_view.visibility = View.GONE
        shop_detail_RL = view.findViewById(R.id.shop_detail_RL)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        add_order_tv.setOnClickListener(this)
        shop_detail_RL.setOnClickListener(this)
        rl_view_all_order_main.setOnClickListener(null)
    }


    private fun setData() {
        try {
            //generateOrderListDate()

            if (maddShopDataObj != null) {
                viewAllOrderList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(maddShopDataObj?.shop_id!!) as ArrayList<CollectionDetailsEntity>
                shopId = maddShopDataObj?.shop_id!!
                shopName = maddShopDataObj?.shopName!!

                if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                    order_amount_tv.visibility = View.VISIBLE
                    var amount = 0.0
                    for (i in viewAllOrderList?.indices!!) {
                        if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.collection))
                            amount += viewAllOrderList?.get(i)?.collection?.toDouble()!!
                    }
                    val totalPrice = String.format("%.2f", amount.toFloat())
                    order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                } else
                    order_amount_tv.visibility = View.GONE

                //myshop_name_TV.text = maddShopDataObj?.shopName
                myshop_address_TV.text = maddShopDataObj?.address
                tv_contact_number.text = "Owner Contact Number: " + maddShopDataObj?.ownerContactNumber


            }

            myshop_name_TV.text = shopName

            val drawable = TextDrawable.builder()
                    .buildRoundRect(shopName.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

            shop_IV.setImageDrawable(drawable)


            if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                initAdapter(viewAllOrderList)
                no_shop_tv.visibility = View.GONE
            } else {
                /*if (AppUtils.isOnline(mContext)) {
                    getCollectionListApi(shopId)
                } else {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))*/
                no_shop_tv.visibility = View.VISIBLE
                //}
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCollectionListApi(shop_id: String?) {
        /*val repository = CollectionListRepoProvider.collectionListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.collectionList(Pref.session_token!!, Pref.user_id!!, shop_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val orderList = result as CollectionListResponseModel
                            if (orderList.status == NetworkConstant.SUCCESS) {
                                if (orderList.collection_details_list == null || orderList.collection_details_list?.size!! == 0) {
                                    progress_wheel.stopSpinning()
                                    no_shop_tv.visibility = View.VISIBLE
                                } else
                                    saveToDatabase(orderList.collection_details_list!!)

                            } else if (orderList.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                no_shop_tv.visibility = View.VISIBLE
                            } else if (orderList.status == NetworkConstant.SESSION_MISMATCH) {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(orderList.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )*/



        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            setData()
            return
        }

        val repository = NewCollectionListRepoProvider.newCollectionListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.collectionList(Pref.session_token!!, Pref.user_id!!, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val collection = result as NewCollectionListResponseModel
                            if (collection.status == NetworkConstant.SUCCESS) {
                                if (collection.collection_list == null || collection.collection_list?.size!! == 0) {
                                    progress_wheel.stopSpinning()
                                    //no_shop_tv.visibility = View.VISIBLE
                                    setData()
                                } else{}
                                    saveToDatabase(collection.collection_list!!)

                            } else if (collection.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                //no_shop_tv.visibility = View.VISIBLE
                                setData()
                            } else if (collection.status == NetworkConstant.SESSION_MISMATCH) {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(collection.message!!)
                                setData()
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            //no_shop_tv.visibility = View.VISIBLE
                            setData()
                        })
        )


    }

    //private fun saveToDatabase(collection_details_list: ArrayList<CollectionListDataModel>) {
    private fun saveToDatabase(collection_details_list: ArrayList<CollectionDetailsEntity>) {
        /*doAsync {

            for (i in collection_details_list.indices) {
                val collectionList = CollectionDetailsEntity()
                collectionList.date = AppUtils.convertToCommonFormat(collection_details_list[i].collection_date!!)
                collectionList.shop_id = shopId
                collectionList.isUploaded = true
                collectionList.collection_id = collection_details_list[i].collection_id
                collectionList.collection = collection_details_list[i].collection

                AppDatabase.getDBInstance()!!.collectionDetailsDao().insert(collectionList)
            }

            uiThread {
                progress_wheel.stopSpinning()
                viewAllOrderList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopId) as ArrayList<CollectionDetailsEntity>
                initAdapter(viewAllOrderList)

                try {
                    if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {

                        var amount = 0.0
                        for (i in viewAllOrderList?.indices!!) {
                            if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.collection))
                                amount += viewAllOrderList?.get(i)?.collection?.toDouble()!!
                        }

                        order_amount_tv.visibility = View.VISIBLE
                        val totalPrice = String.format("%.2f", amount.toFloat())
                        order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }*/



        doAsync {

    /*        for (i in collection_details_list.indices) {
                val collectionList = CollectionDetailsEntity()
                collectionList.date = AppUtils.convertDateTimeToCommonFormat(collection_details_list[i].collection_date!!) *//*AppUtils.convertToCommonFormat(collection_details_list[i].collection_date!!)*//*
                collectionList.shop_id = collection_details_list[i].shop_id
                collectionList.isUploaded = true
                collectionList.collection_id = collection_details_list[i].collection_id
                collectionList.collection = collection_details_list[i].collection
                collectionList.only_time = AppUtils.convertDateTimeToTime(collection_details_list[i].collection_date!!)
                collectionList.patient_no = collection_details_list[i].patient_no
                collectionList.patient_name = collection_details_list[i].patient_name
                collectionList.patient_address = collection_details_list[i].patient_address
                AppDatabase.getDBInstance()!!.collectionDetailsDao().insert(collectionList)
            }*/

            AppDatabase.getDBInstance()!!.collectionDetailsDao().insertAll(collection_details_list!!)

            uiThread {
                progress_wheel.stopSpinning()
                //val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getDateWiseCollection(AppUtils.getFormattedDate(myCalendar)) as ArrayList<CollectionDetailsEntity>

                /*viewAllOrderList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopId) as ArrayList<CollectionDetailsEntity>

                if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                    initAdapter(viewAllOrderList)
                    no_shop_tv.visibility = View.GONE
                } else {
                    no_shop_tv.visibility = View.VISIBLE
                }


                //initAdapter(viewAllOrderList)

                try {
                    if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {

                        var amount = 0.0
                        for (i in viewAllOrderList?.indices!!) {
                            if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.collection))
                                amount += viewAllOrderList?.get(i)?.collection?.toDouble()!!
                        }

                        order_amount_tv.visibility = View.VISIBLE
                        val totalPrice = String.format("%.2f", amount.toFloat())
                        order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }*/

                setData()

            }
        }
    }

    private fun generateOrderListDate() {
        for (i in 0..9) {
            var mViewAllOrderListEntity: ViewAllOrderListEntity = ViewAllOrderListEntity()
            mViewAllOrderListEntity.amount = "1000"
            mViewAllOrderListEntity.itemId = i
            mViewAllOrderListEntity.date = "03-Sep-18"
            ViewAllOrderListEntityList.add(mViewAllOrderListEntity)
        }
    }

    override fun onClick(p0: View?) {
        i = 0
        when (p0?.id) {
            R.id.add_order_tv -> {
                //AddOrderDialog()

//                try {
//
//                    if (!Pref.isAddAttendence)
//                        (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
//                    else {
//                        AddCollectionDialog.getInstance(maddShopDataObj, true, shopName, "", "", object : AddCollectionDialog.AddCollectionClickLisneter {
//                            override fun onClick(collection: String, date: String) {
//
//
//                                val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
//                                if (addShop != null) {
//
//                                    //if (addShop.isUploaded) {
//
//                                    doAsync {
//
//                                        val collectionDetails = CollectionDetailsEntity()
//                                        collectionDetails.collection = collection/*.substring(1)*/
//
//                                        val random = Random()
//                                        val m = random.nextInt(9999 - 1000) + 1000
//
//                                        //collectionDetails.collection_id = Pref.user_id + "_" + m /*+ "_" + System.currentTimeMillis().toString()*/
//                                        collectionDetails.collection_id = Pref.user_id + "c" + m
//                                        collectionDetails.shop_id = shopId
//                                        collectionDetails.date = date
//                                        collectionDetails.only_time = AppUtils.getCurrentTime()  //AppUtils.getCurrentDate()
//                                        AppDatabase.getDBInstance()!!.collectionDetailsDao().insert(collectionDetails)
//
//                                        val collectionDate = AppUtils.getCurrentDateForShopActi() + "T" + collectionDetails.only_time
//
//                                        uiThread {
//
//                                            if (AppUtils.isOnline(mContext)) {
//                                                if (addShop.isUploaded) {
//                                                    addCollectionApi(collectionDetails.shop_id, collectionDetails.collection_id, "",
//                                                            "", collection, collectionDate)
//                                                } else {
//                                                    syncShop(addShop, collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
//                                                            collectionDate)
//                                                }
//
//
//                                            } else {
//                                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
//
//                                                viewAllOrderList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopId) as ArrayList<CollectionDetailsEntity>
//
//                                                initAdapter(viewAllOrderList)
//                                                no_shop_tv.visibility = View.GONE
//
//                                                if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
//                                                    order_amount_tv.visibility = View.VISIBLE
//                                                    var amount = 0.0
//                                                    for (i in viewAllOrderList?.indices!!) {
//                                                        if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.collection))
//                                                            amount += viewAllOrderList?.get(i)?.collection?.toDouble()!!
//                                                    }
//
//                                                    val totalPrice = String.format("%.2f", amount.toFloat())
//                                                    order_amount_tv.text = "Total Collection: ₹ $totalPrice"
//                                                } else
//                                                    order_amount_tv.visibility = View.GONE
//                                            }
//                                        }
//                                    }
//                                }
//
//                            }
//                        }).show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionDialog")
//                    }
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
            }

            R.id.shop_detail_RL -> {
                //if (orderListObj != null)
                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, shopId)
            }
        }
    }

    private fun syncShop(addShop: AddShopDBModelEntity, shop_id: String?, order_id: String?, amount: String, desc: String, collection: String,
                         currentDateForShopActi: String) {
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


        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name


        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate

        addShopData.purpose=mAddShopDBModelEntity.purpose

        callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shop_id, order_id, amount, collection, currentDateForShopActi, desc, mAddShopDBModelEntity.doc_degree)
    }

    var isShopRegistrationInProcess = false
    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, shop_id: String?, order_id: String?, amount: String, collection: String,
                               currentDateForShopActi: String, desc: String, degree_imgPath: String?) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        if (isShopRegistrationInProcess)
            return

        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("==============================SyncShop Input Params(Collection)==============================")
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
                                                    addCollectionApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi)
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
                                                    addCollectionApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi)
                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)

                                        isShopRegistrationInProcess = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
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
                                                    addCollectionApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi)
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
                                                    addCollectionApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi)
                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)

                                        isShopRegistrationInProcess = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                isShopRegistrationInProcess = false
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

            shopDurationData.shop_revisit_uniqKey=shopActivity.shop_revisit_uniqKey!!


            /*10-12-2021*/
            shopDurationData.updated_by = Pref.user_id
            try {
                shopDurationData.updated_on = shopActivity.updated_on!!
            }catch (ex:Exception){
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

                shopDurationData.shop_revisit_uniqKey=shopActivity.shop_revisit_uniqKey!!


                /*10-12-2021*/
                shopDurationData.updated_by = Pref.user_id
                try{
                    shopDurationData.updated_on = shopActivity.updated_on!!
                }
                catch (ex:Exception){
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

    private fun addCollectionApi(shop_id: String?, order_id: String?, amount: String, desc: String, collection: String, date: String?) {
        val repository = AddCollectionRepoProvider.addCollectionRepository()

        val addCollection = AddCollectionInputParamsModel()
        addCollection.collection = collection
        addCollection.collection_date = date
        addCollection.collection_id = order_id
        addCollection.session_token = Pref.session_token
        addCollection.user_id = Pref.user_id
        addCollection.shop_id = shop_id

        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.addCollection(addCollection)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val orderList = result as BaseResponse
                            progress_wheel.stopSpinning()
                            if (orderList.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, order_id!!)
                            }

                            (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                            viewAllOrderList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopId) as ArrayList<CollectionDetailsEntity>

                            initAdapter(viewAllOrderList)
                            no_shop_tv.visibility = View.GONE

                            if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                                order_amount_tv.visibility = View.VISIBLE
                                var amount = 0.0
                                for (i in viewAllOrderList?.indices!!) {
                                    if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.collection))
                                        amount += viewAllOrderList?.get(i)?.collection?.toDouble()!!
                                }

                                val totalPrice = String.format("%.2f", amount.toFloat())
                                order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                            } else
                                order_amount_tv.visibility = View.GONE

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")

                            (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                            viewAllOrderList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopId) as ArrayList<CollectionDetailsEntity>

                            initAdapter(viewAllOrderList)
                            no_shop_tv.visibility = View.GONE

                            if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                                order_amount_tv.visibility = View.VISIBLE
                                var amount = 0.0
                                for (i in viewAllOrderList?.indices!!) {
                                    if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.collection))
                                        amount += viewAllOrderList?.get(i)?.collection?.toDouble()!!
                                }

                                val totalPrice = String.format("%.2f", amount.toFloat())
                                order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                            } else
                                order_amount_tv.visibility = View.GONE
                        })
        )
    }

    private fun initAdapter(viewAllOrderList: ArrayList<CollectionDetailsEntity>?) {

        ViewAllOrderListRecyclerViewAdapter = CollectionDetailsAdapter(mContext, viewAllOrderList /*ViewAllOrderListEntityList*/,
                object : CollectionDetailsAdapter.onScrollEndListener {
                    override fun onScrollEnd() {
                    }

                },
                object : CollectionDetailsAdapter.OnItemClickListener {
                    override fun onSyncClick(position: Int) {
                        if (AppUtils.isOnline(mContext)) {

                            val collectionDate = AppUtils.getCurrentDateFormatInTa(viewAllOrderList?.get(position)?.date!!) + "T" + viewAllOrderList?.get(position)?.only_time

                            val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
                            if (addShop.isUploaded)
                                syncAddCollectionApi(viewAllOrderList?.get(position)?.shop_id, viewAllOrderList?.get(position)?.collection_id, "",
                                        "", viewAllOrderList?.get(position)?.collection!!, collectionDate,
                                        viewAllOrderList[position].bill_id, viewAllOrderList[position].order_id, viewAllOrderList[position])
                            else {
                                syncShopFromSyncOption(addShop, viewAllOrderList?.get(position)?.shop_id, viewAllOrderList?.get(position)?.collection_id, "",
                                        "", viewAllOrderList?.get(position)?.collection!!/*.substring(1)*/, collectionDate,
                                        viewAllOrderList[position].bill_id, viewAllOrderList[position].order_id, viewAllOrderList[position])
                            }
                        } else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    }

                    override fun onViewClick(position: Int) {
                        AddCollectionDialog.getInstance(viewAllOrderList?.get(position), false, shopName, "", "", "", object : AddCollectionDialog.AddCollectionClickLisneter {
                            override fun onClick(collection: String, date: String, paymentId: String, instrument: String, bank: String, filePath: String, feedback: String, patientName: String, patientAddress: String, patinetNo: String
                            , hospital:String,emailAddress:String,order_id:String) {
                            }
                        }).show((mContext as DashboardActivity).supportFragmentManager, "AddOrderDialog")
                    }
                })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        order_list_rv.layoutManager = layoutManager
        order_list_rv.adapter = ViewAllOrderListRecyclerViewAdapter
    }


    private fun syncShopFromSyncOption(addShop: AddShopDBModelEntity, shop_id: String?, collection_id: String?, amount: String, desc: String, collection: String,
                                       currentDateForShopActi: String, billId: String?, orderId: String?, collectionDetailsEntity: CollectionDetailsEntity) {
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


        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name

        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate
        addShopData.purpose=mAddShopDBModelEntity.purpose

        callAddShopApiForSync(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shop_id, collection_id, amount, collection,
                currentDateForShopActi, desc, mAddShopDBModelEntity.doc_degree, billId, orderId, collectionDetailsEntity)
    }

    private fun callAddShopApiForSync(addShop: AddShopRequestData, shop_imgPath: String?, shop_id: String?, collection_id: String?, amount: String, collection: String,
                                      currentDateForShopActi: String, desc: String, degree_imgPath: String?, billId: String?, orderId: String?,
                                      collectionDetailsEntity: CollectionDetailsEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        if (isShopRegistrationInProcess)
            return


        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("================SyncShop Input Params(Collection)==================")
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
        XLog.d("=====================================================================")

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
                                                    syncAddCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetailsEntity)
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
                                                    syncAddCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetailsEntity)
                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)

                                        isShopRegistrationInProcess = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
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
                                                    syncAddCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetailsEntity)
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
                                                    syncAddCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetailsEntity)
                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)

                                        isShopRegistrationInProcess = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                isShopRegistrationInProcess = false
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }

    }

    private fun syncAddCollectionApi(shop_id: String?, collection_id: String?, amount: String, desc: String, collection: String,
                                     date: String, billId: String?, orderId: String?, collectionDetailsEntity: CollectionDetailsEntity) {

        val addCollection = AddCollectionInputParamsModel()
        addCollection.collection = collection
        addCollection.collection_date = /*AppUtils.getCurrentDateFormatInTa(*/date//)
        addCollection.collection_id = collection_id
        addCollection.session_token = Pref.session_token
        addCollection.user_id = Pref.user_id
        addCollection.shop_id = shop_id
        addCollection.bill_id = if (TextUtils.isEmpty(billId)) "" else billId!!
        addCollection.order_id = if (TextUtils.isEmpty(orderId)) "" else orderId!!
        addCollection.payment_id = if (TextUtils.isEmpty(collectionDetailsEntity.payment_id)) "" else collectionDetailsEntity.payment_id!!
        addCollection.instrument_no = if (TextUtils.isEmpty(collectionDetailsEntity.instrument_no)) "" else collectionDetailsEntity.instrument_no!!
        addCollection.bank = if (TextUtils.isEmpty(collectionDetailsEntity.bank)) "" else collectionDetailsEntity.bank!!
        addCollection.remarks = if (TextUtils.isEmpty(collectionDetailsEntity.feedback)) "" else collectionDetailsEntity.feedback!!
        addCollection.patient_name = if (TextUtils.isEmpty(collectionDetailsEntity.patient_name)) "" else collectionDetailsEntity.patient_name!!
        addCollection.patient_address = if (TextUtils.isEmpty(collectionDetailsEntity.patient_address)) "" else collectionDetailsEntity.patient_address!!
        addCollection.patient_no = if (TextUtils.isEmpty(collectionDetailsEntity.patient_no)) "" else collectionDetailsEntity.patient_no!!

        addCollection.Hospital = if (TextUtils.isEmpty(collectionDetailsEntity.Hospital)) "" else collectionDetailsEntity.Hospital!!
        addCollection.Email_Address = if (TextUtils.isEmpty(collectionDetailsEntity.Email_Address)) "" else collectionDetailsEntity.Email_Address!!

        progress_wheel.spin()

        if (TextUtils.isEmpty(collectionDetailsEntity.file_path)) {
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

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")

                                    viewAllOrderList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shop_id!!) as ArrayList<CollectionDetailsEntity>
                                    initAdapter(viewAllOrderList)
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                            })
            )
        }
        else {
            val repository = AddCollectionRepoProvider.addCollectionMultipartRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addCollection(addCollection, collectionDetailsEntity.file_path, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, collection_id!!)

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")

                                    viewAllOrderList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shop_id!!) as ArrayList<CollectionDetailsEntity>
                                    initAdapter(viewAllOrderList)
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                            })
            )
        }
    }

}
package com.kcteam.features.stock

import android.content.Context
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

import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.StockDetailsListEntity
import com.kcteam.app.domain.StockProductListEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.AppUtils.Companion.stockStatus
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.stock.StockAdapter
import com.kcteam.features.stock.api.StockRepositoryProvider
import com.kcteam.features.stock.model.AddStockInputParamsModel
import com.kcteam.features.stock.model.NewStockListResponseModel
import com.kcteam.features.viewAllOrder.model.AddOrderInputProductList
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 10-09-2019.
 */
class StockListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var shop_detail_RL: RelativeLayout
    private lateinit var add_order_tv: FloatingActionButton
    private lateinit var rl_view_all_order_main: RelativeLayout
    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var shop_IV: ImageView
    private lateinit var rv_stock_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var myshop_address_TV: AppCustomTextView
    private lateinit var order_amount_tv: AppCustomTextView
    private lateinit var tv_contact_number: AppCustomTextView
    private lateinit var no_shop_tv: AppCustomTextView

    private var stockList: ArrayList<StockDetailsListEntity>? = null
    private var isShopRegistrationInProcess = false;

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {

        var mAddShopDataObj: AddShopDBModelEntity? = null

        fun getInstance(objects: Any): StockListFragment {
            val stockFragment = StockListFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is AddShopDBModelEntity) {
                    mAddShopDataObj = objects
                }
            }
            return stockFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_stock_list, container, false)

        stockStatus = 1

        initView(view)
        initClickListener()

        val list = AppDatabase.getDBInstance()!!.stockDetailsListDao().getAll()

        if (list == null || list.isEmpty())
            getStockList()
        else
            setData()

        return view
    }

    private fun initView(view: View?) {
        add_order_tv = view?.findViewById(R.id.add_order_tv)!!
        shop_detail_RL = view.findViewById(R.id.shop_detail_RL)
        rl_view_all_order_main = view.findViewById(R.id.rl_view_all_order_main)
        myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
        shop_IV = view.findViewById(R.id.shop_IV)
        rv_stock_list = view.findViewById(R.id.rv_stock_list)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        myshop_address_TV = view.findViewById(R.id.myshop_address_TV)
        tv_contact_number = view.findViewById(R.id.tv_contact_number)
        order_amount_tv = view.findViewById(R.id.order_amount_tv)
        no_shop_tv = view.findViewById(R.id.no_shop_tv)
    }

    private fun initClickListener() {
        add_order_tv.setOnClickListener(this)
        shop_detail_RL.setOnClickListener(this)
        rl_view_all_order_main.setOnClickListener(null)
    }

    private fun getStockList() {
        val repository = StockRepositoryProvider.provideStockRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getStockList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as NewStockListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val stock_details_list = response.stock_list

                                if (stock_details_list != null && stock_details_list.isNotEmpty()) {

                                    doAsync {

                                        for (i in stock_details_list.indices) {
                                            val stockDetailsList = StockDetailsListEntity()
                                            stockDetailsList.date = stock_details_list[i].stock_date_time //AppUtils.convertToCommonFormat(order_details_list[i].date!!)
                                            if (!TextUtils.isEmpty(stock_details_list[i].stock_date_time))
                                                stockDetailsList.only_date = AppUtils.convertDateTimeToCommonFormat(stock_details_list[i].stock_date_time!!)
                                            stockDetailsList.shop_id = stock_details_list[i].shop_id

                                            if (!TextUtils.isEmpty(stock_details_list[i].stock_amount)) {
                                                val finalAmount = String.format("%.2f", stock_details_list[i].stock_amount?.toFloat())
                                                stockDetailsList.amount = finalAmount
                                            }

                                            stockDetailsList.isUploaded = true
                                            stockDetailsList.stock_id = stock_details_list[i].stock_id

                                            if (stock_details_list[i].product_list != null && stock_details_list[i].product_list?.size!! > 0) {
                                                for (j in stock_details_list[i].product_list?.indices!!) {
                                                    val stockProductList = StockProductListEntity()
                                                    stockProductList.brand = stock_details_list[i].product_list?.get(j)?.brand
                                                    stockProductList.brand_id = stock_details_list[i].product_list?.get(j)?.brand_id
                                                    stockProductList.category_id = stock_details_list[i].product_list?.get(j)?.category_id
                                                    stockProductList.watt = stock_details_list[i].product_list?.get(j)?.watt
                                                    stockProductList.watt_id = stock_details_list[i].product_list?.get(j)?.watt_id
                                                    stockProductList.product_id = stock_details_list[i].product_list?.get(j)?.id.toString()
                                                    stockProductList.category = stock_details_list[i].product_list?.get(j)?.category
                                                    stockProductList.stock_id = stock_details_list[i].stock_id
                                                    stockProductList.product_name = stock_details_list[i].product_list?.get(j)?.product_name

                                                    /*if (order_details_list[i].product_list?.get(j)?.rate?.contains(".")!!)
                                                        productOrderList.rate = order_details_list[i].product_list?.get(j)?.rate?.toDouble()?.toInt().toString()
                                                    else*/
                                                    if (!TextUtils.isEmpty(stock_details_list[i].product_list?.get(j)?.rate)) {
                                                        val finalRate = String.format("%.2f", stock_details_list[i].product_list?.get(j)?.rate?.toFloat())
                                                        stockProductList.rate = finalRate
                                                    }

                                                    stockProductList.qty = stock_details_list[i].product_list?.get(j)?.qty

                                                    /*if (order_details_list[i].product_list?.get(j)?.total_price?.contains(".")!!)
                                                        productOrderList.total_price = order_details_list[i].product_list?.get(j)?.total_price?.toDouble()?.toInt().toString()
                                                    else*/

                                                    if (!TextUtils.isEmpty(stock_details_list[i].product_list?.get(j)?.total_price)) {
                                                        val finalTotalPrice = String.format("%.2f", stock_details_list[i].product_list?.get(j)?.total_price?.toFloat())
                                                        stockProductList.total_price = finalTotalPrice
                                                    }
                                                    stockProductList.shop_id = stock_details_list[i].shop_id

                                                    AppDatabase.getDBInstance()!!.stockProductDao().insert(stockProductList)
                                                }
                                            }

                                            AppDatabase.getDBInstance()!!.stockDetailsListDao().insert(stockDetailsList)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            setData()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    setData()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                setData()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            setData()
                        })
        )
    }


    private fun setData() {

        if (mAddShopDataObj != null) {
            stockList = AppDatabase.getDBInstance()!!.stockDetailsListDao().getListAccordingToShopId(mAddShopDataObj?.shop_id!!) as ArrayList<StockDetailsListEntity>
            /*val shopId = ViewAllOrderListFragment.maddShopDataObj?.shop_id!!
            val shopName = ViewAllOrderListFragment.maddShopDataObj?.shopName!!*/

            if (stockList != null && stockList?.size!! > 0) {
                //order_amount_tv.visibility = View.VISIBLE
                no_shop_tv.visibility = View.GONE
                val amount = stockList?.indices!!
                        .filterNot { TextUtils.isEmpty(stockList?.get(it)?.amount) }
                        .sumByDouble { stockList?.get(it)?.amount?.toDouble()!! }
                /*for (i in stockList?.indices!!) {
                    if (!TextUtils.isEmpty(stockList?.get(i)?.amount))
                        amount += stockList?.get(i)?.amount?.toDouble()!!
                }*/

                val finalAmount = String.format("%.2f", amount.toFloat())
                if (finalAmount > "0.00") {
                    order_amount_tv.visibility = View.VISIBLE
                    order_amount_tv.text = "Total Stock Amount: ₹ $finalAmount"
                }
            } else {
                order_amount_tv.visibility = View.GONE
                no_shop_tv.visibility = View.VISIBLE
            }

            myshop_address_TV.text = mAddShopDataObj?.address
            tv_contact_number.text = "Owner Contact Number: " + mAddShopDataObj?.ownerContactNumber
        }


        myshop_name_TV.text = mAddShopDataObj?.shopName

        val drawable = TextDrawable.builder()
                .buildRoundRect(mAddShopDataObj?.shopName?.toUpperCase()?.trim()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

        shop_IV.setImageDrawable(drawable)

        myshop_address_TV.text = mAddShopDataObj?.address

        if (stockList != null && stockList?.size!! > 0)
            initAdapter(stockList)
    }

    private fun initAdapter(stockList: ArrayList<StockDetailsListEntity>?) {

        val stockAdapter = StockAdapter(mContext, stockList, object : StockAdapter.onScrollEndListener {
            override fun onScrollEnd() {
            }

        }, object : StockAdapter.OnItemClickListener {
            override fun onSyncClick(position: Int) {
                if (AppUtils.isOnline(mContext)) {

                    val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(mAddShopDataObj?.shop_id!!)
                    if (addShop.isUploaded)
                        syncAddStockApi(stockList?.get(position)?.shop_id, stockList?.get(position)?.stock_id, stockList?.get(position)?.amount!!,
                                stockList[position].date!!, stockList[position].stock_lat, stockList[position].stock_long)
                    else {
                        syncShopFromSyncOption(addShop, stockList?.get(position)?.shop_id, stockList?.get(position)?.stock_id, stockList?.get(position)?.amount!!/*.substring(1)*/,
                                stockList[position].date!!, stockList[position].stock_lat, stockList[position].stock_long)
                    }
                } else
                    (mContext as DashboardActivity).showSnackMessage("Please check your internet connection")
            }

            override fun onViewClick(position: Int) {
                /*AddOrderDialog.getInstance(viewAllOrderList?.get(position), false, shopName, object : AddOrderDialog.AddOrderClickLisneter {
                    override fun onUpdateClick(amount: String, desc: String, collection: String) {
                        (mContext as DashboardActivity).loadFragment(FragType.ViewCartFragment, true, viewAllOrderList?.get(position)*//*?.shop_id*//*!!)
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "AddOrderDialog")*/
                (mContext as DashboardActivity).loadFragment(FragType.StockDetailsFragment, true, stockList?.get(position)/*?.shop_id*/!!)
            }
        })

        val layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        rv_stock_list.layoutManager = layoutManager
        rv_stock_list.adapter = stockAdapter
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.add_order_tv -> {
                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else
                    (mContext as DashboardActivity).loadFragment(FragType.OrderTypeListFragment, true, mAddShopDataObj?.shop_id!!)
            }

            R.id.shop_detail_RL -> {
                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, mAddShopDataObj?.shop_id!!)
            }

        }
    }

    private fun syncAddStockApi(shop_id: String?, stock_id: String?, amount: String, date: String, stock_lat: String?, stock_long: String?) {

        val addStock = AddStockInputParamsModel()
        addStock.stock_amount = amount
        addStock.stock_date_time = date //AppUtils.getCurrentDateFormatInTa(date)
        addStock.stock_id = stock_id
        addStock.shop_id = shop_id
        addStock.session_token = Pref.session_token
        addStock.user_id = Pref.user_id
        addStock.latitude = stock_lat
        addStock.longitude = stock_long

        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shop_id!!)

        if (shopActivity != null) {
            if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)

                if (!TextUtils.isEmpty(shopDetail.address))
                    addStock.address = shopDetail.address
                else
                    addStock.address = ""
            } else {
                if (!TextUtils.isEmpty(stock_lat) && !TextUtils.isEmpty(stock_long))
                    addStock.address = LocationWizard.getLocationName(mContext, stock_lat!!.toDouble(), stock_long!!.toDouble())
                else
                    addStock.address = ""
            }
        } else {
            if (!TextUtils.isEmpty(stock_lat) && !TextUtils.isEmpty(stock_long))
                addStock.address = LocationWizard.getLocationName(mContext, stock_lat!!.toDouble(), stock_long!!.toDouble())
            else
                addStock.address = ""
        }

        val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)
        addStock.shop_type = addShop?.type

        val list = AppDatabase.getDBInstance()!!.stockProductDao().getDataAccordingToShopAndStockId(stock_id!!, shop_id!!)
        val productList = java.util.ArrayList<AddOrderInputProductList>()

        for (i in list.indices) {
            val product = AddOrderInputProductList()
            product.id = list[i].product_id
            product.qty = list[i].qty
            product.rate = list[i].rate
            product.total_price = list[i].total_price
            product.product_name = list[i].product_name
            productList.add(product)
        }

        addStock.product_list = productList


        val repository = StockRepositoryProvider.provideStockRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.addStock(addStock)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val orderList = result as BaseResponse
                            progress_wheel.stopSpinning()
                            if (orderList.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.stockDetailsListDao().updateIsUploaded(true, stock_id)

                                (mContext as DashboardActivity).showSnackMessage("Synced successfully")

                                stockList = AppDatabase.getDBInstance()!!.stockDetailsListDao().getListAccordingToShopId(shop_id) as java.util.ArrayList<StockDetailsListEntity>
                                initAdapter(stockList)
                            } else
                                (mContext as DashboardActivity).showSnackMessage(orderList.message!!)

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_stock))
                        })
        )
    }

    private fun syncShopFromSyncOption(addShop: AddShopDBModelEntity, shop_id: String?, stock_id: String?, amount: String, currentDateForShopActi: String,
                                       stock_lat: String?, stock_long: String?) {
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

        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id!!,false)
        addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!


        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name


        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate

        callAddShopApiForSync(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shop_id, stock_id, amount,
                currentDateForShopActi, stock_lat, stock_long, mAddShopDBModelEntity.doc_degree)
        //callAddShopApi(addShopData, "")
    }

    private fun callAddShopApiForSync(addShop: AddShopRequestData, shop_imgPath: String?, shop_id: String?, stock_id: String?, amount: String,
                                      currentDateForShopActi: String, stock_lat: String?, stock_long: String?, degree_imgPath: String?) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        if (isShopRegistrationInProcess)
            return

        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("==========SyncShop Input Params (Stock)=====================")
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
        XLog.d("==============================================================")

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
                                                    syncAddStockApi(shop_id, stock_id, amount, currentDateForShopActi, stock_lat, stock_long)
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
                                                    syncAddStockApi(shop_id, stock_id, amount, currentDateForShopActi, stock_lat, stock_long)
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
                                                    syncAddStockApi(shop_id, stock_id, amount, currentDateForShopActi, stock_lat, stock_long)
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
                                                    syncAddStockApi(shop_id, stock_id, amount, currentDateForShopActi, stock_lat, stock_long)
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
            }catch(ex:Exception){
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


    fun updateList() {
        stockList = AppDatabase.getDBInstance()!!.stockDetailsListDao().getListAccordingToShopId(mAddShopDataObj?.shop_id!!) as ArrayList<StockDetailsListEntity>


        if (stockList != null && stockList?.size!! > 0) {
            initAdapter(stockList)
            no_shop_tv.visibility = View.GONE
        } else {
            no_shop_tv.visibility = View.VISIBLE
        }

        /*initAdapter(viewAllOrderList)
        no_shop_tv.visibility = View.GONE*/

        try {
            if (stockList != null && stockList?.size!! > 0) {
                var amount = 0.0
                for (i in stockList?.indices!!) {
                    if (!TextUtils.isEmpty(stockList?.get(i)?.amount))
                        amount += stockList?.get(i)?.amount?.toDouble()!!
                }
                val finalAmount = String.format("%.2f", amount.toFloat())
                if (finalAmount > "0.00") {
                    order_amount_tv.visibility = View.VISIBLE
                    order_amount_tv.text = "Total Stock Amount: ₹ $finalAmount"
                } else
                    order_amount_tv.visibility = View.GONE
            } else
                order_amount_tv.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
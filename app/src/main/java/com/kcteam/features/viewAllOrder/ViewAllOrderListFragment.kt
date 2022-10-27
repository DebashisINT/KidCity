package com.kcteam.features.viewAllOrder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.elvishew.xlog.XLog
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
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
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.orderList.api.neworderlistapi.NewOrderListRepoProvider
import com.kcteam.features.orderList.model.NewOrderListResponseModel
import com.kcteam.features.shopdetail.presentation.AddCollectionDialog
import com.kcteam.features.shopdetail.presentation.api.addcollection.AddCollectionRepoProvider
import com.kcteam.features.shopdetail.presentation.model.addcollection.AddCollectionInputParamsModel
import com.kcteam.features.viewAllOrder.api.OrderDetailsListRepoProvider
import com.kcteam.features.viewAllOrder.api.addorder.AddOrderRepoProvider
import com.kcteam.features.viewAllOrder.model.AddOrderInputParamsModel
import com.kcteam.features.viewAllOrder.model.AddOrderInputProductList
import com.kcteam.features.viewAllOrder.model.ViewAllOrderListDataModel
import com.kcteam.features.viewAllOrder.model.ViewAllOrderListResponseModel
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.util.*


/**
 * Created by Pratishruti on 15-11-2017.
 */
class ViewAllOrderListFragment : BaseFragment(), View.OnClickListener {

    lateinit var ViewAllOrderListRecyclerViewAdapter: ViewAllOrderListRecyclerViewAdapter
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
    private lateinit var tv_contact_number: AppCustomTextView

    var i: Int = 0
    private var viewAllOrderList: ArrayList<OrderDetailsListEntity>? = null
    private lateinit var shop_IV: ImageView
    private var shopId = ""
    private var shopName = ""
    private var collectionDialog: AddCollectionDialog?= null

    private lateinit var mContext: Context

    companion object {
        var mShopActivityEntity: ShopActivityEntity? = null
        var maddShopDataObj: AddShopDBModelEntity? = null
        var orderListObj: OrderListEntity? = null
        var mSShopID_Str:String? = null
        fun getInstance(objects: Any): ViewAllOrderListFragment {
            val mViewAllOrderListFragment = ViewAllOrderListFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is ShopActivityEntity) {
                    mShopActivityEntity = objects
                    maddShopDataObj = null
                    orderListObj = null
                    mSShopID_Str=mShopActivityEntity?.shopid!!
                } else if (objects is AddShopDBModelEntity) {
                    maddShopDataObj = objects
                    orderListObj = null
                    mShopActivityEntity = null
                    mSShopID_Str=maddShopDataObj?.shop_id
                } else if (objects is OrderListEntity) {
                    orderListObj = objects
                    mShopActivityEntity = null
                    maddShopDataObj = null
                    mSShopID_Str=orderListObj?.shop_id!!
                }
            }
            return mViewAllOrderListFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_view_all_order_list, container, false)
        initView(view)

        AppUtils.stockStatus = 0

        val list = AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll()

        if (list == null || list.isEmpty())
            getOrderList()
        else
            setData()

        return view
    }

    private fun initView(view: View) {
        add_order_tv = view.findViewById(R.id.add_order_tv)
        order_list_rv = view.findViewById(R.id.order_list_rv)
        ViewAllOrderListEntityList = ArrayList()
        myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
        myshop_address_TV = view.findViewById(R.id.myshop_address_TV)
        order_amount_tv = view.findViewById(R.id.order_amount_tv)
        no_shop_tv = view.findViewById(R.id.no_shop_tv)
        //order_amount_tv.text = "Total Order Amount : ₹10,000"
        shop_IV = view.findViewById(R.id.shop_IV)
        rl_view_all_order_main = view.findViewById(R.id.rl_view_all_order_main)
        tv_contact_number = view.findViewById(R.id.tv_contact_number)

        shop_detail_RL = view.findViewById(R.id.shop_detail_RL)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()


        add_order_tv.setOnClickListener(this)
        shop_detail_RL.setOnClickListener(this)
        rl_view_all_order_main.setOnClickListener(null)
    }


    private fun getOrderList() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            setData()
            return
        }

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
                                            }
                                            else {
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
                                            //no_shop_tv.visibility = View.GONE

                                            setData()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    //no_shop_tv.visibility = View.VISIBLE
                                    setData()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                //no_shop_tv.visibility = View.VISIBLE
                                setData()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            //no_shop_tv.visibility = View.VISIBLE
                            setData()
                        })
        )
    }


    private fun setData() {
        try {
            //generateOrderListDate()

            if (mShopActivityEntity != null) {
                viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(mShopActivityEntity?.shopid!!) as ArrayList<OrderDetailsListEntity>
                shopId = mShopActivityEntity?.shopid!!
                shopName = mShopActivityEntity?.shop_name!!

                if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {

                    var amount = 0.0
                    for (i in viewAllOrderList?.indices!!) {
                        if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.amount))
                            amount += viewAllOrderList?.get(i)?.amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())

                    val builder = SpannableStringBuilder()

                    val str1 = SpannableString("Total Order Amount: ")
                    builder.append(str1)

                    val str2 = SpannableString("₹ $finalAmount")
                    str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                    builder.append(str2)

                    order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
                    //tv_contact_number.text = "Owner Contact Number : "+mShopActivityEntity?.
                }

                //myshop_name_TV.text = mShopActivityEntity?.shop_name
                myshop_address_TV.text = mShopActivityEntity?.shop_address

                /*val drawable = TextDrawable.builder()
                        .buildRoundRect(mShopActivityEntity?.shop_name?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

                shop_IV.setImageDrawable(drawable)*/

            }
            else if (maddShopDataObj != null) {
                viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(maddShopDataObj?.shop_id!!) as ArrayList<OrderDetailsListEntity>
                shopId = maddShopDataObj?.shop_id!!
                shopName = maddShopDataObj?.shopName!!

                if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                    order_amount_tv.visibility = View.VISIBLE
                    var amount = 0.0
                    for (i in viewAllOrderList?.indices!!) {
                        if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.amount))
                            amount += viewAllOrderList?.get(i)?.amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())

                    val builder = SpannableStringBuilder()

                    val str1 = SpannableString("Total Order Amount: ")
                    builder.append(str1)

                    val str2 = SpannableString("₹ $finalAmount")
                    str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                    builder.append(str2)

                    order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
                } else
                    order_amount_tv.visibility = View.GONE

                //myshop_name_TV.text = maddShopDataObj?.shopName
                myshop_address_TV.text = maddShopDataObj?.address

                val builder = SpannableStringBuilder()

                val str1 = SpannableString("Owner Contact Number: ")
                builder.append(str1)

                val str2 = SpannableString(maddShopDataObj?.ownerContactNumber)
                str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                builder.append(str2)

                tv_contact_number.setText(builder, TextView.BufferType.SPANNABLE)

                /*val drawable = TextDrawable.builder()
                        .buildRoundRect(maddShopDataObj?.shopName?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

                shop_IV.setImageDrawable(drawable)*/

            }
            else {
                viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(orderListObj?.shop_id!!) as ArrayList<OrderDetailsListEntity>
                shopId = orderListObj?.shop_id!!
                shopName = orderListObj?.shop_name!!


                /*if (!TextUtils.isEmpty(orderListObj?.order_amount)) {
                    order_amount_tv.visibility = View.VISIBLE
                    order_amount_tv.text = "Total Order Amount : ₹ " + orderListObj?.order_amount
                } else
                    order_amount_tv.visibility = View.GONE*/

                //myshop_name_TV.text = orderListObj?.shop_name
                myshop_address_TV.text = orderListObj?.address

                val builder = SpannableStringBuilder()

                val str1 = SpannableString("Owner Contact Number: ")
                builder.append(str1)

                val str2 = SpannableString(orderListObj?.owner_contact_no)
                str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                builder.append(str2)

                tv_contact_number.setText(builder, TextView.BufferType.SPANNABLE)

                /*val drawable = TextDrawable.builder()
                        .buildRoundRect(orderListObj?.shop_name?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

                shop_IV.setImageDrawable(drawable)*/


                if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                    var amount = 0.0
                    for (i in viewAllOrderList?.indices!!) {
                        if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.amount))
                            amount += viewAllOrderList?.get(i)?.amount?.toDouble()!!
                    }

                    order_amount_tv.visibility = View.VISIBLE
                    val finalAmount = String.format("%.2f", amount.toFloat())

                    val builder_ = SpannableStringBuilder()

                    val str1_ = SpannableString("Total Order Amount: ")
                    builder_.append(str1_)

                    val str2_ = SpannableString("₹ $finalAmount")
                    str2_.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2_.length, 0)
                    builder_.append(str2_)

                    order_amount_tv.setText(builder_, TextView.BufferType.SPANNABLE)
                } else
                    order_amount_tv.visibility = View.GONE
            }

            myshop_name_TV.text = shopName

            val drawable = TextDrawable.builder()
                    .buildRoundRect(shopName.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

            shop_IV.setImageDrawable(drawable)


            if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                initAdapter(viewAllOrderList)
                no_shop_tv.visibility = View.GONE
            } else {
                no_shop_tv.visibility = View.VISIBLE
                /*if (AppUtils.isOnline(mContext)) {
                    //if (orderListObj != null)
                    getOrderDetailsListApi(shopId, "" *//*orderListObj?.order_id*//*)
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))*/
            }


            /*if (mShopActivityEntity != null) {
                myshop_name_TV.text = mShopActivityEntity?.shop_name
                myshop_address_TV.text = mShopActivityEntity?.shop_address

                val drawable = TextDrawable.builder()
                        .buildRoundRect(mShopActivityEntity?.shop_name?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

                shop_IV.setImageDrawable(drawable)

            } else if (maddShopDataObj != null) {
                myshop_name_TV.text = maddShopDataObj?.shopName
                myshop_address_TV.text = maddShopDataObj?.address

                val drawable = TextDrawable.builder()
                        .buildRoundRect(maddShopDataObj?.shopName?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

                shop_IV.setImageDrawable(drawable)

            } else if (orderListObj != null) {
                myshop_name_TV.text = orderListObj?.shop_name
                myshop_address_TV.text = orderListObj?.address


                val drawable = TextDrawable.builder()
                        .buildRoundRect(orderListObj?.shop_name?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

                shop_IV.setImageDrawable(drawable)
            }*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getOrderDetailsListApi(shop_id: String?, order_id: String?) {
        val repository = OrderDetailsListRepoProvider.provideOrderDetailsListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getOrderDetailsList(Pref.session_token!!, Pref.user_id!!, shop_id!!, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val orderList = result as ViewAllOrderListResponseModel
                            if (orderList.status == NetworkConstant.SUCCESS) {
                                if (orderList.order_details_list == null || orderList.order_details_list?.size!! == 0) {
                                    progress_wheel.stopSpinning()
                                    no_shop_tv.visibility = View.VISIBLE
                                } else
                                    saveToDatabase(orderList.order_details_list!!)

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
        )
    }

    private fun saveToDatabase(order_details_list: List<ViewAllOrderListDataModel>) {
        doAsync {

            for (i in order_details_list.indices) {
                val orderDetailList = OrderDetailsListEntity()
                orderDetailList.date = order_details_list[i].date //AppUtils.convertToCommonFormat(order_details_list[i].date!!)
                orderDetailList.only_date = AppUtils.convertDateTimeToCommonFormat(order_details_list[i].date!!)
                orderDetailList.shop_id = shopId
                orderDetailList.description = order_details_list[i].description
                /*if (order_details_list[i].amount?.contains(".")!!)
                    orderDetailList.amount = order_details_list[i].amount?.toDouble()?.toInt().toString()
                else
                    orderDetailList.amount = order_details_list[i].amount*/
                val finalAmount = String.format("%.2f", order_details_list[i].amount?.toFloat())
                orderDetailList.amount = finalAmount

                orderDetailList.isUploaded = true
                orderDetailList.order_id = order_details_list[i].id
                orderDetailList.collection = order_details_list[i].collection

                if (order_details_list[i].product_list != null && order_details_list[i].product_list?.size!! > 0) {
                    for (j in order_details_list[i].product_list?.indices!!) {
                        val productOrderList = OrderProductListEntity()
                        productOrderList.brand = order_details_list[i].product_list?.get(j)?.brand
                        productOrderList.brand_id = order_details_list[i].product_list?.get(j)?.brand_id
                        productOrderList.category_id = order_details_list[i].product_list?.get(j)?.category_id
                        productOrderList.watt = order_details_list[i].product_list?.get(j)?.watt
                        productOrderList.watt_id = order_details_list[i].product_list?.get(j)?.watt_id
                        productOrderList.product_id = order_details_list[i].product_list?.get(j)?.id.toString()
                        productOrderList.category = order_details_list[i].product_list?.get(j)?.category
                        productOrderList.order_id = order_details_list[i].id
                        productOrderList.product_name = order_details_list[i].product_list?.get(j)?.product_name

                        /*if (order_details_list[i].product_list?.get(j)?.rate?.contains(".")!!)
                            productOrderList.rate = order_details_list[i].product_list?.get(j)?.rate?.toDouble()?.toInt().toString()
                        else*/
                        productOrderList.rate = order_details_list[i].product_list?.get(j)?.rate

                        productOrderList.qty = order_details_list[i].product_list?.get(j)?.qty

                        /*if (order_details_list[i].product_list?.get(j)?.total_price?.contains(".")!!)
                            productOrderList.total_price = order_details_list[i].product_list?.get(j)?.total_price?.toDouble()?.toInt().toString()
                        else*/
                        productOrderList.total_price = order_details_list[i].product_list?.get(j)?.total_price
                        productOrderList.shop_id = shopId

                        AppDatabase.getDBInstance()!!.orderProductListDao().insert(productOrderList)
                    }
                }

                AppDatabase.getDBInstance()!!.orderDetailsListDao().insert(orderDetailList)
            }

            uiThread {
                progress_wheel.stopSpinning()
                viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopId) as ArrayList<OrderDetailsListEntity>
                initAdapter(viewAllOrderList)

                try {
                    if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {

                        var amount = 0.0
                        for (i in viewAllOrderList?.indices!!) {
                            if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.amount))
                                amount += viewAllOrderList?.get(i)?.amount?.toDouble()!!
                        }

                        order_amount_tv.visibility = View.VISIBLE
                        val finalAmount = String.format("%.2f", amount.toFloat())

                        val builder = SpannableStringBuilder()

                        val str1 = SpannableString("Total Order Amount: ")
                        builder.append(str1)

                        val str2 = SpannableString("₹ $finalAmount")
                        str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                        builder.append(str2)

                        order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

                try {

                    /*AddOrderDialog.getInstance(mShopActivityEntity, true, shopName, object : AddOrderDialog.AddOrderClickLisneter {
                        override fun onUpdateClick(amount: String, desc: String, collection: String) {

                            val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
                            if (addShop != null) {

                                //if (addShop.isUploaded) {

                                doAsync {

                                    val orderListDetails = OrderDetailsListEntity()
                                    orderListDetails.amount = amount//.substring(1)
                                    orderListDetails.description = desc
                                    orderListDetails.collection = collection//.substring(1)

                                    val random = Random()
                                    val m = random.nextInt(9999 - 1000) + 1000

//                                    if (maddShopDataObj != null) {
//                                orderListDetails.order_id = Pref.user_id + "_" + m + "_" + System.currentTimeMillis().toString()
//                                orderListDetails.shop_id = maddShopDataObj?.shop_id
//                            } else if (mShopActivityEntity != null) {
//                                orderListDetails.order_id = Pref.user_id + "_" + m + "_" + System.currentTimeMillis().toString()
//                                orderListDetails.shop_id = mShopActivityEntity?.shopid
//                            } else {
//                                orderListDetails.order_id = Pref.user_id + "_" + m + "_" + System.currentTimeMillis().toString()
//                                orderListDetails.shop_id = orderListObj?.shop_id
//                            }


                                    orderListDetails.order_id = Pref.user_id + "_" + m + "_" + System.currentTimeMillis().toString()
                                    orderListDetails.shop_id = shopId
                                    orderListDetails.date = AppUtils.getCurrentDate()

                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().insert(orderListDetails)

                                    val orderList = AppDatabase.getDBInstance()!!.orderListDao().getListAccordingToShopID(shopId)
                                    if (orderList == null || orderList.isEmpty()) {

                                        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)

                                        if (shop != null) {
                                            val orderListEntity = OrderListEntity()

                                            orderListEntity.address = shop.address
                                            orderListEntity.order_amount = amount.substring(1)
                                            orderListEntity.owner_contact_no = shop.ownerContactNumber
                                            orderListEntity.owner_name = shop.ownerName
                                            orderListEntity.owner_email = shop.ownerEmailId
                                            orderListEntity.pin_code = shop.pinCode
                                            orderListEntity.shop_id = shop.shop_id
                                            orderListEntity.shop_image_link = shop.shopImageUrl
                                            orderListEntity.shop_lat = shop.shopLat.toString()
                                            orderListEntity.shop_long = shop.shopLong.toString()
                                            orderListEntity.shop_name = shop.shopName

//                                            if (!TextUtils.isEmpty(order_list[i].order_id))
//                                        orderListEntity.order_id = order_list[i].order_id

                                            //if (!TextUtils.isEmpty(shop.lastVisitedDate))
                                            orderListEntity.date = AppUtils.getCurrentDateForShopActi()
                                            orderListEntity.date_long = AppUtils.convertDateStringToLong(AppUtils.getCurrentDateForShopActi())

                                            AppDatabase.getDBInstance()!!.orderListDao().insert(orderListEntity)
                                        }
                                    } else {
                                        AppDatabase.getDBInstance()!!.orderListDao().updateDate(AppUtils.getCurrentDateForShopActi(), shopId)
                                        AppDatabase.getDBInstance()!!.orderListDao().updateDateLong(AppUtils.convertDateStringToLong(
                                                AppUtils.getCurrentDateForShopActi()), shopId)
                                    }

                                    uiThread {

                                        if (AppUtils.isOnline(mContext)) {
                                            if (addShop.isUploaded) {
                                                addOrderApi(orderListDetails.shop_id, orderListDetails.order_id, amount.substring(1),
                                                        desc, collection, AppUtils.getCurrentDateForShopActi())
                                            } else {
                                                syncShop(addShop, orderListDetails.shop_id, orderListDetails.order_id, amount.substring(1),
                                                        desc, collection, AppUtils.getCurrentDateForShopActi())
                                            }
                                        } else {
                                            (mContext as DashboardActivity).showSnackMessage("Order added successfully")

                                            viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopId) as ArrayList<OrderDetailsListEntity>

                                            initAdapter(viewAllOrderList)
                                            no_shop_tv.visibility = View.GONE

                                            if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                                                order_amount_tv.visibility = View.VISIBLE
                                                var amount = 0.0
                                                for (i in viewAllOrderList?.indices!!) {
                                                    amount += viewAllOrderList?.get(i)?.amount?.toDouble()!!
                                                }

                                                order_amount_tv.text = "Total Order Amount: ₹ " + amount
                                            } else
                                                order_amount_tv.visibility = View.GONE
                                        }
                                    }
                                }
//                                } else {
//                                    (mContext as DashboardActivity).showSnackMessage("Please sync your shop first")
//                                }
                            }
                        }

//                        override fun onAddedDataSuccess() {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun getDialogInstance(mdialog: Dialog?) {
//                    }
//
//                    override fun onUpdateClick(address: AddShopDBModelEntity?) {
//                        Pref.user_id + "_" + System.currentTimeMillis().toString()
//                        (mContext as DashboardActivity).showSnackMessage("Order added successfully")
//                    }

                    }).show((mContext as DashboardActivity).supportFragmentManager, "AddOrderDialog")*/

                    if (!Pref.isAddAttendence)
                        (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                    else
                        (mContext as DashboardActivity).loadFragment(FragType.OrderTypeListFragment, true, shopId)


                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
        addShopData.added_date = mAddShopDBModelEntity.added_date
        addShopData.assigned_to_pp_id = mAddShopDBModelEntity.assigned_to_pp_id
        addShopData.assigned_to_dd_id = mAddShopDBModelEntity.assigned_to_dd_id
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


        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id,false)
        addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!


        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name

        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate
        addShopData.purpose=mAddShopDBModelEntity.purpose

        callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shop_id, order_id, amount, collection,
                currentDateForShopActi, desc, mAddShopDBModelEntity.doc_degree)
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

        XLog.d("================SyncShop Input Params (Order)====================")
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

        if (degree_imgPath != null)
            XLog.d("doctor degree image path=======> $degree_imgPath")
        XLog.d("===================================================================")

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
                                                    addOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi)
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
                                                    addOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi)
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
                                                    addOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi)
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
                                                    addOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi)
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

            shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!

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

                shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!

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

    private fun addOrderApi(shop_id: String?, order_id: String?, amount: String, desc: String, collection: String, date: String?) {
        val repository = AddOrderRepoProvider.provideAddOrderRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.addOrder(Pref.session_token!!, Pref.user_id!!, shop_id!!, order_id!!, amount, desc, collection, date!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val orderList = result as BaseResponse
                            progress_wheel.stopSpinning()
                            if (orderList.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, order_id)
                            }

                            (mContext as DashboardActivity).showSnackMessage("Order added successfully")

                            viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopId) as ArrayList<OrderDetailsListEntity>

                            initAdapter(viewAllOrderList)
                            no_shop_tv.visibility = View.GONE

                            if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                                order_amount_tv.visibility = View.VISIBLE
                                var amount = 0.0
                                for (i in viewAllOrderList?.indices!!) {
                                    if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.amount))
                                        amount += viewAllOrderList?.get(i)?.amount?.toDouble()!!
                                }
                                val finalAmount = String.format("%.2f", amount.toFloat())

                                val builder = SpannableStringBuilder()

                                val str1 = SpannableString("Total Order Amount: ")
                                builder.append(str1)

                                val str2 = SpannableString("₹ $finalAmount")
                                str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                                builder.append(str2)

                                order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
                            } else
                                order_amount_tv.visibility = View.GONE

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")

                            (mContext as DashboardActivity).showSnackMessage("Order added successfully")

                            viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopId) as ArrayList<OrderDetailsListEntity>

                            initAdapter(viewAllOrderList)
                            no_shop_tv.visibility = View.GONE

                            if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                                order_amount_tv.visibility = View.VISIBLE
                                var amount = 0.0
                                for (i in viewAllOrderList?.indices!!) {
                                    if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.amount))
                                        amount += viewAllOrderList?.get(i)?.amount?.toDouble()!!
                                }
                                val finalAmount = String.format("%.2f", amount.toFloat())

                                val builder = SpannableStringBuilder()

                                val str1 = SpannableString("Total Order Amount: ")
                                builder.append(str1)

                                val str2 = SpannableString("₹ $finalAmount")
                                str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                                builder.append(str2)

                                order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
                            } else
                                order_amount_tv.visibility = View.GONE
                        })
        )
    }

    private fun AddOrderDialog() {
        try {
            if (mShopActivityEntity != null) {
                /*AddOrderDialog.getInstance(mShopActivityEntity!!, true, object : ShopAddressUpdateListener {
                    override fun onAddedDataSuccess() {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun getDialogInstance(mdialog: Dialog?) {
                    }

                    override fun onUpdateClick(address: AddShopDBModelEntity?) {
                        (mContext as DashboardActivity).showSnackMessage("Order added successfully")
                    }

                }).show((mContext as DashboardActivity).supportFragmentManager, "AddOrderDialog")*/
            } else if (maddShopDataObj != null) {
                /* AddOrderDialog.getInstance(maddShopDataObj!!, true, object : ShopAddressUpdateListener {
                     override fun onAddedDataSuccess() {
                         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                     }

                     override fun getDialogInstance(mdialog: Dialog?) {
                     }

                     override fun onUpdateClick(address: AddShopDBModelEntity?) {
                         (mContext as DashboardActivity).showSnackMessage("Order added successfully")
                     }

                 }).show((mContext as DashboardActivity).supportFragmentManager, "AddOrderDialog")*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initAdapter(viewAllOrderList: ArrayList<OrderDetailsListEntity>?) {

        ViewAllOrderListRecyclerViewAdapter = ViewAllOrderListRecyclerViewAdapter(mContext, viewAllOrderList /*ViewAllOrderListEntityList*/,
                object : ViewAllOrderListRecyclerViewAdapter.onScrollEndListener {
                    override fun onScrollEnd() {
                    }

                }, object : ViewAllOrderListRecyclerViewAdapter.OnItemClickListener {
            override fun onCollectionClick(position: Int) {
                try {

                    if (!Pref.isAddAttendence)
                        (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                    else {

                        val addShop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(viewAllOrderList?.get(position)?.shop_id)

                        collectionDialog = AddCollectionDialog.getInstance(viewAllOrderList?.get(position), true, addShop?.shopName!!,
                                AppUtils.getCurrentDateFormatInTa(viewAllOrderList?.get(position)?.only_date!!), viewAllOrderList[position].amount!!,
                                viewAllOrderList[position].order_id!!, object : AddCollectionDialog.AddCollectionClickLisneter {
                            override fun onClick(collection: String, date: String, paymentId: String, instrument: String, bank: String,
                                                 filePath: String, feedback: String, patientName: String, patientAddress: String, patinetNo: String,
                                                 hospital:String,emailAddress:String,order_id:String) {


                                if (addShop != null) {

                                    //if (addShop.isUploaded) {

                                    doAsync {

                                        val collectionDetails = CollectionDetailsEntity()
                                        collectionDetails.collection = collection/*.substring(1)*/

                                        val random = Random()
                                        val m = random.nextInt(9999 - 1000) + 1000

                                        //collectionDetails.collection_id = Pref.user_id + "_" + m /*+ "_" + System.currentTimeMillis().toString()*/
                                        collectionDetails.collection_id = Pref.user_id + "c" + m
                                        collectionDetails.shop_id = addShop.shop_id
                                        collectionDetails.date = date //AppUtils.getCurrentDate()
                                        collectionDetails.only_time = AppUtils.getCurrentTime()  //AppUtils.getCurrentDate()
                                        collectionDetails.bill_id = ""
                                        collectionDetails.order_id = viewAllOrderList[position].order_id
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
                                                    if (viewAllOrderList[position].isUploaded) {
                                                        addCollectionApi(collectionDetails.shop_id, collectionDetails.collection_id, "",
                                                                "", collection, collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                                    }
                                                    else {
                                                        syncOrderForCollection(viewAllOrderList[position], collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
                                                                collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                                    }
                                                } else {
                                                    syncShopForCollection(addShop, collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
                                                            collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                                }


                                            } else {
                                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                                voiceCollectionMsg()
                                                /*val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(ShopBillingListFragment.mAddShopDataObj?.shop_id!!) as java.util.ArrayList<CollectionDetailsEntity>

                                                if (list != null && list?.size!! > 0) {
                                                    order_amount_tv.visibility = View.VISIBLE
                                                    var amount = 0.0
                                                    for (i in list?.indices!!) {
                                                        if (!TextUtils.isEmpty(list?.get(i)?.collection))
                                                            amount += list?.get(i)?.collection?.toDouble()!!
                                                    }

                                                    val totalPrice = String.format("%.2f", amount.toFloat())
                                                    order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                                                } else
                                                    order_amount_tv.visibility = View.GONE*/
                                            }
                                        }
                                    }
                                }

                            }
                        })
                        collectionDialog?.show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionDialog")
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onEmailClick(position: Int) {
                val shopType = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopType(viewAllOrderList?.get(position)?.shop_id!!)
                senOrderEmail(viewAllOrderList?.get(position)?.shop_id!!, viewAllOrderList[position].order_id!!, shopType)
            }

            override fun onLocationClick(position: Int) {
                if (!TextUtils.isEmpty(viewAllOrderList?.get(position)?.order_lat) && !TextUtils.isEmpty(viewAllOrderList?.get(position)?.order_long))
                    (mContext as DashboardActivity).openLocationMap(viewAllOrderList?.get(position)!!, false)
                else
                    (mContext as DashboardActivity).showSnackMessage("No order location available")
            }

            override fun onSyncClick(position: Int) {
                if (AppUtils.isOnline(mContext)) {

                    val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
                    if (addShop.isUploaded)
                        syncAddOrderApi(viewAllOrderList?.get(position)?.shop_id, viewAllOrderList?.get(position)?.order_id, viewAllOrderList?.get(position)?.amount!!,
                                viewAllOrderList[position].description!!, viewAllOrderList[position].collection!!,
                                viewAllOrderList[position].date!!, viewAllOrderList[position].remarks, viewAllOrderList[position].signature,
                                viewAllOrderList[position].order_lat, viewAllOrderList[position].order_long, viewAllOrderList[position])
                    else {
                        syncShopFromSyncOption(addShop, viewAllOrderList?.get(position)?.shop_id, viewAllOrderList?.get(position)?.order_id, viewAllOrderList?.get(position)?.amount!!/*.substring(1)*/,
                                viewAllOrderList[position].description!!, viewAllOrderList[position].collection!!/*.substring(1)*/,
                                viewAllOrderList[position].date!!, viewAllOrderList[position].remarks, viewAllOrderList[position].signature,
                                viewAllOrderList[position].order_lat, viewAllOrderList[position].order_long, viewAllOrderList[position])
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
                (mContext as DashboardActivity).loadFragment(FragType.ViewCartFragment, true, viewAllOrderList?.get(position)/*?.shop_id*/!!)
            }
        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        order_list_rv.layoutManager = layoutManager
        order_list_rv.adapter = ViewAllOrderListRecyclerViewAdapter
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
            addOrder.scheme_amount = "0"

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

        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(order.shop_id!!)
        if (shopActivity != null) {
            if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(order.shop_id!!)

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

                                    viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shop_id) as ArrayList<OrderDetailsListEntity>
                                    initAdapter(viewAllOrderList)

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

                                    viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shop_id) as ArrayList<OrderDetailsListEntity>
                                    initAdapter(viewAllOrderList)

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

        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id,false)
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

        /*06-02-2022*/
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
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")

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

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")

                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
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


    private fun syncShopFromSyncOption(addShop: AddShopDBModelEntity, shop_id: String?, order_id: String?, amount: String, desc: String, collection: String,
                                       currentDateForShopActi: String, remarks: String?, signature: String?, orderLat: String?, orderLong: String?,
                                       orderDetailsListEntity: OrderDetailsListEntity) {
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

        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id,false)
        addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!


        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name

        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate
        addShopData.purpose=mAddShopDBModelEntity.purpose

        callAddShopApiForSync(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shop_id, order_id, amount, collection,
                currentDateForShopActi, desc, mAddShopDBModelEntity.doc_degree, remarks, signature, orderLat, orderLong, orderDetailsListEntity)
    }

    private fun callAddShopApiForSync(addShop: AddShopRequestData, shop_imgPath: String?, shop_id: String?, order_id: String?, amount: String, collection: String,
                                      currentDateForShopActi: String, desc: String, degree_imgPath: String?, remarks: String?,
                                      signature: String?, orderLat: String?, orderLong: String?, orderDetailsListEntity: OrderDetailsListEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        if (isShopRegistrationInProcess)
            return

        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("==============================SyncShop Input Params (Order)==============================")
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
                                                    syncAddOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi,
                                                            remarks, signature, orderLat, orderLong, orderDetailsListEntity)
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
                                                    syncAddOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi,
                                                            remarks, signature, orderLat, orderLong, orderDetailsListEntity)
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
                                                    syncAddOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi,
                                                            remarks, signature, orderLat, orderLong, orderDetailsListEntity)
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
                                                    syncAddOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi,
                                                            remarks, signature, orderLat, orderLong, orderDetailsListEntity)
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

    private fun syncAddOrderApi(shop_id: String?, order_id: String?, amount: String, desc: String, collection: String, date: String,
                                remarks: String?, signature: String?, orderLat: String?, orderLong: String?, orderListDetails: OrderDetailsListEntity) {

        val addOrder = AddOrderInputParamsModel()
        addOrder.collection = ""
        addOrder.description = ""
        addOrder.order_amount = amount
        addOrder.order_date = date //AppUtils.getCurrentDateFormatInTa(date)
        addOrder.order_id = order_id
        addOrder.shop_id = shop_id
        addOrder.session_token = Pref.session_token
        addOrder.user_id = Pref.user_id
        addOrder.latitude = orderLat
        addOrder.longitude = orderLong

        if (orderListDetails!!.scheme_amount != null)
            addOrder.scheme_amount = orderListDetails!!.scheme_amount
        else
            addOrder.scheme_amount = ""

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
                if (!TextUtils.isEmpty(orderLat) && !TextUtils.isEmpty(orderLong))
                    addOrder.address = LocationWizard.getLocationName(mContext, orderLat!!.toDouble(), orderLong!!.toDouble())
                else
                    addOrder.address = ""
            }
        } else {
            if (!TextUtils.isEmpty(orderLat) && !TextUtils.isEmpty(orderLong))
                addOrder.address = LocationWizard.getLocationName(mContext, orderLat!!.toDouble(), orderLong!!.toDouble())
            else
                addOrder.address = ""
        }

        /*06-01-2022*/
        if (orderListDetails?.Hospital != null)
            addOrder.Hospital = orderListDetails?.Hospital
        else
            addOrder.Hospital = ""

        if (orderListDetails?.Email_Address != null)
            addOrder.Email_Address = orderListDetails?.Email_Address
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

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")

                                    viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shop_id) as ArrayList<OrderDetailsListEntity>
                                    initAdapter(viewAllOrderList)
                                } else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
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

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")

                                    viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shop_id) as ArrayList<OrderDetailsListEntity>
                                    initAdapter(viewAllOrderList)
                                } else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                            })
            )
        }
    }


    fun updateList() {
        viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopId) as ArrayList<OrderDetailsListEntity>


        if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
            initAdapter(viewAllOrderList)
            no_shop_tv.visibility = View.GONE
        } else {
            no_shop_tv.visibility = View.VISIBLE
        }

        /*initAdapter(viewAllOrderList)
        no_shop_tv.visibility = View.GONE*/

        try {
            if (viewAllOrderList != null && viewAllOrderList?.size!! > 0) {
                order_amount_tv.visibility = View.VISIBLE
                var amount = 0.0
                for (i in viewAllOrderList?.indices!!) {
                    if (!TextUtils.isEmpty(viewAllOrderList?.get(i)?.amount))
                        amount += viewAllOrderList?.get(i)?.amount?.toDouble()!!
                }
                val finalAmount = String.format("%.2f", amount.toFloat())

                val builder = SpannableStringBuilder()

                val str1 = SpannableString("Total Order Amount: ")
                builder.append(str1)

                val str2 = SpannableString("₹ $finalAmount")
                str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                builder.append(str2)

                order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
            } else
                order_amount_tv.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        collectionDialog?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setImage(file: File) {
        collectionDialog?.setImage(file)
    }
}
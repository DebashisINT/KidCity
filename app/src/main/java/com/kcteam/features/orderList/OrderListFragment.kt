package com.kcteam.features.orderList

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.OrderListEntity
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.averageshop.business.InfoWizard
import com.kcteam.features.averageshop.presentation.AverageShopListClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.orderList.api.OrderListRepoProvider
import com.kcteam.features.orderList.model.OrderListDataModel
import com.kcteam.features.orderList.model.OrderListResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.google.gson.Gson
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime


/**
 * Created by Pratishruti on 15-11-2017.
 */
class OrderListFragment : BaseFragment(), DatePickerListener, View.OnClickListener {


    lateinit var OrderListAdapter: OrderListAdapter
    private lateinit var shopList: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var noShopAvailable: AppCompatTextView
    private lateinit var picker: HorizontalPicker
    private lateinit var ShopActivityEntityList: List<ShopActivityEntity>
    private var orderList: ArrayList<OrderListEntity>? = null
    private lateinit var noOfShop: AppCustomTextView
    private lateinit var avg_shop_tv: TextView
    private lateinit var total_shop_TV: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var selectedDate: String
    private lateinit var sync_all_tv: AppCustomTextView
    var i: Int = 0

    private lateinit var mContext: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_average_shop_visit, container, false)
        selectedDate = AppUtils.getCurrentDateForShopActi()
        initView(view)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(view: View) {


        /*NEW CALENDER*/
        picker = view.findViewById<HorizontalPicker>(R.id.datePicker)
        picker.setListener(this)
                .setDays(60)
                .setOffset(30)
                .setDateSelectedColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//box color
                .setDateSelectedTextColor(ContextCompat.getColor(mContext, R.color.white))
                .setMonthAndYearTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//month color
                .setTodayButtonTextColor(ContextCompat.getColor(mContext, R.color.date_selector_color))
                .setTodayDateTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setTodayDateBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent))//
                .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setDayOfWeekTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .showTodayButton(false)
                .init()
        picker.backgroundColor = Color.WHITE
        picker.setDate(DateTime())


        /*NEW CALENDER*/


        noShopAvailable = view.findViewById(R.id.no_shop_tv)
        shopList = view.findViewById(R.id.shop_list_RCV)
        noOfShop = view.findViewById(R.id.no_of_shop_TV)
        avg_shop_tv = view.findViewById(R.id.avg_shop_tv)
        total_shop_TV = view.findViewById(R.id.total_shop_TV)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        sync_all_tv = view.findViewById(R.id.sync_all_tv);
        sync_all_tv.visibility = View.GONE
        progress_wheel.stopSpinning()
        total_shop_TV.text = InfoWizard.getTotalShopVisitCount()
        noOfShop.text = InfoWizard.getAvergareShopVisitCount()
        sync_all_tv.setOnClickListener(this)
        initShopList()

    }

    override fun onClick(p0: View?) {
        i = 0
        when (p0?.id) {
            R.id.sync_all_tv -> {
                //syncAllShopActivity(ShopActivityEntityList[i].shopid!!)
            }
        }
    }

    override fun onDateSelected(dateSelected: DateTime) {
        var dateTime = dateSelected.toString()
        var dateFormat = dateTime.substring(0, dateTime.indexOf('T'))
        selectedDate = dateFormat
        /*ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(dateFormat)

        Collections.reverse(ShopActivityEntityList)

        noShopAvailable.visibility = View.GONE
        shopList.visibility = View.VISIBLE
        initAdapter()*/

        initShopList()

        /*if (ShopActivityEntityList.isNotEmpty()) {
            noShopAvailable.visibility = View.GONE
            shopList.visibility = View.VISIBLE
            try {
                initAdapter()
            } catch (e: UninitializedPropertyAccessException) {
                e.printStackTrace()
                initAdapter()
            }

        } else {
            noShopAvailable.visibility = View.VISIBLE
            shopList.visibility = View.GONE
        }*/

    }

    private fun syncShopActivity(shopId: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        val mList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, selectedDate)
        if (mList.isEmpty())
            return
        val shopActivity = mList[0]
//        var shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shopId)
        val shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token
        val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
        val shopDurationData = ShopDurationRequestData()
        shopDurationData.shop_id = shopActivity.shopid
        if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
            val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
            val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

            if (!Pref.isMultipleVisitEnable) {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())
            }
            else {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
            }

            shopDurationData.spent_duration = duration
        } else {
            shopDurationData.spent_duration = shopActivity.duration_spent
        }
        shopDurationData.visited_date = shopActivity.visited_date
        shopDurationData.visited_time = shopActivity.visited_date
        if (TextUtils.isEmpty(shopActivity.distance_travelled))
            shopActivity.distance_travelled = "0.0"
        shopDurationData.distance_travelled = shopActivity.distance_travelled
        val list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
        if (list != null && list.isNotEmpty())
            shopDurationData.total_visit_count = list[0].totalVisitCount

        if (!TextUtils.isEmpty(shopActivity.feedback))
            shopDurationData.feedback = shopActivity.feedback
        else
            shopDurationData.feedback = ""

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

        shopDataList.add(shopDurationData)

        if (shopDataList.isEmpty()) {
            return
        }
        progress_wheel.spin()
        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()
        var gson = Gson();
        var jsonInString = gson.toJson(shopDurationApiReq);
        Log.v("TAG", jsonInString)
        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            XLog.d("ShopActivityFromAverageShop : " + "User Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name + ", RESPONSE:" + result.message)
                            if (result.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate)
                                OrderListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))

                            } else {
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("ShopActivityFromAverageShop : " + "User Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name + ", ERROR:" + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                        })
        )

    }

    private fun initShopList() {

        /*val orderEntity = OrderListEntity()
        orderEntity.shop_id = 1
        orderEntity.address = "salt lake"
        orderEntity.date = "23-09-2018"
        orderEntity.order_amount = "500"
        orderEntity.owner_email = "abc@gmail.com"
        orderEntity.shop_id = 1
        orderEntity.shop_id = 1
        orderEntity.shop_id = 1
        orderEntity.shop_id = 1
        orderEntity.shop_id = 1*/

        orderList = AppDatabase.getDBInstance()!!.orderListDao().getListAccordingToDate(selectedDate) as ArrayList<OrderListEntity>

        /*ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

        Collections.reverse(ShopActivityEntityList)*/

        noShopAvailable.visibility = View.VISIBLE
        shopList.visibility = View.GONE

        if (orderList != null && orderList?.isNotEmpty()!!) {
            noShopAvailable.visibility = View.GONE
            shopList.visibility = View.VISIBLE
            initAdapter()
        } else {
            /*noShopAvailable.visibility = View.VISIBLE
            shopList.visibility = View.GONE*/

            if (AppUtils.isOnline(mContext)) {
                getOrderListApi()
            } else
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }
    }

    private fun getOrderListApi() {
        val repository = OrderListRepoProvider.provideOrderListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getOrderList(Pref.session_token!!, Pref.user_id!!, selectedDate)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val orderList = result as OrderListResponseModel
                            if (orderList.status == NetworkConstant.SUCCESS) {
                                if (orderList.order_list == null || orderList.order_list?.size!! == 0) {
                                    progress_wheel.stopSpinning()
                                    noShopAvailable.visibility = View.VISIBLE
                                    shopList.visibility = View.GONE
                                } else
                                    saveToDatabase(orderList.order_list!!)

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

    private fun saveToDatabase(order_list: List<OrderListDataModel>) {

        doAsync {

            for (i in order_list.indices) {

                val list = AppDatabase.getDBInstance()!!.orderListDao().getListAccordingToShopID(order_list[i].shop_id!!)

                //if (list == null || list.isEmpty()) {
                    val orderList = OrderListEntity()
                    orderList.address = order_list[i].address
                    orderList.order_amount = order_list[i].order_amount
                    orderList.owner_contact_no = order_list[i].owner_contact_no
                    orderList.owner_name = order_list[i].owner_name
                    orderList.owner_email = order_list[i].owner_email
                    orderList.pin_code = order_list[i].pin_code
                    orderList.shop_id = order_list[i].shop_id
                    orderList.shop_image_link = order_list[i].shop_image_link
                    orderList.shop_lat = order_list[i].shop_lat
                    orderList.shop_long = order_list[i].shop_long
                    orderList.shop_name = order_list[i].shop_name

                    if (!TextUtils.isEmpty(order_list[i].order_id))
                        orderList.order_id = order_list[i].order_id

                    if (!TextUtils.isEmpty(order_list[i].date) && !order_list[i].date.equals("null", ignoreCase = true)) {
                        orderList.date = AppUtils.changeAttendanceDateFormatToCurrent(order_list[i].date!!)
                        orderList.date_long = AppUtils.convertDateStringToLong(AppUtils.changeAttendanceDateFormatToCurrent(order_list[i].date!!))
                    }
                    AppDatabase.getDBInstance()!!.orderListDao().insert(orderList)
                /*} else {

                    if (!TextUtils.isEmpty(order_list[i].date) && !order_list[i].date.equals("null", ignoreCase = true)) {
                        if (AppUtils.changeAttendanceDateFormatToCurrent(order_list[i].date!!) > list[0].date!!) {
                            AppDatabase.getDBInstance()!!.orderListDao().updateDate(AppUtils.changeAttendanceDateFormatToCurrent(order_list[i].date!!), order_list[i].shop_id!!)
                            AppDatabase.getDBInstance()!!.orderListDao().updateDateLong(AppUtils.convertDateStringToLong(
                                    AppUtils.changeAttendanceDateFormatToCurrent(order_list[i].date!!)), order_list[i].shop_id!!)
                        }
                    }
                }*/
            }

            uiThread {
                progress_wheel.stopSpinning()

                noShopAvailable.visibility = View.GONE
                shopList.visibility = View.VISIBLE

                orderList = AppDatabase.getDBInstance()!!.orderListDao().getListAccordingToDate(selectedDate) as ArrayList<OrderListEntity>
                initAdapter()
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun initAdapter() {
        OrderListAdapter = OrderListAdapter(mContext, orderList, object : AverageShopListClickListener {
            override fun onSyncClick(position: Int) {
                //syncShopActivity(ShopActivityEntityList[position].shopid!!)
            }

            override fun OnItemClick(position: Int) {
                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, orderList?.get(position)?.shop_id!!)
            }

            override fun OnMenuClick(position: Int, view: View) {
                initiatePopupWindow(view, position)
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
        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        shopList.layoutManager = layoutManager
        shopList.adapter = OrderListAdapter
    }


    private fun initiatePopupWindow(view: View, position: Int) {
        val popup = PopupWindow(context)
        val layout = layoutInflater.inflate(R.layout.popup_window_shop_item, null)

        popup.contentView = layout
        popup.isOutsideTouchable = true
        popup.isFocusable = true

        var call_ll: LinearLayout = layout.findViewById(R.id.call_ll)
        var direction_ll: LinearLayout = layout.findViewById(R.id.direction_ll)
        var add_order_ll: LinearLayout = layout.findViewById(R.id.add_order_ll)

        var call_iv: ImageView = layout.findViewById(R.id.call_iv)
        var call_tv: TextView = layout.findViewById(R.id.call_tv)
        var direction_iv: ImageView = layout.findViewById(R.id.direction_iv)
        var direction_tv: TextView = layout.findViewById(R.id.direction_tv)
        var order_iv: ImageView = layout.findViewById(R.id.order_iv)
        var order_tv: TextView = layout.findViewById(R.id.order_tv)


        call_ll.setOnClickListener(View.OnClickListener {
            call_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_call_select))

            order_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_add_order_deselect))
            direction_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_direction_deselect))
            order_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))
            direction_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))

            call_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            popup.dismiss()
//            IntentActionable.initiatePhoneCall(mContext, list[position].)
        })

        direction_ll.setOnClickListener(View.OnClickListener {
            direction_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_direction_select))

            call_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_call_deselect))
            order_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_add_order_deselect))
            call_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))
            order_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))

            direction_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            popup.dismiss()
            (mContext as DashboardActivity).openLocationWithTrack()

        })

        add_order_ll.setOnClickListener(View.OnClickListener {
            order_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_add_order_select))

            call_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_call_deselect))
            direction_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_direction_deselect))
            call_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))
            direction_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))

            order_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            popup.dismiss()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))

        })

        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(view)
        popup.update()

    }

    private fun syncAllShopActivity(shopId: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        val mList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, selectedDate)
        if (mList.isEmpty())
            return
        val shopActivity = mList[0]
//        var shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shopId)
        val shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token
        val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
        val shopDurationData = ShopDurationRequestData()
        shopDurationData.shop_id = shopActivity.shopid
        if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
            val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
            val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

            if (!Pref.isMultipleVisitEnable) {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())
            }
            else {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
            }

            shopDurationData.spent_duration = duration
        } else {
            shopDurationData.spent_duration = shopActivity.duration_spent
        }
        shopDurationData.visited_date = shopActivity.visited_date
        shopDurationData.visited_time = shopActivity.visited_date
        if (TextUtils.isEmpty(shopActivity.distance_travelled))
            shopActivity.distance_travelled = "0.0"
        shopDurationData.distance_travelled = shopActivity.distance_travelled
        val list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
        if (list != null && list.isNotEmpty())
            shopDurationData.total_visit_count = list[0].totalVisitCount

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

        shopDataList.add(shopDurationData)

        if (shopDataList.isEmpty()) {
            return
        }
        progress_wheel.spin()
        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            XLog.d("ShopActivityFromAverageShop : " + "User Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name + ", RESPONSE:" + result.message)
                            if (result.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate)
                                //
                                i++
                                if (i < ShopActivityEntityList.size) {
                                    syncAllShopActivity(ShopActivityEntityList[i].shopid!!)
                                } else {
                                    progress_wheel.stopSpinning()
                                    OrderListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                                    i = 0;
                                }

                            } else {
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (error != null) {
                                XLog.d("ShopActivityFromAverageShop : " + "User Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name + ", ERROR:" + error.localizedMessage)
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                            }
                        })
        )

    }

    fun updateItem() {
        orderList = AppDatabase.getDBInstance()!!.orderListDao().getListAccordingToDate(selectedDate) as ArrayList<OrderListEntity>

        if (orderList != null && orderList?.isNotEmpty()!!) {
            noShopAvailable.visibility = View.GONE
            shopList.visibility = View.VISIBLE
            initAdapter()
        } else {
            noShopAvailable.visibility = View.VISIBLE
            shopList.visibility = View.GONE
        }
    }
}
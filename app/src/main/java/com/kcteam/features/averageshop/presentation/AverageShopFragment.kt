package com.kcteam.features.averageshop.presentation

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.google.gson.Gson
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseActivity.Companion.compositeDisposable
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.kcteam.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.*
import com.kcteam.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.kcteam.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.kcteam.features.addshop.presentation.AdapterQuestionList
import com.kcteam.features.addshop.presentation.AddShopFragment
import com.kcteam.features.averageshop.business.InfoWizard
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dashboard.presentation.api.ShopVisitImageUploadRepoProvider
import com.kcteam.features.dashboard.presentation.model.ShopVisitImageUploadInputModel
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.model.ShopRevisitStatusRequest
import com.kcteam.features.location.model.ShopRevisitStatusRequestData
import com.kcteam.features.location.shopRevisitStatus.ShopRevisitStatusRepositoryProvider
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.returnsOrder.ViewAllReturnListFragment
import com.kcteam.features.viewAllOrder.interf.QaOnCLick
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Pratishruti on 15-11-2017.
 */
class AverageShopFragment : BaseFragment(), DatePickerListener, View.OnClickListener {

    lateinit var averageShopListAdapter: AverageShopListAdapter
    private lateinit var shopList: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var noShopAvailable: AppCompatTextView
    private lateinit var picker: HorizontalPicker
    private lateinit var ShopActivityEntityList: List<ShopActivityEntity>
    private lateinit var noOfShop: AppCustomTextView
    private lateinit var avg_shop_tv: TextView
    private lateinit var total_shop_TV: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var selectedDate: String
    private lateinit var sync_all_tv: AppCustomTextView
    var i: Int = 0
    private var j: Int = 0


    val revisitStatusList : MutableList<ShopRevisitStatusRequestData> = ArrayList()

    var rv_qaList: ArrayList<QuestionEntity> = ArrayList()
    var quesAnsList:ArrayList<AddShopFragment.QuestionAns> = ArrayList()
    private var adapterqaList: AdapterQuestionList? = null

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
        sync_all_tv = view.findViewById(R.id.sync_all_tv)
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
                // tested on 23-11-2021 begin
                for (i in ShopActivityEntityList.indices) {
                if (!ShopActivityEntityList[i].isDurationCalculated && ShopActivityEntityList[i].startTimeStamp != "0" && ShopActivityEntityList[i].isUploaded==false) {
                    Pref.durationCompletedShopId = ShopActivityEntityList[i].shopid!!
                    val endTimeStamp = System.currentTimeMillis().toString()
                    val totalMinute = AppUtils.getMinuteFromTimeStamp(ShopActivityEntityList[i].startTimeStamp, endTimeStamp)
                    val duration = AppUtils.getTimeFromTimeSpan(ShopActivityEntityList[i].startTimeStamp, endTimeStamp)

                    if (!Pref.isMultipleVisitEnable) {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(ShopActivityEntityList[i].shopid!!, totalMinute, selectedDate)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, ShopActivityEntityList[i].shopid!!, selectedDate)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(ShopActivityEntityList[i].shopid!!, duration, selectedDate)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, ShopActivityEntityList[i].shopid!!, selectedDate)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, ShopActivityEntityList[i].shopid!!, selectedDate)
                    }
                    else {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(ShopActivityEntityList[i].shopid!!, totalMinute, selectedDate, ShopActivityEntityList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, ShopActivityEntityList[i].shopid!!, selectedDate, ShopActivityEntityList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(ShopActivityEntityList[i].shopid!!, duration, selectedDate, ShopActivityEntityList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, ShopActivityEntityList[i].shopid!!, selectedDate, ShopActivityEntityList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, ShopActivityEntityList[i].shopid!!, selectedDate, ShopActivityEntityList[i].startTimeStamp)
                    }
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), ShopActivityEntityList[i].shopid!!, selectedDate, ShopActivityEntityList[i].startTimeStamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(LocationWizard.getNewLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()), ShopActivityEntityList[i].shopid!!, selectedDate, ShopActivityEntityList[i].startTimeStamp)

                    val netStatus = if (AppUtils.isOnline(mContext))
                        "Online"
                    else
                        "Offline"

                    val netType = if (AppUtils.getNetworkType(mContext).equals("wifi", ignoreCase = true))
                        AppUtils.getNetworkType(mContext)
                    else
                        "Mobile ${AppUtils.mobNetType(mContext)}"

                    if (!Pref.isMultipleVisitEnable) {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                                AppUtils.getBatteryPercentage(mContext).toString(), netStatus, netType.toString(), ShopActivityEntityList[i].shopid!!,selectedDate)
                    }
                    else {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                                AppUtils.getBatteryPercentage(mContext).toString(), netStatus, netType.toString(), ShopActivityEntityList[i].shopid!!, selectedDate, ShopActivityEntityList[i].startTimeStamp)
                    }
//                    AppUtils.isShopVisited = false

                    Pref.isShopVisited=false
                    /*if (Pref.willShowShopVisitReason && totalMinute.toInt() < Pref.minVisitDurationSpentTime.toInt()) {
                        Pref.isShowShopVisitReason = true
                        showRevisitReasonDialog(shopActivityList[i].startTimeStamp)
                    }*/
                }
            }
                //ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)
                Collections.reverse(ShopActivityEntityList)
                // tested on 23-11-2021 end



                /*if (ShopActivityEntityList != null && ShopActivityEntityList.isNotEmpty())
                    syncAllShopActivity(ShopActivityEntityList[i].shopid!!)
                else {
                    var unSyncedList: List<ShopVisitImageModelEntity>? = null
                    for (i in ShopActivityEntityList.indices) {
                        unSyncedList = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                    }

                    if (unSyncedList != null && unSyncedList.isNotEmpty()) {
                        j = 0
                        callShopVisitImageUploadApiForAll(unSyncedList)
                    }
                }*/



                if (!Pref.isMultipleVisitEnable) {
                    if (ShopActivityEntityList != null && ShopActivityEntityList.isNotEmpty()) {

                        var list = ArrayList<ShopActivityEntity>()

                        for (i in ShopActivityEntityList.indices) {
                            val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[i].shopid)
                            if (shop.isUploaded) {
                                if (ShopActivityEntityList[i].isDurationCalculated /*&& !ShopActivityEntityList[i].isUploaded*/) {
                                    if (AppUtils.isVisitSync == "1")
                                        list.add(ShopActivityEntityList[i])
                                    else {
                                        if (!ShopActivityEntityList[i].isUploaded)
                                            list.add(ShopActivityEntityList[i])
                                    }
                                }
                            }
                        }

                        /*if(list.size>0){
                            for( i in list?.indices){
                                var revisitStatusObj= ShopRevisitStatusRequestData()
                                var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(list?.get(i).shop_revisit_uniqKey.toString())
                                if(data != null ){
                                    revisitStatusObj.shop_id=data.shop_id
                                    revisitStatusObj.order_status=data.order_status
                                    revisitStatusObj.order_remarks=data.order_remarks
                                    revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                                    revisitStatusList.add(revisitStatusObj)
                                }
                            }
                        }*/


                        list = list.filter { it.isUploaded == false } as ArrayList<ShopActivityEntity>

                        if (list.size > 0)
                            syncAllShopActivity(list[i].shopid!!, list)
                        else
                            syncShopVisitImage()

                    } else {
                        syncShopVisitImage()
                    }
                }
                else {
                    if (ShopActivityEntityList != null && ShopActivityEntityList.isNotEmpty()) {

                        var list = ArrayList<ShopActivityEntity>()

                        for (i in ShopActivityEntityList.indices) {
                            val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[i].shopid)
                            if (shop.isUploaded) {
                                if (ShopActivityEntityList[i].isDurationCalculated /*&& !ShopActivityEntityList[i].isUploaded*/) {
                                    if (AppUtils.isVisitSync == "1")
                                        list.add(ShopActivityEntityList[i])
                                    else {
                                        if (!ShopActivityEntityList[i].isUploaded)
                                            list.add(ShopActivityEntityList[i])
                                    }
                                }
                            }
                        }

                        list = list.filter { it.isUploaded == false } as ArrayList<ShopActivityEntity>

                        if (list.size > 0)
                            syncAllShopActivityForMultiVisit(list)
                    }
                }
            }
        }
    }

    private fun syncAllShopActivityForMultiVisit(list_: ArrayList<ShopActivityEntity>) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
        val shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token

        for (i in list_.indices) {
            val shopActivity = list_[i]

            val shopDurationData = ShopDurationRequestData()
            shopDurationData.shop_id = shopActivity.shopid
            if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                if (!Pref.isMultipleVisitEnable) {
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())
                } else {
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
            shopDurationData.in_location = shopActivity.in_loc
            shopDurationData.out_location = shopActivity.out_loc
            shopDurationData.shop_revisit_uniqKey=shopActivity.shop_revisit_uniqKey


            /*10-12-2021*/
            shopDurationData.updated_by = Pref.user_id
            try{
                shopDurationData.updated_on = shopActivity.updated_on!!
            }catch (ex:Exception){
                shopDurationData.updated_on= ""
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

            XLog.d("========SYNC ALL VISITED SHOP DATA (AVERAGE SHOP)=====")
            XLog.d("SHOP ID======> " + shopDurationData.shop_id)
            XLog.d("SPENT DURATION======> " + shopDurationData.spent_duration)
            XLog.d("VISIT DATE=========> " + shopDurationData.visited_date)
            XLog.d("VISIT DATE TIME==========> " + shopDurationData.visited_date)
            XLog.d("TOTAL VISIT COUNT========> " + shopDurationData.total_visit_count)
            XLog.d("DISTANCE TRAVELLED========> " + shopDurationData.distance_travelled)
            XLog.d("FEEDBACK========> " + shopDurationData.feedback)
            XLog.d("isFirstShopVisited========> " + shopDurationData.isFirstShopVisited)
            XLog.d("distanceFromHomeLoc========> " + shopDurationData.distanceFromHomeLoc)
            XLog.d("next_visit_date========> " + shopDurationData.next_visit_date)
            XLog.d("early_revisit_reason========> " + shopDurationData.early_revisit_reason)
            XLog.d("device_model========> " + shopDurationData.device_model)
            XLog.d("android_version========> " + shopDurationData.android_version)
            XLog.d("battery========> " + shopDurationData.battery)
            XLog.d("net_status========> " + shopDurationData.net_status)
            XLog.d("net_type========> " + shopDurationData.net_type)
            XLog.d("in_time========> " + shopDurationData.in_time)
            XLog.d("out_time========> " + shopDurationData.out_time)
            XLog.d("start_timestamp========> " + shopDurationData.start_timestamp)
            XLog.d("in_location========> " + shopDurationData.in_location)
            XLog.d("out_location========> " + shopDurationData.out_location)
            XLog.d("=======================================================")
        }

        if (shopDataList.isEmpty()) {
            return
        }

        Log.e("Average Shop", "isShopActivityUpdating====> " + BaseActivity.isShopActivityUpdating)
        if (BaseActivity.isShopActivityUpdating)
            return

        BaseActivity.isShopActivityUpdating = true
/////////////
        revisitStatusList.clear()
        for(i in 0..shopDataList?.size-1){
            var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(shopDataList?.get(i)?.shop_revisit_uniqKey!!.toString())
            if(data!=null ){
                var revisitStatusObj= ShopRevisitStatusRequestData()
                revisitStatusObj.shop_id=data.shop_id
                revisitStatusObj.order_status=data.order_status
                revisitStatusObj.order_remarks=data.order_remarks
                revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                revisitStatusList.add(revisitStatusObj)
            }
        }

/////////////////
        progress_wheel.spin()
        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            XLog.d("ShopActivityFromAverageShop : RESPONSE STATUS:= " + result.status + ", RESPONSE MESSAGE:= " + result.message +
                                    "\nUser Id" + Pref.user_id + ", Session Token" + Pref.session_token)
                            if (result.status == NetworkConstant.SUCCESS) {
                                shopDataList.forEach {
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, it.shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(it.visited_date!!), it.start_timestamp!!)
                                }


                                if(!revisitStatusList.isEmpty()){
                                    callRevisitStatusUploadApi(revisitStatusList!!)
                                }
                                for(i in 0..shopDataList?.size-1){
                                    callCompetetorImgUploadApi(shopDataList?.get(i)?.shop_id!!)
                                }

                                val dateWiseList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                XLog.d("=======UPDATE ADAPTER FOR SYNC ALL VISIT SHOP DATA (AVERAGE SHOP)=======")
                                XLog.d("shop list size====> " + dateWiseList.size)
                                XLog.d("specific date====> $selectedDate")

                                averageShopListAdapter.updateList(dateWiseList)

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                Collections.reverse(ShopActivityEntityList)

                                progress_wheel.stopSpinning()
                                BaseActivity.isShopActivityUpdating = false
                            } else {

                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                BaseActivity.isShopActivityUpdating = false
                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)

                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            if (error != null) {
                                XLog.d("ShopActivityFromAverageShop : ERROR:= " + error.localizedMessage + "\nUser Id" + Pref.user_id +
                                        ", Session Token" + Pref.session_token)
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)
                            }
                        })
        )

    }


    private fun syncShopVisitImage() {
        val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
        if (ShopActivityEntityList != null) {
            for (i in ShopActivityEntityList.indices) {
                /*val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[i].shopid)
                if (shop.isUploaded) {*/
                if (ShopActivityEntityList[i].isDurationCalculated && ShopActivityEntityList[i].isUploaded) {

                    var unSyncedData: List<ShopVisitImageModelEntity>? = null

                    /*val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false,
                            ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)*/

                    if (AppUtils.isVisitSync == "1")
                        unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysListAccordingToShopId(ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                    else
                        unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false,
                                ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)

                    if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                        unSyncedList.add(unSyncedData[0])
                    }
                }
                //}
            }

            if (unSyncedList.size > 0) {
                j = 0
                callShopVisitImageUploadApiForAll(unSyncedList)
            } else {
                val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()
                for (i in ShopActivityEntityList.indices) {
                    if (ShopActivityEntityList[i].isDurationCalculated && ShopActivityEntityList[i].isUploaded) {

                        var unSyncedData: List<ShopVisitAudioEntity>? = null

                        unSyncedData = if (AppUtils.isVisitSync == "1")
                            AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysListAccordingToShopId(ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                        else
                            AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                    ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)

                        if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                            unSyncedAudioList.add(unSyncedData[0])
                        }
                    }
                }

                if (unSyncedAudioList.isNotEmpty()) {
                    j = 0
                    callShopVisitAudioUploadApiForAll(unSyncedAudioList)
                }
            }
        }
    }

    private fun callShopVisitImageUploadApiForAll(unSyncedList: List<ShopVisitImageModelEntity>) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val visitImageShop = ShopVisitImageUploadInputModel()
        visitImageShop.session_token = Pref.session_token
        visitImageShop.user_id = Pref.user_id
        visitImageShop.shop_id = unSyncedList[j].shop_id
        visitImageShop.visit_datetime = unSyncedList[j].visit_datetime

        Log.e("Average Shop", "isShopActivityUpdating=============> " + BaseActivity.isShopActivityUpdating)
        if (BaseActivity.isShopActivityUpdating)
            return

        BaseActivity.isShopActivityUpdating = true

        XLog.d("========UPLOAD REVISIT ALL IMAGE INPUT PARAMS (AVERAGE SHOP)======")
        XLog.d("USER ID======> " + visitImageShop.user_id)
        XLog.d("SESSION ID======> " + visitImageShop.session_token)
        XLog.d("SHOP ID=========> " + visitImageShop.shop_id)
        XLog.d("VISIT DATE TIME==========> " + visitImageShop.visit_datetime)
        XLog.d("IMAGE========> " + unSyncedList[j].shop_image)
        XLog.d("=====================================================================")

        val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.visitShopWithImage(visitImageShop, unSyncedList[j].shop_image!!, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val logoutResponse = result as BaseResponse
                            XLog.d("UPLOAD REVISIT ALL IMAGE : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                            if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.shopVisitImageDao().updateisUploaded(true, unSyncedList.get(j).shop_id!!)

                                j++
                                if (j < unSyncedList.size) {
                                    BaseActivity.isShopActivityUpdating = false
                                    callShopVisitImageUploadApiForAll(unSyncedList)
                                } else {
                                    j = 0
                                    BaseActivity.isShopActivityUpdating = false

                                    //callShopDurationApi()

                                    val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()
                                    for (i in ShopActivityEntityList.indices) {
                                        if (ShopActivityEntityList[i].isDurationCalculated && ShopActivityEntityList[i].isUploaded) {

                                            var unSyncedData: List<ShopVisitAudioEntity>? = null

                                            unSyncedData = if (AppUtils.isVisitSync == "1")
                                                AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysListAccordingToShopId(ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                                            else
                                                AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                                        ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)

                                            if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                unSyncedAudioList.add(unSyncedData[0])
                                            }
                                        }
                                    }

                                    if (unSyncedAudioList.isNotEmpty()) {
                                        progress_wheel.stopSpinning()
                                        j = 0
                                        callShopVisitAudioUploadApiForAll(unSyncedAudioList)
                                    } else {
                                        (mContext as DashboardActivity).showSnackMessage("Sync Successful")
                                        progress_wheel.stopSpinning()

                                        val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                        XLog.d("=======UPDATE ADAPTER FOR SYNC ALL IMAGE (AVERAGE SHOP)=======")
                                        XLog.d("shop list size====> " + list.size)
                                        XLog.d("specific date====> $selectedDate")

                                        averageShopListAdapter.updateList(list)
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                BaseActivity.isShopActivityUpdating = false
                                (mContext as DashboardActivity).showSnackMessage(logoutResponse.message!!)
                            }
                        }, { error ->
                            XLog.d("UPLOAD REVISIT ALL IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                        })
        )
    }

    private fun callShopVisitAudioUploadApiForAll(unSyncedList: List<ShopVisitAudioEntity>) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val visitImageShop = ShopVisitImageUploadInputModel()
        visitImageShop.session_token = Pref.session_token
        visitImageShop.user_id = Pref.user_id
        visitImageShop.shop_id = unSyncedList[j].shop_id
        visitImageShop.visit_datetime = unSyncedList[j].visit_datetime

        Log.e("Average Shop", "isShopActivityUpdating=============> " + BaseActivity.isShopActivityUpdating)
        if (BaseActivity.isShopActivityUpdating)
            return

        BaseActivity.isShopActivityUpdating = true

        XLog.d("========UPLOAD REVISIT ALL AUDIO INPUT PARAMS (AVERAGE SHOP)======")
        XLog.d("USER ID======> " + visitImageShop.user_id)
        XLog.d("SESSION ID======> " + visitImageShop.session_token)
        XLog.d("SHOP ID=========> " + visitImageShop.shop_id)
        XLog.d("VISIT DATE TIME==========> " + visitImageShop.visit_datetime)
        XLog.d("AUDIO========> " + unSyncedList[j].audio)
        XLog.d("=====================================================================")

        val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.visitShopWithAudio(visitImageShop, unSyncedList[j].audio!!, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val logoutResponse = result as BaseResponse
                            XLog.d("UPLOAD REVISIT ALL AUDIO : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                            if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.shopVisitAudioDao().updateisUploaded(true, unSyncedList.get(j).shop_id!!)

                                j++
                                if (j < unSyncedList.size) {
                                    progress_wheel.stopSpinning()
                                    BaseActivity.isShopActivityUpdating = false
                                    callShopVisitAudioUploadApiForAll(unSyncedList)
                                } else {
                                    j = 0
                                    BaseActivity.isShopActivityUpdating = false
                                    (mContext as DashboardActivity).showSnackMessage("Sync Successful")
                                    progress_wheel.stopSpinning()

                                    val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                    XLog.d("=======UPDATE ADAPTER FOR SYNC ALL AUDIO (AVERAGE SHOP)=======")
                                    XLog.d("shop list size====> " + list.size)
                                    XLog.d("specific date====> $selectedDate")

                                    averageShopListAdapter.updateList(list)
                                    //callShopDurationApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                BaseActivity.isShopActivityUpdating = false
                                (mContext as DashboardActivity).showSnackMessage(logoutResponse.message!!)
                            }
                        }, { error ->
                            XLog.d("UPLOAD REVISIT ALL AUDIO : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                        })
        )
    }


    override fun onDateSelected(dateSelected: DateTime) {
        var dateTime = dateSelected.toString()
        var dateFormat = dateTime.substring(0, dateTime.indexOf('T'))
        selectedDate = dateFormat
        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(dateFormat)

        Collections.reverse(ShopActivityEntityList)

        if (ShopActivityEntityList.isNotEmpty()) {
            noShopAvailable.visibility = View.GONE
            shopList.visibility = View.VISIBLE

            XLog.d("===========INIT ADAPTER FOR SPECIFIC DATE (AVERAGE SHOP)========")
            XLog.d("shop list size====> " + ShopActivityEntityList.size)
            XLog.d("specific date====> $selectedDate")

            try {
                initAdapter()
            } catch (e: UninitializedPropertyAccessException) {
                initAdapter()
            }

        } else {
            noShopAvailable.visibility = View.VISIBLE
            shopList.visibility = View.GONE
        }

    }

    private fun syncShopActivity(shopId: String) {


        try {

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
            shopDurationData.in_location = shopActivity.in_loc
            shopDurationData.out_location = shopActivity.out_loc

            shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!
            /*10-12-2021*/
            shopDurationData.updated_by = Pref.user_id
            shopDurationData.updated_on = shopActivity.updated_on!!

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

            if (shopDataList.isEmpty()) {
                return
            }

            XLog.d("===========SYNC VISITED SHOP DATA (AVERAGE SHOP)========")
            XLog.d("SHOP ID======> " + shopDurationData.shop_id)
            XLog.d("SPENT DURATION======> " + shopDurationData.spent_duration)
            XLog.d("VISIT DATE=========> " + shopDurationData.visited_date)
            XLog.d("VISIT DATE TIME==========> " + shopDurationData.visited_date)
            XLog.d("TOTAL VISIT COUNT========> " + shopDurationData.total_visit_count)
            XLog.d("DISTANCE TRAVELLED========> " + shopDurationData.distance_travelled)
            XLog.d("FEEDBACK========> " + shopDurationData.feedback)
            XLog.d("isFirstShopVisited========> " + shopDurationData.isFirstShopVisited)
            XLog.d("distanceFromHomeLoc========> " + shopDurationData.distanceFromHomeLoc)
            XLog.d("next_visit_date========> " + shopDurationData.next_visit_date)
            XLog.d("early_revisit_reason========> " + shopDurationData.early_revisit_reason)
            XLog.d("device_model========> " + shopDurationData.device_model)
            XLog.d("android_version========> " + shopDurationData.android_version)
            XLog.d("battery========> " + shopDurationData.battery)
            XLog.d("net_status========> " + shopDurationData.net_status)
            XLog.d("net_type========> " + shopDurationData.net_type)
            XLog.d("in_time========> " + shopDurationData.in_time)
            XLog.d("out_time========> " + shopDurationData.out_time)
            XLog.d("start_timestamp========> " + shopDurationData.start_timestamp)
            XLog.d("in_location========> " + shopDurationData.in_location)
            XLog.d("out_location========> " + shopDurationData.out_location)
            XLog.d("===========================================================")

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
                                XLog.d("ShopActivityFromAverageShop : " + "User Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name + ", RESPONSE:" + result.message)
                                if (result.status == NetworkConstant.SUCCESS) {

                                    doAsync {

                                        if (!Pref.isMultipleVisitEnable)
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate)
                                        else
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate, shopActivity.startTimeStamp)

                                        /*var unSyncedList: List<ShopVisitImageModelEntity>? = null
                                    for (i in shopDataList.indices) {
                                        unSyncedList = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!, shopDataList[i].visited_date!!)
                                    }*/


                                        val unSyncedList = ArrayList<ShopVisitImageModelEntity>()

                                        if (!Pref.isMultipleVisitEnable) {
                                            for (i in shopDataList.indices) {
                                                val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!, shopDataList[i].visited_date!!)

                                                if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                    unSyncedList.add(unSyncedData[0])
                                                }
                                            }
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()

                                            if (unSyncedList.size > 0) {
                                                callShopVisitImageUploadApi(unSyncedList, false, null)
                                            } else {
                                                val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()

                                                if (!Pref.isMultipleVisitEnable) {
                                                    for (i in shopDataList.indices) {
                                                        val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!, shopDataList[i].visited_date!!)

                                                        if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                            unSyncedAudioList.add(unSyncedData[0])
                                                        }
                                                    }
                                                }

                                                if (unSyncedAudioList.isNotEmpty())
                                                    callShopVisitAudioUploadApi(unSyncedAudioList, false, null)
                                                else {
                                                    (mContext as DashboardActivity).showSnackMessage("Sync successful")
                                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                                    Collections.reverse(ShopActivityEntityList)
                                                    averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                    Collections.reverse(ShopActivityEntityList)
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                XLog.d("ShopActivityFromAverageShop : " + "User Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name + ", ERROR:" + error.localizedMessage)
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)

                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callShopVisitImageUploadApi(unSyncedList: List<ShopVisitImageModelEntity>, isAllSync: Boolean, list_: ArrayList<ShopActivityEntity>?) {

        try {

            val visitImageShop = ShopVisitImageUploadInputModel()
            visitImageShop.session_token = Pref.session_token
            visitImageShop.user_id = Pref.user_id
            visitImageShop.shop_id = unSyncedList[0].shop_id
            visitImageShop.visit_datetime = unSyncedList[0].visit_datetime

            val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()

            XLog.d("=======UPLOAD REVISIT SINGLE IMAGE INPUT PARAMS (AVERAGE SHOP)=======")
            XLog.d("USER ID======> " + visitImageShop.user_id)
            XLog.d("SESSION ID======> " + visitImageShop.session_token)
            XLog.d("SHOP ID=========> " + visitImageShop.shop_id)
            XLog.d("VISIT DATE TIME==========> " + visitImageShop.visit_datetime)
            XLog.d("IMAGE========> " + unSyncedList[0].shop_image)
            XLog.d("======================================================================")

            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.visitShopWithImage(visitImageShop, unSyncedList[0].shop_image!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val logoutResponse = result as BaseResponse
                                progress_wheel.stopSpinning()
                                XLog.d("UPLOAD REVISIT SINGLE IMAGE : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                                if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.shopVisitImageDao().updateisUploaded(true, unSyncedList[0].shop_id!!)

                                    val unSyncedAudioList = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                            ShopActivityEntityList[mPosition].shopid!!, ShopActivityEntityList[mPosition].visited_date!!)

                                    if (unSyncedAudioList != null && unSyncedAudioList.isNotEmpty()) {
                                        callShopVisitAudioUploadApi(unSyncedAudioList, false, null)
                                    }


                                } else {
                                    if (!isAllSync) {
                                        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                        Collections.reverse(ShopActivityEntityList)
                                        (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                    } else {
                                        BaseActivity.isShopActivityUpdating = false
                                        syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                    }
                                }

                            }, { error ->
                                XLog.d("UPLOAD REVISIT SINGLE IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                if (!isAllSync) {
                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                    Collections.reverse(ShopActivityEntityList)
                                    (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                } else {
                                    BaseActivity.isShopActivityUpdating = false
                                    syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                }
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callShopVisitAudioUploadApi(unSyncedAudioList: List<ShopVisitAudioEntity>, isAllSync: Boolean, list_: ArrayList<ShopActivityEntity>?) {
        try {

            val visitImageShop = ShopVisitImageUploadInputModel()
            visitImageShop.session_token = Pref.session_token
            visitImageShop.user_id = Pref.user_id
            visitImageShop.shop_id = unSyncedAudioList[0].shop_id
            visitImageShop.visit_datetime = unSyncedAudioList[0].visit_datetime

            val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()

            XLog.d("=======UPLOAD REVISIT SINGLE AUDIO INPUT PARAMS (AVERAGE SHOP)=======")
            XLog.d("USER ID======> " + visitImageShop.user_id)
            XLog.d("SESSION ID======> " + visitImageShop.session_token)
            XLog.d("SHOP ID=========> " + visitImageShop.shop_id)
            XLog.d("VISIT DATE TIME==========> " + visitImageShop.visit_datetime)
            XLog.d("AUDIO========> " + unSyncedAudioList[0].audio)
            XLog.d("======================================================================")

            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.visitShopWithAudio(visitImageShop, unSyncedAudioList[0].audio!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val logoutResponse = result as BaseResponse
                                progress_wheel.stopSpinning()
                                XLog.d("UPLOAD REVISIT SINGLE IMAGE : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                                if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.shopVisitAudioDao().updateisUploaded(true, unSyncedAudioList[0].shop_id!!)

                                    if (!isAllSync) {

                                        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                        Collections.reverse(ShopActivityEntityList)

                                        averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                                        (mContext as DashboardActivity).showSnackMessage("Sync successful")
                                    } else {
                                        BaseActivity.isShopActivityUpdating = false
                                        syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                    }
                                    /*j++
                                if (j < unSyncedList.size)
                                    callShopVisitImageUploadApi(unSyncedList)
                                else {
                                    j = 0
                                    //callShopDurationApi()
                                }*/
                                } else {
                                    if (!isAllSync) {
                                        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                        Collections.reverse(ShopActivityEntityList)
                                        (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                    } else {
                                        BaseActivity.isShopActivityUpdating = false
                                        syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                    }
                                }

                            }, { error ->
                                XLog.d("UPLOAD REVISIT SINGLE IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                if (!isAllSync) {
                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                    Collections.reverse(ShopActivityEntityList)
                                    (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                } else {
                                    BaseActivity.isShopActivityUpdating = false
                                    syncAllShopActivity(list_?.get(i)?.shopid!!, list_)
                                }
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initShopList() {
        if (AppUtils.isOnline(mContext)) {
            syncShopList()
        }
        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

        Collections.reverse(ShopActivityEntityList)

        if (ShopActivityEntityList.isNotEmpty()) {
            noShopAvailable.visibility = View.GONE

            XLog.d("===========INIT ADAPTER FOR CURRENT DATE (AVERAGE SHOP)========")
            XLog.d("shop list size====> " + ShopActivityEntityList.size)
            XLog.d("current date====> " + AppUtils.getCurrentDateForShopActi())

            initAdapter()
        } else {
            noShopAvailable.visibility = View.VISIBLE
            shopList.visibility = View.GONE
        }
    }


    private fun initAdapter() {
        averageShopListAdapter = AverageShopListAdapter(mContext, ShopActivityEntityList, object : AverageShopListClickListener {
            override fun onSyncClick(position: Int) {

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[position].shopid)

                if (shop != null) {

                    if (shop.isUploaded) {
                        checkToSyncShop(position)
                    } else {
                        syncShop(position, shop)
                    }
                }
            }

            override fun onQuestionnarieClick(shopId: String) {
                dialogOpenQa(shopId)
            }

            override fun onReturnClick(position: Int) {

//                (mContext as DashboardActivity).loadFragment(FragType.ViewAllReturnListFragment, true, ShopActivityEntityList[position].shopid!!)
                //(mContext as DashboardActivity).loadFragment(FragType.ViewAllReturnListFragment, true, ShopActivityEntityList[position].shopid!!)
                (mContext as DashboardActivity).loadFragment(FragType.ViewAllReturnListFragment, true, ShopActivityEntityList[position])
            }

            override fun onDamageClick(shop_id: String) {
                (mContext as DashboardActivity).loadFragment(FragType.ShopDamageProductListFrag, true, shop_id+"~"+Pref.user_id)
            }

            override fun onSurveyClick(shop_id: String) {
                if(Pref.isAddAttendence){
                    (mContext as DashboardActivity).loadFragment(FragType.SurveyViewFrag, true, shop_id)
                }else{
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                }
            }

            override fun onMultipleImageClick(shop: Any,position: Int) {
                if (AppUtils.isOnline(mContext)) {
                    var shopIsuploaded =AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[position].shopid).isUploaded
                    if(shopIsuploaded){
                        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[position].shopid)
                        if(Pref.IsMultipleImagesRequired){
                            (mContext as DashboardActivity).loadFragment(FragType.MultipleImageFragment, true, shop)
                        }
                    }
                    else{
                        (this as DashboardActivity).showSnackMessage("Please snyc shop First..")
                    }

                }
                else{
                    (this as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                }

            }

            override fun OnItemClick(position: Int) {
                try {
                    (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, ShopActivityEntityList[position].shopid!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun OnMenuClick(position: Int, view: View) {
                initiatePopupWindow(view, position)
            }

        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        shopList.layoutManager = layoutManager
        shopList.adapter = averageShopListAdapter
    }

    private fun syncShop(position: Int, shop: AddShopDBModelEntity) {

        try {

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

            /////////////////
            var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(shop.shop_id,false)
            addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!
//////////////////////
            addShopData.project_name = shop.project_name
            addShopData.landline_number = shop.landline_number
            addShopData.agency_name = shop.agency_name

            addShopData.alternateNoForCustomer = shop.alternateNoForCustomer
            addShopData.whatsappNoForCustomer = shop.whatsappNoForCustomer

            // duplicate shop api call
            addShopData.isShopDuplicate=shop.isShopDuplicate

            addShopData.purpose=shop.purpose

            callAddShopApi(addShopData, shop.shopImageLocalPath, shop.doc_degree, position)
            //}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, degree_imgPath: String?, position: Int) {

        try {

            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }


            progress_wheel.spin()

            XLog.d("==========SyncShop Input Params (Average Shop)============")
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
            XLog.d("====================================================")

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
                                                        progress_wheel.stopSpinning()
                                                        getAssignedPPListApi(addShop.shop_id, position)
                                                    }
                                                }
                                            }

                                        }
                                        NetworkConstant.DUPLICATE_SHOP_ID -> {
                                            XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                            AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                            //progress_wheel.stopSpinning()
                                            if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                                AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)


                                                XLog.d("=======Duplicate shop deleted from shop activity table (Average Shop)============")
                                                AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                            }
                                            doAsync {
                                                val resultAs = runLongTask(addShop.shop_id)
                                                uiThread {
                                                    if (resultAs == true) {
                                                        progress_wheel.stopSpinning()
                                                        getAssignedPPListApi(addShop.shop_id, position)
                                                    }

                                                }
                                            }
                                            //getAssignedPPListApi(addShop.shop_id, position)
                                        }
                                        else -> {
                                            progress_wheel.stopSpinning()
                                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                        }
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
                        repository.addShopWithImage(addShop, shop_imgPath, degree_imgPath, mContext)
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
                                                        progress_wheel.stopSpinning()
                                                        getAssignedPPListApi(addShop.shop_id, position)
                                                    }
                                                }
                                            }

                                        }
                                        NetworkConstant.DUPLICATE_SHOP_ID -> {
                                            XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                            AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                            //progress_wheel.stopSpinning()
                                            if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                                AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)


                                                XLog.d("=======Duplicate shop deleted from shop activity table (Average Shop)============")
                                                AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                            }
                                            doAsync {
                                                val resultAs = runLongTask(addShop.shop_id)
                                                uiThread {
                                                    if (resultAs == true) {
                                                        progress_wheel.stopSpinning()
                                                        getAssignedPPListApi(addShop.shop_id, position)
                                                    }

                                                }
                                            }
                                            //getAssignedPPListApi(addShop.shop_id, position)
                                        }
                                        else -> {
                                            progress_wheel.stopSpinning()
                                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                        }
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun runLongTask(shop_id: String?): Any {
        if (shop_id != null) {
            val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(shop_id, true, false)
            if (shopActivity != null)
                callShopActivitySubmit(shop_id)
            return true
        } else
            return false
    }

    private var shop_duration = ""
    private var startTimeStamp = ""
    private fun callShopActivitySubmit(shopId: String) {

        try {

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
                shopDurationData.updated_on = shopActivity.updated_on!!

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
                    shopDurationData.updated_on = shopActivity.updated_on!!

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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAssignedPPListApi(shop_id: String?, position: Int) {

        try {

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
                                                getAssignedDDListApi(shop_id, position)
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        getAssignedDDListApi(shop_id, position)
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedDDListApi(shop_id, position)
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                getAssignedDDListApi(shop_id, position)
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAssignedDDListApi(shop_id: String?, position: Int) {

        try {
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
                                                getAssignedToShopApi(shop_id, position)
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        getAssignedToShopApi(shop_id, position)
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedToShopApi(shop_id, position)
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                getAssignedToShopApi(shop_id, position)
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAssignedToShopApi(shop_id: String?, position: Int) {
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
                                        (mContext as DashboardActivity).showSnackMessage("Sync successful")

                                        if (!Pref.isMultipleVisitEnable)
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate)
                                        else
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate, startTimeStamp)

                                        ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                        Collections.reverse(ShopActivityEntityList)
                                        averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                                    }
                                }
                            }
                            else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Sync successful")

                                if (!Pref.isMultipleVisitEnable)
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate)
                                else
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate, startTimeStamp)

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                Collections.reverse(ShopActivityEntityList)
                                averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage("Sync successful")

                            if (!Pref.isMultipleVisitEnable)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate)
                            else
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id!!, selectedDate, startTimeStamp)

                            ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                            Collections.reverse(ShopActivityEntityList)
                            averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))
                        })
        )
    }

    private var mPosition = 0
    private fun checkToSyncShop(position: Int) {

        try {
            mPosition = position
            if (!ShopActivityEntityList[position].isUploaded)
                syncShopActivity(ShopActivityEntityList[position].shopid!!)
            else {
                val unSyncedList = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false,
                        ShopActivityEntityList[position].shopid!!, ShopActivityEntityList[position].visited_date!!)

                if (unSyncedList != null && unSyncedList.isNotEmpty()) {
                    callShopVisitImageUploadApi(unSyncedList, false, null)
                } else {
                    val unSyncedAudioList = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                            ShopActivityEntityList[position].shopid!!, ShopActivityEntityList[position].visited_date!!)

                    if (unSyncedAudioList != null && unSyncedAudioList.isNotEmpty()) {
                        callShopVisitAudioUploadApi(unSyncedAudioList, false, null)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    private fun syncAllShopActivity(shopId: String, list_: ArrayList<ShopActivityEntity>) {
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
        shopDurationData.in_location = shopActivity.in_loc
        shopDurationData.out_location = shopActivity.out_loc
        shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey


        /*10-12-2021*/
        shopDurationData.updated_by = Pref.user_id
        try{
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

        if (shopDataList.isEmpty()) {
            return
        }

        Log.e("Average Shop", "isShopActivityUpdating====> " + BaseActivity.isShopActivityUpdating)
        if (BaseActivity.isShopActivityUpdating)
            return

        BaseActivity.isShopActivityUpdating = true

        XLog.d("========SYNC ALL VISITED SHOP DATA (AVERAGE SHOP)=====")
        XLog.d("SHOP ID======> " + shopDurationData.shop_id)
        XLog.d("SPENT DURATION======> " + shopDurationData.spent_duration)
        XLog.d("VISIT DATE=========> " + shopDurationData.visited_date)
        XLog.d("VISIT DATE TIME==========> " + shopDurationData.visited_date)
        XLog.d("TOTAL VISIT COUNT========> " + shopDurationData.total_visit_count)
        XLog.d("DISTANCE TRAVELLED========> " + shopDurationData.distance_travelled)
        XLog.d("FEEDBACK========> " + shopDurationData.feedback)
        XLog.d("isFirstShopVisited========> " + shopDurationData.isFirstShopVisited)
        XLog.d("distanceFromHomeLoc========> " + shopDurationData.distanceFromHomeLoc)
        XLog.d("next_visit_date========> " + shopDurationData.next_visit_date)
        XLog.d("early_revisit_reason========> " + shopDurationData.early_revisit_reason)
        XLog.d("device_model========> " + shopDurationData.device_model)
        XLog.d("android_version========> " + shopDurationData.android_version)
        XLog.d("battery========> " + shopDurationData.battery)
        XLog.d("net_status========> " + shopDurationData.net_status)
        XLog.d("net_type========> " + shopDurationData.net_type)
        XLog.d("in_time========> " + shopDurationData.in_time)
        XLog.d("out_time========> " + shopDurationData.out_time)
        XLog.d("start_timestamp========> " + shopDurationData.start_timestamp)
        XLog.d("in_location========> " + shopDurationData.in_location)
        XLog.d("out_location========> " + shopDurationData.out_location)
        XLog.d("=======================================================")

        ////////
        revisitStatusList.clear()
        var key:String = ""
        for(i in 0..list_?.size-1){
            if(list_.get(i).shopid.equals(shopId)){
                key=list_.get(i).shop_revisit_uniqKey!!.toString()
            }
        }

        var revisitStatusObj= ShopRevisitStatusRequestData()
        var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(key)
        if(data!=null ){
            revisitStatusObj.shop_id=data.shop_id
            revisitStatusObj.order_status=data.order_status
            revisitStatusObj.order_remarks=data.order_remarks
            revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
            revisitStatusList.add(revisitStatusObj)
        }
        ///////////

        progress_wheel.spin()
        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            XLog.d("ShopActivityFromAverageShop : RESPONSE STATUS:= " + result.status + ", RESPONSE MESSAGE:= " + result.message +
                                    "\nUser Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid +
                                    ", SHOP: " + mList[0].shop_name)
                            if (result.status == NetworkConstant.SUCCESS) {


                                if(!revisitStatusList.isEmpty()){
                                    callRevisitStatusUploadApi(revisitStatusList!!)
                                }

                                callCompetetorImgUploadApi(shopId)


                                if (!Pref.isMultipleVisitEnable)
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate)
                                else
                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, selectedDate, shopActivity.startTimeStamp)

                                //
                                i++
                                if (i < list_.size) {

                                    /*val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
                                    for (i in shopDataList.indices) {
                                        val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!, shopDataList[i].visited_date!!)

                                        if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                            unSyncedList.add(unSyncedData[0])
                                        }
                                    }*/
                                    progress_wheel.stopSpinning()
                                    /*if (unSyncedList.size > 0) {
                                        callShopVisitImageUploadApi(unSyncedList, true, list_)
                                    } else {*/
                                    BaseActivity.isShopActivityUpdating = false
                                    syncAllShopActivity(list_[i].shopid!!, list_)
                                    //}

                                }
                                else {
                                    i = 0
                                    val unSyncedList = ArrayList<ShopVisitImageModelEntity>()

                                    if (!Pref.isMultipleVisitEnable) {
                                        for (i in list_.indices) {
                                            //val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, list_[i].shopid!!, list_[i].visited_date!!)

                                            var unSyncedData: List<ShopVisitImageModelEntity>? = null

                                            if (AppUtils.isVisitSync == "1") {
                                                unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysListAccordingToShopId(
                                                        list_[i].shopid!!, list_[i].visited_date!!)
                                            } else {
                                                unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(
                                                        false, list_[i].shopid!!, list_[i].visited_date!!)
                                            }

                                            if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                unSyncedList.add(unSyncedData[0])
                                            }
                                        }
                                    }

                                    progress_wheel.stopSpinning()
                                    if (unSyncedList.size > 0) {
                                        j = 0
                                        //callShopVisitImageUploadApi(unSyncedList, true, list_)
                                        BaseActivity.isShopActivityUpdating = false
                                        callShopVisitImageUploadApiForAll(unSyncedList)
                                    } else {

                                        val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()
                                        if (!Pref.isMultipleVisitEnable) {
                                            for (i in ShopActivityEntityList.indices) {
                                                if (ShopActivityEntityList[i].isDurationCalculated && ShopActivityEntityList[i].isUploaded) {

                                                    var unSyncedData: List<ShopVisitAudioEntity>? = null

                                                    unSyncedData = if (AppUtils.isVisitSync == "1")
                                                        AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysListAccordingToShopId(ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)
                                                    else
                                                        AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                                                ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)

                                                    if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                        unSyncedAudioList.add(unSyncedData[0])
                                                    }
                                                }
                                            }
                                        }

                                        if (unSyncedAudioList.isNotEmpty()) {
                                            j = 0
                                            BaseActivity.isShopActivityUpdating = false
                                            callShopVisitAudioUploadApiForAll(unSyncedAudioList)
                                        } else {
                                            BaseActivity.isShopActivityUpdating = false

                                            val dateWiseList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                            XLog.d("=======UPDATE ADAPTER FOR SYNC ALL VISIT SHOP DATA (AVERAGE SHOP)=======")
                                            XLog.d("shop list size====> " + dateWiseList.size)
                                            XLog.d("specific date====> $selectedDate")

                                            averageShopListAdapter.updateList(dateWiseList)
                                            ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                            Collections.reverse(ShopActivityEntityList)
                                        }
                                    }

                                    /*BaseActivity.isShopActivityUpdating = false
                                    averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))

                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                    Collections.reverse(ShopActivityEntityList)*/
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))
                                BaseActivity.isShopActivityUpdating = false
                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            if (error != null) {
                                XLog.d("ShopActivityFromAverageShop : ERROR:= " + error.localizedMessage + "\nUser Id" + Pref.user_id +
                                        ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name)
                                (mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityList)
                            }
                        })
        )

    }


    private fun callRevisitStatusUploadApi(revisitStatusList : MutableList<ShopRevisitStatusRequestData>){
        val revisitStatus = ShopRevisitStatusRequest()
        revisitStatus.user_id=Pref.user_id
        revisitStatus.session_token=Pref.session_token
        revisitStatus.ordernottaken_list=revisitStatusList

        val repository = ShopRevisitStatusRepositoryProvider.provideShopRevisitStatusRepository()
        compositeDisposable.add(
                repository.shopRevisitStatus(revisitStatus)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            XLog.d("callRevisitStatusUploadApi : RESPONSE " + result.status)
                            if (result.status == NetworkConstant.SUCCESS){
                                for(i in revisitStatusList.indices){
                                    AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.updateOrderStatus(revisitStatusList[i]!!.shop_revisit_uniqKey!!)
                                }

                            }
                        },{error ->
                            if (error == null) {
                                XLog.d("callRevisitStatusUploadApi : ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                            } else {
                                XLog.d("callRevisitStatusUploadApi : ERROR " + error.localizedMessage)
                                error.printStackTrace()
                            }
                        })
        )
    }


    private fun callCompetetorImgUploadApi(shop_id:String){
        //val unsynList = AppDatabase.getDBInstance()!!.shopVisitCompetetorImageDao().getUnSyncedCopetetorImg(Pref.user_id!!)
        val unsynList = AppDatabase.getDBInstance()!!.shopVisitCompetetorImageDao().getUnSyncedCopetetorImgByShopID(shop_id)
        var objCompetetor : AddShopRequestCompetetorImg = AddShopRequestCompetetorImg()

        if(unsynList == null || unsynList.size==0)
            return

        var shop_id:String

        //for(i in unsynList.indices){
        objCompetetor.session_token=Pref.session_token
        objCompetetor.shop_id=unsynList.get(0).shop_id
        objCompetetor.user_id=Pref.user_id
        objCompetetor.visited_date=unsynList.get(0).visited_date!!
        shop_id= unsynList.get(0).shop_id.toString()
        val repository = AddShopRepositoryProvider.provideAddShopRepository()
        BaseActivity.compositeDisposable.add(
                repository.addShopWithImageCompetetorImg(objCompetetor,unsynList.get(0).shop_image,mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            if(response.status==NetworkConstant.SUCCESS){
                                AppDatabase.getDBInstance()!!.shopVisitCompetetorImageDao().updateisUploaded(true,shop_id)
                                XLog.d("FUSED LOCATION : CompetetorImg" + ", SHOP: " + shop_id + ", Success: ")
                            }else{
                                XLog.d("FUSED LOCATION : CompetetorImg" + ", SHOP: " + shop_id + ", Failed: ")
                            }
                        },{
                            error ->
                            if (error != null) {
                                XLog.d("FUSED LOCATION : CompetetorImg" + ", SHOP: " + shop_id + ", ERROR: " + error.localizedMessage)
                            }
                        })
        )
        //}


    }
       private fun syncShopList() {
        val shopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false)
           if (shopList.isEmpty()){

           }
           else {
               val addShopData = AddShopRequestData()
               val mAddShopDBModelEntity = shopList[0]
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

               var uniqKeyObj = AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id, false)
               addShopData.shop_revisit_uniqKey = uniqKeyObj?.shop_revisit_uniqKey!!

               addShopData.project_name = mAddShopDBModelEntity.project_name
               if(mAddShopDBModelEntity.landline_number!=null)
               addShopData.landline_number = mAddShopDBModelEntity.landline_number
               else
                   addShopData.landline_number =""
               if(mAddShopDBModelEntity.agency_name!=null)
                addShopData.agency_name = mAddShopDBModelEntity.agency_name
               else
                   addShopData.agency_name =""

               if(mAddShopDBModelEntity.alternateNoForCustomer!=null)
                   addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
               else
                   addShopData.alternateNoForCustomer =""

               if(mAddShopDBModelEntity.whatsappNoForCustomer!=null)
                   addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer
               else
                   addShopData.whatsappNoForCustomer =""

               // duplicate shop api call
               addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate

               addShopData.purpose=mAddShopDBModelEntity.purpose


               callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shopList, true,
                       mAddShopDBModelEntity.doc_degree)

           }


    }

    fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, shopList: MutableList<AddShopDBModelEntity>?,
                       isFromInitView: Boolean, degree_imgPath: String?) {
        if (!AppUtils.isOnline(mContext)) {
            (this as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("=============SyncShop Input Params=================")
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
        XLog.d("====================================================")

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
                                            uiThread {
                                                syncShopList()
                                            }
                                        }
                                    }
                                    NetworkConstant.DUPLICATE_SHOP_ID -> {
                                        XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)


                                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                            AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                            AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                        }
                                        doAsync {
                                            uiThread {
                                                syncShopList()
                                            }
                                        }

                                    }
                                    else -> {
                                        (this as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    }
                                }
                            }, { error ->
                                error.printStackTrace()
                                (this as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
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


                                        doAsync {
                                            uiThread {
                                                syncShopList()
                                            }
                                        }
                                    }
                                    NetworkConstant.DUPLICATE_SHOP_ID -> {
                                        XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                            AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                            AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                        }
                                        doAsync {
                                            uiThread {
                                                syncShopList()
                                            }
                                        }

                                    }
                                    else -> {
                                        (this as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    }
                                }
                            }, { error ->
                                error.printStackTrace()
                                (this as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
    }

    fun dialogOpenQa(shopId:String) {
        var qsAnsByShopIDList=AppDatabase.getDBInstance()?.questionSubmitDao()?.getQsAnsByShopIDToInt(shopId!!) as ArrayList<QuestionSubmit>

        if(qsAnsByShopIDList==null || qsAnsByShopIDList.isEmpty()) {
            Toaster.msgShort(mContext, "No List Found")
            return
        }
        quesAnsList.clear()
        for(l in 0..qsAnsByShopIDList.size-1){
            if(qsAnsByShopIDList.get(l).answer!!){
                quesAnsList.add(AddShopFragment.QuestionAns(qsAnsByShopIDList.get(l).question_id!!, "1"))
            }else{
                quesAnsList.add(AddShopFragment.QuestionAns(qsAnsByShopIDList.get(l).question_id!!, "0"))
            }
        }
        rv_qaList = AppDatabase.getDBInstance()?.questionMasterDao()?.getAll() as ArrayList<QuestionEntity>
        if(rv_qaList==null || rv_qaList.isEmpty()) {
            Toaster.msgShort(mContext, "No List Found")
            return
        }

        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(true)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_qa)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_qa_headerTV) as AppCustomTextView
        val  rv_QAList = simpleDialog.findViewById(R.id.rv_qa_list) as RecyclerView
        rv_QAList.layoutManager = LinearLayoutManager(mContext)

        adapterqaList = AdapterQuestionList(mContext,quesAnsList,rv_qaList,false,object : QaOnCLick {
            override fun getQaID(qaID: String, ans: String) {
                AppDatabase.getDBInstance()?.questionSubmitDao()?.updateAnswerByQueAndShopIdNew(ans,qaID,shopId,false)
            }
        })
        rv_QAList.adapter = adapterqaList
        dialogHeader.text = "Hi " + Pref.user_name!! + "!"
        val dialogYes = simpleDialog.findViewById(R.id.dialog_qa_ok) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
        })
        simpleDialog.show()
    }


    private fun voiceAttendanceMsg(msg: String) {
        if (Pref.isVoiceEnabledForAttendanceSubmit) {
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Add Day Start", "TTS error in converting Text to Speech!");
        }
    }


}
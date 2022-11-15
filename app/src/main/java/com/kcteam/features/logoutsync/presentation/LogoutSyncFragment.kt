package com.kcteam.features.logoutsync.presentation

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.kcteam.CustomConstants
import com.elvishew.xlog.XLog

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
import com.kcteam.features.activities.api.ActivityRepoProvider
import com.kcteam.features.activities.model.*
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.billing.api.AddBillingRepoProvider
import com.kcteam.features.billing.model.AddBillingInputParamsModel
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dashboard.presentation.ReasonDialog
import com.kcteam.features.dashboard.presentation.api.ShopVisitImageUploadRepoProvider
import com.kcteam.features.dashboard.presentation.api.dashboardApi.DashboardRepoProvider
import com.kcteam.features.dashboard.presentation.model.ShopVisitImageUploadInputModel
import com.kcteam.features.document.api.DocumentRepoProvider
import com.kcteam.features.document.model.AddEditDocumentInputParams
import com.kcteam.features.document.model.DocumentAttachmentModel
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.UserLocationDataEntity
import com.kcteam.features.location.api.LocationRepoProvider
import com.kcteam.features.location.model.*
import com.kcteam.features.location.shopRevisitStatus.ShopRevisitStatusRepositoryProvider
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.logout.presentation.api.LogoutRepositoryProvider
import com.kcteam.features.nearbyshops.api.updateaddress.ShopAddressUpdateRepoProvider
import com.kcteam.features.nearbyshops.model.updateaddress.AddressUpdateRequest
import com.kcteam.features.orderhistory.activitiesapi.LocationFetchRepositoryProvider
import com.kcteam.features.orderhistory.api.LocationUpdateRepositoryProviders
import com.kcteam.features.orderhistory.model.*
import com.kcteam.features.performance.api.UpdateGpsStatusRepoProvider
import com.kcteam.features.performance.model.UpdateGpsInputParamsModel
import com.kcteam.features.quotation.api.QuotationRepoProvider
import com.kcteam.features.quotation.model.AddQuotInputModel
import com.kcteam.features.shopdetail.presentation.api.EditShopRepoProvider
import com.kcteam.features.shopdetail.presentation.api.addcollection.AddCollectionRepoProvider
import com.kcteam.features.shopdetail.presentation.model.addcollection.AddCollectionInputParamsModel
import com.kcteam.features.stock.api.StockRepositoryProvider
import com.kcteam.features.stock.model.AddStockInputParamsModel
import com.kcteam.features.stockAddCurrentStock.AddShopStockFragment
import com.kcteam.features.stockAddCurrentStock.ShopAddCurrentStockList
import com.kcteam.features.stockAddCurrentStock.ShopAddCurrentStockRequest
import com.kcteam.features.stockAddCurrentStock.api.ShopAddStockProvider
import com.kcteam.features.stockCompetetorStock.ShopAddCompetetorStockProductList
import com.kcteam.features.stockCompetetorStock.ShopAddCompetetorStockRequest
import com.kcteam.features.stockCompetetorStock.api.AddCompStockProvider
import com.kcteam.features.task.api.TaskRepoProvider
import com.kcteam.features.task.model.AddTaskInputModel
import com.kcteam.features.timesheet.api.TimeSheetRepoProvider
import com.kcteam.features.timesheet.model.AddTimeSheetInputModel
import com.kcteam.features.viewAllOrder.api.addorder.AddOrderRepoProvider
import com.kcteam.features.viewAllOrder.model.AddOrderInputParamsModel
import com.kcteam.features.viewAllOrder.model.AddOrderInputProductList
import com.kcteam.mappackage.SendBrod
import com.kcteam.widgets.AppCustomTextView
import com.kcteam.MonitorService
import com.kcteam.features.addshop.model.*
import com.kcteam.features.addshop.model.assigntopplist.AddShopUploadImg
import com.kcteam.features.login.api.LoginRepositoryProvider
import com.kcteam.features.login.model.GetConcurrentUserResponse
import com.kcteam.features.returnsOrder.ReturnProductList
import com.kcteam.features.returnsOrder.ReturnRequest
import com.kcteam.features.viewAllOrder.model.NewOrderSaveApiModel
import com.kcteam.features.viewAllOrder.orderNew.NewOrderScrActiFragment
import com.kcteam.features.viewAllOrder.orderNew.NeworderScrCartFragment
import com.elvishew.xlog.LogUtils.compress
import com.facebook.stetho.common.LogUtil
import com.google.common.util.concurrent.ListenableFuture
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_new.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.collections.ArrayList

/**
 * Created by Kinsuk on 14-01-2019.
 */
class LogoutSyncFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var addShopTickImg: AppCompatImageView
    private lateinit var addShopSyncImg: AppCompatImageView

    private lateinit var addReturnSyncImg: AppCompatImageView
    private lateinit var addReturnTickImg: AppCompatImageView

    private lateinit var addCurrentStockSyncImg: AppCompatImageView
    private lateinit var addCurrentStockTickImg: AppCompatImageView
    private lateinit var addCompetetorStockSyncImg: AppCompatImageView
    private lateinit var addCompetetorStockTickImg: AppCompatImageView
    //private lateinit var addShopRetryImg: AppCompatImageView

    private lateinit var addOrderTickImg: AppCompatImageView
    private lateinit var addOrderSyncImg: AppCompatImageView
    //private lateinit var addOrderRetryImg: AppCompatImageView

    private lateinit var collectionTickImg: AppCompatImageView
    private lateinit var collectionSyncImg: AppCompatImageView
    //private lateinit var collectionRetryImg: AppCompatImageView

    private lateinit var gpsTickImg: AppCompatImageView
    private lateinit var gpsSyncImg: AppCompatImageView
    //private lateinit var gpsRetryImg: AppCompatImageView

    private lateinit var revisitTickImg: AppCompatImageView
    private lateinit var revisitSyncImg: AppCompatImageView
    //private lateinit var revisitRetryImg: AppCompatImageView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var take_photo_tv: AppCustomTextView

    private lateinit var tv_shop_retry: AppCustomTextView
    private lateinit var tv_revisit_retry: AppCustomTextView
    private lateinit var tv_order_retry: AppCustomTextView
    private lateinit var tv_collection_retry: AppCustomTextView
    private lateinit var tv_gps_retry: AppCustomTextView
    private lateinit var tv_logout: AppCustomTextView
    private lateinit var rl_sync_main: RelativeLayout

    private lateinit var bill_sync_tv: AppCustomTextView
    private lateinit var bill_sync_img: AppCompatImageView
    private lateinit var bill_tick_img: AppCompatImageView
    private lateinit var tv_bill_retry: AppCustomTextView

    private lateinit var meeting_sync_tv: AppCustomTextView
    private lateinit var meeting_sync_img: AppCompatImageView
    private lateinit var meeting_tick_img: AppCompatImageView
    private lateinit var tv_meeting_retry: AppCustomTextView

    private lateinit var stock_tick_img: AppCompatImageView
    private lateinit var stock_sync_img: AppCompatImageView
    private lateinit var tv_stock_retry: AppCustomTextView
    private lateinit var stock_sync_tv: AppCustomTextView

    private lateinit var rl_stock: RelativeLayout
    private lateinit var rl_meeting: RelativeLayout
    private lateinit var rl_collection: RelativeLayout
    private lateinit var rl_order: RelativeLayout
    private lateinit var rl_shop: RelativeLayout
    private lateinit var rl_currentStock: RelativeLayout
    private lateinit var rl_competitorStock: RelativeLayout
    private lateinit var rl_return: RelativeLayout
    private lateinit var rl_quot: RelativeLayout
    private lateinit var rl_team: RelativeLayout
    private lateinit var rl_timesheet: RelativeLayout
    private lateinit var rl_task: RelativeLayout
    private lateinit var rl_activity: RelativeLayout
    private lateinit var rl_doc: RelativeLayout

    private lateinit var add_shop_sync_tv: AppCustomTextView

    private lateinit var quot_sync_tv: AppCustomTextView
    private lateinit var quot_sync_img: AppCompatImageView
    private lateinit var quot_tick_img: AppCompatImageView
    private lateinit var tv_quot_retry: AppCustomTextView

    private lateinit var team_sync_tv: AppCustomTextView
    private lateinit var team_sync_img: AppCompatImageView
    private lateinit var team_tick_img: AppCompatImageView
    private lateinit var tv_team_retry: AppCustomTextView

    private lateinit var timesheet_sync_tv: AppCustomTextView
    private lateinit var timesheet_sync_img: AppCompatImageView
    private lateinit var timesheet_tick_img: AppCompatImageView
    private lateinit var tv_timesheet_retry: AppCustomTextView

    private lateinit var task_sync_tv: AppCustomTextView
    private lateinit var task_sync_img: AppCompatImageView
    private lateinit var task_tick_img: AppCompatImageView
    private lateinit var tv_task_retry: AppCustomTextView

    private lateinit var activity_sync_tv: AppCustomTextView
    private lateinit var activity_sync_img: AppCompatImageView
    private lateinit var activity_tick_img: AppCompatImageView
    private lateinit var tv_activity_retry: AppCustomTextView

    private lateinit var doc_sync_tv: AppCustomTextView
    private lateinit var doc_sync_img: AppCompatImageView
    private lateinit var doc_tick_img: AppCompatImageView
    private lateinit var tv_doc_retry: AppCustomTextView

    private var i = 0

    private var isRetryShop = false
    private var isRetryVisit = false
    private var isRetryOrder = false
    private var isRetryCollection = false
    private var isBiilingEntry = false
    private var isRetryGps = false
    private var isRetryStock = false
    private var isRetryMeeting = false
    private var isRetryQuotation = false
    private var isRetryTeamShop = false
    private var isRetryTimesheet = false
    private var isRetryTask = false
    private var isRetryDocument = false
    private var shopId = ""
    private var shopVisitDate = ""
    private var previousShopVisitDateNumber = 0L

    private var reasonDialog: ReasonDialog? = null
    private var reason = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_logout_sync, container, false)
        //Pref.DayEndMarked=true
        initView(view)


        if ((mContext as DashboardActivity).isForceLogout) {
            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            val list = AppDatabase.getDBInstance()!!.addMeetingDao().durationAvailable(false)
            if (list != null) {
                for (i in 0 until list.size) {
                    val endTimeStamp = System.currentTimeMillis().toString()
                    val duration = AppUtils.getTimeFromTimeSpan(list[i].startTimeStamp!!, endTimeStamp)
                    AppDatabase.getDBInstance()!!.addMeetingDao().updateEndTimeOfMeeting(endTimeStamp, list[i].id, AppUtils.getCurrentDateForShopActi())
                    AppDatabase.getDBInstance()!!.addMeetingDao().updateTimeDurationForDayOfMeeting(list[i].id, duration, AppUtils.getCurrentDateForShopActi())
                    AppDatabase.getDBInstance()!!.addMeetingDao().updateDurationAvailable(true, list[i].id, AppUtils.getCurrentDateForShopActi())
                }
            }


            val shopActivityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
            for (i in shopActivityList.indices) {
                if (!shopActivityList[i].isDurationCalculated && shopActivityList[i].startTimeStamp != "0") {
                    Pref.durationCompletedShopId = shopActivityList[i].shopid!!
                    val endTimeStamp = System.currentTimeMillis().toString()
                    val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivityList[i].startTimeStamp, endTimeStamp)
                    val duration = AppUtils.getTimeFromTimeSpan(shopActivityList[i].startTimeStamp, endTimeStamp)

                    if (!Pref.isMultipleVisitEnable) {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivityList[i].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivityList[i].shopid!!, duration, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                    }
                    else {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivityList[i].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivityList[i].shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                    }
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(LocationWizard.getNewLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)

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
                                AppUtils.getBatteryPercentage(mContext).toString(), netStatus, netType.toString(), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                    }
                    else {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                                AppUtils.getBatteryPercentage(mContext).toString(), netStatus, netType.toString(), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                    }
//                    AppUtils.isShopVisited = false

                    Pref.isShopVisited=false
                    if (Pref.willShowShopVisitReason && totalMinute.toInt() < Pref.minVisitDurationSpentTime.toInt()) {
                        Pref.isShowShopVisitReason = true
                        showRevisitReasonDialog(shopActivityList[i].startTimeStamp)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!Pref.isShowShopVisitReason) {
            Handler().postDelayed(Runnable {
                checkToCallAddShopApi()
                //callShopProductStockApi()
                //callShopCompetetorProductStockApi()
            }, 2000)
        }
    }

    private fun showRevisitReasonDialog(startTimeStamp: String) {
        reasonDialog = null
        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(Pref.durationCompletedShopId)
        reasonDialog = ReasonDialog.getInstance(shop?.shopName!!, "You are revisiting ${Pref.shopText} but the " +
                "duration spent is less than ${Pref.minVisitDurationSpentTime} minutes. Please write the reason below.", reason) {
            reasonDialog?.dismiss()
            Pref.isShowShopVisitReason = false

            if (!Pref.isMultipleVisitEnable)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEarlyRevisitReason(it, Pref.durationCompletedShopId, AppUtils.getCurrentDateForShopActi())
            else
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEarlyRevisitReason(it, Pref.durationCompletedShopId, AppUtils.getCurrentDateForShopActi(), startTimeStamp)

            Handler().postDelayed(Runnable {
                checkToCallAddShopApi()
                //callShopProductStockApi()
                //callShopCompetetorProductStockApi()
            }, 2000)
        }
        reasonDialog?.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

/////////////////////////////////////////////////////////
    private fun callShopProductStockApi(){
    try{
        if (AppUtils.isOnline(mContext)){
            var unsyncData= AppDatabase.getDBInstance()?.shopCurrentStockEntryDao()!!.getShopStockAllUnsynced()
            if(unsyncData != null && unsyncData.isNotEmpty() && unsyncData.size!=0){
                var i=0
                //for(i in 0..unsyncData.size-1){
                    var currentStock : ShopAddCurrentStockRequest = ShopAddCurrentStockRequest()
                    currentStock.user_id=Pref.user_id
                    currentStock.session_token=Pref.session_token
                    currentStock.stock_id=unsyncData.get(i)?.stock_id
                    currentStock.shop_id= unsyncData.get(i)?.shop_id
                    currentStock.visited_datetime=unsyncData.get(i).visited_datetime

                    var currentStockProductList = AppDatabase.getDBInstance()?.shopCurrentStockProductsEntryDao()!!.getShopProductsStockAllByStockID(currentStock.stock_id.toString())
                    var productList:ArrayList<ShopAddCurrentStockList> = ArrayList()
                    for(j in 0..currentStockProductList.size-1){
                        var obj= ShopAddCurrentStockList()
                        obj.product_id=currentStockProductList.get(j).product_id
                        obj.product_stock_qty=currentStockProductList.get(j).product_stock_qty
                        productList.add(obj)
                    }
                    currentStock.stock_product_list=productList

                    val repository = ShopAddStockProvider.provideShopAddStockRepository()
                    compositeDisposable.add(
                            repository.shopAddStock(currentStock)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        XLog.d("Stock/AddCurrentStock : RESPONSE " + result.status)
                                        if (result.status == NetworkConstant.SUCCESS){
                                            AppDatabase.getDBInstance()?.shopCurrentStockEntryDao()!!.syncShopStocktable(currentStock.stock_id.toString())
                                            AppDatabase.getDBInstance()?.shopCurrentStockProductsEntryDao()!!.syncShopProductsStock(currentStock?.stock_id.toString())
                                            /*if(i == unsyncData.size-1){
                                                callShopCompetetorProductStockApi()
                                            }*/
                                            callShopProductStockApi()
                                        }
                                    },{error ->
                                        if (error == null) {
                                            XLog.d("Stock/AddCurrentStock : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
                                        } else {
                                            XLog.d("Stock/AddCurrentStock : ERROR " + error.localizedMessage)
                                            error.printStackTrace()
                                        }
                                        callShopCompetetorProductStockApi()
                                    })
                    )
                //}
            }else{
                callShopCompetetorProductStockApi()
            }
        }else{
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            }
        }catch (ex:Exception){
            XLog.d("LogoutSync : Stock/AddCurrentStock : ERROR " )
        callShopCompetetorProductStockApi()
        }

    }

    private fun callShopCompetetorProductStockApi(){
        stopAnimation(addCurrentStockSyncImg)
        addCurrentStockSyncImg.visibility=View.GONE
        addCurrentStockTickImg.visibility=View.VISIBLE
        try{
            var currentStock : ShopAddCompetetorStockRequest = ShopAddCompetetorStockRequest()
            var unsyncData= AppDatabase.getDBInstance()?.competetorStockEntryDao()!!.getShopCompetetorStockAllUnsynced()
            if(unsyncData != null && unsyncData.isNotEmpty() && unsyncData.size!=0){
                var i=0
                //for(i in 0..unsyncData.size-1){
                    currentStock.user_id=Pref.user_id
                    currentStock.session_token=Pref.session_token
                    currentStock.shop_id=unsyncData?.get(i).shop_id
                    currentStock.visited_datetime=unsyncData?.get(i).visited_datetime
                    currentStock.competitor_stock_id=unsyncData?.get(i).competitor_stock_id

                    var currentProductStockList= AppDatabase.getDBInstance()?.competetorStockEntryProductDao()?.getComProductStockByStockIDUnsynced(currentStock?.competitor_stock_id.toString())
                    var productList:MutableList<ShopAddCompetetorStockProductList> = ArrayList()
                    for(j in 0..currentProductStockList!!.size-1){
                        var obj= ShopAddCompetetorStockProductList()
                        obj.brand_name=currentProductStockList.get(j).brand_name
                        obj.product_name=currentProductStockList.get(j).product_name
                        obj.qty=currentProductStockList.get(j).qty
                        obj.mrp=currentProductStockList.get(j).mrp
                        productList.add(obj)
                    }
                    currentStock.competitor_stock_list=productList

                    val repository = AddCompStockProvider.provideCompStockRepositiry()
                    compositeDisposable.add(
                            repository.addCompStock(currentStock)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        XLog.d("CompetitorStock/AddCompetitorStock : RESPONSE " + result.status)
                                        if (result.status == NetworkConstant.SUCCESS){
                                            AppDatabase.getDBInstance()?.competetorStockEntryDao()?.syncShopCompStocktable(currentStock?.competitor_stock_id.toString())
                                            AppDatabase.getDBInstance()?.competetorStockEntryProductDao()?.syncShopCompProductable(currentStock?.competitor_stock_id.toString())
                                            /*if(i == unsyncData.size-1){
                                                checkToCallAddShopApi()
                                            }*/
                                            callShopCompetetorProductStockApi()
                                        }
                                    },{error ->
                                        if (error == null) {
                                            XLog.d("CompetitorStock/AddCompetitorStock : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
                                        } else {
                                            XLog.d("CompetitorStock/AddCompetitorStock : ERROR " + error.localizedMessage)
                                            error.printStackTrace()
                                        }
                                        //checkToCallActivity()
                                        syncNewOrderScr()
                                    })
                    )
                //}
            }else{
                //checkToCallAddShopApi()


                //checkToCallActivity()
                syncNewOrderScr()
            }
        }catch (ex:Exception){
            //checkToCallAddShopApi()


            //checkToCallActivity()
            syncNewOrderScr()
        }
    }


    data class NewOrderRoomDataLogoutPurpose(var order_id: String, var shop_id: String,var order_date:String)
    //08-09-2021
    private fun syncNewOrderScr(){
        try{
            var newOrderRoomDataList:ArrayList<NeworderScrCartFragment.NewOrderRoomData> = ArrayList()
            var unsyncList=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getUnSyncOrderAll()
/////////////////////////////////

            var unsyncListDistOrderID=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getUnSyncOrderAllUniqOrderID()
            var newOrderSaveApiModel: NewOrderSaveApiModel=NewOrderSaveApiModel()
            if(unsyncListDistOrderID != null && unsyncListDistOrderID.isNotEmpty() && unsyncListDistOrderID.size!=0){

                newOrderSaveApiModel.user_id=Pref.user_id
                newOrderSaveApiModel.session_token=Pref.session_token
                newOrderSaveApiModel.order_id=unsyncListDistOrderID.get(0).order_id
                newOrderSaveApiModel.shop_id=unsyncListDistOrderID.get(0).shop_id
                newOrderSaveApiModel.order_date=unsyncListDistOrderID.get(0).order_date


                var unsyncListttt=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getUnSyncOrderAllByOrdID(unsyncListDistOrderID!!.get(0).order_id)
                var newOrderRoomDataListttt:ArrayList<NeworderScrCartFragment.NewOrderRoomData> = ArrayList()

                for(l in 0..unsyncListttt!!.size-1){
                    var newOrderRoomDataa= NeworderScrCartFragment.NewOrderRoomData(unsyncListttt!!.get(l).order_id!!,
                            unsyncListttt!!.get(l).product_id!!,
                            unsyncListttt!!.get(l).product_name!!, unsyncList!!.get(l).gender!!,
                            unsyncListttt!!.get(l).color_id!!, unsyncList!!.get(l).color_name!!,
                            unsyncListttt!!.get(l).size!!,
                            unsyncListttt!!.get(l).qty!!,unsyncListttt!!.get(l).rate!!)
                    newOrderRoomDataListttt.add(newOrderRoomDataa)
                }
                newOrderSaveApiModel.product_list=newOrderRoomDataListttt



                /////
                val addOrder = AddOrderInputParamsModel()
                addOrder.collection = ""
                addOrder.description = ""
                addOrder.order_amount = "0"
                addOrder.order_date = AppUtils.getCurrentISODateTime()
                addOrder.order_id = newOrderSaveApiModel.order_id
                addOrder.shop_id = newOrderSaveApiModel.shop_id!!
                addOrder.session_token = Pref.session_token
                addOrder.user_id = Pref.user_id
                addOrder.latitude = Pref.latitude
                addOrder.longitude = Pref.longitude

                addOrder.patient_name = ""
                addOrder.patient_address = ""
                addOrder.patient_no = ""
                addOrder.remarks = ""

                if (!TextUtils.isEmpty(Pref.latitude) && !TextUtils.isEmpty(Pref.longitude))
                    addOrder.address = LocationWizard.getLocationName(mContext, Pref.latitude!!.toDouble(), Pref.longitude!!.toDouble())
                else
                    addOrder.address = ""

                val productList = ArrayList<AddOrderInputProductList>()
                for(i in 0..newOrderRoomDataListttt.size-1){
                    val product = AddOrderInputProductList()
                    product.id=newOrderRoomDataListttt.get(i).product_id!!
                    product.product_name=newOrderRoomDataListttt.get(i).product_name!!
                    product.qty=newOrderRoomDataListttt.get(i).qty!!
                    product.rate="0"
                    product.total_price="0"
                    productList.add(product)
                }

                addOrder.product_list=productList

                ///////


                val repository = AddOrderRepoProvider.provideAddOrderRepository()
                BaseActivity.compositeDisposable.add(
                        repository.addOrderNewOrderScr(newOrderSaveApiModel)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    XLog.d("NewOrderScrCartFrag OrderWithProductAttribute/OrderWithProductAttribute : RESPONSE " + result.status)
                                    if (result.status == NetworkConstant.SUCCESS){

                                        doAsync {

                                            AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.syncNewOrder(newOrderSaveApiModel.order_id.toString())

                                            uiThread {
                                                updateSecondaryOrderApi(addOrder)
                                                //syncNewOrderScr()
                                            }
                                        }
                                    }
                                },{error ->
                                    if (error == null) {
                                        XLog.d("NewOrderScrCartFrag OrderWithProductAttribute/OrderWithProductAttribute : ERROR " )
                                    } else {
                                        XLog.d("NewOrderScrCartFrag OrderWithProductAttribute/OrderWithProductAttribute : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
                                    //checkToCallActivity()
                                    syncQuesSubmit()
                                })
                )
            } else{
                //checkToCallActivity()
                syncQuesSubmit()
            }


            /*if(unsyncList != null && unsyncList.isNotEmpty() && unsyncList.size!=0){

                var newOrderRoomData= NeworderScrCartFragment.NewOrderRoomData(unsyncList.get(0).order_id!!,
                        unsyncList.get(0).product_id!!,
                        unsyncList.get(0).product_name!!, unsyncList.get(0).gender!!,
                        unsyncList.get(0).color_id!!, unsyncList.get(0).color_name!!,
                        unsyncList.get(0).size!!,
                        unsyncList.get(0).qty!!)
                newOrderRoomDataList.add(newOrderRoomData)

                var newOrderSaveApiModel: NewOrderSaveApiModel = NewOrderSaveApiModel()
                newOrderSaveApiModel.user_id=Pref.user_id
                newOrderSaveApiModel.session_token=Pref.session_token
                newOrderSaveApiModel.order_id=unsyncList.get(0).order_id
                newOrderSaveApiModel.shop_id= unsyncList.get(0).shop_id
                newOrderSaveApiModel.order_date=AppUtils.getCurrentDateyymmdd()
                newOrderSaveApiModel.product_list=newOrderRoomDataList

                val repository = AddOrderRepoProvider.provideAddOrderRepository()
                BaseActivity.compositeDisposable.add(
                        repository.addOrderNewOrderScr(newOrderSaveApiModel)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    XLog.d("NewOrderScrCartFrag OrderWithProductAttribute/OrderWithProductAttribute : RESPONSE " + result.status)
                                    if (result.status == NetworkConstant.SUCCESS){

                                        doAsync {

                                            AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.syncNewOrderComplex(unsyncList.get(0).order_id!!,unsyncList.get(0).product_id!!,
                                                    unsyncList.get(0).gender!!,unsyncList.get(0).size!!,unsyncList.get(0).qty!!,unsyncList.get(0).shop_id!!,
                                                    unsyncList.get(0).color_id!!)

                                            uiThread {
                                                syncNewOrderScr()
                                            }
                                        }
                                    }
                                },{error ->
                                    if (error == null) {
                                        XLog.d("NewOrderScrCartFrag OrderWithProductAttribute/OrderWithProductAttribute : ERROR " )
                                    } else {
                                        XLog.d("NewOrderScrCartFrag OrderWithProductAttribute/OrderWithProductAttribute : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
                                    checkToCallActivity()
                                })
                )

            }
            else{
                checkToCallActivity()
            }*/
        }catch (ex:Exception){
            ex.printStackTrace()
            //checkToCallActivity()
            syncQuesSubmit()
        }
    }




    //8-12-2021
    private fun syncQuesSubmit(){
        try{
            var questionSubmit : AddQuestionSubmitRequestData = AddQuestionSubmitRequestData()

            var uniqUnsyncShopID=AppDatabase.getDBInstance()?.questionSubmitDao()?.getUnSyncUniqShopID(false)

            if(uniqUnsyncShopID != null && uniqUnsyncShopID.isNotEmpty() && uniqUnsyncShopID.size!=0){

                questionSubmit.user_id=Pref.user_id
                questionSubmit.session_token=Pref.session_token
                questionSubmit.shop_id=uniqUnsyncShopID?.get(0)

                var questionList=AppDatabase.getDBInstance()?.questionSubmitDao()?.getQsAnsByShopID(questionSubmit.shop_id!!,false) as ArrayList<QuestionSubmit>
                questionSubmit.Question_list=questionList

                val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
                BaseActivity.compositeDisposable.add(
                        repository.addQues(questionSubmit)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val questionSubmitResponse= result as BaseResponse
                                    XLog.d("QuestionSubmit : RESPONSE " + result.status)
                                    if (result.status == NetworkConstant.SUCCESS){

                                        doAsync {
                                            AppDatabase.getDBInstance()!!.questionSubmitDao().updateIsUploaded(true,questionSubmit.shop_id!!)
                                            uiThread {
                                                syncQuesSubmit()
                                            }
                                        }
                                    }
                                },{error ->
                                    if (error == null) {
                                        XLog.d("QuestionSubmit : ERROR " )
                                    } else {
                                        XLog.d("QuestionSubmit : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
                                    syncUpdatedQuesSubmit()
//                                    checkToCallActivity()
                                })
                )
            }else{
                syncUpdatedQuesSubmit()
//                checkToCallActivity()
            }
        }catch (ex:Exception){
            XLog.d("QuestionSubmit : ERROR " + ex.toString())
            ex.printStackTrace()
            syncUpdatedQuesSubmit()
//            checkToCallActivity()
        }
    }


    private fun syncUpdatedQuesSubmit(){
        try{
            var questionSubmit : AddQuestionSubmitRequestData = AddQuestionSubmitRequestData()

            var uniqUnsyncShopID=AppDatabase.getDBInstance()?.questionSubmitDao()?.getUnSyncUpdatedUniqShopID(false)

            if(uniqUnsyncShopID != null && uniqUnsyncShopID.isNotEmpty() && uniqUnsyncShopID.size!=0){

                questionSubmit.user_id=Pref.user_id
                questionSubmit.session_token=Pref.session_token
                questionSubmit.shop_id=uniqUnsyncShopID?.get(0)

                var questionList=AppDatabase.getDBInstance()?.questionSubmitDao()?.getQsAnsUpdatedByShopID(questionSubmit.shop_id!!,false) as ArrayList<QuestionSubmit>
                questionSubmit.Question_list=questionList

                val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
                BaseActivity.compositeDisposable.add(
                        repository.addQuesUpdate(questionSubmit)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val questionSubmitResponse= result as BaseResponse
                                    XLog.d("QuestionSubmit : RESPONSE " + result.status)
                                    if (result.status == NetworkConstant.SUCCESS){

                                        doAsync {
                                            AppDatabase.getDBInstance()!!.questionSubmitDao().updateIsUpdateUploaded(true,questionSubmit.shop_id!!)
                                            uiThread {
                                                syncUpdatedQuesSubmit()
                                            }
                                        }
                                    }
                                },{error ->
                                    if (error == null) {
                                        XLog.d("QuestionSubmit : ERROR " )
                                    } else {
                                        XLog.d("QuestionSubmit : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
                                    addShopSeconaryUploadImg()
//                                    checkToCallActivity()
                                })
                )
            }else{
                addShopSeconaryUploadImg()
//                checkToCallActivity()
            }
        }catch (ex:Exception){
            XLog.d("QuestionSubmit : ERROR " + ex.toString())
            ex.printStackTrace()
            addShopSeconaryUploadImg()
//            checkToCallActivity()
        }
    }

    /*9-12-2021*/
    data class SecondaryShopImg1(var lead_shop_id:String,var rubylead_image1 :String)
    private fun addShopSeconaryUploadImg() {

        println("sec-image addShopSeconaryUploadImg")
        var objCompetetor: AddShopUploadImg = AddShopUploadImg()
        var secondaryImgShopID=AppDatabase.getDBInstance()?.addShopSecondaryImgDao()!!.getUnsnycShopImage1(false) as ArrayList<SecondaryShopImg1>

        if(secondaryImgShopID != null && secondaryImgShopID.isNotEmpty() && secondaryImgShopID.size!=0){
            var shopId=secondaryImgShopID.get(0).lead_shop_id
            var imagePathupload=secondaryImgShopID.get(0).rubylead_image1

            objCompetetor.session_token = Pref.session_token!!
            objCompetetor.lead_shop_id = shopId
            objCompetetor.user_id = Pref.user_id

            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImageuploadImg1(objCompetetor, imagePathupload, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                println("sec-image addShopSeconaryUploadImg "+response.status.toString())
                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopSecondaryImgDao().updateisUploaded1(true, shopId)
                                    addShopSeconaryUploadImg()
                                    XLog.d("AddShop : Img1" + ", SHOP: " + shopId + ", Success: ")
                                } else {
                                    XLog.d("AddShop : Img1" + ", SHOP: " + shopId + ", Failed: ")
                                    checkToCallActivity()
                                }
                            }, { error ->
                                println("sec-image addShopSeconaryUploadImg error")
                                if (error != null) {
                                    XLog.d("AddShop : Img1" + ", SHOP: " + shopId + ", ERROR: " + error.localizedMessage)
                                }
                                checkToCallActivity()
                            })
            )

        }else{
            addShopSeconaryUploadImg2()
        }

    }

    data class SecondaryShopImg2(var lead_shop_id:String,var rubylead_image2 :String)
    private fun addShopSeconaryUploadImg2() {
        println("sec-image addShopSeconaryUploadImg2")
        var objCompetetor: AddShopUploadImg = AddShopUploadImg()
        var secondaryImgShopID2=AppDatabase.getDBInstance()?.addShopSecondaryImgDao()!!.getUnsnycShopImage2(false) as ArrayList<SecondaryShopImg2>

        if(secondaryImgShopID2 != null && secondaryImgShopID2.isNotEmpty() && secondaryImgShopID2.size!=0){
            var shopId=secondaryImgShopID2.get(0).lead_shop_id
            var imagePathupload2=secondaryImgShopID2.get(0).rubylead_image2

            objCompetetor.session_token = Pref.session_token
            objCompetetor.lead_shop_id = shopId
            objCompetetor.user_id = Pref.user_id

            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImageuploadImg2(objCompetetor, imagePathupload2, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                println("sec-image addShopSeconaryUploadImg2 "+response.status.toString())
                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopSecondaryImgDao().updateisUploaded2(true, shopId)
                                    addShopSeconaryUploadImg2()
                                    XLog.d("AddShop : Img2" + ", SHOP: " + shopId + ", Success: ")
                                } else {
                                    XLog.d("AddShop : Img2" + ", SHOP: " + shopId + ", Failed: ")
                                    /*call return*/
                                    callReturnApi()
//                                    checkToCallActivity()
                                }
                            }, { error ->
                                println("sec-image addShopSeconaryUploadImg2 error")
                                if (error != null) {
                                    XLog.d("AddShop : Img2" + ", SHOP: " + shopId + ", ERROR: " + error.localizedMessage)
                                }
                                /*call return*/
                                callReturnApi()
//                                checkToCallActivity()
                            })
            )
        }else{
            /*call return*/
            callReturnApi()
//            checkToCallActivity()
        }

    }

    ////*
    private fun updateSecondaryOrderApi(addOrder:AddOrderInputParamsModel){
        val repository = AddOrderRepoProvider.provideAddOrderRepository()
        BaseActivity.compositeDisposable.add(
                repository.addNewOrder(addOrder)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val orderList = result as BaseResponse
                            if (orderList.status == NetworkConstant.SUCCESS) {
                                syncNewOrderScr()
                            }
                            //(mContext as DashboardActivity).showSnackMessage("Order added successfully")

                        }, { error ->
                            error.printStackTrace()
                            //(mContext as DashboardActivity).showSnackMessage("Something went wrong.")
                            //(mContext as DashboardActivity).showSnackMessage("Order added successfully")
                            XLog.d("LogoutSync OrderWithProductAttribute/updateSecondaryOrderApi : ERROR "+ error.toString())
                            syncNewOrderScr()
                        })
        )
    }




///////////////////////////////////////////////

    private fun checkToCallAddShopApi() {

        stopAnimation(addCompetetorStockSyncImg)
        addCompetetorStockSyncImg.visibility=View.GONE
        addCompetetorStockTickImg.visibility=View.VISIBLE
        if (Pref.isShopAddEditAvailable) {
            if (AppUtils.isOnline(mContext)) {

                val shopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false)

                if (shopList != null && shopList.isNotEmpty()) {
                    i = 0
                    syncShop(shopList[i], shopList)
                } else {
                    checkToCallSyncEditShop()
                }

            } else
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        } else
            checkToCallSyncOrder()
    }

    private fun initView(view: View) {

        take_photo_tv = view.findViewById(R.id.take_photo_tv)

        addShopTickImg = view.findViewById(R.id.add_shop_tick_img)
        addShopSyncImg = view.findViewById(R.id.add_shop_sync_img)
        addReturnSyncImg = view.findViewById(R.id.add_return_sync_img)
        addReturnTickImg = view.findViewById(R.id.add_return_tick_img)
        addCurrentStockSyncImg = view.findViewById(R.id.add_current_stock_sync_img)
        addCurrentStockTickImg = view.findViewById(R.id.add_current_stock_tick_img)
        addCompetetorStockSyncImg = view.findViewById(R.id.add_competetor_stock_sync_img)
        addCompetetorStockTickImg = view.findViewById(R.id.add_competetor_stock_tick_img)
        //addShopRetryImg = view.findViewById(R.id.retry_add_shop_img)

        addOrderTickImg = view.findViewById(R.id.add_order_tick_img)
        addOrderSyncImg = view.findViewById(R.id.add_order_sync_img)
        //addOrderRetryImg = view.findViewById(R.id.retry_add_order_img)

        collectionTickImg = view.findViewById(R.id.collection_tick_img)
        collectionSyncImg = view.findViewById(R.id.collection_sync_img)
        //collectionRetryImg = view.findViewById(R.id.retry_collection_img)

        gpsTickImg = view.findViewById(R.id.gps_tick_img)
        gpsSyncImg = view.findViewById(R.id.gps_sync_img)
        //gpsRetryImg = view.findViewById(R.id.retry_gps_img)

        revisitTickImg = view.findViewById(R.id.revisit_tick_img)
        revisitSyncImg = view.findViewById(R.id.revisit_sync_img)
        //revisitRetryImg = view.findViewById(R.id.retry_revisit_img)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        tv_shop_retry = view.findViewById(R.id.tv_shop_retry)
        tv_revisit_retry = view.findViewById(R.id.tv_revisit_retry)
        tv_order_retry = view.findViewById(R.id.tv_order_retry)
        tv_collection_retry = view.findViewById(R.id.tv_collection_retry)
        tv_gps_retry = view.findViewById(R.id.tv_gps_retry)
        tv_logout = view.findViewById(R.id.tv_logout)
        rl_sync_main = view.findViewById(R.id.rl_sync_main)
        tv_logout.isEnabled = false

        bill_sync_tv = view.findViewById(R.id.bill_sync_tv)
        bill_sync_img = view.findViewById(R.id.bill_sync_img)
        bill_tick_img = view.findViewById(R.id.bill_tick_img)
        tv_bill_retry = view.findViewById(R.id.tv_bill_retry)


        meeting_sync_tv = view.findViewById(R.id.meeting_sync_tv)
        meeting_sync_img = view.findViewById(R.id.meeting_sync_img)
        meeting_tick_img = view.findViewById(R.id.meeting_tick_img)
        tv_meeting_retry = view.findViewById(R.id.tv_meeting_retry)

        stock_tick_img = view.findViewById(R.id.stock_tick_img)
        stock_sync_img = view.findViewById(R.id.stock_sync_img)
        tv_stock_retry = view.findViewById(R.id.tv_stock_retry)
        stock_sync_tv = view.findViewById(R.id.stock_sync_tv)

        rl_stock = view.findViewById(R.id.rl_stock)
        rl_meeting = view.findViewById(R.id.rl_meeting)
        rl_shop = view.findViewById(R.id.rl_shop)
        rl_currentStock = view.findViewById(R.id.rl_current_stock)
        rl_competitorStock = view.findViewById(R.id.rl_competetor_stock)
        rl_return =  view.findViewById(R.id.rl_return)
        rl_order = view.findViewById(R.id.rl_order)
        rl_collection = view.findViewById(R.id.rl_collection)
        rl_quot = view.findViewById(R.id.rl_quot)
        rl_team = view.findViewById(R.id.rl_team)
        rl_timesheet = view.findViewById(R.id.rl_timesheet)
        rl_task = view.findViewById(R.id.rl_task)
        rl_activity = view.findViewById(R.id.rl_activity)
        rl_doc = view.findViewById(R.id.rl_doc)

        add_shop_sync_tv = view.findViewById(R.id.add_shop_sync_tv)


        quot_sync_tv = view.findViewById(R.id.quot_sync_tv)
        quot_sync_img = view.findViewById(R.id.quot_sync_img)
        quot_tick_img = view.findViewById(R.id.quot_tick_img)
        tv_quot_retry = view.findViewById(R.id.tv_quot_retry)

        team_sync_tv = view.findViewById(R.id.team_sync_tv)
        team_sync_img = view.findViewById(R.id.team_sync_img)
        team_tick_img = view.findViewById(R.id.team_tick_img)
        tv_team_retry = view.findViewById(R.id.tv_team_retry)

        timesheet_sync_tv = view.findViewById(R.id.timesheet_sync_tv)
        timesheet_sync_img = view.findViewById(R.id.timesheet_sync_img)
        timesheet_tick_img = view.findViewById(R.id.timesheet_tick_img)
        tv_timesheet_retry = view.findViewById(R.id.tv_timesheet_retry)

        task_sync_tv = view.findViewById(R.id.task_sync_tv)
        task_sync_img = view.findViewById(R.id.task_sync_img)
        task_tick_img = view.findViewById(R.id.task_tick_img)
        tv_task_retry = view.findViewById(R.id.tv_task_retry)

        activity_sync_tv = view.findViewById(R.id.activity_sync_tv)
        activity_sync_img = view.findViewById(R.id.activity_sync_img)
        activity_tick_img = view.findViewById(R.id.activity_tick_img)
        tv_activity_retry = view.findViewById(R.id.tv_activity_retry)

        doc_sync_tv = view.findViewById(R.id.doc_sync_tv)
        doc_sync_img = view.findViewById(R.id.doc_sync_img)
        doc_tick_img = view.findViewById(R.id.doc_tick_img)
        tv_doc_retry = view.findViewById(R.id.tv_doc_retry)

        /*if (Pref.isReplaceShopText)
            add_shop_sync_tv.text = getString(R.string.customers)
        else
            add_shop_sync_tv.text = getString(R.string.shops)*/

        team_sync_tv.text = "Team " + Pref.shopText + "(s)"
        add_shop_sync_tv.text = Pref.shopText + "(s)"

        rl_stock.apply {
            visibility = if (Pref.willStockShow)
                View.VISIBLE
            else
                View.GONE
        }

        meeting_sync_tv.text = Pref.meetingText


        rl_meeting.apply {
            visibility = if (Pref.isMeetingAvailable)
                View.VISIBLE
            else
                View.GONE
        }

        rl_shop.apply {
            visibility = if (Pref.isShopAddEditAvailable)
                View.VISIBLE
            else
                View.GONE
        }

        rl_currentStock.apply {
            visibility = if (Pref.isCurrentStockEnable)
                View.VISIBLE
            else
                View.GONE
        }

        /*17-12-2021*/
        rl_return.apply {
            visibility = if (Pref.IsReturnEnableforParty)
                View.VISIBLE
            else
                View.GONE
        }

        rl_competitorStock.apply {
            visibility = if (Pref.IscompetitorStockRequired)
                View.VISIBLE
            else
                View.GONE
        }

        rl_order.apply {
            visibility = if (Pref.isOrderShow)
                View.VISIBLE
            else
                View.GONE
        }

        rl_collection.apply {
            visibility = if (Pref.isCollectioninMenuShow)
                View.VISIBLE
            else
                View.GONE
        }

        rl_quot.apply {
            visibility = if (Pref.isQuotationShow)
                View.VISIBLE
            else
                View.GONE
        }

        rl_team.apply {
            visibility = if (Pref.isOfflineTeam)
                View.VISIBLE
            else
                View.GONE
        }

        rl_timesheet.apply {
            visibility = if (Pref.willTimesheetShow)
                View.VISIBLE
            else
                View.GONE
        }

        rl_task.apply {
            visibility = if (Pref.isTaskEnable)
                View.VISIBLE
            else
                View.GONE
        }

        rl_activity.apply {
            visibility = if (Pref.willActivityShow)
                View.VISIBLE
            else
                View.GONE
        }

        rl_doc.apply {
            visibility = if (Pref.isDocumentRepoShow)
                View.VISIBLE
            else
                View.GONE
        }

        /*addShopRetryImg.setOnClickListener(this)
        addOrderRetryImg.setOnClickListener(this)
        collectionRetryImg.setOnClickListener(this)
        gpsRetryImg.setOnClickListener(this)
        revisitRetryImg.setOnClickListener(this)*/
        tv_shop_retry.setOnClickListener(this)
        tv_revisit_retry.setOnClickListener(this)
        tv_order_retry.setOnClickListener(this)
        tv_collection_retry.setOnClickListener(this)
        tv_gps_retry.setOnClickListener(this)
        tv_bill_retry.setOnClickListener(this)
        tv_meeting_retry.setOnClickListener(this)
        tv_logout.setOnClickListener(this)
        rl_sync_main.setOnClickListener(null)
        tv_stock_retry.setOnClickListener(this)
        tv_quot_retry.setOnClickListener(this)
        tv_team_retry.setOnClickListener(this)
        tv_timesheet_retry.setOnClickListener(this)
        tv_task_retry.setOnClickListener(this)
        tv_activity_retry.setOnClickListener(this)
        tv_doc_retry.setOnClickListener(this)

        animateSyncImage(addShopSyncImg)
        animateSyncImage(addCurrentStockSyncImg)
        animateSyncImage(addCompetetorStockSyncImg)
        animateSyncImage(addOrderSyncImg)
        animateSyncImage(collectionSyncImg)
        animateSyncImage(gpsSyncImg)
        animateSyncImage(revisitSyncImg)
        animateSyncImage(bill_sync_img)
        animateSyncImage(meeting_sync_img)
        animateSyncImage(stock_sync_img)
        animateSyncImage(quot_sync_img)
        animateSyncImage(team_sync_img)
        animateSyncImage(timesheet_sync_img)
        animateSyncImage(task_sync_img)
        animateSyncImage(activity_sync_img)
        animateSyncImage(doc_sync_img)
        animateSyncImage(addReturnSyncImg)

        if ((mContext as DashboardActivity).isChangedPassword)
            tv_logout.visibility = View.GONE
        else {
            if ((mContext as DashboardActivity).isClearData)
                tv_logout.visibility = View.GONE
            else
                tv_logout.visibility = View.VISIBLE
        }
    }


    private fun syncShop(addShopDBModelEntity: AddShopDBModelEntity, shopList: MutableList<AddShopDBModelEntity>) {
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


        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id,false)
        addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.agency_name)) {
            addShopData.agency_name = mAddShopDBModelEntity.agency_name
        }
        else {
            addShopData.agency_name = mAddShopDBModelEntity.ownerName
        }
        if (!TextUtils.isEmpty(mAddShopDBModelEntity.lead_contact_number)) {
            addShopData.lead_contact_number = mAddShopDBModelEntity.lead_contact_number
        }
        else {
            addShopData.lead_contact_number = mAddShopDBModelEntity.ownerContactNumber
        }

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.project_name))
            addShopData.project_name=mAddShopDBModelEntity.project_name
        else
            addShopData.project_name=""

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.landline_number))
            addShopData.landline_number=mAddShopDBModelEntity.landline_number
        else
            addShopData.landline_number=""

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.alternateNoForCustomer))
            addShopData.alternateNoForCustomer=mAddShopDBModelEntity.alternateNoForCustomer
        else
            addShopData.alternateNoForCustomer=""

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.whatsappNoForCustomer))
            addShopData.whatsappNoForCustomer=mAddShopDBModelEntity.whatsappNoForCustomer
        else
            addShopData.whatsappNoForCustomer=""

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate

        addShopData.purpose=mAddShopDBModelEntity.purpose

        addShopData.GSTN_Number=mAddShopDBModelEntity.gstN_Number
        addShopData.ShopOwner_PAN=mAddShopDBModelEntity.shopOwner_PAN


        callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, mAddShopDBModelEntity.doc_degree, shopList)
        //callAddShopApi(addShopData, "")


    }

    //===================================Add Shop============================================//
    fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, degree_imgPath: String?, shopList: MutableList<AddShopDBModelEntity>) {
        /*if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }*/


        //(mContext as DashboardActivity).showSnackMessage("Syncing Shops")

        progress_wheel.spin()

        XLog.d("=======SyncShop Input Params (Logout sync)======")
        XLog.d("shop id====> " + addShop.shop_id)
        val index = addShop.shop_id!!.indexOf("_")
        XLog.d("decoded shop id====> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
        XLog.d("shop added date====> " + addShop.added_date)
        XLog.d("shop address====> " + addShop.address)
        XLog.d("assigned to dd id=====> " + addShop.assigned_to_dd_id)
        XLog.d("assigned to pp id=====> " + addShop.assigned_to_pp_id)
        XLog.d("date aniversery====> " + addShop.date_aniversary)
        XLog.d("dob====> " + addShop.dob)
        XLog.d("shop owner phn no====> " + addShop.owner_contact_no)
        XLog.d("shop owner email====> " + addShop.owner_email)
        XLog.d("shop owner name====> " + addShop.owner_name)
        XLog.d("shop pincode====> " + addShop.pin_code)
        XLog.d("session token====> " + addShop.session_token)
        XLog.d("shop lat====> " + addShop.shop_lat)
        XLog.d("shop long====> " + addShop.shop_long)
        XLog.d("shop name====> " + addShop.shop_name)
        XLog.d("shop type====> " + addShop.type)
        XLog.d("user id====> " + addShop.user_id)
        XLog.d("amount=====> " + addShop.amount)
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
            XLog.d("shop image path====> $shop_imgPath")

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
        XLog.d("=================================================")

        if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(degree_imgPath)) {
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
                                                    syncShop(shopList[i], shopList)
                                                } else {
                                                    //(mContext as DashboardActivity).showSnackMessage("Shops sync successful")
                                                    i = 0
                                                    progress_wheel.stopSpinning()
                                                    //checkToCallSyncOrder()
                                                    //checkToCallVisitShopApi()
                                                    checkToCallSyncEditShop()
                                                }
                                            }
                                        }
                                    }

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()

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
                                                    syncShop(shopList[i], shopList)
                                                } else {
                                                    //(mContext as DashboardActivity).showSnackMessage("Shops sync successful")
                                                    i = 0
                                                    progress_wheel.stopSpinning()
                                                    //checkToCallSyncOrder()
                                                    //checkToCallVisitShopApi()
                                                    checkToCallSyncEditShop()
                                                }
                                            }

                                        }
                                    }
                                } else {

                                    i++
                                    if (i < shopList.size) {
                                        syncShop(shopList[i], shopList)
                                    } else {
                                        //(mContext as DashboardActivity).showSnackMessage("Shops sync successful")
                                        i = 0
                                        progress_wheel.stopSpinning()
                                        //checkToCallSyncOrder()
                                        //checkToCallVisitShopApi()
                                        checkToCallSyncEditShop()
                                    }

                                }

                            }, { error ->
                                error.printStackTrace()

                                i++
                                if (i < shopList.size) {
                                    syncShop(shopList[i], shopList)
                                } else {
                                    //(mContext as DashboardActivity).showSnackMessage("Shops sync successful")
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    //checkToCallSyncOrder()
                                    //checkToCallVisitShopApi()
                                    checkToCallSyncEditShop()
                                }

                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + "ERROR: " + error.localizedMessage)
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
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                                i++
                                                if (i < shopList.size) {
                                                    syncShop(shopList[i], shopList)
                                                } else {
                                                    //(mContext as DashboardActivity).showSnackMessage("Shops sync successful")
                                                    i = 0
                                                    progress_wheel.stopSpinning()
                                                    //checkToCallSyncOrder()
                                                    //checkToCallVisitShopApi()
                                                    checkToCallSyncEditShop()
                                                }
                                            }
                                        }
                                    }

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()

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
                                                    syncShop(shopList[i], shopList)
                                                } else {
                                                    //(mContext as DashboardActivity).showSnackMessage("Shops sync successful")
                                                    i = 0
                                                    progress_wheel.stopSpinning()
                                                    //checkToCallSyncOrder()
                                                    //checkToCallVisitShopApi()
                                                    checkToCallSyncEditShop()
                                                }
                                            }

                                        }
                                    }
                                } else {

                                    i++
                                    if (i < shopList.size) {
                                        syncShop(shopList[i], shopList)
                                    } else {
                                        //(mContext as DashboardActivity).showSnackMessage("Shops sync successful")
                                        i = 0
                                        progress_wheel.stopSpinning()
                                        //checkToCallSyncOrder()
                                        //checkToCallVisitShopApi()
                                        checkToCallSyncEditShop()
                                    }

                                }

                            }, { error ->
                                error.printStackTrace()

                                i++
                                if (i < shopList.size) {
                                    syncShop(shopList[i], shopList)
                                } else {
                                    //(mContext as DashboardActivity).showSnackMessage("Shops sync successful")
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    //checkToCallSyncOrder()
                                    //checkToCallVisitShopApi()
                                    checkToCallSyncEditShop()
                                }

                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + "ERROR: " + error.localizedMessage)
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

            /*8-12-2021*/
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

                /*8-12-2021*/
                shopDurationData.updated_by = Pref.user_id
                try{
                    shopDurationData.updated_on = shopActivity.updated_on!!
                }
                catch (ex:Exception){
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
                                if (!Pref.isMultipleVisitEnable) {
                                    if (list[0].isVisited && list[0].isDurationCalculated) {
                                        AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, AppUtils.getCurrentDateForShopActi())
                                    }
                                }
                                else {
                                    list.forEach {
                                        if (it.isVisited && it.isDurationCalculated)
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopId, AppUtils.getCurrentDateForShopActi(), it.startTimeStamp!!)
                                    }
                                }
                            }

                        }, { error ->
                            error.printStackTrace()
                            if (error != null)
                                XLog.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + "ERROR:" + error.localizedMessage)
//                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )

    }
    //==============================================Add Shop===================================================================//


    private fun checkToCallSyncEditShop() {
        val list = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnsyncEditShop(0, true)

        if (list != null && list.size > 0) {
            i = 0
            editShop(list)
        } else {
            stopAnimation(addShopSyncImg)
            val shopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false)
            if (shopList != null && shopList.isNotEmpty()) {
                tv_shop_retry.visibility = View.VISIBLE
            } else {
                tv_shop_retry.visibility = View.GONE
                addShopTickImg.visibility = View.VISIBLE
            }

            addShopSyncImg.visibility = View.GONE

            if (!isRetryShop) {
                checkToCallSyncOrder()
            } else
                isRetryShop = false
        }

    }

    //================================================Edit Shop===============================================================//
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun editShop(list: MutableList<AddShopDBModelEntity>) {
        val addShopData = AddShopRequestData()
        val mAddShopDBModelEntity = list[i]
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
        addShopData.added_date = ""
        addShopData.amount = addShopData.amount
        addShopData.area_id = addShopData.area_id
        addShopData.model_id = addShopData.model_id
        addShopData.primary_app_id = addShopData.primary_app_id
        addShopData.secondary_app_id = addShopData.secondary_app_id
        addShopData.lead_id = addShopData.lead_id
        addShopData.stage_id = addShopData.stage_id
        addShopData.funnel_stage_id = addShopData.funnel_stage_id
        addShopData.booking_amount = addShopData.booking_amount
        addShopData.type_id = addShopData.type_id

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.dateOfBirth))
            addShopData.dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.dateOfBirth)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.dateOfAniversary))
            addShopData.date_aniversary = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.dateOfAniversary)

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

        /*14-12-2021*/
        if (addShopData.agency_name!=null && !addShopData.agency_name.equals(""))
            addShopData.agency_name =addShopData.agency_name!!
        else
            addShopData.agency_name = ""

       /*11-02-2022*/
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        /*GSTIN & PAN NUMBER*/
        if (addShopData.GSTN_Number!=null && !addShopData.GSTN_Number.equals(""))
            mAddShopDBModelEntity.gstN_Number =addShopData.GSTN_Number!!
        else
            mAddShopDBModelEntity.gstN_Number = ""

        if (addShopData.ShopOwner_PAN!=null && !addShopData.ShopOwner_PAN.equals(""))
            mAddShopDBModelEntity.shopOwner_PAN =addShopData.ShopOwner_PAN!!
        else
            mAddShopDBModelEntity.shopOwner_PAN = ""


        XLog.d("=====SyncEditShop Input Params (Logout sync)======")
        XLog.d("shop id====> " + addShopData.shop_id)
        val index = addShopData.shop_id!!.indexOf("_")
        XLog.d("decoded shop id====> " + addShopData.user_id + "_" + AppUtils.getDate(addShopData.shop_id!!.substring(index + 1, addShopData.shop_id!!.length).toLong()))
        XLog.d("shop added date====> " + addShopData.added_date)
        XLog.d("shop address====> " + addShopData.address)
        XLog.d("assigned to dd id====> " + addShopData.assigned_to_dd_id)
        XLog.d("assigned to pp id=====> " + addShopData.assigned_to_pp_id)
        XLog.d("date aniversery=====> " + addShopData.date_aniversary)
        XLog.d("dob====> " + addShopData.dob)
        XLog.d("shop owner phn no===> " + addShopData.owner_contact_no)
        XLog.d("shop owner email====> " + addShopData.owner_email)
        XLog.d("shop owner name====> " + addShopData.owner_name)
        XLog.d("shop pincode====> " + addShopData.pin_code)
        XLog.d("session token====> " + addShopData.session_token)
        XLog.d("shop lat====> " + addShopData.shop_lat)
        XLog.d("shop long===> " + addShopData.shop_long)
        XLog.d("shop name====> " + addShopData.shop_name)
        XLog.d("shop type===> " + addShopData.type)
        XLog.d("user id====> " + addShopData.user_id)
        XLog.d("amount=======> " + addShopData.amount)
        XLog.d("area id=======> " + addShopData.area_id)
        XLog.d("model id=======> " + addShopData.model_id)
        XLog.d("primary app id=======> " + addShopData.primary_app_id)
        XLog.d("secondary app id=======> " + addShopData.secondary_app_id)
        XLog.d("lead id=======> " + addShopData.lead_id)
        XLog.d("stage id=======> " + addShopData.stage_id)
        XLog.d("funnel stage id=======> " + addShopData.funnel_stage_id)
        XLog.d("booking amount=======> " + addShopData.booking_amount)
        XLog.d("type id=======> " + addShopData.type_id)
        if (mAddShopDBModelEntity.shopImageLocalPath != null)
            XLog.d("shop image path====> " + mAddShopDBModelEntity.shopImageLocalPath)
        XLog.d("family member dob=======> " + addShopData.family_member_dob)
        XLog.d("director name=======> " + addShopData.director_name)
        XLog.d("key person's name=======> " + addShopData.key_person_name)
        XLog.d("phone no=======> " + addShopData.phone_no)
        XLog.d("additional dob=======> " + addShopData.addtional_dob)
        XLog.d("additional doa=======> " + addShopData.addtional_doa)
        XLog.d("doctor family member dob=======> " + addShopData.doc_family_member_dob)
        XLog.d("specialization=======> " + addShopData.specialization)
        XLog.d("average patient count per day=======> " + addShopData.average_patient_per_day)
        XLog.d("category=======> " + addShopData.category)
        XLog.d("doctor address=======> " + addShopData.doc_address)
        XLog.d("doctor pincode=======> " + addShopData.doc_pincode)
        XLog.d("chambers or hospital under same headquarter=======> " + addShopData.is_chamber_same_headquarter)
        XLog.d("chamber related remarks=======> " + addShopData.is_chamber_same_headquarter_remarks)
        XLog.d("chemist name=======> " + addShopData.chemist_name)
        XLog.d("chemist name=======> " + addShopData.chemist_address)
        XLog.d("chemist pincode=======> " + addShopData.chemist_pincode)
        XLog.d("assistant name=======> " + addShopData.assistant_name)
        XLog.d("assistant contact no=======> " + addShopData.assistant_contact_no)
        XLog.d("assistant dob=======> " + addShopData.assistant_dob)
        XLog.d("assistant date of anniversary=======> " + addShopData.assistant_doa)
        XLog.d("assistant family dob=======> " + addShopData.assistant_family_dob)
        XLog.d("entity id=======> " + addShopData.entity_id)
        XLog.d("party status id=======> " + addShopData.party_status_id)
        XLog.d("retailer id=======> " + addShopData.retailer_id)
        XLog.d("dealer id=======> " + addShopData.dealer_id)
        XLog.d("beat id=======> " + addShopData.beat_id)
        if (mAddShopDBModelEntity.doc_degree != null)
            XLog.d("doctor degree image path=======> " + mAddShopDBModelEntity.doc_degree)
        XLog.d("assigned to shop id=======> " + addShopData.assigned_to_shop_id)
        XLog.d("actual_address=======> " + addShopData.actual_address)
        XLog.d("================================================")

        progress_wheel.spin()

        if (TextUtils.isEmpty(mAddShopDBModelEntity.shopImageLocalPath) && TextUtils.isEmpty(mAddShopDBModelEntity.doc_degree)) {
            val repository = EditShopRepoProvider.provideEditShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.editShop(addShopData)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("Edit Shop : " + ", SHOP: " + addShopData.shop_name + ", STATUS: " + addShopResult.status + ",RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsEditUploaded(1, addShopData.shop_id)
                                    progress_wheel.stopSpinning()

                                    i++
                                    if (i < list.size) {
                                        editShop(list)
                                    } else {
                                        i = 0
                                        stopAnimation(addShopSyncImg)
                                        val shopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false)
                                        if (shopList != null && shopList.isNotEmpty()) {
                                            tv_shop_retry.visibility = View.VISIBLE
                                        } else {

                                            val list_ = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnsyncEditShop(0, true)
                                            if (list_ != null && list_.size > 0)
                                                tv_shop_retry.visibility = View.VISIBLE
                                            else {
                                                tv_shop_retry.visibility = View.GONE
                                                addShopTickImg.visibility = View.VISIBLE
                                            }
                                        }
                                        addShopSyncImg.visibility = View.GONE


                                        if (!isRetryShop) {
                                            checkToCallSyncOrder()
                                        } else
                                            isRetryShop = false
                                    }


                                } else if (addShopResult.status == NetworkConstant.SESSION_MISMATCH) {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).clearData()
                                    startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                    (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                    (mContext as DashboardActivity).finish()
                                } else {
                                    progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))

                                    i++
                                    if (i < list.size) {
                                        editShop(list)
                                    } else {
                                        stopAnimation(addShopSyncImg)
                                        addShopSyncImg.visibility = View.GONE
                                        tv_shop_retry.visibility = View.VISIBLE

                                        i = 0
                                        if (!isRetryShop) {
                                            checkToCallSyncOrder()
                                        } else
                                            isRetryShop = false
                                    }
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                try {
                                    XLog.d("Edit Shop : " + ", SHOP: " + addShopData.shop_name + ", ERROR: " + error.message)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                i++
                                if (i < list.size) {
                                    editShop(list)
                                } else {
                                    stopAnimation(addShopSyncImg)
                                    addShopSyncImg.visibility = View.GONE
                                    tv_shop_retry.visibility = View.VISIBLE

                                    i = 0
                                    if (!isRetryShop) {
                                        checkToCallSyncOrder()
                                    } else
                                        isRetryShop = false
                                }
                            })
            )
        }
        else {
            val repository = EditShopRepoProvider.provideEditShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShopData, mAddShopDBModelEntity.shopImageLocalPath, mAddShopDBModelEntity.doc_degree, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("Edit Shop : " + ", SHOP: " + addShopData.shop_name + ", STATUS: " + addShopResult.status + ",RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsEditUploaded(1, addShopData.shop_id)
                                    progress_wheel.stopSpinning()

                                    i++
                                    if (i < list.size) {
                                        editShop(list)
                                    } else {
                                        i = 0
                                        stopAnimation(addShopSyncImg)
                                        val shopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false)
                                        if (shopList != null && shopList.isNotEmpty()) {
                                            tv_shop_retry.visibility = View.VISIBLE
                                        } else {

                                            val list_ = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnsyncEditShop(0, true)
                                            if (list_ != null && list_.size > 0)
                                                tv_shop_retry.visibility = View.VISIBLE
                                            else {
                                                tv_shop_retry.visibility = View.GONE
                                                addShopTickImg.visibility = View.VISIBLE
                                            }
                                        }
                                        addShopSyncImg.visibility = View.GONE


                                        if (!isRetryShop) {
                                            checkToCallSyncOrder()
                                        } else
                                            isRetryShop = false
                                    }


                                } else if (addShopResult.status == NetworkConstant.SESSION_MISMATCH) {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).clearData()
                                    startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                    (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                    (mContext as DashboardActivity).finish()
                                } else {
                                    progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))

                                    i++
                                    if (i < list.size) {
                                        editShop(list)
                                    } else {
                                        stopAnimation(addShopSyncImg)
                                        addShopSyncImg.visibility = View.GONE
                                        tv_shop_retry.visibility = View.VISIBLE

                                        i = 0
                                        if (!isRetryShop) {
                                            checkToCallSyncOrder()
                                        } else
                                            isRetryShop = false
                                    }
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                try {
                                    XLog.d("Edit Shop : " + ", SHOP: " + addShopData.shop_name + ", ERROR: " + error.message)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                i++
                                if (i < list.size) {
                                    editShop(list)
                                } else {
                                    stopAnimation(addShopSyncImg)
                                    addShopSyncImg.visibility = View.GONE
                                    tv_shop_retry.visibility = View.VISIBLE

                                    i = 0
                                    if (!isRetryShop) {
                                        checkToCallSyncOrder()
                                    } else
                                        isRetryShop = false
                                }
                            })
            )
        }
    }
    //================================================Edit Shop===============================================================//


    private fun checkToCallSyncOrder() {
        if (Pref.isOrderShow) {
            val orderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getUnsyncedData(false)

            val orderDetailsList = ArrayList<OrderDetailsListEntity>()

            if (orderList != null && orderList.isNotEmpty()) {

                for (i in orderList.indices) {
                    val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(orderList[i].shop_id)

                    if (shop != null && shop.isUploaded)
                        orderDetailsList.add(orderList[i])
                }

                if (orderDetailsList.size > 0) {
                    i = 0
                    syncAllOrder(orderDetailsList[i], orderDetailsList)
                } else {
                    stopAnimation(addOrderSyncImg)
                    addOrderTickImg.visibility = View.VISIBLE
                    addOrderSyncImg.visibility = View.GONE

                    if (!isRetryOrder)
                        checkToGpsStatus()
                    else
                        isRetryOrder = false
                }
            } else {
                stopAnimation(addOrderSyncImg)
                addOrderTickImg.visibility = View.VISIBLE
                addOrderSyncImg.visibility = View.GONE

                if (!isRetryOrder)
                    checkToGpsStatus()
                else
                    isRetryOrder = false
            }
        } else
            checkToGpsStatus()
    }

    //===================================================Add Order==============================================================//
    private fun syncAllOrder(order: OrderDetailsListEntity, orderList: List<OrderDetailsListEntity>) {
        /*if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }*/

        //(mContext as DashboardActivity).showSnackMessage("Syncing Order")

        val addOrder = AddOrderInputParamsModel()
        addOrder.collection = ""
        addOrder.description = ""
        addOrder.order_amount = order.amount
        addOrder.order_date = order.date
        addOrder.order_id = order.order_id
        addOrder.shop_id = order.shop_id
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

        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(order.shop_id!!)
        if (shopActivity != null) {
            if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(order.shop_id)

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


        val list = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToShopAndOrderId(order.order_id!!, order.shop_id!!)
        val productList = java.util.ArrayList<AddOrderInputProductList>()

        for (j in list.indices) {
            val product = AddOrderInputProductList()
            product.id = list[j].product_id
            product.qty = list[j].qty
            product.rate = list[j].rate
            product.total_price = list[j].total_price
            product.product_name = list[j].product_name
            product.scheme_qty = list[j].scheme_qty
            product.scheme_rate = list[j].scheme_rate
            product.total_scheme_price = list[j].total_scheme_price

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
                                val orderListResponse = result as BaseResponse
                                XLog.e("Add Order : \n" + ", SHOP ID===> " + orderList[i].shop_id + ", STATUS====> " + orderListResponse.status + ",RESPONSE MESSAGE:" + orderListResponse.message)
                                if (orderListResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, order.order_id!!)

                                    i++
                                    if (i < orderList.size) {
                                        syncAllOrder(orderList[i], orderList)
                                    } else {
                                        //(mContext as DashboardActivity).showSnackMessage("Order sync successful")

                                        val order_list = AppDatabase.getDBInstance()!!.orderDetailsListDao().getUnsyncedData(false)
                                        val orderDetailsList = ArrayList<OrderDetailsListEntity>()
                                        if (order_list != null && order_list.isNotEmpty()) {

                                            for (i in order_list.indices) {
                                                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(order_list[i].shop_id)

                                                if (shop.isUploaded)
                                                    orderDetailsList.add(order_list[i])
                                            }

                                            if (orderDetailsList.size > 0) {
                                                tv_order_retry.visibility = View.VISIBLE
                                            } else {
                                                tv_order_retry.visibility = View.GONE
                                                addOrderTickImg.visibility = View.VISIBLE
                                            }
                                        } else {
                                            tv_order_retry.visibility = View.GONE
                                            addOrderTickImg.visibility = View.VISIBLE
                                        }
                                        stopAnimation(addOrderSyncImg)
                                        addOrderSyncImg.visibility = View.GONE


                                        progress_wheel.stopSpinning()
                                        i = 0
                                        if (!isRetryOrder)
                                            checkToGpsStatus()
                                        else
                                            isRetryOrder = false
                                    }
                                } else {
                                    //progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                    i++
                                    if (i < orderList.size) {
                                        syncAllOrder(orderList[i], orderList)
                                    } else {
                                        //(mContext as DashboardActivity).showSnackMessage("Order sync successful")
                                        stopAnimation(addOrderSyncImg)
                                        tv_order_retry.visibility = View.VISIBLE
                                        addOrderSyncImg.visibility = View.GONE

                                        progress_wheel.stopSpinning()
                                        i = 0
                                        if (!isRetryOrder)
                                            checkToGpsStatus()
                                        else
                                            isRetryOrder = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                //progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                                //(mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                try {
                                    XLog.d("Add Order : \n" + ", SHOP ID===> " + orderList[i].shop_id + ", ERROR====> " + error.message)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                i++
                                if (i < orderList.size) {
                                    syncAllOrder(orderList[i], orderList)
                                } else {
                                    //(mContext as DashboardActivity).showSnackMessage("Order sync successful")
                                    stopAnimation(addOrderSyncImg)
                                    tv_order_retry.visibility = View.VISIBLE
                                    addOrderSyncImg.visibility = View.GONE

                                    progress_wheel.stopSpinning()
                                    i = 0
                                    if (!isRetryOrder)
                                        checkToGpsStatus()
                                    else
                                        isRetryOrder = false
                                }
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
                                val orderListResponse = result as BaseResponse
                                XLog.e("Add Order : \n" + ", SHOP ID===> " + orderList[i].shop_id + ", STATUS====> " + orderListResponse.status + ",RESPONSE MESSAGE:" + orderListResponse.message)
                                if (orderListResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, order.order_id!!)

                                    i++
                                    if (i < orderList.size) {
                                        syncAllOrder(orderList[i], orderList)
                                    } else {
                                        //(mContext as DashboardActivity).showSnackMessage("Order sync successful")

                                        val order_list = AppDatabase.getDBInstance()!!.orderDetailsListDao().getUnsyncedData(false)
                                        val orderDetailsList = ArrayList<OrderDetailsListEntity>()
                                        if (order_list != null && order_list.isNotEmpty()) {

                                            for (i in order_list.indices) {
                                                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(order_list[i].shop_id)

                                                if (shop.isUploaded)
                                                    orderDetailsList.add(order_list[i])
                                            }

                                            if (orderDetailsList.size > 0) {
                                                tv_order_retry.visibility = View.VISIBLE
                                            } else {
                                                tv_order_retry.visibility = View.GONE
                                                addOrderTickImg.visibility = View.VISIBLE
                                            }
                                        } else {
                                            tv_order_retry.visibility = View.GONE
                                            addOrderTickImg.visibility = View.VISIBLE
                                        }
                                        stopAnimation(addOrderSyncImg)
                                        addOrderSyncImg.visibility = View.GONE


                                        progress_wheel.stopSpinning()
                                        i = 0
                                        if (!isRetryOrder)
                                            checkToGpsStatus()
                                        else
                                            isRetryOrder = false
                                    }
                                } else {
                                    //progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                    i++
                                    if (i < orderList.size) {
                                        syncAllOrder(orderList[i], orderList)
                                    } else {
                                        //(mContext as DashboardActivity).showSnackMessage("Order sync successful")
                                        stopAnimation(addOrderSyncImg)
                                        tv_order_retry.visibility = View.VISIBLE
                                        addOrderSyncImg.visibility = View.GONE

                                        progress_wheel.stopSpinning()
                                        i = 0
                                        if (!isRetryOrder)
                                            checkToGpsStatus()
                                        else
                                            isRetryOrder = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                //progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                                //(mContext as DashboardActivity).showSnackMessage(mContext.getString(R.string.unable_to_sync))

                                try {
                                    XLog.d("Add Order : \n" + ", SHOP ID===> " + orderList[i].shop_id + ", ERROR====> " + error.message)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                i++
                                if (i < orderList.size) {
                                    syncAllOrder(orderList[i], orderList)
                                } else {
                                    //(mContext as DashboardActivity).showSnackMessage("Order sync successful")
                                    stopAnimation(addOrderSyncImg)
                                    tv_order_retry.visibility = View.VISIBLE
                                    addOrderSyncImg.visibility = View.GONE

                                    progress_wheel.stopSpinning()
                                    i = 0
                                    if (!isRetryOrder)
                                        checkToGpsStatus()
                                    else
                                        isRetryOrder = false
                                }
                            })
            )
        }
    }
    //===================================================Add Order==============================================================//


    private fun checkToGpsStatus() {
        val list = AppDatabase.getDBInstance()!!.gpsStatusDao().getDataSyncStateWise(false)

        if (list != null && list.isNotEmpty()) {
            i = 0
            callUpdateGpsStatusApi(list)
        } else {
            //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
            stopAnimation(gpsSyncImg)
            gpsTickImg.visibility = View.VISIBLE
            gpsSyncImg.visibility = View.GONE


            if (!isRetryGps)
                checkToCallCollectionApi()
            else
                isRetryGps = false
        }
    }

    //============================================================Update Gps Status============================================================//
    private fun callUpdateGpsStatusApi(list: List<GpsStatusEntity>) {

        //(mContext as DashboardActivity).showSnackMessage("Syncing Gps")

        val updateGps = UpdateGpsInputParamsModel()
        updateGps.date = list[i].date
        updateGps.gps_id = list[i].gps_id
        updateGps.gps_off_time = list[i].gps_off_time
        updateGps.gps_on_time = list[i].gps_on_time
        updateGps.user_id = Pref.user_id
        updateGps.session_token = Pref.session_token
        updateGps.duration = AppUtils.getTimeInHourMinuteFormat(list[i].duration?.toLong()!!)

        XLog.d("========SYNC GPS INPUT PARAMS========")
        XLog.d("date====> " + updateGps.date)
        XLog.d("gps_id====> " + updateGps.gps_id)
        XLog.d("gps_off_time====> " + updateGps.gps_off_time)
        XLog.d("gps_on_time====> " + updateGps.gps_on_time)
        XLog.d("user_id====> " + updateGps.user_id)
        XLog.d("session_token====> " + updateGps.session_token)
        XLog.d("duration====> " + updateGps.duration)
        XLog.d("=====================================")

        progress_wheel.spin()

        val repository = UpdateGpsStatusRepoProvider.updateGpsStatusRepository()
        BaseActivity.compositeDisposable.add(
                repository.updateGpsStatus(updateGps)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val gpsStatusResponse = result as BaseResponse
                            XLog.d("SYNC GPS : " + "RESPONSE : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name
                                    + ",MESSAGE : " + gpsStatusResponse.message)
                            if (gpsStatusResponse.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.gpsStatusDao().updateIsUploadedAccordingToId(true, list[i].id)


                                i++
                                if (i < list.size) {
                                    callUpdateGpsStatusApi(list)
                                } else {
                                    //(mContext as DashboardActivity).showSnackMessage("Gps sync succesful")
                                    stopAnimation(gpsSyncImg)
                                    val list_ = AppDatabase.getDBInstance()!!.gpsStatusDao().getDataSyncStateWise(false)
                                    if (list_ != null && list_.isNotEmpty())
                                        tv_gps_retry.visibility = View.VISIBLE
                                    else {
                                        tv_gps_retry.visibility = View.GONE
                                        gpsTickImg.visibility = View.VISIBLE
                                    }
                                    gpsSyncImg.visibility = View.GONE


                                    i = 0
                                    progress_wheel.stopSpinning()
                                    //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                    if (!isRetryGps)
                                        checkToCallCollectionApi()
                                    else
                                        isRetryGps = false
                                }

                            } else {

                                i++
                                if (i < list.size) {
                                    callUpdateGpsStatusApi(list)
                                } else {
                                    //(mContext as DashboardActivity).showSnackMessage("Gps sync succesful")
                                    stopAnimation(gpsSyncImg)
                                    tv_gps_retry.visibility = View.VISIBLE
                                    gpsSyncImg.visibility = View.GONE

                                    i = 0
                                    progress_wheel.stopSpinning()
                                    //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                    if (!isRetryGps)
                                        checkToCallCollectionApi()
                                    else
                                        isRetryGps = false
                                }

                            }


                        }, { error ->
                            //
                            XLog.d("SYNC GPS : " + "RESPONSE ERROR: " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            i++
                            if (i < list.size) {
                                callUpdateGpsStatusApi(list)
                            } else {
                                //(mContext as DashboardActivity).showSnackMessage("Gps sync succesful")
                                stopAnimation(gpsSyncImg)
                                tv_gps_retry.visibility = View.VISIBLE
                                gpsSyncImg.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()
                                //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                if (!isRetryGps)
                                    checkToCallCollectionApi()
                                else
                                    isRetryGps = false
                            }
                        })
        )
    }
    //============================================================Update Gps Status============================================================//


    private fun checkToCallCollectionApi() {
        if (Pref.isCollectioninMenuShow) {
            val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getUnsyncCollection(false)

            val collectionList = ArrayList<CollectionDetailsEntity>()

            if (list != null && list.isNotEmpty()) {

                for (i in list.indices) {
                    val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(list[i].shop_id)

                    if (shop.isUploaded)
                        collectionList.add(list[i])
                }

                if (collectionList.size > 0) {
                    i = 0

                    val collectionDateTime = AppUtils.getCurrentDateFormatInTa(collectionList[i].date!!) + "T" + collectionList[i].only_time

                    syncAddCollectionApi(collectionList[i].shop_id, collectionList[i].collection_id, collectionList[i].collection!!, collectionDateTime,
                            collectionList, collectionList[i].bill_id, collectionList[i].order_id, collectionList[i].payment_id,
                            collectionList[i].instrument_no, collectionList[i].bank, collectionList[i].feedback, collectionList[i].file_path,
                            collectionList[i].patient_name, collectionList[i].patient_address, collectionList[i].patient_no,list[i].Hospital,
                            list[i].Email_Address)
                } else {
                    stopAnimation(collectionSyncImg)
                    collectionTickImg.visibility = View.VISIBLE
                    collectionSyncImg.visibility = View.GONE

                    if (!isRetryCollection)
                        checkToCallVisitShopApi()
                    else
                        isRetryCollection = false
                }
            } else {
                stopAnimation(collectionSyncImg)
                collectionTickImg.visibility = View.VISIBLE
                collectionSyncImg.visibility = View.GONE

                if (!isRetryCollection)
                    checkToCallVisitShopApi()
                else
                    isRetryCollection = false
            }
        } else
            checkToCallVisitShopApi()
    }


    //============================================Add Collection===========================================================//
    private fun syncAddCollectionApi(shop_id: String?, collection_id: String?, collection: String, date: String,
                                     list: List<CollectionDetailsEntity>, billId: String?, orderId: String?, paymentId: String?,
                                     instrumentNo: String?, bank: String?, feedback: String?, filePath: String?, patientName: String?,
                                     patientAddress: String?, patientNo: String?,hospital:String?,emailAddress:String?) {

        val addCollection = AddCollectionInputParamsModel()
        addCollection.collection = collection
        addCollection.collection_date = /*AppUtils.getCurrentDateFormatInTa(*/date//)
        addCollection.collection_id = collection_id
        addCollection.session_token = Pref.session_token
        addCollection.user_id = Pref.user_id
        addCollection.shop_id = shop_id
        addCollection.bill_id = if (TextUtils.isEmpty(billId)) "" else billId!!
        addCollection.order_id = if (TextUtils.isEmpty(orderId)) "" else orderId!!
        addCollection.instrument_no = if (TextUtils.isEmpty(instrumentNo)) "" else instrumentNo!!
        addCollection.bank= if (TextUtils.isEmpty(bank)) "" else bank!!
        addCollection.remarks = if (TextUtils.isEmpty(feedback)) "" else feedback!!
        addCollection.payment_id = if (TextUtils.isEmpty(paymentId)) "" else paymentId!!
        addCollection.patient_name = if (TextUtils.isEmpty(patientName)) "" else patientName!!
        addCollection.patient_address = if (TextUtils.isEmpty(patientAddress)) "" else patientAddress!!
        addCollection.patient_no = if (TextUtils.isEmpty(patientNo)) "" else patientNo!!
        /*06-01-2022*/
        addCollection.Hospital = if (TextUtils.isEmpty(hospital)) "" else hospital!!
        addCollection.Email_Address = if (TextUtils.isEmpty(emailAddress)) "" else emailAddress!!

        XLog.d("===SYNC COLLECTION INPUT PARAMS (Logout Sync)====")
        XLog.d("Collection Amount===> " + addCollection.collection)
        XLog.d("Collection Date==> " + addCollection.collection_date)
        XLog.d("ColLection ID===> " + addCollection.collection_id)
        XLog.d("Shop ID===> " + addCollection.shop_id)
        XLog.d("user_id==> " + addCollection.user_id)
        XLog.d("session_token===> " + addCollection.session_token)
        XLog.d("billId===> " + addCollection.bill_id)
        XLog.d("order_id===> " + addCollection.order_id)
        XLog.d("payment_id===> " + addCollection.payment_id)
        XLog.d("instrument_no===> " + addCollection.instrument_no)
        XLog.d("bank===> " + addCollection.bank)
        XLog.d("remarks===> " + addCollection.remarks)
        XLog.d("patient_name===> " + addCollection.patient_name)
        XLog.d("patient_address===> " + addCollection.patient_address)
        XLog.d("patient_no===> " + addCollection.patient_no)
        XLog.d("Hospital===> " + addCollection.Hospital)
        XLog.d("Email Address===> " + addCollection.Email_Address)

        if (filePath != null)
            XLog.d("filePath===> $filePath")
        else
            XLog.d("filePath===> ")
        XLog.d("=====================================================")

        progress_wheel.spin()

        if (TextUtils.isEmpty(filePath)) {
            val repository = AddCollectionRepoProvider.addCollectionRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addCollection(addCollection)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse

                                XLog.d("SYNC COLLECTION : " + "RESPONSE : " + "\n" + "STATUS : " + orderList.status + ",MESSAGE : " + orderList.message
                                        + ", COLLECTION ID : " + addCollection.collection_id)
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, collection_id!!)

                                    i++
                                    if (i < list.size) {
                                        val collectionDateTime = AppUtils.getCurrentDateFormatInTa(list[i].date!!) + "T" + list[i].only_time
                                        syncAddCollectionApi(list[i].shop_id, list[i].collection_id, list[i].collection!!,
                                                collectionDateTime, list, list[i].bill_id, orderId, list[i].payment_id,
                                                list[i].instrument_no, list[i].bank, list[i].feedback, list[i].file_path, list[i].patient_name, list[i].patient_address, list[i].patient_no,list[i].Hospital,
                                                list[i].Email_Address)
                                    } else {


                                        val list_ = AppDatabase.getDBInstance()!!.collectionDetailsDao().getUnsyncCollection(false)
                                        val collectionList = ArrayList<CollectionDetailsEntity>()
                                        if (list_ != null && list_.isNotEmpty()) {

                                            for (i in list_.indices) {
                                                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(list_[i].shop_id)

                                                if (shop.isUploaded)
                                                    collectionList.add(list_[i])
                                            }
                                            if (collectionList.size > 0) {
                                                tv_collection_retry.visibility = View.VISIBLE
                                            } else {
                                                tv_collection_retry.visibility = View.GONE
                                                collectionTickImg.visibility = View.VISIBLE
                                            }
                                        } else {
                                            tv_collection_retry.visibility = View.GONE
                                            collectionTickImg.visibility = View.VISIBLE
                                        }

                                        stopAnimation(collectionSyncImg)
                                        collectionSyncImg.visibility = View.GONE

                                        i = 0
                                        if (!isRetryCollection)
                                            checkToCallVisitShopApi()
                                        else
                                            isRetryCollection = false
                                    }
                                } else {
                                    i++
                                    if (i < list.size) {
                                        val collectionDateTime = AppUtils.getCurrentDateFormatInTa(list[i].date!!) + "T" + list[i].only_time
                                        syncAddCollectionApi(list[i].shop_id, list[i].collection_id, list[i].collection!!,
                                                collectionDateTime, list, list[i].bill_id, orderId, list[i].payment_id,
                                                list[i].instrument_no, list[i].bank, list[i].feedback, list[i].file_path,
                                                list[i].patient_name, list[i].patient_address, list[i].patient_no,
                                                list[i].Hospital,
                                                list[i].Email_Address)
                                    } else {
                                        stopAnimation(collectionSyncImg)
                                        tv_collection_retry.visibility = View.VISIBLE
                                        collectionSyncImg.visibility = View.GONE

                                        i = 0
                                        if (!isRetryCollection)
                                            checkToCallVisitShopApi()
                                        else
                                            isRetryCollection = false
                                    }
                                }
                            }, { error ->

                                try {
                                    XLog.d("SYNC COLLECTION : ERROR : " + error.message + ", COLLECTION ID : " + addCollection.collection_id)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                i++
                                if (i < list.size) {
                                    val collectionDateTime = AppUtils.getCurrentDateFormatInTa(list[i].date!!) + "T" + list[i].only_time
                                    syncAddCollectionApi(list[i].shop_id, list[i].collection_id, list[i].collection!!,
                                            collectionDateTime, list, list[i].bill_id, orderId, list[i].payment_id,
                                            list[i].instrument_no, list[i].bank, list[i].feedback, list[i].file_path,
                                            list[i].patient_name, list[i].patient_address, list[i].patient_no,list[i].Hospital,
                                            list[i].Email_Address)
                                } else {
                                    stopAnimation(collectionSyncImg)
                                    tv_collection_retry.visibility = View.VISIBLE
                                    collectionSyncImg.visibility = View.GONE

                                    i = 0
                                    if (!isRetryCollection)
                                        checkToCallVisitShopApi()
                                    else
                                        isRetryCollection = false
                                }
                            })
            )
        }
        else {
            val repository = AddCollectionRepoProvider.addCollectionMultipartRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addCollection(addCollection, filePath, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse

                                XLog.d("SYNC COLLECTION : " + "RESPONSE : " + "\n" + "STATUS : " + orderList.status + ",MESSAGE : " + orderList.message
                                        + ", COLLECTION ID : " + addCollection.collection_id)
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, collection_id!!)

                                    i++
                                    if (i < list.size) {
                                        val collectionDateTime = AppUtils.getCurrentDateFormatInTa(list[i].date!!) + "T" + list[i].only_time
                                        syncAddCollectionApi(list[i].shop_id, list[i].collection_id, list[i].collection!!,
                                                collectionDateTime, list, list[i].bill_id, orderId, list[i].payment_id,
                                                list[i].instrument_no, list[i].bank, list[i].feedback, list[i].file_path,
                                                list[i].patient_name, list[i].patient_address, list[i].patient_no,list[i].Hospital,
                                                list[i].Email_Address)
                                    } else {


                                        val list_ = AppDatabase.getDBInstance()!!.collectionDetailsDao().getUnsyncCollection(false)
                                        val collectionList = ArrayList<CollectionDetailsEntity>()
                                        if (list_ != null && list_.isNotEmpty()) {

                                            for (i in list_.indices) {
                                                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(list_[i].shop_id)

                                                if (shop.isUploaded)
                                                    collectionList.add(list_[i])
                                            }
                                            if (collectionList.size > 0) {
                                                tv_collection_retry.visibility = View.VISIBLE
                                            } else {
                                                tv_collection_retry.visibility = View.GONE
                                                collectionTickImg.visibility = View.VISIBLE
                                            }
                                        } else {
                                            tv_collection_retry.visibility = View.GONE
                                            collectionTickImg.visibility = View.VISIBLE
                                        }

                                        stopAnimation(collectionSyncImg)
                                        collectionSyncImg.visibility = View.GONE

                                        i = 0
                                        if (!isRetryCollection)
                                            checkToCallVisitShopApi()
                                        else
                                            isRetryCollection = false
                                    }
                                } else {
                                    i++
                                    if (i < list.size) {
                                        val collectionDateTime = AppUtils.getCurrentDateFormatInTa(list[i].date!!) + "T" + list[i].only_time
                                        syncAddCollectionApi(list[i].shop_id, list[i].collection_id, list[i].collection!!,
                                                collectionDateTime, list, list[i].bill_id, orderId, list[i].payment_id,
                                                list[i].instrument_no, list[i].bank, list[i].feedback, list[i].file_path,
                                                list[i].patient_name, list[i].patient_address, list[i].patient_no,list[i].Hospital,
                                                list[i].Email_Address)
                                    } else {
                                        stopAnimation(collectionSyncImg)
                                        tv_collection_retry.visibility = View.VISIBLE
                                        collectionSyncImg.visibility = View.GONE

                                        i = 0
                                        if (!isRetryCollection)
                                            checkToCallVisitShopApi()
                                        else
                                            isRetryCollection = false
                                    }
                                }
                            }, { error ->

                                try {
                                    XLog.d("SYNC COLLECTION : ERROR : " + error.message + ", COLLECTION ID : " + addCollection.collection_id)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                i++
                                if (i < list.size) {
                                    val collectionDateTime = AppUtils.getCurrentDateFormatInTa(list[i].date!!) + "T" + list[i].only_time
                                    syncAddCollectionApi(list[i].shop_id, list[i].collection_id, list[i].collection!!,
                                            collectionDateTime, list, list[i].bill_id, orderId, list[i].payment_id,
                                            list[i].instrument_no, list[i].bank, list[i].feedback, list[i].file_path,
                                            list[i].patient_name, list[i].patient_address, list[i].patient_no,list[i].Hospital,
                                            list[i].Email_Address)
                                } else {
                                    stopAnimation(collectionSyncImg)
                                    tv_collection_retry.visibility = View.VISIBLE
                                    collectionSyncImg.visibility = View.GONE

                                    i = 0
                                    if (!isRetryCollection)
                                        checkToCallVisitShopApi()
                                    else
                                        isRetryCollection = false
                                }
                            })
            )
        }
    }
    //============================================Add Collection===========================================================//


    //==========================================Revisit Shop============================================================//
    private fun checkToCallVisitShopApi() {

        if (Pref.user_id.isNullOrEmpty() || BaseActivity.isShopActivityUpdating)
            return

        /* Get all the shop list that has been synched successfully*/
        val syncedShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(true)

        if (syncedShopList != null && syncedShopList.size > 0) {

            BaseActivity.isShopActivityUpdating = true

            val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
            val syncedShop = ArrayList<ShopActivityEntity>()

            val revisitStatusList : MutableList<ShopRevisitStatusRequestData> = ArrayList()

            for (k in 0 until syncedShopList.size) {

                if (!Pref.isMultipleVisitEnable) {
                    /* Get shop activity that has completed time duration calculation*/
                    val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(syncedShopList[k].shop_id, true,
                            false)
                    if (shopActivity == null) {

                        val shop_activity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForTodayShop(syncedShopList[k].shop_id,
                                true, true, AppUtils.getCurrentDateForShopActi())
                        if (shop_activity != null)
                            syncedShop.add(shop_activity)

                    } else {
                        val shopDurationData = ShopDurationRequestData()
                        shopDurationData.shop_id = shopActivity?.shopid
                        shopDurationData.spent_duration = shopActivity?.duration_spent
                        shopDurationData.visited_date = shopActivity?.visited_date
                        shopDurationData.visited_time = shopActivity?.visited_date
                        if (TextUtils.isEmpty(shopActivity.distance_travelled))
                            shopActivity.distance_travelled = "0.0"
                        shopDurationData.distance_travelled = shopActivity.distance_travelled
                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity?.shopid) != null)
                            shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity?.shopid).totalVisitCount
                        else
                            shopDurationData.total_visit_count = "1"

                        val currentShopVisitDateNumber = AppUtils.getTimeStampFromDateOnly(shopActivity.date!!)

                        if (shopId == shopActivity.shopid && previousShopVisitDateNumber == currentShopVisitDateNumber)
                            continue

                        shopId = shopActivity.shopid!!
                        shopVisitDate = shopActivity.date!!
                        previousShopVisitDateNumber = currentShopVisitDateNumber

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

                        /*8-12-2021*/
                        shopDurationData.updated_by = Pref.user_id
                        //shopDurationData.updated_on = shopActivity.updated_on!!
                        shopDurationData.updated_on = AppUtils.getCurrentDateForShopActi()

                        if (shopActivity.pros_id!=null && !shopActivity.pros_id.equals(""))
                            shopDurationData.pros_id = shopActivity.pros_id!!
                        else
                            shopDurationData.pros_id = ""

                        if (shopActivity.agency_name!=null && !shopActivity.agency_name.equals(""))
                            shopDurationData.agency_name =shopActivity.agency_name!!
                        else
                            shopDurationData.agency_name = ""

                        if (shopActivity.approximate_1st_billing_value!=null && !shopActivity.approximate_1st_billing_value.equals(""))
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


                        //////////////////////////
                        var revisitStatusObj=ShopRevisitStatusRequestData()
                        var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(shopDurationData.shop_revisit_uniqKey.toString())
                        if(data != null){
                            revisitStatusObj.shop_id=data.shop_id
                            revisitStatusObj.order_status=data.order_status
                            revisitStatusObj.order_remarks=data.order_remarks
                            revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                            revisitStatusList.add(revisitStatusObj)
                        }
                    }
                }
                else {
                    val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShopList(syncedShopList[k].shop_id, true,
                            false)

                    shopActivity?.forEach {
                        val shopDurationData = ShopDurationRequestData()
                        shopDurationData.shop_id = it.shopid
                        shopDurationData.spent_duration = it.duration_spent
                        shopDurationData.visited_date = it.visited_date
                        shopDurationData.visited_time = it.visited_date
                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid) != null)
                            shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid).totalVisitCount
                        else
                            shopDurationData.total_visit_count = "1"

                        if (TextUtils.isEmpty(it.distance_travelled))
                            it.distance_travelled = "0.0"
                        shopDurationData.distance_travelled = it.distance_travelled

                        if (!TextUtils.isEmpty(it.feedback))
                            shopDurationData.feedback = it.feedback
                        else
                            shopDurationData.feedback = ""

                        shopDurationData.isFirstShopVisited = it.isFirstShopVisited
                        shopDurationData.distanceFromHomeLoc = it.distance_from_home_loc

                        shopDurationData.next_visit_date = it.next_visit_date

                        if (!TextUtils.isEmpty(it.early_revisit_reason))
                            shopDurationData.early_revisit_reason = it.early_revisit_reason
                        else
                            shopDurationData.early_revisit_reason = ""

                        shopDurationData.device_model = it.device_model
                        shopDurationData.android_version = it.android_version
                        shopDurationData.battery = it.battery
                        shopDurationData.net_status = it.net_status
                        shopDurationData.net_type = it.net_type
                        shopDurationData.in_time = it.in_time
                        shopDurationData.out_time = it.out_time
                        shopDurationData.start_timestamp = it.startTimeStamp
                        shopDurationData.in_location = it.in_loc
                        shopDurationData.out_location = it.out_loc
                        shopDurationData.shop_revisit_uniqKey=it.shop_revisit_uniqKey

                        /*8-12-2021*/
                        shopDurationData.updated_by = Pref.user_id
                        //shopDurationData.updated_on = it.updated_on!!
                        shopDurationData.updated_on = AppUtils.getCurrentDateForShopActi()


                        if (it.pros_id!=null && !it.pros_id.equals(""))
                            shopDurationData.pros_id = it.pros_id!!
                        else
                            shopDurationData.pros_id = ""

                        if (it.agency_name!=null && !it.agency_name.equals(""))
                            shopDurationData.agency_name =it.agency_name!!
                        else
                            shopDurationData.agency_name = ""

                        if (it.approximate_1st_billing_value!=null && !it.approximate_1st_billing_value.equals(""))
                            shopDurationData.approximate_1st_billing_value = it.approximate_1st_billing_value!!
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


                        //////////////////////////
                        var revisitStatusObj=ShopRevisitStatusRequestData()
                        var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getSingleItem(shopDurationData.shop_revisit_uniqKey.toString())
                        if(data != null ){
                            revisitStatusObj.shop_id=data.shop_id
                            revisitStatusObj.order_status=data.order_status
                            revisitStatusObj.order_remarks=data.order_remarks
                            revisitStatusObj.shop_revisit_uniqKey=data.shop_revisit_uniqKey
                            revisitStatusList.add(revisitStatusObj)
                        }
                    }
                }
            }

            if (shopDataList.isEmpty()) {
                //isShopActivityUpdating = false

                val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
                if (syncedShop != null && syncedShop.isNotEmpty()) {
                    for (j in syncedShop.indices) {
                        val unSyncImage = AppDatabase.getDBInstance()!!.shopVisitImageDao().getUnSyncedData(false, syncedShop[j].shopid!!)
                        if (unSyncImage != null)
                            unSyncedList.add(unSyncImage)
                    }
                    if (unSyncedList != null && unSyncedList.isNotEmpty()) {
                        i = 0
                        BaseActivity.isShopActivityUpdating = false
                        callShopVisitImageUploadApi(unSyncedList)
                    } else {

                        /*revisitTickImg.visibility = View.VISIBLE
                        revisitSyncImg.visibility = View.GONE

                        BaseActivity.isShopActivityUpdating = false
                        calllogoutApi(Pref.user_id!!, Pref.session_token!!)*/

                        val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()
                        syncedShop.forEach {
                            val unSyncAudio = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getUnSyncedData(false, it.shopid!!)
                            if (unSyncAudio != null)
                                unSyncedAudioList.add(unSyncAudio)
                        }

                        if (unSyncedAudioList.isNotEmpty()) {
                            i = 0
                            BaseActivity.isShopActivityUpdating = false
                            callShopVisitAudioUploadApi(unSyncedAudioList)
                        } else
                            checkToRetryVisitButton()
                    }
                } else {

                    /*revisitTickImg.visibility = View.VISIBLE
                    revisitSyncImg.visibility = View.GONE

                    BaseActivity.isShopActivityUpdating = false
                    calllogoutApi(Pref.user_id!!, Pref.session_token!!)*/

                    checkToRetryVisitButton()
                }
            }
            else {

                XLog.e("====SYNC VISITED SHOP (LOGOUT SYNC)====")
                XLog.e("ShopData List size===> " + shopDataList.size)

                //val newShopList = FTStorageUtils.removeDuplicateData(shopDataList)

                val hashSet = HashSet<ShopDurationRequestData>()
                val newShopList = ArrayList<ShopDurationRequestData>()

                if (!Pref.isMultipleVisitEnable) {
                    for (i in shopDataList.indices) {
                        if (hashSet.add(shopDataList[i]))
                            newShopList.add(shopDataList[i])
                    }
                }

                val shopDurationApiReq = ShopDurationRequest()
                shopDurationApiReq.user_id = Pref.user_id
                shopDurationApiReq.session_token = Pref.session_token
                if (newShopList.size > 0) {
                    XLog.e("Unique ShopData List size===> " + newShopList.size)
                    shopDurationApiReq.shop_list = newShopList
                } else
                    shopDurationApiReq.shop_list = shopDataList

                val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

                XLog.d("callShopDurationApi (Logout Sync): REQUEST")

                revisitStatusList.clear()
                var data=AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.getUnsyncedList()
                if(data != null ){
                    for(i in data?.indices){
                        var revisitStatusObj=ShopRevisitStatusRequestData()
                        revisitStatusObj.shop_id=data?.get(i)?.shop_id!!
                        revisitStatusObj.order_status=data?.get(i)?.order_status!!
                        revisitStatusObj.order_remarks=data?.get(i)?.order_remarks!!
                        revisitStatusObj.shop_revisit_uniqKey=data?.get(i)?.shop_revisit_uniqKey!!
                        revisitStatusList.add(revisitStatusObj)
                    }
                }

                BaseActivity.compositeDisposable.add(
                        repository.shopDuration(shopDurationApiReq)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
//                        .timeout(60 * 1, TimeUnit.SECONDS)
                                .subscribe({ result ->
                                    XLog.d("callShopDurationApi : RESPONSE " + result.status)
                                    if (result.status == NetworkConstant.SUCCESS) {


                                        if(!revisitStatusList.isEmpty()){
                                            callRevisitStatusUploadApi(revisitStatusList!!)
                                        }

                                        callCompetetorImgUploadApi()


                                        if (newShopList.size > 0) {
                                            for (i in 0 until newShopList.size) {
                                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, newShopList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(newShopList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                            }
                                            BaseActivity.isShopActivityUpdating = false
                                            syncShopVisitImage(newShopList)
                                        }
                                        else {
                                            BaseActivity.isShopActivityUpdating = false

                                            if (!Pref.isMultipleVisitEnable) {
                                                for (i in 0 until shopDataList.size) {
                                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                                }

                                                syncShopVisitImage(shopDataList)
                                            }
                                            else {
                                                for (i in 0 until shopDataList.size) {
                                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!), shopDataList[i].start_timestamp!!)
                                                }

                                                // multivisit test
                                                syncShopVisitImage(shopDataList)

                                                //checkToRetryVisitButton()
                                            }
                                        }
                                    } else {
                                        BaseActivity.isShopActivityUpdating = false
                                        /*revisitTickImg.visibility = View.VISIBLE
                                        revisitSyncImg.visibility = View.GONE

                                        BaseActivity.isShopActivityUpdating = false
                                        calllogoutApi(Pref.user_id!!, Pref.session_token!!)*/

                                        checkToRetryVisitButton()
                                    }
                                }, { error ->
                                    BaseActivity.isShopActivityUpdating = false
                                    //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                    if (error == null) {
                                        XLog.d("callShopDurationApi : ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                                    } else {
                                        XLog.d("callShopDurationApi : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
                                    checkToRetryVisitButton()
//                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                                })
                )
            }
        }
        else {
            stopAnimation(revisitSyncImg)
            revisitTickImg.visibility = View.VISIBLE
            revisitSyncImg.visibility = View.GONE
            checkToCallBillingApi()
            //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
        }
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


    private fun callCompetetorImgUploadApi(){
        val unsynList = AppDatabase.getDBInstance()!!.shopVisitCompetetorImageDao().getUnSyncedCopetetorImg(Pref.user_id!!)
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
                                    callCompetetorImgUploadApi()
                                    XLog.d("FUSED LOCATION : CompetetorImg" + ", SHOP: " + shopId + ", Success: ")
                                }else{
                                    XLog.d("FUSED LOCATION : CompetetorImg" + ", SHOP: " + shopId + ", Failed: ")
                                }
                            },{
                                error ->
                                if (error != null) {
                                    XLog.d("FUSED LOCATION : CompetetorImg" + ", SHOP: " + shopId + ", ERROR: " + error.localizedMessage)
                                }
                            })
            )
       // }


    }






    private var mShopDataList: MutableList<ShopDurationRequestData>? = null
    private fun syncShopVisitImage(shopDataList: MutableList<ShopDurationRequestData>) {
        /*var unSyncedList: List<ShopVisitImageModelEntity>? = null
        for (i in shopDataList.indices) {
            unSyncedList = AppDatabase.getDBInstance()!!.shopVisitImageDao().getUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!)
        }*/
        mShopDataList = shopDataList
        val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
        for (i in shopDataList.indices) {
            val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!, shopDataList[i].visited_date!!)

            if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                unSyncedList.add(unSyncedData[0])
            }
        }

        if (unSyncedList.size > 0) {
            i = 0
            callShopVisitImageUploadApi(unSyncedList)
        } else {

            /*revisitTickImg.visibility = View.VISIBLE
            revisitSyncImg.visibility = View.GONE

            calllogoutApi(Pref.user_id!!, Pref.session_token!!)*/


            val unSyncAudioList = ArrayList<ShopVisitAudioEntity>()
            shopDataList.forEach {
                val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false, it.shop_id!!, it.visited_date!!)

                if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                    unSyncAudioList.add(unSyncedData[0])
                }
            }

            if (unSyncAudioList.isNotEmpty()) {
                i = 0
                callShopVisitAudioUploadApi(unSyncAudioList)
            } else
                checkToRetryVisitButton()
        }
    }


    private fun checkToRetryVisitButton() {
        stopAnimation(revisitSyncImg)
        BaseActivity.isShopActivityUpdating = false
        revisitSyncImg.visibility = View.GONE
        /* Get all the shop list that has been synched successfully*/
        val syncedShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(true)
        if (syncedShopList != null && syncedShopList.size > 0) {

            val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
            val syncedShop = ArrayList<ShopActivityEntity>()

            for (k in 0 until syncedShopList.size) {

                if (!Pref.isMultipleVisitEnable) {
                    /* Get shop activity that has completed time duration calculation*/
                    val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(syncedShopList[k].shop_id, true,
                            false)
                    if (shopActivity == null) {

                        val shop_activity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForTodayShop(syncedShopList[k].shop_id,
                                true, true, AppUtils.getCurrentDateForShopActi())
                        if (shop_activity != null)
                            syncedShop.add(shop_activity)

                    } else {
                        val shopDurationData = ShopDurationRequestData()
                        shopDurationData.shop_id = shopActivity?.shopid
                        shopDurationData.spent_duration = shopActivity?.duration_spent
                        shopDurationData.visited_date = shopActivity?.visited_date
                        shopDurationData.visited_time = shopActivity?.visited_date
                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity?.shopid) != null)
                            shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity?.shopid).totalVisitCount
                        else
                            shopDurationData.total_visit_count = "1"
                        shopDurationData.shop_revisit_uniqKey=shopActivity?.shop_revisit_uniqKey
                        shopDataList.add(shopDurationData)
                    }
                }
                else {
                    val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShopList(syncedShopList[k].shop_id, true, false)

                    shopActivity?.forEach {
                        val shopDurationData = ShopDurationRequestData()
                        shopDurationData.shop_id = it.shopid
                        shopDurationData.spent_duration = it.duration_spent
                        shopDurationData.visited_date = it.visited_date
                        shopDurationData.visited_time = it.visited_date
                        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid) != null)
                            shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid).totalVisitCount
                        else
                            shopDurationData.total_visit_count = "1"
                        shopDurationData.shop_revisit_uniqKey=it?.shop_revisit_uniqKey
                        shopDataList.add(shopDurationData)
                    }
                }
            }
            if (shopDataList.isEmpty()) {
                //isShopActivityUpdating = false

                val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
                if (syncedShop != null && syncedShop.isNotEmpty()) {
                    for (j in syncedShop.indices) {
                        val unSyncImage = AppDatabase.getDBInstance()!!.shopVisitImageDao().getUnSyncedData(false, syncedShop[j].shopid!!)
                        if (unSyncImage != null)
                            unSyncedList.add(unSyncImage)
                    }
                    if (unSyncedList != null && unSyncedList.isNotEmpty()) {
                        tv_revisit_retry.visibility = View.VISIBLE

                        if (!isRetryVisit)
                            checkToCallBillingApi()
                        else
                            isRetryVisit = true

                    } else {
                        val unSyncAudioList = ArrayList<ShopVisitAudioEntity>()
                        syncedShop.forEach {
                            val unSyncAudioFile = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getUnSyncedData(false, it.shopid!!)
                            unSyncAudioFile?.let { audio ->
                                unSyncAudioList.add(audio)
                            }
                        }

                        if (unSyncAudioList.isNotEmpty()) {
                            tv_revisit_retry.visibility = View.VISIBLE

                            if (!isRetryVisit)
                                checkToCallBillingApi()
                            else
                                isRetryVisit = true
                        } else {
                            tv_revisit_retry.visibility = View.GONE
                            revisitTickImg.visibility = View.VISIBLE

                            if (!isRetryVisit)
                                checkToCallBillingApi()
                            else
                                isRetryVisit = true
                        }
                    }
                } else {
                    tv_revisit_retry.visibility = View.GONE
                    revisitTickImg.visibility = View.VISIBLE

                    if (!isRetryVisit)
                        checkToCallBillingApi()
                    else
                        isRetryVisit = true
                }
            } else {
                tv_revisit_retry.visibility = View.VISIBLE

                if (!isRetryVisit)
                    checkToCallBillingApi()
                else
                    isRetryVisit = true
            }
        } else {
            tv_revisit_retry.visibility = View.GONE
            revisitTickImg.visibility = View.VISIBLE

            if (!isRetryVisit)
                checkToCallBillingApi()
            else
                isRetryVisit = true
        }
    }

    private fun callShopVisitImageUploadApi(unSyncedList: List<ShopVisitImageModelEntity>) {

        try {

            if (BaseActivity.isShopActivityUpdating)
                return

            BaseActivity.isShopActivityUpdating = true

            val visitImageShop = ShopVisitImageUploadInputModel()
            visitImageShop.session_token = Pref.session_token
            visitImageShop.user_id = Pref.user_id
            visitImageShop.shop_id = unSyncedList[i].shop_id
            visitImageShop.visit_datetime = unSyncedList[i].visit_datetime

            XLog.d("====UPLOAD REVISIT ALL IMAGE INPUT PARAMS (Logout Sync)======")
            XLog.d("USER ID====> " + visitImageShop.user_id)
            XLog.d("SESSION ID====> " + visitImageShop.session_token)
            XLog.d("SHOP ID====> " + visitImageShop.shop_id)
            XLog.d("VISIT DATE TIME=====> " + visitImageShop.visit_datetime)
            XLog.d("IMAGE=====> " + unSyncedList[i].shop_image)
            XLog.d("===============================================================")

            val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()

            BaseActivity.compositeDisposable.add(
                    repository.visitShopWithImage(visitImageShop, unSyncedList[i].shop_image!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val logoutResponse = result as BaseResponse
                                XLog.d("UPLOAD REVISIT ALL IMAGE : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                                if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.shopVisitImageDao().updateisUploaded(true, unSyncedList.get(i).shop_id!!)
                                    BaseActivity.isShopActivityUpdating = false
                                    i++
                                    if (i < unSyncedList.size)
                                        callShopVisitImageUploadApi(unSyncedList)
                                    else {
                                        i = 0
                                        checkToCallAudioApi()
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    BaseActivity.isShopActivityUpdating = false
                                    //checkToCallSyncOrder()
                                    checkToRetryVisitButton()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("UPLOAD REVISIT ALL IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                BaseActivity.isShopActivityUpdating = false
                                //checkToCallSyncOrder()
                                checkToRetryVisitButton()
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            progress_wheel.stopSpinning()
            BaseActivity.isShopActivityUpdating = false
            //checkToCallSyncOrder()
        }
    }

    private fun checkToCallAudioApi() {
        val unSyncAudioList = ArrayList<ShopVisitAudioEntity>()
        mShopDataList?.forEach {
            val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false, it.shop_id!!, it.visited_date!!)

            if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                unSyncAudioList.add(unSyncedData[0])
            }
        }

        if (unSyncAudioList.isNotEmpty()) {
            i = 0
            callShopVisitAudioUploadApi(unSyncAudioList)
        } else
            checkToRetryVisitButton()
    }


    private fun callShopVisitAudioUploadApi(unSyncedList: List<ShopVisitAudioEntity>) {

        try {

            if (BaseActivity.isShopActivityUpdating)
                return

            BaseActivity.isShopActivityUpdating = true

            val visitImageShop = ShopVisitImageUploadInputModel()
            visitImageShop.session_token = Pref.session_token
            visitImageShop.user_id = Pref.user_id
            visitImageShop.shop_id = unSyncedList[i].shop_id
            visitImageShop.visit_datetime = unSyncedList[i].visit_datetime

            XLog.d("====UPLOAD REVISIT ALL AUDIO INPUT PARAMS (Logout Sync)======")
            XLog.d("USER ID====> " + visitImageShop.user_id)
            XLog.d("SESSION ID====> " + visitImageShop.session_token)
            XLog.d("SHOP ID====> " + visitImageShop.shop_id)
            XLog.d("VISIT DATE TIME=====> " + visitImageShop.visit_datetime)
            XLog.d("AUDIO=====> " + unSyncedList[i].audio)
            XLog.d("===============================================================")

            val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()

            BaseActivity.compositeDisposable.add(
                    repository.visitShopWithAudio(visitImageShop, unSyncedList[i].audio!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val logoutResponse = result as BaseResponse
                                XLog.d("UPLOAD REVISIT ALL AUDIO : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                                if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.shopVisitAudioDao().updateisUploaded(true, unSyncedList.get(i).shop_id!!)
                                    BaseActivity.isShopActivityUpdating = false
                                    i++
                                    if (i < unSyncedList.size)
                                        callShopVisitAudioUploadApi(unSyncedList)
                                    else {

                                        checkToRetryVisitButton()

                                        i = 0
                                        //callShopDurationApi()
                                        /*BaseActivity.isShopActivityUpdating = false
                                        calllogoutApi(Pref.user_id!!, Pref.session_token!!)*/
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    BaseActivity.isShopActivityUpdating = false
                                    //checkToCallSyncOrder()
                                    checkToRetryVisitButton()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("UPLOAD REVISIT ALL AUDIO : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                BaseActivity.isShopActivityUpdating = false
                                //checkToCallSyncOrder()
                                checkToRetryVisitButton()
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            progress_wheel.stopSpinning()
            BaseActivity.isShopActivityUpdating = false
            //checkToCallSyncOrder()
        }
    }
    //==========================================Revisit Shop============================================================//


    private fun checkToCallBillingApi() {
        val list = AppDatabase.getDBInstance()!!.billingDao().getDataSyncWise(false)

        if (list != null && list.isNotEmpty()) {
            i = 0
            callAddBillApi(list[i], list)
        } else {
            stopAnimation(bill_sync_img)
            bill_tick_img.visibility = View.VISIBLE
            tv_bill_retry.visibility = View.GONE
            bill_sync_img.visibility = View.GONE

            if (!isBiilingEntry)
                checkToCallAddStockApi()
            else
                isBiilingEntry = false
        }
    }

    //================================================Add Bill==========================================================//
    private fun callAddBillApi(billing: BillingEntity, list: List<BillingEntity>) {

        if (BaseActivity.isApiInitiated)
            return

        BaseActivity.isApiInitiated = true

        val addBill = AddBillingInputParamsModel()
        addBill.bill_id = billing.bill_id
        addBill.invoice_amount = billing.invoice_amount
        addBill.invoice_date = billing.invoice_date
        addBill.invoice_no = billing.invoice_no
        addBill.remarks = billing.remarks
        addBill.order_id = billing.order_id
        addBill.session_token = Pref.session_token!!
        addBill.user_id = Pref.user_id!!
        addBill.patient_no = billing.patient_no
        addBill.patient_name = billing.patient_name
        addBill.patient_address = billing.patient_address

        val orderProductList = AppDatabase.getDBInstance()!!.billProductDao().getDataAccordingToBillId(addBill.bill_id)
        val productList = ArrayList<AddOrderInputProductList>()

        for (i in orderProductList.indices) {
            val product = AddOrderInputProductList()
            product.id = orderProductList[i].product_id
            product.qty = orderProductList[i].qty
            product.rate = orderProductList[i].rate
            product.total_price = orderProductList[i].total_price
            product.product_name = orderProductList[i].product_name
            productList.add(product)
        }

        addBill.product_list = productList

        XLog.d("======SYNC BILLING DETAILS INPUT PARAMS (SYNC ALL)======")
        XLog.d("USER ID===> " + addBill.user_id)
        XLog.d("SESSION ID====> " + addBill.session_token)
        XLog.d("BILL ID====> " + addBill.bill_id)
        XLog.d("INVOICE NO.====> " + addBill.invoice_no)
        XLog.d("INVOICE DATE====> " + addBill.invoice_date)
        XLog.d("INVOICE AMOUNT====> " + addBill.invoice_amount)
        XLog.d("REMARKS====> " + addBill.remarks)
        XLog.d("ORDER ID====> " + addBill.order_id)

        try {
            XLog.d("PATIENT NO====> " + addBill.patient_no)
            XLog.d("PATIENT NAME====> " + addBill.patient_name)
            XLog.d("PATIENT ADDRESS====> " + addBill.patient_address)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        if (!TextUtils.isEmpty(billing.attachment))
            XLog.d("ATTACHMENT=======> " + billing.attachment)

        XLog.d("PRODUCT LIST SIZE====> " + addBill.product_list?.size)
        XLog.d("=========================================================")

        if (!TextUtils.isEmpty(billing.attachment)) {
            val repository = AddBillingRepoProvider.addBillImageRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.addBillingDetailsMultipart(addBill, billing.attachment, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val baseResponse = result as BaseResponse
                                XLog.d("SYNC BILLING DETAILS : " + "RESPONSE : " + baseResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + baseResponse.message)
                                BaseActivity.isApiInitiated = false

                                if (baseResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.billingDao().updateIsUploadedBillingIdWise(true, addBill.bill_id)

                                    i++
                                    if (i < list.size) {
                                        callAddBillApi(list[i], list)
                                    } else {
                                        stopAnimation(bill_sync_img)
                                        val list_ = AppDatabase.getDBInstance()!!.billingDao().getDataSyncWise(false)
                                        if (list_ != null && list_.isNotEmpty())
                                            tv_bill_retry.visibility = View.VISIBLE
                                        else {
                                            tv_bill_retry.visibility = View.GONE
                                            bill_tick_img.visibility = View.VISIBLE
                                        }
                                        bill_sync_img.visibility = View.GONE

                                        i = 0
                                        progress_wheel.stopSpinning()
                                        if (!isBiilingEntry)
                                            checkToCallAddStockApi()
                                        else
                                            isBiilingEntry = false
                                    }
                                } else {
                                    i++
                                    if (i < list.size) {
                                        callAddBillApi(list[i], list)
                                    } else {
                                        stopAnimation(bill_sync_img)
                                        tv_bill_retry.visibility = View.VISIBLE
                                        bill_sync_img.visibility = View.GONE
                                        i = 0
                                        progress_wheel.stopSpinning()
                                        if (!isBiilingEntry)
                                            checkToCallAddStockApi()
                                        else
                                            isBiilingEntry = false
                                    }
                                }

                                progress_wheel.stopSpinning()

                            }, { error ->
                                XLog.d("SYNC BILLING DETAILS : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                BaseActivity.isApiInitiated = false

                                i++
                                if (i < list.size) {
                                    callAddBillApi(list[i], list)
                                } else {
                                    stopAnimation(bill_sync_img)
                                    tv_bill_retry.visibility = View.VISIBLE
                                    bill_sync_img.visibility = View.GONE
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    if (!isBiilingEntry)
                                        checkToCallAddStockApi()
                                    else
                                        isBiilingEntry = false
                                }

                            })
            )
        } else {
            val repository = AddBillingRepoProvider.addBillRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.addBillingDetails(addBill)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val baseResponse = result as BaseResponse
                                XLog.d("SYNC BILLING DETAILS : " + "RESPONSE : " + baseResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + baseResponse.message)
                                BaseActivity.isApiInitiated = false

                                if (baseResponse.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.billingDao().updateIsUploadedBillingIdWise(true, addBill.bill_id)

                                    i++
                                    if (i < list.size) {
                                        callAddBillApi(list[i], list)
                                    } else {
                                        stopAnimation(bill_sync_img)
                                        val list_ = AppDatabase.getDBInstance()!!.billingDao().getDataSyncWise(false)
                                        if (list_ != null && list_.isNotEmpty())
                                            tv_bill_retry.visibility = View.VISIBLE
                                        else {
                                            tv_bill_retry.visibility = View.GONE
                                            bill_tick_img.visibility = View.VISIBLE
                                        }
                                        bill_sync_img.visibility = View.GONE

                                        i = 0
                                        progress_wheel.stopSpinning()
                                        if (!isBiilingEntry)
                                            checkToCallAddStockApi()
                                        else
                                            isBiilingEntry = false
                                    }
                                } else {
                                    i++
                                    if (i < list.size) {
                                        callAddBillApi(list[i], list)
                                    } else {
                                        stopAnimation(bill_sync_img)
                                        tv_bill_retry.visibility = View.VISIBLE
                                        bill_sync_img.visibility = View.GONE
                                        i = 0
                                        progress_wheel.stopSpinning()
                                        if (!isBiilingEntry)
                                            checkToCallAddStockApi()
                                        else
                                            isBiilingEntry = false
                                    }
                                }

                                progress_wheel.stopSpinning()

                            }, { error ->
                                XLog.d("SYNC BILLING DETAILS : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                BaseActivity.isApiInitiated = false

                                i++
                                if (i < list.size) {
                                    callAddBillApi(list[i], list)
                                } else {
                                    stopAnimation(bill_sync_img)
                                    tv_bill_retry.visibility = View.VISIBLE
                                    bill_sync_img.visibility = View.GONE
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    if (!isBiilingEntry)
                                        checkToCallAddStockApi()
                                    else
                                        isBiilingEntry = false
                                }

                            })
            )
        }
    }
    //================================================Add Bill==========================================================//

    private fun checkToCallAddStockApi() {
        if (Pref.willStockShow) {
            val list = AppDatabase.getDBInstance()!!.stockDetailsListDao().getUnsyncedData(false)

            if (list != null && list.isNotEmpty()) {
                i = 0
                callAddStockApi(list[i], list)
            } else {
                stopAnimation(stock_sync_img)
                stock_tick_img.visibility = View.VISIBLE
                tv_stock_retry.visibility = View.GONE
                stock_sync_img.visibility = View.GONE

                if (!isRetryStock)
                    checkToCallMeetingApi()
                else
                    isRetryStock = false
            }
        } else
            checkToCallMeetingApi()
    }

    //===============================================Add Stock============================================================//
    private fun callAddStockApi(stockDetailsListEntity: StockDetailsListEntity, stockList: List<StockDetailsListEntity>) {
        val addStock = AddStockInputParamsModel()
        addStock.stock_amount = stockDetailsListEntity.amount
        addStock.stock_date_time = stockDetailsListEntity.date
        addStock.stock_id = stockDetailsListEntity.stock_id
        addStock.shop_id = stockDetailsListEntity.shop_id
        addStock.session_token = Pref.session_token
        addStock.user_id = Pref.user_id
        addStock.latitude = stockDetailsListEntity.stock_lat
        addStock.longitude = stockDetailsListEntity.stock_long

        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(addStock.shop_id!!)

        if (shopActivity != null) {
            if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(addStock.shop_id)

                if (!TextUtils.isEmpty(shopDetail.address))
                    addStock.address = shopDetail.address
                else
                    addStock.address = ""
            } else {
                if (!TextUtils.isEmpty(stockDetailsListEntity.stock_lat) && !TextUtils.isEmpty(stockDetailsListEntity.stock_long))
                    addStock.address = LocationWizard.getLocationName(mContext, stockDetailsListEntity.stock_lat!!.toDouble(), stockDetailsListEntity.stock_long!!.toDouble())
                else
                    addStock.address = ""
            }
        } else {
            if (!TextUtils.isEmpty(stockDetailsListEntity.stock_lat) && !TextUtils.isEmpty(stockDetailsListEntity.stock_long))
                addStock.address = LocationWizard.getLocationName(mContext, stockDetailsListEntity.stock_lat!!.toDouble(), stockDetailsListEntity.stock_long!!.toDouble())
            else
                addStock.address = ""
        }

        val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(stockDetailsListEntity.shop_id)
        addStock.shop_type = addShop?.type

        val list = AppDatabase.getDBInstance()!!.stockProductDao().getDataAccordingToShopAndStockId(stockDetailsListEntity.stock_id!!, stockDetailsListEntity.shop_id!!)
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

                            XLog.e("Add STOCK : \n" + ", SHOP ID===> " + stockList[i].shop_id + ", STOCK ID===> " + stockList[i].stock_id + ", STATUS====> " + orderList.status + ",RESPONSE MESSAGE:" + orderList.message)

                            if (orderList.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.stockDetailsListDao().updateIsUploaded(true, stockDetailsListEntity.stock_id!!)
                            }

                            i++
                            if (i < stockList.size) {
                                callAddStockApi(stockList[i], stockList)
                            } else {
                                stopAnimation(stock_sync_img)
                                val list_ = AppDatabase.getDBInstance()!!.stockDetailsListDao().getUnsyncedData(false)
                                if (list_ != null && list_.isNotEmpty())
                                    tv_stock_retry.visibility = View.VISIBLE
                                else {
                                    tv_stock_retry.visibility = View.GONE
                                    stock_tick_img.visibility = View.VISIBLE
                                }
                                stock_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                if (!isRetryStock)
                                    checkToCallMeetingApi()
                                else
                                    isRetryStock = false
                            }


                        }, { error ->
                            error.printStackTrace()

                            XLog.d("Add STOCK : \n" + ", SHOP ID===> " + stockList[i].shop_id + ", STOCK ID===> " + stockList[i].stock_id + ", ERROR====> " + error.message)

                            i++
                            if (i < list.size) {
                                callAddStockApi(stockList[i], stockList)
                            } else {
                                stopAnimation(stock_sync_img)
                                val list_ = AppDatabase.getDBInstance()!!.stockDetailsListDao().getUnsyncedData(false)
                                if (list_ != null && list_.isNotEmpty())
                                    tv_stock_retry.visibility = View.VISIBLE
                                else {
                                    tv_stock_retry.visibility = View.GONE
                                    stock_tick_img.visibility = View.VISIBLE
                                }
                                stock_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                if (!isRetryStock)
                                    checkToCallMeetingApi()
                                else
                                    isRetryStock = false
                            }
                        })
        )
    }
    //============================================================Add Stock=====================================================================//


    private var isFromLogout = false
    private fun checkToCallMeetingApi() {
        if (Pref.isMeetingAvailable) {
            val list = AppDatabase.getDBInstance()!!.addMeetingDao().durationAvailableSyncWise(true, false)

            isFromLogout = false
            if (list != null && list.isNotEmpty()) {
                isFromLogout = true
                callMeetingUploadApi(list)
            } else {
                stopAnimation(meeting_sync_img)
                meeting_tick_img.visibility = View.VISIBLE
                tv_meeting_retry.visibility = View.GONE
                meeting_sync_img.visibility = View.GONE

                if (!isRetryMeeting)
                    checkToCallAddQuotApi()
                else
                    isRetryMeeting = false
            }
        } else {
            if (!isRetryMeeting)
                checkToCallAddQuotApi()
            else
                isRetryMeeting = false
        }
    }


    //==========================================================Upload Meeting====================================================//
    private fun callMeetingUploadApi(list: List<MeetingEntity>) {

        XLog.e("IS MEETING UPDATING (LOGOUT SYNC)===========> ${BaseActivity.isMeetingUpdating}")

        if (BaseActivity.isMeetingUpdating) {

            if (isFromLogout) {
                stopAnimation(meeting_sync_img)
                meeting_tick_img.visibility = View.GONE
                tv_meeting_retry.visibility = View.VISIBLE
                meeting_sync_img.visibility = View.GONE
                //tv_logout.isEnabled = true

                if (!isRetryMeeting)
                    checkToCallAddQuotApi()
                else
                    isRetryMeeting = false
            }

            return
        }

        BaseActivity.isMeetingUpdating = true

        val meeting = MeetingDurationInputParams()
        meeting.session_token = Pref.session_token!!
        meeting.user_id = Pref.user_id!!

        val meetingDataList = ArrayList<MeetingDurationDataModel>()

        for (i in list.indices) {
            val meetingData = MeetingDurationDataModel()
            meetingData.duration = list[i].duration_spent!!
            meetingData.latitude = list[i].lattitude!!
            meetingData.longitude = list[i].longitude!!
            meetingData.remarks = list[i].remakrs!!
            meetingData.meeting_type_id = list[i].meetingTypeId!!
            meetingData.distance_travelled = list[i].distance_travelled!!
            meetingData.date = list[i].date!!
            meetingData.address = list[i].address!!
            meetingData.pincode = list[i].pincode!!
            meetingData.date_time = list[i].date_time!!

            meetingDataList.add(meetingData)
        }

        meeting.meeting_list = meetingDataList

        XLog.d("========UPLOAD MEETING DATA INPUT PARAMS (LOGOUT SYNC)======")
        XLog.d("USER ID======> " + meeting.user_id)
        XLog.d("SESSION ID======> " + meeting.session_token)
        XLog.d("MEETING LIST SIZE=========> " + meeting.meeting_list.size)
        XLog.d("=============================================================")

        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()
        BaseActivity.compositeDisposable.add(
                repository.meetingDuration(meeting)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("UPLOAD MEETING DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                doAsync {
                                    for (i in list.indices) {
                                        AppDatabase.getDBInstance()!!.addMeetingDao().updateIsUploaded(true, list[i].id)
                                    }

                                    uiThread {
                                        BaseActivity.isMeetingUpdating = false
                                        stopAnimation(meeting_sync_img)
                                        meeting_tick_img.visibility = View.VISIBLE
                                        tv_meeting_retry.visibility = View.GONE
                                        meeting_sync_img.visibility = View.GONE

                                        if (!isRetryMeeting)
                                            checkToCallAddQuotApi()
                                        else
                                            isRetryMeeting = false
                                    }
                                }
                            } else {
                                BaseActivity.isMeetingUpdating = false
                                stopAnimation(meeting_sync_img)
                                tv_meeting_retry.visibility = View.VISIBLE
                                meeting_sync_img.visibility = View.GONE

                                if (!isRetryMeeting)
                                    checkToCallAddQuotApi()
                                else
                                    isRetryMeeting = false
                            }

                        }, { error ->
                            XLog.d("UPLOAD MEETING DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            BaseActivity.isMeetingUpdating = false

                            stopAnimation(meeting_sync_img)
                            tv_meeting_retry.visibility = View.VISIBLE
                            meeting_sync_img.visibility = View.GONE

                            if (!isRetryMeeting)
                                checkToCallAddQuotApi()
                            else
                                isRetryMeeting = false
                        })
        )

    }
    //==========================================================Upload Meeting=======================================================//


    private fun checkToCallAddQuotApi() {
        if (Pref.isQuotationShow) {
            val list = AppDatabase.getDBInstance()!!.quotDao().getQuotSyncWise(false) as ArrayList<QuotationEntity>

            if (list != null && list.isNotEmpty()) {
                val quotList = ArrayList<QuotationEntity>()

                list.forEach {
                    val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shop_id)
                    if (shop.isUploaded)
                        quotList.add(it)

                }

                if (quotList != null && quotList.isNotEmpty()) {
                    i = 0
                    callAddQuotApi(quotList[i], quotList)
                } else {
                    checkToEditQuotationCall()
                }
            } else {

                checkToEditQuotationCall()
            }
        } else {
            checkToCallUpdateAddress()
        }
    }

    //=====================================================ADD QUOTATION===========================================================
    private fun callAddQuotApi(quotEntity: QuotationEntity, quotList: ArrayList<QuotationEntity>) {
        XLog.d("==============Sync Add Quot. Input Params (Logout Sync)====================")
        XLog.d("shop id=======> " + quotEntity.shop_id)
        XLog.d("quot. date=======> " + quotEntity.date)
        XLog.d("quot. id=======> " + quotEntity.quo_id)
        XLog.d("quot. no=======> " + quotEntity.quo_no)
        XLog.d("hypothecation=======> " + quotEntity.hypothecation)
        XLog.d("account_no=======> " + quotEntity.account_no)
        XLog.d("model_id=======> " + quotEntity.model_id)
        XLog.d("bs_id=======> " + quotEntity.bs_id)
        XLog.d("gearbox=======> " + quotEntity.gearbox)
        XLog.d("number1=======> " + quotEntity.number1)
        XLog.d("value1=======> " + quotEntity.value1)
        XLog.d("value2=======> " + quotEntity.value2)
        XLog.d("tyres1=======> " + quotEntity.tyres1)
        XLog.d("number2=======> " + quotEntity.number2)
        XLog.d("value3=======> " + quotEntity.value3)
        XLog.d("value4=======> " + quotEntity.value4)
        XLog.d("tyres2=======> " + quotEntity.tyres2)
        XLog.d("amount=======> " + quotEntity.amount)
        XLog.d("discount=======> " + quotEntity.discount)
        XLog.d("cgst=======> " + quotEntity.cgst)
        XLog.d("sgst=======> " + quotEntity.sgst)
        XLog.d("tcs=======> " + quotEntity.tcs)
        XLog.d("insurance=======> " + quotEntity.insurance)
        XLog.d("net_amount=======> " + quotEntity.net_amount)
        XLog.d("remarks=======> " + quotEntity.remarks)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("=====================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        val addQuot = AddQuotInputModel(Pref.session_token!!, Pref.user_id!!, quotEntity.shop_id!!, quotEntity.quo_id!!,
                quotEntity.quo_no!!, quotEntity.date!!, quotEntity.hypothecation!!, quotEntity.account_no!!, quotEntity.model_id!!,
                quotEntity.bs_id!!, quotEntity.gearbox!!, quotEntity.number1!!, quotEntity.value1!!, quotEntity.value2!!,
                quotEntity.tyres1!!, quotEntity.number2!!, quotEntity.value3!!, quotEntity.value4!!, quotEntity.tyres2!!, quotEntity.amount!!,
                quotEntity.discount!!, quotEntity.cgst!!, quotEntity.sgst!!, quotEntity.tcs!!, quotEntity.insurance!!, quotEntity.net_amount!!,
                quotEntity.remarks!!)

        BaseActivity.compositeDisposable.add(
                repository.addQuot(addQuot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("ADD QUOT. DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.quotDao()?.updateIsUploaded(true, quotEntity.quo_id!!)
                            }

                            i++
                            if (i < quotList.size) {
                                callAddQuotApi(quotList[i], quotList)
                            } else {
                                stopAnimation(quot_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.quotDao()?.getQuotSyncWise(false)
                                if (list_ != null && list_.isNotEmpty())
                                    tv_quot_retry.visibility = View.VISIBLE
                                else {
                                    tv_quot_retry.visibility = View.GONE
                                    quot_tick_img.visibility = View.VISIBLE
                                }
                                quot_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                checkToEditQuotationCall()
                            }


                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("ADD QUOT. DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()

                            i++
                            if (i < quotList.size) {
                                callAddQuotApi(quotList[i], quotList)
                            } else {
                                stopAnimation(quot_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.quotDao()?.getQuotSyncWise(false)
                                if (list_ != null && list_.isNotEmpty())
                                    tv_quot_retry.visibility = View.VISIBLE
                                else {
                                    tv_quot_retry.visibility = View.GONE
                                    quot_tick_img.visibility = View.VISIBLE
                                }
                                quot_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                checkToEditQuotationCall()
                            }
                        })
        )
    }
    //===========================================================ADD QUOT========================================================

    private fun checkToEditQuotationCall() {
        val editList = AppDatabase.getDBInstance()!!.quotDao().getQuotEditSyncWise(0)

        if (editList != null && editList.isNotEmpty()) {
            val quotList = ArrayList<QuotationEntity>()

            editList.forEach {
                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shop_id)
                if (shop.isUploaded)
                    quotList.add(it)

            }

            if (quotList != null && quotList.isNotEmpty()) {
                i = 0
                callEditQuotApi(quotList[i], quotList)
            } else {
                stopAnimation(quot_sync_img)
                quot_tick_img.visibility = View.VISIBLE
                tv_quot_retry.visibility = View.GONE
                quot_sync_img.visibility = View.GONE

                if (!isRetryQuotation)
                    checkToCallUpdateAddress()
                else
                    isRetryQuotation = false
            }
        } else {
            stopAnimation(quot_sync_img)
            quot_tick_img.visibility = View.VISIBLE
            tv_quot_retry.visibility = View.GONE
            quot_sync_img.visibility = View.GONE

            if (!isRetryQuotation)
                checkToCallUpdateAddress()
            else
                isRetryQuotation = false
        }
    }

    //=====================================================EDIT QUOTATION===========================================================
    private fun callEditQuotApi(quotEntity: QuotationEntity, quotList: ArrayList<QuotationEntity>) {
        XLog.d("==============Sync Edit Quot. Input Params (Logout Sync)====================")
        XLog.d("shop id=======> " + quotEntity.shop_id)
        XLog.d("quot. date=======> " + quotEntity.date)
        XLog.d("quot. id=======> " + quotEntity.quo_id)
        XLog.d("quot. no=======> " + quotEntity.quo_no)
        XLog.d("hypothecation=======> " + quotEntity.hypothecation)
        XLog.d("account_no=======> " + quotEntity.account_no)
        XLog.d("model_id=======> " + quotEntity.model_id)
        XLog.d("bs_id=======> " + quotEntity.bs_id)
        XLog.d("gearbox=======> " + quotEntity.gearbox)
        XLog.d("number1=======> " + quotEntity.number1)
        XLog.d("value1=======> " + quotEntity.value1)
        XLog.d("value2=======> " + quotEntity.value2)
        XLog.d("tyres1=======> " + quotEntity.tyres1)
        XLog.d("number2=======> " + quotEntity.number2)
        XLog.d("value3=======> " + quotEntity.value3)
        XLog.d("value4=======> " + quotEntity.value4)
        XLog.d("tyres2=======> " + quotEntity.tyres2)
        XLog.d("amount=======> " + quotEntity.amount)
        XLog.d("discount=======> " + quotEntity.discount)
        XLog.d("cgst=======> " + quotEntity.cgst)
        XLog.d("sgst=======> " + quotEntity.sgst)
        XLog.d("tcs=======> " + quotEntity.tcs)
        XLog.d("insurance=======> " + quotEntity.insurance)
        XLog.d("net_amount=======> " + quotEntity.net_amount)
        XLog.d("remarks=======> " + quotEntity.remarks)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("=====================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        val addQuot = AddQuotInputModel(Pref.session_token!!, Pref.user_id!!, quotEntity.shop_id!!, quotEntity.quo_id!!,
                quotEntity.quo_no!!, quotEntity.date!!, quotEntity.hypothecation!!, quotEntity.account_no!!, quotEntity.model_id!!,
                quotEntity.bs_id!!, quotEntity.gearbox!!, quotEntity.number1!!, quotEntity.value1!!, quotEntity.value2!!,
                quotEntity.tyres1!!, quotEntity.number2!!, quotEntity.value3!!, quotEntity.value4!!, quotEntity.tyres2!!, quotEntity.amount!!,
                quotEntity.discount!!, quotEntity.cgst!!, quotEntity.sgst!!, quotEntity.tcs!!, quotEntity.insurance!!, quotEntity.net_amount!!,
                quotEntity.remarks!!)

        BaseActivity.compositeDisposable.add(
                repository.addQuot(addQuot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("EDIT QUOT. DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.quotDao()?.updateIsEdit(1, quotEntity.quo_id!!)
                            }

                            i++
                            if (i < quotList.size) {
                                callEditQuotApi(quotList[i], quotList)
                            } else {
                                stopAnimation(quot_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.quotDao()?.getQuotSyncWise(false)
                                if (list_ != null && list_.isNotEmpty())
                                    tv_quot_retry.visibility = View.VISIBLE
                                else {
                                    tv_quot_retry.visibility = View.GONE
                                    quot_tick_img.visibility = View.VISIBLE
                                }
                                quot_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                if (!isRetryQuotation)
                                    checkToCallUpdateAddress()
                                else
                                    isRetryQuotation = false
                            }


                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("EDIT QUOT. DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()

                            i++
                            if (i < quotList.size) {
                                callEditQuotApi(quotList[i], quotList)
                            } else {
                                stopAnimation(quot_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.quotDao()?.getQuotSyncWise(false)
                                if (list_ != null && list_.isNotEmpty())
                                    tv_quot_retry.visibility = View.VISIBLE
                                else {
                                    tv_quot_retry.visibility = View.GONE
                                    quot_tick_img.visibility = View.VISIBLE
                                }
                                quot_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                if (!isRetryQuotation)
                                    checkToCallUpdateAddress()
                                else
                                    isRetryQuotation = false
                            }
                        })
        )
    }
    //===========================================================EDIT QUOT========================================================

    private fun checkToCallUpdateAddress() {
        if (Pref.isOfflineTeam) {
            val list = AppDatabase.getDBInstance()?.memberShopDao()?.getShopSyncWise(false)
            if (list != null && list.isNotEmpty()) {
                i = 0
                callUpdateAddressApi(list[i], list as ArrayList<MemberShopEntity>)
            } else {
                stopAnimation(team_sync_img)
                team_tick_img.visibility = View.VISIBLE
                tv_team_retry.visibility = View.GONE
                team_sync_img.visibility = View.GONE

                if (!isRetryTeamShop)
                    checkToCallTimesheet()
                else
                    isRetryTeamShop = false
            }
        } else {
            stopAnimation(team_sync_img)
            team_tick_img.visibility = View.VISIBLE
            tv_team_retry.visibility = View.GONE
            team_sync_img.visibility = View.GONE

            if (!isRetryTeamShop)
                checkToCallTimesheet()
            else
                isRetryTeamShop = false
        }
    }


    //=====================================================UPDATE TEAM SHOP ADDRESS===========================================================
    private fun callUpdateAddressApi(team: MemberShopEntity, teamList: ArrayList<MemberShopEntity>) {
        XLog.d("==============Sync Team Shop Input Params (Logout Sync)====================")
        XLog.d("shop id=======> " + team.shop_id)
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("shop_lat=======> " + team.shop_lat)
        XLog.d("shop_long=======> " + team.shop_long)
        XLog.d("shop_address=======> " + team.shop_address)
        XLog.d("shop_pincode=======> " + team.shop_pincode)
        XLog.d("isAddressUpdated=======> 1")
        XLog.d("===========================================================================")

        progress_wheel.spin()
        val repository = ShopAddressUpdateRepoProvider.provideShopAddressUpdateRepo()

        val addressUpdateReq = AddressUpdateRequest()
        addressUpdateReq.apply {
            user_id = Pref.user_id
            shop_id = team.shop_id
            shop_lat = team.shop_lat
            shop_long = team.shop_long
            shop_address = team.shop_address
            isAddressUpdated = "1"
            pincode = team.shop_pincode
        }

        BaseActivity.compositeDisposable.add(
                repository.getShopAddressUpdate(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("UPDATE ADDRESS DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.memberShopDao()?.updateIsUploaded(true, team.shop_id!!)
                            }

                            i++
                            if (i < teamList.size) {
                                callUpdateAddressApi(teamList[i], teamList)
                            } else {
                                stopAnimation(team_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.memberShopDao()?.getShopSyncWise(false)
                                if (list_ != null && list_.isNotEmpty())
                                    tv_team_retry.visibility = View.VISIBLE
                                else {
                                    tv_team_retry.visibility = View.GONE
                                    team_tick_img.visibility = View.VISIBLE
                                }
                                team_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                if (!isRetryTeamShop)
                                    checkToCallTimesheet()
                                else
                                    isRetryTeamShop = false
                            }


                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("UPDATE ADDRESS DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()

                            i++
                            if (i < teamList.size) {
                                callUpdateAddressApi(teamList[i], teamList)
                            } else {
                                stopAnimation(team_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.memberShopDao()?.getShopSyncWise(false)
                                if (list_ != null && list_.isNotEmpty())
                                    tv_team_retry.visibility = View.VISIBLE
                                else {
                                    tv_team_retry.visibility = View.GONE
                                    team_tick_img.visibility = View.VISIBLE
                                }
                                team_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                if (!isRetryTeamShop)
                                    checkToCallTimesheet()
                                else
                                    isRetryTeamShop = false
                            }
                        })
        )
    }
    //===========================================================UPDATE TEAM SHOP ADDRESS========================================================//


    private fun checkToCallTimesheet() {
        if (Pref.willTimesheetShow) {
            val list = AppDatabase.getDBInstance()?.timesheetDao()?.getTimesheetSyncWise(false)
            if (list != null && list.isNotEmpty()) {
                i = 0
                callAddTimesheetApi(list[i], list as ArrayList<TimesheetListEntity>)
            } else {
                stopAnimation(timesheet_sync_img)
                timesheet_tick_img.visibility = View.VISIBLE
                tv_timesheet_retry.visibility = View.GONE
                timesheet_sync_img.visibility = View.GONE

                if (!isRetryTimesheet)
                    checkToCallTask()
                else
                    isRetryTimesheet = false
            }
        } else {
            stopAnimation(timesheet_sync_img)
            timesheet_tick_img.visibility = View.VISIBLE
            tv_timesheet_retry.visibility = View.GONE
            timesheet_sync_img.visibility = View.GONE

            if (!isRetryTimesheet)
                checkToCallTask()
            else
                isRetryTimesheet = false
        }
    }


    //=====================================================ADD TIMESHEET===========================================================
    private fun callAddTimesheetApi(timeSheet: TimesheetListEntity, list: List<TimesheetListEntity>) {
        XLog.d("==============Sync Timesheet Input Params (Logout Sync)====================")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("date=======> " + timeSheet.date)
        XLog.d("client_id=======> " + timeSheet.client_id)
        XLog.d("project_id=======> " + timeSheet.project_id)
        XLog.d("activity_id=======> " + timeSheet.activity_id)
        XLog.d("product_id=======> " + timeSheet.product_id)
        XLog.d("time=======> " + timeSheet.time)
        XLog.d("comments=======> " + timeSheet.comments)
        XLog.d("timesheet_id=======> " + timeSheet.timesheet_id)
        XLog.d("image=======> " + timeSheet.image)
        XLog.d("===========================================================================")

        progress_wheel.spin()

        val addIntput = AddTimeSheetInputModel(Pref.session_token!!, Pref.user_id!!, timeSheet.date!!, timeSheet.client_id!!,
                timeSheet.project_id!!, timeSheet.activity_id!!, timeSheet.product_id!!, timeSheet.time!!, timeSheet.comments!!,
                timeSheet.timesheet_id!!)

        if (TextUtils.isEmpty(timeSheet.image)) {
            val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.addTimeSheet(addIntput)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                XLog.d("ADD TIMESHEET: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.timesheetDao()?.updateIsUploaded(true, timeSheet.timesheet_id!!)
                                }

                                i++
                                if (i < list.size) {
                                    callAddTimesheetApi(list[i], list)
                                } else {
                                    stopAnimation(timesheet_sync_img)
                                    val list_ = AppDatabase.getDBInstance()?.timesheetDao()?.getTimesheetSyncWise(false)
                                    if (list_ != null && list_.isNotEmpty())
                                        tv_timesheet_retry.visibility = View.VISIBLE
                                    else {
                                        tv_timesheet_retry.visibility = View.GONE
                                        timesheet_tick_img.visibility = View.VISIBLE
                                    }
                                    timesheet_sync_img.visibility = View.GONE

                                    i = 0
                                    progress_wheel.stopSpinning()

                                    if (!isRetryTimesheet)
                                        checkToCallTask()
                                    else
                                        isRetryTimesheet = false
                                }


                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("ADD TIMESHEET: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()

                                i++
                                if (i < list.size) {
                                    callAddTimesheetApi(list[i], list)
                                } else {
                                    stopAnimation(timesheet_sync_img)
                                    val list_ = AppDatabase.getDBInstance()?.timesheetDao()?.getTimesheetSyncWise(false)
                                    if (list_ != null && list_.isNotEmpty())
                                        tv_timesheet_retry.visibility = View.VISIBLE
                                    else {
                                        tv_timesheet_retry.visibility = View.GONE
                                        timesheet_tick_img.visibility = View.VISIBLE
                                    }
                                    timesheet_sync_img.visibility = View.GONE

                                    i = 0
                                    progress_wheel.stopSpinning()

                                    if (!isRetryTimesheet)
                                        checkToCallTask()
                                    else
                                        isRetryTimesheet = false
                                }
                            })
            )
        }
        else {
            val repository = TimeSheetRepoProvider.timeSheetImageRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.addTimesheetWithImage(addIntput, timeSheet.image!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                XLog.d("ADD TIMESHEET: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.timesheetDao()?.updateIsUploaded(true, timeSheet.timesheet_id!!)
                                }

                                i++
                                if (i < list.size) {
                                    callAddTimesheetApi(list[i], list)
                                } else {
                                    stopAnimation(timesheet_sync_img)
                                    val list_ = AppDatabase.getDBInstance()?.timesheetDao()?.getTimesheetSyncWise(false)
                                    if (list_ != null && list_.isNotEmpty())
                                        tv_timesheet_retry.visibility = View.VISIBLE
                                    else {
                                        tv_timesheet_retry.visibility = View.GONE
                                        timesheet_tick_img.visibility = View.VISIBLE
                                    }
                                    timesheet_sync_img.visibility = View.GONE

                                    i = 0
                                    progress_wheel.stopSpinning()

                                    if (!isRetryTimesheet)
                                        checkToCallTask()
                                    else
                                        isRetryTimesheet = false
                                }


                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("ADD TIMESHEET: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()

                                i++
                                if (i < list.size) {
                                    callAddTimesheetApi(list[i], list)
                                } else {
                                    stopAnimation(timesheet_sync_img)
                                    val list_ = AppDatabase.getDBInstance()?.timesheetDao()?.getTimesheetSyncWise(false)
                                    if (list_ != null && list_.isNotEmpty())
                                        tv_timesheet_retry.visibility = View.VISIBLE
                                    else {
                                        tv_timesheet_retry.visibility = View.GONE
                                        timesheet_tick_img.visibility = View.VISIBLE
                                    }
                                    timesheet_sync_img.visibility = View.GONE

                                    i = 0
                                    progress_wheel.stopSpinning()

                                    if (!isRetryTimesheet)
                                        checkToCallTask()
                                    else
                                        isRetryTimesheet = false
                                }
                            })
            )
        }
    }
    //===========================================================ADD TIMESHEET========================================================


    private fun checkToCallTask() {
        if (Pref.isTaskEnable) {
            val list = AppDatabase.getDBInstance()?.taskDao()?.getTaskSyncWise(false)
            if (list != null && list.isNotEmpty()) {
                i = 0
                callAddTaskApi(list[i], list as ArrayList<TaskEntity>)
            } else {

                val list_ = AppDatabase.getDBInstance()?.taskDao()?.getTaskStatusWise(0)
                if (list_ != null && list_.isNotEmpty()) {
                    i = 0
                    callStatusUpdateApi(list_[i], list_ as ArrayList<TaskEntity>)
                }
                else {
                    stopAnimation(task_sync_img)
                    task_tick_img.visibility = View.VISIBLE
                    tv_task_retry.visibility = View.GONE
                    task_sync_img.visibility = View.GONE

                    if (!isRetryTask)
                        checkToCallDocument()
                    else
                        isRetryTask = false
                }
            }
        } else {
            stopAnimation(task_sync_img)
            task_tick_img.visibility = View.VISIBLE
            tv_task_retry.visibility = View.GONE
            task_sync_img.visibility = View.GONE

            if (!isRetryTask)
                checkToCallDocument()
            else
                isRetryTask = false
        }
    }
    //========================================================Task================================================================//

    private fun callAddTaskApi(task: TaskEntity, taskList: ArrayList<TaskEntity>) {
        XLog.d("==============Sync Add Task Input Params (Logout Sync)====================")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("date=======> " + task.date)
        XLog.d("task_id=======> " + task.task_id)
        XLog.d("task_name=======> " + task.task_name)
        XLog.d("details=======> " + task.details)
        XLog.d("isCompleted=======> " + task.isCompleted)
        XLog.d("eventId=======> " + task.eventId)
        XLog.d("===================================================================")

        val taskInput = AddTaskInputModel(Pref.session_token!!, Pref.user_id!!, task.task_id!!, task.date!!, task.task_name!!,
                task.details!!, task.isCompleted, task.eventId)

        progress_wheel.spin()
        val repository = TaskRepoProvider.taskRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.addTask(taskInput)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("ADD TASK: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.taskDao()?.updateIsUploaded(true, task.task_id!!)
                            }

                            i++
                            if (i < taskList.size) {
                                callAddTaskApi(taskList[i], taskList)
                            } else {

                                val list_ = AppDatabase.getDBInstance()?.taskDao()?.getTaskSyncWise(true)

                                i = 0
                                progress_wheel.stopSpinning()

                                if (list_ != null && list_.isNotEmpty()) {
                                    val statusList = ArrayList<TaskEntity>()
                                    list_.forEach {
                                        if (it.isStatusUpdated == 0)
                                            statusList.add(it)
                                    }

                                    if (statusList.isNotEmpty())
                                        callStatusUpdateApi(statusList[i], statusList)
                                    else {
                                        stopAnimation(task_sync_img)
                                        val list = AppDatabase.getDBInstance()?.taskDao()?.getTaskSyncWise(false)
                                        if (list != null && list.isNotEmpty())
                                            tv_task_retry.visibility = View.VISIBLE
                                        else {
                                            tv_task_retry.visibility = View.GONE
                                            task_tick_img.visibility = View.VISIBLE
                                        }
                                        task_sync_img.visibility = View.GONE

                                        if (!isRetryTask)
                                            checkToCallDocument()
                                        else
                                            isRetryTask = false
                                    }
                                }
                                else {
                                    stopAnimation(task_sync_img)

                                    val list = AppDatabase.getDBInstance()?.taskDao()?.getTaskSyncWise(false)
                                    if (list != null && list.isNotEmpty())
                                        tv_task_retry.visibility = View.VISIBLE
                                    else {
                                        tv_task_retry.visibility = View.GONE
                                        task_tick_img.visibility = View.VISIBLE
                                    }
                                    task_sync_img.visibility = View.GONE

                                    if (!isRetryTask)
                                        checkToCallDocument()
                                    else
                                        isRetryTask = false
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("ADD TASK: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()

                            i++
                            if (i < taskList.size) {
                                callAddTaskApi(taskList[i], taskList)
                            } else {
                                stopAnimation(task_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.taskDao()?.getTaskSyncWise(true)

                                i = 0
                                progress_wheel.stopSpinning()

                                if (list_ != null && list_.isNotEmpty()) {
                                    val statusList = ArrayList<TaskEntity>()
                                    list_.forEach {
                                        if (it.isStatusUpdated == 0)
                                            statusList.add(it)
                                    }

                                    if (statusList.isNotEmpty())
                                        callStatusUpdateApi(statusList[i], statusList)
                                    else {
                                        val list = AppDatabase.getDBInstance()?.taskDao()?.getTaskSyncWise(false)
                                        if (list != null && list.isNotEmpty())
                                            tv_task_retry.visibility = View.VISIBLE
                                        else {
                                            tv_task_retry.visibility = View.GONE
                                            task_tick_img.visibility = View.VISIBLE
                                        }
                                        task_sync_img.visibility = View.GONE

                                        if (!isRetryTask)
                                            checkToCallDocument()
                                        else
                                            isRetryTask = false
                                    }
                                }
                                else {
                                    val list = AppDatabase.getDBInstance()?.taskDao()?.getTaskSyncWise(false)
                                    if (list != null && list.isNotEmpty())
                                        tv_task_retry.visibility = View.VISIBLE
                                    else {
                                        tv_task_retry.visibility = View.GONE
                                        task_tick_img.visibility = View.VISIBLE
                                    }
                                    task_sync_img.visibility = View.GONE

                                    if (!isRetryTask)
                                        checkToCallDocument()
                                    else
                                        isRetryTask = false
                                }
                            }

                        })
        )
    }

    private fun callStatusUpdateApi(task: TaskEntity, statusList: ArrayList<TaskEntity>) {
        XLog.d("============Update Task Status Input Params (Task List)===========")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("task_id=======> " + task.task_id)
        XLog.d("isCompleted=======> " + task.isCompleted)
        XLog.d("===================================================================")

        progress_wheel.spin()
        val repository = TaskRepoProvider.taskRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.updateStatus(task.task_id, task.isCompleted)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("UPDATE TASK STATUS: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.taskDao()?.updateIsStatus(1, task.task_id!!)
                            }

                            i++
                            if (i < statusList.size) {
                                callAddTaskApi(statusList[i], statusList)
                            } else {
                                stopAnimation(task_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.taskDao()?.getTaskStatusWise(0)

                                if (list_ != null && list_.isNotEmpty())
                                    tv_task_retry.visibility = View.VISIBLE
                                else {
                                    tv_task_retry.visibility = View.GONE
                                    task_tick_img.visibility = View.VISIBLE
                                }
                                task_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                if (!isRetryTask)
                                    checkToCallDocument()
                                else
                                    isRetryTask = false
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("UPDATE TASK STATUS: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()

                            i++
                            if (i < statusList.size) {
                                callAddTaskApi(statusList[i], statusList)
                            } else {
                                stopAnimation(task_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.taskDao()?.getTaskStatusWise(0)

                                if (list_ != null && list_.isNotEmpty())
                                    tv_task_retry.visibility = View.VISIBLE
                                else {
                                    tv_task_retry.visibility = View.GONE
                                    task_tick_img.visibility = View.VISIBLE
                                }
                                task_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                if (!isRetryTask)
                                    checkToCallDocument()
                                else
                                    isRetryTask = false
                            }

                        })
        )
    }
    //======================================================Task======================================================================//


    private fun checkToCallDocument() {
        if (Pref.isDocumentRepoShow) {
            val list = AppDatabase.getDBInstance()?.documentListDao()?.getDocSyncWise(false)
            if (list != null && list.isNotEmpty())
                callAddDocumentApi(list)
            else {
                stopAnimation(doc_sync_img)
                doc_tick_img.visibility = View.VISIBLE
                tv_doc_retry.visibility = View.GONE
                doc_sync_img.visibility = View.GONE

                if (!isRetryDocument)
                    //checkToCallActivity() // sam comm 05-07-21
                    callShopProductStockApi()
                else
                    isRetryDocument = false
            }
        } else {
            stopAnimation(doc_sync_img)
            doc_tick_img.visibility = View.VISIBLE
            tv_doc_retry.visibility = View.GONE
            doc_sync_img.visibility = View.GONE

            if (!isRetryDocument)
                //checkToCallActivity() // sam comm 05-07-21
            callShopProductStockApi()
            else
                isRetryDocument = false
        }
    }


    //=====================================================ADD DOCUMENT===========================================================
    private fun callAddDocumentApi(list: List<DocumentListEntity>) {
        progress_wheel.spin()

        val docInfoList = ArrayList<DocumentAttachmentModel>()
        list.forEach {
            val docInfo = DocumentAttachmentModel(it.attachment!!, it.list_id!!, it.type_id!!, it.date_time!!)
            docInfoList.add(docInfo)
        }

        val repository = DocumentRepoProvider.documentRepoProviderMultipart()
        BaseActivity.compositeDisposable.add(
                repository.addEditDoc(AddEditDocumentInputParams(Pref.session_token!!, Pref.user_id!!), docInfoList, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse
                            XLog.d("SYNC DOCUMENT RESPONSE=======> " + response.status)

                            if (response.status == NetworkConstant.SUCCESS) {
                                list.forEach {
                                    AppDatabase.getDBInstance()?.documentListDao()?.updateIsUploaded(true, it.list_id!!)
                                }
                            }

                            stopAnimation(doc_sync_img)
                            doc_tick_img.visibility = View.VISIBLE
                            tv_doc_retry.visibility = View.GONE
                            doc_sync_img.visibility = View.GONE

                            if (!isRetryDocument)
                                //checkToCallActivity() // sam comm 05-07-21
                            callShopProductStockApi()
                            else
                                isRetryDocument = false

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("SYNC DOCUMENT ERROR=======> " + error.localizedMessage)

                            stopAnimation(doc_sync_img)
                            doc_tick_img.visibility = View.VISIBLE
                            tv_doc_retry.visibility = View.GONE
                            doc_sync_img.visibility = View.GONE

                            if (!isRetryDocument)
                                //checkToCallActivity() // sam comm 05-07-21
                            callShopProductStockApi()
                            else
                                isRetryDocument = false
                        })
        )
    }
    //===========================================================ADD DOCUMENT========================================================


    fun isWorkerRunning(tag:String):Boolean{
        val workInstance = WorkManager.getInstance(mContext)
        val status: ListenableFuture<List<WorkInfo>> = WorkManager.getInstance(mContext).getWorkInfosByTag(tag)
        try{
            var runningStatus:Boolean = false
            val workInfoList:List<WorkInfo> = status.get()
            for( obj: WorkInfo in workInfoList){
                var state : WorkInfo.State =  obj.state
                runningStatus = state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED
            }
            return runningStatus
        }
        catch (ex: ExecutionException){
            return false
        }
        catch (ex:InterruptedException){
            return false
        }
    }

    private fun checkToCallActivity() {

        var intent = Intent(mContext, MonitorService::class.java)
        intent.action = CustomConstants.STOP_MONITOR_SERVICE
        //mContext.startService(intent)
        mContext.stopService(intent)

        SendBrod.stopBrod(mContext)
        SendBrod.stopBrodColl(mContext)
        SendBrod.stopBrodZeroOrder(mContext)
        SendBrod.stopBrodDOBDOA(mContext)

        try{
            WorkManager.getInstance(mContext).cancelAllWork()
            WorkManager.getInstance(mContext).cancelAllWorkByTag("workerTag")
            XLog.d("Logout Sync workerservice status : " + isWorkerRunning("workerTag").toString())
        }catch (ex:Exception){
            ex.printStackTrace()
        }

        if (Pref.willActivityShow) {
            val list = AppDatabase.getDBInstance()?.activDao()?.getDataSyncWise(false)
            if (list != null && list.isNotEmpty()) {
                i = 0
                callAddActivityApi(list[i], list as ArrayList<ActivityEntity>)
            } else {
                checkToCallChemistVisitApi()
            }
        } else {
            stopAnimation(activity_sync_img)
            activity_tick_img.visibility = View.VISIBLE
            tv_activity_retry.visibility = View.GONE
            activity_sync_img.visibility = View.GONE

            take_photo_tv.text = getString(R.string.data_sync_completed)
            if (!(mContext as DashboardActivity).isChangedPassword) {
                if ((mContext as DashboardActivity).isClearData) {
                    Handler().postDelayed(Runnable {
                        val packageName = mContext.packageName
                        val runtime = Runtime.getRuntime()
                        runtime.exec("pm clear " + packageName)
                        Pref.isClearData = false
                    }, 500)
                } else {
                    Handler().postDelayed(Runnable {
                        tv_logout.isEnabled = true
                        if(Pref.DayEndMarked){
                            performLogout()
                        }

                    }, 200)
                }
            } else {
                if ((mContext as DashboardActivity).isClearData) {
                    Handler().postDelayed(Runnable {
                        val packageName = mContext.packageName
                        val runtime = Runtime.getRuntime()
                        runtime.exec("pm clear " + packageName)
                        Pref.isClearData = false
                    }, 500)
                } else {
                    Handler().postDelayed(Runnable {
                        logoutYesClick()
                    }, 200)
                }
            }
        }
    }


    //=====================================================ADD ACTIVITY===========================================================
    private fun callAddActivityApi(activity: ActivityEntity, list: ArrayList<ActivityEntity>) {
        XLog.d("==============Sync Activity Input Params (Logout Sync)====================")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("id=======> " + activity.activity_id)
        XLog.d("party_id=======> " + activity.party_id)
        XLog.d("details=======> " + activity.details)
        XLog.d("date=======> " + activity.date)
        XLog.d("time=======> " + activity.time)
        XLog.d("name=======> " + activity.name)
        XLog.d("time=======> " + activity.time)
        XLog.d("activity_id=======> " + activity.activity_dropdown_id)
        XLog.d("type_id=======> " + activity.type_id)
        XLog.d("product_id=======> " + activity.product_id)
        XLog.d("subject=======> " + activity.subject)
        XLog.d("details=======> " + activity.details)
        XLog.d("duration=======> " + activity.duration)
        XLog.d("priority_id=======> " + activity.priority_id)
        XLog.d("due_date=======> " + activity.due_date)
        XLog.d("due_time=======> " + activity.due_time)
        XLog.d("attachments=======> " + activity.attachments)
        XLog.d("========================================================================")

        val activityInput = AddActivityInputModel(Pref.session_token!!, Pref.user_id!!, activity.activity_id!!, activity.party_id!!,
                activity.date!!, activity.time!!, activity.name!!, activity.activity_dropdown_id!!, activity.type_id!!,
                activity.product_id!!, activity.subject!!, activity.details!!, activity.duration!!, activity.priority_id!!,
                activity.due_date!!, activity.due_time!!)

        if (TextUtils.isEmpty(activity.attachments)) {
            val repository = ActivityRepoProvider.activityRepoProvider()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.addActivity(activityInput)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                XLog.d("ADD ACTIVITY: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.activDao()?.updateIsUploaded(true, activity.activity_id!!)
                                }

                                //progress_wheel.stopSpinning()

                                i++
                                if (i < list.size) {
                                    callAddActivityApi(list[i], list)
                                } else {
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    checkToCallChemistVisitApi()
                                }

                            }, { error ->
                                XLog.d("ADD ACTIVITY: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                //progress_wheel.stopSpinning()
                                error.printStackTrace()

                                i++
                                if (i < list.size) {
                                    callAddActivityApi(list[i], list)
                                } else {
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    checkToCallChemistVisitApi()
                                }
                            })
            )
        }
        else {

            val imgList = ArrayList<ActivityImage>()

            if (!TextUtils.isEmpty(activity.attachments)) {
                imgList.add(ActivityImage(activity.attachments!!, "attachment"))
            }

            if (!TextUtils.isEmpty(activity.image)) {
                imgList.add(ActivityImage(activity.image!!, "image"))
            }

            val repository = ActivityRepoProvider.activityImageRepoProvider()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.addActivityWithAttachment(activityInput, imgList, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                XLog.d("ADD ACTIVITY: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.activDao()?.updateIsUploaded(true, activity.activity_id!!)
                                }

                                //progress_wheel.stopSpinning()

                                i++
                                if (i < list.size) {
                                    callAddActivityApi(list[i], list)
                                } else {
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    checkToCallChemistVisitApi()
                                }

                            }, { error ->
                                XLog.d("ADD ACTIVITY: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                //progress_wheel.stopSpinning()
                                error.printStackTrace()

                                i++
                                if (i < list.size) {
                                    callAddActivityApi(list[i], list)
                                } else {
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    checkToCallChemistVisitApi()
                                }
                            })
            )
        }

    }

    private fun checkToCallChemistVisitApi() {
        val list = AppDatabase.getDBInstance()!!.addChemistDao().getDataSyncWise(false)

        if (list != null && list.isNotEmpty()) {
            i = 0
            callChemistVisitApi(list[i], list)
        } else {
            checkToCallDoctorVisitApi()
        }
    }

    private fun callChemistVisitApi(addChemistEntity: AddChemistEntity, list: List<AddChemistEntity>) {
        val chemistVisit = AddChemistVisitInputModel()

        if (!TextUtils.isEmpty(addChemistEntity.chemist_visit_id))
            chemistVisit.chemist_visit_id = addChemistEntity.chemist_visit_id!!

        chemistVisit.isPob = addChemistEntity.pob

        if (!TextUtils.isEmpty(addChemistEntity.visit_date))
            chemistVisit.next_visit_date = addChemistEntity.visit_date

        if (!TextUtils.isEmpty(addChemistEntity.remarks))
            chemistVisit.remarks = addChemistEntity.remarks!!

        if (!TextUtils.isEmpty(addChemistEntity.remarks_mr))
            chemistVisit.remarks_mr = addChemistEntity.remarks_mr!!

        if (!TextUtils.isEmpty(addChemistEntity.volume))
            chemistVisit.volume = addChemistEntity.volume!!

        if (!TextUtils.isEmpty(addChemistEntity.shop_id))
            chemistVisit.shop_id = addChemistEntity.shop_id!!

        chemistVisit.user_id = Pref.user_id!!
        chemistVisit.session_token = Pref.session_token!!

        val mlist = AppDatabase.getDBInstance()!!.addChemistProductDao().getDataIdPodWise(chemistVisit.chemist_visit_id, false) as java.util.ArrayList
        val productList = java.util.ArrayList<AddChemistProductModel>()
        if (mlist != null) {
            for (i in mlist.indices) {
                val product = AddChemistProductModel()
                product.product_id = mlist[i].id.toString()
                product.product_name = mlist[i].product_name!!
                productList.add(product)
            }
        }
        chemistVisit.product_list = productList

        val podList = AppDatabase.getDBInstance()!!.addChemistProductDao().getDataIdPodWise(chemistVisit.chemist_visit_id, true) as java.util.ArrayList
        val podProductList = java.util.ArrayList<AddChemistProductModel>()
        if (podList != null) {
            for (i in podList.indices) {
                val product = AddChemistProductModel()
                product.product_id = podList[i].id.toString()
                product.product_name = podList[i].product_name!!
                productList.add(product)
            }
        }
        chemistVisit.pob_product_list = podProductList


        XLog.d("======SYNC CHEMIST VISIT INPUT PARAMS (LOGOUT SYNC)======")
        XLog.d("USER ID===> " + chemistVisit.user_id)
        XLog.d("SESSION ID====> " + chemistVisit.session_token)
        XLog.d("CHEMIST VISIT ID====> " + chemistVisit.chemist_visit_id)
        XLog.d("SHOP_ID====> " + chemistVisit.shop_id)
        XLog.d("IS POB====> " + chemistVisit.isPob)
        XLog.d("NEXT VISIT DATE====> " + chemistVisit.next_visit_date)
        XLog.d("VOLUME====> " + chemistVisit.volume)
        XLog.d("REMARKS====> " + chemistVisit.remarks)
        XLog.d("REMARKS MR====> " + chemistVisit.remarks_mr)
        XLog.d("PRODUCT LIST SIZE====> " + chemistVisit.product_list.size)
        XLog.d("POB PRODUCT LIST SIZE====> " + chemistVisit.pob_product_list.size)
        XLog.d("=========================================================")

        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.uploadChemistVisit(chemistVisit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse

                            XLog.d("SYNC CHEMIST VISIT DETAILS : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + response.message)
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.addChemistDao().updateIsUploaded(true, chemistVisit.chemist_visit_id)

                                i++
                                if (i < list.size) {
                                    callChemistVisitApi(list[i], list)
                                } else {
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    checkToCallDoctorVisitApi()
                                }
                            } else {
                                i++
                                if (i < list.size) {
                                    callChemistVisitApi(list[i], list)
                                } else {
                                    i = 0
                                    progress_wheel.stopSpinning()
                                    checkToCallDoctorVisitApi()
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            XLog.d("SYNC CHEMIST VISIT DETAILS : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + error.localizedMessage)

                            i++
                            if (i < list.size) {
                                callChemistVisitApi(list[i], list)
                            } else {
                                i = 0
                                progress_wheel.stopSpinning()
                                checkToCallDoctorVisitApi()
                            }
                        })
        )
    }

    private fun checkToCallDoctorVisitApi() {
        val list = AppDatabase.getDBInstance()!!.addDocDao().getDataSyncWise(false)

        if (list != null && list.isNotEmpty()) {
            i = 0
            callDoctorVisitApi(list[i], list)
        } else {
            stopAnimation(activity_sync_img)
            activity_tick_img.visibility = View.VISIBLE
            tv_activity_retry.visibility = View.GONE
            activity_sync_img.visibility = View.GONE

            take_photo_tv.text = getString(R.string.data_sync_completed)
            if (!(mContext as DashboardActivity).isChangedPassword) {
                if ((mContext as DashboardActivity).isClearData) {
                    Handler().postDelayed(Runnable {
                        val packageName = mContext.packageName
                        val runtime = Runtime.getRuntime()
                        runtime.exec("pm clear " + packageName)
                        Pref.isClearData = false
                    }, 500)
                } else {
                    Handler().postDelayed(Runnable {
                        tv_logout.isEnabled = true
                        if(Pref.DayEndMarked){
                            performLogout()
                        }

                    }, 200)
                }
            } else {
                if ((mContext as DashboardActivity).isClearData) {
                    Handler().postDelayed(Runnable {
                        val packageName = mContext.packageName
                        val runtime = Runtime.getRuntime()
                        runtime.exec("pm clear " + packageName)
                        Pref.isClearData = false
                    }, 500)
                } else {
                    Handler().postDelayed(Runnable {
                        logoutYesClick()
                    }, 200)
                }
            }
        }
    }

    private fun callDoctorVisitApi(addDoctorEntity: AddDoctorEntity, list: List<AddDoctorEntity>) {
        val docVisit = AddDoctorVisitInputModel()
        if (!TextUtils.isEmpty(addDoctorEntity.doc_visit_id))
            docVisit.doc_visit_id = addDoctorEntity.doc_visit_id!!

        if (!TextUtils.isEmpty(addDoctorEntity.amount))
            docVisit.amount = addDoctorEntity.amount!!

        if (!TextUtils.isEmpty(addDoctorEntity.visit_date))
            docVisit.next_visit_date = addDoctorEntity.visit_date!!

        if (!TextUtils.isEmpty(addDoctorEntity.volume))
            docVisit.crm_volume = addDoctorEntity.volume!!

        if (!TextUtils.isEmpty(addDoctorEntity.remarks_mr))
            docVisit.remarks_mr = addDoctorEntity.remarks_mr!!

        if (!TextUtils.isEmpty(addDoctorEntity.doc_remark))
            docVisit.doc_remarks = addDoctorEntity.doc_remark!!

        if (!TextUtils.isEmpty(addDoctorEntity.shop_id))
            docVisit.shop_id = addDoctorEntity.shop_id!!

        docVisit.user_id = Pref.user_id!!
        docVisit.session_token = Pref.session_token!!

        if (!TextUtils.isEmpty(addDoctorEntity.crm_from_date))
            docVisit.from_cme_date = addDoctorEntity.crm_from_date!!

        if (!TextUtils.isEmpty(addDoctorEntity.crm_to_date))
            docVisit.to_crm_date = addDoctorEntity.crm_to_date!!

        docVisit.is_crm = addDoctorEntity.crm_status
        docVisit.is_gift = addDoctorEntity.gift_status
        docVisit.is_money = addDoctorEntity.money_status
        docVisit.is_prescriber = addDoctorEntity.prescribe_status
        docVisit.is_qty = addDoctorEntity.qty_status
        docVisit.is_sample = addDoctorEntity.sample_status

        if (!TextUtils.isEmpty(addDoctorEntity.qty_text))
            docVisit.qty_vol_text = addDoctorEntity.qty_text!!

        if (!TextUtils.isEmpty(addDoctorEntity.what))
            docVisit.what = addDoctorEntity.what!!

        if (!TextUtils.isEmpty(addDoctorEntity.which_kind))
            docVisit.which_kind = addDoctorEntity.which_kind!!

        val mList = AppDatabase.getDBInstance()!!.addDocProductDao().getDataIdPodWise(addDoctorEntity.doc_visit_id!!, 0) as java.util.ArrayList
        val productList = java.util.ArrayList<AddChemistProductModel>()
        if (mList != null) {
            for (i in mList.indices) {
                val product = AddChemistProductModel()
                product.product_id = mList[i].product_id!!
                product.product_name = mList[i].product_name!!
                productList.add(product)
            }
        }
        docVisit.product_list = productList

        val qtyProductList = AppDatabase.getDBInstance()!!.addDocProductDao().getDataIdPodWise(addDoctorEntity.doc_visit_id!!, 1) as java.util.ArrayList
        val podProductList = java.util.ArrayList<AddChemistProductModel>()
        if (qtyProductList != null) {
            for (i in qtyProductList.indices) {
                val product = AddChemistProductModel()
                product.product_id = qtyProductList[i].product_id!!
                product.product_name = qtyProductList[i].product_name!!
                podProductList.add(product)
            }
        }
        docVisit.qty_product_list = podProductList

        val sampleProductDbList = AppDatabase.getDBInstance()!!.addDocProductDao().getDataIdPodWise(addDoctorEntity.doc_visit_id!!, 2) as java.util.ArrayList
        val sampleProductList = java.util.ArrayList<AddChemistProductModel>()
        if (sampleProductDbList != null) {
            for (i in sampleProductDbList.indices) {
                val product = AddChemistProductModel()
                product.product_id = sampleProductDbList[i].product_id!!
                product.product_name = sampleProductDbList[i].product_name!!
                sampleProductList.add(product)
            }
        }
        docVisit.sample_product_list = sampleProductList

        XLog.d("======SYNC DOCTOR VISIT INPUT PARAMS (SYNC ALL)======")
        XLog.d("USER ID===> " + docVisit.user_id)
        XLog.d("SESSION ID====> " + docVisit.session_token)
        XLog.d("DOCTOR VISIT ID====> " + docVisit.doc_visit_id)
        XLog.d("SHOP_ID====> " + docVisit.shop_id)
        XLog.d("AMOUNT====> " + docVisit.amount)
        XLog.d("NEXT VISIT DATE====> " + docVisit.next_visit_date)
        XLog.d("VOLUME====> " + docVisit.crm_volume)
        XLog.d("DOCTOR REMARKS====> " + docVisit.doc_remarks)
        XLog.d("REMARKS MR====> " + docVisit.remarks_mr)
        XLog.d("FROM DATE====> " + docVisit.from_cme_date)
        XLog.d("TO DATE====> " + docVisit.to_crm_date)
        XLog.d("IS GIFT====> " + docVisit.is_gift)
        XLog.d("IS CRM====> " + docVisit.is_crm)
        XLog.d("IS MONEY====> " + docVisit.is_money)
        XLog.d("IS PRESCRIBER====> " + docVisit.is_prescriber)
        XLog.d("IS QTY====> " + docVisit.is_qty)
        XLog.d("IS SAMPLE====> " + docVisit.is_sample)
        XLog.d("QTY VOL TEXT====> " + docVisit.qty_vol_text)
        XLog.d("WHAT====> " + docVisit.what)
        XLog.d("WHICH====> " + docVisit.which_kind)
        XLog.d("PRODUCT LIST SIZE====> " + docVisit.product_list.size)
        XLog.d("QTY PRODUCT LIST SIZE====> " + docVisit.qty_product_list.size)
        XLog.d("SAMPLE PRODUCT LIST SIZE====> " + docVisit.sample_product_list.size)
        XLog.d("=========================================================")

        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.uploadDoctorVisit(docVisit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse
                            XLog.d("SYNC DOCTOR VISIT DETAILS : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + response.message)

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.addDocDao().updateIsUploaded(true, docVisit.doc_visit_id)

                                i++
                                if (i < list.size) {
                                    callDoctorVisitApi(list[i], list)
                                } else {
                                    stopAnimation(activity_sync_img)
                                    val list_ = AppDatabase.getDBInstance()?.activDao()?.getDataSyncWise(false)
                                    if (list_ != null && list_.isNotEmpty())
                                        tv_activity_retry.visibility = View.VISIBLE
                                    else {
                                        val chemistList = AppDatabase.getDBInstance()!!.addChemistDao().getDataSyncWise(false)
                                        if (chemistList != null && chemistList.isNotEmpty())
                                            tv_activity_retry.visibility = View.VISIBLE
                                        else {
                                            val doctorList = AppDatabase.getDBInstance()!!.addDocDao().getDataSyncWise(false)
                                            if (doctorList != null && doctorList.isNotEmpty())
                                                tv_activity_retry.visibility = View.VISIBLE
                                            else {
                                                tv_activity_retry.visibility = View.GONE
                                                activity_tick_img.visibility = View.VISIBLE
                                            }
                                        }
                                    }
                                    activity_sync_img.visibility = View.GONE

                                    i = 0
                                    progress_wheel.stopSpinning()

                                    take_photo_tv.text = getString(R.string.data_sync_completed)
                                    if (!(mContext as DashboardActivity).isChangedPassword) {
                                        if ((mContext as DashboardActivity).isClearData) {
                                            Handler().postDelayed(Runnable {
                                                val packageName = mContext.packageName
                                                val runtime = Runtime.getRuntime()
                                                runtime.exec("pm clear " + packageName)
                                                Pref.isClearData = false
                                            }, 500)
                                        } else {
                                            Handler().postDelayed(Runnable {
                                                tv_logout.isEnabled = true
                                                if(Pref.DayEndMarked){
                                                    performLogout()
                                                }

                                            }, 200)
                                        }
                                    } else {
                                        if ((mContext as DashboardActivity).isClearData) {
                                            Handler().postDelayed(Runnable {
                                                val packageName = mContext.packageName
                                                val runtime = Runtime.getRuntime()
                                                runtime.exec("pm clear " + packageName)
                                                Pref.isClearData = false
                                            }, 500)
                                        } else {
                                            Handler().postDelayed(Runnable {
                                                logoutYesClick()
                                            }, 200)
                                        }
                                    }
                                }
                            } else {
                                i++
                                if (i < list.size) {
                                    callDoctorVisitApi(list[i], list)
                                } else {
                                    stopAnimation(activity_sync_img)
                                    val list_ = AppDatabase.getDBInstance()?.activDao()?.getDataSyncWise(false)
                                    if (list_ != null && list_.isNotEmpty())
                                        tv_activity_retry.visibility = View.VISIBLE
                                    else {
                                        val chemistList = AppDatabase.getDBInstance()!!.addChemistDao().getDataSyncWise(false)
                                        if (chemistList != null && chemistList.isNotEmpty())
                                            tv_activity_retry.visibility = View.VISIBLE
                                        else {
                                            val doctorList = AppDatabase.getDBInstance()!!.addDocDao().getDataSyncWise(false)
                                            if (doctorList != null && doctorList.isNotEmpty())
                                                tv_activity_retry.visibility = View.VISIBLE
                                            else {
                                                tv_activity_retry.visibility = View.GONE
                                                activity_tick_img.visibility = View.VISIBLE
                                            }
                                        }
                                    }
                                    activity_sync_img.visibility = View.GONE

                                    i = 0
                                    progress_wheel.stopSpinning()

                                    take_photo_tv.text = getString(R.string.data_sync_completed)
                                    if (!(mContext as DashboardActivity).isChangedPassword) {
                                        if ((mContext as DashboardActivity).isClearData) {
                                            Handler().postDelayed(Runnable {
                                                val packageName = mContext.packageName
                                                val runtime = Runtime.getRuntime()
                                                runtime.exec("pm clear " + packageName)
                                                Pref.isClearData = false
                                            }, 500)
                                        } else {
                                            Handler().postDelayed(Runnable {
                                                tv_logout.isEnabled = true
                                                if(Pref.DayEndMarked){
                                                    performLogout()
                                                }

                                            }, 200)
                                        }
                                    } else {
                                        if ((mContext as DashboardActivity).isClearData) {
                                            Handler().postDelayed(Runnable {
                                                val packageName = mContext.packageName
                                                val runtime = Runtime.getRuntime()
                                                runtime.exec("pm clear " + packageName)
                                                Pref.isClearData = false
                                            }, 500)
                                        } else {
                                            Handler().postDelayed(Runnable {
                                                logoutYesClick()
                                            }, 200)
                                        }
                                    }
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            XLog.d("SYNC DOCTOR VISIT DETAILS : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + error.localizedMessage)

                            i++
                            if (i < list.size) {
                                callDoctorVisitApi(list[i], list)
                            } else {
                                stopAnimation(activity_sync_img)
                                val list_ = AppDatabase.getDBInstance()?.activDao()?.getDataSyncWise(false)
                                if (list_ != null && list_.isNotEmpty())
                                    tv_activity_retry.visibility = View.VISIBLE
                                else {
                                    val chemistList = AppDatabase.getDBInstance()!!.addChemistDao().getDataSyncWise(false)
                                    if (chemistList != null && chemistList.isNotEmpty())
                                        tv_activity_retry.visibility = View.VISIBLE
                                    else {
                                        val doctorList = AppDatabase.getDBInstance()!!.addDocDao().getDataSyncWise(false)
                                        if (doctorList != null && doctorList.isNotEmpty())
                                            tv_activity_retry.visibility = View.VISIBLE
                                        else {
                                            tv_activity_retry.visibility = View.GONE
                                            activity_tick_img.visibility = View.VISIBLE
                                        }
                                    }
                                }
                                activity_sync_img.visibility = View.GONE

                                i = 0
                                progress_wheel.stopSpinning()

                                take_photo_tv.text = getString(R.string.data_sync_completed)
                                if (!(mContext as DashboardActivity).isChangedPassword) {
                                    if ((mContext as DashboardActivity).isClearData) {
                                        Handler().postDelayed(Runnable {
                                            val packageName = mContext.packageName
                                            val runtime = Runtime.getRuntime()
                                            runtime.exec("pm clear " + packageName)
                                            Pref.isClearData = false
                                        }, 500)
                                    } else {
                                        Handler().postDelayed(Runnable {
                                            tv_logout.isEnabled = true
                                            if(Pref.DayEndMarked){
                                                performLogout()
                                            }

                                        }, 200)
                                    }
                                } else {
                                    if ((mContext as DashboardActivity).isClearData) {
                                        Handler().postDelayed(Runnable {
                                            val packageName = mContext.packageName
                                            val runtime = Runtime.getRuntime()
                                            runtime.exec("pm clear " + packageName)
                                            Pref.isClearData = false
                                        }, 500)
                                    } else {
                                        Handler().postDelayed(Runnable {
                                            logoutYesClick()
                                        }, 200)
                                    }
                                }
                            }
                        })
        )
    }
    //===========================================================ADD ACTIVITY========================================================



    //===============================================Logout===========================================================================//
    private fun calllogoutApi(user_id: String, session_id: String) {
        if (Pref.current_latitude == null || Pref.current_longitude == null) {
            (mContext as DashboardActivity).showSnackMessage("Can't fetch location.Please wait for some time ")
            return
        }
        if (BaseActivity.isApiInitiated)
            return
        BaseActivity.isApiInitiated = true


        var distance = 0.0
        val list = AppDatabase.getDBInstance()!!.userLocationDataDao().all
        if (list != null && list.size > 0) {
            val latestLat = list[list.size - 1].latitude
            val latestLong = list[list.size - 1].longitude

            /*val previousLat = list[list.size - 2].latitude
            val previousLong = list[list.size - 2].longitude*/

//            if (Pref.logout_latitude != "0.0" && Pref.logout_longitude != "0.0") {
//                /*if (latestLat != Pref.latitude && latestLong != Pref.longitude) {
//                    val distance = LocationWizard.getDistance(latestLat.toDouble(), latestLong.toDouble(),
//                            Pref.latitude!!.toDouble(), Pref.longitude!!.toDouble())
//
//                    XLog.d("LOGOUT : DISTANCE=====> $distance")
//                }*/
//
//                /*val distance = LocationWizard.getDistance(previousLat.toDouble(), previousLong.toDouble(),
//                        latestLat.toDouble(), latestLong.toDouble())*/
//
//                distance = LocationWizard.getDistance(latestLat.toDouble(), latestLong.toDouble(),
//                        Pref.logout_latitude.toDouble(), Pref.logout_longitude.toDouble())
//            }
        }

        val unSyncedList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADayNotSyn(AppUtils.getCurrentDateForShopActi(), false)

        if (unSyncedList != null && unSyncedList.isNotEmpty()) {
            var totalDistance = 0.0
            for (i in unSyncedList.indices) {
                totalDistance += unSyncedList[i].distance.toDouble()
            }

            distance = Pref.tempDistance.toDouble() + totalDistance
        } else
            distance = Pref.tempDistance.toDouble()


        var location = ""

        if (Pref.logout_latitude != "0.0" && Pref.logout_longitude != "0.0") {
            location = LocationWizard.getAdressFromLatlng(mContext, Pref.logout_latitude.toDouble(), Pref.logout_longitude.toDouble())

            if (location.contains("http"))
                location = "Unknown"
        }

        XLog.d("LOGOUT : " + "REQUEST : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
        XLog.d("=========LOGOUT INPUT PARAMS========")
        XLog.d("LOGOUT : USER ID===> $user_id")
        XLog.d("LOGOUT : SESSION ID==> $session_id")
        XLog.d("LOGOUT : LAT====> " + Pref.logout_latitude)
        XLog.d("LOGOUT : LONG=====> " + Pref.logout_longitude)
        XLog.d("LOGOUT : DISTANCE=====> $distance")
        XLog.d("LOGOUT : LOGOUT TIME====> " + AppUtils.getCurrentDateTime())
        XLog.d("LOGOUT : IS AUTO LOGOUT===> 0")
        XLog.d("LOGOUT : LOCATION====> $location")
        XLog.d("=====================================")

        val repository = LogoutRepositoryProvider.provideLogoutRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.logout(user_id, session_id, Pref.logout_latitude, Pref.logout_longitude, AppUtils.getCurrentDateTime(), distance.toString(),
                        "0", location)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val logoutResponse = result as BaseResponse
                            XLog.d("LOGOUT : " + "RESPONSE : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                            if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).isChangedPassword = false
                                Pref.tempDistance = "0.0"

                                /*if ((mContext as DashboardActivity).isForceLogout)
                                    Pref.prevOrderCollectionCheckTimeStamp = 0L*/

                                if (unSyncedList != null && unSyncedList.isNotEmpty()) {
                                    for (i in unSyncedList.indices) {
                                        AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploaded(true, unSyncedList[i].locationId)
                                    }
                                }

                                (mContext as DashboardActivity).syncShopListAndLogout()
                            } else if (logoutResponse.status == NetworkConstant.SESSION_MISMATCH) {
                                //clearData()
                                (mContext as DashboardActivity).isChangedPassword = false
                                startActivity(Intent(mContext, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                (mContext as DashboardActivity).finishAffinity()
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Failed to logout")

                                if ((mContext as DashboardActivity).isChangedPassword) {
                                    (mContext as DashboardActivity).isChangedPassword = false
                                    (mContext as DashboardActivity).onBackPressed()
                                }
                            }
                            BaseActivity.isApiInitiated = false


                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            XLog.d("LOGOUT : " + "RESPONSE ERROR: " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(error.localizedMessage)

                            if ((mContext as DashboardActivity).isChangedPassword) {
                                (mContext as DashboardActivity).isChangedPassword = false
                                (mContext as DashboardActivity).onBackPressed()
                            }
                        })
        )
    }
    //====================================================Logout===========================================================================//


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tv_shop_retry -> {

                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryShop = true
                checkToCallAddShopApi()
            }
            R.id.tv_gps_retry -> {

                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryGps = true
                checkToGpsStatus()
            }
            R.id.tv_revisit_retry -> {

                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryVisit = true
                checkToCallVisitShopApi()
            }
            R.id.tv_order_retry -> {

                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryOrder = true
                checkToCallSyncOrder()
            }
            R.id.tv_collection_retry -> {

                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryCollection = true
                checkToCallCollectionApi()
            }
            R.id.tv_bill_retry -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }
                isBiilingEntry = true
                checkToCallBillingApi()
            }
            R.id.tv_stock_retry -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryStock = true
                checkToCallAddStockApi()
            }
            R.id.tv_meeting_retry -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryMeeting = true
                checkToCallMeetingApi()
            }
            R.id.tv_quot_retry -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryQuotation = true
                checkToCallAddQuotApi()
            }

            R.id.tv_team_retry -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryTeamShop = true
                checkToCallUpdateAddress()
            }

            R.id.tv_timesheet_retry -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryTimesheet = true
                checkToCallTimesheet()
            }

            R.id.tv_task_retry -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryTask = true
                checkToCallTask()
            }

            R.id.tv_doc_retry -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                isRetryDocument = true
                checkToCallDocument()
            }

            R.id.tv_activity_retry -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    return
                }

                //checkToCallActivity() // sam comm 05-07-21
                callShopProductStockApi()
            }

            R.id.tv_logout -> {

                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage("Good internet must required to logout, please switch on the internet and proceed. Thanks.")
                    return
                }

                performLogout()

            }
        }
    }


    //08-09-2021
/*    private fun syncNewOrderScr(){
        try{
            var unsyncList=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getUnSyncOrderAll
        }catch (ex:Exception){ex.printStackTrace()}
    }*/



    private fun performLogout() {
        if(Pref.DayEndMarked){
            if (Pref.isShowLogoutReason && !TextUtils.isEmpty(Pref.approvedOutTime)) {
                val currentTimeInLong = AppUtils.convertTimeWithMeredianToLong(AppUtils.getCurrentTimeWithMeredian())
                val approvedOutTimeInLong = AppUtils.convertTimeWithMeredianToLong(Pref.approvedOutTime)

                if (currentTimeInLong < approvedOutTimeInLong)
                    showLogoutLocReasonDialog()
                else
                    logoutYesClick()
            }
            else
                logoutYesClick()
        }else{

            CommonDialog.getInstance(AppUtils.hiFirstNameText()+"!", getString(R.string.confirm_logout), getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
                override fun onLeftClick() {
                    (mContext as DashboardActivity).onBackPressed()
                }

                override fun onRightClick(editableData: String) {
                    //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                    if (Pref.isShowLogoutReason && !TextUtils.isEmpty(Pref.approvedOutTime)) {
                        val currentTimeInLong = AppUtils.convertTimeWithMeredianToLong(AppUtils.getCurrentTimeWithMeredian())
                        val approvedOutTimeInLong = AppUtils.convertTimeWithMeredianToLong(Pref.approvedOutTime)

                        if (currentTimeInLong < approvedOutTimeInLong)
                            showLogoutLocReasonDialog()
                        else
                            logoutYesClick()
                    }
                    else
                        logoutYesClick()
                }

            }).show((mContext as DashboardActivity).supportFragmentManager, "")


        }


    }

    private fun showLogoutLocReasonDialog() {
        val body = "You applicable out time is: ${Pref.approvedOutTime}. You are doing early logout. Please write below the reason."
        reasonDialog = ReasonDialog.getInstance(AppUtils.hiFirstNameText()+"!", body, reason) {
            if (!AppUtils.isOnline(mContext))
                Toaster.msgShort(mContext, getString(R.string.no_internet))
            else {
                reasonDialog?.dismiss()
                submitLogoutReason(it)
            }
        }
        reasonDialog?.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun submitLogoutReason(mReason: String) {
        progress_wheel.spin()
        val repository = DashboardRepoProvider.provideDashboardRepository()
        BaseActivity.compositeDisposable.add(
                repository.submiLogoutReason(mReason)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                reason = ""
                                logoutYesClick()
                            }
                            else {
                                reason = mReason
                                showLogoutLocReasonDialog()
                                Toaster.msgShort(mContext, result.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            reason = mReason
                            showLogoutLocReasonDialog()
                            Toaster.msgShort(mContext, getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun logoutYesClick() {
        val list = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationNotUploaded(false)

        if (AppUtils.isOnline(mContext)) {
            if (list != null && list.isNotEmpty()) {
                syncLocationActivity(list)
            } else {
                AppUtils.isLocationActivityUpdating = false
                //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                getUnknownList()
            }

        } else {
            AppUtils.isLocationActivityUpdating = false
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }
    }

    private fun syncLocationActivity(list: List<UserLocationDataEntity>) {

        XLog.d("syncLocationActivity Logout : ENTER")


        if (Pref.user_id.isNullOrEmpty())
            return

        val syncList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADayNotSyn(AppUtils.getCurrentDateForShopActi(), true)

        //val list = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationNotUploaded(false)
        if (/*list.isEmpty() ||*/ AppUtils.isLocationActivityUpdating)
            return

        AppUtils.isLocationActivityUpdating = true

        //writeDataToFile(list)

        /*val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000*/

        val locationUpdateReq = LocationUpdateRequest()
        locationUpdateReq.user_id = Pref.user_id
        locationUpdateReq.session_token = Pref.session_token

        val locationList: MutableList<LocationData> = ArrayList()
        val locationListAllId: MutableList<LocationData> = ArrayList()
        var distanceCovered: Double = 0.0
        var timeStamp = 0L


        val allLocationList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi()).toMutableList()
        //val unSyncList: MutableList<UserLocationDataEntity> = ArrayList()
        val apiLocationList: MutableList<UserLocationDataEntity> = ArrayList()

//        for (i in 0 until list.size) {
//            if (list[i].latitude == null || list[i].longitude == null)
//                continue
//            val locationData = LocationData()
//
//
//            /*locationData.locationId = list[i].locationId.toString()
//            locationData.date = list[i].updateDateTime
//            locationData.distance_covered = list[i].distance
//            locationData.latitude = list[i].latitude
//            locationData.longitude = list[i].longitude
//            locationData.location_name = list[i].locationName
//            locationData.shops_covered = list[i].shops
//            locationData.last_update_time = list[i].time + " " + list[i].meridiem*/
//
//            if (syncList == null || syncList.isEmpty()) {
//                if (i == 0) {
//                    locationData.locationId = list[i].locationId.toString()
//                    locationData.date = list[i].updateDateTime
//                    locationData.distance_covered = list[i].distance
//                    locationData.latitude = list[i].latitude
//                    locationData.longitude = list[i].longitude
//                    locationData.location_name = list[i].locationName
//                    locationData.shops_covered = list[i].shops
//                    locationData.last_update_time = list[i].time + " " + list[i].meridiem
//                    locationList.add(locationData)
//                }
//            }
//
//            distanceCovered += list[i].distance.toDouble()
//
//            if (i != 0 && i % 5 == 0) {
//                locationData.locationId = list[i].locationId.toString()
//                locationData.date = list[i].updateDateTime
//
//                locationData.distance_covered = distanceCovered.toString()
//
//                locationData.latitude = list[i].latitude
//                locationData.longitude = list[i].longitude
//                locationData.location_name = list[i].locationName
//                locationData.shops_covered = list[i].shops
//                locationData.last_update_time = list[i].time + " " + list[i].meridiem
//                locationList.add(locationData)
//
//                distanceCovered = 0.0
//            }
//
//            /*if (TextUtils.isEmpty(list[i].unique_id)) {
//                //list[i].unique_id = m.toString()
//                AppDatabase.getDBInstance()!!.userLocationDataDao().updateUniqueId(m.toString(), list[i].locationId)
//            }*/
//
//            val locationDataAll = LocationData()
//            locationDataAll.locationId = list[i].locationId.toString()
//            locationListAllId.add(locationDataAll)
//        }


        /*allLocationList.filter { it.latitude != null && it.longitude != null }.toMutableList().also {
            apiLocationList.add(it[0])
            it.removeAt(0)
        }.forEachIndexed { index, userLocationDataEntity ->
            distanceCovered += userLocationDataEntity.distance.toDouble()

            try {

                val timeStamp = userLocationDataEntity.timestamp.toLong()

                if (index % 5 == 0) {
                    userLocationDataEntity.distance = distanceCovered.toString()
                    apiLocationList.add(userLocationDataEntity)
                    distanceCovered = 0.0
                }

            } catch (e: Exception) {
                e.printStackTrace()

                userLocationDataEntity.distance = distanceCovered.toString()
                apiLocationList.add(userLocationDataEntity)
                distanceCovered = 0.0
            }
        }*/

        var fiveMinsRowGap = 5

        if (Pref.locationTrackInterval == "30")
            fiveMinsRowGap = 10

        for (i in 0 until allLocationList.size) {
            if (allLocationList[i].latitude == null || allLocationList[i].longitude == null)
                continue

            //apiLocationList.add(allLocationList[i])

            if (i == 0) {
                apiLocationList.add(allLocationList[i])
            }

            distanceCovered += allLocationList[i].distance.toDouble()

            if (i != 0 /*&& i % 5 == 0*/) {
                try {

                    val timeStamp_ = allLocationList[i].timestamp.toLong()

                    if (i % fiveMinsRowGap == 0) {
                        allLocationList[i].distance = distanceCovered.toString()

                        if (timeStamp != 0L) {
                            val hh = timeStamp / 3600
                            timeStamp %= 3600
                            val mm = timeStamp / 60
                            timeStamp %= 60
                            val ss = timeStamp
                            allLocationList[i].home_duration = AppUtils.format(hh) + ":" + AppUtils.format(mm) + ":" + AppUtils.format(ss)
                        }
                        apiLocationList.add(allLocationList[i])
                        distanceCovered = 0.0
                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                    allLocationList[i].distance = distanceCovered.toString()
                    if (timeStamp != 0L) {
                        val hh = timeStamp / 3600
                        timeStamp %= 3600
                        val mm = timeStamp / 60
                        timeStamp %= 60
                        val ss = timeStamp
                        allLocationList[i].home_duration = AppUtils.format(hh) + ":" + AppUtils.format(mm) + ":" + AppUtils.format(ss)
                    }
                    apiLocationList.add(allLocationList[i])
                    distanceCovered = 0.0
                }
            }
        }

        /*apiLocationList.filter { !it.isUploaded }.forEach {
            val locationData = LocationData()

            locationData.apply {
                locationId = it.locationId.toString()
                date = it.updateDateTime
                distance_covered = it.distance
                latitude = it.latitude
                longitude = it.longitude
                location_name = it.locationName
                shops_covered = it.shops
                last_update_time = it.time + " " + it.meridiem
                meeting_attended = it.meeting
                locationList.add(locationData)


                val locationDataAll = LocationData()
                locationDataAll.also { locData ->
                    locData.locationId = it.locationId.toString()
                    locationListAllId.add(locData)
                }
            }
        }*/


        for (i in apiLocationList.indices) {
            if (!apiLocationList[i].isUploaded) {

                XLog.e("Final Home Duration (Location Fuzed Service)=================> ${apiLocationList[i].home_duration}")
                XLog.e("Time (Location Fuzed Service)=================> ${apiLocationList[i].time} ${apiLocationList[i].meridiem}")


                val locationData = LocationData()

                locationData.locationId = apiLocationList[i].locationId.toString()
                locationData.date = apiLocationList[i].updateDateTime
                locationData.distance_covered = apiLocationList[i].distance
                locationData.latitude = apiLocationList[i].latitude
                locationData.longitude = apiLocationList[i].longitude
                locationData.location_name = apiLocationList[i].locationName
                locationData.shops_covered = apiLocationList[i].shops
                locationData.last_update_time = apiLocationList[i].time + " " + apiLocationList[i].meridiem
                locationData.meeting_attended = apiLocationList[i].meeting
                locationData.home_distance = apiLocationList[i].home_distance
                locationData.network_status = apiLocationList[i].network_status
                locationData.battery_percentage = apiLocationList[i].battery_percentage
                locationData.home_duration = apiLocationList[i].home_duration
                locationList.add(locationData)


                val locationDataAll = LocationData()
                locationDataAll.locationId = apiLocationList[i].locationId.toString()
                locationListAllId.add(locationDataAll)
            }
        }

        if (locationList.size > 0) {

            locationUpdateReq.location_details = locationList
            val repository = LocationUpdateRepositoryProviders.provideLocationUpdareRepository()

            XLog.d("syncLocationActivity Logout : REQUEST")
            progress_wheel.spin()

            BaseActivity.compositeDisposable.add(
                    repository.sendLocationUpdate(locationUpdateReq)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
//                        .timeout(60 * 1, TimeUnit.SECONDS)
                            .subscribe({ result ->
                                val updateShopActivityResponse = result as BaseResponse

                                XLog.d("syncLocationActivity Logout : RESPONSE : " + updateShopActivityResponse.status + ":" + updateShopActivityResponse.message)

                                if (updateShopActivityResponse.status == NetworkConstant.SUCCESS) {

                                    doAsync {

                                        for (i in 0 until  locationListAllId/*locationList*/.size) {

                                            //AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploaded(true, locationList[i].locationId.toInt())

                                            if (syncList != null && syncList.isNotEmpty()) {

                                                if (i == 0)
                                                    AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploadedFor5Items(true, syncList[syncList.size - 1].locationId.toInt(), locationListAllId[i].locationId.toInt())
                                                else
                                                    AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploadedFor5Items(true, locationListAllId[i - 1].locationId.toInt(), locationListAllId[i].locationId.toInt())

                                            } else {
                                                if (i == 0)
                                                    AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploaded(true, locationListAllId[i].locationId.toInt())
                                                else
                                                    AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploadedFor5Items(true, locationListAllId[i - 1].locationId.toInt(), locationListAllId[i].locationId.toInt())
                                            }
                                        }

                                        uiThread {
                                            AppUtils.isLocationActivityUpdating = false
                                            progress_wheel.stopSpinning()
                                            //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                            getUnknownList()
                                        }
                                    }
                                } else {
                                    AppUtils.isLocationActivityUpdating = false
                                    progress_wheel.stopSpinning()
                                    //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                    getUnknownList()
                                }

                            }, { error ->
                                AppUtils.isLocationActivityUpdating = false
                                progress_wheel.stopSpinning()
                                //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                getUnknownList()

                                if (error == null) {
                                    XLog.d("syncLocationActivity Logout : ERROR : " + "UNEXPECTED ERROR IN LOCATION ACTIVITY API")
                                } else {
                                    XLog.d("syncLocationActivity Logout : ERROR : " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                            })
            )
        } else {
            XLog.e("=======locationList is empty (Logout)=========")
            AppUtils.isLocationActivityUpdating = false
            //calllogoutApi(Pref.user_id!!, Pref.session_token!!)
            getUnknownList()
        }
    }

    private fun getUnknownList() {
        val repository = LocationFetchRepositoryProvider.provideLocationFetchRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.fetchUnknownLocation()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UnknownReponseModel
                            if (response.status == NetworkConstant.SUCCESS)
                                submitLoc(response.location_list)
                            else {
                                progress_wheel.stopSpinning()
                                callAppInfoApi()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            callAppInfoApi()
                        })
        )
    }

    private fun submitLoc(locationList: ArrayList<LocationDataModel>?) {
        doAsync {
            locationList?.forEachIndexed { i, it ->
                Log.e("Logout", "Unknown loc index============> $i")
                it.location_name = LocationWizard.getLocationName(mContext, it.latitude.toDouble(), it.longitude.toDouble())
            }

            uiThread {
                val submitLocation = SubmitLocationInputModel(Pref.session_token!!, Pref.user_id!!, locationList)
                progress_wheel.stopSpinning()
                val repository = LocationFetchRepositoryProvider.provideLocationFetchRepository()
                progress_wheel.spin()
                BaseActivity.compositeDisposable.add(
                        repository.submitLoc(submitLocation)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val response = result as BaseResponse
                                    if (response.status == NetworkConstant.SUCCESS) {
                                    }

                                    progress_wheel.stopSpinning()
                                    callAppInfoApi()

                                }, { error ->
                                    error.printStackTrace()
                                    progress_wheel.stopSpinning()
                                    callAppInfoApi()
                                })
                )
            }
        }
    }


    private fun callAppInfoApi() {
        if (!Pref.isAppInfoEnable) {
            calllogoutApi(Pref.user_id!!, Pref.session_token!!)
            return
        }

        val unSyncData = AppDatabase.getDBInstance()?.batteryNetDao()?.getDataSyncStateWise(false)

        if (unSyncData == null || unSyncData.isEmpty()) {
            XLog.e("=======Appinfo list is empty (Logout Sync)=========")
            calllogoutApi(Pref.user_id!!, Pref.session_token!!)
            return
        }

        if (AppUtils.isAppInfoUpdating)
            return

        AppUtils.isAppInfoUpdating = true

        val appInfoList = ArrayList<AppInfoDataModel>()

        unSyncData.forEach {
            appInfoList.add(AppInfoDataModel(it.bat_net_id!!, it.date_time!!, it.bat_status!!, it.bat_level!!, it.net_type!!,
                    it.mob_net_type!!, it.device_model!!, it.android_version!!,it.Available_Storage!!,it.Total_Storage!!,it.Power_Saver_Status))
        }
        var totalVisitRevisitCount = AppDatabase.getDBInstance()!!.shopActivityDao().getVisitRevisitCountByDate(AppUtils.getCurrentDateForShopActi())
        var totalVisitRevisitCountSynced = AppDatabase.getDBInstance()!!.shopActivityDao().getVisitRevisitCountByDateSyncedUnSynced(AppUtils.getCurrentDateForShopActi(),true)
        var totalVisitRevisitCountUnSynced = AppDatabase.getDBInstance()!!.shopActivityDao().getVisitRevisitCountByDateSyncedUnSynced(AppUtils.getCurrentDateForShopActi(),false)

        val appInfoInput = AppInfoInputModel(Pref.session_token!!, Pref.user_id!!, appInfoList,totalVisitRevisitCount.toString(),totalVisitRevisitCountSynced.toString(),totalVisitRevisitCountUnSynced.toString())

        XLog.d("============App Info Input(Logout Sync)===========")
        XLog.d("session_token==========> " + appInfoInput.session_token)
        XLog.d("user_id==========> " + appInfoInput.user_id)
        XLog.d("app_info_list.size==========> " + appInfoInput.app_info_list?.size)
        XLog.d("powerSaverStatus==========> " + Pref.PowerSaverStatus)
        XLog.d("==================================================")

        progress_wheel.spin()
        val repository = LocationRepoProvider.provideLocationRepository()
        BaseActivity.compositeDisposable.add(
                repository.appInfo(appInfoInput)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
//                        .timeout(60 * 1, TimeUnit.SECONDS)
                        .subscribe({ result ->
                            val response = result as BaseResponse

                            XLog.d("App Info : RESPONSE : " + response.status + ":" + response.message)
                            AppUtils.isAppInfoUpdating = false

                            if (response.status == NetworkConstant.SUCCESS) {
                                doAsync {
                                    unSyncData.forEach {
                                        AppDatabase.getDBInstance()?.batteryNetDao()?.updateIsUploadedAccordingToId(true, it.id)
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                    }
                                }
                            }
                            else {
                                progress_wheel.stopSpinning()
                                calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                            }

                        }, { error ->
                            AppUtils.isAppInfoUpdating = false
                            XLog.d("App Info : ERROR : " + error.localizedMessage)
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                        })
        )
    }



    private fun animateSyncImage(icon: AppCompatImageView) {
        icon.animation = AnimationUtils.loadAnimation(mContext, R.anim.rotation_sync)
        icon.startAnimation(icon.animation)
    }

    private fun stopAnimation(icon: AppCompatImageView) {
        icon.animate().cancel()
        icon.clearAnimation()
    }

    /*20-12-2021*/
    private fun callReturnApi(){
        stopAnimation(addReturnSyncImg)
        addReturnSyncImg.visibility=View.GONE
        addReturnTickImg.visibility=View.VISIBLE
        try{
            var returnList : ReturnRequest = ReturnRequest()
            var unsyncData= AppDatabase.getDBInstance()?.returnDetailsDao()!!.getAllUnsynced()
            if(unsyncData != null && unsyncData.isNotEmpty() && unsyncData.size!=0){
                var i=0
                returnList.user_id=Pref.user_id
                returnList.session_token=Pref.session_token
                returnList.shop_id=unsyncData?.get(i).shop_id
                returnList.return_id=unsyncData?.get(i).return_id
                returnList.latitude=unsyncData?.get(i).return_lat
                returnList.longitude=unsyncData?.get(i).return_long
                returnList.description=unsyncData?.get(i).description
                returnList.return_date_time=unsyncData?.get(i).date
                returnList.address=""
                returnList.return_amount=unsyncData?.get(i).amount


                var returnProductList= AppDatabase.getDBInstance()?.returnProductListDao()?.getIDUnsynced(returnList?.return_id.toString())
                var reproductList:MutableList<ReturnProductList> = ArrayList()
                for(j in 0..returnProductList!!.size-1){
                    var obj= ReturnProductList()
                    obj.id=returnProductList.get(j).product_id.toString()
                    obj.product_name=returnProductList.get(j).product_name
                    obj.qty=returnProductList.get(j).qty
                    obj.rate=returnProductList.get(j).rate
                    obj.total_price=returnProductList.get(j).total_price
                    reproductList.add(obj)
                }
                returnList.return_list=reproductList

                val repository = AddOrderRepoProvider.provideAddOrderRepository()
                compositeDisposable.add(
                        repository.addReturn(returnList)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    XLog.d("Return : RESPONSE " + result.status)
                                    if (result.status == NetworkConstant.SUCCESS){
                                        AppDatabase.getDBInstance()?.returnDetailsDao()?.updateIsUploaded(true,returnList.return_id!!)
                                        callReturnApi()
                                    }
                                    else if(result.status == NetworkConstant.SESSION_MISMATCH) {
                                    (mContext as DashboardActivity).showSnackMessage(result.message!!)
                                }
                                },{error ->
                                    if (error == null) {
                                        XLog.d("Return : ERROR " + "UNEXPECTED ERROR IN Add Return API")
                                    } else {
                                        XLog.d("Return : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
//                                    checkToCallActivity()
                                    callLogshareApi()
                                })
                )

            }else{
//                checkToCallActivity()
                callLogshareApi()
            }
        }catch (ex:Exception){
//            checkToCallActivity()
            callLogshareApi()

        }
    }

    private fun callLogshareApi(){
        if(Pref.LogoutWithLogFile){
            try{
                val filesForZip: Array<String> = arrayOf(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "xkcteamlogsample/log").path)
                ZipOutputStream(BufferedOutputStream(FileOutputStream(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "xkcteamlogsample/log.zip").path))).use { out ->
                    for (file in filesForZip) {
                        FileInputStream(file).use { fi ->
                            BufferedInputStream(fi).use { origin ->
                                val entry = ZipEntry(file.substring(file.lastIndexOf("/")))
                                out.putNextEntry(entry)
                                origin.copyTo(out, 1024)
                            }
                        }
                    }
                }

                val addReqData = AddLogReqData()
                addReqData.user_id = Pref.user_id
                val fileUrl = Uri.parse(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "xkcteamlogsample/log.zip").path);
                val file = File(fileUrl.path)
                if (!file.exists()) {
                    checkToCallActivity()
                }
                val uri: Uri = FileProvider.getUriForFile(mContext, mContext!!.applicationContext.packageName.toString() + ".provider", file)
                try{
                    val repository = EditShopRepoProvider.provideEditShopRepository()
                    BaseActivity.compositeDisposable.add(
                        repository.addLogfile(addReqData,file.toString(),mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                XLog.d("Logshare : RESPONSE " + result.status)
                                if (result.status == NetworkConstant.SUCCESS){
                                    //XLog.d("Return : RESPONSE URL " + result.file_url +  " " +Pref.user_name)
                                }
                                checkToCallActivity()
                            },{error ->
                                if (error == null) {
                                    XLog.d("Logshare : ERROR " + "UNEXPECTED ERROR IN Log share API")
                                } else {
                                    XLog.d("Logshare : ERROR " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                                checkToCallActivity()
                            })
                    )

                } catch (ex:Exception){
                    ex.printStackTrace()
                    XLog.d("Logshare : Exception " + "UNEXPECTED ERROR IN Log share API")
                    checkToCallActivity()
                }
            }catch (ex:Exception){
                XLog.d("Logshare : log.zip error " + ex.message)
                checkToCallActivity()
            }

        }else{
            checkToCallActivity()
        }
    }


}
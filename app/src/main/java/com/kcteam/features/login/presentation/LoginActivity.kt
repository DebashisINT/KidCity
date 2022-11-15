package com.kcteam.features.login.presentation


import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.*
import com.kcteam.app.AlarmReceiver.Companion.setAlarm
import com.kcteam.app.domain.*
import com.kcteam.app.utils.*
import com.kcteam.app.utils.AppUtils.Companion.getCurrentTimeInMintes
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.features.activities.api.ActivityRepoProvider
import com.kcteam.features.activities.model.*
import com.kcteam.features.addAttendence.FingerprintDialog
import com.kcteam.features.addAttendence.SelfieDialog
import com.kcteam.features.addshop.api.areaList.AreaListRepoProvider
import com.kcteam.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.kcteam.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.*
import com.kcteam.features.addshop.model.TypeListResponseModel
import com.kcteam.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.kcteam.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.kcteam.features.alarm.model.AlarmData
import com.kcteam.features.beatCustom.BeatGetStatusModel
import com.kcteam.features.beatCustom.api.GetBeatRegProvider
import com.kcteam.features.billing.api.billinglistapi.BillingListRepoProvider
import com.kcteam.features.billing.model.BillingListResponseModel
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dashboard.presentation.api.dayStartEnd.DayStartEndRepoProvider
import com.kcteam.features.dashboard.presentation.api.gteroutelistapi.GetRouteListRepoProvider
import com.kcteam.features.dashboard.presentation.model.SelectedRouteListResponseModel
import com.kcteam.features.dashboard.presentation.model.StatusDayStartEnd
import com.kcteam.features.document.api.DocumentRepoProvider
import com.kcteam.features.document.model.DocumentListResponseModel
import com.kcteam.features.document.model.DocumentTypeResponseModel
import com.kcteam.features.forgotpassword.presentation.ForgotPasswordDialog
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.SingleShotLocationProvider
import com.kcteam.features.location.api.LocationRepoProvider
import com.kcteam.features.location.model.AppInfoResponseModel
import com.kcteam.features.location.model.VisitRemarksResponseModel
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.ShopFeedbackEntity
import com.kcteam.features.login.UserLoginDataEntity
import com.kcteam.features.login.api.LoginRepositoryProvider
import com.kcteam.features.login.api.alarmconfigapi.AlarmConfigRepoProvider
import com.kcteam.features.login.api.global_config.ConfigFetchRepoProvider
import com.kcteam.features.login.api.productlistapi.ProductListRepoProvider
import com.kcteam.features.login.api.user_config.UserConfigRepoProvider
import com.kcteam.features.login.model.*
import com.kcteam.features.login.model.alarmconfigmodel.AlarmConfigResponseModel
import com.kcteam.features.login.model.globalconfig.ConfigFetchResponseModel
import com.kcteam.features.login.model.mettingListModel.MeetingListResponseModel
import com.kcteam.features.login.model.productlistmodel.ModelListResponse
import com.kcteam.features.login.model.productlistmodel.NewOdrScrOrderListModel
import com.kcteam.features.login.model.productlistmodel.ProductListOfflineResponseModelNew
import com.kcteam.features.login.model.productlistmodel.ProductListResponseModel
import com.kcteam.features.login.model.userconfig.UserConfigResponseModel
import com.kcteam.features.meetinglist.api.MeetingRepoProvider
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamAreaListResponseModel
import com.kcteam.features.member.model.UserPjpResponseModel
import com.kcteam.features.nearbyshops.api.ShopListRepositoryProvider
import com.kcteam.features.nearbyshops.model.*
import com.kcteam.features.newcollection.model.NewCollectionListResponseModel
import com.kcteam.features.newcollection.model.PaymentModeResponseModel
import com.kcteam.features.newcollection.newcollectionlistapi.NewCollectionListRepoProvider
import com.kcteam.features.orderList.api.neworderlistapi.NewOrderListRepoProvider
import com.kcteam.features.orderList.model.NewOrderListResponseModel
import com.kcteam.features.orderList.model.ReturnListResponseModel
import com.kcteam.features.quotation.api.QuotationRepoProvider
import com.kcteam.features.quotation.model.BSListResponseModel
import com.kcteam.features.quotation.model.QuotationListResponseModel
import com.kcteam.features.stock.api.StockRepositoryProvider
import com.kcteam.features.stock.model.NewStockListResponseModel
import com.kcteam.features.stockAddCurrentStock.api.ShopAddStockProvider
import com.kcteam.features.stockAddCurrentStock.model.CurrentStockGetData
import com.kcteam.features.stockCompetetorStock.api.AddCompStockProvider
import com.kcteam.features.stockCompetetorStock.model.CompetetorStockGetData
import com.kcteam.features.task.api.TaskRepoProvider
import com.kcteam.features.task.model.TaskListResponseModel
import com.kcteam.features.timesheet.api.TimeSheetRepoProvider
import com.kcteam.features.timesheet.model.TimeSheetConfigResponseModel
import com.kcteam.features.timesheet.model.TimeSheetDropDownResponseModel
import com.kcteam.features.viewAllOrder.api.OrderDetailsListRepoProvider
import com.kcteam.features.viewAllOrder.model.NewOrderDataModel
import com.kcteam.features.viewPPDDStock.api.stocklist.StockListRepoProvider
import com.kcteam.features.viewPPDDStock.model.stocklist.StockListDataModel
import com.kcteam.features.viewPPDDStock.model.stocklist.StockListResponseModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.google.android.material.textfield.TextInputEditText
import com.google.common.util.concurrent.ListenableFuture
import com.theartofdev.edmodo.cropper.CropImage
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_new.*
import net.alexandroid.gps.GpsStatusDetector
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.nio.channels.FileChannel
import java.util.*
import java.util.concurrent.ExecutionException


/**Permission NameDISABLE KEYGUARD Status
 * Created by Pratishruti on 26-10-2017.
 */
class LoginActivity : BaseActivity(), View.OnClickListener, LocationListener {

    override fun onLocationChanged(location: Location) {
        Pref.latitude = location?.latitude.toString()
        Pref.longitude = location?.longitude.toString()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        Log.d("TAG", "onStatusChanged");
    }

    override fun onProviderEnabled(p0: String) {
        getLocation()
    }

    override fun onProviderDisabled(p0: String) {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this);
        }
    }

    private lateinit var forgotPassword: AppCustomTextView

    //    private lateinit var userLoginDataEntity: UserLoginDataEntity
    private lateinit var alert_snack_bar: CoordinatorLayout
    private lateinit var username_EDT: AppCustomEditText
    private lateinit var password_EDT: TextInputEditText
    private lateinit var list: List<AddShopDBModelEntity>
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var version_name_TV: AppCustomTextView
    private var locationManager: LocationManager? = null
    var isGPS = false
    var isNetwork = false
    var canGetLocation = true
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f
    private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong()
    var loc: Location? = null
    private var permissionUtils: PermissionUtils? = null
    private val DRAW_OVER_OTHER_APP_PERMISSION = 123
    private var fingerprintDialog: FingerprintDialog? = null
    private var isFingerPrintSupported = true
    private var selfieDialog: SelfieDialog? = null
    private var mCurrentPhotoPath: String = ""
    private var filePath: String = ""
    private lateinit var iv_shopImage: ImageView
    private var checkFingerPrint: CheckFingerPrint? = null
    private lateinit var tv_internet_info: AppCustomTextView
    private lateinit var cb_remember_me: CheckBox
    private lateinit var share_log_login_TV: AppCustomTextView

    private lateinit var tvappCustomAnydeskInfo: AppCustomTextView

    private lateinit var icon_internet:ImageView
//    private lateinit var tvappCustomAnydesk: AppCustomTextView
//    private lateinit var tvappCustomSharelog: AppCustomTextView

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_new)
        mContext = this@LoginActivity
        println("xyz - login oncreate started" + AppUtils.getCurrentDateTime());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            initPermissionCheck()
        else
            getIMEI()

        locationManager = getSystemService(Service.LOCATION_SERVICE) as LocationManager
        isGPS = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (!isGPS && !isNetwork) {
//            showSettingsAlert();
            getLastLocation();
        } else {

            // get location
            getLocation();
        }

        initView()

        if (AppUtils.getSharedPreferenceslogShareinLogin(this)) {
            share_log_login_TV.visibility = View.GONE
        } else {
            share_log_login_TV.visibility = View.GONE
        }

        //askForSystemOverlayPermission()

        /*Handler().postDelayed(Runnable {
            getConfigFetchApi()
        },100)*/

        WorkManager.getInstance(this).cancelAllWork()
        WorkManager.getInstance(this).cancelAllWorkByTag("workerTag")
    }

    fun isWorkerRunning(tag:String):Boolean{
        val workInstance = WorkManager.getInstance(this)
        val status: ListenableFuture<List<WorkInfo>> = WorkManager.getInstance(this).getWorkInfosByTag(tag)
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


    override fun onStart() {
        super.onStart()
        //getConfigFetchApi()
    }

    private fun getConfigFetchApi() {

        /*if (!AppUtils.isOnline(this)) {
            showSnackMessage(getString(R.string.no_internet))
            return
        }*/

        val repository = ConfigFetchRepoProvider.provideConfigFetchRepository()
        //progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.configFetch()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val configResponse = result as ConfigFetchResponseModel
                            XLog.d("ConfigFetchApiResponse : " + "\n" + "Status=====> " + configResponse.status + ", Message====> " + configResponse.message)

                            progress_wheel.stopSpinning()
                            if (configResponse.status == NetworkConstant.SUCCESS) {

                                if (!TextUtils.isEmpty(configResponse.min_distance))
                                    AppUtils.minDistance = configResponse.min_distance!!

                                if (!TextUtils.isEmpty(configResponse.max_distance))
                                    AppUtils.maxDistance = configResponse.max_distance!!

                                if (!TextUtils.isEmpty(configResponse.max_accuracy))
                                    AppUtils.maxAccuracy = configResponse.max_accuracy!!

                                if (!TextUtils.isEmpty(configResponse.min_accuracy))
                                    AppUtils.minAccuracy = configResponse.min_accuracy!!

                                /*if (!TextUtils.isEmpty(configResponse.idle_time))
                                    AppUtils.idle_time = configResponse.idle_time!!*/

                                if (configResponse.willStockShow != null)
                                    Pref.willStockShow = configResponse.willStockShow!!

                                // From Hahnemann
                                if (configResponse.isPrimaryTargetMandatory != null)
                                    Pref.isPrimaryTargetMandatory = configResponse.isPrimaryTargetMandatory!!

                                if (configResponse.isRevisitCaptureImage != null)
                                    Pref.isRevisitCaptureImage = configResponse.isRevisitCaptureImage!!

                                if (configResponse.isShowAllProduct != null)
                                    Pref.isShowAllProduct = configResponse.isShowAllProduct!!

                                if (configResponse.isStockAvailableForAll != null)
                                    Pref.isStockAvailableForAll = configResponse.isStockAvailableForAll!!

                                if (configResponse.isStockAvailableForPopup != null)
                                    Pref.isStockAvailableForPopup = configResponse.isStockAvailableForPopup!!

                                if (configResponse.isOrderAvailableForPopup != null)
                                    Pref.isOrderAvailableForPopup = configResponse.isOrderAvailableForPopup!!

                                if (configResponse.isCollectionAvailableForPopup != null)
                                    Pref.isCollectionAvailableForPopup = configResponse.isCollectionAvailableForPopup!!

                                if (configResponse.isDDFieldEnabled != null)
                                    Pref.isDDFieldEnabled = configResponse.isDDFieldEnabled!!

                                if (!TextUtils.isEmpty(configResponse.maxFileSize))
                                    Pref.maxFileSize = configResponse.maxFileSize!!

                                if (configResponse.willKnowYourStateShow != null)
                                    Pref.willKnowYourStateShow = configResponse.willKnowYourStateShow!!

                                if (configResponse.willAttachmentCompulsory != null)
                                    Pref.willAttachmentCompulsory = configResponse.willAttachmentCompulsory!!

                                if (configResponse.canAddBillingFromBillingList != null)
                                    Pref.canAddBillingFromBillingList = configResponse.canAddBillingFromBillingList!!

                                if (!TextUtils.isEmpty(configResponse.allPlanListHeaderText))
                                    Pref.allPlanListHeaderText = configResponse.allPlanListHeaderText!!

                                if (configResponse.willSetYourTodaysTargetVisible != null)
                                    Pref.willSetYourTodaysTargetVisible = configResponse.willSetYourTodaysTargetVisible!!

                                if (!TextUtils.isEmpty(configResponse.attendenceAlertHeading))
                                    Pref.attendenceAlertHeading = configResponse.attendenceAlertHeading!!

                                if (!TextUtils.isEmpty(configResponse.attendenceAlertText))
                                    Pref.attendenceAlertText = configResponse.attendenceAlertText!!

                                if (!TextUtils.isEmpty(configResponse.meetingText))
                                    Pref.meetingText = configResponse.meetingText!!

                                if (!TextUtils.isEmpty(configResponse.meetingDistance))
                                    Pref.meetingDistance = configResponse.meetingDistance!!

                                if (configResponse.isActivatePJPFeature != null)
                                    Pref.isActivatePJPFeature = configResponse.isActivatePJPFeature!!

                                if (configResponse.willReimbursementShow != null)
                                    Pref.willReimbursementShow = configResponse.willReimbursementShow!!

                                if (!TextUtils.isEmpty(configResponse.updateBillingText))
                                    Pref.updateBillingText = configResponse.updateBillingText!!

                                if (configResponse.isRateOnline != null)
                                    Pref.isRateOnline = configResponse.isRateOnline!!

                                if (!TextUtils.isEmpty(configResponse.ppText))
                                    Pref.ppText = configResponse.ppText

                                if (!TextUtils.isEmpty(configResponse.ddText))
                                    Pref.ddText = configResponse.ddText

                                if (!TextUtils.isEmpty(configResponse.shopText))
                                    Pref.shopText = configResponse.shopText

                                if (configResponse.isCustomerFeatureEnable != null)
                                    Pref.isCustomerFeatureEnable = configResponse.isCustomerFeatureEnable!!

                                if (configResponse.isAreaVisible != null)
                                    Pref.isAreaVisible = configResponse.isAreaVisible!!

                                if (!TextUtils.isEmpty(configResponse.cgstPercentage))
                                    Pref.cgstPercentage = configResponse.cgstPercentage

                                if (!TextUtils.isEmpty(configResponse.sgstPercentage))
                                    Pref.sgstPercentage = configResponse.sgstPercentage

                                if (!TextUtils.isEmpty(configResponse.tcsPercentage))
                                    Pref.tcsPercentage = configResponse.tcsPercentage

                                if (!TextUtils.isEmpty(configResponse.docAttachmentNo))
                                    Pref.docAttachmentNo = configResponse.docAttachmentNo

                                if (!TextUtils.isEmpty(configResponse.chatBotMsg))
                                    Pref.chatBotMsg = configResponse.chatBotMsg

                                if (!TextUtils.isEmpty(configResponse.contactMail))
                                    Pref.contactMail = configResponse.contactMail

                                if (configResponse.isVoiceEnabledForAttendanceSubmit != null)
                                    Pref.isVoiceEnabledForAttendanceSubmit = configResponse.isVoiceEnabledForAttendanceSubmit!!

                                if (configResponse.isVoiceEnabledForOrderSaved != null)
                                    Pref.isVoiceEnabledForOrderSaved = configResponse.isVoiceEnabledForOrderSaved!!

                                if (configResponse.isVoiceEnabledForInvoiceSaved != null)
                                    Pref.isVoiceEnabledForInvoiceSaved = configResponse.isVoiceEnabledForInvoiceSaved!!

                                if (configResponse.isVoiceEnabledForCollectionSaved != null)
                                    Pref.isVoiceEnabledForCollectionSaved = configResponse.isVoiceEnabledForCollectionSaved!!

                                if (configResponse.isVoiceEnabledForHelpAndTipsInBot != null)
                                    Pref.isVoiceEnabledForHelpAndTipsInBot = configResponse.isVoiceEnabledForHelpAndTipsInBot!!


                                if (configResponse.GPSAlert != null)
                                    Pref.GPSAlertGlobal = configResponse.GPSAlert!!

                                //02-11-2021
                                if (configResponse.IsDuplicateShopContactnoAllowedOnline != null)
                                    Pref.IsDuplicateShopContactnoAllowedOnline = configResponse.IsDuplicateShopContactnoAllowedOnline!!


                                /*26-11-2021*/

                                if (configResponse.BatterySetting != null)
                                    Pref.BatterySettingGlobal = configResponse.BatterySetting!!

                                if (configResponse.PowerSaverSetting != null)
                                    Pref.PowerSaverSettingGlobal = configResponse.PowerSaverSetting!!

                                /*1-12-2021*/
                                if (configResponse.IsnewleadtypeforRuby != null)
                                    Pref.IsnewleadtypeforRuby = configResponse.IsnewleadtypeforRuby!!


                                /*16-12-2021*/
                                if (configResponse.IsReturnActivatedforPP != null)
                                    Pref.IsReturnActivatedforPP = configResponse.IsReturnActivatedforPP!!

                                if (configResponse.IsReturnActivatedforDD != null)
                                    Pref.IsReturnActivatedforDD = configResponse.IsReturnActivatedforDD!!

                                if (configResponse.IsReturnActivatedforSHOP != null)
                                    Pref.IsReturnActivatedforSHOP = configResponse.IsReturnActivatedforSHOP!!

                                /*06-01-2022*/
                                if (configResponse.MRPInOrder != null)
                                    Pref.MRPInOrderGlobal = configResponse.MRPInOrder!!
                                if (configResponse.FaceRegistrationFrontCamera != null)
                                    Pref.FaceRegistrationOpenFrontCamera = configResponse.FaceRegistrationFrontCamera!!

                                /*07-02-2022*/
                                try{
                                    Pref.SqMtrRateCalculationforQuotEuro = configResponse.SqMtrRateCalculationforQuotEuro!!.toString()
                                }catch (ex:Exception){
                                    Pref.SqMtrRateCalculationforQuotEuro = "0.0"
                                }

                                /*17-02-2022*/
                                if (configResponse.NewQuotationRateCaption != null)
                                Pref.NewQuotationRateCaption = configResponse.NewQuotationRateCaption!!

                                if (configResponse.NewQuotationShowTermsAndCondition != null)
                                    Pref.NewQuotationShowTermsAndCondition = configResponse.NewQuotationShowTermsAndCondition!!

                                if (configResponse.IsCollectionEntryConsiderOrderOrInvoice != null)
                                    Pref.IsCollectionEntryConsiderOrderOrInvoice = configResponse.IsCollectionEntryConsiderOrderOrInvoice!!

                                if (!TextUtils.isEmpty(configResponse.contactNameText))
                                    Pref.contactNameText = configResponse.contactNameText

                                if (!TextUtils.isEmpty(configResponse.contactNumberText))
                                    Pref.contactNumberText = configResponse.contactNumberText

                                if (!TextUtils.isEmpty(configResponse.emailText))
                                    Pref.emailText = configResponse.emailText

                                if (!TextUtils.isEmpty(configResponse.dobText))
                                    Pref.dobText = configResponse.dobText

                                if (!TextUtils.isEmpty(configResponse.dateOfAnniversaryText))
                                    Pref.dateOfAnniversaryText = configResponse.dateOfAnniversaryText

                                if (configResponse.ShopScreenAftVisitRevisit != null)
                                    Pref.ShopScreenAftVisitRevisitGlobal = configResponse.ShopScreenAftVisitRevisit!!

                                if (configResponse.IsSurveyRequiredforNewParty != null)
                                    Pref.IsSurveyRequiredforNewParty = configResponse.IsSurveyRequiredforNewParty!!

                                if (configResponse.IsSurveyRequiredforDealer != null)
                                    Pref.IsSurveyRequiredforDealer = configResponse.IsSurveyRequiredforDealer!!

                                 if (configResponse.IsShowHomeLocationMap != null)
                                    Pref.IsShowHomeLocationMapGlobal = configResponse.IsShowHomeLocationMap!!

                                if (configResponse.IsBeatRouteAvailableinAttendance != null)
                                    Pref.IsBeatRouteAvailableinAttendance = configResponse.IsBeatRouteAvailableinAttendance!!

                                if (configResponse.IsAllBeatAvailable != null)
                                    Pref.IsAllBeatAvailableforParty = configResponse.IsAllBeatAvailable!!

                                if (configResponse.BeatText != null)
                                    Pref.beatText=configResponse.BeatText!!

                                if (configResponse.TodaysTaskText != null)
                                    Pref.TodaysTaskText=configResponse.TodaysTaskText!!

                                if (configResponse.IsDistributorSelectionRequiredinAttendance != null)
                                    Pref.IsDistributorSelectionRequiredinAttendance = configResponse.IsDistributorSelectionRequiredinAttendance!!

                                if (configResponse.IsAllowNearbyshopWithBeat != null)
                                    Pref.IsAllowNearbyshopWithBeat = configResponse.IsAllowNearbyshopWithBeat!!

                                if (configResponse.IsGSTINPANEnableInShop != null)
                                    Pref.IsGSTINPANEnableInShop = configResponse.IsGSTINPANEnableInShop!!

                                if (configResponse.IsMultipleImagesRequired != null)
                                    Pref.IsMultipleImagesRequired = configResponse.IsMultipleImagesRequired!!


                                /*if (configResponse.willShowUpdateDayPlan != null)
                                    Pref.willShowUpdateDayPlan = configResponse.willShowUpdateDayPlan!!

                                if (!TextUtils.isEmpty(configResponse.updateDayPlanText))
                                    Pref.updateDayPlanText = configResponse.updateDayPlanText!!

                                if (!TextUtils.isEmpty(configResponse.dailyPlanListHeaderText))
                                    Pref.dailyPlanListHeaderText = configResponse.dailyPlanListHeaderText!!*/
                            }
                            isApiInitiated = false
                            /*API_Optimization 02-03-2022*/
                            //checkToCallAlarmConfigApi()   // calling instead of checkToCallAlarmConfigApi()
                            isStartOrEndDay()

                        }, { error ->
                            isApiInitiated = false
                            error.printStackTrace()
                            /*API_Optimization 02-03-2022*/
                            //checkToCallAlarmConfigApi()   // calling instead of checkToCallAlarmConfigApi()
                            isStartOrEndDay()
                            progress_wheel.stopSpinning()
                            XLog.d("ConfigFetchApiResponse ERROR: " + error.localizedMessage)
                        })
        )
    }

    private fun checkToCallAlarmConfigApi() {
        if (Pref.willAlarmTrigger)
            callAlarmConfig()
        else {
            checkToCallBillListApi()
        }
    }

    private fun callAlarmConfig() {
        val repository = AlarmConfigRepoProvider.provideAlarmConfigRepository()
        //progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.alarmConfig()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val configResponse = result as AlarmConfigResponseModel
                            XLog.d("AlarmConfigApiResponse : " + "\n" + "Status=====> " + configResponse.status + ", Message====> " + configResponse.message)

                            progress_wheel.stopSpinning()
                            if (configResponse.status == NetworkConstant.SUCCESS) {

                                val alarmArr = ArrayList<AlarmData>()
                                for (item in configResponse.alarm_settings_list!!) {

                                    if (getCurrentTimeInMintes() < ((item.alarm_time_hours!!.toInt() * 60) + item.alarm_time_mins!!.toInt())) {
                                        val al = AlarmData()
                                        al.requestCode = 2010 + item.id!!.toInt()
                                        al.id = item.id
                                        al.report_id = item.report_id!!
                                        al.report_title = item.report_title!!
                                        al.alarm_time_hours = item.alarm_time_hours!!
                                        al.alarm_time_mins = item.alarm_time_mins!!

                                        alarmArr.add(al)

                                        setAlarm(this, item.alarm_time_hours!!.toInt(), item.alarm_time_mins!!.toInt(), al.requestCode)
                                    }

                                }

                                AlarmReceiver.saveSharedPreferencesLogList(this, alarmArr)
                            }

                            isApiInitiated = false
                            checkToCallBillListApi()
                        }, { error ->
                            isApiInitiated = false
                            error.printStackTrace()
                            checkToCallBillListApi()
                            progress_wheel.stopSpinning()
                            XLog.d("AlarmConfigApiResponse ERROR: " + error.localizedMessage)
                        })
        )
    }

    private fun checkToCallBillListApi() {
        val list = AppDatabase.getDBInstance()!!.billingDao().getAll()
        if (list != null && list.isNotEmpty())
            checkToCallRateList()
        else
            getBillListApi()
    }

    private fun getBillListApi() {
        if (Pref.canAddBillingFromBillingList) {
            XLog.d("API_Optimization GET getBillListApi Login : enable " + "Time: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            val repository = BillingListRepoProvider.provideBillListRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.getBillList(Pref.session_token!!, Pref.user_id!!, "")
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BillingListResponseModel
                                isApiInitiated = false
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

                                                if (!TextUtils.isEmpty(billing_list[i].billing_image))
                                                    billing.attachment = billing_list[i].billing_image

                                                AppDatabase.getDBInstance()!!.billingDao().insertAll(billing)

                                                for (j in billing_list[i].product_list?.indices!!) {
                                                    val billingProductEntity = BillingProductListEntity()
                                                    billingProductEntity.bill_id = billing.bill_id
                                                    billingProductEntity.order_id = billing.order_id
                                                    billingProductEntity.brand = billing_list[i].product_list?.get(j)?.brand
                                                    billingProductEntity.brand_id = billing_list[i].product_list?.get(j)?.brand_id
                                                    billingProductEntity.category = billing_list[i].product_list?.get(j)?.category
                                                    billingProductEntity.category_id = billing_list[i].product_list?.get(j)?.category_id
                                                    billingProductEntity.product_name = billing_list[i].product_list?.get(j)?.product_name
                                                    billingProductEntity.product_id = billing_list[i].product_list?.get(j)?.id
                                                    billingProductEntity.qty = billing_list[i].product_list?.get(j)?.qty
                                                    billingProductEntity.rate = billing_list[i].product_list?.get(j)?.rate
                                                    billingProductEntity.total_price = billing_list[i].product_list?.get(j)?.total_price
                                                    billingProductEntity.watt = billing_list[i].product_list?.get(j)?.watt
                                                    billingProductEntity.watt_id = billing_list[i].product_list?.get(j)?.watt_id

                                                    AppDatabase.getDBInstance()!!.billProductDao().insert(billingProductEntity)
                                                }
                                            }

                                            uiThread {
                                                progress_wheel.stopSpinning()
                                                checkToCallRateList()
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        checkToCallRateList()
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallRateList()
                                }

                            }, { error ->
                                error.printStackTrace()
                                isApiInitiated = false
                                progress_wheel.stopSpinning()
                                checkToCallRateList()
                            })
            )
        }
        else{
            XLog.d("API_Optimization GET getBillListApi Login : disable " + "Time: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            checkToCallRateList()
        }
    }

    private fun checkToCallRateList() {
        /*if (Pref.isRateNotEditable) {
            getProductRateListApi()
        } else*/

        val list = AppDatabase.getDBInstance()!!.addMeetingTypeDao().getAll()
        if (list != null && list.isNotEmpty())
            checkToCallMeetingList()
        else
            getMeetingTypeListApi()
    }

    private fun getMeetingTypeListApi() {
        val repository = LoginRepositoryProvider.provideLoginRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getMeetingList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as MeetingListResponseModel
                            isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.meeting_type_list != null && response.meeting_type_list!!.size > 0) {

                                    doAsync {

                                        val list = response.meeting_type_list

                                        for (i in list!!.indices) {
                                            val meetingType = MeetingTypeEntity()
                                            meetingType.typeId = list[i].type_id.toInt()
                                            meetingType.typeText = list[i].type_text

                                            AppDatabase.getDBInstance()!!.addMeetingTypeDao().insertAll(meetingType)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallMeetingList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallMeetingList()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallMeetingList()
                            }

                        }, { error ->
                            error.printStackTrace()
                            isApiInitiated = false
                            progress_wheel.stopSpinning()
                            checkToCallMeetingList()
                        })
        )
    }


    private fun checkToCallMeetingList() {
        val list = AppDatabase.getDBInstance()!!.addMeetingDao().getAll()
        if (list != null && list.isNotEmpty())
            checkToCallProductRateList()
        else
            getMeetingList()
    }

    private fun getMeetingList() {
        if (Pref.isMeetingAvailable) {
            XLog.d("API_Optimization GET getMeetingList Login : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
        progress_wheel.spin()
        val repository = MeetingRepoProvider.meetingRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.meetingList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as com.kcteam.features.meetinglist.model.MeetingListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.meeting_list != null && response.meeting_list!!.size > 0) {

                                    doAsync {
                                        for (i in response.meeting_list!!.indices) {
                                            val meetingEntity = MeetingEntity()
                                            meetingEntity.date = response.meeting_list?.get(i)?.date
                                            meetingEntity.duration_spent = response.meeting_list?.get(i)?.duration
                                            meetingEntity.remakrs = response.meeting_list?.get(i)?.remarks
                                            meetingEntity.date_time = response.meeting_list?.get(i)?.date_time
                                            meetingEntity.lattitude = response.meeting_list?.get(i)?.latitude
                                            meetingEntity.longitude = response.meeting_list?.get(i)?.longitude
                                            meetingEntity.meetingTypeId = response.meeting_list?.get(i)?.meeting_type_id
                                            meetingEntity.address = response.meeting_list?.get(i)?.address
                                            meetingEntity.pincode = response.meeting_list?.get(i)?.pincode
                                            meetingEntity.distance_travelled = response.meeting_list?.get(i)?.distance_travelled
                                            meetingEntity.isUploaded = true
                                            meetingEntity.isDurationCalculated = true

                                            AppDatabase.getDBInstance()!!.addMeetingDao().insertAll(meetingEntity)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallProductRateList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallProductRateList()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallProductRateList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            checkToCallProductRateList()
                        })
        )
        }
        else{
            XLog.d("API_Optimization GET getMeetingList Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallProductRateList()
        }
    }

    private fun checkToCallProductRateList() {
        val list = AppDatabase.getDBInstance()?.productRateDao()?.getAll()
        if (list != null && list.isNotEmpty())
            checkToCallAreaListApi()
        else
            getProductRateListApi()
    }

    private fun getProductRateListApi() {
        if(Pref.isOrderShow){
            XLog.d("API_Optimization getProductRateListApi Login : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
        val repository = ProductListRepoProvider.productListProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getProductRateOfflineListNew()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            //val response = result as ProductListOfflineResponseModel
                            val response = result as ProductListOfflineResponseModelNew
                            isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {
                                val productRateList = response.product_rate_list

                                if (productRateList != null && productRateList.size > 0) {
                                    //AppUtils.saveSharedPreferencesProductRateList(this@LoginActivity, productRateList)

                                    doAsync {


                                        AppDatabase.getDBInstance()?.productRateDao()?.insertAll(productRateList)


                                        /*  productRateList.forEach {
                                              val productRate = ProductRateEntity()
                                              AppDatabase.getDBInstance()?.productRateDao()?.insert(productRate.apply {
                                                  product_id = it.product_id
                                                  rate1 = it.rate1
                                                  rate2 = it.rate2
                                                  rate3 = it.rate3
                                                  rate4 = it.rate4
                                                  rate5 = it.rate5
                                                  stock_amount = it.stock_amount
                                                  stock_unit = it.stock_unit
                                                  isStockShow = it.isStockShow
                                                  isRateShow = it.isRateShow
                                              })
                                          }*/

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallAreaListApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallAreaListApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallAreaListApi()
                            }

                        }, { error ->
                            error.printStackTrace()
                            isApiInitiated = false
                            progress_wheel.stopSpinning()
                            checkToCallAreaListApi()
                        })
        )
        }else{
            XLog.d("API_Optimization getProductRateListApi Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallAreaListApi()
        }
    }

    private fun checkToCallAreaListApi() {
        val areaList = AppDatabase.getDBInstance()?.areaListDao()?.getAll()
        if ((areaList == null || areaList.isEmpty()) && !TextUtils.isEmpty(Pref.profile_city))
            getAreaListApi()
        else
            checkToCallShopTypeApi()
    }

    private fun getAreaListApi() {
        if(Pref.isAreaVisible) {
            XLog.d("API_Optimization getAreaListApi Login : enable " + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            val repository = AreaListRepoProvider.provideAreaListRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.areaList(Pref.profile_city, "")
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as AreaListResponseModel
                                if (response.status == NetworkConstant.SUCCESS) {
                                    val list = response.area_list

                                    if (list != null && list.isNotEmpty()) {

                                        doAsync {

                                            list.forEach {
                                                val area = AreaListEntity()
                                                AppDatabase.getDBInstance()?.areaListDao()?.insert(area.apply {
                                                    area_id = it.area_id
                                                    area_name = it.area_name
                                                })
                                            }

                                            uiThread {
                                                progress_wheel.stopSpinning()
                                                checkToCallShopTypeApi()
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        checkToCallShopTypeApi()
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallShopTypeApi()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                error.printStackTrace()
                                checkToCallShopTypeApi()
                            })
            )
        }
        else{
            XLog.d("API_Optimization getAreaListApi Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallShopTypeApi()
        }
    }

    private fun checkToCallShopTypeApi() {
        /*14-12-2021*/
        AppDatabase.getDBInstance()?.shopTypeDao()?.deleteAll()
        val shopTypeList = AppDatabase.getDBInstance()?.shopTypeDao()?.getAll()
        if (shopTypeList == null || shopTypeList.isEmpty())
            getShopTypeListApi()
        else
            getPjpListApi()
    }

    private fun getShopTypeListApi() {
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getShopTypeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ShopTypeResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.Shoptype_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        list.forEach {
                                            val shop = ShopTypeEntity()
                                            AppDatabase.getDBInstance()?.shopTypeDao()?.insertAll(shop.apply {
                                                shoptype_id = it.shoptype_id
                                                shoptype_name = it.shoptype_name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getPjpListApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getPjpListApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getPjpListApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            getPjpListApi()
                        })
        )
    }


    private fun getPjpListApi() {
        if(Pref.isActivatePJPFeature){
            XLog.d("API_Optimization GET getPjpListApi Login : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getUserPJPList(AppUtils.getCurrentDateForShopActi())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UserPjpResponseModel
                            XLog.d("GET USER PJP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.pjp_list != null && response.pjp_list.isNotEmpty()) {

                                    doAsync {

                                        AppDatabase.getDBInstance()?.pjpListDao()?.deleteAll()

                                        response.pjp_list.forEach {
                                            val pjpEntity = PjpListEntity()
                                            AppDatabase.getDBInstance()?.pjpListDao()?.insert(pjpEntity.apply {
                                                pjp_id = it.id
                                                from_time = it.from_time
                                                to_time = it.to_time
                                                customer_name = it.customer_name
                                                customer_id = it.customer_id
                                                location = it.location
                                                date = it.date
                                                remarks = it.remarks
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkModelList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkModelList()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                checkModelList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET USER PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            checkModelList()
                        })
        )
        }else{
            XLog.d("API_Optimization GET getPjpListApi Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkModelList()
        }
    }

    private fun checkModelList() {
        val list = AppDatabase.getDBInstance()?.modelListDao()?.getAll()
        if (list == null || list.isEmpty())
            getModelListApi()
        else
            checkPrimaryAppList()
    }

    private fun getModelListApi() {
        println("xyzzz - getModelListApi called" + AppUtils.getCurrentDateTime());
        progress_wheel.spin()
        //checkPrimaryAppList()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                //repository.getModelList()
                repository.getModelListNew()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            //val response = result as ModelListResponseModelNew
                            val response = result as ModelListResponse
                            XLog.d("GET MODEL DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.model_list != null && response.model_list!!.isNotEmpty()) {

                                    doAsync {
                                        println("xyzzz - getModelListApi db_______started" + AppUtils.getCurrentDateTime());
                                        AppDatabase.getDBInstance()?.modelListDao()?.insertAllLarge(response.model_list!!)
                                        //AppDatabase.getDBInstance()?.modelListDao()?.getSinglepe()
                                        println("xyzzz - getModelListApi db_______started" + AppUtils.getCurrentDateTime());
                                        /*           response.model_list?.forEach {

                                                       val modelEntity = ModelEntity()
                                                       AppDatabase.getDBInstance()?.modelListDao()?.insertAll(modelEntity.apply {
                                                           model_id = it.id
                                                           model_name = it.name
                                                       })

                                                   }*/

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkPrimaryAppList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkPrimaryAppList()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                checkPrimaryAppList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET MODEL DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            checkPrimaryAppList()
                        })
        )
    }


    private fun checkPrimaryAppList() {
        /*val list = AppDatabase.getDBInstance()?.primaryAppListDao()?.getAll()
        if (list == null || list.isEmpty())*/
        gePrimaryAppListApi()
        /*else
            checkSecondaryAppList()*/
    }

    private fun gePrimaryAppListApi() {
        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getPrimaryAppList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as PrimaryAppListResponseModel
                            XLog.d("GET PRIMARY APP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.primary_application_list != null && response.primary_application_list!!.isNotEmpty()) {

                                    AppDatabase.getDBInstance()?.primaryAppListDao()?.deleteAll()

                                    doAsync {

                                        response.primary_application_list?.forEach {
                                            val primaryEntity = PrimaryAppEntity()
                                            AppDatabase.getDBInstance()?.primaryAppListDao()?.insertAll(primaryEntity.apply {
                                                primary_app_id = it.id
                                                primary_app_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkSecondaryAppList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkSecondaryAppList()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                checkSecondaryAppList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET PRIMARY APP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            checkSecondaryAppList()
                        })
        )
    }

    private fun checkSecondaryAppList() {
        /*val list = AppDatabase.getDBInstance()?.secondaryAppListDao()?.getAll()
        if (list == null || list.isEmpty())*/
        geSecondaryAppListApi()
        /*else
            checkLeadList()*/
    }

    private fun geSecondaryAppListApi() {
        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getSecondaryAppList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as SecondaryAppListResponseModel
                            XLog.d("GET SECONDARY APP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.secondary_application_list != null && response.secondary_application_list!!.isNotEmpty()) {

                                    AppDatabase.getDBInstance()?.secondaryAppListDao()?.deleteAll()

                                    doAsync {

                                        response.secondary_application_list?.forEach {
                                            val secondaryEntity = SecondaryAppEntity()
                                            AppDatabase.getDBInstance()?.secondaryAppListDao()?.insertAll(secondaryEntity.apply {
                                                secondary_app_id = it.id
                                                secondary_app_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkLeadList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkLeadList()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                checkLeadList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET SECONDARY APP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            checkLeadList()
                        })
        )
    }

    private fun checkLeadList() {
        /*val list = AppDatabase.getDBInstance()?.leadTypeDao()?.getAll()
        if (list == null || list.isEmpty())*/
        geLeadApi()
        /*else
            checkStageList()*/
    }

    private fun geLeadApi() {
        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getLeadTypeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as LeadListResponseModel
                            XLog.d("GET LEAD TYPE DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.lead_type_list != null && response.lead_type_list!!.isNotEmpty()) {

                                    AppDatabase.getDBInstance()?.leadTypeDao()?.deleteAll()

                                    doAsync {

                                        response.lead_type_list?.forEach {
                                            val leadEntity = LeadTypeEntity()
                                            AppDatabase.getDBInstance()?.leadTypeDao()?.insertAll(leadEntity.apply {
                                                lead_id = it.id
                                                lead_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkStageList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkStageList()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                checkStageList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET LEAD TYPE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            checkStageList()
                        })
        )
    }

    private fun checkStageList() {
        /*val list = AppDatabase.getDBInstance()?.stageDao()?.getAll()
        if (list == null || list.isEmpty())*/
        geStageApi()
        /*else
            checkFunnelStageList()*/
    }

    private fun geStageApi() {
        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getStagList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as StageListResponseModel
                            XLog.d("GET STAGE DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.stage_list != null && response.stage_list!!.isNotEmpty()) {

                                    AppDatabase.getDBInstance()?.stageDao()?.deleteAll()

                                    doAsync {

                                        response.stage_list?.forEach {
                                            val stageEntity = StageEntity()
                                            AppDatabase.getDBInstance()?.stageDao()?.insertAll(stageEntity.apply {
                                                stage_id = it.id
                                                stage_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkFunnelStageList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkFunnelStageList()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                checkFunnelStageList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET STAGE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            checkFunnelStageList()
                        })
        )
    }

    private fun checkFunnelStageList() {
        /*val list = AppDatabase.getDBInstance()?.funnelStageDao()?.getAll()
        if (list == null || list.isEmpty())*/
        geFunnelStageApi()
        /*else
            checkBSList()*/
    }

    private fun geFunnelStageApi() {
        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getFunnelStageList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as FunnelStageListResponseModel
                            XLog.d("GET FUNNEL STAGE DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.funnel_stage_list != null && response.funnel_stage_list!!.isNotEmpty()) {

                                    AppDatabase.getDBInstance()?.funnelStageDao()?.deleteAll()

                                    doAsync {

                                        response.funnel_stage_list?.forEach {
                                            val funnelStageEntity = FunnelStageEntity()
                                            AppDatabase.getDBInstance()?.funnelStageDao()?.insertAll(funnelStageEntity.apply {
                                                funnel_stage_id = it.id
                                                funnel_stage_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkBSList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkBSList()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                checkBSList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET FUNNEL STAGE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            checkBSList()
                        })
        )
    }

    private fun checkBSList() {
        val list = AppDatabase.getDBInstance()?.bsListDao()?.getAll()
        if (list == null || list.isEmpty())
            geBSApi()
        else
            checkQuotSList()
    }

    private fun geBSApi() {
        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getBSList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BSListResponseModel
                            XLog.d("GET BS DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.bs_list != null && response.bs_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.bs_list?.forEach {
                                            val bsEntity = BSListEntity()
                                            AppDatabase.getDBInstance()?.bsListDao()?.insertAll(bsEntity.apply {
                                                bs_id = it.id
                                                bs_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkQuotSList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkQuotSList()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                checkQuotSList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET BS DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            checkQuotSList()
                        })
        )
    }

    private fun checkQuotSList() {
        val list = AppDatabase.getDBInstance()?.quotDao()?.getAll()
        if (list == null || list.isEmpty())
            geQuotApi()
        else
            checkToCallTypeApi()
    }

    private fun geQuotApi() {
        if(Pref.isQuotationShow) {
            XLog.d("API_Optimization geQuotApi Login : enable " + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            progress_wheel.spin()
            val repository = QuotationRepoProvider.provideBSListRepository()
            BaseActivity.compositeDisposable.add(
                    repository.getQuotList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as QuotationListResponseModel
                                XLog.d("GET QUOT DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                                if (response.status == NetworkConstant.SUCCESS) {

                                    if (response.quot_list != null && response.quot_list!!.isNotEmpty()) {

                                        doAsync {

                                            response.quot_list?.forEach {
                                                val quotEntity = QuotationEntity()
                                                AppDatabase.getDBInstance()?.quotDao()?.insert(quotEntity.apply {
                                                    quo_id = it.quo_id
                                                    quo_no = it.quo_no

                                                    date = it.date

                                                    hypothecation = if (!TextUtils.isEmpty(it.hypothecation))
                                                        it.hypothecation
                                                    else
                                                        ""
                                                    account_no = if (!TextUtils.isEmpty(it.account_no))
                                                        it.account_no
                                                    else
                                                        ""

                                                    model_id = it.model_id
                                                    bs_id = it.bs_id

                                                    gearbox = if (!TextUtils.isEmpty(it.gearbox))
                                                        it.gearbox
                                                    else
                                                        ""

                                                    number1 = if (!TextUtils.isEmpty(it.number1))
                                                        it.number1
                                                    else
                                                        ""

                                                    value1 = if (!TextUtils.isEmpty(it.value1))
                                                        it.value1
                                                    else
                                                        ""

                                                    value2 = if (!TextUtils.isEmpty(it.value2))
                                                        it.value2
                                                    else
                                                        ""

                                                    tyres1 = if (!TextUtils.isEmpty(it.tyres1))
                                                        it.tyres1
                                                    else
                                                        ""

                                                    number2 = if (!TextUtils.isEmpty(it.number2))
                                                        it.number2
                                                    else
                                                        ""

                                                    value3 = if (!TextUtils.isEmpty(it.value3))
                                                        it.value3
                                                    else
                                                        ""

                                                    value4 = if (!TextUtils.isEmpty(it.value4))
                                                        it.value4
                                                    else
                                                        ""

                                                    tyres2 = if (!TextUtils.isEmpty(it.tyres2))
                                                        it.tyres2
                                                    else
                                                        ""

                                                    amount = if (!TextUtils.isEmpty(it.amount))
                                                        it.amount
                                                    else
                                                        ""

                                                    discount = if (!TextUtils.isEmpty(it.discount))
                                                        it.discount
                                                    else
                                                        ""

                                                    cgst = if (!TextUtils.isEmpty(it.cgst))
                                                        it.cgst
                                                    else
                                                        ""

                                                    sgst = if (!TextUtils.isEmpty(it.sgst))
                                                        it.sgst
                                                    else
                                                        ""

                                                    tcs = if (!TextUtils.isEmpty(it.tcs))
                                                        it.tcs
                                                    else
                                                        ""

                                                    insurance = if (!TextUtils.isEmpty(it.insurance))
                                                        it.insurance
                                                    else
                                                        ""

                                                    net_amount = if (!TextUtils.isEmpty(it.net_amount))
                                                        it.net_amount
                                                    else
                                                        ""

                                                    remarks = if (!TextUtils.isEmpty(it.remarks))
                                                        it.remarks
                                                    else
                                                        ""

                                                    shop_id = it.shop_id
                                                    isUploaded = true
                                                })
                                            }

                                            uiThread {
                                                progress_wheel.stopSpinning()
                                                checkToCallTypeApi()
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        checkToCallTypeApi()
                                    }


                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallTypeApi()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("GET QUOT DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                checkToCallTypeApi()
                            })
            )
        }
        else{
            XLog.d("API_Optimization geQuotApi Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallTypeApi()
        }
    }

    private fun checkToCallTypeApi() {
        AppDatabase.getDBInstance()?.typeListDao()?.delete()
        val typeList = AppDatabase.getDBInstance()?.typeListDao()?.getAll()
        XLog.d("login activity typeList_size " + typeList)
        if (typeList == null || typeList.isEmpty())
            getTypeListApi()
        else
            getTeamAreaListApi()
    }

    private fun getTypeListApi() {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.typeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TypeListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.type_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        list.forEach {
                                            val type = TypeListEntity()
                                            AppDatabase.getDBInstance()?.typeListDao()?.insert(type.apply {
                                                type_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getTeamAreaListApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getTeamAreaListApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getTeamAreaListApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            getTeamAreaListApi()
                        })
        )
    }

    private fun getTeamAreaListApi() {
        if(Pref.isOfflineTeam){
            XLog.d("API_call_optimization Login->isOfflineTeam Enable: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )

        val repository = TeamRepoProvider.teamRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.teamAreaList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamAreaListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.area_list

                                if (list != null && list.isNotEmpty()) {

                                    AppDatabase.getDBInstance()?.memberAreaDao()?.deleteAll()

                                    doAsync {

                                        list.forEach {
                                            val area = TeamAreaEntity()
                                            AppDatabase.getDBInstance()?.memberAreaDao()?.insertAll(area.apply {
                                                area_id = it.area_id
                                                area_name = it.area_name
                                                user_id = it.user_id
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getTimesheetDropdownApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getTimesheetDropdownApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getTimesheetDropdownApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            getTimesheetDropdownApi()
                        })
        )
        }else{
            XLog.d("API_call_optimization Login->isOfflineTeam Disable: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            getTimesheetDropdownApi()
        }
    }

    private fun getTimesheetDropdownApi() {
        if(Pref.willTimesheetShow) {
            XLog.d("API_Optimization->  TIMESHEET Enable: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            progress_wheel.spin()
            val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
            BaseActivity.compositeDisposable.add(
                    repository.getTimeSheetDropdown()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as TimeSheetDropDownResponseModel
                                XLog.d("TIMESHEET DROPDOWN: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                                if (response.status == NetworkConstant.SUCCESS) {

                                    AppDatabase.getDBInstance()?.clientDao()?.deleteAll()
                                    AppDatabase.getDBInstance()?.projectDao()?.deleteAll()
                                    AppDatabase.getDBInstance()?.activityDao()?.deleteAll()
                                    AppDatabase.getDBInstance()?.productDao()?.deleteAll()


                                    doAsync {

                                        response.client_list?.forEach {
                                            val client = ClientListEntity()
                                            AppDatabase.getDBInstance()?.clientDao()?.insertAll(client.apply {
                                                client_id = it.client_id
                                                client_name = it.client_name
                                            })
                                        }

                                        response.project_list?.forEach {
                                            val project = ProjectListEntity()
                                            AppDatabase.getDBInstance()?.projectDao()?.insertAll(project.apply {
                                                project_id = it.project_id
                                                project_name = it.project_name
                                            })
                                        }

                                        response.product_list?.forEach {
                                            val product = TimesheetProductListEntity()
                                            AppDatabase.getDBInstance()?.productDao()?.insertAll(product.apply {
                                                product_id = it.product_id
                                                product_name = it.product_name
                                            })
                                        }

                                        response.activity_list?.forEach {
                                            val activity = ActivityListEntity()
                                            AppDatabase.getDBInstance()?.activityDao()?.insertAll(activity.apply {
                                                activity_id = it.activity_id
                                                activity_name = it.activity_name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getTimesheetConfig()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getTimesheetConfig()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                XLog.d("TIMESHEET DROPDOWN: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                getTimesheetConfig()
                            })
            )
        }else{
            XLog.d("API_Optimization-> TIMESHEET Disable: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallActivityTypeApi()
        }
    }

    private fun getTimesheetConfig() {
        progress_wheel.spin()
        val repository = TimeSheetRepoProvider.timeSheetRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.timeSheetConfig(true)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TimeSheetConfigResponseModel
                            XLog.d("TIMESHEET CONFIG: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {
                                response.apply {
                                    Pref.timesheet_past_days = timesheet_past_days
                                    Pref.supervisor_name = supervisor_name
                                    Pref.client_text = client_text
                                    Pref.product_text = product_text
                                    Pref.activity_text = activity_text
                                    Pref.project_text = project_text
                                    Pref.time_text = time_text
                                    Pref.comment_text = comment_text
                                    Pref.submit_text = submit_text
                                }

                                checkToCallActivityTypeApi()
                            } else
                                checkToCallActivityTypeApi()


                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("TIMESHEET CONFIG: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            checkToCallActivityTypeApi()
                        })
        )
    }

    private fun checkToCallActivityTypeApi() {
        val typeList = AppDatabase.getDBInstance()?.typeDao()?.getAll()
        if (typeList == null || typeList.isEmpty())
            getTypeApi()
        else
            checkToCallPriorityApi()
    }

    private fun getTypeApi() {
        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.typeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as com.kcteam.features.activities.model.TypeListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.type_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        list.forEach {
                                            val type = TypeEntity()
                                            AppDatabase.getDBInstance()?.typeDao()?.insert(type.apply {
                                                type_id = it.id
                                                name = it.name
                                                activity_id = it.activityId
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallPriorityApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallPriorityApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallPriorityApi()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            checkToCallPriorityApi()
                        })
        )
    }

    private fun checkToCallPriorityApi() {
        val list = AppDatabase.getDBInstance()?.priorityDao()?.getAll()
        if (list == null || list.isEmpty())
            getPriorityList()
        else
            checkToCallActivityDropdownApi()
    }

    private fun getPriorityList() {
        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.priorityList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as PriorityListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.priority_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        list.forEach {
                                            val priority = PriorityListEntity()
                                            AppDatabase.getDBInstance()?.priorityDao()?.insertAll(priority.apply {
                                                priority_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallActivityDropdownApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallActivityDropdownApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallActivityDropdownApi()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            checkToCallActivityDropdownApi()
                        })
        )
    }

    private fun checkToCallActivityDropdownApi() {
        val list = AppDatabase.getDBInstance()?.activityDropdownDao()?.getAll()
        if (list == null || list.isEmpty())
            getActivityDropdownList()
        else
            checkToCallActivityApi()
    }

    private fun getActivityDropdownList() {
        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.activityDropdownList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ActivityDropdownListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.activity_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        list.forEach {
                                            val activity = ActivityDropDownEntity()
                                            AppDatabase.getDBInstance()?.activityDropdownDao()?.insertAll(activity.apply {
                                                activity_id = it.id
                                                activity_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallActivityApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallActivityApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallActivityApi()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            checkToCallActivityApi()
                        })
        )
    }

    private fun checkToCallActivityApi() {
        val list = AppDatabase.getDBInstance()?.activDao()?.getAll()
        if (list == null || list.isEmpty())
            getActivityList()
        else
            checkToCallChemistVisitApi()
    }

    private fun getActivityList() {
        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.activityList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ActivityListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.activity_list != null && response.activity_list!!.isNotEmpty()) {

                                    doAsync {
                                        response.activity_list!!.forEach {
                                            val activity = ActivityEntity()
                                            AppDatabase.getDBInstance()?.activDao()?.insertAll(activity.apply {
                                                activity_id = it.id
                                                party_id = it.party_id
                                                date = it.date
                                                time = it.time
                                                name = it.name
                                                activity_dropdown_id = it.activity_id
                                                type_id = it.type_id
                                                product_id = it.product_id
                                                subject = it.subject
                                                details = it.details
                                                duration = it.duration
                                                priority_id = it.priority_id
                                                due_date = it.due_date
                                                due_time = it.due_time
                                                attachments = it.attachments
                                                image = it.image
                                                isUploaded = true
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallChemistVisitApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallChemistVisitApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallChemistVisitApi()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            checkToCallChemistVisitApi()
                        })
        )
    }

    private fun checkToCallChemistVisitApi() {
        var isChemistTypeAvailable:Boolean = false
        var isDoctorTypeAvaliable:Boolean = false
        doAsync {
            val list = AppDatabase.getDBInstance()!!.shopTypeDao().getAll()
            for (i in list.indices) {
                if(list[i].shoptype_name!!.contains("Chemist",ignoreCase = true)){
                    isChemistTypeAvailable = true
                    break
                }
                else if(list[i].shoptype_name!!.contains("Doctor",ignoreCase = true)){
                    isDoctorTypeAvaliable = true
                    break
                }
            }
            uiThread {
                if(isChemistTypeAvailable){
                    val list = AppDatabase.getDBInstance()!!.addChemistDao().getAll()
                    if (list != null && list.isNotEmpty()){
                        if(isDoctorTypeAvaliable)
                            checkToCallDoctorVisitApi()
                        else
                            checkToCallStockListApi()
                    } else
                        getChemistVisitListApi()
                }
                else if(isDoctorTypeAvaliable){
                    checkToCallDoctorVisitApi()
                }
                else{
                    checkToCallStockListApi()
                }

            }
        }
    }

    private fun getChemistVisitListApi() {
        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getChemistVisit()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ChemistVisitResponseModel
                            isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {
                                val chemistVisitList = response.chemist_visit_list

                                if (chemistVisitList != null && chemistVisitList.isNotEmpty()) {

                                    doAsync {

                                        for (i in chemistVisitList.indices) {
                                            val chemistEntity = AddChemistEntity()
                                            chemistEntity.shop_id = chemistVisitList[i].shop_id
                                            chemistEntity.pob = chemistVisitList[i].isPob

                                            chemistEntity.remarks = chemistVisitList[i].remarks
                                            chemistEntity.remarks_mr = chemistVisitList[i].remarks_mr
                                            chemistEntity.visit_date = chemistVisitList[i].next_visit_date
                                            chemistEntity.volume = chemistVisitList[i].volume
                                            chemistEntity.chemist_visit_id = chemistVisitList[i].chemist_visit_id
                                            chemistEntity.isUploaded = true

                                            for (j in chemistVisitList[i].product_list.indices) {
                                                val pobChemEntity = AddChemistProductListEntity()
                                                pobChemEntity.chemist_visit_id = chemistEntity.chemist_visit_id
                                                pobChemEntity.shop_id = chemistEntity.shop_id
                                                pobChemEntity.isPob = false
                                                pobChemEntity.product_id = chemistVisitList[i].product_list[j].product_id
                                                pobChemEntity.product_name = chemistVisitList[i].product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addChemistProductDao().insertAll(pobChemEntity)
                                            }

                                            for (j in chemistVisitList[i].pob_product_list.indices) {
                                                val pobChemEntity = AddChemistProductListEntity()
                                                pobChemEntity.chemist_visit_id = chemistEntity.chemist_visit_id
                                                pobChemEntity.shop_id = chemistEntity.shop_id
                                                pobChemEntity.isPob = true
                                                pobChemEntity.product_id = chemistVisitList[i].pob_product_list[j].product_id
                                                pobChemEntity.product_name = chemistVisitList[i].pob_product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addChemistProductDao().insertAll(pobChemEntity)
                                            }


                                            AppDatabase.getDBInstance()!!.addChemistDao().insertAll(chemistEntity)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallDoctorVisitApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallDoctorVisitApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallDoctorVisitApi()
                            }

                        }, { error ->
                            error.printStackTrace()
                            isApiInitiated = false
                            progress_wheel.stopSpinning()
                            checkToCallDoctorVisitApi()
                        })
        )
    }

    private fun checkToCallDoctorVisitApi() {
        val list = AppDatabase.getDBInstance()!!.addDocDao().getAll()
        if (list != null && list.isNotEmpty())
            checkToCallStockListApi()
        else
            getDoctorVisitListApi()
    }

    private fun getDoctorVisitListApi() {
        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getDoctortVisit()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as DoctorListResponseModel
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {
                                val docVisitList = response.doc_visit_list

                                if (docVisitList != null && docVisitList.isNotEmpty()) {

                                    doAsync {

                                        for (i in docVisitList.indices) {

                                            val docEntity = AddDoctorEntity()
                                            docEntity.shop_id = docVisitList[i].shop_id
                                            docEntity.remarks_mr = docVisitList[i].remarks_mr
                                            docEntity.visit_date = docVisitList[i].next_visit_date
                                            docEntity.doc_visit_id = docVisitList[i].doc_visit_id
                                            docEntity.crm_from_date = docVisitList[i].from_cme_date
                                            docEntity.crm_to_date = docVisitList[i].to_crm_date
                                            docEntity.volume = docVisitList[i].crm_volume
                                            docEntity.which_kind = docVisitList[i].which_kind
                                            docEntity.doc_remark = docVisitList[i].doc_remarks
                                            docEntity.qty_text = docVisitList[i].qty_vol_text
                                            docEntity.prescribe_status = docVisitList[i].is_prescriber
                                            docEntity.qty_status = docVisitList[i].is_qty
                                            docEntity.sample_status = docVisitList[i].is_sample
                                            docEntity.crm_status = docVisitList[i].is_crm
                                            docEntity.money_status = docVisitList[i].is_money
                                            docEntity.gift_status = docVisitList[i].is_gift
                                            docEntity.amount = docVisitList[i].amount
                                            docEntity.what = docVisitList[i].what
                                            docEntity.isUploaded = true

                                            for (j in docVisitList[i].product_list.indices) {
                                                val pobChemEntity = AddDoctorProductListEntity()
                                                pobChemEntity.doc_visit_id = docEntity.doc_visit_id
                                                pobChemEntity.shop_id = docEntity.shop_id
                                                pobChemEntity.product_status = 0
                                                pobChemEntity.product_id = docVisitList[i].product_list[j].product_id
                                                pobChemEntity.product_name = docVisitList[i].product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addDocProductDao().insertAll(pobChemEntity)
                                            }

                                            for (j in docVisitList[i].qty_product_list.indices) {
                                                val pobChemEntity = AddDoctorProductListEntity()
                                                pobChemEntity.doc_visit_id = docEntity.doc_visit_id
                                                pobChemEntity.shop_id = docEntity.shop_id
                                                pobChemEntity.product_status = 1
                                                pobChemEntity.product_id = docVisitList[i].qty_product_list[j].product_id
                                                pobChemEntity.product_name = docVisitList[i].qty_product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addDocProductDao().insertAll(pobChemEntity)
                                            }

                                            for (j in docVisitList[i].sample_product_list.indices) {
                                                val pobChemEntity = AddDoctorProductListEntity()
                                                pobChemEntity.doc_visit_id = docEntity.doc_visit_id
                                                pobChemEntity.shop_id = docEntity.shop_id
                                                pobChemEntity.product_status = 2
                                                pobChemEntity.product_id = docVisitList[i].sample_product_list[j].product_id
                                                pobChemEntity.product_name = docVisitList[i].sample_product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addDocProductDao().insertAll(pobChemEntity)
                                            }

                                            AppDatabase.getDBInstance()!!.addDocDao().insertAll(docEntity)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallStockListApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallStockListApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallStockListApi()
                            }

                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            checkToCallStockListApi()
                        })
        )
    }

    private fun checkToCallStockListApi() {
        val list = AppDatabase.getDBInstance()!!.stockDetailsListDao().getAll()
        if (list != null && list.isNotEmpty()) {
            checkToCallDocumentTypeList()
        } else
            getStockList()
    }


    private fun getStockList() {
        if (Pref.willStockShow) {
            XLog.d("API_Optimization GET getStockList Login : enable " + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
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
                                                stockDetailsList.qty = stock_details_list[i].stock_qty

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
                                                checkToCallDocumentTypeList()
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        checkToCallDocumentTypeList()
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallDocumentTypeList()
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                checkToCallDocumentTypeList()
                            })
            )
        }
        else{
            XLog.d("API_Optimization GET getStockList  Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallDocumentTypeList()
        }
    }

    private fun checkToCallDocumentTypeList() {
        val list = AppDatabase.getDBInstance()?.documentTypeDao()?.getAll()
        if (list != null && list.isNotEmpty())
            checkToCallDocumentListApi()
        else
            getDocumentTypeApi()
    }

    private fun getDocumentTypeApi() {
        if (Pref.isDocumentRepoShow) {
            XLog.d("API_Optimization GET getDocumentListApi Login : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            val repository = DocumentRepoProvider.documentRepoProvider()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.getDocType()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as DocumentTypeResponseModel
                                XLog.d("DOCUMENT TYPE LIST RESPONSE=======> " + response.status)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    if (response.type_list != null && response.type_list!!.size > 0) {

                                        doAsync {
                                            response.type_list?.forEach {
                                                val docTypeEntity = DocumentypeEntity()
                                                AppDatabase.getDBInstance()?.documentTypeDao()?.insert(docTypeEntity.apply {
                                                    type_id = it.id
                                                    type_name = it.type
                                                    image = it.image
                                                    IsForOrganization = it.IsForOrganization
                                                    IsForOwn = it.IsForOwn
                                                })
                                            }

                                            uiThread {
                                                progress_wheel.stopSpinning()
                                                checkToCallDocumentListApi()
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        checkToCallDocumentListApi()
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallDocumentListApi()
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                XLog.d("DOCUMENT TYPE LIST ERROR=======> " + error.localizedMessage)
                                checkToCallDocumentListApi()
                            })
            )
        }
        else{
            XLog.d("API_Optimization GET getDocumentListApi  Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallPaymentApi()
        }
    }

    private fun checkToCallDocumentListApi() {
        val list = AppDatabase.getDBInstance()?.documentListDao()?.getAll()
        if (list != null && list.isNotEmpty())
            checkToCallPaymentApi()
        else
            getDocumentListApi()
    }

    private fun getDocumentListApi() {
        if (Pref.isDocumentRepoShow) {
            XLog.d("API_Optimization GET getDocumentListApi  Login : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            val repository = DocumentRepoProvider.documentRepoProvider()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.getDocList("")
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as DocumentListResponseModel
                                XLog.d("DOCUMENT LIST RESPONSE=======> " + response.status)

                                if (response.status == NetworkConstant.SUCCESS) {
                                    if (response.doc_list != null && response.doc_list!!.size > 0) {
                                        doAsync {
                                            response.doc_list?.forEach {
                                                val docListEntity = DocumentListEntity()
                                                AppDatabase.getDBInstance()?.documentListDao()?.insert(docListEntity.apply {
                                                    list_id = it.id
                                                    type_id = it.type_id
                                                    date_time = it.date_time
                                                    attachment = it.attachment
                                                    isUploaded = true
                                                    document_name = it.document_name
                                                })
                                            }

                                            uiThread {
                                                progress_wheel.stopSpinning()
                                                checkToCallPaymentApi()
                                            }
                                        }


                                    } else {
                                        progress_wheel.stopSpinning()
                                        checkToCallPaymentApi()
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallPaymentApi()
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                checkToCallPaymentApi()
                                XLog.d("DOCUMENT LIST ERROR=======> " + error.localizedMessage)
                            })
            )
        }
        else{
            XLog.d("API_Optimization GET getDocumentListApi  Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallPaymentApi()
        }
    }

    private fun checkToCallPaymentApi() {
        val list = AppDatabase.getDBInstance()?.paymenttDao()?.getAll()
        if (list != null && list.isNotEmpty())
            checkEntityList()
        else
            getPaymentApi()
    }

    private fun getPaymentApi() {
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
                                            checkEntityList()
                                        }
                                    }


                                } else {
                                    progress_wheel.stopSpinning()
                                    checkEntityList()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkEntityList()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            checkEntityList()
                            XLog.d("PAYMENT ERROR=======> " + error.localizedMessage)
                        })
        )
    }

    private fun checkEntityList() {
        val list = AppDatabase.getDBInstance()?.entityDao()?.getAll() as ArrayList<EntityTypeEntity>
        if (list != null && list.isNotEmpty())
            checkPartyStatusList()
        else
            getEntityTypeListApi()
    }

    private fun getEntityTypeListApi() {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.entityList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as EntityResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.entity_type

                                if (list != null && list.isNotEmpty()) {
                                    doAsync {
                                        list.forEach {
                                            val entity = EntityTypeEntity()
                                            AppDatabase.getDBInstance()?.entityDao()?.insert(entity.apply {
                                                entity_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkPartyStatusList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkPartyStatusList()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkPartyStatusList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            checkPartyStatusList()
                        })
        )
    }

    private fun checkPartyStatusList() {
        val list = AppDatabase.getDBInstance()?.partyStatusDao()?.getAll() as ArrayList<PartyStatusEntity>
        if (list != null && list.isNotEmpty())
            checkRetailerList()
        else
            getPartyStatusListApi()
    }

    private fun getPartyStatusListApi() {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.partyStatusList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as PartyStatusResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.party_status
                                if (list != null && list.isNotEmpty()) {
                                    doAsync {

                                        list.forEach {
                                            val party = PartyStatusEntity()
                                            AppDatabase.getDBInstance()?.partyStatusDao()?.insert(party.apply {
                                                party_status_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkRetailerList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkRetailerList()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkRetailerList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            checkRetailerList()
                        })
        )
    }

    private fun checkRetailerList() {
        val list = AppDatabase.getDBInstance()?.retailerDao()?.getAll() as ArrayList<RetailerEntity>
        if (list != null && list.isNotEmpty())
            checkDealerList()
        else
            getRetailerListApi()
    }

    private fun getRetailerListApi() {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.retailerList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as RetailerListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.retailer_list
                                if (list != null && list.isNotEmpty()) {
                                    doAsync {

                                        list.forEach {
                                            val retailer = RetailerEntity()
                                            AppDatabase.getDBInstance()?.retailerDao()?.insert(retailer.apply {
                                                retailer_id = it.id
                                                name = it.name
                                                type_id = it.type_id
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkDealerList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkDealerList()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkDealerList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            checkDealerList()
                        })
        )
    }

    private fun checkDealerList() {
        val list = AppDatabase.getDBInstance()?.dealerDao()?.getAll() as ArrayList<DealerEntity>
        if (list != null && list.isNotEmpty())
            checkBeatList()
        else
            getDealerListApi()
    }

    private fun getDealerListApi() {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.dealerList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as DealerListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.dealer_list
                                if (list != null && list.isNotEmpty()) {
                                    doAsync {

                                        list.forEach {
                                            val dealer = DealerEntity()
                                            AppDatabase.getDBInstance()?.dealerDao()?.insert(dealer.apply {
                                                dealer_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkBeatList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkBeatList()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkBeatList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            checkBeatList()
                        })
        )
    }

    private fun checkBeatList() {
        val list = AppDatabase.getDBInstance()?.beatDao()?.getAll() as ArrayList<BeatEntity>
        if (list != null && list.isNotEmpty())
            getAssignedToShopApi()
        else
            getBeatListApi()
    }

    private fun getBeatListApi() {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.beatList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BeatListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.beat_list
                                if (list != null && list.isNotEmpty()) {
                                    doAsync {

                                        list.forEach {
                                            val beat = BeatEntity()
                                            AppDatabase.getDBInstance()?.beatDao()?.insert(beat.apply {
                                                beat_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getAssignedToShopApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedToShopApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getAssignedToShopApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            getAssignedToShopApi()
                        })
        )
    }

    private fun getAssignedToShopApi() {
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
                                        checkToCallDeviceInfoApi()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallDeviceInfoApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            checkToCallDeviceInfoApi()
                        })
        )
    }

    private fun checkToCallDeviceInfoApi() {
        val list = AppDatabase.getDBInstance()?.batteryNetDao()?.getAll()
        if (list != null && list.isNotEmpty())
            getRemarksList()
        else
            callDeviceInfoListApi()
    }


    private fun callDeviceInfoListApi() {
        progress_wheel.spin()
        val repository = LocationRepoProvider.provideLocationRepository()
        BaseActivity.compositeDisposable.add(
                repository.getAppInfo()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AppInfoResponseModel
                            XLog.e("Get App Info : RESPONSE : " + response.status + ":" + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {
                                doAsync {
                                    response.app_info_list?.forEach {
                                        val deviceInfo = BatteryNetStatusEntity()
                                        AppDatabase.getDBInstance()?.batteryNetDao()?.insert(deviceInfo.apply {
                                            bat_net_id = it.id
                                            date_time = it.date_time
                                            date = AppUtils.changeAttendanceDateFormatToCurrent(it.date_time)
                                            android_version = it.android_version
                                            device_model = it.device_model
                                            bat_level = it.battery_percentage
                                            bat_status = it.battery_status
                                            net_type = it.network_type
                                            mob_net_type = it.mobile_network_type
                                            isUploaded = true
                                        })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        getRemarksList()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getRemarksList()
                            }

                        }, { error ->
                            error.printStackTrace()
                            XLog.e("Get App Info : ERROR : " + error.localizedMessage)
                            progress_wheel.stopSpinning()
                            getRemarksList()
                        })
        )
    }

    private fun getRemarksList() {
        progress_wheel.spin()
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()
        BaseActivity.compositeDisposable.add(
                repository.getRemarksList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as VisitRemarksResponseModel
                            XLog.d("Visit Remarks List : RESPONSE " + response.status)
                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.visitRemarksDao()?.delete()

                                doAsync {
                                    response.remarks_list?.forEach {
                                        val visitRemarks = VisitRemarksEntity()
                                        AppDatabase.getDBInstance()?.visitRemarksDao()?.insertAll(visitRemarks.apply {
                                            remarks_id = it.id
                                            name = it.name
                                        })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        //sam
                                        getCurrentStockApi()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                //sam
                                getCurrentStockApi()
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            XLog.d("Visit Remarks List : ERROR " + error.localizedMessage)
                            //sam
                            getCurrentStockApi()
                        })
        )
    }


    private fun checkToCallTaskListApi() {
        val list = AppDatabase.getDBInstance()?.taskDao()?.getAll()
        if (list != null && list.isNotEmpty())
            gotoHomeActivity()
        else
            callTaskListApi()
    }


    private fun callTaskListApi() {
        progress_wheel.spin()
        val repository = TaskRepoProvider.taskRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.taskList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TaskListResponseModel
                            XLog.d("TASK LIST: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.task_list != null && response.task_list!!.isNotEmpty()) {
                                    doAsync {
                                        response.task_list?.forEach {
                                            val task = TaskEntity()
                                            AppDatabase.getDBInstance()?.taskDao()?.insertAll(task.apply {
                                                task_id = it.id
                                                date = it.date
                                                task_name = it.task
                                                details = it.details
                                                isUploaded = true
                                                isCompleted = it.isCompleted
                                                isStatusUpdated = -1
                                                eventId = it.eventID
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            //sam
                                            getCurrentStockApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    //sam
                                    getCurrentStockApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                //sam
                                getCurrentStockApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("TASK LIST: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            //sam
                            getCurrentStockApi()
                        })
        )
    }

    private fun initPermissionCheck() {

        permissionUtils = PermissionUtils(this, object : PermissionUtils.OnPermissionListener {
            @TargetApi(Build.VERSION_CODES.M)
            override fun onPermissionGranted() {
                getIMEI()

                /*if(Build.VERSION.SDK_INT>=30){
                    if (!Environment.isExternalStorageManager()){
                        fileManagePermii()
                    }else{
                        Handler().postDelayed(Runnable {
                            if (!Settings.canDrawOverlays(this@LoginActivity)) {
                                getOverlayPermission()
                            }
                        }, 1000)
                    }
                }else{*/
                Handler().postDelayed(Runnable {
                    if (!Settings.canDrawOverlays(this@LoginActivity)) {
                        getOverlayPermission()
                    }
                }, 1000)
                //}


            }

            override fun onPermissionNotGranted() {
                //AppUtils.showButtonSnackBar(this@SplashActivity, rl_splash_main, getString(R.string.error_loc_permission_request_msg))
                showSnackMessage(getString(R.string.accept_permission))
                /*Handler().postDelayed(Runnable {
                    finish()
                    System.exit(0)
                },3000)*/
            }

        }, arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECORD_AUDIO))
    }

    fun fileManagePermii() {
        /*try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
            startActivityForResult(intent, 777)
        } catch (e: java.lang.Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            startActivityForResult(intent, 777)
        }*/


/*        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        val uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        startActivityForResult(intent,777)*/
    }

    private fun getLastLocation() {
        try {
            val criteria = Criteria()
            val provider = locationManager!!.getBestProvider(criteria, false)
            val location = locationManager!!.getLastKnownLocation(provider!!)
            Pref.latitude = location?.latitude.toString()
            Pref.longitude = location?.longitude.toString()
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

    }

    private fun getLocation() {
        try {
            if (canGetLocation) {
                if (isGPS) {
                    // from GPS
                    locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this)

                    if (locationManager != null) {
                        loc = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (loc != null) {
                            Pref.latitude = loc?.latitude.toString()
                            Pref.longitude = loc?.longitude.toString()
                        }
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this)

                    if (locationManager != null) {
                        loc = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (loc != null) {
                            Pref.latitude = loc?.latitude.toString()
                            Pref.longitude = loc?.longitude.toString()
                        }

                    }
                }

            } else {
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

    }

    override fun onResume() {

        /*try {
            var signal = CancellationSignal()
            if (signal.isCanceled) {
                signal = CancellationSignal()
                signal.cancel()
            }
            checkForFingerPrint()
            super.onResume()
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        super.onResume()
        /*if (PermissionHelper.checkStoragePermission(this)) {
            if (Pref.imei.isEmpty())
                getIMEI()
        }*/
        takeActionOnGeofence()
        checkForFingerPrint()
    }

    private fun checkForFingerPrint() {
        try {

            if (checkFingerPrint != null)
                checkFingerPrint = null

            checkFingerPrint = CheckFingerPrint()
            checkFingerPrint?.checkFingerPrint(this, object : CheckFingerPrint.FingerPrintListener {
                override fun isFingerPrintSupported(status: Boolean) {
                    if (status) {
                        Log.e("LoginActivity", "========Device support fingerprint===========")
                    } else {
                        Log.e("LoginActivity", "==========Device does not support fingerprint===========")
                        isFingerPrintSupported = false
                    }
                }

                override fun onSuccess(signal: CancellationSignal?) {
                    Log.e("LoginActivity", "============Fingerprint accepted=============")

                    if (fingerprintDialog != null && fingerprintDialog?.isVisible!!) {
                        fingerprintDialog?.dismiss()

                        if (Pref.isSelfieMandatoryForAttendance)
                            showSelfieDialog()
                        else
                            prapareLogin(this@LoginActivity)
                    }
                }

                override fun onError(msg: String) {
                    Log.e("LoginActivity", "Fingerprint error=====> " + msg)

                    when {
                        msg.equals("Fingerprint operation cancelled.", ignoreCase = true) -> {
                        }
                        msg.equals("Fingerprint operation cancelled", ignoreCase = true) -> {
                        }
                        msg.equals("Fingerprint operation canceled", ignoreCase = true) -> {
                        }
                        msg.equals("Fingerprint operation canceled.", ignoreCase = true) -> {
                        }
                        else -> Toaster.msgLong(this@LoginActivity, msg)
                    }
                }

            })

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CheckFingerPrint().FingerprintHandler().doAuth()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getIMEI() {
        //if (PermissionHelper.checkPhoneStatePermisssion(this)) {
        /*Save device default IMEI*/
        try {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (Build.VERSION.SDK_INT >= 29) {
                /*if (telephonyManager.phoneCount == 2)
                    Pref.imei = telephonyManager.getImei(0)
                else
                    Pref.imei = telephonyManager.imei*/

                Pref.imei = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);

            } else {
                Pref.imei = telephonyManager.deviceId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //}
    }

    private fun initView() {
        val login_TV= findViewById<ImageView>(R.id.login_TV)
        login_TV.isEnabled = true
        username_EDT = findViewById(R.id.username_EDT)
        password_EDT = findViewById(R.id.password_EDT)
        forgotPassword = findViewById(R.id.forgot_password_TV)
        progress_wheel = findViewById(R.id.progress_wheel)
        version_name_TV = findViewById(R.id.version_name_TV)
        share_log_login_TV = findViewById(R.id.share_log_login_TV)
        version_name_TV.text = AppUtils.getVersionName(this@LoginActivity)
        progress_wheel.stopSpinning()
        iv_shopImage = findViewById(R.id.iv_shopImage)
        tv_internet_info = findViewById(R.id.tv_internet_info)
        cb_remember_me = findViewById(R.id.cb_remember_me)

        icon_internet = findViewById(R.id.icon_internet)
        tvappCustomAnydeskInfo = findViewById(R.id.activity_login_tvappCustomAnydeskInfo)
//        tvappCustomAnydesk = findViewById(R.id.activity_login_tvappCustomAnydesk)
//        tvappCustomSharelog = findViewById(R.id.activity_login_tvappCustomLogs)
//        tvappCustomAnydesk.setOnClickListener(this)
//        tvappCustomSharelog.setOnClickListener(this)
        tvappCustomAnydeskInfo.setOnClickListener(this)
        cb_remember_me.isChecked = Pref.isRememberMe


        tvappCustomAnydeskInfo.isSelected = false

        var launchIntent: Intent? = packageManager.getLaunchIntentForPackage("com.anydesk.anydeskandroid")
        if (launchIntent != null) {
//            activity_login_tvappCustomAnydesk.text = resources.getString(R.string.label_open_anydesk)
        } else {
//            activity_login_tvappCustomAnydesk.text = resources.getString(R.string.label_install_anydesk)
        }



        if (Pref.isRememberMe) {
            username_EDT.setText(Pref.PhnNo)
            password_EDT.setText(Pref.pwd)
        }

        alert_snack_bar = findViewById<CoordinatorLayout>(R.id.alert_snack_bar)
        login_TV.setOnClickListener(this)
        forgotPassword.setOnClickListener(this)
        share_log_login_TV.setOnClickListener(this)
        cb_remember_me.setOnCheckedChangeListener { buttonView, isChecked ->


            cb_remember_me.isChecked = isChecked
            Pref.isRememberMe = isChecked

            if (!Pref.isRememberMe) {
                Pref.PhnNo = ""
                Pref.pwd = ""
            }
        }


    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.login_TV -> {

                val stat = StatFs(Environment.getExternalStorageDirectory().path)
                val bytesAvailable: Long
                bytesAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    stat.blockSizeLong * stat.availableBlocksLong
                } else {
                    stat.blockSize.toLong() * stat.availableBlocks.toLong()
                }
                val megAvailable = bytesAvailable / (1024 * 1024)
                println("storage " + megAvailable.toString());
                XLog.d("phone storage : FREE SPACE AVAILABLE : " + megAvailable.toString() + " Time :" + AppUtils.getCurrentDateTime())


                if (megAvailable < 5000 && false) {
                    val simpleDialog = Dialog(this)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    if (Pref.user_name != null) {
                        dialog_yes_no_headerTV.text = "Hi " + Pref.user_name!! + "!"
                    } else {
                        dialog_yes_no_headerTV.text = "Hi User" + "!"
                    }
                    //dialogHeader.text = "You have only "+megAvailable.toString()+ " MB available to store data. It is not sufficient\n" +
                    //"to proceed. Please clear memory and Retry Login again. Thanks."

                    dialogHeader.text = "Please note that memory available is less than 5 GB. App may not function properly. Please make available memory greater than 5 GB."

                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                        login_TV.isEnabled = false
                        println("xyzy - login called" + AppUtils.getCurrentDateTime());
                        //Crashlytics.getInstance().crash()
                        if (TextUtils.isEmpty(username_EDT.text.toString().trim())) {
                            showSnackMessage(getString(R.string.error_enter_username))
                            login_TV.isEnabled = true
                        } else if (TextUtils.isEmpty(password_EDT.text.toString().trim())) {
                            showSnackMessage(getString(R.string.error_enter_pwd))
                            login_TV.isEnabled = true
                        } else {
                            AppUtils.hideSoftKeyboard(this)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (Settings.canDrawOverlays(this)) {
                                    initiateLogin()
                                } else {
                                    //Permission is not available. Display error text.
                                    //getOverlayPermission()

                                    // overlay testing
                                    initiateLogin()
                                }
                            } else {
                                initiateLogin()
                            }


                        }
                        /*gotoHomeActivity()
                        isLoginLoaded = true*/
                    })
                    simpleDialog.show()
                } else {
                    login_TV.isEnabled = false
                    println("xyzy - login called" + AppUtils.getCurrentDateTime());
                    //Crashlytics.getInstance().crash()
                    if (TextUtils.isEmpty(username_EDT.text.toString().trim())) {
                        showSnackMessage(getString(R.string.error_enter_username))
                        login_TV.isEnabled = true
                    } else if (TextUtils.isEmpty(password_EDT.text.toString().trim())) {
                        showSnackMessage(getString(R.string.error_enter_pwd))
                        login_TV.isEnabled = true
                    } else {
                        AppUtils.hideSoftKeyboard(this)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (Settings.canDrawOverlays(this)) {
                                initiateLogin()
                            } else {
                                //Permission is not available. Display error text.
                                //getOverlayPermission()

                                // overlay testing
                                initiateLogin()
                            }
                        } else {
                            initiateLogin()
                        }


                    }
                    /*gotoHomeActivity()
                    isLoginLoaded = true*/
                }


            }
            R.id.forgot_password_TV -> {
                val pop = ForgotPasswordDialog()
                val fm = supportFragmentManager
                pop.show(fm, "name")

            }

         /*   R.id.activity_login_tvappCustomLogs -> {
                *//*if(Build.VERSION.SDK_INT>=30){
                    if (!Environment.isExternalStorageManager()){
                        fileManagePermi()
                    }else{
                        openShareIntents()
                    }
                }else{
                    openShareIntents()
                }*//*
                openShareIntents()
                activity_login_llList.visibility = View.INVISIBLE
            }*/

//            R.id.activity_login_tvappCustomAnydesk -> {
//                var launchIntent: Intent? = packageManager.getLaunchIntentForPackage("com.anydesk.anydeskandroid")
//                if (launchIntent != null) {
//                    startActivity(launchIntent)
//                    activity_login_llList.visibility = View.INVISIBLE
//                } else {
//                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.anydesk.anydeskandroid"))
//                    startActivity(intent)
//                    activity_login_llList.visibility = View.INVISIBLE
//                }
//            }

            R.id.activity_login_tvappCustomAnydeskInfo -> {
                val simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(true)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_settings)
                val tvappCustomAnydesk = simpleDialog.findViewById(R.id.activity_login_tvappCustomAnydesk) as AppCustomTextView
                val tvappCustomSharelog = simpleDialog.findViewById(R.id.activity_login_tvappCustomLogs) as AppCustomTextView
                val tvappDbShare = simpleDialog.findViewById(R.id.activity_login_tvappdbLogs) as AppCustomTextView

                if(Pref.WillRoomDBShareinLogin)
                    tvappDbShare.visibility = View.VISIBLE
                else
                    tvappDbShare.visibility = View.GONE

                tvappDbShare.setOnClickListener {
                    copyFile()
                    simpleDialog.dismiss()

                }
                tvappCustomAnydesk.setOnClickListener {
                    var launchIntent: Intent? = packageManager.getLaunchIntentForPackage("com.anydesk.anydeskandroid")
                    if (launchIntent != null) {
                        startActivity(launchIntent)
                    } else {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.anydesk.anydeskandroid"))
                        startActivity(intent)
                    }
                    simpleDialog.dismiss()
                }
                tvappCustomSharelog.setOnClickListener {
                    openShareIntents()
                    simpleDialog.dismiss()

                }
                var launchIntent: Intent? = packageManager.getLaunchIntentForPackage("com.anydesk.anydeskandroid")
                if (launchIntent != null) {
                    tvappCustomAnydesk.text = resources.getString(R.string.label_open_anydesk)
                } else {
                    tvappCustomAnydesk.text = resources.getString(R.string.label_install_anydesk)
                 }

                if (!activity_login_tvappCustomAnydeskInfo.isSelected) {
                    activity_login_tvappCustomAnydeskInfo.isSelected = true
//                    activity_login_llList.visibility = View.VISIBLE
                } else {
                    activity_login_tvappCustomAnydeskInfo.isSelected = false
//                    activity_login_llList.visibility = View.INVISIBLE
                }

                simpleDialog.show()


            }

            R.id.share_log_login_TV -> {
                /* if(Build.VERSION.SDK_INT>=30){
                     if (!Environment.isExternalStorageManager()){
                         fileManagePermi()
                     }else{
                         openShareIntents()
                     }
                 }else{
                     openShareIntents()
                 }*/
                openShareIntents()
            }
        }
    }

    private fun copyFile() {
        var ppath:String=""
        try {
            val sd = Environment.getExternalStorageDirectory()
            val data = Environment.getDataDirectory()
            if (sd.canWrite()) {
                val currentDBPath = getDatabasePath("fts_db").absolutePath
                val backupDBPath = "fts_db.db"
                ppath = currentDBPath
                val currentDB = File(currentDBPath)
                val backupDB = File(sd, backupDBPath)
                if (currentDB.exists()) {
                    val src: FileChannel = FileInputStream(currentDB).getChannel()
                    val dst: FileChannel = FileOutputStream(backupDB).getChannel()
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        openShareDB(ppath)

    }
    fun openShareDB(dbpath:String) {
        try {
            var currentDBPath = dbpath
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                currentDBPath="/data/user/0/com.kcteam/databases/fts_db"
            }
            val shareIntent = Intent(Intent.ACTION_SEND)
            val fileUrl = Uri.parse(File(currentDBPath, "").path);
            val file = File(fileUrl.path)
            if (!file.exists()) {
                return
            }
            val uri: Uri = FileProvider.getUriForFile(this, applicationContext.packageName.toString() + ".provider", file)
            shareIntent.type = "image/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(shareIntent, "Share log using"));
        } catch (e: Exception) {
            e.printStackTrace()
            Toaster.msgLong(this,e.toString())
        }
    }




    private fun fileManagePermi() {
//        val intent = Intent()
//        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
//        val uri = Uri.fromParts("package", this.packageName, null)
//        intent.data = uri
//        startActivity(intent)

    }

    fun openShareIntents() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
//        val phototUri = Uri.parse(localAbsoluteFilePath)
            //val fileUrl = Uri.parse(File(Environment.getExternalStorageDirectory(), "xkcteamlogsample/log").path);
            //27-09-2021
            val fileUrl = Uri.parse(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "xkcteamlogsample/log").path);
            val file = File(fileUrl.path)
            if (!file.exists()) {
                return
            }

//            val uri = Uri.fromFile(file)
            val uri: Uri = FileProvider.getUriForFile(mContext, mContext!!.applicationContext.packageName.toString() + ".provider", file)
//        shareIntent.data = fileUrl
            shareIntent.type = "image/png"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(shareIntent, "Share log using"));
        } catch (e: Exception) {
            e.printStackTrace()
        }


//        Uri uri = Uri.fromFile(file);
//        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(Intent.createChooser(emailIntent,""))
    }


    private fun initiateLogin() {
        println("xyz - initiateLogin started" + AppUtils.getCurrentDateTime());
        val list = AppDatabase.getDBInstance()?.addShopEntryDao()?.all
        if (Pref.isClearData) {
            if (list != null && list.isNotEmpty()) {
                Toaster.msgLong(this, "Please clear your data, in order to login")
                return
            }
        }

        /***********API CALL************/
        if (AppUtils.isOnline(this)) {
            //tv_internet_info.setBackgroundColor(resources.getColor(R.color.color_custom_green))
            tv_internet_info.setTextColor(resources.getColor(R.color.color_custom_white_1))
            tv_internet_info.text = getString(R.string.login_net_connected1)
            icon_internet.setImageDrawable(mContext.getDrawable(R.drawable.ic_internet_new))
//            tv_internet_info.slideDown()

            if (isApiInitiated)
                return
            //prapareLogin(this)
            println("xyz - initiateLogin end" + AppUtils.getCurrentDateTime());
            Handler(Looper.getMainLooper()).postDelayed({
                callNewSettingsApi()
            }, 400)
        } else {
            //showSnackMessage(getString(R.string.no_internet))
//            tv_internet_info.setBackgroundColor(resources.getColor(R.color.color_custom_red))
            tv_internet_info.setTextColor(resources.getColor(R.color.color_custom_red_1))
            tv_internet_info.text = getString(R.string.login_net_disconnected1)
            icon_internet.setImageDrawable(mContext.getDrawable(R.drawable.ic_wifi_connection_offline_new))
            login_TV.isEnabled = true
        }
    }
    fun View.slideUp(duration: Int = 1000) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, this.height.toFloat(), 0f)
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }
    fun View.slideDown(duration: Int = 1000) {
        visibility = View.VISIBLE
        val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
        animate.duration = duration.toLong()
        animate.fillAfter = true
        this.startAnimation(animate)
    }

    private fun callNewSettingsApi() {
        println("xyz - callNewSettingsApi started" + AppUtils.getCurrentDateTime());
        val repository = LoginRepositoryProvider.provideLoginRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getNewSettings(username_EDT.text.toString().trim(), password_EDT.text.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val newSettings = result as NewSettingsResponseModel

                            if (newSettings.status == NetworkConstant.SUCCESS) {
                                progress_wheel.stopSpinning()
                                if (newSettings.isFingerPrintMandatoryForAttendance != null)
                                    Pref.isFingerPrintMandatoryForAttendance = newSettings.isFingerPrintMandatoryForAttendance!!

                                if (newSettings.isFingerPrintMandatoryForVisit != null)
                                    Pref.isFingerPrintMandatoryForVisit = newSettings.isFingerPrintMandatoryForVisit!!

                                if (newSettings.isSelfieMandatoryForAttendance != null)
                                    Pref.isSelfieMandatoryForAttendance = newSettings.isSelfieMandatoryForAttendance!!

                                if (newSettings.isAddAttendence!!) {
                                    if (Pref.isFingerPrintMandatoryForAttendance) {
                                        if (isFingerPrintSupported) {
                                            checkForFingerPrint()

                                            fingerprintDialog = FingerprintDialog()
                                            fingerprintDialog?.show(supportFragmentManager, "")
                                        } else {
                                            if (Pref.isSelfieMandatoryForAttendance)
                                                showSelfieDialog()
                                            else
                                                prapareLogin(this@LoginActivity)
                                        }
                                    } else if (Pref.isSelfieMandatoryForAttendance)
                                        showSelfieDialog()
                                    else
                                        prapareLogin(this@LoginActivity)
                                } else
                                    prapareLogin(this@LoginActivity)

                            } else {
                                progress_wheel.stopSpinning()
                                openDialogPopup(newSettings.message!!)
                                login_TV.isEnabled = true
                            }
                            isApiInitiated = false

                        }, { error ->
                            XLog.d(" Login callNewSettingsApi : error : " +error.message.toString())
                            isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            login_TV.isEnabled = true
                            showSnackMessage(getString(R.string.something_went_wrong_new))
                        })
        )
    }

    private fun showSelfieDialog() {
        selfieDialog = SelfieDialog.getInstance({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                initCameraPermissionCheck()
            else {
                captureFrontImage()
            }
        }, false)
        selfieDialog?.show(supportFragmentManager, "")
    }

    private fun initCameraPermissionCheck() {
        permissionUtils = PermissionUtils(this, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                captureFrontImage()
            }

            override fun onPermissionNotGranted() {
                showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    private fun captureFrontImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = AppUtils.createImageFile()
                // Save a file: path for use with ACTION_VIEW intents
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    val photoURI: Uri = if (Build.VERSION.SDK_INT >= 24) {
                        FileProvider.getUriForFile(this, packageName + ".provider", photoFile)
                    } else
                        Uri.fromFile(photoFile)
                    mCurrentPhotoPath = "file:" + photoFile.absolutePath
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startActivityForResult(takePictureIntent, PermissionHelper.REQUEST_CODE_CAMERA)
                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
                ex.printStackTrace()
                return
            }
        }
    }

    fun prapareLogin(context: Context) {
        println("xyz - callNewSettingsApi end" + AppUtils.getCurrentDateTime());
        if (!AppUtils.isLocationEnabled(this)) {
            showSnackMessage(getString(R.string.alert_nolocation))
            login_TV.isEnabled = true

            //new gps call
            var mGpsStatusDetector: GpsStatusDetector? = null
            val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                mGpsStatusDetector = GpsStatusDetector(this)
                val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    mGpsStatusDetector?.checkGpsStatus()
                }
            }

            return
        }
        isApiInitiated = true
        XLog.d("LoginLocationRequest : " + "\n, IMEI :" + Pref.imei + ", Time :" + AppUtils.getCurrentDateTime() + ", Version :" + AppUtils.getVersionName(this))
//        progress_wheel.spin()
        if (Pref.latitude != null && Pref.latitude!!.trim() != "") {
            println("xyz - callApi started" + AppUtils.getCurrentDateTime());
            callApi(username_EDT.text.toString().trim(), password_EDT.text.toString())
        } else {
            val gpsTracker = GPSTracker(this)
            if (gpsTracker.isGPSTrackingEnabled) {
                Pref.latitude = gpsTracker.getLatitude().toString()
                Pref.longitude = gpsTracker.getLongitude().toString()
                println("xyz - callApi started" + AppUtils.getCurrentDateTime());
                callApi(username_EDT.text.toString().trim(), password_EDT.text.toString().trim())
            }
//            SingleShotLocationProvider.requestSingleUpdate(context,
//                    object : SingleShotLocationProvider.LocationCallback {
//                        override fun onStatusChanged(status: String) {
//                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                        }
//
//                        override fun onProviderEnabled(status: String) {
//                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                        }
//
//                        override fun onProviderDisabled(status: String) {
//                            isApiInitiated=false
//                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                        }
//
//                        override fun onNewLocationAvailable(location: Location) {
//                            XLog.d("LoginLocationAvailable : "+"\n , IMEI :" +Pref.imei+ ", Time :" + AppUtils.getCurrentDateTime()+ ", Version :" +AppUtils.getVersionName(this@LoginActivity))
//                            progress_wheel.stopSpinning()
//                            Pref.latitude = location.latitude.toString()
//                            Pref.longitude = location.longitude.toString()
//                            callApi(username_EDT.text.toString().trim(), password_EDT.text.toString().trim())
//                        }
//                    })
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        /*if (requestCode == PermissionHelper.REQUEST_CODE_PHONE_STATE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getIMEI()
            } else {
                PermissionHelper.checkPhoneStatePermisssion(this)
            }

        } else if (requestCode == PermissionHelper.REQUEST_CODE_STORAGE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getIMEI()
            } else {
                PermissionHelper.checkStoragePermission(this)
            }
        }*/

        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun callApi(username: String, password: String) {

        if (Pref.latitude.isNullOrEmpty() || Pref.longitude.isNullOrEmpty()) {
            isApiInitiated = false
            progress_wheel.stopSpinning()
            showSnackMessage(resources.getString(R.string.alert_location_unavailable))
            return
        }
        if (Pref.imei.isEmpty()) {
            isApiInitiated = false
            progress_wheel.stopSpinning()
            showSnackMessage(resources.getString(R.string.alert_imei_unavailable))
            getIMEI()
            return
        }

        if (Pref.isRememberMe) {
            Pref.PhnNo = username
            Pref.pwd = password
        } else {
            Pref.PhnNo = ""
            Pref.pwd = ""
        }

        var mLocation = ""

        if (Pref.latitude != "0.0" && Pref.longitude != "0.0") {
            mLocation = LocationWizard.getAdressFromLatlng(this, Pref.latitude?.toDouble()!!, Pref.longitude?.toDouble()!!)

            if (mLocation.contains("http"))
                mLocation = "Unknown"

            doLogin(username, password, mLocation)
        } else if (Pref.latitude == "0.0" && Pref.longitude == "0.0") {
            progress_wheel.spin()
            SingleShotLocationProvider.requestSingleUpdate(this,
                    object : SingleShotLocationProvider.LocationCallback {
                        override fun onStatusChanged(status: String) {
                        }

                        override fun onProviderEnabled(status: String) {
                        }

                        override fun onProviderDisabled(status: String) {
                        }

                        override fun onNewLocationAvailable(location: Location) {
                            progress_wheel.stopSpinning()
                            Pref.latitude = location.latitude.toString()
                            Pref.longitude = location.longitude.toString()

                            mLocation = LocationWizard.getAdressFromLatlng(this@LoginActivity, Pref.latitude?.toDouble()!!, Pref.longitude?.toDouble()!!)

                            if (mLocation.contains("http"))
                                mLocation = "Unknown"
                            println("xyz - callNewSettingsApi end" + AppUtils.getCurrentDateTime());
                            doLogin(username, password, mLocation)
                        }
                    })
        }
    }
    
    
    private fun doLogin(username: String, password: String, location: String) {
        println("xyz - doLogin started" + AppUtils.getCurrentDateTime());

        XLog.d("LoginApiRequest : " + "\n, IMEI :" + Pref.imei + ", Time :" + AppUtils.getCurrentDateTime() + ", Version :" + AppUtils.getVersionName(this) +
                ", username : " + username + ", password : " + password + ", lat : " + Pref.latitude + ", long : " + Pref.longitude + ", location : " + location +
                ", device token : " + Pref.deviceToken)

        val repository = LoginRepositoryProvider.provideLoginRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.login(username, password, Pref.latitude.toString(), Pref.longitude.toString(), /*"2018-12-21 10:22:23"*/
                        AppUtils.getCurrentDateTime(), /*"356145081053376"*/ Pref.imei, AppUtils.getVersionName(this), location, Pref.deviceToken)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val loginResponse = result as LoginResponse
                            XLog.d("LoginApiResponse : " + "\n" + "Status====> " + loginResponse.status + ", Message===> " + loginResponse.message)
                            if (loginResponse.status == NetworkConstant.SUCCESS) {

                                /*if (Pref.isAddAttendence && (TextUtils.isEmpty(Pref.temp_login_date) || Pref.temp_login_date != AppUtils.getCurrentDateChanged())) {
                                    Pref.isAddAttendence = false
                                }*/

                                if (Pref.temp_user_id == loginResponse.user_details!!.user_id) {
                                    doAfterLoginFunctionality(loginResponse)
                                } else {
                                    doAsync {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.userLocationDataDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.userAttendanceDataDao().delete()
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.stateDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.cityDao().deleteAll()
                                        /*AppDatabase.getDBInstance()!!.marketingDetailDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.marketingDetailImageDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.marketingCategoryMasterDao().deleteAll()*/
                                        AppDatabase.getDBInstance()!!.ppListDao().delete()
                                        AppDatabase.getDBInstance()!!.ddListDao().delete()
                                        AppDatabase.getDBInstance()!!.workTypeDao().delete()
                                        AppDatabase.getDBInstance()!!.orderListDao().delete()
                                        AppDatabase.getDBInstance()!!.orderDetailsListDao().delete()
                                        AppDatabase.getDBInstance()!!.shopVisitImageDao().delete()
                                        AppDatabase.getDBInstance()!!.updateStockDao().delete()
                                        AppDatabase.getDBInstance()!!.performanceDao().delete()
                                        AppDatabase.getDBInstance()!!.gpsStatusDao().delete()
                                        AppDatabase.getDBInstance()!!.collectionDetailsDao().delete()
                                        AppDatabase.getDBInstance()!!.inaccurateLocDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.leaveTypeDao().delete()
                                        AppDatabase.getDBInstance()!!.routeDao().deleteRoute()
                                        AppDatabase.getDBInstance()!!.productListDao().deleteAllProduct()
                                        AppDatabase.getDBInstance()!!.orderProductListDao().delete()
                                        AppDatabase.getDBInstance()!!.stockListDao().delete()
                                        AppDatabase.getDBInstance()!!.routeShopListDao().deleteData()
                                        AppDatabase.getDBInstance()!!.selectedWorkTypeDao().delete()
                                        AppDatabase.getDBInstance()!!.selectedRouteListDao().deleteRoute()
                                        AppDatabase.getDBInstance()!!.selectedRouteShopListDao().deleteData()
                                        AppDatabase.getDBInstance()!!.updateOutstandingDao().delete()
                                        AppDatabase.getDBInstance()!!.idleLocDao().delete()
                                        AppDatabase.getDBInstance()!!.billingDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.billProductDao().delete()
                                        AppDatabase.getDBInstance()!!.addMeetingDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.addMeetingTypeDao().deleteAll()
                                        AppDatabase.getDBInstance()!!.productRateDao().deleteAll()
                                        AppDatabase.getDBInstance()?.areaListDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.shopTypeDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.modelListDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.primaryAppListDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.secondaryAppListDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.leadTypeDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.stageDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.funnelStageDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.bsListDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.quotDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.typeListDao()?.delete()
                                        AppDatabase.getDBInstance()?.memberAreaDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.memberShopDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.memberDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.clientDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.projectDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.activityDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.productDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.shopVisitAudioDao()?.delete()
                                        AppDatabase.getDBInstance()?.taskDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.batteryNetDao()?.delete()
                                        AppDatabase.getDBInstance()?.timesheetDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.activityDropdownDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.typeDao()?.delete()
                                        AppDatabase.getDBInstance()?.priorityDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.activDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.addDocProductDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.addDocDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.addChemistProductDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.addChemistDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.documentTypeDao()?.delete()
                                        AppDatabase.getDBInstance()?.documentListDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.paymenttDao()?.delete()
                                        AppDatabase.getDBInstance()?.entityDao()?.delete()
                                        AppDatabase.getDBInstance()?.partyStatusDao()?.delete()
                                        AppDatabase.getDBInstance()?.retailerDao()?.delete()
                                        AppDatabase.getDBInstance()?.dealerDao()?.delete()
                                        AppDatabase.getDBInstance()?.beatDao()?.delete()
                                        AppDatabase.getDBInstance()?.assignToShopDao()?.delete()
                                        AppDatabase.getDBInstance()!!.shopVisitCompetetorImageDao().deleteUnSyncedCopetetorImg()
                                        Pref.isLocationActivitySynced = false
                                        Pref.prevOrderCollectionCheckTimeStamp = 0L

                                        uiThread {
                                            doAfterLoginFunctionality(loginResponse)
                                        }
                                    }
                                }

                                XLog.d("LoginApiResponse : " + "\n" + "Username :" + Pref.user_name+ ", IMEI :" + Pref.imei + ", Time :" + AppUtils.getCurrentDateTime() + ", Version :" + AppUtils.getVersionName(this))

                            }
                            else if (loginResponse.status == "220") {
                                progress_wheel.stopSpinning()
                                //showSnackMessage(loginResponse.message!!)

                                login_TV.isEnabled = true
                                Toaster.msgLong(this, loginResponse.message!!)
                            }
                            else {
                                progress_wheel.stopSpinning()

                                if(loginResponse.message!!.contains("IMEI",ignoreCase = true))
                                {
                                    var realName=""
                                    try{
                                        realName= Pref.user_name!!.replace("null","")
                                    }catch (ex:Exception){
                                        realName=""
                                    }
//                                    openDialogPopupIMEI("Hi! $realName ($username)","Current Login ID has already been used from another mobile device. You are not allowed to " +
//                                            "login from your current device due to IMEI BLOCKED! Please talk to Admin.")

                                    openDialogPopupIMEI("Hi! $realName ($username)","The Current Device is already in use by another User ($realName). You are not allowed to " +
                                            "login from your current device due to IMEI BLOCKED! Please talk to Admin.")
                                }else{
                                    openDialogPopup(loginResponse.message!!)
                                }

                                login_TV.isEnabled = true
                            }
                            isApiInitiated = false

                        }, { error ->
                            login_TV.isEnabled = true
                            isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("LoginApiResponse ERROR: " + error.localizedMessage + "\n" + "Username :" + Pref.user_name + ", IMEI :" + Pref.imei + ", Time :" + AppUtils.getCurrentDateTime() + ", Version :" + AppUtils.getVersionName(this))
                            showSnackMessage(resources.getString(R.string.alert_login_failed))
                        })
        )
    }


    private fun doAfterLoginFunctionality(loginResponse: LoginResponse) {
        println("xyz - doLogin end" + AppUtils.getCurrentDateTime());
        println("xyz - doAfterLoginFunctionality started" + AppUtils.getCurrentDateTime());
        // setEveningAlarm(this, 15, 9)

        Pref.user_id = loginResponse.user_details!!.user_id
        Pref.temp_user_id = loginResponse.user_details!!.user_id
        Pref.user_name = loginResponse.user_details!!.name
        Pref.session_token = loginResponse.session_token
        Pref.isLogoutInitiated = false
        Pref.login_time = AppUtils.getCurrentTimeWithMeredian()
        Pref.login_date_time = AppUtils.getCurrentDateTime()
        Pref.login_date = AppUtils.getCurrentDateChanged()
        Pref.isFieldWorkVisible = loginResponse.user_details?.isFieldWorkVisible!!

        XLog.d("Login User ID " + "User ID :" + Pref.user_id)

        startService(Intent(this, MemberShopListIntentService::class.java))

        /*Pref.temp_login_date = "28-Mar-2019"

        if (!TextUtils.isEmpty(Pref.temp_login_date) && Pref.login_date != Pref.temp_login_date)
            Pref.prevOrderCollectionCheckTimeStamp = 0L*/

        Pref.temp_login_date = AppUtils.getCurrentDateChanged()
        Pref.totalShopVisited = loginResponse.user_count!!.total_shop_visited!!
        Pref.totalTimeSpenAtShop = loginResponse.user_count!!.total_time_spent_at_shop!!
        Pref.totalAttendance = loginResponse.user_count!!.total_attendance!!
        Pref.isAutoLogout = false
        Pref.isAddAttendence = loginResponse.user_details!!.isAddAttendence?.toBoolean()!!
        Pref.isSeenTermsConditions = false
        Pref.termsConditionsText = ""
        Pref.approvedInTime = AppUtils.convertTime(FTStorageUtils.getStringTimeToDate(loginResponse.user_details!!.user_login_time))
        Pref.approvedOutTime = AppUtils.convertTime(FTStorageUtils.getStringTimeToDate(loginResponse.user_details!!.user_logout_time))
        //Pref.approvedOutTime = "09:00 PM"

        /*if (!TextUtils.isEmpty(loginResponse.user_details?.attendance_text))
            Pref.attendance_text = loginResponse.user_details?.attendance_text!!*/

        if (!TextUtils.isEmpty(loginResponse.user_details!!.home_lat) && !TextUtils.isEmpty(loginResponse.user_details!!.home_long)) {
            Pref.isHomeLocAvailable = true
            Pref.home_latitude = loginResponse.user_details!!.home_lat.toString()
            Pref.home_longitude = loginResponse.user_details!!.home_long.toString()
        } else
            Pref.isHomeLocAvailable = false


        if (!TextUtils.isEmpty(loginResponse.user_details!!.willAlarmTrigger))
            Pref.willAlarmTrigger = loginResponse.user_details!!.willAlarmTrigger?.toBoolean()!!

        if (!Pref.isAddAttendence)
            Pref.totalS2SDistance = "0.0"

        //saveDataInLocalDataBase(loginResponse)
        if (!TextUtils.isEmpty(loginResponse.user_details?.Gps_Accuracy))
            Pref.gpsAccuracy = loginResponse.user_details?.Gps_Accuracy!!
        else
            Pref.gpsAccuracy = "100"

        if (!TextUtils.isEmpty(loginResponse.user_details?.isOnLeave))
            Pref.isOnLeave = loginResponse.user_details?.isOnLeave!!

        if (TextUtils.isEmpty(loginResponse.user_details?.isOnLeave)) {
            Pref.IsLeavePressed = false
        } else if (loginResponse.user_details?.isOnLeave.equals("true")) {
            Pref.IsLeavePressed = true
        } else if (loginResponse.user_details?.isOnLeave.equals("false")) {
            Pref.IsLeavePressed = false
        }
        Log.e("leave", Pref.IsLeavePressed.toString())



        XLog.d("LoginApiResponse : " + "\n" + "GPS SETTINGS=====> " + Pref.gpsAccuracy)

        if (!TextUtils.isEmpty(loginResponse.user_details!!.add_attendence_time))
            Pref.add_attendence_time = loginResponse.user_details!!.add_attendence_time
        else
            Pref.add_attendence_time = ""

        if (!TextUtils.isEmpty(loginResponse.user_details!!.idle_time))
            AppUtils.idle_time = loginResponse.user_details!!.idle_time!!

        if (!TextUtils.isEmpty(loginResponse.user_details!!.distributor_name))
            Pref.distributorName = loginResponse.user_details!!.distributor_name!!

        if (!TextUtils.isEmpty(loginResponse.user_details!!.market_worked))
            Pref.marketWorked = loginResponse.user_details!!.market_worked!!

        /*val stateList = ArrayList<LoginStateListDataModel>()

        val state = LoginStateListDataModel()
        state.id = "1"
        state.state_name = "West Bengal"
        stateList.add(state)

        val state2 = LoginStateListDataModel()
        state2.id = "2"
        state2.state_name = "Bihar"
        stateList.add(state2)

        val state3 = LoginStateListDataModel()
        state3.id = "3"
        state3.state_name = "U.P."
        stateList.add(state3)

        val state4 = LoginStateListDataModel()
        state4.id = "4"
        state4.state_name = "M.P."
        stateList.add(state4)

        val state5 = LoginStateListDataModel()
        state5.id = "5"
        state5.state_name = "Asssam"
        stateList.add(state5)*/

        //loginResponse.user_details!!.state_list = stateList

        if (loginResponse.state_list != null && loginResponse.state_list!!.size > 0)
            AppUtils.saveSharedPreferencesStateList(this, loginResponse.state_list!!)

        try {
            setProfileDetailInPref(loginResponse.user_details!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try{
            Pref.IsOnLeaveForTodayApproved=false
            Pref.OnLeaveForTodayStatus=""

            if(loginResponse.user_details!!.IsOnLeaveForToday!!){
                Pref.IsOnLeaveForTodayApproved=true
            }else{
                Pref.IsOnLeaveForTodayApproved=false
                Pref.OnLeaveForTodayStatus=loginResponse.user_details!!.OnLeaveForTodayStatus!!.toUpperCase()
            }
        }catch (ex:Exception){
            ex.printStackTrace()
            Pref.OnLeaveForTodayStatus=""
        }

        Pref.NotiCountFlag = false
        Pref.IsPendingColl = false
        Pref.IsZeroOrder = false

        progress_wheel.stopSpinning()


        // updated on 17/08/2021
        //getListFromDatabase()
        println("xyz - doAfterLoginFunctionality end" + AppUtils.getCurrentDateTime());
        println("xyz - isStartOrEndDay started" + AppUtils.getCurrentDateTime());

        callUserConfigApi()

        //isStartOrEndDay()
    }


    fun isStartOrEndDay() {
        if(Pref.IsShowDayStart) {
            XLog.d("API_Optimization GET isStartOrEndDay Login : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            try {
                Pref.DayStartMarked = false
                Pref.DayEndMarked = false
                Pref.DayStartShopType = ""
                Pref.DayStartShopID = ""
                //Pref.IsDDvistedOnceByDay=false
                val repository = DayStartEndRepoProvider.dayStartRepositiry()
                BaseActivity.compositeDisposable.add(
                        repository.dayStartEndStatus(AppUtils.getCurrentDateyymmdd())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    XLog.d("Login DayStart : RESPONSE " + result.status)
                                    val response = result as StatusDayStartEnd
                                    if (response.status == NetworkConstant.SUCCESS) {

                                        doAsync {
                                            Pref.DayStartMarked = response.DayStartMarked!!
                                            Pref.DayEndMarked = response.DayEndMarked!!
                                            Pref.DayStartShopType = response.day_start_shop_type!!
                                            Pref.DayStartShopID = response.day_start_shop_id!!
                                            Pref.IsDDvistedOnceByDay = response.IsDDvistedOnceByDay!!

                                            uiThread {
                                                if (Pref.DayEndMarked) {
                                                    //showSnackMessage(resources.getString(R.string.alert_imei_unavailable))
                                                    Pref.user_id = ""
                                                    showSnackMessage("You already marked Day End. You will be able to login tomorrow! Thanks.")
                                                    login_TV.isEnabled = true
                                                } else {
                                                    getListFromDatabase()
                                                }
                                            }
                                        }

                                    } else {
                                        Pref.IsDDvistedOnceByDay = false
                                        getListFromDatabase()
                                    }
                                }, { error ->
                                    if (error == null) {
                                        XLog.d("Login DayStart : ERROR " + "UNEXPECTED ERROR IN DayStart API")
                                    } else {
                                        XLog.d("Login DayStart : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
                                    Pref.IsDDvistedOnceByDay = false
                                    getListFromDatabase()
                                })
                )
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                getListFromDatabase()
            }
        }
        else
            {
                XLog.d("API_Optimization GET isStartOrEndDay Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
                getListFromDatabase()
            }

    }


    private fun setProfileDetailInPref(user_details: UserDetail) {
        if (user_details.profile_image == null) {
            Pref.isProfileUpdated = false
            return
        }
        if (user_details.profile_image != null)
            Pref.profile_img = user_details.profile_image!!
        Pref.profile_state = user_details.state!!
        Pref.profile_city = user_details.city!!
        Pref.profile_pincode = user_details.pincode!!
        Pref.profile_country = getString(R.string.india)
        Pref.profile_address = user_details.address!!
        if (Pref.profile_state.isNotBlank())
            Pref.isProfileUpdated = true

    }

    private fun getListFromDatabase() {
        println("xyz - isStartOrEndDay end" + AppUtils.getCurrentDateTime());
        println("xyz - getListFromDatabase started" + AppUtils.getCurrentDateTime());
        list = AppDatabase.getDBInstance()!!.addShopEntryDao().uniqueShoplist
        if (list.isEmpty())
            callShopListApi()
        else {
            /*if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                getProductList("")
            else
                getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)*/
            checkToCallAssignedDDListApi()
        }
    }

    private fun callShopListApi() {
        println("xyz - getListFromDatabase end" + AppUtils.getCurrentDateTime());
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getShopList(Pref.session_token!!, Pref.user_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            var shopList = result as ShopListResponse
                            if (shopList.status == NetworkConstant.SUCCESS) {
                                if (shopList.data!!.shop_list!!.isNotEmpty()) {
                                    convertToShopListSetAdapter(shopList.data!!.shop_list!!)
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                        getProductList("")
                                    else
                                        getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                                }
                            } else if (shopList.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                                if (/*(assignDDList == null || assignDDList.isEmpty()) &&*/ !TextUtils.isEmpty(Pref.profile_state))
                                    getAssignedDDListApi()
                                else {

                                    if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                        getProductList("")
                                    else
                                        getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                                if (/*(assignDDList == null || assignDDList.isEmpty()) &&*/ !TextUtils.isEmpty(Pref.profile_state))
                                    getAssignedDDListApi()
                                else {

                                    if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                        getProductList("")
                                    else
                                        getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                                }
//                                showSnackMessage(shopList.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                            if (/*(assignDDList == null || assignDDList.isEmpty()) &&*/ !TextUtils.isEmpty(Pref.profile_state))
                                getAssignedDDListApi()
                            else {

                                if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                    getProductList("")
                                else
                                    getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                            }
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }

    /*Team new work*/
    private fun callExtraTeamShopListApi() {
        //list = AppDatabase.getDBInstance()!!.addShopEntryDao().uniqueShoplist
        if (true){
            try{
                val repository = ShopListRepositoryProvider.provideShopListRepository()
                progress_wheel.spin()
                BaseActivity.compositeDisposable.add(
                        repository.getExtraTeamShopList(Pref.session_token!!, Pref.user_id!!)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    var shopList = result as ShopListResponse
                                    if (shopList.status == NetworkConstant.SUCCESS) {
                                        convertToShopListTeamSetAdapter(shopList.data!!.shop_list!!)
                                    }  else {
                                        gotoHomeActivity()
                                    }
                                }, { error ->
                                    error.printStackTrace()
                                    progress_wheel.stopSpinning()
                                    gotoHomeActivity()
                                })
                )
            }
            catch(ex:Exception){
                ex.printStackTrace()
                gotoHomeActivity()
            }
        }
        else{
            gotoHomeActivity()
        }


    }


    private fun getProductList(date: String?) {
        //Hardcoded for EuroBond
        //if(Pref.isOrderShow || true){
        if(Pref.isOrderShow){
            XLog.d("API_Optimization getProductList Login : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )

        println("xyzzz - getProductList started" + AppUtils.getCurrentDateTime());
        val repository = ProductListRepoProvider.productListProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                //repository.getProductList(Pref.session_token!!, Pref.user_id!!, date!!)
                repository.getProductList(Pref.session_token!!, Pref.user_id!!, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ProductListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {

                                var list = response.product_list


                                /*                            var ppL: ArrayList<ProductListEntity>? = ArrayList()
                                                            var ppObj:ProductListEntity = ProductListEntity()
                                                            ppObj.id=35354
                                                            ppObj.brand_id="1"
                                                            ppObj.category_id="2"
                                                            ppObj.watt_id= "11"
                                                            ppObj.brand="Meca"
                                                            ppObj.product_name="GLOCILIN CLAV 625"
                                                            ppObj.category="DRUGS"
                                                            ppObj.watt="N.A"

                                                            ppL!!.add(ppObj)

                                                            response.product_list?.add(ppObj)
                                                            list=response.product_list*/

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        if (!TextUtils.isEmpty(date))
                                            AppDatabase.getDBInstance()?.productListDao()?.deleteAllProduct()


                                        //var listConv : List<ProductListEntity> = list!! as List<ProductListEntity>
                                        println("xyzzz - getProductList db_______started size " + list.size);
                                        println("xyzzz - getProductList db_______started" + AppUtils.getCurrentDateTime());

                                        AppDatabase.getDBInstance()?.productListDao()?.insertAll(list!!)

                                        println("xyzzz - getProductList db_______end" + AppUtils.getCurrentDateTime());

                                        //AppDatabase.getDBInstance()?.productListDao()?.updateData(listConv!!)

                                        /*for (i in list.indices) {
                                            val productEntity = ProductListEntity()
                                            productEntity.id = list[i].id?.toInt()!!
                                            productEntity.product_name = list[i].product_name
                                            productEntity.watt = list[i].watt
                                            productEntity.category = list[i].category
                                            productEntity.brand = list[i].brand
                                            productEntity.brand_id = list[i].brand_id
                                            productEntity.watt_id = list[i].watt_id
                                            productEntity.category_id = list[i].category_id
                                            productEntity.date = AppUtils.getCurrentDateForShopActi()
                                            AppDatabase.getDBInstance()?.productListDao()?.insert(productEntity)
                                        }*/

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            //checkToCallSelectedRouteListApi()
                                            checkToCallOrderList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                    //checkToCallSelectedRouteListApi()
                                    checkToCallOrderList()
                                }
                            } else {
                                progress_wheel.stopSpinning()

                                //checkToCallSelectedRouteListApi()
                                checkToCallOrderList()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            //checkToCallSelectedRouteListApi()
                            checkToCallOrderList()
                        })
        )
        }else{
            XLog.d("API_Optimization getProductList Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallOrderList()
        }
    }

    private fun checkToCallOrderList() {
        val list = AppDatabase.getDBInstance()!!.orderDetailsListDao().getAll()
        if (list != null && list.isNotEmpty()) {
            checkToCallSelectedRouteListApi()
        } else
            getOrderList()
    }

    private fun getOrderList() {

        if(Pref.isOrderShow){
            XLog.d("API_Optimization getOrderList Login : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )

        println("xyz - getOrderList started" + AppUtils.getCurrentDateTime());
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
                                            if (!TextUtils.isEmpty(order_details_list[i].order_date_time))
                                                orderDetailList.only_date = AppUtils.convertDateTimeToCommonFormat(order_details_list[i].order_date_time!!)
                                            orderDetailList.shop_id = order_details_list[i].shop_id
                                            orderDetailList.description = ""

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


                                            /*28-12-2021*/
                                            if (!TextUtils.isEmpty(order_details_list[i].scheme_amount)) {
                                                val finalScAmount = String.format("%.2f", order_details_list[i].scheme_amount?.toFloat())
                                                orderDetailList.scheme_amount = finalScAmount
                                            }


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

                                                    /*28-12-2021*/
                                                    if (!TextUtils.isEmpty(order_details_list[i].product_list?.get(j)?.scheme_rate)) {
                                                        val finalScRate = String.format("%.2f", order_details_list[i].product_list?.get(j)?.scheme_rate?.toFloat())
                                                        productOrderList.scheme_rate = finalScRate
                                                    }
                                                    if (!TextUtils.isEmpty(order_details_list[i].product_list?.get(j)?.scheme_rate)) {
                                                        productOrderList.scheme_qty = order_details_list[i].product_list?.get(j)?.scheme_qty
                                                    } else {
                                                        productOrderList.scheme_qty = "0"
                                                    }
                                                    if (!TextUtils.isEmpty(order_details_list[i].product_list?.get(j)?.total_scheme_price)) {
                                                        val finalTotalScPrice = String.format("%.2f", order_details_list[i].product_list?.get(j)?.total_scheme_price?.toFloat())
                                                        productOrderList.total_scheme_price = finalTotalScPrice
                                                    }

                                                    productOrderList.shop_id = order_details_list[i].shop_id

                                                    AppDatabase.getDBInstance()!!.orderProductListDao().insert(productOrderList)
                                                }
                                            }

                                            AppDatabase.getDBInstance()!!.orderDetailsListDao().insert(orderDetailList)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            checkToCallSelectedRouteListApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                    checkToCallSelectedRouteListApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()

                                checkToCallSelectedRouteListApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            checkToCallSelectedRouteListApi()
                        })
        )
        }else{
            XLog.d("API_Optimization getOrderList Login : disdable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallSelectedRouteListApi()
        }
    }

    private fun checkToCallSelectedRouteListApi() {
        println("xyz - getOrderList ended" + AppUtils.getCurrentDateTime());
        val list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getAll()
        if (list != null && list.isNotEmpty()) {
            //callUserConfigApi()
            //checkToCallCollectionApi()
            AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.delete()
            AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.deleteData()
            AppDatabase.getDBInstance()?.selectedRouteListDao()?.deleteRoute()

        } //else {
        getSelectedRouteList()
        //}
    }


    private fun checkToCallCollectionApi() {
        println("xyz - getSelectedRouteList ended" + AppUtils.getCurrentDateTime());
        val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getAll()
        if (list != null && list.isNotEmpty()) {
            /*API_Optimization 02-03-2022*/
            //callUserConfigApi()
            checkToCallAlarmConfigApi()   // calling instead of callUserConfigApi()
        } else
            callCollectionListApi()
    }

    private fun callCollectionListApi() {
        if (Pref.isCollectioninMenuShow) {
            XLog.d("API_Optimization GET callCollectionListApi Login : enable " +  "Time: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
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
                                    /*API_Optimization 02-03-2022*/
                                    //callUserConfigApi()
                                    checkToCallAlarmConfigApi()   // calling instead of callUserConfigApi()
                                } else
                                    saveToDatabase(collection.collection_list!!)

                            } else {
                                progress_wheel.stopSpinning()
                                /*API_Optimization 02-03-2022*/
                                //callUserConfigApi()
                                checkToCallAlarmConfigApi()   // calling instead of callUserConfigApi()
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            /*API_Optimization 02-03-2022*/
                            //callUserConfigApi()
                            checkToCallAlarmConfigApi()   // calling instead of callUserConfigApi()
                        })
        )
        }
        else{
            XLog.d("API_Optimization GET callCollectionListApi  Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            checkToCallAlarmConfigApi()
        }
    }

    //private fun saveToDatabase(collection_details_list: ArrayList<CollectionListDataModel>) {
    private fun saveToDatabase(collection_details_list: ArrayList<CollectionDetailsEntity>) {
        doAsync {
            println("xyz - saveToDatabase started" + AppUtils.getCurrentDateTime());
            //for (i in collection_details_list.indices) {
            //val collectionList = CollectionDetailsEntity()
            //collectionList.date = AppUtils.convertDateTimeToCommonFormat(collection_details_list[i].collection_date!!) //  /*AppUtils.convertToCommonFormat(collection_details_list[i].collection_date!!)*/
            //collectionList.shop_id = collection_details_list[i].shop_id      //
            //collectionList.isUploaded = true
            //collectionList.collection_id = collection_details_list[i].collection_id
            //collectionList.collection = collection_details_list[i].collection    //
            //collectionList.only_time = AppUtils.convertDateTimeToTime(collection_details_list[i].collection_date!!)  //
            //collectionList.payment_id = collection_details_list[i].payment_id //
            //collectionList.instrument_no = collection_details_list[i].instrument_no//
            //collectionList.feedback = collection_details_list[i].remarks//
            //collectionList.bank = collection_details_list[i].bank//
            //collectionList.file_path = collection_details_list[i].doc//
            //collectionList.bill_id = collection_details_list[i].bill_id//
            //collectionList.order_id = collection_details_list[i].order_id//
            //collectionList.patient_no = collection_details_list[i].patient_no//
            //collectionList.patient_name = collection_details_list[i].patient_name//
            //collectionList.patient_address = collection_details_list[i].patient_address//


            //AppDatabase.getDBInstance()!!.collectionDetailsDao().insert(collectionList)
            //}


            AppDatabase.getDBInstance()!!.collectionDetailsDao().insertAll(collection_details_list!!)



            uiThread {
                progress_wheel.stopSpinning()
                println("xyz - saveToDatabase ended" + AppUtils.getCurrentDateTime());
                /*API_Optimization 02-03-2022*/
                //callUserConfigApi()
                checkToCallAlarmConfigApi()   // calling instead of callUserConfigApi()
            }
        }
    }

    private fun callUserConfigApi() {
        println("xyz - callUserConfigApi started" + AppUtils.getCurrentDateTime());
        val repository = UserConfigRepoProvider.provideUserConfigRepository()
        //progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.userConfig(Pref.user_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UserConfigResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                try {
                                    Log.e("Login", "willLeaveApprovalEnable================> " + Pref.willLeaveApprovalEnable)

                                    if (response.getconfigure != null && response.getconfigure!!.size > 0) {
                                        for (i in response.getconfigure!!.indices) {
                                            if (response.getconfigure!![i].Key.equals("isVisitSync", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    AppUtils.isVisitSync = response.getconfigure!![i].Value!!
                                            } else if (response.getconfigure!![i].Key.equals("isAddressUpdate", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    AppUtils.isAddressUpdated = response.getconfigure!![i].Value!!
                                            } else if (response.getconfigure!![i].Key.equals("willShowUpdateDayPlan", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willShowUpdateDayPlan = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("updateDayPlanText", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.updateDayPlanText = response.getconfigure!![i].Value!!
                                            } else if (response.getconfigure!![i].Key.equals("dailyPlanListHeaderText", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.dailyPlanListHeaderText = response.getconfigure!![i].Value!!
                                            } else if (response.getconfigure!![i].Key.equals("isRateNotEditable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isRateNotEditable = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isMeetingAvailable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isMeetingAvailable = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure?.get(i)?.Key.equals("willShowTeamDetails", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.willShowTeamDetails = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isAllowPJPUpdateForTeam", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isAllowPJPUpdateForTeam = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("willLeaveApprovalEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willLeaveApprovalEnable = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure?.get(i)?.Key.equals("willReportShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.willReportShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("willAttendanceReportShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.willAttendanceReportShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("willPerformanceReportShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.willPerformanceReportShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("willVisitReportShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.willVisitReportShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("attendance_text", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.attendance_text = response.getconfigure?.get(i)?.Value!!
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("willTimesheetShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.willTimesheetShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isAttendanceFeatureOnly", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isAttendanceFeatureOnly = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("iscollectioninMenuShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isCollectioninMenuShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isVisitShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isVisitShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isOrderShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isOrderShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isShopAddEditAvailable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShopAddEditAvailable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isEntityCodeVisible", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isEntityCodeVisible = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isAreaMandatoryInPartyCreation", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isAreaMandatoryInPartyCreation = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isShowPartyInAreaWiseTeam", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowPartyInAreaWiseTeam = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isChangePasswordAllowed", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isChangePasswordAllowed = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isQuotationShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isQuotationShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isQuotationPopupShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isQuotationPopupShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isHomeRestrictAttendance", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isHomeRestrictAttendance = response.getconfigure?.get(i)?.Value!!
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("homeLocDistance", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.homeLocDistance = response.getconfigure?.get(i)?.Value!!
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("shopLocAccuracy", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.shopLocAccuracy = response.getconfigure?.get(i)?.Value!!
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isMultipleAttendanceSelection", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isMultipleAttendanceSelection = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isOrderReplacedWithTeam", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isOrderReplacedWithTeam = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isDDShowForMeeting", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isDDShowForMeeting = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isDDMandatoryForMeeting", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isDDMandatoryForMeeting = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isOfflineTeam", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isOfflineTeam = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isAllTeamAvailable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isAllTeamAvailable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isNextVisitDateMandatory", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isNextVisitDateMandatory = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isRecordAudioEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isRecordAudioEnable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isShowCurrentLocNotifiaction", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowCurrentLocNotifiaction = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isUpdateWorkTypeEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isUpdateWorkTypeEnable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isAchievementEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isAchievementEnable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isTarVsAchvEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isTarVsAchvEnable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isLeaveEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isLeaveEnable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isOrderMailVisible", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isOrderMailVisible = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isShopEditEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShopEditEnable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isTaskEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isTaskEnable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("isAppInfoEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isAppInfoEnable = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("appInfoMins", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.appInfoMins = response.getconfigure?.get(i)?.Value!!
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("autoRevisitDistance", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.autoRevisitDistance = response.getconfigure!![i].Value!!
                                            } else if (response.getconfigure!![i].Key.equals("autoRevisitTime", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.autoRevisitTime = response.getconfigure!![i].Value!!
                                            } else if (response.getconfigure!![i].Key.equals("willAutoRevisitEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willAutoRevisitEnable = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("dynamicFormName", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.dynamicFormName = response.getconfigure!![i].Value!!
                                            } else if (response.getconfigure!![i].Key.equals("willDynamicShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willDynamicShow = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("willActivityShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willActivityShow = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("willMoreVisitUpdateCompulsory", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willMoreVisitUpdateCompulsory = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("willMoreVisitUpdateOptional", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willMoreVisitUpdateOptional = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isDocumentRepoShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isDocumentRepoShow = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isChatBotShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isChatBotShow = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isAttendanceBotShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isAttendanceBotShow = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isVisitBotShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isVisitBotShow = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isShowOrderRemarks", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isShowOrderRemarks = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isShowOrderSignature", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isShowOrderSignature = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isShowSmsForParty", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isShowSmsForParty = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isVisitPlanShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isVisitPlanShow = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isVisitPlanMandatory", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isVisitPlanMandatory = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isAttendanceDistanceShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isAttendanceDistanceShow = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("willTimelineWithFixedLocationShow", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willTimelineWithFixedLocationShow = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isShowTimeline", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isShowTimeline = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("willScanVisitingCard", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willScanVisitingCard = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isCreateQrCode", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isCreateQrCode = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isScanQrForRevisit", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isScanQrForRevisit = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("isShowLogoutReason", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.isShowLogoutReason = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("willShowHomeLocReason", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willShowHomeLocReason = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("willShowShopVisitReason", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willShowShopVisitReason = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("minVisitDurationSpentTime", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.minVisitDurationSpentTime = response.getconfigure?.get(i)?.Value!!
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("willShowPartyStatus", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure!![i].Value))
                                                    Pref.willShowPartyStatus = response.getconfigure!![i].Value == "1"
                                            } else if (response.getconfigure!![i].Key.equals("willShowEntityTypeforShop", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.willShowEntityTypeforShop = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShowRetailerEntity", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowRetailerEntity = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShowDealerForDD", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowDealerForDD = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShowBeatGroup", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowBeatGroup = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShowShopBeatWise", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowShopBeatWise = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShowBankDetailsForShop", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowBankDetailsForShop = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShowOTPVerificationPopup", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowOTPVerificationPopup = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("locationTrackInterval", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.locationTrackInterval = response.getconfigure!![i].Value!!
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShowMicroLearning", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowMicroLearning = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("homeLocReasonCheckMins", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.homeLocReasonCheckMins = response.getconfigure!![i].Value!!
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("currentLocationNotificationMins", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.currentLocationNotificationMins = response.getconfigure!![i].Value!!
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isMultipleVisitEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isMultipleVisitEnable = response.getconfigure!![i].Value!! == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShowVisitRemarks", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowVisitRemarks = response.getconfigure!![i].Value!! == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShowNearbyCustomer", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShowNearbyCustomer = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isServiceFeatureEnable", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isServiceFeatureEnable = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isPatientDetailsShowInOrder", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isPatientDetailsShowInOrder = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isPatientDetailsShowInCollection", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isPatientDetailsShowInCollection = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isShopImageMandatory", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isShopImageMandatory = response.getconfigure!![i].Value == "1"
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("isLogShareinLogin", ignoreCase = true)) {
                                                if (response.getconfigure!![i].Value == "1") {
                                                    AppUtils.saveSharedPreferenceslogShareinLogin(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferenceslogShareinLogin(this, false)
                                                }

                                            } else if (response.getconfigure!![i].Key.equals("IsCompetitorenable", ignoreCase = true)) {
                                                Pref.isCompetitorImgEnable = response.getconfigure!![i].Value == "1"
                                                if (Pref.isCompetitorImgEnable) {
                                                    AppUtils.saveSharedPreferencesCompetitorImgEnable(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesCompetitorImgEnable(this, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsOrderStatusRequired", ignoreCase = true)) {
                                                Pref.isOrderStatusRequired = response.getconfigure!![i].Value == "1"
                                                if (Pref.isOrderStatusRequired) {
                                                    AppUtils.saveSharedPreferencesOrderStatusRequired(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesOrderStatusRequired(this, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsCurrentStockEnable", ignoreCase = true)) {
                                                Pref.isCurrentStockEnable = response.getconfigure!![i].Value == "1"

                                                if (Pref.isCurrentStockEnable) {
                                                    AppUtils.saveSharedPreferencesCurrentStock(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesCurrentStock(this, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsCurrentStockApplicableforAll", ignoreCase = true)) {
                                                Pref.IsCurrentStockApplicableforAll = response.getconfigure!![i].Value == "1"

                                                if (Pref.IsCurrentStockApplicableforAll) {
                                                    AppUtils.saveSharedPreferencesCurrentStockApplicableForAll(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesCurrentStockApplicableForAll(this, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IscompetitorStockRequired", ignoreCase = true)) {
                                                Pref.IscompetitorStockRequired = response.getconfigure!![i].Value == "1"

                                                if (Pref.IscompetitorStockRequired) {
                                                    AppUtils.saveSharedPreferencesIscompetitorStockRequired(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIscompetitorStockRequired(this, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsCompetitorStockforParty", ignoreCase = true)) {
                                                Pref.IsCompetitorStockforParty = response.getconfigure!![i].Value == "1"

                                                if (Pref.IsCompetitorStockforParty) {
                                                    AppUtils.saveSharedPreferencesIsCompetitorStockforParty(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsCompetitorStockforParty(this, false)
                                                }
                                            }
//                                            else if(response.getconfigure!![i].Key.equals("IsFaceDetectionOn", ignoreCase = true)){
                                            else if (response.getconfigure!![i].Key.equals("ShowFaceRegInMenu", ignoreCase = true)) {
                                                Pref.IsFaceDetectionOn = response.getconfigure!![i].Value == "1"
                                                if (Pref.IsFaceDetectionOn) {
                                                    AppUtils.saveSharedPreferencesIsFaceDetectionOn(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsFaceDetectionOn(this, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsFaceDetection", ignoreCase = true)) {
                                                Pref.IsFaceDetection = response.getconfigure!![i].Value == "1"
                                                if (Pref.IsFaceDetection) {
                                                    AppUtils.saveSharedPreferencesIsFaceDetection(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsFaceDetection(this, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsFaceDetectionWithCaptcha", ignoreCase = true)) {
                                                Pref.IsFaceDetectionWithCaptcha = response.getconfigure!![i].Value == "1"
                                                if (Pref.IsFaceDetectionWithCaptcha) {
                                                    AppUtils.saveSharedPreferencesIsFaceDetectionWithCaptcha(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsFaceDetectionWithCaptcha(this, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsScreenRecorderEnable", ignoreCase = true)) {
                                                Pref.IsScreenRecorderEnable = response.getconfigure!![i].Value == "1"
                                                if (Pref.IsScreenRecorderEnable) {
                                                    AppUtils.saveSharedPreferencesIsScreenRecorderEnable(this, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsScreenRecorderEnable(this, false)
                                                }
                                            }
//                                            else if (response.getconfigure?.get(i)?.Key.equals("IsFromPortal", ignoreCase = true)) {
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsDocRepoFromPortal", ignoreCase = true)) {
                                                Pref.IsFromPortal = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsFromPortal = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsDocRepShareDownloadAllowed", ignoreCase = true)) {
                                                Pref.IsDocRepShareDownloadAllowed = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsDocRepShareDownloadAllowed = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuAddAttendance", ignoreCase = true)) {
                                                Pref.IsShowMenuAddAttendance = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuAddAttendance = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuAttendance", ignoreCase = true)) {
                                                Pref.IsShowMenuAttendance = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuAttendance = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuMIS Report", ignoreCase = true)) {
                                                Pref.IsShowMenuMIS_Report = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuMIS_Report = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuAnyDesk", ignoreCase = true)) {
                                                Pref.IsShowMenuAnyDesk = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuAnyDesk = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuPermission Info", ignoreCase = true)) {
                                                Pref.IsShowMenuPermission_Info = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuPermission_Info = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuScan QR Code", ignoreCase = true)) {
                                                Pref.IsShowMenuScan_QR_Code = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuScan_QR_Code = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuChat", ignoreCase = true)) {
                                                Pref.IsShowMenuChat = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuChat = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuWeather Details", ignoreCase = true)) {
                                                Pref.IsShowMenuWeather_Details = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuWeather_Details = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuHome Location", ignoreCase = true)) {
                                                Pref.IsShowMenuHome_Location = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuHome_Location = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuShare Location", ignoreCase = true)) {
                                                Pref.IsShowMenuShare_Location = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuShare_Location = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuMap View", ignoreCase = true)) {
                                                Pref.IsShowMenuMap_View = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuMap_View = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuReimbursement", ignoreCase = true)) {
                                                Pref.IsShowMenuReimbursement = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuReimbursement = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuOutstanding Details PP/DD", ignoreCase = true)) {
                                                Pref.IsShowMenuOutstanding_Details_PP_DD = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuOutstanding_Details_PP_DD = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowMenuStock Details - PP/DD", ignoreCase = true)) {
                                                Pref.IsShowMenuStock_Details_PP_DD = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMenuStock_Details_PP_DD = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsLeaveGPSTrack", ignoreCase = true)) {
                                                Pref.IsLeaveGPSTrack = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsLeaveGPSTrack = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowActivitiesInTeam", ignoreCase = true)) {
                                                Pref.IsShowActivitiesInTeam = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowActivitiesInTeam = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            // Added setting Login 12-08-21
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsShowPartyOnAppDashboard", ignoreCase = true)) {
                                                Pref.IsShowPartyOnAppDashboard = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowPartyOnAppDashboard = response.getconfigure?.get(i)?.Value == "1"
                                                }

                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowAttendanceOnAppDashboard", ignoreCase = true)) {
                                                Pref.IsShowAttendanceOnAppDashboard = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowAttendanceOnAppDashboard = response.getconfigure?.get(i)?.Value == "1"
                                                }

                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowTotalVisitsOnAppDashboard", ignoreCase = true)) {
                                                Pref.IsShowTotalVisitsOnAppDashboard = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowTotalVisitsOnAppDashboard = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowVisitDurationOnAppDashboard", ignoreCase = true)) {
                                                Pref.IsShowVisitDurationOnAppDashboard = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowVisitDurationOnAppDashboard = response.getconfigure?.get(i)?.Value == "1"
                                                }

                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowDayStart", ignoreCase = true)) {
                                                Pref.IsShowDayStart = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowDayStart = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                                //Pref.IsShowDayStart = true
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsshowDayStartSelfie", ignoreCase = true)) {
                                                Pref.IsshowDayStartSelfie = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsshowDayStartSelfie = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                                //Pref.IsshowDayStartSelfie = true
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowDayEnd", ignoreCase = true)) {
                                                Pref.IsShowDayEnd = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowDayEnd = response.getconfigure?.get(i)?.Value == "1"
                                                }

                                                //Pref.IsShowDayEnd = true
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsshowDayEndSelfie", ignoreCase = true)) {
                                                Pref.IsshowDayEndSelfie = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsshowDayEndSelfie = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                                //Pref.IsshowDayEndSelfie = true
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowLeaveInAttendance", ignoreCase = true)) {
                                                Pref.IsShowLeaveInAttendance = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowLeaveInAttendance = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            //19-08-21
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsShowMarkDistVisitOnDshbrd", ignoreCase = true)) {
                                                Pref.IsShowMarkDistVisitOnDshbrd = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowMarkDistVisitOnDshbrd = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsActivateNewOrderScreenwithSize", ignoreCase = true)) {
                                                Pref.IsActivateNewOrderScreenwithSize = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsActivateNewOrderScreenwithSize = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsPhotoDeleteShow", ignoreCase = true)) {
                                                Pref.IsPhotoDeleteShow = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsPhotoDeleteShow = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            /*28-09-2021 For Gupta Power*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("RevisitRemarksMandatory", ignoreCase = true)) {
                                                Pref.RevisitRemarksMandatory = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.RevisitRemarksMandatory = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("GPSAlert", ignoreCase = true)) {
                                                Pref.GPSAlert = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.GPSAlert = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("GPSAlertwithSound", ignoreCase = true)) {
                                                Pref.GPSAlertwithSound = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.GPSAlertwithSound = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            /*29-10-2021 Team Attendance*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsTeamAttendance", ignoreCase = true)) {
                                                Pref.IsTeamAttendance = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsTeamAttendance = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            /*24-11-2021 ITC face And Distributoraccu*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("FaceDetectionAccuracyUpper")) {
                                                Pref.FaceDetectionAccuracyUpper = response.getconfigure!![i].Value!!
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.FaceDetectionAccuracyUpper = response.getconfigure?.get(i)?.Value!!
                                                }
                                                CustomStatic.FaceDetectionAccuracyUpper = Pref.FaceDetectionAccuracyUpper
                                            } else if (response.getconfigure?.get(i)?.Key.equals("FaceDetectionAccuracyLower")) {
                                                Pref.FaceDetectionAccuracyLower = response.getconfigure!![i].Value!!
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.FaceDetectionAccuracyLower = response.getconfigure?.get(i)?.Value!!
                                                }
                                                CustomStatic.FaceDetectionAccuracyLower = Pref.FaceDetectionAccuracyLower
                                            } else if (response.getconfigure?.get(i)?.Key.equals("DistributorGPSAccuracy")) {
                                                Pref.DistributorGPSAccuracy = response.getconfigure!![i].Value!!
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.DistributorGPSAccuracy = response.getconfigure?.get(i)?.Value!!
                                                }
                                                if (Pref.DistributorGPSAccuracy.length == 0 || Pref.DistributorGPSAccuracy.equals("")) {
                                                    Pref.DistributorGPSAccuracy = "500"
                                                }
                                                XLog.d("DistributorGPSAccuracy " + Pref.DistributorGPSAccuracy)
                                            }


                                            /*26-10-2021*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("BatterySetting", ignoreCase = true)) {
                                                Pref.BatterySetting = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.BatterySetting = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("PowerSaverSetting", ignoreCase = true)) {
                                                Pref.PowerSaverSetting = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.PowerSaverSetting = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            /*16-12-2021*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsReturnEnableforParty", ignoreCase = true)) {
                                                Pref.IsReturnEnableforParty = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsReturnEnableforParty = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            /*06-01-2022*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("MRPInOrder", ignoreCase = true)) {
                                                Pref.MRPInOrder = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.MRPInOrder = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("FaceRegistrationFrontCamera", ignoreCase = true)) {
                                                Pref.FaceRegistrationFrontCamera = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.FaceRegistrationFrontCamera = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                                /*18-01-2022*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("IslandlineforCustomer", ignoreCase = true)) {
                                                Pref.IslandlineforCustomer = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IslandlineforCustomer = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure?.get(i)?.Key.equals("IsprojectforCustomer", ignoreCase = true)) {
                                                Pref.IsprojectforCustomer = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsprojectforCustomer = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure?.get(i)?.Key.equals("Leaveapprovalfromsupervisor", ignoreCase = true)) {
                                                Pref.Leaveapprovalfromsupervisor = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.Leaveapprovalfromsupervisor = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure?.get(i)?.Key.equals("Leaveapprovalfromsupervisorinteam", ignoreCase = true)) {
                                                Pref.Leaveapprovalfromsupervisorinteam = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.Leaveapprovalfromsupervisorinteam = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsRestrictNearbyGeofence", ignoreCase = true)) {
                                                Pref.IsRestrictNearbyGeofence = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsRestrictNearbyGeofence = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            /*07-02-2022*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsNewQuotationfeatureOn", ignoreCase = true)) {
                                                Pref.IsNewQuotationfeatureOn = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsNewQuotationfeatureOn = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            /*10-02-2022*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsAlternateNoForCustomer", ignoreCase = true)) {
                                                Pref.IsAlternateNoForCustomer = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsAlternateNoForCustomer = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsWhatsappNoForCustomer", ignoreCase = true)) {
                                                Pref.IsWhatsappNoForCustomer = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsWhatsappNoForCustomer = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                           /*17-02-2022*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsNewQuotationNumberManual", ignoreCase = true)) {
                                                Pref.IsNewQuotationNumberManual = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsNewQuotationNumberManual = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("ShowQuantityNewQuotation", ignoreCase = true)) {
                                                Pref.ShowQuantityNewQuotation = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowQuantityNewQuotation = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("ShowAmountNewQuotation", ignoreCase = true)) {
                                                Pref.ShowAmountNewQuotation = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowQuantityNewQuotation = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }


                                            else if (response.getconfigure?.get(i)?.Key.equals("ShowUserwiseLeadMenu", ignoreCase = true)) {
                                                Pref.ShowUserwiseLeadMenu = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowUserwiseLeadMenu = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("GeofencingRelaxationinMeter", ignoreCase = true)) {
                                                try{
                                                    Pref.GeofencingRelaxationinMeter = response.getconfigure!![i].Value!!.toInt()
                                                    if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                        Pref.GeofencingRelaxationinMeter = response.getconfigure!![i].Value!!.toInt()
                                                    }
                                                }catch(ex:Exception){
                                                    Pref.GeofencingRelaxationinMeter = 100
                                                }
                                            }else if (response.getconfigure?.get(i)?.Key.equals("IsFeedbackHistoryActivated", ignoreCase = true)) {
                                                    Pref.IsFeedbackHistoryActivated = response.getconfigure!![i].Value == "1"
                                                    if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                        Pref.IsFeedbackHistoryActivated = response.getconfigure?.get(i)?.Value == "1"
                                                    }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsAutoLeadActivityDateTime", ignoreCase = true)) {
                                                Pref.IsAutoLeadActivityDateTime = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsAutoLeadActivityDateTime = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }else if (response.getconfigure?.get(i)?.Key.equals("LogoutWithLogFile", ignoreCase = true)) {
                                                Pref.LogoutWithLogFile = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.LogoutWithLogFile = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure?.get(i)?.Key.equals("ShowCollectionAlert", ignoreCase = true)) {
                                                Pref.ShowCollectionAlert = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowCollectionAlert = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("ShowZeroCollectioninAlert", ignoreCase = true)) {
                                                Pref.ShowZeroCollectioninAlert = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowZeroCollectioninAlert = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                             else if (response.getconfigure?.get(i)?.Key.equals("IsCollectionOrderWise", ignoreCase = true)) {
                                                Pref.IsCollectionOrderWise = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsCollectionOrderWise = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                              else if (response.getconfigure?.get(i)?.Key.equals("ShowCollectionOnlywithInvoiceDetails", ignoreCase = true)) {
                                                Pref.ShowCollectionOnlywithInvoiceDetails = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowCollectionOnlywithInvoiceDetails = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure?.get(i)?.Key.equals("IsPendingCollectionRequiredUnderTeam", ignoreCase = true)) {
                                                Pref.IsPendingCollectionRequiredUnderTeam = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsPendingCollectionRequiredUnderTeam = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure?.get(i)?.Key.equals("IsShowRepeatOrderinNotification", ignoreCase = true)) {
                                                Pref.IsShowRepeatOrderinNotification = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowRepeatOrderinNotification = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsShowRepeatOrdersNotificationinTeam", ignoreCase = true)) {
                                                Pref.IsShowRepeatOrdersNotificationinTeam = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowRepeatOrdersNotificationinTeam = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure?.get(i)?.Key.equals("AutoDDSelect", ignoreCase = true)) {
                                                Pref.AutoDDSelect = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.AutoDDSelect = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("ShowPurposeInShopVisit", ignoreCase = true)) {
                                                Pref.ShowPurposeInShopVisit = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowPurposeInShopVisit = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure?.get(i)?.Key.equals("GPSAlertwithVibration", ignoreCase = true)) {
                                                Pref.GPSAlertwithVibration = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.GPSAlertwithVibration = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("WillRoomDBShareinLogin", ignoreCase = true)) {
                                                Pref.WillRoomDBShareinLogin = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.WillRoomDBShareinLogin = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("ShopScreenAftVisitRevisit", ignoreCase = true)) {
                                                Pref.ShopScreenAftVisitRevisit = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShopScreenAftVisitRevisit = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }else if (response.getconfigure!![i].Key.equals("IsShowNearByTeam", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowNearByTeam = response.getconfigure!![i].Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure!![i].Key.equals("IsFeedbackAvailableInShop", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsFeedbackAvailableInShop = response.getconfigure!![i].Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure!![i].Key.equals("IsAllowBreakageTracking", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsAllowBreakageTracking = response.getconfigure!![i].Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure!![i].Key.equals("IsAllowBreakageTrackingunderTeam", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsAllowBreakageTrackingunderTeam = response.getconfigure!![i].Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure!![i].Key.equals("IsRateEnabledforNewOrderScreenwithSize", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsRateEnabledforNewOrderScreenwithSize = response.getconfigure!![i].Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure!![i].Key.equals("IgnoreNumberCheckwhileShopCreation", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IgnoreNumberCheckwhileShopCreation = response.getconfigure!![i].Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure!![i].Key.equals("Showdistributorwisepartyorderreport", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.Showdistributorwisepartyorderreport = response.getconfigure!![i].Value == "1"
                                                }
                                            }

                                            else if (response.getconfigure?.get(i)?.Key.equals("IsShowHomeLocationMap", ignoreCase = true)) {
                                                Pref.IsShowHomeLocationMap = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowHomeLocationMap =
                                                        response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("ShowAttednaceClearmenu", ignoreCase = true)) {
                                                Pref.ShowAttednaceClearmenu = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowAttednaceClearmenu= response.getconfigure?.get(i)?.Value == "1"
                                                }

                                            }else if (response.getconfigure?.get(i)?.Key.equals("IsBeatRouteReportAvailableinTeam", ignoreCase = true)) {
                                                Pref.IsBeatRouteReportAvailableinTeam = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsBeatRouteReportAvailableinTeam= response.getconfigure?.get(i)?.Value == "1"
                                                }

                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("GPSNetworkIntervalMins", ignoreCase = true)) {
                                                try{
                                                    Pref.GPSNetworkIntervalMins =response.getconfigure!![i].Value!!
                                                }catch (e: Exception) {
                                                    e.printStackTrace()
                                                    Pref.GPSNetworkIntervalMins = "0"
                                                }
                                            }






                                            /*else if (response.getconfigure?.get(i)?.Key.equals("isFingerPrintMandatoryForAttendance", ignoreCase = true)) {
                                                if (!TextUtilsDash.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isFingerPrintMandatoryForAttendance = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("isFingerPrintMandatoryForVisit", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isFingerPrintMandatoryForVisit = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("isSelfieMandatoryForAttendance", ignoreCase = true)) {
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.isSelfieMandatoryForAttendance = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }*/
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            Log.e("Login", "willLeaveApprovalEnable================> " + Pref.willLeaveApprovalEnable)
                            println("xyz - callUserConfigApi ended" + AppUtils.getCurrentDateTime());
                            progress_wheel.stopSpinning()
                            getConfigFetchApi()

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            getConfigFetchApi()
                        })
        )
    }


    private fun getSelectedRouteList() {
        println("xyz - getSelectedRouteList started" + AppUtils.getCurrentDateTime());
        val repository = GetRouteListRepoProvider.routeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.routeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as SelectedRouteListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val workTypeList = response.worktype

                                if (workTypeList != null && workTypeList.isNotEmpty()) {

                                    doAsync {

                                        for (i in workTypeList.indices) {
                                            val selectedwortkType = SelectedWorkTypeEntity()
                                            selectedwortkType.ID = workTypeList[i].id?.toInt()!!
                                            selectedwortkType.Descrpton = workTypeList[i].name
                                            selectedwortkType.date = AppUtils.getCurrentDate()
                                            AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.insertAll(selectedwortkType)
                                        }

                                        val routeList = response.route_list
                                        if (routeList != null && routeList.isNotEmpty()) {
                                            for (i in routeList.indices) {
                                                val selectedRoute = SelectedRouteEntity()
                                                selectedRoute.route_id = routeList[i].id
                                                selectedRoute.route_name = routeList[i].route_name
                                                selectedRoute.date = AppUtils.getCurrentDate()

                                                val routeShopList = routeList[i].shop_details_list
                                                if (routeShopList != null && routeShopList.size > 0) {
                                                    for (j in routeShopList.indices) {
                                                        val selectedRouteShop = SelectedRouteShopListEntity()
                                                        selectedRouteShop.route_id = routeList[i].id
                                                        selectedRouteShop.shop_address = routeShopList[j].shop_address
                                                        selectedRouteShop.shop_contact_no = routeShopList[j].shop_contact_no
                                                        selectedRouteShop.shop_name = routeShopList[j].shop_name
                                                        selectedRouteShop.shop_id = routeShopList[j].shop_id
                                                        selectedRouteShop.date = AppUtils.getCurrentDate()
                                                        AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.insert(selectedRouteShop)
                                                    }
                                                }

                                                AppDatabase.getDBInstance()?.selectedRouteListDao()?.insert(selectedRoute)
                                            }
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            //callUserConfigApi()
                                            checkToCallCollectionApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    //callUserConfigApi()
                                    checkToCallCollectionApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                //callUserConfigApi()
                                checkToCallCollectionApi()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            //callUserConfigApi()
                            checkToCallCollectionApi()
                        })
        )
    }

    private fun convertToShopListSetAdapter(shop_list: List<ShopData>) {
        val list: MutableList<AddShopDBModelEntity> = ArrayList()

        for (i in 0 until shop_list.size) {
            val shopObj = AddShopDBModelEntity()
            shopObj.shop_id = shop_list[i].shop_id
            shopObj.shopName = shop_list[i].shop_name
            shopObj.shopImageLocalPath = shop_list[i].Shop_Image
            shopObj.shopLat = shop_list[i].shop_lat!!.toDouble()
            shopObj.shopLong = shop_list[i].shop_long!!.toDouble()
            shopObj.duration = ""
            shopObj.endTimeStamp = ""
            shopObj.timeStamp = ""
            shopObj.dateOfBirth = shop_list[i].dob
            shopObj.dateOfAniversary = shop_list[i].date_aniversary
            shopObj.visitDate = AppUtils.getCurrentDate()
            if (shop_list[i].total_visit_count == "0")
                shopObj.totalVisitCount = "1"
            else
                shopObj.totalVisitCount = shop_list[i].total_visit_count
            shopObj.address = shop_list[i].address
            shopObj.ownerEmailId = shop_list[i].owner_email
            shopObj.ownerContactNumber = shop_list[i].owner_contact_no
            shopObj.pinCode = shop_list[i].pin_code
            shopObj.isUploaded = true
            shopObj.ownerName = shop_list[i].owner_name
            shopObj.user_id = Pref.user_id
            shopObj.orderValue = 0
            shopObj.type = shop_list[i].type
            shopObj.assigned_to_dd_id = shop_list[i].assigned_to_dd_id
            shopObj.assigned_to_pp_id = shop_list[i].assigned_to_pp_id
            shopObj.isAddressUpdated = shop_list[i].isAddressUpdated == "1"
            shopObj.is_otp_verified = shop_list[i].is_otp_verified
            shopObj.added_date = shop_list[i].added_date

            if (shop_list[i].amount == null || shop_list[i].amount == "0.00")
                shopObj.amount = ""
            else
                shopObj.amount = shop_list[i].amount

            if (shop_list[i].last_visit_date!!.contains("."))
                shopObj.lastVisitedDate = AppUtils.changeAttendanceDateFormat(shop_list[i].last_visit_date!!.split(".")[0])
            else
                shopObj.lastVisitedDate = AppUtils.changeAttendanceDateFormat(shop_list[i].last_visit_date!!)

            if (shopObj.lastVisitedDate == AppUtils.getCurrentDateChanged())
                shopObj.visited = true
            else
                shopObj.visited = false

            if (shop_list[i].entity_code == null)
                shopObj.entity_code = ""
            else
                shopObj.entity_code = shop_list[i].entity_code


            if (shop_list[i].area_id == null)
                shopObj.area_id = ""
            else
                shopObj.area_id = shop_list[i].area_id

            if (TextUtils.isEmpty(shop_list[i].model_id))
                shopObj.model_id = ""
            else
                shopObj.model_id = shop_list[i].model_id

            if (TextUtils.isEmpty(shop_list[i].primary_app_id))
                shopObj.primary_app_id = ""
            else
                shopObj.primary_app_id = shop_list[i].primary_app_id

            if (TextUtils.isEmpty(shop_list[i].secondary_app_id))
                shopObj.secondary_app_id = ""
            else
                shopObj.secondary_app_id = shop_list[i].secondary_app_id

            if (TextUtils.isEmpty(shop_list[i].lead_id))
                shopObj.lead_id = ""
            else
                shopObj.lead_id = shop_list[i].lead_id

            if (TextUtils.isEmpty(shop_list[i].stage_id))
                shopObj.stage_id = ""
            else
                shopObj.stage_id = shop_list[i].stage_id

            if (TextUtils.isEmpty(shop_list[i].funnel_stage_id))
                shopObj.funnel_stage_id = ""
            else
                shopObj.funnel_stage_id = shop_list[i].funnel_stage_id

            if (TextUtils.isEmpty(shop_list[i].booking_amount))
                shopObj.booking_amount = ""
            else
                shopObj.booking_amount = shop_list[i].booking_amount

            if (TextUtils.isEmpty(shop_list[i].type_id))
                shopObj.type_id = ""
            else
                shopObj.type_id = shop_list[i].type_id

            shopObj.family_member_dob = shop_list[i].family_member_dob
            shopObj.director_name = shop_list[i].director_name
            shopObj.person_name = shop_list[i].key_person_name
            shopObj.person_no = shop_list[i].phone_no
            shopObj.add_dob = shop_list[i].addtional_dob
            shopObj.add_doa = shop_list[i].addtional_doa

            shopObj.doc_degree = shop_list[i].degree
            shopObj.doc_family_dob = shop_list[i].doc_family_member_dob
            shopObj.specialization = shop_list[i].specialization
            shopObj.patient_count = shop_list[i].average_patient_per_day
            shopObj.category = shop_list[i].category
            shopObj.doc_address = shop_list[i].doc_address
            shopObj.doc_pincode = shop_list[i].doc_pincode
            shopObj.chamber_status = shop_list[i].is_chamber_same_headquarter.toInt()
            shopObj.remarks = shop_list[i].is_chamber_same_headquarter_remarks
            shopObj.chemist_name = shop_list[i].chemist_name
            shopObj.chemist_address = shop_list[i].chemist_address
            shopObj.chemist_pincode = shop_list[i].chemist_pincode
            shopObj.assistant_name = shop_list[i].assistant_name
            shopObj.assistant_no = shop_list[i].assistant_contact_no
            shopObj.assistant_dob = shop_list[i].assistant_dob
            shopObj.assistant_doa = shop_list[i].assistant_doa
            shopObj.assistant_family_dob = shop_list[i].assistant_family_dob

            if (TextUtils.isEmpty(shop_list[i].entity_id))
                shopObj.entity_id = ""
            else
                shopObj.entity_id = shop_list[i].entity_id

            if (TextUtils.isEmpty(shop_list[i].party_status_id))
                shopObj.party_status_id = ""
            else
                shopObj.party_status_id = shop_list[i].party_status_id

            if (TextUtils.isEmpty(shop_list[i].retailer_id))
                shopObj.retailer_id = ""
            else
                shopObj.retailer_id = shop_list[i].retailer_id

            if (TextUtils.isEmpty(shop_list[i].dealer_id))
                shopObj.dealer_id = ""
            else
                shopObj.dealer_id = shop_list[i].dealer_id

            if (TextUtils.isEmpty(shop_list[i].beat_id))
                shopObj.beat_id = ""
            else
                shopObj.beat_id = shop_list[i].beat_id

            if (TextUtils.isEmpty(shop_list[i].account_holder))
                shopObj.account_holder = ""
            else
                shopObj.account_holder = shop_list[i].account_holder

            if (TextUtils.isEmpty(shop_list[i].account_no))
                shopObj.account_no = ""
            else
                shopObj.account_no = shop_list[i].account_no

            if (TextUtils.isEmpty(shop_list[i].bank_name))
                shopObj.bank_name = ""
            else
                shopObj.bank_name = shop_list[i].bank_name

            if (TextUtils.isEmpty(shop_list[i].ifsc_code))
                shopObj.ifsc_code = ""
            else
                shopObj.ifsc_code = shop_list[i].ifsc_code

            if (TextUtils.isEmpty(shop_list[i].upi))
                shopObj.upi_id = ""
            else
                shopObj.upi_id = shop_list[i].upi

            if (TextUtils.isEmpty(shop_list[i].assigned_to_shop_id))
                shopObj.assigned_to_shop_id = ""
            else
                shopObj.assigned_to_shop_id = shop_list[i].assigned_to_shop_id

            shopObj.project_name=shop_list[i].project_name
            shopObj.landline_number=shop_list[i].landline_number
            shopObj.agency_name=shop_list[i].agency_name
            /*10-2-2022*/
            shopObj.alternateNoForCustomer=shop_list[i].alternateNoForCustomer
            shopObj.whatsappNoForCustomer=shop_list[i].whatsappNoForCustomer
            shopObj.isShopDuplicate=shop_list[i].isShopDuplicate

            shopObj.purpose=shop_list[i].purpose

            /*GSTIN & PAN NUMBER*/
            shopObj.gstN_Number=shop_list[i].GSTN_Number
            shopObj.shopOwner_PAN=shop_list[i].ShopOwner_PAN


            list.add(shopObj)
            AppDatabase.getDBInstance()!!.addShopEntryDao().insert(shopObj)
        }
        progress_wheel.stopSpinning()


        val stockList = AppDatabase.getDBInstance()!!.stockListDao().getAll()
        if (stockList == null || stockList.isEmpty()) {
            callStockListApi()
        } else {
            checkToCallAssignedDDListApi()
        }
    }


    private fun convertToShopListTeamSetAdapter(shop_list: List<ShopData>) {
        val list: MutableList<AddShopDBModelEntity> = ArrayList()

        for (i in 0 until shop_list.size) {

            val listCheck = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shop_list[i].shop_id) as ArrayList<AddShopDBModelEntity>
            if(listCheck.size == 0){
                val shopObj = AddShopDBModelEntity()
                shopObj.shop_id = shop_list[i].shop_id
                shopObj.shopName = shop_list[i].shop_name
                shopObj.shopImageLocalPath = shop_list[i].Shop_Image
                shopObj.shopLat = shop_list[i].shop_lat!!.toDouble()
                shopObj.shopLong = shop_list[i].shop_long!!.toDouble()
                shopObj.duration = ""
                shopObj.endTimeStamp = ""
                shopObj.timeStamp = ""
                shopObj.dateOfBirth = shop_list[i].dob
                shopObj.dateOfAniversary = shop_list[i].date_aniversary
                shopObj.visitDate = AppUtils.getCurrentDate()
                if (shop_list[i].total_visit_count == "0")
                    shopObj.totalVisitCount = "1"
                else
                    shopObj.totalVisitCount = shop_list[i].total_visit_count
                shopObj.address = shop_list[i].address
                shopObj.ownerEmailId = shop_list[i].owner_email
                shopObj.ownerContactNumber = shop_list[i].owner_contact_no
                shopObj.pinCode = shop_list[i].pin_code
                shopObj.isUploaded = true
                shopObj.ownerName = shop_list[i].owner_name
                shopObj.user_id = Pref.user_id
                shopObj.orderValue = 0
                shopObj.type = shop_list[i].type
                shopObj.assigned_to_dd_id = shop_list[i].assigned_to_dd_id
                shopObj.assigned_to_pp_id = shop_list[i].assigned_to_pp_id
                shopObj.isAddressUpdated = shop_list[i].isAddressUpdated == "1"
                shopObj.is_otp_verified = shop_list[i].is_otp_verified
                shopObj.added_date = shop_list[i].added_date

                if (shop_list[i].amount == null || shop_list[i].amount == "0.00")
                    shopObj.amount = ""
                else
                    shopObj.amount = shop_list[i].amount

                if (shop_list[i].last_visit_date!!.contains("."))
                    shopObj.lastVisitedDate = AppUtils.changeAttendanceDateFormat(shop_list[i].last_visit_date!!.split(".")[0])
                else
                    shopObj.lastVisitedDate = AppUtils.changeAttendanceDateFormat(shop_list[i].last_visit_date!!)

                if (shopObj.lastVisitedDate == AppUtils.getCurrentDateChanged())
                    shopObj.visited = true
                else
                    shopObj.visited = false

                if (shop_list[i].entity_code == null)
                    shopObj.entity_code = ""
                else
                    shopObj.entity_code = shop_list[i].entity_code


                if (shop_list[i].area_id == null)
                    shopObj.area_id = ""
                else
                    shopObj.area_id = shop_list[i].area_id

                if (TextUtils.isEmpty(shop_list[i].model_id))
                    shopObj.model_id = ""
                else
                    shopObj.model_id = shop_list[i].model_id

                if (TextUtils.isEmpty(shop_list[i].primary_app_id))
                    shopObj.primary_app_id = ""
                else
                    shopObj.primary_app_id = shop_list[i].primary_app_id

                if (TextUtils.isEmpty(shop_list[i].secondary_app_id))
                    shopObj.secondary_app_id = ""
                else
                    shopObj.secondary_app_id = shop_list[i].secondary_app_id

                if (TextUtils.isEmpty(shop_list[i].lead_id))
                    shopObj.lead_id = ""
                else
                    shopObj.lead_id = shop_list[i].lead_id

                if (TextUtils.isEmpty(shop_list[i].stage_id))
                    shopObj.stage_id = ""
                else
                    shopObj.stage_id = shop_list[i].stage_id

                if (TextUtils.isEmpty(shop_list[i].funnel_stage_id))
                    shopObj.funnel_stage_id = ""
                else
                    shopObj.funnel_stage_id = shop_list[i].funnel_stage_id

                if (TextUtils.isEmpty(shop_list[i].booking_amount))
                    shopObj.booking_amount = ""
                else
                    shopObj.booking_amount = shop_list[i].booking_amount

                if (TextUtils.isEmpty(shop_list[i].type_id))
                    shopObj.type_id = ""
                else
                    shopObj.type_id = shop_list[i].type_id

                shopObj.family_member_dob = shop_list[i].family_member_dob
                shopObj.director_name = shop_list[i].director_name
                shopObj.person_name = shop_list[i].key_person_name
                shopObj.person_no = shop_list[i].phone_no
                shopObj.add_dob = shop_list[i].addtional_dob
                shopObj.add_doa = shop_list[i].addtional_doa

                shopObj.doc_degree = shop_list[i].degree
                shopObj.doc_family_dob = shop_list[i].doc_family_member_dob
                shopObj.specialization = shop_list[i].specialization
                shopObj.patient_count = shop_list[i].average_patient_per_day
                shopObj.category = shop_list[i].category
                shopObj.doc_address = shop_list[i].doc_address
                shopObj.doc_pincode = shop_list[i].doc_pincode
                shopObj.chamber_status = shop_list[i].is_chamber_same_headquarter.toInt()
                shopObj.remarks = shop_list[i].is_chamber_same_headquarter_remarks
                shopObj.chemist_name = shop_list[i].chemist_name
                shopObj.chemist_address = shop_list[i].chemist_address
                shopObj.chemist_pincode = shop_list[i].chemist_pincode
                shopObj.assistant_name = shop_list[i].assistant_name
                shopObj.assistant_no = shop_list[i].assistant_contact_no
                shopObj.assistant_dob = shop_list[i].assistant_dob
                shopObj.assistant_doa = shop_list[i].assistant_doa
                shopObj.assistant_family_dob = shop_list[i].assistant_family_dob

                if (TextUtils.isEmpty(shop_list[i].entity_id))
                    shopObj.entity_id = ""
                else
                    shopObj.entity_id = shop_list[i].entity_id

                if (TextUtils.isEmpty(shop_list[i].party_status_id))
                    shopObj.party_status_id = ""
                else
                    shopObj.party_status_id = shop_list[i].party_status_id

                if (TextUtils.isEmpty(shop_list[i].retailer_id))
                    shopObj.retailer_id = ""
                else
                    shopObj.retailer_id = shop_list[i].retailer_id

                if (TextUtils.isEmpty(shop_list[i].dealer_id))
                    shopObj.dealer_id = ""
                else
                    shopObj.dealer_id = shop_list[i].dealer_id

                if (TextUtils.isEmpty(shop_list[i].beat_id))
                    shopObj.beat_id = ""
                else
                    shopObj.beat_id = shop_list[i].beat_id

                if (TextUtils.isEmpty(shop_list[i].account_holder))
                    shopObj.account_holder = ""
                else
                    shopObj.account_holder = shop_list[i].account_holder

                if (TextUtils.isEmpty(shop_list[i].account_no))
                    shopObj.account_no = ""
                else
                    shopObj.account_no = shop_list[i].account_no

                if (TextUtils.isEmpty(shop_list[i].bank_name))
                    shopObj.bank_name = ""
                else
                    shopObj.bank_name = shop_list[i].bank_name

                if (TextUtils.isEmpty(shop_list[i].ifsc_code))
                    shopObj.ifsc_code = ""
                else
                    shopObj.ifsc_code = shop_list[i].ifsc_code

                if (TextUtils.isEmpty(shop_list[i].upi))
                    shopObj.upi_id = ""
                else
                    shopObj.upi_id = shop_list[i].upi

                if (TextUtils.isEmpty(shop_list[i].assigned_to_shop_id))
                    shopObj.assigned_to_shop_id = ""
                else
                    shopObj.assigned_to_shop_id = shop_list[i].assigned_to_shop_id

                shopObj.project_name=shop_list[i].project_name
                shopObj.landline_number=shop_list[i].landline_number
                shopObj.agency_name=shop_list[i].agency_name
                /*10-2-2022*/
                shopObj.alternateNoForCustomer=shop_list[i].alternateNoForCustomer
                shopObj.whatsappNoForCustomer=shop_list[i].whatsappNoForCustomer
                shopObj.isShopDuplicate=shop_list[i].isShopDuplicate

                shopObj.purpose=shop_list[i].purpose
                shopObj.isOwnshop = false

                list.add(shopObj)

                AppDatabase.getDBInstance()!!.addShopEntryDao().insert(shopObj)
            }
        }
        progress_wheel.stopSpinning()

        gotoHomeActivity()
//
//        val stockList = AppDatabase.getDBInstance()!!.stockListDao().getAll()
//        if (stockList == null || stockList.isEmpty()) {
//            callStockListApi()
//        } else {
//            checkToCallAssignedDDListApi()
//        }
    }

    private fun checkToCallAssignedDDListApi() {
        val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
        if (/*(assignDDList == null || assignDDList.isEmpty()) && */!TextUtils.isEmpty(Pref.profile_state))
            getAssignedDDListApi()
        else {
            if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                getProductList("")
            else
                getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
        }
    }

    private fun callStockListApi() {
        val repository = StockListRepoProvider.stockListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.stockList(Pref.session_token!!, Pref.user_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val stockList = result as StockListResponseModel
                            if (stockList.status == NetworkConstant.SUCCESS) {

                                doAsync {
                                    saveValueToDb(stockList.stock_list)

                                    uiThread {
                                        progress_wheel.stopSpinning()

                                        checkToCallAssignedDDListApi()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()

                                checkToCallAssignedDDListApi()
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()

                            checkToCallAssignedDDListApi()
                        })
        )
    }

    private fun saveValueToDb(stockListData: ArrayList<StockListDataModel>?) {

        for (i in stockListData?.indices!!) {
            val updateStockList = StockListEntity()
            updateStockList.current_date = stockListData[i].stock_date
            updateStockList.mo = stockListData[i].m_o
            updateStockList.po = stockListData[i].p_o
            updateStockList.co = stockListData[i].c_o
            updateStockList.shop_id = stockListData[i].shop_id
            updateStockList.stock_value = stockListData[i].stock_value
            AppDatabase.getDBInstance()!!.stockListDao().insert(updateStockList)
        }
    }

    private fun getAssignedDDListApi() {
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
                                            val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                            if (/*(assignPPList == null || assignPPList.isEmpty()) && */!TextUtils.isEmpty(Pref.profile_state))
                                                getAssignedPPListApi()
                                            else {
                                                if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                                    getProductList("")
                                                else
                                                    getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                                            }
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                    if (/*(assignPPList == null || assignPPList.isEmpty()) && */!TextUtils.isEmpty(Pref.profile_state))
                                        getAssignedPPListApi()
                                    else {
                                        if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                            getProductList("")
                                        else
                                            getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                if (/*(assignPPList == null || assignPPList.isEmpty()) && */!TextUtils.isEmpty(Pref.profile_state))
                                    getAssignedPPListApi()
                                else {
                                    if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                        getProductList("")
                                    else
                                        getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                            if (/*(assignPPList == null || assignPPList.isEmpty()) && */!TextUtils.isEmpty(Pref.profile_state))
                                getAssignedPPListApi()
                            else {
                                if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                    getProductList("")
                                else
                                    getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                            }
                        })
        )
    }

    private fun getAssignedPPListApi() {
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
                                            if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                                getProductList("")
                                            else
                                                getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                        getProductList("")
                                    else
                                        getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                    getProductList("")
                                else
                                    getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                getProductList("")
                            else
                                getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                        })
        )
    }

    private fun saveDataInLocalDataBase(loginResponse: LoginResponse) {
        if (AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!, AppUtils.getCurrentDateChanged()).isEmpty()) {
            var userLoginDataEntity = UserLoginDataEntity()
            userLoginDataEntity.logindate = AppUtils.getCurrentDateChanged()
//            userLoginDataEntity.login_date= AppUtils.getCurrentDateInDate()
            userLoginDataEntity.logintime = AppUtils.getCurrentTimeWithMeredian()
            userLoginDataEntity.userId = loginResponse.user_details!!.user_id!!
            AppDatabase.getDBInstance()!!.userAttendanceDataDao().insertAll(userLoginDataEntity)
        }

    }


    private fun gotoHomeActivity() {
        login_TV.isEnabled = true

        progress_wheel.stopSpinning()
        setServiceAlarm(this, 1, 123)

        //AppDatabase.getDBInstance()?.shopActivityDao()?.trash2("2022-04-04","432_1879749092874","28")
        //AppDatabase.getDBInstance()!!.leadActivityDao().trash2("0d181797-65b8-4d96-929d-15a71ae16192","2022-04-05")

        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
        intent.putExtra("fromClass", "LoginActivity")
        overridePendingTransition(0, 0)
        finish()
        startActivity(intent)
    }


    fun getCurrentStockApi() {
        if (Pref.isCurrentStockEnable) {
            XLog.d("API_Optimization GET getCurrentStockApi Login : enable " + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            progress_wheel.spin()
            var shopAll = AppDatabase.getDBInstance()!!.shopCurrentStockEntryDao().getShopStockAll()
            if (shopAll != null && shopAll?.isNotEmpty()) {
                progress_wheel.stopSpinning()
                getCompStockApi()
            } else {
                try {
                    val repository = ShopAddStockProvider.provideShopAddStockRepository()
                    BaseActivity.compositeDisposable.add(
                            repository.getCurrStockList(Pref.session_token!!, Pref.user_id!!, "")
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        XLog.d("Login Stock/CurrentStockList " + result.status)
                                        val response = result as CurrentStockGetData
                                        if (response.status == NetworkConstant.SUCCESS) {
                                            if (response.stock_list!! != null && response.stock_list!!.isNotEmpty()) {
                                                doAsync {
                                                    for (i in response.stock_list?.indices!!) {
                                                        var obj = CurrentStockEntryModelEntity()
                                                        obj.user_id = Pref.user_id!!
                                                        obj.stock_id = response.stock_list?.get(i)?.stock_id!!
                                                        obj.shop_id = response.stock_list?.get(i)?.shop_id!!
                                                        obj.visited_datetime = response.stock_list?.get(i)?.visited_datetime!!
                                                        obj.visited_date = response.stock_list?.get(i)?.visited_datetime?.take(10)
                                                        obj.total_product_stock_qty = response.stock_list?.get(i)?.total_qty!!
                                                        obj.isUploaded = true
                                                        AppDatabase.getDBInstance()?.shopCurrentStockEntryDao()!!.insert(obj)

                                                        val proDuctList = response.stock_list?.get(i)?.product_list
                                                        for (j in proDuctList?.indices!!) {
                                                            var objjj = CurrentStockEntryProductModelEntity()
                                                            objjj.stock_id = response.stock_list?.get(i)?.stock_id!!
                                                            objjj.shop_id = response.stock_list?.get(i)?.shop_id!!
                                                            objjj.product_id = proDuctList?.get(j).product_id.toString()!!
                                                            objjj.product_stock_qty = proDuctList?.get(j).product_stock_qty!!
                                                            objjj.user_id = Pref.user_id
                                                            objjj.isUploaded = true

                                                            AppDatabase.getDBInstance()?.shopCurrentStockProductsEntryDao()!!.insert(objjj)
                                                        }
                                                    }
                                                    uiThread {
                                                        progress_wheel.stopSpinning()
                                                        getCompStockApi()
                                                    }
                                                }
                                            } else {
                                                progress_wheel.stopSpinning()
                                                getCompStockApi()
                                            }
                                        } else {
                                            progress_wheel.stopSpinning()
                                            getCompStockApi()
                                        }
                                    }, { error ->
                                        if (error == null) {
                                            XLog.d("Login Stock/CurrentStockList : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
                                        } else {
                                            XLog.d("Login Stock/CurrentStockList : ERROR " + error.localizedMessage)
                                            error.printStackTrace()
                                        }
                                        progress_wheel.stopSpinning()
                                        getCompStockApi()
                                    })
                    )
                } catch (ex: Exception) {
                    progress_wheel.stopSpinning()
                    XLog.d("Login Stock/CurrentStockList : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
                    getCompStockApi()
                }
            }
        }
        else{
            XLog.d("API_Optimization GET getCurrentStockApi Login : disable " + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            getCompStockApi()
        }
    }

    fun getCompStockApi() {
        if (Pref.IscompetitorStockRequired) {
            XLog.d("API_Optimization GET getCompStockApi Login : enable " + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            progress_wheel.spin()
            var comListAll = AppDatabase.getDBInstance()!!.competetorStockEntryDao().getCompetetorStockAll()
            if (comListAll != null && comListAll?.isNotEmpty()) {
                progress_wheel.stopSpinning()
                getShopTypeStockVisibility()
            } else {
                try {
                    val repository = AddCompStockProvider.provideCompStockRepositiry()
                    BaseActivity.compositeDisposable.add(
                            repository.getCompStockList(Pref.session_token!!, Pref.user_id!!, "")
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        XLog.d("Login CompetitorStock/CompetitorStockList : RESPONSE " + result.status)
                                        val response = result as CompetetorStockGetData
                                        if (response.status == NetworkConstant.SUCCESS) {
                                            if (response.competitor_stock_list!! != null && response.competitor_stock_list!!.isNotEmpty()) {
                                                doAsync {

                                                    for (i in response.competitor_stock_list?.indices!!) {
                                                        var obj = CcompetetorStockEntryModelEntity()
                                                        obj.user_id = Pref?.user_id!!
                                                        obj.competitor_stock_id = response.competitor_stock_list?.get(i)?.competitor_stock_id!!
                                                        obj.shop_id = response.competitor_stock_list?.get(i)?.shop_id!!
                                                        obj.visited_datetime = response.competitor_stock_list?.get(i)?.visited_datetime!!
                                                        obj.visited_date = response.competitor_stock_list?.get(i)?.visited_datetime?.take(10)
                                                        obj.total_product_stock_qty = response.competitor_stock_list?.get(i)?.total_qty!!
                                                        obj.isUploaded = true
                                                        AppDatabase.getDBInstance()?.competetorStockEntryDao()?.insert(obj)

                                                        val proDuctList = response.competitor_stock_list?.get(i)?.product_list
                                                        for (j in proDuctList?.indices!!) {
                                                            var objjj = CompetetorStockEntryProductModelEntity()
                                                            objjj.user_id = Pref.user_id
                                                            objjj.competitor_stock_id = response.competitor_stock_list?.get(i)?.competitor_stock_id!!
                                                            objjj.shop_id = response.competitor_stock_list?.get(i)?.shop_id!!

                                                            objjj.brand_name = proDuctList?.get(j)?.brand_name
                                                            objjj.product_name = proDuctList?.get(j)?.product_name
                                                            objjj.qty = proDuctList?.get(j)?.qty
                                                            objjj.mrp = proDuctList?.get(j)?.mrp
                                                            objjj.isUploaded = true
                                                            AppDatabase.getDBInstance()?.competetorStockEntryProductDao()?.insert(objjj)
                                                        }
                                                    }

                                                    uiThread {
                                                        progress_wheel.stopSpinning()
                                                        getShopTypeStockVisibility()
                                                    }
                                                }

                                            } else {
                                                progress_wheel.stopSpinning()
                                                getShopTypeStockVisibility()
                                            }
                                        } else {
                                            progress_wheel.stopSpinning()
                                            getShopTypeStockVisibility()
                                        }
                                    }, { error ->
                                        if (error == null) {
                                            XLog.d("Login CompetitorStock/CompetitorStockList : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
                                        } else {
                                            XLog.d("Login CompetitorStock/CompetitorStockList : ERROR " + error.localizedMessage)
                                            error.printStackTrace()
                                        }
                                        progress_wheel.stopSpinning()
                                        getShopTypeStockVisibility()
                                    })
                    )
                } catch (ex: Exception) {
                    progress_wheel.stopSpinning()
                    XLog.d("Login CompetitorStock/CompetitorStockList : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
                    getShopTypeStockVisibility()
                }
            }
        }
        else{
            XLog.d("API_Optimization GET getCompStockApi Login : disable " + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            getShopTypeStockVisibility()
        }
    }


    fun getShopTypeStockVisibility() {
        try {
            AppDatabase.getDBInstance()?.shopTypeStockViewStatusDao()?.deleteAll()
            val repository = ShopListRepositoryProvider.provideShopListRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.getShopTypeStockVisibilityList()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as ShopTypeStockViewResponseModel
                                if (response.status == NetworkConstant.SUCCESS) {
                                    val list = response.Shoptype_list

                                    if (list != null && list.isNotEmpty()) {
                                        doAsync {

                                            list.forEach {
                                                val shop = ShopTypeStockViewStatus()
                                                AppDatabase.getDBInstance()?.shopTypeStockViewStatusDao()?.insertAll(shop.apply {
                                                    shoptype_id = it.shoptype_id
                                                    shoptype_name = it.shoptype_name
                                                    CurrentStockEnable = it.CurrentStockEnable
                                                    CompetitorStockEnable = it.CompetitorStockEnable
                                                })
                                            }

                                            uiThread {
                                                progress_wheel.stopSpinning()
                                                getNewOrderDataList()
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        getNewOrderDataList()
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getNewOrderDataList()
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                error.printStackTrace()
                                getNewOrderDataList()
                            })
            )

        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            getNewOrderDataList()
        }
    }

    //03-09-2021
    fun getNewOrderDataList() {
        if(Pref.IsActivateNewOrderScreenwithSize) {
            XLog.d("API_Optimization getNewOrderDataList Login : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            try {
                AppDatabase.getDBInstance()?.newOrderGenderDao()?.deleteAll()
                AppDatabase.getDBInstance()?.newOrderProductDao()?.deleteAll()
                AppDatabase.getDBInstance()?.newOrderColorDao()?.deleteAll()
                AppDatabase.getDBInstance()?.newOrderSizeDao()?.deleteAll()

                val repository = OrderDetailsListRepoProvider.provideOrderDetailsListRepository()
                progress_wheel.spin()
                BaseActivity.compositeDisposable.add(
                        repository.getNewOrderData()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val response = result as NewOrderDataModel
                                    if (response.status == NetworkConstant.SUCCESS) {
                                        var list_gender = response.Gender_list
                                        var list_product = response.Product_list
                                        var list_color = response.Color_list
                                        var list_size = response.size_list

                                        if (list_gender != null && list_gender.isNotEmpty()) {
                                            doAsync {

                                                for (l in 0..list_gender.size - 1) {
                                                    if (list_gender.get(l).gender_id == 1) {
                                                        Pref.new_ord_gender_male = list_gender.get(l).gender.toString().toUpperCase()
                                                    }
                                                    if (list_gender.get(l).gender_id == 2) {
                                                        Pref.new_ord_gender_female = list_gender.get(l).gender.toString().toUpperCase()
                                                    }
                                                }


                                                AppDatabase.getDBInstance()?.newOrderGenderDao()?.insertAll(list_gender)
                                                AppDatabase.getDBInstance()?.newOrderGenderDao()?.updateGendertoUpperCase()



                                                if (list_product != null && list_product.isNotEmpty()) {
                                                    AppDatabase.getDBInstance()?.newOrderProductDao()?.insertAll(list_product)
                                                    AppDatabase.getDBInstance()?.newOrderProductDao()?.updateProducttoUpperCase()
                                                }
                                                if (list_color != null && list_color.isNotEmpty()) {
                                                    AppDatabase.getDBInstance()?.newOrderColorDao()?.insertAll(list_color)
                                                    AppDatabase.getDBInstance()?.newOrderColorDao()?.updateColorNametoUpperCase()

                                                }
                                                if (list_size != null && list_size.isNotEmpty()) {
                                                    AppDatabase.getDBInstance()?.newOrderSizeDao()?.insertAll(list_size)
                                                    AppDatabase.getDBInstance()?.newOrderSizeDao()?.updateSizeNametoUpperCase()
                                                }


                                                uiThread {
                                                    progress_wheel.stopSpinning()
                                                    getNewOrderHistory()
                                                }
                                            }
                                        } else {
                                            progress_wheel.stopSpinning()
                                            getNewOrderHistory()
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        getNewOrderHistory()
                                    }

                                }, { error ->
                                    progress_wheel.stopSpinning()
                                    error.printStackTrace()
                                    getNewOrderHistory()
                                })
                )
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                getNewOrderHistory()
            }

        }else{
            XLog.d("API_Optimization getNewOrderDataList Login : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            getQuestionApi()
        }
    }


    private fun getNewOrderHistory() {

        try {
            val list = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getAll()
            if (list!!.size == 0) {
                val repository = OrderDetailsListRepoProvider.provideOrderDetailsListRepository()
                BaseActivity.compositeDisposable.add(
                        //repository.getNewOrderHistoryData()
                        repository.getNewOrderHistoryDataSimplefied()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val response = result as NewOdrScrOrderListModel
                                    if (response.status == NetworkConstant.SUCCESS) {

                                        doAsync {

                                            AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.insertAll(response.order_list!!.asReversed())
                                            AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.updateSizeNametoUpperCase()


                                            /*for(i in 0..response.Shop_list!!.size-1){
                                                var ordL=response.Shop_list!!.get(i).OrderList
                                                for(j in 0..ordL.size!!-1){
                                                    var prodList=ordL!!.get(j).product_list
                                                    for(k in 0..prodList.size-1){
                                                        var colorList=prodList.get(k).color_list
                                                        for(l in 0..colorList.size-1){


                                                            var obj:NewOrderScrOrderEntity= NewOrderScrOrderEntity()
                                                            obj.order_id=ordL.get(j).order_id
                                                            obj.product_id=prodList.get(k).product_id.toString()
                                                            obj.product_name=prodList.get(k).product_name.toString()
                                                            obj.gender=prodList.get(k).gender.toString()
                                                            obj.size=colorList.get(l).size.toString()
                                                            obj.qty=colorList.get(l).qty.toString()
                                                            obj.order_date=ordL.get(j).order_date
                                                            obj.shop_id= response.Shop_list!!.get(i).shop_id
                                                            obj.color_id=colorList.get(l).color_id.toString()
                                                            obj.color_name="cname"
                                                            obj.isUploaded=true
                                                            AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.insert(obj)

                                                        }
                                                    }
                                                }
                                            }*/

                                            uiThread {
                                                getQuestionApi()

                                            }
                                        }
                                    } else {
                                        getQuestionApi()

                                    }

                                }, { error ->
                                    progress_wheel.stopSpinning()
                                    getQuestionApi()

                                })
                )
            } else {
                getQuestionApi()

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            getQuestionApi()

        }

    }




    override fun onPause() {

        if (checkFingerPrint?.signal != null)
            checkFingerPrint?.signal?.cancel()
        else {
            checkFingerPrint?.signal = CancellationSignal()
            checkFingerPrint?.signal?.cancel()
        }

        super.onPause()
    }

    private fun setServiceAlarm(context: Context, minute: Int, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NewAlarmReceiver::class.java)
        intent.putExtra("request_code", requestCode)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        //val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance(Locale.ENGLISH)

        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }*/

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, 1 * 60 * 1000, pendingIntent)
    }

    fun showSnackMessage(message: String) {
        //DisplayAlert.showSnackMessage(this@LoginActivity, alert_snack_bar, message)
        Toaster.msgShort(this, message)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null) {
            locationManager!!.removeUpdates(this);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("login " + resultCode.toString());
        if (requestCode == 777) {
            println("login 777");
            if (!Settings.canDrawOverlays(this@LoginActivity)) {
                getOverlayPermission()
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        //Permission is not available. Display error text.
                        errorToast();
                        finish();
                    }
                }
            } else if (requestCode == PermissionHelper.REQUEST_CODE_CAMERA) {
                /*getCameraImage(data)

                if (!TextUtils.isEmpty(filePath)) {
                    XLog.e("===========Add Shop Image (DashboardActivity)===========")
                    XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                    val contentURI = FTStorageUtils.getImageContentUri(this, File(Uri.parse(filePath).path).absolutePath)

                    XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                    try {
                        CropImage.activity(contentURI)
                                .setAspectRatio(40, 21)
                                .start(this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        XLog.e("Error: " + e.localizedMessage)
                    }
                }*/

                getCameraImage(data)
                val fileSize = AppUtils.getCompressImage(filePath)

                val fileSizeInKB = fileSize / 1024
                Log.e("Login", "image file size after compression==========> $fileSizeInKB KB")


                val file = File(filePath)

                uploadSelfie(file)

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                val resultUri = result.uri

                AppUtils.getCompressBillingImage(resultUri.toString(), this)
                val file = File(resultUri.path!!)

                uploadSelfie(file)
            } else if (requestCode == 777) {
                println("login 777");
                if (!Settings.canDrawOverlays(this@LoginActivity)) {
                    getOverlayPermission()
                }
            }
        }
    }

    private fun uploadSelfie(file: File) {
        selfieDialog?.dismiss()

        if (!AppUtils.isOnline(this)) {
            //showSnackMessage(getString(R.string.no_internet))
//            tv_internet_info.setBackgroundColor(resources.getColor(R.color.color_custom_red))
            tv_internet_info.setTextColor(resources.getColor(R.color.color_custom_red_1))
            tv_internet_info.text = getString(R.string.login_net_disconnected1)
            icon_internet.setImageDrawable(mContext.getDrawable(R.drawable.ic_wifi_connection_offline_new))
            return
        }

//        tv_internet_info.setBackgroundColor(resources.getColor(R.color.color_custom_green))
        tv_internet_info.setTextColor(resources.getColor(R.color.color_custom_white_1))
        tv_internet_info.text = getString(R.string.login_net_connected1)
        icon_internet.setImageDrawable(mContext.getDrawable(R.drawable.ic_internet_new))
//        tv_internet_info.slideDown()
        val repository = LoginRepositoryProvider.provideLoginImgRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.loginWithImage(file.absolutePath, this, username_EDT.text.toString().trim(), password_EDT.text.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result_ ->
                            progress_wheel.stopSpinning()
                            val response = result_ as BaseResponse

                            if (response.status == NetworkConstant.SUCCESS) {
                                prapareLogin(this@LoginActivity)
                            } else {
                                BaseActivity.isApiInitiated = false
                                showSnackMessage(response.message!!)
                            }


                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun getCameraImage(data: Intent?) {
        val isCamera: Boolean
        isCamera = if (!AppUtils.isN) {
            if (data == null) {
                true
            } else {
                val action = data.action
                if (action == null) {
                    false
                } else {
                    action == android.provider.MediaStore.ACTION_IMAGE_CAPTURE
                }
            }
        } else
            true

        var selectedImageUri: Uri?
        if (isCamera) {
            selectedImageUri = Uri.parse(mCurrentPhotoPath) // outputFileUri;
            // outputFileUri = null;
        } else {
            selectedImageUri = data?.data
        }
        if (selectedImageUri == null)
            selectedImageUri = Uri.parse(mCurrentPhotoPath)
        val filemanagerstring = selectedImageUri!!.path

        val selectedImagePath = AppUtils.getPath(this, selectedImageUri)

        when {
            selectedImagePath != null -> filePath = selectedImagePath
            filemanagerstring != null -> filePath = filemanagerstring
            else -> {
                //Toaster.msgShort(baseActivity, "Unknown Path")
                XLog.e("Bitmap", "Unknown Path")
            }
        }
    }

    private fun errorToast() {
        Toast.makeText(this, "Draw over other app permission not available. Can't start the application without the permission. Please grant the permission from settings", Toast.LENGTH_LONG).show()
    }

    private fun askForSystemOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION)
        }
    }

    private fun getOverlayPermission() {
        CommonDialog.getInstance(getString(R.string.overlaypermission), getString(R.string.overlay_permission_description), getString(R.string.cancel), getString(R.string.ok), false, object : CommonDialogClickListener {
            override fun onLeftClick() {
                // overlay testing
                //finish()
            }

            override fun onRightClick(editableData: String) {
                askForSystemOverlayPermission()
            }

        }).show(supportFragmentManager, "")
    }

    fun getShopDummyImageFile(): File {
        var bm: Bitmap? = null
        if (bm == null) {
            val bitmap = (iv_shopImage.drawable as BitmapDrawable).bitmap
            bm = bitmap
        }
        val bytes = ByteArrayOutputStream()
        bm!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        /*var destination = File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis().toString() + ".jpg")*/
//27-09-2021
        var destination = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                System.currentTimeMillis().toString() + ".jpg")

        val camera_image_path = destination?.absolutePath
        val fo: FileOutputStream
        try {
            destination?.createNewFile()
            fo = FileOutputStream(destination)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return destination
    }


    private fun getQuestionApi() {
        progress_wheel.spin()
        try {
            val list = AppDatabase.getDBInstance()?.questionMasterDao()?.getAll()
            if (list!!.size == 0) {
                val repository = ShopListRepositoryProvider.provideShopListRepository()
                BaseActivity.compositeDisposable.add(
                        repository.getQuestionList()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val response = result as QuesListResponseModel
                                    XLog.d("GET PROS DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                                    if (response.status == NetworkConstant.SUCCESS) {

                                        if (response.Question_list != null && response.Question_list!!.isNotEmpty()) {
                                            doAsync {
                                                AppDatabase.getDBInstance()?.questionMasterDao()?.insertAllBulk(response.Question_list!!)
                                                uiThread {
                                                    getProspectApi()
                                                }
                                            }
                                        } else {
                                            progress_wheel.stopSpinning()
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        getProspectApi()

                                    }

                                }, { error ->
                                    progress_wheel.stopSpinning()
                                    XLog.d("GET STAGE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                    error.printStackTrace()
                                    getProspectApi()
                                })
                )
            } else {
                progress_wheel.stopSpinning()

                getProspectApi()
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            progress_wheel.stopSpinning()

            getProspectApi()
        }
    }

    private fun getProspectApi() {
        try {
            val list = AppDatabase.getDBInstance()?.prosDao()?.getAll()
            if (list!!.size == 0) {
                val repository = ShopListRepositoryProvider.provideShopListRepository()
                BaseActivity.compositeDisposable.add(
                        repository.getProsList()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val response = result as ProsListResponseModel
                                    XLog.d("GET PROS DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                                    if (response.status == NetworkConstant.SUCCESS) {
                                        if (response.Prospect_list != null && response.Prospect_list!!.isNotEmpty()) {
                                            doAsync {
                                                AppDatabase.getDBInstance()?.prosDao()?.insertAll(response.Prospect_list!!)
                                                uiThread {
                                                    getQuestionAnswerSubmitDetails()
                                                }
                                            }
                                        } else {
                                            progress_wheel.stopSpinning()
                                        }
                                    } else {
                                        getQuestionAnswerSubmitDetails()
                                    }

                                }, { error ->
                                    progress_wheel.stopSpinning()
                                    getQuestionAnswerSubmitDetails()
                                })
                )
            } else {
                getQuestionAnswerSubmitDetails()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            getQuestionAnswerSubmitDetails()

        }

    }

    private fun getQuestionAnswerSubmitDetails() {
        progress_wheel.spin()
        try {
            val list = AppDatabase.getDBInstance()?.questionSubmitDao()?.getAll()
            if (list!!.size == 0) {
                val repository = ShopListRepositoryProvider.provideShopListRepository()
                BaseActivity.compositeDisposable.add(
                        repository.getQuestionAnsSubmitDetails()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val response = result as GetQtsAnsSubmitDtlsResponseModel
                                    XLog.d("GET PROS DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                                    if (response.status == NetworkConstant.SUCCESS) {
                                        if (response.Question_list != null && response.Question_list!!.isNotEmpty()) {
                                            doAsync {
                                                AppDatabase.getDBInstance()?.questionSubmitDao()?.insertAll(response.Question_list!! as ArrayList<QuestionSubmitEntity>)

                                                AppDatabase.getDBInstance()?.questionSubmitDao()?.updateisUpdateToUploadedLogin(true)
                                                AppDatabase.getDBInstance()?.questionSubmitDao()?.updateTrueTo1("1", "true")
                                                AppDatabase.getDBInstance()?.questionSubmitDao()?.updateFalseTo0("0", "false")
                                                uiThread {
                                                    getSecImageToLogin()
                                                }
                                            }
                                        } else {
                                            progress_wheel.stopSpinning()
                                        }
                                    } else {
                                        getSecImageToLogin()
                                    }

                                }, { error ->
                                    progress_wheel.stopSpinning()
                                    getSecImageToLogin()
                                })
                )
            } else {
                progress_wheel.stopSpinning()
                getSecImageToLogin()
            }
        } catch (ex: Exception) {
            progress_wheel.stopSpinning()
            ex.printStackTrace()
            getSecImageToLogin()

        }

    }


    private fun getSecImageToLogin() {
        progress_wheel.spin()
        try {
            val list = AppDatabase.getDBInstance()?.addShopSecondaryImgDao()?.getAll()
            if (list!!.size == 0) {
                val repository = ShopListRepositoryProvider.provideShopListRepository()
                BaseActivity.compositeDisposable.add(
                        repository.getSecUploadImages()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    val response = result as GetSecImageUploadResponseModel
                                    XLog.d("GET Image Seconadray Upload  DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                                    if (response.status == NetworkConstant.SUCCESS) {
                                        if (response.lead_shop_list != null && response.lead_shop_list!!.isNotEmpty()) {
                                            doAsync {
                                                var list = response.lead_shop_list as MutableList<AddShopSecondaryImgEntity>
                                                for (i in 0..response.lead_shop_list!!.size - 1) {
                                                    AppDatabase.getDBInstance()?.addShopSecondaryImgDao()?.updateListOfSecImages(list.get(i).lead_shop_id!!.toString(), list.get(i).rubylead_image1.toString()!!, list[i].rubylead_image2!!)
                                                }
                                                uiThread {
                                                    getReturnList()
                                                }
                                            }
                                        } else {
                                            progress_wheel.stopSpinning()
                                        }
                                    } else {
                                        getReturnList()
                                    }

                                }, { error ->
                                    progress_wheel.stopSpinning()
                                    getReturnList()
                                })
                )
            } else {
                progress_wheel.stopSpinning()
                getReturnList()
            }
        } catch (ex: Exception) {
            progress_wheel.stopSpinning()
            ex.printStackTrace()
            getReturnList()

        }

    }


    /*20-12-2021*/
   private fun getReturnList() {
        println("xyz - getReturnList started" + AppUtils.getCurrentDateTime());

        var returnDtlsList=AppDatabase.getDBInstance()!!.returnDetailsDao().getAll()
        if(returnDtlsList.size==0){
            val repository = NewOrderListRepoProvider.provideOrderListRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.getReturnList(Pref.session_token!!, Pref.user_id!!, "")
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as ReturnListResponseModel
                                if (response.status == NetworkConstant.SUCCESS) {
                                    val return_details_list = response.return_list

                                    if (return_details_list != null && return_details_list.isNotEmpty()) {

                                        doAsync {

                                            for (i in return_details_list.indices) {
                                                val returnDetailList = ReturnDetailsEntity()
                                                returnDetailList.date = return_details_list[i].return_date_time!!.replace(" ", "T") //AppUtils.convertToCommonFormat(order_details_list[i].date!!)
                                                if (!TextUtils.isEmpty(return_details_list[i].return_date_time))
                                                    returnDetailList.only_date = AppUtils.convertDateTimeToCommonFormat(return_details_list[i].return_date_time!!.replace(" ", "T"))
                                                returnDetailList.shop_id = return_details_list[i].shop_id
                                                returnDetailList.description = ""

                                                if (!TextUtils.isEmpty(return_details_list[i].return_amount)) {
                                                    val finalAmount = String.format("%.2f", return_details_list[i].return_amount?.toFloat())
                                                    returnDetailList.amount = finalAmount
                                                }

                                                returnDetailList.isUploaded = true
                                                returnDetailList.return_id = return_details_list[i].return_id

                                                if (!TextUtils.isEmpty(return_details_list[i].return_lat) && !TextUtils.isEmpty(return_details_list[i].return_long)) {
                                                    returnDetailList.return_lat = return_details_list[i].return_lat
                                                    returnDetailList.return_long = return_details_list[i].return_long
                                                } else {
                                                    returnDetailList.return_lat = return_details_list[i].shop_lat
                                                    returnDetailList.return_long = return_details_list[i].shop_long
                                                }


                                                if (return_details_list[i].product_list != null && return_details_list[i].product_list?.size!! > 0) {
                                                    for (j in return_details_list[i].product_list?.indices!!) {
                                                        val productReturnList = ReturnProductListEntity()
                                                        productReturnList.brand = return_details_list[i].product_list?.get(j)?.brand
                                                        productReturnList.brand_id = return_details_list[i].product_list?.get(j)?.brand_id
                                                        productReturnList.category_id = return_details_list[i].product_list?.get(j)?.category_id
                                                        productReturnList.watt = return_details_list[i].product_list?.get(j)?.watt
                                                        productReturnList.watt_id = return_details_list[i].product_list?.get(j)?.watt_id
                                                        productReturnList.product_id = return_details_list[i].product_list?.get(j)?.id.toString()
                                                        productReturnList.category = return_details_list[i].product_list?.get(j)?.category
                                                        productReturnList.return_id = return_details_list[i].return_id
                                                        productReturnList.product_name = return_details_list[i].product_list?.get(j)?.product_name

                                                        if (!TextUtils.isEmpty(return_details_list[i].product_list?.get(j)?.rate)) {
                                                            val finalRate = String.format("%.2f", return_details_list[i].product_list?.get(j)?.rate?.toFloat())
                                                            productReturnList.rate = finalRate
                                                        }

                                                        productReturnList.qty = return_details_list[i].product_list?.get(j)?.qty

                                                        if (!TextUtils.isEmpty(return_details_list[i].product_list?.get(j)?.total_price)) {
                                                            val finalTotalPrice = String.format("%.2f", return_details_list[i].product_list?.get(j)?.total_price?.toFloat())
                                                            productReturnList.total_price = finalTotalPrice
                                                        }
                                                        productReturnList.shop_id = return_details_list[i].shop_id

                                                        AppDatabase.getDBInstance()!!.returnProductListDao().insert(productReturnList)
                                                    }
                                                }

                                                AppDatabase.getDBInstance()!!.returnDetailsDao().insert(returnDetailList)
                                            }

                                            uiThread {
                                                progress_wheel.stopSpinning()
                                                getShopFeedback()
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        getShopFeedback()

                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getShopFeedback()

                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                getShopFeedback()
                            })
            )
        }else{
            getShopFeedback()
        }

    }

    private fun getShopFeedback(){
        try{
            var feedList= AppDatabase.getDBInstance()?.shopFeedbackDao()?.getAll()
            if(feedList!!.size==0){
                XLog.d("getShopFeedback : feedList empty")
                val repository = NewOrderListRepoProvider.provideOrderListRepository()
                BaseActivity.compositeDisposable.add(
                    repository.getShopFeedback(Pref.user_id!!, "","","90")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ShopFeedbackResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                if(response.shop_list!=null && response.shop_list!!.size>0){
                                    val objList = response.shop_list
                                    doAsync {
                                        println("xTag_ start")
                                        AppDatabase.getDBInstance()?.shopFeedbackDao()?.deleteAll()
                                        for(i in 0..objList!!.size-1){
                                            var feedList=objList.get(i).feedback_remark_list!!
                                            for(j in 0..feedList.size-1){
                                                var ob: ShopFeedbackEntity = ShopFeedbackEntity()
                                                ob.apply {
                                                    shop_id=objList.get(i).shop_id
                                                    feedback=feedList.get(j).feedback
                                                    date_time=feedList.get(j).date_time
                                                    if(feedback.equals(""))
                                                        feedback="N/A"
                                                }

                                                AppDatabase.getDBInstance()?.shopFeedbackDao()?.insert(ob)
                                            }

                                        }
                                        uiThread {
                                            println("xTag_ finish")
                                            getBeatStatus()
                                        }
                                    }
                                }
                            } else {
                                getBeatStatus()

                            }

                        }, { error ->
                            error.localizedMessage
                            getBeatStatus()
                        })
                )
            }else{
                XLog.d("getShopFeedback : gotoHomeActivity")
                getBeatStatus()
            }
        }catch (ex:Exception){
            ex.printStackTrace()
            getBeatStatus()
        }
    }

    fun getBeatStatus(){
        try{
            Pref.SelectedBeatIDFromAttend="-1"
            val repository = GetBeatRegProvider.provideSaveButton()
            BaseActivity.compositeDisposable.add(
                repository.getBeat(Pref.user_id!!,AppUtils.getCurrentDateyymmdd(),Pref.session_token!!)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val viewResult = result as BeatGetStatusModel
                        if (viewResult!!.status == NetworkConstant.SUCCESS) {
                            Pref.SelectedBeatIDFromAttend = viewResult.beat_id?.toString()!!
                            callExtraTeamShopListApi()
//                            gotoHomeActivity()
                        } else {
                            callExtraTeamShopListApi()
//                            gotoHomeActivity()
                        }
                    }, { error ->
                        callExtraTeamShopListApi()
//                        gotoHomeActivity()
                    })
            )
        }
        catch (ex:Exception){
            ex.printStackTrace()
            callExtraTeamShopListApi()
//            gotoHomeActivity()
        }
    }


    fun openDialogPopup(text:String){
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_ok)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_header_TV) as AppCustomTextView
        dialogHeader.text = text
        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
        })
        simpleDialog.show()
    }

    fun openDialogPopupIMEI(header:String,text:String){
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_ok_imei)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_header) as AppCustomTextView
        val dialogBody = simpleDialog.findViewById(R.id.dialog_yes_body) as AppCustomTextView
        dialogHeader.text = header
        dialogBody.text = text
        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
        })
        simpleDialog.show()
    }

}
package com.kcteam.features.dashboard.presentation

//import com.fieldtrackingsystem.features.logout.presentation.LogOutTimeSelect

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.*
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.*
import com.kcteam.ContactUtils.retrieveAllContacts
import com.kcteam.R
import com.kcteam.app.*
import com.kcteam.app.NewFileUtils.browseDocuments
import com.kcteam.app.NewFileUtils.getExtension
import com.kcteam.app.domain.*
import com.kcteam.app.types.DashboardType
import com.kcteam.app.types.FragType
import com.kcteam.app.types.TopBarConfig
import com.kcteam.app.uiaction.DisplayAlert
import com.kcteam.app.utils.*
import com.kcteam.app.utils.AppUtils.Companion.isProfile
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.NewQuotation.AddQuotFormFragment
import com.kcteam.features.NewQuotation.ViewAllQuotListFragment
import com.kcteam.features.NewQuotation.ViewDetailsQuotFragment
import com.kcteam.features.SearchLocation.SearchLocationFragment
import com.kcteam.features.SearchLocation.locationInfoModel
import com.kcteam.features.TA.ViewAllTAListFragment
import com.kcteam.features.achievement.AchievementFragment
import com.kcteam.features.activities.presentation.*
import com.kcteam.features.addAttendence.*
import com.kcteam.features.addAttendence.api.addattendenceapi.AddAttendenceRepoProvider
import com.kcteam.features.addAttendence.api.leavetytpeapi.LeaveTypeRepoProvider
import com.kcteam.features.addAttendence.model.GetReportToFCMResponse
import com.kcteam.features.addAttendence.model.GetReportToResponse
import com.kcteam.features.addAttendence.model.Leave_list_Response
import com.kcteam.features.addorder.presentation.AddOrderFragment
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.addshop.presentation.AddShopFragment
import com.kcteam.features.addshop.presentation.OTPVerificationDialog
import com.kcteam.features.addshop.presentation.ScanImageFragment
import com.kcteam.features.addshop.presentation.ShowCardDetailsDialog
import com.kcteam.features.alarm.model.AlarmData
import com.kcteam.features.alarm.presetation.AttendanceReportFragment
import com.kcteam.features.alarm.presetation.PerformanceReportFragment
import com.kcteam.features.alarm.presetation.VisitReportDetailsFragment
import com.kcteam.features.alarm.presetation.VisitReportFragment
import com.kcteam.features.attendance.AttendanceFragment
import com.kcteam.features.attendance.api.AttendanceRepositoryProvider
import com.kcteam.features.attendance.model.AttendanceRequest
import com.kcteam.features.attendance.model.AttendanceResponse
import com.kcteam.features.averageshop.presentation.AverageShopFragment
import com.kcteam.features.avgorder.presentation.AverageOrderFragment
import com.kcteam.features.avgtimespent.presentation.AvgTimespentShopListFragment
import com.kcteam.features.beatCustom.TeamBeatListFragment
import com.kcteam.features.billing.presentation.AddBillingFragment
import com.kcteam.features.billing.presentation.BillingDetailsFragment
import com.kcteam.features.billing.presentation.BillingListFragment
import com.kcteam.features.changepassword.presentation.ChangePasswordDialog
import com.kcteam.features.chat.model.ChatListDataModel
import com.kcteam.features.chat.model.ChatUserDataModel
import com.kcteam.features.chat.model.GroupUserDataModel
import com.kcteam.features.chat.presentation.*
import com.kcteam.features.chatbot.presentation.ChatBotFragment
import com.kcteam.features.chatbot.presentation.ChatBotShopListFragment
import com.kcteam.features.chatbot.presentation.ReportsFragment
import com.kcteam.features.chatbot.presentation.SelectLanguageDialog
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.commondialogsinglebtn.AddFeedbackSingleBtnDialog
import com.kcteam.features.commondialogsinglebtn.CommonDialogSingleBtn
import com.kcteam.features.commondialogsinglebtn.OnDialogClickListener
import com.kcteam.features.commondialogsinglebtn.TermsAndConditionsSingleBtnDialog
import com.kcteam.features.dailyPlan.prsentation.AllShopListFragment
import com.kcteam.features.dailyPlan.prsentation.DailyPlanListFragment
import com.kcteam.features.dailyPlan.prsentation.PlanDetailsFragment
import com.kcteam.features.damageProduct.ShopDamageProductListFrag
import com.kcteam.features.damageProduct.ShopDamageProductSubmitFrag
import com.kcteam.features.dashboard.presentation.api.ShopVisitImageUploadRepoProvider
import com.kcteam.features.dashboard.presentation.api.dashboardApi.DashboardRepoProvider
import com.kcteam.features.dashboard.presentation.api.otpsentapi.OtpSentRepoProvider
import com.kcteam.features.dashboard.presentation.api.otpverifyapi.OtpVerificationRepoProvider
import com.kcteam.features.dashboard.presentation.api.unreadnotificationapi.UnreadNotificationRepoProvider
import com.kcteam.features.dashboard.presentation.getcontentlisapi.GetContentListRepoProvider
import com.kcteam.features.dashboard.presentation.model.ContentListResponseModel
import com.kcteam.features.dashboard.presentation.model.ShopVisitImageUploadInputModel
import com.kcteam.features.dashboard.presentation.model.UnreadNotificationResponseModel
import com.kcteam.features.device_info.presentation.DeviceInfoListFragment
import com.kcteam.features.distributorwiseorder.DistributorwiseorderlistFragment
import com.kcteam.features.document.DocumentRepoFeatureNewFragment
import com.kcteam.features.document.presentation.DocumentListFragment
import com.kcteam.features.document.presentation.DocumentTypeListFragment
import com.kcteam.features.document.presentation.OpenFileWebViewFragment
import com.kcteam.features.dymanicSection.presentation.AddDynamicFragment
import com.kcteam.features.dymanicSection.presentation.AllDynamicListFragment
import com.kcteam.features.dymanicSection.presentation.DynamicListFragment
import com.kcteam.features.dymanicSection.presentation.EditDynamicFragment
import com.kcteam.features.gpsDisabilityScreen.GpsDisableFragment
import com.kcteam.features.home.presentation.HomeFragment
import com.kcteam.features.homelocation.presentation.HomeLocationFragment
import com.kcteam.features.homelocation.presentation.HomeLocationMapFragment
import com.kcteam.features.know_your_state.KnowYourStateFragment
import com.kcteam.features.lead.LeadFrag
import com.kcteam.features.lead.ViewLeadFrag
import com.kcteam.features.leaveapplynew.LeaveHome
import com.kcteam.features.leaveapplynew.model.clearAttendanceonRejectReqModelRejectReqModel
import com.kcteam.features.localshops.LocalShopListFragment
import com.kcteam.features.localshops.LocalShopListMapFragment
import com.kcteam.features.localshops.NearByShopsMapFragment
import com.kcteam.features.location.*
import com.kcteam.features.location.api.LocationRepoProvider
import com.kcteam.features.location.model.*
import com.kcteam.features.location.shopRevisitStatus.ShopRevisitStatusRepositoryProvider
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.ShopFeedbackEntity
import com.kcteam.features.login.model.alarmconfigmodel.AlarmConfigDataModel
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.logout.presentation.api.LogoutRepositoryProvider
import com.kcteam.features.logoutsync.presentation.LogoutSyncFragment
import com.kcteam.features.marketing.presentation.MarketingPagerFragment
import com.kcteam.features.meetinglist.prsentation.MeetingListFragment
import com.kcteam.features.member.MapViewForTeamFrag
import com.kcteam.features.member.model.TeamLocDataModel
import com.kcteam.features.member.model.TeamShopListDataModel
import com.kcteam.features.member.presentation.*
import com.kcteam.features.micro_learning.presentation.FileOpeningTimeIntentService
import com.kcteam.features.micro_learning.presentation.MicroLearningListFragment
import com.kcteam.features.micro_learning.presentation.MicroLearningWebViewFragment
import com.kcteam.features.myallowancerequest.MyallowanceRequestFragment
import com.kcteam.features.myjobs.model.CustomerDataModel
import com.kcteam.features.myjobs.presentation.*
import com.kcteam.features.myorder.presentation.MyOrderListFragment
import com.kcteam.features.myprofile.presentation.MyProfileFragment
import com.kcteam.features.nearbyshops.api.ShopListRepositoryProvider
import com.kcteam.features.nearbyshops.model.ShopData
import com.kcteam.features.nearbyshops.model.ShopListResponse
import com.kcteam.features.nearbyshops.presentation.BeatListFragment
import com.kcteam.features.nearbyshops.presentation.NearByShopsListFragment
import com.kcteam.features.nearbyshops.presentation.NewNearByShopsListFragment
import com.kcteam.features.nearbyuserlist.presentation.NearbyUserListFragment
import com.kcteam.features.newcollection.CollectionDetailsStatusFragment
import com.kcteam.features.newcollection.CollectionShopListFragment
import com.kcteam.features.newcollection.NewCollectionListFragment
import com.kcteam.features.newcollectionreport.*
import com.kcteam.features.notification.NotificationFragment
import com.kcteam.features.orderList.NewDateWiseOrderListFragment
import com.kcteam.features.orderList.NewOrderListFragment
import com.kcteam.features.orderList.OrderListFragment
import com.kcteam.features.orderdetail.presentation.OrderDetailFragment
import com.kcteam.features.orderhistory.ActivityMapFragment
import com.kcteam.features.orderhistory.OrderhistoryFragment
import com.kcteam.features.orderhistory.TimeLineFragment
import com.kcteam.features.orderhistory.activitiesapi.LocationFetchRepositoryProvider
import com.kcteam.features.orderhistory.model.FetchLocationRequest
import com.kcteam.features.orderhistory.model.FetchLocationResponse
import com.kcteam.features.orderhistory.model.LocationData
import com.kcteam.features.performance.GpsStatusFragment
import com.kcteam.features.performance.PerformanceFragment
import com.kcteam.features.performance.api.UpdateGpsStatusRepoProvider
import com.kcteam.features.performance.model.UpdateGpsInputParamsModel
import com.kcteam.features.permissionList.ViewPermissionFragment
import com.kcteam.features.photoReg.PhotoAttendanceFragment
import com.kcteam.features.photoReg.ProtoRegistrationFragment
import com.kcteam.features.photoReg.RegisTerFaceFragment
import com.kcteam.features.photoReg.TeamAttendanceFragment
import com.kcteam.features.quotation.presentation.*
import com.kcteam.features.reimbursement.presentation.EditReimbursementFragment
import com.kcteam.features.reimbursement.presentation.ReimbursementDetailsFragment
import com.kcteam.features.reimbursement.presentation.ReimbursementFragment
import com.kcteam.features.reimbursement.presentation.ReimbursementListFragment
import com.kcteam.features.report.presentation.*
import com.kcteam.features.returnsOrder.*
import com.kcteam.features.settings.presentation.SettingsFragment
import com.kcteam.features.shopFeedbackHistory.ShopFeedbackHisFrag
import com.kcteam.features.shopdetail.presentation.*
import com.kcteam.features.shopdetail.presentation.api.EditShopRepoProvider
import com.kcteam.features.stock.StockDetailsFragment
import com.kcteam.features.stock.StockListFragment
import com.kcteam.features.stockAddCurrentStock.AddShopStockFragment
import com.kcteam.features.stockAddCurrentStock.UpdateShopStockFragment
import com.kcteam.features.stockAddCurrentStock.ViewStockDetailsFragment
import com.kcteam.features.stockCompetetorStock.AddCompetetorStockFragment
import com.kcteam.features.stockCompetetorStock.CompetetorStockFragment
import com.kcteam.features.stockCompetetorStock.ViewComStockProductDetails
import com.kcteam.features.survey.SurveyFrag
import com.kcteam.features.survey.SurveyViewDtlsFrag
import com.kcteam.features.survey.SurveyViewFrag
import com.kcteam.features.task.presentation.AddTaskFragment
import com.kcteam.features.task.presentation.CalenderTaskFragment
import com.kcteam.features.task.presentation.EditTaskFragment
import com.kcteam.features.task.presentation.TaskListFragment
import com.kcteam.features.timesheet.presentation.AddTimeSheetFragment
import com.kcteam.features.timesheet.presentation.EditTimeSheetFragment
import com.kcteam.features.timesheet.presentation.TimeSheetListFragment
import com.kcteam.features.viewAllOrder.CartFragment
import com.kcteam.features.viewAllOrder.OrderTypeListFragment
import com.kcteam.features.viewAllOrder.ViewAllOrderListFragment
import com.kcteam.features.viewAllOrder.ViewCartFragment
import com.kcteam.features.viewAllOrder.orderNew.NewOdrScrListFragment
import com.kcteam.features.viewAllOrder.orderNew.NewOrderScrActiFragment
import com.kcteam.features.viewAllOrder.orderNew.NewOrderScrOrderDetailsFragment
import com.kcteam.features.viewAllOrder.orderNew.NeworderScrCartFragment
import com.kcteam.features.viewPPDDStock.ViewOutstandingFragment
import com.kcteam.features.viewPPDDStock.ViewPPDDListFragment
import com.kcteam.features.viewPPDDStock.ViewPPDDListOutstandingFragment
import com.kcteam.features.viewPPDDStock.ViewStockFragment
import com.kcteam.features.weather.presentation.WeatherFragment
import com.kcteam.mappackage.MapActivity
import com.kcteam.mappackage.MapActivityWithoutPath
import com.kcteam.mappackage.SendBrod
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.messaging.FirebaseMessaging
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.themechangeapp.pickimage.PermissionHelper
import com.themechangeapp.pickimage.PermissionHelper.Companion.REQUEST_CODE_DOCUMENT
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.menu.*
import net.alexandroid.gps.GpsStatusDetector
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/*
 * Created by rp : 26-10-2017:17:59
 */

class DashboardActivity : BaseActivity(), View.OnClickListener, BaseNavigation, OnCompleteListener<Void>, GpsStatusDetector.GpsStatusDetectorCallBack {
    override fun onComplete(task: Task<Void>) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;

        if (task.isSuccessful) {
            if (Pref.isGeoFenceAdded)
                return
            Pref.isGeoFenceAdded = true
//            updateGeofencesAdded(!getGeofencesAdded());
//            var messageId = getGeofencesAdded() ? R.string.geofences_added :
//                    R.string.geofences_removed;
//            Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();
//            if (getGeofencesAdded())
//               showSnackMessage("onCompleteSuccess:GeofenceAdded")
//            else
//                showSnackMessage("onCompleteSuccess:GeofenceRemoved")
        } else {
            // Get the status code for the error and log it using a user-friendly message.
//          var errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            showSnackMessage("onCompleteError")
//            Log.w(TAG, errorMessage);
        }
    }

    @SuppressLint("MissingPermission")
    override fun loadFragment(mFragType: FragType, addToStack: Boolean, initializeObject: Any) {
        AppUtils.contx = this

        drawerLayout.closeDrawers()

        if (isFinishing || getCurrentFragType() == mFragType) {
            if (getCurrentFragType() != FragType.MemberListFragment && getCurrentFragType() != FragType.OfflineMemberListFragment)
                return
        }

        if (!isFromAlarm)
            AppUtils.hideSoftKeyboard(this)

        val mTransaction = supportFragmentManager.beginTransaction()
        mTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            println("fcm_token " + token.toString());
            XLog.d("token : " + token.toString())
        })

        println("load_frag " + mFragType.toString() + "     " + Pref.user_id.toString());
        //AppDatabase.getDBInstance()!!.userLocationDataDao().updateUnknownLocationTest(AppUtils.getCurrentDateForShopActi(),"Unknown",false)

        if (addToStack) {
            mTransaction.add(R.id.frame_layout_container, getFragInstance(mFragType, initializeObject, true)!!, mFragType.toString())
            mTransaction.addToBackStack(mFragType.toString()).commitAllowingStateLoss()
        } else {
            mTransaction.replace(R.id.frame_layout_container, getFragInstance(mFragType, initializeObject, true)!!, mFragType.toString())
            mTransaction.commitAllowingStateLoss()
        }

    }


    private fun checkByU(token: String) {
        var fbToken = "fRL4OYJgTNCLFcMPKOcPhH:APA91bHZHlJf56uD_TqnD-Pq0Rvl0ao9x1ZZhtZvu2MpbAJ5FJD_1TrrTnRhfx0ABzfj2WKaX_ji8mjx1W_eawbZs5KUOD8OM0GpTK2m8IV9863_jIvNaFVknSlnAH1T5I3X4iJBERCF"
        if (fbToken != "") {
            try {
                val jsonObject = JSONObject()
                val notificationBody = JSONObject()
                notificationBody.put("title", "titleeeeee")
                notificationBody.put("body", "bodyyyyy")
                notificationBody.put("flag_status", "flag_status")
                notificationBody.put("applied_user_id", Pref.user_id)
                notificationBody.put("act", "1")
                jsonObject.put("data", notificationBody)
                val jsonArray = JSONArray()
                jsonArray.put(0, fbToken)
                jsonObject.put("registration_ids", jsonArray)
                sendCustomNotification(jsonObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun check(token: String) {
        var fbToken = "einJt0q5QnmA2YH09qrhD2:APA91bHV8S0T937dZcLqh0tpDTukvZea6Ue3rVh4hl59jDDCom2xKoCSgbmK6JTjTi5jw3iZykqhpwiX0nQcI8f-ng0xdFZZZ9LIagSWBdTzjqig8U4zZwgnJWYRPZ99xBWsBmECPkH-"
        if (fbToken != "") {
            try {
                val jsonObject = JSONObject()
                val notificationBody = JSONObject()
                notificationBody.put("title", "titleeeeee")
                notificationBody.put("body", "bodyyyyy")
                notificationBody.put("flag", "flag")
                notificationBody.put("applied_user_id", Pref.user_id)
                notificationBody.put("act", "1")
                jsonObject.put("data", notificationBody)
                val jsonArray = JSONArray()
                jsonArray.put(0, fbToken)
                jsonObject.put("registration_ids", jsonArray)
                sendCustomNotification(jsonObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
    fun sendCustomNotification(notification: JSONObject) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                object : Response.Listener<JSONObject?> {
                    override fun onResponse(response: JSONObject?) {

                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {

                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getString(R.string.firebase_key)
                params["Content-Type"] = "application/json"
                return params
            }
        }

        MySingleton.getInstance(applicationContext)!!.addToRequestQueue(jsonObjectRequest)
    }


    private lateinit var mReceiver: BroadcastReceiver
    private lateinit var mReceiverAddshop: BroadcastReceiver
    private lateinit var mReceiverNearbyshop: BroadcastReceiver
    private lateinit var mReceiverAutoLogout: BroadcastReceiver
    private lateinit var mContext: Context

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    lateinit var searchView: MaterialSearchView
    private lateinit var bottomBar: View

    private lateinit var home_IV: ImageView
    private lateinit var add_shop_IV: ImageView
    private lateinit var nearby_shops_IV: ImageView
    private lateinit var my_orders_IV: ImageView
    private lateinit var iv_search_icon: ImageView
     lateinit var iv_sync_icon: ImageView

    private lateinit var headerTV: AppCustomTextView
    private lateinit var home_TV: AppCustomTextView
    private lateinit var add_shop_TV: AppCustomTextView
    private lateinit var nearby_shops_TV: AppCustomTextView
    private lateinit var my_orders_TV: AppCustomTextView
    private lateinit var maps_TV: AppCustomTextView
    private lateinit var tickTV: ImageView
    private lateinit var ta_tv: AppCustomTextView
    private lateinit var view_pp_dd_tv: AppCustomTextView

    private lateinit var home_RL: RelativeLayout
    private lateinit var add_shop_RL: RelativeLayout
    private lateinit var nearby_shops_RL: RelativeLayout
    private lateinit var my_orders_RL: RelativeLayout
    private lateinit var alert_snack_bar: CoordinatorLayout

    private var imageArrayList: MutableList<View> = ArrayList()
    private var textArrayList: MutableList<View> = ArrayList()


    private lateinit var addOrderTV: AppCustomTextView
    private lateinit var orderHistoryTV: AppCustomTextView
    private lateinit var addTravelAllowenceTV: AppCustomTextView
    private lateinit var settingsTV: AppCustomTextView
    private lateinit var returnTV: AppCustomTextView
    private lateinit var myAllowRequest: AppCustomTextView
    private lateinit var logoutTV: AppCustomTextView
    lateinit var logo: AppCompatImageView
    lateinit var tv_noti_count: AppCustomTextView
    private lateinit var iv_home_icon: ImageView
    private lateinit var nearbyShops: AppCustomTextView
    private lateinit var assignedLead: AppCustomTextView
    private lateinit var surveyMenu: AppCustomTextView
    private lateinit var shareLogs: AppCustomTextView
    private lateinit var reimbursement_tv: AppCustomTextView
    private lateinit var achievement_tv: AppCustomTextView
    private lateinit var iv_shopImage: ImageView

    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var menuMis: AppCustomTextView
    private lateinit var login_time_tv: AppCustomTextView
    private lateinit var login_time_am_tv: AppCustomTextView
    private lateinit var profile_name_TV: AppCustomTextView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var version_name_TV: AppCustomTextView
    private lateinit var add_attendence_tv: AppCustomTextView
    private lateinit var profilePicture: de.hdodenhof.circleimageview.CircleImageView
    private lateinit var iv_filter_icon: ImageView
    private lateinit var rl_confirm_btn: RelativeLayout
    private lateinit var tv_pp_dd_outstanding: AppCustomTextView
    private lateinit var tv_location: AppCustomTextView
    private lateinit var collection_TV: AppCustomTextView
    private lateinit var state_report_TV: AppCustomTextView
    private lateinit var target_TV: AppCustomTextView
    private lateinit var iv_list_party: ImageView
    private lateinit var rl_report: RelativeLayout
    private lateinit var iv_drop_down_icon: ImageView
    private lateinit var ll_report_list: LinearLayout
    private lateinit var tv_attendance_report: AppCustomTextView
    private lateinit var tv_performance_report: AppCustomTextView
    private lateinit var tv_visit_report: AppCustomTextView
    private lateinit var meeting_TV: AppCustomTextView
    private lateinit var team_TV: AppCustomTextView
    private lateinit var iv_map: AppCompatImageView
    private lateinit var timesheet_TV: AppCustomTextView
    private lateinit var tv_change_pwd: AppCustomTextView
    private lateinit var all_team_TV: AppCustomTextView
    lateinit var update_worktype_tv: AppCustomTextView
    private lateinit var achv_TV: AppCustomTextView
    private lateinit var targ_achv_TV: AppCustomTextView
    private lateinit var leave_tv: AppCustomTextView
    private lateinit var task_TV: AppCustomTextView
    private lateinit var dynamic_TV: AppCustomTextView
    private lateinit var activity_TV: AppCustomTextView
    private lateinit var rl_collection: RelativeLayout
    private lateinit var iv_collection_drop_down_icon: AppCompatImageView
    private lateinit var ll_collection_list: LinearLayout
    private lateinit var tv_report: AppCustomTextView
    private lateinit var tv_entry: AppCustomTextView
    private lateinit var share_loc_TV: AppCustomTextView
    private lateinit var iv_settings: AppCompatImageView
    private lateinit var weather_TV: AppCustomTextView
    private lateinit var doc_TV: AppCustomTextView
    private lateinit var chat_bot_TV: AppCustomTextView
    private lateinit var distributor_wise_order_list_TV: AppCustomTextView
    private lateinit var ic_calendar: AppCompatImageView
    private lateinit var ic_chat_bot: AppCompatImageView
    private lateinit var iv_cancel_chat: AppCompatImageView
    private lateinit var chat_TV: AppCustomTextView
    private lateinit var iv_people: AppCompatImageView
    private lateinit var tv_confirm_btn: AppCustomTextView
    private lateinit var iv_scan: AppCompatImageView
    private lateinit var iv_view_text: AppCompatImageView
    private lateinit var scan_TV: AppCustomTextView
    private lateinit var nearby_user_TV: AppCustomTextView
    private lateinit var fl_net_status: FrameLayout
    private lateinit var home_loc_TV: AppCustomTextView
    private lateinit var device_info_TV: AppCustomTextView
    private lateinit var permission_info_TV: AppCustomTextView
    private lateinit var anydesk_info_TV: AppCustomTextView
    private lateinit var screen_record_info_TV: AppCustomTextView
    private lateinit var check_custom_status_TV: AppCustomTextView
    private lateinit var micro_learning_TV: AppCustomTextView

    private lateinit var photo_registration: AppCustomTextView
    private lateinit var photo_team_attendance: AppCustomTextView
    private lateinit var tv_clear_attendance: AppCustomTextView

    private lateinit var alarmCofifDataModel: AlarmConfigDataModel
    private lateinit var quo_TV: AppCustomTextView

    private var mShopId: String = ""
    private var backpressed: Long = 0
    public val SELECT_CAMERA = 1
    private lateinit var tv_performance: AppCustomTextView


    private var mAddShopDBModelEntity: AddShopDBModelEntity? = null
    private var mStoreName: String = ""
    private var mCurrentPhotoPath: String = ""
    private var filePath: String = ""

    var qtyList = ArrayList<String>()
    var rateList = ArrayList<String>()
    /*06-01-2022*/
    var mrpList = ArrayList<String>()

    /*28-12-2021*/
    var schemaqtyList = ArrayList<String>()
    var schemarateList = ArrayList<String>()

    var totalScPrice = ArrayList<Double>()

    var totalPrice = ArrayList<Double>()
    private var filter: IntentFilter? = null
    var isGpsDisabled = false
    private var i = 0
    private lateinit var iv_delete_icon: ImageView
     lateinit var rl_cart: RelativeLayout
    lateinit var tv_cart_count: AppCustomTextView
    private var isAddAttendaceAlert = false

    /*Interface to update Shoplist Frag on search event*/
    private var searchListener: SearchListener? = null

    var shop_type = ""

    private var isAttendanceAlertPresent = false

    var reimbursementSelectPosition = 0

    var shop: AddShopDBModelEntity? = null
    var isConfirmed = false
    var isTodaysPerformance = false

    lateinit var teamHierarchy: ArrayList<String>
    private var idealLocAlertDialog: CommonDialogSingleBtn? = null
    private var forceLogoutDialog: CommonDialogSingleBtn? = null

    public fun setSearchListener(searchListener: SearchListener) {
        this.searchListener = searchListener
    }

    /*********************Geofence*****************/
    private enum class PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    private lateinit var mGeofencingClient: GeofencingClient
    private var mGeofenceList: ArrayList<Geofence> = arrayListOf()
    private var mGeofencePendingIntent: PendingIntent? = null
    private var mPendingGeofenceTask = PendingGeofenceTask.NONE
    private var gpsReceiver: GpsLocationReceiver? = null
    private var mGpsStatusDetector: GpsStatusDetector? = null
    private var geoFenceBroadcast: GeofenceBroadcastReceiver? = null
    private var isFromAlarm: Boolean = false
    private var pushStatus = -1
    private var orderCollectionAlertDialog: CommonDialogSingleBtn? = null
    private var isOrderAdded = false
    private var isCollectionAdded = false
    private var isOrderDialogShow = false
    var isDailyPlanFromAlarm = false
    var isAttendanceFromAlarm = false
    var isPerformanceFromAlarm = false
    var isVisitFromAlarm = false
    private var isOtherUsersShopRevisit = false
    var isChangedPassword = false
    var isChatFromDrawer = false
    var isRefreshChatUserList = false
    var newUserModel: GroupUserDataModel?= null
    var isCodeScaneed = false

    /*********************Geofence*****************/
//    private lateinit var geofenceService:Intent


    private var isTermsAndConditionsPopShow = false
    private var termsConditionsDialog: TermsAndConditionsSingleBtnDialog? = null
    var isForceLogout = false
    var activityLocationList: MutableList<UserLocationDataEntity>? = null
    var activityLocationListNew: MutableList<LocationData>? = null
    var isMemberMap = false
    var memberLocationList: MutableList<TeamLocDataModel>? = null
    var isAddedEdited = false
    var isFingerPrintSupported = true
    private var checkFingerPrint: CheckFingerPrint? = null
    var nearbyShopList: MutableList<AddShopDBModelEntity>? = null
    var isTimesheetAddedEdited = false
    var isAllMemberShop = false
    var areaId = ""
    var isBack = false
    private var selfieDialog: SelfieDialog? = null
    var isAllTeam = false
    var leaveType = ""
    var isClearData = false
    var dynamicScreen = ""
    var isDynamicFormUpdated = false
    var isFromMenu = false
    var isFromShop = false
    var isChatBotLocalShop = false
    var isChatBotAttendance = false
    var isWeatherFromDrawer = false
    var isAttendanceReportFromDrawer = false
    var isPerformanceReportFromDrawer = false
    var isVisitReportFromDrawer = false
    var isShopFromChatBot = false
    var isOrderFromChatBot = false
    var isCollectionStatusFromDrawer = false
    var isMapFromDrawer = false
    var isTargAchvFromDrawer = false
    var isAchvFromDrawer = false
    var userName = ""
    var grpId = ""
    var isGrp = false
    var visitDistance = ""
    lateinit var textToSpeech: TextToSpeech
    var visitReportDate = ""
    private var isVisitCardScan = false
    private var feedback = ""
    private var revisitImage = ""
    private var nextVisitDate = ""
    private var mFilePath = ""
    private var feedbackDialog: AddFeedbackSingleBtnDialog? = null
    private var shopName = ""
    private var contactNumber = ""
    private var isCodeScan = false
    private var isForRevisit = false
    private var reasonDialog: ReasonDialog? = null
    private var reason = ""
    private var netStatus = ""
    var isSubmit = false
    var isCalledJobApi = false

    private var feedBackDialogCompetetorImg=false
    lateinit var drawerLL :LinearLayout

    private var prosId = ""
    private var approxValue = ""

    lateinit var simpleDialogProcess : Dialog
    lateinit var dialogHeaderProcess: AppCustomTextView
    lateinit var dialog_yes_no_headerTVProcess: AppCustomTextView


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        mContext = this@DashboardActivity


        filter = IntentFilter()
        filter?.addAction(AppUtils.gpsDisabledAction)
        filter?.addAction(AppUtils.gpsEnabledAction)

        gpsReceiver = GpsLocationReceiver()
        teamHierarchy = ArrayList()
        //geoFenceBroadcast = GeofenceBroadcastReceiver()

        //checkToShowHomeLocationAlert()

        //checkToShowAddAttendanceAlert()
        //checkGPSAvailability()

        initView()
        updateUI()

        //Code by wasim
        if (intent != null) {
            var sss=intent.hasExtra("TYPE")
            var s=intent.hasExtra("fromClass")
            var ssss=intent.getStringExtra("TYPE")
            if (intent.getParcelableExtra<AlarmData>("ALARM_DATA") != null) {
                isFromAlarm = true
                val alaramData = intent.getParcelableExtra<AlarmData>("ALARM_DATA")
                alarmCofifDataModel = AlarmConfigDataModel()
                alarmCofifDataModel.requestCode = alaramData!!.requestCode
                alarmCofifDataModel.id = alaramData!!.id
                alarmCofifDataModel.report_id = alaramData!!.report_id
                alarmCofifDataModel.report_title = alaramData!!.report_title
                alarmCofifDataModel.alarm_time_hours = alaramData!!.alarm_time_hours
                alarmCofifDataModel.alarm_time_mins = alaramData!!.alarm_time_mins
            } else if (intent.hasExtra("fromClass")) {
                if (intent.getStringExtra("fromClass").equals("LoginActivity", ignoreCase = true)) {
                    /*CommonDialogSingleBtn.getInstance(getString(R.string.terms_conditions), getString(R.string.dummy_text), getString(R.string.ok), object : OnDialogClickListener {
                        override fun onOkClick() {
                        }
                    }).show(supportFragmentManager, "CommonDialogSingleBtn")*/

                    isTermsAndConditionsPopShow = true

                    val list = AppDatabase.getDBInstance()?.shopActivityDao()?.getShopActivityNextVisitDateWise(AppUtils.getCurrentDateForShopActi())
                    list?.forEach {
                        val notification = NotificationUtils(getString(R.string.app_name), "", "", "")
                        val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(it.shopid)
                        val body = "${AppUtils.hiFirstNameText()}!, your visit to " + it.shop_name + " whose contact no. is: " + shop?.ownerContactNumber + " & address is " +
                                shop?.address +" due today as per next visit date. Thanks."
                        notification.sendRevisitDueNotification(this, body)
                    }

                    val shopList = AppDatabase.getDBInstance()?.addShopEntryDao()?.all
                    shopList?.forEach {
                        if (!TextUtils.isEmpty(it.dateOfBirth)) {
                            //if (AppUtils.getCurrentDateForShopActi() == AppUtils.changeAttendanceDateFormatToCurrent(it.dateOfBirth)) {
                            if (AppUtils.getCurrentMonthDayForShopActi() == AppUtils.changeAttendanceDateFormatToMonthDay(it.dateOfBirth)) {
                                val notification = NotificationUtils(getString(R.string.app_name), "", "", "")
                                var body = ""
                                body = if (TextUtils.isEmpty(it.ownerEmailId))
                                    "Please wish Mr. " + it.ownerName + " of " + it.shopName + ", Contact Number: " + it.ownerContactNumber + " for birthday today."
                                else
                                    "Please wish Mr. " + it.ownerName + " of " + it.shopName + ", Contact Number: " + it.ownerContactNumber + ", Email: " + it.ownerEmailId + " for birthday today."
                                tv_noti_count.visibility=View.VISIBLE
                                Pref.NotiCountFlag = true
                                notification.sendLocNotification(this, body)
                            }
                        }

                        if (!TextUtils.isEmpty(it.dateOfAniversary)) {
                            //if (AppUtils.getCurrentDateForShopActi() == AppUtils.changeAttendanceDateFormatToCurrent(it.dateOfAniversary)) {
                            if (AppUtils.getCurrentMonthDayForShopActi() == AppUtils.changeAttendanceDateFormatToMonthDay(it.dateOfAniversary)) {
                                val notification = NotificationUtils(getString(R.string.app_name), "", "", "")
                                var body = ""
                                body = if (TextUtils.isEmpty(it.ownerEmailId))
                                    "Please wish Mr. " + it.ownerName + " of " + it.shopName + ", Contact Number: " + it.ownerContactNumber + " for Anniversary today."
                                else
                                    "Please wish Mr. " + it.ownerName + " of " + it.shopName + ", Contact Number: " + it.ownerContactNumber + ", Email: " + it.ownerEmailId + " for Anniversary today."
                                tv_noti_count.visibility=View.VISIBLE
                                Pref.NotiCountFlag = true
                                notification.sendLocNotification(this, body)
                            }
                        }

                    }

                    val leadActivityList=AppDatabase.getDBInstance()!!.leadActivityDao().getAll(AppUtils.getCurrentDateForShopActi())
                    leadActivityList?.forEach {
                        val notification = NotificationUtils(getString(R.string.app_name), "", "", "")
                        //val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(it.shopid)
                        val body = "${AppUtils.hiFirstNameText()}!, your activity to " + it.customer_name + " whose contact no. is: " + it?.mobile_no +" due today as per next activity date. Thanks."
                        notification.sendLeadActivityNotification(this, body)
                    }

                    val taskList = AppDatabase.getDBInstance()?.taskDao()?.getTaskDateWise(AppUtils.getCurrentDateForShopActi())
                    taskList?.forEach {
                        val notification = NotificationUtils(getString(R.string.app_name), "", "", "")
                        val body = "Your task " + it.task_name + " due today."
                        notification.sendTaskDueNotification(this, body)
                    }

                }
                else if (intent.getStringExtra("fromClass").equals("push", ignoreCase = true)) {
                    isClearData = true
                }
            }
        }

        if (isTermsAndConditionsPopShow) {
            callTermsAndConditionsdApi()
        } else {
            if (!Pref.isSeenTermsConditions)
                showTermsConditionsPopup()
        }


        //AlarmReceiver.setAlarm(this, 12, 11, 12345)

        checkGPS_Availability()

        /*********************Geofence*****************/
//        mGeofenceList = ArrayList()
//        mGeofencePendingIntent = null
//        // Get the geofences used. Geofence data is hard coded in this sample.
//        populateGeofenceList();
//        mGeofencingClient = LocationServices.getGeofencingClient(this);
//        if (!checkPermissions()) {
//            PermissionHelper.checkLocationPermission(this, 0)
//        } else {
//            if(!Pref.isGeoFenceAdded)
//                addGeofences()
//        }
        /*********************Geofence*****************/

        /*********************GeofenceService*****************/
//        if(!Pref.isGeoFenceAdded) {
//            Pref.isGeoFenceAdded=true
//            geofenceService = Intent(this, GeofenceService::class.java)
//            startService(geofenceService)
//        }
        /*********************GeofenceService*****************/
        if (AppDatabase.getDBInstance()!!.shopActivityDao().getAll().isNotEmpty())
            takeActionOnGeofence()
        setProfileImg()
        initBackStackActionSet()
        loadHomeFragment()


        if (/*!isFromAlarm && intent.hasExtra("TYPE")*/pushStatus == 0 && forceLogoutDialog == null) {
            pushStatus = -1
            //logo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake))
            loadFragment(FragType.NotificationFragment, true, "")
        }

        Pref.appLaunchDate = AppUtils.getCurrentDate()


        if (!TextUtils.isEmpty(Pref.appLaunchDate)) {
            if (!AppUtils.compareWithCurrentDate(Pref.appLaunchDate!!)) {
                Pref.appLaunchDate = AppUtils.getCurrentDate()
                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsVisitedToFalse(false)
            }
        } else {
            Pref.appLaunchDate = AppUtils.getCurrentDate()
        }

        //Code by wasim
        LocalBroadcastManager.getInstance(this).registerReceiver(mAlarmReceiver, IntentFilter("ALARM_RECIEVER_BROADCAST"))

        if (shouldFetchLocationActivity())
            fetchActivityList()
        /*else {
            if (isTermsAndConditionsPopShow) {
                callTermsAndConditionsdApi()
            } else {
                if (!Pref.isSeenTermsConditions)
                    showTermsConditionsPopup()
            }
        }*/
//


        //TODO stuff
        /*if (intent != null && intent.extras != null && !isAttendanceAlertPresent && !isGpsDisabled)
            callShopVisitConfirmationDialog(intent.extras.get("NAME") as String, intent.extras.get("ID") as String)*/

//        searchView.openSearch()


        LocalBroadcastManager.getInstance(this).registerReceiver(fcmClearDataReceiver, IntentFilter("FCM_ACTION_RECEIVER_CLEAR_DATA"))
        LocalBroadcastManager.getInstance(this).registerReceiver(fcmReceiver, IntentFilter("FCM_ACTION_RECEIVER"))
        LocalBroadcastManager.getInstance(this).registerReceiver(fcmReceiver_leave, IntentFilter("FCM_ACTION_RECEIVER_LEAVE"))
        LocalBroadcastManager.getInstance(this).registerReceiver(fcmReceiver_leave_status, IntentFilter("FCM_ACTION_RECEIVER_LEAVE_STATUS"))
        LocalBroadcastManager.getInstance(this).registerReceiver(fcmReceiver_quotation_approval, IntentFilter("FCM_ACTION_RECEIVER_quotation_approval"))
        LocalBroadcastManager.getInstance(this).registerReceiver(idealLocReceiver, IntentFilter("IDEAL_LOC_BROADCAST"))
        LocalBroadcastManager.getInstance(this).registerReceiver(collectionAlertReceiver, IntentFilter("ALERT_RECIEVER_BROADCAST"))
        LocalBroadcastManager.getInstance(this).registerReceiver(forceLogoutReceiver, IntentFilter("FORCE_LOGOUT_BROADCAST"))
        LocalBroadcastManager.getInstance(this).registerReceiver(autoRevisit, IntentFilter("AUTO_REVISIT_BROADCAST"))
        LocalBroadcastManager.getInstance(this).registerReceiver(offlineShopReceiver, IntentFilter("OFFLINE_SHOP_BROADCAST"))
        LocalBroadcastManager.getInstance(this).registerReceiver(chatReceiver, IntentFilter("FCM_CHAT_ACTION_RECEIVER"))
        LocalBroadcastManager.getInstance(this).registerReceiver(localeReceiver, IntentFilter("CHANGE_LOCALE_BROADCAST"))
        LocalBroadcastManager.getInstance(this).registerReceiver(updateStatusReceiver, IntentFilter("FCM_STATUS_ACTION_RECEIVER"))
        LocalBroadcastManager.getInstance(this).registerReceiver(homeLocReceiver, IntentFilter("HOME_LOC_ACTION_RECEIVER"))
        LocalBroadcastManager.getInstance(this).registerReceiver(revisitReceiver, IntentFilter("REVISIT_REASON_BROADCAST"))

        LocalBroadcastManager.getInstance(this).registerReceiver(updatePJP, IntentFilter("UPDATE_PJP_LIST"))

        Handler().postDelayed(Runnable {
            if (Pref.isShowHomeLocReason && Pref.willShowHomeLocReason && (reasonDialog == null || !reasonDialog?.isVisible!!))
                showHomeLocReasonDialog()
        }, 500)


        Handler().postDelayed(Runnable {
            if (Pref.isShowShopVisitReason && Pref.willShowShopVisitReason && (reasonDialog == null || !reasonDialog?.isVisible!!))
                showRevisitReasonDialog(0, null, "", "", null, null)
        }, 700)

        if (!AppUtils.isOnline(mContext)) {
            fl_net_status.background = getDrawable(R.drawable.red_round)
            netStatus = "Offline"
        }

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (AppUtils.isN) {
            connectivityManager?.let {
                it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        try {
                            fl_net_status.background = getDrawable(R.drawable.green_round)
                            netStatus = "Online"
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onLost(network: Network) {
                        try {
                            fl_net_status.background = getDrawable(R.drawable.red_round)
                            netStatus = "Offline"
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                })
            }
        }
        else {
            val builder = NetworkRequest.Builder()
            builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            val networkRequest = builder.build()
            connectivityManager?.registerNetworkCallback(networkRequest,
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            super.onAvailable(network)
                            try {
                                fl_net_status.background = getDrawable(R.drawable.green_round)
                                netStatus = "Online"
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onLost(network: Network) {
                            super.onLost(network)
                            try {
                                fl_net_status.background = getDrawable(R.drawable.red_round)
                                netStatus = "Offline"
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
        }

        Handler().postDelayed(Runnable {
        if(!isWorkerRunning("workerTag")){
            val constraint = Constraints.Builder()
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()
            val request = PeriodicWorkRequest.Builder(WorkerService::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .addTag("workerTag")
                .build()
            WorkManager.getInstance(this).enqueueUniquePeriodicWork("loc_worker", ExistingPeriodicWorkPolicy.KEEP, request)
        }
        }, 1000)

    }

    fun isWorkerRunning(tag:String):Boolean{
        val workInstance = WorkManager.getInstance(this)
        val status: ListenableFuture<List<WorkInfo>> = WorkManager.getInstance(this).getWorkInfosByTag(tag)
        try{
            var runningStatus:Boolean = false
            val workInfoList:List<WorkInfo> = status.get()
            for( obj:WorkInfo in workInfoList){
                var state : WorkInfo.State =  obj.state
                runningStatus = state ==WorkInfo.State.RUNNING || state ==WorkInfo.State.ENQUEUED
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

    fun checkToShowHomeLocationAlert() {
        if (!Pref.isHomeLocAvailable) {
            if(Pref.IsShowHomeLocationMapGlobal && Pref.IsShowHomeLocationMap){
                showHomeLocationAlert()
            }
        } else{
            if(Pref.IsOnLeaveForTodayApproved==false && !Pref.OnLeaveForTodayStatus.equals("PENDING"))
                checkToShowAddAttendanceAlert()
            else{
                if(Pref.IsOnLeaveForTodayApproved==false && Pref.OnLeaveForTodayStatus.equals("PENDING")){
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                    dialogHeader.text = "Your applied leave status has not been updated. Contact your Supervisor."
                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
                }
            }
        }
    }



    private fun showHomeLocationAlert() {

        CommonDialogSingleBtn.getInstance("Attendance Address", "Please pin your attendance address from the map", "Open map", object : OnDialogClickListener {

            override fun onOkClick() {
                (mContext as DashboardActivity).loadFragment(FragType.SearchLocationFragment, true, "")
            }
        }).show(supportFragmentManager, "CommonDialogSingleBtn")
    }

    private fun callTermsAndConditionsdApi() {

        isTermsAndConditionsPopShow = false

        if (!AppUtils.isOnline(mContext)) {
            checkToShowHomeLocationAlert()
            showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.e("=========Call terms & conditions api (Dashboard)============")

        val repository = GetContentListRepoProvider.getContentListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getContentList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ContentListResponseModel

                            XLog.e("RESPONSE: " + response.status + ", MESSAGE: " + response.message)

                            if (response.status == NetworkConstant.SUCCESS) {
                                if (!Pref.isSefieAlarmed)
                                    progress_wheel.stopSpinning()

                                if (response.contentlist != null && response.contentlist!!.size > 0) {


                                    for (i in response.contentlist!!.indices) {
                                        if (response.contentlist?.get(i)?.TemplateID == "1") {
                                            Pref.termsConditionsText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                                Html.fromHtml(response.contentlist?.get(i)?.content!!, Html.FROM_HTML_MODE_COMPACT).toString()
                                            else
                                                Html.fromHtml(response.contentlist?.get(i)?.content!!).toString()
                                        }
                                    }

                                    showTermsConditionsPopup()
                                }

                            } else if (response.status == NetworkConstant.SESSION_MISMATCH) {
                                if (!Pref.isSefieAlarmed)
                                    progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else if (response.status == NetworkConstant.NO_DATA) {
                                if (!Pref.isSefieAlarmed)
                                    progress_wheel.stopSpinning()
                                checkToShowHomeLocationAlert()

                            } else {
                                if (!Pref.isSefieAlarmed)
                                    progress_wheel.stopSpinning()
                                checkToShowHomeLocationAlert()
                            }

                        }, { error ->
                            XLog.e("ERROR: " + error.message)
                            error.printStackTrace()
                            if (!Pref.isSefieAlarmed)
                                progress_wheel.stopSpinning()
                            checkToShowHomeLocationAlert()
                        })
        )
    }

    private fun showTermsConditionsPopup() {

        if (TextUtils.isEmpty(Pref.termsConditionsText)) {
            checkToShowHomeLocationAlert()
            return
        }

        XLog.e("=========Show terms & conditions popup (Dashboard)============")

        if (termsConditionsDialog != null) {
            termsConditionsDialog?.dismissAllowingStateLoss()
            termsConditionsDialog = null
        }

        termsConditionsDialog = TermsAndConditionsSingleBtnDialog.getInstance(getString(R.string.terms_conditions), Pref.termsConditionsText, "I Agree", object : OnDialogClickListener {
            override fun onOkClick() {
                Pref.isSeenTermsConditions = true
                checkToShowHomeLocationAlert()
            }
        })//.show(supportFragmentManager, "CommonDialogSingleBtn")
        termsConditionsDialog?.show(supportFragmentManager, "CommonDialogSingleBtn")
    }

    fun checkLocationMode() {
        val locationMode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
        if (locationMode != 3) {
            showSnackMessage("Please set location mode to High Accuracy")
            Handler().postDelayed(Runnable {
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }, 1000)
        }
    }

    override fun onStart() {
        super.onStart()
//        showSnackMessage("onStart")
//        if (!checkPermissions()) {
//            showSnackMessage("onStart:No permission")
//            PermissionHelper.checkLocationPermission(this, 0)
//        } else {
//            addGeofences()
//            showSnackMessage("onStart:GeofenceAdded")
//        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun checkGPS_Availability() {
        mGpsStatusDetector = GpsStatusDetector(this)
        var manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isGpsDisabled = true
            //loadFragment(FragType.GpsDisableFragment, true, "")
            mGpsStatusDetector?.checkGpsStatus()
        } else {
            isGpsDisabled = false
            //checkLocationMode()
            if (intent != null && intent.extras != null /*&& Pref.isAddAttendence*/ /*&& !isGpsDisabled*/) {

                //Code by wasim
                if (!isFromAlarm) {
                    if (intent.extras!!.get("NAME") != null) {
                        //if (forceLogoutDialog == null)
                        if (Pref.isAddAttendence)
                            callShopVisitConfirmationDialog(intent.extras!!.get("NAME") as String, intent.extras!!.get("ID") as String)
                        else {
                            Handler().postDelayed(Runnable {
                                checkToShowAddAttendanceAlert()
                            }, 1000)
                        }
                    } else {
                        if (intent.hasExtra("TYPE")) {
                            //Toaster.msgShort(this,intent.getStringExtra("TYPE").toString())
                            if (intent.getStringExtra("TYPE").equals("PUSH", ignoreCase = true))
                                pushStatus = 0
                            else if (intent.getStringExtra("TYPE").equals("DUE", ignoreCase = true)) {
                                if (getFragment() != null && getFragment() !is NearByShopsListFragment)
                                    loadFragment(FragType.NearByShopsListFragment, false, "")
                            }else if (intent.getStringExtra("TYPE").equals("ACTIVITYDUE", ignoreCase = true)) {
                                if (getFragment() != null && getFragment() !is LeadFrag)
                                    loadFragment(FragType.LeadFrag, false, "")
                            }
                            else if (intent.getStringExtra("TYPE").equals("TASK", ignoreCase = true)) {
                                if (getFragment() != null && getFragment() !is TaskListFragment)
                                    loadFragment(FragType.TaskListFragment, false, "")
                            }
                            else if (intent.getStringExtra("TYPE").equals("Msg", ignoreCase = true)) {
                                val chatUser = intent.getSerializableExtra("chatUser") as ChatUserDataModel
                                Handler().postDelayed(Runnable {
                                    if (getFragment() != null && getFragment() is ChatListFragment)
                                        onBackPressed()

                                    userName = chatUser.name
                                    loadFragment(FragType.ChatListFragment, true, chatUser)
                                }, 500)
                            }
                            else if (intent.getStringExtra("TYPE").equals("TIMESHEET", ignoreCase = true)) {
                                Handler().postDelayed(Runnable {
                                    if (getFragment() != null && getFragment() !is TimeSheetListFragment)
                                        loadFragment(FragType.TimeSheetListFragment, false, "")
                                }, 500)
                            }
                            else if (intent.getStringExtra("TYPE").equals("REIMBURSEMENT", ignoreCase = true)) {
                                Handler().postDelayed(Runnable {
                                    if (getFragment() != null && getFragment() !is ReimbursementListFragment)
                                        loadFragment(FragType.ReimbursementListFragment, false, "")
                                }, 500)
                            }
                            else if (intent.getStringExtra("TYPE").equals("VIDEO", ignoreCase = true)) {
                                Handler().postDelayed(Runnable {
                                    if (getFragment() != null && getFragment() !is MicroLearningListFragment)
                                        loadFragment(FragType.MicroLearningListFragment, false, "")
                                }, 500)
                            }
                            else if (intent.getStringExtra("TYPE").equals("clearData", ignoreCase = true)) {
                                isClearData = true

                                Handler().postDelayed(Runnable {
                                    if (getFragment() != null && getFragment() !is LogoutSyncFragment) {
                                        if (AppUtils.isOnline(this))
                                            loadFragment(FragType.LogoutSyncFragment, false, "")
                                        else
                                            showSnackMessage(getString(R.string.no_internet))
                                    }
                                }, 500)

                            } else if(intent.getStringExtra("TYPE").equals("LEAVE_APPLY", ignoreCase = true)) {
                                var usrID=intent.getStringExtra("USER_ID")!!

                                Handler().postDelayed(Runnable {
                                    if (getFragment() != null && getFragment() !is LeaveHome)
                                        loadFragment(FragType.LeaveHome, false, usrID)
                                }, 700)
                            }
                            else if(intent.getStringExtra("TYPE").equals("LEAVE_STATUS", ignoreCase = true)) {
                                var usrID=intent.getStringExtra("USER_ID")!!
                                Handler().postDelayed(Runnable {
                                    if (getFragment() != null && getFragment() !is LeaveHome)
                                    //loadFragment(FragType.LeaveHome, false, usrID)
                                        loadFragment(FragType.LeaveListFragment, false, "")
                                }, 700)
                            }  else if(intent.getStringExtra("TYPE").equals("quotation_approval", ignoreCase = true)) {
                                Handler().postDelayed(Runnable {
                                    if (getFragment() != null && getFragment() !is ViewAllQuotListFragment)
                                    loadFragment(FragType.MemberListFragment, false, Pref.user_id!!)
                                }, 700)
                            }else if(intent.getStringExtra("TYPE").equals("ZERO_COLL_STATUS", ignoreCase = true)) {
                                Handler().postDelayed(Runnable {
                                    if (getFragment() != null && getFragment() !is CollectionNotiViewPagerFrag1 && getFragment() !is CollectionNotiViewPagerFrag) {
                                        SendBrod.stopBrodColl(this)
                                        SendBrod.stopBrodZeroOrder(this)
                                        tv_noti_count.visibility=View.GONE

                                        if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification==false){
                                            loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                                        } else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false){
                                            loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                                        } else if(Pref.ShowCollectionAlert ==false && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification){
                                            loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                                        } else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false){
                                            loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                                        } else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification){
                                            loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                                        } else if(Pref.ShowCollectionAlert == false && Pref.ShowZeroCollectioninAlert  && Pref.IsShowRepeatOrderinNotification) {
                                            loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                                        } else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification){
                                            loadFragment(FragType.CollectionNotiViewPagerFrag2, true, "")
                                        }
                                    }
                                }, 500)
                            }
                            else {
                                showForceLogoutPopup()
                            }
                        }
                    }
                }
                intent = null
            }

            Handler().postDelayed(Runnable {
                if (getFragment() != null && getFragment() is GpsDisableFragment) {
                    onBackPressed()
                }
            }, 500)

            if (PermissionHelper.checkLocationPermission(this, 0)) {
                if (!FTStorageUtils.isMyServiceRunning(LocationFuzedService::class.java, this)) {
                    /*Start & Stop Expensive service stuff when logged out*/
                    serviceStatusActionable()
                    /*val serviceLauncher = Intent(this, LocationFuzedService::class.java)
                    startService(serviceLauncher)*/
                }
            }
        }
    }

    // GpsStatusDetectorCallBack
    override fun onGpsSettingStatus(enabled: Boolean) {

        if (enabled) {
            (mContext as DashboardActivity).showSnackMessage("GPS enabled")
        } else
            (mContext as DashboardActivity).showSnackMessage("GPS disabled")
    }

    override fun onGpsAlertCanceledByUser() {
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun syncShopListAndLogout() {
        Pref.logout_time = AppUtils.getCurrentTimeWithMeredian()
        Pref.logout_latitude = "0.0"
        Pref.logout_longitude = "0.0"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        finish()
        super.clearData()
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()

        var launchIntent: Intent? = packageManager.getLaunchIntentForPackage("com.anydesk.anydeskandroid")
        if(launchIntent!=null){
            anydesk_info_TV.text="Open Anydesk"
        }else{
            anydesk_info_TV.text="Install Anydesk"
        }

/*        if(DashboardFragment.hbRecorder ==null){
            screen_record_info_TV.text="Start Screen Recorder"
        }else{
            if(DashboardFragment.hbRecorder!!.isBusyRecording){
                screen_record_info_TV.text="Stop Recording"
            }else{
                screen_record_info_TV.text="Start Screen Recorder"
            }
        }*/

        if(DashboardFragment.isRecordRootVisible){
            screen_record_info_TV.text="Stop Recording"
        }else{
            screen_record_info_TV.text="Screen Recorder"
        }


        if (!isGpsDisabled)
            checkLocationMode()
        val networkIntentFilter = IntentFilter()
        networkIntentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        registerReceiver(gpsReceiver, networkIntentFilter);

        registerReceiver(broadcastReceiver, filter)
        //registerReceiver(geoFenceBroadcast, IntentFilter())
        //checkToShowAddAttendanceAlert()

        callUnreadNotificationApi()

        checkForFingerPrint()
    }


    fun checkForFingerPrint() {
        try {

            if (checkFingerPrint != null)
                checkFingerPrint = null

            AppUtils.changeLanguage(this, "en")

            checkFingerPrint = CheckFingerPrint()
            checkFingerPrint?.checkFingerPrint(this, object : CheckFingerPrint.FingerPrintListener {
                override fun isFingerPrintSupported(status: Boolean) {
                    if (status) {
                        Log.e("DashboardActivity", "========Device support fingerprint===========")
                    } else {
                        Log.e("DashboardActivity", "==========Device does not support fingerprint===========")
                        isFingerPrintSupported = false
                    }
                }

                override fun onSuccess(signal: CancellationSignal?) {

                    /*if (signal?.isCanceled!!) {
                        signal.cancel()
                    }*/
                    AppUtils.changeLanguage(this@DashboardActivity, "en")
                    Log.e("DashboardActivity", "============Fingerprint accepted=============")

                    if (AppUtils.isRevisit!!) {
                        if (fingerprintDialog != null && fingerprintDialog?.isVisible!!) {
                            fingerprintDialog?.dismiss()
                            revisitShop(revisitImage)
                        }
                    } else {
                        if (getFragment() != null) {
                            if (getFragment() is AddAttendanceFragment) {
                                (getFragment() as AddAttendanceFragment).continueAddAttendance()
                            } else if (getFragment() is DailyPlanListFragment) {
                                (getFragment() as DailyPlanListFragment).continueAddAttendance()
                            } else if (getFragment() is AddShopFragment) {
                                (getFragment() as AddShopFragment).addShop()
                            }
                        }
                    }

                    if (getFragment() != null && getFragment() is ChatBotFragment)
                        AppUtils.changeLanguage(this@DashboardActivity, (getFragment() as ChatBotFragment).language)
                }

                override fun onError(msg: String) {
                    Log.e("DashboardActivity", "Fingerprint error=====> $msg")

                    if (!Locale.getDefault().language.equals("en", ignoreCase = true))
                        return

                    when {
                        msg.equals("Fingerprint operation cancelled.", ignoreCase = true) -> {
                        }
                        msg.equals("Fingerprint operation cancelled", ignoreCase = true) -> {
                        }
                        msg.equals("Fingerprint operation canceled", ignoreCase = true) -> {
                        }
                        msg.equals("Fingerprint operation canceled.", ignoreCase = true) -> {
                        }
                        else -> Toaster.msgLong(mContext, msg)
                    }

                    if (getFragment() != null && getFragment() is ChatBotFragment)
                        AppUtils.changeLanguage(this@DashboardActivity, (getFragment() as ChatBotFragment).language)
                }

            })

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CheckFingerPrint().FingerprintHandler().doAuth()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun callUnreadNotificationApi() {

        if (!AppUtils.isOnline(this))
            return

        val repository = UnreadNotificationRepoProvider.unreadNotificationRepoProvider()
        //progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.unreadNotification()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UnreadNotificationResponseModel
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.isUnreadNotificationPresent.equals("true", ignoreCase = true))
                                    logo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake))
                                else {
                                    logo.clearAnimation()
                                    logo.animate().cancel()
                                }
                            }
                        }, { error ->
                            error.printStackTrace()
                        })
        )
    }

    fun checkToShowAddAttendanceAlert() {
        var inTime = ""
        var outTime = ""

        /*if (AppUtils.getCurrentTimeWithMeredian().contains("AM") || AppUtils.getCurrentTimeWithMeredian().contains("PM")) {
            inTime = "8:00 AM"
            outTime = "11:59 PM"
        } else if (AppUtils.getCurrentTimeWithMeredian().contains("a.m.") || AppUtils.getCurrentTimeWithMeredian().contains("p.m")) {
            inTime = "8:00 a.m."
            outTime = "11:59 p.m."
        } else if (AppUtils.getCurrentTimeWithMeredian().contains("am") || AppUtils.getCurrentTimeWithMeredian().contains("pm")) {
            inTime = "8:00 am"
            outTime = "11:59 pm"
        } else if (AppUtils.getCurrentTimeWithMeredian().contains("A.M.") || AppUtils.getCurrentTimeWithMeredian().contains("P.M.")) {
            inTime = "8:00 A.M."
            outTime = "11:59 P.M."
        }*/

        //Pref.isAddAttendence = false
        /*if (AppUtils.convertDateTimeWithMeredianToLong(AppUtils.getCurrentTimeWithMeredian()) >= AppUtils.convertDateTimeWithMeredianToLong(inTime)
                && AppUtils.convertDateTimeWithMeredianToLong(AppUtils.getCurrentTimeWithMeredian()) <= AppUtils.convertDateTimeWithMeredianToLong(outTime)) {*/

        Handler().postDelayed(Runnable {
            if (!Pref.isAddAttendence && !Pref.isAutoLogout && forceLogoutDialog == null) {
                isAttendanceAlertPresent = true
                showAddAttendanceAlert()
            } else {
                isAttendanceAlertPresent = false

                if (isOrderDialogShow)
                    showOrderCollectionAlert(isOrderAdded, isCollectionAdded)
            }
        }, 250)


        /*} else {
            isAttendanceAlertPresent = false
        }*/
    }

    var appliedLeaveList: ArrayList<Leave_list_Response> = ArrayList()

    private var dialog: CommonDialog? = null
    private fun showAddAttendanceAlert() {
        /*CommonDialogSingleBtn.getInstance(getString(R.string.attendance_alert), getString(R.string.attendance_msg_bdy), getString(R.string.ok), object : OnDialogClickListener {
            override fun onOkClick() {
                if (!isGpsDisabled) {
                    isAddAttendaceAlert = true
                    loadFragment(FragType.AddAttendanceFragment, true, "")
                }
                //else
                //    showAddAttendanceAlert()
            }
        }).show(supportFragmentManager, "CommonDialogSingleBtn")*/


        if (dialog == null || !dialog?.isVisible!!) {
            dialog = CommonDialog.getInstance(AppUtils.hiFirstNameText() + "!", getString(R.string.attendance_msg_bdy), /*getString(R.string.cancel),
                    getString(R.string.ok),*/ true, object : CommonDialogClickListener {
                override fun onLeftClick() {
                }

                override fun onRightClick(editableData: String) {
                    if (!isGpsDisabled) {

                        /*if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude)) {
                            if (Pref.isHomeLocAvailable) {

                                if (!TextUtils.isEmpty(Pref.home_latitude) && !TextUtils.isEmpty(Pref.home_longitude)) {
                                    val distance = LocationWizard.getDistance(Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble(), Pref.current_latitude.toDouble(),
                                            Pref.current_longitude.toDouble())

                                    XLog.e("Distance from home====> $distance")

                                    if (distance * 1000 > 50) {
                                        isAddAttendaceAlert = true
                                        loadFragment(FragType.AddAttendanceFragment, true, "")
                                    } else
                                        (mContext as DashboardActivity).showSnackMessage("Attendance can not be added from home")
                                } else {
                                    XLog.e("========Home location is not available========")
                                    isAddAttendaceAlert = true
                                    loadFragment(FragType.AddAttendanceFragment, true, "")
                                }

                            } else {
                                XLog.e("========isHomeLocAvailable is false========")
                                isAddAttendaceAlert = true
                                loadFragment(FragType.AddAttendanceFragment, true, "")
                            }
                        } else {
                            XLog.e("========Current location is not available========")*/
                        isAddAttendaceAlert = true

                        val attendanceReq = AttendanceRequest()
                        attendanceReq.user_id = Pref.user_id!!
                        attendanceReq.session_token = Pref.session_token
                        attendanceReq.start_date = AppUtils.getCurrentDateForCons()
                        attendanceReq.end_date = AppUtils.getCurrentDateForCons()

                        val repository = AttendanceRepositoryProvider.provideAttendanceRepository()
                        progress_wheel.spin()
                        BaseActivity.compositeDisposable.add(
                                repository.getAttendanceList(attendanceReq)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.io())
                                        .subscribe({ result ->
                                            val attendanceList = result as AttendanceResponse
                                            if (attendanceList.status == "205") {
                                                progress_wheel.stopSpinning()
                                                loadFragment(FragType.AddAttendanceFragment, true, "")
                                            } else if (attendanceList.status == NetworkConstant.SUCCESS) {
                                                progress_wheel.stopSpinning()
                                                Pref.isAddAttendence = true
                                                (mContext as DashboardActivity).showSnackMessage("${AppUtils.hiFirstNameText()}. Attendance already marked for the day.")
                                            }

                                        }, { error ->
                                            progress_wheel.stopSpinning()
                                            error.printStackTrace()
                                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                        })
                        )


                        //loadFragment(FragType.AddAttendanceFragment, true, "")
                        //}
                    }
                }

            })//.show(supportFragmentManager, "")
            dialog?.show(supportFragmentManager, "")
        }
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AppUtils.gpsDisabledAction) {
                isGpsDisabled = true
                loadFragment(FragType.GpsDisableFragment, true, "")

                /*val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps()
                }*/

            } else {
                isGpsDisabled = false
                if (getFragment() != null && getFragment() is GpsDisableFragment) {
                    onBackPressed()
                }
            }
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

//        try {
//            /*LocalBroadcastManager.getInstance(this).*/unregisterReceiver(idealLocReceiver)
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(fcmReceiver)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
    }


    override fun onDestroy() {

        DashboardFragment.isRecordRootVisible=false

        textToSpeech?.let {
            it.stop()
            it.shutdown()
        }

        unregisterReceiver(broadcastReceiver)

        if (gpsReceiver != null)
            unregisterReceiver(gpsReceiver)

        //Code by wasim
        if (mAlarmReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mAlarmReceiver)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        if (idealLocReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(idealLocReceiver)
        }

        if (fcmReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(fcmReceiver)


        if (fcmReceiver_leave != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(fcmReceiver_leave)

        if (fcmReceiver_quotation_approval != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(fcmReceiver_quotation_approval)


        if (collectionAlertReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(collectionAlertReceiver)


        if (forceLogoutReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(forceLogoutReceiver)

        if (autoRevisit != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(autoRevisit)

        if (chatReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(chatReceiver)

        if (updateStatusReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(updateStatusReceiver)

        if (localeReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localeReceiver)

        if (homeLocReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(homeLocReceiver)

        if (revisitReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(revisitReceiver)

        if(updatePJP!=null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(updatePJP)

        super.onDestroy()
        /*if (geoFenceBroadcast != null)
            unregisterReceiver(geoFenceBroadcast)*/
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        onFireAlarm(intent!!)

        if (intent == null || intent.extras == null)
            return
        var shopName = ""
        var shopId = ""
        if (intent.extras!!.get("NAME") != null)
            shopName = intent.extras!!.get("NAME") as String
        if (intent.extras!!.get("ID") != null)
            shopId = intent.extras!!.get("ID") as String
        if (intent.extras!!.getString("ACTION").equals("CANCEL", true))
            cancelNotification(shopId)
        else if (shopName.isNotBlank() && shopId.isNotBlank() && !isGpsDisabled) {
            //if (forceLogoutDialog == null) {
            if (Pref.isAddAttendence)
                callShopVisitConfirmationDialog(intent.extras!!.get("NAME") as String, intent.extras!!.get("ID") as String)
            else {
                Handler().postDelayed(Runnable {
                    checkToShowAddAttendanceAlert()
                }, 1000)
            }
            //}
        } else if (intent.hasExtra("TYPE")) {
            var tt=intent.getStringExtra("TYPE")
            //logo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake))
            if (intent.getStringExtra("TYPE").equals("PUSH", ignoreCase = true)) {
                if (forceLogoutDialog == null)
                    loadFragment(FragType.NotificationFragment, true, "")
            } else if (intent.getStringExtra("TYPE").equals("DUE", ignoreCase = true)) {
                if (getFragment() != null && getFragment() !is NearByShopsListFragment)
                    loadFragment(FragType.NearByShopsListFragment, false, "")
            }else if (intent.getStringExtra("TYPE").equals("ACTIVITYDUE", ignoreCase = true)) {
                if (getFragment() != null && getFragment() !is LeadFrag)
                    loadFragment(FragType.LeadFrag, false, "")
            }
            else if (intent.getStringExtra("TYPE").equals("TASK", ignoreCase = true)) {
                if (getFragment() != null && getFragment() !is TaskListFragment)
                    loadFragment(FragType.TaskListFragment, false, "")
            }
            else if (intent.getStringExtra("TYPE").equals("Msg", ignoreCase = true)) {
                if (getFragment() != null && getFragment() is ChatListFragment)
                    onBackPressed()
                val chatUser = intent.getSerializableExtra("chatUser") as ChatUserDataModel
                userName = chatUser.name
                loadFragment(FragType.ChatListFragment, true, chatUser)
            }
            else if (intent.getStringExtra("TYPE").equals("TIMESHEET", ignoreCase = true)) {
                if (getFragment() != null && getFragment() !is TimeSheetListFragment)
                    loadFragment(FragType.TimeSheetListFragment, false, "")
            }
            else if (intent.getStringExtra("TYPE").equals("REIMBURSEMENT", ignoreCase = true)) {
                if (getFragment() != null && getFragment() !is ReimbursementListFragment)
                    loadFragment(FragType.ReimbursementListFragment, false, "")
            }
            else if (intent.getStringExtra("TYPE").equals("VIDEO", ignoreCase = true)) {
                if (getFragment() != null && getFragment() !is MicroLearningListFragment)
                    loadFragment(FragType.MicroLearningListFragment, false, "")
            }
            else if (intent.getStringExtra("TYPE").equals("clearData", ignoreCase = true)) {
                isClearData = true
                Handler().postDelayed(Runnable {
                    if (getFragment() != null && getFragment() !is LogoutSyncFragment) {
                        if (AppUtils.isOnline(this))
                            loadFragment(FragType.LogoutSyncFragment, false, "")
                        else
                            showSnackMessage(getString(R.string.no_internet))
                    }
                }, 500)
            }
            else if(intent.getStringExtra("TYPE").equals("LEAVE_APPLY", ignoreCase = true)) {
                var usrID=intent.getStringExtra("USER_ID")!!
                Handler().postDelayed(Runnable {
                    loadFragment(FragType.LeaveHome, false, usrID)
                }, 300)
                //loadFragment(FragType.LeaveHome, false, intent.getStringExtra("USER_ID")!!)
            }
            else if(intent.getStringExtra("TYPE").equals("LEAVE_STATUS", ignoreCase = true)) {
                var usrID=intent.getStringExtra("USER_ID")!!
                Handler().postDelayed(Runnable {
                    //loadFragment(FragType.LeaveHome, false, usrID)
                    loadFragment(FragType.LeaveListFragment, false, "")
                }, 300)
                //loadFragment(FragType.LeaveHome, false, intent.getStringExtra("USER_ID")!!)
            }else if(intent.getStringExtra("TYPE").equals("quotation_approval", ignoreCase = true)) {
                Handler().postDelayed(Runnable {
                    if (getFragment() != null && getFragment() !is ViewAllQuotListFragment)
                        loadFragment(FragType.MemberListFragment, false, Pref.user_id!!)
                }, 700)
            }else if(intent.getStringExtra("TYPE").equals("ZERO_COLL_STATUS", ignoreCase = true)) {
                Handler().postDelayed(Runnable {
                    if (getFragment() != null && getFragment() !is CollectionNotiViewPagerFrag1 && getFragment() !is CollectionNotiViewPagerFrag) {
                        SendBrod.stopBrodColl(this)
                        SendBrod.stopBrodZeroOrder(this)
                        tv_noti_count.visibility=View.GONE

                        if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification==false){
                            loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                        } else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false){
                            loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                        } else if(Pref.ShowCollectionAlert ==false && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification){
                            loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                        } else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false){
                            loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                        } else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification){
                            loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                        } else if(Pref.ShowCollectionAlert == false && Pref.ShowZeroCollectioninAlert  && Pref.IsShowRepeatOrderinNotification) {
                            loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                        } else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification){
                            loadFragment(FragType.CollectionNotiViewPagerFrag2, true, "")
                        }

                    }
                }, 500)
            }
            else {
                showForceLogoutPopup()
            }
        }
    }

    private fun loadHomeFragment() {
        //Code by wasim
        if (isFromAlarm) {
            navigateFragmentByReportId(alarmCofifDataModel, false)
        } else if (isClearData) {
            if (AppUtils.isOnline(this))
                loadFragment(FragType.LogoutSyncFragment, false, "")
            else
                showSnackMessage(getString(R.string.no_internet))
        } else {
            loadFragment(FragType.DashboardFragment, false, DashboardType.Home)

            if (Pref.isSefieAlarmed)
                showSelfieDialog()
        }

    }

    private fun initView() {
        ta_tv = findViewById(R.id.ta_tv)
        view_pp_dd_tv = findViewById(R.id.view_pp_dd_tv)
        iv_shopImage = findViewById(R.id.iv_shopImage)
        progress_wheel = findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        profile_name_TV = findViewById(R.id.profile_name_TV)
        profile_name_TV.text = Pref.user_name
        addOrderTV = findViewById(R.id.add_order_TV)  // Home menu
        orderHistoryTV = findViewById(R.id.order_history_TV) // Timeline menu
        addTravelAllowenceTV = findViewById(R.id.add_travel_allowence_TV)  // Shop menu
        settingsTV = findViewById(R.id.settings_TV) // Order menu
        myAllowRequest = findViewById(R.id.my_allowence_request_TV)  // Attendance menu
        add_attendence_tv = findViewById(R.id.add_attendence_tv)
        tv_performance = findViewById(R.id.tv_performance)
        iv_delete_icon = findViewById(R.id.iv_delete_icon)
        rl_cart = findViewById(R.id.rl_cart)
        tv_cart_count = findViewById(R.id.tv_cart_count)
        iv_filter_icon = findViewById(R.id.iv_filter_icon)
        rl_confirm_btn = findViewById(R.id.rl_confirm_btn)
        tv_pp_dd_outstanding = findViewById(R.id.tv_pp_dd_outstanding)

        returnTV = findViewById(R.id.return_TV)


        /*if (AppUtils.getCurrentTimeWithMeredian() >= "8:00 AM" && AppUtils.getCurrentTimeWithMeredian() <= "10:30 AM")
            add_attendence_tv.visibility = View.VISIBLE
        else
            add_attendence_tv.visibility = View.GONE*/

        logoutTV = findViewById(R.id.logout_TV)
        bottomBar = findViewById(R.id.include_bottom_tab)
        menuMis = findViewById(R.id.mis_TV)
        login_time_tv = findViewById(R.id.login_time_tv)
        login_time_tv.text = Pref.login_time
        login_time_am_tv = findViewById(R.id.login_time_am_tv)
        profilePicture = findViewById(R.id.iv_profile_picture)
        shareLogs = findViewById(R.id.share_log_TV)
        reimbursement_tv = findViewById(R.id.reimbursement_TV)
        achievement_tv = findViewById(R.id.achievement_TV)
        collection_TV = findViewById(R.id.collection_TV)
        state_report_TV = findViewById(R.id.state_report_TV) // performance menu
        iv_list_party = findViewById(R.id.iv_list_party)
        quo_TV = findViewById(R.id.quo_TV)

        if (profilePicture != null && Pref.profile_img != null && Pref.profile_img.trim().isNotEmpty()) {
            // Picasso.with(this).load(Pref.user_profile_img).into(profilePicture)
            /*Picasso.get()
                    .load(Pref.profile_img)
                    .resize(100, 100)
                    .placeholder(R.drawable.ic_menu_profile_image)
                    .error(R.drawable.ic_menu_profile_image)
                    .into(profilePicture, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError(e: java.lang.Exception?) {
                            e?.printStackTrace()
                            //profilePicture.setImageResource(R.drawable.ic_menu_profile_image)
                        }
                    })*/


            Glide.with(mContext)
                    .load(Pref.profile_img)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_menu_profile_image).error(R.drawable.ic_menu_profile_image))
                    .into(profilePicture)

        } else
            profilePicture.setImageResource(R.drawable.ic_menu_profile_image)

        login_time_am_tv.text = Pref.merediam
        version_name_TV = findViewById(R.id.version_name_TV)
        version_name_TV.text = AppUtils.getVersionName(this@DashboardActivity)
        iv_sync_icon = findViewById(R.id.iv_sync_icon)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawerlayout)
        toolbar = findViewById<Toolbar>(R.id.toolbar)
        searchView = findViewById<MaterialSearchView>(R.id.search_view)
        iv_search_icon = findViewById<ImageView>(R.id.iv_search_icon)
        home_IV = findViewById<ImageView>(R.id.home_IV)
        add_shop_IV = findViewById<ImageView>(R.id.add_shop_IV)
        nearby_shops_IV = findViewById<ImageView>(R.id.nearby_shops_IV)
        my_orders_IV = findViewById<ImageView>(R.id.my_orders_IV)
        headerTV = findViewById<AppCustomTextView>(R.id.tv_header)
        tickTV = findViewById<ImageView>(R.id.iv_tick_icon)
        logo = findViewById(R.id.logo)
        tv_noti_count = findViewById(R.id.tv_noti_count)
        maps_TV = findViewById(R.id.maps_TV)
        target_TV = findViewById(R.id.target_TV) // List of party menu
        rl_report = findViewById(R.id.rl_report)
        iv_drop_down_icon = findViewById(R.id.iv_drop_down_icon)
        ll_report_list = findViewById(R.id.ll_report_list)
        tv_attendance_report = findViewById(R.id.tv_attendance_report)
        tv_performance_report = findViewById(R.id.tv_performance_report)
        tv_visit_report = findViewById(R.id.tv_visit_report)
        meeting_TV = findViewById(R.id.meeting_TV)
        team_TV = findViewById(R.id.team_TV)
        iv_map = findViewById(R.id.iv_map)
        timesheet_TV = findViewById(R.id.timesheet_TV)
        //logo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake))
        update_worktype_tv = findViewById(R.id.update_worktype_tv)
        dynamic_TV = findViewById(R.id.dynamic_TV)

        imageArrayList.add(home_IV)
        imageArrayList.add(add_shop_IV)
        imageArrayList.add(nearby_shops_IV)
        imageArrayList.add(my_orders_IV)

        home_TV = findViewById<AppCustomTextView>(R.id.home_TV)
        add_shop_TV = findViewById<AppCustomTextView>(R.id.add_shop_TV)
        nearby_shops_TV = findViewById<AppCustomTextView>(R.id.nearby_shops_TV)
        my_orders_TV = findViewById<AppCustomTextView>(R.id.my_orders_TV)
        nearbyShops = findViewById<AppCustomTextView>(R.id.nearby_shop_TV)
        assignedLead = findViewById<AppCustomTextView>(R.id.assigned_lead_TV)
        surveyMenu = findViewById<AppCustomTextView>(R.id.assigned_survey_TV)

        textArrayList.add(home_TV)
        textArrayList.add(add_shop_TV)
        textArrayList.add(nearby_shops_TV)
        textArrayList.add(my_orders_TV)

        tv_location = findViewById(R.id.tv_location)
        home_RL = findViewById<RelativeLayout>(R.id.home_RL)
        add_shop_RL = findViewById<RelativeLayout>(R.id.add_shop_RL)
        nearby_shops_RL = findViewById<RelativeLayout>(R.id.nearby_shops_RL)
        my_orders_RL = findViewById<RelativeLayout>(R.id.my_orders_RL)
        iv_home_icon = findViewById(R.id.iv_home_icon)
        iv_home_icon.setOnClickListener(this)
        tv_change_pwd = findViewById(R.id.tv_change_pwd)
        all_team_TV = findViewById(R.id.all_team_TV)
        achv_TV = findViewById(R.id.achv_TV)
        targ_achv_TV = findViewById(R.id.targ_achv_TV)
        leave_tv = findViewById(R.id.leave_tv)  //Apply Leave menu
        task_TV = findViewById(R.id.task_TV)
        activity_TV = findViewById(R.id.activity_TV)
        rl_collection = findViewById(R.id.rl_collection)
        iv_collection_drop_down_icon = findViewById(R.id.iv_collection_drop_down_icon)
        ll_collection_list = findViewById(R.id.ll_collection_list)
        tv_report = findViewById(R.id.tv_report)
        tv_entry = findViewById(R.id.tv_entry)
        share_loc_TV = findViewById(R.id.share_loc_TV)
        iv_settings = findViewById(R.id.iv_settings)
        weather_TV = findViewById(R.id.weather_TV)
        doc_TV = findViewById(R.id.doc_TV)
        chat_bot_TV = findViewById(R.id.chat_bot_TV)
        distributor_wise_order_list_TV = findViewById(R.id.distributor_wise_order_list_TV)
        alert_snack_bar = findViewById(R.id.alert_snack_bar)
        ic_calendar = findViewById(R.id.ic_calendar)
        ic_chat_bot = findViewById(R.id.ic_chat_bot)
        iv_cancel_chat = findViewById(R.id.iv_cancel_chat)
        chat_TV = findViewById(R.id.chat_TV)
        iv_people = findViewById(R.id.iv_people)
        tv_confirm_btn = findViewById(R.id.tv_confirm_btn)
        iv_scan = findViewById(R.id.iv_scan)
        iv_view_text = findViewById(R.id.iv_view_text)
        scan_TV = findViewById(R.id.scan_TV)
        nearby_user_TV = findViewById(R.id.nearby_user_TV) // Near by team member menu
        fl_net_status = findViewById(R.id.fl_net_status)
        home_loc_TV = findViewById(R.id.home_loc_TV)
        device_info_TV = findViewById(R.id.device_info_TV)
        permission_info_TV = findViewById(R.id.permission_info_TV)
        anydesk_info_TV = findViewById(R.id.anydesk_info_TV)
        screen_record_info_TV = findViewById(R.id.screen_record_info_TV)
        check_custom_status_TV = findViewById(R.id.check_custom_status_TV)
        micro_learning_TV = findViewById(R.id.micro_learning_TV)

        photo_registration = findViewById(R.id.photo_registration)
        photo_team_attendance = findViewById(R.id.photo_team_attendance)

        tv_clear_attendance = findViewById(R.id.tv_clear_attendance)

        home_RL.setOnClickListener(this)
        add_shop_RL.setOnClickListener(this)
        nearby_shops_RL.setOnClickListener(this)
        my_orders_RL.setOnClickListener(this)
        addOrderTV.setOnClickListener(this)
        orderHistoryTV.setOnClickListener(this)
        addTravelAllowenceTV.setOnClickListener(this)
        settingsTV.setOnClickListener(this)
        myAllowRequest.setOnClickListener(this)
        logoutTV.setOnClickListener(this)
        menuMis.setOnClickListener(this)
        tickTV.setOnClickListener(this)
        logo.setOnClickListener(this)
        nearbyShops.setOnClickListener(this)
        assignedLead.setOnClickListener(this)
        surveyMenu.setOnClickListener(this)
        shareLogs.setOnClickListener(this)
        reimbursement_tv.setOnClickListener(this)
        achievement_tv.setOnClickListener(this)
        iv_search_icon.setOnClickListener(this)
        profilePicture.setOnClickListener(this)
        maps_TV.setOnClickListener(this)
        iv_sync_icon.setOnClickListener(this)
        add_attendence_tv.setOnClickListener(this)
        ta_tv.setOnClickListener(this)
        view_pp_dd_tv.setOnClickListener(this)
        tv_performance.setOnClickListener(this)
        iv_delete_icon.setOnClickListener(this)
        iv_filter_icon.setOnClickListener(this)
        rl_confirm_btn.setOnClickListener(this)
        tv_pp_dd_outstanding.setOnClickListener(this)
        tv_location.setOnClickListener(this)
        //collection_TV.setOnClickListener(this)
        state_report_TV.setOnClickListener(this)
        target_TV.setOnClickListener(this)
        iv_list_party.setOnClickListener(this)
        rl_report.setOnClickListener(this)
        tv_visit_report.setOnClickListener(this)
        tv_performance_report.setOnClickListener(this)
        tv_attendance_report.setOnClickListener(this)
        meeting_TV.setOnClickListener(this)
        team_TV.setOnClickListener(this)
        iv_map.setOnClickListener(this)
        timesheet_TV.setOnClickListener(this)
        tv_change_pwd.setOnClickListener(this)
        quo_TV.setOnClickListener(this)
        all_team_TV.setOnClickListener(this)
        update_worktype_tv.setOnClickListener(this)
        achv_TV.setOnClickListener(this)
        targ_achv_TV.setOnClickListener(this)
        leave_tv.setOnClickListener(this)
        task_TV.setOnClickListener(this)
        dynamic_TV.setOnClickListener(this)
        activity_TV.setOnClickListener(this)
        rl_collection.setOnClickListener(this)
        tv_report.setOnClickListener(this)
        tv_entry.setOnClickListener(this)
        share_loc_TV.setOnClickListener(this)
        iv_settings.setOnClickListener(this)
        weather_TV.setOnClickListener(this)
        doc_TV.setOnClickListener(this)
        chat_bot_TV.setOnClickListener(this)
        distributor_wise_order_list_TV.setOnClickListener(this)
        ic_calendar.setOnClickListener(this)
        ic_chat_bot.setOnClickListener(this)
        iv_cancel_chat.setOnClickListener(this)
        chat_TV.setOnClickListener(this)
        iv_people.setOnClickListener(this)
        iv_scan.setOnClickListener(this)
        iv_view_text.setOnClickListener(this)
        scan_TV.setOnClickListener(this)
        nearby_user_TV.setOnClickListener(this)
        fl_net_status.setOnClickListener(this)
        home_loc_TV.setOnClickListener(this)
        device_info_TV.setOnClickListener(this)
        permission_info_TV.setOnClickListener(this)
        anydesk_info_TV.setOnClickListener(this)
        screen_record_info_TV.setOnClickListener(this)
        check_custom_status_TV.setOnClickListener(this)
        micro_learning_TV.setOnClickListener(this)

        photo_registration.setOnClickListener(this)
        photo_team_attendance.setOnClickListener(this)
        assignedLead.setOnClickListener(this)
        surveyMenu.setOnClickListener(this)
        tv_clear_attendance.setOnClickListener(this)

        drawerLL=findViewById(R.id.activity_dashboard_lnr_lyt_slide_view)
        drawerLL.setOnClickListener(this)

        rl_cart.setOnClickListener(this) //06-09-2021

        returnTV.setOnClickListener(this)

        toolbar.contentInsetStartWithNavigation = 0
        toolbar.setPadding(0, toolbar.paddingTop, 0, toolbar.paddingBottom)
        toolbar.setTitle(R.string.blank)
        toolbar.setSubtitle(R.string.blank)

        simpleDialogProcess = Dialog(mContext)
        simpleDialogProcess.setCancelable(false)
        simpleDialogProcess.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialogProcess.setContentView(R.layout.dialog_message)
        dialogHeaderProcess = simpleDialogProcess.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
        dialog_yes_no_headerTVProcess = simpleDialogProcess.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView


        mDrawerToggle = object : ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.blank, R.string.blank) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                /*if (slideOffset == 0.toFloat()
                        && getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_STANDARD) {
                    // drawer closed
                    getActionBar()
                            .setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    invalidateOptionsMenu();
                } else if (slideOffset != 0.toFloat()
                        && getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS) {
                    // started opening
                    AppUtils.hideSoftKeyboard(this@DashboardActivity)
                    getActionBar()
                            .setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    invalidateOptionsMenu();
                }*/
                AppUtils.hideSoftKeyboard(this@DashboardActivity)

                if (getFragment() != null && getFragment() is ReimbursementListFragment) {
                    if ((getFragment() as ReimbursementListFragment).mPopupWindow != null && (getFragment() as ReimbursementListFragment).mPopupWindow!!.isShowing)
                        (getFragment() as ReimbursementListFragment).mPopupWindow?.dismiss()

                    if ((getFragment() as ReimbursementListFragment).conveyancePopupWindow != null &&
                            (getFragment() as ReimbursementListFragment).conveyancePopupWindow!!.isShowing)
                        (getFragment() as ReimbursementListFragment).conveyancePopupWindow?.dismiss()
                }

                iv_drop_down_icon.isSelected = false
                ll_report_list.visibility = View.GONE

                iv_collection_drop_down_icon.isSelected = false
                ll_collection_list.visibility = View.GONE

                super.onDrawerSlide(drawerView, slideOffset)
            }
        }
        drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.isDrawerIndicatorEnabled = true
        mDrawerToggle.toolbarNavigationClickListener = View.OnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
        }
        mDrawerToggle.syncState()

        toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
        setSupportActionBar(toolbar)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
//                if (searchListener==null)
//                    return false
//                searchListener?.onSearchQueryListener(query)
//                if(getCurrentFragType()== FragType.NearByShopsListFragment){
//                    (getFragment() as NearByShopsListFragment).setSearchListener(object : SearchListener {
//                        override fun onSearchQueryListener(query: String) {
//                            Toast.makeText(mContext, query, Toast.LENGTH_SHORT).show()
//                        }
//
//                    })
//                }
//                Toast.makeText(this@DashboardActivity, query, Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
//                if(newText.isEmpty()){
////                    searchView.clearSuggestions()
//                    return false
//                }
//                Toast.makeText(this@DashboardActivity, newText, Toast.LENGTH_SHORT).show()
                if (searchListener == null)
                    return false
                searchListener?.onSearchQueryListener(newText.trim())


//                val arr = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopBySearchData(newText)
//                searchView.addSuggestions(arr)
//                Toast.makeText(this@DashboardActivity, newText, Toast.LENGTH_SHORT).show()
                return false
            }
        })

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val ttsLang = textToSpeech.setLanguage(Locale.US)

                if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED)
                    Log.e("Dashboard Activity", "TTS Language is not supported!")
                else
                    Log.e("Dashboard Activity", "TTS Language Supported.")

                Log.e("Dashboard Activity", "TTS Initialization success.")
            } else
                Log.e("Dashboard Activity", "TTS Initialization failed!")
        })
    }

    fun updateUI() {
        rl_report.apply {
            visibility = if (Pref.willReportShow)
                View.VISIBLE
            else
                View.GONE
        }


        addTravelAllowenceTV.text = Pref.shopText + "(s)"
        nearbyShops.text = "Nearby " + Pref.shopText + "(s)"

        /*if (Pref.isReplaceShopText) {
            addTravelAllowenceTV.text = getString(R.string.customers)
            nearbyShops.text = getString(R.string.nearby_customer)
        } else {
            addTravelAllowenceTV.text = getString(R.string.shops)
            nearbyShops.text = getString(R.string.nearby_shops)
        }*/


        if (Pref.isMeetingAvailable)
            meeting_TV.visibility = View.VISIBLE
        else
            meeting_TV.visibility = View.GONE

        if (Pref.willShowTeamDetails)
            team_TV.visibility = View.VISIBLE
        else
            team_TV.visibility = View.GONE

        if (Pref.willReimbursementShow)
            reimbursement_tv.visibility = View.VISIBLE
        else
            reimbursement_tv.visibility = View.GONE

        if (Pref.willShowUpdateDayPlan) {
            target_TV.visibility = View.VISIBLE
            target_TV.text = Pref.dailyPlanListHeaderText
        } else
            target_TV.visibility = View.GONE

        if (Pref.isCollectioninMenuShow)
            rl_collection.visibility = View.VISIBLE
        else
            rl_collection.visibility = View.GONE

        if (Pref.willKnowYourStateShow)
            state_report_TV.visibility = View.VISIBLE
        else
            state_report_TV.visibility = View.GONE

        if (Pref.willAttendanceReportShow)
            tv_attendance_report.visibility = View.VISIBLE
        else
            tv_attendance_report.visibility = View.GONE

        if (Pref.willPerformanceReportShow)
            tv_performance_report.visibility = View.VISIBLE
        else
            tv_performance_report.visibility = View.GONE

        if (Pref.willVisitReportShow)
            tv_visit_report.visibility = View.VISIBLE
        else
            tv_visit_report.visibility = View.GONE

        if (Pref.willTimesheetShow)
            timesheet_TV.visibility = View.VISIBLE
        else
            timesheet_TV.visibility = View.GONE

        if (Pref.isOrderShow)
            settingsTV.visibility = View.VISIBLE
        else
            settingsTV.visibility = View.GONE

        if (Pref.isVisitShow) {
            if (!Pref.isServiceFeatureEnable)
                nearbyShops.visibility = View.VISIBLE
            else
                nearbyShops.visibility = View.GONE

            addTravelAllowenceTV.visibility = View.VISIBLE
        } else {
            addTravelAllowenceTV.visibility = View.GONE
            nearbyShops.visibility = View.GONE
        }

        if (Pref.isAttendanceFeatureOnly) {
            //addTravelAllowenceTV.visibility = View.GONE
            //nearbyShops.visibility = View.GONE
            logoutTV.text = getString(R.string.logout)
            //settingsTV.visibility = View.GONE
        } else {
            //addTravelAllowenceTV.visibility = View.VISIBLE
            //nearbyShops.visibility = View.VISIBLE
            logoutTV.text = getString(R.string.sync_logout)
            //settingsTV.visibility = View.VISIBLE
        }

        if (Pref.isChangePasswordAllowed)
            tv_change_pwd.visibility = View.VISIBLE
        else
            tv_change_pwd.visibility = View.GONE


        if (Pref.isQuotationShow)
            quo_TV.visibility = View.VISIBLE
        else
            quo_TV.visibility = View.GONE

        if (Pref.isAllTeamAvailable)
            all_team_TV.visibility = View.VISIBLE
        else
            all_team_TV.visibility = View.GONE

        update_worktype_tv.apply {
            visibility = if (!Pref.isAddAttendence)
                View.GONE
            else {
                if (Pref.isOnLeave.equals("true", ignoreCase = true))
                    View.GONE
                else {
                    if (Pref.isUpdateWorkTypeEnable)
                        View.VISIBLE
                    else
                        View.GONE
                }
            }
        }

        if (Pref.isLeaveEnable)
            leave_tv.visibility = View.VISIBLE
        else
            leave_tv.visibility = View.GONE

        if (Pref.isAchievementEnable)
            achv_TV.visibility = View.VISIBLE
        else
            achv_TV.visibility = View.GONE

        if (Pref.isTarVsAchvEnable)
            targ_achv_TV.visibility = View.VISIBLE
        else
            targ_achv_TV.visibility = View.GONE

        if (Pref.isTaskEnable)
            task_TV.visibility = View.VISIBLE
        else
            task_TV.visibility = View.GONE

        if (Pref.willDynamicShow)
            dynamic_TV.visibility = View.VISIBLE
        else
            dynamic_TV.visibility = View.GONE

        if (Pref.willActivityShow)
            activity_TV.visibility = View.VISIBLE
        else
            activity_TV.visibility = View.GONE

        if (Pref.isDocumentRepoShow)
            doc_TV.visibility = View.VISIBLE
        else
            doc_TV.visibility = View.GONE


        if (Pref.isChatBotShow)
            chat_bot_TV.visibility = View.VISIBLE
        else
            chat_bot_TV.visibility = View.GONE

        if (Pref.Showdistributorwisepartyorderreport)
            distributor_wise_order_list_TV.visibility = View.VISIBLE
        else
            distributor_wise_order_list_TV.visibility = View.GONE



        if (Pref.isShowTimeline)
            orderHistoryTV.visibility = View.VISIBLE
        else
            orderHistoryTV.visibility = View.GONE

        if (Pref.isAppInfoEnable)
            device_info_TV.visibility = View.VISIBLE
        else
            device_info_TV.visibility = View.GONE

        if (Pref.isShowMicroLearning)
            micro_learning_TV.visibility = View.VISIBLE
        else
            micro_learning_TV.visibility = View.GONE

        if (Pref.IsShowNearByTeam)
            nearby_user_TV.visibility = View.VISIBLE
        else
            nearby_user_TV.visibility = View.GONE

        if (Pref.isShowNearbyCustomer)
            nearby_shop_TV.visibility = View.VISIBLE
        else
            nearby_shop_TV.visibility = View.GONE

        var launchIntent: Intent? = packageManager.getLaunchIntentForPackage("com.anydesk.anydeskandroid")
        if(launchIntent!=null){
            anydesk_info_TV.text="Open Anydesk"
        }else{
            anydesk_info_TV.text="Install Anydesk"
        }

        if(AppUtils.getSharedPreferenceslogShareinLogin(mContext)){
            shareLogs.visibility=View.VISIBLE
        }else{
            shareLogs.visibility=View.GONE
        }
        /*29-10-2021 Team Attendance*/
        if(Pref.IsTeamAttendance){
        //if(AppUtils.getSharedPreferencesIsFaceDetectionOn(mContext)){
            photo_team_attendance.visibility=View.VISIBLE
        }else{
            photo_team_attendance.visibility=View.GONE
        }

        /*Clear Attendance*/
        if(Pref.ShowAttednaceClearmenu){
            tv_clear_attendance.visibility=View.VISIBLE
        }else{
            tv_clear_attendance.visibility=View.GONE
        }


        if(AppUtils.getSharedPreferencesIsFaceDetectionOn(mContext)){
            photo_registration.visibility=View.VISIBLE
//            photo_team_attendance.visibility=View.VISIBLE
        }else{
            photo_registration.visibility=View.GONE
//            photo_team_attendance.visibility=View.GONE
        }

        if(AppUtils.getSharedPreferencesIsScreenRecorderEnable(mContext)){
            screen_record_info_TV.visibility=View.VISIBLE
        }else{
            screen_record_info_TV.visibility=View.GONE
        }


        if(Pref.IsShowMenuAddAttendance){
            add_attendence_tv.visibility=View.VISIBLE
        }else{
            add_attendence_tv.visibility=View.GONE
        }
        if(Pref.IsShowMenuAttendance){
            myAllowRequest.visibility=View.VISIBLE
        }else{
            myAllowRequest.visibility=View.GONE
        }
        if(Pref.IsShowMenuMIS_Report){
            mis_TV.visibility=View.VISIBLE
        }else{
            mis_TV.visibility=View.GONE
        }
        if(Pref.IsShowMenuAnyDesk){
            anydesk_info_TV.visibility=View.VISIBLE
        }else{
            anydesk_info_TV.visibility=View.GONE
        }
        if(Pref.IsShowMenuPermission_Info){
            permission_info_TV.visibility=View.VISIBLE
        }else{
            permission_info_TV.visibility=View.GONE
        }
        if(Pref.IsShowMenuScan_QR_Code){
            scan_TV.visibility=View.VISIBLE
        }else{
            scan_TV.visibility=View.GONE
        }
        if(Pref.IsShowMenuChat){
            chat_TV.visibility=View.VISIBLE
        }else{
            chat_TV.visibility=View.GONE
        }
        if(Pref.IsShowMenuWeather_Details){
            weather_TV.visibility=View.VISIBLE
        }else{
            weather_TV.visibility=View.GONE
        }
        if(Pref.IsShowMenuHome_Location){
            home_loc_TV.visibility=View.VISIBLE
        }else{
            home_loc_TV.visibility=View.GONE
        }
        if(Pref.IsShowMenuShare_Location){
            share_loc_TV.visibility=View.VISIBLE
        }else{
            share_loc_TV.visibility=View.GONE
        }
        if(Pref.IsShowMenuMap_View){
            maps_TV.visibility=View.VISIBLE
        }else{
            maps_TV.visibility=View.GONE
        }
        if(Pref.IsShowMenuReimbursement){
            reimbursement_tv.visibility=View.VISIBLE
        }else{
            reimbursement_tv.visibility=View.GONE
        }
        if(Pref.IsShowMenuOutstanding_Details_PP_DD){
            tv_pp_dd_outstanding.visibility=View.VISIBLE
        }else{
            tv_pp_dd_outstanding.visibility=View.GONE
        }
        if(Pref.IsShowMenuStock_Details_PP_DD){
            view_pp_dd_tv.visibility=View.VISIBLE
        }else{
            view_pp_dd_tv.visibility=View.GONE
        }

        /*21-12-2021*/
        if (Pref.isOrderShow){
            if(Pref.IsReturnEnableforParty){
                returnTV.visibility=View.VISIBLE
            }
            else{
                returnTV.visibility=View.GONE
            }
        }else{
            returnTV.visibility=View.GONE
        }
        /*02-03-2022*/
        if(Pref.ShowUserwiseLeadMenu){
            assignedLead.visibility=View.VISIBLE
        }else{
            assignedLead.visibility=View.GONE
        }
        if(Pref.IsMenuSurveyEnabled){
            surveyMenu.visibility=View.VISIBLE
        }else{
            surveyMenu.visibility=View.GONE
        }

        //val frag: DashboardFragment? = supportFragmentManager.findFragmentByTag("DashboardFragment") as DashboardFragment?



        Handler().postDelayed(Runnable {
            if (getFragment() != null && getFragment() is DashboardFragment) {
                if (Pref.isScanQrForRevisit)
                    iv_scan.visibility = View.VISIBLE
                else
                    iv_scan.visibility = View.GONE

                if (!Pref.isAttendanceFeatureOnly)
                    logo.visibility = View.VISIBLE
                else
                    logo.visibility = View.GONE
            }
        }, 500)
    }

    private fun showOrderCollectionAlert(isOrderAdded: Boolean, isCollectionAdded: Boolean) {

        try {

            if (isForceLogout)
                return

            var header = AppUtils.hiFirstNameText()+"!"
            var body = ""

            if (!isOrderAdded && !isCollectionAdded) {
                //header = AppUtils.hiFirstNameText()
                body = "No order or collection synced till now. Thanks."
            } else if (!isOrderAdded) {
                //header = AppUtils.hiFirstNameText()
                body = "No order synced till now. Thanks."
            } else if (!isCollectionAdded) {
                //header = AppUtils.hiFirstNameText()
                body = "No collection synced till now. Thanks."
            }

            if (orderCollectionAlertDialog != null) {
                orderCollectionAlertDialog?.dismissAllowingStateLoss()
                orderCollectionAlertDialog = null
            }

            orderCollectionAlertDialog = CommonDialogSingleBtn.getInstance(header, body, getString(R.string.ok), object : OnDialogClickListener {
                override fun onOkClick() {
                    isOrderDialogShow = false
                }
            })//.show(supportFragmentManager, "CommonDialogSingleBtn")


            XLog.e("Order Alert Dialog show time====> " + AppUtils.getCurrentTime())

            orderCollectionAlertDialog?.show(supportFragmentManager, "CommonDialogSingleBtn")

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    public fun setProfileImg() {
        if (profilePicture != null && Pref.profile_img != null && Pref.profile_img.trim().isNotEmpty()) {
            //Picasso.with(this).load(Pref.profile_img).into(profilePicture)
            /*Picasso.get()
                    .load(Pref.profile_img)
                    .resize(100, 100)
                    .into(profilePicture)*/

            Glide.with(mContext)
                    .load(Pref.profile_img)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_menu_profile_image).error(R.drawable.ic_menu_profile_image))
                    .into(profilePicture)
        }
        if (profile_name_TV != null && Pref.user_name != null && Pref.user_name!!.trim().isNotEmpty()) {
            profile_name_TV.text = Pref.user_name
        }

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.activity_dashboard_lnr_lyt_slide_view -> {
                Toast.makeText(this, "asdasf", Toast.LENGTH_LONG).show()
            }

            R.id.home_RL -> {
                deSelectAll()
                home_IV.isSelected = true
                home_TV.isSelected = true
                loadFragment(FragType.HomeFragment, false, "")
            }
            R.id.add_shop_RL -> {
                deSelectAll()
                add_shop_IV.isSelected = true
                add_shop_TV.isSelected = true
                loadFragment(FragType.AddShopFragment, true, "")
            }
            R.id.nearby_shops_RL -> {
                deSelectAll()
                nearby_shops_IV.isSelected = true
                nearby_shops_TV.isSelected = true
                isShopFromChatBot = false
                loadFragment(FragType.NearByShopsListFragment, false, "")
            }
            R.id.my_orders_RL -> {
                deSelectAll()
                my_orders_IV.isSelected = true
                my_orders_TV.isSelected = true
                loadFragment(FragType.MyOrderListFragment, false, "")
            }

            R.id.add_order_TV -> {
//                check("")
                loadFragment(FragType.DashboardFragment, false, DashboardType.Home)
            }

            R.id.order_history_TV -> {
                isMemberMap = false
                if (!Pref.willTimelineWithFixedLocationShow)
                    loadFragment(FragType.OrderhistoryFragment, false, "")
                else
                    loadFragment(FragType.TimeLineFragment, false, "")
            }
            R.id.add_travel_allowence_TV -> {
                deSelectAll()

                if (!Pref.isShowShopBeatWise) {
                    isShopFromChatBot = false
                    if (!Pref.isServiceFeatureEnable)
                        loadFragment(FragType.NearByShopsListFragment, false, "")
                    else
                        loadFragment(FragType.CustomerListFragment, false, "")
                } else
                    loadFragment(FragType.BeatListFragment, false, "")
            }

            R.id.my_allowence_request_TV -> {
                deSelectAll()
                isChatBotAttendance = false
                loadFragment(FragType.AttendanceFragment, false, "")
            }

            R.id.add_attendence_tv -> {

                var inTime = ""
                var outTime = ""

                /*if (AppUtils.getCurrentTimeWithMeredian().contains("AM") || AppUtils.getCurrentTimeWithMeredian().contains("PM")) {
                    inTime = "8:00 AM"
                    outTime = "11:59 PM"
                } else if (AppUtils.getCurrentTimeWithMeredian().contains("a.m.") || AppUtils.getCurrentTimeWithMeredian().contains("p.m.")) {
                    inTime = "8:00 a.m."
                    outTime = "11:59 p.m."
                } else if (AppUtils.getCurrentTimeWithMeredian().contains("am") || AppUtils.getCurrentTimeWithMeredian().contains("pm")) {
                    inTime = "8:00 am"
                    outTime = "11:59 pm"
                } else if (AppUtils.getCurrentTimeWithMeredian().contains("A.M.") || AppUtils.getCurrentTimeWithMeredian().contains("P.M.")) {
                    inTime = "8:00 A.M."
                    outTime = "11:59 P.M."
                }

                if (AppUtils.convertDateTimeWithMeredianToLong(AppUtils.getCurrentTimeWithMeredian()) >= AppUtils.convertDateTimeWithMeredianToLong(inTime)
                        && AppUtils.convertDateTimeWithMeredianToLong(AppUtils.getCurrentTimeWithMeredian()) <= AppUtils.convertDateTimeWithMeredianToLong(outTime)) {*/
                if (Pref.isAddAttendence)
                    (mContext as DashboardActivity).showSnackMessage("${AppUtils.hiFirstNameText()}!. Attendance already marked for the day.")
                else {
                    /*if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude)) {
                        if (Pref.isHomeLocAvailable) {

                            if (!TextUtils.isEmpty(Pref.home_latitude) && !TextUtils.isEmpty(Pref.home_longitude)) {
                                val distance = LocationWizard.getDistance(Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble(), Pref.current_latitude.toDouble(),
                                        Pref.current_longitude.toDouble())

                                XLog.e("Distance from home====> $distance")

                                if (distance * 1000 > 50)
                                    loadFragment(FragType.AddAttendanceFragment, false, "")
                                else
                                    (mContext as DashboardActivity).showSnackMessage("Attendance can not be added from home")
                            } else {
                                XLog.e("========Home location is not available========")
                                loadFragment(FragType.AddAttendanceFragment, false, "")
                            }

                        } else {
                            XLog.e("========isHomeLocAvailable is false========")
                            loadFragment(FragType.AddAttendanceFragment, false, "")
                        }
                    } else {
                        XLog.e("========Current location is not available========")*/


                    val attendanceReq = AttendanceRequest()
                    attendanceReq.user_id = Pref.user_id!!
                    attendanceReq.session_token = Pref.session_token
                    attendanceReq.start_date = AppUtils.getCurrentDateForCons()
                    attendanceReq.end_date = AppUtils.getCurrentDateForCons()

                    val repository = AttendanceRepositoryProvider.provideAttendanceRepository()
                    progress_wheel.spin()
                    BaseActivity.compositeDisposable.add(
                            repository.getAttendanceList(attendanceReq)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        val attendanceList = result as AttendanceResponse
                                        if (attendanceList.status == "205") {
                                            progress_wheel.stopSpinning()
                                            loadFragment(FragType.AddAttendanceFragment, true, "")
                                        } else if (attendanceList.status == NetworkConstant.SUCCESS) {
                                            progress_wheel.stopSpinning()
                                            Pref.isAddAttendence = true
                                            (mContext as DashboardActivity).showSnackMessage("${AppUtils.hiFirstNameText()}. Attendance already marked for the day.")
                                        }

                                    }, { error ->
                                        progress_wheel.stopSpinning()
                                        error.printStackTrace()
                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                    })
                    )


                    //loadFragment(FragType.AddAttendanceFragment, false, "")
                    //}
                }
                /*} else
                    showSnackMessage("Attendance can be added only between 8:00 AM and 11:59 PM")*/

                //loadFragment(FragType.AddAttendanceFragment, false, "")
            }

            R.id.tv_performance -> {
                loadFragment(FragType.PerformanceFragment, true, "")
            }

            R.id.ta_tv -> {
                //loadFragment(FragType.ViewAllTAListFragment, false, "")
                showSnackMessage(getString(R.string.functionality_disabled))
            }

            R.id.view_pp_dd_tv -> {
                //loadFragment(FragType.ViewPPDDListFragment, false, false)
                showSnackMessage(getString(R.string.functionality_disabled))
            }
            R.id.tv_pp_dd_outstanding -> {
                loadFragment(FragType.ViewPPDDListOutstandingFragment, false, true)
            }
            R.id.settings_TV -> {
                //showSnackMessage(getString(R.string.functionality_disabled))
                //09-09-2021
                isOrderFromChatBot = false
                if (Pref.IsActivateNewOrderScreenwithSize) {
                    CustomStatic.IsOrderFromTotalOrder = false
                    loadFragment(FragType.NewOdrScrListFragment, false, "")
                } else {
                    loadFragment(FragType.NewOrderListFragment, false, "")
                }

            }

            R.id.return_TV -> {
                loadFragment(FragType.NewReturnListFragment, false, "")
            }
            R.id.state_report_TV -> {
                loadFragment(FragType.KnowYourStateFragment, false, "")
            }
            R.id.logout_TV -> {
                //performLogout()
                if (Pref.DayEndMarked == false && Pref.IsShowDayEnd == true && Pref.DayStartMarked) {
                    showSnackMessage("Please mark Day End before logout. Thanks.")
                } else {
                    if (Pref.isAttendanceFeatureOnly)
                        performLogout()
                    else {
                        isClearData = false
                        if (AppUtils.isOnline(this@DashboardActivity)) {
                            Handler().postDelayed(Runnable {
                                //callShopDurationApi()
                                //rectifyUnknownLoc()
                                syncGpsNetData()
                            }, 350)
                        //loadFragment(FragType.LogoutSyncFragment, true, "")
                        } else
                            showSnackMessage("Good internet must required to sync all data, please switch on the internet and proceed.")
                    }
                }

            }
            R.id.mis_TV -> {
                loadFragment(FragType.ReportFragment, false, "")
            }
            R.id.tv_location -> {
                //loadFragment(FragType.LocationListFragment, false, "")
            }
            R.id.iv_tick_icon -> {
                if (getCurrentFragType() == FragType.AddShopFragment) {
                    (getFragment() as AddShopFragment).validateAndSaveData()
                }
            }
            R.id.logo -> {
                /*if (getFragment() != null && getFragment() is AddAttendanceFragment && !isAddAttendaceAlert)
                    loadFragment(FragType.DashboardFragment, false, "")
                else*/ /*if (getFragment() != null && getFragment() !is GpsDisableFragment && !isAddAttendaceAlert &&
                        getFragment() !is OrderTypeListFragment && getFragment() !is CartFragment && getFragment() !is VisitReportFragment && getFragment() !is AttendanceReportFragment
                        && getFragment() !is PerformanceReportFragment && getFragment() !is VisitReportDetailsFragment)
                    loadFragment(FragType.DashboardFragment, false, "")*/


                println("load fragg ${Pref.IsCollectionOrderWise} ${Pref.ShowCollectionAlert} ${Pref.ShowZeroCollectioninAlert} ${Pref.ShowCollectionOnlywithInvoiceDetails} ${Pref.IsPendingCollectionRequiredUnderTeam}" +
                        " ${Pref.IsCollectionEntryConsiderOrderOrInvoice}"  );


                Pref.IsPendingColl=false
                Pref.IsZeroOrder=false
                SendBrod.stopBrodColl(this)
                SendBrod.stopBrodZeroOrder(this)
                SendBrod.stopBrodDOBDOA(this)
                tv_noti_count.visibility=View.GONE

                Pref.NotiCountFlag = false

                //Pref.IsCollectionOrderWise = true
                //Pref.ShowCollectionAlert = true
                //Pref.ShowZeroCollectioninAlert = false
                //Pref.ShowCollectionOnlywithInvoiceDetails = false
                //Pref.IsPendingCollectionRequiredUnderTeam = false
                //Pref.IsCollectionEntryConsiderOrderOrInvoice = false
                //Pref.IsShowRepeatOrderinNotification = true

                if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification==false){
                    loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                }
                else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false){
                    loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                }
                else if(Pref.ShowCollectionAlert ==false && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification){
                    loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                }
                else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false){
                    loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                }
               else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification){
                    loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                }
                else if(Pref.ShowCollectionAlert == false && Pref.ShowZeroCollectioninAlert  && Pref.IsShowRepeatOrderinNotification) {
                    loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                }
                else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification){
                    loadFragment(FragType.CollectionNotiViewPagerFrag2, true, "")
                }
                else{
                    loadFragment(FragType.NotificationFragment, true, "")
                }

//                loadFragment(FragType.NotificationFragment, true, "")
                //showSnackMessage("Under Development")
            }
            R.id.iv_home_icon -> {
                if (getFragment() != null && (getFragment() is ViewAllOrderListFragment || getFragment() is NotificationFragment) && (ShopDetailFragment.isOrderEntryPressed || AddShopFragment.isOrderEntryPressed)
                        && AppUtils.getSharedPreferenceslogOrderStatusRequired(this)) {


                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_yes_no)
                    val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                    val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView

                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                        val dialog = Dialog(mContext)
                        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialog.setContentView(R.layout.dialog_cancel_order_status)

                        val user_name = dialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                        val order_status = dialog.findViewById(R.id.tv_cancel_order_status) as AppCustomTextView
                        val cancel_remarks = dialog.findViewById(R.id.et_cancel_order_remarks) as AppCustomEditText
                        val submitRemarks = dialog.findViewById(R.id.tv_cancel_order_submit_remarks) as AppCustomTextView

                        order_status.text = "Failure"
                        user_name.text = "Hi" + Pref.user_name + "!"

                        submitRemarks.setOnClickListener(View.OnClickListener { view ->
                            if (!TextUtils.isEmpty(cancel_remarks.text.toString().trim())) {
                                //Toast.makeText(mContext,cancel_remarks.text.toString(),Toast.LENGTH_SHORT).show()
                                val obj = OrderStatusRemarksModelEntity()
                                //obj.shop_id= mShopId
                                obj.shop_id = ViewAllOrderListFragment.mSShopID_Str.toString()
                                obj.user_id = Pref.user_id
                                obj.order_status = order_status.text.toString()
                                obj.order_remarks = cancel_remarks!!.text!!.toString()
                                obj.visited_date_time = AppUtils.getCurrentDateTime()
                                obj.visited_date = AppUtils.getCurrentDateForShopActi()
                                obj.isUploaded = false

                                var shopAll = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                                if (shopAll.size == 1) {
                                    obj.shop_revisit_uniqKey = shopAll.get(0).shop_revisit_uniqKey
                                } else if (shopAll.size != 0) {
                                    obj.shop_revisit_uniqKey = shopAll.get(shopAll.size - 1).shop_revisit_uniqKey
                                }
                                if (shopAll.size != 0)
                                    AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                                dialog.dismiss()

                                if (ShopDetailFragment.isOrderEntryPressed) {
                                    ShopDetailFragment.isOrderEntryPressed = false
                                }
                                if (AddShopFragment.isOrderEntryPressed) {
                                    AddShopFragment.isOrderEntryPressed = false
                                }


                                loadFragment(FragType.DashboardFragment, false, "")

                                Handler().postDelayed(Runnable {
                                    (getFragment() as DashboardFragment).updateItem()
                                }, 500)

                            } else {
                                submitRemarks.setError("Enter Remarks")
                                submitRemarks.requestFocus()
                            }

                        })
                        dialog.show()
                    })
                    dialogNo.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()


                } else {
                    loadFragment(FragType.DashboardFragment, false, "")

                    Handler().postDelayed(Runnable {
                        (getFragment() as DashboardFragment).updateItem()
                    }, 500)
                }

            }
            //19-08-21 nearBy shop visit stop untill daystart
            R.id.nearby_shop_TV -> {
                isChatBotLocalShop = false
                if (Pref.IsShowDayStart) {
                    if (!Pref.isAddAttendence) {
                        (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                        return
                    } else {
                        if (!Pref.DayStartMarked) {
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText() + "!"
                            dialogHeader.text = "Please start your day..."
                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
//                            (mContext as DashboardActivity).showSnackMessage("Please start your day")
                        } else {
                            loadFragment(FragType.LocalShopListFragment, false, "")
                        }
                    }
                } else {
                    loadFragment(FragType.LocalShopListFragment, false, "")
                }
            }
            R.id.assigned_lead_TV -> {

                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else
                    loadFragment(FragType.LeadFrag, false, "")
            }
            R.id.assigned_survey_TV -> {

                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else
                    loadFragment(FragType.SurveyFrag, false, "")
            }
//            R.id.nearby_shop_TV -> {
//                isChatBotLocalShop = false
//                loadFragment(FragType.LocalShopListFragment, false, "")
//            }
            R.id.share_log_TV -> {
                /*if(Build.VERSION.SDK_INT>=30){
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
            R.id.iv_search_icon -> {
                searchView.openSearch()
            }
            R.id.iv_profile_picture -> {
                loadFragment(FragType.MyProfileFragment, false, "")
            }
            R.id.maps_TV -> {
                isMapFromDrawer = true
                loadFragment(FragType.NearByShopsMapFragment, false, "")
            }
            R.id.iv_sync_icon -> {
                when {
                    getCurrentFragType() == FragType.NearByShopsMapFragment -> (getFragment() as NearByShopsMapFragment).fetchCurrentLocation()
                    getCurrentFragType() == FragType.NearByShopsListFragment -> (getFragment() as NearByShopsListFragment).refreshShopList()
                    getCurrentFragType() == FragType.OrderTypeListFragment -> (getFragment() as OrderTypeListFragment).refreshProductList()
                    getCurrentFragType() == FragType.ReturnTypeListFragment -> (getFragment() as ReturnTypeListFragment).refreshProductList()
                    getCurrentFragType() == FragType.NewOrderListFragment -> (getFragment() as NewOrderListFragment).refreshOrderList()
                    getCurrentFragType() == FragType.AddShopFragment -> (getFragment() as AddShopFragment).refreshList()
                    getCurrentFragType() == FragType.ShopDetailFragment -> (getFragment() as ShopDetailFragment).refreshList()
                    getCurrentFragType() == FragType.OfflineMemberListFragment -> (getFragment() as OfflineMemberListFragment).refreshList()
                    getCurrentFragType() == FragType.OfflineAllShopListFragment -> (getFragment() as OfflineAllShopListFragment).refreshList()
                    getCurrentFragType() == FragType.OfflineShopListFragment -> (getFragment() as OfflineShopListFragment).refreshList()
                    getCurrentFragType() == FragType.TimeSheetListFragment -> (getFragment() as TimeSheetListFragment).refreshList()
                    getCurrentFragType() == FragType.DashboardFragment -> (getFragment() as DashboardFragment).refresh()

                    getCurrentFragType() == FragType.NewReturnListFragment -> (getFragment() as NewReturnListFragment).refreshOrderList()
                    getCurrentFragType() == FragType.MapViewForTeamFrag -> (getFragment() as MapViewForTeamFrag).refreshMap()
                }
            }
            R.id.iv_delete_icon -> {
                if (getCurrentFragType() == FragType.OrderTypeListFragment) {
                    (getFragment() as OrderTypeListFragment).goToNextScreen()
                }
                if (getCurrentFragType() == FragType.NewOrderScrActiFragment) {
                    (getFragment() as NewOrderScrActiFragment).clickToCart()
                }
                if (getCurrentFragType() == FragType.ReturnTypeListFragment) {
                    (getFragment() as ReturnTypeListFragment).goToNextScreen()
                }
            }
            R.id.iv_filter_icon -> {
                if (getCurrentFragType() == FragType.OrderTypeListFragment) {
                    (getFragment() as OrderTypeListFragment).setData()
                }
            }

            R.id.rl_confirm_btn -> {
                if (getCurrentFragType() == FragType.CartFragment) {
                    (getFragment() as CartFragment).onConfirmClick()
                }
                if (getCurrentFragType() == FragType.CartReturnFragment) {
                    (getFragment() as CartReturnFragment).onConfirmClick()
                } else if (getCurrentFragType() == FragType.AddBillingFragment) {
                    (getFragment() as AddBillingFragment).onConfirmClick()
                } else if (getCurrentFragType() == FragType.NeworderScrCartFragment) {
                    (getFragment() as NeworderScrCartFragment).showCheckAlert()
                }
                /*else if (getCurrentFragType() == FragType.DocumentListFragment) {
                    (getFragment() as DocumentListFragment).onConfirmClick()
                }*/
            }
            R.id.reimbursement_TV -> {
                loadFragment(FragType.ReimbursementListFragment, false, "")
                //showSnackMessage(getString(R.string.under_development))
            }
            R.id.achievement_TV -> {
                //Toast.makeText(mContext, "Configuration required. Please contact admin", Toast.LENGTH_SHORT).show()
                //showSnackMessage("Configuration required. Please contact admin")
                loadFragment(FragType.AchievementFragment, false, "")
            }
            /*R.id.collection_TV -> {
                loadFragment(FragType.NewCollectionListFragment, false, "")
            }*/
            R.id.target_TV -> {
                /*if (!Pref.isAddAttendence)
                    checkToShowAddAttendanceAlert()
                else {*/
                isDailyPlanFromAlarm = false
                loadFragment(FragType.DailyPlanListFragment, false, "")
                //}
            }

            R.id.iv_list_party -> {
                loadFragment(FragType.AllShopListFragment, true, "")
            }
            R.id.rl_report -> {
                if (!iv_drop_down_icon.isSelected) {
                    iv_drop_down_icon.isSelected = true
                    ll_report_list.visibility = View.VISIBLE
                } else {
                    iv_drop_down_icon.isSelected = false
                    ll_report_list.visibility = View.GONE
                }
            }
            R.id.tv_attendance_report -> {
                isAttendanceFromAlarm = false
                isAttendanceReportFromDrawer = true
                loadFragment(FragType.AttendanceReportFragment, false, "")
            }
            R.id.tv_performance_report -> {
                isPerformanceFromAlarm = false
                isPerformanceReportFromDrawer = true
                loadFragment(FragType.PerformanceReportFragment, false, "")
            }
            R.id.tv_visit_report -> {
                isVisitFromAlarm = false
                isVisitReportFromDrawer = true
                loadFragment(FragType.VisitReportFragment, false, "")
            }
            R.id.meeting_TV -> {
                loadFragment(FragType.MeetingListFragment, false, "")
            }

            R.id.team_TV -> {
                if (Pref.isOfflineTeam)
                    loadFragment(FragType.OfflineMemberListFragment, true, Pref.user_id!!)
                else {
                    isAllTeam = false
                    loadFragment(FragType.MemberListFragment, true, Pref.user_id!!)
                }
            }
            R.id.iv_map -> {
                if (getCurrentFragType() == FragType.WeatherFragment) {
                    loadFragment(FragType.SearchLocationFragment, true, "")
                } else if (getCurrentFragType() == FragType.LocalShopListFragment) {
                    if (nearbyShopList != null && nearbyShopList!!.size > 0)
                        loadFragment(FragType.LocalShopListMapFragment, true, nearbyShopList!!)
                    else
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                } else {
                    if (!isMemberMap) {
                        if (Pref.willTimelineWithFixedLocationShow) {
                            activityLocationListNew?.takeIf { it.size > 0 }?.let {
                                loadFragment(FragType.ActivityMapFragment, true, it)
                            } ?: let {
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                            }
                        } else {
                            activityLocationList?.takeIf { it.size > 0 }?.let {
                                loadFragment(FragType.ActivityMapFragment, true, it)
                            } ?: let {
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                            }
                        }
                    } else {
                        memberLocationList?.takeIf { it.size > 0 }?.let {
                            loadFragment(FragType.ActivityMapFragment, true, it)
                        } ?: let {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                        }
                    }
                }
            }
            R.id.timesheet_TV -> {
                loadFragment(FragType.TimeSheetListFragment, false, "")
            }
            R.id.tv_change_pwd -> {
                ChangePasswordDialog.getInstance { newPassword: String, oldPassword: String ->
                    changePasswordApi(newPassword, oldPassword)
                }.show(supportFragmentManager, "")
            }
            R.id.quo_TV -> {
                isBack = false
                loadFragment(FragType.QuotationListFragment, false, "")
            }

            R.id.all_team_TV -> {
                isAllTeam = true
                loadFragment(FragType.MemberListFragment, true, Pref.user_id!!)
            }

            R.id.update_worktype_tv -> {
                loadFragment(FragType.UpdateWorkTypeFragment, false, "")
            }

            R.id.achv_TV -> {
                isAchvFromDrawer = true
                loadFragment(FragType.AchievementReportFragment, false, "")
            }

            R.id.targ_achv_TV -> {
                isTargAchvFromDrawer = true
                loadFragment(FragType.TargetVsAchvFragment, false, "")
            }

            R.id.leave_tv -> {
                loadFragment(FragType.LeaveListFragment, false, "")
            }

            R.id.task_TV -> {
//                checkByU("")
                loadFragment(FragType.TaskListFragment, false, "")
            }

            R.id.dynamic_TV -> {
                loadFragment(FragType.AllDynamicListFragment, false, "")
            }

            R.id.activity_TV -> {
                isFromMenu = true
                loadFragment(FragType.AddActivityFragment, false, "")
            }

            R.id.rl_collection -> {
                if (!iv_collection_drop_down_icon.isSelected) {
                    iv_collection_drop_down_icon.isSelected = true
                    ll_collection_list.visibility = View.VISIBLE
                } else {
                    iv_collection_drop_down_icon.isSelected = false
                    ll_collection_list.visibility = View.GONE
                }
            }

            R.id.tv_report -> {
                //loadFragment(FragType.NewCollectionListFragment, false, "")
                isCollectionStatusFromDrawer = true
                loadFragment(FragType.CollectionDetailsStatusFragment, false, "")
            }

            R.id.tv_entry -> {
                isShopFromChatBot = false
                if(Pref.IsCollectionOrderWise){
                    loadFragment(FragType.NewOrderListFragment, false, "")
                }else{
                    loadFragment(FragType.NearByShopsListFragment, false, "")
                }
            }

            R.id.share_loc_TV -> {
                val uri = "https://www.google.com/maps/?q=" + Pref.current_latitude + "," + Pref.current_longitude
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.let {
                    it.type = "text/plain"
                    it.putExtra(Intent.EXTRA_TEXT, uri)
                    startActivity(Intent.createChooser(it, "Share via"))
                }
            }

            R.id.iv_settings -> {
                loadFragment(FragType.SettingsFragment, true, "")
            }

            R.id.weather_TV -> {
                isWeatherFromDrawer = true
                loadFragment(FragType.WeatherFragment, false, "")
            }

//            R.id.doc_TV -> {
//                loadFragment(FragType.DocumentTypeListFragment, false, "")
//            }

            R.id.doc_TV -> {
                if (Pref.IsFromPortal) {
                    loadFragment(FragType.DocumentRepoFeatureNewFragment, false, "")
                } else {
                    CustomStatic.IsDocZero = false
                    loadFragment(FragType.DocumentTypeListFragment, false, "")
                }
            }

            R.id.chat_bot_TV -> {
                showLanguageAlert(true)
            }
            R.id.distributor_wise_order_list_TV ->{
                loadFragment(FragType.DistributorwiseorderlistFragment, true, "")
            }

            R.id.photo_registration -> {
                if (AppUtils.isOnline(mContext)) {
                    loadFragment(FragType.ProtoRegistrationFragment, false, "")
                } else {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                }
            }

            R.id.photo_team_attendance -> {
                if (AppUtils.isOnline(mContext)) {
                    loadFragment(FragType.PhotoAttendanceFragment, false, "")
                } else {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                }

            }

            R.id.tv_clear_attendance -> {
                if (AppUtils.isOnline(mContext)) {
                    //var shopCreated = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopCreatedToday(AppUtils.getCurrentDate())
                    var shopRevit = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateyymmdd())
                    var ordListToday = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingDate(AppUtils.getCurrentDate())
                    var collistToday = AppDatabase.getDBInstance()!!.collectionDetailsDao().getDateWiseCollection(AppUtils.getCurrentDate())
                    var newOrdListToday = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getRateListByDate(AppUtils.getCurrentDateyymmdd())

                    //if(shopCreated!!.size>0 || shopRevit!!.size>0 || ordListToday.size>0 || collistToday.size>0){
                    if(shopRevit!!.size>0 || ordListToday.size>0 || collistToday.size>0 || newOrdListToday!!.size>0){
                    //if(shopRevit!!.size>0 || collistToday.size>0){
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                        dialogHeader.text = "Attendance can not be clear for today."
                        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()
                    }else{
                        var simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_yes_no)
                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                        val dialogBody = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                        val btn_no = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                        val btn_yes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView

                        dialogHeader.text = AppUtils.hiFirstNameText() + "!"
                        dialogBody.text = "Do you want to clear the Attendance or Leave for ${AppUtils.getCurrentDateChanged().replace("-"," ")}"

                        btn_yes.setOnClickListener({ view ->
                            simpleDialog.cancel()
                            apiCallOnClearAttenReject()
                        })
                        btn_no.setOnClickListener({ view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()
                    }

                } else {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                }

            }

            R.id.ic_calendar -> {
                if (getCurrentFragType() == FragType.TaskListFragment)
                    (getFragment() as TaskListFragment).showCalender()
            }

            R.id.ic_chat_bot -> {
                showLanguageAlert(false)
            }

            R.id.iv_cancel_chat -> {
                onBackPressed()
            }

            R.id.chat_TV -> {
                loadFragment(FragType.ChatUserListFragment, false, "")
            }

            R.id.iv_people -> {
                loadFragment(FragType.ShowPeopleFragment, true, grpId)
            }

            R.id.iv_scan -> {
                //isVisitCardScan = true
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck()
                else
                    captureImage()*/

                //(getFragment() as AddShopFragment).processImage(null)

                if (getFragment() != null) {
                    if (getFragment() is AddShopFragment)
                        loadFragment(FragType.ScanImageFragment, true, "")
                    else if (getFragment() is DashboardFragment) {
                        isForRevisit = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            isCodeScan = true
                            initPermissionCheck()
                        } else
                            loadFragment(FragType.CodeScannerFragment, true, "")
                    }
                }
            }

            R.id.iv_view_text -> {
                val textList = AppUtils.loadSharedPreferencesTextList(this)
                if (textList == null || textList.isEmpty()) {
                    showSnackMessage(getString(R.string.no_data_available))
                    return
                }

                ShowCardDetailsDialog.newInstance(textList).show(supportFragmentManager, "")
            }

            R.id.scan_TV -> {
                /*val integrator =  IntentIntegrator(this)
                integrator.apply {
                    setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                    setPrompt("Scan a qrcode")
                    setCameraId(0)  // Use a specific camera of the device
                    setBeepEnabled(true)
                    setOrientationLocked(false)
                    initiateScan()
                }*/

                isForRevisit = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    isCodeScan = true
                    initPermissionCheck()
                } else
                    loadFragment(FragType.CodeScannerFragment, true, "")
            }

            R.id.nearby_user_TV -> {
                loadFragment(FragType.NearbyUserListFragment, false, "")
            }

            R.id.fl_net_status -> {
                InternetStatusDialog.getInstance(netStatus).show(supportFragmentManager, "")
            }

            R.id.home_loc_TV -> {
                if (TextUtils.isEmpty(Pref.home_latitude) || TextUtils.isEmpty(Pref.home_longitude))
                    showSnackMessage("No Home Location Configured. Talk to Admin.")
                else if (Pref.home_latitude == "0.0" || Pref.home_longitude == "0.0")
                    showSnackMessage("No Home Location Configured. Talk to Admin.")
                else
                    loadFragment(FragType.HomeLocationFragment, false, "")
            }

            R.id.device_info_TV -> {
                loadFragment(FragType.DeviceInfoListFragment, false, "")
            }
            R.id.permission_info_TV -> {
                loadFragment(FragType.ViewPermissionFragment, false, "")
            }

            R.id.anydesk_info_TV -> {
                var launchIntent: Intent? = packageManager.getLaunchIntentForPackage("com.anydesk.anydeskandroid")
                if (launchIntent != null) {
                    drawerLayout.closeDrawers()
                    startActivity(launchIntent)
                } else {
                    drawerLayout.closeDrawers()
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.anydesk.anydeskandroid"))
                    startActivity(intent)
                }
            }

            R.id.screen_record_info_TV -> {

                permissionUtils = PermissionUtils(this, object : PermissionUtils.OnPermissionListener {
                    override fun onPermissionGranted() {

                        if (DashboardFragment.hbRecorder != null) {

                            if (DashboardFragment.hbRecorder!!.isBusyRecording && DashboardFragment.hbRecorder != null) {
                                Toast.makeText(this@DashboardActivity, "Please Stop Recording", Toast.LENGTH_SHORT).show()
                            } else {
                                if (DashboardFragment.isRecordRootVisible) {
                                    screen_record_info_TV.text = "Screen Recorder"
                                    DashboardFragment.ll_recorder_root.visibility = View.GONE
                                    DashboardFragment.isRecordRootVisible = false
                                    drawerLayout.closeDrawers()
                                } else {
                                    screen_record_info_TV.text = "Stop Recording"
                                    DashboardFragment.ll_recorder_root.visibility = View.VISIBLE
                                    DashboardFragment.isRecordRootVisible = true
                                    drawerLayout.closeDrawers()
                                }
                            }
                        } else {
                            if (DashboardFragment.isRecordRootVisible) {
                                screen_record_info_TV.text = "Screen Recorder"
                                DashboardFragment.ll_recorder_root.visibility = View.GONE
                                DashboardFragment.isRecordRootVisible = false
                                drawerLayout.closeDrawers()
                            } else {
                                screen_record_info_TV.text = "Stop Recording"
                                DashboardFragment.ll_recorder_root.visibility = View.VISIBLE
                                DashboardFragment.isRecordRootVisible = true
                                drawerLayout.closeDrawers()
                            }
                        }

                    }

                    override fun onPermissionNotGranted() {
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission_storage))
                    }

                }, arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))


                /*  if(DashboardFragment.hbRecorder ==null){
                    screen_record_info_TV.text="Start Screen Recorder"
                    DashboardFragment.ll_recorder_root.visibility=View.VISIBLE
                }else{
                    if(DashboardFragment.hbRecorder!!.isBusyRecording){
                        screen_record_info_TV.text="Stop Recording"
                        DashboardFragment.ll_recorder_root.visibility=View.GONE
                    }else{
                        screen_record_info_TV.text="Start Screen Recorder"
                        DashboardFragment.ll_recorder_root.visibility=View.VISIBLE
                    }
                }*/
            }
            R.id.check_custom_status_TV->{
                Toaster.msgShort(this,isWorkerRunning("workerTag").toString())
            }

            R.id.micro_learning_TV -> {
                loadFragment(FragType.MicroLearningListFragment, false, "")
            }

        }
    }

    private fun fileManagePermi(){
     /*   val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        val uri = Uri.fromParts("package", this.packageName, null)
        intent.data = uri
        startActivity(intent)*/

    }


    fun showLanguageAlert(mIsChatFromDrawer: Boolean) {
        SelectLanguageDialog.newInstance(object : SelectLanguageDialog.OnItemSelectedListener {
            override fun onItemSelect(language: String) {
                isChatFromDrawer = mIsChatFromDrawer
                if (isChatFromDrawer)
                    loadFragment(FragType.ChatBotFragment, false, language)
                else
                    loadFragment(FragType.ChatBotFragment, true, language)
            }
        }).show(supportFragmentManager, "")
    }

    private fun changePasswordApi(newPassword: String, oldPassword: String) {
        val repository = GetContentListRepoProvider.getContentListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.changePassword(oldPassword, newPassword)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse

                            XLog.e("RESPONSE: " + response.status + ", MESSAGE: " + response.message)

                            progress_wheel.stopSpinning()
                            showSnackMessage(response.message!!)

                            if (response.status == NetworkConstant.SUCCESS) {
                                isChangedPassword = true
                                isClearData = false
                                Handler().postDelayed(Runnable {
                                    loadFragment(FragType.LogoutSyncFragment, true, "")
                                }, 500)
                            }


                        }, { error ->
                            XLog.e("ERROR: " + error.message)
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }


    fun openShareIntents() {
        //openShare()
        //return
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

            //val uri = Uri.fromFile(file)
            //27-09-2021
            val uri: Uri = FileProvider.getUriForFile(mContext, mContext!!.applicationContext.packageName.toString() + ".provider", file)
//        shareIntent.data = fileUrl
            shareIntent.type = "image/png"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            //shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share log using"));
        } catch (e: Exception) {
            e.printStackTrace()
        }


//        Uri uri = Uri.fromFile(file);
//        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(Intent.createChooser(emailIntent,""))
    }

    fun openShare(){
        try{

            val intent = Intent()
            intent.action = Intent.ACTION_SEND_MULTIPLE
            intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.")
            intent.type = "image/*"

            val fileUrl1 = Uri.parse(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "xkcteamlogsample/log").path);
            val fileUrl2 = Uri.parse(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "xkcteamlogsample/log.bak.1").path);
            val files = ArrayList<Uri>()
            if (!File(fileUrl1.path).exists()) {
                return
            }
            files.add(fileUrl1)

            if (File(fileUrl2.path).exists()) {
                files.add(fileUrl2)
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
            startActivity(Intent.createChooser(intent, "Share log using"))
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

    private fun deSelectAll() {
        try {
            for (i in 0 until imageArrayList.size) {
                imageArrayList[i].isSelected = false
                textArrayList[i].isSelected = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun getFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.frame_layout_container)
    }

    fun showSnackMessage(message: String) {
        DisplayAlert.showSnackMessage(this@DashboardActivity, alert_snack_bar, message)
    }

    fun showSnackMessage(message: String, duration: Int) {
        DisplayAlert.showSnackMessage(this@DashboardActivity, alert_snack_bar, message, duration)
    }

    public fun getCurrentFragType(): FragType {
        val f = supportFragmentManager.findFragmentById(R.id.frame_layout_container)
                ?: return FragType.DEFAULT
        val name = f::class.java.simpleName
        return FragType.valueOf(name)
    }


    private fun getFragInstance(mFragType: FragType, initializeObject: Any, enableFragGeneration: Boolean): Fragment? {

        var mFragment: Fragment? = null

        when (mFragType) {
            FragType.BaseFragment -> {
                if (enableFragGeneration) {
                    mFragment = BaseFragment()
                }
                setTopBarTitle(getString(R.string.blank))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.AddShopFragment -> {

                if (enableFragGeneration) {
                    mFragment = AddShopFragment.getInstance(initializeObject)
                }

                setTopBarTitle("Add " + Pref.shopText)

                /* if (Pref.isReplaceShopText)
                     setTopBarTitle(getString(R.string.add_customer))
                 else
                     setTopBarTitle(getString(R.string.add_shop))*/

                setTopBarVisibility(TopBarConfig.ADDSHOP)
            }
            FragType.NearByShopsListFragment -> {

                if (enableFragGeneration) {
                    mFragment = NearByShopsListFragment.getInstance(initializeObject)
                }

                setTopBarTitle(Pref.shopText + "(s)")

                /*if (Pref.isReplaceShopText)
                    setTopBarTitle(getString(R.string.customers))
                else
                    setTopBarTitle(getString(R.string.shops))*/

                setTopBarVisibility(TopBarConfig.SHOPLIST)
                try {
                    if (getFragment() != null && getFragment() is NearByShopsListFragment)
                        (getFragment() as NearByShopsListFragment).updateUI("")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            FragType.NewNearByShopsListFragment -> {

                if (enableFragGeneration) {
                    mFragment = NewNearByShopsListFragment()
                }
                setTopBarTitle(getString(R.string.shops))
                setTopBarVisibility(TopBarConfig.SHOPLISTV1)
                try {
                    if (getFragment() != null && getFragment() is NewNearByShopsListFragment)
                        (getFragment() as NewNearByShopsListFragment).updateUI("")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            FragType.ShopDetailFragment -> {

                if (enableFragGeneration) {
                    mFragment = ShopDetailFragment.getInstance(initializeObject)
                }

//                setTopBarTitle(Pref.shopText + " Details")
                setTopBarTitle("Details")

                /* if (Pref.isReplaceShopText)
                     setTopBarTitle(getString(R.string.customer_details))
                 else
                     setTopBarTitle(getString(R.string.shop_detail))*/

                setTopBarVisibility(TopBarConfig.SHOPDETAILS)
            }
            FragType.ShopDetailFragmentV1 -> {

                if (enableFragGeneration) {
                    mFragment = ShopDetailFragmentV1.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.shop_detail))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.OrderDetailFragment -> {
                if (enableFragGeneration) {
                    mFragment = OrderDetailFragment()
                }
                setTopBarTitle(getString(R.string.order_detail))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.HomeFragment -> {
                if (enableFragGeneration) {
                    mFragment = HomeFragment()
                }
//                setTopBarTitle(getString(R.string.order_detail))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.MyOrderListFragment -> {
                if (enableFragGeneration) {
                    mFragment = MyOrderListFragment()
                }
                setTopBarTitle(getString(R.string.my_orders))
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.MyallowanceRequestFragment -> {
                if (enableFragGeneration) {
                    mFragment = MyallowanceRequestFragment()
                }
                setTopBarTitle(getString(R.string.my_allowence_request))
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.OrderhistoryFragment -> {

                if (enableFragGeneration) {
                    mFragment = OrderhistoryFragment()
                }
                //isMemberMap = false
                setTopBarTitle(getString(R.string.traveling_history))
                setTopBarVisibility(TopBarConfig.ACTIVITYMAP)
            }

            FragType.SearchLocationFragment -> {

                if (enableFragGeneration) {
                    mFragment = SearchLocationFragment()
                }
                setTopBarTitle(/*getString(R.string.history)*/"Map")
                if (getFragment() != null) {
                    if (getFragment() is DashboardFragment)
                        setTopBarVisibility(TopBarConfig.SEARCHLOCATION)
                    else if (getFragment() is AddShopFragment || getFragment() is WeatherFragment)
                        setTopBarVisibility(TopBarConfig.SEARCHLOCATIONFROMADDSHOP)
                }
            }
            FragType.StockListFragment -> {
                if (enableFragGeneration) {
                    mFragment = StockListFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.stock_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.UpdateShopStockFragment -> {
                if (enableFragGeneration) {
                    mFragment = UpdateShopStockFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.current_stock_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.CompetetorStockFragment -> {
                if (enableFragGeneration) {
                    mFragment = CompetetorStockFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.competetor_stock_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ViewStockDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewStockDetailsFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.current_stock_product_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddShopStockFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddShopStockFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.outlet_stock_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddCompetetorStockFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddCompetetorStockFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.competetor_stock_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ViewComStockProductDetails -> {
                if (enableFragGeneration) {
                    mFragment = ViewComStockProductDetails.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.competetor_stock_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.StockDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = StockDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.stock_detail))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddOrderFragment -> {

                if (enableFragGeneration) {
                    mFragment = AddOrderFragment()
                }
                setTopBarTitle(getString(R.string.add_order))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.SettingsFragment -> {
                if (enableFragGeneration) {
                    mFragment = SettingsFragment()
                }
                setTopBarTitle(getString(R.string.settings))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.DashboardFragment -> {
                if (enableFragGeneration) {
                    mFragment = DashboardFragment()
                }

                if (!Pref.isAttendanceFeatureOnly)
                    setTopBarTitle(getString(R.string.dashboard))
                else
                    setTopBarTitle(getString(R.string.attendance_report))

                setTopBarVisibility(TopBarConfig.DASHBOARD)
                try {
                    if (getFragment() != null && getFragment() is DashboardFragment)
                        (getFragment() as DashboardFragment).updateUI("")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            FragType.YesterdayRouteFragment -> {
                if (enableFragGeneration) {
                    mFragment = YesterdayRouteFragment().getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.view_yesterdays_route))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AttendanceFragment -> {

                if (enableFragGeneration) {
                    mFragment = AttendanceFragment()
                }
                setTopBarTitle(getString(R.string.view_attendance))

                if (isChatBotAttendance) {
                    //isChatBotAttendance = false
                    setTopBarVisibility(TopBarConfig.ATTENDENCEBACKLIST)
                } else
                    setTopBarVisibility(TopBarConfig.ATTENDENCELIST)
            }

            FragType.ViewAllOrderListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewAllOrderListFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.order_detail))
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.ViewAllQuotListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewAllQuotListFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.quotation_detail))
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.ViewDetailsQuotFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewDetailsQuotFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.quotation_detail2))
                setTopBarVisibility(TopBarConfig.BACK)
            }


            FragType.AddQuotFormFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddQuotFormFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.quotation_add))
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.ViewAllTAListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewAllTAListFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.ta_list))
                setTopBarVisibility(TopBarConfig.HOME)
            }

            FragType.PerformanceFragment -> {
                if (enableFragGeneration) {
                    mFragment = PerformanceFragment()
                }
                setTopBarTitle(getString(R.string.performance))
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.ViewOutstandingFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewOutstandingFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.outstanding_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ViewPPDDListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewPPDDListFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.stock_details_pp_dd))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.ViewPPDDListOutstandingFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewPPDDListOutstandingFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.outstanding_details_pp_dd))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.OrderListFragment -> {

                if (enableFragGeneration) {
                    mFragment = OrderListFragment()
                }
                setTopBarTitle(getString(R.string.orders))
                setTopBarVisibility(TopBarConfig.HOME)
            }

            FragType.NewOrderListFragment -> {
                if (enableFragGeneration) {
                    mFragment = NewOrderListFragment()
                }
                setTopBarTitle(getString(R.string.orders))
                setTopBarVisibility(TopBarConfig.ORDERLIST)
            }
            /*21-12-2021*/
            FragType.NewReturnListFragment -> {
                if (enableFragGeneration) {
                    mFragment = NewReturnListFragment()
                }
                setTopBarTitle(getString(R.string.returns))
//                setTopBarVisibility(TopBarConfig.RETURNLIST)
            }

            FragType.NewDateWiseOrderListFragment -> {

                if (enableFragGeneration) {
                    mFragment = NewDateWiseOrderListFragment() //OrderListFragment()
                }
                setTopBarTitle(getString(R.string.orders))
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.AddAttendanceFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddAttendanceFragment()
                }
                setTopBarTitle(getString(R.string.add_attendance))

                if (isAddAttendaceAlert) {
                    setTopBarVisibility(TopBarConfig.ADDATTENDANCE)
                } else
                    setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.ReportFragment -> {

                if (enableFragGeneration) {
                    mFragment = ReportFragment()
                }
                setTopBarTitle(getString(R.string.mis_reports))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.AverageShopFragment -> {

                if (enableFragGeneration) {
                    mFragment = AverageShopFragment()
                }
                setTopBarTitle(getString(R.string.visits_to_shops_new))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AvgTimespentShopListFragment -> {

                if (enableFragGeneration) {
                    mFragment = AvgTimespentShopListFragment()
                }
                setTopBarTitle(getString(R.string.time_spent_on_each_shop_new))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.LocalShopListFragment -> {
                if (enableFragGeneration) {
                    mFragment = LocalShopListFragment()
                }

//                setTopBarTitle("Nearby Located Party" + /*Pref.shopText +*/ " List")
                setTopBarTitle("Nearby Parties")
                /*if (Pref.isReplaceShopText)
                    setTopBarTitle(getString(R.string.nearby_customer))
                else
                    setTopBarTitle(getString(R.string.nearby_shops))*/

                setTopBarVisibility(TopBarConfig.LOCALSHOP)
                //isChatBotLocalShop = false
                try {
                    if (getFragment() != null && getFragment() is LocalShopListFragment)
                        (getFragment() as LocalShopListFragment).updateUI("")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            FragType.LeadFrag -> {
                if (enableFragGeneration) {
                    mFragment = LeadFrag()
                }
                setTopBarTitle("Assigned Lead")
                setTopBarVisibility(TopBarConfig.HOME)
                setTopBarVisibility(TopBarConfig.LEADFR)
            }
            FragType.ViewLeadFrag -> {
                if (enableFragGeneration) {
                    mFragment = ViewLeadFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Activity Details")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.SurveyFrag -> {
                if (enableFragGeneration) {
                    mFragment = SurveyFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Survey")
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.TeamBeatListFragment -> {
                if (enableFragGeneration) {
                    mFragment = TeamBeatListFragment.getInstance(initializeObject)
                }
                setTopBarTitle("Team Beat List")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.SurveyViewFrag -> {
                if (enableFragGeneration) {
                    mFragment = SurveyViewFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Survey Details")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.SurveyViewDtlsFrag -> {
                if (enableFragGeneration) {
                    mFragment = SurveyViewDtlsFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Survey Details")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ShopFeedbackHisFrag -> {
                if (enableFragGeneration) {
                    mFragment = ShopFeedbackHisFrag.newInstance(initializeObject)
                }
                setTopBarTitle("History Details")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ShopDamageProductListFrag -> {
                if (enableFragGeneration) {
                    mFragment = ShopDamageProductListFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Damage Product")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ShopDamageProductSubmitFrag -> {
                if (enableFragGeneration) {
                    mFragment = ShopDamageProductSubmitFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Add Breakage")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.DistributorwiseorderlistFragment -> {
                if (enableFragGeneration) {
                    mFragment = DistributorwiseorderlistFragment()
                }
                setTopBarTitle("DISTRIBUTOR WISE ORDER LIST")
                setTopBarVisibility(TopBarConfig.DISTWISEORDER)
            }
            FragType.NearByShopsMapFragment -> {
                if (enableFragGeneration) {
                    mFragment = NearByShopsMapFragment()
                }
                setTopBarTitle(getString(R.string.map_view))
                setTopBarVisibility(TopBarConfig.MAPVIEW)
            }
            FragType.CollectionNotiViewPagerFrag -> {
                if (enableFragGeneration) {
                    mFragment = CollectionNotiViewPagerFrag()
                }
                setTopBarTitle("General")
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.CollectionPendingTeamFrag -> {
                if (enableFragGeneration) {
                    mFragment = CollectionPendingTeamFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Collenction Pending")
                setTopBarVisibility(TopBarConfig.BACK)
            }

             FragType.CollectionPendingTeamDtlsFrag -> {
                if (enableFragGeneration) {
                    mFragment = CollectionPendingTeamDtlsFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Collenction Pending Details")
                setTopBarVisibility(TopBarConfig.BACK)
            }





            FragType.CollectionNotiViewPagerFrag1 -> {
                if (enableFragGeneration) {
                    mFragment = CollectionNotiViewPagerFrag1()
                }
                setTopBarTitle("General")
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.CollectionNotiViewPagerFrag2 -> {
                if (enableFragGeneration) {
                    mFragment = CollectionNotiViewPagerFrag2()
                }
                setTopBarTitle("General")
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.CollectionPendingDtlsFrag -> {
                if (enableFragGeneration) {
                    mFragment = CollectionPendingDtlsFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Collection Pending Details")
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.RepeatOrderFrag -> {
                if (enableFragGeneration) {
                    mFragment = RepeatOrderFrag()
                }
                setTopBarTitle("Repeat Order")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.TeamRepeatOrderFrag -> {
                if (enableFragGeneration) {
                    mFragment = TeamRepeatOrderFrag.getInstance(initializeObject)
                }
                setTopBarTitle("Repeat Order")
                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.AverageOrderFragment -> {

                if (enableFragGeneration) {
                    mFragment = AverageOrderFragment()
                }
                setTopBarTitle(getString(R.string.average_order_visited))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.MyProfileFragment -> {

                if (enableFragGeneration) {
                    mFragment = MyProfileFragment()
                }
                setTopBarTitle(getString(R.string.my_profile))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.MarketingPagerFragment -> {
                if (enableFragGeneration) {
                    mFragment = MarketingPagerFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.marketing_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.GpsStatusFragment -> {
                if (enableFragGeneration) {
                    mFragment = GpsStatusFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.gps_status))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.CollectionDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = CollectionDetailsFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.collection_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.OrderTypeListFragment -> {
                if (enableFragGeneration) {
                    mFragment = OrderTypeListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.search_product))
                setTopBarVisibility(TopBarConfig.CART)
            }
            FragType.ReturnTypeListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ReturnTypeListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.search_product))
                setTopBarVisibility(TopBarConfig.CART)
            }
            FragType.CartFragment -> {
                if (enableFragGeneration) {
                    mFragment = CartFragment.newInstance(initializeObject)
                }
                if (AppUtils.stockStatus == 0) {
                    setTopBarTitle(getString(R.string.cart_details))
                    tv_confirm_btn.text = "Place Order"
                } else if (AppUtils.stockStatus == 1) {
                    setTopBarTitle(getString(R.string.opening_stock))
                    tv_confirm_btn.text = "Place Stock"
                }

                setTopBarVisibility(TopBarConfig.CARTDETAILS)
            }
            FragType.CartReturnFragment -> {
                if (enableFragGeneration) {
                    mFragment = CartReturnFragment.newInstance(initializeObject)
                }
                if (AppUtils.stockStatus == 2) {
                    setTopBarTitle(getString(R.string.cart_details))
                    tv_confirm_btn.text = "Place Return"
                }
                setTopBarVisibility(TopBarConfig.CARTDETAILS)
            }
            FragType.ViewStockFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewStockFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.stock_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ViewCartFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewCartFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.order_detail))
                setTopBarVisibility(TopBarConfig.BACK)
            }
        /*17-12-2021*/
            FragType.ViewCartReturnFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewCartReturnFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.return_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }


            FragType.GpsDisableFragment -> {
                if (enableFragGeneration) {
                    mFragment = GpsDisableFragment()
                }

                setTopBarTitle(AppUtils.hiFirstNameText())
                setTopBarVisibility(TopBarConfig.GPS)
            }
            FragType.LocationListFragment -> {
                if (enableFragGeneration) {
                    mFragment = LocationListFragment()
                }
                setTopBarTitle("Location List")
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.LogoutSyncFragment -> {
                if (enableFragGeneration) {
                    mFragment = LogoutSyncFragment()
                }
                setTopBarTitle("Sync Data")

                if (isChangedPassword)
                    setTopBarVisibility(TopBarConfig.GPS)
                else {
                    if (!isForceLogout) {
                        if (isClearData)
                            setTopBarVisibility(TopBarConfig.GPS)
                        else
                            setTopBarVisibility(TopBarConfig.BACK)
                    } else
                        setTopBarVisibility(TopBarConfig.GPS)
                }
            }
            FragType.ReimbursementFragment -> {
                if (enableFragGeneration) {
                    mFragment = ReimbursementFragment()
                }
                setTopBarTitle(getString(R.string.reimbursement))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ReimbursementListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ReimbursementListFragment()
                }
                setTopBarTitle(getString(R.string.reimbursement_list))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.ReimbursementDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = ReimbursementDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.reimbursement_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.EditReimbursementFragment -> {
                if (enableFragGeneration) {
                    mFragment = EditReimbursementFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.reimbursement_edit))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AchievementFragment -> {
                if (enableFragGeneration) {
                    mFragment = AchievementFragment()
                }
                setTopBarTitle(getString(R.string.my_performance))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.NewCollectionListFragment -> {
                if (enableFragGeneration) {
                    mFragment = NewCollectionListFragment()
                }
                setTopBarTitle(getString(R.string.collection))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.BillingListFragment -> {
                if (enableFragGeneration) {
                    mFragment = BillingListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.billing_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddBillingFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddBillingFragment.newInstance(initializeObject)
                }
                tv_confirm_btn.text = "Confirm"
                setTopBarTitle(getString(R.string.update_billing_details))
                setTopBarVisibility(TopBarConfig.CARTDETAILS)
            }
            FragType.AttendanceReportFragment -> {
                if (enableFragGeneration) {
                    mFragment = AttendanceReportFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.attendance_report))

                if (isAttendanceFromAlarm)
                    setTopBarVisibility(TopBarConfig.GPS)
                else {
                    if (isAttendanceReportFromDrawer)
                        setTopBarVisibility(TopBarConfig.HOME)
                    else
                        setTopBarVisibility(TopBarConfig.BACK)
                }
                //setTopBarVisibility(TopBarConfig.GPS)

            }
            FragType.PerformanceReportFragment -> {
                if (enableFragGeneration) {
                    mFragment = PerformanceReportFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.performance_report))
                if (isPerformanceFromAlarm)
                    setTopBarVisibility(TopBarConfig.GPS)
                else {
                    if (isPerformanceReportFromDrawer)
                        setTopBarVisibility(TopBarConfig.HOME)
                    else
                        setTopBarVisibility(TopBarConfig.BACK)
                }

                //setTopBarVisibility(TopBarConfig.GPS)
            }
            FragType.VisitReportFragment -> {
                if (enableFragGeneration) {
                    mFragment = VisitReportFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.visit_report))
                if (isVisitFromAlarm)
                    setTopBarVisibility(TopBarConfig.GPS)
                else {
                    if (isVisitReportFromDrawer)
                        setTopBarVisibility(TopBarConfig.HOME)
                    else
                        setTopBarVisibility(TopBarConfig.BACK)
                }

                //setTopBarVisibility(TopBarConfig.GPS)
            }
            FragType.VisitReportDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = VisitReportDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.visit_report_details))
                setTopBarVisibility(TopBarConfig.VISITREPORTDETAILS)
            }
            FragType.NotificationFragment -> {
                if (enableFragGeneration) {
                    mFragment = NotificationFragment()
                }
                setTopBarTitle(getString(R.string.notification))
                setTopBarVisibility(TopBarConfig.NOTIFICATION)
            }
            FragType.BillingDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = BillingDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.bill))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.KnowYourStateFragment -> {
                if (enableFragGeneration) {
                    mFragment = KnowYourStateFragment()
                }
                setTopBarTitle(getString(R.string.report_on_state))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.DailyPlanListFragment -> {
                if (enableFragGeneration) {
                    mFragment = DailyPlanListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(Pref.dailyPlanListHeaderText)

                if (!isDailyPlanFromAlarm) {
                    if (!AppUtils.isFromAttendance)
                        setTopBarVisibility(TopBarConfig.TARGETPLAN)
                    else
                        setTopBarVisibility(TopBarConfig.TARGETPLANBACK)
                } else
                    setTopBarVisibility(TopBarConfig.TARGETPLANGPS)
            }
            FragType.PlanDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = PlanDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.plan_details))

                if (!isDailyPlanFromAlarm)
                    setTopBarVisibility(TopBarConfig.BACK)
                else
                    setTopBarVisibility(TopBarConfig.TARGETPLANDETAILSBACK)
            }
            FragType.AllShopListFragment -> {
                if (enableFragGeneration) {
                    mFragment = AllShopListFragment()
                }
                setTopBarTitle(Pref.allPlanListHeaderText)
                setTopBarVisibility(TopBarConfig.ALLSHOPLIST)
            }
            FragType.MeetingListFragment -> {
                if (enableFragGeneration) {
                    mFragment = MeetingListFragment()
                }
                setTopBarTitle(getString(R.string.meeting_list))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.MemberListFragment -> {
                if (enableFragGeneration) {
                    mFragment = MemberListFragment.newInstance(initializeObject)
                }

                if (!isAllTeam)
                    setTopBarTitle(getString(R.string.team_details))
                else
                    setTopBarTitle(getString(R.string.all_team_details))

                /*if (!isAddBackStack)
                    setTopBarVisibility(TopBarConfig.HOME)
                else*/
                setTopBarVisibility(TopBarConfig.ONLINEMEMBERLIST)
            }
            FragType.MemberShopListFragment -> {
                if (enableFragGeneration) {
                    mFragment = MemberShopListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.team_shop_details))
                setTopBarVisibility(TopBarConfig.MEMBERSHOPLIST)
            }
            FragType.MemberAllShopListFragment -> {
                if (enableFragGeneration) {
                    mFragment = MemberAllShopListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.team_shop_details))
                setTopBarVisibility(TopBarConfig.MEMBERSHOPLIST)
            }
            FragType.ActivityMapFragment -> {
                if (enableFragGeneration) {
                    mFragment = ActivityMapFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.map))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.MemberActivityFragment -> {
                if (enableFragGeneration) {
                    mFragment = MemberActivityFragment.newInstance(initializeObject)
                }
                isMemberMap = true
                setTopBarTitle(getString(R.string.traveling_history))
                setTopBarVisibility(TopBarConfig.MEMBERACTIVITYMAP)
            }
            FragType.MemberPJPListFragment -> {
                if (enableFragGeneration) {
                    mFragment = MemberPJPListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.journey_plan_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddPJPFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddPJPFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.add_pjp))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.EditPJPFragment -> {
                if (enableFragGeneration) {
                    mFragment = EditPJPFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.edit_pjp))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddPJPLocationFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddPJPLocationFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.map))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.LocalShopListMapFragment -> {
                if (enableFragGeneration) {
                    mFragment = LocalShopListMapFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.map))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.TimeSheetListFragment -> {
                if (enableFragGeneration) {
                    mFragment = TimeSheetListFragment()
                }
                setTopBarTitle(getString(R.string.timesheet_list))
                setTopBarVisibility(TopBarConfig.TIMESHEETLIST)
            }
            FragType.AddTimeSheetFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddTimeSheetFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.timesheet_add))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.EditTimeSheetFragment -> {
                if (enableFragGeneration) {
                    mFragment = EditTimeSheetFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.timesheet_edit))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AreaListFragment -> {
                if (enableFragGeneration) {
                    mFragment = AreaListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.area_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddQuotationFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddQuotationFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.add_quo))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.QuotationListFragment -> {
                if (enableFragGeneration) {
                    mFragment = QuotationListFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.quot_list))

                if (isBack)
                    setTopBarVisibility(TopBarConfig.BACK)
                else
                    setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.QuotationDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = QuotationDetailsFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.quot_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.DateWiseQuotationList -> {
                if (enableFragGeneration) {
                    mFragment = DateWiseQuotationList()
                }
                setTopBarTitle(getString(R.string.quot_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.EditQuotationFragment -> {
                if (enableFragGeneration) {
                    mFragment = EditQuotationFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.edit_quot))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.OfflineMemberListFragment -> {
                if (enableFragGeneration) {
                    mFragment = OfflineMemberListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.team_details))
                setTopBarVisibility(TopBarConfig.OFFLINEMEMBERLIST)
            }
            FragType.OfflineAllShopListFragment -> {
                if (enableFragGeneration) {
                    mFragment = OfflineAllShopListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.team_shop_details))
                setTopBarVisibility(TopBarConfig.OFFLINESHOPLIST)
            }
            FragType.OfflineShopListFragment -> {
                if (enableFragGeneration) {
                    mFragment = OfflineShopListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.team_shop_details))
                setTopBarVisibility(TopBarConfig.OFFLINESHOPLIST)
            }
            FragType.OfflineAreaListFragment -> {
                if (enableFragGeneration) {
                    mFragment = OfflineAreaListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.area_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.UpdateWorkTypeFragment -> {
                if (enableFragGeneration) {
                    mFragment = UpdateWorkTypeFragment()
                }
                setTopBarTitle(getString(R.string.update_work_type))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.AchievementReportFragment -> {
                if (enableFragGeneration) {
                    mFragment = AchievementReportFragment()
                }
                setTopBarTitle(getString(R.string.achv_report))

                if (isAchvFromDrawer)
                    setTopBarVisibility(TopBarConfig.HOME)
                else
                    setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AchievementDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = AchievementDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.achv_report_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.TargetVsAchvFragment -> {
                if (enableFragGeneration) {
                    mFragment = TargetVsAchvFragment()
                }
                setTopBarTitle(getString(R.string.targ_achv_report))

                if (isTargAchvFromDrawer)
                    setTopBarVisibility(TopBarConfig.HOME)
                else
                    setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.TargetVsAchvDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = TargetVsAchvDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.targ_achv_report_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.LeaveListFragment -> {
                if (enableFragGeneration) {
                    mFragment = LeaveListFragment()
                }
                setTopBarTitle(getString(R.string.leave_list))
                setTopBarVisibility(TopBarConfig.LEAVELIST)
            }
            FragType.ApplyLeaveFragment -> {
                if (enableFragGeneration) {
                    mFragment = ApplyLeaveFragment()
                }
                setTopBarTitle(getString(R.string.apply_leave))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.TaskListFragment -> {
                if (enableFragGeneration) {
                    mFragment = TaskListFragment()
                }
                setTopBarTitle(getString(R.string.task_list))
                setTopBarVisibility(TopBarConfig.TASKLIST)
            }
            FragType.AddTaskFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddTaskFragment()
                }
                setTopBarTitle(getString(R.string.add_task))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.EditTaskFragment -> {
                if (enableFragGeneration) {
                    mFragment = EditTaskFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.edit_task))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddDynamicFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddDynamicFragment.newInstance(initializeObject)
                }
                setTopBarTitle("Add $dynamicScreen")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AllDynamicListFragment -> {
                if (enableFragGeneration) {
                    mFragment = AllDynamicListFragment()
                }
                setTopBarTitle(getString(R.string.dynamic_list))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.DynamicListFragment -> {
                if (enableFragGeneration) {
                    mFragment = DynamicListFragment.newInstance(initializeObject)
                }
                setTopBarTitle("$dynamicScreen List")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.EditDynamicFragment -> {
                if (enableFragGeneration) {
                    mFragment = EditDynamicFragment.newInstance(initializeObject)
                }
                setTopBarTitle("Edit $dynamicScreen")
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddActivityFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddActivityFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.add_activity))

                if (isFromMenu)
                    setTopBarVisibility(TopBarConfig.HOME)
                else
                    setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.EditActivityFragment -> {
                if (enableFragGeneration) {
                    mFragment = EditActivityFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.edit_activity))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ActivityDetailsListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ActivityDetailsListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.activities))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ActivityShopListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ActivityShopListFragment()
                }
                setTopBarTitle(getString(R.string.activity_party))
                setTopBarVisibility(TopBarConfig.ACTIVITSHOP)
            }
            FragType.DateWiseActivityListFragment -> {
                if (enableFragGeneration) {
                    mFragment = DateWiseActivityListFragment()
                }
                setTopBarTitle(getString(R.string.date_wise_activities))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ShopBillingListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ShopBillingListFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.billing_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ShopBillingDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = ShopBillingDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.bill))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.CollectionDetailsStatusFragment -> {
                if (enableFragGeneration) {
                    mFragment = CollectionDetailsStatusFragment()
                }
                setTopBarTitle(getString(R.string.collection_details))

                if (isCollectionStatusFromDrawer)
                    setTopBarVisibility(TopBarConfig.HOME)
                else
                    setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.CollectionShopListFragment -> {
                if (enableFragGeneration) {
                    mFragment = CollectionShopListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.collection_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ChemistActivityListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ChemistActivityListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.activity_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.DoctorActivityListFragment -> {
                if (enableFragGeneration) {
                    mFragment = DoctorActivityListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.activity_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddChemistFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddChemistFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.add_activity))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.EditChemistActivityFragment -> {
                if (enableFragGeneration) {
                    mFragment = EditChemistActivityFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.edit_activity))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ChemistDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = ChemistDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.activity_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.AddDoctorFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddDoctorFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.add_activity))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.EditDoctorActivityFragment -> {
                if (enableFragGeneration) {
                    mFragment = EditDoctorActivityFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.edit_activity))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.DoctorDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = DoctorDetailsFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.activity_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.WeatherFragment -> {
                if (enableFragGeneration) {
                    mFragment = WeatherFragment()
                }
                setTopBarTitle(getString(R.string.weather))
                setTopBarVisibility(TopBarConfig.WEATHERMAP)
            }
            FragType.DocumentTypeListFragment -> {
                if (enableFragGeneration) {
                    mFragment = DocumentTypeListFragment()
                }
                setTopBarTitle(getString(R.string.doc_repo))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.DocumentListFragment -> {
                if (enableFragGeneration) {
                    mFragment = DocumentListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.doc_repo))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ChatBotFragment -> {
                if (enableFragGeneration) {
                    mFragment = ChatBotFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.chat_bot))
                setTopBarVisibility(TopBarConfig.CHATBOT)
            }
            FragType.CalenderTaskFragment -> {
                if (enableFragGeneration) {
                    mFragment = CalenderTaskFragment()
                }
                setTopBarTitle(getString(R.string.calendar))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ReportsFragment -> {
                if (enableFragGeneration) {
                    mFragment = ReportsFragment()
                }
                setTopBarTitle(getString(R.string.reports))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ChatUserListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ChatUserListFragment()
                }
                setTopBarTitle(getString(R.string.chat))
                setTopBarVisibility(TopBarConfig.CHATUSER)
            }
            FragType.ChatListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ChatListFragment.newInstance(initializeObject)
                }
                setTopBarTitle(AppUtils.decodeEmojiAndText(userName))
                setTopBarVisibility(TopBarConfig.CHAT)
            }
            FragType.AddGroupFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddGroupFragment()
                }
                setTopBarTitle(getString(R.string.new_grp))
                setTopBarVisibility(TopBarConfig.NEWGROUP)
            }
            FragType.AddPeopleFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddPeopleFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.add_people))
                setTopBarVisibility(TopBarConfig.NEWGROUP)
            }
            FragType.ShowPeopleFragment -> {
                if (enableFragGeneration) {
                    mFragment = ShowPeopleFragment.newInstance(initializeObject)
                }
                setTopBarTitle(userName)
                setTopBarVisibility(TopBarConfig.NEWGROUP)
            }
            FragType.AddNewMsgFragment -> {
                if (enableFragGeneration) {
                    mFragment = AddNewMsgFragment()
                }
                setTopBarTitle(getString(R.string.new_conversation))
                setTopBarVisibility(TopBarConfig.NEWGROUP)
            }
            FragType.TimeLineFragment -> {

                if (enableFragGeneration) {
                    mFragment = TimeLineFragment()
                }
                isMemberMap = false
                setTopBarTitle(getString(R.string.traveling_history))
                setTopBarVisibility(TopBarConfig.ACTIVITYMAP)
            }
            FragType.ScanImageFragment -> {
                if (enableFragGeneration) {
                    mFragment = ScanImageFragment()
                }
                setTopBarTitle(getString(R.string.scan_visiting_card))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.CodeScannerFragment -> {
                if (enableFragGeneration) {
                    mFragment = CodeScannerFragment()
                }

                if (!isForRevisit)
                    setTopBarTitle(getString(R.string.code_scanner))
                else
                    setTopBarTitle("Scan QR for ${Pref.shopText} Revisit")

                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.NearbyUserListFragment -> {
                if (enableFragGeneration) {
                    mFragment = NearbyUserListFragment()
                }
                setTopBarTitle(getString(R.string.nearby_user))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.ChatBotShopListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ChatBotShopListFragment.getInstance(initializeObject)
                }

                try {
                    val isVisit = initializeObject as Boolean
                    if (isVisit)
                        setTopBarTitle("Visit Analysis ${Pref.shopText}wise")
                    else
                        setTopBarTitle("Order Analysis ${Pref.shopText}wise")
                    setTopBarVisibility(TopBarConfig.CHATBOTSHOP)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            FragType.HomeLocationFragment -> {
                if (enableFragGeneration) {
                    mFragment = HomeLocationFragment()
                }
                setTopBarTitle(AppUtils.hiFirstNameText())
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.HomeLocationMapFragment -> {
                if (enableFragGeneration) {
                    mFragment = HomeLocationMapFragment()
                }
                setTopBarTitle(getString(R.string.map))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.BeatListFragment -> {
                if (enableFragGeneration) {
                    mFragment = BeatListFragment()
                }
                setTopBarTitle(getString(R.string.beat_list))
                setTopBarVisibility(TopBarConfig.BEATLIST)
            }
            FragType.DeviceInfoListFragment -> {
                if (enableFragGeneration) {
                    mFragment = DeviceInfoListFragment()
                }
                setTopBarTitle(getString(R.string.device_info))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.ViewPermissionFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewPermissionFragment()
                }
                setTopBarTitle(getString(R.string.permission_info))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.MicroLearningListFragment -> {
                if (enableFragGeneration) {
                    mFragment = MicroLearningListFragment()
                }
                setTopBarTitle(getString(R.string.training_contents))
                setTopBarVisibility(TopBarConfig.MICROLEARNING)
            }
            FragType.MicroLearningWebViewFragment -> {
                if (enableFragGeneration) {
                    mFragment = MicroLearningWebViewFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.training_contents))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.OpenFileWebViewFragment -> {
                if (enableFragGeneration) {
                    mFragment = OpenFileWebViewFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.attach_document))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.MyJobsFragment -> {
                if (enableFragGeneration) {
                    //mFragment = MyJobsFragment()
                    mFragment = MyJobsFragment.setUserID(initializeObject)
                }
                setTopBarTitle(getString(R.string.myjobs))
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.JobsCustomerFragment -> {
                if (enableFragGeneration) {
                    mFragment = JobsCustomerFragment.newInstance(initializeObject)
                }
                setTopBarTitle(Pref.shopText)
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.WorkInProgressFragment -> {
                if (enableFragGeneration) {
                    mFragment = WorkInProgressFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.wip))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.WorkOnHoldFragment -> {
                if (enableFragGeneration) {
                    mFragment = WorkOnHoldFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.woh))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.WorkCompletedFragment -> {
                if (enableFragGeneration) {
                    mFragment = WorkCompletedFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.work_completed))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.WorkCancelledFragment -> {
                if (enableFragGeneration) {
                    mFragment = WorkCancelledFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.work_cancelled))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.UpdateReviewFragment -> {
                if (enableFragGeneration) {
                    mFragment = UpdateReviewFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.update_review))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.CustomerListFragment -> {
                if (enableFragGeneration) {
                    mFragment = CustomerListFragment()
                }
                setTopBarTitle(getString(R.string.customer) + "(s)")
                setTopBarVisibility(TopBarConfig.CUSTOMER)
            }
            FragType.ServiceHistoryFragment -> {
                if (enableFragGeneration) {
                    mFragment = ServiceHistoryFragment.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.service_history))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.ProtoRegistrationFragment -> {
                if (enableFragGeneration) {
                    mFragment = ProtoRegistrationFragment.getInstance(initializeObject)
                    //mFragment = BaseFragment()
                }
                setTopBarTitle(getString(R.string.photo_registration))
                setTopBarVisibility(TopBarConfig.HOME)
                setTopBarVisibility(TopBarConfig.PHOTOREG)
            }
            FragType.LeaveHome -> {
                if (enableFragGeneration) {
                    mFragment = LeaveHome.getInstance(initializeObject)
                    //mFragment = BaseFragment()
                }
                setTopBarTitle("Leave Approval")
                setTopBarVisibility(TopBarConfig.HOME)
            }
            FragType.PhotoAttendanceFragment -> {
                if (enableFragGeneration) {
                    mFragment = PhotoAttendanceFragment.getInstance(initializeObject)
                    //mFragment = BaseFragment()
                }
                setTopBarTitle(getString(R.string.team_attendance))
                setTopBarVisibility(TopBarConfig.HOME)
                setTopBarVisibility(TopBarConfig.PHOTOREG)
            }
            FragType.RegisTerFaceFragment -> {
                if (enableFragGeneration) {
                    mFragment = RegisTerFaceFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.photo_registration))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.TeamAttendanceFragment -> {
                if (enableFragGeneration) {
                    mFragment = TeamAttendanceFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.team_attendance))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.DocumentRepoFeatureNewFragment -> {
                if (enableFragGeneration) {
                    mFragment = DocumentRepoFeatureNewFragment()
                }
                setTopBarTitle(getString(R.string.doc_repo))
                setTopBarVisibility(TopBarConfig.HOME)
                setTopBarVisibility(TopBarConfig.NOTIFICATION)
            }
            FragType.NewOrderScrActiFragment -> {
                if (enableFragGeneration) {
                    mFragment = NewOrderScrActiFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.add_order_text))
                setTopBarVisibility(TopBarConfig.NEWORDERCART)
//                setTopBarVisibility(TopBarConfig.CART)
            }
            FragType.NeworderScrCartFragment -> {
                if (enableFragGeneration) {
                    mFragment = NeworderScrCartFragment.getInstance(initializeObject)
                }
//                tv_confirm_btn.text = "Place Order"
                setTopBarVisibility(TopBarConfig.NEWORDERSCRCART)
                if (CustomStatic.IsFromViewNewOdrScr == true) {
                    setTopBarTitle("Order Details")
                } else {
                    setTopBarTitle(getString(R.string.new_order_scr_cart))
                }

//                setTopBarVisibility(TopBarConfig.BACK)
            }

            FragType.NewOrderScrOrderDetailsFragment -> {
                if (enableFragGeneration) {
                    mFragment = NewOrderScrOrderDetailsFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.new_order_scr_list))
                setTopBarVisibility(TopBarConfig.BACK)
            }
            FragType.NewOdrScrListFragment -> {
                if (enableFragGeneration) {
                    mFragment = NewOdrScrListFragment()
                }
                setTopBarTitle(getString(R.string.orders))
                // setTopBarVisibility(TopBarConfig.ORDERLIST)
                setTopBarVisibility(TopBarConfig.HOME)
            }

              FragType.MapViewForTeamFrag -> {
                if (enableFragGeneration) {
                    mFragment = MapViewForTeamFrag.newInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.map_view))
                setTopBarVisibility(TopBarConfig.TEAMMAP)
            }

            FragType.LeaveHome -> {
                if (enableFragGeneration) {
                    mFragment = LeaveHome()
                }
                setTopBarVisibility(TopBarConfig.HOME)
            }

            FragType.ViewAllReturnListFragment -> {
                if (enableFragGeneration) {
                    mFragment = ViewAllReturnListFragment.getInstance(initializeObject)
                }
                setTopBarTitle(getString(R.string.return_details))
                setTopBarVisibility(TopBarConfig.BACK)
            }

            else -> {
                if (enableFragGeneration) {
                    mFragment = DashboardFragment().getInstance(initializeObject)

                }
                setTopBarTitle(getString(R.string.blank))
                setTopBarVisibility(TopBarConfig.HOME)
            }




        }

//        FragType.DocumentRepoFeatureNewFragment->{
//
//        }

        /*if (getFragment() != null && getFragment() is ReimbursementListFragment) {
            if ((getFragment() as ReimbursementListFragment).mPopupWindow != null && (getFragment() as ReimbursementListFragment).mPopupWindow!!.isShowing)
                (getFragment() as ReimbursementListFragment).mPopupWindow?.dismiss()
        }*/

        searchView.closeSearch()
        return mFragment
    }

    fun setTopBarTitle(title: String) {
         headerTV.text = title
    }

    private fun setTopBarVisibility(mTopBarConfig: TopBarConfig) {
        tv_noti_count.visibility = View.GONE
        when (mTopBarConfig) {
            TopBarConfig.HOME -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.CUSTOMER -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.MICROLEARNING -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.BEATLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.CHATUSER -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                /*// Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.isDrawerIndicatorEnabled = false
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)*/
            }
            TopBarConfig.CHATBOT -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.VISIBLE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (isChatFromDrawer) {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    // Show hamburger
                    mDrawerToggle.isDrawerIndicatorEnabled = true
                    toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                } else {
                    // Show back button
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    mDrawerToggle.isDrawerIndicatorEnabled = false
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
            TopBarConfig.LEAVELIST -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)

                if (Pref.isChatBotShow)
                    ic_chat_bot.visibility = View.VISIBLE
                else
                    ic_chat_bot.visibility = View.GONE

                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.ATTENDENCELIST -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)

                if (Pref.isChatBotShow)
                    ic_chat_bot.visibility = View.VISIBLE
                else
                    ic_chat_bot.visibility = View.GONE

                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.TASKLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.VISIBLE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.LOCALSHOP -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.GONE
                iv_map.visibility = View.VISIBLE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (!isChatBotLocalShop) {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    // Show hamburger
                    mDrawerToggle.isDrawerIndicatorEnabled = true
                    toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                } else {
                    // Show back button
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    mDrawerToggle.isDrawerIndicatorEnabled = false
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
            TopBarConfig.ORDERLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (Pref.isChatBotShow)
                    ic_chat_bot.visibility = View.VISIBLE
                else
                    ic_chat_bot.visibility = View.GONE

                if (isOrderFromChatBot) {
                    // Show back button
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    mDrawerToggle.isDrawerIndicatorEnabled = false
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                } else {
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    // Show hamburger
                    mDrawerToggle.isDrawerIndicatorEnabled = true
                    toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
            TopBarConfig.DASHBOARD -> {
                iv_home_icon.visibility = View.GONE
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE

                if (!Pref.isAttendanceFeatureOnly)
                    logo.visibility = View.VISIBLE
                else
                    logo.visibility = View.GONE

                if(Pref.NotiCountFlag){
                    tv_noti_count.visibility = View.VISIBLE
                }else{
                    tv_noti_count.visibility = View.GONE
                }

                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.VISIBLE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.VISIBLE

                if (Pref.isScanQrForRevisit)
                    iv_scan.visibility = View.VISIBLE
                else
                    iv_scan.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.SHOPLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                logo.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (Pref.isChatBotShow)
                    ic_chat_bot.visibility = View.VISIBLE
                else
                    ic_chat_bot.visibility = View.GONE

                if (isShopFromChatBot /*|| Pref.isShowShopBeatWise*/) {
                    // Show back button
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    mDrawerToggle.isDrawerIndicatorEnabled = false
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                } else {
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    // Show hamburger
                    mDrawerToggle.isDrawerIndicatorEnabled = true
                    toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
            TopBarConfig.SHOPLISTV1 -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                logo.visibility = View.VISIBLE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.MAPVIEW -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                logo.visibility = View.VISIBLE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (isMapFromDrawer) {
                    supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                    // Show hamburger
                    mDrawerToggle.isDrawerIndicatorEnabled = true
                    toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                } else {
                    // Show back button
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    mDrawerToggle.isDrawerIndicatorEnabled = false
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
            TopBarConfig.GPS -> {
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_home_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                mDrawerToggle.setHomeAsUpIndicator(null)
                //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.CART -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.VISIBLE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.CHATBOTSHOP -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.NEWORDERCART -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.VISIBLE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.CARTDETAILS -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.VISIBLE
                iv_settings.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.ADDATTENDANCE -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (Pref.isChatBotShow)
                    ic_chat_bot.visibility = View.VISIBLE
                else
                    ic_chat_bot.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.LOGOUTSYNC -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                mDrawerToggle.setHomeAsUpIndicator(null)
            }
            TopBarConfig.VISITREPORTDETAILS -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                iv_settings.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.NOTIFICATION -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.SEARCHLOCATION -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.SEARCHLOCATIONFROMADDSHOP -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.TARGETPLAN -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)

                if (Pref.isChatBotShow)
                    ic_chat_bot.visibility = View.VISIBLE
                else
                    ic_chat_bot.visibility = View.GONE

                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.TARGETPLANBACK -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (Pref.isChatBotShow)
                    ic_chat_bot.visibility = View.VISIBLE
                else
                    ic_chat_bot.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.TARGETPLANDETAILSBACK -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.TARGETPLANGPS -> {
                /*mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_home_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                logo.visibility = View.GONE
                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)*/


                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_home_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                mDrawerToggle.setHomeAsUpIndicator(null)
            }
            TopBarConfig.ALLSHOPLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.MEMBERACTIVITYMAP -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                iv_settings.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_map.visibility = View.VISIBLE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (!isMemberMap) {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    // Show hamburger
                    mDrawerToggle.isDrawerIndicatorEnabled = true
                    toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                } else {
                    // Show back button
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
            TopBarConfig.ACTIVITYMAP -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                iv_settings.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_map.visibility = View.VISIBLE
                ic_calendar.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (Pref.isChatBotShow)
                    ic_chat_bot.visibility = View.VISIBLE
                else
                    ic_chat_bot.visibility = View.GONE

                if (!isMemberMap) {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    // Show hamburger
                    mDrawerToggle.isDrawerIndicatorEnabled = true
                    toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                } else {
                    // Show back button
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
            TopBarConfig.ADDSHOP -> {
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (Pref.willScanVisitingCard) {
                    iv_scan.visibility = View.VISIBLE
                    iv_view_text.visibility = View.VISIBLE
                    iv_home_icon.visibility = View.GONE
                    logo.visibility = View.GONE
                } else {
                    iv_scan.visibility = View.GONE
                    iv_view_text.visibility = View.GONE
                    iv_home_icon.visibility = View.VISIBLE
                    logo.visibility = View.VISIBLE
                }

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.PHOTOREG -> {
                mDrawerToggle.isDrawerIndicatorEnabled = true
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (Pref.willScanVisitingCard) {
                    iv_scan.visibility = View.VISIBLE
                    iv_view_text.visibility = View.VISIBLE
                    iv_home_icon.visibility = View.GONE
                    logo.visibility = View.GONE
                } else {
                    iv_scan.visibility = View.GONE
                    iv_view_text.visibility = View.GONE
                    iv_home_icon.visibility = View.VISIBLE
                    logo.visibility = View.VISIBLE
                }

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }

            TopBarConfig.LEADFR -> {
                mDrawerToggle.isDrawerIndicatorEnabled = true
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                fl_net_status.visibility = View.GONE

//                if (Pref.willScanVisitingCard) {
//                    iv_scan.visibility = View.VISIBLE
//                    iv_view_text.visibility = View.VISIBLE
//                    iv_home_icon.visibility = View.GONE
//                    logo.visibility = View.GONE
//                }
//                else {
//                    iv_scan.visibility = View.GONE
//                    iv_view_text.visibility = View.GONE
//                    iv_home_icon.visibility = View.VISIBLE
//                    logo.visibility = View.VISIBLE
//                }

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.SHOPDETAILS -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.MEMBERSHOPLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.OFFLINESHOPLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.OFFLINEMEMBERLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.TIMESHEETLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.ONLINEMEMBERLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.ACTIVITSHOP -> {
                iv_home_icon.visibility = View.VISIBLE
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                // Show hamburger
                mDrawerToggle.isDrawerIndicatorEnabled = true
                toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
            TopBarConfig.WEATHERMAP -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                iv_settings.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_map.visibility = View.VISIBLE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (isWeatherFromDrawer) {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    // Show hamburger
                    mDrawerToggle.isDrawerIndicatorEnabled = true
                    toolbar.setNavigationIcon(R.drawable.ic_header_menu_icon)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                } else {
                    // Show back button
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
            }
            TopBarConfig.ATTENDENCEBACKLIST -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (Pref.isChatBotShow)
                    ic_chat_bot.visibility = View.VISIBLE
                else
                    ic_chat_bot.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.NEWGROUP -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.VISIBLE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.CHAT -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                if (isGrp)
                    iv_people.visibility = View.VISIBLE
                else
                    iv_people.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.BACK -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.VISIBLE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.TEAMMAP -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.VISIBLE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.DISTWISEORDER -> {
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.GONE
                logo.clearAnimation()
                logo.animate().cancel()
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            TopBarConfig.NEWORDERSCRCART -> {
                /*  if(CustomStatic.IsFromViewNewOdrScr==false){
                    rl_confirm_btn.visibility = View.VISIBLE
                }
                else{
                    rl_confirm_btn.visibility = View.GONE
                }*/
                rl_confirm_btn.visibility = View.GONE
                iv_home_icon.visibility = View.GONE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                logo.visibility = View.GONE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE


                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            else -> {
                iv_home_icon.visibility = View.VISIBLE
                mDrawerToggle.isDrawerIndicatorEnabled = false
                iv_search_icon.visibility = View.GONE
                iv_sync_icon.visibility = View.GONE
                rl_cart.visibility = View.GONE
                iv_filter_icon.visibility = View.GONE
                rl_confirm_btn.visibility = View.GONE
                logo.visibility = View.VISIBLE
                iv_list_party.visibility = View.GONE
                iv_map.visibility = View.GONE
                iv_settings.visibility = View.GONE
                ic_calendar.visibility = View.GONE
                ic_chat_bot.visibility = View.GONE
                iv_cancel_chat.visibility = View.GONE
                iv_people.visibility = View.GONE
                iv_scan.visibility = View.GONE
                iv_view_text.visibility = View.GONE
                fl_net_status.visibility = View.GONE

                // Show back button
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_header_back_arrow)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }


    }

    var isShowAlert = true
    var qrCodeText = ""
    @SuppressLint("NewApi")
    override fun onBackPressed() {
        val fm = supportFragmentManager
        fm.executePendingTransactions()
        //TODO Hide Soft Keyboard
        AppUtils.hideSoftKeyboard(this)

        XLog.e("Current Fragment========> " + getFragment())

        var tt=getFragment().toString()
        var ttt=fm.backStackEntryCount

        if (fm.backStackEntryCount == 0 && getFragment() != null && (getFragment() is PerformanceReportFragment || getFragment() is AttendanceReportFragment
                        || getFragment() is VisitReportFragment || getFragment() is DailyPlanListFragment)) {
            if (isConfirmed) {
                loadFragment(FragType.DashboardFragment, false, Any())
                isConfirmed = false
            } else {
                if (getFragment() is DailyPlanListFragment) {
                    loadFragment(FragType.DashboardFragment, false, Any())

                    Handler().postDelayed(Runnable {
                        try {
                            (getFragment() as DashboardFragment).updateItem()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, 350)
                }
            }
        } else if (fm.backStackEntryCount == 0 && getFragment() != null && getFragment() !is DashboardFragment) {
            if (getFragment() != null && getFragment() is MicroLearningListFragment) {
                if ((getFragment() as MicroLearningListFragment).isFilterSelected)
                    (getFragment() as MicroLearningListFragment).showAllList()
                else {
                    loadFragment(FragType.DashboardFragment, false, Any())
                    Handler().postDelayed(Runnable {
                        (getFragment() as DashboardFragment).updateItem()
                    }, 500)
                }
            }else if(getFragment() != null && getFragment() is LeaveHome){
                //loadFragment(FragType.DashboardFragment, false, Any())
                //loadFragment(FragType.MemberListFragment, false, Pref.user_id!!)
                Handler().postDelayed(Runnable {
                    //(getFragment() as DashboardFragment).updateItem()
                    loadFragment(FragType.MemberListFragment, false, Pref.user_id!!)
                }, 500)
            }
            else {
                loadFragment(FragType.DashboardFragment, false, Any())
                //Added Saheli 27-07-21
                AppUtils.changeLanguage(this, "en")
                Handler().postDelayed(Runnable {
                    (getFragment() as DashboardFragment).updateItem()
                }, 500)
            }
        } else if (getFragment() != null && getFragment() is DashboardFragment) {
            if (backpressed + 2000 > System.currentTimeMillis()) {
                finish()
                super.onBackPressed()
            } else {
                showSnackMessage(getString(R.string.alert_exit))
            }
            backpressed = System.currentTimeMillis()

        } else if (getFragment() != null && getFragment() is SearchLocationFragment) {

            super.onBackPressed()

            if (getFragment() != null) {
                if (getFragment() is DashboardFragment) {
                    if (locationInfoModel != null)
                        (getFragment() as DashboardFragment).sendHomeLoc(locationInfoModel)
                    else
                        checkToShowHomeLocationAlert()
                }
                else if (getFragment() is WeatherFragment) {
                    if (locationInfoModel != null) {
                        (getFragment() as WeatherFragment).getLocationFromMap(locationInfoModel)
                        locationInfoModel = null
                    }
                }
                else {
                    super.onBackPressed()
                    if (locationInfoModel != null) {
                        loadFragment(FragType.AddShopFragment, true, locationInfoModel!!)
                    }
                }
            }

        } else if (getFragment() != null && getFragment() is OrderTypeListFragment) {

            if (isShowAlert)
                showAlert()
            else {
                super.onBackPressed()
                isShowAlert = true

                AppUtils.clearPreferenceKey(this, "PRODUCT_RATE_LIST")

                if (getFragment() != null && getFragment() is ViewAllOrderListFragment)
                    (getFragment() as ViewAllOrderListFragment).updateList()
                else if (getFragment() != null && getFragment() is StockListFragment)
                    (getFragment() as StockListFragment).updateList()

                qtyList.clear()
                rateList.clear()
                totalPrice.clear()
                totalScPrice.clear()

                schemaqtyList.clear()
                schemarateList.clear()
                mrpList.clear()
            }
        }
        else if (getFragment() != null && getFragment() is ReturnTypeListFragment) {
            if (isShowAlert)
                showAlert()
            else {
                super.onBackPressed()
                isShowAlert = true

                AppUtils.clearPreferenceKey(this, "PRODUCT_RATE_LIST")

                if (getFragment() != null && getFragment() is ViewAllReturnListFragment)
                    (getFragment() as ViewAllReturnListFragment).updateList()

                qtyList.clear()
                rateList.clear()
                totalPrice.clear()
                totalScPrice.clear()
                schemaqtyList.clear()
                schemarateList.clear()
                mrpList.clear()

            }
        }
        else if (getFragment() != null && getFragment() is AddBillingFragment) {

            schemaqtyList.clear()

            qtyList.clear()
            rateList.clear()
            totalPrice.clear()
            totalScPrice.clear()
            schemarateList.clear()
            mrpList.clear()

            super.onBackPressed()

            when {
                getFragment() is BillingListFragment -> (getFragment() as BillingListFragment).updateItem()
                getFragment() is NewDateWiseOrderListFragment -> (getFragment() as NewDateWiseOrderListFragment).updateItem()
                getFragment() is NewOrderListFragment -> (getFragment() as NewOrderListFragment).updateItem()
            }
        }
        /*else if (getFragment() != null && getFragment() is CartFragment) {

            super.onBackPressed()

            totalPrice.clear()
        }*/
        else if (getFragment() != null && getFragment() is GpsDisableFragment) {
            if (!isGpsDisabled)
                super.onBackPressed()
        } else if (getFragment() != null && (getFragment() is AttendanceReportFragment || getFragment() is PerformanceReportFragment ||
                        getFragment() is VisitReportFragment)) {
            if (isConfirmed) {
                super.onBackPressed()
                isConfirmed = false
            }
        } else if (getFragment() != null && getFragment() is AddAttendanceFragment) {

            if(Pref.IsPendingColl && Pref.ShowZeroCollectioninAlert){
                SendBrod.sendBrodColl(this)
                tv_noti_count.visibility=View.VISIBLE
            }else{
                tv_noti_count.visibility=View.GONE
            }

            if(Pref.IsZeroOrder && Pref.IsShowRepeatOrderinNotification){
                SendBrod.sendBrodZeroOrder(this)
                tv_noti_count.visibility=View.VISIBLE
            }else{
                tv_noti_count.visibility=View.GONE
            }

            /*if(Pref.IsTodayDOBDOA){
                //SendBrod.sendBrodDOBDOA(this)
                tv_noti_count.visibility=View.VISIBLE
            }else{
                tv_noti_count.visibility=View.GONE
            }*/

            isAddAttendaceAlert = false
            AppUtils.isFromAttendance = false
            super.onBackPressed()

            XLog.e("isAddAttendence========> " + Pref.isAddAttendence)

            Handler().postDelayed(Runnable {

                if (!Pref.isAddAttendence)
                    checkToShowAddAttendanceAlert()
                else {
                    isAttendanceAlertPresent = false
                    if (getFragment() != null && getFragment() is DashboardFragment)
                        (getFragment() as DashboardFragment).updateBottomList()
                }

            }, 500)

        } else if (getFragment() != null && getFragment() is ReimbursementFragment) {
            super.onBackPressed()
            if (getFragment() != null && getFragment() is ReimbursementListFragment)
                (getFragment() as ReimbursementListFragment).callApi()
        } else if (getFragment() != null && getFragment() is EditReimbursementFragment) {
            super.onBackPressed()
            if (getFragment() != null && getFragment() is ReimbursementListFragment)
                (getFragment() as ReimbursementListFragment).callApi()
        } else if (getFragment() != null && getFragment() is MemberListFragment) {
            super.onBackPressed()
            if (getFragment() != null) {
                if (getFragment() !is MemberListFragment)
                    teamHierarchy.clear()
                else
                    (getFragment() as MemberListFragment).updateTeamHierarchy()

                if (getFragment() is DashboardFragment)
                    (getFragment() as DashboardFragment).updateItem()
            }
        }
        else if (getFragment() != null && getFragment() is ShopDamageProductSubmitFrag) {
            super.onBackPressed()
            if (getFragment() != null && getFragment() is ShopDamageProductListFrag)
                (getFragment() as ShopDamageProductListFrag).updatePage()
        }
        else if (getFragment() != null && getFragment() is SurveyFrag) {
            super.onBackPressed()
            if (getFragment() != null && getFragment() is SurveyViewFrag)
                (getFragment() as SurveyViewFrag).updatePage()
        }

        else if (getFragment() != null && (getFragment() is MemberAllShopListFragment || getFragment() is MemberShopListFragment || getFragment() is AreaListFragment)) {

            if (getFragment() is MemberAllShopListFragment) {
                if ((getFragment() as MemberAllShopListFragment).shopIdList.isNotEmpty()) {
                    (getFragment() as MemberAllShopListFragment).updateListOnBackPress()
                    return
                }
            }

            super.onBackPressed()
            if (getFragment() != null) {
                if (getFragment() is MemberListFragment)
                    (getFragment() as MemberListFragment).updateMemberTeamHierarchy()
            }
        } else if (getFragment() != null && (getFragment() is AddPJPFragment || getFragment() is EditPJPFragment)) {
            super.onBackPressed()

            if (getFragment() != null && getFragment() is MemberPJPListFragment && isAddedEdited) {
                isAddedEdited = false
                (getFragment() as MemberPJPListFragment).updateList()
            }

        } else if (getFragment() != null && getFragment() is LogoutSyncFragment) {
            if (!isForceLogout) {
                super.onBackPressed()
                if (getFragment() != null && getFragment() is ChatBotFragment)
                    (getFragment() as ChatBotFragment).update()
            }
        } else if (getFragment() != null && getFragment() is AddPJPLocationFragment) {

            val lat = (getFragment() as AddPJPLocationFragment).selectedLat
            val lng = (getFragment() as AddPJPLocationFragment).selectedLong
            val address = (getFragment() as AddPJPLocationFragment).selectedAddress
            val radius = (getFragment() as AddPJPLocationFragment).radius

            super.onBackPressed()

            if (getFragment() != null && getFragment() is AddPJPFragment)
                (getFragment() as AddPJPFragment).updateAddress(lat, lng, address, radius)
            else if (getFragment() != null && getFragment() is EditPJPFragment)
                (getFragment() as EditPJPFragment).updateAddress(lat, lng, address, radius)
        } else if (getFragment() != null && (getFragment() is AddTimeSheetFragment || getFragment() is EditTimeSheetFragment)) {
            super.onBackPressed()

            if (getFragment() != null && getFragment() is TimeSheetListFragment && isTimesheetAddedEdited) {
                isTimesheetAddedEdited = false
                (getFragment() as TimeSheetListFragment).updateList()
            }

        }
        /*else if (getFragment() != null && getFragment() is DailyPlanListFragment) {
            super.onBackPressed()
            if (getFragment() != null && getFragment() is AddAttendanceFragment)
                super.onBackPressed()

        }*/
        else if (getFragment() != null && getFragment() is OfflineMemberListFragment) {
            super.onBackPressed()
            if (getFragment() != null) {
                if (getFragment() !is OfflineMemberListFragment)
                    teamHierarchy.clear()
                else
                    (getFragment() as OfflineMemberListFragment).updateTeamHierarchy()

                if (getFragment() is DashboardFragment)
                    (getFragment() as DashboardFragment).updateItem()
            }
        } else if (getFragment() != null && (getFragment() is OfflineAllShopListFragment || getFragment() is OfflineShopListFragment || getFragment() is OfflineAreaListFragment)) {

            if (getFragment() is OfflineAllShopListFragment) {
                (getFragment() as OfflineAllShopListFragment).isAddressUpdated = false
                if ((getFragment() as OfflineAllShopListFragment).shopIdList.isNotEmpty()) {
                    (getFragment() as OfflineAllShopListFragment).updateListOnBackPress()
                    return
                }
            }

            super.onBackPressed()
            if (getFragment() != null) {
                if (getFragment() is OfflineMemberListFragment)
                    (getFragment() as OfflineMemberListFragment).updateMemberTeamHierarchy()
            }
        } else if (getFragment() != null && (getFragment() is AddDynamicFragment || getFragment() is EditDynamicFragment)) {

            super.onBackPressed()
            if (isDynamicFormUpdated) {
                isDynamicFormUpdated = false
                if (getFragment() != null && getFragment() is DynamicListFragment)
                    (getFragment() as DynamicListFragment).updateList()
            }
        }
        else if (getFragment() != null && (getFragment() is AddGroupFragment || getFragment() is AddNewMsgFragment ||
                        getFragment() is ChatListFragment)) {

            if (getFragment() is AddNewMsgFragment) {
                super.onBackPressed()

                if (newUserModel != null) {

                    newUserModel?.also {
                        if (it.name.contains("(")) {
                            val name = it.name.substring(0, it.name.indexOf("("))
                            userName = name
                        } else
                            userName = it.name

                        loadFragment(FragType.ChatListFragment, true, it)
                    }
                    newUserModel = null
                }
                else {
                    if (getFragment() != null && getFragment() is ChatUserListFragment)
                        (getFragment() as ChatUserListFragment).updateList()
                }
            }
            else {
                super.onBackPressed()
                if (getFragment() != null && getFragment() is ChatUserListFragment)
                    (getFragment() as ChatUserListFragment).updateList()
            }
        }
        else if (getFragment() != null && getFragment() is ScanImageFragment) {

            val picTexts = (getFragment() as ScanImageFragment).stringArrays
            val isCopy = (getFragment() as ScanImageFragment).isCopy

            if (isCopy)
                AppUtils.saveSharedPreferencesImageText(this, picTexts)

            super.onBackPressed()

            if (getFragment() != null && getFragment() is AddShopFragment) {
                (getFragment() as AddShopFragment).processImage(/*File(resultUri.path!!)*/picTexts, isCopy)
            }
        }
        else if (getFragment() != null && getFragment() is CodeScannerFragment) {
            super.onBackPressed()

            if (!isCodeScaneed)
                return

            isCodeScaneed = false
            if (!isForRevisit) {
                if (!TextUtils.isEmpty(qrCodeText))
                    CodeScannerTextDialog.newInstance(qrCodeText).show(supportFragmentManager, "")
                else
                    showSnackMessage("Scan QR Code has been cancelled.")
            }
            else {
                if (!Pref.isAddAttendence) {
                    checkToShowAddAttendanceAlert()
                    return
                }

                if (Pref.isOnLeave.equals("true", ignoreCase = true)) {
                    showSnackMessage(getString(R.string.error_you_are_in_leave))
                    return
                }

                try {
                    val shopId = qrCodeText.substring(0, qrCodeText.indexOf("\n"))

                    val userId = shopId.substring(0, shopId.indexOf("_"))
                    if (userId != Pref.user_id) {
                        //showSnackMessage("Scanned QR is not your ${Pref.shopText}. Revisit not possible. Thanks")
                        CommonDialogSingleBtn.getInstance(AppUtils.hiFirstNameText() + "!", "Scanned QR is not your ${Pref.shopText}. Revisit not possible. Thanks",
                                "Ok", object : OnDialogClickListener {
                            override fun onOkClick() {
                            }
                        }).show(supportFragmentManager, "")
                        return
                    }

                    val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(shopId)

                    val distance = LocationWizard.getDistance(shop?.shopLat!!, shop.shopLong!!, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())

                    if (distance * 1000 > Pref.gpsAccuracy.toDouble()) {
                        //showSnackMessage("Hi, you are not at the nearby location. Be there and try to scan for Revisit.")
                        CommonDialogSingleBtn.getInstance(AppUtils.hiFirstNameText() + "!", "Hi, you are not at the nearby location. Please be there & scan QR to revisit for today.",
                                "Ok", object : OnDialogClickListener {
                            override fun onOkClick() {
                            }
                        }).show(supportFragmentManager, "")
                    } else {
                        mAddShopDBModelEntity = shop
                        terminateOtherShopVisit(1, shop, shop.shopName, shopId, null, null)

                        if (!Pref.isShowShopVisitReason) {
                            shopName = mStoreName
                            contactNumber = shop.ownerContactNumber

                            startOwnShopRevisit(shop, shop.shopName, shopId)

                        }
                    }
                }
                catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    showSnackMessage("Invalid QR Code")
                }
            }
        }
        else if (getFragment() != null && getFragment() is MicroLearningListFragment) {
            if ((getFragment() as MicroLearningListFragment).isFilterSelected)
                (getFragment() as MicroLearningListFragment).showAllList()
            else
                super.onBackPressed()
        }
        else if (getFragment() != null && getFragment() is JobsCustomerFragment) {
            if((getFragment() as JobsCustomerFragment).isUpdateStatusClicked) {
                (getFragment() as JobsCustomerFragment).isUpdateStatusClicked = false
                val mCustomerdata = (getFragment() as JobsCustomerFragment).customerdata
                super.onBackPressed()
                loadFragment(FragType.JobsCustomerFragment, true, mCustomerdata!!)
            }
            else {
                super.onBackPressed()

                if (getFragment() != null && getFragment() is MyJobsFragment) {
                    if (isCalledJobApi) {
                        isCalledJobApi = false
                        (getFragment() as MyJobsFragment).getCustomerListApi()
                    }
                }
            }
        }
        else if(getFragment() != null && getFragment() is ViewAllOrderListFragment && (ShopDetailFragment.isOrderEntryPressed || AddShopFragment.isOrderEntryPressed) && AppUtils.getSharedPreferenceslogOrderStatusRequired(this)){

            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_yes_no)
            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView

            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
                if (true) {
                    val dialog = Dialog(mContext)
                    //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.setContentView(R.layout.dialog_cancel_order_status)

                    val user_name = dialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                    val order_status = dialog.findViewById(R.id.tv_cancel_order_status) as AppCustomTextView
                    val cancel_remarks = dialog.findViewById(R.id.et_cancel_order_remarks) as AppCustomEditText
                    val submitRemarks = dialog.findViewById(R.id.tv_cancel_order_submit_remarks) as AppCustomTextView

                    order_status.text = "Failure"
                    user_name.text = "Hi " + Pref.user_name + "!"

                    submitRemarks.setOnClickListener(View.OnClickListener { view ->
                        if (!TextUtils.isEmpty(cancel_remarks.text.toString().trim())) {
                            Toast.makeText(mContext, cancel_remarks.text.toString(), Toast.LENGTH_SHORT).show()
                            val obj = OrderStatusRemarksModelEntity()
                            //obj.shop_id= mShopId

                            obj.shop_id = ViewAllOrderListFragment.mSShopID_Str.toString()
                            obj.user_id = Pref.user_id
                            obj.order_status = order_status.text.toString()
                            obj.order_remarks = cancel_remarks!!.text!!.toString()
                            obj.visited_date_time = AppUtils.getCurrentDateTime()
                            obj.visited_date = AppUtils.getCurrentDateForShopActi()
                            obj.isUploaded = false


                            var shopAll = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                            if (shopAll.size == 1) {
                                obj.shop_revisit_uniqKey = shopAll.get(0).shop_revisit_uniqKey
                            } else if (shopAll.size != 0) {
                                obj.shop_revisit_uniqKey = shopAll.get(shopAll.size - 1).shop_revisit_uniqKey
                            }

                            if (shopAll.size != 0)
                                AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                            dialog.dismiss()

                            if (ShopDetailFragment.isOrderEntryPressed) {
                                ShopDetailFragment.isOrderEntryPressed = false
                            }
                            if (AddShopFragment.isOrderEntryPressed) {
                                AddShopFragment.isOrderEntryPressed = false
                            }


                            super.onBackPressed()
                            when {
                                getFragment() is ShopDetailFragment -> (getFragment() as ShopDetailFragment).updateItem()
                            }
                        } else {
                            submitRemarks.setError("Enter Remarks")
                            submitRemarks.requestFocus()
                        }

                    })
                    dialog.show()
                }
            })
            dialogNo.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()

        }
        /*Date 14-09-2021*/
        else if (getFragment() != null && getFragment() is NewOrderScrOrderDetailsFragment) {
            loadFragment(FragType.DashboardFragment, false, DashboardType.Home)
        }
        else if (getFragment() != null && getFragment() is NewOrderScrActiFragment && CustomStatic.NewOrderTotalCartItem>0) {
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_yes_no)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
            dialogHeader.text = "Click Yes to clear the cart and back to the list to start again."
            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
                CustomStatic.NewOrderTotalCartItem = 0
                super.onBackPressed();
                //(mContext as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, false, NewOrderScrOrderDetailsFragment.shop_id)
            })
            dialogNo.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()

        }
        else if(getFragment() != null && getFragment() is AddQuotFormFragment){
            super.onBackPressed()
            if (getFragment() != null && getFragment() is ViewAllQuotListFragment){
                (getFragment() as ViewAllQuotListFragment).updateView()
            }

        }else if(getFragment() != null && getFragment() is ViewDetailsQuotFragment){
            super.onBackPressed()
            CustomStatic.IsNewQuotEdit=false
        }else if(getFragment() != null && getFragment() is ViewLeadFrag){
            super.onBackPressed()
            if (getFragment() != null && getFragment() is LeadFrag && CustomStatic.IsViewLeadAddUpdate){
                (getFragment() as LeadFrag).updateView()
            }
        }else if(getFragment() != null && getFragment() is CollectionPendingDtlsFrag){
            super.onBackPressed()
            if (getFragment() != null && getFragment() is CollectionNotiViewPagerFrag1)
                (getFragment() as CollectionNotiViewPagerFrag1).updateView()
            if (getFragment() != null && getFragment() is CollectionNotiViewPagerFrag)
                (getFragment() as CollectionNotiViewPagerFrag).updateView()
            if (getFragment() != null && getFragment() is CollectionNotiViewPagerFrag2)
                (getFragment() as CollectionNotiViewPagerFrag2).updateView()
        }else if(getFragment() != null && getFragment() is MapViewForTeamFrag){
            if (getFragment() != null && getFragment() is MapViewForTeamFrag)
                MapViewForTeamFrag.timer!!.cancel()
            super.onBackPressed()
        }else if(getFragment() != null && getFragment() is NewOdrScrListFragment){
            super.onBackPressed()
            if (getFragment() != null && getFragment() is DashboardFragment){
                (getFragment() as DashboardFragment).updateOrdAmtForNewOrd()
            }
        }
        else {
            super.onBackPressed()

            when {
                getFragment() is MemberListFragment -> (getFragment() as MemberListFragment).updateItem()
                getFragment() is LeaveListFragment -> (getFragment() as LeaveListFragment).updateItem()
                getFragment() is ViewPPDDListFragment -> (getFragment() as ViewPPDDListFragment).updateItem()
                getFragment() is ShopDetailFragment -> (getFragment() as ShopDetailFragment).updateItem()
                getFragment() is OrderListFragment -> (getFragment() as OrderListFragment).updateItem()
                getFragment() is DashboardFragment -> (getFragment() as DashboardFragment).updateItem()
                getFragment() is BillingListFragment -> (getFragment() as BillingListFragment).updateItem()
                getFragment() is NewDateWiseOrderListFragment -> (getFragment() as NewDateWiseOrderListFragment).updateItem()
                getFragment() is NewOrderListFragment -> (getFragment() as NewOrderListFragment).updateItem()
                getFragment() is ReimbursementListFragment -> (getFragment() as ReimbursementListFragment).updateFloatingButton()
                getFragment() is MemberShopListFragment -> (getFragment() as MemberShopListFragment).updateAdapter()
                getFragment() is QuotationListFragment -> (getFragment() as QuotationListFragment).updateList()
                getFragment() is OfflineShopListFragment -> (getFragment() as OfflineShopListFragment).updateAdapter()
                getFragment() is TaskListFragment -> (getFragment() as TaskListFragment).updateList()
                getFragment() is ChemistActivityListFragment -> (getFragment() as ChemistActivityListFragment).updateItem()
                getFragment() is DoctorActivityListFragment -> (getFragment() as DoctorActivityListFragment).updateItem()
                getFragment() is DateWiseActivityListFragment -> (getFragment() as DateWiseActivityListFragment).updateList()
                getFragment() is ActivityDetailsListFragment -> (getFragment() as ActivityDetailsListFragment).updateList()
                getFragment() is ChatBotFragment -> (getFragment() as ChatBotFragment).update()
                getFragment() is BeatListFragment -> (getFragment() as BeatListFragment).update()

                getFragment() is UpdateShopStockFragment -> (getFragment() as UpdateShopStockFragment).update()
                getFragment() is CompetetorStockFragment -> (getFragment() as CompetetorStockFragment).update()

                getFragment() is NewOrderScrActiFragment -> (getFragment() as NewOrderScrActiFragment).updateCartQty()

                        getFragment() is MicroLearningListFragment -> {
                    val intent = Intent(this, FileOpeningTimeIntentService::class.java)
                    intent.also {
                        it.putExtra("id", (getFragment() as MicroLearningListFragment).selectedFile?.id)
                        it.putExtra("start_time", (getFragment() as MicroLearningListFragment).openingDateTime)
                        startService(it)
                    }
                }
                getFragment() is JobsCustomerFragment -> {
                    if (isSubmit) {
                        isSubmit = false
                        isCalledJobApi = true
                        (getFragment() as JobsCustomerFragment).getStatusApi()
                    }
                }
            }
        }
//        searchView.closeSearch()
    }

    private fun showAlert() {

        if (tv_cart_count.visibility == View.GONE) {
            isShowAlert = false
            AppUtils.isAllSelect = false
            onBackPressed()
            return
        }

        CommonDialog.getInstance(AppUtils.hiFirstNameText() + "!", "Click Ok to clear the cart and back to the list to start again.", getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                isShowAlert = false
                AppUtils.isAllSelect = false
                onBackPressed()
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private var locationInfoModel: locationInfoModel? = null
    fun getLocationInfoModel(mlocationInfoModel: locationInfoModel) {
        locationInfoModel = mlocationInfoModel
    }

    private fun initBackStackActionSet() {
        supportFragmentManager.addOnBackStackChangedListener {
            getFragInstance(getCurrentFragType(), "", false)
        }
    }

    private fun performLogout() {
        CommonDialog.getInstance(AppUtils.hiFirstNameText() + "!", getString(R.string.confirm_logout), getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {

            }

            override fun onRightClick(editableData: String) {
                if (AppUtils.isOnline(this@DashboardActivity)) {

                    if (Pref.isShowLogoutReason && !TextUtils.isEmpty(Pref.approvedOutTime)) {
                        val currentTimeInLong = AppUtils.convertTimeWithMeredianToLong(AppUtils.getCurrentTimeWithMeredian())
                        val approvedOutTimeInLong = AppUtils.convertTimeWithMeredianToLong(Pref.approvedOutTime)

                        if (currentTimeInLong < approvedOutTimeInLong)
                            showLogoutLocReasonDialog()
                        else
                            calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                    } else
                        calllogoutApi(Pref.user_id!!, Pref.session_token!!)


                    /*val list = AppDatabase.getDBInstance()!!.gpsStatusDao().getDataSyncStateWise(false)

                    if (list != null && list.isNotEmpty()) {
                        i = 0
                        callUpdateGpsStatusApi(list)
                    } else {
                        calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                    }*/

                    // loadFragment(FragType.LogoutSyncFragment, false, "")

                } else
                    showSnackMessage("Good internet must required to logout, please switch on the internet and proceed. Thanks.")

                /*AppUtils.isLoginLoaded = false
                val serviceLauncher = Intent(this@DashboardActivity, LocationFuzedService::class.java)
                stopService(serviceLauncher)

                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()*/
            }

        }).show(supportFragmentManager, "")
    }

    private fun showLogoutLocReasonDialog() {
        reasonDialog = null
        val body = "You applicable out time is: ${Pref.approvedOutTime}!. You are doing early logout. Please write below the reason."
        reasonDialog = ReasonDialog.getInstance(AppUtils.hiFirstNameText() + "!", body, reason) {
            if (!AppUtils.isOnline(this))
                Toaster.msgShort(this, getString(R.string.no_internet))
            else {
                reasonDialog?.dismiss()
                submitLogoutReason(it)
            }
        }
        reasonDialog?.show(supportFragmentManager, "")
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
                                calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                            } else {
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

    private fun callUpdateGpsStatusApi(list: List<GpsStatusEntity>) {

        val updateGps = UpdateGpsInputParamsModel()
        updateGps.date = list[i].date
        updateGps.gps_id = list[i].gps_id
        updateGps.gps_off_time = list[i].gps_off_time
        updateGps.gps_on_time = list[i].gps_on_time
        updateGps.user_id = Pref.user_id
        updateGps.session_token = Pref.session_token
        updateGps.duration = AppUtils.getTimeInHourMinuteFormat(list[i].duration?.toLong()!!)

        progress_wheel.spin()
        val repository = UpdateGpsStatusRepoProvider.updateGpsStatusRepository()
        BaseActivity.compositeDisposable.add(
                repository.updateGpsStatus(updateGps)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val gpsStatusResponse = result as BaseResponse
                            XLog.d("GPS SYNC : " + "RESPONSE : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name
                                    + ",MESSAGE : " + gpsStatusResponse.message)
                            if (gpsStatusResponse.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.gpsStatusDao().updateIsUploadedAccordingToId(true, list[i].id)
                            }

                            i++
                            if (i < list.size) {
                                callUpdateGpsStatusApi(list)
                            } else {
                                i = 0
                                progress_wheel.stopSpinning()
                                calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                            }

                        }, { error ->
                            //
                            XLog.d("GPS SYNC : " + "RESPONSE ERROR: " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            i++
                            if (i < list.size) {
                                callUpdateGpsStatusApi(list)
                            } else {
                                i = 0
                                progress_wheel.stopSpinning()
                                calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                            }
                        })
        )
    }

    fun syncShopList() {

        Pref.logout_time = AppUtils.getCurrentTimeWithMeredian()

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        super.clearData()
    }

    fun openLocationWithTrack() {
        startActivity(Intent(this@DashboardActivity, MapActivity::class.java))
        overridePendingTransition(0, 0)
    }

    fun openLocationMap(customerData: CustomerDataModel, isCurrentLocShow: Boolean) {
        val mapIntent = Intent(this@DashboardActivity, MapActivityWithoutPath::class.java)
        mapIntent.also {
            it.putExtra("latitude", customerData.latitude)
            it.putExtra("longitude", customerData.longitude)
            it.putExtra("shopname", customerData.name)
            it.putExtra("address", customerData.address)
            it.putExtra("isCurrentLocShow", isCurrentLocShow)
            it.putExtra("isOrderLoc", false)
            it.putExtra("orderLat", "0.0")
            it.putExtra("orderLong", "0.0")
            it.putExtra("orderNo", "")
            it.putExtra("orderAddress", "")
            startActivity(it)
        }
        overridePendingTransition(0, 0)

    }

    fun openLocationMap(mAddShopDBModelEntity: AddShopDBModelEntity, isCurrentLocShow: Boolean) {
        val mapIntent: Intent = Intent(this@DashboardActivity, MapActivityWithoutPath::class.java)
        mapIntent.putExtra("latitude", mAddShopDBModelEntity.shopLat.toString())
        mapIntent.putExtra("longitude", mAddShopDBModelEntity.shopLong.toString())
        mapIntent.putExtra("shopname", mAddShopDBModelEntity.shopName)
        mapIntent.putExtra("address", mAddShopDBModelEntity.address)
        mapIntent.putExtra("isCurrentLocShow", isCurrentLocShow)
        mapIntent.putExtra("isOrderLoc", false)
        mapIntent.putExtra("orderLat", "0.0")
        mapIntent.putExtra("orderLong", "0.0")
        mapIntent.putExtra("orderNo", "")
        mapIntent.putExtra("orderAddress", "")
        startActivity(mapIntent)
        overridePendingTransition(0, 0)

    }
        /*20-12-2021*/
    fun openLocationMap(returnObj: ReturnDetailsEntity, isCurrentLocShow: Boolean) {
        val mapIntent = Intent(this@DashboardActivity, MapActivityWithoutPath::class.java)
        val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(returnObj.shop_id)

        mapIntent.putExtra("latitude", shop?.shopLat.toString())
        mapIntent.putExtra("longitude", shop?.shopLong.toString())
        mapIntent.putExtra("shopname", shop?.shopName)
        mapIntent.putExtra("address", shop?.address)
        mapIntent.putExtra("orderLat", returnObj.return_lat)
        mapIntent.putExtra("orderLong", returnObj.return_long)
        mapIntent.putExtra("orderNo", returnObj.return_id)
        mapIntent.putExtra("orderAddress", LocationWizard.getLocationName(this, returnObj.return_lat?.toDouble()!!, returnObj.return_long?.toDouble()!!))
        mapIntent.putExtra("isCurrentLocShow", isCurrentLocShow)
        mapIntent.putExtra("isOrderLoc", true)
        startActivity(mapIntent)
        overridePendingTransition(0, 0)
    }


    fun openLocationMap(order: OrderDetailsListEntity, isCurrentLocShow: Boolean) {
        val mapIntent = Intent(this@DashboardActivity, MapActivityWithoutPath::class.java)
        val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(order.shop_id)

        mapIntent.putExtra("latitude", shop?.shopLat.toString())
        mapIntent.putExtra("longitude", shop?.shopLong.toString())
        mapIntent.putExtra("shopname", shop?.shopName)
        mapIntent.putExtra("address", shop?.address)
        mapIntent.putExtra("orderLat", order.order_lat)
        mapIntent.putExtra("orderLong", order.order_long)
        mapIntent.putExtra("orderNo", order.order_id)
        mapIntent.putExtra("orderAddress", LocationWizard.getLocationName(this, order.order_lat?.toDouble()!!, order.order_long?.toDouble()!!))
        mapIntent.putExtra("isCurrentLocShow", isCurrentLocShow)
        mapIntent.putExtra("isOrderLoc", true)
        startActivity(mapIntent)
        overridePendingTransition(0, 0)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == PermissionHelper.REQUEST_CODE_CAMERA) {
                //XLog.d("DashboardActivity : " + " , " + " Camera Image FilePath :" + FTStorageUtils.IMG_URI)
                if (AppUtils.isRevisit!!) {

                    /*CropImage.activity(FTStorageUtils.IMG_URI)
                            .setAspectRatio(40, 21)
                            .start(this)*/

                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {

                        XLog.e("===========RevisitShop Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }


                        //revisitShop()

                        /*val file = File(filePath)
                        var newFile: File? = null

                        progress_wheel.spin()
                        doAsync {

                            val processImage = ProcessImageUtils_v1(this@DashboardActivity, file, 20)
                            newFile = processImage.ProcessImage()

                            uiThread {
                                progress_wheel.stopSpinning()
                                if (newFile != null) {
                                    XLog.e("=========Image Capture from new technique==========")
                                    filePath = newFile?.absolutePath!!
                                    addShopVisitPic(newFile!!.length(), imageUpDateTime)
                                }
                                else {
                                    // Image compression
                                    val fileSize = AppUtils.getCompressImage(filePath)
                                    addShopVisitPic(fileSize, imageUpDateTime)
                                }

                                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(mShopId)
                                if (shopDetail.is_otp_verified.equals("false", ignoreCase = true)) {
                                    if (AppUtils.isOnline(this@DashboardActivity))
                                        showShopVerificationDialog()
                                    else
                                        loadFragment(FragType.ShopDetailFragment, true, mShopId)
                                } else
                                    loadFragment(FragType.ShopDetailFragment, true, mShopId)
                            }
                        }*/

                        /*OTPVerificationDialog.getInstance(object : OTPVerificationDialog.OnOTPButtonClickListener {
                            override fun onOkButtonClick() {
                            }
                        }).show((mContext as DashboardActivity).supportFragmentManager, "OTPVerificationDialog")*/
                    }
                } else if (Pref.isSefieAlarmed) {
                    getCameraImage(data)
                    val fileSize = AppUtils.getCompressImage(filePath)

                    val fileSizeInKB = fileSize / 1024
                    Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

                    uploadSelfie(File(filePath))

                }
                else if(isVisitCardScan) {
                    getCameraImage(data)
                    /*val fileSize = AppUtils.getCompressImage(filePath)

                    val fileSizeInKB = fileSize / 1024
                    Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

                    isVisitCardScan = false
                    if (getFragment() != null && getFragment() is AddShopFragment)
                        (getFragment() as AddShopFragment).processImage(File(filePath))*/

                    if (!TextUtils.isEmpty(filePath)) {

                        XLog.e("===========Visiting Card Scan Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.AddShopFragment /*&& FTStorageUtils.IMG_URI != null*/) {
                    /*CropImage.activity(FTStorageUtils.IMG_URI)
                            .setAspectRatio(40, 21)
                            .start(this)*/


                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Add Shop Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")
                        /*14-12-2021*/
                        if(Pref.IsnewleadtypeforRuby){
                            try {
                                CropImage.activity(contentURI)
                                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                                        .setMinCropWindowSize(500, 500)
                                        .setAspectRatio(1, 1)
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setOutputCompressQuality(100)
                                        .start(this)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                XLog.e("Error: " + e.localizedMessage)
                            }
                        }
                        else{
                            try {
                                CropImage.activity(contentURI)
                                        .setAspectRatio(40, 21)
                                        .start(this)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                XLog.e("Error: " + e.localizedMessage)
                            }
                        }
                    }

                }else if (getCurrentFragType() == FragType.ProtoRegistrationFragment) {
                    // request for camera image
                    getCameraImage(data)
                    if (!TextUtils.isEmpty(filePath)) {
                        //val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, filePath)
                        //XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")
                        //val fileSize = AppUtils.getCompressBillingImage(contentURI.toString(), this)
                        //updatePhotoRegAadhaarCroppedImg(fileSize, contentURI)

                        (getFragment() as ProtoRegistrationFragment).setImage(filePath)

                    }

                }
                else if (getCurrentFragType() == FragType.MyProfileFragment /*&& FTStorageUtils.IMG_URI != null*/) {
                    /*AppUtils.getCompressContentImage(FTStorageUtils.IMG_URI, this)
                    (getFragment() as MyProfileFragment).setImage(FTStorageUtils.IMG_URI)*/

                    getCameraImage(data)
                    /*val fileSize = AppUtils.getCompressImage(filePath)
                    editProfilePic(fileSize)*/

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Profile Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }


                }
                else if (getCurrentFragType() == FragType.ReimbursementFragment) {
                    /*AppUtils.getCompressContentImage(FTStorageUtils.IMG_URI, this)
                    (getFragment() as MyProfileFragment).setImage(FTStorageUtils.IMG_URI)*/

                    getCameraImage(data)

                    /* val file = File(filePath)
                     var newFile: File? = null

                     progress_wheel.spin()
                     doAsync {

                         val processImage = ProcessImageUtils_v1(this@DashboardActivity, file, 50)
                         newFile = processImage.ProcessImage()

                         uiThread {
                             //progress_wheel.stopSpinning()
                             if (newFile != null) {
                                 XLog.e("=========Image Capture from new technique==========")
                                 filePath = newFile?.absolutePath!!
                                 reimbursementPic(newFile!!.length())
                             } else {
                                 // Image compression
                                 val fileSize = AppUtils.getCompressImage(filePath)
                                 reimbursementPic(fileSize)
                             }
                         }
                     }*/

                    (getFragment() as ReimbursementFragment).setImage(filePath)

                }
                else if (getCurrentFragType() == FragType.EditReimbursementFragment) {
                    /*AppUtils.getCompressContentImage(FTStorageUtils.IMG_URI, this)
                    (getFragment() as MyProfileFragment).setImage(FTStorageUtils.IMG_URI)*/

                    getCameraImage(data)

                    /*val file = File(filePath)
                    var newFile: File? = null

                    progress_wheel.spin()
                    doAsync {

                        val processImage = ProcessImageUtils_v1(this@DashboardActivity, file, 50)
                        newFile = processImage.ProcessImage()

                        uiThread {
                            //progress_wheel.stopSpinning()
                            if (newFile != null) {
                                XLog.e("=========Image Capture from new technique==========")
                                filePath = newFile?.absolutePath!!
                                reimbursementEditPic(newFile!!.length())
                            } else {
                                // Image compression
                                val fileSize = AppUtils.getCompressImage(filePath)
                                reimbursementEditPic(fileSize)
                            }
                        }
                    }*/

                    (getFragment() as EditReimbursementFragment).setImage(filePath)

                }
                else if (getCurrentFragType() == FragType.MarketingPagerFragment && FTStorageUtils.IMG_URI != null) {
                    AppUtils.getCompressImage(FTStorageUtils.IMG_URI.toString())
                    (getFragment() as MarketingPagerFragment).setImage(FTStorageUtils.IMG_URI)
                }
                else if (getCurrentFragType() == FragType.ViewAllTAListFragment && FTStorageUtils.IMG_URI != null) {

                    AppUtils.getCompressImage(FTStorageUtils.IMG_URI.toString())
                    (getFragment() as ViewAllTAListFragment).getCaptureImage(FTStorageUtils.IMG_URI)

                }
                else if (getCurrentFragType() == FragType.AddBillingFragment) {

                    getCameraImage(data)

                    /*val file = File(filePath)
                    var newFile: File? = null

                    progress_wheel.spin()
                    doAsync {

                        val processImage = ProcessImageUtils_v1(this@DashboardActivity, file, 50)
                        newFile = processImage.ProcessImage()

                        uiThread {
                            progress_wheel.stopSpinning()
                            if (newFile != null) {
                                XLog.e("=========Image Capture from new technique==========")
                                filePath = newFile?.absolutePath!!
                                addBillingPic(newFile!!.length())
                            } else {
                                // Image compression
                                val fileSize = AppUtils.getCompressImage(filePath)
                                addBillingPic(fileSize)
                            }
                        }
                    }*/

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Add Billing Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }


                }
                else if (getCurrentFragType() == FragType.NearByShopsListFragment || getCurrentFragType() == FragType.NewDateWiseOrderListFragment ||
                        getCurrentFragType() == FragType.NewOrderListFragment || getCurrentFragType() == FragType.ShopBillingListFragment ||
                        getCurrentFragType() == FragType.ViewAllOrderListFragment) {

                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Add Collection Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }


                }
                else if (getCurrentFragType() == FragType.AddDynamicFragment) {

                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Add Dynamic form Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.EditDynamicFragment) {

                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Edit Dynamic form Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.AddActivityFragment) {

                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Add Activity form Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.EditActivityFragment) {

                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Edit Activity form Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.ShopDetailFragment /*&& FTStorageUtils.IMG_URI != null*/) {
                    /*CropImage.activity(FTStorageUtils.IMG_URI)
                            .setAspectRatio(40, 21)
                            .start(this)*/


                    /*CropImage.activity(FTStorageUtils.IMG_URI)
                            .setAspectRatio(40, 21)
                            .start(this)*/


                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Edit Shop Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }


                }
                else if (getCurrentFragType() == FragType.AddAttendanceFragment /*&& FTStorageUtils.IMG_URI != null*/) {
                    /*CropImage.activity(FTStorageUtils.IMG_URI)
                            .setAspectRatio(40, 21)
                            .start(this)*/


                    /*getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Add Attendance Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

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
                    addAttendanceImg(fileSize, true)

                }
                else if (getCurrentFragType() == FragType.DailyPlanListFragment /*&& FTStorageUtils.IMG_URI != null*/) {
                    /*CropImage.activity(FTStorageUtils.IMG_URI)
                            .setAspectRatio(40, 21)
                            .start(this)*/


                    /*getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Add Attendance Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

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
                    addAttendanceImg(fileSize, false)

                }
                else if (getCurrentFragType() == FragType.AddTimeSheetFragment) {
                    getCameraImage(data)
                    (getFragment() as AddTimeSheetFragment).setImage(filePath)

                }
                else if (getCurrentFragType() == FragType.EditTimeSheetFragment) {
                    getCameraImage(data)
                    (getFragment() as EditTimeSheetFragment).setImage(filePath)

                }
                else if (getCurrentFragType() == FragType.DocumentListFragment) {
                    getCameraImage(data)
                    (getFragment() as DocumentListFragment).setImage(filePath)
                }
                else if (getCurrentFragType() == FragType.WorkInProgressFragment) {
                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Work in Progress Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.WorkOnHoldFragment) {
                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Work in Hold Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.WorkCompletedFragment) {
                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Work Completed Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.WorkCancelledFragment) {
                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Work Cancelled Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.UpdateReviewFragment) {
                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Update Review Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setAspectRatio(40, 21)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                } else if (getCurrentFragType() == FragType.ShopDamageProductSubmitFrag) {

                    getCameraImage(data)
                    val fileSize = AppUtils.getCompressImage(filePath)
                    val fileSizeInKB = fileSize / 1024
                    val file = File(filePath)
                    (getFragment() as ShopDamageProductSubmitFrag).setImage(file)




                    /*getCameraImage(data)
                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Update Review Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setMinCropWindowSize(500, 500)
                                .setAspectRatio(1, 1)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setOutputCompressQuality(100)
                                .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }*/
                }else if(getCurrentFragType() == FragType.SurveyFrag){
                    getCameraImage(data)
                    val fileSize = AppUtils.getCompressImage(filePath)
                    val fileSizeInKB = fileSize / 1024
                    val file = File(filePath)
                    (getFragment() as SurveyFrag).setImage(file)
                }
                else if(getCurrentFragType() == FragType.RegisTerFaceFragment){
                    getCameraImage(data)

                    if (!TextUtils.isEmpty(filePath)) {
                        XLog.e("===========Update Review Image (DashboardActivity)===========")
                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")

                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")

                        try {
                            CropImage.activity(contentURI)
                                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                                    .setMinCropWindowSize(500, 500)
                                    .setAspectRatio(1, 1)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setOutputCompressQuality(100)
                                    .start(this)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            XLog.e("Error: " + e.localizedMessage)
                        }
                    }
                }
                else if (getCurrentFragType() == FragType.DashboardFragment) {
                    getCameraImage(data)
                    if (!TextUtils.isEmpty(filePath)) {
                            //30-08-2021
//                        XLog.e("===========Update Review Image (DashboardActivity)===========")
//                        XLog.e("DashboardActivity :  ,  Camera Image FilePath : $filePath")
//
//                        val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, File(Uri.parse(filePath).path).absolutePath)

                        (getFragment() as DashboardFragment).setImage(filePath)



//                        XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")
//
//                        try {
//                            CropImage.activity(contentURI)
//                                    .setAspectRatio(40, 21)
//                                    .start(this)
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            XLog.e("Error: " + e.localizedMessage)
//                        }
                    }
                }
            }



            else if (ImagePickerManager.REQUEST_GET_GALLERY_PHOTO == requestCode && null != data) {
                //val filePath = ImagePickerManager.getImagePathFromData(data, this)
                if (getCurrentFragType() == FragType.ViewAllTAListFragment) {
                    if (data != null)
                        (getFragment() as ViewAllTAListFragment).showPickedFileFromGalleryFetch(data)
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val resultUri = result.uri


                    if(feedBackDialogCompetetorImg){
                        val fileSize = AppUtils.getCompressOldImage(resultUri.toString(), this)
                        feedbackDialog?.setImage(resultUri, fileSize / 1024)
                        feedBackDialogCompetetorImg=false
                        return
                    }

                    if (AppUtils.isRevisit!!) {
                        callFeedbackDialog(resultUri.toString())
                        //revisitShop(resultUri.toString())
                    }
                    else if(isVisitCardScan) {
                        isVisitCardScan = false
                        if (getFragment() != null && getFragment() is AddShopFragment) {
                            //(getFragment() as AddShopFragment).processImage(/*File(resultUri.path!!)*/)
                        }
                    }
                    else {
                        when {
                            getCurrentFragType() == FragType.RegisTerFaceFragment -> {
                                val fileSize = AppUtils.getCompressOldImage(resultUri.toString(), this)
                                //(getFragment() as RegisTerFaceFragment).setImageData(result!!)
                                getAddFacePic(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.ShopDamageProductSubmitFrag -> {
                                val fileSize = AppUtils.getCompressOldImage(resultUri.toString(), this)
                                getDamagedPic(fileSize, resultUri)
                            }

                            getCurrentFragType() == FragType.AddShopFragment -> {
                                var fileSize = AppUtils.getCompressOldImage(resultUri.toString(), this)

                                //new image compress
                                var qlty=30
                                while((fileSize/1024)>50) {
                                   /* qlty=qlty-5
                                    if(qlty<5){
                                        break
                                    }*/
                                    fileSize = AppUtils.getCompressOldImagev1(resultUri.toString(), this, qlty)
                                }

                                getAddShopPic(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.ShopDetailFragment -> {
                                //AppUtils.getCompressImage(resultUri.toString())
                                val fileSize = AppUtils.getCompressOldImage(resultUri.toString(), this)
                                getEditShopPic(fileSize, resultUri)
                                //(getFragment() as ShopDetailFragment).setImage(resultUri)
                            }
                            getCurrentFragType() == FragType.AddBillingFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                addBillingCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.AddAttendanceFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                addAttendanceCroppedImg(fileSize, resultUri, true)
                            }
                            getCurrentFragType() == FragType.DailyPlanListFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                addAttendanceCroppedImg(fileSize, resultUri, false)
                            }
                            getCurrentFragType() == FragType.DashboardFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                addDashboardStartCroppedImg(fileSize, resultUri, false)
                            }
                            getCurrentFragType() == FragType.MyProfileFragment -> {
                                var fileSize = AppUtils.getCompressOldImage(resultUri.toString(), this)

                                //new image compress
                                var qlty=30
                                while((fileSize/1024)>50) {
                                    /*qlty=qlty-5
                                    if(qlty<5){
                                        break
                                    }*/
                                    fileSize = AppUtils.getCompressOldImagev1(resultUri.toString(), this, qlty)
                                }

                                editProfilePic(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.AddDynamicFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                addDynamicFormCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.EditDynamicFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                editDynamicFormCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.AddActivityFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                addActivityFormCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.EditActivityFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                editActivityFormCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.WorkInProgressFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                wipCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.WorkOnHoldFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                wohCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.WorkCompletedFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                workCompletedCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.WorkCancelledFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                workCancelledCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.UpdateReviewFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                updateReviewCroppedImg(fileSize, resultUri)
                            }
                            getCurrentFragType() == FragType.ProtoRegistrationFragment -> {
                                val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                updatePhotoRegAadhaarCroppedImg(fileSize, resultUri)
                            }
                            else -> {
                                if (getCurrentFragType() == FragType.NearByShopsListFragment || getCurrentFragType() == FragType.NewDateWiseOrderListFragment ||
                                        getCurrentFragType() == FragType.NewOrderListFragment || getCurrentFragType() == FragType.ShopBillingListFragment ||
                                        getCurrentFragType() == FragType.ViewAllOrderListFragment) {
                                    val fileSize = AppUtils.getCompressBillingImage(resultUri.toString(), this)
                                    addCollectionCroppedImg(fileSize, resultUri)
                                }
                            }
                        }
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    var error = result.error
                }
//
            } else if (requestCode == PermissionHelper.REQUEST_CODE_STORAGE) {
                if (getCurrentFragType() == FragType.MyProfileFragment) {
                    //AppUtils.getCompressContentImage(data!!.data, this)

                    /*getGalleryImage(this, data)
                    val fileSize = AppUtils.getCompressImage(filePath)
                    editProfilePic(fileSize)*/

                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)

                    //(getFragment() as MyProfileFragment).setImage(data.data)
                } else if (getCurrentFragType() == FragType.MarketingPagerFragment) {
                    AppUtils.getCompressImage(data!!.data.toString())
                    (getFragment() as MarketingPagerFragment).setImage(data.data!!)
                } else if (getCurrentFragType() == FragType.AddShopFragment) {

                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    /*14-12-2021*/
                    if(Pref.IsnewleadtypeforRuby){
                        CropImage.activity(data.data)
                                .setMinCropWindowSize(500, 500)
                                .setAspectRatio(1, 1)
                                .start(this)
                    }else{
                        CropImage.activity(data.data)
                                .setAspectRatio(40, 21)
                                .start(this)
                    }

                } else if (getCurrentFragType() == FragType.ShopDetailFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                } else if (getCurrentFragType() == FragType.AddBillingFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                } else if (getCurrentFragType() == FragType.AddDynamicFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                } else if (getCurrentFragType() == FragType.EditDynamicFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                } else if (getCurrentFragType() == FragType.AddActivityFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                } else if (getCurrentFragType() == FragType.EditActivityFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                }
                else if (getCurrentFragType() == FragType.NearByShopsListFragment || getCurrentFragType() == FragType.NewDateWiseOrderListFragment ||
                        getCurrentFragType() == FragType.NewOrderListFragment || getCurrentFragType() == FragType.ShopBillingListFragment ||
                        getCurrentFragType() == FragType.ViewAllOrderListFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                }
                else if (getCurrentFragType() == FragType.ReimbursementFragment) {
                    //AppUtils.getCompressContentImage(data!!.data, this)

                    getGalleryImage(this, data)
                    /*val fileSize = AppUtils.getCompressImage(filePath)
                    editProfilePic(fileSize)*/

                    /*val file = File(filePath)
                    var newFile: File? = null

                    progress_wheel.spin()
                    doAsync {

                        val processImage = ProcessImageUtils_v1(this@DashboardActivity, file, 50)
                        newFile = processImage.ProcessImage()

                        uiThread {
                            //progress_wheel.stopSpinning()
                            if (newFile != null) {
                                XLog.e("=========Gallery Image from new technique==========")
                                filePath = newFile?.absolutePath!!
                                reimbursementPic(newFile!!.length())
                            } else {
                                // Image compression
                                val fileSize = AppUtils.getCompressImage(filePath)
                                reimbursementPic(fileSize)
                            }
                        }
                    }*/

                    (getFragment() as ReimbursementFragment).setImage(filePath)

                    //(getFragment() as MyProfileFragment).setImage(data.data)
                } else if (getCurrentFragType() == FragType.EditReimbursementFragment) {
                    //AppUtils.getCompressContentImage(data!!.data, this)

                    getGalleryImage(this, data)
                    /*val fileSize = AppUtils.getCompressImage(filePath)
                    editProfilePic(fileSize)*/

                    /*val file = File(filePath)
                    var newFile: File? = null

                    progress_wheel.spin()
                    doAsync {

                        val processImage = ProcessImageUtils_v1(this@DashboardActivity, file, 50)
                        newFile = processImage.ProcessImage()

                        uiThread {
                            if (newFile != null) {
                                XLog.e("=========Image Capture from new technique==========")
                                filePath = newFile?.absolutePath!!
                                reimbursementEditPic(newFile!!.length())
                            } else {
                                // Image compression
                                val fileSize = AppUtils.getCompressImage(filePath)
                                reimbursementEditPic(fileSize)
                            }
                        }
                    }*/

                    (getFragment() as EditReimbursementFragment).setImage(filePath)

                    //(getFragment() as MyProfileFragment).setImage(data.data)
                }
                else if (getCurrentFragType() == FragType.DocumentListFragment) {
                    getGalleryImage(this, data)
                    (getFragment() as DocumentListFragment).setImage(filePath)
                }
                else if (getCurrentFragType() == FragType.WorkInProgressFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                }
                else if (getCurrentFragType() == FragType.WorkOnHoldFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                }
                else if (getCurrentFragType() == FragType.WorkCompletedFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                }
                else if (getCurrentFragType() == FragType.WorkCancelledFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                }
                else if (getCurrentFragType() == FragType.UpdateReviewFragment) {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)
                }else if(getCurrentFragType() == FragType.RegisTerFaceFragment){
                    CropImage.activity(data?.data)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setMinCropWindowSize(400, 400)
                            .setAspectRatio(1, 1)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAllowRotation(false)
                            .setOutputCompressQuality(100)
                            .start(this)
                }else if(getCurrentFragType() == FragType.ProtoRegistrationFragment){
                    // for gallary image
                    getGalleryImage(this, data)
                    if (!TextUtils.isEmpty(filePath)) {
                        //val contentURI = FTStorageUtils.getImageContentUri(this@DashboardActivity, filePath)
                        //XLog.e("DashboardActivity :  ,  contentURI FilePath : $contentURI")
                        //val fileSize = AppUtils.getCompressBillingImage(contentURI.toString(), this)
                        //updatePhotoRegAadhaarCroppedImg(fileSize, contentURI)

                        (getFragment() as ProtoRegistrationFragment).setImage(filePath)

                    }
                }else if (getCurrentFragType() == FragType.SurveyFrag) {
                    getGalleryImage(this, data)
                    (getFragment() as SurveyFrag).setImageFromPath(filePath)
                }
                else {
                    XLog.d("DashboardActivity : " + " , " + " Gallery Image FilePath :" + data!!.data)
                    CropImage.activity(data.data)
                            .setAspectRatio(40, 21)
                            .start(this)

                }

            } else if (requestCode == REQUEST_CODE_DOCUMENT) {
                try {
                    if (data != null && data.data != null) {
                        filePath = NewFileUtils.getRealPath(this@DashboardActivity, data.data)

                        if (filePath.contains("_.*_")) {
                            showSnackMessage("Invalid file path")
                            return
                        }

                        if (filePath.contains("google")) {
                            showSnackMessage("Can not select document from google drive")
                            return
                        }

                        val file = File(filePath)

                        val extension = getExtension(file)

                        try {
                            Log.e("Dashboard", "extension======> $extension")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }

                        if (extension.contains("msword") || extension.contains("doc") || extension.contains("docx") ||
                                extension.contains("xls") || extension.contains("xlsx") || extension.contains("pdf") ||
                                extension.contains("jpg") || extension.contains("jpeg") || extension.contains("png")) {

                            if (extension.contains("jpg") || extension.contains("jpeg") || extension.contains("png")) {
                                if (getCurrentFragType() == FragType.DocumentListFragment) {
                                    getGalleryImage(this, data)
                                    (getFragment() as DocumentListFragment).setImage(filePath)
                                }
                                else {
                                    CropImage.activity(data.data)
                                            .setAspectRatio(40, 21)
                                            .start(this)
                                }
                            } else {
                                when {
                                    getCurrentFragType() == FragType.AddBillingFragment -> addBillingPic(file.length())
                                    getCurrentFragType() == FragType.AddDynamicFragment -> addDynamicFormDocument(file.length())
                                    getCurrentFragType() == FragType.EditDynamicFragment -> editDynamicFormDocument(file.length())
                                    getCurrentFragType() == FragType.AddActivityFragment -> addActivityFormDocument(file.length())
                                    getCurrentFragType() == FragType.EditActivityFragment -> editActivityFormDocument(file.length())
                                    getCurrentFragType() == FragType.DocumentListFragment -> addEditDocFormDocument(file.length())
                                    getCurrentFragType() == FragType.WorkInProgressFragment -> wipDocument(file.length())
                                    getCurrentFragType() == FragType.WorkOnHoldFragment -> wohDocument(file.length())
                                    getCurrentFragType() == FragType.WorkCompletedFragment -> workCompletedDocument(file.length())
                                    getCurrentFragType() == FragType.WorkCancelledFragment -> workCancelledDocument(file.length())
                                    getCurrentFragType() == FragType.UpdateReviewFragment -> updateReviewDocument(file.length())
                                    getCurrentFragType() == FragType.ProtoRegistrationFragment -> updatePhotoAadhaarDocument(file.length())

                                    else -> {
                                        if (getCurrentFragType() == FragType.NearByShopsListFragment || getCurrentFragType() == FragType.NewDateWiseOrderListFragment ||
                                                getCurrentFragType() == FragType.NewOrderListFragment || getCurrentFragType() == FragType.ShopBillingListFragment ||
                                                getCurrentFragType() == FragType.ViewAllOrderListFragment) {
                                            addCollectionDocument(file.length())
                                        }
                                    }
                                }
                            }
                        } else
                            showSnackMessage("File is corrupted. Can not choose file.")
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            /*else if (requestCode == PermissionHelper.REQUEST_CODE_GET_FILE) {
                val selectedImageUri = data?.data
                //OI FILE Manager
                val uriPath = selectedImageUri?.path
                getGalleryImage(this, data)

                if (getCurrentFragType() == FragType.DocumentListFragment)
                    (getFragment() as DocumentListFragment).shareLocalFile(filePath, uriPath)
            }*/
            else if (requestCode == PermissionHelper.REQUEST_CODE_AUDIO) {
                if (!AppUtils.isRevisit!!) {
                    if (getFragment() != null && getFragment() is AddShopFragment)
                        (getFragment() as AddShopFragment).saveAudio()
                } else
                    feedbackDialog?.setAudio()
            }
            else if (requestCode == PermissionHelper.REQUEST_CODE_EXO_PLAYER) {
                if (getFragment() != null && getFragment() is MicroLearningListFragment) {
                    (getFragment() as MicroLearningListFragment).getLearningList()

                    val intent = Intent(this, FileOpeningTimeIntentService::class.java)
                    intent.also {
                        it.putExtra("id", (getFragment() as MicroLearningListFragment).selectedFile?.id)
                        it.putExtra("start_time", (getFragment() as MicroLearningListFragment).openingDateTime)
                        startService(it)
                    }
                }
            }
            else if(CustomStatic.IsCameraFacingFromTeamAttdCametaStatus){
                CustomStatic.IsCameraFacingFromTeamAttdCametaStatus=false
            }
            else {
                if (data?.action.equals("com.google.zxing.client.android.SCAN")) {
                    /*val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
                    val contents = result.contents
                    if (!TextUtils.isEmpty(contents))
                        CodeScannerTextDialog.newInstance(contents).show(supportFragmentManager, "")
                    else
                        showSnackMessage("QrCode scan cancel")*/
                }
                else {
                    checkLocationMode()
                    try {
                        if (getFragment() != null && getFragment() is GpsDisableFragment) {
                            (getFragment() as GpsDisableFragment).onFragmentActivityResult(requestCode, resultCode, data)

                            if (getFragment() != null && getFragment() !is AddAttendanceFragment && !isGpsDisabled)
                                checkToShowAddAttendanceAlert()
                        } else {
                            if (getFragment() != null && getFragment() !is AddAttendanceFragment && !isGpsDisabled)
                                checkToShowAddAttendanceAlert()

                            if (intent != null && intent.extras != null && /*!isAttendanceAlertPresent &&*/ !isGpsDisabled) {
                                if (Pref.isAddAttendence)
                                    callShopVisitConfirmationDialog(intent.extras!!.get("NAME") as String, intent.extras!!.get("ID") as String)
                                else
                                    checkToShowAddAttendanceAlert()
                                intent = null
                            } else {
                                if (intent != null && intent.hasExtra("TYPE")) {
                                    //logo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake))
                                    if (intent.getStringExtra("TYPE").equals("PUSH", ignoreCase = true)) {
                                        if (forceLogoutDialog == null)
                                            loadFragment(FragType.NotificationFragment, true, "")
                                    } else if (intent.getStringExtra("TYPE").equals("DUE", ignoreCase = true)) {
                                        if (getFragment() != null && getFragment() !is NearByShopsListFragment)
                                            loadFragment(FragType.NearByShopsListFragment, false, "")
                                    }else if (intent.getStringExtra("TYPE").equals("ACTIVITYDUE", ignoreCase = true)) {
                                        if (getFragment() != null && getFragment() !is LeadFrag)
                                            loadFragment(FragType.LeadFrag, false, "")
                                    }
                                    else if (intent.getStringExtra("TYPE").equals("TASK", ignoreCase = true)) {
                                        if (getFragment() != null && getFragment() !is TaskListFragment)
                                            loadFragment(FragType.TaskListFragment, false, "")
                                    } else if (intent.getStringExtra("TYPE").equals("Msg", ignoreCase = true)) {
                                        if (getFragment() != null && getFragment() is ChatListFragment)
                                            onBackPressed()
                                        val chatUser = intent.getSerializableExtra("chatUser") as ChatUserDataModel
                                        userName = chatUser.name
                                        loadFragment(FragType.ChatListFragment, true, chatUser)
                                    } else if (intent.getStringExtra("TYPE").equals("TIMESHEET", ignoreCase = true)) {
                                        if (getFragment() != null && getFragment() !is TimeSheetListFragment)
                                            loadFragment(FragType.TimeSheetListFragment, false, "")
                                    } else if (intent.getStringExtra("TYPE").equals("REIMBURSEMENT", ignoreCase = true)) {
                                        if (getFragment() != null && getFragment() !is ReimbursementListFragment)
                                            loadFragment(FragType.ReimbursementListFragment, false, "")
                                    }
                                    else if (intent.getStringExtra("TYPE").equals("VIDEO", ignoreCase = true)) {
                                        if (getFragment() != null && getFragment() !is MicroLearningListFragment)
                                            loadFragment(FragType.MicroLearningListFragment, false, "")
                                    }
                                    else if (intent.getStringExtra("TYPE").equals("clearData", ignoreCase = true)) {
                                        isClearData = true
                                        Handler().postDelayed(Runnable {
                                            if (getFragment() != null && getFragment() !is LogoutSyncFragment) {
                                                if (AppUtils.isOnline(this))
                                                    loadFragment(FragType.LogoutSyncFragment, false, "")
                                                else
                                                    showSnackMessage(getString(R.string.no_internet))
                                            }
                                        }, 500)
                                    }else if(intent.getStringExtra("TYPE").equals("LEAVE_APPLY", ignoreCase = true)) {
                                        var usrID=intent.getStringExtra("USER_ID")!!
                                        Handler().postDelayed(Runnable {

                                            loadFragment(FragType.LeaveHome, false, usrID)
                                        }, 300)
                                        //loadFragment(FragType.LeaveHome, false, intent.getStringExtra("USER_ID")!!)
                                    }
                                    else if(intent.getStringExtra("TYPE").equals("LEAVE_STATUS", ignoreCase = true)) {
                                        var usrID=intent.getStringExtra("USER_ID")!!
                                        Handler().postDelayed(Runnable {
                                            //loadFragment(FragType.LeaveHome, false, usrID)
                                            loadFragment(FragType.LeaveListFragment, false, "")
                                        }, 300)
                                        //loadFragment(FragType.LeaveHome, false, intent.getStringExtra("USER_ID")!!)
                                    }else if(intent.getStringExtra("TYPE").equals("quotation_approval", ignoreCase = true)) {
                                        Handler().postDelayed(Runnable {
                                            if (getFragment() != null && getFragment() !is ViewAllQuotListFragment)
                                            loadFragment(FragType.MemberListFragment, false, Pref.user_id!!)
                                        }, 700)
                                    }else if(intent.getStringExtra("TYPE").equals("ZERO_COLL_STATUS", ignoreCase = true)) {
                                        Handler().postDelayed(Runnable {
                                            if (getFragment() != null && getFragment() !is CollectionNotiViewPagerFrag1 && getFragment() !is CollectionNotiViewPagerFrag) {
                                                SendBrod.stopBrodColl(this)
                                                SendBrod.stopBrodZeroOrder(this)
                                                tv_noti_count.visibility=View.GONE

                                                if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification==false){
                                                    loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                                                } else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false){
                                                    loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                                                } else if(Pref.ShowCollectionAlert ==false && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification){
                                                    loadFragment(FragType.CollectionNotiViewPagerFrag, true, "")
                                                } else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false){
                                                    loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                                                } else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification){
                                                    loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                                                } else if(Pref.ShowCollectionAlert == false && Pref.ShowZeroCollectioninAlert  && Pref.IsShowRepeatOrderinNotification) {
                                                    loadFragment(FragType.CollectionNotiViewPagerFrag1, true, "")
                                                } else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification){
                                                    loadFragment(FragType.CollectionNotiViewPagerFrag2, true, "")
                                                }
                                            }
                                        }, 500)
                                    }
                                    else
                                        showForceLogoutPopup()

                                    intent = null
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            //mGpsStatusDetector?.checkOnActivityResult(requestCode, resultCode)

            if (requestCode == PermissionHelper.REQUEST_CODE_AUDIO) {

            } else {
                if (isGpsDisabled)
                    loadFragment(FragType.GpsDisableFragment, true, "")
            }
        }
    }


    private fun terminateOtherShopVisit(revisitStatus: Int, addShopDBModelEntity: AddShopDBModelEntity?, storeName: String,
                                        shopId: String, onlineTeamShop: TeamShopListDataModel?, offlineTeamShop: MemberShopEntity?) {

        if (AppUtils.isAutoRevisit) {
            showSnackMessage("Auto Revisit just started")
            return
        }

        try {
            val list = AppDatabase.getDBInstance()!!.addMeetingDao().durationAvailable(false)
            if (list != null) {
                for (i in list.indices) {
                    val endTimeStamp = System.currentTimeMillis().toString()
                    val duration = AppUtils.getTimeFromTimeSpan(list[i].startTimeStamp!!, endTimeStamp)
                    val totalMinute = AppUtils.getMinuteFromTimeStamp(list[i].startTimeStamp!!, endTimeStamp)

                    AppDatabase.getDBInstance()!!.addMeetingDao().updateEndTimeOfMeeting(endTimeStamp, list[i].id, AppUtils.getCurrentDateForShopActi())
                    AppDatabase.getDBInstance()!!.addMeetingDao().updateTimeDurationForDayOfMeeting(list[i].id, duration, AppUtils.getCurrentDateForShopActi())
                    AppDatabase.getDBInstance()!!.addMeetingDao().updateDurationAvailable(true, list[i].id, AppUtils.getCurrentDateForShopActi())

                    //If duration is greater than 20 hour then stop incrementing
                    /*if (totalMinute.toInt() <= Pref.minVisitDurationSpentTime.toInt()) {

                        return
                    }*/
                }
            }

            /*Terminate All other Shop Visit*/
            val shopList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
            for (i in shopList.indices) {
                if (/*shopList[i].shopid != mAddShopDBModelEntity?.shop_id &&*/ !shopList[i].isDurationCalculated) {
                    Pref.durationCompletedShopId = shopList[i].shopid!!
                    Pref.durationCompletedStartTimeStamp = shopList[i].startTimeStamp!!
                    val endTimeStamp = System.currentTimeMillis().toString()
                    val duration = AppUtils.getTimeFromTimeSpan(shopList[i].startTimeStamp, endTimeStamp)
                    val totalMinute = AppUtils.getMinuteFromTimeStamp(shopList[i].startTimeStamp, endTimeStamp)
                    //If duration is greater than 20 hour then stop incrementing
                    if (totalMinute.toInt() > 20 * 60) {
                        if (!Pref.isMultipleVisitEnable)
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                        else
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopList[i].startTimeStamp)
                        return
                    }

                    if (!Pref.isMultipleVisitEnable) {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopList[i].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopList[i].shopid!!, duration, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                    }
                    else {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopList[i].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopList[i].shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopList[i].startTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopList[i].startTimeStamp)
                    }
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopList[i].startTimeStamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(LocationWizard.getNewLocationName(this, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()), shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopList[i].startTimeStamp)

                    val netStatus = if (AppUtils.isOnline(this))
                        "Online"
                    else
                        "Offline"

                    val netType = if (AppUtils.getNetworkType(this).equals("wifi", ignoreCase = true))
                        AppUtils.getNetworkType(this)
                    else
                        "Mobile ${AppUtils.mobNetType(this)}"

                    if (!Pref.isMultipleVisitEnable) {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                                AppUtils.getBatteryPercentage(this).toString(), netStatus, netType.toString(), shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                    }
                    else {
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                                AppUtils.getBatteryPercentage(this).toString(), netStatus, netType.toString(), shopList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopList[i].startTimeStamp)
                    }

                    if (Pref.willShowShopVisitReason && totalMinute.toInt() < Pref.minVisitDurationSpentTime.toInt()) {
                        Pref.isShowShopVisitReason = true
                        showRevisitReasonDialog(revisitStatus, addShopDBModelEntity, storeName, shopId, onlineTeamShop, offlineTeamShop)
                    }
                }
            }

            /*if (Pref.isShowShopVisitReason)
                return

            revisitShop(image)*/

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun revisitShop(image: String) {

        var shopName = ""
        var shopLat = 0.0
        var shopLong = 0.0
        var wilStartRevisit = false
        var shopGpsAddress = ""

        val shopActivityEntity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(mShopId, AppUtils.getCurrentDateForShopActi())
        val imageUpDateTime = AppUtils.getCurrentISODateTime()

        if (Pref.isMultipleVisitEnable)
            wilStartRevisit = true
        else {
            if (shopActivityEntity.isEmpty() || shopActivityEntity[0].date != AppUtils.getCurrentDateForShopActi())
                wilStartRevisit = true
        }

        if (wilStartRevisit) {
            val mShopActivityEntity = ShopActivityEntity()
            mShopActivityEntity.startTimeStamp = System.currentTimeMillis().toString()
            mShopActivityEntity.isUploaded = false
            mShopActivityEntity.isVisited = true
            mShopActivityEntity.shop_name = mStoreName
            mShopActivityEntity.duration_spent = "00:00:00"
            mShopActivityEntity.date = AppUtils.getCurrentDateForShopActi()
            mShopActivityEntity.shop_address = mAddShopDBModelEntity?.address
            mShopActivityEntity.shopid = mAddShopDBModelEntity?.shop_id
            mShopActivityEntity.visited_date = imageUpDateTime //AppUtils.getCurrentISODateTime()
            mShopActivityEntity.isDurationCalculated = false
            if (mAddShopDBModelEntity?.totalVisitCount != null && mAddShopDBModelEntity?.totalVisitCount != "") {
                val visitCount = mAddShopDBModelEntity?.totalVisitCount?.toInt()!! + 1
                AppDatabase.getDBInstance()!!.addShopEntryDao().updateTotalCount(visitCount.toString(), mShopId)
                AppDatabase.getDBInstance()!!.addShopEntryDao().updateLastVisitDate(AppUtils.getCurrentDateChanged(), mShopId)
            }

            var distance = 0.0
            XLog.e("======New Distance (At revisit time)=========")

            val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(mShopId)
            if (!TextUtils.isEmpty(shop.actual_address))
                shopGpsAddress = shop.actual_address
            else
                shopGpsAddress = LocationWizard.getNewLocationName(this, shop.shopLat.toDouble(), shop.shopLong.toDouble())

            if (Pref.isOnLeave.equals("false", ignoreCase = true)) {

                XLog.e("=====User is at work (At revisit time)=======")

                /*if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude)) {
                if (!TextUtils.isEmpty(Pref.source_latitude) && !TextUtils.isEmpty(Pref.source_longitude)) {
                    distance = LocationWizard.getDistance(Pref.source_latitude.toDouble(), Pref.source_longitude.toDouble(),
                            Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())

                    XLog.e("=====Both location available=======")
                } else {
                    //distance = LocationWizard.getDistance(Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble(), 0.0, 0.0)
                    distance = 0.0 //LocationWizard.getDistance(0.0, 0.0, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())

                    XLog.e("=====Only new location available=======")
                }
                Pref.source_latitude = Pref.current_latitude
                Pref.source_longitude = Pref.current_longitude
            } else {
                if (!TextUtils.isEmpty(Pref.source_latitude) && !TextUtils.isEmpty(Pref.source_longitude)) {
                    //distance = LocationWizard.getDistance(Pref.source_latitude.toDouble(), Pref.source_longitude.toDouble(), 0.0, 0.0)
                    distance = 0.0 //LocationWizard.getDistance(0.0, 0.0, Pref.source_latitude.toDouble(), Pref.source_longitude.toDouble())

                    XLog.e("=====Only old location available=======")
                } else {
                    distance = 0.0

                    XLog.e("=====No location available=======")
                }
            }*/

                shopName = shop.shopName
                val locationList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi())

                shopLat = shop.shopLat
                shopLong = shop.shopLong
                //val distance = LocationWizard.getDistance(shop.shopLat, shop.shopLong, location.latitude, location.longitude)

                val userlocation = UserLocationDataEntity()
                userlocation.latitude = shop.shopLat.toString()
                userlocation.longitude = shop.shopLong.toString()

                var loc_distance = 0.0

                if (locationList != null && locationList.isNotEmpty()) {
                    loc_distance = LocationWizard.getDistance(locationList[locationList.size - 1].latitude.toDouble(), locationList[locationList.size - 1].longitude.toDouble(),
                            userlocation.latitude.toDouble(), userlocation.longitude.toDouble())
                }
                val finalDistance = (Pref.tempDistance.toDouble() + loc_distance).toString()

                XLog.e("===Distance (At shop revisit time)===")
                XLog.e("Temp Distance====> " + Pref.tempDistance)
                XLog.e("Normal Distance====> $loc_distance")
                XLog.e("Total Distance====> $finalDistance")
                XLog.e("=====================================")

                userlocation.distance = finalDistance
                userlocation.locationName = LocationWizard.getNewLocationName(this, userlocation.latitude.toDouble(), userlocation.longitude.toDouble())
                userlocation.timestamp = LocationWizard.getTimeStamp()
                userlocation.time = LocationWizard.getFormattedTime24Hours(true)
                userlocation.meridiem = LocationWizard.getMeridiem()
                userlocation.hour = LocationWizard.getHour()
                userlocation.minutes = LocationWizard.getMinute()
                userlocation.isUploaded = false
                userlocation.shops = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()).size.toString()
                userlocation.updateDate = AppUtils.getCurrentDateForShopActi()
                userlocation.updateDateTime = AppUtils.getCurrentDateTime()
                userlocation.meeting = AppDatabase.getDBInstance()!!.addMeetingDao().getMeetingDateWise(AppUtils.getCurrentDateForShopActi()).size.toString()
                userlocation.network_status = if (AppUtils.isOnline(this)) "Online" else "Offline"
                userlocation.battery_percentage = AppUtils.getBatteryPercentage(this).toString()
                AppDatabase.getDBInstance()!!.userLocationDataDao().insertAll(userlocation)

                XLog.e("=====Shop revisit data added=======")

                Pref.totalS2SDistance = (Pref.totalS2SDistance.toDouble() + userlocation.distance.toDouble()).toString()

                distance = Pref.totalS2SDistance.toDouble()
                Pref.totalS2SDistance = "0.0"
                Pref.tempDistance = "0.0"
            } else {
                XLog.e("=====User is on leave (At revisit time)=======")
                distance = 0.0
            }

            XLog.e("shop to shop distance (At revisit time)=====> $distance")

            mShopActivityEntity.distance_travelled = distance.toString()

//            AppUtils.isShopVisited = true
            Pref.isShopVisited=true

            if (!TextUtils.isEmpty(feedback))
                mShopActivityEntity.feedback = feedback

            mShopActivityEntity.next_visit_date = nextVisitDate

            val todaysVisitedShop = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

            if (todaysVisitedShop == null || todaysVisitedShop.isEmpty()) {
                mShopActivityEntity.isFirstShopVisited = true

                if (!TextUtils.isEmpty(Pref.home_latitude) && !TextUtils.isEmpty(Pref.home_longitude)) {
                    val distance_ = LocationWizard.getDistance(Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble(),
                            shopLat, shopLong)
                    mShopActivityEntity.distance_from_home_loc = distance_.toString()
                } else
                    mShopActivityEntity.distance_from_home_loc = "0.0"
            } else {
                mShopActivityEntity.isFirstShopVisited = false
                mShopActivityEntity.distance_from_home_loc = ""
            }

            mShopActivityEntity.in_time = AppUtils.getCurrentTimeWithMeredian()
            mShopActivityEntity.in_loc = shopGpsAddress


            var shopAll=AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
            mShopActivityEntity.shop_revisit_uniqKey=Pref.user_id+System.currentTimeMillis().toString()

            /*13-12-2021*/
            if (prosId!=null && !prosId.equals(""))
                mShopActivityEntity.pros_id = prosId
            else{
                mShopActivityEntity.pros_id=""
            }

            if (approxValue!=null && !approxValue.equals(""))
                mShopActivityEntity.approximate_1st_billing_value = approxValue
            else{
                mShopActivityEntity.approximate_1st_billing_value=""
            }

            try{
                mShopActivityEntity.agency_name=AppDatabase.getDBInstance()!!.shopActivityDao().getAgencyName(mShopActivityEntity.shopid!!)
            }catch (ex: Exception){
                mShopActivityEntity.agency_name=""
            }
            if(mShopActivityEntity.agency_name==null){
                mShopActivityEntity.agency_name=""
            }

            mShopActivityEntity.updated_by=Pref.user_id
            mShopActivityEntity.updated_on= AppUtils.getCurrentDateForShopActi()

            /*if(shopAll==null || shopAll.isEmpty()){
                mShopActivityEntity.shop_revisit_uniqKey = Pref.user_id+AppUtils.getCurrentDateMonth()+"10001"
            }else{
                if(shopAll[shopAll.size-1].shop_revisit_uniqKey != null && (shopAll[shopAll.size-1].shop_revisit_uniqKey?.length!! > 1))
                 mShopActivityEntity.shop_revisit_uniqKey=(shopAll[shopAll.size-1].shop_revisit_uniqKey!!.toLong()+1).toString()
                else
                    mShopActivityEntity.shop_revisit_uniqKey = Pref.user_id+AppUtils.getCurrentDateMonth()+"10001"
            }*/


            AppDatabase.getDBInstance()!!.shopActivityDao().insertAll(mShopActivityEntity)

            // shop feedback work
            var feedObj: ShopFeedbackEntity = ShopFeedbackEntity()
            feedObj.shop_id=mShopActivityEntity.shopid
            feedObj.feedback=mShopActivityEntity.feedback
            feedObj.date_time=AppUtils.getCurrentDateTime()
            if(feedObj.feedback.equals("") || mShopActivityEntity.feedback==null)
                feedObj.feedback="N/A"
            AppDatabase.getDBInstance()?.shopFeedbackDao()?.insert(feedObj)

        }

        AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(mShopId)!![0].visited = true

        val performance = AppDatabase.getDBInstance()!!.performanceDao().getTodaysData(AppUtils.getCurrentDateForShopActi())
        if (performance != null) {
            val list = AppDatabase.getDBInstance()!!.shopActivityDao().getDurationCalculatedVisitedShopForADay(AppUtils.getCurrentDateForShopActi(), true)
            AppDatabase.getDBInstance()!!.performanceDao().updateTotalShopVisited(list.size.toString(), AppUtils.getCurrentDateForShopActi())
            var totalTimeSpentForADay = 0
            for (i in list.indices) {
                totalTimeSpentForADay += list[i].totalMinute.toInt()
            }
            AppDatabase.getDBInstance()!!.performanceDao().updateTotalDuration(totalTimeSpentForADay.toString(), AppUtils.getCurrentDateForShopActi())
        } else {
            val list = AppDatabase.getDBInstance()!!.shopActivityDao().getDurationCalculatedVisitedShopForADay(AppUtils.getCurrentDateForShopActi(), true)
            val performanceEntity = PerformanceEntity()
            performanceEntity.date = AppUtils.getCurrentDateForShopActi()
            performanceEntity.total_shop_visited = list.size.toString()
            var totalTimeSpentForADay = 0
            for (i in list.indices) {
                totalTimeSpentForADay += list[i].totalMinute.toInt()
            }
            performanceEntity.total_duration_spent = totalTimeSpentForADay.toString()
            AppDatabase.getDBInstance()!!.performanceDao().insert(performanceEntity)
        }

        var shopTyByID = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopType(mShopId)!!.toString()
        if (Pref.IsnewleadtypeforRuby && shopTyByID.equals("2")){
            println("addShopVisitPic shoptype2");
            val fileSize = AppUtils.getCompressOldImage(image, this)
            addShopVisitPic(fileSize, imageUpDateTime, shopName, image)
        }else if (Pref.IsnewleadtypeforRuby && !shopTyByID.equals("2")){
            println("addShopVisitPic !shoptype2");
        }else if (Pref.isRevisitCaptureImage) {
            println("addShopVisitPic normal");
            // Image compression
            //val fileSize = AppUtils.getCompressImage(image)
            val fileSize = AppUtils.getCompressOldImage(image, this)
            addShopVisitPic(fileSize, imageUpDateTime, shopName, image)
        }


        if (Pref.isRecordAudioEnable) {
            val shopVisitAudio = ShopVisitAudioEntity()
            AppDatabase.getDBInstance()?.shopVisitAudioDao()?.insert(shopVisitAudio.apply {
                shop_id = mShopId
                isUploaded = false
                audio = mFilePath
                visit_datetime = imageUpDateTime
            })
        }

        val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(mShopId)
        if (shopDetail.is_otp_verified.equals("false", ignoreCase = true)) {
            if (AppUtils.isOnline(this@DashboardActivity)) {
                if (!isOtherUsersShopRevisit) {
                    cancelNotification(mShopId)
                    showShopVerificationDialog()
                } else
                    showOrderCollectionDialog()
            } else
                afterShopRevisit()
        } else
            afterShopRevisit()
    }

    private fun showRevisitReasonDialog(revisitStatus: Int, addShopDBModelEntity: AddShopDBModelEntity?, storeName: String,
                                        shopId: String, onlineTeamShop: TeamShopListDataModel?, offlineTeamShop: MemberShopEntity?) {
        reasonDialog = null
        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(Pref.durationCompletedShopId)
        reasonDialog = ReasonDialog.getInstance(shop?.shopName!!, "You are revisiting ${Pref.shopText} but the " +
                "duration spent is less than ${Pref.minVisitDurationSpentTime} minutes. Please write the reason below.", reason) {
            reasonDialog?.dismiss()
            Pref.isShowShopVisitReason = false

            if (!Pref.isMultipleVisitEnable)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEarlyRevisitReason(it, Pref.durationCompletedShopId, AppUtils.getCurrentDateForShopActi())
            else
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEarlyRevisitReason(it, Pref.durationCompletedShopId, AppUtils.getCurrentDateForShopActi(), Pref.durationCompletedStartTimeStamp)

            when (revisitStatus) {
                1 -> startOwnShopRevisit(addShopDBModelEntity!!, storeName, shopId)
                2 -> startRevisitOnlineTeamShop(onlineTeamShop!!)
                3 -> startRevisitOfflineTeamShop(offlineTeamShop!!)
            }
        }
        reasonDialog?.show(supportFragmentManager, "")
    }

    private fun afterShopRevisit() {
        if (!isOtherUsersShopRevisit) {
            cancelNotification(mShopId)
            if(Pref.ShopScreenAftVisitRevisit && Pref.ShopScreenAftVisitRevisitGlobal){
            loadFragment(FragType.ShopDetailFragment, true, mShopId)
            }else{
                AppUtils.isRevisit = false
                Handler().postDelayed(Runnable {
                    loadFragment(FragType.DashboardFragment, true, "")
                    if(getCurrentFragType() == FragType.DashboardFragment)
                        (getFragment() as DashboardFragment).initBottomAdapter()
                }, 300)
            }
        } else {
            /*Team new work*/
            if (Pref.isOrderAvailableForPopup) {
                showOrderCollectionDialog()
            }
            else{
                if (getFragment() != null && getFragment() is MemberShopListFragment)
                    (getFragment() as MemberShopListFragment).updateAdapter()
                else if (getFragment() != null && getFragment() is OfflineShopListFragment)
                    (getFragment() as OfflineShopListFragment).updateAdapter()
            }
        }
    }

    private fun showOrderCollectionDialog() {

        val addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(mShopId)

        var orderText = ""
        orderText = if (Pref.isQuotationPopupShow)
            "Quot. Entry"
        else
            "Order. Entry"

        CommonDialog.getInstance("Action", "What you like to do?", orderText, "Collection Entry", false,
                object : CommonDialogClickListener {
                    override fun onLeftClick() {
                        if (Pref.isQuotationPopupShow)
                            (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
                        else
                            (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                    }

                    override fun onRightClick(editableData: String) {
                        (mContext as DashboardActivity).loadFragment(FragType.CollectionDetailsFragment, true, addShopData)
                    }

                }, object : CommonDialog.OnCloseClickListener {
            override fun onCloseClick() {
                if (getFragment() != null && getFragment() is MemberShopListFragment)
                    (getFragment() as MemberShopListFragment).updateAdapter()
                else if (getFragment() != null && getFragment() is OfflineShopListFragment)
                    (getFragment() as OfflineShopListFragment).updateAdapter()
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun showShopVerificationDialog() {
        if (!Pref.isShowOTPVerificationPopup) {
            if (Pref.ShopScreenAftVisitRevisit && Pref.ShopScreenAftVisitRevisitGlobal) {
                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, mShopId)
            } else {
                AppUtils.isRevisit = false
                Handler().postDelayed(Runnable {
                    (mContext as DashboardActivity).loadFragment(FragType.DashboardFragment, true, "")
                    if(getCurrentFragType() == FragType.DashboardFragment)
                        (getFragment() as DashboardFragment).initBottomAdapter()
                }, 300)
            }
        }
//            loadFragment(FragType.ShopDetailFragment, true, mShopId)
        else {
            ShopVerificationDialog.getInstance(mShopId, object : ShopVerificationDialog.OnOTPButtonClickListener {
                override fun onEditClick(number: String) {
                    val addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(mShopId)
                    if (!addShopData.isUploaded || addShopData.isEditUploaded == 0) {
                        /*if (Pref.isReplaceShopText)
                        showSnackMessage("Please sync this customer first.")
                    else
                        showSnackMessage("Please sync this shop first.")*/

                        showSnackMessage("Please sync this " + Pref.shopText + " first")

                        loadFragment(FragType.NearByShopsListFragment, true, "")
                    } else {
                        saveData(addShopData, number)
                    }

                }

                override fun onCancelClick() {
                    //(mContext as DashboardActivity).onBackPressed()
                    if (Pref.ShopScreenAftVisitRevisit && Pref.ShopScreenAftVisitRevisitGlobal) {
                        (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, mShopId)
                    } else {
                        AppUtils.isRevisit = false
                        (mContext as DashboardActivity).loadFragment(FragType.DashboardFragment, true, "")
                    }
//                    loadFragment(FragType.ShopDetailFragment, true, mShopId)
                }

                override fun onOkButtonClick(otp: String) {
                    callOtpSentApi(mShopId)
                }
            }).show((mContext as DashboardActivity).supportFragmentManager, "ShopVerificationDialog")
        }
    }

    private fun saveData(addShopData: AddShopDBModelEntity, number: String) {
        AppDatabase.getDBInstance()?.addShopEntryDao()?.updateContactNo(addShopData.shop_id, number)

        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(mShopId)
        convertToReqAndApiCall(shop)
    }


    private fun convertToReqAndApiCall(addShopData: AddShopDBModelEntity) {
        if (Pref.user_id == null || Pref.user_id == "" || Pref.user_id == " ") {
            (mContext as DashboardActivity).showSnackMessage("Please login again")
            BaseActivity.isApiInitiated = false
            return
        }

        val addShopReqData = AddShopRequestData()
        addShopReqData.session_token = Pref.session_token
        addShopReqData.address = addShopData.address
        addShopReqData.owner_contact_no = addShopData.ownerContactNumber
        addShopReqData.owner_email = addShopData.ownerEmailId
        addShopReqData.owner_name = addShopData.ownerName
        addShopReqData.pin_code = addShopData.pinCode
        addShopReqData.shop_lat = addShopData.shopLat.toString()
        addShopReqData.shop_long = addShopData.shopLong.toString()
        addShopReqData.shop_name = addShopData.shopName.toString()
        addShopReqData.shop_id = addShopData.shop_id
        addShopReqData.added_date = ""
        addShopReqData.user_id = Pref.user_id
        addShopReqData.type = addShopData.type
        addShopReqData.assigned_to_pp_id = addShopData.assigned_to_pp_id
        addShopReqData.assigned_to_dd_id = addShopData.assigned_to_dd_id

        if (!TextUtils.isEmpty(addShopData.dateOfBirth))
            addShopReqData.dob = AppUtils.changeAttendanceDateFormatToCurrent(addShopData.dateOfBirth)

        if (!TextUtils.isEmpty(addShopData.dateOfAniversary))
            addShopReqData.date_aniversary = AppUtils.changeAttendanceDateFormatToCurrent(addShopData.dateOfAniversary)

        addShopReqData.amount = addShopData.amount
        addShopReqData.area_id = addShopData.area_id
        addShopReqData.model_id = addShopData.model_id
        addShopReqData.primary_app_id = addShopData.primary_app_id
        addShopReqData.secondary_app_id = addShopData.secondary_app_id
        addShopReqData.lead_id = addShopData.lead_id
        addShopReqData.stage_id = addShopData.stage_id
        addShopReqData.funnel_stage_id = addShopData.funnel_stage_id
        addShopReqData.booking_amount = addShopData.booking_amount
        addShopReqData.type_id = addShopData.type_id

        addShopReqData.director_name = addShopData.director_name
        addShopReqData.key_person_name = addShopData.person_name
        addShopReqData.phone_no = addShopData.person_no

        if (!TextUtils.isEmpty(addShopData.family_member_dob))
            addShopReqData.family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(addShopData.family_member_dob)

        if (!TextUtils.isEmpty(addShopData.add_dob))
            addShopReqData.addtional_dob = AppUtils.changeAttendanceDateFormatToCurrent(addShopData.add_dob)

        if (!TextUtils.isEmpty(addShopData.add_doa))
            addShopReqData.addtional_doa = AppUtils.changeAttendanceDateFormatToCurrent(addShopData.add_doa)

        addShopReqData.specialization = addShopData.specialization
        addShopReqData.category = addShopData.category
        addShopReqData.doc_address = addShopData.doc_address
        addShopReqData.doc_pincode = addShopData.doc_pincode
        addShopReqData.is_chamber_same_headquarter = addShopData.chamber_status.toString()
        addShopReqData.is_chamber_same_headquarter_remarks = addShopData.remarks
        addShopReqData.chemist_name = addShopData.chemist_name
        addShopReqData.chemist_address = addShopData.chemist_address
        addShopReqData.chemist_pincode = addShopData.chemist_pincode
        addShopReqData.assistant_contact_no = addShopData.assistant_no
        addShopReqData.average_patient_per_day = addShopData.patient_count
        addShopReqData.assistant_name = addShopData.assistant_name

        if (!TextUtils.isEmpty(addShopData.doc_family_dob))
            addShopReqData.doc_family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(addShopData.doc_family_dob)

        if (!TextUtils.isEmpty(addShopData.assistant_dob))
            addShopReqData.assistant_dob = AppUtils.changeAttendanceDateFormatToCurrent(addShopData.assistant_dob)

        if (!TextUtils.isEmpty(addShopData.assistant_doa))
            addShopReqData.assistant_doa = AppUtils.changeAttendanceDateFormatToCurrent(addShopData.assistant_doa)

        if (!TextUtils.isEmpty(addShopData.assistant_family_dob))
            addShopReqData.assistant_family_dob = AppUtils.changeAttendanceDateFormatToCurrent(addShopData.assistant_family_dob)

        addShopReqData.entity_id = addShopData.entity_id
        addShopReqData.party_status_id = addShopData.party_status_id
        addShopReqData.retailer_id = addShopData.retailer_id
        addShopReqData.dealer_id = addShopData.dealer_id
        addShopReqData.beat_id = addShopData.beat_id
        addShopReqData.assigned_to_shop_id = addShopData.assigned_to_shop_id
        addShopReqData.actual_address = addShopData.actual_address

        /*val addShop = AddShopRequest()
        addShop.data = addShopReqData*/

        if (AppUtils.isOnline(mContext)) {

            if (BaseActivity.isApiInitiated)
                return

            BaseActivity.isApiInitiated = true

            callEditShopApi(addShopReqData, addShopData.shopImageLocalPath, addShopData.doc_degree)
        } else {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }
    }

    private fun callEditShopApi(addShopReqData: AddShopRequestData, shopImageLocalPath: String?, doc_degree: String?) {
        progress_wheel.spin()

        if (TextUtils.isEmpty(shopImageLocalPath) && TextUtils.isEmpty(doc_degree)) {
            val repository = EditShopRepoProvider.provideEditShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.editShop(addShopReqData)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("Edit Shop : " + ", SHOP: " + addShopReqData.shop_name + ", RESPONSE:" + result.message)
                                when (addShopResult.status) {
                                    NetworkConstant.SUCCESS -> {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsEditUploaded(1, addShopReqData.shop_id)
                                        progress_wheel.stopSpinning()
                                        //                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")
                                        (mContext as DashboardActivity).updateFence()

                                        showShopVerificationDialog()

                                    }
                                    NetworkConstant.SESSION_MISMATCH -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).clearData()
                                        startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                        (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                        (mContext as DashboardActivity).finish()
                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                    }
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                error.printStackTrace()
                                BaseActivity.isApiInitiated = false
                                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                            })
            )
        }
        else {
            val repository = EditShopRepoProvider.provideEditShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShopReqData, shopImageLocalPath, doc_degree, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("Edit Shop : " + ", SHOP: " + addShopReqData.shop_name + ", RESPONSE:" + result.message)
                                when (addShopResult.status) {
                                    NetworkConstant.SUCCESS -> {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsEditUploaded(1, addShopReqData.shop_id)
                                        progress_wheel.stopSpinning()
                                        //                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")
                                        (mContext as DashboardActivity).updateFence()

                                        showShopVerificationDialog()

                                    }
                                    NetworkConstant.SESSION_MISMATCH -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).clearData()
                                        startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                        (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                        (mContext as DashboardActivity).finish()
                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                    }
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                error.printStackTrace()
                                BaseActivity.isApiInitiated = false
                                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                            })
            )
        }
    }


    private fun callOtpSentApi(shop_id: String) {
        val repository = OtpSentRepoProvider.otpSentRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.otpSent(shop_id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val addShopResult = result as BaseResponse
                            progress_wheel.stopSpinning()
                            /*if (addShopResult.status == NetworkConstant.SUCCESS) {

                                (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                showOtpVerificationDialog()

                            } else {
                                (mContext as DashboardActivity).showSnackMessage("OTP sent failed")
                                loadFragment(FragType.ShopDetailFragment, true, mShopId)
                            }*/

                            showOtpVerificationDialog(true)
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            /*(mContext as DashboardActivity).showSnackMessage("OTP sent failed")
                            loadFragment(FragType.ShopDetailFragment, true, mShopId)*/
                            showOtpVerificationDialog(true)
                        })
        )
    }

    private fun showOtpVerificationDialog(isShowTimer: Boolean) {
        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(mShopId)
        OTPVerificationDialog.getInstance(shop.ownerContactNumber, isShowTimer, shop.shopName, object : OTPVerificationDialog.OnOTPButtonClickListener {
            override fun onResentClick() {
                callOtpSentApi(mShopId)
            }

            override fun onCancelClick() {
                //(mContext as DashboardActivity).onBackPressed()
                loadFragment(FragType.ShopDetailFragment, true, mShopId)
            }

            override fun onOkButtonClick(otp: String) {
                //callOtpVerifyApi(otp, mShopId)

                val distance = LocationWizard.getDistance(shop.shopLat, shop.shopLong, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())

                if (distance * 1000 <= 20)
                    callOtpVerifyApi(otp, mShopId)
                else
                    (mContext as DashboardActivity).showSnackMessage("OTP can be verified only from the shop.")
            }
        }).show((mContext as DashboardActivity).supportFragmentManager, "OTPVerificationDialog")
    }

    private fun callOtpVerifyApi(otp: String, shop_id: String) {
        val repository = OtpVerificationRepoProvider.otpVerifyRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.otpVerify(shop_id, otp)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val addShopResult = result as BaseResponse
                            progress_wheel.stopSpinning()
                            if (addShopResult.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsOtpVerified("true", shop_id)
                                (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                loadFragment(FragType.ShopDetailFragment, true, mShopId)
                            } else {
                                (mContext as DashboardActivity).showSnackMessage("OTP verification failed.")
                                showOtpVerificationDialog(false)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("OTP verification failed.")
                            showOtpVerificationDialog(false)
                        })
        )
    }

    fun getGalleryImage(context: Context, data: Intent?) {
        val selectedImage = data?.data
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
        cursor!!.moveToFirst()
        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        filePath = picturePath
        cursor.close()
    }

    private fun reimbursementPic(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression-----------------> $fileSizeInKB KB")
        //if (fileSizeInKB <= 200) {
        isProfile = false
        progress_wheel.stopSpinning()
        (getFragment() as ReimbursementFragment).setImage(filePath)
        /*} else {
            editProfilePic(AppUtils.getCompressImage(filePath))
        }*/
    }

    private fun reimbursementEditPic(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression-----------------> $fileSizeInKB KB")
        //if (fileSizeInKB <= 200) {
        isProfile = false
        progress_wheel.stopSpinning()
        (getFragment() as EditReimbursementFragment).setImage(filePath)
        /*} else {
            editProfilePic(AppUtils.getCompressImage(filePath))
        }*/
    }

    private fun addBillingPic(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")
        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as AddBillingFragment).setCameraImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun addBillingCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as AddBillingFragment).setCameraImage(file)
    }

    private fun addDynamicFormCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as AddDynamicFragment).setImage(file)
    }

    private fun addDynamicFormDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as AddDynamicFragment).setImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun addActivityFormCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as AddActivityFragment).setImage(file)
    }

    private fun addActivityFormDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as AddActivityFragment).setImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun wipCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as WorkInProgressFragment).setImage(file)
    }

    private fun wipDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as WorkInProgressFragment).setImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun wohCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as WorkOnHoldFragment).setImage(file)
    }

    private fun wohDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as WorkOnHoldFragment).setImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun workCompletedCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as WorkCompletedFragment).setImage(file)
    }

    private fun workCompletedDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as WorkCompletedFragment).setImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun workCancelledCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as WorkCancelledFragment).setImage(file)
    }

    private fun workCancelledDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as WorkCancelledFragment).setImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun updateReviewCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as UpdateReviewFragment).setImage(file)
    }

    private fun updatePhotoRegAadhaarCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        //(getFragment() as ProtoRegistrationFragment).setImage(file)
    }


    private fun updateReviewDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as UpdateReviewFragment).setImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun updatePhotoAadhaarDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")
        Pref.maxFileSize="400"
        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as ProtoRegistrationFragment).setDoc(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun editActivityFormCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as EditActivityFragment).setImage(file)
    }

    private fun addCollectionCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(resultUri.path!!)

                if (getFragment() != null) {
                    when {
                        getFragment() is NearByShopsListFragment -> (getFragment() as NearByShopsListFragment).setImage(file)
                        getFragment() is NewOrderListFragment -> (getFragment() as NewOrderListFragment).setImage(file)
                        getFragment() is NewDateWiseOrderListFragment -> (getFragment() as NewDateWiseOrderListFragment).setImage(file)
                        getFragment() is ShopBillingListFragment -> (getFragment() as ShopBillingListFragment).setImage(file)
                        getFragment() is ViewAllOrderListFragment -> (getFragment() as ViewAllOrderListFragment).setImage(file)
                    }
                }
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun editActivityFormDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as EditActivityFragment).setImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }


    private fun addEditDocFormDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as DocumentListFragment).setDocument(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun addCollectionDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)

                if (getFragment() != null) {
                    when {
                        getFragment() is NearByShopsListFragment -> (getFragment() as NearByShopsListFragment).setImage(file)
                        getFragment() is NewOrderListFragment -> (getFragment() as NewOrderListFragment).setImage(file)
                        getFragment() is NewDateWiseOrderListFragment -> (getFragment() as NewDateWiseOrderListFragment).setImage(file)
                        getFragment() is ShopBillingListFragment -> (getFragment() as ShopBillingListFragment).setImage(file)
                        getFragment() is ViewAllOrderListFragment -> (getFragment() as ViewAllOrderListFragment).setImage(file)
                    }
                }
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun editDynamicFormCroppedImg(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        (getFragment() as EditDynamicFragment).setImage(file)
    }

    private fun editDynamicFormDocument(fileSize: Long) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB <= Pref.maxFileSize.toInt()) {
                val file = File(filePath)
                (getFragment() as EditDynamicFragment).setImage(file)
            } else
                showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
        }
    }

    private fun addAttendanceCroppedImg(fileSize: Long, resultUri: Uri, isFromAddAttendance: Boolean) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
        if (isFromAddAttendance)
            (getFragment() as AddAttendanceFragment).setCameraImage(file)
        else
            (getFragment() as DailyPlanListFragment).setCameraImage(file)
    }

    private fun addDashboardStartCroppedImg(fileSize: Long, resultUri: Uri, isFromAddAttendance: Boolean) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(resultUri.path!!)
//        (getFragment() as DashboardFragment).setCameraImage(file)

    }


    private fun addAttendanceImg(fileSize: Long, isFromAddAttendance: Boolean) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression==========> $fileSizeInKB KB")

        val file = File(filePath)
        if (isFromAddAttendance)
            (getFragment() as AddAttendanceFragment).setCameraImage(file)
        else
            (getFragment() as DailyPlanListFragment).setCameraImage(file)
    }

    private fun editProfilePic(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        //Toaster.msgShort(this,"Addshop pic size : "+fileSizeInKB.toString()+" kb")
        Log.e("Dashboard", "image file size after compression-----------------> $fileSizeInKB KB")
        //if (fileSizeInKB <= 200) {
        isProfile = false
        (getFragment() as MyProfileFragment).setImage(resultUri.path!!)
        /*} else {
            editProfilePic(AppUtils.getCompressImage(filePath))
        }*/
    }

    private fun getAddFacePic(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression-----------------> $fileSizeInKB KB")
        //if (fileSizeInKB <= 200)
        (getFragment() as RegisTerFaceFragment).setImage(resultUri, fileSizeInKB)
        /*else {
            getAddShopPic(AppUtils.getCompressOldImage(resultUri.toString(), this), resultUri)
        }*/
    }

    private fun getDamagedPic(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression-----------------> $fileSizeInKB KB")
        //if (fileSizeInKB <= 200)
        (getFragment() as ShopDamageProductSubmitFrag).setImage(resultUri, fileSizeInKB)
        /*else {
            getAddShopPic(AppUtils.getCompressOldImage(resultUri.toString(), this), resultUri)
        }*/
    }

    private fun getAddShopPic(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression-----------------> $fileSizeInKB KB")
        //if (fileSizeInKB <= 200)
        (getFragment() as AddShopFragment).setImage(resultUri, fileSizeInKB)
        /*else {
            getAddShopPic(AppUtils.getCompressOldImage(resultUri.toString(), this), resultUri)
        }*/
    }

    private fun getEditShopPic(fileSize: Long, resultUri: Uri) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Dashboard", "image file size after compression-----------------> $fileSizeInKB KB")
        //if (fileSizeInKB <= 200)
        (getFragment() as ShopDetailFragment).setImage(resultUri, fileSizeInKB)
        /*else {
            getEditShopPic(AppUtils.getCompressOldImage(resultUri.toString(), this), resultUri)
        }*/
    }

    private fun addShopVisitPic(fileSize: Long, imageUpDateTime: String, shop: String, image: String) {
        val fileSizeInKB = fileSize / 1024
        XLog.e("Dashboard: $shop image file size after compression==========> $fileSizeInKB KB")
        if (fileSizeInKB > 200) {
            val newFileSize = AppUtils.getCompressImage(image)
            val newFileSizeInKB = newFileSize / 1024
            XLog.e("Dashboard: $shop new image file size after compression==========> $newFileSizeInKB KB")
        }
        val shopVisit = ShopVisitImageModelEntity()
        shopVisit.shop_id = mShopId
        shopVisit.shop_image = image //Environment.getExternalStorageDirectory().path
        shopVisit.visit_datetime = imageUpDateTime
        AppDatabase.getDBInstance()!!.shopVisitImageDao().insert(shopVisit)

        //AppUtils.isRevisit = false
        //loadFragment(FragType.ShopDetailFragment, true, mShopId)
        /*} else {
            addShopVisitPic(AppUtils.getCompressImage(filePath), imageUpDateTime)
        }*/
    }

    private fun callVisitShopImageUploadApi(mShopId: String, imageLink: String, imageUpDateTime: String) {

        val visitImageShop = ShopVisitImageUploadInputModel()
        visitImageShop.session_token = Pref.session_token
        visitImageShop.user_id = Pref.user_id
        visitImageShop.shop_id = mShopId
        visitImageShop.visit_datetime = imageUpDateTime

        val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.visitShopWithImage(visitImageShop, imageLink, this)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val logoutResponse = result as BaseResponse

                            if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.shopVisitImageDao().updateisUploaded(true, mShopId)
                            }
                            AppUtils.isRevisit = false
                            loadFragment(FragType.ShopDetailFragment, true, mShopId)
                            BaseActivity.isApiInitiated = false

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(error.localizedMessage)

                            AppUtils.isRevisit = false
                            loadFragment(FragType.ShopDetailFragment, true, mShopId)
                        })
        )
    }


    fun getCameraImage(data: Intent?) {

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

        val selectedImagePath = AppUtils.getPath(mContext as Activity, selectedImageUri)

        when {
            selectedImagePath != null -> filePath = selectedImagePath
            filemanagerstring != null -> filePath = filemanagerstring
            else -> {
                //Toaster.msgShort(baseActivity, "Unknown Path")
                XLog.e("Bitmap", "Unknown Path")
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Pref.isSefieAlarmed)
            captureFrontImage()
        else if (isCodeScan) {
            isCodeScan = false
            loadFragment(FragType.CodeScannerFragment, true, "")
        }
        else if (getFragment() is MyProfileFragment)
            (getFragment() as MyProfileFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is AddShopFragment)
            (getFragment() as AddShopFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is ShopDetailFragment)
            (getFragment() as ShopDetailFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is ReimbursementFragment)
            (getFragment() as ReimbursementFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is AddBillingFragment)
            (getFragment() as AddBillingFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is AddDynamicFragment)
            (getFragment() as AddDynamicFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is EditDynamicFragment)
            (getFragment() as EditDynamicFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is EditReimbursementFragment)
            (getFragment() as EditReimbursementFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is AddAttendanceFragment)
            (getFragment() as AddAttendanceFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is DailyPlanListFragment)
            (getFragment() as DailyPlanListFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is AddActivityFragment)
            (getFragment() as AddActivityFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is EditActivityFragment)
            (getFragment() as EditActivityFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is AddTimeSheetFragment)
            (getFragment() as AddTimeSheetFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is EditTimeSheetFragment)
            (getFragment() as EditTimeSheetFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is DocumentListFragment)
            (getFragment() as DocumentListFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is AddTaskFragment)
            (getFragment() as AddTaskFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is EditTaskFragment)
            (getFragment() as EditTaskFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is TaskListFragment)
            (getFragment() as TaskListFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is NearByShopsListFragment)
            (getFragment() as NearByShopsListFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is NewOrderListFragment)
            (getFragment() as NewOrderListFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is NewDateWiseOrderListFragment)
            (getFragment() as NewDateWiseOrderListFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is ViewAllOrderListFragment)
            (getFragment() as ViewAllOrderListFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is ShopBillingListFragment)
            (getFragment() as ShopBillingListFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is ScanImageFragment)
            (getFragment() as ScanImageFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is WorkInProgressFragment)
            (getFragment() as WorkInProgressFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is WorkOnHoldFragment)
            (getFragment() as WorkOnHoldFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is WorkCompletedFragment)
            (getFragment() as WorkCompletedFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is WorkCancelledFragment)
            (getFragment() as WorkCancelledFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is UpdateReviewFragment)
            (getFragment() as UpdateReviewFragment).onRequestPermission(requestCode, permissions, grantResults)
        else if (getFragment() is DashboardFragment)
            (getFragment() as DashboardFragment).onRequestPermission(requestCode, permissions, grantResults)
        else {
            if (requestCode == PermissionHelper.REQUEST_CODE_CAMERA) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionHelper.checkStoragePermission(this)) {
                        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (AppUtils.isRevisit!!) {
                            //AppUtils.isRevisit = false
                            intent.putExtra("shopId", mShopId)
                            captureImage()
                            /*val uri = Uri.parse(Environment.getExternalStorageDirectory().absolutePath + System.currentTimeMillis() + "_fts.jpg")
                        FTStorageUtils.IMG_URI = uri
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)*/
                        } else if (isProfile)
                            captureImage()
                        else {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
                            startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)
                        }
                    } else {
                        Toast.makeText(this,
                                "Camera permission has not been granted, cannot saved images",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (requestCode == PermissionHelper.REQUEST_CODE_STORAGE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (PermissionHelper.checkCameraPermission(this)) {

                        if (AppUtils.isProfile) {
                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                            startActivityForResult(galleryIntent, PermissionHelper.REQUEST_CODE_CAMERA)
                        } else {
                            intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
                            startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)
                        }
                    } else {
                        Toast.makeText(this,
                                "External write permission has not been granted, cannot saved images",
                                Toast.LENGTH_SHORT).show()

                    }
                }
            } else if (requestCode == PermissionHelper.TAG_LOCATION_RESULTCODE) {

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (!Pref.isGeoFenceAdded)
                            takeActionOnGeofence()

                    }

                } else {
                    mPendingGeofenceTask = PendingGeofenceTask.NONE
                    PermissionHelper.checkLocationPermission(this, 0)


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Location permission has not been granted", Toast.LENGTH_LONG).show()
                }

            } else
                permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun getPhotoFileUri(fileName: String): Uri {
        val folderPath = FTStorageUtils.getFolderPath(this)
        val imageFile = FTStorageUtils.overWriteFile(folderPath, fileName)
        // FTStorageUtils.IMG_URI = Uri.fromFile(imageFile)
        FTStorageUtils.IMG_URI = FileProvider.getUriForFile(this, "com.fieldtrackingsystem.provider", imageFile);
        return Uri.fromFile(imageFile)
    }


    fun callShopVisitConfirmationDialog(storeName: String, shopId: String) {
        val addShopEntity = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)

        Handler().postDelayed(Runnable {
            if (addShopEntity != null && getFragment() != null && getFragment() !is GpsDisableFragment && forceLogoutDialog == null) {
                val userId = shopId.substring(0, shopId.indexOf("_"))
                if (/*userId == Pref.user_id &&*/ !Pref.isAutoLogout) {
                    XLog.e("=====User's shop (Dashboard Activity)========")
                    callDialog(addShopEntity, storeName, shopId)
                } else
                    XLog.e("=====Another user's shop (Dashboard Activity)========")
            }
        }, 350)

    }

    private fun callDialog(addShopDBModelEntity: AddShopDBModelEntity, storeName: String, shopId: String) {

        var popupBody = ""

        shopName = storeName
        contactNumber = AppDatabase.getDBInstance()!!.addShopEntryDao().getContactNumber(shopId)

        popupBody = if (Pref.isRevisitCaptureImage) {
            /*if (Pref.isReplaceShopText)
                "Do you want to revisit $storeName($contactNumber) customer? Take image to complete your visit"
            else
                "Do you want to revisit $storeName($contactNumber) shop? Take image to complete your visit"*/

            "Wish to Revisit the selected ${Pref.shopText} $storeName($contactNumber) now? Take image to complete your visit"

        } else {
            /*if (Pref.isReplaceShopText)
                "Do you want to revisit $storeName($contactNumber) customer?"
            else
                "Do you want to revisit $storeName($contactNumber) shop?"*/

            "Wish to Revisit the selected ${Pref.shopText} $storeName($contactNumber) now?"
        }

        /*var header = ""

        header = if (Pref.isReplaceShopText)
            "Revisit Customer"
        else
            "Revisit Shop"*/

        CommonDialog.getInstance(AppUtils.hiFirstNameText() + "!", popupBody, "NO", "YES", object : CommonDialogClickListener {
            override fun onLeftClick() {
                cancelNotification(shopId)

            }

            override fun onRightClick(editableData: String) {
                mAddShopDBModelEntity = addShopDBModelEntity
                terminateOtherShopVisit(1, addShopDBModelEntity, storeName, shopId, null, null)

                if (Pref.isShowShopVisitReason)
                    return

                startOwnShopRevisit(addShopDBModelEntity, storeName, shopId)

                /*if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity)) {
                    val photo = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val uri = Uri.parse(*//*"file:///sdcard/photo.jpg"*//*Environment.getExternalStorageDirectory().absolutePath + System.currentTimeMillis() + "_fts.jpg")
                    FTStorageUtils.IMG_URI = uri
                    photo.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(photo, PermissionHelper.REQUEST_CODE_CAMERA)
                }*/

            }

        }).show(supportFragmentManager, "CommonDialog")
    }

    private fun startOwnShopRevisit(addShopDBModelEntity: AddShopDBModelEntity, storeName: String, shopId: String) {
        cancelNotification(shopId)
        mShopId = shopId
        mStoreName = storeName
        mAddShopDBModelEntity = addShopDBModelEntity

        //loadFragment(FragType.ShopDetailFragment, true, shopId)
        //takePhotoFromCamera(PermissionHelper.REQUEST_CODE_CAMERA)
        AppUtils.isRevisit = true
        //if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity)) {
        /*intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //intent.putExtra("shopId", shopId)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
        startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)*/
        //  captureImage()
        //}
        isOtherUsersShopRevisit = false

        /*28-12-2021*/
       var shopNameByID = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopType(mShopId)!!.toString()

        if (Pref.isRevisitCaptureImage) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(Pref.IsnewleadtypeforRuby && shopNameByID.equals("2")){
                    initPermissionCheckRubyCUstomi(shopNameByID)
                }
                else if (Pref.IsnewleadtypeforRuby && !shopNameByID.equals("2")){
                    callFeedbackDialog("")
                }
                else{
                    initPermissionCheck()
                }
            }
            else{
                if(Pref.IsnewleadtypeforRuby && shopNameByID.equals("2")){
                    captureFrontImage()
                }else{
                    captureImage()
                }
            }
        } else {
            callFeedbackDialog("")
        }
    }

    private fun callFeedbackDialog(image: String) {
        revisitImage = image

        feedbackDialog = AddFeedbackSingleBtnDialog.getInstance(shopName + "\n" + contactNumber, getString(R.string.confirm_revisit), mShopId, object : AddFeedbackSingleBtnDialog.OnOkClickListener {
            override fun onOkClick(mFeedback: String, mNextVisitDate: String, filePath: String, mapproxValue: String, mprosId: String) {
                /*28-09-2021 For Gupta Power*/
                if (Pref.RevisitRemarksMandatory && !TextUtils.isEmpty(mFeedback)) {
                    if (mapproxValue != null || !mapproxValue.equals("") && (mprosId != null || !mprosId.equals(""))) {
                        feedback = mFeedback
                        nextVisitDate = mNextVisitDate
                        mFilePath = filePath
                        approxValue = mapproxValue
                        prosId = mprosId
                    } else {
                        feedback = mFeedback
                        nextVisitDate = mNextVisitDate
                        mFilePath = filePath
                    }

                    if (Pref.isFingerPrintMandatoryForVisit) {
                        if (isFingerPrintSupported) {
                            showFingerprintPopup()
                        } else {
                            revisitShop(image)
                        }
                    } else
                        revisitShop(image)
                } else {
                    if (!TextUtils.isEmpty(mFeedback))
                        feedback = mFeedback

                    if (mapproxValue != null || !mapproxValue.equals("") && (mprosId != null || !mprosId.equals(""))) {
                        feedback = mFeedback
                        nextVisitDate = mNextVisitDate
                        mFilePath = filePath
                        approxValue = mapproxValue
                        prosId = mprosId
                    } else {
                        feedback = mFeedback
                        nextVisitDate = mNextVisitDate
                        mFilePath = filePath
                    }

                    if (Pref.isFingerPrintMandatoryForVisit) {
                        if (isFingerPrintSupported)
                            showFingerprintPopup()
                        else
                            revisitShop(image)
                    } else
                        revisitShop(image)
                }
                /* if (!TextUtils.isEmpty(mFeedback))
                    feedback = mFeedback

                nextVisitDate = mNextVisitDate
                mFilePath = filePath

                if (Pref.isFingerPrintMandatoryForVisit) {
                    if (isFingerPrintSupported)
                        showFingerprintPopup()
                    else
                        revisitShop(image)
                } else
                    revisitShop(image)*/
            }

            override fun onCloseClick(mfeedback: String) {
                feedback = mfeedback
                if (Pref.isFingerPrintMandatoryForVisit) {
                    if (isFingerPrintSupported)
                        showFingerprintPopup()
                    else
                        revisitShop(image)
                } else
                    revisitShop(image)
            }

            override fun onClickCompetitorImg() {
                feedBackDialogCompetetorImg = true
            }
        })
        feedbackDialog?.show((mContext as DashboardActivity).supportFragmentManager, "AddFeedbackSingleBtnDialog")
    }

    private var fingerprintDialog: FingerprintDialog? = null
    private fun showFingerprintPopup() {
        checkForFingerPrint()

        fingerprintDialog = FingerprintDialog()
        fingerprintDialog?.show(supportFragmentManager, "")
    }

    fun callDialog(teamShop: TeamShopListDataModel) {

        var popupBody = ""

        shopName = teamShop.shop_name
        contactNumber = teamShop.shop_contact

        popupBody = if (Pref.isRevisitCaptureImage) {
            /*if (Pref.isReplaceShopText)
                "Do you want to revisit $storeName($contactNumber) customer? Take image to complete your visit"
            else
                "Do you want to revisit $storeName($contactNumber) shop? Take image to complete your visit"*/

            "Wish to Revisit the selected ${Pref.shopText} ${teamShop.shop_name}(${teamShop.shop_contact}) now? Take image to complete your visit"

        } else {
            /*if (Pref.isReplaceShopText)
                "Do you want to revisit $storeName($contactNumber) customer?"
            else
                "Do you want to revisit $storeName($contactNumber) shop?"*/

            "Wish to Revisit the selected ${Pref.shopText} ${teamShop.shop_name}(${teamShop.shop_contact}) now?"
        }

        CommonDialog.getInstance(AppUtils.hiFirstNameText() + "!", popupBody, "NO", "YES", object : CommonDialogClickListener {
            override fun onLeftClick() {
                //cancelNotification(shopId)
            }

            override fun onRightClick(editableData: String) {
                //cancelNotification(shopId)
                mAddShopDBModelEntity = AddShopDBModelEntity()
                mAddShopDBModelEntity?.shop_id = teamShop.shop_id
                terminateOtherShopVisit(2, null, "", "", teamShop, null)

                if (Pref.isShowShopVisitReason)
                    return

                callExtraTeamShopListApi(teamShop)
                //startRevisitOnlineTeamShop(teamShop)
            }

        }).show(supportFragmentManager, "CommonDialog")
    }

    private fun callExtraTeamShopListApi(teamShop: TeamShopListDataModel) {
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
                                        progress_wheel.stopSpinning()
                                        var obj = shopList.data!!.shop_list!!.filter { it.shop_id.equals(teamShop.shop_id) } as ArrayList<ShopData>
                                        if(obj.size>0){
                                            teamShop.total_visit_count = obj.get(obj.size-1).total_visit_count!!
                                        }
                                        startRevisitOnlineTeamShop(teamShop)

                                    }  else {
                                        progress_wheel.stopSpinning()
                                        startRevisitOnlineTeamShop(teamShop)
                                    }
                                }, { error ->
                                    error.printStackTrace()
                                    progress_wheel.stopSpinning()
                                    startRevisitOnlineTeamShop(teamShop)
                                })
                )
            }
        catch(ex:Exception){
                ex.printStackTrace()
            progress_wheel.stopSpinning()
            startRevisitOnlineTeamShop(teamShop)
            }
        }



    @SuppressLint("SuspiciousIndentation")
    private fun startRevisitOnlineTeamShop(teamShop: TeamShopListDataModel) {
        mShopId = teamShop.shop_id
        mStoreName = teamShop.shop_name

        mAddShopDBModelEntity = AddShopDBModelEntity()
        mAddShopDBModelEntity?.shop_id = teamShop.shop_id
        mAddShopDBModelEntity?.address = teamShop.shop_address
        mAddShopDBModelEntity?.pinCode = teamShop.shop_pincode
        mAddShopDBModelEntity?.shopName = teamShop.shop_name
        mAddShopDBModelEntity?.shopLat = teamShop.shop_lat.toDouble()
        mAddShopDBModelEntity?.shopLong = teamShop.shop_long.toDouble()
        mAddShopDBModelEntity?.isUploaded = true
        mAddShopDBModelEntity?.ownerContactNumber = teamShop.shop_contact
//        mAddShopDBModelEntity?.totalVisitCount = teamShop.total_visited
        try{
            /*Team new work*/
            if(teamShop.total_visit_count!!.equals("")){
                mAddShopDBModelEntity?.totalVisitCount = "1"
            }else{
                mAddShopDBModelEntity?.totalVisitCount = teamShop.total_visit_count!!.toString()
            }
//            mAddShopDBModelEntity?.totalVisitCount = teamShop.total_visit_count!!.toString()

        }catch(ex:Exception){
          ex.printStackTrace()
        }
        mAddShopDBModelEntity?.lastVisitedDate = teamShop.last_visit_date
        mAddShopDBModelEntity?.type = teamShop.shop_type

        if (teamShop.entity_code == null)
            mAddShopDBModelEntity?.entity_code = ""
        else
            mAddShopDBModelEntity?.entity_code = teamShop.entity_code

        if (teamShop.area_id == null)
            mAddShopDBModelEntity?.area_id = ""
        else
            mAddShopDBModelEntity?.area_id = teamShop.area_id

        if (TextUtils.isEmpty(teamShop.model_id))
            mAddShopDBModelEntity?.model_id = ""
        else
            mAddShopDBModelEntity?.model_id = teamShop.model_id

        if (TextUtils.isEmpty(teamShop.primary_app_id))
            mAddShopDBModelEntity?.primary_app_id = ""
        else
            mAddShopDBModelEntity?.primary_app_id = teamShop.primary_app_id

        if (TextUtils.isEmpty(teamShop.secondary_app_id))
            mAddShopDBModelEntity?.secondary_app_id = ""
        else
            mAddShopDBModelEntity?.secondary_app_id = teamShop.secondary_app_id

        if (TextUtils.isEmpty(teamShop.lead_id))
            mAddShopDBModelEntity?.lead_id = ""
        else
            mAddShopDBModelEntity?.lead_id = teamShop.lead_id

        if (TextUtils.isEmpty(teamShop.stage_id))
            mAddShopDBModelEntity?.stage_id = ""
        else
            mAddShopDBModelEntity?.stage_id = teamShop.stage_id

        if (TextUtils.isEmpty(teamShop.funnel_stage_id))
            mAddShopDBModelEntity?.funnel_stage_id = ""
        else
            mAddShopDBModelEntity?.funnel_stage_id = teamShop.funnel_stage_id

        if (TextUtils.isEmpty(teamShop.booking_amount))
            mAddShopDBModelEntity?.booking_amount = ""
        else
            mAddShopDBModelEntity?.booking_amount = teamShop.booking_amount


        if (TextUtils.isEmpty(teamShop.type_id))
            mAddShopDBModelEntity?.type_id = ""
        else
            mAddShopDBModelEntity?.type_id = teamShop.type_id

        /*Team new work*/
        if (TextUtils.isEmpty(teamShop.owner_name))
            mAddShopDBModelEntity?.ownerName = ""
        else
            mAddShopDBModelEntity?.ownerName = teamShop.owner_name

        /*Team new work*/
        try{
            var shopisExistTblbyThisId = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(mAddShopDBModelEntity?.shop_id)
            if(shopisExistTblbyThisId!=null)
            //delete by shop id
            AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(mAddShopDBModelEntity?.shop_id)
        }catch (ex:Exception){
            ex.printStackTrace()
        }

        mAddShopDBModelEntity?.isOwnshop = false

        AppDatabase.getDBInstance()!!.addShopEntryDao().insert(mAddShopDBModelEntity)

        AppUtils.isRevisit = true
        val userId = teamShop.shop_id.substring(0, teamShop.shop_id.indexOf("_"))
        isOtherUsersShopRevisit = userId != Pref.user_id

        if (Pref.isRevisitCaptureImage) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                initPermissionCheck()
            else
                captureImage()
        } else {
            callFeedbackDialog("")
        }
    }

    fun callDialog(teamShop: MemberShopEntity) {

        var popupBody = ""

        shopName = teamShop.shop_name!!
        contactNumber = teamShop.shop_contact!!

        popupBody = if (Pref.isRevisitCaptureImage) {
            /*if (Pref.isReplaceShopText)
                "Do you want to revisit $storeName($contactNumber) customer? Take image to complete your visit"
            else
                "Do you want to revisit $storeName($contactNumber) shop? Take image to complete your visit"*/

            "Wish to Revisit the selected ${Pref.shopText} ${teamShop.shop_name}(${teamShop.shop_contact}) now? Take image to complete your visit"

        } else {
            /*if (Pref.isReplaceShopText)
                "Do you want to revisit $storeName($contactNumber) customer?"
            else
                "Do you want to revisit $storeName($contactNumber) shop?"*/

            "Wish to Revisit the selected ${Pref.shopText} ${teamShop.shop_name}(${teamShop.shop_contact}) now?"
        }

        CommonDialog.getInstance(AppUtils.hiFirstNameText() + "!", popupBody, "NO", "YES", object : CommonDialogClickListener {
            override fun onLeftClick() {
                //cancelNotification(shopId)
            }

            override fun onRightClick(editableData: String) {
                //cancelNotification(shopId)
                mAddShopDBModelEntity = AddShopDBModelEntity()
                mAddShopDBModelEntity?.shop_id = teamShop.shop_id
                terminateOtherShopVisit(3, null, "", "", null, teamShop)

                if (Pref.isShowShopVisitReason)
                    return

                startRevisitOfflineTeamShop(teamShop)

            }

        }).show(supportFragmentManager, "CommonDialog")
    }

    private fun startRevisitOfflineTeamShop(teamShop: MemberShopEntity) {
        mShopId = teamShop.shop_id!!
        mStoreName = teamShop.shop_name!!

        mAddShopDBModelEntity = AddShopDBModelEntity()
        mAddShopDBModelEntity?.shop_id = teamShop.shop_id
        mAddShopDBModelEntity?.address = teamShop.shop_address
        mAddShopDBModelEntity?.pinCode = teamShop.shop_pincode
        mAddShopDBModelEntity?.shopName = teamShop.shop_name
        mAddShopDBModelEntity?.shopLat = teamShop.shop_lat?.toDouble()
        mAddShopDBModelEntity?.shopLong = teamShop.shop_long?.toDouble()
        mAddShopDBModelEntity?.isUploaded = true
        mAddShopDBModelEntity?.ownerContactNumber = teamShop.shop_contact
        mAddShopDBModelEntity?.totalVisitCount = teamShop.total_visited
        mAddShopDBModelEntity?.lastVisitedDate = teamShop.last_visit_date
        mAddShopDBModelEntity?.type = teamShop.shop_type

        if (teamShop.entity_code == null)
            mAddShopDBModelEntity?.entity_code = ""
        else
            mAddShopDBModelEntity?.entity_code = teamShop.entity_code


        if (teamShop.area_id == null)
            mAddShopDBModelEntity?.area_id = ""
        else
            mAddShopDBModelEntity?.area_id = teamShop.area_id

        if (TextUtils.isEmpty(teamShop.model_id))
            mAddShopDBModelEntity?.model_id = ""
        else
            mAddShopDBModelEntity?.model_id = teamShop.model_id

        if (TextUtils.isEmpty(teamShop.primary_app_id))
            mAddShopDBModelEntity?.primary_app_id = ""
        else
            mAddShopDBModelEntity?.primary_app_id = teamShop.primary_app_id

        if (TextUtils.isEmpty(teamShop.secondary_app_id))
            mAddShopDBModelEntity?.secondary_app_id = ""
        else
            mAddShopDBModelEntity?.secondary_app_id = teamShop.secondary_app_id

        if (TextUtils.isEmpty(teamShop.lead_id))
            mAddShopDBModelEntity?.lead_id = ""
        else
            mAddShopDBModelEntity?.lead_id = teamShop.lead_id

        if (TextUtils.isEmpty(teamShop.stage_id))
            mAddShopDBModelEntity?.stage_id = ""
        else
            mAddShopDBModelEntity?.stage_id = teamShop.stage_id

        if (TextUtils.isEmpty(teamShop.funnel_stage_id))
            mAddShopDBModelEntity?.funnel_stage_id = ""
        else
            mAddShopDBModelEntity?.funnel_stage_id = teamShop.funnel_stage_id

        if (TextUtils.isEmpty(teamShop.booking_amount))
            mAddShopDBModelEntity?.booking_amount = ""
        else
            mAddShopDBModelEntity?.booking_amount = teamShop.booking_amount


        if (TextUtils.isEmpty(teamShop.type_id))
            mAddShopDBModelEntity?.type_id = ""
        else
            mAddShopDBModelEntity?.type_id = teamShop.type_id


        AppDatabase.getDBInstance()!!.addShopEntryDao().insert(mAddShopDBModelEntity)

        AppUtils.isRevisit = true
        val userId = teamShop.shop_id?.substring(0, teamShop.shop_id?.indexOf("_")!!)
        isOtherUsersShopRevisit = userId != Pref.user_id

        if (Pref.isRevisitCaptureImage) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                initPermissionCheck()
            else
                captureImage()
        } else {
            callFeedbackDialog("")
        }
    }

    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                if (!isCodeScan)
                    captureImage()
                else {
                    isCodeScan = false
                    loadFragment(FragType.CodeScannerFragment, true, "")
                }
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }
    private fun initPermissionCheckRubyCUstomi(shopNameByID: String) {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                if (!isCodeScan)
                    if (Pref.IsnewleadtypeforRuby && shopNameByID.equals("2")) {
                        captureFrontImage()
                    } else {
                        captureImage()
                    }
                else {
                    isCodeScan = false
                    loadFragment(FragType.CodeScannerFragment, true, "")
                }
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun captureImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = AppUtils.createImageFile()
                // Save a file: path for use with ACTION_VIEW intents
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    val photoURI: Uri = if (Build.VERSION.SDK_INT >= 24) {
                        FileProvider.getUriForFile(mContext, mContext.packageName + ".provider", photoFile)
                    } else
                        Uri.fromFile(photoFile)
                    mCurrentPhotoPath = "file:" + photoFile.absolutePath
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 0)
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    (mContext as DashboardActivity).startActivityForResult(takePictureIntent, PermissionHelper.REQUEST_CODE_CAMERA)
                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
                ex.printStackTrace()
                return
            }
        }
    }

    fun captureFrontImage() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = AppUtils.createImageFile()
                // Save a file: path for use with ACTION_VIEW intents
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    val photoURI: Uri = if (Build.VERSION.SDK_INT >= 24) {
                        FileProvider.getUriForFile(mContext, mContext.packageName + ".provider", photoFile)
                    } else
                        Uri.fromFile(photoFile)
                    mCurrentPhotoPath = "file:" + photoFile.absolutePath
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    (mContext as DashboardActivity).startActivityForResult(takePictureIntent, PermissionHelper.REQUEST_CODE_CAMERA)
                }
            } catch (ex: Exception) {
                // Error occurred while creating the File
                ex.printStackTrace()
                return
            }
        }
    }

    fun openFileManager() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "*/*"
//        startActivityForResult(intent, REQUEST_CODE_DOCUMENT)

        browseDocuments(this@DashboardActivity, REQUEST_CODE_DOCUMENT)
    }

    fun takePhotoFromCamera(selectPicture: Int) {

        val filePath = AppUtils.getPhotoFilePath(mContext, "Image_" + System.currentTimeMillis().toString())
        AppUtils.sImagePath = filePath
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(filePath)))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        this.startActivityForResult(intent, selectPicture)
    }

    fun cancelNotification(shopId: String) {
        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(shopId.hashCode())
    }

    private fun calllogoutApi(user_id: String, session_id: String) {
        if (Pref.latitude == null || Pref.longitude == null) {
            showSnackMessage("Can't fetch location.Please wait for some time ")
            return
        }
        if (BaseActivity.isApiInitiated)
            return
        BaseActivity.isApiInitiated = true

        var location = ""

        if (Pref.latitude != "0.0" && Pref.longitude != "0.0") {
            location = LocationWizard.getAdressFromLatlng(this, Pref.latitude?.toDouble()!!, Pref.longitude?.toDouble()!!)

            if (location.contains("http"))
                location = "Unknown"
        }

        XLog.d("LOGOUT : " + "REQUEST : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
        XLog.d("==============================LOGOUT INPUT PARAMS==============================")
        XLog.d("LOGOUT : USER ID======> $user_id")
        XLog.d("LOGOUT : SESSION ID======> $session_id")
        XLog.d("LOGOUT : LAT=========> " + Pref.latitude)
        XLog.d("LOGOUT : LONG==========> " + Pref.longitude)
        XLog.d("LOGOUT : LOGOUT TIME========> " + AppUtils.getCurrentDateTime())
        XLog.d("LOGOUT : IS AUTO LOGOUT=======> 0")
        XLog.d("LOGOUT : LOCATION=========> $location")
        XLog.d("===============================================================================")

        val repository = LogoutRepositoryProvider.provideLogoutRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.logout(user_id, session_id, Pref.latitude!!, Pref.longitude!!, AppUtils.getCurrentDateTime(), "0.0", "0",
                        location)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            var logoutResponse = result as BaseResponse
                            XLog.d("LOGOUT : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() +
                                    ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                            if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                syncShopList()
                            } else if (logoutResponse.status == NetworkConstant.SESSION_MISMATCH) {
//                                clearData()
                                startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                finishAffinity()
                            } else {
                                //progress_wheel.stopSpinning()
                                showSnackMessage("Failed to logout")
                            }
                            BaseActivity.isApiInitiated = false


                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            XLog.d("LOGOUT : " + "RESPONSE ERROR: " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name +
                                    ",MESSAGE : " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(error.localizedMessage)
                        })
        )


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (supportFragmentManager.backStackEntryCount == 0 || getCurrentFragType() == FragType.DashboardFragment) {
                    AppUtils.hideSoftKeyboard(this)
                    drawerLayout.openDrawer(GravityCompat.START)
                } else {
                    onBackPressed()
                }

                return true
            }
            R.id.action_search -> {
                // Open the search view on the menu item click.
//                 search
                return true
            }
//
//

        }
        return super.onOptionsItemSelected(item)
    }

//    inner class AutoStart : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            if (intent.action == "android.intent.action.BOOT_COMPLETED") {
//                Toast.makeText(this@DashboardActivity, "LOGOUT", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    fun showProgress() {
        if (!progress_wheel.isSpinning)
            progress_wheel.spin()
    }

    fun hideProgress() {
        progress_wheel.stopSpinning()
    }

    fun fetchActivityList() {
        if (!Pref.isLocationActivitySynced) {
            val fetchLocReq = FetchLocationRequest()
            fetchLocReq.user_id = Pref.user_id
            fetchLocReq.session_token = Pref.session_token
            fetchLocReq.date_span = ""
            fetchLocReq.from_date = AppUtils.getCurrentDate()
            fetchLocReq.to_date = AppUtils.getCurrentDate()
            callFetchLocationApi(fetchLocReq)

            /*if (isTermsAndConditionsPopShow) {
                callTermsAndConditionsdApi()
            } else {
                if (!Pref.isSeenTermsConditions)
                    showTermsConditionsPopup()
            }*/


        } /*else {
            if (isTermsAndConditionsPopShow) {
                callTermsAndConditionsdApi()
            } else {
                if (!Pref.isSeenTermsConditions)
                    showTermsConditionsPopup()
            }
        }*/
    }

    private fun callFetchLocationApi(fetchLocReq: FetchLocationRequest) {
        val repository = LocationFetchRepositoryProvider.provideLocationFetchRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.fetchLocationUpdate(fetchLocReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val shopList = result as FetchLocationResponse
                            if (shopList.status == NetworkConstant.SUCCESS) {
                                convertToModelAndSave(shopList.location_details, shopList.visit_distance)

                                /*if (isTermsAndConditionsPopShow) {
                                    callTermsAndConditionsdApi()
                                }
                                else {
                                    if (!Pref.isSeenTermsConditions)
                                        showTermsConditionsPopup()
                                }*/

                            } else if (shopList.status == NetworkConstant.SESSION_MISMATCH) {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else if (shopList.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()

                                /*if (isTermsAndConditionsPopShow) {
                                    callTermsAndConditionsdApi()
                                } else {
                                    if (!Pref.isSeenTermsConditions)
                                        showTermsConditionsPopup()
                                }*/

                            } else {
                                progress_wheel.stopSpinning()
                                /*if (isTermsAndConditionsPopShow) {
                                    callTermsAndConditionsdApi()
                                } else {
                                    if (!Pref.isSeenTermsConditions)
                                        showTermsConditionsPopup()
                                }*/
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()

                            /*if (isTermsAndConditionsPopShow) {
                                callTermsAndConditionsdApi()
                            } else {
                                if (!Pref.isSeenTermsConditions)
                                    showTermsConditionsPopup()
                            }*/
                        })
        )
    }


    private fun convertToModelAndSave(location_details: List<LocationData>?, visitDistance: String) {
        if (location_details!!.isEmpty())
            return

        doAsync {
            for (i in 0 until location_details.size) {
                var localData = UserLocationDataEntity()
                if (location_details[i].latitude == null)
                    continue
                else
                    localData.latitude = location_details[i].latitude!!

                if (location_details[i].longitude == null)
                    continue
                else
                    localData.longitude = location_details[i].longitude!!

                if (location_details[i].date == null)
                    continue
                else {
                    localData.updateDate = AppUtils.changeAttendanceDateFormatToCurrent(location_details[i].date!!)
                    localData.updateDateTime = location_details[i].date!!
                }
                if (location_details[i].last_update_time == null)
                    continue
                else {
                    val str = location_details[i].last_update_time
                    localData.time = str.split(" ")[0]
                    localData.meridiem = str.split(" ")[1]
                }
                localData.isUploaded = true
                localData.minutes = "0"
                localData.hour = "0"
                if (location_details[i].distance_covered == null)
                    continue
                else
                    localData.distance = location_details[i].distance_covered!!

                if (location_details[i].shops_covered == null)
                    continue
                else
                    localData.shops = location_details[i].shops_covered!!
                if (location_details[i].location_name == null)
                    continue
                else
                    localData.locationName = location_details[i].location_name!!

                if (location_details[i].date == null)
                    continue
                else
                    localData.timestamp = AppUtils.getTimeStampFromDate(location_details[i].date!!)

                if (location_details[i].meeting_attended == null)
                    continue
                else
                    localData.meeting = location_details[i].meeting_attended!!

                if (visitDistance == null)
                    continue
                else
                    localData.visit_distance = visitDistance

                if (location_details[i].network_status == null)
                    continue
                else
                    localData.network_status = location_details[i].network_status

                if (location_details[i].battery_percentage == null)
                    continue
                else
                    localData.battery_percentage = location_details[i].battery_percentage

                XLog.d("====================Current location (Dashboard)=====================")
                XLog.d("distance=====> " + localData.distance)
                XLog.d("lat====> " + localData.latitude)
                XLog.d("long=====> " + localData.longitude)
                XLog.d("location=====> " + localData.locationName)
                XLog.d("date time=====> " + localData.updateDateTime)
                XLog.d("meeting_attended=====> " + localData.meeting)
                XLog.d("visit_distance=====> " + localData.visit_distance)
                XLog.d("network_status=====> " + localData.network_status)
                XLog.d("battery_percentage=====> " + localData.battery_percentage)

                AppDatabase.getDBInstance()!!.userLocationDataDao().insert(localData)

                XLog.d("=====================location added to db (Dashboard)======================")
            }

            uiThread {

                progress_wheel.stopSpinning()

                /*if (isTermsAndConditionsPopShow) {
                    callTermsAndConditionsdApi()
                }*/
            }
        }
    }


    fun shouldFetchLocationActivity(): Boolean {
        return (AppDatabase.getDBInstance()!!.userLocationDataDao().all.size == 0)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun updateFence() {

        if(Pref.IsLeavePressed==true && Pref.IsLeaveGPSTrack == false){
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            LocationJobService.updateFence("UPDATE_FENCE")

            val componentName = ComponentName(this, LocationJobService::class.java)
            val jobInfo = JobInfo.Builder(12, componentName)
                    //.setRequiresCharging(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    //.setRequiresDeviceIdle(true)
                    .setOverrideDeadline(1000)
                    .build()

            val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val resultCode = jobScheduler.schedule(jobInfo)

            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                XLog.d("==============================Job scheduled (Dashboard Activity)===============================")
            } else {
                XLog.d("===========================Job not scheduled (Dashboard Activity)==============================")
            }
        } else {
            val myIntent = Intent(this, LocationFuzedService::class.java)
            val bundle = Bundle();
            bundle.putString("ACTION", "UPDATE_FENCE")
            myIntent.putExtras(bundle)
            startService(myIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    public fun getShopDummyImageFile(): File {
        var bm: Bitmap? = null
        if (bm == null) {
            val bitmap = (iv_shopImage.drawable as BitmapDrawable).bitmap
            bm = bitmap
        }
        val bytes = ByteArrayOutputStream()
        bm!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        var destination =
                //File(Environment.getExternalStorageDirectory(),
                //27-09-2021
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
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

    fun generateNoteOnSD(context: Context?, sFileName: String?, sBody: String?) {
        try {
            val root =  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "xkcteamlogsample/log" + ".txt")
            if (!root.exists()) {
                root.mkdirs()
            }
            val gpxfile = File(root, sFileName)
            val writer = FileWriter(gpxfile)
            writer.append(sBody)
            writer.flush()
            writer.close()
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    public fun getDummyFile(): File {
//        var bm: Bitmap? = null
//        if (bm == null) {
//            val bitmap = (iv_shopImage.drawable as BitmapDrawable).bitmap
//            bm = bitmap
//        }
        val bytes = ByteArrayOutputStream()
//        bm!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        var destination = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "xkcteamlogsample/log")
        val fo: FileOutputStream
        val fo1:FileWriter
        try {
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

// @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle toolbar item clicks here. It'll
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        switch (id) {
//            case R.id.action_search:
//                // Open the search view on the menu item click.
//
//                searchView.openSearch();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    //Code by wasim
    private val mAlarmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onFireAlarm(intent)
        }
    }

    private fun onFireAlarm(intent: Intent) {
        if (intent != null) {
            if (intent.getParcelableExtra<AlarmData>("ALARM_DATA") != null) {
                //isFromAlarm = true
                val alaramData = intent.getParcelableExtra<AlarmData>("ALARM_DATA")
                alarmCofifDataModel = AlarmConfigDataModel()
                alarmCofifDataModel.requestCode = alaramData!!.requestCode
                alarmCofifDataModel.id = alaramData!!.id
                alarmCofifDataModel.report_id = alaramData!!.report_id
                alarmCofifDataModel.report_title = alaramData!!.report_title
                alarmCofifDataModel.alarm_time_hours = alaramData!!.alarm_time_hours
                alarmCofifDataModel.alarm_time_mins = alaramData!!.alarm_time_mins

                navigateFragmentByReportId(alarmCofifDataModel, true)

            }
        }
    }

    private fun navigateFragmentByReportId(armData: AlarmConfigDataModel, isaddedToTask: Boolean) {
        //Report ID   "1:attendance,2:Shop Visit,3:performance" Please change fragment name according to requirement
        selfieDialog?.dismiss()
        when (armData.report_id?.toInt()) {
            1 -> {
                isAttendanceFromAlarm = true
                loadFragment(FragType.AttendanceReportFragment, isaddedToTask, alarmCofifDataModel)
            }
            2 -> {
                isVisitFromAlarm = true
                loadFragment(FragType.VisitReportFragment, isaddedToTask, alarmCofifDataModel)
            }
            3 -> {
                isPerformanceFromAlarm = true
                isTodaysPerformance = true
                loadFragment(FragType.PerformanceReportFragment, isaddedToTask, alarmCofifDataModel)
            }
            4 -> {
                isPerformanceFromAlarm = true
                isTodaysPerformance = false
                loadFragment(FragType.PerformanceReportFragment, isaddedToTask, alarmCofifDataModel)
            }
            5 -> {

                try {
                    Handler().postDelayed(Runnable {

                        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout_container)

                        if (currentFragment != null && currentFragment is DailyPlanListFragment) {
                            onBackPressed()
                        }
                    }, 200)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                Handler().postDelayed(Runnable {
                    isDailyPlanFromAlarm = true
                    loadFragment(FragType.DailyPlanListFragment, isaddedToTask, alarmCofifDataModel)
                }, 450)
            }
            6 -> {
                XLog.e("=================Show selfie dialog (DashboardActivity)=================")

                if (isFromAlarm) {
                    val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout_container)
                    if (currentFragment != null && currentFragment !is DashboardFragment)
                        loadFragment(FragType.DashboardFragment, isaddedToTask, DashboardType.Home)
                    else if (currentFragment == null)
                        loadFragment(FragType.DashboardFragment, isaddedToTask, DashboardType.Home)
                }

                Pref.isSefieAlarmed = true
                Pref.reportId = armData.report_id!!
                showSelfieDialog()
            }
        }

        //cancelNotification(armData.report_id!!)
    }

    private fun showSelfieDialog() {
        selfieDialog = SelfieDialog.getInstance({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                initCameraPermissionCheck()
            else {
                captureFrontImage()
            }
        }, true)
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

    private fun uploadSelfie(file: File) {
        if (!AppUtils.isOnline(this)) {
            Toaster.msgShort(this, getString(R.string.no_internet))
            return
        }

        selfieDialog?.dismiss()


        val repository = DashboardRepoProvider.provideDashboardImgRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.alarmWithSelfie(file.absolutePath, this, Pref.reportId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result_ ->
                            val response = result_ as BaseResponse
                            showSnackMessage(response.message!!)
                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {
                                Pref.isSefieAlarmed = false
                            } else {
                                showSelfieDialog()
                            }


                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            showSnackMessage(getString(R.string.something_went_wrong))
                            showSelfieDialog()
                        })
        )
    }

    private val autoRevisit = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            try {

                if (getFragment() != null && getFragment() is DashboardFragment) {
                    (getFragment() as DashboardFragment).updateUi()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val offlineShopReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            try {

                if (getFragment() != null) {
                    if (getFragment() is OfflineAllShopListFragment)
                        (getFragment() as OfflineAllShopListFragment).updateUi()
                    else if (getFragment() is OfflineShopListFragment)
                        (getFragment() as OfflineShopListFragment).updateUi()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val idealLocReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            try {

                if (isForceLogout || !Pref.isSeenTermsConditions || !Pref.isAddAttendence || !Pref.isHomeLocAvailable)
                    return

                val startTime = AppUtils.getMeredianTimeFromDateTime(intent.getStringExtra("startTime")!!)
                val endTime = AppUtils.getMeredianTimeFromDateTime(intent.getStringExtra("endTime")!!)

                if (idealLocAlertDialog != null) {
                    idealLocAlertDialog?.dismissAllowingStateLoss()
                    idealLocAlertDialog = null
                }

                idealLocAlertDialog = CommonDialogSingleBtn.getInstance(AppUtils.hiFirstNameText() + "!", "It seeems that you are at the same nearby locations from $startTime to $endTime. Thanks.", getString(R.string.ok), object : OnDialogClickListener {
                    override fun onOkClick() {
                        if (isOrderDialogShow)
                            showOrderCollectionAlert(isOrderAdded, isCollectionAdded)
                    }
                })//.show(supportFragmentManager, "CommonDialogSingleBtn")

                idealLocAlertDialog?.show(supportFragmentManager, "CommonDialogSingleBtn")

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val fcmReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            logo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake))
        }
    }

    private val fcmReceiver_leave = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            logo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake))
        }
    }

    private val fcmReceiver_leave_status = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            logo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake))
        }
    }

    private val fcmReceiver_quotation_approval = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            logo.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.shake))
        }
    }

    private val fcmClearDataReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isClearData = true


            Log.e("DashboardActivity", "==============Open Logout from broadcast(Dashboard Activity)================")

            Handler().postDelayed(Runnable {
                if (getFragment() != null && getFragment() !is LogoutSyncFragment) {
                    if (AppUtils.isOnline(this@DashboardActivity))
                        loadFragment(FragType.LogoutSyncFragment, true, "")
                    else
                        showSnackMessage(getString(R.string.no_internet))
                }
            }, 500)
        }
    }

    private val collectionAlertReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (Pref.isAttendanceFeatureOnly)
                return

            isOrderAdded = intent.getBooleanExtra("isOrderAdded", false)
            isCollectionAdded = intent.getBooleanExtra("isCollectionAdded", false)

            isOrderDialogShow = true

            if (idealLocAlertDialog == null && Pref.isSeenTermsConditions && Pref.isAddAttendence && Pref.isHomeLocAvailable && forceLogoutDialog == null)
                showOrderCollectionAlert(isOrderAdded, isCollectionAdded)
        }
    }

    private val forceLogoutReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            showForceLogoutPopup()
        }
    }

    private val chatReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("Dashboard", "==================Chat Broadcast===============")
            AppUtils.isBroadCastRecv = true
            try {
                val notification = NotificationUtils(getString(R.string.app_name), "", "", "")
                if (getFragment() != null && getFragment() is ChatListFragment) {
                    val chatUser = intent.getSerializableExtra("chatUser") as ChatUserDataModel
                    if ((getFragment() as ChatListFragment).toID != chatUser.id) {
                        notification.msgNotification(this@DashboardActivity, intent.getStringExtra("body") as String, intent.getSerializableExtra("chatData") as ChatListDataModel,
                                chatUser)
                    }
                    else
                        (getFragment() as ChatListFragment).updateUi(intent)

                }
                else
                    notification.msgNotification(this@DashboardActivity, intent.getStringExtra("body") as String, intent.getSerializableExtra("chatData") as ChatListDataModel,
                            intent.getSerializableExtra("chatUser") as ChatUserDataModel)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val updateStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (getFragment() != null && getFragment() is ChatListFragment) {
                    (getFragment() as ChatListFragment).updateStatus()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val localeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                if (getFragment() != null && getFragment() is ChatBotFragment)
                    AppUtils.changeLanguage(this@DashboardActivity, (getFragment() as ChatBotFragment).language)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val homeLocReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                showHomeLocReasonDialog()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val revisitReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                showRevisitReasonDialog(0, null, "", "", null, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val updatePJP = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                 if(getCurrentFragType() == FragType.DashboardFragment){
                     (getFragment() as DashboardFragment).initBottomAdapter()
                 }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showHomeLocReasonDialog() {
        reasonDialog = null
        reasonDialog = ReasonDialog.getInstance(AppUtils.hiFirstNameText() + "!", "You have been detected nearby home location", reason) {
            if (!AppUtils.isOnline(this))
                Toaster.msgShort(this, getString(R.string.no_internet))
            else {
                reasonDialog?.dismiss()
                submitHomeLocReason(it)
            }
        }
        reasonDialog?.show(supportFragmentManager, "")
    }

    private fun submitHomeLocReason(mReason: String) {
        progress_wheel.spin()
        val repository = DashboardRepoProvider.provideDashboardRepository()
        BaseActivity.compositeDisposable.add(
                repository.submitHomeLocReason(mReason)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                Pref.isShowHomeLocReason = false
                                reason = ""
                                AppUtils.changeLanguage(this, "en")
                                Pref.homeLocEndTimeStamp = System.currentTimeMillis().toString()

                                if (getFragment() != null && getFragment() is ChatBotFragment)
                                    AppUtils.changeLanguage(this, (getFragment() as ChatBotFragment).language)
                            } else {
                                reason = mReason
                                showHomeLocReasonDialog()
                                Toaster.msgShort(this, result.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            reason = mReason
                            showHomeLocReasonDialog()
                            Toaster.msgShort(this, getString(R.string.something_went_wrong))
                        })
        )
    }


    override fun showForceLogoutPopup() {

        if (Pref.isAttendanceFeatureOnly)
            return

        Handler().postDelayed(Runnable {

            if (getFragment() != null && getFragment() !is LogoutSyncFragment && !Pref.isAutoLogout) {

                if (orderCollectionAlertDialog != null) {
                    orderCollectionAlertDialog?.dismissAllowingStateLoss()
                    orderCollectionAlertDialog = null
                }

                if (idealLocAlertDialog != null) {
                    idealLocAlertDialog?.dismissAllowingStateLoss()
                    idealLocAlertDialog = null
                }

                if (forceLogoutDialog != null) {
                    forceLogoutDialog?.dismissAllowingStateLoss()
                    forceLogoutDialog = null
                }

                forceLogoutDialog = CommonDialogSingleBtn.getInstance(AppUtils.hiFirstNameText() + "!", "Final logout time of the day is ${Pref.approvedOutTime}. Click on Ok to " +
                        "logout now & complete the automated data sync. Thanks.", getString(R.string.ok), object : OnDialogClickListener {

                    override fun onOkClick() {
                        if (AppUtils.isOnline(this@DashboardActivity)) {
                            isForceLogout = true
                            loadFragment(FragType.LogoutSyncFragment, true, "")
                        } else
                            Toaster.msgShort(this@DashboardActivity, getString(R.string.no_internet))
                    }
                })//.show(supportFragmentManager, "CommonDialogSingleBtn")
                forceLogoutDialog?.show(supportFragmentManager, "CommonDialogSingleBtn")
            }

        }, 200)
    }



/*    override fun HBRecorderOnError(errorCode: Int, reason: String?) {

    }

    override fun HBRecorderOnStart() {

    }

    override fun HBRecorderOnComplete() {

      *//*  val frag: DashboardFragment? = supportFragmentManager.findFragmentByTag("DashboardFragment") as DashboardFragment?
        frag!!.timerRecord(false)*//*

        var intent = Intent(mContext, ScreenRecService::class.java)
        intent.action = CustomConstants.STOP_Screen_SERVICE
        mContext.stopService(intent)

        //DashboardFragment.timerRecord(false)

        screen_record_info_TV.text="Start Screen Recorder"
        val path = hbRecorder!!.filePath
        val fileUrl = Uri.parse(path)
        val file = File(fileUrl.path)
        val uri = Uri.fromFile(file)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "video/mp4"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, "Share video using"));
    }

    private fun startRecordingScreen() {

        if(hbRecorder == null){
            hbRecorder = HBRecorder(this, this)
        }

        //DashboardFragment.cancelTimer()
        //DashboardFragment.timerRecord(true)

        hbRecorder!!.enableCustomSettings()
        hbRecorder!!.setOutputFormat("MPEG_4")
        hbRecorder!!.isAudioEnabled(false)
        hbRecorder!!.recordHDVideo(false)
        hbRecorder!!.setVideoFrameRate(20)
        hbRecorder!!.setVideoBitrate(1000000)


        val mediaProjectionManager =
                getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val permissionIntent =
                mediaProjectionManager?.createScreenCaptureIntent()
        startActivityForResult(permissionIntent, 271)
    }*/


    fun updateScreenRecStatus() {
        Log.e("xcv", "updateScreenRecStatus")
        if(DashboardFragment.isRecordRootVisible){
            screen_record_info_TV.text="Stop Recording"
        }else{
            screen_record_info_TV.text="Screen Recorder"
        }
    }

    private fun apiCallOnClearAttenReject() {  // clearing leave if isOnLeave is true
        var req : clearAttendanceonRejectReqModelRejectReqModel = clearAttendanceonRejectReqModelRejectReqModel()
        req.user_id=Pref.user_id
        req.leave_apply_date=AppUtils.getCurrentDateForShopActi()
        req.isOnLeave=true
        req.IsLeaveDelete = "1"

        val repository = LeaveTypeRepoProvider.leaveTypeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.clearAttendanceonRejectclick(req)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as BaseResponse
                    progress_wheel.stopSpinning()
                    if (response.status == NetworkConstant.SUCCESS) {
                        apiCallOnClearAttenReject1()
                    }
                }, { error ->
                    progress_wheel.stopSpinning()
                    (mContext as DashboardActivity).showSnackMessage("ERROR")
                })
        )
    }

    private fun apiCallOnClearAttenReject1() { // clearing attendance if isOnLeave is false
        var req : clearAttendanceonRejectReqModelRejectReqModel = clearAttendanceonRejectReqModelRejectReqModel()
        req.user_id=Pref.user_id
        req.leave_apply_date=AppUtils.getCurrentDateForShopActi()
        req.isOnLeave=false
        req.IsLeaveDelete = "1"

        val repository = LeaveTypeRepoProvider.leaveTypeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.clearAttendanceonRejectclick(req)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as BaseResponse
                    progress_wheel.stopSpinning()
                    if (response.status == NetworkConstant.SUCCESS) {
                        getSupervisorIDInfo()
                    }
                }, { error ->
                    progress_wheel.stopSpinning()
                    (mContext as DashboardActivity).showSnackMessage("ERROR")
                })
        )
    }

    private fun getSupervisorIDInfo(){
        try{
            progress_wheel.spin()
            val repository = AddAttendenceRepoProvider.addAttendenceRepo()
            BaseActivity.compositeDisposable.add(
                repository.getReportToUserID(Pref.user_id.toString(),Pref.session_token.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as GetReportToResponse
                        progress_wheel.stopSpinning()
                        if (response.status == NetworkConstant.SUCCESS) {
                            getSupervisorFCMInfo(response.report_to_user_id!!)
                        }

                    }, { error ->
                        XLog.d("Apply Leave Response ERROR=========> " + error.message)
                        BaseActivity.isApiInitiated = false
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
            progress_wheel.stopSpinning()
        }

    }

    private fun getSupervisorFCMInfo(usrID:String){
        try{
            progress_wheel.spin()
            val repository = AddAttendenceRepoProvider.addAttendenceRepo()
            BaseActivity.compositeDisposable.add(
                repository.getReportToFCMInfo(usrID,Pref.session_token.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as GetReportToFCMResponse
                        progress_wheel.stopSpinning()
                        if (response.status == NetworkConstant.SUCCESS) {
                            sendFCMNotiSupervisor(response.device_token!!)
                        }
                    }, { error ->
                        XLog.d("Apply Leave Response ERROR=========> " + error.message)
                        BaseActivity.isApiInitiated = false
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
            progress_wheel.stopSpinning()
        }

    }

    private fun sendFCMNotiSupervisor(superVisor_fcmToken:String){
        if (superVisor_fcmToken != "") {
            try {
                progress_wheel.spin()
                val jsonObject = JSONObject()
                val notificationBody = JSONObject()
                notificationBody.put("body","Attendance/Leave cleared by : "+Pref.user_name!!)
                notificationBody.put("flag", "flag_att_leave_clear")
                jsonObject.put("data", notificationBody)
                val jsonArray = JSONArray()
                jsonArray.put(0,superVisor_fcmToken)
                jsonObject.put("registration_ids", jsonArray)
                progress_wheel.stopSpinning()
                sendCustomNotificationForAttLeavClear(jsonObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                progress_wheel.stopSpinning()
            }
        }

    }

    fun sendCustomNotificationForAttLeavClear(notification: JSONObject) {
        progress_wheel.spin()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
            object : Response.Listener<JSONObject?> {
                override fun onResponse(response: JSONObject?) {
                    progress_wheel.stopSpinning()
                    AttendClearMsg()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    progress_wheel.stopSpinning()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getString(R.string.firebase_key)
                params["Content-Type"] = "application/json"
                return params
            }
        }

        MySingleton.getInstance(mContext)!!.addToRequestQueue(jsonObjectRequest)
    }

    fun AttendClearMsg(){
        drawerLayout.closeDrawers()
        Pref.isAddAttendence=false
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_message)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
        dialogHeader.text = "Attendance/Leave cleared for today."
        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
        })
        simpleDialog.show()
    }



    private fun syncGpsNetData() {
        val unSyncData = AppDatabase.getDBInstance()?.newGpsStatusDao()?.getNotUploaded(false)
        if (unSyncData == null || unSyncData.isEmpty()){
            rectifyUnknownLoc()
        }else{
            progress_wheel.spin()
            val gps_net_status_list = ArrayList<NewGpsStatusEntity>()
            unSyncData.forEach {
                var obj :NewGpsStatusEntity = NewGpsStatusEntity()
                obj.apply {
                    id=it.id
                    date_time = it.date_time
                    gps_service_status = it.gps_service_status
                    network_status = it.network_status
                }
                gps_net_status_list.add(obj)
            }

            var sendObj : GpsNetInputModel = GpsNetInputModel()
            sendObj.user_id = Pref.user_id!!
            sendObj.session_token = Pref.session_token!!
            sendObj.gps_net_status_list = gps_net_status_list

            val repository = LocationRepoProvider.provideLocationRepository()
            compositeDisposable.add(
                repository.gpsNetInfo(sendObj)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as BaseResponse
                        progress_wheel.stopSpinning()
                        if (response.status == NetworkConstant.SUCCESS) {
                            doAsync {
                                unSyncData.forEach {
                                    AppDatabase.getDBInstance()?.newGpsStatusDao()?.updateIsUploadedAccordingToId(true, it.id)
                                }
                                uiThread {
                                    rectifyUnknownLoc()
                                }
                            }
                        }else{
                            rectifyUnknownLoc()
                        }
                    }, { error ->
                        if (error == null) {
                            XLog.d("App Info : ERROR : " + "UNEXPECTED ERROR IN LOCATION ACTIVITY API")
                        } else {
                            XLog.d("App Info : ERROR : " + error.localizedMessage)
                            error.printStackTrace()
                        }
                        progress_wheel.stopSpinning()
                        rectifyUnknownLoc()
                    })
            )
        }

    }

    fun rectifyUnknownLoc(){
        dialogHeaderProcess.text = "Syncing Important Data. Please wait..."
        val dialogYes = simpleDialogProcess.findViewById(R.id.tv_message_ok) as AppCustomTextView
        simpleDialogProcess.show()

        try {
            doAsync {

                var unknownList = AppDatabase.getDBInstance()!!.userLocationDataDao().getUnknownLocation(AppUtils.getCurrentDateForShopActi(),"Unknown",false)
                if(unknownList.size>0){
                    for(i in 0..unknownList.size-1){
                        var updatedLoc = LocationWizard.getLocationName(this@DashboardActivity, unknownList.get(i).latitude.toDouble(), unknownList.get(i).longitude.toDouble())
                        if(!updatedLoc.equals("Unknown")){
                            AppDatabase.getDBInstance()!!.userLocationDataDao().updateUnknownLocation(unknownList.get(i).locationId.toString(),updatedLoc)
                        }
                    }
                }
                uiThread {
                    callShopDurationApi()
                }
            }

        }catch (ex:Exception){
            ex.printStackTrace()
            callShopDurationApi()
        }
    }

    private fun callShopDurationApi() {
        //dialogHeaderProcess.text = "Syncing Important Data. Please wait..."
        //val dialogYes = simpleDialogProcess.findViewById(R.id.tv_message_ok) as AppCustomTextView
        //simpleDialogProcess.show()

        var shopId = ""
        var previousShopVisitDateNumber = 0L
        var shopVisitDate = ""
        var i = 0

        if (Pref.user_id.isNullOrEmpty() || BaseActivity.isShopActivityUpdating){
            simpleDialogProcess.dismiss()
            (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
        }else{
            val syncedShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(true)
            if (syncedShopList.isEmpty()){
                simpleDialogProcess.dismiss()
                (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
            }else{
                BaseActivity.isShopActivityUpdating = true

                val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
                val syncedShop = ArrayList<ShopActivityEntity>()

                val revisitStatusList : MutableList<ShopRevisitStatusRequestData> = ArrayList()

                doAsync {

                    for (k in 0 until syncedShopList.size) {

                        if (!Pref.isMultipleVisitEnable) {
                            /* Get shop activity that has completed time duration calculation*/
                            val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(syncedShopList[k].shop_id, true, false)

                            if (shopActivity == null) {
                                val shop_activity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForTodayShop(syncedShopList[k].shop_id,true, true,
                                    AppUtils.getCurrentDateForShopActi())
                                if (shop_activity != null)
                                    syncedShop.add(shop_activity)

                            } else {
                                val shopDurationData = ShopDurationRequestData()
                                shopDurationData.shop_id = shopActivity.shopid
                                shopDurationData.spent_duration = shopActivity.duration_spent
                                shopDurationData.visited_date = shopActivity.visited_date
                                shopDurationData.visited_time = shopActivity.visited_date
                                if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid) != null)
                                    shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid).totalVisitCount
                                else
                                    shopDurationData.total_visit_count = "1"

                                if (TextUtils.isEmpty(shopActivity.distance_travelled))
                                    shopActivity.distance_travelled = "0.0"
                                shopDurationData.distance_travelled = shopActivity.distance_travelled

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
                                shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!

                                shopDurationData.updated_by = Pref.user_id
                                try{
                                    shopDurationData.updated_on = shopActivity.updated_on!!
                                }catch(ex:Exception){
                                    shopDurationData.updated_on = ""
                                }

                                if (!TextUtils.isEmpty(shopActivity.pros_id) && shopActivity.pros_id!=null)
                                    shopDurationData.pros_id = shopActivity.pros_id!!
                                else
                                    shopDurationData.pros_id = ""


                                if (!TextUtils.isEmpty(shopActivity.agency_name) && shopActivity.agency_name!=null)
                                    shopDurationData.agency_name =shopActivity.agency_name!!
                                else
                                    shopDurationData.agency_name = ""

                                if (!TextUtils.isEmpty(shopActivity.approximate_1st_billing_value) && shopActivity.approximate_1st_billing_value!=null)
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
                                var revisitStatusObj= ShopRevisitStatusRequestData()
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
                        else {
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationCalculatedStatusByShopID(syncedShopList[k].shop_id.toString(),true,AppUtils.getCurrentDateForShopActi())
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
                                shopDurationData.shop_revisit_uniqKey=it.shop_revisit_uniqKey!!

                                shopDurationData.updated_by = Pref.user_id
                                try {
                                    shopDurationData.updated_on = it.updated_on!!
                                }
                                catch(ex:Exception){
                                    shopDurationData.updated_on = ""
                                }

                                if (!TextUtils.isEmpty(it.pros_id!!))
                                    shopDurationData.pros_id = it.pros_id!!
                                else
                                    shopDurationData.pros_id = ""

                                if (!TextUtils.isEmpty(it.agency_name!!))
                                    shopDurationData.agency_name =it.agency_name!!
                                else
                                    shopDurationData.agency_name = ""

                                if (!TextUtils.isEmpty(it.approximate_1st_billing_value))
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
                                var revisitStatusObj= ShopRevisitStatusRequestData()
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

                    uiThread {
                        if (shopDataList.isEmpty()) {
                            BaseActivity.isShopActivityUpdating = false
                            simpleDialogProcess.dismiss()
                            (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                        } else {
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

                            XLog.d("callShopDurationApi : REQUEST")

                            compositeDisposable.add(
                                repository.shopDuration(shopDurationApiReq)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        XLog.d("callShopDurationApi : RESPONSE " + result.status)
                                        if (result.status == NetworkConstant.SUCCESS) {

                                            if(!revisitStatusList.isEmpty()){
                                                callRevisitStatusUploadApi(revisitStatusList!!)
                                            }


                                            if (newShopList.size > 0) {
                                                for (i in 0 until newShopList.size) {
                                                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, newShopList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(newShopList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                                }
                                                BaseActivity.isShopActivityUpdating = false
                                                syncShopVisitImage(newShopList)
                                            } else {

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

                                                     syncShopVisitImage(shopDataList)
                                                }
                                            }
                                            BaseActivity.isShopActivityUpdating = false
                                            simpleDialogProcess.dismiss()
                                            (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                        }else{
                                            BaseActivity.isShopActivityUpdating = false
                                            simpleDialogProcess.dismiss()
                                            (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                        }
                                        BaseActivity.isShopActivityUpdating = false
                                    }, { error ->
                                        BaseActivity.isShopActivityUpdating = false
                                        simpleDialogProcess.dismiss()
                                        (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                        if (error == null) {
                                            XLog.d("callShopDurationApi : ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                                        } else {
                                            XLog.d("callShopDurationApi : ERROR " + error.localizedMessage)
                                            error.printStackTrace()
                                        }
                                    })
                            )
                        }
                    }
                }
            }
        }
    }

    private var mShopDataList: MutableList<ShopDurationRequestData>? = null
    private fun syncShopVisitImage(shopDataList: MutableList<ShopDurationRequestData>) {
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
                                //checkToCallAudioApi()
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            BaseActivity.isShopActivityUpdating = false
                            //checkToCallSyncOrder()
                            //checkToRetryVisitButton()
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        XLog.d("UPLOAD REVISIT ALL IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                        error.printStackTrace()
                        BaseActivity.isShopActivityUpdating = false
                        //checkToCallSyncOrder()
                        //checkToRetryVisitButton()
                    })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            progress_wheel.stopSpinning()
            BaseActivity.isShopActivityUpdating = false
            //checkToCallSyncOrder()
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


}

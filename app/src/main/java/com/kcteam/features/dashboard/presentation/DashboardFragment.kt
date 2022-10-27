package com.kcteam.features.dashboard.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomConstants
import com.kcteam.CustomStatic
import com.kcteam.Customdialog.CustomDialog
import com.kcteam.Customdialog.OnDialogCustomClickListener
import com.kcteam.R
import com.kcteam.ScreenRecService
import com.kcteam.app.*
import com.kcteam.app.domain.*
import com.kcteam.app.types.DashboardType
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.*
import com.kcteam.app.widgets.MovableFloatingActionButton
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.faceRec.DetectorActivity
import com.kcteam.faceRec.FaceStartActivity
import com.kcteam.faceRec.tflite.SimilarityClassifier
import com.kcteam.faceRec.tflite.TFLiteObjectDetectionAPIModel
import com.kcteam.features.DecimalDigitsInputFilter
import com.kcteam.features.SearchLocation.locationInfoModel
import com.kcteam.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.kcteam.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.AssignedToShopListResponseModel
import com.kcteam.features.addshop.model.BeatListResponseModel
import com.kcteam.features.addshop.model.EntityResponseModel
import com.kcteam.features.addshop.model.PartyStatusResponseModel
import com.kcteam.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.kcteam.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.kcteam.features.alarm.api.attendance_report_list_api.AttendanceReportRepoProvider
import com.kcteam.features.alarm.model.AlarmData
import com.kcteam.features.alarm.model.AttendanceReportDataModel
import com.kcteam.features.alarm.presetation.AttendanceReportAdapter
import com.kcteam.features.averageshop.api.ShopActivityRepositoryProvider
import com.kcteam.features.averageshop.business.InfoWizard
import com.kcteam.features.averageshop.model.ShopActivityRequest
import com.kcteam.features.averageshop.model.ShopActivityResponse
import com.kcteam.features.averageshop.model.ShopActivityResponseDataList
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.commondialogsinglebtn.CommonDialogSingleBtn
import com.kcteam.features.commondialogsinglebtn.OnDialogClickListener
import com.kcteam.features.dashboard.presentation.api.dashboardApi.DashboardRepoProvider
import com.kcteam.features.dashboard.presentation.api.dayStartEnd.DayStartEndRepoProvider
import com.kcteam.features.dashboard.presentation.api.gteroutelistapi.GetRouteListRepoProvider
import com.kcteam.features.dashboard.presentation.api.submithomeloc.SubmitHomeLocationRepoProvider
import com.kcteam.features.dashboard.presentation.model.DaystartDayendRequest
import com.kcteam.features.dashboard.presentation.model.SelectedRouteListResponseModel
import com.kcteam.features.dashboard.presentation.model.StatusDayStartEnd
import com.kcteam.features.dashboard.presentation.model.SubmitHomeLocationInputModel
import com.kcteam.features.document.api.DocumentRepoProvider
import com.kcteam.features.document.model.DocumentListResponseModel
import com.kcteam.features.document.model.DocumentTypeResponseModel
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.SingleShotLocationProvider
import com.kcteam.features.location.UserLocationDataEntity
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.model.VisitRemarksResponseModel
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.api.LoginRepositoryProvider
import com.kcteam.features.login.api.alarmconfigapi.AlarmConfigRepoProvider
import com.kcteam.features.login.api.global_config.ConfigFetchRepoProvider
import com.kcteam.features.login.api.productlistapi.ProductListRepoProvider
import com.kcteam.features.login.api.user_config.UserConfigRepoProvider
import com.kcteam.features.login.model.alarmconfigmodel.AlarmConfigResponseModel
import com.kcteam.features.login.model.globalconfig.ConfigFetchResponseModel
import com.kcteam.features.login.model.mettingListModel.MeetingListResponseModel
import com.kcteam.features.login.model.productlistmodel.ProductListResponseModel
import com.kcteam.features.login.model.userconfig.UserConfigResponseModel
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamAreaListResponseModel
import com.kcteam.features.member.model.TeamListResponseModel
import com.kcteam.features.member.model.TeamShopListResponseModel
import com.kcteam.features.member.model.UserPjpResponseModel
import com.kcteam.features.nearbyshops.api.ShopListRepositoryProvider
import com.kcteam.features.nearbyshops.model.*
import com.kcteam.features.photoReg.api.GetUserListPhotoRegProvider
import com.kcteam.features.photoReg.model.UserFacePicUrlResponse
import com.kcteam.features.report.presentation.ReportAdapter
import com.kcteam.features.timesheet.api.TimeSheetRepoProvider
import com.kcteam.features.timesheet.model.TimeSheetConfigResponseModel
import com.kcteam.features.timesheet.model.TimeSheetDropDownResponseModel
import com.kcteam.mappackage.SendBrod
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.hbisoft.hbrecorder.HBRecorder
import com.hbisoft.hbrecorder.HBRecorderListener
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by rp : 31-10-2017:16:49
 */
class DashboardFragment : BaseFragment(), View.OnClickListener, HBRecorderListener, View.OnTouchListener {

    var dX = 0f
    var dY = 0f
    var lastAction = 0


    private lateinit var fab: FloatingActionButton
    private lateinit var mContext: Context
    private lateinit var mRouteActivityDashboardAdapter: RouteActivityDashboardAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var history_ll: LinearLayout
    private lateinit var shop_ll: LinearLayout
    private lateinit var attandance_ll: LinearLayout
    private lateinit var order_ll: LinearLayout
    private var mFragment: DashboardType = DashboardType.Home
    private lateinit var reportList: RecyclerView
    private lateinit var adapter: ReportAdapter
    private lateinit var list: ArrayList<AddShopDBModelEntity>
    private lateinit var avgTime: AppCustomTextView
    private lateinit var avgShop: AppCustomTextView
    private lateinit var avgOrder: AppCustomTextView
//    private lateinit var shops_RL: RelativeLayout
//    private lateinit var time_RL: RelativeLayout
//    private lateinit var price_RL: RelativeLayout
    private lateinit var best_performing_shop_TV: AppCustomTextView
    private lateinit var no_shop_tv: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_view_all: AppCustomTextView
    private lateinit var rv_pjp_list: RecyclerView
    private lateinit var rl_dashboard_fragment_main: RelativeLayout
    private lateinit var tv_shop: TextView
    private lateinit var tv_order: TextView
    private lateinit var no_of_order_TV: AppCustomTextView
    private lateinit var iv_order_icon: ImageView
    private lateinit var iv_quot_icon: ImageView
    private lateinit var fab_bot: MovableFloatingActionButton

    private lateinit var ll_attendance_report_main: LinearLayout
    private lateinit var rv_attendance_report_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var tv_pick_date_range: AppCustomTextView
    private lateinit var progress_wheel_attendance: ProgressWheel
    private lateinit var shop_tome_order_tab_LL: HorizontalScrollView

    lateinit var cancel_timer: TextView
    lateinit var pause_record: TextView

    private lateinit var n_shops_TV: AppCustomTextView
    private lateinit var no_of_shop_TV: AppCustomTextView

    private lateinit var n_time_TV: AppCustomTextView
    private lateinit var no_of_time_TV: AppCustomTextView


    private lateinit var start_TV: AppCustomTextView
    private lateinit var end_TV: AppCustomTextView
    private var isCameraDayStart: Boolean = false

    private var reasonDialog: ReasonDialog? = null
    private var reason = ""

    private lateinit var DDVisit_TV: AppCustomTextView
    private lateinit var DDVisitdate_TV: AppCustomTextView


    private lateinit var DDVisit_Rl: FrameLayout




    private lateinit var StartRL: FrameLayout
    private lateinit var endRL: FrameLayout

    private lateinit var start_shop: AppCustomTextView
    private lateinit var enddate_TV: AppCustomTextView


    private var isStartCall:Boolean=false
    private var isCalledFromStart:Boolean=false
    private var isEndCall:Boolean=false

    /*horizontal scroll 01-10-2021*/
    private lateinit var ll_dash_total_visit_newD  : LinearLayout
    private lateinit var ll_dash_visit_duration_newD   : LinearLayout
    private lateinit var ll_dash_total_order_newD   : LinearLayout


    private lateinit var ll_dash_day_start_newD   : LinearLayout
    private lateinit var ll_dash_point_visit_newD   : LinearLayout
    private lateinit var ll_dash_day_end_newD   : LinearLayout





    private val customProgressDialog: CustomProgressDialog by lazy {
        CustomProgressDialog.getInstance()
    }

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    fun getInstance(objects: Any): DashboardFragment {
        val cardFragment = DashboardFragment()
        mFragment = objects as DashboardType
        return cardFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_dashboard_new, container, false)
        //saheli added 27-07-21
        AppUtils.changeLanguage(mContext, "en")


        println("dash_test_check initView begin "+AppUtils.getCurrentDateTime()  );
        initView(view)
        println("dash_test_check initView end "+AppUtils.getCurrentDateTime()  );


        if (Pref.isAttendanceFeatureOnly) {

            /*Handler().postDelayed(Runnable {
                getAttendanceReport(AppUtils.getCurrentDateForShopActi())
            }, 500)*/
        } else
            getShopActivityList()

        return view
    }

    private fun getShopActivityList() {
        println("dash_test_check getShopActivityList begin "+AppUtils.getCurrentDateTime()  );
        if (AppDatabase.getDBInstance()!!.shopActivityDao().getAll().isEmpty()) {
            Handler().postDelayed(Runnable {
                callShopActivityApi()
            }, 1500)
        } else
            checkToCallMemberList()
    }

    private fun callShopActivityApi() {
        var shopActivity = ShopActivityRequest()
        shopActivity.user_id = Pref.user_id
        shopActivity.session_token = Pref.session_token
        shopActivity.date_span = "30"
        shopActivity.from_date = ""
        shopActivity.to_date = ""
        val repository = ShopActivityRepositoryProvider.provideShopActivityRepository()

        BaseActivity.compositeDisposable.add(
                //repository.fetchShopActivity(shopActivity)
                repository.fetchShopActivitynew(Pref.session_token!!, Pref.user_id!!, "30", "", "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            var shopActityResponse = result as ShopActivityResponse
                            if (shopActityResponse.status == "200") {
                                updateShopTableInDB(shopActityResponse.date_list)
                                var list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                var totalMinute = InfoWizard.getTotalShopVisitTimeForActi()
                                Pref.totalShopVisited = (Pref.totalShopVisited.toInt() - list.size).toString()
                                Pref.totalTimeSpenAtShop = (Pref.totalTimeSpenAtShop.toInt() - totalMinute).toString()

                                val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()
                                XLog.e("=======RESPONSE FROM SHOP ACTIVITY API (DASHBOARD FRAGMENT)=======")
                                XLog.e("Today's Shop Visit Count====> $todaysShopVisitCount")

                                avgShop.text = todaysShopVisitCount
                                avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

                                when {
                                    Pref.willActivityShow -> avgOrder.text = InfoWizard.getActivityForToday()
                                    Pref.isQuotationShow -> avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
                                    else -> avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
                                }

//                                (mContext as DashboardActivity).showSnackMessage(result.message!!)
                            } else {
//                                (mContext as DashboardActivity).showSnackMessage(result.message!!)
                                //TODO SNACK MESSAGE

                            }

                            initBottomAdapter()
                            checkToCallMemberList()
                            println("dash_test_check getShopActivityList end "+AppUtils.getCurrentDateTime()  );
                            (mContext as DashboardActivity).takeActionOnGeofence()
                        }, { error ->
                            error.printStackTrace()
                            //(mContext as DashboardActivity).showSnackMessage("ERROR")
                            //TODO SNACK MESSAGE
                            initBottomAdapter()
                            checkToCallMemberList()
                            (mContext as DashboardActivity).takeActionOnGeofence()
                        })
        )
    }

    private fun updateShopTableInDB(date_list: List<ShopActivityResponseDataList>?) {
        for (i in date_list!!.indices) {
            for (j in 0 until date_list[i].shop_list!!.size) {
                var shopActivityEntity = ShopActivityEntity()
                shopActivityEntity.shopid = date_list[i].shop_list!![j].shopid
                shopActivityEntity.shop_name = date_list[i].shop_list!![j].shop_name
                shopActivityEntity.shop_address = date_list[i].shop_list!![j].shop_address
                shopActivityEntity.date = date_list[i].shop_list!![j].date
                if (date_list[i].shop_list!![j].duration_spent!!.contains("."))
                    shopActivityEntity.duration_spent = date_list[i].shop_list!![j].duration_spent!!.split(".")[0]
                else
                    shopActivityEntity.duration_spent = date_list[i].shop_list!![j].duration_spent!!
                shopActivityEntity.totalMinute = AppUtils.convertMinuteFromHHMMSS(shopActivityEntity.duration_spent)

                if (!TextUtils.isEmpty(date_list[i].shop_list!![j].start_timestamp))
                    shopActivityEntity.startTimeStamp = date_list[i].shop_list!![j].start_timestamp!!
                else
                    shopActivityEntity.startTimeStamp = "0"

                shopActivityEntity.endTimeStamp = "0"
                shopActivityEntity.visited_date = date_list[i].shop_list!![j].visited_date
                shopActivityEntity.isUploaded = true
                shopActivityEntity.isVisited = true
                shopActivityEntity.isDurationCalculated = true
                shopActivityEntity.isFirstShopVisited = false
                shopActivityEntity.distance_from_home_loc = ""

                shopActivityEntity.device_model = date_list[i].shop_list!![j].device_model
                shopActivityEntity.android_version = date_list[i].shop_list!![j].android_version
                shopActivityEntity.battery = date_list[i].shop_list!![j].battery
                shopActivityEntity.net_status = date_list[i].shop_list!![j].net_status
                shopActivityEntity.net_type = date_list[i].shop_list!![j].net_type

                shopActivityEntity.in_time = date_list[i].shop_list!![j].in_time
                shopActivityEntity.out_time = date_list[i].shop_list!![j].out_time

                shopActivityEntity.in_loc = date_list[i].shop_list!![j].in_location
                shopActivityEntity.out_loc = date_list[i].shop_list!![j].out_location
                shopActivityEntity.shop_revisit_uniqKey = date_list[i].shop_list!![j].Key!!

                shopActivityEntity.agency_name=date_list[i].shop_list!![j].agency_name
                shopActivityEntity.pros_id=date_list[i].shop_list!![j].pros_id

                AppDatabase.getDBInstance()!!.shopActivityDao().insertAll(shopActivityEntity)
            }
        }

    }

    override fun onResume() {
        super.onResume()

        if (hbRecorder != null) {
            if (hbRecorder!!.isBusyRecording) {
                iv_screen_status.setImageResource(R.drawable.green_round)
                pause_record.visibility = View.VISIBLE
                if (hbRecorder!!.isRecordingPaused) {
                    pause_record.text = ">"
                } else {
                    pause_record.text = "||"
                }
            } else {
                pause_record.visibility = View.GONE
                iv_screen_status.setImageResource(R.drawable.red_circle)
            }
        } else {
            pause_record.visibility = View.GONE
            iv_screen_status.setImageResource(R.drawable.red_circle)
        }



        (mContext as DashboardActivity).teamHierarchy.clear()

        val f = mContext.getDatabasePath(AppConstant.DBNAME)
        val dbSize = f.length()
        val dbSizeInKB = dbSize / 1024
        XLog.e("Original DataBase Size====> $dbSize")
        XLog.e("DataBase Size====> $dbSizeInKB KB")

        //checkToCallMemberList()
        writeDataToFile()
        println("on Resume " +AppUtils.getCurrentDateTime());

        /*if(Pref.IsActivateNewOrderScreenwithSize){
            var rateListToday= AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getRateListByDate(AppUtils.getCurrentDateyymmdd()) as List<String>
            var sum = 0.0
            for(i in 0..rateListToday.size-1){
                sum+=rateListToday.get(i).toDouble()
            }
            avgOrder.text= getString(R.string.rupee_symbol) + sum.toString()
        }*/

        updateOrdAmtForNewOrd()
    }

    fun updateOrdAmtForNewOrd(){
        if(Pref.IsActivateNewOrderScreenwithSize){
            var rateListToday= AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getRateListByDate(AppUtils.getCurrentDateyymmdd()) as List<NewOrderScrOrderEntity>
            var sum = 0.0
            for(i in 0..rateListToday.size-1){
                sum+=rateListToday.get(i).qty!!.toDouble() * rateListToday.get(i).rate!!.toDouble()
            }
            avgOrder.text= getString(R.string.rupee_symbol) + String.format("%.02f",sum)
        }
    }



    private fun writeDataToFile() {

        val list = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi())

        val company = JSONArray()

        for (i in 0 until list.size) {
            if (list[i].latitude == null || list[i].longitude == null)
                continue
            val jsonObject = JSONObject()
            jsonObject.put("locationId", list[i].locationId)
            jsonObject.put("latitude", list[i].latitude)
            jsonObject.put("longitude", list[i].longitude)
            jsonObject.put("distance_covered", list[i].distance)
            jsonObject.put("location_name", list[i].locationName)
            jsonObject.put("timestamp", list[i].timestamp)
            jsonObject.put("time", list[i].time)
            jsonObject.put("hours", list[i].hour)
            jsonObject.put("minutes", list[i].minutes)
            jsonObject.put("shops", list[i].shops)
            jsonObject.put("meridiem", list[i].meridiem)
            jsonObject.put("isUploaded", list[i].isUploaded)
            jsonObject.put("updateDate", list[i].updateDate)
            jsonObject.put("updateDateTime", list[i].updateDateTime)
            company.put(jsonObject)
        }

        val accurateObject = JSONObject()
        accurateObject.put("accurate_locations", company)

        /*val parentObject = JSONObject()
        parentObject.put("accurate_loc", accurateObject)*/

        try {
            var output: Writer? = null
            val folderPath = FTStorageUtils.getFolderPath(mContext)
            val file = File("$folderPath/FTS_Todays_Accurate_Location.txt")
            if (file.exists()) {
                Log.e("Location List", "File deleted")
                file.delete()
            }
            output = BufferedWriter(FileWriter(file))
            output.write(accurateObject.toString())
            output.close()
            Log.e("Location list", "Value saved")

            val length = file.length()
            val lengthInKB = length / 1024

            XLog.e("Original today accurate table Size====> $length")
            XLog.e("Today accurate table Size====> $lengthInKB KB")

        } catch (e: Exception) {
              e.printStackTrace()
        }

        if (AppUtils.isOnline(mContext)) {
            if(CustomStatic.IsPJPAddEdited){
                CustomStatic.IsPJPAddEdited=false
                //Toaster.msgShort(mContext,"DashboardFragment getOnlyPjpListApi() call")
                if(Pref.isActivatePJPFeature){
                    XLog.d("API_Optimization-> GET getPjpListApi : enable check->writeDataToFile" +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
                    getOnlyPjpListApi()
                }else{
                    XLog.d("API_Optimization-> GET getPjpListApi : disable check->writeDataToFile" +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
                }
            }else{
                //Toaster.msgShort(mContext,"DashboardFragment getOnlyPjpListApi() not call")
            }
        }

    }

    fun getOnlyPjpListApi() {
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        AppDatabase.getDBInstance()?.pjpListDao()?.deleteAll()

        progress_wheel?.spin()
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
                                            initBottomAdapter()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    initBottomAdapter()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                initBottomAdapter()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET USER PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            initBottomAdapter()
                        })
        )
    }


    override fun updateUI(any: Any) {
        super.updateUI(any)

        val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()
        XLog.e("=======UPDATE UI (DASHBOARD FRAGMENT)=======")
        XLog.e("Today's Shop Visit Count====> $todaysShopVisitCount")

        avgShop.text = todaysShopVisitCount
        avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

        when {
            Pref.willActivityShow -> avgOrder.text = InfoWizard.getActivityForToday()
            Pref.isQuotationShow -> avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
            else -> avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
        }

        UpdateLocationData()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @SuppressLint("UseRequireInsteadOfGet", "RestrictedApi")
    private fun initView(view: View?) {

        cancel_timer = view!!.findViewById(R.id.cancel_timer)
        iv_screen_status = view!!.findViewById(R.id.iv_screen_status)
        tv_timer = view!!.findViewById(R.id.tv_timer)
        pause_record = view!!.findViewById(R.id.pause_record)
        ll_recorder_root = view!!.findViewById(R.id.ll_recorder_root)
        ll_recorder_root.setOnTouchListener(this);

        cancel_timer.setOnClickListener(this)
        iv_screen_status.setOnClickListener(this)
        pause_record.setOnClickListener(this)

        if (isRecordRootVisible) {
            ll_recorder_root.visibility = View.VISIBLE
        } else {
            ll_recorder_root.visibility = View.GONE
        }

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.custom_toolbar_back,mContext.theme))
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.custom_toolbar_back))
        }*/


        fab = view!!.findViewById(R.id.fab)
        fab.setOnClickListener(this)
        history_ll = view.findViewById(R.id.history_ll)
        shop_ll = view.findViewById(R.id.shop_ll)
        attandance_ll = view.findViewById(R.id.attandance_ll)
        order_ll = view.findViewById(R.id.order_ll)
        reportList = view.findViewById(R.id.report_RCV)
        avgTime = view.findViewById(R.id.n_time_TV)
        avgShop = view.findViewById(R.id.n_shops_TV)
        avgOrder = view.findViewById(R.id.n_order_TV)
        avgOrder.text = getString(R.string.rupee_symbol) + "0.00"
//        shops_RL = view.findViewById(R.id.shops_RL)
//        time_RL = view.findViewById(R.id.time_RL)
//        price_RL = view.findViewById(R.id.price_RL)
        best_performing_shop_TV = view.findViewById(R.id.best_performing_shop_TV)
        no_shop_tv = view.findViewById(R.id.no_shop_tv)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        tv_view_all = view.findViewById(R.id.tv_view_all)
        rl_dashboard_fragment_main = view.findViewById(R.id.rl_dashboard_fragment_main)
        ll_attendance_report_main = view.findViewById(R.id.ll_attendance_report_main)

        rv_pjp_list = view.findViewById(R.id.rv_pjp_list)
        rv_pjp_list.layoutManager = LinearLayoutManager(mContext)

        rv_attendance_report_list = view.findViewById(R.id.rv_attendance_report_list)
        rv_attendance_report_list.layoutManager = LinearLayoutManager(mContext)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        tv_pick_date_range = view.findViewById(R.id.tv_pick_date_range)
        progress_wheel_attendance = view.findViewById(R.id.progress_wheel_attendance)
        progress_wheel_attendance.stopSpinning()
        tv_shop = view.findViewById(R.id.tv_shop)
        tv_order = view.findViewById(R.id.tv_order)
        no_of_order_TV = view.findViewById(R.id.no_of_order_TV)
        iv_order_icon = view.findViewById(R.id.iv_order_icon)
        iv_quot_icon = view.findViewById(R.id.iv_quot_icon)
        fab_bot = view.findViewById(R.id.fab_bot)

        shop_tome_order_tab_LL = view.findViewById(R.id.shop_tome_order_tab_LL)


        n_shops_TV = view.findViewById(R.id.n_shops_TV)
        no_of_shop_TV = view.findViewById(R.id.no_of_shop_TV)

        n_time_TV = view.findViewById(R.id.n_time_TV)
        no_of_time_TV = view.findViewById(R.id.no_of_time_TV)


        start_TV = view.findViewById(R.id.start_TV)
        end_TV = view.findViewById(R.id.end_TV)

        DDVisit_TV =  view.findViewById(R.id.DDVisit_TV)
        DDVisit_Rl =  view.findViewById(R.id.DDVisitRL)
        DDVisitdate_TV =  view.findViewById(R.id.DDVisitdate_TV)

        StartRL = view.findViewById(R.id.StartRL)
        endRL= view.findViewById(R.id.endRL)

        start_shop = view.findViewById(R.id.start_shop)
        enddate_TV = view.findViewById(R.id.enddate_TV)


    /*horizontal scroll 01-10-2021*/
        ll_dash_total_visit_newD   = view.findViewById(R.id.ll_dash_total_visit_newD)
        ll_dash_visit_duration_newD    = view.findViewById(R.id.ll_dash_visit_duration_newD)
        ll_dash_total_order_newD    = view.findViewById(R.id.ll_dash_total_order_newD)
        ll_dash_day_start_newD    = view.findViewById(R.id.ll_dash_day_start_newD)
        ll_dash_point_visit_newD    = view.findViewById(R.id.ll_dash_point_visit_newD)
        ll_dash_day_end_newD    = view.findViewById(R.id.ll_dash_day_end_newD)


//        fab.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_toolbar_back))

        start_TV.setOnClickListener({ view ->

            CustomStatic.FaceDetectionAccuracyLower=Pref.FaceDetectionAccuracyLower

            if(Pref.BatterySettingGlobal && Pref.BatterySetting ){
                if(AppUtils.getBatteryPercentage(mContext).toInt()<=15){
                    CustomDialog.getInstance(AppUtils.hiFirstNameText(),getString(R.string.battery_setting_message),"OK","", "0",object : OnDialogCustomClickListener {
                        override fun onOkClick() {

                            startTvClick()

                        }
                        override fun onYesClick() {

                        }
                        override fun onNoClick() {
                        }
                    }).show((mContext as DashboardActivity).supportFragmentManager, "CustomDialog")
                }else{
                    startTvClick()
                }
            }else{
                startTvClick()
            }




        })


        end_TV.setOnClickListener({ view ->

            CustomStatic.FaceDetectionAccuracyLower=Pref.FaceDetectionAccuracyLower

            if(Pref.BatterySettingGlobal && Pref.BatterySetting ){
                if(AppUtils.getBatteryPercentage(mContext).toInt()<=15){
                    CustomDialog.getInstance(AppUtils.hiFirstNameText(),getString(R.string.battery_setting_message),"OK","", "0",object : OnDialogCustomClickListener {
                        override fun onOkClick() {

                            endTvClick()

                        }
                        override fun onYesClick() {

                        }
                        override fun onNoClick() {
                        }
                    }).show((mContext as DashboardActivity).supportFragmentManager, "CustomDialog")
                }else{
                    endTvClick()
                }
            }else{
                endTvClick()
            }




        })

        DDVisit_TV.setOnClickListener({ view ->
            if(Pref.BatterySettingGlobal && Pref.BatterySetting ){
                if(AppUtils.getBatteryPercentage(mContext).toInt()<=15){
                    CustomDialog.getInstance(AppUtils.hiFirstNameText(),getString(R.string.battery_setting_message),"OK","", "0",object : OnDialogCustomClickListener {
                        override fun onOkClick() {
                            pointTvClick()
                        }
                        override fun onYesClick() {

                        }
                        override fun onNoClick() {
                        }
                    }).show((mContext as DashboardActivity).supportFragmentManager, "CustomDialog")
                }else{
                    pointTvClick()
                }
            }else{
                pointTvClick()
            }

        })

        println("dash_test_check facedetect begin "+AppUtils.getCurrentDateTime()  );
        faceDetectorSetUp()
        println("dash_test_check facedetect end "+AppUtils.getCurrentDateTime()  );

        /*if (Pref.isReplaceShopText)
            tv_shop.text = getString(R.string.customers)
        else
            tv_shop.text = getString(R.string.shops)*/

        tv_shop.text = Pref.shopText + "(s)"

        if (Pref.willShowUpdateDayPlan)
            tv_view_all.visibility = View.VISIBLE
        else
            tv_view_all.visibility = View.GONE

        val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()
        XLog.e("=======INIT VIEW (DASHBOARD FRAGMENT)=======")
        XLog.e("Today's Shop Visit Count====> $todaysShopVisitCount")

        avgShop.text = todaysShopVisitCount
        avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

        history_ll.setOnClickListener(this)
        shop_ll.setOnClickListener(this)
        attandance_ll.setOnClickListener(this)
        order_ll.setOnClickListener(this)
//        shops_RL.setOnClickListener(this)
//        time_RL.setOnClickListener(this)
//        price_RL.setOnClickListener(this)
        ll_dash_total_order_newD.setOnClickListener(this)

        tv_view_all.setOnClickListener(this)
        tv_pick_date_range.setOnClickListener(this)
        n_shops_TV.setOnClickListener(this)
        n_time_TV.setOnClickListener(this)

        fab_bot.setCustomClickListener {
            (mContext as DashboardActivity).showLanguageAlert(false)
        }

//        best_performing_shop_TV.text = getString(R.string.todays_task)

        best_performing_shop_TV.text = "${Pref.TodaysTaskText}"

        tv_pick_date_range.text = AppUtils.getFormattedDate(myCalendar.time)

        //addShopDBModel()

        /*val list=AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingDate(AppUtils.getCurrentDate())

        if (list==null || list.isEmpty()) {
            avgOrder.text = "0"
        }
        else {
            var totalAmount = 0.0

            for (i in list.indices) {
                totalAmount += list[i].total_price?.toDouble()!!
            }
            //val totalPrice = DecimalFormat("##.##").format(totalAmount)
            val totalPrice = String.format("%.2f", totalAmount.toFloat())
            itemView.tv_total_amount.text = context.getString(R.string.rupee_symbol)+totalPrice
        }*/

        if (Pref.isAttendanceFeatureOnly) {
            ll_attendance_report_main.visibility = View.VISIBLE
            rl_dashboard_fragment_main.visibility = View.GONE

            //getAttendanceReport(AppUtils.getCurrentDateForShopActi())

        } else {
            ll_attendance_report_main.visibility = View.GONE
            rl_dashboard_fragment_main.visibility = View.VISIBLE
        }

        /*if (Pref.isOrderShow) {
            order_ll.visibility = View.VISIBLE
            price_RL.visibility = View.VISIBLE
        } else {
            order_ll.visibility = View.GONE
            price_RL.visibility = View.GONE
        }*/

        if (!Pref.isMeetingAvailable && !Pref.isShopAddEditAvailable)
            fab.visibility = View.GONE
        else
            fab.visibility = View.VISIBLE


        if (Pref.isServiceFeatureEnable) {
            tv_order.text = getString(R.string.myjobs)
            iv_order_icon.setImageResource(R.drawable.ic_activity_white)
            iv_order_icon.visibility = View.VISIBLE
            iv_quot_icon.visibility = View.GONE
            shop_tome_order_tab_LL.visibility = View.GONE
        } else if (Pref.willActivityShow) {
            tv_order.text = getString(R.string.activities)
            no_of_order_TV.text = getString(R.string.today_activity)
            avgOrder.text = InfoWizard.getActivityForToday()
            iv_order_icon.setImageResource(R.drawable.ic_activity_white)
            iv_order_icon.visibility = View.VISIBLE
            iv_quot_icon.visibility = View.GONE
            shop_tome_order_tab_LL.visibility = View.VISIBLE
        } else {
            shop_tome_order_tab_LL.visibility = View.VISIBLE
            if (Pref.isQuotationShow) {
                tv_order.text = getString(R.string.quotation)
                no_of_order_TV.text = getString(R.string.total_quot)
                avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
                iv_order_icon.visibility = View.GONE
                iv_quot_icon.visibility = View.VISIBLE
            } else {
                no_of_order_TV.text = getString(R.string.total_order_value_new)
                avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
                iv_order_icon.visibility = View.VISIBLE
                iv_quot_icon.visibility = View.GONE

                if (Pref.isOrderReplacedWithTeam) {
                    tv_order.text = getString(R.string.team_details)
                    iv_order_icon.setImageResource(R.drawable.ic_team_icon)
//                    price_RL.visibility = View.GONE
                    ll_dash_total_order_newD.visibility = View.GONE
                } else {
                    tv_order.text = getString(R.string.orders)
                    iv_order_icon.setImageResource(R.drawable.ic_dashboard_order_icon)

                    if (Pref.isOrderShow) {
                        order_ll.visibility = View.VISIBLE
                        //30-08-21

                        ll_dash_total_order_newD.visibility = View.VISIBLE
//                        avgOrder.visibility = View.VISIBLE
//                        no_of_order_TV.visibility = View.VISIBLE


//                        price_RL.visibility = View.VISIBLE
                    } else {
                        order_ll.visibility = View.GONE
//                        price_RL.visibility = View.GONE

                        ll_dash_total_order_newD.visibility = View.GONE
//                        avgOrder.visibility = View.GONE
//                        no_of_order_TV.visibility = View.GONE
                    }
                }
            }
        }

        if (Pref.isChatBotShow)
            fab_bot.visibility = View.VISIBLE
        else
            fab_bot.visibility = View.GONE



        if (Pref.isShowTimeline)
            history_ll.visibility = View.VISIBLE
        else
            history_ll.visibility = View.GONE

        //Added setting Login 12-08-21
        if (Pref.IsShowPartyOnAppDashboard)
            shop_ll.visibility = View.VISIBLE
        else
            shop_ll.visibility = View.GONE

        //Pref.IsShowAttendanceOnAppDashboard = true
        if (Pref.IsShowAttendanceOnAppDashboard)
            attandance_ll.visibility = View.VISIBLE
        else
            attandance_ll.visibility = View.GONE

        //Pref.IsShowTotalVisitsOnAppDashboard = true
        //Pref.IsShowVisitDurationOnAppDashboard = true

        if (Pref.IsShowTotalVisitsOnAppDashboard) {
//            shops_RL.visibility = View.VISIBLE
            ll_dash_total_visit_newD.visibility = View.VISIBLE
//            n_shops_TV.visibility = View.VISIBLE
//            no_of_shop_TV.visibility = View.VISIBLE
        } else {
//            shops_RL.visibility = View.GONE
            ll_dash_total_visit_newD.visibility = View.GONE
//            n_shops_TV.visibility = View.GONE
//            no_of_shop_TV.visibility = View.GONE
        }

        if (Pref.IsShowVisitDurationOnAppDashboard) {
//            time_RL.visibility = View.VISIBLE
            ll_dash_visit_duration_newD.visibility =  View.VISIBLE
//            n_time_TV.visibility = View.VISIBLE
//            no_of_time_TV.visibility = View.VISIBLE
        } else {
            ll_dash_visit_duration_newD.visibility =  View.GONE
//            time_RL.visibility = View.GONE
//            n_time_TV.visibility = View.GONE
//            no_of_time_TV.visibility = View.GONE
        }

        //Pref.IsDMS = true
        //19-08-21
        if (Pref.IsShowMarkDistVisitOnDshbrd) {
            ll_dash_point_visit_newD.visibility = View.VISIBLE
//            DDVisit_TV.visibility = View.VISIBLE
//            DDVisitdate_TV.visibility = View.VISIBLE
//            DDVisit_Rl.visibility = View.VISIBLE
        } else {
            ll_dash_point_visit_newD.visibility = View.GONE
//            DDVisit_TV.visibility = View.GONE
//            DDVisitdate_TV.visibility = View.GONE
//            DDVisit_Rl.visibility = View.GONE
        }

        if(Pref.IsShowDayStart){
            ll_dash_day_start_newD.visibility = View.VISIBLE
//            StartRL.visibility = View.VISIBLE
//            start_shop.visibility = View.VISIBLE
        }
        else{
            ll_dash_day_start_newD.visibility = View.GONE
//            StartRL.visibility = View.GONE
//            start_shop.visibility = View.GONE

        }
        if(Pref.IsShowDayEnd){

            ll_dash_day_end_newD.visibility = View.VISIBLE
            /*horizontal scroll 01-10-2021*/
//            endRL.visibility = View.VISIBLE
//            enddate_TV.visibility = View.VISIBLE
        }
        else{
            ll_dash_day_end_newD.visibility = View.GONE
//            endRL.visibility = View.GONE
//            enddate_TV.visibility = View.GONE
        }


        initAdapter()
        initBottomAdapter()
    }



    private fun getAttendanceReport(date: String) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        var isApiCalling = true

        val repository = AttendanceReportRepoProvider.provideAttendanceReportRepository()
        progress_wheel_attendance.spin()
        BaseActivity.compositeDisposable.add(
                repository.getAttendanceReportList(date)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        /*.doOnTerminate {
                            Log.e("Dashboard", "===========On Terminate Attendance Report==========")
                            if (isApiCalling)
                                getAttendanceReport(date)
                        }*/
                        .subscribe({ result ->
                            val attendanceList = result as AttendanceReportDataModel

                            isApiCalling = false

                            when {
                                attendanceList.status == NetworkConstant.SUCCESS -> {
                                    progress_wheel_attendance.stopSpinning()

                                    if (attendanceList.attendance_report_list != null && attendanceList.attendance_report_list!!.size > 0) {

                                        tv_no_data_available.visibility = View.GONE
                                        rv_attendance_report_list.adapter = AttendanceReportAdapter(mContext, attendanceList.attendance_report_list, object : AttendanceReportAdapter.OnClickListener {
                                            override fun onCallClick(adapterPosition: Int) {
                                                if (TextUtils.isEmpty(attendanceList.attendance_report_list!![adapterPosition].contact_no) || attendanceList.attendance_report_list!![adapterPosition].contact_no.equals("null", ignoreCase = true)
                                                        || !AppUtils.isValidateMobile(attendanceList.attendance_report_list!![adapterPosition].contact_no!!)) {
                                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_phn_no_unavailable))
                                                } else {
                                                    IntentActionable.initiatePhoneCall(mContext, attendanceList.attendance_report_list!![adapterPosition].contact_no)
                                                }
                                            }
                                        })
                                    } else
                                        tv_no_data_available.visibility = View.VISIBLE

                                }
                                attendanceList.status == NetworkConstant.SESSION_MISMATCH -> {
                                    progress_wheel_attendance.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
                                    startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                    (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                    (mContext as DashboardActivity).finish()
                                }
                                attendanceList.status == NetworkConstant.NO_DATA -> {
                                    progress_wheel_attendance.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)

                                }
                                else -> {
                                    progress_wheel_attendance.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
                                }
                            }

                        }, { error ->
                            isApiCalling = false
                            progress_wheel_attendance.stopSpinning()
                            error.printStackTrace()
                            tv_no_data_available.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }


    var shopDurationData: ShopDurationRequestData? = null
    override fun onClick(p0: View?) {
        when (p0!!.id) {

            R.id.fab -> {
                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else {
                    /*if (!Pref.isMeetingAvailable)
                        (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
                    else {
                        CommonDialog.getInstance("Action", "What you like to do?", "Add Shop", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
                            override fun onLeftClick() {
                                (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
                            }

                            override fun onRightClick(editableData: String) {
                                showAddMeetingAlert()
                            }

                        }).show((mContext as DashboardActivity).supportFragmentManager, "")
                    }*/

                    if(Pref.IsShowDayStart){
                        if (!Pref.DayStartMarked) {
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                            dialogHeader.text = "Please start your day..."
                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()

//                            (mContext as DashboardActivity).showSnackMessage("Please start your day")
                            return
                        }
                    }


                    if (Pref.isMeetingAvailable && Pref.isShopAddEditAvailable) {
                        CustomStatic.IsCommDLeftBtnColor = true
                        CustomStatic.IsCommDRightBtnColor = true
                        CommonDialog.getInstance("${AppUtils.hiFirstNameText()+"!"}", "What will you be doing now?", "Add a ${Pref.shopText}", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
                            override fun onLeftClick() {
                                (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
                            }

                            override fun onRightClick(editableData: String) {
                                showAddMeetingAlert()
                            }

                        }).show((mContext as DashboardActivity).supportFragmentManager, "")
                    } else if (Pref.isMeetingAvailable)
                        showAddMeetingAlert()
                    else if (Pref.isShopAddEditAvailable)
                        (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
                }
            }

//            R.id.fab -> {
//                //19-08-21 customer shop create stop untill daystart
//                if (Pref.IsShowDayStart) {
//                    if (!Pref.DayStartMarked) {
//                        (mContext as DashboardActivity).showSnackMessage("Please start your day")
//                    } else {
//                        if (!Pref.isAddAttendence)
//                            (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
//                        else {
//                            /*if (!Pref.isMeetingAvailable)
//                                (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                            else {
//                                CommonDialog.getInstance("Action", "What you like to do?", "Add Shop", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
//                                    override fun onLeftClick() {
//                                        (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                                    }
//
//                                    override fun onRightClick(editableData: String) {
//                                        showAddMeetingAlert()
//                                    }
//
//                                }).show((mContext as DashboardActivity).supportFragmentManager, "")
//                            }*/
//
//                            if (Pref.isMeetingAvailable && Pref.isShopAddEditAvailable) {
//                                CommonDialog.getInstance("Select Activity", "What will you be doing now?", "Add a ${Pref.shopText}", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
//                                    override fun onLeftClick() {
//                                        (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                                    }
//
//                                    override fun onRightClick(editableData: String) {
//                                        showAddMeetingAlert()
//                                    }
//
//                                }).show((mContext as DashboardActivity).supportFragmentManager, "")
//                            } else if (Pref.isMeetingAvailable)
//                                showAddMeetingAlert()
//                            else if (Pref.isShopAddEditAvailable)
//                                (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                        }
//                    }
//                }
//                else
//                    if (!Pref.isAddAttendence)
//                        (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
//                    else {
//                        /*if (!Pref.isMeetingAvailable)
//                            (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                        else {
//                            CommonDialog.getInstance("Action", "What you like to do?", "Add Shop", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
//                                override fun onLeftClick() {
//                                    (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                                }
//
//                                override fun onRightClick(editableData: String) {
//                                    showAddMeetingAlert()
//                                }
//
//                            }).show((mContext as DashboardActivity).supportFragmentManager, "")
//                        }*/
//
//                        if (Pref.isMeetingAvailable && Pref.isShopAddEditAvailable) {
//                            CommonDialog.getInstance("Select Activity", "What will you be doing now?", "Add a ${Pref.shopText}", Pref.meetingText, false, false, true, object : CommonDialogClickListener {
//                                override fun onLeftClick() {
//                                    (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                                }
//
//                                override fun onRightClick(editableData: String) {
//                                    showAddMeetingAlert()
//                                }
//
//                            }).show((mContext as DashboardActivity).supportFragmentManager, "")
//                        } else if (Pref.isMeetingAvailable)
//                            showAddMeetingAlert()
//                        else if (Pref.isShopAddEditAvailable)
//                            (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
//                    }
//            }


            R.id.history_ll -> {
                (mContext as DashboardActivity).isMemberMap = false
                if (!Pref.willTimelineWithFixedLocationShow)
                    (mContext as DashboardActivity).loadFragment(FragType.OrderhistoryFragment, false, "")
                else
                    (mContext as DashboardActivity).loadFragment(FragType.TimeLineFragment, false, "")

            }
            R.id.cancel_timer -> {
                if (!tv_timer.text.toString().trim().equals("00.00.00")) {
                    tv_timer.text = "00.00.00"
                } else {
                    ll_recorder_root.visibility = View.GONE
                    DashboardFragment.isRecordRootVisible = false
                    (mContext as DashboardActivity).updateScreenRecStatus()
                }
            }
            R.id.pause_record -> {
                if (hbRecorder != null) {
                    if (hbRecorder!!.isBusyRecording) {
                        if (hbRecorder!!.isRecordingPaused) {
                            hbRecorder!!.resumeScreenRecording()
                            ScreenRecService.isPause = false
                            pause_record.text = "||"
                        } else {
                            hbRecorder!!.pauseScreenRecording()
                            ScreenRecService.isPause = true
                            pause_record.text = ">"
                        }

                    }
                }
            }
            R.id.iv_screen_status -> {
                if (hbRecorder != null) {
                    if (hbRecorder!!.isBusyRecording) {
                        pause_record.visibility = View.GONE
                        hbRecorder!!.stopScreenRecording()
                    } else {
                        pause_record.visibility = View.VISIBLE
                        startRecordingScreen()
                    }
                } else {
                    pause_record.visibility = View.VISIBLE
                    startRecordingScreen()
                }

            }
            R.id.shop_ll -> {

                /*val list = ArrayList<ShopDurationRequestData>()

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_72"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_89"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_82"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_72"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_897"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-30T00:00:02"
                shopDurationData?.visited_time = "2019-01-30T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_72"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-29T00:00:02"
                shopDurationData?.visited_time = "2019-01-29T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                shopDurationData = ShopDurationRequestData()
                shopDurationData?.shop_id = "1_72"
                shopDurationData?.spent_duration = "00"
                shopDurationData?.visited_date = "2019-01-28T00:00:02"
                shopDurationData?.visited_time = "2019-01-28T00:00:02"
                shopDurationData?.total_visit_count = "1"
                list.add(shopDurationData!!)

                Log.e("Dashboard", "Duplicate array list size===> " + list.size)*/

                //val newShopList = FTStorageUtils.removeDuplicateData(list)

                //val hashSet = HashSet<ShopDurationRequestData>()
                /*hashSet.addAll(list)
                list.clear()
                list.addAll(hashSet)*/

                /*val newShopList = ArrayList<ShopDurationRequestData>()
                for (i in list.indices){
                    if (hashSet.add(list[i]))
                        newShopList.add(list[i])
                }*/

                //Log.e("Dashboard", "Unique new list size===> " + newShopList.size)

                if (!Pref.isShowShopBeatWise) {
                    (mContext as DashboardActivity).isShopFromChatBot = false
                    if (!Pref.isServiceFeatureEnable)
                        (mContext as DashboardActivity).loadFragment(FragType.NearByShopsListFragment, false, "")
                    else
                        (mContext as DashboardActivity).loadFragment(FragType.CustomerListFragment, false, "")
                } else
                    (mContext as DashboardActivity).loadFragment(FragType.BeatListFragment, false, "")
                //(mContext as DashboardActivity).loadFragment(FragType.NewNearByShopsListFragment, false, "")

            }
            R.id.attandance_ll -> {
                (mContext as DashboardActivity).isChatBotAttendance = false
                (mContext as DashboardActivity).loadFragment(FragType.AttendanceFragment, false, "")

            }
            R.id.order_ll -> {
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                when {
                    Pref.isServiceFeatureEnable -> {
                        Pref.IsMyJobFromTeam = false
                        (mContext as DashboardActivity).loadFragment(FragType.MyJobsFragment, false, "")
                    }
                    Pref.willActivityShow -> (mContext as DashboardActivity).loadFragment(FragType.ActivityShopListFragment, false, "")
                    Pref.isQuotationShow -> {
                        (mContext as DashboardActivity).isBack = false
                        (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, false, "")
                    }
                    Pref.isOrderReplacedWithTeam -> (mContext as DashboardActivity).loadFragment(FragType.MemberListFragment, true, Pref.user_id!!)
                    else -> {
                        (mContext as DashboardActivity).isOrderFromChatBot = false
                        if(Pref.IsActivateNewOrderScreenwithSize){
                            CustomStatic.IsOrderFromTotalOrder=false
                            (mContext as DashboardActivity).loadFragment(FragType.NewOdrScrListFragment, false, "")
                        }else{
                            (mContext as DashboardActivity).loadFragment(FragType.NewOrderListFragment, false, "")
                        }

                    }

                }

                //(mContext as DashboardActivity).loadFragment(FragType.OrderListFragment, false, "")
            }
            //R.id.shops_RL -> {
            R.id.n_shops_TV -> {
                (mContext as DashboardActivity).loadFragment(FragType.AverageShopFragment, true, "")
            }
            //R.id.time_RL -> {
            R.id.n_time_TV -> {
                (mContext as DashboardActivity).loadFragment(FragType.AvgTimespentShopListFragment, true, "")
            }

//            R.id.price_RL ->
                R.id.ll_dash_total_order_newD -> {
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                when {
                    //Pref.isServiceFeatureEnable -> (mContext as DashboardActivity).loadFragment(FragType.MyJobsFragment, true, "")
                    Pref.IsActivateNewOrderScreenwithSize -> {
                        CustomStatic.IsOrderFromTotalOrder=true
                        (mContext as DashboardActivity).loadFragment(FragType.NewOdrScrListFragment, true, "")
                    }
                    Pref.willActivityShow -> (mContext as DashboardActivity).loadFragment(FragType.DateWiseActivityListFragment, true, "")
                    Pref.isQuotationShow -> (mContext as DashboardActivity).loadFragment(FragType.DateWiseQuotationList, true, "")
                    else -> (mContext as DashboardActivity).loadFragment(FragType.NewDateWiseOrderListFragment, true, "")
                }
            }

            R.id.tv_view_all -> {
                (mContext as DashboardActivity).loadFragment(FragType.AllShopListFragment, true, "")
            }
            R.id.tv_pick_date_range -> {
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.show()
            }

            R.id.fab_bot -> {
                (mContext as DashboardActivity).showLanguageAlert(false)
            }
        }
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        tv_pick_date_range.text = AppUtils.getFormattedDate(myCalendar.time)

        getAttendanceReport(AppUtils.getFormattedDateForApi(myCalendar.time))
    }


    private fun showAddMeetingAlert() {
        AddMeetingDialog.getInstance(Pref.meetingText, "", "Cancel", "Confirm", false, true,
                false, object : AddMeetingDialog.OnButtonClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String, selectedTypeId: String) {

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

                val meetingEntity = MeetingEntity()
                meetingEntity.date = AppUtils.getCurrentDateForShopActi()
                meetingEntity.duration_spent = "00:00:00"
                meetingEntity.remakrs = editableData
                meetingEntity.startTimeStamp = System.currentTimeMillis().toString()
                meetingEntity.date_time = AppUtils.getCurrentISODateTime()
                meetingEntity.lattitude = Pref.current_latitude
                meetingEntity.longitude = Pref.current_longitude
                meetingEntity.meetingTypeId = selectedTypeId

                var address = LocationWizard.getAdressFromLatlng(mContext, meetingEntity.lattitude?.toDouble(), meetingEntity.longitude?.toDouble())

                if (address.contains("http"))
                    address = "Unknown"

                meetingEntity.address = address
                meetingEntity.pincode = LocationWizard.getPostalCode(mContext, meetingEntity.lattitude?.toDouble()!!, meetingEntity.longitude?.toDouble()!!)

                var distance = 0.0

                if (Pref.isOnLeave.equals("false", ignoreCase = true)) {

                    XLog.e("=====User is at work (At meeting revisit time)=======")

                    val userlocation = UserLocationDataEntity()
                    userlocation.latitude = meetingEntity.lattitude!!
                    userlocation.longitude = meetingEntity.longitude!!

                    var loc_distance = 0.0

                    val locationList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi())

                    if (locationList != null && locationList.isNotEmpty()) {
                        loc_distance = LocationWizard.getDistance(locationList[locationList.size - 1].latitude.toDouble(), locationList[locationList.size - 1].longitude.toDouble(),
                                userlocation.latitude.toDouble(), userlocation.longitude.toDouble())
                    }
                    val finalDistance = (Pref.tempDistance.toDouble() + loc_distance).toString()

                    XLog.e("===Distance (At meeting revisit time)===")
                    XLog.e("Temp Distance====> " + Pref.tempDistance)
                    XLog.e("Normal Distance====> $loc_distance")
                    XLog.e("Total Distance====> $finalDistance")
                    XLog.e("=====================================")

                    userlocation.distance = finalDistance
                    userlocation.locationName = LocationWizard.getNewLocationName(mContext, userlocation.latitude.toDouble(), userlocation.longitude.toDouble())
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
                    userlocation.network_status = if (AppUtils.isOnline(mContext)) "Online" else "Offline"
                    userlocation.battery_percentage = AppUtils.getBatteryPercentage(mContext).toString()
                    AppDatabase.getDBInstance()!!.userLocationDataDao().insertAll(userlocation)

                    Pref.totalS2SDistance = (Pref.totalS2SDistance.toDouble() + userlocation.distance.toDouble()).toString()

                    distance = Pref.totalS2SDistance.toDouble()
                    Pref.totalS2SDistance = "0.0"
                    Pref.tempDistance = "0.0"
                } else {
                    XLog.e("=====User is on leave (At meeting revisit time)=======")
                    distance = 0.0
                }

                meetingEntity.distance_travelled = distance.toString()

                AppDatabase.getDBInstance()!!.addMeetingDao().insertAll(meetingEntity)
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    @SuppressLint("WrongConstant", "UseRequireInsteadOfGet")
    private fun initAdapter() {
        mRouteActivityDashboardAdapter = RouteActivityDashboardAdapter(this.context!!, AppDatabase.getDBInstance()!!.userLocationDataDao().all)
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
    }


    private fun getLocationList(): List<UserLocationDataEntity> {
        return AppDatabase.getDBInstance()!!.userLocationDataDao().all

    }

    fun UpdateLocationData() {
        mRouteActivityDashboardAdapter.update(getLocationList())
        mRouteActivityDashboardAdapter.notifyDataSetChanged()
    }

    /*@SuppressLint("WrongConstant")
    public fun initBottomAdapter() {

        /*val performList = ArrayList<AddShopDBModelEntity>()
        val updatedPerformList = ArrayList<AddShopDBModelEntity>()

        for (i in list.indices) {
            if (i == 0)
                performList.add(list[i])
            else {
                if (list[i].shop_id != list[i - 1].shop_id) {
                    performList.add(list[i])
                }
            }
        }


        for (i in performList.indices) {
            if (i > 4)
                break

            updatedPerformList.add(performList[i])
        }

        if (updatedPerformList.size == 1)
            best_performing_shop_TV.text = "Best performing " + updatedPerformList.size + " shop"
        else
            best_performing_shop_TV.text = "Best performing " + updatedPerformList.size + " shops"*/


        val work_type_list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getTodaysData(AppUtils.getCurrentDate()) as ArrayList<SelectedWorkTypeEntity>

        if (work_type_list != null && work_type_list.size > 0) {
            no_shop_tv.visibility = View.GONE
            reportList.visibility = View.VISIBLE
            //adapter = ReportAdapter(mContext, work_type_list)
            layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
            reportList.layoutManager = layoutManager
            reportList.adapter = TodaysWorkAdapter(mContext, work_type_list)
            reportList.isNestedScrollingEnabled = false
        } else {
            reportList.visibility = View.GONE
        }

        if (Pref.isAttendanceFeatureOnly)
            getAttendanceReport(AppUtils.getCurrentDateForShopActi())
        else {
            //getUserPjpList(work_type_list)

            //val pjpList = AppDatabase.getDBInstance()?.pjpListDao()?.getAll() as ArrayList<PjpListEntity>
            //task new updation
            var pjpList = AppDatabase.getDBInstance()?.pjpListDao()?.getAllByDate(AppUtils.getCurrentDateForShopActi()) as ArrayList<PjpListEntity>

            if(!Pref.SelectedBeatIDFromAttend.equals("-1") && Pref.IsBeatRouteAvailableinAttendance && Pref.isAddAttendence){
                var shopListWithBeat:Any
                if(Pref.IsDistributorSelectionRequiredinAttendance){
                    shopListWithBeat = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopBeatWiseDD(Pref.SelectedBeatIDFromAttend,Pref.SelectedDDIDFromAttend)
                }else{
                    shopListWithBeat = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopBeatWise(Pref.SelectedBeatIDFromAttend)
                }
                if(shopListWithBeat.size>0){
                    for(l in 0..shopListWithBeat.size-1){

                        var obj :PjpListEntity = PjpListEntity()
                        if(pjpList.size>0){
                            obj.pjp_id=(pjpList.get(pjpList.size-1).pjp_id!!.toInt()+1).toString()
                        }else{
                            obj.pjp_id="1"
                        }
                        obj.from_time=""
                        obj.to_time=""

                        var beatName = AppDatabase.getDBInstance()?.beatDao()?.getSingleItem(shopListWithBeat.get(l).beat_id)!!.name

                        obj.customer_name="${Pref.beatText}"+" : "+beatName+"\n"+"${Pref.shopText}"+" : "+shopListWithBeat.get(l).shopName
                        obj.customer_id=shopListWithBeat.get(l).shop_id
                        obj.location=""
                        obj.date=AppUtils.getCurrentDateForShopActi()
                        obj.remarks=""

                        pjpList.add(obj)
                    }
                }


            }

            if (pjpList != null && pjpList.isNotEmpty()) {
                no_shop_tv.visibility = View.GONE
                rv_pjp_list.visibility = View.VISIBLE

                reportList.visibility = View.GONE

                rv_pjp_list.adapter = PjpAdapter(mContext, pjpList, object : PJPClickListner {
                    override fun visitShop(shop: Any) {
                        if (!Pref.isAddAttendence) {
                            (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                        }
                        else {
                            val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                            (mContext as DashboardActivity).callShopVisitConfirmationDialog(nearbyShop.shopName, nearbyShop.shop_id)
                        }
                    }
                })

            }
            else {
                rv_pjp_list.visibility = View.GONE
                reportList.visibility = View.VISIBLE

                if (work_type_list == null || work_type_list.size == 0)
                    no_shop_tv.visibility = View.VISIBLE
            }
        }
    }*/

    var initBottomAdapterUpdaton = false

    @SuppressLint("WrongConstant")
    public fun initBottomAdapter() {
        if(initBottomAdapterUpdaton){
            return
        }
        /*val performList = ArrayList<AddShopDBModelEntity>()
        val updatedPerformList = ArrayList<AddShopDBModelEntity>()

        for (i in list.indices) {
            if (i == 0)
                performList.add(list[i])
            else {
                if (list[i].shop_id != list[i - 1].shop_id) {
                    performList.add(list[i])
                }
            }
        }


        for (i in performList.indices) {
            if (i > 4)
                break

            updatedPerformList.add(performList[i])
        }

        if (updatedPerformList.size == 1)
            best_performing_shop_TV.text = "Best performing " + updatedPerformList.size + " shop"
        else
            best_performing_shop_TV.text = "Best performing " + updatedPerformList.size + " shops"*/

        initBottomAdapterUpdaton=true

        println("pjp_tag_insert")

        val work_type_list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getTodaysData(AppUtils.getCurrentDate()) as ArrayList<SelectedWorkTypeEntity>

        if (work_type_list != null && work_type_list.size > 0) {
            no_shop_tv.visibility = View.GONE
            reportList.visibility = View.VISIBLE
            //adapter = ReportAdapter(mContext, work_type_list)
            layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
            reportList.layoutManager = layoutManager
            reportList.adapter = TodaysWorkAdapter(mContext, work_type_list)
            reportList.isNestedScrollingEnabled = false
        }
        else {
            reportList.visibility = View.GONE
        }

        if (Pref.isAttendanceFeatureOnly)
            getAttendanceReport(AppUtils.getCurrentDateForShopActi())
        else {
            //getUserPjpList(work_type_list)

            //val pjpList = AppDatabase.getDBInstance()?.pjpListDao()?.getAll() as ArrayList<PjpListEntity>
            //task new updation
            var pjpList = AppDatabase.getDBInstance()?.pjpListDao()?.getAllByDate(AppUtils.getCurrentDateForShopActi()) as ArrayList<PjpListEntity>

            //Pref.IsBeatRouteAvailableinAttendance=true

            if(!Pref.SelectedBeatIDFromAttend.equals("-1") && Pref.IsBeatRouteAvailableinAttendance && Pref.isAddAttendence){

                scope.launch {
                    pjpList = loadpjpWithThread(pjpList)
                }.invokeOnCompletion {
                    if (pjpList != null && pjpList.isNotEmpty()) {
                        no_shop_tv.visibility = View.GONE
                        rv_pjp_list.visibility = View.VISIBLE
                        reportList.visibility = View.GONE

                        var layoutManager: RecyclerView.LayoutManager
                        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager
                        rv_pjp_list.layoutManager = layoutManager

                        progress_wheel.spin()

                        var pjpAdapterNew = PjpAdapter(mContext, pjpList, object : PJPClickListner {
                            override fun visitShop(shop: Any) {
                                if (!Pref.isAddAttendence) {
                                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                                }
                                else {
                                    val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                                    (mContext as DashboardActivity).callShopVisitConfirmationDialog(nearbyShop.shopName, nearbyShop.shop_id)
                                }
                            }
                        })
                        rv_pjp_list.adapter = pjpAdapterNew
                        progress_wheel.stopSpinning()
                    }
                    else {
                        rv_pjp_list.visibility = View.GONE
                        reportList.visibility = View.VISIBLE

                        if (work_type_list == null || work_type_list.size == 0)
                            no_shop_tv.visibility = View.VISIBLE
                    }
                }

                /*var shopListWithBeat = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopBeatWise(Pref.SelectedBeatIDFromAttend)
                if(shopListWithBeat.size>0){
                    doAsync {
                        for(l in 0..shopListWithBeat.size-1){
                            var obj :PjpListEntity = PjpListEntity()
                            if(pjpList.size>0){
                                obj.pjp_id=(pjpList.get(pjpList.size-1).pjp_id!!.toInt()+1).toString()
                            }else{
                                obj.pjp_id="1"
                            }
                            obj.from_time=""
                            obj.to_time=""

                            var beatName = AppDatabase.getDBInstance()?.beatDao()?.getSingleItem(shopListWithBeat.get(l).beat_id)!!.name

                            obj.customer_name="${Pref.beatText}"+" : "+beatName+"\n"+"${Pref.shopText}"+" : "+shopListWithBeat.get(l).shopName
                            obj.customer_id=shopListWithBeat.get(l).shop_id
                            obj.location=""
                            obj.date=AppUtils.getCurrentDateForShopActi()
                            obj.remarks=""

                            pjpList.add(obj)
                            println("pjp_tag ${obj.pjp_id}");
                        }
                        uiThread {
                            if (pjpList != null && pjpList.isNotEmpty()) {
                                no_shop_tv.visibility = View.GONE
                                rv_pjp_list.visibility = View.VISIBLE

                                reportList.visibility = View.GONE

                                rv_pjp_list.adapter = PjpAdapter(mContext, pjpList, object : PJPClickListner {
                                    override fun visitShop(shop: Any) {
                                        if (!Pref.isAddAttendence) {
                                            (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                                        }
                                        else {
                                            val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                                            (mContext as DashboardActivity).callShopVisitConfirmationDialog(nearbyShop.shopName, nearbyShop.shop_id)
                                        }
                                    }
                                })

                            }
                            else {
                                rv_pjp_list.visibility = View.GONE
                                reportList.visibility = View.VISIBLE

                                if (work_type_list == null || work_type_list.size == 0)
                                    no_shop_tv.visibility = View.VISIBLE
                            }
                        }
                    }
                }*/

            }
            else{
                if (pjpList != null && pjpList.isNotEmpty()) {
                    no_shop_tv.visibility = View.GONE
                    rv_pjp_list.visibility = View.VISIBLE

                    reportList.visibility = View.GONE

                    rv_pjp_list.adapter = PjpAdapter(mContext, pjpList, object : PJPClickListner {
                        override fun visitShop(shop: Any) {
                            if (!Pref.isAddAttendence) {
                                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                            }
                            else {
                                val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                                (mContext as DashboardActivity).callShopVisitConfirmationDialog(nearbyShop.shopName, nearbyShop.shop_id)
                            }
                        }
                    })
                }
                else {
                    rv_pjp_list.visibility = View.GONE
                    reportList.visibility = View.VISIBLE

                    if (work_type_list == null || work_type_list.size == 0)
                        no_shop_tv.visibility = View.VISIBLE
                }
            }
        }


        Handler().postDelayed(Runnable {
            initBottomAdapterUpdaton = false
        }, 10000)

    }

    val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    suspend fun loadpjpWithThread(pjL:ArrayList<PjpListEntity>): ArrayList<PjpListEntity>{

        val res  = CoroutineScope(Dispatchers.IO).launch {
            try {
                //AppDatabase.getDBInstance()?.addShopEntryDao()?.updateBeat("6")
                var shopListWithBeat:Any
                if(Pref.IsDistributorSelectionRequiredinAttendance){
                    shopListWithBeat = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopBeatWiseDD(Pref.SelectedBeatIDFromAttend,Pref.SelectedDDIDFromAttend)
                }else{
                    shopListWithBeat = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopBeatWise(Pref.SelectedBeatIDFromAttend)
                }
                if(shopListWithBeat.size>0){
                    for(l in 0..shopListWithBeat.size-1){
                        var obj :PjpListEntity = PjpListEntity()
                        if(pjL.size>0){
                            obj.pjp_id=(pjL.get(pjL.size-1).pjp_id!!.toInt()+1).toString()
                        }else{
                            obj.pjp_id="1"
                        }
                        obj.from_time=""
                        obj.to_time=""

                        var beatName = AppDatabase.getDBInstance()?.beatDao()?.getSingleItem(shopListWithBeat.get(l).beat_id)!!.name

                        obj.customer_name="${Pref.beatText}"+" : "+beatName+"\n"+"${Pref.shopText}"+" : "+shopListWithBeat.get(l).shopName
                        obj.customer_id=shopListWithBeat.get(l).shop_id
                        obj.location=""
                        obj.date=AppUtils.getCurrentDateForShopActi()
                        obj.remarks=""

                        pjL.add(obj)
                        println("pjp_tag ${obj.pjp_id}");
                    }
                }

                true
            } catch (ex: Exception) {
                ex.printStackTrace()
                println("tag_ res error ${Thread.currentThread().name}")
                false
            }
        }
        res.join()
        return pjL

    }

    private fun getUserPjpList(workTypeList: ArrayList<SelectedWorkTypeEntity>) {
        if (!AppUtils.isOnline(mContext)) {

            if (workTypeList == null || workTypeList.size == 0)
                no_shop_tv.visibility = View.VISIBLE

            rv_pjp_list.visibility = View.GONE
            //(mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getUserPJPList(AppUtils.getCurrentDateForShopActi())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UserPjpResponseModel
                            XLog.d("GET USER PJP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.pjp_list != null && response.pjp_list.size > 0) {
                                    no_shop_tv.visibility = View.GONE
                                    rv_pjp_list.visibility = View.VISIBLE
                                    //rv_pjp_list.adapter = PjpAdapter(mContext, response.pjp_list)

                                } else {
                                    rv_pjp_list.visibility = View.GONE
                                    //(mContext as DashboardActivity).showSnackMessage(response.message!!)

                                    if (workTypeList == null || workTypeList.size == 0)
                                        no_shop_tv.visibility = View.VISIBLE
                                }
                            } else {
                                rv_pjp_list.visibility = View.GONE
                                //(mContext as DashboardActivity).showSnackMessage(response.message!!)

                                if (workTypeList == null || workTypeList.size == 0)
                                    no_shop_tv.visibility = View.VISIBLE
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET USER PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            //(mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            rv_pjp_list.visibility = View.GONE

                            if (workTypeList == null || workTypeList.size == 0)
                                no_shop_tv.visibility = View.VISIBLE
                        })
        )
    }

    fun addShopDBModel() {

        try {
            val endDate = AppUtils.convertDateStringToLong(AppUtils.getCurrentDateForShopActi())
            val startDate = AppUtils.convertDateStringToLong(AppUtils.getThreeMonthsPreviousDate(AppUtils.getCurrentDateForShopActi()))

            val orderList = AppDatabase.getDBInstance()!!.orderListDao().getListAccordingToDateRange(startDate, endDate) as ArrayList<OrderListEntity>

            /*val hashSet = HashSet<OrderListEntity>()
            hashSet.addAll(orderList)
            orderList.clear()
            orderList.addAll(hashSet)*/

            list = ArrayList<AddShopDBModelEntity>()

            /*if (orderList != null && orderList.isNotEmpty()) {

                no_shop_tv.visibility = View.GONE

                for (i in orderList.indices) {
                    val maxAmount = AppDatabase.getDBInstance()!!.orderDetailsListDao().getAmountAccordingToShopId(orderList[i].shop_id!!)

                    if (!TextUtils.isEmpty(maxAmount)) {
                        val mAddShopDBModelEntity = AddShopDBModelEntity()
                        mAddShopDBModelEntity.shop_id = orderList[i].shop_id
                        mAddShopDBModelEntity.address = orderList[i].address
                        mAddShopDBModelEntity.shopName = orderList[i].shop_name
                        mAddShopDBModelEntity.orderValue = maxAmount.toFloat().toInt() //orderList[i].order_amount?.toFloat()!!.toInt()
                        list.add(mAddShopDBModelEntity)
                    }
                }

                Collections.sort(list, object : Comparator<AddShopDBModelEntity> {
                    override fun compare(p0: AddShopDBModelEntity?, p1: AddShopDBModelEntity?): Int {
                        return p0?.orderValue?.let { p1?.orderValue?.compareTo(it) }!!
                    }
                })

            } else {*/
            //best_performing_shop_TV.text = /*"Best Performing 0 shop"*/ getString(R.string.best_performing_shop)
            //no_shop_tv.visibility = View.VISIBLE
            val mAddShopDBModelEntity1: AddShopDBModelEntity = AddShopDBModelEntity()
            mAddShopDBModelEntity1.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity1.shopName = "The Tommy Hilfinger"
            mAddShopDBModelEntity1.orderValue = 200

            val mAddShopDBModelEntity2: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity2.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity2.shopName = "Addidus Store"
            mAddShopDBModelEntity2.orderValue = 300


            val mAddShopDBModelEntity3: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity3.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity3.shopName = "Turtle Outlet"
            mAddShopDBModelEntity3.orderValue = 400

            val mAddShopDBModelEntity4: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity4.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity4.shopName = "Levice International"
            mAddShopDBModelEntity4.orderValue = 500

            val mAddShopDBModelEntity5: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity5.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity5.shopName = "Image Kolkata Store"
            mAddShopDBModelEntity5.orderValue = 600

            val mAddShopDBModelEntity6: AddShopDBModelEntity = AddShopDBModelEntity()

            mAddShopDBModelEntity5.address = "SDF Module GP block Kol 700091"
            mAddShopDBModelEntity5.shopName = "Image Kolkata Store"
            mAddShopDBModelEntity5.orderValue = 600


            list.add(mAddShopDBModelEntity1)
            list.add(mAddShopDBModelEntity2)
            list.add(mAddShopDBModelEntity3)
            list.add(mAddShopDBModelEntity4)
            list.add(mAddShopDBModelEntity5)
            list.add(mAddShopDBModelEntity6)


            Collections.sort(list, object : Comparator<AddShopDBModelEntity> {
                override fun compare(p0: AddShopDBModelEntity?, p1: AddShopDBModelEntity?): Int {
                    return p0?.orderValue?.let { p1?.orderValue?.compareTo(it) }!!
                }
            })

            //}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun updateItem() {
        //addShopDBModel()
        //initBottomAdapter()



        if (!TextUtils.isEmpty(Pref.isFieldWorkVisible) && Pref.isFieldWorkVisible.equals("false", ignoreCase = true)) {
            val list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getAll()
            if (Pref.isAddAttendence && (list == null || list.isEmpty()))
                getSelectedRouteList()
            else
                initBottomAdapter()
        } else
            initBottomAdapter()
    }

    fun updateBottomList() {
        if (!TextUtils.isEmpty(Pref.isFieldWorkVisible) && Pref.isFieldWorkVisible.equals("false", ignoreCase = true))
            getSelectedRouteList()
        else {
            initBottomAdapter()
            checkToCallMemberList()
        }
    }

    private fun getSelectedRouteList() {
        val repository = GetRouteListRepoProvider.routeListRepoProvider()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()

        BaseActivity.compositeDisposable.add(
                repository.routeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as SelectedRouteListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val workTypeList = response.worktype

                                if (workTypeList != null && workTypeList.isNotEmpty()) {

                                    AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.delete()
                                    AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.deleteData()
                                    AppDatabase.getDBInstance()?.selectedRouteListDao()?.deleteRoute()

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
                                            initBottomAdapter()
                                            checkToCallMemberList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    checkToCallMemberList()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                checkToCallMemberList()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            checkToCallMemberList()
                        })
        )
    }

    fun sendHomeLoc(locationInfoModel: locationInfoModel?) {

        if (AppUtils.isOnline(mContext))
            callSubmitLocApi(locationInfoModel)
        else {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

            Handler().postDelayed(Runnable {
                (mContext as DashboardActivity).checkToShowHomeLocationAlert()
            }, 200)
        }
    }

    private fun callSubmitLocApi(locationInfoModel: locationInfoModel?) {

        if (TextUtils.isEmpty(Pref.session_token) || TextUtils.isEmpty(Pref.user_id)) {
            startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
            (mContext as DashboardActivity).overridePendingTransition(0, 0)
            (mContext as DashboardActivity).finish()
        }

        val submitLoc = SubmitHomeLocationInputModel()
        submitLoc.session_token = Pref.session_token!!
        submitLoc.user_id = Pref.user_id!!

        if (!TextUtils.isEmpty(locationInfoModel?.latitude))
            submitLoc.latitude = locationInfoModel?.latitude!!

        if (!TextUtils.isEmpty(locationInfoModel?.longitude))
            submitLoc.longitude = locationInfoModel?.longitude!!

        if (!TextUtils.isEmpty(locationInfoModel?.address))
            submitLoc.address = locationInfoModel?.address!!

        if (!TextUtils.isEmpty(locationInfoModel?.city))
            submitLoc.city = locationInfoModel?.city!!

        if (!TextUtils.isEmpty(locationInfoModel?.state))
            submitLoc.state = locationInfoModel?.state!!

        if (!TextUtils.isEmpty(locationInfoModel?.pinCode))
            submitLoc.pincode = locationInfoModel?.pinCode!!

        val repository = SubmitHomeLocationRepoProvider.submitHomeLocRepo()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
        BaseActivity.compositeDisposable.add(
                repository.submitAttendance(submitLoc)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                Pref.isHomeLocAvailable = true
                                Pref.home_latitude = submitLoc.latitude
                                Pref.home_longitude = submitLoc.longitude
                                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()

                            } else if (response.status == NetworkConstant.SESSION_MISMATCH) {
//                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                Handler().postDelayed(Runnable {
                                    (mContext as DashboardActivity).checkToShowHomeLocationAlert()
                                }, 200)
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            Handler().postDelayed(Runnable {
                                (mContext as DashboardActivity).checkToShowHomeLocationAlert()
                            }, 200)
                        })
        )
    }


    private fun checkToCallMemberList() {
        XLog.e("==============checkToCallMemberList(Dashboard Fragment)==============")
        Handler().postDelayed(Runnable {
            if (Pref.isOfflineTeam && Pref.isAddAttendence) {
                var progress_wheel: ProgressWheel? = null
                if (Pref.isAttendanceFeatureOnly)
                    progress_wheel = progress_wheel_attendance
                else
                    progress_wheel = this.progress_wheel

                progress_wheel?.spin()

                doAsync {

                    val list = AppDatabase.getDBInstance()?.memberDao()?.getAll()

                    uiThread {
                        progress_wheel.stopSpinning()
                        if (list == null || list.isEmpty())
                            callMemberListApi()
                    }
                }
            }
        }, 200)
    }

    private fun callMemberListApi() {
        //return
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }


        if (customProgressDialog.isAdded)
            return

        XLog.e("==============call offline member api(Dashboard Fragment)==============")

        val repository = TeamRepoProvider.teamRepoProvider()
        customProgressDialog.show((mContext as DashboardActivity).supportFragmentManager, "")
        BaseActivity.compositeDisposable.add(
                repository.offlineTeamList("")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.member_list != null && response.member_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.member_list?.forEach {
                                            val member = MemberEntity()
                                            AppDatabase.getDBInstance()?.memberDao()?.insertAll(member.apply {
                                                user_id = it.user_id
                                                user_name = it.user_name
                                                contact_no = it.contact_no
                                                super_id = it.super_id
                                                super_name = it.super_name
                                                date_time = AppUtils.getCurrentISODateTime()
                                            })
                                        }

                                        uiThread {
                                            //callMemberShopListApi()
                                            customProgressDialog.dismiss()
                                        }
                                    }
                                } else {
                                    //callMemberShopListApi()
                                    customProgressDialog.dismiss()
                                }

                            } else if (response.status == NetworkConstant.NO_DATA) {
                                //callMemberShopListApi()
                                customProgressDialog.dismiss()
                            } else if (response.status == NetworkConstant.SESSION_MISMATCH) {
//                                (mContext as DashboardActivity).clearData()
                                customProgressDialog.dismiss()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else {
                                customProgressDialog.dismiss()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            customProgressDialog.dismiss()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun callMemberShopListApi() {

        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.offlineTeamShopList("")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamShopListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.shop_list != null && response.shop_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.shop_list?.forEach {
                                            val memberShop = MemberShopEntity()
                                            AppDatabase.getDBInstance()?.memberShopDao()?.insertAll(memberShop.apply {
                                                user_id = it.user_id
                                                shop_id = it.shop_id
                                                shop_name = it.shop_name
                                                shop_lat = it.shop_lat
                                                shop_long = it.shop_long
                                                shop_address = it.shop_address
                                                shop_pincode = it.shop_pincode
                                                shop_contact = it.shop_contact
                                                total_visited = it.total_visited
                                                last_visit_date = it.last_visit_date
                                                shop_type = it.shop_type
                                                dd_name = it.dd_name
                                                entity_code = it.entity_code
                                                model_id = it.model_id
                                                primary_app_id = it.primary_app_id
                                                secondary_app_id = it.secondary_app_id
                                                lead_id = it.lead_id
                                                funnel_stage_id = it.funnel_stage_id
                                                stage_id = it.stage_id
                                                booking_amount = it.booking_amount
                                                type_id = it.type_id
                                                area_id = it.area_id
                                                assign_to_pp_id = it.assign_to_pp_id
                                                assign_to_dd_id = it.assign_to_dd_id
                                                isUploaded = true
                                                date_time = AppUtils.getCurrentISODateTime()
                                            })
                                        }

                                        uiThread {
                                            customProgressDialog.dismiss()
                                        }
                                    }
                                }

                            } else if (response.status == NetworkConstant.SESSION_MISMATCH) {
//                                (mContext as DashboardActivity).clearData()
                                customProgressDialog.dismiss()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else {
                                customProgressDialog.dismiss()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            customProgressDialog.dismiss()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    fun updateUi() {

        val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()
        XLog.e("=======UPDATE UI FOR AUTO REVISIT (DASHBOARD FRAGMENT)=======")
        XLog.e("Today's Shop Visit Count====> $todaysShopVisitCount")

        avgShop.text = todaysShopVisitCount
        avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

        when {
            Pref.willActivityShow -> avgOrder.text = InfoWizard.getActivityForToday()
            Pref.isQuotationShow -> avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
            else -> avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
        }

        UpdateLocationData()

        if (!TextUtils.isEmpty(Pref.isFieldWorkVisible) && Pref.isFieldWorkVisible.equals("false", ignoreCase = true)) {
            val list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getAll()
            if (Pref.isAddAttendence && (list == null || list.isEmpty()))
                getSelectedRouteList()
            else
                initBottomAdapter()
        } else
            initBottomAdapter()
    }

    fun refresh() {
        println("dash_refresh call")
        CustomStatic.IsSnycClickablebyOnce = true
        (mContext as DashboardActivity).iv_sync_icon.isEnabled = false

        /*API_Optimization 02-03-2022*/
        //checkToCallAssignedDDListApi()

        if(AppUtils.isOnline(mContext)){
            disableScreen()
        }


        //callUserConfigApi()   // calling instead of checkToCallAssignedDDListApi()
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
                                AppDatabase.getDBInstance()?.beatDao()?.delete()
                                list.forEach {
                                    val beat = BeatEntity()
                                    AppDatabase.getDBInstance()?.beatDao()?.insert(beat.apply {
                                        beat_id = it.id
                                        name = it.name
                                    })
                                }
                                uiThread {
                                    progress_wheel.stopSpinning()
                                    callUserConfigApi()
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            callUserConfigApi()
                        }
                    } else {
                        progress_wheel.stopSpinning()
                        callUserConfigApi()
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    callUserConfigApi()
                })
        )
    }

    private fun checkToCallAssignedDDListApi() {
        if (!TextUtils.isEmpty(Pref.profile_state))
            getAssignedDDListApi()
        else {
            if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                getProductList("")
            else
                getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
        }
    }

    private fun getAssignedDDListApi() {
        val repository = AssignToDDListRepoProvider.provideAssignDDListRepository()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
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
                                            if (!TextUtils.isEmpty(Pref.profile_state))
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
                                    if (!TextUtils.isEmpty(Pref.profile_state))
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
                                if (!TextUtils.isEmpty(Pref.profile_state))
                                    getAssignedPPListApi()
                                else {
                                    if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                        getProductList("")
                                    else
                                        getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                                }
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            if (!TextUtils.isEmpty(Pref.profile_state))
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
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
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
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
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

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (AppDatabase.getDBInstance()?.productListDao()?.getAll()!!.isEmpty())
                                getProductList("")
                            else
                                getProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()?.get(0)?.date)
                        })
        )
    }


    private fun getProductList(date: String?) {
        if(Pref.isOrderShow){
            XLog.d("API_Optimization  getProductList DashFrag  : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
        val repository = ProductListRepoProvider.productListProvider()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
        BaseActivity.compositeDisposable.add(
                //repository.getProductList(Pref.session_token!!, Pref.user_id!!, date!!)
                repository.getProductList(Pref.session_token!!, Pref.user_id!!, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ProductListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.product_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        if (!TextUtils.isEmpty(date))
                                            AppDatabase.getDBInstance()?.productListDao()?.deleteAllProduct()

                                        AppDatabase.getDBInstance()?.productListDao()?.insertAll(list!!)

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
                                            getSelectedRouteListRefresh()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getSelectedRouteListRefresh()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getSelectedRouteListRefresh()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            getSelectedRouteListRefresh()
                        })
        )
        }else{
            XLog.d("API_Optimization getProductList DashFrag : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            getSelectedRouteListRefresh()
        }
    }

    private fun getSelectedRouteListRefresh() {
        val list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getAll()
        if (list != null && list.isNotEmpty()) {
            AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.delete()
            AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.deleteData()
            AppDatabase.getDBInstance()?.selectedRouteListDao()?.deleteRoute()
        }

        val repository = GetRouteListRepoProvider.routeListRepoProvider()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
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
                                            /*API_Optimization 02-03-2022*/
                                            //callUserConfigApi()
                                            checkToCallAlarmConfigApi()   // calling instead of callUserConfigApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    /*API_Optimization 02-03-2022*/
                                    //callUserConfigApi()
                                    checkToCallAlarmConfigApi()   // calling instead of callUserConfigApi()
                                }
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

    private fun callUserConfigApi() {
        val repository = UserConfigRepoProvider.provideUserConfigRepository()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
        BaseActivity.compositeDisposable.add(
                repository.userConfig(Pref.user_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UserConfigResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {

                                try {

                                    Log.e("Dashboard", "willLeaveApprovalEnable================> " + Pref.willLeaveApprovalEnable)


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
                                                    AppUtils.saveSharedPreferenceslogShareinLogin(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferenceslogShareinLogin(mContext, false)
                                                }

                                            } else if (response.getconfigure!![i].Key.equals("IsCompetitorenable", ignoreCase = true)) {
                                                Pref.isCompetitorImgEnable = response.getconfigure!![i].Value == "1"
                                                if (Pref.isCompetitorImgEnable) {
                                                    AppUtils.saveSharedPreferencesCompetitorImgEnable(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesCompetitorImgEnable(mContext, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsOrderStatusRequired", ignoreCase = true)) {
                                                Pref.isOrderStatusRequired = response.getconfigure!![i].Value == "1"
                                                if (Pref.isOrderStatusRequired) {
                                                    AppUtils.saveSharedPreferencesOrderStatusRequired(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesOrderStatusRequired(mContext, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsCurrentStockEnable", ignoreCase = true)) {
                                                Pref.isCurrentStockEnable = response.getconfigure!![i].Value == "1"

                                                if (Pref.isCurrentStockEnable) {
                                                    AppUtils.saveSharedPreferencesCurrentStock(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesCurrentStock(mContext, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsCurrentStockApplicableforAll", ignoreCase = true)) {
                                                Pref.IsCurrentStockApplicableforAll = response.getconfigure!![i].Value == "1"

                                                if (Pref.IsCurrentStockApplicableforAll) {
                                                    AppUtils.saveSharedPreferencesCurrentStockApplicableForAll(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesCurrentStockApplicableForAll(mContext, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IscompetitorStockRequired", ignoreCase = true)) {
                                                Pref.IscompetitorStockRequired = response.getconfigure!![i].Value == "1"

                                                if (Pref.IscompetitorStockRequired) {
                                                    AppUtils.saveSharedPreferencesIscompetitorStockRequired(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIscompetitorStockRequired(mContext, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsCompetitorStockforParty", ignoreCase = true)) {
                                                Pref.IsCompetitorStockforParty = response.getconfigure!![i].Value == "1"

                                                if (Pref.IsCompetitorStockforParty) {
                                                    AppUtils.saveSharedPreferencesIsCompetitorStockforParty(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsCompetitorStockforParty(mContext, false)
                                                }
                                            }
//                                            else if (response.getconfigure!![i].Key.equals("IsFaceDetectionOn", ignoreCase = true)) {
                                            else if (response.getconfigure!![i].Key.equals("ShowFaceRegInMenu", ignoreCase = true)) {
                                                Pref.IsFaceDetectionOn = response.getconfigure!![i].Value == "1"
                                                if (Pref.IsFaceDetectionOn) {
                                                    AppUtils.saveSharedPreferencesIsFaceDetectionOn(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsFaceDetectionOn(mContext, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsFaceDetection", ignoreCase = true)) {
                                                Pref.IsFaceDetection = response.getconfigure!![i].Value == "1"
                                                if (Pref.IsFaceDetection) {
                                                    AppUtils.saveSharedPreferencesIsFaceDetection(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsFaceDetection(mContext, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsFaceDetectionWithCaptcha", ignoreCase = true)) {
                                                Pref.IsFaceDetectionWithCaptcha = response.getconfigure!![i].Value == "1"

                                                if (Pref.IsFaceDetectionWithCaptcha) {
                                                    AppUtils.saveSharedPreferencesIsFaceDetectionWithCaptcha(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsFaceDetectionWithCaptcha(mContext, false)
                                                }
                                            } else if (response.getconfigure!![i].Key.equals("IsScreenRecorderEnable", ignoreCase = true)) {
                                                Pref.IsScreenRecorderEnable = response.getconfigure!![i].Value == "1"
                                                if (Pref.IsScreenRecorderEnable) {
                                                    AppUtils.saveSharedPreferencesIsScreenRecorderEnable(mContext, true)
                                                } else {
                                                    AppUtils.saveSharedPreferencesIsScreenRecorderEnable(mContext, false)
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
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsshowDayStartSelfie", ignoreCase = true)) {
                                                Pref.IsshowDayStartSelfie = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsshowDayStartSelfie = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsShowDayEnd", ignoreCase = true)) {
                                                Pref.IsShowDayEnd = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsShowDayEnd = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsshowDayEndSelfie", ignoreCase = true)) {
                                                Pref.IsshowDayEndSelfie = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsshowDayEndSelfie = response.getconfigure?.get(i)?.Value == "1"
                                                }
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
                                            else if (response.getconfigure?.get(i)?.Key.equals("FaceDetectionAccuracyUpper", ignoreCase = true)) {
                                                Pref.FaceDetectionAccuracyUpper = response.getconfigure!![i].Value!!
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.FaceDetectionAccuracyUpper = response.getconfigure?.get(i)?.Value!!
                                                }
                                                CustomStatic.FaceDetectionAccuracyUpper = Pref.FaceDetectionAccuracyUpper
                                            } else if (response.getconfigure?.get(i)?.Key.equals("FaceDetectionAccuracyLower", ignoreCase = true)) {
                                                Pref.FaceDetectionAccuracyLower = response.getconfigure!![i].Value!!
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.FaceDetectionAccuracyLower = response.getconfigure?.get(i)?.Value!!
                                                }
                                                CustomStatic.FaceDetectionAccuracyLower = Pref.FaceDetectionAccuracyLower
                                            } else if (response.getconfigure?.get(i)?.Key.equals("DistributorGPSAccuracy", ignoreCase = true)) {
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
                                            /*16-12-2021 return features*/
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsReturnEnableforParty", ignoreCase = true)) {
                                                Pref.IsReturnEnableforParty = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsReturnEnableforParty = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("MRPInOrder", ignoreCase = true)) {
                                                Pref.MRPInOrder = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.MRPInOrder = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("FaceRegistrationFrontCamera", ignoreCase = true)) {
                                                Pref.FaceRegistrationFrontCamera = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.FaceRegistrationFrontCamera = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("IslandlineforCustomer", ignoreCase = true)) {
                                                Pref.IslandlineforCustomer = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IslandlineforCustomer = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsprojectforCustomer", ignoreCase = true)) {
                                                Pref.IsprojectforCustomer = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsprojectforCustomer = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("Leaveapprovalfromsupervisor", ignoreCase = true)) {
                                                Pref.Leaveapprovalfromsupervisor = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.Leaveapprovalfromsupervisor = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("Leaveapprovalfromsupervisorinteam", ignoreCase = true)) {
                                                Pref.Leaveapprovalfromsupervisorinteam = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.Leaveapprovalfromsupervisorinteam = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsRestrictNearbyGeofence", ignoreCase = true)) {
                                                Pref.IsRestrictNearbyGeofence = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsRestrictNearbyGeofence = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsNewQuotationfeatureOn", ignoreCase = true)) {
                                                Pref.IsNewQuotationfeatureOn = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsNewQuotationfeatureOn = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsAlternateNoForCustomer", ignoreCase = true)) {
                                                Pref.IsAlternateNoForCustomer = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsAlternateNoForCustomer = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("IsWhatsappNoForCustomer", ignoreCase = true)) {
                                                Pref.IsWhatsappNoForCustomer = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsWhatsappNoForCustomer = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }
                                            else if (response.getconfigure?.get(i)?.Key.equals("IsNewQuotationNumberManual", ignoreCase = true)) {
                                                Pref.IsNewQuotationNumberManual = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.IsNewQuotationNumberManual = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("ShowQuantityNewQuotation", ignoreCase = true)) {
                                                Pref.ShowQuantityNewQuotation = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowQuantityNewQuotation = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("ShowAmountNewQuotation", ignoreCase = true)) {
                                                Pref.ShowAmountNewQuotation = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowQuantityNewQuotation = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            } else if (response.getconfigure?.get(i)?.Key.equals("ShowUserwiseLeadMenu", ignoreCase = true)) {
                                                Pref.ShowUserwiseLeadMenu = response.getconfigure!![i].Value == "1"
                                                if (!TextUtils.isEmpty(response.getconfigure?.get(i)?.Value)) {
                                                    Pref.ShowUserwiseLeadMenu = response.getconfigure?.get(i)?.Value == "1"
                                                }
                                            }else if (response.getconfigure?.get(i)?.Key.equals("GeofencingRelaxationinMeter", ignoreCase = true)) {
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
                                            }else if (response.getconfigure?.get(i)?.Key.equals("ShopScreenAftVisitRevisit", ignoreCase = true)) {
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


                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            Log.e("Dashboard", "willLeaveApprovalEnable================> " + Pref.willLeaveApprovalEnable)

                            progress_wheel.stopSpinning()
                            getConfigFetchApi()

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            getConfigFetchApi()
                        })
        )
    }

    private fun getConfigFetchApi() {
        val repository = ConfigFetchRepoProvider.provideConfigFetchRepository()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
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

                                //if (configResponse.SqMtrRateCalculationforQuotEuro != null)
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

                            }
                            BaseActivity.isApiInitiated = false
                            /*API_Optimization 02-03-2022*/
                            //checkToCallAlarmConfigApi()
                            checkToCallAssignedDDListApi()   // calling instead of checkToCallAlarmConfigApi()

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            /*API_Optimization 02-03-2022*/
                            //checkToCallAlarmConfigApi()
                            checkToCallAssignedDDListApi()   // calling instead of checkToCallAlarmConfigApi()
                            progress_wheel.stopSpinning()
                            XLog.d("ConfigFetchApiResponse ERROR: " + error.localizedMessage)
                        })
        )
    }

    private fun checkToCallAlarmConfigApi() {
        if (Pref.willAlarmTrigger)
            callAlarmConfig()
        else
            getPjpListApi()

    }

    private fun callAlarmConfig() {
        val repository = AlarmConfigRepoProvider.provideAlarmConfigRepository()
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
        BaseActivity.compositeDisposable.add(
                repository.alarmConfig()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val configResponse = result as AlarmConfigResponseModel
                            XLog.d("AlarmConfigApiResponse : " + "\n" + "Status=====> " + configResponse.status + ", Message====> " + configResponse.message)

                            progress_wheel.stopSpinning()
                            if (configResponse.status == NetworkConstant.SUCCESS) {

                                val alarmArr = java.util.ArrayList<AlarmData>()
                                for (item in configResponse.alarm_settings_list!!) {

                                    if (AppUtils.getCurrentTimeInMintes() < ((item.alarm_time_hours!!.toInt() * 60) + item.alarm_time_mins!!.toInt())) {
                                        val al = AlarmData()
                                        al.requestCode = 2010 + item.id!!.toInt()
                                        al.id = item.id
                                        al.report_id = item.report_id!!
                                        al.report_title = item.report_title!!
                                        al.alarm_time_hours = item.alarm_time_hours!!
                                        al.alarm_time_mins = item.alarm_time_mins!!

                                        alarmArr.add(al)

                                        AlarmReceiver.setAlarm(mContext, item.alarm_time_hours!!.toInt(), item.alarm_time_mins!!.toInt(), al.requestCode)
                                    }

                                }

                                AlarmReceiver.saveSharedPreferencesLogList(mContext, alarmArr)
                            }

                            BaseActivity.isApiInitiated = false
                            getPjpListApi()
                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("AlarmConfigApiResponse ERROR: " + error.localizedMessage)
                            getPjpListApi()
                        })
        )
    }

    private fun getPjpListApi() {
        if(Pref.isActivatePJPFeature){
            XLog.d("API_Optimization-> GET getPjpListApi : enable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
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
                            XLog.d("GET USER PJP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            getTeamAreaListApi()
                        })
        )
        }else{
            XLog.d("API_Optimization-> GET getPjpListApi : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            getTeamAreaListApi()
        }
    }

    private fun getTeamAreaListApi() {
        if(Pref.isOfflineTeam) {
            XLog.d("API_Optimization-> isOfflineTeam Enable: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            val repository = TeamRepoProvider.teamRepoProvider()
            var progress_wheel: ProgressWheel? = null
            if (Pref.isAttendanceFeatureOnly)
                progress_wheel = progress_wheel_attendance
            else
                progress_wheel = this.progress_wheel

            progress_wheel?.spin()
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
        }
        else{
            XLog.d("API_Optimization-> isOfflineTeam Disable: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            getTimesheetDropdownApi()
        }
    }

    private fun getTimesheetDropdownApi() {
        if (Pref.willTimesheetShow) {
            XLog.d("API_Optimization-> TIMESHEET Enable: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
            var progress_wheel: ProgressWheel? = null
            if (Pref.isAttendanceFeatureOnly)
                progress_wheel = progress_wheel_attendance
            else
                progress_wheel = this.progress_wheel

            progress_wheel?.spin()
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
        }
        else{
            XLog.d("API_Optimization-> TIMESHEET Disable: " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            gePrimaryAppListApi()
        }
    }

    private fun getTimesheetConfig() {
        var progress_wheel: ProgressWheel? = null
        if (Pref.isAttendanceFeatureOnly)
            progress_wheel = progress_wheel_attendance
        else
            progress_wheel = this.progress_wheel

        progress_wheel?.spin()
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
                            }

                            gePrimaryAppListApi()

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("TIMESHEET CONFIG: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            gePrimaryAppListApi()
                        })
        )
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
                                            geSecondaryAppListApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    geSecondaryAppListApi()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                geSecondaryAppListApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET PRIMARY APP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            geSecondaryAppListApi()
                        })
        )
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
                                            geLeadApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    geLeadApi()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                geLeadApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET SECONDARY APP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            geLeadApi()
                        })
        )
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
                                            geStageApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    geStageApi()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                geStageApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET LEAD TYPE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            geStageApi()
                        })
        )
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
                                            geFunnelStageApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    geFunnelStageApi()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                geFunnelStageApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET STAGE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            geFunnelStageApi()
                        })
        )
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
                                            getEntityTypeListApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getEntityTypeListApi()
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                getEntityTypeListApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET FUNNEL STAGE DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            getEntityTypeListApi()
                        })
        )
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
                                    AppDatabase.getDBInstance()?.entityDao()?.delete()
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
                                            getPartyStatusListApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getPartyStatusListApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getPartyStatusListApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            getPartyStatusListApi()
                        })
        )
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
                                    AppDatabase.getDBInstance()?.partyStatusDao()?.delete()
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
                                            getMeetingTypeListApi()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getMeetingTypeListApi()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getMeetingTypeListApi()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            getMeetingTypeListApi()
                        })
        )
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
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.meeting_type_list != null && response.meeting_type_list!!.size > 0) {

                                    AppDatabase.getDBInstance()!!.addMeetingTypeDao().deleteAll()

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
                                            getRemarksList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getRemarksList()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getRemarksList()
                            }

                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
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
                                        //changeUI()
                                        getDocumentTypeApi()
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                //changeUI()
                                getDocumentTypeApi()
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            XLog.d("Visit Remarks List : ERROR " + error.localizedMessage)
                            //changeUI()
                            getDocumentTypeApi()
                        })
        )
    }

    private fun getDocumentTypeApi() {
        if (Pref.isDocumentRepoShow) {
            XLog.d("API_Optimization GET getDocumentListApi dashboard : enable " + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)
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
                                            AppDatabase.getDBInstance()?.documentTypeDao()?.delete()
                                            var list = response.type_list
                                            for (i in 0..list!!.size - 1) {
                                                var docTypeEntity: DocumentypeEntity = DocumentypeEntity()
                                                docTypeEntity.type_id = list.get(i).id
                                                docTypeEntity.type_name = list.get(i).type
                                                docTypeEntity.image = list.get(i).image
                                                docTypeEntity.IsForOrganization = list.get(i).IsForOrganization
                                                docTypeEntity.IsForOwn = list.get(i).IsForOwn

                                                AppDatabase.getDBInstance()?.documentTypeDao()?.insert(docTypeEntity)
                                            }

                                            /*response.type_list?.forEach {
                                            val docTypeEntity = DocumentypeEntity()
                                            AppDatabase.getDBInstance()?.documentTypeDao()?.insert(docTypeEntity.apply {
                                                type_id = it.id
                                                type_name = it.type
                                                image = it.image
                                                IsForOrganization = it.IsForOrganization
                                                IsForOwn = it.IsForOwn
                                            })
                                        }*/

                                            uiThread {
                                                progress_wheel.stopSpinning()
                                                getDocumentListApi()
                                            }
                                        }
                                    } else {
                                        progress_wheel.stopSpinning()
                                        getDocumentListApi()
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getDocumentListApi()
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                XLog.d("DOCUMENT TYPE LIST ERROR=======> " + error.localizedMessage)
                                getDocumentListApi()
                            })
            )
        }
        else{
            XLog.d("API_Optimization GET getDocumentListApi  dashboard : disable " +  "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name )
            changeUI()
        }
    }

    private fun getDocumentListApi() {
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
                                        //AppDatabase.getDBInstance()?.documentListDao()?.deleteAll()
                                        AppDatabase.getDBInstance()?.documentListDao()?.deleteAllHttp()

                                        var list=response.doc_list
                                        for(i in 0..list!!.size-1){
                                            if(list.get(i).attachment.contains("Commonfolder")){
                                                var docListEntity:DocumentListEntity = DocumentListEntity()
                                                docListEntity.list_id = list.get(i).id
                                                docListEntity.type_id = list.get(i).type_id
                                                docListEntity.date_time = list.get(i).date_time
                                                docListEntity.attachment = list.get(i).attachment
                                                docListEntity.isUploaded = true
                                                docListEntity.document_name = list.get(i).document_name
                                                AppDatabase.getDBInstance()?.documentListDao()?.insert(docListEntity)
                                            }


                                        }

                                        /*response.doc_list?.forEach {
                                            val docListEntity = DocumentListEntity()
                                            AppDatabase.getDBInstance()?.documentListDao()?.insert(docListEntity.apply {
                                                list_id = it.id
                                                type_id = it.type_id
                                                date_time = it.date_time
                                                attachment = it.attachment
                                                isUploaded = true
                                                document_name = it.document_name
                                            })
                                        }*/

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            changeUI()
                                        }
                                    }

                                } else {
                                    progress_wheel.stopSpinning()
                                    changeUI()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                changeUI()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            changeUI()
                            XLog.d("DOCUMENT LIST ERROR=======> " + error.localizedMessage)
                        })
        )
    }

    @SuppressLint("RestrictedApi")
    private fun changeUI() {
        tv_shop.text = Pref.shopText + "(s)"

        if (Pref.willShowUpdateDayPlan)
            tv_view_all.visibility = View.VISIBLE
        else
            tv_view_all.visibility = View.GONE

        val todaysShopVisitCount = InfoWizard.getAvergareShopVisitCount()

        avgShop.text = todaysShopVisitCount
        avgTime.text = InfoWizard.getAverageShopVisitTimeDuration() + " Hrs"

        //best_performing_shop_TV.text = getString(R.string.todays_task)
        best_performing_shop_TV.text = "${Pref.TodaysTaskText}"

        tv_pick_date_range.text = AppUtils.getFormattedDate(myCalendar.time)

        if (Pref.isAttendanceFeatureOnly) {
            ll_attendance_report_main.visibility = View.VISIBLE
            rl_dashboard_fragment_main.visibility = View.GONE
        } else {
            ll_attendance_report_main.visibility = View.GONE
            rl_dashboard_fragment_main.visibility = View.VISIBLE
        }

        if (!Pref.isMeetingAvailable && !Pref.isShopAddEditAvailable)
            fab.visibility = View.GONE
        else
            fab.visibility = View.VISIBLE

        if (Pref.isServiceFeatureEnable) {
            tv_order.text = getString(R.string.myjobs)
            iv_order_icon.setImageResource(R.drawable.ic_activity_white)
            iv_order_icon.visibility = View.VISIBLE
            iv_quot_icon.visibility = View.GONE
            shop_tome_order_tab_LL.visibility = View.GONE
        } else if (Pref.willActivityShow) {
//            Toaster.msgShort(mContext,Pref.willActivityShow.toString())
            tv_order.text = getString(R.string.activities)
            no_of_order_TV.text = getString(R.string.today_activity)
            avgOrder.text = InfoWizard.getActivityForToday()
            iv_order_icon.setImageResource(R.drawable.ic_activity_white)
            iv_order_icon.visibility = View.VISIBLE
            iv_quot_icon.visibility = View.GONE
            shop_tome_order_tab_LL.visibility = View.VISIBLE
        } else {
            shop_tome_order_tab_LL.visibility = View.VISIBLE
            if (Pref.isQuotationShow) {
                tv_order.text = getString(R.string.quotation)
                no_of_order_TV.text = getString(R.string.total_quot)
                avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalQuotAmountForToday()
                iv_order_icon.visibility = View.GONE
                iv_quot_icon.visibility = View.VISIBLE
            } else {
                no_of_order_TV.text = getString(R.string.total_order_value_new)
                avgOrder.text = getString(R.string.rupee_symbol) + InfoWizard.getTotalOrderAmountForToday()
                iv_order_icon.visibility = View.VISIBLE
                iv_quot_icon.visibility = View.GONE

                if (Pref.isOrderReplacedWithTeam) {
                    tv_order.text = getString(R.string.team_details)
                    iv_order_icon.setImageResource(R.drawable.ic_team_icon)
                    ll_dash_total_order_newD.visibility = View.GONE
//                    price_RL.visibility = View.GONE
                } else {
                    tv_order.text = getString(R.string.orders)
                    iv_order_icon.setImageResource(R.drawable.ic_dashboard_order_icon)

                    if (Pref.isOrderShow) {
                        order_ll.visibility = View.VISIBLE
                        //price_RL.visibility = View.VISIBLE
                        ll_dash_total_order_newD.visibility = View.VISIBLE

//                        avgOrder.visibility = View.VISIBLE
//                        no_of_order_TV.visibility = View.VISIBLE
                    } else {
                        order_ll.visibility = View.GONE
                        ll_dash_total_order_newD.visibility = View.GONE
                        //price_RL.visibility = View.VISIBLE
//                        avgOrder.visibility = View.GONE
//                        no_of_order_TV.visibility = View.GONE
                    }
                }
            }
        }

        if (Pref.isChatBotShow)
            fab_bot.visibility = View.VISIBLE
        else
            fab_bot.visibility = View.GONE

        if (Pref.isShowTimeline)
            history_ll.visibility = View.VISIBLE
        else
            history_ll.visibility = View.GONE

        if (Pref.IsShowMarkDistVisitOnDshbrd) {
            ll_dash_point_visit_newD.visibility = View.VISIBLE
//            DDVisit_TV.visibility = View.VISIBLE
//            DDVisitdate_TV.visibility = View.VISIBLE
//            DDVisit_Rl.visibility = View.VISIBLE
        } else {
            ll_dash_point_visit_newD.visibility = View.GONE
//            DDVisit_TV.visibility = View.GONE
//            DDVisitdate_TV.visibility = View.GONE
//            DDVisit_Rl.visibility = View.GONE
        }

        if(Pref.IsShowDayStart){
            ll_dash_day_start_newD.visibility = View.VISIBLE
//            StartRL.visibility = View.VISIBLE
//            start_shop.visibility = View.VISIBLE
        }
        else{
            ll_dash_day_start_newD.visibility = View.GONE
//            StartRL.visibility = View.GONE
//            start_shop.visibility = View.GONE

        }
        if(Pref.IsShowDayEnd){
            ll_dash_day_end_newD.visibility = View.VISIBLE
//            endRL.visibility = View.VISIBLE
//            enddate_TV.visibility = View.VISIBLE
        }
        else{
            ll_dash_day_end_newD.visibility = View.GONE
//            endRL.visibility = View.GONE
//            enddate_TV.visibility = View.GONE
        }

        if (Pref.IsShowTotalVisitsOnAppDashboard) {
            ll_dash_total_visit_newD.visibility = View.VISIBLE
//            shops_RL.visibility = View.VISIBLE
//            n_shops_TV.visibility = View.VISIBLE
//            no_of_shop_TV.visibility = View.VISIBLE
        } else {
//            shops_RL.visibility = View.GONE
            ll_dash_total_visit_newD.visibility = View.GONE
//            n_shops_TV.visibility = View.GONE
//            no_of_shop_TV.visibility = View.GONE
        }

        if (Pref.IsShowVisitDurationOnAppDashboard) {
//            time_RL.visibility = View.VISIBLE
            ll_dash_visit_duration_newD.visibility =  View.VISIBLE
//            n_time_TV.visibility = View.VISIBLE
//            no_of_time_TV.visibility = View.VISIBLE
        } else {
            ll_dash_visit_duration_newD.visibility =  View.GONE
//            time_RL.visibility = View.GONE
//            n_time_TV.visibility = View.GONE
//            no_of_time_TV.visibility = View.GONE
        }

        if (Pref.IsShowPartyOnAppDashboard)
            shop_ll.visibility = View.VISIBLE
        else
            shop_ll.visibility = View.GONE

        //Pref.IsShowAttendanceOnAppDashboard = true
        if (Pref.IsShowAttendanceOnAppDashboard)
            attandance_ll.visibility = View.VISIBLE
        else
            attandance_ll.visibility = View.GONE

        initAdapter()
        initBottomAdapter()

        CustomStatic.IsSnycClickablebyOnce = false
        (mContext as DashboardActivity).iv_sync_icon.isEnabled = true

        enableScreen()
        (mContext as DashboardActivity).updateUI()
    }


    companion object {
        lateinit var iv_screen_status: ImageView
        lateinit var tv_timer: TextView
        var hbRecorder: HBRecorder? = null
        lateinit var ll_recorder_root: LinearLayout
        var isRecordRootVisible: Boolean = false

    }


/*    fun timerRecord(isStart:Boolean){
        //screen_timer.setBase(SystemClock.elapsedRealtime());
        if(isStart){
            iv_screen_status.setImageResource(R.drawable.green_round)
            screen_timer.start()
        }else{
            iv_screen_status.setImageResource(R.drawable.red_circle)
            screen_timer.stop()
        }

    }

    fun cancelTimer(){
        screen_timer.setBase(SystemClock.elapsedRealtime())
    }*/


    @SuppressLint("UseRequireInsteadOfGet")
    private fun startRecordingScreen() {

        if (hbRecorder == null) {
            hbRecorder = HBRecorder(mContext, this)
        }

        //DashboardFragment.cancelTimer()
        //DashboardFragment.timerRecord(true)

        hbRecorder!!.enableCustomSettings()
        hbRecorder!!.setOutputFormat("MPEG_4")
        hbRecorder!!.isAudioEnabled(false)
        hbRecorder!!.recordHDVideo(false)
        hbRecorder!!.setVideoFrameRate(20)
        hbRecorder!!.setVideoBitrate(1000000)


        val mediaProjectionManager = getActivity()!!.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val permissionIntent = mediaProjectionManager?.createScreenCaptureIntent()
        startActivityForResult(permissionIntent, 271)
    }

    override fun HBRecorderOnError(errorCode: Int, reason: String?) {

    }

    override fun HBRecorderOnStart() {
        iv_screen_status.setImageResource(R.drawable.green_round)
        val intent = Intent(mContext, ScreenRecService::class.java)
        intent.action = CustomConstants.START_Screen_SERVICE
        mContext.startService(intent)
    }


    @SuppressLint("UseRequireInsteadOfGet")
    override fun HBRecorderOnComplete() {
        var intent = Intent(mContext, ScreenRecService::class.java)
        intent.action = CustomConstants.STOP_Screen_SERVICE
        mContext.stopService(intent)

        iv_screen_status.setImageResource(R.drawable.red_circle)

        //DashboardFragment.timerRecord(false)


        val path = hbRecorder!!.filePath
        val fileUrl = Uri.parse(path)
        val file = File(fileUrl.path)
//        val uri = Uri.fromFile(file)
        val uri:Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)


        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_simple_screen_rec)
        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_simple_ok) as AppCustomTextView
        val heading = simpleDialog.findViewById(R.id.dialog_simple_header_TV) as AppCustomTextView
        heading.text = "You may delete the recording file " + hbRecorder!!.fileName + " from storage after sharing it."
        dialogYes.setOnClickListener({ view ->
            simpleDialog.dismiss()
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "video/mp4"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            mContext.startActivity(Intent.createChooser(shareIntent, "Share video using"));
        })

        simpleDialog.show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 271) {
                /*           val intent = Intent(mContext, ScreenRecService::class.java)
                           intent.action = CustomConstants.START_Screen_SERVICE
                           mContext.startService(intent)*/
                hbRecorder!!.startScreenRecording(data, resultCode, mContext as Activity)
            }
            if (requestCode == 171){
                println("reg_face - dashboard_frag face Detect Face Match"+AppUtils.getCurrentDateTime());
                if(isCalledFromStart){
                    isStartCall=true
                    start_TV.performClick()
                }else{
                    isEndCall=true
                    end_TV.performClick()
                }
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event!!.getActionMasked()) {
            MotionEvent.ACTION_DOWN -> {
                dX = v!!.getX() - event.getRawX();
                dY = v!!.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
            }
            MotionEvent.ACTION_MOVE -> {
                v!!.setY(event.getRawY() + dY);
                v!!.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
            }
            MotionEvent.ACTION_UP -> {
                /* if (lastAction == MotionEvent.ACTION_DOWN)
                     Toast.makeText(mContext,"cl",Toast.LENGTH_SHORT).show()*/

            }
            else -> {
                return false
            }
        }
        return true;
    }


    ///// added on 13/08/2021

    private var isGetLocation = -1
    private var tagNoNearbyShop: Boolean = false

    //////start
   //30-08-2021
    fun getLocforStart() {
        if (AppUtils.isOnline(mContext)) {
            if (AppUtils.mLocation != null) {
                if (AppUtils.mLocation!!.accuracy <= Pref.gpsAccuracy.toInt()) {
                    if (AppUtils.mLocation!!.accuracy <= Pref.shopLocAccuracy.toFloat()) {
                        getNearyShopList(AppUtils.mLocation!!)
                    } else {
                        //getDDList(AppUtils.mLocation!!)
                        singleLocation()
                    }
                } else {
                    XLog.d("=====Inaccurate current location (Local Shop List)=====")
                    singleLocation()
                }
            } else {
                XLog.d("=====null location (Local Shop List)======")
                singleLocation()
            }
        } else
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

    }

    private fun singleLocation() {
        progress_wheel.spin()
        isGetLocation = -1
        SingleShotLocationProvider.requestSingleUpdate(mContext,
                object : SingleShotLocationProvider.LocationCallback {
                    override fun onStatusChanged(status: String) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(status: String) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(status: String) {
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNewLocationAvailable(location: Location) {
                        if (isGetLocation == -1) {
                            isGetLocation = 0
                            if (location.accuracy > Pref.gpsAccuracy.toInt()) {
                                (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                                progress_wheel.stopSpinning()
                            } else
                                getNearyShopList(location)
                        }
                    }

                })

        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                try {
                    if (isGetLocation == -1) {
                        isGetLocation = 1
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage("GPS data to show nearby party is inaccurate. Please stop " +
                                "internet, stop GPS/Location service, and then restart internet and GPS services to get nearby party list.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 15000)
    }

  /*  fun getNearyShopList(location: Location) {
        var nearBy: Double = Pref.shopLocAccuracy.toDouble()
        var shop_id: String = ""
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        var finalNearByDD: AssignToDDEntity = AssignToDDEntity()

        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        if (newList != null && newList.size > 0) {
            for (i in 0 until newList.size) {
                val shopLat: Double = newList[i].shopLat
                val shopLong: Double = newList[i].shopLong
                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                    if (isShopNearby) {
                        if ((location.distanceTo(shopLocation)) < nearBy) {
                            nearBy = location.distanceTo(shopLocation).toDouble()
                            finalNearByShop = newList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
        val newDDList = java.util.ArrayList<AssignToDDEntity>()
        for (i in allDDList.indices) {
            newDDList.add(allDDList[i])
        }

        if (newDDList != null && newDDList.size > 0) {
            for (i in 0 until newDDList.size) {
                val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                if (ddLat != null && ddLong != null) {
                    val ddLocation = Location("")
                    ddLocation.latitude = ddLat
                    ddLocation.longitude = ddLong
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                    if (isShopNearby) {
                        if ((location.distanceTo(ddLocation)) < nearBy) {
                            nearBy = location.distanceTo(ddLocation).toDouble()
                            finalNearByDD = newDDList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
            startDay(finalNearByShop, finalNearByDD, location, false)
        } else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
            startDay(finalNearByShop, finalNearByDD, location, true)
        } else {
            progress_wheel.stopSpinning()
            // 27-08-21 For ITC
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
            dialogHeader.text = "No nearby Shop/Distributor found..."
            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("No nearby Shop/Distributor found")
        }

    }*/
    fun getNearyShopList(location: Location) {
        var nearestDist=5000
        //var nearBy: Double = Pref.shopLocAccuracy.toDouble()
//        var nearBy: Double = 4000.00
        var nearBy: Double = Pref.DistributorGPSAccuracy.toDouble()
        var shop_id: String = ""
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        var finalNearByDD: AssignToDDEntity = AssignToDDEntity()

        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        if (newList != null && newList.size > 0) {
            for (i in 0 until newList.size) {
                val shopLat: Double = newList[i].shopLat
                val shopLong: Double = newList[i].shopLong
                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, Pref.DistributorGPSAccuracy.toInt())
                    var dist=location.distanceTo(shopLocation).toInt()  //21-10-2021
                    if (isShopNearby) {
                        if ((location.distanceTo(shopLocation)) < nearBy) {
                            nearBy = location.distanceTo(shopLocation).toDouble()
                            finalNearByShop = newList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }else{
                        if(dist<nearestDist){
                            nearestDist=dist
                        }
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
        val newDDList = java.util.ArrayList<AssignToDDEntity>()
        for (i in allDDList.indices) {
            newDDList.add(allDDList[i])
        }

        if (newDDList != null && newDDList.size > 0) {
            for (i in 0 until newDDList.size) {
                val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                if (ddLat != null && ddLong != null) {
                    val ddLocation = Location("")
                    ddLocation.latitude = ddLat
                    ddLocation.longitude = ddLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation,Pref.DistributorGPSAccuracy.toInt())
                    var dist=location.distanceTo(ddLocation).toInt()  //21-10-2021
                    if (isShopNearby) {
                        if ((location.distanceTo(ddLocation)) < nearBy) {
                            nearBy = location.distanceTo(ddLocation).toDouble()
                            finalNearByDD = newDDList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }else{
                        if(dist<nearestDist){
                            nearestDist=dist
                        }
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
            XLog.d("DAYSTART" + " , " + " Time :" + AppUtils.getCurrentDateTime()+"nearby dd found")
            startDay(finalNearByShop, finalNearByDD, location, false)
        } else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
            XLog.d("DAYSTART" + " , " + " Time :" + AppUtils.getCurrentDateTime()+"nearby shop found")
            startDay(finalNearByShop, finalNearByDD, location, true)
        } else {
            XLog.d("DAYSTART" + " , " + " Time :" + AppUtils.getCurrentDateTime()+"no nearby shop/dd "+"user lat: "+location.latitude.toString()+" long :"+location.longitude)

            progress_wheel.stopSpinning()
            // 27-08-21 For ITC
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
            if(nearestDist==5000){
                dialogHeader.text = "No nearby Shop/Point found..."+". Current location has been detected "+nearestDist.toString() +" mtr or more distance from the Distributor or Retail point from your handset GPS."
            }else{
                dialogHeader.text = "No nearby Shop/Point found..."+". Current location has been detected "+nearestDist.toString() +" mtr distance from the Distributor or Retail point from your handset GPS."
            }

            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("No nearby Shop/Distributor found")
        }

    }


    fun startDay(nearByShop: AddShopDBModelEntity, nearByDD: AssignToDDEntity, loc: Location, isShop: Boolean) {
        try {
            var dayst: DaystartDayendRequest = DaystartDayendRequest()
            dayst.user_id = Pref.user_id
            dayst.session_token = Pref.session_token
            dayst.date = AppUtils.getCurrentDateTime()
            dayst.location_name = LocationWizard.getNewLocationName(mContext, loc.latitude, loc.longitude)
            dayst.latitude = loc.latitude.toString()
            dayst.longitude = loc.longitude.toString()
            dayst.IsDDvistedOnceByDay = "0"
            dayst.visit_distributor_date_time = ""
            dayst.visit_distributor_id = ""
            dayst.visit_distributor_name = ""
            if (isShop) {
                //dayst.latitude = nearByShop.shopLat.toString()
                //dayst.longitude = nearByShop.shopLong.toString()
                dayst.shop_type = nearByShop.type
                dayst.shop_id = nearByShop.shop_id
                dayst.isStart = "1"
                dayst.isEnd = "0"
                dayst.sale_Value = "0.0"
                dayst.remarks = ""
            } else {
                //dayst.latitude = nearByDD.dd_latitude.toString()
                //dayst.longitude = nearByDD.dd_longitude.toString()
                dayst.shop_type = "4"
                dayst.shop_id = nearByDD.dd_id
                dayst.isStart = "1"
                dayst.isEnd = "0"
                dayst.sale_Value = "0.0"
                dayst.remarks = ""
            }

            val repository = DayStartEndRepoProvider.dayStartRepositiry()
            BaseActivity.compositeDisposable.add(
                    repository.dayStart(dayst)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                XLog.d("DashboardFragment DayStart : RESPONSE " + result.status)
                                val response = result as BaseResponse
                                if (response.status == NetworkConstant.SUCCESS) {
                                    //(mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                                    //voiceAttendanceMsg("Hi, your day start marked successfully.")
                                    isStartOrEndDay(false)
                                }
                            }, { error ->
                                if (error == null) {
                                    XLog.d("DashboardFragment DayStart : ERROR " + "UNEXPECTED ERROR IN DayStart API")
                                } else {
                                    XLog.d("DashboardFragment DayStart : ERROR " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                                progress_wheel.stopSpinning()
                            })
            )

        } catch (ex: Exception) {
            ex.printStackTrace()
            progress_wheel.stopSpinning()
        }

    }



    //////end 30-08-2021
    fun getLocforEnd() {
        if (AppUtils.isOnline(mContext)) {
            if (AppUtils.mLocation != null) {
                if (AppUtils.mLocation!!.accuracy <= Pref.gpsAccuracy.toInt()) {
                    if (AppUtils.mLocation!!.accuracy <= Pref.shopLocAccuracy.toFloat()) {
                        getNearyShopListEnd(AppUtils.mLocation!!)
                    } else {
                        //getDDList(AppUtils.mLocation!!)
                        singleLocationEnd()
                    }
                } else {
                    XLog.d("=====Inaccurate current location (Local Shop List)=====")
                    singleLocationEnd()
                }
            } else {
                XLog.d("=====null location (Local Shop List)======")
                singleLocationEnd()
            }
        } else
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

    }

    private fun singleLocationEnd() {
        progress_wheel.spin()
        isGetLocation = -1
        SingleShotLocationProvider.requestSingleUpdate(mContext,
                object : SingleShotLocationProvider.LocationCallback {
                    override fun onStatusChanged(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNewLocationAvailable(location: Location) {
                        if (isGetLocation == -1) {
                            isGetLocation = 0
                            if (location.accuracy > Pref.gpsAccuracy.toInt()) {
                                (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                                progress_wheel.stopSpinning()
                            } else
                                getNearyShopListEnd(location)
                        }
                    }

                })

        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                try {
                    if (isGetLocation == -1) {
                        isGetLocation = 1
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage("GPS data to show nearby party is inaccurate. Please stop " +
                                "internet, stop GPS/Location service, and then restart internet and GPS services to get nearby party list.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 15000)
    }

   /* fun getNearyShopListEnd(location: Location) {
        var nearBy: Double = Pref.shopLocAccuracy.toDouble()
        var shop_id: String = ""
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        var finalNearByDD: AssignToDDEntity = AssignToDDEntity()

        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        if (newList != null && newList.size > 0) {
            for (i in 0 until newList.size) {
                val shopLat: Double = newList[i].shopLat
                val shopLong: Double = newList[i].shopLong
                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                    if (isShopNearby) {
                        if ((location.distanceTo(shopLocation)) < nearBy) {
                            nearBy = location.distanceTo(shopLocation).toDouble()
                            finalNearByShop = newList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
        val newDDList = java.util.ArrayList<AssignToDDEntity>()
        for (i in allDDList.indices) {
            newDDList.add(allDDList[i])
        }

        if (newDDList != null && newDDList.size > 0) {
            for (i in 0 until newDDList.size) {
                val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                if (ddLat != null && ddLong != null) {
                    val ddLocation = Location("")
                    ddLocation.latitude = ddLat
                    ddLocation.longitude = ddLong
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                    if (isShopNearby) {
                        if ((location.distanceTo(ddLocation)) < nearBy) {
                            nearBy = location.distanceTo(ddLocation).toDouble()
                            finalNearByDD = newDDList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }


        //finalNearByDD=newDDList[5]

        if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
            endDay(finalNearByShop, finalNearByDD, location, false)
        } else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
            endDay(finalNearByShop, finalNearByDD, location, true)
        } else {
            progress_wheel.stopSpinning()
            // 27-08-21 For ITC
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
            dialogHeader.text = "No nearby Shop/Distributor found..."
            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("No nearby Shop/Distributor found")
        }

    }*/

    fun getNearyShopListEnd(location: Location) {
        var nearestDist=5000
        //var nearBy: Double = Pref.shopLocAccuracy.toDouble()
//        var nearBy: Double = 4000.00
        var nearBy: Double = Pref.DistributorGPSAccuracy.toDouble()
        var shop_id: String = ""
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        var finalNearByDD: AssignToDDEntity = AssignToDDEntity()

        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        if (newList != null && newList.size > 0) {
            for (i in 0 until newList.size) {
                val shopLat: Double = newList[i].shopLat
                val shopLong: Double = newList[i].shopLong
                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, Pref.DistributorGPSAccuracy.toInt())
                    var dist=location.distanceTo(shopLocation).toInt()  //21-10-2021
                    if (isShopNearby) {
                        if ((location.distanceTo(shopLocation)) < nearBy) {
                            nearBy = location.distanceTo(shopLocation).toDouble()
                            finalNearByShop = newList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }else{
                        if(dist<nearestDist){
                            nearestDist=dist
                        }
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
        val newDDList = java.util.ArrayList<AssignToDDEntity>()
        for (i in allDDList.indices) {
            newDDList.add(allDDList[i])
        }

        if (newDDList != null && newDDList.size > 0) {
            for (i in 0 until newDDList.size) {
                val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                if (ddLat != null && ddLong != null) {
                    val ddLocation = Location("")
                    ddLocation.latitude = ddLat
                    ddLocation.longitude = ddLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, Pref.DistributorGPSAccuracy.toInt())
                    var dist=location.distanceTo(ddLocation).toInt()  //21-10-2021
                    if (isShopNearby) {
                        if ((location.distanceTo(ddLocation)) < nearBy) {
                            nearBy = location.distanceTo(ddLocation).toDouble()
                            finalNearByDD = newDDList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }else{
                        if(dist<nearestDist){
                            nearestDist=dist
                        }
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }


        //finalNearByDD=newDDList[5]

        if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
            XLog.d("DAYEND" + " , " + " Time :" + AppUtils.getCurrentDateTime()+"nearby dd found")
            endDay(finalNearByShop, finalNearByDD, location, false)
        } else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
            XLog.d("DAYEND" + " , " + " Time :" + AppUtils.getCurrentDateTime()+"nearby shop found")
            endDay(finalNearByShop, finalNearByDD, location, true)
        } else {
            XLog.d("DAYEND" + " , " + " Time :" + AppUtils.getCurrentDateTime()+"no nearby shop/dd "+"user lat: "+location.latitude.toString()+" long :"+location.longitude)
            progress_wheel.stopSpinning()
            // 27-08-21 For ITC
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
            if(nearestDist==5000){
                dialogHeader.text = "No nearby Shop/Point found..."+". Current location has been detected "+nearestDist.toString() +" mtr or more distance from the Distributor or Retail point from your handset GPS."
            }else{
                dialogHeader.text = "No nearby Shop/Point found..."+". Current location has been detected "+nearestDist.toString() +" mtr distance from the Distributor or Retail point from your handset GPS."
            }

            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("No nearby Shop/Distributor found")
        }

    }

    fun endDay(nearByShop: AddShopDBModelEntity, nearByDD: AssignToDDEntity, loc: Location, isShop: Boolean) {
        progress_wheel.stopSpinning()
        var saleValue: String = ""
        /*if (isShop) {
            if (Pref.DayStartShopType.equals("1") && nearByShop.type.equals("1")) {
                progress_wheel.stopSpinning()
                (mContext as DashboardActivity).showSnackMessage("You must visit office to mark day end! Thanks.")
                return
            }
        }*/

        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_end_day_sale_value)
        val et_saleValue: EditText = simpleDialog.findViewById(R.id.dialog_et_sale_value) as EditText
        val submit = simpleDialog.findViewById(R.id.tv_dialog_submit) as AppCustomTextView

        et_saleValue.setOnFocusChangeListener({ v, hasFocus ->
            if (hasFocus) {
                et_saleValue.setBackgroundResource(R.drawable.blue_line_custom_selected)
            } else {
                et_saleValue.setBackgroundResource(R.drawable.blue_line_custom)
            }
        })

        /*  try {
              et_saleValue.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter(9, 2)))
          } catch (ex: Exception) {
              ex.printStackTrace()
          }*/
        submit.setOnClickListener({ view ->
            simpleDialog.cancel()

            saleValue = et_saleValue.text.toString()

            if (saleValue.length > 0) {
                try {
                    var dayst: DaystartDayendRequest = DaystartDayendRequest()
                    dayst.user_id = Pref.user_id
                    dayst.session_token = Pref.session_token
                    dayst.date = AppUtils.getCurrentDateTime()
                    dayst.location_name = LocationWizard.getNewLocationName(mContext, loc.latitude, loc.longitude)
                    dayst.latitude = loc.latitude.toString()
                    dayst.longitude = loc.longitude.toString()
                    if (Pref.IsDDvistedOnceByDay) {
                        dayst.IsDDvistedOnceByDay = "1"
                    } else {
                        dayst.IsDDvistedOnceByDay = "0"
                    }
                    dayst.visit_distributor_date_time = AppUtils.getCurrentDateTime()
                    dayst.visit_distributor_id = Pref.visit_distributor_id
                    dayst.visit_distributor_name = Pref.visit_distributor_name
                    if (isShop) {
                        //dayst.latitude = nearByShop.shopLat.toString()
                        //dayst.longitude = nearByShop.shopLong.toString()
                        dayst.shop_type = nearByShop.type
                        dayst.shop_id = nearByShop.shop_id
                        dayst.isStart = "0"
                        dayst.isEnd = "1"
                        dayst.sale_Value = saleValue
                        dayst.remarks = ""
                    } else {
                        //dayst.latitude = nearByDD.dd_latitude.toString()
                        //dayst.longitude = nearByDD.dd_longitude.toString()
                        dayst.shop_type = "4"
                        dayst.shop_id = nearByDD.dd_id
                        dayst.isStart = "0"
                        dayst.isEnd = "1"
                        dayst.sale_Value = saleValue
                        dayst.remarks = ""
                    }
                    val repository = DayStartEndRepoProvider.dayStartRepositiry()
                    BaseActivity.compositeDisposable.add(
                            repository.dayStart(dayst)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        XLog.d("DashboardFragment DayEnd : RESPONSE " + result.status)
                                        val response = result as BaseResponse
                                        if (response.status == NetworkConstant.SUCCESS) {
                                            //(mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                                            //voiceAttendanceMsg("Hi, your day end marked successfully.")
                                            isStartOrEndDay(true)
                                        }
                                    }, { error ->
                                        if (error == null) {
                                            XLog.d("DashboardFragment DayEnd : ERROR " + "UNEXPECTED ERROR IN DayStart API")
                                        } else {
                                            XLog.d("DashboardFragment DayEnd : ERROR " + error.localizedMessage)
                                            error.printStackTrace()
                                        }
                                        progress_wheel.stopSpinning()
                                    })
                    )

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    progress_wheel.stopSpinning()
                }
            } else {
                (mContext as DashboardActivity).showSnackMessage("Please enter sale value")
                progress_wheel.stopSpinning()
            }

        })
        simpleDialog.show()

    }


    fun isStartOrEndDay(isDayEnd: Boolean) {
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
                                XLog.d("DashboardFragment isStartOrEndDay : RESPONSE " + result.status + " " + AppUtils.getCurrentDateTime())
                                val response = result as StatusDayStartEnd
                                if (response.status == NetworkConstant.SUCCESS) {
                                    doAsync {
                                        Pref.DayStartMarked = response.DayStartMarked!!
                                        Pref.DayEndMarked = response.DayEndMarked!!
                                        Pref.DayStartShopType = response.day_start_shop_type!!
                                        Pref.DayStartShopID = response.day_start_shop_id!!
                                        if(Pref.IsDDvistedOnceByDay !=true){
                                            Pref.IsDDvistedOnceByDay = response.IsDDvistedOnceByDay!!
                                        }


                                        uiThread {
                                            progress_wheel.stopSpinning()

                                            //Pref.IsshowDayStartSelfie=true
                                            //Pref.IsshowDayEndSelfie=true

                                            if (isDayEnd == false) {
                                                if (Pref.IsshowDayStartSelfie) {
                                                    isCameraDayStart = true
                                                    initPermissionCheck()
                                                } else {
                                                    // 27-08-21 For ITC
                                                    val simpleDialog = Dialog(mContext)
                                                    simpleDialog.setCancelable(false)
                                                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                                    simpleDialog.setContentView(R.layout.dialog_message)
                                                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                                                    dialogHeader.text = "Thanks! Day started successfully."
                                                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                                    dialogYes.setOnClickListener({ view ->
                                                        simpleDialog.cancel()
                                                    })
                                                    simpleDialog.show()
//                                                    (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                                                    voiceAttendanceMsg("Hi, your day start marked successfully.")
                                                }
                                            }
                                            if (isDayEnd == true) {
                                                if (Pref.IsshowDayEndSelfie) {
                                                    isCameraDayStart = false
                                                    initPermissionCheck()
                                                } else {
                                                    // 27-08-21 For ITC
                                                    val simpleDialog = Dialog(mContext)
                                                    simpleDialog.setCancelable(false)
                                                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                                    simpleDialog.setContentView(R.layout.dialog_message)
                                                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                                                    dialogHeader.text = "Thanks! Day ended Successfully."
                                                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                                    dialogYes.setOnClickListener({ view ->
                                                        simpleDialog.cancel()
                                                        Handler(Looper.getMainLooper()).postDelayed({
                                                            (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                                        }, 2000)
                                                    })
                                                    simpleDialog.show()
//                                                    (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                                                    voiceAttendanceMsg("Hi, your day end marked successfully.")
                                                   /* Handler(Looper.getMainLooper()).postDelayed({
                                                        (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                                    }, 2000)*/
                                                }
                                            }
                                        }
                                    }

                                } else {
                                    progress_wheel.stopSpinning()
                                    //getListFromDatabase()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                }
                            }, { error ->
                                if (error == null) {
                                    XLog.d("DashboardFragment isStartOrEndDay : ERROR " + "UNEXPECTED ERROR IN DayStartEnd API")
                                } else {
                                    XLog.d("DashboardFragment isStartOrEndDay : ERROR " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                                progress_wheel.stopSpinning()
                                //getListFromDatabase()
                            })
            )
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
            //getListFromDatabase()
            progress_wheel.stopSpinning()
        }

    }

    private fun voiceAttendanceMsg(msg: String) {
        if (Pref.isVoiceEnabledForAttendanceSubmit) {
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Add Day Start", "TTS error in converting Text to Speech!");
        }
    }


    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                //showPictureDialog()
                launchCamera()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun launchCamera() {
        if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, (mContext as DashboardActivity).getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)*/

            (mContext as DashboardActivity).captureFrontImage()
        }
    }

    //fun setCameraImage(file: File) {
    fun setCameraImage(filePath: String) {
        var file = File(filePath)

        if (file == null || TextUtils.isEmpty(file.absolutePath)) {
            (mContext as DashboardActivity).showSnackMessage("Invalid Image")
            return
        }
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }


        if (isCameraDayStart && Pref.IsshowDayStartSelfie) {
            //Toaster.msgShort(mContext,"isCameraDayStart true")
            val repository = DashboardRepoProvider.provideDashboardImgRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.dayStartWithImage(file.absolutePath, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                progress_wheel.stopSpinning()
                                val response = result as BaseResponse
                                if (response.status == NetworkConstant.SUCCESS) {
                                    // 27-08-21 For ITC
                                    val simpleDialog = Dialog(mContext)
                                    simpleDialog.setCancelable(false)
                                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    simpleDialog.setContentView(R.layout.dialog_message)
                                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                                    //dialogHeader.text = "Thanks! Updated Successfully."
                                    dialogHeader.text = "Thanks! Day started successfully."
                                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                    dialogYes.setOnClickListener({ view ->
                                        simpleDialog.cancel()
                                    })
                                    simpleDialog.show()
//                                    (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                                    voiceAttendanceMsg("Hi, your day start marked successfully.")
                                } else {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                }
                            }, { error ->
                                error.printStackTrace()
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
        else if (isCameraDayStart == false && Pref.IsshowDayEndSelfie) {
            //Toaster.msgShort(mContext,"isCameraDayStart false")
            val repository = DashboardRepoProvider.provideDashboardImgRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.dayEndWithImage(file.absolutePath, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                progress_wheel.stopSpinning()
                                val response = result as BaseResponse
                                if (response.status == NetworkConstant.SUCCESS) {
                                    //19-08-21 Added for force day end
//                                     performLogout()
                                    // 27-08-21 For ITC
                                    val simpleDialog = Dialog(mContext)
                                    simpleDialog.setCancelable(false)
                                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    simpleDialog.setContentView(R.layout.dialog_message)
                                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                                    //dialogHeader.text = "Thanks! Updated Successfully."
                                    dialogHeader.text = "Thanks! Day ended Successfully."
                                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                    dialogYes.setOnClickListener({ view ->
                                        simpleDialog.cancel()
                                    })
                                    simpleDialog.show()
//                                    (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
                                    voiceAttendanceMsg("Hi, your day end marked successfully.")

                                    Handler(Looper.getMainLooper()).postDelayed({
                                        (mContext as DashboardActivity).loadFragment(FragType.LogoutSyncFragment, false, "")
                                    }, 2000)

                                } else {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                }
                            }, { error ->
                                error.printStackTrace()
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }

    }


    fun setImage(filePath: String) {
        val file = File(filePath)
        var newFile: File? = null
        progress_wheel.spin()
        doAsync {
            val processImage = ProcessImageUtils_v1(mContext, file, 50)
            newFile = processImage.ProcessImageSelfie()
            uiThread {
                if (newFile != null) {
                    XLog.e("=========Image from new technique==========")
                    val fileSize = AppUtils.getCompressImage(filePath)
                    setCameraImage(filePath)
                } else {
                    // Image compression
                    val fileSize = AppUtils.getCompressImage(filePath)
                    setCameraImage(filePath)
                }
            }
        }
    }

//Distributor Visit 30-08-2021
    private fun getLocforDD() {
        if (AppUtils.isOnline(mContext) || true) {
            if (AppUtils.mLocation != null) {
                if (AppUtils.mLocation!!.accuracy <= Pref.gpsAccuracy.toInt()) {
                    if (AppUtils.mLocation!!.accuracy <= Pref.shopLocAccuracy.toFloat()) {
                        getNearyShopListDD(AppUtils.mLocation!!)
                    } else {
                        //getDDList(AppUtils.mLocation!!)
                        singleLocationDD()
                    }
                } else {
                    XLog.d("=====Inaccurate current location (Local Shop List)=====")
                    singleLocationDD()
                }
            } else {
                XLog.d("=====null location (Local Shop List)======")
                singleLocationDD()
            }
        } else
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
    }

    private fun singleLocationDD() {
        progress_wheel.spin()
        isGetLocation = -1
        SingleShotLocationProvider.requestSingleUpdate(mContext,
                object : SingleShotLocationProvider.LocationCallback {
                    override fun onStatusChanged(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNewLocationAvailable(location: Location) {
                        if (isGetLocation == -1) {
                            isGetLocation = 0
                            if (location.accuracy > Pref.gpsAccuracy.toInt()) {
                                (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                                progress_wheel.stopSpinning()
                            } else
                                getNearyShopListDD(location)
                        }
                    }

                })

        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                try {
                    if (isGetLocation == -1) {
                        isGetLocation = 1
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage("GPS data to show nearby party is inaccurate. Please stop " +
                                "internet, stop GPS/Location service, and then restart internet and GPS services to get nearby party list.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 15000)
    }

    private fun getNearyShopListDD(location: Location) {
//        var nearBy: Double = Pref.shopLocAccuracy.toDouble()
        var nearBy: Double =  Pref.DistributorGPSAccuracy.toDouble()
        var shop_id: String = ""
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        var finalNearByDD: AssignToDDEntity = AssignToDDEntity()

        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        /*    if (newList != null && newList.size > 0) {
                for (i in 0 until newList.size) {
                    val shopLat: Double = newList[i].shopLat
                    val shopLong: Double = newList[i].shopLong
                    if (shopLat != null && shopLong != null) {
                        val shopLocation = Location("")
                        shopLocation.latitude = shopLat
                        shopLocation.longitude = shopLong
                        val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                        if (isShopNearby) {
                            if ((location.distanceTo(shopLocation)) < nearBy) {
                                nearBy = location.distanceTo(shopLocation).toDouble()
                                finalNearByShop = newList[i]
                            }
                            //startDay(newList[i], location)
                            //break
                        }
                    }
                }

            } else {
                //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
            }*/

        val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
        val newDDList = java.util.ArrayList<AssignToDDEntity>()
        for (i in allDDList.indices) {
            newDDList.add(allDDList[i])
        }

        if (newDDList != null && newDDList.size > 0) {
            for (i in 0 until newDDList.size) {
                val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                if (ddLat != null && ddLong != null) {
                    val ddLocation = Location("")
                    ddLocation.latitude = ddLat
                    ddLocation.longitude = ddLong
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                    if (isShopNearby) {
                        if ((location.distanceTo(ddLocation)) < nearBy) {
                            nearBy = location.distanceTo(ddLocation).toDouble()
                            finalNearByDD = newDDList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }


        //finalNearByDD=newDDList[5]

        if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
            Pref.IsDDvistedOnceByDay = true
            Pref.visit_distributor_id = finalNearByDD.dd_id.toString()
            Pref.visit_distributor_name = finalNearByDD.dd_name.toString()
            Pref.visit_distributor_date_time = AppUtils.getCurrentDateTime()
            progress_wheel.stopSpinning()
            // 27-08-21 For ITC
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
            //dialogHeader.text = "Distributor Visited..."
            dialogHeader.text = "Thanks! Distributor visited Successfully."
            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("Distributor Visited")
            voiceAttendanceMsg("Distributor Visited")
//            ddDay(finalNearByShop, finalNearByDD, location, false)
        } /*else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
//            ddDay(finalNearByShop, finalNearByDD, location, true)
            *//*Pref.IsDDvistedOnceByDay = true
            Pref.visit_distributor_id = finalNearByShop.shop_id.toString()
            Pref.visit_distributor_name = finalNearByShop.shopName.toString()
            Pref.visit_distributor_date_time = AppUtils.getCurrentDateTime()*//*
        }*/ else {
            progress_wheel.stopSpinning()
            // 27-08-21 For ITC
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
            dialogHeader.text = "No nearby Shop/Distributor found..."
            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
//            (mContext as DashboardActivity).showSnackMessage("No nearby Shop/Distributor found")
        }


    }

    fun ddDay(nearByShop: AddShopDBModelEntity, nearByDD: AssignToDDEntity, loc: Location, isShop: Boolean) {
        progress_wheel.spin()
                try {
                    var dayst: DaystartDayendRequest = DaystartDayendRequest()
                    dayst.user_id = Pref.user_id
                    dayst.session_token = Pref.session_token
                    dayst.date = AppUtils.getCurrentDateTime()
                    dayst.location_name = LocationWizard.getNewLocationName(mContext, loc.latitude, loc.longitude)
                    dayst.latitude = loc.latitude.toString()
                    dayst.longitude = loc.longitude.toString()
                    dayst.IsDDvistedOnceByDay = Pref.IsDDvistedOnceByDay.toString()
                    dayst.visit_distributor_date_time = AppUtils.getCurrentDateTime()
                    dayst.visit_distributor_id = nearByDD.dd_id
                    dayst.visit_distributor_name = nearByDD.dd_name

                    val repository = DayStartEndRepoProvider.dayStartRepositiry()
                    BaseActivity.compositeDisposable.add(
                            repository.dayStart(dayst)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribe({ result ->
                                        XLog.d("DashboardFragment DayEnd : RESPONSE " + result.status)
                                        val response = result as BaseResponse
                                        if (response.status == NetworkConstant.SUCCESS) {
                                            (mContext as DashboardActivity).showSnackMessage("Thanks! Updated Successfully.")
//                                            voiceAttendanceMsg("Hi, your day end marked successfully.")
                                        }
                                    }, { error ->
                                        if (error == null) {
                                            XLog.d("DashboardFragment DayEnd : ERROR " + "UNEXPECTED ERROR IN DayStart API")
                                        } else {
                                            XLog.d("DashboardFragment DayEnd : ERROR " + error.localizedMessage)
                                            error.printStackTrace()
                                        }
                                        progress_wheel.stopSpinning()
                                    })
                    )

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    progress_wheel.stopSpinning()
                }


        }



    /////////////////////////////// 10-09-2021
    var cropToFrameTransform: Matrix? = Matrix()
    var faceDetector: FaceDetector? = null
    private val TF_OD_API_MODEL_FILE = "mobile_face_net.tflite"
    val TF_OD_API_IS_QUANTIZED = false
    val TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt"
    val TF_OD_API_INPUT_SIZE = 112

    private var rgbFrameBitmap: Bitmap? = null
    private var faceBmp: Bitmap? = null
    protected var previewWidth = 0
    protected var previewHeight = 0
    private var portraitBmp: Bitmap? = null

    fun faceDetectorSetUp(){
        try {
            FaceStartActivity.detector = TFLiteObjectDetectionAPIModel.create(
                    mContext.getAssets(),
                    TF_OD_API_MODEL_FILE,
                    TF_OD_API_LABELS_FILE,
                    TF_OD_API_INPUT_SIZE,
                    TF_OD_API_IS_QUANTIZED)
            //cropSize = TF_OD_API_INPUT_SIZE;
        } catch (e: IOException) {
            e.printStackTrace()
            //LOGGER.e(e, "Exception initializing classifier!");
            val toast = Toast.makeText(mContext, "Classifier could not be initialized", Toast.LENGTH_SHORT)
            toast.show()
            //finish()
        }
        val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

        val detector = FaceDetection.getClient(options)

        faceDetector = detector
    }


    fun getPicUrl(){
        //31-08-2021
        BaseActivity.isApiInitiated=false
        val repository = GetUserListPhotoRegProvider.provideUserListPhotoReg()
        BaseActivity.compositeDisposable.add(
                repository.getUserFacePicUrlApi(Pref.user_id!!,Pref.session_token!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UserFacePicUrlResponse
                            if(response.status== NetworkConstant.SUCCESS){

                                CustomStatic.FaceUrl=response.face_image_link

                                //val intent = Intent(mContext, FaceStartActivity::class.java)
                                //startActivityForResult(intent, 111)


                                //var bitmap :Bitmap? = null
                                //registerFace(bitmap);
                                println("reg_face - GetImageFromUrl called"+AppUtils.getCurrentDateTime());
                                GetImageFromUrl().execute(CustomStatic.FaceUrl)

                                XLog.d(" AddAttendanceFragment : FaceRegistration/FaceMatch" +response.status.toString() +", : "  + ", Success: ")
                            }else{
                                BaseActivity.isApiInitiated = false
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_reg_face))
                                progress_wheel.stopSpinning()
                                XLog.d("AddAttendanceFragment : FaceRegistration/FaceMatch : " + response.status.toString() +", : "  + ", Failed: ")
                            }
                        },{
                            error ->
                            if (error != null) {
                                XLog.d("AddAttendanceFragment : FaceRegistration/FaceMatch : " + " : "  + ", ERROR: " + error.localizedMessage)
                            }
                            BaseActivity.isApiInitiated = false
                        })
        )
    }

    inner class GetImageFromUrl : AsyncTask<String?, Void?, Bitmap?>() {
        fun GetImageFromUrl() {
            //this.imageView = img;
        }
        override fun doInBackground(vararg url: String?): Bitmap {
            var bitmappppx: Bitmap? = null
            val stringUrl = url[0]
            bitmappppx = null
            val inputStream: InputStream
            try {
                inputStream = URL(stringUrl).openStream()
                bitmappppx = BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmappppx!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            println("reg_face - registerFace called"+AppUtils.getCurrentDateTime());
            registerFace(result)
        }

    }

    private fun registerFace(mBitmap: Bitmap?) {
        progress_wheel.stopSpinning()
        //BaseActivity.isApiInitiated=false
        println("reg_face - add_attendance_registerFace"+AppUtils.getCurrentDateTime());
        try {
            if (mBitmap == null) {
                //Toast.makeText(this, "No File", Toast.LENGTH_SHORT).show()
                return
            }
            //ivFace.setImageBitmap(mBitmap)
            previewWidth = mBitmap.width
            previewHeight = mBitmap.height
            rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
            portraitBmp = mBitmap
            val image = InputImage.fromBitmap(mBitmap, 0)
            faceBmp = Bitmap.createBitmap(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, Bitmap.Config.ARGB_8888)
            faceDetector?.process(image)?.addOnSuccessListener(OnSuccessListener<List<Face>> { faces ->
                if (faces.size == 0) {
                    println("reg_face - dashboard_frag no face detected"+AppUtils.getCurrentDateTime());
                    return@OnSuccessListener
                }
                Handler().post {
                    object : Thread() {
                        override fun run() {
                            //action
                            println("reg_face - dashboard_frag face detected"+AppUtils.getCurrentDateTime());
                            onFacesDetected(1, faces, true) //no need to add currtime
                        }
                    }.start()
                }
            })



        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun onFacesDetected(currTimestamp: Long, faces: List<Face>, add: Boolean) {
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.0f
        val mappedRecognitions: MutableList<SimilarityClassifier.Recognition> = LinkedList()


        //final List<Classifier.Recognition> results = new ArrayList<>();

        // Note this can be done only once
        val sourceW = rgbFrameBitmap!!.width
        val sourceH = rgbFrameBitmap!!.height
        val targetW = portraitBmp!!.width
        val targetH = portraitBmp!!.height
        val transform = createTransform(
                sourceW,
                sourceH,
                targetW,
                targetH,
                90)
        val mutableBitmap = portraitBmp!!.copy(Bitmap.Config.ARGB_8888, true)
        val cv = Canvas(mutableBitmap)

        // draws the original image in portrait mode.
        cv.drawBitmap(rgbFrameBitmap!!, transform!!, null)
        val cvFace = Canvas(faceBmp!!)
        val saved = false
        for (face in faces) {
            //results = detector.recognizeImage(croppedBitmap);
            val boundingBox = RectF(face.boundingBox)

            //final boolean goodConfidence = result.getConfidence() >= minimumConfidence;
            val goodConfidence = true //face.get;
            if (boundingBox != null && goodConfidence) {

                // maps crop coordinates to original
                cropToFrameTransform?.mapRect(boundingBox)

                // maps original coordinates to portrait coordinates
                val faceBB = RectF(boundingBox)
                transform.mapRect(faceBB)

                // translates portrait to origin and scales to fit input inference size
                //cv.drawRect(faceBB, paint);
                val sx = TF_OD_API_INPUT_SIZE.toFloat() / faceBB.width()
                val sy = TF_OD_API_INPUT_SIZE.toFloat() / faceBB.height()
                val matrix = Matrix()
                matrix.postTranslate(-faceBB.left, -faceBB.top)
                matrix.postScale(sx, sy)
                cvFace.drawBitmap(portraitBmp!!, matrix, null)

                //canvas.drawRect(faceBB, paint);
                var label = ""
                var confidence = -1f
                var color = Color.BLUE
                var extra: Any? = null
                var crop: Bitmap? = null
                if (add) {
                    try {
                        crop = Bitmap.createBitmap(portraitBmp!!,
                                faceBB.left.toInt(),
                                faceBB.top.toInt(),
                                faceBB.width().toInt(),
                                faceBB.height().toInt())
                    } catch (eon: java.lang.Exception) {
                        //runOnUiThread(Runnable { Toast.makeText(mContext, "Failed to detect", Toast.LENGTH_LONG) })
                    }
                }
                val startTime = SystemClock.uptimeMillis()
                val resultsAux = FaceStartActivity.detector.recognizeImage(faceBmp, add)
                val lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime
                if (resultsAux.size > 0) {
                    val result = resultsAux[0]
                    extra = result.extra
                    //          Object extra = result.getExtra();
//          if (extra != null) {
//            LOGGER.i("embeeding retrieved " + extra.toString());
//          }
                    val conf = result.distance
                    if (conf < 1.0f) {
                        confidence = conf
                        label = result.title
                        color = if (result.id == "0") {
                            Color.GREEN
                        } else {
                            Color.RED
                        }
                    }
                }
                val flip = Matrix()
                flip.postScale(1f, -1f, previewWidth / 2.0f, previewHeight / 2.0f)

                //flip.postScale(1, -1, targetW / 2.0f, targetH / 2.0f);
                flip.mapRect(boundingBox)
                val result = SimilarityClassifier.Recognition(
                        "0", label, confidence, boundingBox)
                result.color = color
                result.location = boundingBox
                result.extra = extra
                result.crop = crop
                mappedRecognitions.add(result)
            }
        }

        //    if (saved) {
//      lastSaved = System.currentTimeMillis();
//    }

        Log.e("xc", "startabc" )
        val rec = mappedRecognitions[0]
        FaceStartActivity.detector.register("", rec)
        val intent = Intent(mContext, DetectorActivity::class.java)
        startActivityForResult(intent, 171)
//        startActivity(new Intent(this,DetectorActivity.class));
//        finish();

        // detector.register("Sakil", rec);
        /*   runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivFace.setImageBitmap(rec.getCrop());
                //showAddFaceDialog(rec);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.image_edit_dialog, null);
                ImageView ivFace = dialogLayout.findViewById(R.id.dlg_image);
                TextView tvTitle = dialogLayout.findViewById(R.id.dlg_title);
                EditText etName = dialogLayout.findViewById(R.id.dlg_input);

                tvTitle.setText("Register Your Face");
                ivFace.setImageBitmap(rec.getCrop());
                etName.setHint("Please tell your name");
                detector.register("sam", rec); //for register a face

                //button.setPressed(true);
                //button.performClick();
            }

        });*/

        // updateResults(currTimestamp, mappedRecognitions);
    }

    fun createTransform(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, applyRotation: Int): Matrix? {
        val matrix = Matrix()
        if (applyRotation != 0) {
            if (applyRotation % 90 != 0) {
                // LOGGER.w("Rotation of %d % 90 != 0", applyRotation);
            }

            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)

            // Rotate around origin.
            matrix.postRotate(applyRotation.toFloat())
        }

//        // Account for the already applied rotation, if any, and then determine how
//        // much scaling is needed for each axis.
//        final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;
//        final int inWidth = transpose ? srcHeight : srcWidth;
//        final int inHeight = transpose ? srcWidth : srcHeight;
        if (applyRotation != 0) {

            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
        }
        return matrix
    }



    private fun startTvClick(){
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))

        }
        else if(isStartCall==false && Pref.DayStartMarked == false && Pref.isAddAttendence){

            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_yes_no)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
            dialogHeader.text = "Are you sure ?"
            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
                //if(Pref.IsshowDayStartSelfie){
                //isCameraDayStart=true
                //initPermissionCheck()
                //}else{
                progress_wheel.spin()
                isCalledFromStart=true
                getPicUrl()
                //}
            })
            dialogNo.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
        }
        else{
            println("reg_face - start_tv"+AppUtils.getCurrentDateTime());
            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            }
            else {
                if (!Pref.isAddAttendence) {
                    // 27-08-21 For ITC
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                    dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                    dialogHeader.text = "Please mark your attendance."
                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
//                    (mContext as DashboardActivity).showSnackMessage("Please mark your attendance")
                } else {
                    if (!Pref.DayStartMarked) {

                        getLocforStart()

                    } else {
                        // 27-08-21 For ITC
                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                        dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                        dialogHeader.text = "Your Day started already."
                        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()
//                        (mContext as DashboardActivity).showSnackMessage("Day started already")
                    }
                }
            }
        }
    }

    private fun endTvClick(){
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }
        else if(isEndCall==false && Pref.DayEndMarked == false && Pref.DayStartMarked ==true){

            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_yes_no)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
            dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
            dialogHeader.text = "Are you sure to Exit for the Day?"
            val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
            val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
                //if(Pref.IsshowDayStartSelfie){
                //isCameraDayStart=true
                //initPermissionCheck()
                //}else{
                progress_wheel.spin()
                isCalledFromStart=false
                getPicUrl()
                //}
            })
            dialogNo.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
        }
        else {
            if (Pref.DayEndMarked) {
                // 27-08-21 For ITC
                val simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_message)
                val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
                dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
                dialogHeader.text = "Your Day already ended..."
                val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                dialogYes.setOnClickListener({ view ->
                    simpleDialog.cancel()
                })
                simpleDialog.show()
//                    (mContext as DashboardActivity).showSnackMessage("Day already ended")
            } else {
                //Pref.IsDDvistedOnceByDay=true
                if (Pref.DayStartMarked) {

                    getLocforEnd()

                } else {
                    // 27-08-21 For ITC
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                    dialogHeader.text = "Please start your day..."
                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
//                        (mContext as DashboardActivity).showSnackMessage("Please start your day")
                    //test
                }
            }
        }
    }

    private fun pointTvClick(){
        if (!AppUtils.isOnline(mContext) && false) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }
        else {
            if (!Pref.isAddAttendence) {
                // 27-08-21 For ITC
                val simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_message)
                val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                dialogHeader.text = "Please Marked Your Attendance First..."
                val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                dialogYes.setOnClickListener({ view ->
                    simpleDialog.cancel()
                })
                simpleDialog.show()
//                    (mContext as DashboardActivity).showSnackMessage("Please Marked Your Attendance First")
            } else {
                if (!Pref.IsDDvistedOnceByDay) {
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_yes_no)
                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                    dialogHeader.text = "Wish to Visit Distributor Location Right Now?"
                    val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                    val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                        getLocforDD()
                    })
                    dialogNo.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
                } else {
                    // 27-08-21 For ITC
                    val simpleDialog = Dialog(mContext)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                    dialogHeader.text = "You are already visited once..."
                    val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogYes.setOnClickListener({ view ->
                        simpleDialog.cancel()
                    })
                    simpleDialog.show()
//                            (mContext as DashboardActivity).showSnackMessage("You are already visited once...")
                }

            }
        }
    }


    private fun disableScreen(){
        requireActivity().getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private fun enableScreen(){
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}

package com.kcteam.base.presentation


import android.annotation.TargetApi
import android.app.*
import android.app.job.JobInfo
import android.app.job.JobScheduler
import androidx.lifecycle.LifecycleRegistry
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputFilter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.kcteam.CustomConstants
import com.kcteam.CustomStatic
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.*
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.FTStorageUtils.isMyServiceRunning
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.BaseResponse
import com.kcteam.features.alarm.presetation.FloatingWidgetService
import com.kcteam.features.commondialogsinglebtn.CommonDialogSingleBtn
import com.kcteam.features.commondialogsinglebtn.OnDialogClickListener
import com.kcteam.features.dashboard.presentation.ToastBroadcastReceiver
import com.kcteam.features.geofence.GeofenceService
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.logout.presentation.api.LogoutRepositoryProvider
import com.kcteam.features.orderhistory.api.LocationUpdateRepositoryProviders
import com.kcteam.features.orderhistory.model.LocationData
import com.kcteam.features.orderhistory.model.LocationUpdateRequest
import com.kcteam.features.performance.api.UpdateGpsStatusRepoProvider
import com.kcteam.features.performance.model.UpdateGpsInputParamsModel
import com.kcteam.MonitorService
import com.kcteam.app.domain.*
import com.kcteam.features.DecimalDigitsInputFilter
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.model.AddShopRequestCompetetorImg
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dashboard.presentation.api.ShopVisitImageUploadRepoProvider
import com.kcteam.features.dashboard.presentation.api.dayStartEnd.DayStartEndRepoProvider
import com.kcteam.features.dashboard.presentation.model.DaystartDayendRequest
import com.kcteam.features.dashboard.presentation.model.ShopVisitImageUploadInputModel
import com.kcteam.features.location.*
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.model.ShopRevisitStatusRequest
import com.kcteam.features.location.model.ShopRevisitStatusRequestData
import com.kcteam.features.location.shopRevisitStatus.ShopRevisitStatusRepositoryProvider
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.mappackage.SendBrod
import com.kcteam.widgets.AppCustomTextView
import com.google.android.gms.location.FusedLocationProviderApi
import com.google.android.gms.location.LocationRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import net.alexandroid.gps.GpsStatusDetector
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


/**
 * Created by Pratishruti on 26-10-2017.
 */

open class BaseActivity : AppCompatActivity(), GpsStatusDetector.GpsStatusDetectorCallBack {

    private val mRegistry = LifecycleRegistry(this)
    private lateinit var geofenceService: Intent
    var progressDialog: CustomProgressDialog? = null
    private var filter: IntentFilter? = null
    private var permissionUtils: PermissionUtils? = null
    private var mGpsStatusDetector: GpsStatusDetector? = null
    private var i = 0
    private var autoLogoutDialog: CommonDialogSingleBtn? = null
    private var autoTimeDialog: CommonDialogSingleBtn? = null


    private fun getProgressInstance(): CustomProgressDialog {
        if (progressDialog == null)
            progressDialog = CustomProgressDialog(this)
        return progressDialog!!
    }


    companion object {
        @JvmStatic
        val compositeDisposable: CompositeDisposable = CompositeDisposable()
        var isApiInitiated = false
        var isShopActivityUpdating = false
        var isMeetingUpdating = false
    }


    /*   override fun getLifecycle(): LifecycleRegistry{
           return mRegistry
       }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isApiInitiated = false

        /*filter = IntentFilter()
        filter?.addAction(AppUtils.gpsDisabledAction)
        filter?.addAction(AppUtils.gpsEnabledAction)*/

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionUtils = PermissionUtils(this, object : PermissionUtils.OnPermissionListener {
                override fun onPermissionGranted() {
                    checkGPSAvailability()
                }

                override fun onPermissionNotGranted() {
                    //Toast.makeText(this@BaseActivity, "Please accept permission from settings", Toast.LENGTH_LONG).show()
                }

            }, arrayOf<String>(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
        } else
            checkGPSAvailability()*/
    }


    override fun onResume() {
        super.onResume()

        //registerReceiver(broadcastReceiver, filter)

        if (android.provider.Settings.Global.getInt(contentResolver, android.provider.Settings.Global.AUTO_TIME, 0) == 0) {
            autoTime()
            return
        }

        //checkGPSAvailability()

        if (Pref.user_id.isNullOrEmpty())
            return

        XLog.e("BaseActivity: Login Date====> " + Pref.login_date)
        XLog.e("BaseActivity: Current Date====> " + AppUtils.getCurrentDateChanged())

        if (Pref.user_id!!.isNotEmpty() && AppUtils.getLongTimeStampFromDate2(Pref.login_date!!) != AppUtils.getLongTimeStampFromDate2(AppUtils.getCurrentDateChanged())) {
            Pref.isAutoLogout = true
        } /*else
            Pref.isAutoLogout = false*/

        //Pref.isAutoLogout=true
        if (Pref.isAutoLogout) {
            //Pref.isAddAttendence = false
            //Pref.DayStartMarked = false
            //Pref.DayEndMarked = false

            performLogout()
            //syncShopList()
            //uploadShopRevisitData()

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        } else {

            if (!TextUtils.isEmpty(Pref.approvedOutTime)) {

                val currentTimeInLong = AppUtils.convertTimeWithMeredianToLong(AppUtils.getCurrentTimeWithMeredian())
                val approvedOutTimeInLong = AppUtils.convertTimeWithMeredianToLong(Pref.approvedOutTime)

                if (currentTimeInLong >= approvedOutTimeInLong) {
                    showForceLogoutPopup()
                }
            }
        }
    }

    open fun showForceLogoutPopup() {
    }

    /*val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AppUtils.gpsDisabledAction) {

            }
        }
    }*/

    open fun takeActionOnGeofence() {

        if (Pref.IsLeavePressed == true && Pref.IsLeaveGPSTrack == false) {
            return
        }

        if (Pref.user_id == null)
            return
        if (Pref.user_id!!.isNotEmpty()) {
            Pref.isGeoFenceAdded = true
            geofenceService = Intent(this, GeofenceService::class.java)
            startService(geofenceService)
        } else {
            if (Pref.isGeoFenceAdded) {
                Pref.isGeoFenceAdded = false
                geofenceService = Intent(this, GeofenceService::class.java)
                stopService(geofenceService)
            }
        }
    }


    private fun autoTime() {

        if (autoTimeDialog == null || !autoTimeDialog?.isVisible!!) {
            autoTimeDialog = CommonDialogSingleBtn.getInstance(getString(R.string.date_n_time), getString(R.string.auto_time_zone), getString(R.string.cancel), object : OnDialogClickListener {
                override fun onOkClick() {
                    startActivityForResult(Intent(android.provider.Settings.ACTION_DATE_SETTINGS), 0)
                }
            })//.show(supportFragmentManager, "CommonDialogSingleBtn")
            autoTimeDialog?.show(supportFragmentManager, "CommonDialogSingleBtn")
        }
    }


    private fun performLogout() {

        if (autoLogoutDialog == null) {
            autoLogoutDialog = CommonDialogSingleBtn.getInstance(AppUtils.hiFirstNameText()+"!", "Final logout for the date ${AppUtils.convertLoginTimeToAutoLogoutTimeFormat(Pref.login_date!!)} is pending. Click Ok to complete final logout.", getString(R.string.ok), object : OnDialogClickListener {

                override fun onOkClick() {

                    val list = AppDatabase.getDBInstance()!!.gpsStatusDao().getDataSyncStateWise(false)


                    if (AppUtils.isOnline(this@BaseActivity)) {

                        if (list != null && list.isNotEmpty()) {
                            i = 0
                            callUpdateGpsStatusApi(list)
                        } else {
                            checkToCallLocationSync()
                        }
                    } else {
                        Toaster.msgShort(this@BaseActivity, getString(R.string.no_internet))
                        performLogout()
                    }
                }
            })//.show(supportFragmentManager, "CommonDialogSingleBtn")
            //}

            //if (autoLogoutDialog?.dialog != null && !autoLogoutDialog?.dialog?.isShowing!!)
            autoLogoutDialog?.show(supportFragmentManager, "CommonDialogSingleBtn")
        } else {
            if (autoLogoutDialog?.dialog != null && !autoLogoutDialog?.dialog?.isShowing!!)
                autoLogoutDialog?.show(supportFragmentManager, "CommonDialogSingleBtn")
            else {
                if (autoLogoutDialog?.dialog == null)
                    autoLogoutDialog?.show(supportFragmentManager, "CommonDialogSingleBtn")
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun checkToCallLocationSync() {
        val locationList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationNotUploaded(false)
        if (locationList != null && locationList.isNotEmpty())
            syncLocationActivity(locationList)
        else
            initiateLogoutApi()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun syncLocationActivity(list: List<UserLocationDataEntity>) {

        XLog.d("syncLocationActivity Logout : ENTER")


        if (Pref.user_id.isNullOrEmpty())
            return

        if (AppUtils.isLocationActivityUpdating)
            return

        AppUtils.isLocationActivityUpdating = true

        val locationUpdateReq = LocationUpdateRequest()
        locationUpdateReq.user_id = Pref.user_id
        locationUpdateReq.session_token = Pref.session_token

        val locationList: MutableList<LocationData> = ArrayList()
        val locationListAllId: MutableList<LocationData> = ArrayList()
        var distanceCovered: Double = 0.0
        var timeStamp = 0L

        val allLocationList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.convertFromRightToReverseFormat(Pref.login_date!!)).toMutableList()
        val apiLocationList: MutableList<UserLocationDataEntity> = ArrayList()

        val syncList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADayNotSyn(AppUtils.convertFromRightToReverseFormat(Pref.login_date!!), true)

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

            if (!TextUtils.isEmpty(allLocationList[i].home_duration)) {
                XLog.e("Home Duration (Location Fuzed Service)=================> ${allLocationList[i].home_duration}")
                XLog.e("Time (Location Fuzed Service)=================> ${allLocationList[i].time}")
                val arr = allLocationList[i].home_duration?.split(":".toRegex())?.toTypedArray()
                timeStamp += arr?.get(2)?.toInt()?.toLong()!!
                timeStamp += 60 * arr[1].toInt().toLong()
                timeStamp += 3600 * arr[0].toInt().toLong()
            }

            if (i != 0) {
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
            getProgressInstance().showDialogForLoading(this)

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

                                        for (i in 0 until locationListAllId/*locationList*/.size) {

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
                                            getProgressInstance().dismissDialog()
                                            initiateLogoutApi()
                                        }
                                    }
                                } else {
                                    AppUtils.isLocationActivityUpdating = false
                                    getProgressInstance().dismissDialog()
                                    initiateLogoutApi()
                                }

                            }, { error ->
                                AppUtils.isLocationActivityUpdating = false
                                getProgressInstance().dismissDialog()
                                initiateLogoutApi()

                                if (error == null) {
                                    XLog.d("syncLocationActivity Logout : ERROR : " + "UNEXPECTED ERROR IN LOCATION ACTIVITY API")
                                } else {
                                    XLog.d("syncLocationActivity Logout : ERROR : " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                            })
            )
        } else {
            XLog.e("=======locationList is empty (Auto Logout)=========")
            AppUtils.isLocationActivityUpdating = false
            initiateLogoutApi()
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initiateLogoutApi() {
        getProgressInstance().showDialogForLoading(this@BaseActivity)
        Pref.logout_time = "11:59 PM"
        if(Pref.DayStartMarked && Pref.IsShowDayStart){
            singleLocationEnd()
        }else{
            calllogoutApi(Pref.user_id!!, Pref.session_token!!)
        }
    }

    private fun singleLocationEnd() {

        SingleShotLocationProvider.requestSingleUpdate(this,
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
                        if(location!=null)
                            endDay(location)
                        else{
                           var lloc:Location = Location("")
                            lloc.latitude=Pref.current_latitude.toDouble()
                            lloc.longitude=Pref.current_longitude.toDouble()
                            endDay(lloc)
                        }
                    }

                })

    }

    fun endDay(location: Location) {
        try{
            var saleValue: String = ""
            var dayst: DaystartDayendRequest = DaystartDayendRequest()
            dayst.user_id = Pref.user_id
            dayst.session_token = Pref.session_token
            //dayst.date = AppUtils.getCurrentDateTime()
            dayst.date = AppUtils.getCurrentDateTime12(Pref.login_date!!)
            dayst.location_name = LocationWizard.getNewLocationName(this, location.latitude, location.longitude)
            dayst.latitude = location.latitude.toString()
            dayst.longitude = location.longitude.toString()
            dayst.shop_type = ""
            dayst.shop_id = ""
            dayst.isStart = "0"
            dayst.isEnd = "1"
            dayst.sale_Value = "0.0"
            dayst.remarks = "No Day End Value for auto logout"
            val repository = DayStartEndRepoProvider.dayStartRepositiry()
            BaseActivity.compositeDisposable.add(
                    repository.dayStart(dayst)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                XLog.d("DashboardFragment DayEnd : RESPONSE " + result.status)
                                val response = result as BaseResponse
                                if (response.status == NetworkConstant.SUCCESS) {
                                    calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                }
                            }, { error ->
                                if (error == null) {
                                    calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                    XLog.d("DashboardFragment DayEnd : ERROR " + "UNEXPECTED ERROR IN DayStart API")
                                } else {
                                    calllogoutApi(Pref.user_id!!, Pref.session_token!!)
                                    XLog.d("DashboardFragment DayEnd : ERROR " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                            })
            )
        }
        catch (ex:java.lang.Exception){
            ex.printStackTrace()
            calllogoutApi(Pref.user_id!!, Pref.session_token!!)
        }


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

    getProgressInstance().showDialogForLoading(this@BaseActivity)

    val repository = UpdateGpsStatusRepoProvider.updateGpsStatusRepository()
    BaseActivity.compositeDisposable.add(
            repository.updateGpsStatus(updateGps)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val gpsStatusResponse = result as BaseResponse
                        XLog.d("GPS_STATUS : " + "RESPONSE : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name
                                + ",MESSAGE : " + gpsStatusResponse.message)
                        if (gpsStatusResponse.status == NetworkConstant.SUCCESS) {
                            AppDatabase.getDBInstance()!!.gpsStatusDao().updateIsUploadedAccordingToId(true, list[i].id)
                        }

                        i++
                        if (i < list.size) {
                            callUpdateGpsStatusApi(list)
                        } else {
                            i = 0
                            getProgressInstance().dismissDialog()
                            checkToCallLocationSync()
                        }

                    }, { error ->
                        //
                        XLog.d("GPS_STATUS : " + "RESPONSE ERROR: " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                        error.printStackTrace()
                        i++
                        if (i < list.size) {
                            callUpdateGpsStatusApi(list)
                        } else {
                            i = 0
                            getProgressInstance().dismissDialog()
                            checkToCallLocationSync()
                        }
                    })
    )
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private fun calllogoutApi(user_id: String, session_id: String) {


    if (Pref.current_latitude == null || Pref.current_longitude == null) {
        return
    }

    //uploadShopRevisitData()
        //syncShopList()


    Handler().postDelayed(Runnable {

        var intent = Intent(this, MonitorService::class.java)
        intent.action = CustomConstants.STOP_MONITOR_SERVICE
        //mContext.startService(intent)
        stopService(intent)

        SendBrod.stopBrod(this)

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

        val unSyncedList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADayNotSyn(AppUtils.convertFromRightToReverseFormat(Pref.login_date!!), false)

        if (unSyncedList != null && unSyncedList.isNotEmpty()) {
            var totalDistance = 0.0
            for (i in unSyncedList.indices) {
                totalDistance += unSyncedList[i].distance.toDouble()
            }

            distance = Pref.tempDistance.toDouble() + totalDistance
        }
        else
            distance = Pref.tempDistance.toDouble()

        var location = ""

        if (Pref.logout_latitude != "0.0" && Pref.logout_longitude != "0.0") {
            location = LocationWizard.getAdressFromLatlng(this, Pref.logout_latitude.toDouble(), Pref.logout_longitude.toDouble())

            if (location.contains("http"))
                location = "Unknown"
        }

        XLog.d("AUTO_LOGOUT : " + "REQUEST : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name)

        XLog.d("=======AUTO_LOGOUT INPUT PARAMS======")
        XLog.d("AUTO_LOGOUT : USER ID======> $user_id")
        XLog.d("AUTO_LOGOUT : SESSION ID======> $session_id")
        XLog.d("AUTO_LOGOUT : LAT====> " + Pref.logout_latitude)
        XLog.d("AUTO_LOGOUT : LONG=====> " + Pref.logout_longitude)
        XLog.d("AUTO_LOGOUT : DISTANCE=====> $distance")
        XLog.d("AUTO_LOGOUT : LOGOUT TIME========> " + AppUtils.getCurrentDateTime12(Pref.login_date!!))
        XLog.d("AUTO_LOGOUT : IS AUTO LOGOUT=======> 1")
        XLog.d("AUTO_LOGOUT : LOCATION=======> $location")
        XLog.d("=======================================")


        val repository = LogoutRepositoryProvider.provideLogoutRepository()
        BaseActivity.compositeDisposable.add(
                repository.logout(user_id, session_id, Pref.logout_latitude, Pref.logout_longitude, /*"2018-12-21 23:59:00"*/AppUtils.getCurrentDateTime12(Pref.login_date!!),
                        distance.toString(), "1", location)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val logoutResponse = result as BaseResponse
                            XLog.d("AUTO_LOGOUT : " + "RESPONSE : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                            if (logoutResponse.status == NetworkConstant.SUCCESS) {

                                Pref.tempDistance = "0.0"
                                //Pref.prevOrderCollectionCheckTimeStamp = 0L

                                if (unSyncedList != null && unSyncedList.isNotEmpty()) {
                                    for (i in unSyncedList.indices) {
                                        AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploaded(true, unSyncedList[i].locationId)
                                    }
                                }

                                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                notificationManager.cancelAll()

                                Pref.logout_latitude = "0.0"
                                Pref.logout_longitude = "0.0"

                                clearData()
                                Pref.isAutoLogout = false
                                Pref.isAddAttendence = false
                            } else
                                performLogout()

                            BaseActivity.isApiInitiated = false
                            takeActionOnGeofence()
                            getProgressInstance().dismissDialog()
                        },
                                { error ->
                                    //
                                    Toaster.msgShort(this@BaseActivity, getString(R.string.something_went_wrong))
                                    XLog.d("AUTO_LOGOUT : " + "RESPONSE ERROR: " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                    error.printStackTrace()
                                    getProgressInstance().dismissDialog()
                                    performLogout()
                                })
        )
    }, 6500)

}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun clearData() {
    XLog.d("AUTO_LOGOUT New: clearData" + AppUtils.getCurrentDateTime())
    println("BaseActivity ClearData");
    doAsync {
        val result = runLongTask()
        uiThread {
            if (result == true) {
                Pref.user_id = ""
                Pref.session_token = ""
                Pref.login_date = ""
                Pref.isLogoutInitiated = false
                Pref.latitude = ""
                Pref.longitude = ""
                Pref.isShopVisited = false
                Pref.isOnLeave = ""
                Pref.willAlarmTrigger = false
                Pref.isHomeLocAvailable = false
                Pref.approvedInTime = ""
                Pref.approvedOutTime = ""
                Pref.home_latitude = ""
                Pref.home_longitude = ""
                Pref.isFieldWorkVisible = ""
                Pref.isOfflineTeam = false
                isMeetingUpdating = false
                Pref.visitDistance = ""
                Pref.distributorName = ""
                Pref.marketWorked = ""
                //AppUtils.timer = null

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                    jobScheduler.cancelAll()
                    XLog.d("============Alert Job scheduler cancel (Base Activity)==============")
                }
                else {
                    val serviceLauncher = Intent(this@BaseActivity, CollectionOrderAlertService::class.java)
                    stopService(serviceLauncher)
                }*/


                try {
                    val intent = Intent(this@BaseActivity, ToastBroadcastReceiver::class.java)
                    //intent.setAction(MyReceiver.ACTION_ALARM_RECEIVER)
//                    val pendingIntent = PendingIntent.getBroadcast(this@BaseActivity, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                    // FLAG_IMMUTABLE update
                    val pendingIntent = PendingIntent.getBroadcast(this@BaseActivity, 1, intent, PendingIntent.FLAG_IMMUTABLE)
                    val backupAlarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    backupAlarmMgr.cancel(pendingIntent)
                    pendingIntent.cancel()

                    Log.e("BaseActivity", "Stop Job Intent Service")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                AppUtils.clearPreferenceKey(this@BaseActivity, "STATE_LIST")
                AppUtils.clearPreferenceKey(this@BaseActivity, "PRODUCT_RATE_LIST")
                AppUtils.clearPreferenceKey(this@BaseActivity, "TEXT_LIST")
                AppUtils.clearPreferenceKey(this@BaseActivity, "Location")

                serviceStatusActionable()

                /*try {
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
                                //AppUtils.isShopVisited = false
                            } else {
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivityList[i].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivityList[i].shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                            }
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(LocationWizard.getNewLocationName(this@BaseActivity, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)

                            val netStatus = if (AppUtils.isOnline(this@BaseActivity))
                                "Online"
                            else
                                "Offline"

                            val netType = if (AppUtils.getNetworkType(this@BaseActivity).equals("wifi", ignoreCase = true))
                                AppUtils.getNetworkType(this@BaseActivity)
                            else
                                "Mobile ${AppUtils.mobNetType(this@BaseActivity)}"

                            if (!Pref.isMultipleVisitEnable) {
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                                        AppUtils.getBatteryPercentage(this@BaseActivity).toString(), netStatus, netType.toString(), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                            } else {
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                                        AppUtils.getBatteryPercentage(this@BaseActivity).toString(), netStatus, netType.toString(), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                            }
                            if (Pref.willShowShopVisitReason && totalMinute.toInt() <= Pref.minVisitDurationSpentTime.toInt())
                                Pref.isShowShopVisitReason = true
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
*/

                val intent = Intent(this@BaseActivity, LoginActivity::class.java)
                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(0, 0)
                finishAffinity()
            }

        }
    }
}

private fun runLongTask(): Any {
    try {
        if (AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!, AppUtils.getCurrentDateChanged()).isNotEmpty()) {
            val loginTime = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginTime(Pref.user_id!!, AppUtils.getCurrentDateChanged())
            val isOnLeave = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getIsOnLeave(Pref.user_id!!, AppUtils.getCurrentDateChanged())
            val logoutTime = Pref.logout_time!!
            var result = ""
            if (isOnLeave.equals("false", ignoreCase = true))
                result = AppUtils.getTimeDuration(loginTime, logoutTime)
            AppDatabase.getDBInstance()!!.userAttendanceDataDao().updateDuration(result, Pref.user_id!!, AppUtils.getCurrentDateChanged())
            AppDatabase.getDBInstance()!!.userAttendanceDataDao().updateLogoutTimeN(AppUtils.convertTime(FTStorageUtils.getStringToDate(AppUtils.getCurrentISODateTime())), Pref.user_id!!, AppUtils.getCurrentDateChanged())
        } else {
            if (!TextUtils.isEmpty(Pref.add_attendence_time)) {
                val loginTime = Pref.add_attendence_time!!
                val logoutTime = Pref.logout_time!!
                val result = AppUtils.getTimeDuration(loginTime, logoutTime)
                AppDatabase.getDBInstance()!!.userAttendanceDataDao().updateDuration(result, Pref.user_id!!, Pref.login_date!!)
                AppDatabase.getDBInstance()!!.userAttendanceDataDao().updateLogoutTimeN(logoutTime, Pref.user_id!!, Pref.login_date!!)
                Pref.add_attendence_time = ""
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return true
}


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun checkGPSAvailability() {
    var manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        buildAlertMessageNoGps()
    } else {
        //if (PermissionHelper.checkLocationPermission(this, 0)) {
        //Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE, 3)
        if (!FTStorageUtils.isMyServiceRunning(LocationFuzedService::class.java, this)) {
            /*Start & Stop Expensive service stuff when logged out*/
            serviceStatusActionable()
            /*val serviceLauncher = Intent(this, LocationFuzedService::class.java)
            startService(serviceLauncher)*/
        }
        //}
    }
}


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun serviceStatusActionable() {
    try {
        if (Pref.IsLeavePressed == true && Pref.IsLeaveGPSTrack == false) {
            return
        }
        val serviceLauncher = Intent(this, LocationFuzedService::class.java)
        if (Pref.user_id != null && Pref.user_id!!.isNotEmpty()) {
            startMonitorService()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val componentName = ComponentName(this, LocationJobService::class.java)
                val jobInfo = JobInfo.Builder(12, componentName)
                        //.setRequiresCharging(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        //.setRequiresDeviceIdle(true)
                        .setOverrideDeadline(1000)
                        .build()

                val resultCode = jobScheduler.schedule(jobInfo)

                if (resultCode == JobScheduler.RESULT_SUCCESS) {
                    XLog.d("===============================Job scheduled (Base Activity) " + AppUtils.getCurrentDateTime() + "============================")
                } else {
                    XLog.d("=====================Job not scheduled (Base Activity) " + AppUtils.getCurrentDateTime() + "====================================")
                }
            } else {
                startService(serviceLauncher)
                startMonitorService()
            }
        } else {
            stopService(serviceLauncher)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                jobScheduler.cancelAll()
                XLog.d("===============================Job scheduler cancel (Base Activity)" + AppUtils.getCurrentDateTime() + "============================")

                /*if (AppUtils.mGoogleAPIClient != null) {
                    AppUtils.mGoogleAPIClient?.disconnect()
                    AppUtils.mGoogleAPIClient = null
                }*/
            }

            /*val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()*/

            AlarmReceiver.stopServiceAlarm(this, 123)
            XLog.d("===========Service alarm is stopped (Base Activity)================")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun buildAlertMessageNoGps() {
    /*val builder = AlertDialog.Builder(this)
    builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 100)
            })
//                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
    val alert = builder.create()
    alert.show()*/

    mGpsStatusDetector = GpsStatusDetector(this)
    mGpsStatusDetector?.checkGpsStatus()
}

// GpsStatusDetectorCallBack
override fun onGpsSettingStatus(enabled: Boolean) {

    if (enabled)
        Log.e("splash", "GPS enabled")
    else
        Log.e("splash", "GPS disabled")
}

override fun onGpsAlertCanceledByUser() {
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    /*if (requestCode == 100) {
        var manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (PermissionHelper.checkLocationPermission(this, 0)) {
                val serviceLauncher = Intent(this, LocationFuzedService::class.java)
                startService(serviceLauncher)
            }
        } else {
            //buildAlertMessageNoGps()
        }


    }*/

    /*if (resultCode == Activity.RESULT_OK) {
        mGpsStatusDetector?.checkOnActivityResult(requestCode, resultCode)
        //checkGPSProvider()
        val serviceLauncher = Intent(this, LocationFuzedService::class.java)
        startService(serviceLauncher)
    }
    else {
        finish()
        System.exit(0)
    }*/
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun clearDataOnLogoutSync() {
    doAsync {
        var result = runLongTask()
        uiThread {
            if (result == true) {
                Pref.user_id = ""
                Pref.session_token = ""
                Pref.login_date = ""
                Pref.isLogoutInitiated = false
                Pref.latitude = ""
                Pref.longitude = ""

                serviceStatusActionable()

                var shopActivityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                for (i in 0 until shopActivityList.size) {
                    if (!shopActivityList[i].isDurationCalculated && shopActivityList[i].startTimeStamp != "0") {
                        val endTimeStamp = System.currentTimeMillis().toString()
                        val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivityList[i].startTimeStamp, endTimeStamp)
                        val duration = AppUtils.getTimeFromTimeSpan(shopActivityList[i].startTimeStamp, endTimeStamp)
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivityList[i].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivityList[i].shopid!!, duration, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                        AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())

//                            AppUtils.isShopVisited = false
                        Pref.isShopVisited = false
                    }
                }

                CommonDialogSingleBtn.getInstance(getString(R.string.data_sync_completed_header), getString(R.string.data_sync_completed_content), getString(R.string.ok), object : OnDialogClickListener {
                    override fun onOkClick() {
                        startActivity(Intent(this@BaseActivity, LoginActivity::class.java))
                        overridePendingTransition(0, 0)
                        finishAffinity()
                    }
                }).show(supportFragmentManager, "CommonDialogSingleBtn")

            }

        }
    }
}

override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
}

override fun onStop() {
    super.onStop()
    if (isMyServiceRunning(FloatingWidgetService::class.java, this)) {
        val i = Intent(applicationContext, FloatingWidgetService::class.java)
        stopService(i)
    }
}

override fun onDestroy() {
    compositeDisposable.clear()
    isApiInitiated = false

    super.onDestroy()
    //unregisterReceiver(broadcastReceiver)
}

///////////////////////////////////////////////////////////////////////////////////////////////
fun startMonitorService() {
    if (!isMonitorServiceRunning()) {
        XLog.d("MonitorService Started : " + " Time :" + AppUtils.getCurrentDateTime())
        val intent = Intent(applicationContext, MonitorService::class.java)
        intent.action = CustomConstants.START_MONITOR_SERVICE
        startService(intent)
        //Toast.makeText(this, "Loc service started", Toast.LENGTH_SHORT).show()
    }

}

fun stopLocationService() {
    if (isMonitorServiceRunning()) {
        //Intent intent=new Intent(getApplicationContext(), LocationService.class);
        val intent = Intent(this, MonitorService::class.java)
        intent.action = CustomConstants.STOP_MONITOR_SERVICE
        startService(intent)
        //Toast.makeText(this, "Loc service stop", Toast.LENGTH_SHORT).show()
    }
}

fun isMonitorServiceRunning(): Boolean {
    val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    if (activityManager != null) {
        val servicesList = activityManager.getRunningServices(Int.MAX_VALUE)
        for (serviceInfo in servicesList) {
            if (MonitorService::class.java.getName() == serviceInfo.service.className) {
                //if (serviceInfo.foreground) {
                return true
                //}
            }
        }
        return false
    }
    return false
}


//////////////////////
val revisitStatusList : MutableList<ShopRevisitStatusRequestData> = ArrayList()
    private var j: Int = 0
   lateinit var ShopActivityEntityListNew: List<ShopActivityEntity>

    private fun uploadShopRevisitData(){
        //AppDatabase.getDBInstance()!!.shopActivityDao().xtest(false,"2021-11-27")
        //AppDatabase.getDBInstance()!!.shopActivityDao().xtest1(false,"2021-11-27")
    var logout_date=AppUtils.convertLoginTimeToAutoLogoutTimeFormatyymmdd(Pref.login_date!!)

        XLog.d("AUTO_LOGOUT New: logout_date_prev" + logout_date)

        //logout_date="2021-11-28"
        ShopActivityEntityListNew = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(logout_date)
    Collections.reverse(ShopActivityEntityListNew)

    // tested on 23-11-2021 begin
    for (i in ShopActivityEntityListNew.indices) {
        if (!ShopActivityEntityListNew[i].isDurationCalculated && ShopActivityEntityListNew[i].startTimeStamp != "0" && ShopActivityEntityListNew[i].isUploaded==false) {
            Pref.durationCompletedShopId = ShopActivityEntityListNew[i].shopid!!
            val endTimeStamp = System.currentTimeMillis().toString()
            val totalMinute = AppUtils.getMinuteFromTimeStamp(ShopActivityEntityListNew[i].startTimeStamp, endTimeStamp)
            val duration = AppUtils.getTimeFromTimeSpan(ShopActivityEntityListNew[i].startTimeStamp, endTimeStamp)

            if (!Pref.isMultipleVisitEnable) {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(ShopActivityEntityListNew[i].shopid!!, totalMinute, logout_date)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, ShopActivityEntityListNew[i].shopid!!, logout_date)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(ShopActivityEntityListNew[i].shopid!!, duration,logout_date)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, ShopActivityEntityListNew[i].shopid!!,logout_date)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, ShopActivityEntityListNew[i].shopid!!, logout_date)
            }
            else {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(ShopActivityEntityListNew[i].shopid!!, totalMinute, logout_date, ShopActivityEntityListNew[i].startTimeStamp)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, ShopActivityEntityListNew[i].shopid!!,logout_date, ShopActivityEntityListNew[i].startTimeStamp)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(ShopActivityEntityListNew[i].shopid!!, duration, logout_date, ShopActivityEntityListNew[i].startTimeStamp)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, ShopActivityEntityListNew[i].shopid!!, logout_date, ShopActivityEntityListNew[i].startTimeStamp)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, ShopActivityEntityListNew[i].shopid!!, logout_date, ShopActivityEntityListNew[i].startTimeStamp)
            }
            AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), ShopActivityEntityListNew[i].shopid!!, logout_date, ShopActivityEntityListNew[i].startTimeStamp)
            AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(LocationWizard.getNewLocationName(this, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()), ShopActivityEntityListNew[i].shopid!!,logout_date, ShopActivityEntityListNew[i].startTimeStamp)

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
                        AppUtils.getBatteryPercentage(this).toString(), netStatus, netType.toString(), ShopActivityEntityListNew[i].shopid!!,logout_date)
            }
            else {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                        AppUtils.getBatteryPercentage(this).toString(), netStatus, netType.toString(), ShopActivityEntityListNew[i].shopid!!, logout_date, ShopActivityEntityListNew[i].startTimeStamp)
            }
//                    AppUtils.isShopVisited = false

            Pref.isShopVisited=false
            /*if (Pref.willShowShopVisitReason && totalMinute.toInt() < Pref.minVisitDurationSpentTime.toInt()) {
                Pref.isShowShopVisitReason = true
                showRevisitReasonDialog(shopActivityList[i].startTimeStamp)
            }*/
        }
    }
    ShopActivityEntityListNew = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(logout_date)
    Collections.reverse(ShopActivityEntityListNew)
    // tested on 23-11-2021 end

        XLog.d("AUTO_LOGOUT New: uploadShopRevisitData" + AppUtils.getCurrentDateTime())

    if (!Pref.isMultipleVisitEnable) {
        if (ShopActivityEntityListNew != null && ShopActivityEntityListNew.isNotEmpty()) {

            val list = ArrayList<ShopActivityEntity>()

            for (i in ShopActivityEntityListNew.indices) {
                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityListNew[i].shopid)
                if (shop.isUploaded) {
                    if (ShopActivityEntityListNew[i].isDurationCalculated /*&& !ShopActivityEntityList[i].isUploaded*/) {
                        if (AppUtils.isVisitSync == "1")
                            list.add(ShopActivityEntityListNew[i])
                        else {
                            if (!ShopActivityEntityListNew[i].isUploaded)
                                list.add(ShopActivityEntityListNew[i])
                        }
                    }
                }
            }


            if (list.size > 0)
                syncAllShopActivity(list[i].shopid!!, list,logout_date)
            else
                syncShopVisitImage()

        } else {
            syncShopVisitImage()
        }
    }
    else {
        if (ShopActivityEntityListNew != null && ShopActivityEntityListNew.isNotEmpty()) {

            val list = ArrayList<ShopActivityEntity>()

            for (i in ShopActivityEntityListNew.indices) {
                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityListNew[i].shopid)
                if (shop.isUploaded) {
                    if (ShopActivityEntityListNew[i].isDurationCalculated /*&& !ShopActivityEntityList[i].isUploaded*/) {
                        if (AppUtils.isVisitSync == "1")
                            list.add(ShopActivityEntityListNew[i])
                        else {
                            if (!ShopActivityEntityListNew[i].isUploaded)
                                list.add(ShopActivityEntityListNew[i])
                        }
                    }
                }
            }


            if (list.size > 0)
                syncAllShopActivityForMultiVisit(list,logout_date)
        }
    }
    }

    private fun syncAllShopActivityForMultiVisit(list_: ArrayList<ShopActivityEntity>,selectedDate:String) {
        if (!AppUtils.isOnline(this)) {
            (this as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
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




                                ShopActivityEntityListNew = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                Collections.reverse(ShopActivityEntityListNew)


                                BaseActivity.isShopActivityUpdating = false
                            } else {

                                (this as DashboardActivity).showSnackMessage(this.getString(R.string.unable_to_sync))
                                BaseActivity.isShopActivityUpdating = false
                                ShopActivityEntityListNew = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityListNew)
                            }

                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            if (error != null) {
                                XLog.d("ShopActivityFromAverageShop : ERROR:= " + error.localizedMessage + "\nUser Id" + Pref.user_id +
                                        ", Session Token" + Pref.session_token)
                                (this as DashboardActivity).showSnackMessage(this.getString(R.string.unable_to_sync))

                                ShopActivityEntityListNew = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                Collections.reverse(ShopActivityEntityListNew)
                            }
                        })
        )

    }


    private fun syncAllShopActivity(shopId: String, list_: ArrayList<ShopActivityEntity>,selectedDate:String) {
        if (!AppUtils.isOnline(this)) {
            (this as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
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

        Log.e("Average Shop BaseActivity AUTO_LOGOUT New", "isShopActivityUpdating====> " + BaseActivity.isShopActivityUpdating)
        if (BaseActivity.isShopActivityUpdating)
            return

        BaseActivity.isShopActivityUpdating = true

        XLog.d("========SYNC ALL VISITED SHOP DATA AUTO_LOGOUT New (AVERAGE SHOP)=====" + " date-time : "+AppUtils.getCurrentDateTime())
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

        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            XLog.d("ShopActivityFromAverageShop BaseActivity AUTO_LOGOUT New: RESPONSE STATUS:= " + result.status + ", RESPONSE MESSAGE:= " + result.message +
                                    "\nUser Id" + Pref.user_id + ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid +
                                    ", SHOP: " + mList[0].shop_name+" date-time : "+AppUtils.getCurrentDateTime())
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

                                    /*if (unSyncedList.size > 0) {
                                        callShopVisitImageUploadApi(unSyncedList, true, list_)
                                    } else {*/
                                    BaseActivity.isShopActivityUpdating = false
                                    syncAllShopActivity(list_[i].shopid!!, list_,selectedDate)
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


                                    if (unSyncedList.size > 0) {
                                        j = 0
                                        //callShopVisitImageUploadApi(unSyncedList, true, list_)
                                        BaseActivity.isShopActivityUpdating = false
                                        callShopVisitImageUploadApiForAll(unSyncedList)
                                    }
                                    else {

                                        val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()
                                        if (!Pref.isMultipleVisitEnable) {
                                            for (i in ShopActivityEntityListNew.indices) {
                                                if (ShopActivityEntityListNew[i].isDurationCalculated && ShopActivityEntityListNew[i].isUploaded) {

                                                    var unSyncedData: List<ShopVisitAudioEntity>? = null

                                                    unSyncedData = if (AppUtils.isVisitSync == "1")
                                                        AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysListAccordingToShopId(ShopActivityEntityListNew[i].shopid!!, ShopActivityEntityListNew[i].visited_date!!)
                                                    else
                                                        AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                                                ShopActivityEntityListNew[i].shopid!!, ShopActivityEntityListNew[i].visited_date!!)

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

                                            //averageShopListAdapter.updateList(dateWiseList)
                                            ShopActivityEntityListNew = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                                            Collections.reverse(ShopActivityEntityListNew)
                                        }
                                    }

                                    /*BaseActivity.isShopActivityUpdating = false
                                    averageShopListAdapter.updateList(AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate))

                                    ShopActivityEntityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

                                    Collections.reverse(ShopActivityEntityList)*/
                                }

                            } else {

                                (this as DashboardActivity).showSnackMessage(this.getString(R.string.unable_to_sync))
                                BaseActivity.isShopActivityUpdating = false
                                ShopActivityEntityListNew = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                Collections.reverse(ShopActivityEntityListNew)
                            }

                        }, { error ->

                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            if (error != null) {
                                XLog.d("ShopActivityFromAverageShop BaseActivity : ERROR:= " + error.localizedMessage + "\nUser Id" + Pref.user_id +
                                        ", Session Token" + Pref.session_token + ", SHOP_ID: " + mList[0].shopid + ", SHOP: " + mList[0].shop_name)
                                (this as DashboardActivity).showSnackMessage(this.getString(R.string.unable_to_sync))

                                ShopActivityEntityListNew = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)

                                Collections.reverse(ShopActivityEntityListNew)
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
                            XLog.d("callRevisitStatusUploadApi BaseActivity: RESPONSE " + result.status)
                            if (result.status == NetworkConstant.SUCCESS){
                                for(i in revisitStatusList.indices){
                                    AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.updateOrderStatus(revisitStatusList[i]!!.shop_revisit_uniqKey!!)
                                }

                            }
                        },{error ->
                            if (error == null) {
                                XLog.d("callRevisitStatusUploadApi BaseActivity: ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                            } else {
                                XLog.d("callRevisitStatusUploadApi BaseActivity: ERROR " + error.localizedMessage)
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
                repository.addShopWithImageCompetetorImg(objCompetetor,unsynList.get(0).shop_image,this)
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


    private fun callShopVisitImageUploadApiForAll(unSyncedList: List<ShopVisitImageModelEntity>) {
        if (!AppUtils.isOnline(this)) {
            (this as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
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
        BaseActivity.compositeDisposable.add(
                repository.visitShopWithImage(visitImageShop, unSyncedList[j].shop_image!!, this)
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
                                    for (i in ShopActivityEntityListNew.indices) {
                                        if (ShopActivityEntityListNew[i].isDurationCalculated && ShopActivityEntityListNew[i].isUploaded) {

                                            var unSyncedData: List<ShopVisitAudioEntity>? = null

                                            unSyncedData = if (AppUtils.isVisitSync == "1")
                                                AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysListAccordingToShopId(ShopActivityEntityListNew[i].shopid!!, ShopActivityEntityListNew[i].visited_date!!)
                                            else
                                                AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                                        ShopActivityEntityListNew[i].shopid!!, ShopActivityEntityListNew[i].visited_date!!)

                                            if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                                                unSyncedAudioList.add(unSyncedData[0])
                                            }
                                        }
                                    }

                                    if (unSyncedAudioList.isNotEmpty()) {
                                        j = 0
                                        callShopVisitAudioUploadApiForAll(unSyncedAudioList)
                                    } else {
                                        (this as DashboardActivity).showSnackMessage("Sync Successful")
                                    }
                                }
                            } else {

                                BaseActivity.isShopActivityUpdating = false
                                (this as DashboardActivity).showSnackMessage(logoutResponse.message!!)
                            }
                        }, { error ->
                            XLog.d("UPLOAD REVISIT ALL IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false
                            (this as DashboardActivity).showSnackMessage(this.getString(R.string.unable_to_sync))
                        })
        )
    }

    private fun syncShopVisitImage() {
        val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
        if (ShopActivityEntityListNew != null) {
            for (i in ShopActivityEntityListNew.indices) {
                /*val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(ShopActivityEntityList[i].shopid)
                if (shop.isUploaded) {*/
                if (ShopActivityEntityListNew[i].isDurationCalculated && ShopActivityEntityListNew[i].isUploaded) {

                    var unSyncedData: List<ShopVisitImageModelEntity>? = null

                    /*val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false,
                            ShopActivityEntityList[i].shopid!!, ShopActivityEntityList[i].visited_date!!)*/

                    if (AppUtils.isVisitSync == "1")
                        unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysListAccordingToShopId(ShopActivityEntityListNew[i].shopid!!, ShopActivityEntityListNew[i].visited_date!!)
                    else
                        unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false,
                                ShopActivityEntityListNew[i].shopid!!, ShopActivityEntityListNew[i].visited_date!!)

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
                for (i in ShopActivityEntityListNew.indices) {
                    if (ShopActivityEntityListNew[i].isDurationCalculated && ShopActivityEntityListNew[i].isUploaded) {

                        var unSyncedData: List<ShopVisitAudioEntity>? = null

                        unSyncedData = if (AppUtils.isVisitSync == "1")
                            AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysListAccordingToShopId(ShopActivityEntityListNew[i].shopid!!, ShopActivityEntityListNew[i].visited_date!!)
                        else
                            AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false,
                                    ShopActivityEntityListNew[i].shopid!!, ShopActivityEntityListNew[i].visited_date!!)

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

    private fun callShopVisitAudioUploadApiForAll(unSyncedList: List<ShopVisitAudioEntity>) {
        if (!AppUtils.isOnline(this)) {
            (this as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
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

        BaseActivity.compositeDisposable.add(
                repository.visitShopWithAudio(visitImageShop, unSyncedList[j].audio!!, this)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val logoutResponse = result as BaseResponse
                            XLog.d("UPLOAD REVISIT ALL AUDIO : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                            if (logoutResponse.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.shopVisitAudioDao().updateisUploaded(true, unSyncedList.get(j).shop_id!!)

                                j++
                                if (j < unSyncedList.size) {

                                    BaseActivity.isShopActivityUpdating = false
                                    callShopVisitAudioUploadApiForAll(unSyncedList)
                                } else {
                                    j = 0
                                    BaseActivity.isShopActivityUpdating = false
                                    (this as DashboardActivity).showSnackMessage("Sync Successful")


                                    //callShopDurationApi()
                                }
                            } else {

                                BaseActivity.isShopActivityUpdating = false
                                (this as DashboardActivity).showSnackMessage(logoutResponse.message!!)
                            }
                        }, { error ->
                            XLog.d("UPLOAD REVISIT ALL AUDIO : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            BaseActivity.isShopActivityUpdating = false

                            (this as DashboardActivity).showSnackMessage(this.getString(R.string.unable_to_sync))
                        })
        )
    }





    private fun syncShopList() {
        val shopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false)
        XLog.d("AUTO_LOGOUT New : syncShopList" + AppUtils.getCurrentDateTime())
        if (shopList.isEmpty() || shopList.size==0){
            uploadShopRevisitData()
        }else{
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


            callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shopList, true,
                    mAddShopDBModelEntity.doc_degree)
        }



    }

    fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, shopList: MutableList<AddShopDBModelEntity>?,
                       isFromInitView: Boolean, degree_imgPath: String?) {
        if (!AppUtils.isOnline(this)) {
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
                                XLog.d("syncShopFromShopList : BaseActivity " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)

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
                                        XLog.d("DuplicateShop : BaseActivity " + ", SHOP: " + addShop.shop_name)
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
                                    XLog.d("syncShopFromShopList : BaseActivity " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
        else {
            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShop, shop_imgPath, degree_imgPath, this)
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

}

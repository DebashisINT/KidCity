package com.kcteam

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.*
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.GpsStatusEntity
import com.kcteam.app.domain.NewGpsStatusEntity
import com.kcteam.app.domain.PerformanceEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.features.location.LocationFuzedService
import com.kcteam.features.location.LocationJobService
import com.kcteam.mappackage.SendBrod
import com.elvishew.xlog.XLog
import kotlinx.android.synthetic.main.activity_splash.*
import java.text.SimpleDateFormat
import java.util.*

class MonitorService:Service() {
    private val monitorNotiID = 201
    private var monitorBroadcast : MonitorBroadcast? = null
    var powerSaver:Boolean = false
    var isFirst:Boolean = true

    var timer : Timer? = null
    private val POWER_SAVE_MODE_SETTING_NAMES = arrayOf(
            "SmartModeStatus", // huawei setting name
            "POWER_SAVE_MODE_OPEN" // xiaomi setting name
    )

    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        if (intent != null) {
//            val action = intent.action
//            if (action != null) {
//                if (action == CustomConstants.START_MONITOR_SERVICE) {
//                    serviceStatusActionable()
//                } else if (action == CustomConstants.STOP_MONITOR_SERVICE) {
//                    //stopMonitorService()
//                }
//            }
//        }
//        return super.onStartCommand(intent, flags, startId)

        timer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                println("abc - 3 sec method");
                serviceStatusActionable()

            }
        }
        timer!!.schedule(task, 0, 8000)

        // 15 mins is 60000 * 15


        // 15 mins is 60000 * 15R
        return START_STICKY
    }

    fun serviceStatusActionable() {

        XLog.d("MonitorService running : Time :" + AppUtils.getCurrentDateTime())

        Log.e("abc", "startabc")
        monitorBroadcast = MonitorBroadcast()

        var powerMode: String = ""
        val powerManager = this.getSystemService(POWER_SERVICE) as PowerManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (powerManager.isPowerSaveMode) {
                Pref.PowerSaverStatus = "On"
                powerMode = "Power Save Mode ON"

                Log.e("pww", "Power Save Mode ON")
                XLog.d("pww : Power Save Mode ON" + " Time :" + AppUtils.getCurrentDateTime())

                Handler(Looper.getMainLooper()).postDelayed({
                    if (Pref.GPSAlertGlobal) {
                        if (Pref.GPSAlert) {
                            SendBrod.sendBrod(this)

                            /*val intent = Intent(this, PowerSavingSettingsActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)*/

                        }
                    }
                }, 500)

                powerSaver = true

                calculategpsStatus(false)
                //sendGPSOffBroadcast()
            } else {
                //Log.e("pww", "Power Save Mode OFF" )
                //XLog.d("pww : Power Save Mode OFF" + " Time :" + AppUtils.getCurrentDateTime())
                Pref.PowerSaverStatus = "Off"
                if (powerSaver) {
                    calculategpsStatus(true)
                }


                powerMode = "Power Save Mode OFF"

                powerSaver = false
                Handler(Looper.getMainLooper()).postDelayed({
                    if (!Pref.isLocFuzedBroadPlaying) {
                        SendBrod.stopBrod(this)

                        /*   val intent = Intent(this, DashboardActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)*/

                    }

                }, 500)
                //cancelGpsBroadcast()
            }
        }
        val newNetStatusObj = NewGpsStatusEntity()
        if (shouldShopActivityUpdate()) {
            newNetStatusObj.date_time = AppUtils.getCurrentDateTime()
            newNetStatusObj.network_status = if (AppUtils.isOnline(this)) "Online" else "Offline"
            if (FTStorageUtils.isMyServiceRunning(LocationFuzedService::class.java, this)) {
                //XLog.d("MonitorService LocationFuzedService : " + "true" + "," + " Time :" + AppUtils.getCurrentDateTime())
                //XLog.d("MonitorService Power Save Mode Status : " + powerMode + "," + " Time :" + AppUtils.getCurrentDateTime())
                /*if(powerSaver){
                    sendGPSOffBroadcast()
                }else{
                    cancelGpsBroadcast()
                }*/
                newNetStatusObj.gps_service_status = "Started"
            } else {
                newNetStatusObj.gps_service_status = "Stopped"
                if (!FTStorageUtils.isMyServiceRunning(LocationFuzedService::class.java, this)) {
                    restartLocationService()
                }
                XLog.d("MonitorService LocationFuzedService : " + "false" + "," + " Time :" + AppUtils.getCurrentDateTime())
                XLog.d("MonitorService  Power Save Mode Status : " + powerMode + "," + " Time :" + AppUtils.getCurrentDateTime())
                XLog.d("Monitor Service Stopped" + "" + "," + " Time :" + AppUtils.getCurrentDateTime())
                if (!isFirst) {
                    Log.e("abc", "abc stoptimer")
                    timer!!.cancel()
                }
                isFirst = false
            }

            Log.e("inside outside shouldGpsNetSyncDuration", AppUtils.getCurrentDateTime())
            if (shouldGpsNetSyncDuration() && !Pref.GPSNetworkIntervalMins.equals("0")) {
                Log.e("inside shouldGpsNetSyncDuration", AppUtils.getCurrentDateTime())
                AppDatabase.getDBInstance()?.newGpsStatusDao()?.insert(newNetStatusObj)
            }

        }


        var manu = Build.MANUFACTURER.toUpperCase(Locale.getDefault())
        if (manu.equals("XIAOMI")) {
            if (isPowerSaveModeCompat(this)) {

                println("pww - Power Save Mode ON xm")
                Log.e("pww", "Power Save Mode ON xm")
                XLog.d("pww : Power Save Mode ON xm" + " Time :" + AppUtils.getCurrentDateTime())


                powerMode = "Power Save Mode ON"

                if (Pref.GPSAlertGlobal) {
                    if (Pref.GPSAlert) {
                        SendBrod.sendBrod(this)

                        /*   val intent = Intent(this, PowerSavingSettingsActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)*/
                    }
                }
                //sendGPSOffBroadcast()
            } else {

                println("pww - Power Save Mode OFF xm")
                // Log.e("pww", "Power Save Mode OFF xm" )
                //XLog.d("pww : Power Save Mode OFF xm" + " Time :" + AppUtils.getCurrentDateTime())


                powerMode = "Power Save Mode OFF"
                SendBrod.stopBrod(this)

                /*        val intent = Intent(this, DashboardActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)*/

                //cancelGpsBroadcast()
            }
        }


        if(shouldShopActivityUpdate()){
            if (FTStorageUtils.isMyServiceRunning(LocationFuzedService::class.java, this)) {
                XLog.d("MonitorService LocationFuzedService : " + "trueee" + "," + " Time :" + AppUtils.getCurrentDateTime())
                //XLog.d("MonitorService Power Save Mode Status : " + powerMode + "," + " Time :" + AppUtils.getCurrentDateTime())
                /*if(powerSaver){
                    sendGPSOffBroadcast()
                }else{
                    cancelGpsBroadcast()
                }*/

                println("monitor_track on")
            }else{
                println("monitor_track off")
                if (!FTStorageUtils.isMyServiceRunning(LocationFuzedService::class.java, this)) {
                    restartLocationService()
                }

                XLog.d("MonitorService LocationFuzedService : " + "false" + "," + " Time :" + AppUtils.getCurrentDateTime())
                XLog.d("MonitorService  Power Save Mode Status : " + powerMode + "," + " Time :" + AppUtils.getCurrentDateTime())
                XLog.d("Monitor Service Stopped" + "" + "," + " Time :" + AppUtils.getCurrentDateTime())
                if(!isFirst){
                    Log.e("abc", "abc stoptimer" )
                    timer!!.cancel()
                }
                isFirst=false
            }
        }

    }

    private fun shouldGpsNetSyncDuration(): Boolean {
        AppUtils.changeLanguage(this,"en")

        var t = Math.abs(System.currentTimeMillis() - Pref.prevGpsNetSyncTimeStamp)

        return if (Math.abs(System.currentTimeMillis() - Pref.prevGpsNetSyncTimeStamp) > 1000 * 60 * Pref.GPSNetworkIntervalMins.toInt()) {
            Pref.prevGpsNetSyncTimeStamp = System.currentTimeMillis()
            //changeLocale()
            true
            //server timestamp is within 10 minutes of current system time
        } else {
            //changeLocale()
            false
        }
    }

    fun sendGPSOffBroadcast(){
        if(Pref.user_id.toString().length > 0){
            XLog.d("MonitorService Called for Battery Broadcast :  Time :" + AppUtils.getCurrentDateTime())
            //var notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            //notificationManager.cancel(monitorNotiID)
            MonitorBroadcast.isSound=Pref.GPSAlertwithSound
            var intent: Intent = Intent(this, MonitorBroadcast::class.java)
            intent.putExtra("notiId", monitorNotiID)
            intent.putExtra("fuzedLoc", "Fuzed Stop.")
            this.sendBroadcast(intent)
        }
    }


    fun cancelGpsBroadcast(){
        if (monitorNotiID != 0){
            if(MonitorBroadcast.player!=null){
                MonitorBroadcast.player.stop()
                MonitorBroadcast.player=null
                MonitorBroadcast.vibrator.cancel()
                MonitorBroadcast.vibrator=null
            }
            var notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(monitorNotiID)
        }
    }

    private fun isPowerSaveModeCompat(context: Context): Boolean {
        for (name in POWER_SAVE_MODE_SETTING_NAMES) {
            val mode = Settings.System.getInt(context.contentResolver, name, -1)
            if (mode != -1) {
                return POWER_SAVE_MODE_VALUES[Build.MANUFACTURER.toUpperCase(Locale.getDefault())] == mode
            }
        }
        return false
    }

    private val POWER_SAVE_MODE_VALUES = mapOf(
            "HUAWEI" to 4,
            "XIAOMI" to 1
    )

    override fun stopService(name: Intent?): Boolean {
        stopForeground(true)
        stopSelf()
        return super.stopService(name)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        stopSelf()
        timer!!.cancel()
    }

    @SuppressLint("NewApi")
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        //serviceStatusActionable()
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not Yet Implemented")
    }

    private fun checkGpsStatus() {
        val locationManager: LocationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        } else {

        }
    }


    fun shouldShopActivityUpdate(): Boolean {
        return if (Math.abs(System.currentTimeMillis() - Pref.prevShopActivityTimeStampMonitorService) > 10000) {
            Pref.prevShopActivityTimeStampMonitorService = System.currentTimeMillis()
            true
            //server timestamp is within 5 minutes of current system time
        } else {
            false
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun restartLocationService() {
        try {
            if(Pref.IsLeavePressed== true && Pref.IsLeaveGPSTrack == false){
                return
            }
            val serviceLauncher = Intent(this, LocationFuzedService::class.java)
            if (Pref.user_id != null && Pref.user_id!!.isNotEmpty()) {
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
                        XLog.d("===============================From MonitorS LocationFuzedService   Job scheduled (Base Activity) " + AppUtils.getCurrentDateTime() + "============================")
                    } else {
                        XLog.d("=====================From MonitorS LocationFuzedService Job not scheduled (Base Activity) " + AppUtils.getCurrentDateTime() + "====================================")
                    }
                } else
                    startService(serviceLauncher)
            } else {
                /*stopService(serviceLauncher)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                    jobScheduler.cancelAll()
                    XLog.d("===============================From MonitorS LocationFuzedService Job scheduler cancel (Base Activity)" + AppUtils.getCurrentDateTime() + "============================")
                }

                AlarmReceiver.stopServiceAlarm(this, 123)
                XLog.d("===========From MonitorS LocationFuzedService Service alarm is stopped (Base Activity)================")*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private fun calculategpsStatus(gpsStatus: Boolean) {

        if (!AppUtils.isOnReceived) {
            XLog.e("First time airplane off detect working")
            AppUtils.isOnReceived = true

            if (!gpsStatus) {
                //Toast.makeText(context, "GPS is disabled!", Toast.LENGTH_LONG).show()
                if (!AppUtils.isGpsOffCalled) {
                    AppUtils.isGpsOffCalled = true
                    Log.e("GpsLocationReceiver", "===========GPS is disabled=============")
                    AppUtils.gpsOffTime = dateFormat.parse(/*"18:14:55"*/AppUtils.getCurrentTime()).time
                    AppUtils.gpsDisabledTime = AppUtils.getCurrentTimeWithMeredian()
                    Log.e("GpsLocationReceiver", "gpsOffTime------------------> " + AppUtils.getTimeInHourMinuteFormat(AppUtils.gpsOffTime))

                    /*val local_intent = Intent()
                    local_intent.action = AppUtils.gpsDisabledAction
                    sendBroadcast(local_intent)*/
                }
            } else {
                if (AppUtils.isGpsOffCalled) {
                    AppUtils.isGpsOffCalled = false
                    Log.e("GpsLocationReceiver", "===========GPS is enabled================")
                    AppUtils.gpsOnTime = dateFormat.parse(AppUtils.getCurrentTime()).time
                    AppUtils.gpsEnabledTime = AppUtils.getCurrentTimeWithMeredian()
                    Log.e("GpsLocationReceiver", "gpsOnTime---------------------> " + AppUtils.getTimeInHourMinuteFormat(AppUtils.gpsOnTime))

                    /*val local_intent = Intent()
                    local_intent.action = AppUtils.gpsEnabledAction
                    sendBroadcast(local_intent)*/
                }
            }

            val performance = AppDatabase.getDBInstance()!!.performanceDao().getTodaysData(AppUtils.getCurrentDateForShopActi())
            if (performance == null) {
                if ((AppUtils.gpsOnTime - AppUtils.gpsOffTime) > 0) {
                    val performanceEntity = PerformanceEntity()
                    performanceEntity.date = AppUtils.getCurrentDateForShopActi()
                    performanceEntity.gps_off_duration = (AppUtils.gpsOnTime - AppUtils.gpsOffTime).toString()
                    Log.e("GpsLocationReceiver", "duration----------------> " + AppUtils.getTimeInHourMinuteFormat(AppUtils.gpsOnTime - AppUtils.gpsOffTime))
                    AppDatabase.getDBInstance()!!.performanceDao().insert(performanceEntity)
                    saveGPSStatus((AppUtils.gpsOnTime - AppUtils.gpsOffTime).toString())
                    AppUtils.gpsOnTime = 0
                    AppUtils.gpsOffTime = 0
                }
            } else {
                if (TextUtils.isEmpty(performance.gps_off_duration)) {
                    if ((AppUtils.gpsOnTime - AppUtils.gpsOffTime) > 0) {
                        AppDatabase.getDBInstance()!!.performanceDao().updateGPSoffDuration((AppUtils.gpsOnTime - AppUtils.gpsOffTime).toString(), AppUtils.getCurrentDateForShopActi())
                        Log.e("GpsLocationReceiver", "duration----------> " + AppUtils.getTimeInHourMinuteFormat(AppUtils.gpsOnTime - AppUtils.gpsOffTime))
                        saveGPSStatus((AppUtils.gpsOnTime - AppUtils.gpsOffTime).toString())
                        AppUtils.gpsOnTime = 0
                        AppUtils.gpsOffTime = 0
                    }
                } else {
                    if ((AppUtils.gpsOnTime - AppUtils.gpsOffTime) > 0) {
                        val duration = AppUtils.gpsOnTime - AppUtils.gpsOffTime
                        val totalDuration = performance.gps_off_duration?.toLong()!! + duration
                        Log.e("GpsLocationReceiver", "duration-------> " + AppUtils.getTimeInHourMinuteFormat(totalDuration))
                        AppDatabase.getDBInstance()!!.performanceDao().updateGPSoffDuration(totalDuration.toString(), AppUtils.getCurrentDateForShopActi())
                        saveGPSStatus(duration.toString())
                        AppUtils.gpsOnTime = 0
                        AppUtils.gpsOffTime = 0
                    }
                }
            }
            AppUtils.isOnReceived = false
        }
    }

    private fun saveGPSStatus(duration: String) {
        val gpsStatus = GpsStatusEntity()
        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        gpsStatus.gps_id = Pref.user_id + "_" + m + m
        gpsStatus.date = AppUtils.getCurrentDateForShopActi()
        gpsStatus.gps_off_time = AppUtils.gpsDisabledTime
        gpsStatus.gps_on_time = AppUtils.gpsEnabledTime
        gpsStatus.duration = duration
        AppDatabase.getDBInstance()!!.gpsStatusDao().insert(gpsStatus)
        AppUtils.gpsDisabledTime = ""
        AppUtils.gpsEnabledTime = ""
        AppUtils.isGpsReceiverCalled = false
    }

}
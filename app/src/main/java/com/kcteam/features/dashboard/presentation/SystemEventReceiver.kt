package com.kcteam.features.dashboard.presentation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import com.kcteam.app.AlarmReceiver
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.GpsStatusEntity
import com.kcteam.app.domain.PerformanceEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.location.LocationWizard
import com.kcteam.mappackage.SendBrod
import com.elvishew.xlog.XLog
import java.text.SimpleDateFormat
import java.util.*

class SystemEventReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == "android.intent.action.AIRPLANE_MODE" ||
                intent.action == "android.intent.action.ACTION_SHUTDOWN") {

            if (intent.action == "android.intent.action.BOOT_COMPLETED")
                XLog.e("=======================Boot Completed successfully ${AppUtils.getCurrentDateTime()} (SystemEventReceiver)=======================")
            else if(intent.action == "android.intent.action.AIRPLANE_MODE") {
                var text = ""

                if (Settings.Global.getInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, 0) != 0){
                    text = "Airplane Mode is On "
                    SendBrod.sendBrod(context)
                    calculategpsStatus(false)
                }
                else{
                    XLog.e("First time airplane off detect")
                    text = "Airplane Mode is Off "
                    SendBrod.stopBrod(context)
                    calculategpsStatus(true)

                }
                XLog.e("========================${text + AppUtils.getCurrentDateTime()}=======================")

            }else if(intent.action == "android.intent.action.ACTION_SHUTDOWN"){
                val locationName = LocationWizard.getLocationName(context, Pref.latitude!!.toDouble(), Pref.longitude!!.toDouble())
                XLog.e("\n======================== \n Phone Shutdown || DateTime : ${AppUtils.getCurrentDateTime()} || Location : last_lat: ${Pref.latitude} || last_long: ${Pref.longitude} || LocationName ${locationName} \n=======================")
            }else if(intent.action == "android.os.action.POWER_SAVE_MODE_CHANGED"){
                XLog.e("\n android.os.action.POWER_SAVE_MODE_CHANGED")
            }

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
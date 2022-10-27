package com.kcteam.features.alarm.presetation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.kcteam.app.AlarmReceiver
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.ToastBroadcastReceiver
import com.elvishew.xlog.XLog


/**
 * Created by Wasim on 15-02-2019.
 */
class AlarmBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            /*if (Pref.user_id!!.isNotEmpty() && Pref.willAlarmTrigger) {
                val alarmDataArr = AlarmReceiver.loadSharedPreferencesLogList(context)
                for (item in alarmDataArr) {
                    if (AppUtils.getCurrentTimeInMintes() < ((item.alarm_time_hours.toInt() * 60) + item.alarm_time_mins.toInt())) {
                        AlarmReceiver.setAlarm(context, item.alarm_time_hours.toInt(), item.alarm_time_mins.toInt(), item.requestCode)
                    }
                }
            }
            else if (!TextUtils.isEmpty(Pref.user_id)){
                val toastIntent = Intent(context, ToastBroadcastReceiver::class.java)
                val toastAlarmIntent = PendingIntent.getBroadcast(context, 1, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                val startTime = System.currentTimeMillis() //alarm starts immediately
                val backupAlarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                backupAlarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_HOUR, toastAlarmIntent)
            }*/


            if (!TextUtils.isEmpty(Pref.user_id)) {
                XLog.e("=======================Boot Completed successfully ${AppUtils.getCurrentDateTime()} (AlarmBootReceiver)=======================")

                val toastIntent = Intent(context, ToastBroadcastReceiver::class.java)
                //val toastAlarmIntent = PendingIntent.getBroadcast(context, 1, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                // FLAG_IMMUTABLE update
                val toastAlarmIntent = PendingIntent.getBroadcast(context, 1, toastIntent, PendingIntent.FLAG_IMMUTABLE)
                val startTime = System.currentTimeMillis() //alarm starts immediately
                val backupAlarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                backupAlarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_HOUR, toastAlarmIntent)

                if (Pref.willAlarmTrigger) {
                    val alarmDataArr = AlarmReceiver.loadSharedPreferencesLogList(context)
                    for (item in alarmDataArr) {
                        if (AppUtils.getCurrentTimeInMintes() < ((item.alarm_time_hours.toInt() * 60) + item.alarm_time_mins.toInt())) {
                            AlarmReceiver.setAlarm(context, item.alarm_time_hours.toInt(), item.alarm_time_mins.toInt(), item.requestCode)
                        }
                    }
                }
            }
        }
    }
}
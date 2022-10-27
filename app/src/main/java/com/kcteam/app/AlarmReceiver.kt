package com.kcteam.app

/*import com.fieldtrackingsystem.app.AlarmReceiver.Companion.SEND_ACTION*/

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.SharedWakeLock
import com.kcteam.features.alarm.model.AlarmData
import com.kcteam.features.alarm.presetation.AlarmRingingService
import com.kcteam.features.alarm.presetation.FloatingWidgetService
import com.kcteam.features.location.LocationFuzedService
import com.kcteam.features.location.LocationJobService
import com.elvishew.xlog.XLog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList


/**
 * Alarm Receiver for Data Collect & Send it to the server
 */
class AlarmReceiver : BroadcastReceiver() {


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {
//        Pref.user_id="";
//        if(Pref.login_date!= AppUtils.getCurrentDateChanged() && Pref.user_id!!.isNotEmpty())
//        Pref.isAutoLogout=true
//        Toast.makeText(context, "You can logout now!", Toast.LENGTH_LONG).show()
//        val c = Calendar.getInstance()// Toast.makeText(context,"",Toast.LENGTH_LONG).show();
//        val timeOfDay = c.get(Calendar.HOUR_OF_DAY)
//        if (timeOfDay >= 23 && timeOfDay <= 24) {
//            context.startService(Intent(context, BackgroundService::class.java))
//        }

        //        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
        //            scheduleServiceAlarm(context);
        //        } else if (SEND_ACTION.equals(intent.getAction())) {
        //            scheduleServiceAlarm(context);
        //            //If You Want To Call Api Start a intent service from here
        ////            context.startService(new Intent(context, BackgroundService.class));
        //        }

        if (Pref.willAlarmTrigger && Pref.user_id!!.isNotEmpty()) {

            if (Pref.isOnLeave.equals("true", ignoreCase = true))
                return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(context)) {
                    SharedWakeLock.get(context).acquireFullWakeLock()
                    val i = Intent(context, FloatingWidgetService::class.java)
                    i.putExtra("requestCode", intent.getIntExtra("requestCode", -1))
                    context.startService(i)
                }
            } else {
                SharedWakeLock.get(context).acquireFullWakeLock()
                val i = Intent(context, FloatingWidgetService::class.java)
                i.putExtra("requestCode", intent.getIntExtra("requestCode", -1))
                // i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startService(i)

            }
        }

        if (intent.hasExtra("request_code")) {
            if (intent.getIntExtra("request_code", 0) == 123) {
                if (FTStorageUtils.isMyServiceRunning(LocationFuzedService::class.java, context)) {
                    XLog.e("Alarm_Service: Service is running.")
                } else {
                    XLog.e("Alarm_Service: Service is stopped.")

                    if (Pref.user_id != null && Pref.user_id!!.isNotEmpty()) {

                        if(Pref.IsLeavePressed==true && Pref.IsLeaveGPSTrack == false){
                            return
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                            val componentName = ComponentName(context, LocationJobService::class.java)
                            val jobInfo = JobInfo.Builder(12, componentName)
                                    //.setRequiresCharging(true)
                                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                                    //.setRequiresDeviceIdle(true)
                                    .setOverrideDeadline(1000)
                                    .build()

                            val resultCode = jobScheduler.schedule(jobInfo)

                            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                                XLog.d("===============================Job scheduled (Alarm Receiver)============================")
                            } else {
                                XLog.d("=====================Job not scheduled (Alarm Receiver)====================================")
                            }
                        } else {
                            val serviceLauncher = Intent(context, LocationFuzedService::class.java)
                            context.startService(serviceLauncher)
                        }
                    }
                }
            }
        }

        /*  val i = Intent(context, AlarmActivityWithSnooze::class.java)
          i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
          context.startActivity(i)*/

    }

    companion object {

        /*  private val TAG = BroadcastReceiver::class.java.simpleName
          private val INTENT_REQUEST_CODE = 1338

          val SEND_ACTION = "net.iquall.mobiprobecarriereditionapi.BackgroundService.SEND_ACTION"

          private val LOG_FILE_NAME = "Alarm_Log"

          private val PREFERENCES = "net.iquall.mobiprobecarriereditionapi.BackgroundService.PREFERENCES"

          fun scheduleServiceAlarm(context: Context) {

              //        Logger.i(TAG, "Scheduling Alarm");
              Toast.makeText(context, "Scheduled", Toast.LENGTH_LONG).show()

              val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
              val intent = Intent(context, AlarmReceiver::class.java)
              intent.action = SEND_ACTION

              val broadcast = PendingIntent.getBroadcast(context, INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

              //Reprogramamos la alarma si ya existía
              //            alarmManager.cancel(broadcast);
              //            broadcast.cancel();

              val interval: Long = 1000
              //
              //        if (Prefs.isDriveTestEnabled(context)) {
              //            interval = Prefs.getDriveTestReportsInterval(context);
              //        } else {
              //            interval = Prefs.getReportsInterval(context);
              //        }

              val nextScheduleTime = System.currentTimeMillis() + interval
              //            if (!isAlarmActive(context))
              alarmManager.set(AlarmManager.RTC_WAKEUP, nextScheduleTime, broadcast)
              Log.v(LOG_FILE_NAME, "Scheduling Alarm for Time:- " + nextScheduleTime)
              //        Logger.addLogToTxtFile(context, LOG_FILE_NAME, "================================================");
          }

          */
        /**
         * Cancel all insances of this alarm.
         *//*
        fun cancelServiceAlarm(context: Context) {

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.action = SEND_ACTION

            val broadcast = PendingIntent.getBroadcast(context, INTENT_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            alarmManager.cancel(broadcast)

        }


        private fun isAlarmActive(mContext: Context): Boolean {

            return PendingIntent.getBroadcast(mContext, 0,
                    Intent(SEND_ACTION),
                    PendingIntent.FLAG_NO_CREATE) != null
        }*/


        /*  fun setMorningAlarm(context: Context, hoursOFDay: Int, minute: Int) {
              val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

              val intent = Intent(context, AlarmReceiver::class.java)

              val pendingIntent = PendingIntent.getBroadcast(context, ALARM_REQUEST_CODE_MORNING, intent, PendingIntent.FLAG_UPDATE_CURRENT)

              val calendar = Calendar.getInstance()

              calendar.timeInMillis = System.currentTimeMillis()
              calendar.set(Calendar.HOUR_OF_DAY, 15)
              calendar.set(Calendar.MINUTE, 35)

              *//*
            Alarm will be triggered once exactly at 5:30
            *//*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        }*/

        fun setAlarm(context: Context, hoursOFDay: Int, minute: Int, requestCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("requestCode", requestCode)
//            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT)
        // FLAG_IMMUTABLE update
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

            val calendar = Calendar.getInstance(Locale.ENGLISH)

            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, hoursOFDay)
            calendar.set(Calendar.MINUTE, minute)

            /*
            Alarm will be triggered once exactly at 5:30
            */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        }

        //Trigger alarm manager with entered time interval
        fun triggerAlarmManagerWithSpecifiedTime(context: Context, alarmTriggerTime: Int, requestCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("requestCode", requestCode)
//            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT)
            // FLAG_IMMUTABLE update
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

            val calendar = Calendar.getInstance(Locale.ENGLISH)

            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + alarmTriggerTime)

            /*
            Alarm will be triggered once exactly at 5:30
            */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }

        }

        /* //Trigger alarm manager with entered time interval
         fun triggerAlarmManagerWithSpecifiedTime(context: Context, alarmTriggerTime: Int, requestCode: Int) {
             val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

             val intent = Intent(context, AlarmReceiver::class.java)
             intent.putExtra("requestCode", requestCode)
             val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
             manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + alarmTriggerTime * 1000, pendingIntent)

         }*/

        //Trigger alarm manager with entered time interval
        fun triggerAlarmManagerWithSnooze(context: Context, alarmTriggerTime: Int, requestCode: Int) {
            // get a Calendar object with current time
            val cal = Calendar.getInstance(Locale.ENGLISH)
            // add alarmTriggerTime seconds to the calendar object
            cal.add(Calendar.SECOND, 20)
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager//get instance of alarm manager
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, 0)
            // FLAG_IMMUTABLE update
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent,PendingIntent.FLAG_IMMUTABLE)
            manager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)//set alarm manager with entered timer by converting into milliseconds


        }

        fun stopAlarmManager(context: Context, alarmRequestCode: Int) {
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(context, alarmRequestCode, alarmIntent, PendingIntent.FLAG_NO_CREATE)
            // FLAG_IMMUTABLE update
            val pendingIntent = PendingIntent.getBroadcast(context, alarmRequestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
            if (pendingIntent != null) {
                manager.cancel(pendingIntent)//cancel the alarm manager of the pending intent
            }
            context.stopService(Intent(context, AlarmRingingService::class.java))

        }

        fun saveSharedPreferencesLogList(context: Context, alarmData: ArrayList<AlarmData>) {
            val mPrefs = context.getSharedPreferences("ALARM_DATA_FTS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            val gson = Gson()
            val json = gson.toJson(alarmData)
            prefsEditor.putString("myJson", json)
            prefsEditor.commit()
        }

        fun loadSharedPreferencesLogList(context: Context): ArrayList<AlarmData> {
            var alarmData: ArrayList<AlarmData> = ArrayList<AlarmData>()
            val mPrefs = context.getSharedPreferences("ALARM_DATA_FTS", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = mPrefs.getString("myJson", "")
            if (json?.isEmpty()!!) {
                alarmData = ArrayList<AlarmData>()
            } else {
                val type = object : TypeToken<List<AlarmData>>() {

                }.type
                alarmData = gson.fromJson(json, type)
            }
            return alarmData
        }

        fun setServiceAlarm(context: Context, minute: Int, requestCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("request_code", requestCode)
            //val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            // FLAG_IMMUTABLE update
            val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)

            val calendar = Calendar.getInstance(Locale.ENGLISH)

            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1)


            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }*/

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, (minute * 60 * 1000).toLong(), pendingIntent)
        }

        fun stopServiceAlarm(context: Context, alarmRequestCode: Int) {
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(context, NewAlarmReceiver::class.java)
//            val pendingIntent = PendingIntent.getBroadcast(context, alarmRequestCode, alarmIntent, PendingIntent.FLAG_NO_CREATE)
            // FLAG_IMMUTABLE update
            val pendingIntent = PendingIntent.getBroadcast(context, alarmRequestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
            if (pendingIntent != null) {
                manager.cancel(pendingIntent)//cancel the alarm manager of the pending intent
            }
        }
    }
}



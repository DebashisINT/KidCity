package com.kcteam.app

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.elvishew.xlog.XLog
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.features.location.LocationFuzedService
import com.kcteam.features.location.LocationJobService

/**
 * Created by Saikat on 30-04-2019.
 */
class NewAlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra("request_code")) {
            if (intent.getIntExtra("request_code", 0) == 123) {
                XLog.e("Time(NewAlarmReceiver): " + AppUtils.getCurrentDateTime())

                if (FTStorageUtils.isMyServiceRunning(LocationFuzedService::class.java, context)) {
                    XLog.e("==========Service is running (NewAlarmReceiver)===========")
                } else {
                    XLog.e("==========Service is stopped (NewAlarmReceiver)===========")

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
                                XLog.d("===============================Job scheduled (NewAlarmReceiver)============================")
                            } else {
                                XLog.d("=====================Job not scheduled (NewAlarmReceiver)====================================")
                            }
                        } else {
                            val serviceLauncher = Intent(context, LocationFuzedService::class.java)
                            context.startService(serviceLauncher)
                        }
                    }
                }
            }
        }
    }
}
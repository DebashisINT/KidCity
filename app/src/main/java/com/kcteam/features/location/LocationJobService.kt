package com.kcteam.features.location

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import android.text.TextUtils
import com.elvishew.xlog.XLog
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.SystemEventReceiver


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
/**
 * Created by riddhi on 7/11/17.
 */
class LocationJobService : JobService() {

    /*private val eventReceiver: SystemEventReceiver by lazy {
        SystemEventReceiver()
    }*/

    companion object {
        private var updateFence = ""

        fun updateFence(s: String) {
            updateFence = s
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartJob(p0: JobParameters?): Boolean {

        if(Pref.IsLeavePressed== true && Pref.IsLeaveGPSTrack == false){
            return true
        }

        XLog.d("=============================Start Job " + AppUtils.getCurrentDateTime() + "==============================")

        val myIntent = Intent(this, LocationFuzedService::class.java)

        if (!TextUtils.isEmpty(updateFence)) {
            val bundle = Bundle()
            bundle.putString("ACTION", "UPDATE_FENCE")
            myIntent.putExtras(bundle)
        }

        /*try {
            startService(myIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            startForegroundService(myIntent)
        }*/
        
        startForegroundService(myIntent)

        /*registerReceiver(eventReceiver, IntentFilter().apply {
            addAction("android.intent.action.AIRPLANE_MODE")
            addAction("android.intent.action.BOOT_COMPLETED")
        })*/

        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        XLog.d("=========================Stop Job " + AppUtils.getCurrentDateTime() + "============================")

        //unregisterReceiver(eventReceiver)

        if (Pref.user_id != null && Pref.user_id!!.isNotEmpty()) {
            val componentName = ComponentName(this, LocationJobService::class.java)
            val jobInfo = JobInfo.Builder(12, componentName)
                    //.setRequiresCharging(true)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY/*JobInfo.NETWORK_TYPE_NONE*/)
                    //.setRequiresDeviceIdle(true)
                    //.setPersisted(true)
                    .setOverrideDeadline(1000)
                    .build()

            val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val resultCode = jobScheduler.schedule(jobInfo)

            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                XLog.d("========================Job rescheduled (LocationJobService) " + AppUtils.getCurrentDateTime() + "==============================")
            } else {
                XLog.d("========================Job not rescheduled (LocationJobService) " + AppUtils.getCurrentDateTime() + "==========================")
            }
        }

        return true
    }
}
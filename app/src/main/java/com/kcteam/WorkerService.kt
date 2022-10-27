package com.kcteam

import androidx.work.Worker
import androidx.work.WorkerParameters
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.ListenableWorker
import com.kcteam.app.AlarmReceiver
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.features.location.LocationFuzedService
import com.kcteam.features.location.LocationJobService
import com.elvishew.xlog.XLog

class WorkerService(context: Context,workParm:WorkerParameters):Worker(context,workParm) {

    override fun doWork(): Result {
        println("tag_ worker ${AppUtils.contx.toString()}")
        XLog.d("Worker doWork")
        try{
            if(AppUtils.contx!=null){
                if (!FTStorageUtils.isMyServiceRunning(LocationFuzedService::class.java, AppUtils.contx)) {
                    XLog.d("Worker doWork LocationFuzedService called")
                    serviceStatusActionable()
                }else{
                    XLog.d("Worker doWork LocationFuzedService running")
                }
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }
        return ListenableWorker.Result.success()
    }

    override fun onStopped() {
        println("tag_ worker stopped")
        super.onStopped()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun serviceStatusActionable() {
        try {
            if(Pref.IsLeavePressed== true && Pref.IsLeaveGPSTrack == false){
                return
            }
            val serviceLauncher = Intent(AppUtils.contx, LocationFuzedService::class.java)
            if (Pref.user_id != null && Pref.user_id!!.isNotEmpty()) {
                XLog.d("serviceStatusActionable")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val jobScheduler = AppUtils.contx!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                    val componentName = ComponentName(AppUtils.contx as Activity, LocationJobService::class.java)
                    val jobInfo = JobInfo.Builder(12, componentName)
                        //.setRequiresCharging(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        //.setRequiresDeviceIdle(true)
                        .setOverrideDeadline(1000)
                        .build()

                    val resultCode = jobScheduler.schedule(jobInfo)

                    if (resultCode == JobScheduler.RESULT_SUCCESS) {
                        XLog.d("=============================== doWork serviceStatusActionable   Job scheduled  " + AppUtils.getCurrentDateTime() + "============================")
                    } else {
                        XLog.d("===================== doWork serviceStatusActionable Job not scheduled  " + AppUtils.getCurrentDateTime() + "====================================")
                    }
                } else
                    AppUtils.contx!!.startService(serviceLauncher)
            } else {
                AppUtils.contx!!.stopService(serviceLauncher)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val jobScheduler = AppUtils.contx!!.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                    jobScheduler.cancelAll()
                    XLog.d("===============================Worker Job scheduler cancel (Base Activity)" + AppUtils.getCurrentDateTime() + "============================")

                    /*if (AppUtils.mGoogleAPIClient != null) {
                        AppUtils.mGoogleAPIClient?.disconnect()
                        AppUtils.mGoogleAPIClient = null
                    }*/
                }

                /*val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()*/

                AlarmReceiver.stopServiceAlarm(AppUtils.contx as Activity, 123)
                XLog.d("===========Worker Service alarm is stopped (Base Activity)================")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
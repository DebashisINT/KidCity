package com.kcteam.features.dashboard.presentation

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.elvishew.xlog.XLog
import com.kcteam.app.Pref

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
/**
 * Created by Saikat on 05-03-2019.
 */
class CollectionOrderAlertJobService: JobService() {

    override fun onStartJob(p0: JobParameters?): Boolean {

        XLog.d("============Start Job (CollectionOrderAlertService)==============")

        startService(Intent(this, CollectionOrderAlertService::class.java))

        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {

        XLog.d("===========Stop Job (CollectionOrderAlertService)================")

        try {
            if (Pref.user_id != null && Pref.user_id!!.isNotEmpty()) {
                val componentName = ComponentName(this, CollectionOrderAlertService::class.java)
                val jobInfo = JobInfo.Builder(12, componentName)
                        //.setRequiresCharging(true)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        //.setRequiresDeviceIdle(true)
                        .setOverrideDeadline(1000)
                        .build()

                val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val resultCode = jobScheduler.schedule(jobInfo)

                if (resultCode == JobScheduler.RESULT_SUCCESS) {
                    XLog.d("===========Job rescheduled (CollectionOrderAlertService)================")
                } else {
                    XLog.d("==========Job not rescheduled (CollectionOrderAlertService)==============")
                }
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }

        return true
    }
}
package com.kcteam.features.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kcteam.app.utils.AppUtils
import com.elvishew.xlog.XLog


class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        XLog.d("geofencePendingIntent : onReceive   " + " , " + " Time :" + AppUtils.getCurrentDateTime() + " , onReceive ")

        GeofenceTransitionsJobIntentService.enqueueWork(context, intent)

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
        } else {
            val intent_ = Intent(context, GeofenceTransitionsIntentService::class.java)
            context.startService(intent_)
        }*/
    }
}

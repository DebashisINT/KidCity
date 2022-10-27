package com.kcteam.features.dashboard.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Saikat on 06-03-2019.
 */

class ToastBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AlertJobIntentService.enqueueWork(context, intent)
        }
        else {*/
            val serviceIntent = Intent(context, AlertService::class.java)
            context.startService(serviceIntent)
        //}
    }
}

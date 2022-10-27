package com.kcteam.features.gpsstatus

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.location.LocationManager




/**
 * Created by Pratishruti on 18-01-2018.
 */
class GpsReceiver
/**
 * initializes receiver with callback
 * @param iLocationCallBack Location callback
 */
(private val locationCallBack: LocationCallBack) : BroadcastReceiver() {

    /**
     * triggers on receiving external broadcast
     * @param context Context
     * @param intent Intent
     */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action!!.matches("android.location.PROVIDERS_CHANGED".toRegex())) {
            val lm =context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationCallBack.onLocationTriggered(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))
        }
    }
}
package com.kcteam.features.geofence

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.elvishew.xlog.XLog
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.location.GeofenceBroadcastReceiver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

/**
 * Created by Pratishruti on 22-02-2018.
 */
class GeofenceService : Service(), OnCompleteListener<Void> {
    override fun onComplete(p0: Task<Void>) {

    }

    private val mGeofenceList: ArrayList<Geofence> = arrayListOf()
    /**
     * Provides access to the Geofencing API.
     */
    private lateinit var mGeofencingClient: GeofencingClient

    private enum class PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    private lateinit var mGeofencePendingIntent: PendingIntent

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        XLog.d("onCreate:GeofenceService " + " , " + " Time :" + AppUtils.getCurrentDateTime())
        populateandAddGeofences()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        XLog.d("onStartCommand:GeofenceService " + " , " + " Time :" + AppUtils.getCurrentDateTime())
        return START_STICKY
    }

    fun populateandAddGeofences() {

        XLog.d("populateandAddGeofences : " + " , " + " Time :" + AppUtils.getCurrentDateTime())
        XLog.d("populateandAddGeofences : " + " , " + " Circular radius : " + Pref.gpsAccuracy.toFloat())

        val list = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        //val list = AppDatabase.getDBInstance()!!.addShopEntryDao().getTop10()

        XLog.e("Geofence:== list size=====> " + list.size)

        val newList = java.util.ArrayList<AddShopDBModelEntity>()

        for (i in list.indices) {
            /*val userId = list[i].shop_id.substring(0, list[i].shop_id.indexOf("_"))
            if (userId == Pref.user_id)*/
            //if (!list[i].visited)
                newList.add(list[i])
        }

        XLog.e("Geofence:== new list size=====> " + newList.size)

        var mRadious:Float = Pref.gpsAccuracy.toFloat()
        if(Pref.IsRestrictNearbyGeofence){
            mRadious = Pref.GeofencingRelaxationinMeter.toFloat()
//            mRadious=9999000.99F
        }


        for (i in 0 until newList.size) {

            mGeofenceList.add(Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(newList[i].shop_id)

                    //Sets the delay between GEOFENCE_TRANSITION_ENTER and GEOFENCE_TRANSITION_DWELLING in milliseconds
                    .setLoiteringDelay(1000 * 60)

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            newList[i].shopLat,
                            newList[i].shopLong,
                            //Pref.gpsAccuracy.toFloat()
                            mRadious
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(24 * 60 * 60 * 1000)//will expire after a day

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or
                            Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)

                    // Create the geofence.
                    .build())
        }

        addGeofences()
    }

    /**
     * Adds geofences. This method should be called after the user has granted the location
     * permission.
     */
    @SuppressLint("MissingPermission")
    private fun addGeofences() {

        XLog.d("addGeofences : " + " , " + " Time :" + AppUtils.getCurrentDateTime())

        mGeofencingClient = LocationServices.getGeofencingClient(this)
        val request = getGeofencingRequest()

        request?.let {
            mGeofencingClient.addGeofences(request, getGeofencePendingIntent())
                    .addOnCompleteListener(this)
            XLog.d("addGeofences Success: " + " , " + " Time :" + AppUtils.getCurrentDateTime())
        }
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private fun getGeofencingRequest(): GeofencingRequest? {
        val builder = GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);
        XLog.d("addGeofences : " + " ,Geofence Size : " + mGeofenceList.size)
        // Return a GeofencingRequest.
        return if (mGeofenceList.size > 0) builder.build() else null
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private fun getGeofencePendingIntent(): PendingIntent {
        // Reuse the PendingIntent if we already have it.
        if (::mGeofencePendingIntent.isInitialized) {
            return mGeofencePendingIntent
        }
        XLog.d("geofencePendingIntent : " + " , " + " Time :" + AppUtils.getCurrentDateTime() + " , New Pending Intent for Geofence ")
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        //mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return mGeofencePendingIntent
    }

    override fun onDestroy() {
        super.onDestroy()
        XLog.d("onDestroy : " + "GeofenceService")
        removeGeofence()
    }

    private fun removeGeofence() {
        XLog.d("removeGeofence : ")
        Pref.isGeoFenceAdded = false
        mGeofencingClient.removeGeofences(getGeofencePendingIntent())
    }

}
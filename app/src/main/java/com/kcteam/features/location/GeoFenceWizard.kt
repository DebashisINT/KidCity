package com.kcteam.features.location

import com.google.android.gms.location.Geofence
import java.util.*


/**
 * Created by riddhi on 22/12/17.
 */
class GeoFenceWizard {

    /**
     * Create a Geofence from location lat long
     */
    fun getGeoFence(latitude: Double, longitude: Double): Geofence {

        val id = UUID.randomUUID().toString()
        return Geofence.Builder()
                .setRequestId(id)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .setCircularRegion(latitude, longitude, 200f) // Try changing your radius
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()

    }

}
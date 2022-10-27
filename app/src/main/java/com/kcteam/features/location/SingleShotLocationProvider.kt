package com.kcteam.features.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

/**
 * Created by riddhi on 27/12/17.
 */

object SingleShotLocationProvider {

    interface LocationCallback {
        fun onNewLocationAvailable(location: Location)
        fun onStatusChanged(status: String)
        fun onProviderEnabled(status: String)
        fun onProviderDisabled(status: String)
    }

    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
    // contents of the else and if. Also be sure to check gps permission/settings are allowed.
    // call usually takes <10ms
    @SuppressLint("MissingPermission")
    fun requestSingleUpdate(context: Context, callback: LocationCallback) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var fusedLocationClient: FusedLocationProviderClient
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isGPSEnabled) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE

            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    callback.onNewLocationAvailable(location)
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                    callback.onStatusChanged(provider)
                }

                override fun onProviderEnabled(provider: String) {
                    callback.onProviderEnabled(provider)
                }

                override fun onProviderDisabled(provider: String) {
                    callback.onProviderDisabled(provider)
                }
            }, null)

            /*val t = Timer()
            t.schedule(object : TimerTask() {
                override fun run() {
                    callback.onNewLocationAvailable(Location(""))
                }
            }, 100)*/


        } else if (isNetworkEnabled) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            locationManager.requestSingleUpdate(criteria, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    callback.onNewLocationAvailable(location)
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

                override fun onProviderEnabled(provider: String) {}

                override fun onProviderDisabled(provider: String) {}
            }, null)
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null)
                            callback.onNewLocationAvailable(location)
                    }
        }

    }

    @SuppressLint("MissingPermission")
    fun requestSingleUpdateNearbyShop(context: Context, callback: LocationCallback) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        var locationCallback: com.google.android.gms.location.LocationCallback?=null
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null)
                callback.onNewLocationAvailable(location)
            else {

                /*val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE

                locationManager.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        callback.onNewLocationAvailable(location)
                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                        callback.onStatusChanged(provider)
                    }

                    override fun onProviderEnabled(provider: String) {
                        callback.onProviderEnabled(provider)
                    }

                    override fun onProviderDisabled(provider: String) {
                        callback.onProviderDisabled(provider)
                    }
                }, null)*/


                locationCallback = object : com.google.android.gms.location.LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        if (locationResult != null && locationResult.locations.isNotEmpty()) {
                            val newLocation = locationResult.locations[0]
                            callback.onNewLocationAvailable(newLocation)
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(LocationRequest(), locationCallback!!, null)

            }
        }
    }

    // consider returning Location instead of this dummy wrapper class
//    class GPSCoordinates {
//        var longitude = -1f
//        var latitude = -1f
//
//        constructor(theLatitude: Float, theLongitude: Float) {
//            longitude = theLongitude
//            latitude = theLatitude
//        }
//
//        constructor(theLatitude: Double, theLongitude: Double) {
//            longitude = theLongitude.toFloat()
//            latitude = theLatitude.toFloat()
//        }
//    }

}

package com.kcteam.features.location

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.kcteam.app.Pref
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by riddhi on 7/11/17.
 */
open class LocationWizard {

    companion object {

        var NEARBY_RADIUS = Pref.gpsAccuracy.toInt() // unit as meters

        fun getDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            val earthRadius = 6371.75 // ( 3958.75 miles or 6371.0 kilometers)
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2 - lng1)
            val sindLat = Math.sin(dLat / 2)
            val sindLng = Math.sin(dLng / 2)
            val a = Math.pow(sindLat, 2.0) + (Math.pow(sindLng, 2.0)
                    * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)))
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

            return round(earthRadius * c, 2)

        }

        fun round(value: Double, places: Int): Double {
            var value = value
            if (places < 0) throw IllegalArgumentException()

            val factor = Math.pow(10.0, places.toDouble()).toLong()
            value = value * factor
            val tmp = Math.round(value)
            return tmp.toDouble() / factor
        }

        fun isServiceRunning(context: Context): Boolean {
            val manager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            return manager.getRunningServices(Integer.MAX_VALUE).any { "com.fieldtrackingsystem.features.location.LocationFuzedService" == it.service.className }
        }

        fun getLocationName(mContext: Context, latitude: Double, longitude: Double): String {

            var location = "Unknown"

            try {
                val geocoder: Geocoder = Geocoder(mContext, Locale.ENGLISH)
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
                location = addresses[0].getAddressLine(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return location

        }

        fun getAdressFromLatlng(mContext: Context, lat: Double?, lng: Double?): String {

            var location = "Unknown"

            try {
                val geocoder: Geocoder = Geocoder(mContext, Locale.ENGLISH)
                val addresses: List<Address> = geocoder.getFromLocation(lat!!, lng!!, 1)
                location = addresses[0].getAddressLine(0)

                if (location.contains("http")) {
                    location = addresses[0].locality + " " + addresses[0].adminArea + " " + addresses[0].countryName + " " + addresses[0].postalCode
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return location
        }

        fun getNewLocationName(mContext: Context, latitude: Double, longitude: Double): String {

            var location = "Unknown"

            try {
                val geocoder = Geocoder(mContext, Locale.ENGLISH)
                var addresses: List<Address>? = null
                //for (i in 0..5) {
                for (i in 0..3) {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (addresses != null)
                        break
                }
                location = addresses?.get(0)?.getAddressLine(0)!!
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return location

        }

        fun getPostalCode(mContext: Context, latitude: Double, longitude: Double): String {
            var postalcode = ""
            try {
                val geocoder = Geocoder(mContext, Locale.ENGLISH)
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
                postalcode = addresses[0].postalCode

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return postalcode

        }

        fun getState(mContext: Context, latitude: Double, longitude: Double): String {
            var postalcode = ""
            try {
                val geocoder = Geocoder(mContext, Locale.ENGLISH)
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
                postalcode = addresses[0].adminArea

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return postalcode

        }

        fun getCity(mContext: Context, latitude: Double, longitude: Double): String {
            var postalcode = ""
            try {
                val geocoder = Geocoder(mContext, Locale.ENGLISH)
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
                postalcode = addresses[0].locality

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return postalcode

        }

        fun getCountry(mContext: Context, latitude: Double, longitude: Double): String {
            var postalcode = ""
            try {
                val geocoder = Geocoder(mContext, Locale.ENGLISH)
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
                postalcode = addresses[0].countryName

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return postalcode

        }

        private fun getAddressFromLocation(mContext: Context, location: Location): Address {
            var geocoder = Geocoder(mContext)
            var address = Address(Locale.ENGLISH)
            try {
                var addr = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<*>
                if (addr.isNotEmpty()) {
                    address = addr[0] as Address
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return address
        }

        fun getLocationFromAddress(context: Context, strAddress: String): LatLng? {
            var coder = Geocoder(context)
            var address: List<Address>
            var p1: LatLng? = null

            try {
                // May throw an IOException
                address = coder.getFromLocationName(strAddress, 5)
                if (address == null || address.size == 0) {
                    return null
                }
                var location = address.get(0)
                location.latitude
                location.longitude

                p1 = LatLng(location.latitude, location.longitude)

            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            return p1
        }

        fun getTimeStamp(): String {
            val tsLong = System.currentTimeMillis() / 1000
            return tsLong.toString()
        }

        fun getFormattedTime24Hours(is24Hour: Boolean): String {
            val df: SimpleDateFormat
            if (is24Hour)
                df = SimpleDateFormat("hh:mm", Locale.ENGLISH)
            else
                df = SimpleDateFormat("hh:mm\naa", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getHour(): String {
            val df = SimpleDateFormat("hh", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getMinute(): String {
            val df = SimpleDateFormat("mm", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getMeridiem(): String {
            val df = SimpleDateFormat("aa", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun locationAuthenticity(previousTimestamp: String, nextTimeStamp: String) {
            //Thresh Hold speed 60 Km/h
            Math.abs(System.currentTimeMillis() - Pref.prevTimeStamp) / 1000 //seconds
        }

    }


}
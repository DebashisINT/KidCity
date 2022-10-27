package com.kcteam.app

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Bundle
import android.content.DialogInterface
import android.location.*
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import android.util.Log
import com.kcteam.R
import java.io.IOException
import java.util.*


/**
 * Created by Pratishruti on 23-03-2018.
 */
class GPSTracker(private val mContext: Context) : Service(), LocationListener {

    // flag for GPS Status
    internal var isGPSEnabled = false

    // flag for network status
    internal var isNetworkEnabled = false

    // flag for GPS Tracking is enabled
    /**
     * GPSTracker isGPSTrackingEnabled getter.
     * Check GPS/wifi is enabled
     */
   public var isGPSTrackingEnabled = false
        internal set

    internal var location: Location? = null
    internal var latitude: Double = 0.toDouble()
    internal var longitude: Double = 0.toDouble()

    // How many Geocoder should return our GPSTracker
    internal var geocoderMaxResults = 1

    // Declaring a Location Manager
    protected var locationManager: LocationManager? = null

    // Store LocationManager.GPS_PROVIDER or LocationManager.NETWORK_PROVIDER information
    private var provider_info: String? = null

    init {
        getLocation()
    }

    @SuppressLint("MissingPermission")
            /**
     * Try to get my current location by GPS or Network Provider
     */
    fun getLocation() {

        try {
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager

            //getting GPS status
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

            //getting network status
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            // Try to get location if you GPS Service is enabled
            if (isGPSEnabled) {
                this.isGPSTrackingEnabled = true

                Log.d(TAG, "Application use GPS Service")

                /*
                 * This provider determines location using
                 * satellites. Depending on conditions, this provider may take a while to return
                 * a location fix.
                 */

                provider_info = LocationManager.GPS_PROVIDER

            } else if (isNetworkEnabled) { // Try to get location if you Network Service is enabled
                this.isGPSTrackingEnabled = true

                Log.d(TAG, "Application use Network State to get GPS coordinates")

                /*
                 * This provider determines location based on
                 * availability of cell tower and WiFi access points. Results are retrieved
                 * by means of a network lookup.
                 */
                provider_info = LocationManager.NETWORK_PROVIDER

            }

            // Application can use GPS or Network Provider
            if (!provider_info!!.isEmpty()) {
                locationManager!!.requestLocationUpdates(
                        provider_info!!,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                )

                if (locationManager != null) {
                    location = locationManager!!.getLastKnownLocation(provider_info!!)
                    updateGPSCoordinates()
                }
            }
        } catch (e: Exception) {
            //e.printStackTrace();
            Log.e(TAG, "Impossible to connect to LocationManager", e)
        }

    }

    /**
     * Update GPSTracker latitude and longitude
     */
    fun updateGPSCoordinates() {
        if (location != null) {
            latitude = location!!.getLatitude()
            longitude = location!!.getLongitude()
        }
    }

    /**
     * GPSTracker latitude getter and setter
     * @return latitude
     */
    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.getLatitude()
        }

        return latitude
    }

    /**
     * GPSTracker longitude getter and setter
     * @return
     */
    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.getLongitude()
        }

        return longitude
    }

    /**
     * Stop using GPS listener
     * Calling this method will stop using GPS in your app
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this)
        }
    }

    /**
     * Function to show settings alert dialog
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)

        //Setting Dialog Title
        alertDialog.setTitle("GPSAlertDialogTitle")

        //Setting Dialog Message
        alertDialog.setMessage("GPSAlertDialogMessage")

        //On Pressing Setting button
        alertDialog.setPositiveButton("action_settings", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(intent)
        })

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        alertDialog.show()
    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
    </Address> */
    fun getGeocoderAddress(context: Context): List<Address>? {
        if (location != null) {

            val geocoder = Geocoder(context, Locale.ENGLISH)

            try {

                return geocoder.getFromLocation(latitude, longitude, geocoderMaxResults)
            } catch (e: IOException) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e)
            }

        }

        return null
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    fun getAddressLine(context: Context): String? {
        val addresses = getGeocoderAddress(context)

        if (addresses != null && addresses.size > 0) {
            val address = addresses[0]

            return address.getAddressLine(0)
        } else {
            return null
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    fun getLocality(context: Context): String? {
        val addresses = getGeocoderAddress(context)

        if (addresses != null && addresses.size > 0) {
            val address = addresses[0]

            return address.getLocality()
        } else {
            return null
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    fun getPostalCode(context: Context): String? {
        val addresses = getGeocoderAddress(context)

        if (addresses != null && addresses.size > 0) {
            val address = addresses[0]

            return address.getPostalCode()
        } else {
            return null
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    fun getCountryName(context: Context): String? {
        val addresses = getGeocoderAddress(context)
        if (addresses != null && addresses.size > 0) {
            val address = addresses[0]

            return address.getCountryName()
        } else {
            return null
        }
    }

    override fun onLocationChanged(location: Location) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {

        // Get Class Name
        private val TAG = GPSTracker::class.java.name

        // The minimum distance to change updates in meters
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters

        // The minimum time between updates in milliseconds
        private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong() // 1 minute
    }
}
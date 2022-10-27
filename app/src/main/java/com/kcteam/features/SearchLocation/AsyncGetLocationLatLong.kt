package com.kcteam.features.SearchLocation

import android.content.Context
import android.location.Location
import android.os.AsyncTask


class AsyncGetLocationLatLong(private val mContext: Context, private val description: String, private val place_id: String, private val pickLocationLatLng: OnGetLocationLatLongListener) : AsyncTask<Void, Void, Void>() {

    private var location = Location("")

    override fun doInBackground(vararg params: Void?): Void? {
        location = LocationPlcaeId.searchLatLng(place_id)
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        try {
            if (location.latitude > 0) {
                pickLocationLatLng.onGetLocationLatLong(location)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnGetLocationLatLongListener {
        fun onGetLocationLatLong(location: Location)
    }
}

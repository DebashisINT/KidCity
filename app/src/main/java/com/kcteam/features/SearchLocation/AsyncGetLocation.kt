package com.kcteam.features.SearchLocation

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.places.Places
import java.util.*
import java.util.concurrent.TimeUnit


class AsyncGetLocation(private val mContext: Context, private val name: String,
                       private val mGoogleApiClient: GoogleApiClient?, private val mInterface: GetLocationListener?) :
        AsyncTask<Void, Void, Void>() {

    private var mListPlace: ArrayList<EditTextAddressModel>? = null

    override fun doInBackground(vararg params: Void?): Void? {

        mListPlace = getPredictions(mContext, name, mGoogleApiClient) //LocationAutocomplete.autocomplete(name)

        return null
    }

    private fun getPredictions(baseActivity: Context, constraint: String, mGoogleApiClient: GoogleApiClient?): ArrayList<EditTextAddressModel>? {

        if (mGoogleApiClient != null) {

            Log.i("", "Executing autocomplete query for: $constraint")

            val results = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, constraint, null, null)

            // Wait for predictions, set the timeout.
            val autocompletePredictions = results.await(30, TimeUnit.SECONDS)

            val status = autocompletePredictions.status
            if (!status.isSuccess) {
                //Toast.makeText(baseActivity, "Error: " + status.toString(), Toast.LENGTH_SHORT).show()
                Log.e("", "Error getting place predictions: " + status.toString())
                autocompletePredictions.release()
                return null
            }

            Log.i("", "Query completed. Received " + autocompletePredictions.count
                    + " predictions.")
            val iterator = autocompletePredictions.iterator()
            val resultList = ArrayList<EditTextAddressModel>(autocompletePredictions.count)
            while (iterator.hasNext()) {
                val prediction = iterator.next()
                val editTextModel = EditTextAddressModel()
                editTextModel.place_id = prediction?.placeId!!
                editTextModel.description = prediction.getFullText(null) as String

                Log.d("Fetch address", "primary text-----> " + prediction.getPrimaryText(null))
                Log.d("Fetch address", "secondary text-----> " + prediction.getSecondaryText(null))

                resultList.add(editTextModel/*PlaceAutocomplete(prediction.placeId, prediction.getFullText(null))*/)
            }
            // Buffer release
            autocompletePredictions.release()
            return resultList
        }

        Log.e("", "Google API client is not connected.")
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        try {
            if (mListPlace!!.size > 0) {
                mInterface?.getLocationAddress(mListPlace)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    interface GetLocationListener {
        fun getLocationAddress(mListPlace: ArrayList<EditTextAddressModel>?)
    }
}

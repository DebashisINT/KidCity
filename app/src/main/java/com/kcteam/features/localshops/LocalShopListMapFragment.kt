package com.kcteam.features.localshops

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.location.LocationWizard
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pnikosis.materialishprogress.ProgressWheel

/**
 * Created by Saikat on 28-Apr-20.
 */
class LocalShopListMapFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mContext: Context
    private var mGoogleMap: GoogleMap? = null
    private var mCurrLocationMarker: MutableList<Marker> = ArrayList()
    private var mShopCircles: MutableList<Circle> = ArrayList()
    private lateinit var progress_wheel: ProgressWheel

    companion object {
        private var list: MutableList<AddShopDBModelEntity>? = null

        fun newInstance(objects: Any): LocalShopListMapFragment {
            val fragment = LocalShopListMapFragment()
            if (objects != null) {
                if (objects is ArrayList<*>) {
                    list = objects as ArrayList<AddShopDBModelEntity>
                }
            }
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_nearby_shops_map, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment;
        mapFragment.getMapAsync(this)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = false

        if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude))
            addMarkerActivity(Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
        else
            progress_wheel.stopSpinning()
    }

    private fun addMarkerActivity(cur_lat: Double, cur_lang: Double) {
        if (mGoogleMap == null)
            return

        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(cur_lat, cur_lang), 17f))
        //list?.clear()

        /*val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            val userId = allShopList[i].shop_id.substring(0, allShopList[i].shop_id.indexOf("_"))
            if (userId == Pref.user_id)
                newList.add(allShopList[i])
        }
        list = newList*/

        clearMarker()
        list?.forEach {
            addMarker(it.shopLat, it.shopLong, it.shopName, it.address, BitmapDescriptorFactory.HUE_ORANGE)
        }

        getCurrentLocationMarker(cur_lat, cur_lang)
        progress_wheel.stopSpinning()
    }

    private fun getCurrentLocationMarker(cur_lat: Double, cur_lang: Double) {
        val locationName = LocationWizard.getLocationName(mContext, cur_lat, cur_lang)
        var currentMarker: Marker
        val latLng = LatLng(cur_lat, cur_lang)
        val markerOptions = MarkerOptions()
        markerOptions.apply {
            position(latLng)
            title(locationName)
            snippet(locationName)
            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        }.let {
            currentMarker = mGoogleMap?.addMarker(it)!!
            mCurrLocationMarker.add(currentMarker)
        }
    }

    private fun addMarker(lat: Double, lang: Double, markerName: String, shopAddress: String, markerColor: Float) {
        val latLng = LatLng(lat, lang)
        val currentMarker: Marker

        val markerOptions = MarkerOptions()
        /*markerOptions.position(latLng)
        markerOptions.title(markerName)
        markerOptions.snippet(shopAddress)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shop_map_pointer))*/
//
        /*val circleOptions = CircleOptions()
                .center(LatLng(lat, lang))   //set center
                .radius(100.0)   //set radius in meters
                .fillColor(0x30ff0000)
                .strokeColor(Color.RED)
                .strokeWidth(2f)

        mShopCircles.add(mGoogleMap!!.addCircle(circleOptions))*/

        currentMarker = mGoogleMap!!.addMarker(markerOptions.apply {
            position(latLng)
            title(markerName)
            snippet(shopAddress)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shop_map_pointer))
        })!!
        mCurrLocationMarker.add(currentMarker)
    }

    private fun clearMarker() {
        for (i in 0 until mCurrLocationMarker.size) {
            mCurrLocationMarker[i].remove()
        }
        for (i in 0 until mShopCircles.size) {
            mShopCircles[i].remove()
        }
    }
}
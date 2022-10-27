package com.kcteam.features.orderhistory

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.utils.AppUtils.Companion.bitmapDescriptorFromVector
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.UserLocationDataEntity
import com.kcteam.features.member.model.TeamLocDataModel
import org.jetbrains.anko.collections.forEachWithIndex
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions



/**
 * Created by Saikat on 26-Mar-20.
 */
class ActivityMapFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var mContext: Context
    private lateinit var mapFragment: SupportMapFragment
    private var mGoogleMap: GoogleMap? = null
    private lateinit var progress_wheel: ProgressWheel

    private var memberLocList: ArrayList<TeamLocDataModel>? = null

    companion object {
        private var activityLocationList: MutableList<UserLocationDataEntity>? = null

        fun newInstance(objects: Any): ActivityMapFragment {
            val fragment = ActivityMapFragment()
            if (objects != null) {
                if (objects is ArrayList<*>) {
                    if (objects[0] is UserLocationDataEntity)
                        activityLocationList = objects as ArrayList<UserLocationDataEntity>
                    else if (objects[0] is TeamLocDataModel) {
                        activityLocationList = null

                        val bundle = Bundle()
                        bundle.putSerializable("locList", objects)
                        fragment.arguments = bundle
                    }
                }
            }
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        if (arguments != null && arguments?.getSerializable("locList") != null)
            memberLocList = arguments?.getSerializable("locList") as ArrayList<TeamLocDataModel>?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_activity_map, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = false

        var latitude = 0.0
        var longitude = 0.0

        activityLocationList?.let {
            latitude = it[0].latitude.toDouble()
            longitude = it[0].longitude.toDouble()
        }

        memberLocList?.let {
            latitude = it[0].latitude.toDouble()
            longitude = it[0].longitude.toDouble()
        }

        /*if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude))
            addMarkerActivity(Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
        else
            progress_wheel.stopSpinning()*/

        addMarkerActivity(latitude, longitude)
    }

    private var i = 0
    private fun addMarkerActivity(lat: Double, lang: Double) {
        if (mGoogleMap == null)
            return

        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lang), 15f))

        /*if (activityLocationList != null) {
            activityLocationList?.forEach {
                addMarker(it.latitude, it.longitude, it.locationName)
                i++
            }
        } else if (memberLocList != null) {
            memberLocList?.forEach {
                addMarker(it.latitude, it.longitude, it.location_name)
                i++
            }
        }*/

        /*activityLocationList?.forEach {
            addMarker(it.latitude, it.longitude, it.locationName, i)
            Log.e("ActivityMap","=====activityLocationList=====")
            i++
        }*/

        val options = PolylineOptions().width(5f).color(Color.RED).geodesic(true)

        activityLocationList?.forEachWithIndex { i, userLocationDataEntity ->
            addMarker(userLocationDataEntity.latitude, userLocationDataEntity.longitude, userLocationDataEntity.locationName, i,
                    activityLocationList?.size)
            options.add(LatLng(userLocationDataEntity.latitude.toDouble(), userLocationDataEntity.longitude.toDouble()))
            Log.e("ActivityMap", "=====activityLocationList=====")
        }


        memberLocList?.forEachIndexed { index, teamLocDataModel ->
            addMarker(teamLocDataModel.latitude, teamLocDataModel.longitude, teamLocDataModel.location_name, index,
                    memberLocList?.size)
            options.add(LatLng(teamLocDataModel.latitude.toDouble(), teamLocDataModel.longitude.toDouble()))
            Log.e("ActivityMap", "=====memberLocList=====")
        }
        mGoogleMap?.addPolyline(options)


        /* memberLocList?.forEach {
             addMarker(it.latitude, it.longitude, it.location_name, i)
             Log.e("ActivityMap","=====memberLocList=====")
             i++
         }*/

        //getCurrentLocationMarker(cur_lat, cur_lang)


        progress_wheel.stopSpinning()
    }

    private fun addMarker(latitude: String, longitude: String, locationName: String, position: Int, size: Int?) {
        val latLng = LatLng(latitude.toDouble(), longitude.toDouble())
        val markerOptions = MarkerOptions()

        Log.e("ActivityMap", "$position markar")


        markerOptions.also {
            it.position(latLng)
            it.title(locationName)
            it.snippet(locationName)

            when (position) {
                0 -> {
                    Log.e("ActivityMap", "1st markar")
                    it.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_registered_shop_direction_select))
                }
                size?.minus(1) -> {
                    Log.e("ActivityMap", "Last markar")
                    it.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_green_location))
                }
                else -> {
                    Log.e("ActivityMap", "Intermidiate markar")
                    it.icon(bitmapDescriptorFromVector(mContext, R.drawable.ic_deep_pink_map))
                }
            }
            mGoogleMap?.addMarker(it)!!
        }
    }

    private fun getCurrentLocationMarker(cur_lat: Double, cur_lang: Double) {
        val locationName = LocationWizard.getLocationName(mContext, cur_lat, cur_lang)
        val latLng = LatLng(cur_lat, cur_lang)
        val markerOptions = MarkerOptions()

        markerOptions.also {
            it.position(latLng)
            it.title(locationName)
            it.snippet(locationName)
            it.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
            mGoogleMap?.addMarker(it)!!
        }
    }
}
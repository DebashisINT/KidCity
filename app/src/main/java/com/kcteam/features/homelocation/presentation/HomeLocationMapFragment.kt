package com.kcteam.features.homelocation.presentation

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.member.model.TeamLocDataModel
import com.kcteam.features.orderhistory.ActivityMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.pnikosis.materialishprogress.ProgressWheel
import org.jetbrains.anko.collections.forEachWithIndex

class HomeLocationMapFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var mContext: Context

    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_footer: RelativeLayout
    private lateinit var rl_map_main: RelativeLayout

    private lateinit var mapFragment: SupportMapFragment
    private var mGoogleMap: GoogleMap? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
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

        rl_footer = view.findViewById(R.id.rl_footer)
        rl_footer.visibility = View.GONE

        rl_map_main = view.findViewById(R.id.rl_map_main)
        rl_map_main.setOnClickListener(null)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = false

        if (mGoogleMap == null)
            return

        val latLong = LatLng(Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble())
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15f))

        MarkerOptions().also {
            it.position(latLong)
            it.title("${Pref.user_name} home location")
            it.snippet(LocationWizard.getLocationName(mContext, Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble()))
            //it.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_registered_shop_direction_select))
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
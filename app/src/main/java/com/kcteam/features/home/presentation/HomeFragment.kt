package com.kcteam.features.home.presentation

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory


/**
 * Created by Pratishruti on 27-10-2017.
 */
class HomeFragment : BaseFragment(), OnMapReadyCallback {
    private var mFragmentManager: FragmentManager? = null
    private var mSupportMapFragment: SupportMapFragment? = null
    private var mFragmentTransaction: FragmentTransaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater!!.inflate(R.layout.fragment_add_shop, container, false)
        initView(view)
        initMap()
        return view
    }

    private fun initMap() {
//        mSupportMapFragment = childFragmentManager.findFragmentById(R.id.googlemap_FL) as? SupportMapFragment
//        if (mSupportMapFragment == null) {
//            mFragmentManager = getFragmentManager()
//            mSupportMapFragment = SupportMapFragment.newInstance()
//            mFragmentTransaction = fragmentManager!!.beginTransaction()
//            mFragmentTransaction!!.replace(R.id.frame_layout_container, mSupportMapFragment).commit()
//
//        }
//        if (mSupportMapFragment != null) {
//            mSupportMapFragment!!.getMapAsync(this)
//        }
    }

    private fun initView(view: View?) {
//        mSupportMapFragment = childFragmentManager.findFragmentById(R.id.googlemap_FL) as SupportMapFragment
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap!!.getUiSettings().setAllGesturesEnabled(true);
        googleMap!!.addMarker(MarkerOptions().position(LatLng(22.5726, 88.3639)).title("Marker"))
        val cameraPosition = CameraPosition.Builder().target(LatLng(22.5726, 88.3639)).zoom(15.0f).build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        googleMap.moveCamera(cameraUpdate)
    }
}


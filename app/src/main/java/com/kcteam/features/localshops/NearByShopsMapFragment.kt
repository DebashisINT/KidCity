package com.kcteam.features.localshops

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.location.LocationWizard
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pnikosis.materialishprogress.ProgressWheel

/**
 * Created by Pratishruti on 15-03-2018.
 */
class NearByShopsMapFragment : BaseFragment(), View.OnClickListener, OnMapReadyCallback {
    internal lateinit var mapFragment: SupportMapFragment
    private lateinit var mContext: Context
    internal var mGoogleMap: GoogleMap? = null
    private var list: MutableList<AddShopDBModelEntity> = ArrayList()
    //    internal var mCurrLocationMarker: Marker? = null
    var mCurrLocationMarker: MutableList<Marker> = ArrayList()
    var mShopCircles: MutableList<Circle> = ArrayList()
    //    private var curr_lat=0.0
//    private var curr_long=0.0
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_map_main: RelativeLayout

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater
                .inflate(R.layout.fragment_nearby_shops_map, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment;
        mapFragment.getMapAsync(this)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        rl_map_main = view.findViewById(R.id.rl_map_main)
        rl_map_main.setOnClickListener(null)
//        fetchCurrentLocation()
        return view

    }

    public fun fetchCurrentLocation() {
        progress_wheel.spin()
        if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude))
            addMarkerActivity(Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
        else
            progress_wheel.stopSpinning()
//        SingleShotLocationProvider.requestSingleUpdate(mContext,
//                object : SingleShotLocationProvider.LocationCallback {
//                    override fun onStatusChanged(status: String) {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onProviderEnabled(status: String) {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onProviderDisabled(status: String) {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onNewLocationAvailable(location: Location) {
//                        curr_lat=location.latitude
//                        curr_long=location.longitude
//                        addMarkerActivity(curr_lat,curr_long)
////                        getNearyShopList(location)
//                    }
//
//                })


    }


//    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        // Get the SupportMapFragment and request notification when the map is ready to be used.
//        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment;
//        SingleShotLocationProvider.requestSingleUpdate(mContext,
//                object : SingleShotLocationProvider.LocationCallback {
//                    override fun onStatusChanged(status: String) {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onProviderEnabled(status: String) {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onProviderDisabled(status: String) {
//                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                    }
//
//                    override fun onNewLocationAvailable(location: Location) {
//                        curr_lat=location.latitude
//                        curr_long=location.longitude
//                        getNearyShopList(location)
//                    }
//
//                })
//
//    }

    fun getNearyShopList(location: Location) {
        list.clear()
        list = AppDatabase.getDBInstance()!!.addShopEntryDao().all

//        if (allShopList.size > 0)
//            for (i in 0..allShopList.size - 1) {
//                var shopLat: Double = allShopList[i].shopLat
//                var shopLong: Double = allShopList[i].shopLong
//
//                if (shopLat != null && shopLong != null) {
//                    var shopLocation = Location("")
//                    shopLocation.latitude = shopLat
//                    shopLocation.longitude = shopLong
//                    var isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
//                    if (isShopNearby) {
//                        list.add(allShopList.get(i))
//                    }
//
//                }
//            }


    }


    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap!!.uiSettings.isZoomControlsEnabled = false
//        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(22.154438, 78.755449), 4f))

        if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude))
            addMarkerActivity(Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
        else
            progress_wheel.stopSpinning()

//        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(curr_lat, curr_long), 15f))

    }

    fun addMarkerActivity(cur_lat: Double, cur_lang: Double) {
        if (mGoogleMap == null)
            return
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(cur_lat, cur_lang), 17f))
        list.clear()
        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all

        val newList = java.util.ArrayList<AddShopDBModelEntity>()

        for (i in allShopList.indices) {
            val userId = allShopList[i].shop_id.substring(0, allShopList[i].shop_id.indexOf("_"))
            if (userId == Pref.user_id)
                newList.add(allShopList[i])
        }
        list = newList

        clearMarker()
        for (i in 0 until list.size) {
            try {
                addMarker(list[i].shopLat, list[i].shopLong, list[i].shopName, list[i].address, BitmapDescriptorFactory.HUE_ORANGE,
                        list[i].party_status_id, list[i].entity_id, list[i].type)
            }
            catch (e: Exception) {
                e.printStackTrace()

                addMarker(list[i].shopLat, list[i].shopLong, list[i].shopName, list[i].address, BitmapDescriptorFactory.HUE_ORANGE,
                        "", "", "")
            }
        }
        getCurrentLocationMarker(cur_lat, cur_lang)
        progress_wheel.stopSpinning()
    }

    private fun getCurrentLocationMarker(cur_lat: Double, cur_lang: Double) {
        val locationName = LocationWizard.getLocationName(mContext, cur_lat, cur_lang)
        var currentMarker: Marker
        val latLng = LatLng(cur_lat, cur_lang)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("My Location")
        markerOptions.snippet(locationName)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        currentMarker = mGoogleMap!!.addMarker(markerOptions!!)!!
        mCurrLocationMarker.add(currentMarker)
    }

    private fun addMarker(lat: Double, lang: Double, markerName: String, shopAddress: String, markerColor: Float,
                          partyStatusId: String, entityId: String, type: String) {
        val latLng = LatLng(lat, lang)
        var currentMarker: Marker

        val entity = AppDatabase.getDBInstance()?.entityDao()?.getSingleItem(entityId)
        val partyStatus = AppDatabase.getDBInstance()?.partyStatusDao()?.getSingleItem(partyStatusId)
        var snippetBody = ""
        snippetBody = if (Pref.willShowEntityTypeforShop && Pref.willShowPartyStatus) {
            if (type == "1" && entity != null && partyStatus != null)
                "$shopAddress\nEntity Type: ${entity.name}\nParty Status: ${partyStatus.name}"
            else if (type == "1" && entity != null)
                "$shopAddress\nEntity Type: ${entity.name}"
            else if (partyStatus != null)
                "$shopAddress\nParty Status: ${partyStatus.name}"
            else
                shopAddress
        }
        else if (entity != null && type == "1" && Pref.willShowEntityTypeforShop)
            "$shopAddress\nEntity Type: ${entity.name}"
        else if (partyStatus != null && Pref.willShowPartyStatus)
            "$shopAddress\nParty Status: ${partyStatus.name}"
        else
            shopAddress

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title(markerName)
        markerOptions.snippet(snippetBody)
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shop_map_pointer))
//
        val circleOptions = CircleOptions()
                .center(LatLng(lat, lang))   //set center
                .radius(100.0)   //set radius in meters
                .fillColor(0x30ff0000)
                .strokeColor(Color.RED)
                .strokeWidth(2f)


//        mGoogleMap!!.addCircle(circleOptions)
        mShopCircles.add(mGoogleMap!!.addCircle(circleOptions))
        currentMarker = mGoogleMap!!.addMarker(markerOptions)!!
        mCurrLocationMarker.add(currentMarker)

        mGoogleMap!!.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val info = LinearLayout(mContext)
                info.orientation = LinearLayout.VERTICAL
                val title = TextView(mContext)
                title.setTextColor(Color.BLACK)
                title.gravity = Gravity.CENTER
                title.setTypeface(null, Typeface.BOLD)
                title.text = marker.title
                val snippet = TextView(mContext)
                snippet.setTextColor(Color.GRAY)
                snippet.text = marker.snippet
                info.addView(title)
                info.addView(snippet)
                return info
            }
        })
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
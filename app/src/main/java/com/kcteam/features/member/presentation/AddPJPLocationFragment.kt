package com.kcteam.features.member.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.SearchLocation.AsyncGetLocation
import com.kcteam.features.SearchLocation.EditTextAddressModel
import com.kcteam.features.SearchLocation.LocationAdapter
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.model.PJPMapData
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.IOException
import java.util.*

/**
 * Created by Saikat on 21-Apr-20.
 */
class AddPJPLocationFragment : BaseFragment(), OnMapReadyCallback, View.OnClickListener, LocationAdapter.OnLocationItemClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private lateinit var mContext: Context

    private lateinit var et_radius: AppCustomEditText
    private lateinit var tv_save_btn: AppCustomTextView
    private lateinit var mapFragment: SupportMapFragment
    //private lateinit var autocomplete_fragment: AutocompleteSupportFragment
    private var mGoogleMap: GoogleMap? = null
    private var currentLocationMarker: Marker? = null
    var selectedLat = 0.0
    var selectedLong = 0.0
    var selectedAddress = ""
    private var circle: Circle? = null
    var radius = ""

    private var locationAdapter: LocationAdapter? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var rv_address_list: RecyclerView
    private lateinit var search_progress: ProgressBar
    private lateinit var cross_iv: ImageView
    private lateinit var searchLocation_edt: AppCustomEditText
    private var isLocationClicked = false
    private var mLocationRequest: LocationRequest? = null
    private var isLocationPicked = false
    private var placeId: String = ""
    private var markerOptions: MarkerOptions? = null

    companion object {

        fun getInstance(obj: Any): AddPJPLocationFragment {
            val fragment = AddPJPLocationFragment()

            if (!TextUtils.isEmpty(obj.toString())) {
                if (obj is PJPMapData) {
                    val bundle = Bundle()
                    bundle.putSerializable("pjp_map", obj)
                    fragment.arguments = bundle
                }
            }

            return fragment
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        if (arguments?.getSerializable("pjp_map") != null) {
            val pjpMap = arguments?.getSerializable("pjp_map") as PJPMapData

            pjpMap?.let {
                selectedLat = it.lat
                selectedLong = it.long
                radius = it.radius
                selectedAddress = it.address
            }
        }

        mGoogleApiClient = GoogleApiClient.Builder(mContext)
                .enableAutoManage(activity!!, 0, this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        mGoogleApiClient?.connect()
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }


    override fun onLocationChanged(location: Location) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_pjp_location, container, false)
        initView(view)
        initClickListener()
        initTextChangeListener()
        Handler().postDelayed(Runnable {
            setData()
        }, 1000)

        return view
    }

    private fun initView(view: View) {

        view.apply {
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            tv_save_btn = findViewById(R.id.tv_save_btn)
            et_radius = findViewById(R.id.et_radius)
            cross_iv = findViewById(R.id.cross_iv)
            searchLocation_edt = findViewById(R.id.searchLocation_edt)
            search_progress = findViewById(R.id.search_progress)
            rv_address_list = findViewById(R.id.rv_address_list)
            //autocomplete_fragment = (mContext as DashboardActivity).supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        }

        mapFragment.getMapAsync(this)

        /*autocomplete_fragment.setPlaceFields(Arrays.asList<Place.Field>(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.ADDRESS))

        autocomplete_fragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.e("Add PJP", "Place: " + place.name + ", " + place.id)

                markerOptions = MarkerOptions()
                markerOptions?.position(place.latLng!!)

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                selectedAddress = place.address!!
                markerOptions?.title(selectedAddress)

                // Clears the previously touched position
                //googleMap?.clear();

                // Animating to the touched position
                val cameraPosition = CameraPosition.Builder().target(LatLng(place.latLng?.latitude!!, place.latLng?.longitude!!)).zoom(15f).build()
                mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                //googleMap?.animateCamera(CameraUpdateFactory.newLatLng(p0));

                // Placing a marker on the touched position
                if (currentLocationMarker != null)
                    currentLocationMarker?.remove()

                currentLocationMarker = mGoogleMap?.addMarker(markerOptions)!!


                if (!TextUtils.isEmpty(et_radius.text.toString().trim())) {
                    drawCircle(et_radius.text.toString().trim())
                }
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.e("Add PJP", "An error occurred=========> " + status)
            }
        })*/

        locationAdapter = LocationAdapter(mContext, ArrayList<EditTextAddressModel>(), this)
        rv_address_list.apply {
            layoutManager = LinearLayoutManager(mContext)
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            adapter = locationAdapter
        }

        searchLocation_edt.setOnKeyListener(View.OnKeyListener { p0, p1, p2 ->
            if ((p2?.action == KeyEvent.ACTION_DOWN)) {
                if (!isLocationClicked)
                    return@OnKeyListener false
                else {
                    isLocationClicked = false
                    searchLocation_edt.setText(searchLocation_edt.text.toString())
                    rv_address_list.visibility = View.GONE
                    Places.GeoDataApi.getPlaceById(mGoogleApiClient!!, placeId).setResultCallback { places ->
                        if (places.status.isSuccess) {

                            try {
                                selectedLat = places.get(0).latLng.latitude
                                selectedLong = places.get(0).latLng.longitude

                                markerOptions?.position(places.get(0).latLng)
                                markerOptions?.title(searchLocation_edt.text.toString())
                                //googleMap?.clear();
                                mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLng(places.get(0).latLng))
                                if (currentLocationMarker != null)
                                    currentLocationMarker?.remove()
                                currentLocationMarker = mGoogleMap?.addMarker(markerOptions!!)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        places.release()
                    }
                }


                return@OnKeyListener true
            }
            false
        })

    }

    private fun initClickListener() {
        searchLocation_edt.setOnClickListener(this)
        cross_iv.setOnClickListener(this)
        tv_save_btn.setOnClickListener(this)
    }

    private fun initTextChangeListener() {
        searchLocation_edt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (searchLocation_edt.isFocused && !isLocationClicked) {

                    if (searchLocation_edt.text.toString().trim().length > 2) {
                        if (AppUtils.isOnline(mContext)) {

                            if (!mGoogleApiClient?.isConnected!!)
                                mGoogleApiClient?.connect()

                            fetchAddress(searchLocation_edt.text.toString().trim(), mGoogleApiClient)
                        } else {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                        }
                    } else {
                        search_progress.visibility = View.GONE
                        locationAdapter?.refreshList(ArrayList<EditTextAddressModel>())
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable) {
            }
        })

        et_radius.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (TextUtils.isEmpty(et_radius.text.toString().trim())) {
                    circle?.remove()
                    return
                }

                drawCircle(et_radius.text.toString().trim())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun fetchAddress(locText: String, mGoogleApiClient: GoogleApiClient?) {
        search_progress.visibility = View.VISIBLE
        AsyncGetLocation(mContext, locText, mGoogleApiClient, object : AsyncGetLocation.GetLocationListener {
            override fun getLocationAddress(mListPlace: ArrayList<EditTextAddressModel>?) {
                search_progress.visibility = View.GONE
                if (searchLocation_edt.text.toString().trim().isNotEmpty()) {
                    rv_address_list.visibility = View.VISIBLE
                    locationAdapter?.refreshList(mListPlace)
                    rv_address_list.scrollToPosition(mListPlace?.size!!)
                } else {
                    rv_address_list.visibility = View.GONE
                }
                //locationAdapter?.refreshList(mListPlace)
            }
        }).execute()
    }

    private fun setData() {
        if (!TextUtils.isEmpty(radius)) {

            et_radius.setText(radius)

            drawCircle(radius)

            val markerOptions = MarkerOptions()
            markerOptions.position(LatLng(selectedLat, selectedLong))
            markerOptions.title(selectedAddress)

            val cameraPosition = CameraPosition.Builder().target(LatLng(selectedLat, selectedLong)).zoom(15f).build()
            mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            // Placing a marker on the touched position
            if (currentLocationMarker != null)
                currentLocationMarker?.remove()

            currentLocationMarker = mGoogleMap?.addMarker(markerOptions)!!
        } else {
            val markerOptions = MarkerOptions()
            markerOptions.position(LatLng(Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()))

            fetchPinnedAddress(LatLng(Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()))

            markerOptions.title(selectedAddress)

            val cameraPosition = CameraPosition.Builder().target(LatLng(selectedLat, selectedLong)).zoom(15f).build()
            mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            // Placing a marker on the touched position
            if (currentLocationMarker != null)
                currentLocationMarker?.remove()

            currentLocationMarker = mGoogleMap?.addMarker(markerOptions)!!

            search_progress.visibility = View.GONE
            isLocationPicked = true
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.searchLocation_edt -> {
                searchLocation_edt.requestFocus()
                isLocationClicked = false
            }
            R.id.cross_iv -> {
                searchLocation_edt.setText("")
                searchLocation_edt.requestFocus()
                isLocationPicked = true
            }
            R.id.tv_save_btn -> {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                when {
                    selectedLat == 0.0 -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_loc))
                    TextUtils.isEmpty(et_radius.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_radius))
                    else -> {
                        if (isLocationPicked) {
                            radius = et_radius.text.toString().trim()
                            (mContext as DashboardActivity).onBackPressed()
                        }
                    }
                }
            }
        }
    }

    override fun onLocationItemClick(description: String, place_id: String) {
        isLocationClicked = true
        isLocationPicked = true
        searchLocation_edt.setText(description)
        selectedAddress = description
        rv_address_list.visibility = View.GONE
        placeId = place_id
        Places.GeoDataApi.getPlaceById(mGoogleApiClient!!, place_id).setResultCallback { places ->
            if (places.status.isSuccess) {

                try {

                    selectedLat = places.get(0).latLng.latitude
                    selectedLong = places.get(0).latLng.longitude


                    markerOptions?.position(places.get(0).latLng);
                    markerOptions?.title(description);
                    //googleMap?.clear();
                    val cameraPosition = CameraPosition.Builder().target(LatLng(selectedLat, selectedLong)).zoom(15f).build()
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                    if (currentLocationMarker != null)
                        currentLocationMarker?.remove()

                    currentLocationMarker = mGoogleMap?.addMarker(markerOptions!!)!!

                    fetchPinnedAddress(LatLng(selectedLat, selectedLong))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            places.release()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = true

        mGoogleMap?.setOnMapClickListener { latLng ->

            markerOptions = MarkerOptions()
            markerOptions?.position(latLng)

            // Setting the title for the marker.
            // This will be displayed on taping the marker

            if (latLng != null) {
                fetchPinnedAddress(latLng)
            }

            markerOptions?.title(selectedAddress)

            // Clears the previously touched position
            //googleMap?.clear();

            // Animating to the touched position
            val cameraPosition = CameraPosition.Builder().target(LatLng(latLng.latitude, latLng.longitude)).zoom(15f).build()
            mGoogleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            //googleMap?.animateCamera(CameraUpdateFactory.newLatLng(p0));

            // Placing a marker on the touched position
            if (currentLocationMarker != null)
                currentLocationMarker?.remove()

            currentLocationMarker = mGoogleMap?.addMarker(markerOptions!!)!!


            if (!TextUtils.isEmpty(et_radius.text.toString().trim())) {
                drawCircle(et_radius.text.toString().trim())
            }
        }
    }

    private fun fetchPinnedAddress(latLong: LatLng?) {

        try {
            val geocoder = Geocoder(mContext, Locale.getDefault())
            val addresses: List<android.location.Address> = geocoder.getFromLocation(latLong?.latitude!!, latLong.longitude, 1)!!;
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                selectedAddress = address.getAddressLine(0)
                selectedLat = latLong.latitude
                selectedLong = latLong.longitude

                searchLocation_edt.setText(selectedAddress)
            }


        } catch (ex: IOException) {
            ex.printStackTrace();
        }
    }

    private fun drawCircle(radius: String) {
        val circleOptions = CircleOptions()
                .center(LatLng(selectedLat, selectedLong))   //set center
                .radius(radius.toDouble())   //set radius in meters
                .fillColor(0x30ff0000)
                .strokeColor(Color.RED)
                .strokeWidth(2f)

        circle?.remove()
        circle = mGoogleMap?.addCircle(circleOptions)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onStop() {
        mGoogleApiClient?.takeIf { it.isConnected }?.let {
            it.stopAutoManage(activity!!)
            it.disconnect()
        }

        super.onStop()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onDestroyView() {
        super.onDestroyView()

        mGoogleApiClient?.apply {
            stopAutoManage(activity!!)
            disconnect()
        }
    }
}
package com.kcteam.features.SearchLocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.mappackage.MapActivity.MY_PERMISSIONS_REQUEST_LOCATION
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.io.IOException
import java.util.*


/**
 * Created by Pratishruti on 27-10-2017.
 */
class SearchLocationFragment : BaseFragment(), View.OnClickListener, LocationAdapter.OnLocationItemClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private lateinit var captureShopImage: ImageView
    private lateinit var shopImage: RelativeLayout
    private lateinit var mContext: Context
    private lateinit var searchLocation_edt: AppCustomEditText
    private lateinit var mapView: MapView
    private var locationAdapter: LocationAdapter? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    internal var geocoder: Geocoder? = null
    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0
    private var isLocationClicked = false
    private var mLocationRequest: LocationRequest? = null
    private var googleMap: GoogleMap? = null
    private lateinit var rv_address_list: RecyclerView
    private lateinit var search_progress: ProgressBar
    private var map: GoogleMap? = null
    internal lateinit var location: Location
    internal val handler = Handler()
    private var locationManager: LocationManager? = null
    private var provider: String? = null
    private val minDistance = 1000
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var cross_iv: ImageView
    private var placeId: String = ""
    private lateinit var save_TV: AppCustomTextView
    var fullAdd: String = ""
    var pinCode: String = ""
    private var markerOptions: MarkerOptions? = null
    var isLocationPicked = false
    private var currentLocationMarker: Marker? = null
    private var state = ""
    private var city = ""
    private var country = ""

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.searchlocation, container, false)

        mGoogleApiClient = GoogleApiClient.Builder(mContext)
                .enableAutoManage(activity!!, 0, this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()
        mGoogleApiClient?.connect()

        initView(view, savedInstanceState)

        return view
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient?.isConnected!!) {
            mGoogleApiClient?.stopAutoManage(activity!!);
            mGoogleApiClient?.disconnect()
        }
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun initView(view: View, savedInstanceState: Bundle?) {
        save_TV = view.findViewById(R.id.save_TV)
        cross_iv = view.findViewById(R.id.cross_iv)
        searchLocation_edt = view.findViewById(R.id.searchLocation_edt)
        search_progress = view.findViewById(R.id.search_progress)
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        rv_address_list = view.findViewById(R.id.rv_address_list)

        locationAdapter = LocationAdapter(mContext, ArrayList<EditTextAddressModel>(), this)
        rv_address_list.layoutManager = LinearLayoutManager(mContext)
        rv_address_list.itemAnimator = DefaultItemAnimator() as RecyclerView.ItemAnimator?
        rv_address_list.setHasFixedSize(true)
        rv_address_list.adapter = locationAdapter

        searchLocation_edt.setOnClickListener(this)
        cross_iv.setOnClickListener(this)
        save_TV.setOnClickListener(this)
        initTextChangeListener()

        if (checkLocationPermission()) {
            InitiateMap()
        } else
            return


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
                                selectedLatitude = places.get(0).latLng.latitude
                                selectedLongitude = places.get(0).latLng.longitude



                                markerOptions?.position(places.get(0).latLng);
                                markerOptions?.title(searchLocation_edt.text.toString());
                                //googleMap?.clear();
                                googleMap?.animateCamera(CameraUpdateFactory.newLatLng(places.get(0).latLng));
                                if (currentLocationMarker != null)
                                    currentLocationMarker?.remove()
                                currentLocationMarker = googleMap?.addMarker(markerOptions!!);
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        places.release()
                    }
                }


                return@OnKeyListener true;
            }
            false;
        });
    }

    private fun fetchPinnedAddress(latLong: LatLng?): String {

        try {
            geocoder = Geocoder(mContext, Locale.getDefault())
            val addresses: List<android.location.Address> = geocoder?.getFromLocation(latLong?.latitude!!, latLong.longitude, 1)!!;
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0];
                fullAdd = address.getAddressLine(0);

                if (addresses[0].postalCode != null)
                    pinCode = addresses[0].postalCode

                if (addresses[0].adminArea != null)
                    state = addresses[0].adminArea

                if (addresses[0].locality != null)
                    city = addresses[0].locality

                if (addresses[0].countryName != null)
                    country = addresses[0].countryName

                selectedLatitude = latLong?.latitude!!
                selectedLongitude = latLong.longitude

                //Reset MapView
                if (markerOptions != null) {
                    markerOptions?.position(latLong);
                    markerOptions?.title(latLong.latitude.toString() + " : " + latLong.longitude.toString());
                }
                //googleMap?.clear();
                googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLong));
                if (currentLocationMarker != null)
                    currentLocationMarker?.remove()

                currentLocationMarker = googleMap?.addMarker(markerOptions!!);

                searchLocation_edt.setText(fullAdd)
            }


        } catch (ex: IOException) {
            ex.printStackTrace();
        }
        return fullAdd;
    }

    private fun InitiateMap() {
        try {
            MapsInitializer.initialize(mContext.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync(OnMapReadyCallback { mMap ->
            googleMap = mMap
            googleMap?.uiSettings!!.isMapToolbarEnabled = false;
            // For showing a move to my location button
            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return@OnMapReadyCallback
            }
            googleMap?.isMyLocationEnabled = true
            /* mMap.addMarker(MarkerOptions().position(sydney).title(address));
             mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

            markerOptions = MarkerOptions()

            googleMap?.setOnMapClickListener { p0 ->
                if (AppUtils.isOnline(mContext))
                    search_progress.visibility = View.VISIBLE

                // Setting the position for the marker
                selectedLatitude = p0?.latitude!!
                selectedLongitude = p0.longitude

                markerOptions?.position(p0);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                markerOptions?.title(p0.latitude.toString() + " : " + p0.longitude.toString());

                // Clears the previously touched position
                //googleMap?.clear();

                // Animating to the touched position
                val cameraPosition = CameraPosition.Builder().target(LatLng(selectedLatitude, selectedLongitude)).zoom(15f).build()
                googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                //googleMap?.animateCamera(CameraUpdateFactory.newLatLng(p0));

                // Placing a marker on the touched position
                if (currentLocationMarker != null)
                    currentLocationMarker?.remove()
                currentLocationMarker = googleMap?.addMarker(markerOptions!!)!!;

                search_progress.visibility = View.GONE
                isLocationPicked = true
                if (p0 != null) {
                    fetchPinnedAddress(p0)
                }
            }


            googleMap?.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
                override fun onMarkerDrag(p0: Marker) {
                    selectedLatitude = p0?.position!!.latitude
                    selectedLongitude = p0.position!!.longitude

                    isLocationPicked = true
                    p0.title = p0.position!!.latitude.toString() + " : " + p0.position!!.longitude.toString()
                    //googleMap?.clear();
                    val cameraPosition = CameraPosition.Builder().target(LatLng(selectedLatitude, selectedLongitude)).zoom(15f).build()
                    googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }

                override fun onMarkerDragEnd(p0: Marker) {

                }

                override fun onMarkerDragStart(p0: Marker) {

                }
            });

        })


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
    }

    fun fetchAddress(locText: String, mGoogleApiClient: GoogleApiClient?) {
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

    override fun onPause() {
        super.onPause()
        mapView.onPause()
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
            R.id.save_TV -> {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                if (validate() || isLocationPicked)
                    sendLocationInfoToAddShop()
            }
        }
    }

    private fun validate(): Boolean {
        if (searchLocation_edt.text.toString().trim() == "") {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.blank_location))
            return false
        }
        return true
    }

    private fun sendLocationInfoToAddShop() {
        val mlocationInfoModel = locationInfoModel()
        mlocationInfoModel.address = fullAdd

        if (selectedLatitude.toString() == "")
            mlocationInfoModel.latitude = latitude.toString()
        else
            mlocationInfoModel.latitude = selectedLatitude.toString()

        if (selectedLongitude.toString() == "")
            mlocationInfoModel.longitude = longitude.toString()
        else
            mlocationInfoModel.longitude = selectedLongitude.toString()

        if (!TextUtils.isEmpty(pinCode))
            mlocationInfoModel.pinCode = pinCode

        if (!TextUtils.isEmpty(state))
            mlocationInfoModel.state = state

        if (!TextUtils.isEmpty(city))
            mlocationInfoModel.city = city

        if (!TextUtils.isEmpty(country))
            mlocationInfoModel.country = country

        (mContext as DashboardActivity).getLocationInfoModel(mlocationInfoModel)
        (mContext as DashboardActivity).onBackPressed()
        /*(mContext as DashboardActivity).onBackPressed()
        (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, mlocationInfoModel)*/
    }


    override fun onDestroy() {
        AppUtils.hideSoftKeyboard((mContext as DashboardActivity))
        super.onDestroy()

        mapView.onDestroy()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onDestroyView() {
        super.onDestroyView()
        if (mGoogleApiClient != null) {
            mGoogleApiClient?.stopAutoManage(activity!!)
            mGoogleApiClient?.disconnect()
        }
    }


    override fun onLocationItemClick(description: String, place_id: String) {
        isLocationClicked = true
        isLocationPicked = true
        searchLocation_edt.setText(description)
        fullAdd = description
        rv_address_list.visibility = View.GONE
        placeId = place_id
        Places.GeoDataApi.getPlaceById(mGoogleApiClient!!, place_id).setResultCallback { places ->
            if (places.status.isSuccess) {

                try {

                    selectedLatitude = places.get(0).latLng.latitude
                    selectedLongitude = places.get(0).latLng.longitude


                    markerOptions?.position(places.get(0).latLng);
                    markerOptions?.title(description);
                    //googleMap?.clear();
                    val cameraPosition = CameraPosition.Builder().target(LatLng(selectedLatitude, selectedLongitude)).zoom(15f).build()
                    googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                    if (currentLocationMarker != null)
                        currentLocationMarker?.remove()

                    currentLocationMarker = googleMap?.addMarker(markerOptions!!)!!

                    fetchCurrentAddress(selectedLatitude, selectedLongitude)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            places.release()
        }
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = 1000
        mLocationRequest?.fastestInterval = 1000
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mGoogleApiClient?.isConnected!!) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient!!, mLocationRequest!!, this)

                if (!TextUtils.isEmpty(Pref.latitude) && !TextUtils.isEmpty(Pref.longitude)) {
                    if (Pref.latitude != "0.0" && Pref.longitude != "0.0") {
                        val location = Location("")
                        location.latitude = Pref.latitude!!.toDouble()
                        location.longitude = Pref.longitude!!.toDouble()
                        LoadSaync(location, true)
                    } else
                        getLastKnownLocation()
                } else {
                    getLastKnownLocation()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient!!)

        if (lastLocation != null && lastLocation.latitude != null && lastLocation.latitude != 0.0) {
            LoadSaync(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient!!)!!, true)
        }

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    fun LoadSaync(mLocation: Location, isCameraAnimate: Boolean) {

        try {
            if (isLocationPicked)
                return

            location = mLocation
            latitude = location.latitude
            longitude = location.longitude

            fetchCurrentAddress(latitude, longitude)

            if (selectedLatitude != 0.0 && selectedLongitude != 0.0) {
                if (selectedLatitude != latitude && selectedLongitude != longitude) {

                } else {
                    val markerOptions: MarkerOptions = MarkerOptions();
                    markerOptions.position(LatLng(selectedLatitude, selectedLongitude));
                    markerOptions.title(fullAdd);
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    googleMap?.addMarker(markerOptions);

                    if (isCameraAnimate) {
                        val cameraPosition = CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(15f).build()
                        googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    }

                }
            } else {
                if (isCameraAnimate) {
                    val cameraPosition = CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(15f).build()
                    googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(mContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(mContext as Activity,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(mContext as Activity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            } else {
                ActivityCompat.requestPermissions(mContext as Activity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return false
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    InitiateMap()
                } else {
                    checkLocationPermission()
                }
                return
            }
        }
    }


    private fun fetchCurrentAddress(latitude: Double, longitude: Double): String {
        try {
            geocoder = Geocoder(mContext, Locale.getDefault())
            val addresses: List<android.location.Address> = geocoder?.getFromLocation(latitude, longitude, 1)!!;
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0];
                fullAdd = address.getAddressLine(0);

                if (addresses[0].adminArea != null)
                    state = addresses[0].adminArea

                if (addresses[0].locality != null)
                    city = addresses[0].locality

                if (addresses[0].countryName != null)
                    country = addresses[0].countryName

                if (address.postalCode != null)
                    pinCode = address.postalCode

                selectedLatitude = latitude
                selectedLongitude = longitude

                searchLocation_edt.setText(fullAdd)

            }


        } catch (ex: IOException) {
            ex.printStackTrace();
        }
        return fullAdd;
    }

    override fun onLocationChanged(p0: Location) {
        LoadSaync(location!!, false)
    }

}
package com.kcteam.mappackage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kcteam.R;
import com.kcteam.app.Pref;
import com.kcteam.base.presentation.BaseActivity;
import com.kcteam.features.dashboard.presentation.DashboardActivity;
import com.kcteam.features.location.LocationWizard;
import com.kcteam.widgets.AppCustomTextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kinsuk on 31-10-2017.
 */

public class MapActivityWithoutPath extends BaseActivity implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);

    GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    ArrayList<Marker> markerList = new ArrayList<Marker>();
    SupportMapFragment mapFragment;

    //
//    double currentLat = 22.572646;
//    double currentLong = 88.363895;
    private ImageView iv_back_arrow;
    private AppCustomTextView tv_header;
    private FrameLayout fl_net_status;

    private Geocoder geocoder = null;
    private String shopLat = "0.0", shopLong = "0.0", zipcode, shopName="", address="", orderLat = "0.0", orderLong = "0.0",
                   orderNo = "", orderAddress = "";
    private ImageView iv_home_icon;
    private boolean isCurrentLocShow = false, isOrderLoc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_drawlinr_on_map);

        shopLat = getIntent().getStringExtra("latitude");
        shopLong = getIntent().getStringExtra("longitude");
        shopName = getIntent().getStringExtra("shopname");
        address = getIntent().getStringExtra("address");
        isCurrentLocShow = getIntent().getBooleanExtra("isCurrentLocShow", false);
        isOrderLoc = getIntent().getBooleanExtra("isOrderLoc", false);
        orderLat = getIntent().getStringExtra("orderLat");
        orderLong = getIntent().getStringExtra("orderLong");
        orderNo = getIntent().getStringExtra("orderNo");
        orderAddress = getIntent().getStringExtra("orderAddress");


        initView();

        checkLocationPermission();

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    private void initView() {
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        tv_header = findViewById(R.id.tv_header);
        fl_net_status = findViewById(R.id.fl_net_status);
        fl_net_status.setVisibility(View.GONE);

        iv_back_arrow.setVisibility(View.VISIBLE);

        if (!isOrderLoc) {
            if (!isCurrentLocShow)
                tv_header.setText(getString(R.string.shop_location));
            else {
                double distance = LocationWizard.Companion.getDistance(Double.parseDouble(shopLat), Double.parseDouble(shopLong),
                        Double.parseDouble(Pref.INSTANCE.getCurrent_latitude()), Double.parseDouble(Pref.INSTANCE.getCurrent_longitude()));
                tv_header.setText(Pref.INSTANCE.getShopText() + " Location (" + distance + " KM Approx.)");
                tv_header.setTextSize(15);
            }
        }
        else
            tv_header.setText("Order Location");
        iv_back_arrow.setOnClickListener(this);
        iv_home_icon = findViewById(R.id.iv_home_icon);

        AppCompatImageView logo = findViewById(R.id.logo);
        logo.setVisibility(View.GONE);

        iv_home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivityWithoutPath.this, DashboardActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this tutorial, we add polylines and polygons to represent routes and areas on the map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng latLng = new LatLng(Double.parseDouble(shopLat), Double.parseDouble(shopLong));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(shopName);
        markerOptions.snippet(address);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mGoogleMap.addMarker(markerOptions);

        if (!isOrderLoc) {
            if (!isCurrentLocShow)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            else {
                addMarker(Double.parseDouble(Pref.INSTANCE.getCurrent_latitude()), Double.parseDouble(Pref.INSTANCE.getCurrent_longitude()),
                        "Current Location");

                mGoogleMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(Double.parseDouble(Pref.INSTANCE.getCurrent_latitude()), Double.parseDouble(Pref.INSTANCE.getCurrent_longitude())),
                                latLng)
                        .width(5)
                        .color(Color.RED)
                        .geodesic(true));

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            }
        }
        else {
            LatLng orderLatLng = new LatLng(Double.parseDouble(orderLat), Double.parseDouble(orderLong));
            MarkerOptions orderMarkerOptions = new MarkerOptions();
            orderMarkerOptions.position(orderLatLng);
            orderMarkerOptions.title("Order No.: " + orderNo);
            orderMarkerOptions.snippet(orderAddress);
            orderMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mGoogleMap.addMarker(orderMarkerOptions);


            mGoogleMap.addPolyline(new PolylineOptions()
                    .add(latLng, orderLatLng)
                    .width(5)
                    .color(Color.RED)
                    .geodesic(true));

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(orderLatLng, 13));

            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(MapActivityWithoutPath.this);
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(MapActivityWithoutPath.this);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(MapActivityWithoutPath.this);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
        }
        // Zoom in the Google Map
//        MarkerOptions marker = new MarkerOptions().position(latLng).title("My Location");
//        // adding marker
//        googleMap.addMarker(marker);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    /**
     * Listens for clicks on a polyline.
     *
     * @param polyline The polyline object that the user has clicked.
     */
    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapActivityWithoutPath.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {

            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            if (mGoogleMap != null)
                mGoogleMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            //mCurrLocationMarker.remove();
        }

//        currentLat = location.getLatitude();
//        currentLong =  location.getLongitude();
//
//        //Place current location marker
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));


    }


    private void addMarker(double lat, double lang, String markerName) {
        LatLng latLng = new LatLng(lat, lang);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(markerName);
        markerOptions.snippet(LocationWizard.Companion.getLocationName(this, lat, lang));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)); //BitmapDescriptorFactory.HUE_MAGENTA
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                onBackPressed();
                break;
        }
    }
}

package com.kcteam.mappackage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;


import com.kcteam.R;
import com.kcteam.base.presentation.BaseActivity;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kinsuk on 31-10-2017.
 */

public class MapActivity extends BaseActivity  implements
        OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,View.OnClickListener {


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


    double currentLat = 22.572646;
    double currentLong = 88.363895;

    private ImageView iv_back_arrow;
    private AppCustomTextView tv_header;
    private FrameLayout fl_net_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_drawlinr_on_map);

        checkLocationPermission();

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initView();
    }

    private void initView() {
        iv_back_arrow=findViewById(R.id.iv_back_arrow);
        tv_header=findViewById(R.id.tv_header);
        fl_net_status = findViewById(R.id.fl_net_status);
        fl_net_status.setVisibility(View.GONE);

        iv_back_arrow.setVisibility(View.VISIBLE);
        tv_header.setText(getString(R.string.shop_location));
        iv_back_arrow.setVisibility(View.VISIBLE);
//        iv_back_arrow.setBackground(ContextCompat.getDrawable(this,R.drawable.ic_header_back_arrow));
        iv_back_arrow.setOnClickListener(this);
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

       // addCircleToMap();

        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.

        MarkerOptions markerOptions = new MarkerOptions();


        Polyline polyline2 = googleMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .add(
                        new LatLng(22.572646 - .2, 88.363895),
                        new LatLng(22.572646 -.1, 88.363895 +.1),
                        new LatLng(22.572646 - .16, 88.363895 + .1),
                        new LatLng(22.572646 + .2, 88.363895 + .2),
                        new LatLng(22.572646 + .25, 88.363895-.02)));
        polyline2.setTag("B");
        stylePolyline(polyline2);

        addMarker(22.572646 - .2,88.363895,"Shop1",BitmapDescriptorFactory.HUE_ORANGE);
        addMarker(22.572646 -.1,88.363895 +.1,"Shop2",BitmapDescriptorFactory.HUE_YELLOW);
        addMarker(22.572646 - .2,88.363895 + .1,"Shop3",BitmapDescriptorFactory.HUE_GREEN);
        addMarker(22.572646 + .2,88.363895 + .2,"Shop4",BitmapDescriptorFactory.HUE_GREEN);
        addMarker(22.572646 + .25,88.363895-.02,"Shop5",BitmapDescriptorFactory.HUE_ORANGE);
        addMarker(22.572646 + .3,88.363895 - .3,"Shop6",BitmapDescriptorFactory.HUE_ORANGE);

       // drawCircle(new LatLng(currentLat, currentLong));

        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(currentLat,currentLong))   //set center
                .radius(30000)   //set radius in meters
                .fillColor(0x30ff0000)
                .strokeColor(Color.BLACK)
                .strokeWidth(5);

        googleMap.addCircle(circleOptions);

        // Add polygons to indicate areas on the map.
//        Polygon polygon1 = googleMap.addPolygon(new PolygonOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(-27.457, 153.040),
//                        new LatLng(-33.852, 151.211),
//                        new LatLng(-37.813, 144.962),
//                        new LatLng(-34.928, 138.599)));
        // Store a data object with the polygon, used here to indicate an arbitrary type.
//        polygon1.setTag("alpha");
        // Style the polygon.
      //  stylePolygon(polygon1);



        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
         googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLong), 10));

        // Set listeners for click events.
//        googleMap.setOnPolylineClickListener(this);
//        googleMap.setOnPolygonClickListener(this);
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
     * Styles the polyline, based on type.
     * @param polyline The polyline object that needs styling.
     */
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                // Use a custom bitmap as the cap at the start of the line.
//                polyline.setStartCap(
//                        new CustomCap(
//                                BitmapDescriptorFactory.fromResource(R.drawable.ic_arrow), 20));
                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_GREEN_ARGB);
        polyline.setJointType(JointType.ROUND);
    }

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     */
    private void stylePolygon(Polygon polygon) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor = COLOR_WHITE_ARGB;

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "alpha":
                // Apply a stroke pattern to render a dashed line, and define colors.
                pattern = PATTERN_POLYGON_ALPHA;
                strokeColor = COLOR_GREEN_ARGB;
                fillColor = COLOR_PURPLE_ARGB;
                break;
            case "beta":
                // Apply a stroke pattern to render a line of dots and dashes, and define colors.
                pattern = PATTERN_POLYGON_BETA;
                strokeColor = COLOR_ORANGE_ARGB;
                fillColor = COLOR_BLUE_ARGB;
                break;
        }

        polygon.setStrokePattern(pattern);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    /**
     * Listens for clicks on a polyline.
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

//    /**
//     * Listens for clicks on a polygon.
//     * @param polygon The polygon object that the user has clicked.
//     */
//    @Override
//    public void onPolygonClick(Polygon polygon) {
//        // Flip the values of the red, green, and blue components of the polygon's color.
//        int color = polygon.getStrokeColor() ^ 0x00ffffff;
//        polygon.setStrokeColor(color);
//        color = polygon.getFillColor() ^ 0x00ffffff;
//        polygon.setFillColor(color);
//
//        Toast.makeText(this, "Area type " + polygon.getTag().toString(), Toast.LENGTH_SHORT).show();
//    }





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
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }else{

            if (mGoogleApiClient == null) {
                buildGoogleApiClient();
            }
            if(mGoogleMap!=null)
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
            mCurrLocationMarker.remove();
        }

        currentLat = location.getLatitude();
        currentLong =  location.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
       // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
    }


    private void addMarker(double lat,double lang,String markerName, float markerColor){
        LatLng latLng = new LatLng(lat, lang);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(markerName);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerColor)); //BitmapDescriptorFactory.HUE_MAGENTA
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
    }

//    private void drawCircle(LatLng point){
//
//        // Instantiating CircleOptions to draw a circle around the marker
//        CircleOptions circleOptions = new CircleOptions();
//
//        // Specifying the center of the circle
//        circleOptions.center(point);
//
//        // Radius of the circle
//        circleOptions.radius(20);
//
//        // Border color of the circle
//        circleOptions.strokeColor(Color.BLACK);
//
//        // Fill color of the circle
//        circleOptions.fillColor(0x30ff0000);
//
//        // Border width of the circle
//        circleOptions.strokeWidth(2);
//
//        // Adding the circle to the GoogleMap
//        mGoogleMap.addCircle(circleOptions);
//
//    }


    private void addCircleToMap() {

        // circle settings
        int radiusM = 20;
        double latitude = currentLat;// your center latitude
        double longitude = currentLong;// your center longitude
                LatLng latLng = new LatLng(latitude,longitude);

        // draw circle
        int d = 500; // diameter
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.delivery_status_green));
        c.drawCircle(d/2, d/2, d/2, p);

        // generate BitmapDescriptor from circle Bitmap
        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

// mapView is the GoogleMap
        mGoogleMap.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(latLng,radiusM*2,radiusM*2).
                transparency(0.4f));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back_arrow:
                onBackPressed();
            break;
        }
    }
}

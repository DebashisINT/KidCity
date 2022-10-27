package com.kcteam.features.member

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamLocListResponseModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class MapViewForTeamFrag : BaseFragment(), View.OnClickListener , OnMapReadyCallback{

    private lateinit var mContext: Context
    var mapFragment: SupportMapFragment? = null
    private var mapCustomer: GoogleMap? = null
    var marksAddr: ArrayList<LatLng?> = ArrayList()

    private lateinit var progress_wheel: ProgressWheel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var timer : Timer? = null
        lateinit var userId: String
        lateinit var locDate: String
        fun newInstance(obj: Any): MapViewForTeamFrag {
            val fragment = MapViewForTeamFrag()
            if (obj != null) {
               var data=obj as String
                userId=data.split("~").get(0)
                locDate=data.split("~").get(1)
            }
            return fragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_map_view_team, container, false)
        initView(view)

        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initView(view: View?) {
        progress_wheel = view!!.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        mapFragment = childFragmentManager.findFragmentById(R.id.show_map_frag) as SupportMapFragment



        timer = Timer()
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                callFetchLocationApi()
            }
        }
        timer!!.schedule(task, 0, 60000)

        //callFetchLocationApi()

    }

    private fun showMap() {
        mapFragment!!.getMapAsync(this)
    }


    override fun onClick(v: View?) {

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapCustomer = googleMap

        var i: Int = 0

        for(i in 0..marksAddr.size-1){
            if(i==0){
                val mark = marksAddr[i]
                mapCustomer!!.addMarker(MarkerOptions()
                        .position(mark!!)
                        .icon(bitmapDescriptorFromVector(mContext, R.drawable.ic_flag_green))
                    .title("Marker"))
                mapCustomer!!.moveCamera(CameraUpdateFactory.newLatLng(mark))
                mapCustomer!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
            }else if (i==marksAddr.size-1){
                val mark = marksAddr[i]
                mapCustomer!!.addMarker(MarkerOptions()
                    .position(mark!!)
                    .icon(bitmapDescriptorFromVector(mContext, R.drawable.your_location))
                    .title("Marker"))
                mapCustomer!!.moveCamera(CameraUpdateFactory.newLatLng(mark))
                mapCustomer!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
            }else{
                val mark = marksAddr[i]
                mapCustomer!!.addMarker(MarkerOptions()
                    .position(mark!!)
                    .icon(bitmapDescriptorFromVector(mContext, R.drawable.your_location))
                    .title("Marker"))
                mapCustomer!!.moveCamera(CameraUpdateFactory.newLatLng(mark))
                mapCustomer!!.animateCamera(CameraUpdateFactory.zoomTo(15f))
            }
        }

        val mark = marksAddr[marksAddr.size-1]
        mapCustomer!!.addMarker(MarkerOptions()
            .position(mark!!)
            .icon(bitmapDescriptorFromVector(mContext, R.drawable.scooter_man))
            .title("Marker"))
        mapCustomer!!.moveCamera(CameraUpdateFactory.newLatLngZoom(marksAddr[marksAddr.size-1]!!, 15f))

        val opts = PolylineOptions().addAll(marksAddr).color(Color.BLUE).width(5f)
        mapCustomer!!.addPolyline(opts)
        mapCustomer!!.uiSettings.isZoomControlsEnabled = true

    }


    private fun bitmapDescriptorFromVector(context: Context, vectorResID: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResID)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callFetchLocationApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        val repository = TeamRepoProvider.teamRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.teamLocList(userId, locDate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val list = result as TeamLocListResponseModel
                    when (list.status) {
                        "200" -> {
                            marksAddr= ArrayList()
                            for(i in 0..list.location_details!!.size-1){
                                marksAddr.add(LatLng(list.location_details!!.get(i).latitude.toDouble(),list.location_details!!.get(i).longitude.toDouble()))
                            }
                            showMap()
                            progress_wheel.stopSpinning()
                        }
                        else -> {
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(list.message!!)
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                    progress_wheel.stopSpinning()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                })
        )
    }

    fun refreshMap(){
        progress_wheel.spin()
        Thread.sleep(500)
        progress_wheel.stopSpinning()
        callFetchLocationApi()
    }

}
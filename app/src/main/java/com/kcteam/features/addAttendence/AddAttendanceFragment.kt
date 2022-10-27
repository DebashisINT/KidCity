package com.kcteam.features.addAttendence

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.*
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.*
import com.kcteam.Customdialog.CustomDialog
import com.kcteam.Customdialog.OnDialogCustomClickListener
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.Pref.willShowUpdateDayPlan
import com.kcteam.app.domain.*
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.*
import com.kcteam.app.utils.AppUtils.Companion.hiFirstNameText
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.faceRec.DetectorActivity
import com.kcteam.faceRec.FaceStartActivity
import com.kcteam.faceRec.FaceStartActivity.detector
import com.kcteam.faceRec.tflite.SimilarityClassifier.Recognition
import com.kcteam.faceRec.tflite.TFLiteObjectDetectionAPIModel
import com.kcteam.features.addAttendence.api.WorkTypeListRepoProvider
import com.kcteam.features.addAttendence.api.addattendenceapi.AddAttendenceRepoProvider
import com.kcteam.features.addAttendence.api.leavetytpeapi.LeaveTypeRepoProvider
import com.kcteam.features.addAttendence.api.routeapi.RouteRepoProvider
import com.kcteam.features.addAttendence.model.*
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.BeatListResponseModel
import com.kcteam.features.commondialogsinglebtn.CommonDialogSingleBtn
import com.kcteam.features.commondialogsinglebtn.OnDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.geofence.GeofenceService
import com.kcteam.features.location.LocationFuzedService
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.SingleShotLocationProvider
import com.kcteam.features.login.UserLoginDataEntity
import com.kcteam.features.login.model.LoginStateListDataModel
import com.kcteam.features.newcollectionreport.PendingCollData
import com.kcteam.features.photoReg.api.GetUserListPhotoRegProvider
import com.kcteam.features.photoReg.model.UserFacePicUrlResponse
import com.kcteam.features.survey.SurveyFromListDialog
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.itextpdf.text.pdf.PdfName.VIEW
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 29-08-2018.
 */
class AddAttendanceFragment : Fragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener, OnMapReadyCallback {

    private lateinit var mContext: Context
    private lateinit var tv_attendance_submit: AppCustomTextView
    private lateinit var ll_work_type_list: LinearLayout
    private lateinit var rv_work_type_list: RecyclerView
    private lateinit var iv_work_type_dropdown: ImageView
    private lateinit var ll_add_attendance_main: LinearLayout
    private lateinit var iv_attendance_check: ImageView
    private lateinit var iv_leave_check: ImageView
    private lateinit var tv_work_type: AppCustomTextView
    private lateinit var rl_work_type_header: RelativeLayout
    private lateinit var ll_on_leave: LinearLayout
    private lateinit var ll_at_work: LinearLayout
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var tv_current_address: AppCustomTextView
    private lateinit var tv_current_date_time: AppCustomTextView
    private lateinit var ll_add_attendance_leave_type: LinearLayout
    private lateinit var tv_show_date_range: AppCustomTextView
    private lateinit var rl_leave_type_header: RelativeLayout
    private lateinit var tv_leave_type: AppCustomTextView
    private lateinit var iv_leave_type_dropdown: ImageView
    private lateinit var ll_leave_type_list: LinearLayout
    private lateinit var rv_leave_type_list: RecyclerView
    private lateinit var rl_route_header: RelativeLayout
    private lateinit var tv_route_type: AppCustomTextView
    private lateinit var iv_route_dropdown: ImageView
    private lateinit var ll_route_list: LinearLayout
    private lateinit var rv_route_list: RecyclerView
    private lateinit var cv_route: CardView
    private lateinit var fab_add_work_type: FloatingActionButton
    private lateinit var et_leave_reason_text: AppCustomEditText
    private lateinit var et_order_value: AppCustomEditText
    private lateinit var et_collection_value: AppCustomEditText
    private lateinit var et_shop_visit: AppCustomEditText
    private lateinit var et_shop_revisit: AppCustomEditText
    private lateinit var cv_dd_field: CardView
    private lateinit var et_dd_name: AppCustomEditText
    private lateinit var et_market_worked: AppCustomEditText
    private lateinit var cv_todays_target: CardView
    private lateinit var mapFragment: SupportMapFragment
    private var mGoogleMap: GoogleMap? = null
    private lateinit var tv_address: AppCustomTextView
    private lateinit var et_work_type_text: AppCustomEditText
    private lateinit var tv_approved_in_time: AppCustomTextView
    private lateinit var cv_visit_plan: CardView
    private lateinit var et_from_loc: AppCustomEditText
    private lateinit var et_to_loc: AppCustomEditText
    private lateinit var cv_distance: CardView
    private lateinit var cv_beat: CardView
    private lateinit var et_distance: AppCustomEditText
    private lateinit var tv_beat_type: AppCustomTextView
    private lateinit var tv_dd: AppCustomTextView
    private lateinit var cv_dd: CardView

    private var isOnLeave = false
    private var workTypeId = ""

    private var workTypeModel: WorkTypeListData? = null

    private var startDate = ""
    private var endDate = ""
    private var leaveId = ""
    private var mbeatId = ""

    private lateinit var ll_target_value: LinearLayout
    private lateinit var rv_primary_value_list: RecyclerView

    private var stateList: ArrayList<LoginStateListDataModel>? = null
    private var fingerprintDialog: FingerprintDialog? = null
    private var selfieDialog: SelfieDialog? = null
    private var loc_list: ArrayList<LocationDataModel>? = null
    private var location: LocationDataModel?= null
    private var fromID = ""
    private var toID = ""
    private var fromLat = ""
    private var fromLong = ""
    private var toLat = ""
    private var toLong = ""
    private var assignedToDDId = ""

    private val addAttendenceModel: AddAttendenceInpuModel by lazy {
        AddAttendenceInpuModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_attendence, container, false)
        initView(view)
        initClickListener()

        /*try {
            if (AppDatabase.getDBInstance()?.workTypeDao()?.getAll()!!.isEmpty())
                getWorkTypeListApi()
            else {
                Log.e("add attendance", "database work type")
                val list = (AppDatabase.getDBInstance()?.workTypeDao()?.getAll() as ArrayList<WorkTypeEntity>?)!!

                for (i in list.indices) {
                    AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(false, list[i].ID)
                }

                setAdapter(list)
                checkForLeaveTypeData()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }*/

        locationList()
        //getWorkTypeListApi()

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(view: View) {
        tv_attendance_submit = view.findViewById(R.id.tv_attendance_submit)
        ll_work_type_list = view.findViewById(R.id.ll_work_type_list)
        rv_work_type_list = view.findViewById(R.id.rv_work_type_list)
        iv_work_type_dropdown = view.findViewById(R.id.iv_work_type_dropdown)
        ll_add_attendance_main = view.findViewById(R.id.ll_add_attendance_main)
        ll_add_attendance_main.isEnabled = false
        iv_attendance_check = view.findViewById(R.id.iv_attendance_check)
        iv_leave_check = view.findViewById(R.id.iv_leave_check)
        tv_work_type = view.findViewById(R.id.tv_work_type)
        rl_work_type_header = view.findViewById(R.id.rl_work_type_header)
        ll_on_leave = view.findViewById(R.id.ll_on_leave)
        ll_at_work = view.findViewById(R.id.ll_at_work)
        tv_current_date_time = view.findViewById(R.id.tv_current_date_time)
        tv_current_address = view.findViewById(R.id.tv_current_address)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        ll_add_attendance_leave_type = view.findViewById(R.id.ll_add_attendance_leave_type)
        tv_show_date_range = view.findViewById(R.id.tv_show_date_range)
        rl_leave_type_header = view.findViewById(R.id.rl_leave_type_header)
        tv_leave_type = view.findViewById(R.id.tv_leave_type)
        iv_leave_type_dropdown = view.findViewById(R.id.iv_leave_type_dropdown)
        ll_leave_type_list = view.findViewById(R.id.ll_leave_type_list)
        rv_leave_type_list = view.findViewById(R.id.rv_leave_type_list)
        rl_route_header = view.findViewById(R.id.rl_route_header)
        tv_route_type = view.findViewById(R.id.tv_route_type)
        iv_route_dropdown = view.findViewById(R.id.iv_route_dropdown)
        ll_route_list = view.findViewById(R.id.ll_route_list)
        rv_route_list = view.findViewById(R.id.rv_route_list)
        cv_route = view.findViewById(R.id.cv_route)
        fab_add_work_type = view.findViewById(R.id.fab_add_work_type)
        et_leave_reason_text = view.findViewById(R.id.et_leave_reason_text)
        et_order_value = view.findViewById(R.id.et_order_value)
        et_collection_value = view.findViewById(R.id.et_collection_value)
        et_shop_visit = view.findViewById(R.id.et_shop_visit)
        et_shop_revisit = view.findViewById(R.id.et_shop_revisit)
        ll_target_value = view.findViewById(R.id.ll_target_value)
        cv_dd_field = view.findViewById(R.id.cv_dd_field)
        et_dd_name = view.findViewById(R.id.et_dd_name)
        et_market_worked = view.findViewById(R.id.et_market_worked)
        rv_primary_value_list = view.findViewById(R.id.rv_primary_value_list)
        rv_primary_value_list.layoutManager = LinearLayoutManager(mContext)
        cv_todays_target = view.findViewById(R.id.cv_todays_target)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        tv_address = view.findViewById(R.id.tv_address)
        et_work_type_text = view.findViewById(R.id.et_work_type_text)
        tv_approved_in_time = view.findViewById(R.id.tv_approved_in_time)
        cv_visit_plan = view.findViewById(R.id.cv_visit_plan)
        et_from_loc = view.findViewById(R.id.et_from_loc)
        et_to_loc = view.findViewById(R.id.et_to_loc)
        cv_distance = view.findViewById(R.id.cv_distance)
        et_distance = view.findViewById(R.id.et_distance)
        cv_beat = view.findViewById(R.id.cv_beat_type_root)
        tv_beat_type= view.findViewById(R.id.tv_beat_type)
        tv_dd = view.findViewById(R.id.tv_dd)
        cv_dd = view.findViewById(R.id.cv_dd_root)


        tv_beat_type.hint = "Select " + "${Pref.beatText}" + " Type"
        tv_dd.hint = "Select Distributor"

        if(Pref.IsBeatRouteAvailableinAttendance)
        {
            cv_beat.visibility=View.VISIBLE
        }else{
            cv_beat.visibility=View.GONE
        }
        if(Pref.IsDistributorSelectionRequiredinAttendance)
        {
            cv_dd.visibility=View.VISIBLE
        }else{
            cv_dd.visibility=View.GONE
        }

        if (Pref.isVisitPlanShow)
            cv_visit_plan.visibility = View.VISIBLE
        else
            cv_visit_plan.visibility = View.GONE

        if (Pref.willSetYourTodaysTargetVisible)
            cv_todays_target.visibility = View.VISIBLE
        else
            cv_todays_target.visibility = View.GONE

        stateList = AppUtils.loadSharedPreferencesStateList(mContext)

        if (stateList != null && stateList?.size!! > 0) {
            ll_target_value.visibility = View.GONE
            rv_primary_value_list.visibility = View.VISIBLE

            rv_primary_value_list.adapter = PrimaryValueAdapter(mContext, stateList!!)
        } else {
            ll_target_value.visibility = View.VISIBLE
            rv_primary_value_list.visibility = View.GONE
        }


        if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude))
            tv_current_address.text = LocationWizard.getLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
        tv_current_date_time.text = AppUtils.getCurrentDateTime12Format()
        tv_approved_in_time.text = Pref.attendance_text


        et_work_type_text.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }

            false
        })

        et_leave_reason_text.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }

            false
        })

        //setLeaveTypeAdapter(AppDatabase.getDBInstance()?.leaveTypeDao()?.getAll())
        //setRouteAdapter()

        if(Pref.IsShowLeaveInAttendance){
            if(Pref.OnLeaveForTodayStatus.equals("REJECT")){
                ll_on_leave.visibility=View.GONE
            }else{
                ll_on_leave.visibility=View.VISIBLE
            }
        }else{
            ll_on_leave.visibility=View.GONE
        }


        faceDetectorSetUp()

        if(Pref.BatterySettingGlobal && Pref.BatterySetting ){
            if(AppUtils.getBatteryPercentage(mContext).toInt()<=15){
                CustomDialog.getInstance(hiFirstNameText(),getString(R.string.battery_setting_message),"OK","", "0",object : OnDialogCustomClickListener {
                    override fun onOkClick() {
                        //Toaster.msgShort(mContext, "OK")
                    }
                    override fun onYesClick() {

                    }
                    override fun onNoClick() {
                    }
                }).show((mContext as DashboardActivity).supportFragmentManager, "CustomDialog")
            }
        }



    }

    override fun onResume() {
        super.onResume()

       // faceDetectorSetUp()
    }

  /*  override fun onMapReady(googleMap: GoogleMap?) {
        mGoogleMap = googleMap
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = true

        if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude)) {
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Pref.current_latitude.toDouble(),
                    Pref.current_longitude.toDouble()), 15f))

            val latLng = LatLng(Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
            val markerOptions = MarkerOptions()

            markerOptions.also {
                it.position(latLng)
                *//*it.title(locationName)
                it.snippet(locationName)*//*
                it.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                mGoogleMap?.addMarker(it)!!
            }

            tv_address.text = LocationWizard.getLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
        }
    }*/


    private var selectedRoute = ArrayList<RouteEntity>()
    private var routeID = ""

    @SuppressLint("WrongConstant")
    private fun setRouteAdapter(arrayList: ArrayList<RouteEntity>, selectionStatus: Int, route_id: String?) {
        /*val routeList = ArrayList<String>()
        for (i in 1..10) {
            routeList.add("Route" + i)
        }*/

        rv_route_list.layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        rv_route_list.adapter = RouteAdapter(mContext, arrayList, selectionStatus, route_id!!, object : RouteAdapter.OnRouteClickListener {
            override fun unCheckRoute(route: RouteEntity?, adapterPosition: Int) {

                checkIfRouteSelected(route!!, adapterPosition, arrayList, true)

                if (selectedRoute.size > 0) {

                    for (i in selectedRoute.indices) {
                        if (i == 0) {
                            //workTypeId = selectedRoute[i].ID
                            tv_route_type.text = selectedRoute[i].route_name
                            routeID = selectedRoute[i].route_id!!

                        } else {
                            //workTypeId = workTypeId + ", " + selectedRoute[i].ID
                            routeID = routeID + "," + selectedRoute[i].route_id!!
                            tv_route_type.text = tv_route_type.text.toString().trim() + ", " + selectedRoute[i].route_name
                        }
                    }
                } else {
                    //workTypeId = ""
                    routeID = ""
                    tv_route_type.text = ""

                }
            }

            override fun onRouteCheckClick(route: RouteEntity?, adapterPosition: Int, isCheckBoxClicked: Boolean) {

                checkIfRouteSelected(route!!, adapterPosition, arrayList, isCheckBoxClicked)

                if (selectedRoute.size > 0) {

                    for (i in selectedRoute.indices) {
                        if (i == 0) {
                            //workTypeId = selectedRoute[i].ID
                            tv_route_type.text = selectedRoute[i].route_name
                            routeID = selectedRoute[i].route_id!!

                        } else {
                            //workTypeId = workTypeId + ", " + selectedRoute[i].ID
                            routeID = routeID + "," + selectedRoute[i].route_id!!
                            tv_route_type.text = tv_route_type.text.toString().trim() + ", " + selectedRoute[i].route_name
                        }
                    }
                } else {
                    //workTypeId = ""
                    routeID = ""
                    tv_route_type.text = ""

                }

                //tv_route_type.text = route
            }

            override fun onRouteTextClick(route: RouteEntity?, adapterPosition: Int, selected: Boolean) {
                val list = AppDatabase.getDBInstance()?.routeShopListDao()?.getDataRouteIdWise(route?.route_id!!) as ArrayList<RouteShopListEntity>

                if (list == null || list.size == 0)
                    return

                showRouteShopList(route?.route_id!!, selected)
            }
        })
    }

    private fun showRouteShopList(route_id: String, selected: Boolean) {
        try {
            RouteShopListDialog.getInstance(route_id, selected, object : RouteShopListDialog.RouteShopClickLisneter {
                override fun onCheckClick(leaveTypeList: RouteShopListEntity?) {

                    val list = AppDatabase.getDBInstance()?.routeShopListDao()?.getDataRouteIdWise(leaveTypeList?.route_id!!)

                    val selectedList = AppDatabase.getDBInstance()?.routeShopListDao()?.getSelectedDataRouteIdWise(false, leaveTypeList?.route_id!!)

                    if (list?.size == selectedList?.size) {
                        AppDatabase.getDBInstance()?.routeDao()?.updateIsSelectedAccordingToRouteId(false, leaveTypeList?.route_id!!)
                        val list_ = AppDatabase.getDBInstance()?.routeDao()?.getAll() as ArrayList<RouteEntity>
                        setRouteAdapter(list_, 0, leaveTypeList?.route_id)
                        return
                    }
                    for (i in list?.indices!!) {
                        if (list[i].isSelected) {
                            AppDatabase.getDBInstance()?.routeDao()?.updateIsSelectedAccordingToRouteId(true, leaveTypeList?.route_id!!)
                            val list_ = AppDatabase.getDBInstance()?.routeDao()?.getAll() as ArrayList<RouteEntity>
                            setRouteAdapter(list_, 1, leaveTypeList?.route_id)
                            return
                        }
                    }

                }
            }).show((mContext as DashboardActivity).supportFragmentManager, "")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkIfRouteSelected(route: RouteEntity, adapterPosition: Int, list: ArrayList<RouteEntity>, checkBoxClicked: Boolean) {
        try {
            if (selectedRoute.size > 0) {

                for (i in selectedRoute.indices) {
                    if (selectedRoute[i] == route) {

                        AppDatabase.getDBInstance()?.routeDao()?.updateIsSelectedAccordingToRouteId(false, list[adapterPosition].route_id!!)
                        selectedRoute.remove(list[adapterPosition])
                        return

                    } else if (selectedRoute[i].route_id == route.route_id) {
                        if (!checkBoxClicked)
                            return
                        else {
                            //selectedRoute.removeAt(adapterPosition)
                            AppDatabase.getDBInstance()?.routeDao()?.updateIsSelectedAccordingToRouteId(false, route.route_id!!)
                            selectedRoute.removeAt(i)
                            return
                        }
                    }
                }

                selectedRoute.add(list[adapterPosition])
                AppDatabase.getDBInstance()?.routeDao()?.updateIsSelectedAccordingToRouteId(true, list[adapterPosition].route_id!!)
                val list_ = AppDatabase.getDBInstance()?.routeShopListDao()?.getDataRouteIdWise(route.route_id!!) as ArrayList<RouteShopListEntity>

                if (list_ == null || list_.size == 0)
                    return

                if (checkBoxClicked)
                    showRouteShopList(list[adapterPosition].route_id!!, true)
            } else {
                selectedRoute.add(list[adapterPosition])
                AppDatabase.getDBInstance()?.routeDao()?.updateIsSelectedAccordingToRouteId(true, list[adapterPosition].route_id!!)
                val list_ = AppDatabase.getDBInstance()?.routeShopListDao()?.getDataRouteIdWise(route.route_id!!) as ArrayList<RouteShopListEntity>

                if (list_ == null || list_.size == 0)
                    return

                if (checkBoxClicked)
                    showRouteShopList(list[adapterPosition].route_id!!, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("WrongConstant")
    private fun setLeaveTypeAdapter(leaveTypeList: ArrayList<LeaveTypeEntity>?) {
        rv_leave_type_list.layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        rv_leave_type_list.adapter = LeaveTypeListAdapter(mContext, leaveTypeList!!, object : LeaveTypeListAdapter.OnLeaveTypeClickListener {
            override fun onLeaveTypeClick(leaveType: LeaveTypeEntity?, adapterPosition: Int) {
                tv_leave_type.text = leaveType?.leave_type
                (mContext as DashboardActivity).leaveType = leaveType?.leave_type!!
                leaveId = leaveType.id.toString()
                ll_leave_type_list.visibility = View.GONE
                iv_leave_type_dropdown.isSelected = false
            }
        })
    }

    private fun initClickListener() {
        tv_attendance_submit.setOnClickListener(this)
        ll_on_leave.setOnClickListener(this)
        ll_at_work.setOnClickListener(this)
        rl_work_type_header.setOnClickListener(this)
        rl_leave_type_header.setOnClickListener(this)
        rl_route_header.setOnClickListener(this)
        tv_address.setOnClickListener(null)
        tv_show_date_range.setOnClickListener(this)
        et_from_loc.setOnClickListener(this)
        et_to_loc.setOnClickListener(this)
        cv_beat.setOnClickListener(this)
        cv_dd.setOnClickListener(this)
    }

    private fun locationList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = RouteRepoProvider.routeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getLocList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as LocationListResponseModel
                            if (response.status == NetworkConstant.SUCCESS)
                                loc_list = response.loc_list

                            progress_wheel.stopSpinning()
                            getWorkTypeListApi()

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            //(mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))

                            getWorkTypeListApi()
                        })
        )
    }

    private fun getWorkTypeListApi() {

        /*val workTypeList = ArrayList<WorkTypeListData>()

        workTypeModel = WorkTypeListData()
        workTypeModel?.ID = "1"
        workTypeModel?.Descrpton = "Field Work (Self, ASM, ZSM, SH)"
        workTypeList.add(workTypeModel!!)

        workTypeModel = WorkTypeListData()
        workTypeModel?.ID = "2"
        workTypeModel?.Descrpton = "Meeting"
        workTypeList.add(workTypeModel!!)

        workTypeModel = WorkTypeListData()
        workTypeModel?.ID = "3"
        workTypeModel?.Descrpton = "DD / PP Visit"
        workTypeList.add(workTypeModel!!)


        doAsync {

            for (i in 0 until (workTypeList.size ?: 0)) {
                val workType = WorkTypeEntity()
                workType.ID = workTypeList[i].ID.toInt()
                workType.Descrpton = workTypeList[i].Descrpton
                AppDatabase.getDBInstance()?.workTypeDao()?.insertAll(workType)
            }

            uiThread {
                setAdapter((AppDatabase.getDBInstance()?.workTypeDao()?.getAll() as ArrayList<WorkTypeListData>?)!!)
                checkForLeaveTypeData()
            }
        }*/

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = WorkTypeListRepoProvider.workTypeListRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getWorkTypeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as WorkTypeResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.worktype_list

                                val workList = AppDatabase.getDBInstance()?.workTypeDao()?.getAll()
                                if (workList != null && workList.isNotEmpty())
                                    AppDatabase.getDBInstance()?.workTypeDao()?.delete()

                                for (i in 0 until (list?.size ?: 0)) {
                                    val workType = WorkTypeEntity()
                                    workType.ID = list!![i].ID.toInt()
                                    workType.Descrpton = list[i].Descrpton
                                    AppDatabase.getDBInstance()?.workTypeDao()?.insertAll(workType)
                                }
                                Log.e("add attendance", "api work type")
                                setAdapter(AppDatabase.getDBInstance()?.workTypeDao()?.getAll() as ArrayList<WorkTypeEntity>)
                                checkForLeaveTypeData()
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }

    private fun checkForLeaveTypeData() {
        AppDatabase.getDBInstance()?.leaveTypeDao()?.delete()
        if (AppDatabase.getDBInstance()?.leaveTypeDao()?.getAll()!!.isEmpty())
            getLeaveTypeList()
        else {
            setLeaveTypeAdapter(AppDatabase.getDBInstance()?.leaveTypeDao()?.getAll() as ArrayList<LeaveTypeEntity>)

            //if (AppDatabase.getDBInstance()?.routeDao()?.getAll()!!.isEmpty())
            getRouteList()
            /*else {
                val list = AppDatabase.getDBInstance()?.routeShopListDao()?.getAll()
                for (i in list?.indices!!)
                    AppDatabase.getDBInstance()?.routeShopListDao()?.updateIsUploadedAccordingToRouteId(false, list[i].route_id!!)

                val list_ = AppDatabase.getDBInstance()?.routeDao()?.getAll()
                for (i in list_?.indices!!)
                    AppDatabase.getDBInstance()?.routeDao()?.updateIsSelectedAccordingToRouteId(false, list_[i].route_id!!)

                setRouteAdapter(AppDatabase.getDBInstance()?.routeDao()?.getAll() as ArrayList<RouteEntity>, -1, "")
            }*/
        }
    }

    private fun getRouteList() {
        /*val routeList = ArrayList<RouteEntity>()

        for (i in 1..10) {
            val routeEntity = RouteEntity()
            routeEntity.id = i
            routeEntity.route_name = "Route " + i
            routeList.add(routeEntity)
        }*/

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val list_ = AppDatabase.getDBInstance()?.routeDao()?.getAll()
        val routeShopList = AppDatabase.getDBInstance()?.routeShopListDao()?.getAll()

        val repository = RouteRepoProvider.routeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getRouteList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as RouteResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.route_list
                                /*for (i in 0 until (list?.size ?: 0)) {
                                    val workType = WorkTypeEntity()
                                    workType.ID = list!![i].ID.toInt()
                                    workType.Descrpton = list[i].Descrpton
                                    AppDatabase.getDBInstance()?.workTypeDao()?.insertAll(workType)
                                }*/
                                if (list != null && list.size > 0) {
                                    doAsync {

                                        if (list_ != null) {
                                            AppDatabase.getDBInstance()?.routeDao()?.deleteRoute()

                                            if (routeShopList != null)
                                                AppDatabase.getDBInstance()?.routeShopListDao()?.deleteData()
                                        }

                                        for (i in list.indices) {

                                            val route = RouteEntity()
                                            route.route_id = list[i].id
                                            route.route_name = list[i].route_name

                                            if (list[i].shop_details_list != null) {
                                                for (j in list[i].shop_details_list?.indices!!) {
                                                    val routeShopList = RouteShopListEntity()
                                                    routeShopList.route_id = list[i].id
                                                    routeShopList.shop_id = list[i].shop_details_list?.get(j)?.shop_id
                                                    routeShopList.shop_address = list[i].shop_details_list?.get(j)?.shop_address
                                                    routeShopList.shop_name = list[i].shop_details_list?.get(j)?.shop_name
                                                    routeShopList.shop_contact_no = list[i].shop_details_list?.get(j)?.shop_contact_no
                                                    AppDatabase.getDBInstance()?.routeShopListDao()?.insert(routeShopList)
                                                }
                                            }

                                            AppDatabase.getDBInstance()?.routeDao()?.insert(route)
                                        }

                                        uiThread {
                                            setRouteAdapter(AppDatabase.getDBInstance()?.routeDao()?.getAll() as ArrayList<RouteEntity>, -1, "")
                                            progress_wheel.stopSpinning()
                                        }
                                    }
                                } else
                                    progress_wheel.stopSpinning()
                            } else {
                                progress_wheel.stopSpinning()

                                if (list_ != null && list_.isNotEmpty()) {
                                    setRouteAdapter(AppDatabase.getDBInstance()?.routeDao()?.getAll() as ArrayList<RouteEntity>, -1, "")
                                }
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")

                            if (list_ != null && list_.isNotEmpty()) {
                                setRouteAdapter(AppDatabase.getDBInstance()?.routeDao()?.getAll() as ArrayList<RouteEntity>, -1, "")
                            }
                        })
        )


        /*doAsync {

            for (i in 0 until (routeList.size ?: 0)) {
                AppDatabase.getDBInstance()?.routeDao()?.insert(routeList[i])
            }

            uiThread {
                setRouteAdapter(AppDatabase.getDBInstance()?.routeDao()?.getAll() as ArrayList<RouteEntity>)
            }
        }*/
    }

    var leaveEntity = LeaveTypeEntity()
    private fun getLeaveTypeList() {
        /*val leaveTypeList = ArrayList<LeaveTypeEntity>()

        leaveEntity = LeaveTypeEntity()
        leaveEntity.id = 1
        leaveEntity.leave_type = "Casual Leave"
        leaveTypeList.add(leaveEntity)

        leaveEntity = LeaveTypeEntity()
        leaveEntity.id = 2
        leaveEntity.leave_type = "Planned Leave"
        leaveTypeList.add(leaveEntity)

        leaveEntity = LeaveTypeEntity()
        leaveEntity.id = 3
        leaveEntity.leave_type = "Sick Leave"
        leaveTypeList.add(leaveEntity)

        leaveEntity = LeaveTypeEntity()
        leaveEntity.id = 4
        leaveEntity.leave_type = "Maternity Leave"
        leaveTypeList.add(leaveEntity)

        leaveEntity = LeaveTypeEntity()
        leaveEntity.id = 5
        leaveEntity.leave_type = "Paternity Leave"
        leaveTypeList.add(leaveEntity)

        leaveEntity = LeaveTypeEntity()
        leaveEntity.id = 6
        leaveEntity.leave_type = "Half Day Leave"
        leaveTypeList.add(leaveEntity)

        leaveEntity = LeaveTypeEntity()
        leaveEntity.id = 7
        leaveEntity.leave_type = "Marriage Leave"
        leaveTypeList.add(leaveEntity)*/

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = LeaveTypeRepoProvider.leaveTypeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getLeaveTypeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as LeaveTypeResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.leave_type_list

                                if (list != null && list.size > 0) {
                                    doAsync {

                                        for (i in list.indices) {

                                            val leave = LeaveTypeEntity()
                                            leave.id = list[i].id?.toInt()!!
                                            leave.leave_type = list[i].type_name
                                            AppDatabase.getDBInstance()?.leaveTypeDao()?.insert(leave)
                                        }

                                        uiThread {
                                            setLeaveTypeAdapter(AppDatabase.getDBInstance()?.leaveTypeDao()?.getAll() as ArrayList<LeaveTypeEntity>)

                                            if (AppDatabase.getDBInstance()?.routeDao()?.getAll()!!.isEmpty())
                                                getRouteList()
                                            else {
                                                setRouteAdapter(AppDatabase.getDBInstance()?.routeDao()?.getAll() as ArrayList<RouteEntity>, -1, "")
                                            }
                                            progress_wheel.stopSpinning()
                                        }
                                    }
                                } else
                                    progress_wheel.stopSpinning()
                            } else
                                progress_wheel.stopSpinning()
                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )


        /*doAsync {

            for (i in 0 until (leaveTypeList.size ?: 0)) {
                AppDatabase.getDBInstance()?.leaveTypeDao()?.insert(leaveTypeList[i])
            }

            uiThread {
                setLeaveTypeAdapter(AppDatabase.getDBInstance()?.leaveTypeDao()?.getAll() as ArrayList<LeaveTypeEntity>)

                if (AppDatabase.getDBInstance()?.routeDao()?.getAll()!!.isEmpty())
                    getRouteList()
                else {
                    setRouteAdapter(AppDatabase.getDBInstance()?.routeDao()?.getAll() as ArrayList<RouteEntity>)
                }
            }
        }*/

        /*leaveTypeList.add("Casual Leave")
        leaveTypeList.add("Planned Leave")
        leaveTypeList.add("Sick Leave")
        leaveTypeList.add("Maternity Leave")
        leaveTypeList.add("Paternity Leave")
        leaveTypeList.add("Half Day Leave")
        leaveTypeList.add("Marriage Leave")*/
    }

    private var position = -1
    private var workTypeList = ArrayList<WorkTypeEntity>()
    @SuppressLint("WrongConstant")
    private fun setAdapter(list: ArrayList<WorkTypeEntity>?) {
        if (list != null) {
            rv_work_type_list.layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
            rv_work_type_list.adapter = WorkTypeAdapter(mContext, list, object : WorkTypeAdapter.OnWorkTypeClickListener {
                override fun onWorkTypeClick(workType: WorkTypeEntity?, adapterPosition: Int) {

                    checkIfWorkTypeSelected(workType, adapterPosition, list)

                    if (workTypeList.size > 0) {

                        for (i in workTypeList.indices) {
                            if (Pref.isMultipleAttendanceSelection) {
                                if (i == 0) {
                                    workTypeId = workTypeList[i].ID.toString()
                                    tv_work_type.text = workTypeList[i].Descrpton
                                } else {
                                    workTypeId = workTypeId + "," + workTypeList[i].ID
                                    tv_work_type.text = tv_work_type.text.toString().trim() + ", " + workTypeList[i].Descrpton
                                }
                            }
                            else {
                                workTypeId = workTypeList[i].ID.toString()
                                tv_work_type.text = workTypeList[i].Descrpton
                            }
                        }
                    } else {
                        workTypeId = ""
                        tv_work_type.text = ""

                    }

                    /*workTypeId = workType?.ID!!
                    tv_work_type.text = workType.Descrpton*/


                    //Show Route option
                    if (tv_work_type.text.contains("Field")) {

                        val list_ = AppDatabase.getDBInstance()?.routeDao()?.getAll()
                        if (list_ != null && list_.isNotEmpty())
                            cv_route.visibility = View.VISIBLE
                    } else
                        cv_route.visibility = View.GONE


                    /*ll_work_type_list.visibility = View.GONE
                    iv_work_type_dropdown.isSelected = false*/

                    /*if (TextUtils.isEmpty(tv_work_type.text.toString().trim())) {
                        tv_work_type.text = workType?.Descrpton
                        position = adapterPosition
                    } else {

                        if (position == adapterPosition)
                            tv_work_type.text = tv_work_type.text.toString().trim() + ", " + workType?.Descrpton

                    }*/
                    /*iv_work_type_dropdown.isSelected = false
                    ll_work_type_list.visibility = View.GONE*/

                    /*if (!TextUtils.isEmpty(workTypeId))
                        workTypeId = workTypeId + ", " + workType?.ID!!
                    else
                        workTypeId = workType?.ID!!*/
                }
            })
        }
    }

    private fun checkIfWorkTypeSelected(workType: WorkTypeEntity?, adapterPosition: Int, list: ArrayList<WorkTypeEntity>?) {
        //if (Pref.isMultipleAttendanceSelection) {
            if (workTypeList.size > 0) {

                for (i in workTypeList.indices) {
                    if (workTypeList[i].ID == workType?.ID) {
                        AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(false, workType.ID)
                        workTypeList.remove(list?.get(adapterPosition))
                        return
                    }
                }

                if (!Pref.isMultipleAttendanceSelection) {
                    workTypeList.forEach {
                        AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(false, it.ID)
                        workTypeList.remove(it)
                    }
                }

                AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(true, workType?.ID!!)
                workTypeList.add(list?.get(adapterPosition)!!)

            } else {
                AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(true, workType?.ID!!)
                workTypeList.add(list!![adapterPosition])
            }
        /*}
        else {
            if (list?.get(adapterPosition)?.isSelected!!)
                workTypeList.add(list[adapterPosition])
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 171) {
            if (resultCode == Activity.RESULT_OK) {
                CustomStatic.FaceDetectionAccuracyLower=Pref.FaceDetectionAccuracyLower
                if (data != null) {
                    //var faceMatchStatus:Boolean = data.getBooleanExtra("value",false)
                    var faceMatchStatus:Boolean = data.getBooleanExtra("valueD",false)
                    if(faceMatchStatus){
                        //visibilityCheck()
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.face_match_success))


                        if(AppUtils.getSharedPreferencesIsFaceDetectionWithCaptcha(mContext)){
                            captchaCheck()
                        }else{
                            //31-08-2021
                            BaseActivity.isApiInitiated=true
                            prepareAddAttendanceInputParams()
                        }
                    }

                }
            }
        }
    }

    fun captchaCheck() {
        val simpleDialogg = Dialog(mContext)
        simpleDialogg.setCancelable(false)
        simpleDialogg.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialogg.setContentView(R.layout.attendance_captcha_check)
        val tv_captcha_no = simpleDialogg.findViewById(R.id.tv_captcha_no) as AppCustomTextView
        tv_captcha_no.text = getRandomNumberString().toString()

        val tv_submit_captcha = simpleDialogg.findViewById(R.id.tv_submit_captcha) as AppCustomTextView

        val e1 = simpleDialogg.findViewById(R.id.et_captcha1) as EditText
        val e2 = simpleDialogg.findViewById(R.id.et_captcha2) as EditText
        val e3 = simpleDialogg.findViewById(R.id.et_captcha3) as EditText
        val e4 = simpleDialogg.findViewById(R.id.et_captcha4) as EditText

        tv_submit_captcha.isEnabled=false

        e1.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
                if (!TextUtils.isEmpty(e1.text.toString().trim())) {
                    e2.requestFocus()
                } else {

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        e2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!TextUtils.isEmpty(e2.text.toString().trim())) {
                    e3.requestFocus()
                } else {

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        e3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!TextUtils.isEmpty(e3.text.toString().trim())) {
                    e4.requestFocus()
                } else {

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        e4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!TextUtils.isEmpty(e4.text.toString().trim())) {
                    tv_submit_captcha.isEnabled=true
                    e4.clearFocus()
                } else {

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        tv_submit_captcha.setOnClickListener({ view ->

            if (AppUtils.isOnline(mContext)) {
                if(tv_captcha_no.text.toString().equals(e1.text.toString()+e2.text.toString()+e3.text.toString()+e4.text.toString())){
                    //Toast.makeText(mContext,"",Toast.LENGTH_SHORT).show()
                    simpleDialogg.cancel()
                    prepareAddAttendanceInputParams()
                }else{
                    //Toast.makeText(mContext,"Invalid  captcha",Toast.LENGTH_SHORT).show()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.not_match))
                }
            } else {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.not_match))
            }


        })

        simpleDialogg.show()

    }

    fun getRandomNumberString(): String? {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        val rnd = Random()
        val number = rnd.nextInt(9999)

        // this will convert any number sequence into 6 character.
        return String.format("%04d", number)
    }

    fun getLocforStart() {
            if (AppUtils.isOnline(mContext)) {
                if (AppUtils.mLocation != null) {
                    if (AppUtils.mLocation!!.accuracy <= Pref.gpsAccuracy.toInt()) {
                        if (AppUtils.mLocation!!.accuracy <= Pref.shopLocAccuracy.toFloat()) {
                            getNearyShopListDD(AppUtils.mLocation!!)
                        } else {
                            //getDDList(AppUtils.mLocation!!)
                            singleLocationDD()
                        }
                    } else {
                        XLog.d("=====Inaccurate current location (Local Shop List)=====")
                        singleLocationDD()
                    }
                } else {
                    XLog.d("=====null location (Local Shop List)======")
                    singleLocationDD()
                }
            } else
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))


    }

    private fun singleLocationDD() {
        progress_wheel.spin()
        var isGetLocation = -1
        SingleShotLocationProvider.requestSingleUpdate(mContext,
                object : SingleShotLocationProvider.LocationCallback {
                    override fun onStatusChanged(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNewLocationAvailable(location: Location) {
                        if (isGetLocation == -1) {
                            isGetLocation = 0
                            if (location.accuracy > Pref.gpsAccuracy.toInt()) {
                                (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                                progress_wheel.stopSpinning()
                            } else
                                getNearyShopListDD(location)
                        }
                    }

                })

        val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                try {
                    if (isGetLocation == -1) {
                        isGetLocation = 1
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage("GPS data to show nearby party is inaccurate. Please stop " +
                                "internet, stop GPS/Location service, and then restart internet and GPS services to get nearby party list.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 15000)
    }

    fun getNearyShopListDD(location: Location) {
        var nearestDist=5000
        //var nearBy: Double = Pref.shopLocAccuracy.toDouble()
        //var nearBy: Double = 4000.00
        var nearBy: Double = 500.0
        try {
             nearBy = Pref.DistributorGPSAccuracy.toDouble()
        }catch (e:java.lang.Exception){
             nearBy = 500.0
            Pref.DistributorGPSAccuracy="500"
        }

        var shop_id: String = ""
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        var finalNearByDD: AssignToDDEntity = AssignToDDEntity()

        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        if (newList != null && newList.size > 0) {
            for (i in 0 until newList.size) {
                val shopLat: Double = newList[i].shopLat
                val shopLong: Double = newList[i].shopLong
                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, Pref.DistributorGPSAccuracy.toInt())
                    var dist=location.distanceTo(shopLocation).toInt()  //21-10-2021
                    if (isShopNearby) {
                        if ((location.distanceTo(shopLocation)) < nearBy) {
                            nearBy = location.distanceTo(shopLocation).toDouble()
                            finalNearByShop = newList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }else{
                        if(dist<nearestDist){
                            nearestDist=dist
                        }
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
        val newDDList = java.util.ArrayList<AssignToDDEntity>()
        for (i in allDDList.indices) {
            newDDList.add(allDDList[i])
        }

        if (newDDList != null && newDDList.size > 0) {
            for (i in 0 until newDDList.size) {
                val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                if (ddLat != null && ddLong != null) {
                    val ddLocation = Location("")
                    ddLocation.latitude = ddLat
                    ddLocation.longitude = ddLong
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, Pref.DistributorGPSAccuracy.toInt())
                    var dist=location.distanceTo(ddLocation).toInt()  //21-10-2021
                    if (isShopNearby) {
                        if ((location.distanceTo(ddLocation)) < nearBy) {
                            nearBy = location.distanceTo(ddLocation).toDouble()
                            finalNearByDD = newDDList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }else{
                        if(dist<nearestDist){
                            nearestDist=dist
                        }
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }
        //visibilityCheck()
        if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
            //attendance given
            visibilityCheck()
            //callAddAttendanceApi(addAttendenceModel)
        } else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
            //attendance given
            visibilityCheck()
            // callAddAttendanceApi(addAttendenceModel)
        } else {
            progress_wheel.stopSpinning()
            val simpleDialog = Dialog(mContext)
            simpleDialog.setCancelable(false)
            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            simpleDialog.setContentView(R.layout.dialog_message_broad)
            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
            //dialog_yes_no_headerTV.text = "Hi "+Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)+"!"
            dialog_yes_no_headerTV.text = "Hi "+Pref.user_name!!+"!"
            if(nearestDist==5000){
                dialogHeader.text = "You must be either in Distributor or Outlet point to mark your attendance"+
                        ". Current location has been detected "+nearestDist.toString() +" mtr or more distance from the Distributor or Retail point from your handset GPS."
            }else{
                dialogHeader.text = "You must be either in Distributor or Outlet point to mark your attendance"+
                        ". Current location has been detected "+nearestDist.toString() +" mtr distance from the Distributor or Retail point from your handset GPS."
            }

            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
            dialogYes.setOnClickListener({ view ->
                simpleDialog.cancel()
            })
            simpleDialog.show()
            //(mContext as DashboardActivity).showSnackMessage("You must be either in Distributor or Outlet point to mark your attendance"+
            //". Current location has been detected "+nearestDist.toString() +" mtr distance from the Distributor or Retail point from your handset GPS." )
        }

    }

    /*fun getNearyShopListDD(location: Location) {
        var nearBy: Double = Pref.shopLocAccuracy.toDouble()
        var shop_id: String = ""
        var finalNearByShop: AddShopDBModelEntity = AddShopDBModelEntity()
        var finalNearByDD: AssignToDDEntity = AssignToDDEntity()

        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val newList = java.util.ArrayList<AddShopDBModelEntity>()
        for (i in allShopList.indices) {
            newList.add(allShopList[i])
        }

        if (newList != null && newList.size > 0) {
            for (i in 0 until newList.size) {
                val shopLat: Double = newList[i].shopLat
                val shopLong: Double = newList[i].shopLong
                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                    if (isShopNearby) {
                        if ((location.distanceTo(shopLocation)) < nearBy) {
                            nearBy = location.distanceTo(shopLocation).toDouble()
                            finalNearByShop = newList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        val allDDList = AppDatabase.getDBInstance()!!.ddListDao().getAll()
        val newDDList = java.util.ArrayList<AssignToDDEntity>()
        for (i in allDDList.indices) {
            newDDList.add(allDDList[i])
        }

        if (newDDList != null && newDDList.size > 0) {
            for (i in 0 until newDDList.size) {
                val ddLat: Double = newDDList[i].dd_latitude!!.toDouble()
                val ddLong: Double = newDDList[i].dd_longitude!!.toDouble()
                if (ddLat != null && ddLong != null) {
                    val ddLocation = Location("")
                    ddLocation.latitude = ddLat
                    ddLocation.longitude = ddLong
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, ddLocation, LocationWizard.NEARBY_RADIUS)
                    if (isShopNearby) {
                        if ((location.distanceTo(ddLocation)) < nearBy) {
                            nearBy = location.distanceTo(ddLocation).toDouble()
                            finalNearByDD = newDDList[i]
                        }
                        //startDay(newList[i], location)
                        //break
                    }
                }
            }

        } else {
            //(mContext as DashboardActivity).showSnackMessage("No Shop Found")
        }

        if (finalNearByDD.dd_id != null && finalNearByDD.dd_id!!.length > 1) {
            //attendance given
            visibilityCheck()
            //callAddAttendanceApi(addAttendenceModel)
        } else if (finalNearByShop.shop_id != null && finalNearByShop.shop_id!!.length > 1) {
            //attendance given
            visibilityCheck()
           // callAddAttendanceApi(addAttendenceModel)
        } else {
            progress_wheel.stopSpinning()
            (mContext as DashboardActivity).showSnackMessage("You must be either in Distributor or Outlet point to mark your attendance")
        }

    }*/

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_attendance_submit -> {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                if(Pref.IsShowDayStart){
                    getLocforStart()
                }
                else{
                    visibilityCheck()
                }



          /*      if(AppUtils.getSharedPreferencesIsFaceDetection(mContext) ||true){
                    getPicUrl()
                }else{
                    //visibilityCheck()
                }
*/

//                startActivity(Intent(mContext,FaceStartActivity::class.java))
                //startActivity(Intent(mContext,DetectorActivity::class.java))



            }

            R.id.ll_on_leave -> {
                if (!iv_leave_check.isSelected) {
                    iv_leave_check.isSelected = true
                    iv_attendance_check.isSelected = false
                    ll_add_attendance_main.visibility = View.GONE
                    isOnLeave = true
                    tv_attendance_submit.visibility = View.VISIBLE
                    ll_add_attendance_leave_type.visibility = View.VISIBLE
                    cv_dd_field.visibility = View.GONE
                    //fab_add_work_type.visibility = View.GONE
                }

                val now = Calendar.getInstance(Locale.ENGLISH)
                val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                )
                dpd.isAutoHighlight = true
                dpd.minDate = Calendar.getInstance(Locale.ENGLISH)
                dpd.show((context as Activity).fragmentManager, "Datepickerdialog")

                if (Pref.willLeaveApprovalEnable)
                    tv_attendance_submit.text = getString(R.string.send_for_approval)
                else
                    tv_attendance_submit.text = getString(R.string.submit_button_text)
            }

            R.id.ll_at_work -> {

                if (!checkHomeLocation()) {
                    (mContext as DashboardActivity).showSnackMessage("'Not allowed to Mark Attendance. You are not at permitted location.")
                    return
                }

                if (!iv_attendance_check.isSelected) {
                    iv_leave_check.isSelected = false
                    iv_attendance_check.isSelected = true
                    ll_add_attendance_main.visibility = View.VISIBLE
                    ll_add_attendance_main.isEnabled = true
                    isOnLeave = false
                    tv_attendance_submit.visibility = View.VISIBLE
                    ll_add_attendance_leave_type.visibility = View.GONE

                    if (Pref.isDDFieldEnabled)
                        cv_dd_field.visibility = View.VISIBLE
                    else
                        cv_dd_field.visibility = View.GONE

                    if (willShowUpdateDayPlan)
                        tv_attendance_submit.text = Pref.updateDayPlanText
                    else
                        tv_attendance_submit.text = getString(R.string.submit_button_text)

                    //fab_add_work_type.visibility = View.VISIBLE
                }
            }

            R.id.cv_beat_type_root->{
                if(!Pref.IsDistributorSelectionRequiredinAttendance){
                  val list = AppDatabase.getDBInstance()?.beatDao()?.getAll() as ArrayList<BeatEntity>

                    if (list != null && list.isNotEmpty())
                        showBeatListDialog(list)
                    else
                        getBeatListApi(false)
                }
                else{
                    var beatList=AppDatabase.getDBInstance()?.addShopEntryDao()?.getDistinctBeatID(assignedToDDId) as List<String>

                    if(beatList.size>0){
                        beatList = beatList.filter { it!="" }.distinct()
                        var listFilteredBeat : ArrayList<BeatEntity> = ArrayList()
                        doAsync {
                            listFilteredBeat= ArrayList()
                            for(i in 0..beatList.size-1){
                                var obj = AppDatabase.getDBInstance()?.beatDao()?.getSingleItem(beatList.get(i)) as BeatEntity
                                if(obj!=null){
                                    listFilteredBeat.add(obj)
                                }
                                else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))

                            }
                            uiThread {
                                if (listFilteredBeat != null && listFilteredBeat.isNotEmpty())
                                    showBeatListDialog(listFilteredBeat)
                                else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                            }
                        }
                    }
                    else
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))

                }


            }

            R.id.cv_dd_root->{
                val list = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopNameByDD("4") as ArrayList<AddShopDBModelEntity>
                if (list != null && list.isNotEmpty())
                    showDDListDialog(list)
            }

            R.id.rl_work_type_header -> {
                if (iv_work_type_dropdown.isSelected) {
                    iv_work_type_dropdown.isSelected = false
                    ll_work_type_list.visibility = View.GONE
                } else {
                    iv_work_type_dropdown.isSelected = true
                    ll_work_type_list.visibility = View.VISIBLE
                }
            }

            R.id.rl_leave_type_header -> {
                if (iv_leave_type_dropdown.isSelected) {
                    iv_leave_type_dropdown.isSelected = false
                    ll_leave_type_list.visibility = View.GONE
                } else {
                    iv_leave_type_dropdown.isSelected = true
                    ll_leave_type_list.visibility = View.VISIBLE
                }
            }

            R.id.rl_route_header -> {
                if (iv_route_dropdown.isSelected) {
                    iv_route_dropdown.isSelected = false
                    ll_route_list.visibility = View.GONE
                } else {
                    iv_route_dropdown.isSelected = true
                    ll_route_list.visibility = View.VISIBLE
                }
            }

            R.id.tv_show_date_range -> {
                val now = Calendar.getInstance(Locale.ENGLISH)
                val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                )
                dpd.isAutoHighlight = true
                dpd.minDate = Calendar.getInstance(Locale.ENGLISH)
                dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
            }

            R.id.et_from_loc -> {
                if (loc_list != null && loc_list!!.isNotEmpty())
                    showAreaDialog(true)
                else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_date_found))
            }

            R.id.et_to_loc -> {
                if (loc_list != null && loc_list!!.isNotEmpty())
                    showAreaDialog(false)
                else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_date_found))
            }
        }
    }


    private fun showBeatListDialog(list: ArrayList<BeatEntity>) {
        BeatListCustomDialog.newInstance(list as ArrayList<BeatEntity>) {
            tv_beat_type.text = it.name
            mbeatId = it.beat_id!!
            Pref.SelectedBeatIDFromAttend = mbeatId
        }.show((mContext as DashboardActivity).supportFragmentManager, "")

    }

    private fun showDDListDialog(list: ArrayList<AddShopDBModelEntity>) {
        DDWiseBeatListCustomDialog.newInstance(list as ArrayList<AddShopDBModelEntity>) {
            tv_dd.text = it.shopName
            assignedToDDId = it.shop_id!!
            Pref.SelectedDDIDFromAttend = assignedToDDId
        }.show((mContext as DashboardActivity).supportFragmentManager, "")

    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun showAreaDialog(isFromLoc: Boolean) {
        LocationListDialog.newInstance(loc_list) {
            if (isFromLoc) {
                et_from_loc.setText(it.location)
                fromID = it.id
                fromLat = it.lattitude
                fromLong = it.longitude

                if (Pref.isAttendanceDistanceShow && toID.isNotEmpty())
                    callDistanceApi()
            }
            else {
                et_to_loc.setText(it.location)
                toID = it.id
                toLat = it.lattitude
                toLong = it.longitude

                if (Pref.isAttendanceDistanceShow && fromID.isNotEmpty())
                    callDistanceApi()
            }

        }.show(fragmentManager!!, "")
    }

    private fun callDistanceApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = RouteRepoProvider.routeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getDistance(fromID, toID)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as DistanceResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                cv_distance.visibility = View.VISIBLE
                                et_distance.setText(response.distance + " KM")
                                (mContext as DashboardActivity).visitDistance = response.distance
                            }
                            else {
                                cv_distance.visibility = View.GONE
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            cv_distance.visibility = View.GONE
                        })
        )
    }

    private fun checkHomeLocation(): Boolean {
        if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude)) {
            if (Pref.isHomeLocAvailable) {

                if (!TextUtils.isEmpty(Pref.home_latitude) && !TextUtils.isEmpty(Pref.home_longitude)) {
                    val distance = LocationWizard.getDistance(Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble(), Pref.current_latitude.toDouble(),
                            Pref.current_longitude.toDouble())

                    XLog.e("Distance from home====> $distance")

                    if (Pref.isHomeRestrictAttendance == "0") {
                        if (distance * 1000 > Pref.homeLocDistance.toDouble()) {
                            return true
                        } else {
                            return false
                        }
                    } else if (Pref.isHomeRestrictAttendance == "1")
                        return true
                    else if (Pref.isHomeRestrictAttendance == "2") {
                        if (distance * 1000 > Pref.homeLocDistance.toDouble()) {
                            return false
                        } else {
                            return true
                        }
                    } else
                        return true
                } else {
                    XLog.e("========Home location is not available========")
                    return true
                }

            } else {
                XLog.e("========isHomeLocAvailable is false========")
                return true
            }
        } else {
            XLog.e("========Current location is not available========")
            return true
        }
    }

    override fun onDateSet(datePickerDialog: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int,
                           dayOfMonthEnd: Int) {

        datePickerDialog?.minDate = Calendar.getInstance(Locale.ENGLISH)
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd
        if (dayOfMonth < 10)
            day = "0$dayOfMonth"
        if (dayOfMonthEnd < 10)
            dayEnd = "0$dayOfMonthEnd"
        var fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear + 1).toString() + "") + "-" + year
        var endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd + 1).toString() + "") + "-" + yearEnd
        if (AppUtils.getStrinTODate(endString).before(AppUtils.getStrinTODate(fronString))) {
            (mContext as DashboardActivity).showSnackMessage("Your end date is before start date.")
            return
        }
        //val date = "Leave: From " + day + AppUtils.getDayNumberSuffix(day.toInt()) + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + " " + year + " To " + dayEnd + AppUtils.getDayNumberSuffix(dayEnd.toInt()) + FTStorageUtils.formatMonth((++monthOfYearEnd).toString() + "") + " " + yearEnd
        val date = "<font color=#800000> Leave: From </font>" + day + AppUtils.getDayNumberSuffix(day.toInt()) + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + " " + year + "<font color=#800000> To </font>" + dayEnd + AppUtils.getDayNumberSuffix(dayEnd.toInt()) + FTStorageUtils.formatMonth((++monthOfYearEnd).toString() + "") + " " + yearEnd
        tv_show_date_range.visibility = View.VISIBLE
        tv_show_date_range.text = Html.fromHtml(date)

        startDate = AppUtils.convertFromRightToReverseFormat(fronString)
        endDate = AppUtils.convertFromRightToReverseFormat(endString)

        /*if (AppUtils.isOnline(mContext)) {
            var attendanceReq = AttendanceRequest()
            attendanceReq.user_id = Pref.user_id
            attendanceReq.session_token = Pref.session_token
            attendanceReq.start_date = AppUtils.changeLocalDateFormatToAtt(fronString)
            attendanceReq.end_date = AppUtils.changeLocalDateFormatToAtt(endString)
            callAttendanceListApi(attendanceReq)
        } else {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }*/
    }


    private fun visibilityCheck() {
        if (!isOnLeave) {
            if (TextUtils.isEmpty(workTypeId))
                (mContext as DashboardActivity).showSnackMessage("Please select work type")
            else if(TextUtils.isEmpty(mbeatId) && Pref.IsBeatRouteAvailableinAttendance)
                if(Pref.IsDistributorSelectionRequiredinAttendance ){
                    if(TextUtils.isEmpty(assignedToDDId)){
                        openDialogPopup("Hi! (${Pref.user_name})","Please select ${Pref.ddText}")
                    }else
                        openDialogPopup("Hi! (${Pref.user_name})","Please select ${Pref.beatText} type")
                }else
                    openDialogPopup("Hi! (${Pref.user_name})","Please select ${Pref.beatText} type")
            else {
                if (tv_work_type.text.contains("Field")) {
                    val list_ = AppDatabase.getDBInstance()?.routeDao()?.getAll()
                    if (list_ != null && list_.isNotEmpty()) {
                        if (TextUtils.isEmpty(routeID))
                            (mContext as DashboardActivity).showSnackMessage("Please select route")
                        else
                            checkStateValidation()
                    } else
                        checkStateValidation()
                } else {
                    checkStateValidation()
                }
            }
        } else {
            if (TextUtils.isEmpty(leaveId))
                (mContext as DashboardActivity).showSnackMessage("Please select leave type")
            else if (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate))
                (mContext as DashboardActivity).showSnackMessage("Please select date range")
            else if (Pref.willLeaveApprovalEnable && TextUtils.isEmpty(et_leave_reason_text.text.toString().trim()))
                (mContext as DashboardActivity).showSnackMessage("Please enter remarks")
            else
                checkNetworkConnectivity()
        }
    }

    private fun checkStateValidation() {
        if (stateList != null && stateList!!.size > 0) {
            if (stateList?.size!! != PrimaryValueAdapter.primaryValueList.size && Pref.isPrimaryTargetMandatory) {
                (mContext as DashboardActivity).showSnackMessage("Please enter all primary value plans")
            } else {

                var isInvalid = false

                for (i in PrimaryValueAdapter.primaryValueList.indices) {
                    if (PrimaryValueAdapter.primaryValueList[i].toInt() == 0) {
                        isInvalid = true
                        (mContext as DashboardActivity).showSnackMessage("Please enter valid primary value plan")
                        break
                    }
                }

                if (!isInvalid) {
                    if (Pref.isDDFieldEnabled)
                        checkDDFields()
                    else if (Pref.isVisitPlanMandatory)
                        checkVisitFields()
                    else
                        checkNetworkConnectivity()
                }
            }
        } else
            if (Pref.isDDFieldEnabled)
                checkDDFields()
            else if (Pref.isVisitPlanMandatory)
                checkVisitFields()
            else
                checkNetworkConnectivity()
    }

    private fun checkDDFields() {
        when {
            TextUtils.isEmpty(et_dd_name.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage("Please enter distributor name")
            TextUtils.isEmpty(et_market_worked.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage("Please enter market worked")
            else -> {
                if (Pref.isVisitPlanMandatory)
                    checkVisitFields()
                else
                    checkNetworkConnectivity()
            }
        }
    }

    private fun checkVisitFields() {
        when {
            TextUtils.isEmpty(fromID) -> (mContext as DashboardActivity).showSnackMessage("Please select start location")
            TextUtils.isEmpty(toID) -> (mContext as DashboardActivity).showSnackMessage("Please select end location")
            else -> checkNetworkConnectivity()
        }
    }

    private fun checkNetworkConnectivity() {

        /*if (AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!, AppUtils.getCurrentDateChanged()).isEmpty()) {
            val userLoginDataEntity = UserLoginDataEntity()
            userLoginDataEntity.logindate = AppUtils.getCurrentDateChanged()
//            userLoginDataEntity.login_date= AppUtils.getCurrentDateInDate()
            if (!isOnLeave)
                userLoginDataEntity.logintime = AppUtils.getCurrentTimeWithMeredian()
            userLoginDataEntity.userId = Pref.user_id!!
            AppDatabase.getDBInstance()!!.userAttendanceDataDao().insertAll(userLoginDataEntity)
        }
        Pref.isAddAttendence = true
        (mContext as DashboardActivity).showSnackMessage("Attendance added successfully")
        (mContext as DashboardActivity).onBackPressed()*/

        if (AppUtils.isOnline(mContext)) {
            if (BaseActivity.isApiInitiated)
                return

            BaseActivity.isApiInitiated = true

            /*if (!Pref.willLeaveApprovalEnable) {
                if (!isOnLeave) {
                    if (Pref.isFingerPrintMandatoryForAttendance) {
                        if ((mContext as DashboardActivity).isFingerPrintSupported)
                            showFingerPrintDialog()
                        else
                            prepareAddAttendanceInputParams()
                    } else if (Pref.isSelfieMandatoryForAttendance) {
                        showSelfieDialog()
                    } else
                        prepareAddAttendanceInputParams()
                } else
                    prepareAddAttendanceInputParams()
            } else {
                if (!isOnLeave) {
                    if (Pref.isFingerPrintMandatoryForAttendance) {
                        if ((mContext as DashboardActivity).isFingerPrintSupported)
                            showFingerPrintDialog()
                        else
                            prepareAddAttendanceInputParams()
                    } else if (Pref.isSelfieMandatoryForAttendance) {
                        showSelfieDialog()
                    } else
                        prepareAddAttendanceInputParams()
                } else
                    callLeaveApprovalApi()
            }*/

            if (!Pref.willLeaveApprovalEnable)
                if(AppUtils.getSharedPreferencesIsFaceDetection(mContext) && isOnLeave!=true){
                    progress_wheel.spin()
                    getPicUrl()
                }else{
                    prepareAddAttendanceInputParams()
                }

                //prepareAddAttendanceInputParams()
            else {
                if (!isOnLeave)
                    if(AppUtils.getSharedPreferencesIsFaceDetection(mContext)){
                        progress_wheel.spin()
                        getPicUrl()
                    }else{
                        prepareAddAttendanceInputParams()
                    }
                    //prepareAddAttendanceInputParams()
                else{
                    //callLeaveApprovalApi()
                    calculateDaysForLeave()
                }
        }

        } else
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
    }

    private fun showSelfieDialog() {
        selfieDialog = SelfieDialog.getInstance({
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                initPermissionCheck()
            else {
                launchCamera()
            }
        }, false)
        selfieDialog?.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                //showPictureDialog()
                launchCamera()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun launchCamera() {
        if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, (mContext as DashboardActivity).getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)*/

            (mContext as DashboardActivity).captureFrontImage()
        }
    }

    fun setCameraImage(file: File) {
        selfieDialog?.dismiss()

        if (file == null || TextUtils.isEmpty(file.absolutePath)) {
            (mContext as DashboardActivity).showSnackMessage("Invalid Image")
            return
        }

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = AddAttendenceRepoProvider.sendAttendanceImgRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.attendanceWithImage(file.absolutePath, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse

                            if (response.status == NetworkConstant.SUCCESS) {
                                callAddAttendanceApi(addAttendenceModel)
                            } else {
                                BaseActivity.isApiInitiated = false
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }


                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )

    }

    private fun showFingerPrintDialog() {
        (mContext as DashboardActivity).checkForFingerPrint()

        fingerprintDialog = FingerprintDialog()
        fingerprintDialog?.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    /*private fun callLeaveApprovalApi() {
        val leaveApproval = SendLeaveApprovalInputParams()
        leaveApproval.session_token = Pref.session_token!!
        leaveApproval.user_id = Pref.user_id!!
        leaveApproval.leave_from_date = startDate
        leaveApproval.leave_to_date = endDate
        leaveApproval.leave_type = leaveId

        if (TextUtils.isEmpty(Pref.current_latitude))
            leaveApproval.leave_lat = "0.0"
        else
            leaveApproval.leave_lat = Pref.current_latitude

        if (TextUtils.isEmpty(Pref.current_longitude))
            leaveApproval.leave_long = "0.0"
        else
            leaveApproval.leave_long = Pref.current_longitude

        if (TextUtils.isEmpty(Pref.current_latitude))
            leaveApproval.leave_add = ""
        else
            leaveApproval.leave_add = LocationWizard.getLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())


        if (!TextUtils.isEmpty(et_leave_reason_text.text.toString().trim()))
            leaveApproval.leave_reason = et_leave_reason_text.text.toString().trim()

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("=========Leave Approval Input Params==========")
        XLog.d("session_token======> " + leaveApproval.session_token)
        XLog.d("user_id========> " + leaveApproval.user_id)
        XLog.d("leave_from_date=======> " + leaveApproval.leave_from_date)
        XLog.d("leave_to_date=======> " + leaveApproval.leave_to_date)
        XLog.d("leave_type========> " + leaveApproval.leave_type)
        XLog.d("leave_lat========> " + leaveApproval.leave_lat)
        XLog.d("leave_long========> " + leaveApproval.leave_long)
        XLog.d("leave_add========> " + leaveApproval.leave_add)
        XLog.d("===============================================")

        val repository = AddAttendenceRepoProvider.leaveApprovalRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.sendLeaveApproval(leaveApproval)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse
                            XLog.d("Leave Approval Response Code========> " + response.status)
                            XLog.d("Leave Approval Response Msg=========> " + response.message)
                            BaseActivity.isApiInitiated = false

                            if (response.status == NetworkConstant.SUCCESS) {

                                Pref.prevOrderCollectionCheckTimeStamp = 0L

                                //(mContext as DashboardActivity).showSnackMessage(response.message!!)
                                openPopupshowMessage(response.message!!)
                                //voiceAttendanceMsg("Hi, your leave applied successfully.")

                            /*    Handler().postDelayed(Runnable {
                                    (mContext as DashboardActivity).onBackPressed()
                                }, 500)*/

                            } else {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            XLog.d("Leave Approval Response ERROR=========> " + error.message)
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }*/

    var dateList:ArrayList<String> = ArrayList()
    var count=0

    @SuppressLint("NewApi")
    private fun calculateDaysForLeave(){
        val stDate = LocalDate.parse(startDate)
        val enDate = LocalDate.parse(endDate)
        val diff = Period.between(stDate,enDate)


        dateList.add(startDate)
        var countDate=stDate

        for(i in 0..diff.days-1){
            countDate=countDate.plusDays(1)
            dateList.add(countDate.toString())
        }
        //for(j in 0..dateList.size-1){
        callLeaveApiForUser()
        //}
    }

    private fun callLeaveApiForUser(){

        var stDate=dateList.get(count).toString()
        var enDate=dateList.get(count).toString()

        var addAttendenceModel: AddAttendenceInpuModel = AddAttendenceInpuModel()
        addAttendenceModel.user_id=Pref.user_id.toString()
        addAttendenceModel.add_attendence_time=AppUtils.getCurrentTimeWithMeredian()
        addAttendenceModel.collection_taken="0"
        addAttendenceModel.distance=""
        addAttendenceModel.distributor_name=""
        addAttendenceModel.from_id=""
        addAttendenceModel.is_on_leave="true"
        addAttendenceModel.leave_from_date=stDate
        addAttendenceModel.leave_to_date=enDate
        //addAttendenceModel.work_date_time=AppUtils.getCurrentDateTime()
        addAttendenceModel.work_date_time=stDate + " "+AppUtils.getCurrentTime()

        var mLeaveReason=""
        if (!TextUtils.isEmpty(et_leave_reason_text.text.toString().trim()))
            mLeaveReason = et_leave_reason_text.text.toString().trim()

        addAttendenceModel.leave_reason=mLeaveReason
        addAttendenceModel.leave_type=leaveId
        addAttendenceModel.market_worked=""
        addAttendenceModel.new_shop_visit="0"
        addAttendenceModel.order_taken="0"

        addAttendenceModel.revisit_shop="0"
        addAttendenceModel.route=""
        addAttendenceModel.session_token=""

        addAttendenceModel.work_lat=Pref.current_latitude
        addAttendenceModel.work_long=Pref.current_longitude

        addAttendenceModel.beat_id="0"

        val repository = AddAttendenceRepoProvider.addAttendenceRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.addAttendence(addAttendenceModel)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    progress_wheel.stopSpinning()
                    val response = result as BaseResponse
                    if (response.status == NetworkConstant.SUCCESS) {
                        if(count==(dateList.size-1)){
                            count=0
                            callLeaveApprovalApi()
                        }else{
                            count++
                            callLeaveApiForUser()
                        }
                    } else {
                        BaseActivity.isApiInitiated = false
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                    }
                    Log.e("ApprovalPend work attendance", "api work type")

                }, { error ->
                    XLog.d("AddAttendance Response Msg=========> " + error.message)
                    BaseActivity.isApiInitiated = false
                    progress_wheel.stopSpinning()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                })
        )

    }

    private fun callLeaveApprovalApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        var stDate=dateList.get(count).toString()
        var enDate=dateList.get(count).toString()

        val leaveApproval = SendLeaveApprovalInputParams()
        leaveApproval.session_token = Pref.session_token!!
        leaveApproval.user_id = Pref.user_id!!
        leaveApproval.leave_from_date = stDate
        leaveApproval.leave_to_date = enDate
        leaveApproval.leave_type = leaveId

        var tt=AppUtils.getCurrentDateTime()

        if (TextUtils.isEmpty(Pref.current_latitude))
            leaveApproval.leave_lat = "0.0"
        else
            leaveApproval.leave_lat = Pref.current_latitude

        if (TextUtils.isEmpty(Pref.current_longitude))
            leaveApproval.leave_long = "0.0"
        else
            leaveApproval.leave_long = Pref.current_longitude

        if (TextUtils.isEmpty(Pref.current_latitude))
            leaveApproval.leave_add = ""
        else
            leaveApproval.leave_add = LocationWizard.getLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())


        if (!TextUtils.isEmpty(et_leave_reason_text.text.toString().trim()))
            leaveApproval.leave_reason = et_leave_reason_text.text.toString().trim()

        XLog.d("=========Apply Leave Input Params==========")
        XLog.d("session_token======> " + leaveApproval.session_token)
        XLog.d("user_id========> " + leaveApproval.user_id)
        XLog.d("leave_from_date=======> " + leaveApproval.leave_from_date)
        XLog.d("leave_to_date=======> " + leaveApproval.leave_to_date)
        XLog.d("leave_type========> " + leaveApproval.leave_type)
        XLog.d("leave_lat========> " + leaveApproval.leave_lat)
        XLog.d("leave_long========> " + leaveApproval.leave_long)
        XLog.d("leave_add========> " + leaveApproval.leave_add)
        XLog.d("leave_reason========> " + leaveApproval.leave_reason)
        XLog.d("===============================================")

        val repository = AddAttendenceRepoProvider.leaveApprovalRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.sendLeaveApproval(leaveApproval)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    progress_wheel.stopSpinning()
                    val response = result as BaseResponse
                    XLog.d("Apply Leave Response Code========> " + response.status)
                    XLog.d("Apply Leave Response Msg=========> " + response.message)
                    BaseActivity.isApiInitiated = false

                    if (response.status == NetworkConstant.SUCCESS) {

                        Pref.prevOrderCollectionCheckTimeStamp = 0L

                        if(count==(dateList.size-1)){
                            count=0
                            openPopupshowMessage(response.message!!)
                        }else{
                            count++
                            callLeaveApprovalApi()

                        }

//                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                        //(mContext as DashboardActivity).onBackPressed()

                    } else {
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                    }

                }, { error ->
                    XLog.d("Apply Leave Response ERROR=========> " + error.message)
                    BaseActivity.isApiInitiated = false
                    progress_wheel.stopSpinning()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                })
        )
    }

    private fun openPopupshowMessage(message:String) {
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_message)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
        val dialogBody = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
        val obBtn = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
        dialogHeader.text="Hi "+Pref.user_name+"!"
        dialogBody.text = message
        obBtn.setOnClickListener({ view ->
            simpleDialog.cancel()
            voiceAttendanceMsg("Hi, your leave applied successfully.")
            //(mContext as DashboardActivity).loadFragment(FragType.LeaveListFragment, false, "")
            Handler().postDelayed(Runnable {
                if(Pref.Leaveapprovalfromsupervisor){
                    getSupervisorIDInfo()
                }else{
                    (mContext as DashboardActivity).onBackPressed()
                }
            }, 800)

        })
        simpleDialog.show()

    }

    private fun getSupervisorIDInfo(){
        try{
            val repository = AddAttendenceRepoProvider.addAttendenceRepo()
            BaseActivity.compositeDisposable.add(
                    repository.getReportToUserID(Pref.user_id.toString(),Pref.session_token.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as GetReportToResponse

                                if (response.status == NetworkConstant.SUCCESS) {
                                    getSupervisorFCMInfo(response.report_to_user_id!!)
                                }

                            }, { error ->
                                XLog.d("Apply Leave Response ERROR=========> " + error.message)
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }
    private fun getSupervisorFCMInfo(usrID:String){
        try{
            val repository = AddAttendenceRepoProvider.addAttendenceRepo()
            BaseActivity.compositeDisposable.add(
                    repository.getReportToFCMInfo(usrID,Pref.session_token.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as GetReportToFCMResponse

                                if (response.status == NetworkConstant.SUCCESS) {
                                    sendFCMNotiSupervisor(response.device_token!!)
                                }

                            }, { error ->
                                XLog.d("Apply Leave Response ERROR=========> " + error.message)
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }

    private fun sendFCMNotiSupervisor(superVisor_fcmToken:String){
        if (superVisor_fcmToken != "") {
            try {
                val jsonObject = JSONObject()
                val notificationBody = JSONObject()
                notificationBody.put("body","Leave applied by : "+Pref.user_name!!)
                notificationBody.put("flag", "flag")
                notificationBody.put("applied_user_id",Pref.user_id)
                notificationBody.put("leave_from_date",startDate)
                notificationBody.put("leave_to_date",endDate)
                notificationBody.put("leave_reason",et_leave_reason_text.text.toString().trim())
                notificationBody.put("leave_type",tv_leave_type)
                notificationBody.put("leave_type_id",leaveId)
                jsonObject.put("data", notificationBody)
                val jsonArray = JSONArray()
                jsonArray.put(0,superVisor_fcmToken)
                jsonObject.put("registration_ids", jsonArray)
                sendCustomNotification(jsonObject)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    fun sendCustomNotification(notification: JSONObject) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                object : Response.Listener<JSONObject?> {
                    override fun onResponse(response: JSONObject?) {
                        (mContext as DashboardActivity).onBackPressed()
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {

                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getString(R.string.firebase_key)
                params["Content-Type"] = "application/json"
                return params
            }
        }

        MySingleton.getInstance(mContext)!!.addToRequestQueue(jsonObjectRequest)
    }


    private fun voiceAttendanceMsg(msg: String) {
        if (Pref.isVoiceEnabledForAttendanceSubmit) {
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Add Attendance", "TTS error in converting Text to Speech!");
        }
    }

    fun continueAddAttendance() {

        if (fingerprintDialog != null && fingerprintDialog?.isVisible!!) {
            fingerprintDialog?.dismiss()

            if (Pref.isSelfieMandatoryForAttendance)
                showSelfieDialog()
            else
                callAddAttendanceApi(addAttendenceModel)
        }
    }

    private fun prepareAddAttendanceInputParams() {
        progress_wheel.stopSpinning()

        //(mContext as DashboardActivity).showSnackMessage("prepareAddAttendanceInputParams")
        //Toast.makeText(mContext,"prepareAddAttendanceInputParams",Toast.LENGTH_SHORT).show()
        try {
            addAttendenceModel.session_token = Pref.session_token!!
            addAttendenceModel.user_id = Pref.user_id!!
            addAttendenceModel.is_on_leave = isOnLeave.toString()

            if (!isOnLeave) {
                if (TextUtils.isEmpty(Pref.current_latitude))
                    addAttendenceModel.work_lat = "0.0"
                else
                    addAttendenceModel.work_lat = Pref.current_latitude

                if (TextUtils.isEmpty(Pref.current_longitude))
                    addAttendenceModel.work_long = "0.0"
                else
                    addAttendenceModel.work_long = Pref.current_longitude

                if (TextUtils.isEmpty(Pref.current_latitude))
                    addAttendenceModel.work_address = ""
                else
                    addAttendenceModel.work_address = LocationWizard.getLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())

                addAttendenceModel.work_desc = et_work_type_text.text.toString().trim()
                //addAttendenceModel.work_date_time = AppUtils.getCurrentDateTime12FormatToAttr(tv_current_date_time.text.toString().trim())
                addAttendenceModel.work_lat = Pref.current_latitude
                addAttendenceModel.work_type = workTypeId

                if (!TextUtils.isEmpty(et_order_value.text.toString().trim()))
                    addAttendenceModel.order_taken = et_order_value.text.toString().trim()

                if (!TextUtils.isEmpty(et_collection_value.text.toString().trim()))
                    addAttendenceModel.collection_taken = et_collection_value.text.toString().trim()

                if (!TextUtils.isEmpty(et_shop_visit.text.toString().trim()))
                    addAttendenceModel.new_shop_visit = et_shop_visit.text.toString().trim()

                if (!TextUtils.isEmpty(et_shop_revisit.text.toString().trim()))
                    addAttendenceModel.revisit_shop = et_shop_revisit.text.toString().trim()

                if (!TextUtils.isEmpty(Pref.profile_state))
                    addAttendenceModel.state_id = Pref.profile_state

                if (tv_work_type.text.contains("Field")) {
                    addAttendenceModel.route = routeID

                    val list = AppDatabase.getDBInstance()?.routeShopListDao()?.getSelectedData(true)
                    if (list != null && list.isNotEmpty()) {
                        val routeShopList = ArrayList<AddAttendenceInputDataModel>()
                        for (i in list.indices) {
                            val addAttendanceInputModel = AddAttendenceInputDataModel()
                            addAttendanceInputModel.route = list[i].route_id!!
                            addAttendanceInputModel.shop_id = list[i].shop_id!!
                            routeShopList.add(addAttendanceInputModel)
                        }
                        addAttendenceModel.shop_list = routeShopList
                    }
                }

                if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude)) {
                    Pref.source_latitude = Pref.current_latitude
                    Pref.source_longitude = Pref.current_longitude
                }

                if (stateList != null && stateList!!.size > 0) {

                    val primaryList = ArrayList<PrimaryValueDataModel>()

                    for (i in PrimaryValueAdapter.primaryValueList.indices) {
                        val primaryValue = PrimaryValueDataModel()
                        primaryValue.id = stateList?.get(i)?.id!!
                        primaryValue.primary_value = PrimaryValueAdapter.primaryValueList[i]
                        primaryList.add(primaryValue)
                    }

                    addAttendenceModel.primary_value_list = primaryList
                }

                if (Pref.isDDFieldEnabled) {
                    addAttendenceModel.distributor_name = et_dd_name.text.toString().trim()
                    addAttendenceModel.market_worked = et_market_worked.text.toString().trim()
                }

            } else {
                addAttendenceModel.leave_from_date = startDate
                addAttendenceModel.leave_to_date = endDate
                addAttendenceModel.leave_type = leaveId

                if (!TextUtils.isEmpty(et_leave_reason_text.text.toString().trim()))
                    addAttendenceModel.leave_reason = et_leave_reason_text.text.toString().trim()
            }

            addAttendenceModel.work_date_time = /*"2018-12-21T18:05:41"*/ AppUtils.getCurrentISODateTime()//AppUtils.getCurrentDateTime12FormatToAttr(AppUtils.getCurrentDateTime12Format())
            val addAttendenceTime =  /*"06:05 PM"*/ AppUtils.getCurrentTimeWithMeredian()
            addAttendenceModel.add_attendence_time = addAttendenceTime
            addAttendenceModel.from_id = fromID
            addAttendenceModel.to_id = toID


            if (!TextUtils.isEmpty(fromLat) && !TextUtils.isEmpty(toLat))
                addAttendenceModel.distance = LocationWizard.getDistance(fromLat.toDouble(), fromLong.toDouble(), toLat.toDouble(), toLong.toDouble()).toString()



            addAttendenceModel.beat_id =  if(Pref.IsBeatRouteAvailableinAttendance) Pref.SelectedBeatIDFromAttend else "0"

            doAttendanceViaApiOrPlanScreen()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun doAttendanceViaApiOrPlanScreen() {
        if (!willShowUpdateDayPlan) {

            if (!isOnLeave) {
                if (Pref.isFingerPrintMandatoryForAttendance) {
                    if ((mContext as DashboardActivity).isFingerPrintSupported)
                        showFingerPrintDialog()
                    else {
                        if (Pref.isSelfieMandatoryForAttendance)
                            showSelfieDialog()
                        else
                            callAddAttendanceApi(addAttendenceModel)
                    }
                } else if (Pref.isSelfieMandatoryForAttendance)
                    showSelfieDialog()
                else
                    callAddAttendanceApi(addAttendenceModel)
            } else
                callAddAttendanceApi(addAttendenceModel)

        } else {
            if (!isOnLeave) {
                AppUtils.isFromAttendance = true
                (mContext as DashboardActivity).isDailyPlanFromAlarm = false
                BaseActivity.isApiInitiated = false
                (mContext as DashboardActivity).loadFragment(FragType.DailyPlanListFragment, true, addAttendenceModel)
            } else
                callAddAttendanceApi(addAttendenceModel)
        }
    }

    @SuppressLint("NewApi")
    private fun callAddAttendanceApi(addAttendenceModel: AddAttendenceInpuModel) {
        XLog.e("==========AddAttendance=============")
        XLog.d("=====AddAttendance Input Params========")
        XLog.d("session_token-----> " + addAttendenceModel.session_token)
        XLog.d("user_id----------> " + addAttendenceModel.user_id)
        XLog.d("is_on_leave----------> " + addAttendenceModel.is_on_leave)
        XLog.d("work_lat----------> " + addAttendenceModel.work_lat)
        XLog.d("work_long----------> " + addAttendenceModel.work_long)
        XLog.d("work_address----------> " + addAttendenceModel.work_address)
        XLog.d("work_type----------> " + addAttendenceModel.work_type)
        XLog.d("route----------> " + addAttendenceModel.route)
        XLog.d("leave_from_date----------> " + addAttendenceModel.leave_from_date)
        XLog.d("leave_to_date----------> " + addAttendenceModel.leave_to_date)
        XLog.d("leave_type----------> " + addAttendenceModel.leave_type)
        XLog.d("leave_reason----------> " + addAttendenceModel.leave_reason)
        XLog.d("work_date_time----------> " + addAttendenceModel.work_date_time)
        XLog.d("add_attendence_time----------> " + addAttendenceModel.add_attendence_time)
        XLog.d("order taken----------> " + addAttendenceModel.order_taken)
        XLog.d("collection taken----------> " + addAttendenceModel.collection_taken)
        XLog.d("visit new shop----------> " + addAttendenceModel.new_shop_visit)
        XLog.d("revisit shop----------> " + addAttendenceModel.revisit_shop)
        XLog.d("state id----------> " + addAttendenceModel.state_id)
        XLog.d("shop_list size----------> " + addAttendenceModel.shop_list.size)
        XLog.d("primary_value_list size----------> " + addAttendenceModel.primary_value_list.size)
        XLog.d("update_plan_list size----------> " + addAttendenceModel.update_plan_list.size)
        XLog.d("from_id----------> " + addAttendenceModel.from_id)
        XLog.d("to_id----------> " + addAttendenceModel.to_id)
        XLog.d("distance----------> " + addAttendenceModel.distance)
        XLog.d("======End AddAttendance Input Params======")

        val repository = AddAttendenceRepoProvider.addAttendenceRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.addAttendence(addAttendenceModel)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse
                            XLog.d("AddAttendance Response Code========> " + response.status)
                            XLog.d("AddAttendance Response Msg=========> " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                /*if (AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getAll() != null)
                            AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.delete()

                        if (AppDatabase.getDBInstance()?.selectedRouteListDao()?.getAll() != null)
                            AppDatabase.getDBInstance()?.selectedRouteListDao()?.deleteRoute()

                        if (AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.getAll() != null)
                            AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.deleteData()*/

                                if(isOnLeave && Pref.IsLeaveGPSTrack==false){
                                    try {

                                        Pref.IsLeavePressed=true

                                        if (isLocationServiceRunning(LocationFuzedService::class.java)) {
                                            mContext.stopService(Intent(mContext, LocationFuzedService::class.java))
                                        }

                                        if(isGeofenceServiceRunning()){
                                            lateinit var geofenceService: Intent
                                            Pref.isGeoFenceAdded = false
                                            geofenceService = Intent(mContext, GeofenceService::class.java)
                                            mContext.stopService(geofenceService)
                                        }

                                        if(isMonitorServiceRunning()){
                                            var intent = Intent(mContext, MonitorService::class.java)
                                            intent.action = CustomConstants.STOP_MONITOR_SERVICE
                                            mContext.stopService(intent)
                                        }

                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }
                                }else{
                                    Pref.IsLeavePressed=false
                                }


                                Pref.visitDistance = (mContext as DashboardActivity).visitDistance

                                Pref.prevOrderCollectionCheckTimeStamp = 0L
                                PrimaryValueAdapter.primaryValueList.clear()

                                val list = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!, AppUtils.getCurrentDateChanged())
                                if (list != null && list.isNotEmpty()) {
                                    AppDatabase.getDBInstance()!!.userAttendanceDataDao().deleteTodaysData(AppUtils.getCurrentDateChanged())
                                }

                                if (AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!, AppUtils.getCurrentDateChanged()).isEmpty()) {
                                    val userLoginDataEntity = UserLoginDataEntity()
                                    userLoginDataEntity.logindate = AppUtils.getCurrentDateChanged()
                                    userLoginDataEntity.logindate_number = AppUtils.getTimeStampFromDateOnly(AppUtils.getCurrentDateForShopActi())
                                    //val addAttendenceTime = AppUtils.getCurrentTimeWithMeredian()
                                    //Pref.add_attendence_time = addAttendenceModel.add_attendence_time
                                    if (!isOnLeave) {
                                        userLoginDataEntity.logintime = addAttendenceModel.add_attendence_time
                                        userLoginDataEntity.Isonleave = "false"
                                        Pref.isOnLeave = "false"

                                        if (TextUtils.isEmpty(Pref.isFieldWorkVisible) || Pref.isFieldWorkVisible.equals("true", ignoreCase = true)) {

                                            val list = AppDatabase.getDBInstance()?.workTypeDao()?.getSelectedWork(true)
                                            if (list != null && list.isNotEmpty()) {

                                                for (i in list.indices) {
                                                    val selectedwortkType = SelectedWorkTypeEntity()
                                                    selectedwortkType.ID = list[i].ID
                                                    selectedwortkType.Descrpton = list[i].Descrpton
                                                    selectedwortkType.date = AppUtils.getCurrentDate()
                                                    AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.insertAll(selectedwortkType)
                                                }
                                            }

                                            val routeList = AppDatabase.getDBInstance()?.routeDao()?.getSelectedRoute(true)
                                            if (routeList != null && routeList.isNotEmpty()) {

                                                for (i in routeList.indices) {
                                                    val selectedRoute = SelectedRouteEntity()
                                                    selectedRoute.route_id = routeList[i].route_id
                                                    selectedRoute.route_name = routeList[i].route_name
                                                    selectedRoute.date = AppUtils.getCurrentDate()
                                                    AppDatabase.getDBInstance()?.selectedRouteListDao()?.insert(selectedRoute)
                                                }
                                            }

                                            val routeShopList = AppDatabase.getDBInstance()?.routeShopListDao()?.getSelectedData(true)
                                            if (routeShopList != null && routeShopList.isNotEmpty()) {

                                                for (i in routeShopList.indices) {
                                                    val selectedRouteShop = SelectedRouteShopListEntity()
                                                    selectedRouteShop.route_id = routeShopList[i].route_id
                                                    selectedRouteShop.shop_address = routeShopList[i].shop_address
                                                    selectedRouteShop.shop_contact_no = routeShopList[i].shop_contact_no
                                                    selectedRouteShop.shop_name = routeShopList[i].shop_name
                                                    selectedRouteShop.shop_id = routeShopList[i].shop_id
                                                    selectedRouteShop.date = AppUtils.getCurrentDate()
                                                    AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.insert(selectedRouteShop)
                                                }
                                            }
                                        }

                                        Pref.isAddAttendence = true
                                        Pref.add_attendence_time = addAttendenceModel.add_attendence_time
                                        (mContext as DashboardActivity).update_worktype_tv.apply {
                                            visibility = if (Pref.isUpdateWorkTypeEnable)
                                                View.VISIBLE
                                            else
                                                View.GONE
                                        }

                                        Pref.distributorName = addAttendenceModel.distributor_name
                                        Pref.marketWorked = addAttendenceModel.market_worked

                                        voiceAttendanceMsg("Hi, your attendance mark successfully.")

                                    } else {
                                        userLoginDataEntity.Isonleave = "true"

                                        if (addAttendenceModel.leave_from_date == AppUtils.getCurrentDateForShopActi()) {
                                            Pref.isOnLeave = "true"
                                            Pref.isAddAttendence = true
                                            Pref.add_attendence_time = addAttendenceModel.add_attendence_time

                                            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                            notificationManager.cancelAll()

                                        } else {
                                            Pref.isOnLeave = "false"
                                            Pref.isAddAttendence = false
                                        }

                                        val notification = NotificationUtils(getString(R.string.app_name), "", "", "")
                                        val body = "You have applied leave from date: " + addAttendenceModel.leave_from_date +
                                                ", to date: " + addAttendenceModel.leave_to_date + ", type: " + tv_leave_type.text.toString().trim()
                                        notification.sendLocNotification(mContext, body)

                                        voiceAttendanceMsg("Hi, your leave applied successfully.")
                                    }
                                    userLoginDataEntity.userId = Pref.user_id!!
                                    AppDatabase.getDBInstance()!!.userAttendanceDataDao().insertAll(userLoginDataEntity)
                                }

                                //Pref.isAddAttendence = true
                                BaseActivity.isApiInitiated = false

                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                if(Pref.ShowZeroCollectioninAlert)
                                    calculatePendingColl()
                                if(Pref.IsShowRepeatOrderinNotification)
                                    calculateZeroOrder()
                                //getDobAnniv()

                                Handler().postDelayed(Runnable {
                                    (mContext as DashboardActivity).onBackPressed()
                                }, 1000)




                                /*Pref.isAddAttendence = true
                        BaseActivity.isApiInitiated = false
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                        (mContext as DashboardActivity).onBackPressed()*/
                            } else {
                                BaseActivity.isApiInitiated = false
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                            Log.e("add attendance", "api work type")

                        }, { error ->
                            XLog.d("AddAttendance Response Msg=========> " + error.message)
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )

    }


    private fun calculatePendingColl(){
        progress_wheel.spin()
        if(Pref.IsCollectionEntryConsiderOrderOrInvoice){
            var pendingCollDataList:ArrayList<PendingCollData> = ArrayList()
            val shopList = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopIdHasOrder() as ArrayList<AddShopDBModelEntity>
            if(shopList.size>0){
                for(i in 0..shopList.size-1){
                    var totalInvAmt ="0"
                    var totalCollectionAmt ="0"
                    var dueAmt ="0"


                    var isShopOrder = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopOrder.size!=0){
                        for(j in 0..isShopOrder.size-1){
                            var ob=AppDatabase.getDBInstance()!!.billingDao().getInvoiceSumAmt(isShopOrder.get(j).order_id.toString())
                            if(ob!=null)
                                totalInvAmt=(totalInvAmt.toDouble()+ob.toDouble()).toString()
                        }
                    }

                    var isShopCollection = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopCollection.size!=0){
                        totalCollectionAmt = AppDatabase.getDBInstance()!!.collectionDetailsDao().getCollectSumAmt(shopList.get(i).shop_id.toString())
                    }

                    try{
                        dueAmt = (totalInvAmt.toDouble()-totalCollectionAmt.toDouble()).toString()
                    }catch (ex:Exception){

                    }
                    if(dueAmt.contains("-") || dueAmt.equals("0.0") || dueAmt.equals("0.00")){
                        dueAmt="0"
                    }

                    if(totalInvAmt.toDouble() == dueAmt.toDouble())
                        pendingCollDataList.add(PendingCollData(shopList.get(i).shopName,dueAmt,shopList.get(i).shop_id.toString()))
                }
            }
            if(pendingCollDataList.size>0){
                Pref.IsPendingColl=true
                Pref.NotiCountFlag = true
            }else{
                Pref.IsPendingColl=false
            }
            progress_wheel.stopSpinning()
        }
        else{
            var pendingCollDataList:ArrayList<PendingCollData> = ArrayList()
            val shopList = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopIdHasOrder() as ArrayList<AddShopDBModelEntity>
            if(shopList.size>0){
                for(i in 0..shopList.size-1){
                    var totalOrderAmt ="0"
                    var totalCollectionAmt ="0"
                    var dueAmt ="0"

                    var isShopOrder = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopOrder.size!=0){
                        totalOrderAmt=AppDatabase.getDBInstance()!!.orderDetailsListDao().getOrderSumAmt(shopList.get(i).shop_id.toString()).toString()

                        var isShopCollection = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                        if(isShopCollection.size!=0){
                            totalCollectionAmt = AppDatabase.getDBInstance()!!.collectionDetailsDao().getCollectSumAmt(shopList.get(i).shop_id.toString())
                        }
                        try{
                            dueAmt = (totalOrderAmt.toDouble()-totalCollectionAmt.toDouble()).toString()
                        }catch (ex:Exception){

                        }
                        if(dueAmt.contains("-")){
                            dueAmt="0"
                        }
                        if(totalOrderAmt.toDouble() == dueAmt.toDouble())
                            pendingCollDataList.add(PendingCollData(shopList.get(i).shopName,dueAmt,shopList.get(i).shop_id.toString()))
                    }

                }
            }
            if(pendingCollDataList.size>0){
                Pref.IsPendingColl=true
                Pref.NotiCountFlag = true
            }else{
                Pref.IsPendingColl=false
            }
            progress_wheel.stopSpinning()
        }
        progress_wheel.stopSpinning()
    }

    private fun calculateZeroOrder(){
        var shopList:ArrayList<AddShopDBModelEntity> = ArrayList()

        if(Pref.ZeroOrderInterval.toInt()!=0){
            dateList= ArrayList()
            shopList= ArrayList()
            progress_wheel.spin()
            var currentDStr = AppUtils.getCurrentDateyymmdd()
            var currentD= LocalDate.parse(currentDStr)
            var stDate=currentD.minusDays(Pref.ZeroOrderInterval.toInt().toLong())
            var endDate=currentD.minusDays(1)
            val diff = Period.between(stDate,endDate)

            dateList.add(stDate.toString())
            var countDate=stDate
            for(i in 0..diff.days-1){
                countDate=countDate.plusDays(1)
                dateList.add(countDate.toString())
            }

            var todayDateFormat = AppUtils.convertDateTimeToCommonFormat(AppUtils.getCurrentDateTime())
            var shopListfromOrder = AppDatabase.getDBInstance()!!.orderDetailsListDao().getDistinctShopIDExceptCurrDate(todayDateFormat) as ArrayList<String>

            if(shopListfromOrder!=null){
                for(i in 0..shopListfromOrder.size-1){
                    var isshopOrderTaken=false
                    for(j in 0..dateList.size-1){
                        var convDate=AppUtils.convertDateTimeToCommonFormat(dateList.get(j)+"T00:00:00")
                        var shopCount = AppDatabase.getDBInstance()!!.orderDetailsListDao().getAllByOnlyDate(convDate,shopListfromOrder.get(i))
                        if(shopCount.size>0){
                            isshopOrderTaken=true
                            break
                        }
                    }
                    if(isshopOrderTaken==false){
                        shopList.add(AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopListfromOrder.get(i)))
                    }
                }
            }
            progress_wheel.stopSpinning()
            if(shopList.size>0){
                Pref.IsZeroOrder=true
                Pref.NotiCountFlag = true
            }else{
                Pref.IsZeroOrder=false
            }


        }
    }

    private fun getDobAnniv(){
        Pref.IsTodayDOBDOA = false
        val list= AppDatabase.getDBInstance()?.addShopEntryDao()?.all
        var todayDateFormat = AppUtils.convertDateTimeToCommonFormat(AppUtils.getCurrentDateTime())
        for(i in 0..list!!.size-1){
           var  dob =   AppUtils.convertDateTimeToCommonFormat(list.get(i).dateOfBirth+"T00:00:00")
            var doa =  AppUtils.convertDateTimeToCommonFormat( list.get(i).dateOfAniversary+"T00:00:00")
            if(dob.equals(todayDateFormat) || doa.equals(todayDateFormat)){
                Pref.IsTodayDOBDOA = true
                break
            }
        }
    }

    fun getPicUrl(){
        //31-08-2021
        BaseActivity.isApiInitiated=false
        val repository = GetUserListPhotoRegProvider.provideUserListPhotoReg()
        BaseActivity.compositeDisposable.add(
                repository.getUserFacePicUrlApi(Pref.user_id!!,Pref.session_token!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as UserFacePicUrlResponse
                            if(response.status== NetworkConstant.SUCCESS){

                                CustomStatic.FaceUrl=response.face_image_link

                                //val intent = Intent(mContext, FaceStartActivity::class.java)
                                //startActivityForResult(intent, 111)


                                //var bitmap :Bitmap? = null
                                //registerFace(bitmap);
                                GetImageFromUrl().execute(CustomStatic.FaceUrl)

                                XLog.d(" AddAttendanceFragment : FaceRegistration/FaceMatch" +response.status.toString() +", : "  + ", Success: ")
                            }else{
                                BaseActivity.isApiInitiated = false
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_reg_face))
                                progress_wheel.stopSpinning()
                                XLog.d("AddAttendanceFragment : FaceRegistration/FaceMatch : " + response.status.toString() +", : "  + ", Failed: ")
                            }
                        },{
                            error ->
                            if (error != null) {
                                XLog.d("AddAttendanceFragment : FaceRegistration/FaceMatch : " + " : "  + ", ERROR: " + error.localizedMessage)
                            }
                            BaseActivity.isApiInitiated = false
                        })
        )
    }






    ///////////////////////////////
    var cropToFrameTransform: Matrix? = Matrix()
     var faceDetector: FaceDetector? = null
    private val TF_OD_API_MODEL_FILE = "mobile_face_net.tflite"
     val TF_OD_API_IS_QUANTIZED = false
     val TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt"
     val TF_OD_API_INPUT_SIZE = 112

    private var rgbFrameBitmap: Bitmap? = null
    private var faceBmp: Bitmap? = null
    protected var previewWidth = 0
    protected var previewHeight = 0
    private var portraitBmp: Bitmap? = null

    fun faceDetectorSetUp(){
        try {
            detector = TFLiteObjectDetectionAPIModel.create(
                    mContext.getAssets(),
                    TF_OD_API_MODEL_FILE,
                    TF_OD_API_LABELS_FILE,
                    TF_OD_API_INPUT_SIZE,
                    TF_OD_API_IS_QUANTIZED)
            //cropSize = TF_OD_API_INPUT_SIZE;
        } catch (e: IOException) {
            e.printStackTrace()
            //LOGGER.e(e, "Exception initializing classifier!");
            val toast = Toast.makeText(mContext, "Classifier could not be initialized", Toast.LENGTH_SHORT)
            toast.show()
            //finish()
        }
        val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

        val detector = FaceDetection.getClient(options)

        faceDetector = detector
    }

    private fun registerFace(mBitmap: Bitmap?) {
        try {
            if (mBitmap == null) {
                //Toast.makeText(this, "No File", Toast.LENGTH_SHORT).show()
                return
            }
            //ivFace.setImageBitmap(mBitmap)
            previewWidth = mBitmap.width
            previewHeight = mBitmap.height
            rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
            portraitBmp = mBitmap
            val image = InputImage.fromBitmap(mBitmap, 0)
            faceBmp = Bitmap.createBitmap(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, Bitmap.Config.ARGB_8888)
            faceDetector?.process(image)?.addOnSuccessListener(OnSuccessListener<List<Face>> { faces ->
                if (faces.size == 0) {
                    return@OnSuccessListener
                }
                Handler().post {
                    object : Thread() {
                        override fun run() {
                            //action
                            onFacesDetected(1, faces, true) //no need to add currtime
                        }
                    }.start()
                }
            })



        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun createTransform(srcWidth: Int, srcHeight: Int, dstWidth: Int, dstHeight: Int, applyRotation: Int): Matrix? {
        val matrix = Matrix()
        if (applyRotation != 0) {
            if (applyRotation % 90 != 0) {
                // LOGGER.w("Rotation of %d % 90 != 0", applyRotation);
            }

            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f)

            // Rotate around origin.
            matrix.postRotate(applyRotation.toFloat())
        }

//        // Account for the already applied rotation, if any, and then determine how
//        // much scaling is needed for each axis.
//        final boolean transpose = (Math.abs(applyRotation) + 90) % 180 == 0;
//        final int inWidth = transpose ? srcHeight : srcWidth;
//        final int inHeight = transpose ? srcWidth : srcHeight;
        if (applyRotation != 0) {

            // Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f)
        }
        return matrix
    }

    fun onFacesDetected(currTimestamp: Long, faces: List<Face>, add: Boolean) {
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.0f
        val mappedRecognitions: MutableList<Recognition> = LinkedList()


        //final List<Classifier.Recognition> results = new ArrayList<>();

        // Note this can be done only once
        val sourceW = rgbFrameBitmap!!.width
        val sourceH = rgbFrameBitmap!!.height
        val targetW = portraitBmp!!.width
        val targetH = portraitBmp!!.height
        val transform = createTransform(
                sourceW,
                sourceH,
                targetW,
                targetH,
                90)
        val mutableBitmap = portraitBmp!!.copy(Bitmap.Config.ARGB_8888, true)
        val cv = Canvas(mutableBitmap)

        // draws the original image in portrait mode.
        cv.drawBitmap(rgbFrameBitmap!!, transform!!, null)
        val cvFace = Canvas(faceBmp!!)
        val saved = false
        for (face in faces) {
            //results = detector.recognizeImage(croppedBitmap);
            val boundingBox = RectF(face.boundingBox)

            //final boolean goodConfidence = result.getConfidence() >= minimumConfidence;
            val goodConfidence = true //face.get;
            if (boundingBox != null && goodConfidence) {

                // maps crop coordinates to original
                cropToFrameTransform?.mapRect(boundingBox)

                // maps original coordinates to portrait coordinates
                val faceBB = RectF(boundingBox)
                transform.mapRect(faceBB)

                // translates portrait to origin and scales to fit input inference size
                //cv.drawRect(faceBB, paint);
                val sx = TF_OD_API_INPUT_SIZE.toFloat() / faceBB.width()
                val sy = TF_OD_API_INPUT_SIZE.toFloat() / faceBB.height()
                val matrix = Matrix()
                matrix.postTranslate(-faceBB.left, -faceBB.top)
                matrix.postScale(sx, sy)
                cvFace.drawBitmap(portraitBmp!!, matrix, null)

                //canvas.drawRect(faceBB, paint);
                var label = ""
                var confidence = -1f
                var color = Color.BLUE
                var extra: Any? = null
                var crop: Bitmap? = null
                if (add) {
                    try {
                        crop = Bitmap.createBitmap(portraitBmp!!,
                                faceBB.left.toInt(),
                                faceBB.top.toInt(),
                                faceBB.width().toInt(),
                                faceBB.height().toInt())
                    } catch (eon: java.lang.Exception) {
                        //runOnUiThread(Runnable { Toast.makeText(mContext, "Failed to detect", Toast.LENGTH_LONG) })
                    }
                }
                val startTime = SystemClock.uptimeMillis()
                val resultsAux = FaceStartActivity.detector.recognizeImage(faceBmp, add)
                val lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime
                if (resultsAux.size > 0) {
                    val result = resultsAux[0]
                    extra = result.extra
                    //          Object extra = result.getExtra();
//          if (extra != null) {
//            LOGGER.i("embeeding retrieved " + extra.toString());
//          }
                    val conf = result.distance
                    if (conf < 1.0f) {
                        confidence = conf
                        label = result.title
                        color = if (result.id == "0") {
                            Color.GREEN
                        } else {
                            Color.RED
                        }
                    }
                }
                val flip = Matrix()
                flip.postScale(1f, -1f, previewWidth / 2.0f, previewHeight / 2.0f)

                //flip.postScale(1, -1, targetW / 2.0f, targetH / 2.0f);
                flip.mapRect(boundingBox)
                val result = Recognition(
                        "0", label, confidence, boundingBox)
                result.color = color
                result.location = boundingBox
                result.extra = extra
                result.crop = crop
                mappedRecognitions.add(result)
            }
        }

        //    if (saved) {
//      lastSaved = System.currentTimeMillis();
//    }

        Log.e("xc", "startabc" )
        val rec = mappedRecognitions[0]
        FaceStartActivity.detector.register("", rec)
        val intent = Intent(mContext, DetectorActivity::class.java)
        startActivityForResult(intent, 171)
//        startActivity(new Intent(this,DetectorActivity.class));
//        finish();

        // detector.register("Sakil", rec);
        /*   runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ivFace.setImageBitmap(rec.getCrop());
                //showAddFaceDialog(rec);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.image_edit_dialog, null);
                ImageView ivFace = dialogLayout.findViewById(R.id.dlg_image);
                TextView tvTitle = dialogLayout.findViewById(R.id.dlg_title);
                EditText etName = dialogLayout.findViewById(R.id.dlg_input);

                tvTitle.setText("Register Your Face");
                ivFace.setImageBitmap(rec.getCrop());
                etName.setHint("Please tell your name");
                detector.register("sam", rec); //for register a face

                //button.setPressed(true);
                //button.performClick();
            }

        });*/

        // updateResults(currTimestamp, mappedRecognitions);
    }

    inner class GetImageFromUrl : AsyncTask<String?, Void?, Bitmap?>() {
        fun GetImageFromUrl() {
            //this.imageView = img;
        }
        override fun doInBackground(vararg url: String?): Bitmap {
            var bitmappppx: Bitmap? = null
            val stringUrl = url[0]
            bitmappppx = null
            val inputStream: InputStream
            try {
                inputStream = URL(stringUrl).openStream()
                bitmappppx = BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmappppx!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            registerFace(result)
        }

    }


    private fun isLocationServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = mContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    fun isMonitorServiceRunning(): Boolean {
        val activityManager = mContext.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            val servicesList = activityManager.getRunningServices(Int.MAX_VALUE)
            for (serviceInfo in servicesList) {
                if (MonitorService::class.java.getName() == serviceInfo.service.className) {
                    //if (serviceInfo.foreground) {
                        return true
                    //}
                }
            }
            return false
        }
        return false
    }

    fun isGeofenceServiceRunning(): Boolean {
        val activityManager =  mContext.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            val servicesList = activityManager.getRunningServices(Int.MAX_VALUE)
            for (serviceInfo in servicesList) {
                if (GeofenceService::class.java.getName() == serviceInfo.service.className) {
                    //if (serviceInfo.foreground) {
                        return true
                    //}
                }
            }
            return false
        }
        return false
    }

    private fun getBeatListApi(isFromRefresh: Boolean) {
        if (!isFromRefresh && !AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.beatList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as BeatListResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {
                        val list = response.beat_list

                        if (list != null && list.isNotEmpty()) {

                            if (isFromRefresh)
                                AppDatabase.getDBInstance()?.beatDao()?.delete()

                            doAsync {

                                list.forEach {
                                    val beat = BeatEntity()
                                    AppDatabase.getDBInstance()?.beatDao()?.insert(beat.apply {
                                        beat_id = it.id
                                        name = it.name
                                    })
                                }

                                uiThread {
                                    progress_wheel.stopSpinning()
                                    if (!isFromRefresh)
                                        showBeatListDialog(AppDatabase.getDBInstance()?.beatDao()?.getAll() as ArrayList<BeatEntity>)
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            if (!isFromRefresh)
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                        }
                    } else if (response.status == NetworkConstant.NO_DATA) {
                        progress_wheel.stopSpinning()
                        if (!isFromRefresh)
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)
                    } else {
                        progress_wheel.stopSpinning()
                        if (!isFromRefresh)
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    if (!isFromRefresh)
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    else
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                })
        )
    }
    fun openDialogPopup(header:String,text:String){
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_ok_imei)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_header) as AppCustomTextView
        val dialogBody = simpleDialog.findViewById(R.id.dialog_yes_body) as AppCustomTextView
        dialogHeader.text = header
        dialogBody.text = text
        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
        })
        simpleDialog.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap?.uiSettings?.isZoomControlsEnabled = true

        if (!TextUtils.isEmpty(Pref.current_latitude) && !TextUtils.isEmpty(Pref.current_longitude)) {
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Pref.current_latitude.toDouble(),
                Pref.current_longitude.toDouble()), 15f))

            val latLng = LatLng(Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
            val markerOptions = MarkerOptions()

            markerOptions.also {
                it.position(latLng)
                /*it.title(locationName)
                it.snippet(locationName)*/
                it.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                mGoogleMap?.addMarker(it)!!
            }

            tv_address.text = LocationWizard.getLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
        }

    }

}
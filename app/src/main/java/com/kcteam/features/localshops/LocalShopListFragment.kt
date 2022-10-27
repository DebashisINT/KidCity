package com.kcteam.features.localshops

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.elvishew.xlog.XLog
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialogsinglebtn.AddFeedbackSingleBtnDialog
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard.Companion.NEARBY_RADIUS
import com.kcteam.features.location.SingleShotLocationProvider
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by riddhi on 2/1/18.
 */
class LocalShopListFragment : BaseFragment(), View.OnClickListener {


    private var localShopsListAdapter: LocalShopsListAdapter?= null
    private lateinit var nearByShopsList: RecyclerView
    private lateinit var mContext: Context
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var noShopAvailable: AppCompatTextView
    private var list: MutableList<AddShopDBModelEntity> = ArrayList()
    private lateinit var floating_fab: FloatingActionMenu
    private lateinit var programFab1: FloatingActionButton
    private lateinit var programFab2: FloatingActionButton
    private lateinit var programFab3: FloatingActionButton
    private lateinit var shop_list_parent_rl: RelativeLayout
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel

    private lateinit var getFloatingVal: ArrayList<String>
    private val preid: Int = 100
    private var isGetLocation = -1
    private lateinit var geofenceTv: AppCompatTextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_nearby_shops, container, false)
        initView(view)

        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    (list as ArrayList<AddShopDBModelEntity>)?.let {
                        localShopsListAdapter?.refreshList(it)
                        //tv_cust_no.text = "Total customer(s): " + it.size
                    }
                } else {
                    localShopsListAdapter?.filter?.filter(query)
                }
            }
        })
        return view
    }

    override fun updateUI(any: Any) {
        super.updateUI(any)

        nearByShopsList.visibility = View.GONE
        isGetLocation = -1

        fetchNearbyShops()
    }


    private fun initView(view: View) {
        getFloatingVal = ArrayList<String>()
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        nearByShopsList = view.findViewById(R.id.near_by_shops_RCV)
        noShopAvailable = view.findViewById(R.id.no_shop_tv)
        shop_list_parent_rl = view.findViewById(R.id.shop_list_parent_rl)
        geofenceTv = view.findViewById(R.id.tv_geofence_relax)

        if(Pref.IsRestrictNearbyGeofence){
            geofenceTv.visibility = View.VISIBLE
            geofenceTv.text ="Geofence Relaxed :  " + Pref.GeofencingRelaxationinMeter + " mtr"
        }
        else{
            geofenceTv.visibility = View.GONE
        }

        shop_list_parent_rl.setOnClickListener { view ->
            floating_fab.close(true)
        }
        floating_fab = view.findViewById(R.id.floating_fab)
        floating_fab.menuIconView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_dashboard_filter_icon))
        floating_fab.menuButtonColorNormal = mContext.resources.getColor(R.color.colorAccent)
        floating_fab.menuButtonColorPressed = mContext.resources.getColor(R.color.colorPrimaryDark)
        floating_fab.menuButtonColorRipple = mContext.resources.getColor(R.color.colorPrimary)

        floating_fab.isIconAnimated = false
        floating_fab.setClosedOnTouchOutside(true)

        getFloatingVal.add("Alphabetically")
        getFloatingVal.add("Visit Date")
        getFloatingVal.add("Most Visited")
        floating_fab.visibility = View.GONE

        for (i in getFloatingVal.indices) {
            if (i == 0) {
                programFab1 = FloatingActionButton(activity)
                programFab1.buttonSize = FloatingActionButton.SIZE_MINI
                programFab1.id = preid + i
                programFab1.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                programFab1.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                programFab1.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                programFab1.labelText = getFloatingVal[0]
                floating_fab.addMenuButton(programFab1)
                programFab1.setOnClickListener(this)

            }
            if (i == 1) {
                programFab2 = FloatingActionButton(activity)
                programFab2.buttonSize = FloatingActionButton.SIZE_MINI
                programFab2.id = preid + i
                programFab2.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                programFab2.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                programFab2.labelText = getFloatingVal[1]
                floating_fab.addMenuButton(programFab2)
                programFab2.setOnClickListener(this)

            }

            if (i == 2) {
                programFab3 = FloatingActionButton(activity)
                programFab3.buttonSize = FloatingActionButton.SIZE_MINI
                programFab3.id = preid + i
                programFab3.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                programFab3.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                programFab3.labelText = getFloatingVal[2]
                floating_fab.addMenuButton(programFab3)
                programFab3.setOnClickListener(this)


            }
            //programFab1.setImageResource(R.drawable.ic_filter);
            if (i == 0) {
                programFab1.setImageResource(R.drawable.ic_tick_float_icon)
                programFab1.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
            } else if (i == 1)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
            else
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)

        }

        noShopAvailable.text = "No Registered " + Pref.shopText + " Available"

        if(Pref.IsnewleadtypeforRuby){
            initPermissionCheck()
        }

        fetchNearbyShops()
    }

    private var permissionUtils: PermissionUtils? = null
    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                /*if(SDK_INT >= 30){
                    if (!Environment.isExternalStorageManager()){
                        requestPermission()
                    }else{
                        callUSerListApi()
                    }
                }else{
                    callUSerListApi()
                }*/

                //callUSerListApi()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    @SuppressLint("WrongConstant")
    private fun initAdapter() {

        if (list != null && list.size > 0) {

            XLog.d("Local Shop List:== selected list size=====> " + list.size)

            val newList = ArrayList<AddShopDBModelEntity>()

            for (i in list.indices) {
                val userId = list[i].shop_id.substring(0, list[i].shop_id.indexOf("_"))
                if (userId == Pref.user_id)
                    newList.add(list[i])
            }

            XLog.d("Local Shop List:== new selected list size=====> " + newList.size)

            noShopAvailable.visibility = View.GONE
            nearByShopsList.visibility = View.VISIBLE

            try {

                localShopsListAdapter = LocalShopsListAdapter(mContext, list, object : LocalShopListClickListener {
                    override fun onQuationClick(shop: Any) {
                        (mContext as DashboardActivity).isBack = true
                        val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                        (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, nearbyShop.shop_id)
                    }

                    override fun onReturnClick(position: Int) {
                        (mContext as DashboardActivity).loadFragment(FragType.ViewAllReturnListFragment, true,list[position])
                    }

                    override fun onCallClick(shop: Any) {
                        val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                        IntentActionable.initiatePhoneCall(mContext, nearbyShop.ownerContactNumber)
                    }

                    override fun onOrderClick(shop: Any) {
                        val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                        if(Pref.IsActivateNewOrderScreenwithSize){
                            (mContext as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, true, nearbyShop.shop_id)

//                            (mContext as DashboardActivity).loadFragment(FragType.NewOrderScrActiFragment, true, nearbyShop)
                        }else{
                            (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, nearbyShop)
                        }


                    }

                    override fun onLocationClick(shop: Any) {
                        val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                        (mContext as DashboardActivity).openLocationMap(nearbyShop, false)
                    }

                    override fun visitShop(shop: Any) {
                        var list  = AppDatabase.getDBInstance()!!.shopActivityDao().getAll()
                        var shopType = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopType((shop as AddShopDBModelEntity).shop_id).toString()
                        if(list.size==0 && shopType.equals("16") && Pref.IsnewleadtypeforRuby){
                            Toaster.msgShort(mContext, "please wait,background data under snyc")
                        }else{
                            if (!Pref.isAddAttendence)
                                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                            else {
                                val nearbyShop: AddShopDBModelEntity = shop as AddShopDBModelEntity
                                (mContext as DashboardActivity).callShopVisitConfirmationDialog(nearbyShop.shopName, nearbyShop.shop_id)
                            }
                        }
                    }

                    override fun onHistoryClick(shop: Any) {
                        (mContext as DashboardActivity).loadFragment(FragType.ShopFeedbackHisFrag, true, shop)
                    }

                    override fun onDamageClick(shop_id: String) {
                        (mContext as DashboardActivity).loadFragment(FragType.ShopDamageProductListFrag, true, shop_id+"~"+Pref.user_id)
                    }

                    override fun onSurveyClick(shop_id: String) {
                        if(Pref.isAddAttendence){
                            (mContext as DashboardActivity).loadFragment(FragType.SurveyViewFrag, true, shop_id)
                        }else{
                            (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                        }
                    }
                }, {
                    it
                })

                (mContext as DashboardActivity).nearbyShopList = list

                layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
                nearByShopsList.layoutManager = layoutManager
                nearByShopsList.adapter = localShopsListAdapter
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            XLog.d("=====empty selected list (Local Shop List)=======")

            noShopAvailable.visibility = View.VISIBLE
            nearByShopsList.visibility = View.GONE
        }

        progress_wheel.stopSpinning()

    }


    private fun fetchNearbyShops() {

        /*if (!TextUtils.isEmpty(Pref.latitude) && !TextUtils.isEmpty(Pref.longitude)) {
            val location = Location("")
            location.longitude = Pref.latitude?.toDouble()!!
            location.latitude = Pref.longitude?.toDouble()!!
            getNearyShopList(location)
        }
        else {
            XLog.d("====================null location (Local Shop List)===================")

            progress_wheel.spin()
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
                            if (location.accuracy > 50) {
                                (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                                progress_wheel.stopSpinning()
                            } else
                                getNearyShopList(location)
                        }

                    })
        }*/

        if (Pref.isOnLeave.equals("true", ignoreCase = true)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_you_are_in_leave))
            return
        }


        if (AppUtils.mLocation != null) {
            if (AppUtils.mLocation!!.accuracy <= Pref.gpsAccuracy.toInt())
                getNearyShopList(AppUtils.mLocation!!)
            else {
                XLog.d("=====Inaccurate current location (Local Shop List)=====")
                singleLocation()
            }
        } else {
            XLog.d("=====null location (Local Shop List)======")
            singleLocation()
        }
    }

    private fun singleLocation() {
        progress_wheel.spin()
        SingleShotLocationProvider.requestSingleUpdate(mContext,
                object : SingleShotLocationProvider.LocationCallback {
                    override fun onStatusChanged(status: String) {

                    }

                    override fun onProviderEnabled(status: String) {

                    }

                    override fun onProviderDisabled(status: String) {

                    }

                    override fun onNewLocationAvailable(location: Location) {
                        if (isGetLocation == -1) {
                            isGetLocation = 0
                            if (location.accuracy > Pref.gpsAccuracy.toInt()) {
                                (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                                progress_wheel.stopSpinning()
                            } else
                                getNearyShopList(location)
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


    fun getNearyShopList(location: Location) {

        list.clear()
        //val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().all
        val allShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getAllOwn(true)

        val newList = java.util.ArrayList<AddShopDBModelEntity>()

        for (i in allShopList.indices) {
            /*val userId = allShopList[i].shop_id.substring(0, allShopList[i].shop_id.indexOf("_"))
            if (userId == Pref.user_id)*/
                newList.add(allShopList[i])
        }



        if (newList != null && newList.size > 0) {
            XLog.d("Local Shop List: all shop list size======> " + newList.size)
            XLog.d("======Local Shop List======")
            for (i in 0 until newList.size) {
                val shopLat: Double = newList[i].shopLat
                val shopLong: Double = newList[i].shopLong

                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong

                    /*XLog.d("shop_id====> " + allShopList[i].shop_id)
                    XLog.d("shopName====> " + allShopList[i].shopName)
                    XLog.d("shopLat====> $shopLat")
                    XLog.d("shopLong====> $shopLong")
                    XLog.d("lat=====> " + location.latitude)
                    XLog.d("long=====> " + location.longitude)
                    XLog.d("NEARBY_RADIUS====> $NEARBY_RADIUS")*/

                    var mRadious:Int = NEARBY_RADIUS
                    if(Pref.IsRestrictNearbyGeofence){
                        mRadious = Pref.GeofencingRelaxationinMeter
//                        mRadious=9999000
                    }
                    //val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, NEARBY_RADIUS)
                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, mRadious)
                    if (isShopNearby) {
                        XLog.d("shop_id====> " + newList[i].shop_id+ " shopName====> " + newList[i].shopName)
                        XLog.d("shopLat====> $shopLat"+" shopLong====> $shopLong")
                        XLog.d("lat=====> " + location.latitude+" long=====> " + location.longitude)
                        XLog.d("NEARBY_RADIUS====> $NEARBY_RADIUS")
                        XLog.d("=====" + newList[i].shopName + " is nearby=====")
                        newList[i].visited = !shoulIBotherToUpdate(newList[i].shop_id)
                        list.add(newList[i])
                    } else {
                        // XLog.d("=============" + allShopList[i].shopName + " is NOT nearby===============")
                    }

                } else {
                    XLog.d("shop_id====> " + newList[i].shop_id+ " shopName===> " + newList[i].shopName)

                    if (shopLat != null)
                        XLog.d("shopLat===> $shopLat")
                    else
                        XLog.d("shopLat===> null")

                    if (shopLong != null)
                        XLog.d("shopLong====> $shopLong")
                    else
                        XLog.d("shopLong====> null")
                }
            }
            XLog.d("=============================================")

        } else {
            XLog.d("====empty shop list (Local Shop List)======")
        }

        initAdapter()
    }

    fun shoulIBotherToUpdate(shopId: String): Boolean {
        return !AppDatabase.getDBInstance()!!.shopActivityDao().isShopActivityAvailable(shopId, AppUtils.getCurrentDateForShopActi())
    }
}
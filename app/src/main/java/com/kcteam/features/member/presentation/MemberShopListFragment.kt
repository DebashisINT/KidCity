package com.kcteam.features.member.presentation

import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.CustomStatic
import com.elvishew.xlog.XLog
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.SingleShotLocationProvider
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamShopListDataModel
import com.kcteam.features.member.model.TeamShopListResponseModel
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

/**
 * Created by Saikat on 31-01-2020.
 */
class MemberShopListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var rv_team_shop_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_member_list_shop_main: RelativeLayout
    private lateinit var floating_fab: FloatingActionMenu
    private lateinit var tv_team_struct: AppCustomTextView
    private lateinit var tv_shop_count: AppCustomTextView

    private lateinit var programFab1: FloatingActionButton
    private lateinit var programFab2: FloatingActionButton
    private lateinit var programFab3: FloatingActionButton
    private lateinit var programFab4: FloatingActionButton
    private lateinit var programFab5: FloatingActionButton

    private lateinit var rb_total_shop: AppCompatRadioButton
    private lateinit var rb_visit_shop: AppCompatRadioButton

    private lateinit var getFloatingVal: ArrayList<String>

    private var userId = ""
    private var isGetLocation = -1
    private val preid: Int = 100
    private var mShopType = ""
    private var isVisitShop = false

    private var shopList: ArrayList<TeamShopListDataModel>? = null
    private var totalShopList: ArrayList<TeamShopListDataModel>? = null
    private var adapter: MemberShopListAdapter? = null

    companion object {

        fun newInstance(userId: Any): MemberShopListFragment {
            val fragment = MemberShopListFragment()

            if (userId is String) {
                val bundle = Bundle()
                bundle.putString("user_id", userId)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        userId = arguments?.getString("user_id")?.toString()!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_team_shop_list, container, false)

        initView(view)
        getTeamShopList()

        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    adapter?.refreshList(shopList!!)
                    tv_shop_count.text = "Total " + Pref.shopText + "(s): " + shopList?.size
                } else {
                    adapter?.filter?.filter(query)
                }
            }

        })

        return view
    }

    private fun initView(view: View) {
        floating_fab = view.findViewById(R.id.floating_fab)
        rv_team_shop_list = view.findViewById(R.id.rv_team_shop_list)
        rv_team_shop_list.layoutManager = LinearLayoutManager(mContext)

        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        tv_team_struct = view.findViewById(R.id.tv_team_struct)
        tv_shop_count = view.findViewById(R.id.tv_shop_count)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        rl_member_list_shop_main = view.findViewById(R.id.rl_member_list_shop_main)
        rb_total_shop = view.findViewById(R.id.rb_total_shop)
        rb_visit_shop = view.findViewById(R.id.rb_visit_shop)
        rl_member_list_shop_main.setOnClickListener(null)

        tv_team_struct.apply {
            (mContext as DashboardActivity).teamHierarchy.takeIf { it.isNotEmpty() }?.let {
                visibility = View.VISIBLE

                it.forEachIndexed { i, name ->
                    text = if (i == 0)
                        name
                    else
                        text.toString().trim() + "-> " + name

                }

            } ?: let {
                visibility = View.GONE
            }
        }

        floating_fab.apply {
            menuIconView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_dashboard_filter_icon))
            menuButtonColorNormal = mContext.resources.getColor(R.color.colorAccent)
            menuButtonColorPressed = mContext.resources.getColor(R.color.colorPrimaryDark)
            menuButtonColorRipple = mContext.resources.getColor(R.color.colorPrimary)

            isIconAnimated = false
            setClosedOnTouchOutside(true)
        }

        /*floating_fab.menuIconView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_dashboard_filter_icon))
        floating_fab.menuButtonColorNormal = mContext.resources.getColor(R.color.colorAccent)
        floating_fab.menuButtonColorPressed = mContext.resources.getColor(R.color.colorPrimaryDark)
        floating_fab.menuButtonColorRipple = mContext.resources.getColor(R.color.colorPrimary)

        floating_fab.isIconAnimated = false
        floating_fab.setClosedOnTouchOutside(true)*/

        getFloatingVal = ArrayList<String>()

        getFloatingVal.apply {
            add(getString(R.string.shop_type))
            add(getString(R.string.distributor_type))
            add(getString(R.string.pp_type))
            add(getString(R.string.new_party_type))
            add(getString(R.string.diamond_type))
        }.forEachIndexed { i, item ->
            if (i == 0) {
                programFab1 = FloatingActionButton(activity)
                programFab1.also {
                    it.buttonSize = FloatingActionButton.SIZE_MINI
                    it.id = preid + i
                    it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                    it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    it.labelText = item
                    floating_fab.addMenuButton(it)
                    it.setOnClickListener(this)
                }
            }

            if (i == 1) {
                programFab2 = FloatingActionButton(activity)
                programFab2.also {
                    it.buttonSize = FloatingActionButton.SIZE_MINI
                    it.id = preid + i
                    it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                    it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    it.labelText = item
                    floating_fab.addMenuButton(it)
                    it.setOnClickListener(this)
                }
            }

            if (i == 2) {
                programFab3 = FloatingActionButton(activity)
                programFab3.also {
                    it.buttonSize = FloatingActionButton.SIZE_MINI
                    it.id = preid + i
                    it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                    it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    it.labelText = item
                    floating_fab.addMenuButton(it)
                    it.setOnClickListener(this)
                }
            }

            if (i == 3) {
                programFab4 = FloatingActionButton(activity)
                programFab4.also {
                    it.buttonSize = FloatingActionButton.SIZE_MINI
                    it.id = preid + i
                    it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                    it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    it.labelText = item
                    floating_fab.addMenuButton(it)
                    it.setOnClickListener(this)
                }
            }

            if (i == 4) {
                programFab5 = FloatingActionButton(activity)
                programFab5.also {
                    it.buttonSize = FloatingActionButton.SIZE_MINI
                    it.id = preid + i
                    it.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                    it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    it.labelText = item
                    floating_fab.addMenuButton(it)
                    it.setOnClickListener(this)
                }
            }

            when (i) {
                0 -> {
                    programFab1?.let {
                        it.setImageResource(R.drawable.ic_tick_float_icon)
                        it.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                    }
                }
                1 -> programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
                2 -> programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)
                3 -> programFab4.setImageResource(R.drawable.ic_tick_float_icon_gray)
                4 -> programFab5.setImageResource(R.drawable.ic_tick_float_icon_gray)
            }
        }

        rb_total_shop.setOnClickListener(this)
        rb_visit_shop.setOnClickListener(this)

        tv_shop_count.text = "Total " + Pref.shopText + "(s): 0"
        tv_no_data_available.text = "No " + Pref.shopText + " Available"
    }

    private fun getTeamShopList() {

        if (!AppUtils.isOnline(mContext)) {
            tv_no_data_available.visibility = View.VISIBLE
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.teamShopList(userId, (mContext as DashboardActivity).areaId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamShopListResponseModel
                            XLog.d("GET TEAM SHOP DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {

                                response.team_struct?.let {
                                    tv_team_struct.text = it
                                }

                                if (response.shop_list != null && response.shop_list!!.size > 0) {
                                    totalShopList = response.shop_list
                                    fetchNearbyShops(response.shop_list!!)
                                } else {
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                tv_no_data_available.visibility = View.VISIBLE
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET TEAM SHOP DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            tv_no_data_available.visibility = View.VISIBLE
                        })
        )
    }


    private fun fetchNearbyShops(shop_list: ArrayList<TeamShopListDataModel>) {
        if (AppUtils.mLocation != null) {
            if (AppUtils.mLocation!!.accuracy <= Pref.gpsAccuracy.toInt())
                getNearbyShopList(AppUtils.mLocation!!, shop_list)
            else {
                XLog.d("=====Inaccurate current location (Team Shop List)=====")
                singleLocation(shop_list)
            }
        } else {
            XLog.d("=====null location (Team Shop List)======")
            singleLocation(shop_list)
        }
    }

    private fun getNearbyShopList(location: Location, allShopList: ArrayList<TeamShopListDataModel>) {

        val newShopList = ArrayList<TeamShopListDataModel>()

        allShopList?.takeIf { it.size > 0 }?.let {

            XLog.d("Local Shop List: all shop list size======> " + allShopList.size)
            XLog.d("======Local Shop List======")

            it/*.filter { teamShopListDataModel ->
                teamShopListDataModel.shop_lat.toDouble() != null && teamShopListDataModel.shop_long.toDouble() != null
            }*/.forEach { teamShop ->
                val shopLat = teamShop.shop_lat.toDouble()
                val shopLong = teamShop.shop_long.toDouble()

                if (shopLat != null && shopLong != null) {

                    val shopLocation = Location("")
                    shopLocation.let {
                        it.latitude = shopLat
                        it.longitude = shopLong
                        FTStorageUtils.checkShopPositionWithinRadious(location, it, LocationWizard.NEARBY_RADIUS)
                    }.takeIf { it }?.apply {
                        XLog.d("shop_id====> " + teamShop.shop_id)
                        XLog.d("shopName====> " + teamShop.shop_name)
                        XLog.d("shopLat====> $shopLat")
                        XLog.d("shopLong====> $shopLong")
                        XLog.d("lat=====> " + location.latitude)
                        XLog.d("long=====> " + location.longitude)
                        XLog.d("NEARBY_RADIUS====> ${LocationWizard.NEARBY_RADIUS}")
                        XLog.d("=====" + teamShop.shop_name + " is nearby=====")
                        newShopList.add(teamShop)
                    }
                } else {
                    XLog.d("shop_id====> " + teamShop.shop_id)
                    XLog.d("shopName===> " + teamShop.shop_name)

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

        } ?: let {
            XLog.d("====empty shop list (Local Shop List)======")
        }


        /*if (allShopList != null && allShopList.size > 0) {
            XLog.d("Local Shop List: all shop list size======> " + allShopList.size)
            XLog.d("======Local Shop List======")
            for (i in 0 until allShopList.size) {
                val shopLat: Double = allShopList[i].shop_lat.toDouble()
                val shopLong: Double = allShopList[i].shop_long.toDouble()

                if (shopLat != null && shopLong != null) {
                    val shopLocation = Location("")
                    shopLocation.latitude = shopLat
                    shopLocation.longitude = shopLong

                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(location, shopLocation, LocationWizard.NEARBY_RADIUS)
                    if (isShopNearby) {
                        XLog.d("shop_id====> " + allShopList[i].shop_id)
                        XLog.d("shopName====> " + allShopList[i].shop_name)
                        XLog.d("shopLat====> $shopLat")
                        XLog.d("shopLong====> $shopLong")
                        XLog.d("lat=====> " + location.latitude)
                        XLog.d("long=====> " + location.longitude)
                        XLog.d("NEARBY_RADIUS====> ${LocationWizard.NEARBY_RADIUS}")
                        XLog.d("=====" + allShopList[i].shop_name + " is nearby=====")
                        newShopList.add(allShopList[i])
                    }

                } else {
                    XLog.d("shop_id====> " + allShopList[i].shop_id)
                    XLog.d("shopName===> " + allShopList[i].shop_name)

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
        }*/
        shopList = newShopList
        //initAdapter(newShopList)
        findSpecificTypeList("1")
    }

    private fun singleLocation(shop_list: ArrayList<TeamShopListDataModel>) {
        progress_wheel.spin()
        SingleShotLocationProvider.requestSingleUpdate(mContext, object : SingleShotLocationProvider.LocationCallback {
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
                    } else {
                        progress_wheel.stopSpinning()
                        getNearbyShopList(location, shop_list)
                    }
                }
            }
        })

        /*val t = Timer()
        t.schedule(object : TimerTask() {
            override fun run() {
                try {
                    if (isGetLocation == -1) {
                        isGetLocation = 1
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 15000)*/

        Timer().schedule(15000) {
            try {
                if (isGetLocation == -1) {
                    isGetLocation = 1
                    progress_wheel.stopSpinning()
                    (mContext as DashboardActivity).showSnackMessage("Unable to fetch accurate GPS data. Please try again.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initAdapter(shop_list: ArrayList<TeamShopListDataModel>) {
        tv_no_data_available.visibility = View.GONE
        /*rv_team_shop_list.adapter = MemberShopListAdapter(mContext, shop_list, object : MemberShopListAdapter.OnClickListener {
            override fun onVisitClick(teamShop: TeamShopListDataModel) {
                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else {
                    (mContext as DashboardActivity).callDialog(teamShop)
                }
            }
        })*/

        tv_shop_count.text = "Total " + Pref.shopText + "(s): " + shop_list.size
        adapter = MemberShopListAdapter(mContext, shop_list, isVisitShop, { teamShop: TeamShopListDataModel ->
            if (!Pref.isAddAttendence)
                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
            else {
                (mContext as DashboardActivity).callDialog(teamShop)
            }
        }, { teamShop: TeamShopListDataModel ->
            if (!Pref.isAddAttendence)
                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
            else {
                val addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(teamShop.shop_id)
                if(Pref.IsActivateNewOrderScreenwithSize){//13-09-2021
                    (context as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, true, addShopData!!.shop_id)
                }else {
                    (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, addShopData)
                }
            }
        }, { size: Int ->
            tv_shop_count.text = "Total " + Pref.shopText + "(s): " + size
        },
            { teamShop: TeamShopListDataModel ->
                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else if(Pref.IsAllowBreakageTrackingunderTeam) {
                    //CustomStatic.IsBreakageViewFromTeam = true
                    teamShop.user_id = userId
                    (mContext as DashboardActivity).loadFragment(FragType.ShopDamageProductListFrag, true, teamShop)
                }

            },
            { teamShop: TeamShopListDataModel ->
            if (!Pref.isAddAttendence)
                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
            else {
                val addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(teamShop.shop_id)
                (mContext as DashboardActivity).isBack = true
                (mContext as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, addShopData.shop_id)
            }
        },{
            if(Pref.IsFeedbackHistoryActivated){
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                }
                else{
                    var tt= CustomStatic.ShopFeedBachHisUserId
                    (mContext as DashboardActivity).loadFragment(FragType.ShopFeedbackHisFrag, true, it)
                }
            }
        })

        rv_team_shop_list.adapter = adapter
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            100 -> {
                findSpecificTypeList("1")
                floating_fab.close(true)
                mContext.resources.apply {
                    programFab1.colorNormal = getColor(R.color.delivery_status_green)
                    programFab2.colorNormal = getColor(R.color.colorAccent)
                    programFab3.colorNormal = getColor(R.color.colorAccent)
                    programFab4.colorNormal = getColor(R.color.colorAccent)
                    programFab5.colorNormal = getColor(R.color.colorAccent)
                }
                programFab1.setImageResource(R.drawable.ic_tick_float_icon)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab4.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5.setImageResource(R.drawable.ic_tick_float_icon_gray)

            }
            101 -> {
                findSpecificTypeList("4")
                floating_fab.close(true)
                mContext.resources.apply {
                    programFab1.colorNormal = getColor(R.color.colorAccent)
                    programFab2.colorNormal = getColor(R.color.delivery_status_green)
                    programFab3.colorNormal = getColor(R.color.colorAccent)
                    programFab4.colorNormal = getColor(R.color.colorAccent)
                    programFab5.colorNormal = getColor(R.color.colorAccent)
                }
                programFab1.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab4.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5.setImageResource(R.drawable.ic_tick_float_icon_gray)
            }
            102 -> {
                findSpecificTypeList("2")
                floating_fab.close(true)
                mContext.resources.apply {
                    programFab1.colorNormal = getColor(R.color.colorAccent)
                    programFab2.colorNormal = getColor(R.color.colorAccent)
                    programFab3.colorNormal = getColor(R.color.delivery_status_green)
                    programFab4.colorNormal = getColor(R.color.colorAccent)
                    programFab5.colorNormal = getColor(R.color.colorAccent)
                }
                programFab1.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon)
                programFab4.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5.setImageResource(R.drawable.ic_tick_float_icon_gray)
            }
            103 -> {
                findSpecificTypeList("3")
                floating_fab.close(true)
                mContext.resources.apply {
                    programFab1.colorNormal = getColor(R.color.colorAccent)
                    programFab2.colorNormal = getColor(R.color.colorAccent)
                    programFab3.colorNormal = getColor(R.color.colorAccent)
                    programFab4.colorNormal = getColor(R.color.delivery_status_green)
                    programFab5.colorNormal = getColor(R.color.colorAccent)
                }
                programFab1.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab4.setImageResource(R.drawable.ic_tick_float_icon)
                programFab5.setImageResource(R.drawable.ic_tick_float_icon_gray)

            }
            104 -> {
                findSpecificTypeList("5")
                floating_fab.close(true)
                mContext.resources.apply {
                    programFab1.colorNormal = getColor(R.color.colorAccent)
                    programFab2.colorNormal = getColor(R.color.colorAccent)
                    programFab3.colorNormal = getColor(R.color.colorAccent)
                    programFab4.colorNormal = getColor(R.color.colorAccent)
                    programFab5.colorNormal = getColor(R.color.delivery_status_green)
                }
                programFab1.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab4.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab5.setImageResource(R.drawable.ic_tick_float_icon)
            }

            R.id.rb_total_shop -> {
                isVisitShop = false
                findSpecificTypeList(mShopType)
            }

            R.id.rb_visit_shop -> {
                isVisitShop = true
                findSpecificTypeList(mShopType)
            }
        }
    }

    private fun findSpecificTypeList(shopType: String) {
        mShopType = shopType
        val newShopList = ArrayList<TeamShopListDataModel>()

        /*try {
            for (i in shopList?.indices!!) {
                if (shopList?.get(i)?.shop_type == shopType) {
                    newShopList.add(shopList?.get(i)!!)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initAdapter(newShopList)

        if (newShopList.size == 0)
            tv_no_data_available.visibility = View.VISIBLE*/


        /*if (newShopList.size > 0)
            initAdapter(newShopList)
        else
            tv_no_data_available.visibility = View.VISIBLE*/

        /*if (isVisitShop) {
            shopList?.filter { it.shop_type == shopType }?.let {
                initAdapter(it as ArrayList<TeamShopListDataModel>)

                if (it.isEmpty())
                    tv_no_data_available.visibility = View.VISIBLE
            }
        }
        else {
            totalShopList?.filter { it.shop_type == shopType }?.let {
                initAdapter(it as ArrayList<TeamShopListDataModel>)

                if (it.isEmpty())
                    tv_no_data_available.visibility = View.VISIBLE
            }
        }*/


        shopList?.let {
            initAdapter(it as ArrayList<TeamShopListDataModel>)

            if (it.isEmpty())
                tv_no_data_available.visibility = View.VISIBLE
        }
    }

    fun updateAdapter() {
        findSpecificTypeList(mShopType)
    }
}
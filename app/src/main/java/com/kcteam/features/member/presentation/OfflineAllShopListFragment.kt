package com.kcteam.features.member.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.MemberShopEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.presentation.AccuracyIssueDialog
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.SingleShotLocationProvider
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamShopListResponseModel
import com.kcteam.features.nearbyshops.api.updateaddress.ShopAddressUpdateRepoProvider
import com.kcteam.features.nearbyshops.model.updateaddress.AddressUpdateRequest
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 03-Jul-20.
 */
class OfflineAllShopListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_team_shop_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_member_list_shop_main: RelativeLayout
    private lateinit var tv_team_struct: AppCustomTextView
    private lateinit var tv_shop_count: AppCustomTextView
    private lateinit var tv_shop_path: AppCustomTextView
    private lateinit var tv_shop_loading: AppCustomTextView

    private var userId = ""
    private var shopId = ""
    private var shop_type = ""
    private var isBackPressed = false
    private var shop_list: ArrayList<MemberShopEntity>? = null
    private var adapter: OfflineAllShopAdapter? = null
    private var isAddressUpdating = false
    private var dialog: AccuracyIssueDialog? = null
    var isAddressUpdated = false

    val shopIdList: MutableList<String> by lazy {
        listOf<String>().toMutableList()
    }

    val shopNameList: MutableList<String> by lazy {
        listOf<String>().toMutableList()
    }

    companion object {

        fun newInstance(userId: Any): OfflineAllShopListFragment {
            val fragment = OfflineAllShopListFragment()

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

        isBackPressed = false

        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                try {
                    if (query.isBlank()) {
                        adapter?.refreshList(shop_list!!)
                        //initAdapter(shop_list!!)
                        val shopType = AppDatabase.getDBInstance()?.shopTypeDao()?.getSingleType(shop_list?.get(0)?.shop_type!!)
                        if (shopType != null && !TextUtils.isEmpty(shopType.shoptype_name)) {
                            tv_shop_count.text = "Total " + shopType.shoptype_name + "(s): " + shop_list?.size
                        } else
                            tv_shop_count.text = "Total " + Pref.shopText + "(s): " + shop_list?.size
                    } else {
                        adapter?.filter?.filter(query)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })


        return view
    }

    private fun initView(view: View) {
        rv_team_shop_list = view.findViewById(R.id.rv_team_shop_list)
        rv_team_shop_list.layoutManager = LinearLayoutManager(mContext)

        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        tv_team_struct = view.findViewById(R.id.tv_team_struct)
        tv_shop_count = view.findViewById(R.id.tv_shop_count)
        tv_shop_path = view.findViewById(R.id.tv_shop_path)
        tv_shop_loading = view.findViewById(R.id.tv_shop_loading)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        rl_member_list_shop_main = view.findViewById(R.id.rl_member_list_shop_main)
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

        tv_shop_count.text = "Total " + Pref.shopText + "(s): 0"
        tv_no_data_available.text = "No " + Pref.shopText + " Available"

        if (Pref.isOfflineShopSaved)
            getDataFromDb()
        else {
            tv_shop_loading.visibility = View.VISIBLE
            progress_wheel.spin()
        }
    }

    private fun getDataFromDb() {
        progress_wheel.spin()

        doAsync {

            val list: List<MemberShopEntity> = if (TextUtils.isEmpty((mContext as DashboardActivity).areaId))
                AppDatabase.getDBInstance()?.memberShopDao()?.getSingleUserShop(userId)!!
            else
                AppDatabase.getDBInstance()?.memberShopDao()?.getSingleUserShopAreaWise(userId, (mContext as DashboardActivity).areaId)!!

            uiThread {
                if (list != null && list.isNotEmpty()) {

                    if (TextUtils.isEmpty(shopId)) {
                        val shopList = ArrayList<MemberShopEntity>()

                        list.forEach {
                            if (it.shop_type == "2")
                                shopList.add(it)
                        }

                        list.forEach { it1 ->
                            if (it1.shop_type == "4") {
                                //doAsync {
                                    val shop = AppDatabase.getDBInstance()?.memberShopDao()?.getSingleShop(it1.assign_to_pp_id!!)

                                    //uiThread {
                                        if (shop == null)
                                            shopList.add(it1)
                                    //}
                                //}
                            }
                        }

                        if (shopList.isEmpty()) {
                            list.forEach {
                                if (it.shop_type == "4")
                                    shopList.add(it)
                            }
                        }

                        if (shopList.isEmpty()) {
                            list.forEach {
                                if (it.shop_type == "1")
                                    shopList.add(it)
                            }

                            list.forEach {
                                if (it.shop_type == "5")
                                    shopList.add(it)
                            }

                            list.forEach {
                                if (it.shop_type == "3")
                                    shopList.add(it)
                            }
                        }

                        shop_list = shopList

                    } else {
                        val shopList = ArrayList<MemberShopEntity>()

                        try {
                            if (!TextUtils.isEmpty(shop_type)) {
                                if (!isAddressUpdated) {
                                    if (/*shop_list?.get(0)?.*/shop_type == "2") {
                                        list.forEach {
                                            if (it.shop_type == "4" && it.assign_to_pp_id == shopId) {
                                                shopList.add(it)
                                            }
                                        }
                                    } else if (/*shop_list?.get(0)?.*/shop_type == "4") {
                                        list.forEach {
                                            if (it.shop_type == "1" && it.assign_to_dd_id == shopId)
                                                shopList.add(it)
                                        }

                                        list.forEach {
                                            if (it.shop_type == "5" && it.assign_to_dd_id == shopId)
                                                shopList.add(it)
                                        }

                                        list.forEach {
                                            if (it.shop_type == "3")
                                                shopList.add(it)
                                        }
                                    } else {
                                        list.forEach {
                                            if (it.shop_type == "4" && it.assign_to_pp_id == shopId) {
                                                shopList.add(it)
                                            }
                                        }
                                    }
                                } else {
                                    if (/*shop_list?.get(0)?.*/shop_type == "4") {
                                        list.forEach {
                                            if (it.shop_type == "4" && it.assign_to_pp_id == shopId) {
                                                shopList.add(it)
                                            }
                                        }
                                    } else {
                                        list.forEach {
                                            if (it.shop_type == "1" && it.assign_to_dd_id == shopId)
                                                shopList.add(it)
                                        }

                                        list.forEach {
                                            if (it.shop_type == "5" && it.assign_to_dd_id == shopId)
                                                shopList.add(it)
                                        }

                                        list.forEach {
                                            if (it.shop_type == "3")
                                                shopList.add(it)
                                        }
                                    }
                                }
                                shop_list = shopList
                            }
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    progress_wheel.stopSpinning()
                    initAdapter()
                    shop_type = ""

                } else {
                    rv_team_shop_list.visibility = View.GONE
                    progress_wheel.stopSpinning()
                    tv_no_data_available.visibility = View.VISIBLE

                    if (!isBackPressed && shopId.isNotEmpty())
                        shopIdList.add(shopId)
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun initAdapter() {
        if (shop_list != null && shop_list!!.isNotEmpty()) {
            tv_no_data_available.visibility = View.GONE
            rv_team_shop_list.visibility = View.VISIBLE

            if (!isBackPressed && shopId.isNotEmpty() && !isAddressUpdated)
                shopIdList.add(shopId)

            val shopType = AppDatabase.getDBInstance()?.shopTypeDao()?.getSingleType(shop_list?.get(0)?.shop_type!!)
            if (shopType != null && !TextUtils.isEmpty(shopType.shoptype_name)) {
                tv_shop_count.text = "Total " + shopType.shoptype_name + "(s): " + shop_list?.size
            } else
                tv_shop_count.text = "Total " + Pref.shopText + "(s): " + shop_list?.size

            if (!TextUtils.isEmpty(shopId)) {
                tv_shop_path.visibility = View.VISIBLE

                shopNameList.forEachIndexed { index, shopName ->
                    if (index == shopNameList.size - 1)
                        tv_shop_path.text = shopName
                    else
                        tv_shop_path.text = shopName + "-> "
                }
            } else
                tv_shop_path.visibility = View.GONE


            adapter = OfflineAllShopAdapter(mContext, shop_list!!, { teamShop: MemberShopEntity ->

                if (teamShop.shop_type != "1" && teamShop.shop_type != "5" && teamShop.shop_type != "3") {
                    isAddressUpdated = false
                    shopId = teamShop.shop_id!!
                    shop_type = teamShop.shop_type!!
                    shopNameList.add(teamShop.shop_name!!)
                    isBackPressed = false
                    getDataFromDb()
                }

            }, { teamShop: MemberShopEntity ->

                if (AppUtils.mLocation != null) {
                    if (AppUtils.mLocation!!.accuracy <= Pref.shopLocAccuracy.toFloat()) {
                        openAddressUpdateDialog(teamShop, AppUtils.mLocation!!)
                    } else {
                        XLog.d("======Saved current location is inaccurate (Offline Member Shop List)========")
                        getShopLatLong(teamShop)
                    }
                } else {
                    XLog.d("=====Saved current location is null (Offline Member Shop List)======")
                    getShopLatLong(teamShop)
                }

            }, { size: Int ->
                val shopType_ = AppDatabase.getDBInstance()?.shopTypeDao()?.getSingleType(shop_list?.get(0)?.shop_type!!)
                if (shopType_ != null && !TextUtils.isEmpty(shopType_.shoptype_name)) {
                    tv_shop_count.text = "Total " + shopType_.shoptype_name + "(s): " + size
                } else
                    tv_shop_count.text = "Total " + Pref.shopText + "(s): " + size

            }, { teamShop: MemberShopEntity ->
                callShopAddressUpdateApi(teamShop, true)
            })

            rv_team_shop_list.adapter = adapter
        } else {
            tv_shop_count.text = "Total " + Pref.shopText + "(s): 0"
            rv_team_shop_list.visibility = View.GONE
            tv_no_data_available.visibility = View.VISIBLE

            if (!isBackPressed && shopId.isNotEmpty())
                shopIdList.add(shopId)
        }
    }

    private fun getShopLatLong(teamShop: MemberShopEntity) {
        if (!isAddressUpdating) {

            isAddressUpdating = true
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
                            progress_wheel.stopSpinning()
                            isAddressUpdating = false
                            if (location.accuracy > Pref.shopLocAccuracy.toFloat()) {
                                if (dialog == null) {
                                    dialog = AccuracyIssueDialog()
                                    dialog?.show((mContext as DashboardActivity).supportFragmentManager, "AccuracyIssueDialog")
                                } else {
                                    dialog?.dismissAllowingStateLoss()
                                    dialog?.show((mContext as DashboardActivity).supportFragmentManager, "AccuracyIssueDialog")

                                }
                            } else {
                                openAddressUpdateDialog(teamShop, location)
                            }
                        }
                    })
        }
    }

    private fun openAddressUpdateDialog(teamShop: MemberShopEntity, location: Location) {
        try {
            UpdateMemberShopAddressDialog.getInstance(teamShop, location) { mTeamShop: Any ->
                /*if (AppUtils.isOnline(mContext)) {
                        if (mTeamShop is MemberShopEntity)
                            callShopAddressUpdateApi(mTeamShop)
                    }
                    else
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))*/

                if (mTeamShop is MemberShopEntity) {
                    isAddressUpdated = true
                    mTeamShop.isUploaded = false
                    AppDatabase.getDBInstance()?.memberShopDao()?.updateShop(mTeamShop)
                    callShopAddressUpdateApi(mTeamShop, false)
                }


            }.show((mContext as DashboardActivity).supportFragmentManager, "UpdateShopAddressDialog")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callShopAddressUpdateApi(team: MemberShopEntity, isFromSync: Boolean) {

        if (!AppUtils.isOnline(mContext)) {
            if (isFromSync)
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            else {
                (mContext as DashboardActivity).showSnackMessage("Address updated successfully")
                getDataFromDb()
            }
            return
        }

        val repository = ShopAddressUpdateRepoProvider.provideShopAddressUpdateRepo()
        progress_wheel.spin()

        val addressUpdateReq = AddressUpdateRequest()
        addressUpdateReq.apply {
            user_id = Pref.user_id
            shop_id = team.shop_id
            shop_lat = team.shop_lat
            shop_long = team.shop_long
            shop_address = team.shop_address
            isAddressUpdated = "1"
            pincode = team.shop_pincode
        }

        XLog.d("==============Sync Team Shop Input Params (Offline Shop)====================")
        XLog.d("shop id=======> " + addressUpdateReq.shop_id)
        XLog.d("user_id=======> " + addressUpdateReq.user_id)
        XLog.d("shop_lat=======> " + addressUpdateReq.shop_lat)
        XLog.d("shop_long=======> " + addressUpdateReq.shop_long)
        XLog.d("shop_address=======> " + addressUpdateReq.shop_address)
        XLog.d("shop_pincode=======> " + addressUpdateReq.pincode)
        XLog.d("isAddressUpdated=======> " + addressUpdateReq.isAddressUpdated)
        XLog.d("=============================================================================")


        BaseActivity.compositeDisposable.add(
                repository.getShopAddressUpdate(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                doAsync {
                                    team.isUploaded = true
                                    AppDatabase.getDBInstance()?.memberShopDao()?.updateIsUploaded(true, team.shop_id!!)

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        getDataFromDb()
                                    }
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                if (isFromSync)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else {
                                    (mContext as DashboardActivity).showSnackMessage("Address updated successfully")
                                    getDataFromDb()
                                }
                            }


                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            if (isFromSync)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else {
                                (mContext as DashboardActivity).showSnackMessage("Address updated successfully")
                                getDataFromDb()
                            }
                        })
        )

    }

    fun updateListOnBackPress() {
        isBackPressed = true

        shopIdList.also {
            it.removeAt(it.size - 1)
            shopId = if (it.size > 0)
                it[it.size - 1]
            else
                ""
        }

        shopNameList.also {
            it.removeAt(it.size - 1)
            /*if (it.size > 0)
                it[it.size - 1]
            else
                it[0]*/
        }

        getDataFromDb()
    }

    fun updateUi() {
        tv_shop_loading.visibility = View.GONE
        progress_wheel.stopSpinning()
        getDataFromDb()
    }

    fun refreshList() {
        progress_wheel.spin()

        doAsync {
            val list = AppDatabase.getDBInstance()?.memberShopDao()?.getAll()

            uiThread {
                progress_wheel.stopSpinning()

                if (list != null && list.isNotEmpty())
                    callMemberShopListApi(list[list.size - 1].date_time!!)
                else
                    callMemberShopListApi("")
            }
        }
    }

    private fun callMemberShopListApi(date: String) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()

        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.offlineTeamShopList(date)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamShopListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.shop_list != null && response.shop_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.shop_list?.forEach {
                                            val memberShop = MemberShopEntity()
                                            AppDatabase.getDBInstance()?.memberShopDao()?.insertAll(memberShop.apply {
                                                user_id = it.user_id
                                                shop_id = it.shop_id
                                                shop_name = it.shop_name
                                                shop_lat = it.shop_lat
                                                shop_long = it.shop_long
                                                shop_address = it.shop_address
                                                shop_pincode = it.shop_pincode
                                                shop_contact = it.shop_contact
                                                total_visited = it.total_visited
                                                last_visit_date = it.last_visit_date
                                                shop_type = it.shop_type
                                                dd_name = it.dd_name
                                                entity_code = it.entity_code
                                                model_id = it.model_id
                                                primary_app_id = it.primary_app_id
                                                secondary_app_id = it.secondary_app_id
                                                lead_id = it.lead_id
                                                funnel_stage_id = it.funnel_stage_id
                                                stage_id = it.stage_id
                                                booking_amount = it.booking_amount
                                                type_id = it.type_id
                                                area_id = it.area_id
                                                assign_to_pp_id = it.assign_to_pp_id
                                                assign_to_dd_id = it.assign_to_dd_id
                                                isUploaded = true
                                                date_time = AppUtils.getCurrentISODateTime()
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()

                                            if (TextUtils.isEmpty(date))
                                                getDataFromDb()
                                            else {
                                                if(TextUtils.isEmpty(shopId))
                                                    getDataFromDb()
                                            }
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }
}
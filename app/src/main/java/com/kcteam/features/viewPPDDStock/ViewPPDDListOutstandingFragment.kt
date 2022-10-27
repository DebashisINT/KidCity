package com.kcteam.features.viewPPDDStock

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.domain.*
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.NotificationUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.addshop.presentation.AccuracyIssueDialog
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.marketing.api.marketingcategorylist.MarketingCatListRepoProvider
import com.kcteam.features.marketing.model.MarketingCategoryListResponse
import com.kcteam.features.nearbyshops.api.ShopListRepositoryProvider
import com.kcteam.features.nearbyshops.api.updateaddress.ShopAddressUpdateRepoProvider
import com.kcteam.features.nearbyshops.model.ShopData
import com.kcteam.features.nearbyshops.model.ShopListResponse
import com.kcteam.features.nearbyshops.model.updateaddress.AddressUpdateRequest
import com.kcteam.features.viewPPDDStock.api.UpdateStockRepoProvider
import com.kcteam.features.viewPPDDStock.model.UpdateStockInputParamsModel
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Saikat on 17-12-2018.
 */
class ViewPPDDListOutstandingFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mViewPPDDListAdapter: ViewPPDDListAdapter
    private lateinit var nearByShopsList: RecyclerView
    private lateinit var iv_nearbyImage: ImageView
    private lateinit var mContext: Context
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var noShopAvailable: AppCompatTextView
    private lateinit var list: List<AddShopDBModelEntity>
    private lateinit var floating_fab: FloatingActionMenu
    private lateinit var programFab1: FloatingActionButton
    private lateinit var programFab2: FloatingActionButton
    private lateinit var programFab3: FloatingActionButton
    private lateinit var shop_list_parent_rl: RelativeLayout
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var rb_view_pp_list: RadioButton
    private lateinit var rb_view_dd_list: RadioButton

    private lateinit var getFloatingVal: ArrayList<String>
    private val preid: Int = 100
    private var isShopRegistrationInProcess = false
    private var dialog: AccuracyIssueDialog? = null
    private var isAddressUpdating = false
    private var isOutstanding = false

//    /*Interface to update Shoplist Frag on search event*/
//    private lateinit var searchListener:SearchListener
//    public fun setSearchListener(searchListener:SearchListener){
//        this.searchListener=searchListener
//    }

    companion object {
        //var mShopActivityEntity: ShopActivityEntity? = null
        fun getInstance(isOutstanding: Any): ViewPPDDListOutstandingFragment {
            val mViewPPDDListFragment = ViewPPDDListOutstandingFragment()
//            if (objects is ShopActivityEntity) {
//                mShopActivityEntity = objects
//            }

            val bundle = Bundle()
            bundle.putBoolean("isOutstanding", isOutstanding as Boolean)
            mViewPPDDListFragment.arguments = bundle
            return mViewPPDDListFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        isOutstanding = arguments?.getBoolean("isOutstanding")!!

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_pp_dd_stocks, container, false)
        initView(view)
//        if (AppDatabase.getDBInstance()!!.marketingCategoryMasterDao().getAll().isEmpty())
//            callMarketingCategoryListApi()
        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    list = AppDatabase.getDBInstance()!!.addShopEntryDao().all
                    if (mViewPPDDListAdapter != null)
                        mViewPPDDListAdapter.updateAdapter(list)
                } else {
                    list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopBySearchData(query)
                    if (mViewPPDDListAdapter != null)
                        mViewPPDDListAdapter.updateAdapter(list)
                }


//                Toast.makeText(mContext, query, Toast.LENGTH_SHORT).show()
            }

        })

        return view

    }

    private fun callMarketingCategoryListApi() {
        progress_wheel.spin()
        var repository = MarketingCatListRepoProvider.provideMarketingCatList()
        BaseActivity.compositeDisposable.add(
                repository.getMarketingCategoryList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            var marketingPagerResult = result as MarketingCategoryListResponse
                            if (marketingPagerResult.status == NetworkConstant.SUCCESS) {
                                saveInDB(marketingPagerResult)
//                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")
                            } else {
//                                (mContext as DashboardActivity).showSnackMessage("NOT SUCCESS")
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }

    private fun saveInDB(marketingPagerResult: MarketingCategoryListResponse) {
        var retail_branding_list = marketingPagerResult.RetailBranding
        var pop_material = marketingPagerResult.POPMaterial
        for (i in 0 until retail_branding_list.size) {
            var marketingCatEntity = MarketingCategoryMasterEntity()
            marketingCatEntity.material_id = retail_branding_list[i].material_id.toString()
            marketingCatEntity.material_name = retail_branding_list[i].material_name
            marketingCatEntity.type_id = "1"
            AppDatabase.getDBInstance()!!.marketingCategoryMasterDao().insertAll(marketingCatEntity)
        }
        for (i in 0 until pop_material.size) {
            var marketingCatEntity = MarketingCategoryMasterEntity()
            marketingCatEntity.material_id = pop_material[i].material_id.toString()
            marketingCatEntity.material_name = pop_material[i].material_name
            marketingCatEntity.type_id = "2"
            AppDatabase.getDBInstance()!!.marketingCategoryMasterDao().insertAll(marketingCatEntity)
        }
    }

    private fun getShopListApi() {
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getShopList(Pref.session_token!!, Pref.user_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            var shopList = result as ShopListResponse
                            if (shopList.status == NetworkConstant.SUCCESS) {
                                progress_wheel.stopSpinning()
                                if (shopList.data!!.shop_list == null || shopList.data!!.shop_list!!.isEmpty()) {
                                    noShopAvailable.visibility = View.VISIBLE
                                    nearByShopsList.visibility = View.GONE
                                } else
                                    convertToShopListSetAdapter(shopList.data!!.shop_list!!)
//                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")
                            } else if (shopList.status == NetworkConstant.SESSION_MISMATCH) {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(shopList.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }

    override fun onResume() {
        super.onResume()

    }

    override fun updateUI(any: Any) {
        super.updateUI(any)
        //getListFromDatabase()
    }

    private fun convertToShopListSetAdapter(shop_list: List<ShopData>) {
        var list: MutableList<AddShopDBModelEntity> = ArrayList()
        var shopObj = AddShopDBModelEntity()
        for (i in 0 until shop_list.size) {
            shopObj.shop_id = shop_list[i].shop_id
            shopObj.shopName = shop_list[i].shop_name
            shopObj.shopImageLocalPath = shop_list[i].Shop_Image
            shopObj.shopLat = shop_list[i].shop_lat!!.toDouble()
            shopObj.shopLong = shop_list[i].shop_long!!.toDouble()
            shopObj.duration = "0"
            shopObj.endTimeStamp = "0"
            shopObj.timeStamp = "0"
            shopObj.dateOfBirth = shop_list[i].dob
            shopObj.dateOfAniversary = shop_list[i].date_aniversary
            shopObj.visited = true
            shopObj.visitDate = AppUtils.getCurrentDate()
            shopObj.totalVisitCount = "1"
            shopObj.address = shop_list[i].address
            shopObj.ownerEmailId = shop_list[i].owner_email
            shopObj.ownerContactNumber = shop_list[i].owner_contact_no
            shopObj.pinCode = shop_list[i].pin_code
            shopObj.isUploaded = true
            shopObj.ownerName = shop_list[i].owner_name
            shopObj.user_id = Pref.user_id
            shopObj.orderValue = 0
            shopObj.lastVisitedDate = AppUtils.getCurrentDate()

            if (shop_list[i].entity_code == null)
                shopObj.entity_code = ""
            else
                shopObj.entity_code = shop_list[i].entity_code

            list.add(shopObj)
            AppDatabase.getDBInstance()!!.addShopEntryDao().insert(shopObj)
        }
        initAdapter()
    }

    private fun initView(view: View) {

        if (!isOutstanding)
            (mContext as DashboardActivity).setTopBarTitle(getString(R.string.stock_details_pp_dd))
        else
            (mContext as DashboardActivity).setTopBarTitle(getString(R.string.outstanding_details_pp_dd))

        getFloatingVal = ArrayList<String>()
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        nearByShopsList = view.findViewById(R.id.near_by_shops_RCV)
        noShopAvailable = view.findViewById(R.id.no_shop_tv)
        shop_list_parent_rl = view.findViewById(R.id.shop_list_parent_rl)
        iv_nearbyImage = view.findViewById(R.id.iv_nearbyImage)
        rb_view_pp_list = view.findViewById(R.id.rb_view_pp_list)
        rb_view_pp_list.setOnClickListener(this)
        rb_view_dd_list = view.findViewById(R.id.rb_view_dd_list)
        rb_view_dd_list.setOnClickListener(this)


        shop_list_parent_rl.setOnClickListener { view ->
            floating_fab.close(true)
        }
        floating_fab = view.findViewById(R.id.floating_fab)
        floating_fab.menuIconView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_add))
        floating_fab.menuButtonColorNormal = mContext.resources.getColor(R.color.colorAccent)
        floating_fab.menuButtonColorPressed = mContext.resources.getColor(R.color.colorPrimaryDark)
        floating_fab.menuButtonColorRipple = mContext.resources.getColor(R.color.colorPrimary)

        floating_fab.isIconAnimated = false
        floating_fab.setClosedOnTouchOutside(true)

        getFloatingVal.add("Alphabetically")
        getFloatingVal.add("Visit Date")
        getFloatingVal.add("Most Visited")

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

        (mContext as DashboardActivity).shop_type = "2"
        getListFromDatabase("2")

    }


    private fun getListFromDatabase(type: String) {
        list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType(type)

        if (list != null && !list.isEmpty()) {
            /*for (i in 0 until list.size) {
                val listnew = AppDatabase.getDBInstance()!!.addShopEntryDao().getVisitedShopListByName(list[i].shopName, true)
                list[i].totalVisitCount = listnew.size.toString()
                if (listnew.size > 0)
                    list[i].lastVisitedDate = listnew[listnew.size - 1].visitDate
            }*/
            initAdapter()
            syncShopList()
        } else {
            noShopAvailable.visibility = View.VISIBLE
            nearByShopsList.visibility = View.GONE
        }
    }

    private fun setListVisiBility(): Boolean {
//        return true
        return if (list.isNotEmpty()) {
            noShopAvailable.visibility = View.GONE
            nearByShopsList.visibility = View.VISIBLE
//            initAdapter()
            true
        } else {
            noShopAvailable.visibility = View.VISIBLE
            nearByShopsList.visibility = View.GONE
            false
        }

    }


    private var i = 0
    private fun initAdapter() {
        noShopAvailable.visibility = View.GONE
        nearByShopsList.visibility = View.VISIBLE

        //list = AppDatabase.getDBInstance()!!.addShopEntryDao().all
//        ( list as ArrayList).set()

        val stockList = AppDatabase.getDBInstance()!!.stockListDao().getAll()

        mViewPPDDListAdapter = ViewPPDDListAdapter(this.mContext!!, list, stockList, isOutstanding, object : ViewPPDDListClickListener {
            override fun viewStock(position: Int) {
                if (!isOutstanding)
                    (mContext as DashboardActivity).loadFragment(FragType.ViewStockFragment, true, list[position].shop_id)
                else
                    (mContext as DashboardActivity).loadFragment(FragType.ViewOutstandingFragment, true, list[position].shop_id)
            }

            override fun updateLocClick(position: Int) {

                try {
                    if (!Pref.isAddAttendence)
                        (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                    else {
                        val sdf = SimpleDateFormat("EEEE h:mm a", Locale.getDefault())
                        val d = Date()
                        val day = sdf.format(d)

                        val dayLong = AppUtils.convertDayTimeWithMeredianToLong(day) //System.currentTimeMillis()

                        //if (dayLong >= convertDayTimeWithMeredianToLong("Saturday 12:01 AM") && dayLong <= convertDayTimeWithMeredianToLong("Monday 11:59 AM")) {

                        if (!list[position].isUploaded) {
                            if (AppUtils.isOnline(mContext)) {
                                val addShopData = AddShopRequestData()
                                val mAddShopDBModelEntity = list[position]
                                addShopData.session_token = Pref.session_token
                                addShopData.address = mAddShopDBModelEntity.address
                                addShopData.owner_contact_no = mAddShopDBModelEntity.ownerContactNumber
                                addShopData.owner_email = mAddShopDBModelEntity.ownerEmailId
                                addShopData.owner_name = mAddShopDBModelEntity.ownerName
                                addShopData.pin_code = mAddShopDBModelEntity.pinCode
                                addShopData.shop_lat = mAddShopDBModelEntity.shopLat.toString()
                                addShopData.shop_long = mAddShopDBModelEntity.shopLong.toString()
                                addShopData.shop_name = mAddShopDBModelEntity.shopName.toString()
                                addShopData.type = mAddShopDBModelEntity.type.toString()
                                addShopData.shop_id = mAddShopDBModelEntity.shop_id
                                addShopData.user_id = Pref.user_id
                                addShopData.assigned_to_dd_id = mAddShopDBModelEntity.assigned_to_dd_id
                                addShopData.assigned_to_pp_id = mAddShopDBModelEntity.assigned_to_pp_id
                                addShopData.added_date = mAddShopDBModelEntity.added_date
                                addShopData.amount = mAddShopDBModelEntity.amount
                                //callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, mAddShopDBModelEntity)
                                calUpdateStockAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, mAddShopDBModelEntity)
                                //callAddShopApi(addShopData, "")
                            } else
                                (mContext as DashboardActivity).showSnackMessage("Please sync your shop first")
                        } else {
                            if (!isOutstanding)
                                openAddressUpdateDialog(list[position])
                            else
                                openOutstandingUpdateDialog(list[position])
                        }
                    }
                    /*} else
                        (mContext as DashboardActivity).showSnackMessage("Stock Update is only available from Saturday 12.01 AM to Monday 11.59 AM in every week.")*/
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun syncClick(position: Int) {

                /*val addShopData = AddShopRequestData()
                val mAddShopDBModelEntity = list[position]
                if (mAddShopDBModelEntity.isUploaded == false) {
                    addShopData.session_token = Pref.session_token
                    addShopData.address = mAddShopDBModelEntity.address
                    addShopData.owner_contact_no = mAddShopDBModelEntity.ownerContactNumber
                    addShopData.owner_email = mAddShopDBModelEntity.ownerEmailId
                    addShopData.owner_name = mAddShopDBModelEntity.ownerName
                    addShopData.pin_code = mAddShopDBModelEntity.pinCode
                    addShopData.shop_lat = mAddShopDBModelEntity.shopLat.toString()
                    addShopData.shop_long = mAddShopDBModelEntity.shopLong.toString()
                    addShopData.shop_name = mAddShopDBModelEntity.shopName.toString()
                    addShopData.type = mAddShopDBModelEntity.type.toString()
                    addShopData.shop_id = mAddShopDBModelEntity.shop_id
                    addShopData.user_id = Pref.user_id
                    callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath)
                    //callAddShopApi(addShopData, "")
                }*/

                if (!list[position].isUploaded) {
                    if (AppUtils.isOnline(mContext)) {
                        val addShopData = AddShopRequestData()
                        val mAddShopDBModelEntity = list[position]
                        addShopData.session_token = Pref.session_token
                        addShopData.address = mAddShopDBModelEntity.address
                        addShopData.owner_contact_no = mAddShopDBModelEntity.ownerContactNumber
                        addShopData.owner_email = mAddShopDBModelEntity.ownerEmailId
                        addShopData.owner_name = mAddShopDBModelEntity.ownerName
                        addShopData.pin_code = mAddShopDBModelEntity.pinCode
                        addShopData.shop_lat = mAddShopDBModelEntity.shopLat.toString()
                        addShopData.shop_long = mAddShopDBModelEntity.shopLong.toString()
                        addShopData.shop_name = mAddShopDBModelEntity.shopName.toString()
                        addShopData.type = mAddShopDBModelEntity.type.toString()
                        addShopData.shop_id = mAddShopDBModelEntity.shop_id
                        addShopData.user_id = Pref.user_id
                        addShopData.assigned_to_dd_id = mAddShopDBModelEntity.assigned_to_dd_id
                        addShopData.assigned_to_pp_id = mAddShopDBModelEntity.assigned_to_pp_id
                        addShopData.added_date = mAddShopDBModelEntity.added_date
                        addShopData.amount = mAddShopDBModelEntity.amount
                        callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, mAddShopDBModelEntity)
                        //callAddShopApi(addShopData, "")
                    } else
                        (mContext as DashboardActivity).showSnackMessage("Please check your internet connection")
                } else {
                    val list = AppDatabase.getDBInstance()!!.updateStockDao().getStockAccordingToSyncStateId(false, list[position].shop_id)

                    if (list != null && list.isNotEmpty()) {
                        i = 0
                        if (AppUtils.isOnline(mContext))
                            callUpdateStockApiForSync(list)
                        else
                            (mContext as DashboardActivity).showSnackMessage("Please check your internet connection")
                    }
                }
            }

            override fun mapClick(position: Int) {
                floating_fab.close(true)
//                (mContext as DashboardActivity).openLocationWithTrack()
                // (mContext as DashboardActivity).openLocationMap(list[position].shopLat.toString(), list[position].shopLong.toString())
            }

            override fun orderClick(position: Int) {
                floating_fab.close(true)
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
                (mContext as DashboardActivity).loadFragment(FragType.OrderListFragment, false, "")
            }

            @SuppressLint("NewApi")
            override fun callClick(position: Int) {
                floating_fab.close(true)
                IntentActionable.initiatePhoneCall(mContext, list[position].ownerContactNumber)
//                initiatePopupWindow(view,position)
            }

            override fun OnNearByShopsListClick(position: Int) {
                floating_fab.close(true)
                AppUtils.isFromViewPPDD = true
                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, list[position].shop_id)
            }


        })

        sortAlphabatically()

        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager
        nearByShopsList.layoutManager = layoutManager
        nearByShopsList.adapter = mViewPPDDListAdapter

    }

    private fun openOutstandingUpdateDialog(addShopModelEntity: AddShopDBModelEntity) {
        try {
            UpdateOutstandingDialog.getInstance(addShopModelEntity.shop_id, (mContext as DashboardActivity).shop_type, object : UpdateOutstandingDialog.UpdateOutstandingListener {
                override fun updateOutstanding(mo: String, co: String, po: String) {
                    val updateStock = AppDatabase.getDBInstance()!!.updateOutstandingDao().getOutstandingAccordingToSyncStateId(false/*AppUtils.getCurrentDateForShopActi()*/, addShopModelEntity.shop_id)

                    val currentDateTime = AppUtils.getCurrentISODateTime()

                    if (updateStock == null || updateStock.isEmpty()) {

                        doAsync {

                            saveToDatabase(addShopModelEntity, mo, co, po)

                            uiThread {
                                /*if (AppUtils.isOnline(mContext)) {
                                    callUpdateStockApiForNewStock(addShopModelEntity.shop_id, amount, closingMonth, closingYear, openingMonth,
                                            openingYear, description, mo, co, po, currentDateTime)
                                }
                                else {*/
                                list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                                initAdapter()
                                (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.outstanding_update_success))
                                //}
                            }
                        }

                    } else {
                        /*if (AppUtils.isOnline(mContext))
                            callUpdateStockApiForSyncOneitem(updateStock, amount, mo, co, po, description)
                        else*/
                        (mContext as DashboardActivity).showSnackMessage("Please sync your previous stock first")
                    }
                }
            }).show((mContext as DashboardActivity).supportFragmentManager, "UpdatePPDDOutstandingDialog")
        } catch (e: Exception) {
            //openAddressUpdateDialog(addShopModelEntity)
            e.printStackTrace()
        }
    }

    private fun saveToDatabase(addShopModelEntity: AddShopDBModelEntity, mo: String, co: String, po: String) {
        val updateOutstandingObj = OutstandingListEntity()
        updateOutstandingObj.shop_id = addShopModelEntity.shop_id
        updateOutstandingObj.current_date = AppUtils.getCurrentDateForShopActi()

        if (TextUtils.isEmpty(mo))
            updateOutstandingObj.mo = "0.00"
        else {
            val totalPrice = String.format("%.2f", mo.toFloat())
            updateOutstandingObj.mo = totalPrice
        }
        if (TextUtils.isEmpty(co))
            updateOutstandingObj.co = "0.00"
        else {
            val totalPrice = String.format("%.2f", co.toFloat())
            updateOutstandingObj.co = totalPrice
        }
        if (TextUtils.isEmpty(po))
            updateOutstandingObj.po = "0.00"
        else {
            val totalPrice = String.format("%.2f", po.toFloat())
            updateOutstandingObj.po = totalPrice
        }
        AppDatabase.getDBInstance()!!.updateOutstandingDao().insert(updateOutstandingObj)
    }

    private fun calUpdateStockAddShopApi(addShop: AddShopRequestData, shop_imgPath: String, mAddShopDBModelEntity: AddShopDBModelEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        if (isShopRegistrationInProcess)
            return

        val repository = AddShopRepositoryProvider.provideAddShopRepository()
        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("================SyncShop Input Params (Outstanding)=====================")
        XLog.d("shop id=======> " + addShop.shop_id)
        val index = addShop.shop_id!!.indexOf("_")
        XLog.d("decoded shop id=======> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
        XLog.d("shop added date=======> " + addShop.added_date)
        XLog.d("shop address=======> " + addShop.address)
        XLog.d("assigned to dd id=======> " + addShop.assigned_to_dd_id)
        XLog.d("assigned to pp id=======> " + addShop.assigned_to_pp_id)
        XLog.d("date aniversery=======> " + addShop.date_aniversary)
        XLog.d("dob=======> " + addShop.dob)
        XLog.d("shop owner phn no=======> " + addShop.owner_contact_no)
        XLog.d("shop owner email=======> " + addShop.owner_email)
        XLog.d("shop owner name=======> " + addShop.owner_name)
        XLog.d("shop pincode=======> " + addShop.pin_code)
        XLog.d("session token=======> " + addShop.session_token)
        XLog.d("shop lat=======> " + addShop.shop_lat)
        XLog.d("shop long=======> " + addShop.shop_long)
        XLog.d("shop name=======> " + addShop.shop_name)
        XLog.d("shop type=======> " + addShop.type)
        XLog.d("user id=======> " + addShop.user_id)
        XLog.d("amount=======> " + addShop.amount)
        XLog.d("shop image path=======> $shop_imgPath")
        XLog.d("=========================================================================")

        BaseActivity.compositeDisposable.add(
                repository.addShopWithImage(addShop, shop_imgPath, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val addShopResult = result as AddShopResponse
                            XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                            if (addShopResult.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                (mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                doAsync {
                                    val resultAs = runLongTask(addShop.shop_id)
                                    uiThread {
                                        if (resultAs == true) {

                                            list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                                            initAdapter()

                                            if (isOutstanding)
                                                openOutstandingUpdateDialog(mAddShopDBModelEntity)
                                            else
                                                openAddressUpdateDialog(mAddShopDBModelEntity)
                                        }
                                    }
                                }
                                progress_wheel.stopSpinning()

                            } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                    AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                }
                                doAsync {
                                    var resultAs = runLongTask(addShop.shop_id)
                                    uiThread {
                                        if (resultAs == true) {
                                            /*if (mViewPPDDListAdapter != null) {
                                                list = AppDatabase.getDBInstance()!!.addShopEntryDao().all
                                                mViewPPDDListAdapter.updateAdapter(list)
                                            }*/

                                            list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                                            initAdapter()

                                            if (isOutstanding)
                                                openOutstandingUpdateDialog(mAddShopDBModelEntity)
                                            else
                                                openAddressUpdateDialog(mAddShopDBModelEntity)
                                        }
                                    }
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)


                            }
                            isShopRegistrationInProcess = false

                            if (!AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false).isEmpty())
                                syncShopList()

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                            isShopRegistrationInProcess = false
                            if (error != null)
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                        })
        )
    }

    private fun callUpdateStockApiForSync(updateStock: List<UpdateStockEntity>) {
        val repository = UpdateStockRepoProvider.provideOrderDetailsListRepository()
        progress_wheel.spin()
        val addressUpdateReq = UpdateStockInputParamsModel()
        addressUpdateReq.user_id = Pref.user_id
        addressUpdateReq.shop_id = updateStock[i].shop_id
        addressUpdateReq.closing_stock_amount = updateStock[i].closing_stock_amount
        addressUpdateReq.closing_stock_month = updateStock[i].closing_stock_month
        addressUpdateReq.closing_stock_month_val = updateStock[i].closing_stock_month_val
        addressUpdateReq.closing_stock_year_val = updateStock[i].closing_stock_year_val
        addressUpdateReq.opening_stock_amount = updateStock[i].opening_stock_amount
        addressUpdateReq.opening_stock_month = updateStock[i].opening_stock_month
        addressUpdateReq.opening_stock_month_val = updateStock[i].opening_stock_month_val
        addressUpdateReq.opening_stock_year_val = updateStock[i].opening_stock_year_val
        addressUpdateReq.description = updateStock[i].description
        addressUpdateReq.m_o = updateStock[i].mo
        addressUpdateReq.p_o = updateStock[i].po
        addressUpdateReq.c_o = updateStock[i].co

        BaseActivity.compositeDisposable.add(
                repository.updateStock(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {

                                AppDatabase.getDBInstance()!!.updateStockDao().updateIsUploadedAccordingToId(true, updateStock[i].id)

                                i++
                                if (i < updateStock.size)
                                    callUpdateStockApiForSync(updateStock)
                                else {
                                    i = 0
                                    //(mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                                    initAdapter()
                                }
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                        })
        )
    }


    private fun openAddressUpdateDialog(addShopModelEntity: AddShopDBModelEntity) {
        try {
            UpdatePPDDStockDialog.getInstance(addShopModelEntity.shop_id, (mContext as DashboardActivity).shop_type, object : StockUpdateListener {
                override fun onUpdateClick(openingMonth: String, openingYear: String, amount: String, closingMonth: String, closingYear: String,
                                           description: String, mo: String, co: String, po: String) {

                    val updateStock = AppDatabase.getDBInstance()!!.updateStockDao().getStockAccordingToShopIdStockMonth(addShopModelEntity.shop_id,
                            AppUtils.getMonthValue(closingMonth), closingYear, AppUtils.getMonthValue(openingMonth), openingYear)

                    val currentDateTime = AppUtils.getCurrentISODateTime()

                    if (updateStock == null) {

                        //saveNewStock(amount, mo, po, co, addShopModelEntity.shop_id, closingMonth, closingYear, openingMonth, openingYear, description, currentDateTime)
                        addDataToListDb(amount, mo, po, co, addShopModelEntity.shop_id)
                        doAsync {
                            val updateStockObj = UpdateStockEntity()
                            updateStockObj.shop_id = addShopModelEntity.shop_id
                            updateStockObj.closing_stock_amount = amount
                            updateStockObj.opening_stock_amount = amount
                            updateStockObj.closing_stock_month = closingMonth + ", " + closingYear
                            updateStockObj.opening_stock_month = openingMonth + ", " + openingYear
                            updateStockObj.opening_stock_year_val = openingYear
                            updateStockObj.closing_stock_year_val = closingYear
                            updateStockObj.opening_stock_month_val = AppUtils.getMonthValue(openingMonth)
                            updateStockObj.closing_stock_month_val = AppUtils.getMonthValue(closingMonth)
                            updateStockObj.description = description
                            updateStockObj.current_date = currentDateTime

                            if (TextUtils.isEmpty(mo))
                                updateStockObj.mo = "0"
                            else
                                updateStockObj.mo = mo

                            if (TextUtils.isEmpty(co))
                                updateStockObj.co = "0"
                            else
                                updateStockObj.co = co

                            if (TextUtils.isEmpty(po))
                                updateStockObj.po = "0"
                            else
                                updateStockObj.po = po

                            AppDatabase.getDBInstance()!!.updateStockDao().insert(updateStockObj)

                            uiThread {
                                if (AppUtils.isOnline(mContext))
                                    callUpdateStockApiForNewStock(addShopModelEntity.shop_id, amount, closingMonth, closingYear, openingMonth,
                                            openingYear, description, mo, co, po, currentDateTime)
                                else {
                                    list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                                    initAdapter()
                                    (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))
                                }
                            }
                        }

                    } else {
                        if (!updateStock.isUploaded) {

                            if (AppUtils.isOnline(mContext))
                                callUpdateStockApiForSyncOneitem(updateStock, amount, mo, co, po, description)
                            else
                                (mContext as DashboardActivity).showSnackMessage("Please sync your previous stock first")

                        } else {

                            //saveNewStock(amount, mo, po, co, addShopModelEntity.shop_id, closingMonth, closingYear, openingMonth, openingYear, description, currentDateTime)
                            addDataToListDb(amount, mo, po, co, addShopModelEntity.shop_id)
                            AppDatabase.getDBInstance()!!.updateStockDao().updateClosingAmount(amount, addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth), closingYear)
                            AppDatabase.getDBInstance()!!.updateStockDao().updateOpeningAmount(amount, addShopModelEntity.shop_id, AppUtils.getMonthValue(openingMonth), openingYear)

                            if (TextUtils.isEmpty(mo))
                                AppDatabase.getDBInstance()!!.updateStockDao().updateMO("0", addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth), closingYear, AppUtils.getMonthValue(openingMonth), openingYear)
                            else
                                AppDatabase.getDBInstance()!!.updateStockDao().updateMO(mo, addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth), closingYear, AppUtils.getMonthValue(openingMonth), openingYear)

                            if (TextUtils.isEmpty(po))
                                AppDatabase.getDBInstance()!!.updateStockDao().updatePO("0", addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth), closingYear, AppUtils.getMonthValue(openingMonth), openingYear)
                            else
                                AppDatabase.getDBInstance()!!.updateStockDao().updatePO(po, addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth), closingYear, AppUtils.getMonthValue(openingMonth), openingYear)

                            if (TextUtils.isEmpty(co))
                                AppDatabase.getDBInstance()!!.updateStockDao().updateCO("0", addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth), closingYear, AppUtils.getMonthValue(openingMonth), openingYear)
                            else
                                AppDatabase.getDBInstance()!!.updateStockDao().updateCO(co, addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth), closingYear, AppUtils.getMonthValue(openingMonth), openingYear)


                            AppDatabase.getDBInstance()!!.updateStockDao().updateDescription(description, addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth), closingYear, AppUtils.getMonthValue(openingMonth), openingYear)

                            AppDatabase.getDBInstance()!!.updateStockDao().updateCurrentDate(currentDateTime, addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth), closingYear, AppUtils.getMonthValue(openingMonth), openingYear)

                            AppDatabase.getDBInstance()?.updateStockDao()?.updateIsUploadedForOneItem(false, addShopModelEntity.shop_id, AppUtils.getMonthValue(closingMonth),
                                    closingYear, AppUtils.getMonthValue(openingMonth), openingYear)

                            if (AppUtils.isOnline(mContext))
                                callUpdateStockApiForNewStock(addShopModelEntity.shop_id, amount, closingMonth, closingYear, openingMonth, openingYear,
                                        description, mo, co, po, currentDateTime)
                            else {
                                list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                                initAdapter()
                                (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))
                            }
                        }
                    }

                    //callShopAddressUpdateApi(address!!)
                }

            }).show((mContext as DashboardActivity).supportFragmentManager, "UpdatePPDDStockDialog")
        } catch (e: Exception) {
            //openAddressUpdateDialog(addShopModelEntity)
            e.printStackTrace()
        }
    }

    private fun saveNewStock(amount: String, mo: String, po: String, co: String, shop_id: String, closingMonth: String, closingYear: String,
                             openingMonth: String, openingYear: String, description: String, currentDateTime: String) {
        doAsync {
            val updateStockObj = UpdateStockEntity()
            updateStockObj.shop_id = shop_id
            updateStockObj.closing_stock_amount = amount
            updateStockObj.opening_stock_amount = amount
            updateStockObj.closing_stock_month = closingMonth + ", " + closingYear
            updateStockObj.opening_stock_month = openingMonth + ", " + openingYear
            updateStockObj.opening_stock_year_val = openingYear
            updateStockObj.closing_stock_year_val = closingYear
            updateStockObj.opening_stock_month_val = AppUtils.getMonthValue(openingMonth)
            updateStockObj.closing_stock_month_val = AppUtils.getMonthValue(closingMonth)
            updateStockObj.description = description
            updateStockObj.current_date = AppUtils.getCurrentDate()

            if (TextUtils.isEmpty(mo))
                updateStockObj.mo = "0"
            else
                updateStockObj.mo = mo

            if (TextUtils.isEmpty(co))
                updateStockObj.co = "0"
            else
                updateStockObj.co = co

            if (TextUtils.isEmpty(po))
                updateStockObj.po = "0"
            else
                updateStockObj.po = po

            AppDatabase.getDBInstance()!!.updateStockDao().insert(updateStockObj)

            uiThread {
                if (AppUtils.isOnline(mContext))
                    callUpdateStockApiForNewStock(shop_id, amount, closingMonth, closingYear, openingMonth,
                            openingYear, description, mo, co, po, currentDateTime)
                else {
                    list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                    initAdapter()
                    (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))
                }
            }
        }
    }

    private fun addDataToListDb(amount: String, mo: String, po: String, co: String, shop_id: String) {
        val updateStockList = StockListEntity()
        updateStockList.current_date = AppUtils.getCurrentDateForShopActi()
        updateStockList.mo = mo + ".00"
        updateStockList.stock_value = amount + ".00"

        if (TextUtils.isEmpty(po))
            updateStockList.po = "0.00"
        else
            updateStockList.po = po + ".00"

        if (TextUtils.isEmpty(co))
            updateStockList.co = "0.00"
        else
            updateStockList.co = co + ".00"

        updateStockList.shop_id = shop_id
        AppDatabase.getDBInstance()!!.stockListDao().insert(updateStockList)
    }

    private fun callUpdateStockApiForSyncOneitem(updateStock: UpdateStockEntity, amount: String, mo: String, co: String, po: String, description: String) {
        val repository = UpdateStockRepoProvider.provideOrderDetailsListRepository()
        progress_wheel.spin()
        val addressUpdateReq = UpdateStockInputParamsModel()
        addressUpdateReq.user_id = Pref.user_id
        addressUpdateReq.shop_id = updateStock.shop_id
        addressUpdateReq.closing_stock_amount = updateStock.closing_stock_amount
        addressUpdateReq.closing_stock_month = updateStock.closing_stock_month
        addressUpdateReq.closing_stock_month_val = updateStock.closing_stock_month_val
        addressUpdateReq.closing_stock_year_val = updateStock.closing_stock_year_val
        addressUpdateReq.opening_stock_amount = updateStock.opening_stock_amount
        addressUpdateReq.opening_stock_month = updateStock.opening_stock_month
        addressUpdateReq.opening_stock_month_val = updateStock.opening_stock_month_val
        addressUpdateReq.opening_stock_year_val = updateStock.opening_stock_year_val
        addressUpdateReq.description = updateStock.description
        addressUpdateReq.p_o = updateStock.po
        addressUpdateReq.m_o = updateStock.mo
        addressUpdateReq.c_o = updateStock.co

        BaseActivity.compositeDisposable.add(
                repository.updateStock(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                addDataToListDb(amount, mo, po, co, updateStock.shop_id!!)
                                AppDatabase.getDBInstance()!!.updateStockDao().updateClosingAmount(amount, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!)
                                AppDatabase.getDBInstance()!!.updateStockDao().updateOpeningAmount(amount, updateStock.shop_id!!, updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                if (TextUtils.isEmpty(mo))
                                    AppDatabase.getDBInstance()!!.updateStockDao().updateMO("0", updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)
                                else
                                    AppDatabase.getDBInstance()!!.updateStockDao().updateMO(mo, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                if (TextUtils.isEmpty(po))
                                    AppDatabase.getDBInstance()!!.updateStockDao().updatePO("0", updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)
                                else
                                    AppDatabase.getDBInstance()!!.updateStockDao().updatePO(po, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                if (TextUtils.isEmpty(co))
                                    AppDatabase.getDBInstance()!!.updateStockDao().updateCO("0", updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)
                                else
                                    AppDatabase.getDBInstance()!!.updateStockDao().updateCO(co, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                AppDatabase.getDBInstance()!!.updateStockDao().updateDescription(description, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                        updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)


                                AppDatabase.getDBInstance()!!.updateStockDao().updateCurrentDate(AppUtils.getCurrentDate(), updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                        updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                val updateStockNew = AppDatabase.getDBInstance()!!.updateStockDao().getStockAccordingToShopIdStockMonth(updateStock.shop_id!!,
                                        updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                        updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                callUpdateStockApiForOneitem(updateStockNew, amount)

                                //(mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                        })
        )
    }

    private fun callUpdateStockApiForOneitem(updateStock: UpdateStockEntity, amount: String) {
        val repository = UpdateStockRepoProvider.provideOrderDetailsListRepository()
        progress_wheel.spin()
        val addressUpdateReq = UpdateStockInputParamsModel()
        addressUpdateReq.user_id = Pref.user_id
        addressUpdateReq.shop_id = updateStock.shop_id
        addressUpdateReq.closing_stock_amount = amount
        addressUpdateReq.closing_stock_month = updateStock.closing_stock_month
        addressUpdateReq.closing_stock_month_val = updateStock.closing_stock_month_val
        addressUpdateReq.closing_stock_year_val = updateStock.closing_stock_year_val
        addressUpdateReq.opening_stock_amount = amount
        addressUpdateReq.opening_stock_month = updateStock.opening_stock_month
        addressUpdateReq.opening_stock_month_val = updateStock.opening_stock_month_val
        addressUpdateReq.opening_stock_year_val = updateStock.opening_stock_year_val
        addressUpdateReq.description = updateStock.description
        addressUpdateReq.p_o = updateStock.po
        addressUpdateReq.m_o = updateStock.mo
        addressUpdateReq.c_o = updateStock.co

        BaseActivity.compositeDisposable.add(
                repository.updateStock(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                AppDatabase.getDBInstance()?.updateStockDao()?.updateIsUploadedForOneItem(true, updateStock.shop_id!!, updateStock.closing_stock_month_val!!,
                                        updateStock.closing_stock_year_val!!, updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)
                            } else
                                (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))

                            list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                            initAdapter()

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))

                            list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                            initAdapter()
                        })
        )
    }

    private fun callUpdateStockApiForNewStock(shop_id: String, amount: String, closingMonth: String, closingYear: String, openingMonth: String,
                                              openingYear: String, description: String, mo: String, co: String, po: String, currentDateTime: String) {
        val repository = UpdateStockRepoProvider.provideOrderDetailsListRepository()
        progress_wheel.spin()
        val addressUpdateReq = UpdateStockInputParamsModel()
        addressUpdateReq.user_id = Pref.user_id
        addressUpdateReq.shop_id = shop_id
        addressUpdateReq.closing_stock_amount = amount
        addressUpdateReq.closing_stock_month = closingMonth + ", " + closingYear
        addressUpdateReq.closing_stock_month_val = AppUtils.getMonthValue(closingMonth)
        addressUpdateReq.closing_stock_year_val = closingYear
        addressUpdateReq.opening_stock_amount = amount
        addressUpdateReq.opening_stock_month = openingMonth + ", " + openingYear
        addressUpdateReq.opening_stock_month_val = AppUtils.getMonthValue(openingMonth)
        addressUpdateReq.opening_stock_year_val = openingYear
        addressUpdateReq.description = description
        addressUpdateReq.stock_date = currentDateTime

        if (TextUtils.isEmpty(mo))
            addressUpdateReq.m_o = "0"
        else
            addressUpdateReq.m_o = mo

        if (TextUtils.isEmpty(po))
            addressUpdateReq.p_o = "0"
        else
            addressUpdateReq.p_o = po

        if (TextUtils.isEmpty(co))
            addressUpdateReq.c_o = "0"
        else
            addressUpdateReq.c_o = co

        BaseActivity.compositeDisposable.add(
                repository.updateStock(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {

                                AppDatabase.getDBInstance()?.updateStockDao()?.updateIsUploadedForOneItem(true, shop_id, AppUtils.getMonthValue(closingMonth),
                                        closingYear, AppUtils.getMonthValue(openingMonth), openingYear)

                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            } else
                                (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))

                            list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                            initAdapter()

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))

                            list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                            initAdapter()
                        })
        )
    }

    private fun callShopAddressUpdateApi(addShopModelEntity: AddShopDBModelEntity) {
        val repository = ShopAddressUpdateRepoProvider.provideShopAddressUpdateRepo()
        progress_wheel.spin()
        var addressUpdateReq = AddressUpdateRequest()
        addressUpdateReq.user_id = Pref.user_id
        addressUpdateReq.shop_id = addShopModelEntity.shop_id
        addressUpdateReq.shop_lat = addShopModelEntity.shopLat.toString()
        addressUpdateReq.shop_long = addShopModelEntity.shopLong.toString()
        addressUpdateReq.shop_address = addShopModelEntity.address
        addressUpdateReq.isAddressUpdated = "1"
        addressUpdateReq.pincode = addShopModelEntity.pinCode

        BaseActivity.compositeDisposable.add(
                repository.getShopAddressUpdate(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
//                                AppDatabase.getDBInstance()?.addShopEntryDao()?.updateIsAddressUpdated(addShopModelEntity.shop_id,true)
                                addShopModelEntity.isAddressUpdated = false
                                AppDatabase.getDBInstance()?.addShopEntryDao()?.updateShopDao(addShopModelEntity)
                                (mContext as DashboardActivity).updateFence()
                                mViewPPDDListAdapter.updateAdapter(AppDatabase.getDBInstance()!!.addShopEntryDao().all)
                                sendNotification(addShopModelEntity.shop_id)
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            } else {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(error.localizedMessage)
                        })
        )


    }

    private fun sendNotification(shopId: String) {
        val list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())
        var isDurationCalculated = false
        var shopName = ""
        if (list.isEmpty()) {
            if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId) == null)
                return
            shopName = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId).shopName
        } else {
            isDurationCalculated = list[0].isDurationCalculated
            shopName = list[0].shop_name!!
        }
        XLog.d("Geofence: ENTER : " + "ShopName : " + shopName + ",IS_DURATION_CALCULATED" + isDurationCalculated)
        if (isDurationCalculated)
            return

        XLog.d("Geofence: NearToShop : " + "ShopName : " + shopName)
        // Get an instance of the Notification manager
        val notification = NotificationUtils(getString(R.string.app_name), shopName, shopId, "")
        notification.CreateNotification(mContext, shopId)
//        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String, mAddShopDBModelEntity: AddShopDBModelEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        if (isShopRegistrationInProcess)
            return

        val repository = AddShopRepositoryProvider.provideAddShopRepository()
        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("==============SyncShop Input Params (Outstanding)==================")
        XLog.d("shop id=======> " + addShop.shop_id)
        val index = addShop.shop_id!!.indexOf("_")
        XLog.d("decoded shop id=======> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
        XLog.d("shop added date=======> " + addShop.added_date)
        XLog.d("shop address=======> " + addShop.address)
        XLog.d("assigned to dd id=======> " + addShop.assigned_to_dd_id)
        XLog.d("assigned to pp id=======> " + addShop.assigned_to_pp_id)
        XLog.d("date aniversery=======> " + addShop.date_aniversary)
        XLog.d("dob=======> " + addShop.dob)
        XLog.d("shop owner phn no=======> " + addShop.owner_contact_no)
        XLog.d("shop owner email=======> " + addShop.owner_email)
        XLog.d("shop owner name=======> " + addShop.owner_name)
        XLog.d("shop pincode=======> " + addShop.pin_code)
        XLog.d("session token=======> " + addShop.session_token)
        XLog.d("shop lat=======> " + addShop.shop_lat)
        XLog.d("shop long=======> " + addShop.shop_long)
        XLog.d("shop name=======> " + addShop.shop_name)
        XLog.d("shop type=======> " + addShop.type)
        XLog.d("user id=======> " + addShop.user_id)
        XLog.d("amount=======> " + addShop.amount)
        XLog.d("shop image path=======> $shop_imgPath")
        XLog.d("====================================================================")

        BaseActivity.compositeDisposable.add(
                repository.addShopWithImage(addShop, shop_imgPath, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val addShopResult = result as AddShopResponse
                            XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                            if (addShopResult.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                (mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                doAsync {
                                    val resultAs = runLongTask(addShop.shop_id)
                                    uiThread {
                                        if (resultAs == true) {

                                            list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                                            initAdapter()

                                            //openAddressUpdateDialog(mAddShopDBModelEntity)
                                        }
                                    }
                                }
                                progress_wheel.stopSpinning()

                            } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                    AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                }
                                doAsync {
                                    var resultAs = runLongTask(addShop.shop_id)
                                    uiThread {
                                        if (resultAs == true) {
                                            /*if (mViewPPDDListAdapter != null) {
                                                list = AppDatabase.getDBInstance()!!.addShopEntryDao().all
                                                mViewPPDDListAdapter.updateAdapter(list)
                                            }*/

                                            list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
                                            initAdapter()

                                            //openAddressUpdateDialog(mAddShopDBModelEntity)
                                        }
                                    }
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)


                            }
                            isShopRegistrationInProcess = false

                            if (!AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false).isEmpty())
                                syncShopList()

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                            isShopRegistrationInProcess = false
                            if (error != null)
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                        })
        )
    }

    private fun runLongTask(shop_id: String?): Any {
        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(shop_id!!, true, false)
        if (shopActivity != null)
            callShopActivitySubmit(shop_id)
        return true
    }

    private fun callShopActivitySubmit(shopId: String) {
        var list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())
        if (list.isEmpty())
            return
        var shopActivity = list[0]
        var shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token
        var shopDataList: MutableList<ShopDurationRequestData> = java.util.ArrayList()

        var shopDurationData = ShopDurationRequestData()
        shopDurationData.shop_id = shopActivity.shopid
        if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
            var totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
            AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
            var duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
            AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())
            shopDurationData.spent_duration = duration
        } else {
            shopDurationData.spent_duration = shopActivity.duration_spent
        }
        shopDurationData.visited_date = shopActivity.visited_date
        shopDurationData.visited_time = shopActivity.visited_date
        if (TextUtils.isEmpty(shopActivity.distance_travelled))
            shopActivity.distance_travelled = "0.0"
        shopDurationData.distance_travelled = shopActivity.distance_travelled
        var sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
        if (sList != null && sList.isNotEmpty())
            shopDurationData.total_visit_count = sList[0].totalVisitCount

        if (!TextUtils.isEmpty(shopActivity.feedback))
            shopDurationData.feedback = shopActivity.feedback
        else
            shopDurationData.feedback = ""

        shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
        shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc
        //duration garbage fix
        try{
            if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
            {
                shopDurationData.spent_duration="00:00:10"
            }
        }catch (ex:Exception){
            shopDurationData.spent_duration="00:00:10"
        }
        shopDataList.add(shopDurationData)

        if (shopDataList.isEmpty()) {
            return
        }

        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            XLog.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + ", RESPONSE:" + result.message)
                            if (result.status == NetworkConstant.SUCCESS) {

                            }

                        }, { error ->
                            error.printStackTrace()
                            if (error != null)
                                XLog.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + error.localizedMessage)
//                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )

    }


    private fun doNothing() {

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initiatePopupWindow(view: View, position: Int) {
        val popup = PopupWindow(mContext)
        val layout = layoutInflater.inflate(R.layout.popup_window_shop_item, null)

        popup.contentView = layout
        popup.isOutsideTouchable = true
        popup.isFocusable = true

        var call_ll: LinearLayout = layout.findViewById(R.id.call_ll)
        var direction_ll: LinearLayout = layout.findViewById(R.id.direction_ll)
        var add_order_ll: LinearLayout = layout.findViewById(R.id.add_order_ll)

        var call_iv: ImageView = layout.findViewById(R.id.call_iv)
        var call_tv: TextView = layout.findViewById(R.id.call_tv)
        var direction_iv: ImageView = layout.findViewById(R.id.direction_iv)
        var direction_tv: TextView = layout.findViewById(R.id.direction_tv)
        var order_iv: ImageView = layout.findViewById(R.id.order_iv)
        var order_tv: TextView = layout.findViewById(R.id.order_tv)


        call_ll.setOnClickListener(View.OnClickListener {
            call_iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_registered_shop_call_select))

            order_iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_registered_shop_add_order_deselect))
            direction_iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_registered_shop_direction_deselect))
            order_tv.setTextColor(mContext.getColor(R.color.login_txt_color))
            direction_tv.setTextColor(mContext.getColor(R.color.login_txt_color))

            call_tv.setTextColor(mContext.getColor(R.color.colorPrimary))
            popup.dismiss()
            IntentActionable.initiatePhoneCall(mContext, list[position].ownerContactNumber)
        })

        direction_ll.setOnClickListener(View.OnClickListener {
            direction_iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_registered_shop_direction_select))

            call_iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_registered_shop_call_deselect))
            order_iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_registered_shop_add_order_deselect))
            call_tv.setTextColor(mContext.getColor(R.color.login_txt_color))
            order_tv.setTextColor(mContext.getColor(R.color.login_txt_color))

            direction_tv.setTextColor(mContext.getColor(R.color.colorPrimary))
            popup.dismiss()
            (mContext as DashboardActivity).openLocationWithTrack()

        })

        add_order_ll.setOnClickListener(View.OnClickListener {
            order_iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_registered_shop_add_order_select))

            call_iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_registered_shop_call_deselect))
            direction_iv.setImageDrawable(mContext.getDrawable(R.drawable.ic_registered_shop_direction_deselect))
            call_tv.setTextColor(mContext.getColor(R.color.login_txt_color))
            direction_tv.setTextColor(mContext.getColor(R.color.login_txt_color))

            order_tv.setTextColor(mContext.getColor(R.color.colorPrimary))
            popup.dismiss()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))

        })

        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(view)
        popup.update()

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            100 -> {
                sortAlphabatically()
                floating_fab.close(true)
                programFab1.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab2.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab1.setImageResource(R.drawable.ic_tick_float_icon)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)


            }
            101 -> {
                sortByVisitDate()
                floating_fab.close(true)
                programFab1.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab3.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab1.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)
            }
            102 -> {
                sortByMostVisited()
                floating_fab.close(true)
                programFab1.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab1.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
                programFab3.setImageResource(R.drawable.ic_tick_float_icon)
            }

            R.id.rb_view_dd_list -> {
                (mContext as DashboardActivity).shop_type = "4"
                getListFromDatabase("4")
            }

            R.id.rb_view_pp_list -> {
                (mContext as DashboardActivity).shop_type = "2"
                getListFromDatabase("2")
            }
        }
    }

    fun sortAlphabatically() {

        if (!setListVisiBility())
            return

        Collections.sort(list, object : Comparator<AddShopDBModelEntity> {
            override fun compare(o1: AddShopDBModelEntity, o2: AddShopDBModelEntity): Int {
                return o1.shopName.toUpperCase().compareTo(o2.shopName.toUpperCase())
            }
        })
        if (mViewPPDDListAdapter != null)
            mViewPPDDListAdapter.notifyDataSetChanged()
    }

    fun sortByVisitDate() {

        if (!setListVisiBility())
            return

        Collections.sort(list, object : Comparator<AddShopDBModelEntity> {
            override fun compare(o1: AddShopDBModelEntity, o2: AddShopDBModelEntity): Int {
                return (AppUtils.getLongTimeStampFromDate(o1.lastVisitedDate)).compareTo(AppUtils.getLongTimeStampFromDate(o2.lastVisitedDate))
            }
        })

        Collections.reverse(list)

        if (mViewPPDDListAdapter != null)
            mViewPPDDListAdapter.notifyDataSetChanged()

    }

    fun sortByMostVisited() {

        if (!setListVisiBility())
            return

        Collections.sort(list, object : Comparator<AddShopDBModelEntity> {
            override fun compare(o1: AddShopDBModelEntity, o2: AddShopDBModelEntity): Int {
                return extractInt(o1.totalVisitCount) - extractInt(o2.totalVisitCount)
            }

            internal fun extractInt(s: String): Int {
                val num = s.replace("\\D".toRegex(), "")
                // return 0 if no digits found
                return if (num.isEmpty()) 0 else Integer.parseInt(num)
            }
        })

        Collections.reverse(list)

        if (mViewPPDDListAdapter != null)
            mViewPPDDListAdapter.notifyDataSetChanged()


    }


    private fun syncShopList() {
        val shopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(false)

        if (shopList.isEmpty())
            return

        val addShopData = AddShopRequestData()
        val mAddShopDBModelEntity = shopList[0]
        addShopData.session_token = Pref.session_token
        addShopData.address = mAddShopDBModelEntity.address
        addShopData.owner_contact_no = mAddShopDBModelEntity.ownerContactNumber
        addShopData.owner_email = mAddShopDBModelEntity.ownerEmailId
        addShopData.owner_name = mAddShopDBModelEntity.ownerName
        addShopData.pin_code = mAddShopDBModelEntity.pinCode
        addShopData.shop_lat = mAddShopDBModelEntity.shopLat.toString()
        addShopData.shop_long = mAddShopDBModelEntity.shopLong.toString()
        addShopData.shop_name = mAddShopDBModelEntity.shopName.toString()
        addShopData.type = mAddShopDBModelEntity.type.toString()
        addShopData.shop_id = mAddShopDBModelEntity.shop_id
        addShopData.user_id = Pref.user_id
        addShopData.assigned_to_dd_id = mAddShopDBModelEntity.assigned_to_dd_id
        addShopData.assigned_to_pp_id = mAddShopDBModelEntity.assigned_to_pp_id
        addShopData.added_date = mAddShopDBModelEntity.added_date
        addShopData.amount = mAddShopDBModelEntity.amount
        callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, mAddShopDBModelEntity)
        //callAddShopApi(addShopData, "")
    }

    public fun getFile(): File {
        var bm: Bitmap? = null
        if (bm == null) {
            val bitmap = (iv_nearbyImage.getDrawable() as BitmapDrawable).bitmap
            bm = bitmap
        }
        val bytes = ByteArrayOutputStream()
        bm!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

      /*  var destination = File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis().toString() + ".jpg")*/
//27-09-2021
        var destination = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                System.currentTimeMillis().toString() + ".jpg")

        val camera_image_path = destination?.absolutePath
        val fo: FileOutputStream
        try {
            destination?.createNewFile()
            fo = FileOutputStream(destination)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return destination
    }

    fun updateItem() {
        list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsAccordingToType((mContext as DashboardActivity).shop_type)
        initAdapter()
    }
}
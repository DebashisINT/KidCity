package com.kcteam.features.member.presentation

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.CustomStatic
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
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.billing.api.billinglistapi.BillingListRepoProvider
import com.kcteam.features.billing.model.BillingListResponseModel
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamListDataModel
import com.kcteam.features.member.model.TeamListResponseModel
import com.kcteam.features.myjobs.presentation.MyJobsFragment.Companion.usr_id
import com.kcteam.features.nearbyshops.api.ShopListRepositoryProvider
import com.kcteam.features.nearbyshops.model.ShopData
import com.kcteam.features.nearbyshops.model.ShopListResponse
import com.kcteam.features.newcollection.model.NewCollectionListResponseModel
import com.kcteam.features.newcollection.newcollectionlistapi.NewCollectionListRepoProvider
import com.kcteam.features.orderList.api.neworderlistapi.NewOrderListRepoProvider
import com.kcteam.features.orderList.model.NewOrderListResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 29-01-2020.
 */
class MemberListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_member_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_member_list_main: RelativeLayout
    private lateinit var tv_team_struct: AppCustomTextView
    private lateinit var tv_member_no: AppCustomTextView

    private var userId = ""
    private var isListUpdated = false
    private var isFirstScreen = false
    private var adapter: MemberListAdapter? = null
    private var member_list: ArrayList<TeamListDataModel>? = null

    companion object {

        fun newInstance(userId: Any): MemberListFragment {
            val fragment = MemberListFragment()

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
        val view = inflater.inflate(R.layout.fragment_member_list, container, false)

        initView(view)
        initSearchListener()
        getTeamList()

        return view
    }

    private fun initView(view: View) {
        rv_member_list = view.findViewById(R.id.rv_member_list)
        rv_member_list.layoutManager = LinearLayoutManager(mContext)

        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        tv_team_struct = view.findViewById(R.id.tv_team_struct)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        rl_member_list_main = view.findViewById(R.id.rl_member_list_main)
        rl_member_list_main.setOnClickListener(null)

        tv_member_no = view.findViewById(R.id.tv_member_no)

        if ((mContext as DashboardActivity).isAllTeam)
            tv_member_no.visibility = View.VISIBLE
        else
            tv_member_no.visibility = View.GONE

        setHierarchyData()
    }

    private fun setHierarchyData() {
        tv_team_struct.apply {
            (mContext as DashboardActivity).teamHierarchy.takeIf { it.isNotEmpty() }?.let {
                visibility = View.VISIBLE
                isFirstScreen = false

                it.forEachIndexed { i, name ->
                    text = if (i == 0)
                        name
                    else
                        text.toString().trim() + "-> " + name

                }

            } ?: let {
                visibility = View.GONE
                isFirstScreen = true
            }
        }
    }

    private fun initSearchListener() {
        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    member_list?.let {
                        adapter?.refreshList(it)
                        tv_member_no.text = "Total member(s): " + it.size
                    }
                    //initAdapter(shop_list!!)
                } else {
                    adapter?.filter?.filter(query)
                }
            }
        })
    }


    private fun getTeamList() {

        if (!AppUtils.isOnline(mContext)) {
            tv_no_data_available.visibility = View.VISIBLE
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = TeamRepoProvider.teamRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.teamList(userId, isFirstScreen, (mContext as DashboardActivity).isAllTeam)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamListResponseModel
                            XLog.d("GET TEAM DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {

                                response.team_struct?.let {
                                    tv_team_struct.text = it
                                }

                                if (response.member_list != null && response.member_list!!.size > 0) {
                                    member_list = response.member_list!!
                                    initAdapter(response.member_list!!)
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
                            XLog.d("GET TEAM DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            tv_no_data_available.visibility = View.VISIBLE
                        })
        )
    }

    private fun initAdapter(member_list: ArrayList<TeamListDataModel>) {
        tv_no_data_available.visibility = View.GONE
        tv_member_no.text = "Total member(s): " + member_list.size
        adapter = MemberListAdapter(mContext, member_list, object : MemberListAdapter.OnClickListener {

            override fun onZeroOrderClick(team: TeamListDataModel) {
                getOrderListFromZeroOrd(team.user_id, team)
            }

            override fun onBeatClick(team: TeamListDataModel) {
                (mContext as DashboardActivity).loadFragment(FragType.TeamBeatListFragment, true, team)
            }

            override fun getSize(size: Int) {
                tv_member_no.text = "Total member(s): " + size
            }

            override fun onLocClick(team: TeamListDataModel) {
                (mContext as DashboardActivity).loadFragment(FragType.MemberActivityFragment, true, team.user_id)
            }

            override fun onCollClick(team: TeamListDataModel) {
                getShopListForTeam(team.user_id, team)
            }

            override fun onPjpClick(team: TeamListDataModel) {
                (mContext as DashboardActivity).loadFragment(FragType.MemberPJPListFragment, true, team/*.user_id*/)
            }

            override fun onCallClick(team: TeamListDataModel) {
                if (TextUtils.isEmpty(team.contact_no) || team.contact_no.equals("null", ignoreCase = true)
                        || !AppUtils.isValidateMobile(team.contact_no)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_phn_no_unavailable))
                } else {
                    IntentActionable.initiatePhoneCall(mContext, team.contact_no)
                }
            }

            override fun onShopClick(team: TeamListDataModel) {
                CustomStatic.BreakageViewFromTeam_Name = team.user_name
                //(mContext as DashboardActivity).loadFragment(FragType.MemberShopListFragment, true, member_list[adapterPosition].user_id)
                CommonDialog.getInstance(AppUtils.hiFirstNameText() + "!", "What you like to do?", getString(R.string.total_shops), getString(R.string.new_visit_shop), false, false, true, object : CommonDialogClickListener {
                    override fun onLeftClick() {
                        checkTeamHierarchyList(team.user_name)
                        if (Pref.isShowPartyInAreaWiseTeam) {
                            (mContext as DashboardActivity).loadFragment(FragType.AreaListFragment, true, team.user_id)
                            (mContext as DashboardActivity).isAllMemberShop = true
                        } else {
                            CustomStatic.ShopFeedBachHisUserId = team.user_id
                            (mContext as DashboardActivity).loadFragment(FragType.MemberAllShopListFragment, true, team.user_id)
                        }
                    }

                    override fun onRightClick(editableData: String) {
                        checkTeamHierarchyList(team.user_name)

                        if (Pref.isShowPartyInAreaWiseTeam) {
                            (mContext as DashboardActivity).loadFragment(FragType.AreaListFragment, true, team.user_id)
                            (mContext as DashboardActivity).isAllMemberShop = false
                        } else {
                            CustomStatic.ShopFeedBachHisUserId = team.user_id
                            (mContext as DashboardActivity).loadFragment(FragType.MemberShopListFragment, true, team.user_id)
                        }
                    }

                }).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            override fun onTeamClick(team: TeamListDataModel) {
                //(mContext as DashboardActivity).isAddBackStack = true

                (mContext as DashboardActivity).apply {

                    /*teamHierarchy = if (teamHierarchy.isNotEmpty())
                        teamHierarchy + "-> " + member_list[adapterPosition].user_name
                    else
                        member_list[adapterPosition].user_name*/

                    teamHierarchy.add(team.user_name)

                    loadFragment(FragType.MemberListFragment, true, team.user_id)
                }
            }

            override fun onLeaveClick(team: TeamListDataModel) {
                (mContext as DashboardActivity).loadFragment(FragType.LeaveHome, true, team.user_id)
            }

        })
        rv_member_list.adapter = adapter
    }

    private fun checkTeamHierarchyList(user_name: String) {
        (mContext as DashboardActivity).teamHierarchy.apply {
            //if .(size == 0) {
            add(user_name)
            /*isListUpdated = true
        }*/
        }
    }

    fun updateMemberTeamHierarchy() {
        try {
            (mContext as DashboardActivity).teamHierarchy.apply {
                //if (isListUpdated) {
                removeAt(size - 1)
                /*  isListUpdated = false
            }*/
            }
            initSearchListener()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateTeamHierarchy() {
        /*if (tv_team_struct.visibility == View.GONE || !(mContext as DashboardActivity).teamHierarchy.contains("->")) {
            (mContext as DashboardActivity).teamHierarchy = ""
        }*/
        try {
            (mContext as DashboardActivity).teamHierarchy.apply {
                removeAt(size - 1)
                setHierarchyData()
            }
        } catch (ex: Exception) {

        }

    }

    fun updateItem() {
        getTeamList()
    }

    private fun getShopListForTeam(usr_id: String, obj: TeamListDataModel) {
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getShopList(Pref.session_token!!, usr_id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            var shopList = result as ShopListResponse
                            if (shopList.status == NetworkConstant.SUCCESS) {
                                if (shopList.data!!.shop_list!!.isNotEmpty()) {
                                    convertToShopListSetAdapter(shopList.data!!.shop_list!!, usr_id, obj)
                                } else {
                                    progress_wheel.stopSpinning()
                                    Toaster.msgShort(mContext, "No data found")
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                Toaster.msgShort(mContext, "No data found")
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                        })
        )
    }

    private fun convertToShopListSetAdapter(shop_list: List<ShopData>, usr_id: String, obj: TeamListDataModel) {
        val list: MutableList<ShopDtlsTeamEntity> = ArrayList()
        AppDatabase.getDBInstance()!!.shopDtlsTeamDao().deleteAll()
        for (i in 0 until shop_list.size) {
            val shopObj = ShopDtlsTeamEntity()
            shopObj.shop_id = shop_list[i].shop_id
            shopObj.shop_name = shop_list[i].shop_name

            list.add(shopObj)
            AppDatabase.getDBInstance()!!.shopDtlsTeamDao().insert(shopObj)
        }
        progress_wheel.stopSpinning()
        getOrderList(usr_id, obj)
    }

    private fun getOrderList(usr_id: String, obj: TeamListDataModel) {
        val repository = NewOrderListRepoProvider.provideOrderListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getOrderList(Pref.session_token!!, usr_id, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as NewOrderListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val order_details_list = response.order_list
                                if (order_details_list != null && order_details_list.isNotEmpty()) {
                                    doAsync {
                                        AppDatabase.getDBInstance()!!.orderDtlsTeamDao().delete()
                                        for (i in order_details_list.indices) {
                                            val orderDetailList = OrderDtlsTeamEntity()
                                            orderDetailList.date = order_details_list[i].order_date_time //AppUtils.convertToCommonFormat(order_details_list[i].date!!)
                                            if (!TextUtils.isEmpty(order_details_list[i].order_date_time))
                                                orderDetailList.only_date = AppUtils.convertDateTimeToCommonFormat(order_details_list[i].order_date_time!!)
                                            orderDetailList.shop_id = order_details_list[i].shop_id
                                            orderDetailList.description = ""

                                            if (!TextUtils.isEmpty(order_details_list[i].order_amount)) {
                                                val finalAmount = String.format("%.2f", order_details_list[i].order_amount?.toFloat())
                                                orderDetailList.amount = finalAmount
                                            }

                                            orderDetailList.isUploaded = true
                                            orderDetailList.order_id = order_details_list[i].order_id
                                            orderDetailList.collection = ""

                                            if (!TextUtils.isEmpty(order_details_list[i].order_lat) && !TextUtils.isEmpty(order_details_list[i].order_long)) {
                                                orderDetailList.order_lat = order_details_list[i].order_lat
                                                orderDetailList.order_long = order_details_list[i].order_long
                                            } else {
                                                orderDetailList.order_lat = order_details_list[i].shop_lat
                                                orderDetailList.order_long = order_details_list[i].shop_long
                                            }

                                            orderDetailList.patient_no = order_details_list[i].patient_no
                                            orderDetailList.patient_name = order_details_list[i].patient_name
                                            orderDetailList.patient_address = order_details_list[i].patient_address

                                            orderDetailList.Hospital = order_details_list[i].Hospital
                                            orderDetailList.Email_Address = order_details_list[i].Email_Address

                                            if (!TextUtils.isEmpty(order_details_list[i].scheme_amount)) {
                                                val finalScAmount = String.format("%.2f", order_details_list[i].scheme_amount?.toFloat())
                                                orderDetailList.scheme_amount = finalScAmount
                                            }

                                            AppDatabase.getDBInstance()!!.orderDtlsTeamDao().insert(orderDetailList)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            callCollTeam(usr_id, obj)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                        })
        )
    }

    private fun callCollTeam(usr_id: String, obj: TeamListDataModel) {
        val repository = NewCollectionListRepoProvider.newCollectionListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.collectionList(Pref.session_token!!, usr_id, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val collection = result as NewCollectionListResponseModel
                            if (collection.status == NetworkConstant.SUCCESS) {
                                if (collection.collection_list == null || collection.collection_list?.size!! == 0) {
                                    progress_wheel.stopSpinning()
                                } else {
                                    doAsync {

                                        AppDatabase.getDBInstance()!!.collDtlsTeamDao().delete()

                                        var collList: ArrayList<CollDtlsTeamEntity> = ArrayList()
                                        for (i in 0..collection.collection_list!!.size - 1) {
                                            var obj: CollDtlsTeamEntity = CollDtlsTeamEntity()
                                            obj.date = collection.collection_list!!.get(i).date
                                            obj.isUploaded = collection.collection_list!!.get(i).isUploaded
                                            obj.collection_id = collection.collection_list!!.get(i).collection_id
                                            obj.shop_id = collection.collection_list!!.get(i).shop_id
                                            obj.collection = collection.collection_list!!.get(i).collection
                                            obj.only_time = collection.collection_list!!.get(i).only_time
                                            obj.bill_id = collection.collection_list!!.get(i).bill_id
                                            obj.order_id = collection.collection_list!!.get(i).order_id
                                            obj.payment_id = collection.collection_list!!.get(i).payment_id
                                            obj.instrument_no = collection.collection_list!!.get(i).instrument_no
                                            obj.bank = collection.collection_list!!.get(i).bank
                                            obj.file_path = collection.collection_list!!.get(i).file_path
                                            obj.feedback = collection.collection_list!!.get(i).feedback
                                            obj.patient_no = collection.collection_list!!.get(i).patient_no
                                            obj.patient_name = collection.collection_list!!.get(i).patient_name
                                            obj.patient_address = collection.collection_list!!.get(i).patient_address
                                            obj.Hospital = collection.collection_list!!.get(i).Hospital
                                            obj.Email_Address = collection.collection_list!!.get(i).Email_Address

                                            collList.add(obj)
                                        }

                                        AppDatabase.getDBInstance()!!.collDtlsTeamDao().insertAll(collList)

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getBillTeam(usr_id, obj)
                                        }
                                    }
                                }
                            } else {
                                progress_wheel.stopSpinning()
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                        })
        )
    }

    private fun getBillTeam(usr_id: String, obj: TeamListDataModel) {
        val repository = BillingListRepoProvider.provideBillListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getBillList(Pref.session_token!!, usr_id, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BillingListResponseModel
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {
                                val billing_list = response.billing_list

                                if (billing_list != null && billing_list.isNotEmpty()) {

                                    doAsync {

                                        AppDatabase.getDBInstance()!!.billDtlsTeamDao().deleteAll()

                                        for (i in billing_list.indices) {
                                            val billing = BillDtlsTeamEntity()
                                            billing.bill_id = Pref.user_id + "_bill_" + System.currentTimeMillis()
                                            billing.invoice_no = billing_list[i].invoice_no
                                            billing.invoice_date = billing_list[i].invoice_date
                                            billing.invoice_amount = billing_list[i].invoice_amount
                                            billing.remarks = billing_list[i].remarks
                                            billing.order_id = billing_list[i].order_id
                                            billing.patient_no = billing_list[i].patient_no
                                            billing.patient_name = billing_list[i].patient_name
                                            billing.patient_address = billing_list[i].patient_address
                                            billing.isUploaded = true

                                            if (!TextUtils.isEmpty(billing_list[i].billing_image))
                                                billing.attachment = billing_list[i].billing_image

                                            AppDatabase.getDBInstance()!!.billDtlsTeamDao().insertAll(billing)

                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            (mContext as DashboardActivity).loadFragment(FragType.CollectionPendingTeamFrag, true, obj)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                }
                            } else {
                                progress_wheel.stopSpinning()

                            }

                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                        })
        )
    }


    private fun getOrderListFromZeroOrd(usr_id: String, obj: TeamListDataModel) {
        val repository = NewOrderListRepoProvider.provideOrderListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getOrderList(Pref.session_token!!, usr_id, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as NewOrderListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val order_details_list = response.order_list
                                if (order_details_list != null && order_details_list.isNotEmpty()) {
                                    doAsync {
                                        AppDatabase.getDBInstance()!!.orderDtlsTeamDao().delete()
                                        for (i in order_details_list.indices) {
                                            val orderDetailList = OrderDtlsTeamEntity()
                                            orderDetailList.date = order_details_list[i].order_date_time //AppUtils.convertToCommonFormat(order_details_list[i].date!!)
                                            if (!TextUtils.isEmpty(order_details_list[i].order_date_time))
                                                orderDetailList.only_date = AppUtils.convertDateTimeToCommonFormat(order_details_list[i].order_date_time!!)
                                            orderDetailList.shop_id = order_details_list[i].shop_id
                                            orderDetailList.description = ""

                                            if (!TextUtils.isEmpty(order_details_list[i].order_amount)) {
                                                val finalAmount = String.format("%.2f", order_details_list[i].order_amount?.toFloat())
                                                orderDetailList.amount = finalAmount
                                            }

                                            orderDetailList.isUploaded = true
                                            orderDetailList.order_id = order_details_list[i].order_id
                                            orderDetailList.collection = ""

                                            if (!TextUtils.isEmpty(order_details_list[i].order_lat) && !TextUtils.isEmpty(order_details_list[i].order_long)) {
                                                orderDetailList.order_lat = order_details_list[i].order_lat
                                                orderDetailList.order_long = order_details_list[i].order_long
                                            } else {
                                                orderDetailList.order_lat = order_details_list[i].shop_lat
                                                orderDetailList.order_long = order_details_list[i].shop_long
                                            }

                                            orderDetailList.patient_no = order_details_list[i].patient_no
                                            orderDetailList.patient_name = order_details_list[i].patient_name
                                            orderDetailList.patient_address = order_details_list[i].patient_address

                                            orderDetailList.Hospital = order_details_list[i].Hospital
                                            orderDetailList.Email_Address = order_details_list[i].Email_Address

                                            if (!TextUtils.isEmpty(order_details_list[i].scheme_amount)) {
                                                val finalScAmount = String.format("%.2f", order_details_list[i].scheme_amount?.toFloat())
                                                orderDetailList.scheme_amount = finalScAmount
                                            }

                                            AppDatabase.getDBInstance()!!.orderDtlsTeamDao().insert(orderDetailList)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getShopListForZeroOrd(usr_id, obj)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    Toaster.msgShort(mContext, "No data found")
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                Toaster.msgShort(mContext, "No data found")
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                        })
        )
    }

    private fun getShopListForZeroOrd(usr_id: String, obj: TeamListDataModel) {
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getShopList(Pref.session_token!!, usr_id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            var shopList = result as ShopListResponse
                            if (shopList.status == NetworkConstant.SUCCESS) {
                                if (shopList.data!!.shop_list!!.isNotEmpty()) {
                                    convertToAllShopList(shopList.data!!.shop_list!!, usr_id, obj)
                                } else {
                                    progress_wheel.stopSpinning()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                        })
        )
    }

    private fun convertToAllShopList(shop_list: List<ShopData>, usr_id: String, obj: TeamListDataModel) {

        AppDatabase.getDBInstance()!!.teamAllShopDBModelDao().deleteAll()

        val list: MutableList<TeamAllShopDBModelEntity> = ArrayList()
        for (i in 0 until shop_list.size) {
            val shopObj = TeamAllShopDBModelEntity()
            shopObj.shop_id = shop_list[i].shop_id
            shopObj.shopName = shop_list[i].shop_name
            shopObj.shopImageLocalPath = shop_list[i].Shop_Image
            shopObj.shopLat = shop_list[i].shop_lat!!.toDouble()
            shopObj.shopLong = shop_list[i].shop_long!!.toDouble()
            shopObj.duration = ""
            shopObj.endTimeStamp = ""
            shopObj.timeStamp = ""
            shopObj.dateOfBirth = shop_list[i].dob
            shopObj.dateOfAniversary = shop_list[i].date_aniversary
            shopObj.visitDate = AppUtils.getCurrentDate()
            if (shop_list[i].total_visit_count == "0")
                shopObj.totalVisitCount = "1"
            else
                shopObj.totalVisitCount = shop_list[i].total_visit_count
            shopObj.address = shop_list[i].address
            shopObj.ownerEmailId = shop_list[i].owner_email
            shopObj.ownerContactNumber = shop_list[i].owner_contact_no
            shopObj.pinCode = shop_list[i].pin_code
            shopObj.isUploaded = true
            shopObj.ownerName = shop_list[i].owner_name
            shopObj.user_id = Pref.user_id
            shopObj.orderValue = 0
            shopObj.type = shop_list[i].type
            shopObj.assigned_to_dd_id = shop_list[i].assigned_to_dd_id
            shopObj.assigned_to_pp_id = shop_list[i].assigned_to_pp_id
            shopObj.isAddressUpdated = shop_list[i].isAddressUpdated == "1"
            shopObj.is_otp_verified = shop_list[i].is_otp_verified
            shopObj.added_date = shop_list[i].added_date

            if (shop_list[i].amount == null || shop_list[i].amount == "0.00")
                shopObj.amount = ""
            else
                shopObj.amount = shop_list[i].amount

            if (shop_list[i].last_visit_date!!.contains("."))
                shopObj.lastVisitedDate =
                        AppUtils.changeAttendanceDateFormat(shop_list[i].last_visit_date!!.split(".")[0])
            else
                shopObj.lastVisitedDate =
                        AppUtils.changeAttendanceDateFormat(shop_list[i].last_visit_date!!)

            if (shopObj.lastVisitedDate == AppUtils.getCurrentDateChanged())
                shopObj.visited = true
            else
                shopObj.visited = false

            if (shop_list[i].entity_code == null)
                shopObj.entity_code = ""
            else
                shopObj.entity_code = shop_list[i].entity_code


            if (shop_list[i].area_id == null)
                shopObj.area_id = ""
            else
                shopObj.area_id = shop_list[i].area_id

            if (TextUtils.isEmpty(shop_list[i].model_id))
                shopObj.model_id = ""
            else
                shopObj.model_id = shop_list[i].model_id

            if (TextUtils.isEmpty(shop_list[i].primary_app_id))
                shopObj.primary_app_id = ""
            else
                shopObj.primary_app_id = shop_list[i].primary_app_id

            if (TextUtils.isEmpty(shop_list[i].secondary_app_id))
                shopObj.secondary_app_id = ""
            else
                shopObj.secondary_app_id = shop_list[i].secondary_app_id

            if (TextUtils.isEmpty(shop_list[i].lead_id))
                shopObj.lead_id = ""
            else
                shopObj.lead_id = shop_list[i].lead_id

            if (TextUtils.isEmpty(shop_list[i].stage_id))
                shopObj.stage_id = ""
            else
                shopObj.stage_id = shop_list[i].stage_id

            if (TextUtils.isEmpty(shop_list[i].funnel_stage_id))
                shopObj.funnel_stage_id = ""
            else
                shopObj.funnel_stage_id = shop_list[i].funnel_stage_id

            if (TextUtils.isEmpty(shop_list[i].booking_amount))
                shopObj.booking_amount = ""
            else
                shopObj.booking_amount = shop_list[i].booking_amount

            if (TextUtils.isEmpty(shop_list[i].type_id))
                shopObj.type_id = ""
            else
                shopObj.type_id = shop_list[i].type_id

            shopObj.family_member_dob = shop_list[i].family_member_dob
            shopObj.director_name = shop_list[i].director_name
            shopObj.person_name = shop_list[i].key_person_name
            shopObj.person_no = shop_list[i].phone_no
            shopObj.add_dob = shop_list[i].addtional_dob
            shopObj.add_doa = shop_list[i].addtional_doa

            shopObj.doc_degree = shop_list[i].degree
            shopObj.doc_family_dob = shop_list[i].doc_family_member_dob
            shopObj.specialization = shop_list[i].specialization
            shopObj.patient_count = shop_list[i].average_patient_per_day
            shopObj.category = shop_list[i].category
            shopObj.doc_address = shop_list[i].doc_address
            shopObj.doc_pincode = shop_list[i].doc_pincode
            shopObj.chamber_status = shop_list[i].is_chamber_same_headquarter.toInt()
            shopObj.remarks = shop_list[i].is_chamber_same_headquarter_remarks
            shopObj.chemist_name = shop_list[i].chemist_name
            shopObj.chemist_address = shop_list[i].chemist_address
            shopObj.chemist_pincode = shop_list[i].chemist_pincode
            shopObj.assistant_name = shop_list[i].assistant_name
            shopObj.assistant_no = shop_list[i].assistant_contact_no
            shopObj.assistant_dob = shop_list[i].assistant_dob
            shopObj.assistant_doa = shop_list[i].assistant_doa
            shopObj.assistant_family_dob = shop_list[i].assistant_family_dob

            if (TextUtils.isEmpty(shop_list[i].entity_id))
                shopObj.entity_id = ""
            else
                shopObj.entity_id = shop_list[i].entity_id

            if (TextUtils.isEmpty(shop_list[i].party_status_id))
                shopObj.party_status_id = ""
            else
                shopObj.party_status_id = shop_list[i].party_status_id

            if (TextUtils.isEmpty(shop_list[i].retailer_id))
                shopObj.retailer_id = ""
            else
                shopObj.retailer_id = shop_list[i].retailer_id

            if (TextUtils.isEmpty(shop_list[i].dealer_id))
                shopObj.dealer_id = ""
            else
                shopObj.dealer_id = shop_list[i].dealer_id

            if (TextUtils.isEmpty(shop_list[i].beat_id))
                shopObj.beat_id = ""
            else
                shopObj.beat_id = shop_list[i].beat_id

            if (TextUtils.isEmpty(shop_list[i].account_holder))
                shopObj.account_holder = ""
            else
                shopObj.account_holder = shop_list[i].account_holder

            if (TextUtils.isEmpty(shop_list[i].account_no))
                shopObj.account_no = ""
            else
                shopObj.account_no = shop_list[i].account_no

            if (TextUtils.isEmpty(shop_list[i].bank_name))
                shopObj.bank_name = ""
            else
                shopObj.bank_name = shop_list[i].bank_name

            if (TextUtils.isEmpty(shop_list[i].ifsc_code))
                shopObj.ifsc_code = ""
            else
                shopObj.ifsc_code = shop_list[i].ifsc_code

            if (TextUtils.isEmpty(shop_list[i].upi))
                shopObj.upi_id = ""
            else
                shopObj.upi_id = shop_list[i].upi

            if (TextUtils.isEmpty(shop_list[i].assigned_to_shop_id))
                shopObj.assigned_to_shop_id = ""
            else
                shopObj.assigned_to_shop_id = shop_list[i].assigned_to_shop_id

            shopObj.project_name = shop_list[i].project_name
            shopObj.landline_number = shop_list[i].landline_number
            shopObj.agency_name = shop_list[i].agency_name
            /*10-2-2022*/
            shopObj.alternateNoForCustomer = shop_list[i].alternateNoForCustomer
            shopObj.whatsappNoForCustomer = shop_list[i].whatsappNoForCustomer
            shopObj.isShopDuplicate = shop_list[i].isShopDuplicate


            list.add(shopObj)
            AppDatabase.getDBInstance()!!.teamAllShopDBModelDao().insert(shopObj)
        }
        progress_wheel.stopSpinning()

        (mContext as DashboardActivity).loadFragment(FragType.TeamRepeatOrderFrag, true, obj)
    }

}
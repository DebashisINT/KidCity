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
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.domain.MemberEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamListResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 03-Jul-20.
 */
class OfflineMemberListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_member_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_member_list_main: RelativeLayout
    private lateinit var tv_team_struct: AppCustomTextView

    private var userId = ""
    private var isFirstScreen = false
    private var adapter: OfflineMemberAdapter?= null
    private var memberList: List<MemberEntity> ?= null

    private var isApiCalledForTeam :Boolean = false

    companion object {

        fun newInstance(userId: Any): OfflineMemberListFragment {
            val fragment = OfflineMemberListFragment()

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

        setHierarchyData()
        if(Pref.isOfflineTeam){
            getDataFromDb()
        }else{
            refreshList()
        }

    }

    private fun getDataFromDb() {
        /*val list = AppDatabase.getDBInstance()?.memberDao()?.getSingleUserMember(userId)
        if (list != null && list.isNotEmpty())
            initAdapter(list)
        else
            tv_no_data_available.visibility = View.VISIBLE*/

        if (isFirstScreen) {
            val memberList = ArrayList<MemberEntity>()

            val list = AppDatabase.getDBInstance()?.memberDao()?.getSingleUserMember(userId)

            if(isApiCalledForTeam == false && (list == null || list.size!! ==0)){
                refreshList()
            }

            list?.forEach {
                memberList.add(it)
            }


            val userList = AppDatabase.getDBInstance()?.memberDao()?.getUserSuperWise(userId)
            userList?.forEach {
                memberList.add(it)
            }

            if (memberList.isNotEmpty())
                initAdapter(memberList)
            else
                tv_no_data_available.visibility = View.VISIBLE
        } else {
            val userList = AppDatabase.getDBInstance()?.memberDao()?.getUserSuperWise(userId)
            if (userList != null && userList.isNotEmpty())
                initAdapter(userList)
            else
                tv_no_data_available.visibility = View.VISIBLE
        }
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
                    memberList?.let {
                        adapter?.refreshList(it as ArrayList<MemberEntity>)
                    }
                    //initAdapter(shop_list!!)
                } else {
                    adapter?.filter?.filter(query)
                }
            }
        })
    }


    private fun initAdapter(member_list: List<MemberEntity>) {
        memberList = member_list

        tv_no_data_available.visibility = View.GONE
        adapter = OfflineMemberAdapter(mContext, member_list, object : OfflineMemberAdapter.OnClickListener {

            override fun onLocClick(member: MemberEntity) {
                (mContext as DashboardActivity).loadFragment(FragType.MemberActivityFragment, true, member.user_id!!)
            }

            override fun onPjpClick(member: MemberEntity) {
                (mContext as DashboardActivity).loadFragment(FragType.MemberPJPListFragment, true, member/*.user_id*/)
            }

            override fun onCallClick(member: MemberEntity) {
                if (TextUtils.isEmpty(member.contact_no) || member.contact_no.equals("null", ignoreCase = true)
                        || !AppUtils.isValidateMobile(member.contact_no!!)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_phn_no_unavailable))
                } else {
                    IntentActionable.initiatePhoneCall(mContext, member.contact_no)
                }
            }

            override fun onShopClick(member: MemberEntity) {
                //(mContext as DashboardActivity).loadFragment(FragType.MemberShopListFragment, true, member_list[adapterPosition].user_id)
                CommonDialog.getInstance(AppUtils.hiFirstNameText(), "What you like to do?", getString(R.string.total_shops), getString(R.string.new_visit_shop), false, false, true, object : CommonDialogClickListener {
                    override fun onLeftClick() {
                        checkTeamHierarchyList(member.user_name!!)
                        if (Pref.isShowPartyInAreaWiseTeam) {
                            (mContext as DashboardActivity).loadFragment(FragType.OfflineAreaListFragment, true, member.user_id!!)
                            (mContext as DashboardActivity).isAllMemberShop = true
                        } else {
                                CustomStatic.ShopFeedBachHisUserId = member.user_id!!
                            (mContext as DashboardActivity).loadFragment(FragType.OfflineAllShopListFragment, true, member.user_id!!)
                        }
                    }

                    override fun onRightClick(editableData: String) {
                        checkTeamHierarchyList(member.user_name!!)

                        if (Pref.isShowPartyInAreaWiseTeam) {
                            (mContext as DashboardActivity).loadFragment(FragType.OfflineAreaListFragment, true, member.user_id!!)
                            (mContext as DashboardActivity).isAllMemberShop = false
                        } else {
                            CustomStatic.ShopFeedBachHisUserId = member.user_id!!
                            (mContext as DashboardActivity).loadFragment(FragType.OfflineShopListFragment, true, member.user_id!!)
                        }
                    }

                }).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            override fun onTeamClick(member: MemberEntity) {
                //(mContext as DashboardActivity).isAddBackStack = true

                (mContext as DashboardActivity).apply {

                    /*teamHierarchy = if (teamHierarchy.isNotEmpty())
                        teamHierarchy + "-> " + member_list[adapterPosition].user_name
                    else
                        member_list[adapterPosition].user_name*/

                    teamHierarchy.add(member.user_name!!)

                    loadFragment(FragType.OfflineMemberListFragment, true, member.user_id!!)
                }
            }

            override fun onJobClick(member: MemberEntity) {
                Pref.IsMyJobFromTeam=true
                (mContext as DashboardActivity).loadFragment(FragType.MyJobsFragment, true, member.user_id!!)
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

        (mContext as DashboardActivity).teamHierarchy.apply {
            removeAt(size - 1)
            setHierarchyData()
        }
    }

    fun refreshList() {
        progress_wheel.spin()

        doAsync {
            val list = AppDatabase.getDBInstance()?.memberDao()?.getAll()

            uiThread {
                progress_wheel.stopSpinning()

                if (list != null && list.isNotEmpty())
                    callMemberListApi(list[list.size - 1].date_time!!)
                else
                    callMemberListApi("")
            }
        }
    }

    private fun callMemberListApi(date: String) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = TeamRepoProvider.teamRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.offlineTeamList(date)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.member_list != null && response.member_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.member_list?.forEach {
                                            val member = MemberEntity()
                                            AppDatabase.getDBInstance()?.memberDao()?.insertAll(member.apply {
                                                user_id = it.user_id
                                                user_name = it.user_name
                                                contact_no = it.contact_no
                                                super_id = it.super_id
                                                super_name = it.super_name
                                                date_time = AppUtils.getCurrentISODateTime()
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            isApiCalledForTeam=true
                                            getDataFromDb()
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
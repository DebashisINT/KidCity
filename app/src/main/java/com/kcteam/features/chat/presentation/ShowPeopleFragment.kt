package com.kcteam.features.chat.presentation

import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.SearchListener
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.chat.api.ChatRepoProvider
import com.kcteam.features.chat.model.GroupUserDataModel
import com.kcteam.features.chat.model.GroupUserResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ShowPeopleFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var rv_user_list: RecyclerView
    private lateinit var rl_add_group: RelativeLayout
    private lateinit var fab: FloatingActionButton

    private var grpUserAdapter: GroupUserListAdapter?= null
    private var userList: ArrayList<GroupUserDataModel>?= null
    private var groupNotSelectedUserList: ArrayList<GroupUserDataModel>?= null
    private var grpID = ""

    private val groupUserList: ArrayList<GroupUserDataModel> by lazy {
        ArrayList<GroupUserDataModel>()
    }

    companion object {
        fun newInstance(grpID: Any): ShowPeopleFragment {
            val fragment = ShowPeopleFragment()

            val bundle = Bundle()
            bundle.putString("grpID", grpID as String?)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        grpID = arguments?.getString("grpID").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_show_people, container, false)

        initView(view)
        getUserListApi()

        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    grpUserAdapter?.refreshList(groupUserList!!)
                } else {
                    grpUserAdapter?.filter?.filter(query)
                }
            }
        })

        return view
    }

    private fun initView(view: View) {
        view.apply {
            progress_wheel = findViewById(R.id.progress_wheel)
            tv_no_data = findViewById(R.id.tv_no_data)
            rv_user_list = findViewById(R.id.rv_user_list)
            rl_add_group = findViewById(R.id.rl_add_group)
            fab = findViewById(R.id.fab)
        }
        progress_wheel.stopSpinning()
        rv_user_list.layoutManager = LinearLayoutManager(mContext)

        rl_add_group.setOnClickListener(null)
        fab.setOnClickListener {
            (mContext as DashboardActivity).loadFragment(FragType.AddPeopleFragment, true, grpID)
        }
    }

    private fun getUserListApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = ChatRepoProvider.provideChatRepository()
        BaseActivity.compositeDisposable.add(
                repository.getGroupUserList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as GroupUserResponseModel
                            XLog.d("Get User List STATUS: " + response.status)
                            if (response.status == NetworkConstant.SUCCESS) {
                                progress_wheel.stopSpinning()
                                tv_no_data.visibility = View.GONE
                                userList = response.group_user_list

                                getGrpNotSelectedUserList()
                            }
                            else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            if (error != null)
                                XLog.d("Get User List ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun getGrpNotSelectedUserList() {
        progress_wheel.spin()
        val repository = ChatRepoProvider.provideChatRepository()
        BaseActivity.compositeDisposable.add(
                repository.memberUserList(grpID)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as GroupUserResponseModel
                            XLog.d("Get Group Not Selected User List STATUS: " + response.status)
                            if (response.status == NetworkConstant.SUCCESS) {
                                progress_wheel.stopSpinning()
                                tv_no_data.visibility = View.GONE
                                groupNotSelectedUserList = response.group_user_list

                                val idList = ArrayList<String>()
                                groupNotSelectedUserList?.forEach {
                                    idList.add(it.id)
                                }

                                for (i in userList!!.indices) {
                                    if (!idList.contains(userList?.get(i)?.id))
                                        groupUserList.add(userList?.get(i)!!)
                                }

                                setAdapter()
                            }
                            else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            if (error != null)
                                XLog.d("Get Group Not Selected User List ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun setAdapter() {
        grpUserAdapter = GroupUserListAdapter(mContext, groupUserList) {
        }
        rv_user_list.adapter = grpUserAdapter
    }
}
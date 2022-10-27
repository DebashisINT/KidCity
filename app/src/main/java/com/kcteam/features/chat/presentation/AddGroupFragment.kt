package com.kcteam.features.chat.presentation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.chat.api.ChatRepoProvider
import com.kcteam.features.chat.model.ChatUserResponseModel
import com.kcteam.features.chat.model.GroupUserDataModel
import com.kcteam.features.chat.model.GroupUserResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AddGroupFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var et_grp_name: AppCustomEditText
    private lateinit var iv_done_btn: AppCompatImageView
    private lateinit var rv_selected_list: RecyclerView
    private lateinit var rv_user_list: RecyclerView
    private lateinit var rl_add_group: RelativeLayout

    private var grpUserAdapter: GroupUserListAdapter?= null
    private var selectedUserAdapter: SelectedUserListAdapter?= null
    private var groupUserList: ArrayList<GroupUserDataModel>?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_group, container, false)

        initView(view)
        getChatUserListApi()

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
            et_grp_name = findViewById(R.id.et_grp_name)
            iv_done_btn = findViewById(R.id.iv_done_btn)
            rv_selected_list = findViewById(R.id.rv_selected_list)
            rv_user_list = findViewById(R.id.rv_user_list)
            rl_add_group = findViewById(R.id.rl_add_group)
        }
        progress_wheel.stopSpinning()
        rv_user_list.layoutManager = LinearLayoutManager(mContext)
        rv_selected_list.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
        selectedUserAdapter = SelectedUserListAdapter(mContext) { it, position ->
            for (i in groupUserList?.indices!!) {
                if (groupUserList?.get(i)?.id == it.id) {
                    groupUserList?.get(i)?.isSelected = false
                    grpUserAdapter?.refreshList(groupUserList!!)
                    selectedUserAdapter?.removeItem(position)
                    break
                }
            }
        }
        rv_selected_list.adapter = selectedUserAdapter

        rl_add_group.setOnClickListener(null)
        iv_done_btn.setOnClickListener {
            when {
                TextUtils.isEmpty(et_grp_name.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_grp_name))
                selectedUserAdapter?.selectedUserList!!.isEmpty() -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_user))
                else -> callAddGrpApi()
            }
        }
    }

    private fun getChatUserListApi() {
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
                            XLog.d("Get Group User List STATUS: " + response.status)
                            if (response.status == NetworkConstant.SUCCESS) {
                                progress_wheel.stopSpinning()
                                tv_no_data.visibility = View.GONE
                                groupUserList = response.group_user_list
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
                                XLog.d("Get Group User List ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun callAddGrpApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        var ids = ""

        selectedUserAdapter?.selectedUserList?.forEachIndexed { i, it ->
            if (i == selectedUserAdapter?.selectedUserList?.size!! - 1)
                ids += it.id
            else
                ids = ids + it.id + ","
        }

        ids = ids + "," + Pref.user_id

        progress_wheel.spin()
        val repository = ChatRepoProvider.provideChatRepository()
        BaseActivity.compositeDisposable.add(
                repository.addGroup(AppUtils.encodeEmojiAndText(et_grp_name.text.toString().trim()), ids)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("Add Group STATUS: " + response.status)
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            if (response.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).isRefreshChatUserList = true
                                (mContext as DashboardActivity).onBackPressed()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            if (error != null)
                                XLog.d("Add Group ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun setAdapter() {
        grpUserAdapter = GroupUserListAdapter(mContext, groupUserList) {

            for (i in groupUserList?.indices!!) {
                if (groupUserList?.get(i)?.id == it.id) {
                    if (groupUserList?.get(i)?.isSelected!!) {
                        groupUserList?.get(i)?.isSelected = false

                        for (j in selectedUserAdapter?.selectedUserList!!.indices) {
                            if (it.id == selectedUserAdapter?.selectedUserList?.get(j)?.id) {
                                selectedUserAdapter?.removeItem(j)
                                break
                            }
                        }
                    }
                    else {
                        groupUserList?.get(i)?.isSelected = true
                        selectedUserAdapter?.addItem(groupUserList?.get(i)!!)
                    }
                    break
                }
            }

            /*if (groupUserList?.get(it)?.isSelected!!) {
                groupUserList?.get(it)?.isSelected = false

                for (i in selectedUserAdapter?.selectedUserList!!.indices) {
                    if (groupUserList?.get(it)?.id == selectedUserAdapter?.selectedUserList?.get(i)?.id) {
                        selectedUserAdapter?.removeItem(i)
                        break
                    }
                }
            }
            else {
                groupUserList?.get(it)?.isSelected = true
                selectedUserAdapter?.addItem(groupUserList?.get(it)!!)
            }*/
            grpUserAdapter?.refreshList(groupUserList!!)
        }
        rv_user_list.adapter = grpUserAdapter
    }
}
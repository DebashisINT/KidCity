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
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.areaList.AreaListRepoProvider
import com.kcteam.features.addshop.model.AreaListDataModel
import com.kcteam.features.addshop.model.AreaListResponseModel
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Saikat on 14-May-20.
 */
class AreaListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_area_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_area_main: RelativeLayout
    private lateinit var tv_team_struct: AppCustomTextView

    private var userId = ""


    companion object {

        fun newInstance(userId: Any): AreaListFragment {
            val fragment = AreaListFragment()

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
        val view = inflater.inflate(R.layout.fragment_area_list, container, false)

        initView(view)

        return view
    }

    private fun initView(view: View) {
        view.apply {
            rv_area_list = findViewById(R.id.rv_area_list)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
            progress_wheel = findViewById(R.id.progress_wheel)
            rl_area_main = findViewById(R.id.rl_area_main)
            tv_team_struct = findViewById(R.id.tv_team_struct)
        }

        rv_area_list.layoutManager = LinearLayoutManager(mContext)
        progress_wheel.stopSpinning()
        rl_area_main.setOnClickListener(null)

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
/*
        val areaList = AppDatabase.getDBInstance()?.areaListDao()?.getAll() as ArrayList<AreaListEntity>

        if (areaList != null && areaList.size > 0)
            initAdapter(areaList)
        else {*/
            if (!TextUtils.isEmpty(Pref.profile_city)) {
                if (AppUtils.isOnline(mContext))
                    getAreaListApi()
                else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            } else {
                showProfileAlert()
            }
        //}
    }

    private fun initAdapter(areaList: ArrayList<AreaListDataModel>?) {
        tv_no_data_available.visibility = View.GONE
        rv_area_list.adapter = AreaListAdapter(mContext, areaList!!, { position: Int ->

            (mContext as DashboardActivity).run {
                areaId = areaList[position].area_id

                if (isAllMemberShop) {
                    (mContext as DashboardActivity).loadFragment(FragType.MemberAllShopListFragment, true, userId)
                }
                else
                    (mContext as DashboardActivity).loadFragment(FragType.MemberShopListFragment, true, userId)
            }
        })
    }

    private fun getAreaListApi() {
        val repository = AreaListRepoProvider.provideAreaListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.areaList(Pref.profile_city, userId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AreaListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.area_list

                                if (list != null && list.isNotEmpty()) {

                                    /*doAsync {

                                        list.forEach {
                                            val area = AreaListEntity()
                                            AppDatabase.getDBInstance()?.areaListDao()?.insert(area.apply {
                                                area_id = it.area_id
                                                area_name = it.area_name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            initAdapter(AppDatabase.getDBInstance()?.areaListDao()?.getAll() as ArrayList<AreaListEntity>)
                                        }
                                    }*/

                                    progress_wheel.stopSpinning()
                                    initAdapter(list)

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

    private fun showProfileAlert() {
        CommonDialog.getInstance(getString(R.string.app_name), "Please update your profile", getString(R.string.cancel),
                getString(R.string.ok), true, false, object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                (mContext as DashboardActivity).loadFragment(FragType.MyProfileFragment, false, "")
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }
}
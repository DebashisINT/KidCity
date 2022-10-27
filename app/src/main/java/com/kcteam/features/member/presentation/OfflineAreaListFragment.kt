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
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.TeamAreaEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.api.TeamRepoProvider
import com.kcteam.features.member.model.TeamAreaListResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 06-Jul-20.
 */
class OfflineAreaListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_area_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_area_main: RelativeLayout
    private lateinit var tv_team_struct: AppCustomTextView

    private var userId = ""


    companion object {

        fun newInstance(userId: Any): OfflineAreaListFragment {
            val fragment = OfflineAreaListFragment()

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

        val areaList = AppDatabase.getDBInstance()?.memberAreaDao()?.getAll() as ArrayList<TeamAreaEntity>

        if (areaList != null && areaList.size > 0)
            prepareSingleUserArea(areaList)
        else {
            if (!TextUtils.isEmpty(Pref.profile_city)) {
                if (AppUtils.isOnline(mContext))
                    getAreaListApi()
                else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            } else {
                showProfileAlert()
            }
        }
    }

    private fun initAdapter(areaList: ArrayList<TeamAreaEntity>) {

        if (areaList.isNotEmpty())
            tv_no_data_available.visibility = View.GONE
        else
            tv_no_data_available.visibility = View.VISIBLE

        rv_area_list.adapter = OfflineAreaAdapter(mContext, areaList) { position: Int ->

            (mContext as DashboardActivity).run {
                areaId = areaList[position].area_id!!

                if (isAllMemberShop) {
                    (mContext as DashboardActivity).loadFragment(FragType.OfflineAllShopListFragment, true, userId)
                } else
                    (mContext as DashboardActivity).loadFragment(FragType.OfflineShopListFragment, true, userId)
            }
        }
    }

    private fun getAreaListApi() {
        val repository = TeamRepoProvider.teamRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.teamAreaList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TeamAreaListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.area_list

                                if (list != null && list.isNotEmpty()) {

                                    AppDatabase.getDBInstance()?.memberAreaDao()?.deleteAll()

                                    doAsync {

                                        list.forEach {
                                            val area = TeamAreaEntity()
                                            AppDatabase.getDBInstance()?.memberAreaDao()?.insertAll(area.apply {
                                                area_id = it.area_id
                                                area_name = it.area_name
                                                user_id = it.user_id
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            prepareSingleUserArea(AppDatabase.getDBInstance()?.memberAreaDao()?.getAll())
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

    private fun prepareSingleUserArea(areaList: List<TeamAreaEntity>?) {
        val newAreaList = ArrayList<TeamAreaEntity>()
        areaList?.forEach {
            if (it.user_id?.contains(userId)!!)
                newAreaList.add(it)
        }

        initAdapter(newAreaList)
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
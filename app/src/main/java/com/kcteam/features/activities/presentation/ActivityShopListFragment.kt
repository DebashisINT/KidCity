package com.kcteam.features.activities.presentation

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.domain.ActivityEntity
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.activities.api.ActivityRepoProvider
import com.kcteam.features.activities.model.ActivityListResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class ActivityShopListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var tv_count_no: AppCustomTextView
    private lateinit var rv_shop_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel

    private var adapter: ActivityListAdapter?= null
    private var shopList: ArrayList<AddShopDBModelEntity>?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_activity_shop_list, container, false)

        initView(view)
        initAdapter()

        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                try {
                    if (query.isBlank()) {
                        adapter?.refreshList(shopList!!)
                        tv_count_no.text = "Total count: " + shopList?.size
                    } else
                        adapter?.filter?.filter(query)

                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        return view
    }

    private fun initView(view: View) {
        view.apply {
            tv_count_no = findViewById(R.id.tv_count_no)
            rv_shop_list = findViewById(R.id.rv_shop_list)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
            progress_wheel = findViewById(R.id.progress_wheel)
        }

        progress_wheel.stopSpinning()

        rv_shop_list.layoutManager = LinearLayoutManager(mContext)
    }

    private fun initAdapter() {
        val list= AppDatabase.getDBInstance()?.addShopEntryDao()?.all

        val activityShopList = ArrayList<AddShopDBModelEntity>()
        val notActivityShopList = ArrayList<AddShopDBModelEntity>()
        shopList = ArrayList()

        list?.forEach {
            val activityList = AppDatabase.getDBInstance()?.activDao()?.getShopIdWise(it.shop_id)

            if (activityList != null && activityList.isNotEmpty())
                activityShopList.add(it)
            else
                notActivityShopList.add(it)
        }

        shopList?.addAll(activityShopList)
        shopList?.addAll(notActivityShopList)

        if (list != null && list.isNotEmpty()) {
            tv_no_data_available.visibility = View.GONE
            tv_count_no.text = "Total count: " + list.size

            adapter = ActivityListAdapter(mContext, list as ArrayList<AddShopDBModelEntity>?, {
                if (TextUtils.isEmpty(it.ownerContactNumber) || it.ownerContactNumber.equals("null", ignoreCase = true)
                        || !AppUtils.isValidateMobile(it.ownerContactNumber!!)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_phn_no_unavailable))
                } else {
                    IntentActionable.initiatePhoneCall(mContext, it.ownerContactNumber)
                }
            }, {
                (mContext as DashboardActivity).openLocationMap(it, false)
            }, {
                when (it.type) {
                    "7" -> {
                        (mContext as DashboardActivity).isFromShop = false
                        (mContext as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, it)
                    }
                    "8" -> {
                        (mContext as DashboardActivity).isFromShop = false
                        (mContext as DashboardActivity).loadFragment(FragType.DoctorActivityListFragment, true, it)
                    }
                    else -> (mContext as DashboardActivity).loadFragment(FragType.ActivityDetailsListFragment, true, it.shop_id)
                }
            }, {
                tv_count_no.text = "Total count: $it"
            })
            rv_shop_list.adapter = adapter
        }
        else
            tv_no_data_available.visibility = View.VISIBLE
    }
}
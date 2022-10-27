package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.types.DashboardType
import com.kcteam.app.types.FragType
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.myorder.presentation.MyOrderListClickListener

/**
 * Created by rp : 31-10-2017:16:49
 */
class YesterdayRouteFragment : BaseFragment(), View.OnClickListener {


    private lateinit var fab: FloatingActionButton
    private lateinit var mContext: Context
    private lateinit var mRoutListAdapterYesterday: YesterdayRouteAdapter
    private lateinit var mRouteRecyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var mFragment: DashboardType = DashboardType.Home

    fun getInstance(objects: Any): YesterdayRouteFragment {
        val cardFragment = YesterdayRouteFragment()
//        val fragType:DashboardType=objects as DashboardType
        mFragment = objects as DashboardType
        return cardFragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater!!.inflate(R.layout.fragment_dashboard, container, false)
        initView(view)
        return view

    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(view: View?) {
        fab = view!!.findViewById(R.id.fab)
        fab.setOnClickListener(this)
        mRouteRecyclerView = view.findViewById(R.id.my_activity_list_RCV)
        initAdapter()

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.fab -> {
                (mContext as DashboardActivity).loadFragment(FragType.AddShopFragment, true, "")
            }
        }
    }


    private fun initAdapter() {
        mRoutListAdapterYesterday = YesterdayRouteAdapter(this!!.context!!,mFragment, object : MyOrderListClickListener {
            override fun OnOrderListClick(position: Int) {
                (mContext as DashboardActivity).openLocationWithTrack()
            }
        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        mRouteRecyclerView.layoutManager = layoutManager
        mRouteRecyclerView.adapter = mRoutListAdapterYesterday

    }


}
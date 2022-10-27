package com.kcteam.features.lead

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.NewQuotation.ViewAllQuotListFragment
import com.kcteam.features.lead.model.CustomerLeadList
import com.kcteam.features.lead.model.CustomerListReq
import com.kcteam.widgets.AppCustomTextView
import com.google.android.gms.common.api.internal.LifecycleCallback.getFragment
import com.pnikosis.materialishprogress.ProgressWheel

class LeadFrag: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var tv_pendingTab: AppCustomTextView
    private lateinit var tv_inProcessTab: AppCustomTextView
    private lateinit var leadViewPagerAdapter: LeadViewPagerAdapter
    private lateinit var vp_ViewPager: ViewPager

    private lateinit var progress_wheel:ProgressWheel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var reqData_inProcess_LeadFrag = CustomerListReq()
        var reqData_pending_LeadFrag = CustomerListReq()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_lead, container, false)
        initView(view)
        initAdapter()
         reqData_inProcess_LeadFrag = CustomerListReq()
         reqData_pending_LeadFrag = CustomerListReq()
        return view
    }

    private fun initView(view: View){
        tv_pendingTab = view.findViewById(R.id.tv_leaf_frag_pending_tab)
        tv_inProcessTab = view.findViewById(R.id.tv_leaf_frag_inprocess_tab)
        vp_ViewPager = view.findViewById(R.id.vp_leaf_frag)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        tv_pendingTab.setOnClickListener(this)
        tv_inProcessTab.setOnClickListener(this)

        leadViewPagerAdapter = LeadViewPagerAdapter(fragmentManager)
        vp_ViewPager.currentItem=0
        isPendingTab(true)
        vp_ViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    isPendingTab(true)
                } else {
                    isPendingTab(false)
                }
            }

        })

        CustomStatic.IsViewLeadAddUpdate=false

    }

    fun isPendingTab(ispendingTab: Boolean) {
        if (ispendingTab) {
            tv_pendingTab.isSelected = true
            tv_inProcessTab.isSelected = false
        } else {
            tv_pendingTab.isSelected = false
            tv_inProcessTab.isSelected = true
        }
    }

    private fun initAdapter() {
        vp_ViewPager.adapter = leadViewPagerAdapter
    }

    open fun refreshAdapter() {
        vp_ViewPager.adapter?.notifyDataSetChanged()
    }



    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_leaf_frag_pending_tab -> {
                isPendingTab(true)
                vp_ViewPager.currentItem = 0
            }
            R.id.tv_leaf_frag_inprocess_tab -> {
                isPendingTab(false)
                vp_ViewPager.currentItem = 1
            }
        }
    }

    fun updateView(){
        leadViewPagerAdapter.notifyDataSetChanged()
    }

}
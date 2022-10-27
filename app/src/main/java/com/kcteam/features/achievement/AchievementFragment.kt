package com.kcteam.features.achievement

import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity

/**
 * Created by Saikat on 12-02-2019.
 */

class AchievementFragment : BaseFragment() {

    private lateinit var TL_resource_parent: TabLayout
    private lateinit var VP_resource_parent: ViewPager
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_achievement, container, false)
        initView(v)
        setupViewPager()
        initPagerClickListener()
        return v
    }

    private fun initView(v: View) {
        TL_resource_parent = v.findViewById(R.id.TL_resource_parent) as TabLayout
        VP_resource_parent = v.findViewById(R.id.VP_resource_parent) as ViewPager

        VP_resource_parent.offscreenPageLimit = 4
        TL_resource_parent.setupWithViewPager(VP_resource_parent)
    }

    private fun setupViewPager() {
        val adapter = ResourcePagerAdapter(mContext, (mContext as DashboardActivity).supportFragmentManager)
        adapter.addFragment(FragmentOrder(), "Order")
        adapter.addFragment(Fragment_Visit(), "Visit")
        adapter.addFragment(Fragment_Revisit(), "Revisit")
        adapter.addFragment(Fragment_Collection(), "Collection")

        VP_resource_parent.adapter = adapter
    }

    private fun initPagerClickListener() {
        setCurrentItem(0)
        VP_resource_parent.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                setCurrentItem(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun setCurrentItem(position: Int) {

        when (position) {
            0 -> {
                VP_resource_parent.currentItem = 0
            }
            1 -> {
                VP_resource_parent.currentItem = 1
            }
            2 -> {
                VP_resource_parent.currentItem = 2
            }
            3 -> {
                VP_resource_parent.currentItem = 3
            }
        }
    }
}

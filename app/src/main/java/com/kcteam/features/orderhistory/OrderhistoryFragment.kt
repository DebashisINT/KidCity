package com.kcteam.features.orderhistory

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Kinsuk on 30-10-2017.
 */
class OrderhistoryFragment : BaseFragment(), View.OnClickListener {
    private lateinit var dayWiseTab: AppCustomTextView
    private lateinit var consolidatedTab: AppCustomTextView
    private lateinit var historyTabPagerAdapter: HistoryTabPagerAdapter
    private lateinit var dayConsViewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        initView(view)
        initAdapter()
        return view
    }

    private fun initView(view: View) {
        dayWiseTab = view.findViewById(R.id.daywise_TV)
        consolidatedTab = view.findViewById(R.id.consolidated_TV)
        dayConsViewPager = view.findViewById(R.id.day_cons_viewpager)
        historyTabPagerAdapter = HistoryTabPagerAdapter(fragmentManager)
        dayWiseTab.setOnClickListener(this)
        consolidatedTab.setOnClickListener(this)
        dayConsViewPager.currentItem = 0
        isDayWise(true)
        dayConsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    isDayWise(true)
                } else {
                    isDayWise(false)
                }
            }

        })

    }

    private fun initAdapter() {
        dayConsViewPager.adapter = historyTabPagerAdapter
    }

    open fun refreshAdapter() {
        dayConsViewPager.adapter?.notifyDataSetChanged()
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.daywise_TV -> {
                isDayWise(true)
                dayConsViewPager.currentItem = 0
            }
            R.id.consolidated_TV -> {
                isDayWise(false)
                dayConsViewPager.currentItem = 1
            }
        }
    }

    fun isDayWise(isDayWise: Boolean) {
        if (isDayWise) {
            dayWiseTab.isSelected = true
            consolidatedTab.isSelected = false
        } else {
            dayWiseTab.isSelected = false
            consolidatedTab.isSelected = true
        }
    }

    fun updateChild() {
//        if (dayConsViewPager.currentItem==0 && fragmentManager.fragments.size>2){
//            (fragmentManager.fragments[1] as DayWiseFragment).UpdateLocationData()
//        }else if(dayConsViewPager.currentItem==1 && fragmentManager.fragments.size>3){
//            ( fragmentManager.fragments[2] as ConsolidatedFragment).UpdateLocationData()
//        }

    }


}
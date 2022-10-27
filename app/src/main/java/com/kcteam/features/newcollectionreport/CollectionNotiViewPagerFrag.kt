package com.kcteam.features.newcollectionreport

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel

class CollectionNotiViewPagerFrag: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var tv_notifictionTab: AppCustomTextView
    private lateinit var tv_inpendingTab: AppCustomTextView
    private lateinit var collectionViewPagerAdapter: CollectionViewPagerAdapter
    private lateinit var vp_ViewPager: ViewPager

    private lateinit var progress_wheel:ProgressWheel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_collection, container, false)
        initView(view)
        initAdapter()
        return view
    }

    private fun initView(view: View){
        tv_notifictionTab = view.findViewById(R.id.tv_collection_frag_notification_tab)
        tv_inpendingTab = view.findViewById(R.id.tv_collection_frag_pendingcollection_tab)
        vp_ViewPager = view.findViewById(R.id.vp_collection_frag)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        tv_notifictionTab.setOnClickListener(this)
        tv_inpendingTab.setOnClickListener(this)

        if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false)
            tv_inpendingTab.text="Collection"
        if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert)
            tv_inpendingTab.text="Zero Collection"
        if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert==false && Pref.IsShowRepeatOrderinNotification)
            tv_inpendingTab.text="Repeat Order"

        collectionViewPagerAdapter = CollectionViewPagerAdapter(fragmentManager)
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
            tv_notifictionTab.isSelected = true
            tv_inpendingTab.isSelected = false
        } else {
            tv_notifictionTab.isSelected = false
            tv_inpendingTab.isSelected = true
        }
    }

    private fun initAdapter() {
        vp_ViewPager.adapter = collectionViewPagerAdapter
    }

    open fun refreshAdapter() {
        vp_ViewPager.adapter?.notifyDataSetChanged()
    }



    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_collection_frag_notification_tab -> {
                isPendingTab(true)
                vp_ViewPager.currentItem = 0
            }
            R.id.tv_collection_frag_pendingcollection_tab -> {
                isPendingTab(false)
                vp_ViewPager.currentItem = 1
            }
        }
    }

    fun updateView(){
        collectionViewPagerAdapter.notifyDataSetChanged()
    }

}
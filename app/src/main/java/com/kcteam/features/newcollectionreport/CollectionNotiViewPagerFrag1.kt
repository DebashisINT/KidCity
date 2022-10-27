package com.kcteam.features.newcollectionreport

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel

class CollectionNotiViewPagerFrag1: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var tv_notifictionTab: AppCustomTextView
    private lateinit var tv_inpendingTab: AppCustomTextView
    private lateinit var  tv_ZeroCollTab: AppCustomTextView
    private lateinit var collectionViewPagerAdapter: CollectionViewPager1Adapter
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
        val view = inflater.inflate(R.layout.frag_collection1, container, false)
        initView(view)
        initAdapter()
        return view
    }

    private fun initView(view: View){
        tv_notifictionTab = view.findViewById(R.id.tv_collection_frag_notification_tab)
        tv_inpendingTab = view.findViewById(R.id.tv_collection_frag_pendingcollection_tab)
        tv_ZeroCollTab = view.findViewById(R.id.tv_zero_collection_frag_tab)
        vp_ViewPager = view.findViewById(R.id.vp_collection_frag)
        progress_wheel = view.findViewById(R.id.progress_wheel)


        progress_wheel.stopSpinning()

        tv_notifictionTab.setOnClickListener(this)
        tv_inpendingTab.setOnClickListener(this)
        tv_ZeroCollTab.setOnClickListener(this)

        if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false && Pref.IsShowRepeatOrderinNotification) {
            tv_inpendingTab.text = "Collection"
            tv_ZeroCollTab.text = "Repeat Order"
        }
       else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert&& Pref.IsShowRepeatOrderinNotification){
            tv_inpendingTab.text="Zero Collection"
            tv_ZeroCollTab.text = "Repeat Order"
       }

        collectionViewPagerAdapter = CollectionViewPager1Adapter(fragmentManager)
        vp_ViewPager.currentItem=0
        isPendingTab("0")
        vp_ViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    isPendingTab("0")
                }
                else if(position == 1){
                    isPendingTab("1")
                }
                else if(position == 2){
                    isPendingTab("2")
                }
            }

        })

        CustomStatic.IsViewLeadAddUpdate=false

    }

    fun isPendingTab(ispendingTab: String) {
        if (ispendingTab.equals("0")) {
            tv_notifictionTab.isSelected = true
            tv_inpendingTab.isSelected = false
            tv_ZeroCollTab.isSelected = false
        }  else  if (ispendingTab.equals("1")) {
            tv_notifictionTab.isSelected = false
            tv_inpendingTab.isSelected = true
            tv_ZeroCollTab.isSelected = false
        } else if (ispendingTab.equals("2")){
            tv_notifictionTab.isSelected = false
            tv_inpendingTab.isSelected = false
            tv_ZeroCollTab.isSelected = true
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
                isPendingTab("0")
                vp_ViewPager.currentItem = 0
            }
            R.id.tv_collection_frag_pendingcollection_tab -> {
                isPendingTab("1")
                vp_ViewPager.currentItem = 1
            }
            R.id.tv_zero_collection_frag_tab -> {
                isPendingTab("2")
                vp_ViewPager.currentItem = 2
            }
        }
    }

    fun updateView(){
        collectionViewPagerAdapter.notifyDataSetChanged()
    }

}
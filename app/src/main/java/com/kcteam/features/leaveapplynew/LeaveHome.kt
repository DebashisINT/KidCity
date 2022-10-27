package com.kcteam.features.leaveapplynew

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.leaveapplynew.adapter.LeavePagerAdapter
import com.kcteam.widgets.AppCustomTextView


class LeaveHome: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var approvalTab: AppCustomTextView
    private lateinit var statusTab: AppCustomTextView
    private lateinit var leaveViewPager: ViewPager

    private lateinit var leave_home_viewpager: LeavePagerAdapter

    companion object {
        var user_uid: String = ""
        fun getInstance(objects: Any): LeaveHome {
            val leaveHome = LeaveHome()
            if (!TextUtils.isEmpty(objects.toString())) {
                user_uid = objects.toString()
            }
            return leaveHome
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_leave_home, container, false)
        initView(view)
        initAdapter()
        return view
    }



    private fun initView(view:View){
        approvalTab = view.findViewById(R.id.pendings_approval_tv)
        statusTab = view.findViewById(R.id.leave_status_tv)
        leaveViewPager = view.findViewById(R.id.leave_home_viewpager)

        approvalTab.setOnClickListener(this)
        statusTab.setOnClickListener(this)

        leave_home_viewpager = LeavePagerAdapter(fragmentManager)


        leaveViewPager.currentItem = 0
        isLeaveSelWise(true,false)



        leaveViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    isLeaveSelWise(true,false)
                } else if(position == 1) {
                    isLeaveSelWise(false,true)
                }
            }

        })

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.pendings_approval_tv->{
                isLeaveSelWise(true,false)
                leaveViewPager.currentItem=0
            }
            R.id.leave_status_tv->{
                isLeaveSelWise(false,true)
                leaveViewPager.currentItem=1
            }
        }
    }


    fun isLeaveSelWise(isApproval: Boolean,isStatus:Boolean) {
        if(isApproval){
            approvalTab.isSelected=true
            statusTab.isSelected=false
        }else if(isStatus){
            approvalTab.isSelected=false
            statusTab.isSelected=true
        }

    }

    private fun initAdapter() {
        leaveViewPager.adapter = leave_home_viewpager
    }

    open fun refreshAdapter() {
        leaveViewPager.adapter?.notifyDataSetChanged()
    }

}
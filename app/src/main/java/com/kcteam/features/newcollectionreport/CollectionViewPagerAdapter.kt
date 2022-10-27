package com.kcteam.features.newcollectionreport

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.kcteam.app.Pref
import com.kcteam.features.notification.NotificationFragment
import com.kcteam.features.orderhistory.model.ActionFeed

class CollectionViewPagerAdapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm!!), ActionFeed {
    override fun refresh() {
        notifyDataSetChanged()
    }


    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            return NotificationFragment()
        } else if (position == 1) {
            if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false  && Pref.IsShowRepeatOrderinNotification==false)
                return CollectionPendingFrag()
            else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert  && Pref.IsShowRepeatOrderinNotification==false)
                return ZeroCollectionPendingFrag()
            else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert==false  && Pref.IsShowRepeatOrderinNotification)
                return RepeatOrderFrag()
            else
                return Fragment()
        } else {
            return Fragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getItemPosition(`object`: Any): Int {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return PagerAdapter.POSITION_NONE
    }
}
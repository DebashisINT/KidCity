package com.kcteam.features.newcollectionreport

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.kcteam.app.Pref
import com.kcteam.features.notification.NotificationFragment
import com.kcteam.features.orderhistory.model.ActionFeed

class CollectionViewPager1Adapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm!!), ActionFeed {
    override fun refresh() {
        notifyDataSetChanged()
    }


    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            return NotificationFragment()
        } else if (position == 1) {
            if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert==false){
                return CollectionPendingFrag()
            }else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert){
                return ZeroCollectionPendingFrag()
            }else if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert){
                return CollectionPendingFrag()
            }else{
                return Fragment()
            }
        } else if (position == 2) {
            if(Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false){
                return ZeroCollectionPendingFrag()
            }else if(Pref.ShowZeroCollectioninAlert==false && Pref.IsShowRepeatOrderinNotification){
                return RepeatOrderFrag()
            }else if(Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification){
                return RepeatOrderFrag()
            }else{
                return Fragment()
            }

            /*if(Pref.ShowCollectionAlert && Pref.ShowZeroCollectioninAlert == false  && Pref.IsShowRepeatOrderinNotification==false)
                return CollectionPendingFrag()
            else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert && Pref.IsShowRepeatOrderinNotification==false)
                return ZeroCollectionPendingFrag()
            else if(Pref.ShowCollectionAlert==false && Pref.ShowZeroCollectioninAlert==false && Pref.IsShowRepeatOrderinNotification)
                return RepeatOrderFrag()
            else
                return Fragment()*/
        }
        else {
            return Fragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getItemPosition(`object`: Any): Int {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return PagerAdapter.POSITION_NONE
    }
}
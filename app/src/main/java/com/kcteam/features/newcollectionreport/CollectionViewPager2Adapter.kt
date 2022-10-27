package com.kcteam.features.newcollectionreport

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.kcteam.features.notification.NotificationFragment
import com.kcteam.features.orderhistory.model.ActionFeed

class CollectionViewPager2Adapter(fm: FragmentManager?) : FragmentStatePagerAdapter(fm!!), ActionFeed {
    override fun refresh() {
        notifyDataSetChanged()
    }


    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            return NotificationFragment()
        } else if (position == 1) {
            return CollectionPendingFrag()
        } else if (position == 2) {
            return ZeroCollectionPendingFrag()
        }else if (position == 3) {
            return RepeatOrderFrag()
        }
        else {
            return Fragment()
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getItemPosition(`object`: Any): Int {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return PagerAdapter.POSITION_NONE
    }
}
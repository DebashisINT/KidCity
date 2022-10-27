package com.kcteam.features.achievement

import android.content.Context
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

class ResourcePagerAdapter(private val context: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val mFragments = ArrayList<Fragment>()
    private val mFragmentTitles = ArrayList<String>()


    fun addFragment(fragment: Fragment, title: String) {
        mFragments.add(fragment)
        mFragmentTitles.add(title)
    }


    override fun getItem(pos: Int): Fragment {
        return mFragments[pos]
    }

    override fun getCount(): Int {
        return mFragments.size
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitles[position]
    }


    override fun saveState(): Parcelable? {
        return null
    }

}

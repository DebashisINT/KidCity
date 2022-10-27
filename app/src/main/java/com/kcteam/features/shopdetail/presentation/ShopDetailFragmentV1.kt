package com.kcteam.features.shopdetail.presentation

import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import android.view.*
import android.widget.*
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment

/**
 * Created by Pratishruti on 30-10-2017.
 */
class ShopDetailFragmentV1 : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var tab_layout: TabLayout
    private lateinit var vpShopPager: ViewPager
    private lateinit var llshopSubdetails1: LinearLayout
    private lateinit var llshopSubdetails2: LinearLayout
    private lateinit var tvOrderValueTitle: TextView

    private lateinit var pagerAdapter: PagerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_shop_detail_v1, container, false)

        initView(view)
        return view
    }


    companion object {
        private lateinit var shopId: String
        fun getInstance(shopId: Any?): ShopDetailFragmentV1 {
            val shopDetailFragment = ShopDetailFragmentV1()
            this.shopId = shopId as String
            return shopDetailFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(view: View) {
        tab_layout = view.findViewById(R.id.tab_layout)
        vpShopPager = view.findViewById(R.id.vpShopPager)
        llshopSubdetails1 = view.findViewById(R.id.llshopSubdetails1)
        llshopSubdetails2 = view.findViewById(R.id.llshopSubdetails2)
        tvOrderValueTitle = view.findViewById(R.id.tvOrderValueTitle)

        pagerAdapter = PagerAdapter(childFragmentManager, mContext)
        vpShopPager.setAdapter(pagerAdapter)
        tab_layout.setupWithViewPager(vpShopPager)

        // Iterate over all tabs and set the custom view
        for (i in 0 until tab_layout.getTabCount()) {
            val tab = tab_layout.getTabAt(i)
            if (tab != null)
                tab!!.setCustomView(pagerAdapter.getTabView(i))
        }

        vpShopPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        llshopSubdetails1.visibility = View.GONE
                        llshopSubdetails2.visibility = View.GONE
                    }

                    1 -> {
                        llshopSubdetails1.visibility = View.VISIBLE
                        llshopSubdetails2.visibility = View.VISIBLE
                        tvOrderValueTitle.setText(R.string.total_order_value_v1)
                    }
                    2 -> {
                        llshopSubdetails1.visibility = View.VISIBLE
                        llshopSubdetails2.visibility = View.VISIBLE
                        tvOrderValueTitle.setText(R.string.total_collection_value)
                    }
                }
            }
        })
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val view = tab.customView
                if (view != null) {
                    val tvSelected = view.findViewById<TextView>(R.id.custom_text)
                    tvSelected.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                val view = tab.customView
                if (view != null) {
                    val tvSelected = view.findViewById<TextView>(R.id.custom_text)
                    tvSelected.setTextColor(ContextCompat.getColor(mContext, R.color.black))
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {

        }
    }

    private inner class PagerAdapter internal constructor(fm: FragmentManager, internal var context: Context) : FragmentPagerAdapter(fm) {

        internal var tabTitles = arrayOf("Profile", "Orders", "Collections")

        override fun getCount(): Int {
            return tabTitles.size
        }



        override fun getItem(position: Int): BaseFragment {
            when (position) {
                0 -> return ShopDetailsProfileFragment()
                1 -> return ShopDetailsOrderFragment()
                2 -> return ShopDetailsCollectionsFragment()

            }
            return BaseFragment()
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return tabTitles[position]
        }

        internal fun getTabView(position: Int): View {
            val tab = LayoutInflater.from(context).inflate(R.layout.custom_tabs_shopdetails, null)
            val tv = tab.findViewById<TextView>(R.id.custom_text)
            tv.setText(tabTitles[position])

            return tab
        }

    }


}
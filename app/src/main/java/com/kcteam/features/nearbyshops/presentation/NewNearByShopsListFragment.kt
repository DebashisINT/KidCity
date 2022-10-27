package com.kcteam.features.nearbyshops.presentation

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.appcompat.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kcteam.R
import com.kcteam.app.types.FragType
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu


/**
 * Created by Pratishruti on 30-10-2017.
 */
class NewNearByShopsListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var floating_fab: FloatingActionMenu
    private lateinit var programFab1: FloatingActionButton
    private lateinit var programFab2: FloatingActionButton
    private lateinit var programFab3: FloatingActionButton
    private lateinit var svSearchForShop: SearchView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var llkcteamShoListLayout: LinearLayout


    private lateinit var getFloatingVal: ArrayList<String>
    private val preid: Int = 100


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_nearby_shops_v1, container, false)
        initView(view)

        attachkcteamData(view)


        return view

    }


    override fun onResume() {
        super.onResume()

    }

    override fun updateUI(any: Any) {
        super.updateUI(any)
    }


    private fun initView(view: View) {
        getFloatingVal = ArrayList<String>()

        svSearchForShop = view.findViewById(R.id.svSearchForShop)
        svSearchForShop.isIconified = false


        val editText = svSearchForShop.findViewById<EditText>(R.id.search_src_text)
        val mCloseButton = svSearchForShop.findViewById<ImageView>(R.id.search_close_btn)
        mCloseButton.setOnClickListener(View.OnClickListener {

            editText.setText("")
            editText.clearFocus()
        })

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        floating_fab = view.findViewById(R.id.floating_fab)
        floating_fab.menuIconView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_filter))
        //floating_fab.menuButtonColorNormal = mContext.resources.getColor(R.color.colorAccent)
        floating_fab.menuButtonColorPressed = Color.parseColor("#635b9e")
        floating_fab.menuButtonColorRipple = Color.parseColor("#392f7e")

        floating_fab.isIconAnimated = false
        floating_fab.setClosedOnTouchOutside(true)

        getFloatingVal.add("Alphabetically")
        getFloatingVal.add("Visit Date")
        getFloatingVal.add("Most Visited")

        for (i in getFloatingVal.indices) {
            if (i == 0) {
                programFab1 = FloatingActionButton(activity)
                programFab1.buttonSize = FloatingActionButton.SIZE_MINI
                programFab1.id = preid + i
                programFab1.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                programFab1.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                programFab1.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                programFab1.labelText = getFloatingVal[0]
                floating_fab.addMenuButton(programFab1)
                programFab1.setOnClickListener(this)

            }
            if (i == 1) {
                programFab2 = FloatingActionButton(activity)
                programFab2.buttonSize = FloatingActionButton.SIZE_MINI
                programFab2.id = preid + i
                programFab2.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab2.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                programFab2.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                programFab2.labelText = getFloatingVal[1]
                floating_fab.addMenuButton(programFab2)
                programFab2.setOnClickListener(this)

            }

            if (i == 2) {
                programFab3 = FloatingActionButton(activity)
                programFab3.buttonSize = FloatingActionButton.SIZE_MINI
                programFab3.id = preid + i
                programFab3.colorNormal = mContext.resources.getColor(R.color.colorAccent)
                programFab3.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                programFab3.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                programFab3.labelText = getFloatingVal[2]
                floating_fab.addMenuButton(programFab3)
                programFab3.setOnClickListener(this)


            }
            //programFab1.setImageResource(R.drawable.ic_filter);
            if (i == 0) {
                programFab1.setImageResource(R.drawable.ic_tick_float_icon)
                programFab1.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
            } else if (i == 1)
                programFab2.setImageResource(R.drawable.ic_tick_float_icon_gray)
            else
                programFab3.setImageResource(R.drawable.ic_tick_float_icon_gray)

        }
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {

        }
    }

    private fun attachkcteamData(view: View) {
        llkcteamShoListLayout = view.findViewById(R.id.llkcteamShoListLayout)
        val layoutInflater = layoutInflater
        for (i in 0..5) {
            val view = layoutInflater.inflate(R.layout.row_shop_list, llkcteamShoListLayout, false)
            lateinit var myshop_name_TV: TextView
            lateinit var total_visited_value_TV: TextView
            lateinit var last_visited_date_TV: TextView
            lateinit var tvOrderDate: TextView
            lateinit var tvOrderAmount: TextView
            lateinit var tvOrderPrice: TextView
            lateinit var tvShopCallTitle: TextView
            lateinit var iv_shopImage: ImageView
            lateinit var ivshopCallIcon: ImageView
            lateinit var cvShopCard: CardView

            myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
            total_visited_value_TV = view.findViewById(R.id.total_visited_value_TV)
            last_visited_date_TV = view.findViewById(R.id.last_visited_date_TV)
            tvOrderDate = view.findViewById(R.id.tvOrderDate)
            tvOrderAmount = view.findViewById(R.id.tvOrderAmount)
            tvOrderPrice = view.findViewById(R.id.tvOrderPrice)
            tvShopCallTitle = view.findViewById(R.id.tvShopCallTitle)
            iv_shopImage = view.findViewById(R.id.iv_shopImage)
            ivshopCallIcon = view.findViewById(R.id.ivshopCallIcon)
            cvShopCard = view.findViewById(R.id.cvShopCard)

            cvShopCard.setOnClickListener(View.OnClickListener {

                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragmentV1, true, "")
            })


            when (i) {
                0 -> {
                    myshop_name_TV.setText("Balaji Light House")
                    total_visited_value_TV.setText("2")
                    last_visited_date_TV.setText("08 Jan")
                    tvOrderDate.setText("25 Dec")
                    tvOrderAmount.setText(resources.getString(R.string.Rs_symbol) + "12568")
                    tvOrderPrice.setText(resources.getString(R.string.Rs_symbol) + "1256843")
                    iv_shopImage.setImageResource(R.drawable.sample_shop_img1)
                    tvShopCallTitle.setText("Verified")
                    ivshopCallIcon.setImageResource(R.drawable.ic_call_green)
                }
                1 -> {
                    myshop_name_TV.setText("Om Light House Pvt. Ltd.")
                    total_visited_value_TV.setText("2")
                    last_visited_date_TV.setText("09 Jan")
                    tvOrderDate.setText("26 Dec")
                    tvOrderAmount.setText(resources.getString(R.string.Rs_symbol) + "12568")
                    tvOrderPrice.setText(resources.getString(R.string.Rs_symbol) + "125682")
                    iv_shopImage.setImageResource(R.drawable.sample_shop_img3)
                }
                2 -> {
                    myshop_name_TV.setText("Magan Lal Electrical Pvt. Ltd.")
                    total_visited_value_TV.setText("2")
                    last_visited_date_TV.setText("10 Jan")
                    tvOrderDate.setText("25 Dec")
                    tvOrderAmount.setText(resources.getString(R.string.Rs_symbol) + "125682")
                    tvOrderPrice.setText(resources.getString(R.string.Rs_symbol) + "125682")
                    iv_shopImage.setImageResource(R.drawable.sample_shop_img4)
                    tvShopCallTitle.setText("Verified")
                    ivshopCallIcon.setImageResource(R.drawable.ic_call_green)
                }
                3 -> {
                    myshop_name_TV.setText("Balaji Light House")
                    total_visited_value_TV.setText("2")
                    last_visited_date_TV.setText("09 Jan")
                    tvOrderDate.setText("30 Dec")
                    tvOrderAmount.setText(resources.getString(R.string.Rs_symbol) + "125638")
                    tvOrderPrice.setText(resources.getString(R.string.Rs_symbol) + "125638")
                    iv_shopImage.setImageResource(R.drawable.sample_shop_img1)
                }
                4 -> {
                    myshop_name_TV.setText("Magan Lal Electrical Pvt. Ltd.")
                    total_visited_value_TV.setText("2")
                    last_visited_date_TV.setText("08 Jan")
                    tvOrderDate.setText("25 Dec")
                    tvOrderAmount.setText(resources.getString(R.string.Rs_symbol) + "125568")
                    tvOrderPrice.setText(resources.getString(R.string.Rs_symbol) + "125658")
                    iv_shopImage.setImageResource(R.drawable.sample_shop_img3)
                }
                5 -> {
                    myshop_name_TV.setText("Om Light House Pvt. Ltd.")
                    total_visited_value_TV.setText("2")
                    last_visited_date_TV.setText("09 Jan")
                    tvOrderDate.setText("26 Dec")
                    tvOrderAmount.setText(resources.getString(R.string.Rs_symbol) + "12568")
                    tvOrderPrice.setText(resources.getString(R.string.Rs_symbol) + "125682")
                    iv_shopImage.setImageResource(R.drawable.sample_shop_img1)
                }
            }

            llkcteamShoListLayout.addView(view)
        }

    }


}
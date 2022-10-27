package com.kcteam.features.addshop.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Pratishruti on 02-02-2018.
 */
class AccuracyIssueDialog : DialogFragment() {

    private lateinit var update_TV: AppCustomTextView
    private lateinit var cancel_TV: AppCustomTextView
    private lateinit var mContext: Context
    private lateinit var dialog_content_TV: AppCustomTextView
    private lateinit var dialog_header_TV: AppCustomTextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater?.inflate(R.layout.dialog_address_not_accurate, container, false)

        if ((mContext as DashboardActivity).getCurrentFragType() == FragType.AddShopFragment)
            isCancelable = false

        initView(v)
        return v
    }

    private fun initView(v: View?) {
//        shop_name_TV=v!!.findViewById(R.id.shop_name_TV)
//        address_EDT=v!!.findViewById(R.id.address_EDT)
        update_TV = v!!.findViewById(R.id.update_TV)
        cancel_TV = v.findViewById(R.id.cancel_TV)
        dialog_content_TV = v.findViewById(R.id.dialog_content_TV)
//        new_address_EDT=v.findViewById(R.id.new_address_EDT)
        dialog_header_TV = v.findViewById(R.id.dialog_header_TV)

        dialog_header_TV.text = AppUtils.hiFirstNameText()+"!"

        if ((mContext as DashboardActivity).getCurrentFragType() != FragType.AddShopFragment) {
            cancel_TV.visibility = View.GONE
            update_TV.background = resources.getDrawable(R.drawable.selector_single_button)
            update_TV.setTextColor(resources.getColor(R.color.white))
            dialog_content_TV.text = getString(R.string.address_not_accurate_old)
        }


        update_TV.setOnClickListener(View.OnClickListener {
            if ((mContext as DashboardActivity).getCurrentFragType() != FragType.AddShopFragment) {
                dialog?.dismiss()
            } else {
                dialog?.dismiss()
//                (mContext as DashboardActivity).onBackPressed()
                (mContext as DashboardActivity).loadFragment(FragType.SearchLocationFragment, true, "")
            }
        })
        cancel_TV.setOnClickListener(View.OnClickListener {
            if ((mContext as DashboardActivity).getCurrentFragType() != FragType.AddShopFragment)
                dialog?.dismiss()
            else {
                dialog?.dismiss()
                (mContext as DashboardActivity).onBackPressed()
            }
        })
    }

    /*interface onItemClickListener {
        fun onActionItemClick()
    }*/


}
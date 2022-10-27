package com.kcteam.features.shopdetail.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.widget.AppCompatRadioButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 12-12-2018.
 */
class ShopDetailsInstructionDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var rb_view_create_order: AppCompatRadioButton
    private lateinit var rb_enter_collection: AppCompatRadioButton
    private lateinit var iv_close_icon: ImageView
    private lateinit var add_TV: AppCustomTextView
    private var shopName: String = ""
    private var isAdd: Boolean = false
    private lateinit var tv_collection_hint: AppCustomTextView
    private lateinit var dialog_header_TV: AppCustomTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater?.inflate(R.layout.dialog_shop_details, container, false)
        //addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)
        isCancelable = false

        initView(v)
        initClickListener()

        return v
    }

    private fun initView(v: View) {
        rb_enter_collection = v.findViewById(R.id.rb_enter_collection)
        rb_view_create_order = v.findViewById(R.id.rb_view_create_order)
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
    }

    private fun initClickListener() {
        rb_enter_collection.setOnClickListener(this)
        rb_view_create_order.setOnClickListener(this)
        iv_close_icon.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rb_enter_collection -> {
                dismiss()
            }

            R.id.rb_view_create_order -> {
                dismiss()
            }

            R.id.iv_close_icon -> {
                dismiss()
            }
        }
    }

    interface OnClickListener {
        fun onOrderClick()

        fun onCollectionClick()
    }
}
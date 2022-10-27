package com.kcteam.features.viewAllOrder

import android.content.Context
import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment
import androidx.appcompat.widget.AppCompatImageView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

class AddRemarksSignDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var dialog_header_TV: AppCustomTextView
    private lateinit var iv_close_icon: AppCompatImageView
    private lateinit var et_remarks: AppCustomEditText
    private lateinit var ll_sign: LinearLayout
    private lateinit var iv_sign: ImageView
    private lateinit var tv_ok_btn: AppCustomTextView
    private lateinit var tl_remarks: TextInputLayout

    private var feedback = ""
    private var imagePath = ""

    companion object {

        private lateinit var onCrossClick: () -> Unit
        private lateinit var onOkClick: (String, String) -> Unit

        fun getInstance(feedback: String, imagePath: String, onOkClick: (String, String) -> Unit, onCrossClick: () -> Unit): AddRemarksSignDialog {
            val dialog = AddRemarksSignDialog()

            this.onCrossClick = onCrossClick
            this.onOkClick = onOkClick

            val bundle = Bundle()
            bundle.putString("feedback", feedback)
            bundle.putString("imagePath", imagePath)
            dialog.arguments = bundle

            return dialog
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        feedback = arguments?.getString("feedback").toString()
        imagePath = arguments?.getString("imagePath").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_add_remarks_sign, container, false)

        isCancelable = false

        initView(v)
        initClickListener()

        return v
    }

    private fun initView(v: View) {
        v.apply {
            dialog_header_TV = findViewById(R.id.dialog_header_TV)
            iv_close_icon = findViewById(R.id.iv_close_icon)
            et_remarks = findViewById(R.id.et_remarks)
            ll_sign = findViewById(R.id.ll_sign)
            iv_sign = findViewById(R.id.iv_sign)
            tv_ok_btn = findViewById(R.id.tv_ok_btn)
            tl_remarks = findViewById(R.id.tl_remarks)
        }

        dialog_header_TV.text = AppUtils.hiFirstNameText()+"!"

        if (!TextUtils.isEmpty(feedback))
            et_remarks.setText(feedback)

        if (!TextUtils.isEmpty(imagePath)) {
            iv_sign.visibility = View.VISIBLE
            Glide.with(mContext)
                    .load(imagePath)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_menu_profile_image).error(R.drawable.ic_menu_profile_image))
                    .into(iv_sign)
        }

        if (Pref.isShowOrderRemarks)
            tl_remarks.visibility = View.VISIBLE
        else
            tl_remarks.visibility = View.GONE


        if (Pref.isShowOrderSignature)
            ll_sign.visibility = View.VISIBLE
        else
            ll_sign.visibility = View.GONE
    }

    private fun initClickListener() {
        iv_close_icon.setOnClickListener(this)
        tv_ok_btn.setOnClickListener(this)
        ll_sign.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_close_icon -> {
                dismiss()
                onCrossClick()
            }

            R.id.tv_ok_btn -> {
                dismiss()
                onOkClick(et_remarks.text.toString().trim(), imagePath)
            }

            R.id.ll_sign -> {
                SignatureDialog.getInstance {
                    imagePath = it

                    iv_sign.visibility = View.VISIBLE
                    Glide.with(mContext)
                            .load(imagePath)
                            .apply(RequestOptions.placeholderOf(R.drawable.ic_menu_profile_image).error(R.drawable.ic_menu_profile_image))
                            .into(iv_sign)
                }.show((mContext as DashboardActivity).supportFragmentManager, "")
            }
        }
    }
}
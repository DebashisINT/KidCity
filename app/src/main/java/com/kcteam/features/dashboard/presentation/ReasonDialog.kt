package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

class ReasonDialog : DialogFragment() {

    private lateinit var mContext: Context

    private lateinit var dialog_header_TV: AppCustomTextView
    private lateinit var dialog_content_TV: AppCustomTextView
    private lateinit var et_reason: AppCustomEditText
    private lateinit var ok_TV: AppCustomTextView

    private var header = ""
    private var body = ""
    private var reason = ""

    companion object {

        private lateinit var mOnOkClick: (String) -> Unit

        fun getInstance(header: String, body: String, reason: String, onOkClick: (String) -> Unit): ReasonDialog {
            val cardFragment = ReasonDialog()

            val bundle = Bundle()
            bundle.putString("header", header)
            bundle.putString("body", body)
            bundle.putString("reason", reason)
            cardFragment.arguments = bundle

            mOnOkClick = onOkClick

            return cardFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        header = arguments?.getString("header").toString()
        body = arguments?.getString("body").toString()
        reason = arguments?.getString("reason").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_reason, container, false)
        initView(v)

        isCancelable = false

        return v
    }

    private fun initView(v: View) {
        v.apply {
            dialog_header_TV = v.findViewById(R.id.dialog_header_TV)
            dialog_content_TV = v.findViewById(R.id.dialog_content_TV)
            et_reason = v.findViewById(R.id.et_reason)
            ok_TV = v.findViewById(R.id.ok_TV)
        }

        dialog_header_TV.text = header
        dialog_content_TV.text = body

        if (reason.isNotEmpty())
            et_reason.setText(reason)

        ok_TV.setOnClickListener {
            if (TextUtils.isEmpty(et_reason.text.toString().trim()))
                Toaster.msgShort(mContext, "Please enter reason.")
            else if (et_reason.text.toString().trim().length < 20)
                Toaster.msgShort(mContext, "You must enter a proper reason with a minimum 20 character length.")
            else {
                /*if (!AppUtils.isOnline(mContext))
                    Toaster.msgShort(mContext, getString(R.string.no_internet))
                else*/
                    mOnOkClick(et_reason.text.toString().trim())
            }
        }
    }
}
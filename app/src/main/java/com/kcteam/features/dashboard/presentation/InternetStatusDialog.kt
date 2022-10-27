package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.widgets.AppCustomTextView

class InternetStatusDialog : DialogFragment() {

    private lateinit var mContext: Context

    private lateinit var dialog_header_TV: AppCustomTextView
    private lateinit var tv_net_status: AppCustomTextView
    private lateinit var ok_TV: AppCustomTextView

    private var internetStatus = ""

    companion object {
        fun getInstance(internetStatus: String): InternetStatusDialog {
            val fragment = InternetStatusDialog()

            val bundle = Bundle()
            bundle.putString("internetStatus", internetStatus)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        internetStatus = arguments?.getString("internetStatus").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_net_status, container, false)

        initView(v)

        isCancelable = false
        return v
    }

    private fun initView(v: View) {
        v.apply {
            ok_TV = findViewById(R.id.ok_TV)
            tv_net_status = findViewById(R.id.tv_net_status)
            dialog_header_TV = findViewById(R.id.dialog_header_TV)
        }

        dialog_header_TV.text = AppUtils.hiFirstNameText()+"!"
        tv_net_status.text = internetStatus

        ok_TV.setOnClickListener {
            dismiss()
        }
    }
}
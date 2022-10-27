package com.kcteam.features.addAttendence


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.kcteam.R
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 16-Apr-20.
 */
class FingerprintDialog : DialogFragment() {

    private lateinit var mContext: Context

    private lateinit var tv_cancel_btn: AppCustomTextView
    private lateinit var iv_fingerprint_icon: AppCompatImageView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_fingerprint, container, false)
        isCancelable = false

        initView(v)

        return v
    }

    private fun initView(v: View) {
        v.apply {
            tv_cancel_btn = findViewById(R.id.tv_cancel_btn)
            iv_fingerprint_icon = findViewById(R.id.iv_fingerprint_icon)
        }

        tv_cancel_btn.setOnClickListener {
            BaseActivity.isApiInitiated = false
            dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            //if (!dialog.isShowing) {
            val ft = manager?.beginTransaction()
            ft?.add(this, tag)
            ft?.commitAllowingStateLoss()
            //}
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }
}
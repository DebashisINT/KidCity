package com.kcteam.features.forgotpassword.presentation

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.kcteam.R
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Pratishruti on 31-10-2017.
 */
class ForgotPasswordDialog : DialogFragment(), View.OnClickListener {


    private lateinit var sendTV: AppCustomTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater!!.inflate(R.layout.dialogfragment_forgot_password, container, false)
        initView(v)
        return v
    }

    private fun initView(v: View) {
        sendTV = v.findViewById(R.id.forgot_password_submit_TV)
        sendTV.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.forgot_password_submit_TV -> {
                dismiss()
            }

        }
    }
}
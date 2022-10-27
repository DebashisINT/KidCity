package com.kcteam.features.addshop.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 19-11-2018.
 */
class OTPVerificationDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var dialogHeader: AppCustomTextView
    private lateinit var dialogContent: AppCustomTextView
    private lateinit var dialogCancel: AppCustomTextView
    private lateinit var dialogOk: AppCustomTextView
    private lateinit var et_otp_1: AppCustomEditText
    private lateinit var et_otp_2: AppCustomEditText
    private lateinit var et_otp_3: AppCustomEditText
    private lateinit var et_otp_4: AppCustomEditText
    private lateinit var et_otp_5: AppCustomEditText
    private lateinit var et_otp_6: AppCustomEditText
    private lateinit var tv_timer: AppCustomTextView
    private lateinit var tv_resend_otp: AppCustomTextView

    private var phnNo = ""
    private var isShowTimer = false
    private var shopName = ""

    companion object {

        private var mListener: OnOTPButtonClickListener? = null

        fun getInstance(contact_no: String, isShowTimer: Boolean, shopName: String, listener: OnOTPButtonClickListener): OTPVerificationDialog {
            val fragment = OTPVerificationDialog()
            mListener = listener
            val bundle = Bundle()
            bundle.putString("phn_no", contact_no)
            bundle.putBoolean("isShowTimer", isShowTimer)
            bundle.putString("shopName", shopName)
            fragment.arguments = bundle
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phnNo = arguments?.getString("phn_no").toString()
        isShowTimer = arguments?.getBoolean("isShowTimer")!!
        shopName = arguments?.getString("shopName").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_otp_verification, container, false)
        initView(v)
        isCancelable = false
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    private fun initView(v: View) {
        dialogHeader = v.findViewById(R.id.dialog_header_TV)
        dialogContent = v.findViewById(R.id.dialog_content_TV)
        dialogContent.text = "Please enter below the OTP to verify the contact #$phnNo of $shopName. Thanks."
        dialogCancel = v.findViewById(R.id.cancel_TV)
        dialogOk = v.findViewById(R.id.ok_TV)
        dialogOk.isSelected = true
        tv_resend_otp = v.findViewById(R.id.tv_resend_otp)
        tv_resend_otp.setTextColor(mContext.resources.getColor(R.color.gray))
        et_otp_1 = v.findViewById(R.id.et_otp_1)
        et_otp_2 = v.findViewById(R.id.et_otp_2)
        et_otp_3 = v.findViewById(R.id.et_otp_3)
        et_otp_4 = v.findViewById(R.id.et_otp_4)
        et_otp_5 = v.findViewById(R.id.et_otp_5)
        et_otp_6 = v.findViewById(R.id.et_otp_6)
        tv_timer = v.findViewById(R.id.tv_timer)

        dialogHeader.text = AppUtils.hiFirstNameText()+"!"

        dialogCancel.setOnClickListener(this)
        dialogOk.setOnClickListener(this)
        //tv_timer.setOnClickListener(this)
        tv_resend_otp.setOnClickListener(null)

        if (isShowTimer)
            startCountDown()
        else {
            tv_timer.visibility = View.GONE
            tv_resend_otp.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
            tv_resend_otp.setOnClickListener(this)
        }

        addTextListener()
    }

    private fun startCountDown() {
        object : CountDownTimer(120000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                //tv_timer.text = (millisUntilFinished / 1000).toString() + " seconds"

                var seconds = (millisUntilFinished / 1000).toInt()
                val minutes = seconds / 60
                seconds %= 60
                tv_timer.text = (String.format("%02d", minutes) + ":" + String.format("%02d", seconds))

                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                tv_timer.visibility = View.GONE
                tv_resend_otp.setTextColor(mContext.resources.getColor(R.color.colorPrimary))
                tv_resend_otp.setOnClickListener(this@OTPVerificationDialog)
            }

        }.start()
    }

    private fun addTextListener() {
        et_otp_1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (et_otp_1.text!!.length == 1)
                    et_otp_2.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        et_otp_2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (et_otp_2.text!!.length == 1)
                    et_otp_3.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        et_otp_3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (et_otp_3.text!!.length == 1)
                    et_otp_4.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        et_otp_4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (et_otp_4.text!!.length == 1)
                    et_otp_5.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        et_otp_5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (et_otp_5.text!!.length == 1)
                    et_otp_6.requestFocus()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.cancel_TV -> {
                AppUtils.removeSoftKeyboard(mContext as Activity, et_otp_6)
                dismiss()
                mListener?.onCancelClick()
            }

            R.id.ok_TV -> {

                checkValidation()

                /*if (!TextUtils.isEmpty(et_otp_1.text.toString().trim())) {
                    dismiss()
                    mListener?.onOkButtonClick(et_otp.text.toString().trim())
                } else {
                    Toaster.msgShort(mContext, getString(R.string.otp_can_not_blank))
                }*/
            }

            R.id.tv_resend_otp -> {
                AppUtils.removeSoftKeyboard(mContext as Activity, et_otp_6)
                dismiss()
                mListener?.onResentClick()
            }
        }
    }

    private fun checkValidation() {
        if (TextUtils.isEmpty(et_otp_1.text.toString().trim()) && TextUtils.isEmpty(et_otp_2.text.toString().trim()) &&
                TextUtils.isEmpty(et_otp_3.text.toString().trim()) && TextUtils.isEmpty(et_otp_4.text.toString().trim())
                && TextUtils.isEmpty(et_otp_5.text.toString().trim()) && TextUtils.isEmpty(et_otp_6.text.toString().trim())) {
            et_otp_1.requestFocus()
            AppUtils.removeSoftKeyboard(mContext as Activity, et_otp_1)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.erro_enter_otp))
        } else if (TextUtils.isEmpty(et_otp_2.text.toString().trim())) {
            et_otp_2.requestFocus()
            AppUtils.removeSoftKeyboard(mContext as Activity, et_otp_2)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.erro_enter_otp))
        } else if (TextUtils.isEmpty(et_otp_3.text.toString().trim())) {
            et_otp_3.requestFocus()
            AppUtils.removeSoftKeyboard(mContext as Activity, et_otp_3)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.erro_enter_otp))
        } else if (TextUtils.isEmpty(et_otp_4.text.toString().trim())) {
            et_otp_4.requestFocus()
            AppUtils.removeSoftKeyboard(mContext as Activity, et_otp_4)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.erro_enter_otp))
        } else if (TextUtils.isEmpty(et_otp_5.text.toString().trim())) {
            et_otp_5.requestFocus()
            AppUtils.removeSoftKeyboard(mContext as Activity, et_otp_5)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.erro_enter_otp))
        } else if (TextUtils.isEmpty(et_otp_6.text.toString().trim())) {
            et_otp_6.requestFocus()
            AppUtils.removeSoftKeyboard(mContext as Activity, et_otp_6)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.erro_enter_otp))
        } else {
            AppUtils.removeSoftKeyboard(mContext as Activity, et_otp_6)
            dismiss()
            mListener?.onOkButtonClick(et_otp_1.text.toString().trim() + et_otp_2.text.toString().trim() +
                    et_otp_3.text.toString().trim() + et_otp_4.text.toString().trim() + et_otp_5.text.toString().trim() +
                    et_otp_6.text.toString().trim())
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            val ft = manager?.beginTransaction()
            ft?.add(this, tag)
            ft?.commitAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    interface OnOTPButtonClickListener {
        fun onOkButtonClick(otp: String)

        fun onCancelClick()

        fun onResentClick()
    }

}
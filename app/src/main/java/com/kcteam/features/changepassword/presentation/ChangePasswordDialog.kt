package com.kcteam.features.changepassword.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Pratishruti on 31-10-2017.
 */
class ChangePasswordDialog : DialogFragment(), View.OnClickListener {

    private lateinit var oldPassword: TextInputEditText
    private lateinit var newPassword: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var changePassSave: AppCustomTextView
    private lateinit var mContext: Context
    private lateinit var iv_close_icon: ImageView
    private lateinit var cancel_TV: AppCustomTextView

    companion object {

        private lateinit var onClick: (String, String) -> Unit

        fun getInstance(function: (String, String) -> Unit): ChangePasswordDialog {
            val fragment = ChangePasswordDialog()
            onClick = function
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater!!.inflate(R.layout.dialogfragment_change_password, container, false)
        isCancelable = false
        initView(v)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(v: View) {
        oldPassword = v.findViewById(R.id.old_password_EDT)
        newPassword = v.findViewById(R.id.new_password_EDT)
        confirmPassword = v.findViewById(R.id.confirm_password_EDT)
        changePassSave = v.findViewById(R.id.change_password_save_TV)
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        cancel_TV = v.findViewById(R.id.cancel_TV)

        changePassSave.isSelected = true

        changePassSave.setOnClickListener(this)
        iv_close_icon.setOnClickListener(this)
        cancel_TV.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.change_password_save_TV -> {
                checkValidation()
            }

            R.id.iv_close_icon -> {
                AppUtils.removeSoftKeyboard(mContext as Activity, confirmPassword)
                dismiss()
            }

            R.id.cancel_TV -> {
                AppUtils.removeSoftKeyboard(mContext as Activity, confirmPassword)
                dismiss()
            }
        }
    }

    private fun checkValidation() {
        if (TextUtils.isEmpty(oldPassword.text.toString().trim()))
            Toaster.msgShort(mContext, mContext.getString(R.string.error_enter_old_password))
        else if (TextUtils.isEmpty(newPassword.text.toString().trim()))
            Toaster.msgShort(mContext, mContext.getString(R.string.error_enter_new_password))
        else if (TextUtils.isEmpty(confirmPassword.text.toString().trim()))
            Toaster.msgShort(mContext, mContext.getString(R.string.error_enter_confirm_password))
        else if (oldPassword.text.toString().trim().equals(newPassword.text.toString().trim(), ignoreCase = true))
            Toaster.msgShort(mContext, mContext.getString(R.string.error_enter_valid_new_password))
        else if (!newPassword.text.toString().trim().equals(confirmPassword.text.toString().trim(), ignoreCase = true))
            Toaster.msgShort(mContext, mContext.getString(R.string.error_enter_valid_confirm_password))
        else {
            if (AppUtils.isOnline(mContext)) {
                AppUtils.removeSoftKeyboard(mContext as Activity, confirmPassword)
                onClick(newPassword.text.toString().trim(), oldPassword.text.toString().trim())
                dismiss()
            }
            else
                Toaster.msgShort(mContext, mContext.getString(R.string.no_internet))
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
}
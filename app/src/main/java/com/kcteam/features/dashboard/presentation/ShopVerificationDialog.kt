package com.kcteam.features.dashboard.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.appcompat.widget.AppCompatImageView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 26-11-2018.
 */
class ShopVerificationDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var dialogHeader: AppCustomTextView
    private lateinit var dialogContent: AppCustomTextView
    private lateinit var dialogCancel: AppCustomTextView
    private lateinit var dialogOk: AppCustomTextView
    private lateinit var et_number: AppCustomEditText
    private lateinit var tv_edit_number: AppCustomTextView
    private lateinit var til_edit_number: TextInputLayout
    private var shopId = ""
    private lateinit var iv_close_icon: AppCompatImageView

    companion object {

        private var mListener: OnOTPButtonClickListener? = null

        fun getInstance(mShopId: String, listener: OnOTPButtonClickListener): ShopVerificationDialog {
            val fragment = ShopVerificationDialog()
            mListener = listener

            val bundle = Bundle()
            bundle.putString("shop_id", mShopId)
            fragment.arguments = bundle

            return fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_shop_verification, container, false)
        initView(v)

        isCancelable = false
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        shopId = arguments?.getString("shop_id").toString()
    }


    private fun initView(v: View) {
        dialogHeader = v.findViewById(R.id.dialog_header_TV)
        dialogContent = v.findViewById(R.id.dialog_content_TV)
        dialogCancel = v.findViewById(R.id.cancel_TV)
        dialogOk = v.findViewById(R.id.ok_TV)
        dialogOk.isSelected = true
        et_number = v.findViewById(R.id.et_number)
        tv_edit_number = v.findViewById(R.id.tv_edit_number)
        til_edit_number = v.findViewById(R.id.til_edit_number)
        iv_close_icon = v.findViewById(R.id.iv_close_icon)

        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)

        et_number.setText(shop.ownerContactNumber)

        /*if (Pref.isReplaceShopText)
            dialogContent.text = getString(R.string.customer_verfication_msg_1) + " (" + shop.ownerContactNumber + "). " + getString(R.string.shop_verification_msg_2)
        else
            dialogContent.text = getString(R.string.shop_verfication_msg_1) + " (" + shop.ownerContactNumber + "). " + getString(R.string.shop_verification_msg_2)*/
        dialogHeader.text = "Contact No. Verification"
        dialogContent.text = "Selecting the Confirm option will send OTP to contact #" + shop.ownerContactNumber + ". You should take the OTP and input to verify the same. Thanks."


        dialogOk.text = "Confirm"
        dialogCancel.text = "Update Contact No."

        dialogCancel.setOnClickListener(this)
        dialogOk.setOnClickListener(this)
        tv_edit_number.setOnClickListener(this)
        iv_close_icon.setOnClickListener(this)

        if (Pref.isShopAddEditAvailable) {
            dialogCancel.visibility = View.VISIBLE
            dialogOk.isSelected = true
        } else {
            dialogCancel.visibility = View.GONE
            dialogOk.isSelected = false
        }

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.iv_close_icon -> {
                dismiss()
                mListener?.onCancelClick()
            }

            R.id.ok_TV -> {
                val list = AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(et_number.text.toString().trim())
                if (til_edit_number.visibility == View.VISIBLE) {
                    if (TextUtils.isEmpty(et_number.text.toString().trim())) {
                        //AppUtils.removeSoftKeyboard(mContext as Activity, et_number)
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.edit_number_error))
                        Toaster.msgShort(mContext, getString(R.string.edit_number_error))
                    } else if (!AppUtils.isValidateMobile(et_number.text.toString())) {
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.numbervalid_error))
                        Toaster.msgShort(mContext, getString(R.string.numbervalid_error))
                    } else if (!et_number.text.toString().trim().startsWith("6") && !et_number.text.toString().trim().startsWith("7") &&
                            !et_number.text.toString().trim().startsWith("8") && !et_number.text.toString().trim().startsWith("9")) {
                        //(mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_phn_no), 3000)
                        Toaster.msgShort(mContext, getString(R.string.error_enter_valid_phn_no))
                    } else if (list != null && list.size > 0) {
                        for (i in list.indices) {
                            if (list[i].shop_id != shopId) {
                                /* AppUtils.removeSoftKeyboard(mContext as Activity, et_number)
                                 (mContext as DashboardActivity).showSnackMessage(getString(R.string.contact_number_exist))*/
                                Toaster.msgShort(mContext, getString(R.string.contact_number_exist))
                                break
                            } else {
                                AppUtils.removeSoftKeyboard(mContext as Activity, et_number)
                                dismiss()
                                mListener?.onEditClick(et_number.text.toString().trim())
                                break
                            }
                        }
                    } else {
                        AppUtils.removeSoftKeyboard(mContext as Activity, et_number)
                        dismiss()
                        mListener?.onEditClick(et_number.text.toString().trim())
                    }
                } else {
                    AppUtils.removeSoftKeyboard(mContext as Activity, et_number)
                    dismiss()
                    mListener?.onOkButtonClick(et_number.text.toString().trim())
                }
            }

            R.id.cancel_TV -> {
                //dismiss()
                til_edit_number.visibility = View.VISIBLE
                //dialogOk.text = "Edit Number"
                dialogCancel.visibility = View.GONE
            }
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        //super.show(manager, tag)
        val ft = manager?.beginTransaction()
        ft?.add(this, tag)
        try {
            ft?.commit()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            ft?.commitAllowingStateLoss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface OnOTPButtonClickListener {
        fun onOkButtonClick(otp: String)

        fun onCancelClick()

        fun onEditClick(number: String)
    }
}
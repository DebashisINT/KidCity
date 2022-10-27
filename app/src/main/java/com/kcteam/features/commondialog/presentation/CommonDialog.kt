package com.kcteam.features.commondialog.presentation

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
import android.widget.RelativeLayout
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Pratishruti on 02-11-2017.
 */
class CommonDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var dialogHeader: AppCustomTextView
    private lateinit var dialogContent: AppCustomTextView
    private lateinit var dialogCancel: AppCustomTextView
    private lateinit var dialogOk: AppCustomTextView
    private lateinit var iv_close_icon: AppCompatImageView
    private lateinit var til_edt_text: TextInputLayout
    private lateinit var et_text: AppCustomEditText
    private lateinit var iv_calendar_icon: AppCompatImageView

    private var editableData = ""

    companion object {

        private lateinit var mHeader: String
        private lateinit var mTitle: String
        private lateinit var mLeftBtn: String
        private lateinit var mRightBtn: String
        private var mIsCancelable: Boolean = true
        private lateinit var mListener: CommonDialogClickListener
        private var isShowEditText = false
        private var isShowCross = false
        private var closeClickListener: OnCloseClickListener? = null
        private var isSetDrawable = false

        fun getInstance(header: String, title: String, leftCancel: String, rightOk: String, listener: CommonDialogClickListener): CommonDialog {
            val cardFragment = CommonDialog()
            mHeader = header
            mTitle = title
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            isShowEditText = false
            isShowCross = false
            isSetDrawable = false
            return cardFragment
        }

        fun getInstance(header: String, title: String, leftCancel: String, rightOk: String, mIsShowCross: Boolean, isCancelable: Boolean, listener: CommonDialogClickListener): CommonDialog {
            val cardFragment = CommonDialog()
            mHeader = header
            mTitle = title
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            isShowEditText = false
            isShowCross = mIsShowCross
            mIsCancelable = isCancelable
            isSetDrawable = false
            return cardFragment
        }

        fun getInstance(header: String, title: String, leftCancel: String, rightOk: String, isCancelable: Boolean, listener: CommonDialogClickListener): CommonDialog {
            val cardFragment = CommonDialog()
            mHeader = header
            mTitle = title
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            mIsCancelable = isCancelable
            isShowEditText = false
            isShowCross = false
            isSetDrawable = false
            return cardFragment
        }

        fun getInstance(header: String, title: String,  mIsSetDrawable: Boolean, listener: CommonDialogClickListener): CommonDialog {
            val cardFragment = CommonDialog()
            mHeader = header
            mTitle = title
            mLeftBtn = "Cancel"
            mRightBtn = "Ok"
            mListener = listener
            mIsCancelable = false
            isShowEditText = false
            isShowCross = false
            isSetDrawable = mIsSetDrawable
            return cardFragment
        }

        fun getInstance(header: String, title: String, leftCancel: String, rightOk: String, isCancelable: Boolean, mIsShowEditText: Boolean, mIsShowCross: Boolean, listener: CommonDialogClickListener): CommonDialog {
            val cardFragment = CommonDialog()
            mHeader = header
            mTitle = title
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            mIsCancelable = isCancelable
            isShowEditText = mIsShowEditText
            isShowCross = mIsShowCross
            isSetDrawable = false
            return cardFragment
        }

        fun getInstance(header: String, title: String, leftCancel: String, rightOk: String, isCancelable: Boolean, listener: CommonDialogClickListener, closeClickListener: OnCloseClickListener): CommonDialog {
            val cardFragment = CommonDialog()
            mHeader = header
            mTitle = title
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            mIsCancelable = isCancelable
            isShowEditText = false
            isShowCross = false
            this.closeClickListener = closeClickListener
            isSetDrawable = false
            return cardFragment
        }


        fun getInstanceNew(header: String, title: String, leftCancel: String, rightOk: String, isCancelable: Boolean, listener: CommonDialogClickListener,closeClickListener: OnCloseClickListener): CommonDialog {
            val cardFragment = CommonDialog()
            mHeader = header
            mTitle = title
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            mIsCancelable = isCancelable
            isShowEditText = false
            isShowCross = false
            isSetDrawable = false
            this.closeClickListener = closeClickListener
            return cardFragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialogfragment_common, container, false)
        isCancelable = mIsCancelable
        initView(v)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(v: View) {
        dialogHeader = v.findViewById(R.id.dialog_header_TV)
        dialogContent = v.findViewById(R.id.dialog_content_TV)
        dialogCancel = v.findViewById(R.id.cancel_TV)
        dialogOk = v.findViewById(R.id.ok_TV)

        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        til_edt_text = v.findViewById(R.id.til_edt_text)
        et_text = v.findViewById(R.id.et_text)
        iv_calendar_icon = v.findViewById(R.id.iv_calendar_icon)

        if (isShowEditText) {
            til_edt_text.visibility = View.VISIBLE
            dialogContent.visibility = View.GONE
            til_edt_text.hint = getString(R.string.remark)
        }
        else {
            til_edt_text.visibility = View.GONE
            dialogContent.visibility = View.VISIBLE
        }

        if (AppUtils.isRevisit!! || AppUtils.isShopAdded || isShowCross) {
            iv_close_icon.visibility = View.VISIBLE

            if (AppUtils.isRevisit!!)
                AppUtils.isRevisit = false

            if (AppUtils.isShopAdded)
                AppUtils.isShopAdded = false

        }
        else
            iv_close_icon.visibility = View.GONE

        if (isSetDrawable) {
            /*dialogCancel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0)
            dialogCancel.compoundDrawablePadding = mContext.resources.getDimensionPixelOffset(R.dimen._5sdp)
            dialogOk.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_mark, 0, 0, 0)
            dialogOk.compoundDrawablePadding = mContext.resources.getDimensionPixelOffset(R.dimen._5sdp)*/
            dialogOk.background = mContext.resources.getDrawable(R.drawable.deselected_ok_btn_new)
            //dialogOk.background = mContext.resources.getDrawable(R.drawable.shape_custom_border_green_button)
            //iv_calendar_icon.visibility = View.VISIBLE
        }
        else
            dialogOk.isSelected = true

        dialogHeader.text = mHeader
        dialogContent.text = mTitle
        dialogCancel.text = mLeftBtn
        dialogOk.text = mRightBtn

        iv_close_icon.setOnClickListener(this)
        dialogCancel.setOnClickListener(this)
        dialogOk.setOnClickListener(this)

        if(CustomStatic.IsCommDLeftBtnColor == true && CustomStatic.IsCommDRightBtnColor == true){
            dialogCancel.setBackgroundColor(mContext.getColor(R.color.color_custom_green))
            dialogOk.setBackgroundColor(mContext.getColor(R.color.default_text_color))
        }
        CustomStatic.IsCommDLeftBtnColor = false
        CustomStatic.IsCommDRightBtnColor = false

    }

    override fun onClick(p0: View?) {
//        deSelectAll()
        when (p0!!.id) {
            R.id.cancel_TV -> {
//              dialogCancel.isSelected=true
                if (!mIsCancelable)
                    mListener.onLeftClick()
                dismiss()
            }
            R.id.ok_TV -> {
//              dialogOk.isSelected=true
                dismiss()
                if (!TextUtils.isEmpty(et_text.text.toString().trim()))
                    editableData = et_text.text.toString().trim()

                mListener.onRightClick(editableData)
            }
            R.id.iv_close_icon -> {
                closeClickListener?.onCloseClick()
                dismiss()
            }
        }
    }

    private fun deSelectAll() {
        dialogOk.isSelected = false
        dialogCancel.isSelected = false
    }

    interface OnCloseClickListener {
        fun onCloseClick()
    }
}
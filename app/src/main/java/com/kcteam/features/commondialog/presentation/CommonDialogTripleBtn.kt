package com.hahnemann.features.commondialog.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.widget.AppCompatImageView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.widgets.AppCustomTextView


/**
 * Created by Saikat on 11-10-2019.
 */
class CommonDialogTripleBtn : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var dialogHeader: AppCustomTextView
    private lateinit var dialogContent: AppCustomTextView
    private lateinit var dialogCancel: AppCustomTextView
    private lateinit var dialogOk: AppCustomTextView
    private lateinit var dialogExtra: AppCustomTextView
    private lateinit var iv_close_icon: AppCompatImageView

    companion object {
        private lateinit var mHeader: String
        private lateinit var mTitle: String
        private lateinit var mLeftBtn: String
        private lateinit var mRightBtn: String
        private lateinit var middleBtn: String
        private var mIsCancelable: Boolean = true
        private lateinit var mListener: CommonTripleDialogClickListener

        fun getInstance(header: String, title: String, leftCancel: String, rightOk: String, middleBtn: String, listener: CommonTripleDialogClickListener): CommonDialogTripleBtn {
            val cardFragment = CommonDialogTripleBtn()
            mHeader = header
            mTitle = title
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            this.middleBtn = middleBtn
            return cardFragment
        }

        fun getInstance(header: String, title: String, leftCancel: String, rightOk: String, isCancelable: Boolean, middleBtn: String, listener: CommonTripleDialogClickListener): CommonDialogTripleBtn {
            val cardFragment = CommonDialogTripleBtn()
            mHeader = header
            mTitle = title
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            this.middleBtn = middleBtn
            mIsCancelable = isCancelable
            return cardFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_fragment_triplebtn, container, false)
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
        dialogOk.isSelected = true
        dialogExtra = v.findViewById(R.id.extra_TV)
        dialogExtra.text = mRightBtn
        dialogExtra.setOnClickListener(this)

        iv_close_icon = v.findViewById(R.id.iv_close_icon)

        if (AppUtils.isRevisit!!) {
            iv_close_icon.visibility = View.VISIBLE
            AppUtils.isRevisit = false
        } else
            iv_close_icon.visibility = View.GONE

        dialogHeader.text = mHeader
        dialogContent.text = mTitle
        dialogCancel.text = mLeftBtn
        dialogOk.text = middleBtn

        dialogCancel.setOnClickListener(this)
        dialogOk.setOnClickListener(this)
        iv_close_icon.setOnClickListener(this)
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
                mListener.onMiddleClick()
            }
            R.id.iv_close_icon -> {
                dismiss()
                mListener.onCancelClick()
            }
            R.id.extra_TV -> {
                mListener.onRightClick()
                dismiss()
            }
        }
    }

    private fun deSelectAll() {
        dialogOk.isSelected = false
        dialogCancel.isSelected = false
    }
}
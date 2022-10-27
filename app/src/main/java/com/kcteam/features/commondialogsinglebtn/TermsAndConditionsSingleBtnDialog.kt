package com.kcteam.features.commondialogsinglebtn

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 05-03-2019.
 */
class TermsAndConditionsSingleBtnDialog: DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var dialogHeader: AppCustomTextView
    private lateinit var dialogContent: AppCustomTextView
    private lateinit var dialogOk: AppCustomTextView
    private lateinit var iv_close_icon: ImageView

    companion object {
        private lateinit var mHeader: String
        private lateinit var mTitle: String
        private lateinit var mActionBtn: String
        private lateinit var mRightBtn: String
        private lateinit var mListener: OnDialogClickListener

        fun getInstance(header: String, title: String, actionBtn: String, listener: OnDialogClickListener): TermsAndConditionsSingleBtnDialog {
            val cardFragment = TermsAndConditionsSingleBtnDialog()
            mHeader = header
            mTitle = title
            mActionBtn = actionBtn
            mListener = listener
            return cardFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_terms_conditions, container, false)
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
        dialogOk = v.findViewById(R.id.ok_TV)
        dialogOk.isSelected = false
        dialogOk.text = mActionBtn
        iv_close_icon = v.findViewById(R.id.iv_close_icon)

        if (AppUtils.isShopAdded) {
            iv_close_icon.visibility = View.VISIBLE
            AppUtils.isShopAdded = false
        } else
            iv_close_icon.visibility = View.GONE

        dialogHeader.text = mHeader
        dialogContent.text = mTitle
//        dialogOk.setText(mRightBtn)

        dialogContent.movementMethod = ScrollingMovementMethod()

        dialogOk.setOnClickListener(this)
        iv_close_icon.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
//        deSelectAll()
        when (p0!!.id) {
            R.id.ok_TV -> {
                dialogOk.isSelected = true
                dismiss()
                mListener.onOkClick()
            }
            R.id.iv_close_icon -> {
                dismiss()
            }
        }
    }

    private fun deSelectAll() {
        dialogOk.isSelected = false
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
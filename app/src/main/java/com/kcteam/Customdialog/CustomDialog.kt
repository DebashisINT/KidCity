package com.kcteam.Customdialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.features.commondialogsinglebtn.CommonDialogSingleBtn
import com.kcteam.features.commondialogsinglebtn.OnDialogClickListener
import com.kcteam.widgets.AppCustomTextView


class CustomDialog : DialogFragment(), View.OnClickListener{
    private lateinit var mContext: Context
    private lateinit var dialogHeader: AppCustomTextView
    private lateinit var dialogContent: AppCustomTextView
    private lateinit var dialogOk: AppCustomTextView
    private lateinit var dialogCancle: AppCustomTextView

    companion object {
        private lateinit var mHeader: String
        private lateinit var mTitle: String
        private lateinit var mActionBtn: String
        private lateinit var mActionCancleBtn: String
        private lateinit  var mFlagShowCancle: String
        private lateinit var mListener: OnDialogCustomClickListener


        fun getInstance(header: String, title: String, actionBtn: String,actionCancleBtn:String,FlagShowCancle:String, listener: OnDialogCustomClickListener): CustomDialog {
            val cardFragment = CustomDialog()
            mHeader = header
            mTitle = title
            mActionBtn = actionBtn
            mActionCancleBtn = actionCancleBtn
            mFlagShowCancle= FlagShowCancle
            mListener = listener
            return cardFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.activity_custom_dialog, container, false)
        initView(v)



        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(v: View) {
        dialogHeader = v.findViewById(R.id.dialog_message_headerTV)
        dialogContent = v.findViewById(R.id.dialog_message_header_SubTitleTV)
        dialogOk = v.findViewById(R.id.tv_message_ok)
        dialogCancle = v.findViewById(R.id.tv_message_cancle)
        dialogOk.isSelected = false
        dialogCancle.isSelected = false
        dialogOk.text = mActionBtn


        if(mFlagShowCancle=="1"){
            dialogCancle.visibility = View.VISIBLE
            dialogCancle.text = mActionCancleBtn
            dialogCancle.setOnClickListener(this)
        }
        else{
            dialogCancle.visibility = View.GONE
        }



        dialogHeader.text = mHeader
        dialogContent.text = mTitle


        dialogOk.setOnClickListener(this)
        dialogCancle.setOnClickListener(this)


    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tv_message_ok -> {
                dialogOk.isSelected = true
                dismiss()
                mListener.onOkClick()
            }
            R.id.tv_message_cancle -> {
                dialogCancle.isSelected = true
                dismiss()
                mListener.onNoClick()
            }

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
package com.kcteam.features.splash.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.kcteam.R
import com.kcteam.widgets.AppCustomTextView


class LocationHintDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var tv_ok: AppCustomTextView

    companion object {

        private var listener: OnItemSelectedListener? = null

        fun newInstance(param: OnItemSelectedListener): LocationHintDialog {
            val dialogFragment = LocationHintDialog()

            listener = param

            return dialogFragment
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(true)

        val v = inflater.inflate(R.layout.dialog_loc_hint, container, false)

        isCancelable = false

        initView(v)
        initClickListener()

        return v
    }

    private fun initView(v: View) {
        v.apply {
            tv_ok = findViewById(R.id.tv_ok_loc_hint)
        }
    }

    private fun initClickListener() {
        tv_ok.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.tv_ok_loc_hint -> {
                dismiss()
                listener?.onOkClick()
            }
        }
    }

    interface OnItemSelectedListener {
        fun onOkClick()
    }
}
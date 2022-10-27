package com.kcteam.features.addAttendence


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
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
class SelfieDialog : DialogFragment() {

    private lateinit var mContext: Context

    private lateinit var cancel_TV: AppCustomTextView
    private lateinit var ok_TV: AppCustomTextView
    private var isSingleButton: Boolean? = null

    companion object {

        private lateinit var function: () -> Unit

        fun getInstance(mFunction: () -> Unit, isSingleButton: Boolean): SelfieDialog {
            val dialog = SelfieDialog()
            function = mFunction

            val bundle = Bundle()
            bundle.putBoolean("isSingleButton", isSingleButton)
            dialog.arguments = bundle

            return dialog
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context

        isSingleButton = arguments?.getBoolean("isSingleButton")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_selfie, container, false)
        isCancelable = false

        initView(v)

        return v
    }

    private fun initView(v: View) {
        v.apply {
            cancel_TV = findViewById(R.id.cancel_TV)
            ok_TV = findViewById(R.id.ok_TV)
        }

        if (isSingleButton!!) {
            cancel_TV.visibility = View.GONE
            ok_TV.isSelected = false
        }
        else {
            cancel_TV.visibility = View.VISIBLE
            ok_TV.isSelected = true
        }

        cancel_TV.setOnClickListener {
            BaseActivity.isApiInitiated = false
            dismiss()
        }

        ok_TV.setOnClickListener {
            function()
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
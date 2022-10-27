package com.kcteam.features.dashboard.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
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
class CustomProgressDialog : DialogFragment() {

    private lateinit var mContext: Context

    companion object {

        fun getInstance(): CustomProgressDialog {
            return CustomProgressDialog()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        //dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.custom_progress_dialog, container, false)

        isCancelable = false
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
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
}
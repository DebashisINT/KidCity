package com.kcteam.app

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.Window
import com.kcteam.R

/**
 * Created by Pratishruti on 16-03-2018.
 */
class CustomProgressDialog(val context: Context) {

    private lateinit var progressDialog: Dialog

    init {
        progressDialog = Dialog(context)
        progressDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val row = (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.dialog_custom_progresswheel, null)
        progressDialog.window!!.setContentView(row)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
    }

    fun showDialogForLoading(context: Context): Dialog {
        try {
            progressDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return progressDialog

    }

    fun dismissDialog() {
        if (progressDialog != null && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

}
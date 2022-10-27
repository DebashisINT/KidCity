package com.kcteam.app.uiaction

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.kcteam.R

/**
 * Created by rp : 27-10-2017:18:19
 */
class DisplayAlert {

    companion object {

        fun showSnackMessage(mContext: Context, dashboard_snackbar_layout: CoordinatorLayout, message: String?) {
            if (message == null || message.isEmpty())
                return

            val layoutParams = dashboard_snackbar_layout.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            dashboard_snackbar_layout.layoutParams = layoutParams
            val snackbar = Snackbar
                    .make(dashboard_snackbar_layout, message, Snackbar.LENGTH_LONG)
            val snackbarView = snackbar.view
            try {
                val snackbarTextView = snackbarView.findViewById<TextView>(R.id.snackbar_text) as TextView
                snackbarTextView.textSize = 15f
                snackbarTextView.gravity = View.TEXT_ALIGNMENT_CENTER
            } catch (e: Exception) {
                e.printStackTrace()
            }

            snackbarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            snackbar.show()
        }

        fun showSnackMessage(mContext: Context, dashboard_snackbar_layout: CoordinatorLayout, message: String?, duration: Int) {
            if (message == null || message.isEmpty())
                return

            val layoutParams = dashboard_snackbar_layout.layoutParams as RelativeLayout.LayoutParams
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            dashboard_snackbar_layout.layoutParams = layoutParams
            val snackbar = Snackbar
                    .make(dashboard_snackbar_layout, message, duration)
            val snackbarView = snackbar.view
            try {
                val snackbarTextView = snackbarView.findViewById<TextView>(R.id.snackbar_text) as TextView
                snackbarTextView.textSize = 15f
                snackbarTextView.gravity = View.TEXT_ALIGNMENT_CENTER
            } catch (e: Exception) {
                e.printStackTrace()
            }

            snackbarView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            snackbar.show()
        }
    }


}
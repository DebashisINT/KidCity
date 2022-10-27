package com.kcteam.app.utils

import android.content.Context
import android.graphics.Rect
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.kcteam.R
import com.kcteam.widgets.AppCustomTextView


/**
 * Class used to show Toast Messages
 */
object Toaster {

    private val SHORT_TOAST_DURATION = 2000
    private val TOAST_DURATION_MILLS: Long = 1000 //change if need longer
    private var toast: Toast? = null

    /**
     * Long toast message
     * TOAST_DURATION_MILLS controls the duration
     * currently set to 6 seconds
     *
     * @param context Application Context
     * @param msg     Message to send
     */
    fun msgLong(context: Context?, /*view: View,*/ msg: String?) {
        if (context != null && msg != null) {
            if (toast != null) {
                toast!!.cancel()
            }
            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.toast_layout, null)

            val text = layout.findViewById<View>(R.id.tv_toast) as TextView
            text.text = msg
            Handler(context.mainLooper).post {
                toast = Toast(context)
                toast!!.duration = Toast.LENGTH_LONG
                /*val offset = getOffsetAboveButton(view)
                if (offset != null && offset.size == 2)
                    toast!!.setGravity(Gravity.CENTER, offset[0], offset[1])
                else
                    toast!!.setGravity(Gravity.CENTER, 0, 200)*/
                toast!!.view = layout


                object : CountDownTimer(Math.max(TOAST_DURATION_MILLS - SHORT_TOAST_DURATION, 1000), 1000) {
                    override fun onFinish() {

                        toast!!.show()
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        toast!!.show()
                    }
                }.start()
            }
        }
    }

    /**
     * Short toast message
     * (Predefined in AOS to 2000 ms = 2 sec)
     *
     * @param context Application Context
     * @param msg     Message to send
     */
    fun msgShort(context: Context?, msg: String?) {
        if (context != null && msg != null) {
            if (toast != null) {
                toast!!.cancel()
            }

            Handler(context.mainLooper).post {
                val inflater = LayoutInflater.from(context)
                val layout: View

                layout = inflater.inflate(R.layout.toast_layout, null)

                val text = layout.findViewById<AppCustomTextView>(R.id.tv_toast) as AppCustomTextView
                text.text = msg
                toast = Toast(context)
                toast!!.view = layout

                toast!!.duration = Toast.LENGTH_SHORT


                toast!!.show()
            }
        }
    }

    private fun getOffsetAboveButton(v: View): IntArray? {
        val offset = IntArray(2)
        var xOffset = 0
        var yOffset = 0
        val gvr = Rect()

        val parent = v.parent as View
        val parentHeight = parent.height

        if (v.getGlobalVisibleRect(gvr)) {
            val root = v.rootView

            val halfWidth = root.right / 2
            val halfHeight = root.bottom / 2

            val parentCenterX = (gvr.right - gvr.left) / 2 + gvr.left

            val parentCenterY = (gvr.bottom - gvr.top) / 2 + gvr.top

            if (parentCenterY <= halfHeight) {
                yOffset = -(halfHeight - parentCenterY) - parentHeight
            } else {
                yOffset = parentCenterY - halfHeight - parentHeight
            }

            if (parentCenterX < halfWidth) {
                xOffset = -(halfWidth - parentCenterX)
            }

            if (parentCenterX >= halfWidth) {
                xOffset = parentCenterX - halfWidth
            }
        }
        offset[0] = xOffset
        offset[1] = yOffset
        return offset

    }
}
/**
 * Private constructor. Prevents instantiation from other classes.
 */

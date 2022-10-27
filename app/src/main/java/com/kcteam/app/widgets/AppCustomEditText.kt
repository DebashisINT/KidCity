package com.kcteam.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.kcteam.R
class AppCustomEditText : androidx.appcompat.widget.AppCompatEditText {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setCustomFont(context, attrs)
    }

    private fun setCustomFont(ctx: Context, attrs: AttributeSet) {
        val a = ctx.obtainStyledAttributes(attrs, R.styleable.ViewStyle)
        val customFont = a.getString(R.styleable.ViewStyle_customFont)
        setCustomFont(ctx, customFont)
        a.recycle()
    }

    fun setCustomFont(ctx: Context, asset: String?): Boolean {
        var tf: Typeface? = null
        try {
            tf = Typeface.createFromAsset(ctx.assets, asset)
        } catch (e: Exception) {
            return false
        }

        typeface = tf
        return true
    }


}

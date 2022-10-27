package com.kcteam.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.kcteam.R


class AppCustomTextView : androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
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

    fun init() {
        val tf = Typeface.createFromAsset(context.assets, "fonts/Celias_Light.ttf")
        setTypeface(tf, 1)

    }


}

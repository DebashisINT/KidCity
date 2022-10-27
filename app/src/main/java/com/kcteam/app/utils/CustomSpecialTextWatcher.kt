package com.kcteam.app.utils

import android.text.Editable
import android.text.TextWatcher
import com.kcteam.widgets.AppCustomEditText
import java.math.RoundingMode
import java.text.NumberFormat

/**
 * Created by Saikat on 12-10-2018.
 */

class CustomSpecialTextWatcher
/**
 * Add a text watcher to Edit text for decimal formats
 *
 * @param editText
 * EditText to add DecimalTextWatcher
 * @param before
 * digits before decimal point
 * @param after
 * digits after decimal point
 */
(private val editText: AppCustomEditText, private val integerConstraint: Int, private val fractionConstraint: Int, private val listener: GetCustomTextChangeListener) : TextWatcher {
    private val numberFormat = NumberFormat.getNumberInstance()
    private var temp = ""
    private var moveCaretTo: Int = 0
    private val maxLength: Int

    init {
        this.maxLength = integerConstraint + fractionConstraint + 1
        numberFormat.maximumIntegerDigits = integerConstraint
        numberFormat.maximumFractionDigits = fractionConstraint
        numberFormat.roundingMode = RoundingMode.DOWN
        numberFormat.isGroupingUsed = false
    }

    private fun countOccurrences(str: String, c: Char): Int {
        var count = 0
        for (element in str) {
            if (element == c) {
                count++
            }
        }
        return count
    }

    override fun afterTextChanged(s: Editable) {
        // remove to prevent StackOverFlowException
        editText.removeTextChangedListener(this)
        val ss = s.toString()
        val len = ss.length
        val dots = countOccurrences(ss, '.')
        var shouldParse = dots <= 1 && if (dots == 0) len != integerConstraint + 1 else len < maxLength + 1
        var x = false
        if (dots == 1) {
            val indexOf = ss.indexOf('.')
            try {
                if (ss[indexOf + 1] == '0') {
                    shouldParse = false
                    x = true
                    if (ss.substring(indexOf).length > 2) {
                        shouldParse = true
                        x = false
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
        if (shouldParse) {
            if (len > 1 && ss.lastIndexOf(".") != len - 1) {
                try {
                    val d = java.lang.Double.parseDouble(ss)
                    if (d != null) {
                        //editText.setText(numberFormat.format(d))
                        s.replace(0, s.length, numberFormat.format(d))
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }

            }
        } else {
            if (x) {
                //editText.setText(ss)
                s.replace(0, s.length, ss)
            } else {
                //editText.setText(temp)
                s.replace(0, s.length, temp)
            }
        }
        editText.addTextChangedListener(this) // reset listener

        // tried to fix caret positioning after key type:
        if (editText.text.toString().isNotEmpty()) {
            if (dots == 0 && len >= integerConstraint && moveCaretTo > integerConstraint) {
                moveCaretTo = integerConstraint
            } else if (dots > 0 && len >= maxLength && moveCaretTo > maxLength) {
                moveCaretTo = maxLength
            }
            try {
                editText.setSelection(editText.text.toString().length)
                // et.setSelection(moveCaretTo); <- almost had it :))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        listener.customTextChange(editText.text.toString().trim())
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        listener.beforeTextChange(editText.text.toString().trim())
        moveCaretTo = editText.selectionEnd
        temp = s.toString()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val length = editText.text.toString().length
        if (length > 0) {
            moveCaretTo = start + count - before
        }
    }

    interface GetCustomTextChangeListener {
        fun customTextChange(text: String)
        fun beforeTextChange(text: String)
    }
}

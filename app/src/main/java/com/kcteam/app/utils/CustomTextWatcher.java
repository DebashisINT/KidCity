package com.kcteam.app.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class CustomTextWatcher implements TextWatcher {
    private NumberFormat numberFormat = NumberFormat.getNumberInstance();
    private EditText editText;
    private String temp = "";
    private int moveCaretTo;
    private int integerConstraint;
    private int fractionConstraint;
    private int maxLength;

    /**
     * Add a text watcher to Edit text for decimal formats
     * 
     * @param editText
     *            EditText to add DecimalTextWatcher
     * @param before
     *            digits before decimal point
     * @param after
     *            digits after decimal point
     */
    public CustomTextWatcher(EditText editText, int before, int after) {
        this.editText = editText;
        this.integerConstraint = before;
        this.fractionConstraint = after;
        this.maxLength = before + after + 1;
        numberFormat.setMaximumIntegerDigits(integerConstraint);
        numberFormat.setMaximumFractionDigits(fractionConstraint);
        numberFormat.setRoundingMode(RoundingMode.DOWN);
        numberFormat.setGroupingUsed(false);
    }

    private int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // remove to prevent StackOverFlowException
        editText.removeTextChangedListener(this);
        String ss = s.toString();
        int len = ss.length();
        int dots = countOccurrences(ss, '.');
        boolean shouldParse = dots <= 1 && (dots == 0 ? len != (integerConstraint + 1) : len < (maxLength + 1));
        boolean x = false;
        if (dots == 1) {
            int indexOf = ss.indexOf('.');
            try {
                if (ss.charAt(indexOf + 1) == '0') {
                    shouldParse = false;
                    x = true;
                    if (ss.substring(indexOf).length() > 2) {
                        shouldParse = true;
                        x = false;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (shouldParse) {
            if (len > 1 && ss.lastIndexOf(".") != len - 1) {
                try {
                    Double d = Double.parseDouble(ss);
                    if (d != null) {
                        editText.setText(numberFormat.format(d));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (x) {
                editText.setText(ss);
            } else {
                editText.setText(temp);
            }
        }
        editText.addTextChangedListener(this); // reset listener

        // tried to fix caret positioning after key type:
        if (editText.getText().toString().length() > 0) {
            if (dots == 0 && len >= integerConstraint && moveCaretTo > integerConstraint) {
                moveCaretTo = integerConstraint;
            } else if (dots > 0 && len >= (maxLength) && moveCaretTo > (maxLength)) {
                moveCaretTo = maxLength;
            }
            try {
                editText.setSelection(editText.getText().toString().length());
                // et.setSelection(moveCaretTo); <- almost had it :))
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        moveCaretTo = editText.getSelectionEnd();
        temp = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int length = editText.getText().toString().length();
        if (length > 0) {
            moveCaretTo = start + count - before;
        }
    }
}
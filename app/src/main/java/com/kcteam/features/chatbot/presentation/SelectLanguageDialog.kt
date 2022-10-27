package com.kcteam.features.chatbot.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.domain.AssignToDDEntity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 18-Sep-18.
 */
class SelectLanguageDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var rb_english: RadioButton
    private lateinit var rb_hindi: RadioButton
    private lateinit var rb_bengali: RadioButton
    private lateinit var rb_urdu: RadioButton
    private lateinit var cancel_TV: AppCustomTextView
    private lateinit var ok_TV: AppCustomTextView
    private lateinit var rb_malayalam: RadioButton
    private lateinit var rb_tamil: RadioButton
    private lateinit var rb_telugu: RadioButton
    private lateinit var rb_kannada: RadioButton
    private lateinit var rb_marathi: RadioButton
    private lateinit var rb_oriya: RadioButton

    private var language = ""

    companion object {

        private var listener: OnItemSelectedListener? = null

        fun newInstance(param: OnItemSelectedListener): SelectLanguageDialog {
            val dialogFragment = SelectLanguageDialog()
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
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)

        val v = inflater.inflate(R.layout.dialog_language, container, false)

        isCancelable = false

        initView(v)
        initClickListener()

        return v
    }

    private fun initView(v: View) {
        v.apply {
            rb_english = findViewById(R.id.rb_english)
            rb_hindi = findViewById(R.id.rb_hindi)
            rb_bengali = findViewById(R.id.rb_bengali)
            rb_urdu = findViewById(R.id.rb_urdu)
            cancel_TV = findViewById(R.id.cancel_TV)
            ok_TV = findViewById(R.id.ok_TV)
            rb_malayalam = findViewById(R.id.rb_malayalam)
            rb_tamil = findViewById(R.id.rb_tamil)
            rb_telugu = findViewById(R.id.rb_telugu)
            rb_kannada = findViewById(R.id.rb_kannada)
            rb_marathi = findViewById(R.id.rb_marathi)
            rb_oriya = findViewById(R.id.rb_oriya)
        }

        ok_TV.isSelected = true
    }

    private fun initClickListener() {
        ok_TV.setOnClickListener(this)
        rb_english.setOnClickListener(this)
        rb_hindi.setOnClickListener(this)
        rb_bengali.setOnClickListener(this)
        rb_urdu.setOnClickListener(this)
        cancel_TV.setOnClickListener(this)
        rb_marathi.setOnClickListener(this)
        rb_malayalam.setOnClickListener(this)
        rb_tamil.setOnClickListener(this)
        rb_telugu.setOnClickListener(this)
        rb_kannada.setOnClickListener(this)
        rb_oriya.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.ok_TV -> {
                dismiss()
                listener?.onItemSelect(language)
            }

            R.id.rb_english -> {
                if (!rb_english.isSelected)
                    rb_english.isSelected = true
                language = "en"
            }

            R.id.rb_hindi -> {
                if (!rb_hindi.isSelected)
                    rb_hindi.isSelected = true
                language = "hi"
            }

            R.id.rb_bengali -> {
                if (!rb_bengali.isSelected)
                    rb_bengali.isSelected = true
                language = "bn"
            }

            R.id.rb_urdu -> {
                if (!rb_urdu.isSelected)
                    rb_urdu.isSelected = true
                language = "ur"
            }

            R.id.rb_malayalam -> {
                if (!rb_malayalam.isSelected)
                    rb_malayalam.isSelected = true
                language = "ml"
            }

            R.id.rb_tamil -> {
                if (!rb_tamil.isSelected)
                    rb_tamil.isSelected = true
                language = "ta"
            }

            R.id.rb_telugu -> {
                if (!rb_telugu.isSelected)
                    rb_telugu.isSelected = true
                language = "te"
            }

            R.id.rb_kannada -> {
                if (!rb_kannada.isSelected)
                    rb_kannada.isSelected = true
                language = "kn"
            }

            R.id.rb_marathi -> {
                if (!rb_marathi.isSelected)
                    rb_marathi.isSelected = true
                language = "mr"
            }

            R.id.rb_oriya -> {
                if (!rb_oriya.isSelected)
                    rb_oriya.isSelected = true
                language = "or"
            }

            R.id.cancel_TV -> {
                dismiss()
            }
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelect(language: String)
    }
}
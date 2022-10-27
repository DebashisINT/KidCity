package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.widget.AppCompatImageView
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.utils.Toaster
import com.kcteam.widgets.AppCustomTextView


class CodeScannerTextDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var tv_scan_text: AppCustomTextView
    private lateinit var ll_open: LinearLayout
    private lateinit var ll_share: LinearLayout
    private lateinit var iv_close_icon: AppCompatImageView

    private var text = ""

    companion object {
        fun newInstance(text: String): CodeScannerTextDialog {
            val dialogFragment = CodeScannerTextDialog()

            val bundle = Bundle()
            bundle.putString("text", text)
            dialogFragment.arguments = bundle

            return dialogFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        text = arguments?.getString("text").toString()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(true)

        val v = inflater.inflate(R.layout.dialog_scan_details, container, false)

        initView(v)
        initClickLisetener()

        return v
    }

    private fun initView(v: View) {
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        tv_scan_text = v.findViewById(R.id.tv_scan_text)
        ll_open = v.findViewById(R.id.ll_open)
        ll_share = v.findViewById(R.id.ll_share)

        tv_scan_text.text = text
    }

    private fun initClickLisetener() {
        iv_close_icon.setOnClickListener(this)
        ll_open.setOnClickListener(this)
        ll_share.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_close_icon -> {
                dismiss()
            }

            R.id.ll_open -> {
                if (text.startsWith("https") || text.startsWith("http")) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(text))
                    startActivity(browserIntent)
                }
                else
                    Toaster.msgShort(mContext, "Only url/link can be opened.")
            }

            R.id.ll_share -> {
                val intent = Intent()
                intent.also {
                    it.action = Intent.ACTION_SEND
                    it.type = "text/plain"
                    it.putExtra(Intent.EXTRA_TEXT, text)
                    startActivity(Intent.createChooser(it, "Share via"))
                }
            }
        }
    }
}
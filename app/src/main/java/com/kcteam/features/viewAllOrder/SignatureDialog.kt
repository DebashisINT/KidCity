package com.kcteam.features.viewAllOrder

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.widget.AppCompatImageView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.widgets.AppCustomTextView
import com.github.gcacace.signaturepad.views.SignaturePad
import java.io.File

class SignatureDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var iv_close_icon: AppCompatImageView
    private lateinit var signature_pad: SignaturePad
    private lateinit var cancel_TV: AppCustomTextView
    private lateinit var ok_TV: AppCustomTextView

    companion object {

        private lateinit var onOkClick: (String) -> Unit

        fun getInstance(onOkClick: (String) -> Unit): SignatureDialog {
            val dialog = SignatureDialog()

            this.onOkClick = onOkClick

            return dialog
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_signature_pad, container, false)

        isCancelable = false

        initView(v)
        initClickListener()

        return v
    }
    private fun initView(v: View) {
        v.apply {
            signature_pad = findViewById(R.id.signature_pad)
            iv_close_icon = findViewById(R.id.iv_close_icon)
            cancel_TV = findViewById(R.id.cancel_TV)
            ok_TV = findViewById(R.id.ok_TV)
        }

        ok_TV.isSelected = true

        signature_pad.setOnSignedListener(object : SignaturePad.OnSignedListener{
            override fun onStartSigning() {
            }

            override fun onClear() {
            }

            override fun onSigned() {
            }
        })
    }

    private fun initClickListener() {
        iv_close_icon.setOnClickListener(this)
        ok_TV.setOnClickListener(this)
        cancel_TV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_close_icon -> {
                dismiss()
            }

            R.id.ok_TV -> {
                if (signature_pad.isEmpty)
                    Toaster.msgShort(mContext, "Please enter signature")
                else {
                    val bitmap = signature_pad.signatureBitmap
                    val folderPath = FTStorageUtils.getFolderPath(mContext)
                    val file = File("$folderPath/" + System.currentTimeMillis() + ".jpg")
                    FTStorageUtils.saveBitmapToJPG(bitmap, file)
                    onOkClick(file.absolutePath)
                    dismiss()
                }
            }

            R.id.cancel_TV -> {
                signature_pad.clear()
            }
        }
    }
}
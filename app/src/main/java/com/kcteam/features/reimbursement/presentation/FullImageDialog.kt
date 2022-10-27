package com.kcteam.features.reimbursement.presentation

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.R

/**
 * Created by Saikat on 01-02-2019.
 */
class FullImageDialog : DialogFragment() {

    private lateinit var mContext: Context
    private lateinit var iv_full_image: ImageView

    private var imageLink = ""

    companion object {

        fun getInstance(imageLink: String): FullImageDialog {
            val dialog = FullImageDialog()

            val bundle = Bundle()
            bundle.putString("image_link", imageLink)
            dialog.arguments = bundle
            return dialog
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context

        imageLink = arguments?.getString("image_link")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val v = inflater.inflate(R.layout.dialog_full_image, container, false)

        initView(v)

        return v
    }

    private fun initView(v: View) {
        iv_full_image = v.findViewById(R.id.iv_full_image)

        if (!TextUtils.isEmpty(imageLink)) {
            Glide.with(mContext)
                    .load(imageLink)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                    .into(iv_full_image)
        }
    }
}
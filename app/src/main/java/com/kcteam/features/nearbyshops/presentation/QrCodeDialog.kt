package com.kcteam.features.nearbyshops.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.DialogFragment
import androidx.appcompat.widget.AppCompatImageView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import java.io.File
import java.lang.Exception

class QrCodeDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var iv_qrCode: ImageView
    private lateinit var ll_save: LinearLayout
    private lateinit var ll_share: LinearLayout
    private lateinit var iv_close_icon: AppCompatImageView
    private lateinit var dialog_header_TV: AppCustomTextView

    private var shopId = ""
    private var shopName = ""
    private var orderNo = ""
    private var header = ""

    companion object {

        private lateinit var qrCode: Bitmap

        fun newInstance(mQrCode: Bitmap, shopId: String, shopName: String, orderNo: String, header: String): QrCodeDialog {
            val dialogFragment = QrCodeDialog()

            qrCode = mQrCode

            val bundle = Bundle()
            bundle.also {
                it.putString("shopId", shopId)
                it.putString("shopName", shopName)
                it.putString("orderNo", orderNo)
                it.putString("header", header)
                dialogFragment.arguments = it
            }

            return dialogFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        shopId = arguments?.getString("shopId").toString()
        shopName = arguments?.getString("shopName").toString()
        header = arguments?.getString("header").toString()

        try {
            orderNo = arguments?.getString("orderNo").toString()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(true)

        val v = inflater.inflate(R.layout.dialog_qrcode, container, false)

        initView(v)
        initClickLisetener()

        return v
    }

    private fun initView(v: View) {
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        iv_qrCode = v.findViewById(R.id.iv_qrCode)
        ll_save = v.findViewById(R.id.ll_save)
        ll_share = v.findViewById(R.id.ll_share)
        dialog_header_TV = v.findViewById(R.id.dialog_header_TV)

        dialog_header_TV.text = header
        iv_qrCode.setImageBitmap(qrCode)
    }

    private fun initClickLisetener() {
        iv_close_icon.setOnClickListener(this)
        ll_save.setOnClickListener(this)
        ll_share.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_close_icon -> {
                dismiss()
            }

            R.id.ll_save -> {
                val folderPath = FTStorageUtils.getFolderPath(mContext)

                var file: File
                file = if (TextUtils.isEmpty(orderNo))
                    File("$folderPath/" + shopName + "_" + shopId + ".jpg")
                else
                    File("$folderPath/" + shopName + "_" + orderNo + ".jpg")

                if (file.exists()) {
                    file.delete()
                    if (file.exists()) {
                        file.canonicalFile.delete()
                        if (file.exists()) {
                            mContext.deleteFile(file.name)
                        }
                    }
                }

                FTStorageUtils.saveBitmapToJPG(qrCode, file)
                Toaster.msgShort(mContext, "QrCode saved.")
            }

            R.id.ll_share -> {
                //val path: String = MediaStore.Images.Media.insertImage(mContext.contentResolver, qrCode, "Image Description", null)
                val uri = FTStorageUtils.saveImageExternal(qrCode, mContext, "$shopId.jpg")//Uri.parse(path)

                val intent = Intent()
                intent.also {
                    it.action = Intent.ACTION_SEND
                    it.type = "image/jpeg"
                    it.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(it, "Share via"))
                }
            }
        }
    }
}
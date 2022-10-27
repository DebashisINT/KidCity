package com.kcteam.features.shopdetail.presentation

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.NewFileUtils
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.billing.model.BillingListDataModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.reimbursement.presentation.FullImageDialog
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.pnikosis.materialishprogress.ProgressWheel
import java.io.File

class ShopBillingDetailsFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rl_bill_details_main: RelativeLayout
    private lateinit var et_invoice_no: AppCustomEditText
    private lateinit var et_invoice_amount: AppCustomEditText
    private lateinit var et_remark: AppCustomEditText
    private lateinit var tv_invoice_date: AppCustomTextView
    private lateinit var rv_cart_list: RecyclerView
    private lateinit var tv_total_order_amount: AppCustomTextView
    private lateinit var tv_total_order_value: AppCustomTextView
    private lateinit var ll_attachment: LinearLayout
    private lateinit var tv_attachment: AppCustomTextView
    private lateinit var webview: WebView

    private lateinit var progress_wheel: ProgressWheel

    companion object {

        private var billing: BillingListDataModel? = null

        fun newInstance(objects: Any): ShopBillingDetailsFragment {
            val fragment = ShopBillingDetailsFragment()

            if (objects is BillingListDataModel)
                billing = objects

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_billing_details, container, false)
        initView(view)
        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView(view: View) {

        rl_bill_details_main = view.findViewById(R.id.rl_bill_details_main)
        et_invoice_no = view.findViewById(R.id.et_invoice_no)
        et_invoice_amount = view.findViewById(R.id.et_invoice_amount)
        et_remark = view.findViewById(R.id.et_remark)
        tv_invoice_date = view.findViewById(R.id.tv_invoice_date)
        rv_cart_list = view.findViewById(R.id.rv_cart_list)
        rv_cart_list.layoutManager = LinearLayoutManager(mContext)
        tv_total_order_amount = view.findViewById(R.id.tv_total_order_amount)
        tv_total_order_value = view.findViewById(R.id.tv_total_order_value)
        tv_attachment = view.findViewById(R.id.tv_attachment)
        ll_attachment = view.findViewById(R.id.ll_attachment)
        webview = view.findViewById(R.id.webview)

        et_invoice_no.setText(billing?.invoice_no)
        et_invoice_amount.setText(billing?.invoice_amount)
        tv_invoice_date.text = AppUtils.convertToBillingFormat(billing?.invoice_date!!)

        progress_wheel=view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        rl_bill_details_main.setOnClickListener(null)

        if (!TextUtils.isEmpty(billing?.remarks))
            et_remark.setText(billing?.remarks)
        else
            et_remark.setText("N.A.")

        if (!TextUtils.isEmpty(billing?.billing_image)) {
            try {

                val file = File(billing?.billing_image!!)

                if (!billing?.billing_image?.startsWith("http")!!) {
                    val strFileName = file.name
                    tv_attachment.text = strFileName
                } else {
                    val strFileName = billing?.billing_image?.substring(billing?.billing_image?.lastIndexOf("/")!! + 1)
                    tv_attachment.text = strFileName
                }

                tv_attachment.setOnClickListener {

                    if (billing?.billing_image?.startsWith("http")!!) {
                        val mimeType = NewFileUtils.getMemeTypeFromFile(file.absolutePath + "." + NewFileUtils.getExtension(file))

                        if (mimeType != "image/jpeg" || mimeType != "image/png")
                            downloadFile(billing?.billing_image, tv_attachment.text.toString().trim())
                        else
                            openFile(file)
                    } else
                        openFile(file)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else
            tv_attachment.text = "N.A."

        val list = billing?.product_list

        if (list != null && list.isNotEmpty())
            rv_cart_list.adapter = ShopBillingProductListAdapter(mContext, list)

        tv_total_order_value.text = list?.size.toString()

        var totalAmount = 0.00
        list?.forEach {
            totalAmount += it.total_price?.toDouble()!!
        }

        val finalTotalAmount = String.format("%.2f", totalAmount)
        tv_total_order_amount.text = finalTotalAmount
    }

    private fun downloadFile(downloadUrl: String?, fileName: String) {
        try {

            if (!AppUtils.isOnline(mContext)){
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }

            progress_wheel.spin()

            PRDownloader.download(downloadUrl, FTStorageUtils.getFolderPath(mContext) + "/", fileName)
                    .build()
                    .setOnProgressListener {
                        Log.e("Billing Details", "Attachment Download Progress======> $it")
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            progress_wheel.stopSpinning()
                            val file = File(FTStorageUtils.getFolderPath(mContext) + "/" + fileName)
                            openFile(file)
                        }

                        override fun onError(error: Error) {
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("Download failed")
                            Log.e("Billing Details", "Attachment download error msg=======> " + error.serverErrorMessage)
                        }
                    })

        } catch (e: Exception) {
            (mContext as DashboardActivity).showSnackMessage("Download failed")
            progress_wheel.stopSpinning()
            e.printStackTrace()
        }

    }


    private fun openFile(file: File) {

        val mimeType = NewFileUtils.getMemeTypeFromFile(file.absolutePath + "." + NewFileUtils.getExtension(file))

        if (mimeType?.equals("application/pdf")!!) {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Pdf")
            }
        } else if (mimeType == "application/msword") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/msword")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }
        } else if (mimeType == "application/vnd.ms-excel") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }

        } else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.template") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.openxmlformats-officedocument.wordprocessingml.template")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }
        } else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }

        } else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }
        } else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.template") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }
        } else if (mimeType == "image/jpeg" || mimeType == "image/png") {
            FullImageDialog.getInstance(file.absolutePath).show((mContext as DashboardActivity).supportFragmentManager, "")
        }
    }
}
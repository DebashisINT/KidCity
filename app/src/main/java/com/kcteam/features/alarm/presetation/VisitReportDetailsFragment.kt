package com.kcteam.features.alarm.presetation

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.alarm.model.VisitReportDataModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import java.io.File

/**
 * Created by Saikat on 21-02-2019.
 */
class VisitReportDetailsFragment : BaseFragment() {

    private lateinit var mContext: Context
    private lateinit var rv_view_report_details_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var ll_visit_report_details_main: LinearLayout
    private lateinit var tv_member_name: AppCustomTextView
    private lateinit var iv_share: AppCompatImageView

    companion object {

        private var visitData: VisitReportDataModel? = null

        fun newInstance(objects: Any): VisitReportDetailsFragment {
            val fragment = VisitReportDetailsFragment()

            if (objects is VisitReportDataModel)
                visitData = objects
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_visit_report_details, container, false)
        initView(view)
        initAdapter()
        //getAttendanceReport()

        return view
    }

    private fun initView(view: View) {
        ll_visit_report_details_main = view.findViewById(R.id.ll_visit_report_details_main)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        rv_view_report_details_list = view.findViewById(R.id.rv_view_report_details_list)
        rv_view_report_details_list.layoutManager = LinearLayoutManager(mContext)
        tv_member_name = view.findViewById(R.id.tv_member_name)
        tv_member_name.text = visitData?.member_name
        iv_share = view.findViewById(R.id.iv_share)

        iv_share.setOnClickListener {
            if (visitData?.visit_details_list == null || visitData?.visit_details_list!!.isEmpty())
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
            else {
                val heading = "VISIT REPORT DETAILS"
                var pdfBody = "\n\n\nDate: " + (mContext as DashboardActivity).visitReportDate + "\n\n\nName: " +
                        visitData?.member_name + "\nReport To: " + visitData?.report_to +
                        "\n\n\n\n" + getString(R.string.shop_name) + "                                                                " +
                        getString(R.string.visit_time) + "        " + getString(R.string.duration) + "        " + getString(R.string.distance) +
                        "\n-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------\n\n"

                visitData?.visit_details_list?.forEach {
                    pdfBody += it.shop_name + "                                                              " + it.visit_time +
                            "             " + it.duration_spent + "                    " + it.distance +
                            "\n\n================================================================================\n\n"
                }

                val image = BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher)

                val path = FTStorageUtils.stringToPdf(pdfBody, mContext, "Visit_report_" +
                        (mContext as DashboardActivity).visitReportDate + "_" + Pref.user_id + ".pdf", image, heading, 3.7f)
                if (!TextUtils.isEmpty(path)) {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        val fileUrl = Uri.parse(path)

                        val file = File(fileUrl.path)
                        //val uri = Uri.fromFile(file)
                        //27-09-2021
                        val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
                        shareIntent.type = "image/png"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(Intent.createChooser(shareIntent, "Share pdf using"));
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else
                    (mContext as DashboardActivity).showSnackMessage("Pdf can not be sent.")
            }
        }
    }

    private fun initAdapter() {
        rv_view_report_details_list.adapter = VisitReportDetailsAdapter(mContext, visitData?.visit_details_list)
    }
}
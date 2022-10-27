package com.kcteam.features.report.presentation

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.report.model.AchievementDataModel
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 22-Jul-20.
 */
class AchievementDetailsFragment : BaseFragment() {

    private lateinit var mContext: Context
    private lateinit var rv_view_report_details_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var ll_visit_report_details_main: LinearLayout
    private lateinit var tv_member_name: AppCustomTextView
    private var achvData: AchievementDataModel? = null
    private lateinit var tv_shop_name: AppCustomTextView

    companion object {

        fun newInstance(achvData: Any): AchievementDetailsFragment {
            val fragment = AchievementDetailsFragment()

            if (achvData is AchievementDataModel) {
                val bundle = Bundle()
                bundle.putSerializable("achv", achvData)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        achvData = arguments?.getSerializable("achv") as AchievementDataModel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_achv_details, container, false)
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
        tv_member_name.text = achvData?.member_name
        tv_shop_name = view.findViewById(R.id.tv_shop_name)
        tv_shop_name.text = Pref.shopText + " Name"
    }

    private fun initAdapter() {
        rv_view_report_details_list.adapter = AchievementDetailsAdapter(mContext, achvData?.achv_details_list)
    }
}
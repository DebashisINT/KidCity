package com.kcteam.features.performance

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 26-10-2018.
 */
class GpsStatusFragment : BaseFragment() {

    private lateinit var mContext: Context
    private lateinit var rv_gps_status_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var ll_gps_status_main: LinearLayout
    private var date = ""

    companion object {

        private var ARG_GPS_DATE = "gps_date"

        fun getInstance(date: Any): GpsStatusFragment {
            val fragment = GpsStatusFragment()
            val bundle = Bundle()
            bundle.putString(ARG_GPS_DATE, date.toString())
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        date = arguments?.getString(ARG_GPS_DATE).toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_gps_status, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        rv_gps_status_list = view.findViewById(R.id.rv_gps_status_list)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        ll_gps_status_main = view.findViewById(R.id.ll_gps_status_main)
        ll_gps_status_main.setOnClickListener(null)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()

        if (!TextUtils.isEmpty(date))
            setAdapter()
        else
            tv_no_data_available.visibility = View.VISIBLE
    }

    private fun setAdapter() {
        val list = AppDatabase.getDBInstance()!!.gpsStatusDao().getDataDateWise(date)

        if (list != null && list.isNotEmpty()) {
            tv_no_data_available.visibility = View.GONE
            rv_gps_status_list.layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
            rv_gps_status_list.adapter = GpsStatusAdapter(mContext, list)
        } else
            tv_no_data_available.visibility = View.VISIBLE
    }
}
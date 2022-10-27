package com.kcteam.features.report.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.report.api.report_api.ReportRepoProvider
import com.kcteam.features.report.model.TargetVsAchvDataModel
import com.kcteam.features.report.model.TargetVsAchvResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 22-Jul-20.
 */
class TargetVsAchvFragment : BaseFragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mContext: Context
    private lateinit var rv_achv_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var ll_visit_report_main: LinearLayout
    private lateinit var tv_pick_date_range: AppCustomTextView
    private lateinit var ll_header: LinearLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_achv, container, false)

        initView(view)
        getTargVsAchvReport(AppUtils.getCurrentDateForShopActi(), AppUtils.getCurrentDateForShopActi())

        return view
    }

    private fun initView(view: View) {
        rv_achv_list = view.findViewById(R.id.rv_achv_list)
        rv_achv_list.layoutManager = LinearLayoutManager(mContext)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        ll_visit_report_main = view.findViewById(R.id.ll_visit_report_main)
        tv_pick_date_range = view.findViewById(R.id.tv_pick_date_range)
        ll_header = view.findViewById(R.id.ll_header)
        ll_header.visibility = View.GONE

        tv_pick_date_range.apply {
            visibility = View.VISIBLE
            text = AppUtils.getFormattedDate(Calendar.getInstance(Locale.ENGLISH).time) + " To " + AppUtils.getFormattedDate(Calendar.getInstance(Locale.ENGLISH).time)
        }

        ll_visit_report_main.setOnClickListener(null)
        tv_pick_date_range.setOnClickListener(this)
    }

    private fun getTargVsAchvReport(fromDate: String, toDate: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = ReportRepoProvider.getAchvReport()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getTargVsAchvReport(fromDate, toDate)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TargetVsAchvResponseModel
                            when {
                                response.status == NetworkConstant.SUCCESS -> {
                                    progress_wheel.stopSpinning()
                                    initAdapter(response.targ_achv_report_list)

                                }
                                response.status == NetworkConstant.SESSION_MISMATCH -> {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                    (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                    (mContext as DashboardActivity).finish()
                                }
                                response.status == NetworkConstant.NO_DATA -> {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                }
                                else -> {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            tv_no_data_available.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun initAdapter(targ_vs_achv_list: ArrayList<TargetVsAchvDataModel>?) {

        tv_no_data_available.visibility = View.GONE

        rv_achv_list.adapter = TargetVsAchvAdapter(mContext, targ_vs_achv_list, object : TargetVsAchvAdapter.OnClickListener {
            override fun onViewClick(adapterPosition: Int) {

                if (targ_vs_achv_list?.get(adapterPosition)?.targ_achv_details_list?.size == 0)
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                else
                    (mContext as DashboardActivity).loadFragment(FragType.TargetVsAchvDetailsFragment, true, targ_vs_achv_list?.get(adapterPosition)!!)
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_pick_date_range -> {
                val now = Calendar.getInstance(Locale.ENGLISH)
                val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                )
                dpd.isAutoHighlight = false
                //dpd.maxDate = Calendar.getInstance()
                dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
            }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd

        if (dayOfMonth < 10)
            day = "0$dayOfMonth"

        if (dayOfMonthEnd < 10)
            dayEnd = "0$dayOfMonthEnd"

        val fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear + 1).toString() + "") + "-" + year
        val endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd + 1).toString() + "") + "-" + yearEnd

        if (AppUtils.getStrinTODate(endString).before(AppUtils.getStrinTODate(fronString))) {
            (mContext as DashboardActivity).showSnackMessage("Your end date is before start date.")
            return
        }

        val diffInMillis = AppUtils.getStrinTODate(endString).time - AppUtils.getStrinTODate(fronString).time
        if (TimeUnit.MILLISECONDS.toDays(diffInMillis) > 7) {
            (mContext as DashboardActivity).showSnackMessage("Report must be generated for 7 Days")
            return
        }

        val date = day + AppUtils.getDayNumberSuffix(day.toInt()) + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + " " + year + " To " + dayEnd + AppUtils.getDayNumberSuffix(dayEnd.toInt()) + FTStorageUtils.formatMonth((++monthOfYearEnd).toString() + "") + " " + yearEnd
        tv_pick_date_range.text = date

        getTargVsAchvReport(AppUtils.convertFromRightToReverseFormat(fronString), AppUtils.convertFromRightToReverseFormat(endString))
    }
}
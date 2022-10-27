package com.kcteam.features.alarm.presetation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.alarm.api.report_confirm_api.ReviewConfirmRepoProvider
import com.kcteam.features.alarm.api.visit_report_api.VisitReportRepoProvider
import com.kcteam.features.alarm.model.ReviewConfirmInputModel
import com.kcteam.features.alarm.model.VisitReportDataModel
import com.kcteam.features.alarm.model.VisitReportResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.model.alarmconfigmodel.AlarmConfigDataModel
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Saikat on 21-02-2019.
 */
class VisitReportFragment : BaseFragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mContext: Context
    private lateinit var rv_view_report_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var iv_check_icon: ImageView
    private lateinit var tv_confirm_btn: AppCustomTextView
    private lateinit var tv_reviewed: AppCustomTextView
    private lateinit var include_confirm_footer: RelativeLayout
    private lateinit var view_report_confirm: View
    private lateinit var ll_visit_report_main: LinearLayout
    private lateinit var footer_view: View
    private lateinit var tv_pick_date_range: AppCustomTextView

    companion object {

        private var alarm: AlarmConfigDataModel? = null

        fun newInstance(objects: Any): VisitReportFragment {
            val fragment = VisitReportFragment()

            if (objects is AlarmConfigDataModel)
                alarm = objects
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_visit_report, container, false)

        initView(view)
        getVisitReport(AppUtils.getCurrentDateForShopActi(), AppUtils.getCurrentDateForShopActi())
        //initAdapter()

        return view
    }

    private fun initView(view: View) {
        rv_view_report_list = view.findViewById(R.id.rv_view_report_list)
        rv_view_report_list.layoutManager = LinearLayoutManager(mContext)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        iv_check_icon = view.findViewById(R.id.iv_check_icon)
        tv_confirm_btn = view.findViewById(R.id.tv_confirm_btn)
        tv_reviewed = view.findViewById(R.id.tv_reviewed)
        include_confirm_footer = view.findViewById(R.id.include_confirm_footer)
        view_report_confirm = view.findViewById(R.id.view)
        ll_visit_report_main = view.findViewById(R.id.ll_visit_report_main)
        footer_view = view.findViewById(R.id.view)
        tv_pick_date_range = view.findViewById(R.id.tv_pick_date_range)

        if ((mContext as DashboardActivity).isVisitFromAlarm) {
            include_confirm_footer.visibility = View.VISIBLE
            footer_view.visibility = View.VISIBLE
            tv_pick_date_range.visibility = View.GONE
        }
        else {
            (mContext as DashboardActivity).isConfirmed = true
            include_confirm_footer.visibility = View.GONE
            footer_view.visibility = View.GONE
            tv_pick_date_range.apply {
                visibility = View.VISIBLE
                text = AppUtils.getFormattedDate(Calendar.getInstance(Locale.ENGLISH).time) + " To " + AppUtils.getFormattedDate(Calendar.getInstance(Locale.ENGLISH).time)
            }
        }

        ll_visit_report_main.setOnClickListener(null)
        iv_check_icon.setOnClickListener(this)
        tv_confirm_btn.setOnClickListener(this)
        tv_reviewed.setOnClickListener(this)
        tv_pick_date_range.setOnClickListener(this)
    }

    private fun getVisitReport(fromDate: String, toDate: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = VisitReportRepoProvider.provideVisitReportRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getVisitReportList(fromDate, toDate)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val visitList = result as VisitReportResponseModel
                            when {
                                visitList.status == NetworkConstant.SUCCESS -> {
                                    progress_wheel.stopSpinning()
                                    initAdapter(visitList.visit_report_list)

                                }
                                visitList.status == NetworkConstant.SESSION_MISMATCH -> {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(visitList.message!!)
                                    startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                    (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                    (mContext as DashboardActivity).finish()
                                }
                                visitList.status == NetworkConstant.NO_DATA -> {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(visitList.message!!)

                                }
                                else -> {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(visitList.message!!)
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

    private fun initAdapter(visit_report_list: ArrayList<VisitReportDataModel>?) {

        tv_no_data_available.visibility = View.GONE

        rv_view_report_list.adapter = VisitReportAdapter(mContext, visit_report_list, object : VisitReportAdapter.OnClickListener {
            override fun onViewClick(adapterPosition: Int) {
                if (visit_report_list?.get(adapterPosition)?.total_shop_count == "0")
                    (mContext as DashboardActivity).showSnackMessage(visit_report_list[adapterPosition].member_name + " hasn't visited any shop yet")
                else {
                    (mContext as DashboardActivity).visitReportDate = tv_pick_date_range.text.toString().trim()
                    (mContext as DashboardActivity).loadFragment(FragType.VisitReportDetailsFragment, true, visit_report_list?.get(adapterPosition)!!)
                }
            }
        })
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_check_icon -> {
                iv_check_icon.isSelected = !iv_check_icon.isSelected
            }

            R.id.tv_confirm_btn -> {
                checkValidation()
            }

            R.id.tv_reviewed -> {
                iv_check_icon.isSelected = !iv_check_icon.isSelected
            }

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

    private fun checkValidation() {
        if (!iv_check_icon.isSelected)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_check_review))
        else
            callConfirmReviewApi()

    }

    private fun callConfirmReviewApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        try {

            val reviewConfirm = ReviewConfirmInputModel()
            reviewConfirm.session_token = Pref.session_token!!
            reviewConfirm.user_id = Pref.user_id!!
            reviewConfirm.alarm_id = alarm?.id!!
            reviewConfirm.report_id = alarm?.report_id!!

            if (alarm?.alarm_time_mins == "0")
                alarm?.alarm_time_mins = "00"

            reviewConfirm.report_time = alarm?.alarm_time_hours!! + ":" + alarm?.alarm_time_mins!! + ":00"
            reviewConfirm.view_time = AppUtils.getCurrentTime()

            val repository = ReviewConfirmRepoProvider.provideReviewConfirmRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.reviewConfirm(reviewConfirm)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                when {
                                    response.status == NetworkConstant.SUCCESS -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                        Handler().postDelayed(Runnable {
                                            (mContext as DashboardActivity).isConfirmed = true
                                            (mContext as DashboardActivity).onBackPressed()
                                        }, 100)

                                    }
                                    response.status == NetworkConstant.SESSION_MISMATCH -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                        startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                        (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                        (mContext as DashboardActivity).finish()
                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    }
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
        catch (e:Exception){
            e.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
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

        getVisitReport(AppUtils.convertFromRightToReverseFormat(fronString), AppUtils.convertFromRightToReverseFormat(endString))
    }
}
package com.kcteam.features.myjobs.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.myjobs.api.MyJobRepoProvider
import com.kcteam.features.myjobs.model.CustomerDataModel
import com.kcteam.features.myjobs.model.CustomerListResponseModel
import com.kcteam.features.myjobs.model.HistoryDataModel
import com.kcteam.features.myjobs.model.HistoryResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class ServiceHistoryFragment: BaseFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var mContext: Context

    private lateinit var tv_pick_date: AppCustomTextView
    private lateinit var rv_history_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data_available: AppCustomTextView

    private var startDate = ""
    private var endDate = ""
    private var customerdata: CustomerDataModel? = null

    companion object {
        fun newInstance(mcustomerdata: Any): ServiceHistoryFragment {
            val fragment = ServiceHistoryFragment()

            if (mcustomerdata is CustomerDataModel) {
                val bundle = Bundle()
                bundle.putSerializable("customer", mcustomerdata)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext= context

        startDate = AppUtils.getCurrentDateForShopActi()
        endDate = AppUtils.getCurrentDateForShopActi()
        customerdata = arguments?.getSerializable("customer") as CustomerDataModel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_service_history, container, false)

        initView(view)
        getHistoryListApi()

        return  view
    }

    private fun initView(view: View) {
        view.apply {
            rv_history_list = findViewById(R.id.rv_history_list)
            tv_pick_date = findViewById(R.id.tv_pick_date)
            progress_wheel = findViewById(R.id.progress_wheel)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
        }

        progress_wheel.stopSpinning()
        rv_history_list.layoutManager = LinearLayoutManager(mContext)

        tv_pick_date.text = AppUtils.getFormattedDateFromDate(AppUtils.getCurrentDateForShopActi()) + " To " + AppUtils.getFormattedDateFromDate(AppUtils.getCurrentDateForShopActi())

        tv_pick_date.setOnClickListener {
            val now = Calendar.getInstance(Locale.ENGLISH)
            now.add(Calendar.DAY_OF_MONTH, 1)
            val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.isAutoHighlight = true
            val tomorrowsDateLong = Calendar.getInstance(Locale.ENGLISH).timeInMillis + (1000 * 60 * 60 * 24)
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = tomorrowsDateLong
            //dpd.minDate = cal
            dpd.show((mContext as DashboardActivity).fragmentManager, "Datepickerdialog")
        }
    }

    override fun onDateSet(datePickerDialog: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int,
                           dayOfMonthEnd: Int) {

        datePickerDialog?.minDate = Calendar.getInstance(Locale.ENGLISH)
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
        val date = day + AppUtils.getDayNumberSuffix(day.toInt()) + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + " " + year + " To " + dayEnd + AppUtils.getDayNumberSuffix(dayEnd.toInt()) + FTStorageUtils.formatMonth((++monthOfYearEnd).toString() + "") + " " + yearEnd
        tv_pick_date.text = date

        startDate = AppUtils.convertFromRightToReverseFormat(fronString)
        endDate = AppUtils.convertFromRightToReverseFormat(endString)

        getHistoryListApi()
    }

    private fun getHistoryListApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository= MyJobRepoProvider.jobRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getHistoryList(startDate, endDate)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as HistoryResponseModel
                            progress_wheel.stopSpinning()

                            when (response.status) {
                                NetworkConstant.SUCCESS -> initAdapter(response.history_list!!)
                                NetworkConstant.NO_DATA -> {
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    initAdapter(ArrayList<HistoryDataModel>())
                                }
                                else -> (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun initAdapter(historyList: ArrayList<HistoryDataModel>) {
        if (historyList.isNotEmpty())
            tv_no_data_available.visibility = View.GONE
        else
            tv_no_data_available.visibility = View.VISIBLE

        rv_history_list.adapter = ServiceHistoryAdapter(mContext, historyList)
    }
}
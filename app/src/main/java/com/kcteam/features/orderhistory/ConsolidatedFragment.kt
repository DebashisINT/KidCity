package com.kcteam.features.orderhistory

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.averageshop.business.InfoWizard
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.UserLocationDataEntity
import com.kcteam.features.orderhistory.model.ConsolidatedCount
import com.kcteam.features.report.api.GetMISRepositoryProvider
import com.kcteam.features.report.model.MISResponse
import com.kcteam.features.report.model.MISShopListCount
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Pratishruti on 01-11-2017.
 */
class ConsolidatedFragment : BaseFragment(), CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, View.OnClickListener {


    private lateinit var consolidatedHistory: RecyclerView
    private lateinit var mContext: Context
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var mConsolidatedAdapter: ConsolidatedAdapter? = null


    private lateinit var this_week: AppCompatRadioButton
    private lateinit var last_week: AppCompatRadioButton
    private lateinit var this_month: AppCompatRadioButton
    private lateinit var last_month: AppCompatRadioButton
    private lateinit var date_range: AppCompatRadioButton
    private lateinit var dateRangeTv: AppCustomTextView

    /*private lateinit var radio_grp: RadioGroup
    private lateinit var radio_grp1: RadioGroup*/

    private lateinit var radioList: ArrayList<RadioButton>
    private val mAutoHighlight: Boolean = false
    private var isChkChanged: Boolean = false
    private lateinit var totalDistance: AppCustomTextView
    private lateinit var totalShops: AppCustomTextView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private var list: MutableList<UserLocationDataEntity> = ArrayList()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onDateSet(datePickerDialog: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        val date = "From " + dayOfMonth + AppUtils.getDayNumberSuffix(dayOfMonth) + " " + FTStorageUtils.formatMonth((++monthOfYear).toString()) + " " + year + " To " + dayOfMonthEnd + AppUtils.getDayNumberSuffix(dayOfMonthEnd) + " " + FTStorageUtils.formatMonth((++monthOfYearEnd).toString()) + " " + yearEnd
        dateRangeTv.visibility = View.VISIBLE
        dateRangeTv.text = date
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd
        if (dayOfMonth < 10)
            day = "0$dayOfMonth"
        if (dayOfMonthEnd < 10)
            dayEnd = "0$dayOfMonthEnd"
        var fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear /*+ 1*/).toString() + "") + "-" + year
        var endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd /*+ 1*/).toString() + "") + "-" + yearEnd
        getMISDetail("", AppUtils.changeLocalDateFormatToAtt(fronString), AppUtils.changeLocalDateFormatToAtt(endString), "")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_history_consolidated, container, false)
        initView(view)
        getMISDetail("", AppUtils.getStartDateOfCurrentWeek(), AppUtils.getCurrentDateForCons(), "")
        return view
    }

    private fun initAdapter(list: MutableList<ConsolidatedCount>) {
        consolidatedHistory.visibility = View.VISIBLE
        if (mConsolidatedAdapter == null) {
            mConsolidatedAdapter = ConsolidatedAdapter(mContext, list)
            layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
            consolidatedHistory.layoutManager = layoutManager
            consolidatedHistory.adapter = mConsolidatedAdapter
            consolidatedHistory.isNestedScrollingEnabled = false
        } else {
            mConsolidatedAdapter!!.updateList(list)
        }

    }

    private fun getMISDetail(currentMonthInNum: String, startDate: String, endDate: String, year: String) {

        try {
            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }
            if (BaseActivity.isApiInitiated)
                return
            BaseActivity.isApiInitiated = true
            val repository = GetMISRepositoryProvider.provideMISRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.getMISDetail(Pref.user_id!!, Pref.session_token!!, currentMonthInNum, startDate, endDate, year)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                var misResponse = result as MISResponse
                                if (misResponse.status == NetworkConstant.SUCCESS) {
                                    progress_wheel.stopSpinning()
                                    updateValue(misResponse.shop_list_count)
                                } else {
                                    progress_wheel.stopSpinning()
                                    updateNodataValue()
//                                consolidatedHistory.visibility=View.GONE
//                                (mContext as DashboardActivity).showSnackMessage(misResponse.message!!)
                                    //TODO SNACK MESSAGE

                                }
                                BaseActivity.isApiInitiated = false

                            }, { error ->
                                progress_wheel.stopSpinning()
                                BaseActivity.isApiInitiated = false
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                error.printStackTrace()
                                //TODO SNACK MESSAGE
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateNodataValue() {
        var shop_list_count = MISShopListCount()
        shop_list_count.total_attendance = "0"
        shop_list_count.total_new_shop_added = "0"
        shop_list_count.total_shop_visited = "0"
        shop_list_count.total_time_spent_at_shop = "0"
        updateValue(shop_list_count)
    }

    fun updateValue(shop_list_count: MISShopListCount?) {
        var list: MutableList<ConsolidatedCount> = ArrayList()
        var shopCount = ConsolidatedCount()
        shopCount.displayName = "Total Shop Visited"
        shopCount.displayValue = shop_list_count!!.total_shop_visited
        list.add(shopCount)

        var shopCount1 = ConsolidatedCount()
        shopCount1.displayName = "Total New Shop Added"
        shopCount1.displayValue = shop_list_count.total_new_shop_added
        list.add(shopCount1)

        var shopCount2 = ConsolidatedCount()
        shopCount2.displayName = "Total Attendance (days)"
        shopCount2.displayValue = shop_list_count.total_attendance
        list.add(shopCount2)

        var shopCount3 = ConsolidatedCount()
        shopCount3.displayName = "Total Duration Spent in the Shop (hh:mm)"
        shopCount3.displayValue = InfoWizard.getTotalShopVisitTimeInHH_MM(shop_list_count.total_time_spent_at_shop!!)
        list.add(shopCount3)

        var shopCount4 = ConsolidatedCount()
        shopCount4.displayName = "Average Shop Count"
        shopCount4.displayValue = InfoWizard.getAvgCountOfShopInMIS(shop_list_count.total_shop_visited!!, shop_list_count.total_attendance!!)
        list.add(shopCount4)

        var shopCount5 = ConsolidatedCount()
        shopCount5.displayName = "Average Duration Spent (hh:mm)"
        shopCount5.displayValue = InfoWizard.getAvgTimeOfShopInMIS(shop_list_count.total_time_spent_at_shop!!, shop_list_count.total_shop_visited!!)
        list.add(shopCount5)

        initAdapter(list)

    }

    private fun initView(view: View?) {
        progress_wheel = view!!.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        radioList = ArrayList()
        dateRangeTv = view.findViewById<AppCustomTextView>(R.id.date_range_display)

        this_week = view.findViewById<AppCompatRadioButton>(R.id.this_week)
        last_week = view.findViewById<AppCompatRadioButton>(R.id.last_week)
        this_month = view.findViewById<AppCompatRadioButton>(R.id.this_month)
        last_month = view.findViewById<AppCompatRadioButton>(R.id.last_month)
        date_range = view.findViewById<AppCompatRadioButton>(R.id.date_range)
        totalDistance = view.findViewById(R.id.total_distance)
        totalShops = view.findViewById(R.id.total_shop)


        radioList.add(this_week)
        radioList.add(last_week)
        radioList.add(this_month)
        radioList.add(last_month)
        radioList.add(date_range)

        /*radio_grp = view.findViewById(R.id.radio_grp)
        radio_grp1 = view.findViewById(R.id.radio_grp1)*/

        this_week.setOnClickListener(this)
        last_week.setOnClickListener(this)
        this_month.setOnClickListener(this)
        last_month.setOnClickListener(this)
        date_range.setOnClickListener(this)

        consolidatedHistory = view.findViewById(R.id.history_consolidated_RCV)

        totalDistance.text = getTotalDistanceCovered()
        totalShops.text = getString(R.string.total_shop) + ": " + getTotalShopsVisited()

    }

    private fun getTotalShopsVisited(): String {
        var totalStore: Int = 0
        var list = AppDatabase.getDBInstance()!!.userLocationDataDao().all
        list.size
        for (i in 0..list.size - 1) {
            totalStore = list[i].shops.toInt()
        }
        return (totalStore).toString()
    }

    private fun getTotalDistanceCovered(): String {
        var totalDistance: Double = 0.0
        var list = AppDatabase.getDBInstance()!!.userLocationDataDao().all
        for (i in 0..list.size - 1) {
            totalDistance = totalDistance + list[i].distance.toDouble()
        }
        return (totalDistance.toString())
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.date_range -> {
                if (!isChkChanged) {
                    /*for (item in radioList) {
                        if (item != v) {
                            item.isChecked = false
                        }
                        else
                            item.isChecked = true
                    }*/

                    date_range.isChecked = true
                    this_week.isChecked = false
                    last_week.isChecked = false
                    this_month.isChecked = false
                    last_month.isChecked = false

                    val now = Calendar.getInstance(Locale.ENGLISH)
                    val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                            this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.isAutoHighlight = mAutoHighlight
                    dpd.maxDate = Calendar.getInstance(Locale.ENGLISH)
                    dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
                } else {
                    isChkChanged = false
                }
            }

            R.id.this_week -> {
                if (!isChkChanged) {
                    /*for (item in radioList) {
                       if (item != v) {
                           item.isChecked = false
                       }
                       else
                           item.isChecked = true
                   }*/

                    date_range.isChecked = false
                    this_week.isChecked = true
                    last_week.isChecked = false
                    this_month.isChecked = false
                    last_month.isChecked = false

                    dateRangeTv.visibility = View.GONE
                    consolidatedHistory.visibility = View.VISIBLE
                    var d1 = AppUtils.getStartDateOfCurrentWeek()
                    var d2 = AppUtils.getEndDateOfCurrentWeek()
                    getMISDetail("", d1, AppUtils.getCurrentDateForCons(), "")
                } else {
                    isChkChanged = false
                }
            }
            R.id.last_week -> {
                if (!isChkChanged) {
                    /*for (item in radioList) {
                       if (item != v) {
                           item.isChecked = false
                       }
                       else
                           item.isChecked = true
                   }*/

                    date_range.isChecked = false
                    this_week.isChecked = false
                    last_week.isChecked = true
                    this_month.isChecked = false
                    last_month.isChecked = false

                    dateRangeTv.visibility = View.GONE
                    consolidatedHistory.visibility = View.GONE
                    var d3 = AppUtils.getStartDateOflastWeek()
                    var d4 = AppUtils.getEndDateOfLastWeek()
                    getMISDetail("", d3, d4, "")
                } else {
                    isChkChanged = false
                }
            }
            R.id.this_month -> {

                if (!isChkChanged) {
                    /*for (item in radioList) {
                       if (item != v) {
                           item.isChecked = false
                       }
                       else
                           item.isChecked = true
                   }*/

                    date_range.isChecked = false
                    this_week.isChecked = false
                    last_week.isChecked = false
                    this_month.isChecked = true
                    last_month.isChecked = false

                    dateRangeTv.visibility = View.GONE
                    consolidatedHistory.visibility = View.VISIBLE
                    getMISDetail(AppUtils.getCurrentMonthInNum(), "", "", AppUtils.getCurrentYear())
                } else {
                    isChkChanged = false
                }
            }
            R.id.last_month -> {
                if (!isChkChanged) {
                    /*for (item in radioList) {
                       if (item != v) {
                           item.isChecked = false
                       }
                       else
                           item.isChecked = true
                   }*/

                    date_range.isChecked = false
                    this_week.isChecked = false
                    last_week.isChecked = false
                    this_month.isChecked = false
                    last_month.isChecked = true

                    dateRangeTv.visibility = View.GONE
                    consolidatedHistory.visibility = View.GONE
                    getMISDetail("", AppUtils.getFirstDateOfLastMonth(), AppUtils.getEndDateOflastMonth(), "")
                } else {
                    isChkChanged = false
                }
            }
        }

    }
}


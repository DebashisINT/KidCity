package com.kcteam.features.addAttendence

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import com.kcteam.features.addAttendence.api.addattendenceapi.AddAttendenceRepoProvider
import com.kcteam.features.addAttendence.model.LeaveListDataModel
import com.kcteam.features.addAttendence.model.LeaveListResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 05-Aug-20.
 */
class LeaveListFragment : BaseFragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mContext: Context

    private lateinit var rv_achv_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_pick_date_range: AppCustomTextView
    private lateinit var fab: FloatingActionButton

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_leave_list, container, false)

        initView(view)
        var nextDT=AppUtils.getNextDateForShopActi()
        getLeaveList(nextDT, nextDT)
        //getLeaveList(AppUtils.getCurrentDateForShopActi(), AppUtils.getCurrentDateForShopActi())

        return view
    }

    private fun initView(view: View) {
        rv_achv_list = view.findViewById(R.id.rv_achv_list)
        rv_achv_list.layoutManager = LinearLayoutManager(mContext)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        tv_pick_date_range = view.findViewById(R.id.tv_pick_date_range)
        fab = view.findViewById(R.id.fab)


        tv_pick_date_range.apply {
            visibility = View.VISIBLE
            val tomorrowsDateLong = Calendar.getInstance(Locale.ENGLISH).timeInMillis + (1000 * 60 * 60 * 24)
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = tomorrowsDateLong

            text = AppUtils.getFormattedDate(cal.time) + " To " + AppUtils.getFormattedDate(cal.time)
        }

        tv_pick_date_range.setOnClickListener(this)
        fab.setOnClickListener(this)
    }

    private fun getLeaveList(fromDate: String, toDate: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = AddAttendenceRepoProvider.addAttendenceRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.leaveList(fromDate, toDate)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as LeaveListResponseModel
                            when (response.status) {
                                NetworkConstant.SUCCESS -> {
                                    progress_wheel.stopSpinning()
                                    initAdapter(response.leave_list)

                                }
                                NetworkConstant.SESSION_MISMATCH -> {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                    (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                    (mContext as DashboardActivity).finish()
                                }
                                NetworkConstant.NO_DATA -> {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    rv_achv_list.visibility = View.GONE
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                }
                                else -> {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    rv_achv_list.visibility = View.GONE
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            tv_no_data_available.visibility = View.VISIBLE
                            rv_achv_list.visibility = View.GONE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun initAdapter(leaveList: ArrayList<LeaveListDataModel>?) {
        tv_no_data_available.visibility = View.GONE
        rv_achv_list.visibility = View.VISIBLE
        rv_achv_list.adapter = LeaveAdapter(mContext, leaveList)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_pick_date_range -> {
                val now = Calendar.getInstance(Locale.ENGLISH)
                //now.add(Calendar.DAY_OF_MONTH, +1)
                val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                )
                dpd.isAutoHighlight = false
               /* val tomorrowsDateLong = Calendar.getInstance().timeInMillis + (1000 * 60 * 60 * 24)
                val cal = Calendar.getInstance()
                cal.timeInMillis = tomorrowsDateLong
                dpd.minDate = cal*/
                dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
            }

            R.id.fab -> {
                (mContext as DashboardActivity).loadFragment(FragType.ApplyLeaveFragment, true, "")
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
        if (TimeUnit.MILLISECONDS.toDays(diffInMillis) > 120) {
            (mContext as DashboardActivity).showSnackMessage("Leave list must be generated for 3 months")
            return
        }

        val date = day + AppUtils.getDayNumberSuffix(day.toInt()) + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + " " + year + " To " + dayEnd + AppUtils.getDayNumberSuffix(dayEnd.toInt()) + FTStorageUtils.formatMonth((++monthOfYearEnd).toString() + "") + " " + yearEnd
        tv_pick_date_range.text = date

        getLeaveList(AppUtils.convertFromRightToReverseFormat(fronString), AppUtils.convertFromRightToReverseFormat(endString))
    }

    fun updateItem() {
        getLeaveList(AppUtils.getCurrentDateForShopActi(), AppUtils.getCurrentDateForShopActi())
    }


}
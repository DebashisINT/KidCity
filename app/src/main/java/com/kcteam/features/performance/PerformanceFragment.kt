package com.kcteam.features.performance

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.PerformanceEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Saikat on 24-10-2018.
 */
class PerformanceFragment : BaseFragment(), View.OnClickListener {

    private lateinit var tv_date: AppCustomTextView
    private lateinit var tv_shop_visited: AppCustomTextView
    private lateinit var tv_total_duration: AppCustomTextView
    private lateinit var tv_gps_off_duration: AppCustomTextView
    private lateinit var tv_ideal_duration: AppCustomTextView
    private lateinit var pick_a_date_TV: AppCustomTextView
    private lateinit var mContext: Context
    private var myCalendar = Calendar.getInstance(Locale.ENGLISH)
    private lateinit var ll_performance: LinearLayout
    private lateinit var ll_performance_main: LinearLayout
    private lateinit var tv_total_distance: AppCustomTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_performance, container, false)
        initView(view)
        initClickListener()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(view: View) {
        tv_date = view.findViewById(R.id.tv_date)
        tv_shop_visited = view.findViewById(R.id.tv_shop_visited)
        tv_total_duration = view.findViewById(R.id.tv_total_duration)
        tv_gps_off_duration = view.findViewById(R.id.tv_gps_off_duration)
        tv_ideal_duration = view.findViewById(R.id.tv_ideal_duration)
        pick_a_date_TV = view.findViewById(R.id.pick_a_date_TV)
        pick_a_date_TV.text = AppUtils.getFormattedDate(myCalendar.time)
        ll_performance = view.findViewById(R.id.ll_performance)
        ll_performance_main = view.findViewById(R.id.ll_performance_main)
        ll_performance_main.setOnClickListener(null)
        tv_total_distance = view.findViewById(R.id.tv_total_distance)
    }

    override fun onResume() {
        super.onResume()
        setValue()
    }

    private fun setValue() {
        val performance = AppDatabase.getDBInstance()!!.performanceDao().getTodaysData(AppUtils.getFormattedDateString(myCalendar))
        if (performance != null) {
            ll_performance.visibility = View.VISIBLE
            tv_date.text = AppUtils.convertToCommonFormat(performance.date!!)

            val totalDistance = AppDatabase.getDBInstance()!!.userLocationDataDao().getTotalDistanceForADay(AppUtils.getFormattedDateString(myCalendar)).toString()

            if (!TextUtils.isEmpty(totalDistance)) {
                AppDatabase.getDBInstance()!!.performanceDao().updateTotalDistance(totalDistance, AppUtils.getFormattedDateString(myCalendar))
                val finalTotalDistance = DecimalFormat("##.##").format(totalDistance.toDouble())
                tv_total_distance.text = finalTotalDistance
            } else
                tv_total_distance.text = ""

            if (!TextUtils.isEmpty(performance.ideal_duration))
                tv_ideal_duration.text = AppUtils.getTimeInHourMinuteFormat(performance.ideal_duration?.toLong()!!)
            else
                tv_ideal_duration.text = ""

            if (!TextUtils.isEmpty(performance.gps_off_duration)) {
                tv_gps_off_duration.text = AppUtils.getTimeInHourMinuteFormat(performance.gps_off_duration?.toLong()!!)
                tv_gps_off_duration.setOnClickListener {
                    (mContext as DashboardActivity).loadFragment(FragType.GpsStatusFragment, true, performance.date!!)
                }
            } else
                tv_gps_off_duration.text = ""

            if (!TextUtils.isEmpty(performance.total_shop_visited) && performance.total_shop_visited != "0")
                tv_shop_visited.text = performance.total_shop_visited
            else
                tv_shop_visited.text = ""

            if (!TextUtils.isEmpty(performance.total_duration_spent) && performance.total_duration_spent != "0")
                tv_total_duration.text = AppUtils.convertMinuteToHoursMinFormat(performance.total_duration_spent?.toInt()!!)
            else
                tv_total_duration.text = ""

        } else {
            //ll_performance.visibility = View.GONE

            val totalDistance = AppDatabase.getDBInstance()!!.userLocationDataDao().getTotalDistanceForADay(AppUtils.getFormattedDateString(myCalendar)).toString()
            //AppDatabase.getDBInstance()!!.performanceDao().updateTotalDistance(totalDistance, AppUtils.getCurrentDateForShopActi())

            if (!TextUtils.isEmpty(totalDistance) && !totalDistance.equals("0.0", ignoreCase = true)) {
                val performanceEntity = PerformanceEntity()
                performanceEntity.date = AppUtils.getCurrentDateForShopActi()
                performanceEntity.total_distance = totalDistance
                AppDatabase.getDBInstance()!!.performanceDao().insert(performanceEntity)

                tv_date.text = AppUtils.convertToCommonFormat(AppUtils.getCurrentDateForShopActi())
                val finalTotalDistance = DecimalFormat("##.##").format(totalDistance.toDouble())
                tv_total_distance.text = finalTotalDistance
                tv_total_duration.text = ""
                tv_shop_visited.text = ""
                tv_gps_off_duration.text = ""
                tv_ideal_duration.text = ""
            } else
                ll_performance.visibility = View.GONE
        }
    }

    private fun initClickListener() {
        pick_a_date_TV.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.pick_a_date_TV -> {
                val datePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datePicker.show()
            }
        }
    }

    val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        pick_a_date_TV.text = AppUtils.getFormattedDate(myCalendar.time)

        setValue()
    }
}
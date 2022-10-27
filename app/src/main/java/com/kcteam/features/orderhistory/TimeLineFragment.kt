package com.kcteam.features.orderhistory

import android.app.DatePickerDialog
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
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.orderhistory.activitiesapi.LocationFetchRepositoryProvider
import com.kcteam.features.orderhistory.model.FetchLocationResponse
import com.kcteam.features.orderhistory.model.LocationData
import com.kcteam.widgets.AppCustomTextView

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class TimeLineFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var pickDate: AppCustomTextView
    private lateinit var dayWiseHistory: RecyclerView
    private lateinit var dayWiseAdapter: TimeLineAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var tv_total_distance: AppCustomTextView
    private lateinit var tv_share_logs: AppCustomTextView
    private lateinit var tv_sync_all: AppCustomTextView
    private lateinit var ll_visit_distance: LinearLayout
    private lateinit var tv_visit_distance: AppCustomTextView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel

    private var myCalendar = Calendar.getInstance()
    private var selectedDate = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_history_daywise, container, false)

        initView(view)
        updateLabel()

        selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)
        callFetchLocationApi()

        return view
    }

    private fun initView(view: View) {
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        pickDate = view.findViewById(R.id.pick_a_date_TV)
        dayWiseHistory = view.findViewById(R.id.history_daywise_RCV)
        tv_total_distance = view.findViewById(R.id.tv_total_distance)
        tv_share_logs = view.findViewById(R.id.tv_share_logs)
        tv_sync_all = view.findViewById(R.id.tv_sync_all)
        ll_visit_distance = view.findViewById(R.id.ll_visit_distance)
        tv_visit_distance = view.findViewById(R.id.tv_visit_distance)

        if (Pref.isAttendanceDistanceShow)
            ll_visit_distance.visibility = View.VISIBLE
        else
            ll_visit_distance.visibility = View.GONE

        pickDate.setOnClickListener(this)
        tv_share_logs.setOnClickListener(this)

        tv_sync_all.visibility = View.GONE
        tv_share_logs.visibility = View.GONE
    }

    private fun updateLabel() {
        pickDate.text = AppUtils.getFormattedDate(myCalendar.time)
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.pick_a_date_TV -> {
                val datePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.datePicker.maxDate = Calendar.getInstance().timeInMillis
                datePicker.show()
            }

            R.id.tv_share_logs -> {

            }
        }
    }

    val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)

        updateLabel()
        callFetchLocationApi()
    }

    private fun callFetchLocationApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = LocationFetchRepositoryProvider.provideLocationFetchRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.fetchLocationUpdate(selectedDate)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val shopList = result as FetchLocationResponse
                            if (shopList.status == NetworkConstant.SUCCESS)
                                convertToModelAndSave(shopList.location_details, shopList.visit_distance)
                            else {
                                dayWiseHistory.visibility = View.GONE
                                (mContext as DashboardActivity).showSnackMessage(shopList.message!!)
                            }

                            progress_wheel.stopSpinning()

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            dayWiseHistory.visibility = View.GONE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun convertToModelAndSave(location_details: List<LocationData>?, visitDistance: String) {
        if (location_details!!.isEmpty())
            return

        val locationList = ArrayList<LocationData>()

        for (i in location_details.indices) {
            val localData = LocationData()
            if (location_details[i].latitude == null)
                continue
            else
                localData.latitude = location_details[i].latitude!!

            if (location_details[i].longitude == null)
                continue
            else
                localData.longitude = location_details[i].longitude!!

            if (location_details[i].date == null)
                continue
            else {
                localData.date = location_details[i].date!!
            }
            if (location_details[i].last_update_time == null)
                continue
            else {
                localData.last_update_time = location_details[i].last_update_time
            }
            if (location_details[i].distance_covered == null)
                continue
            else
                localData.distance_covered = location_details[i].distance_covered!!

            if (location_details[i].shops_covered == null)
                continue
            else
                localData.shops_covered = location_details[i].shops_covered!!
            if (location_details[i].location_name == null)
                continue
            else
                localData.location_name = location_details[i].location_name!!

            if (location_details[i].date == null)
                continue
            else
                localData.date = AppUtils.getTimeStampFromDate(location_details[i].date!!)

            if (location_details[i].meeting_attended == null)
                continue
            else
                localData.meeting_attended = location_details[i].meeting_attended!!

            locationList.add(localData)
        }

        initAdapter(locationList, visitDistance)
    }

    private fun initAdapter(shopList: ArrayList<LocationData>, visitDistance: String) {
        dayWiseHistory.visibility = View.VISIBLE

        var totalDistance = 0.0
        shopList.forEach {
            totalDistance += it.distance_covered?.toDouble()!!
        }

        if (!TextUtils.isEmpty(visitDistance))
            tv_visit_distance.text = visitDistance + " Km(s)"

        val finalDistance = String.format("%.2f", totalDistance)
        tv_total_distance.text = "$finalDistance Km(s)"

        (mContext as DashboardActivity).activityLocationListNew = shopList

        dayWiseAdapter = TimeLineAdapter(mContext, shopList)
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        dayWiseHistory.layoutManager = layoutManager
        dayWiseHistory.adapter = dayWiseAdapter
        dayWiseHistory.isNestedScrollingEnabled = false

    }
}
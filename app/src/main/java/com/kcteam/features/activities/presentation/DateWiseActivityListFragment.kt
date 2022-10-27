package com.kcteam.features.activities.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.ActivityEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.activities.api.ActivityRepoProvider
import com.kcteam.features.activities.model.ActivityImage
import com.kcteam.features.activities.model.ActivityListResponseModel
import com.kcteam.features.activities.model.AddActivityInputModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class DateWiseActivityListFragment : BaseFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var mContext: Context

    private lateinit var tv_count_no: AppCustomTextView
    private lateinit var rv_activity_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_pick_date: AppCustomTextView
    private lateinit var rl_datewise_activity_main: RelativeLayout

    private var specifiedDate = ""
    private var specifiedToDate = ""

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_activity_list_datewise, container, false)

        initView(view)

        val list = AppDatabase.getDBInstance()?.activDao()?.getAll()
        if (list == null || list.isEmpty())
            getActivityList()
        else
            initAdapter()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            tv_pick_date = findViewById(R.id.tv_pick_date)
            tv_count_no = findViewById(R.id.tv_count_no)
            rv_activity_list = findViewById(R.id.rv_activity_list)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
            progress_wheel = findViewById(R.id.progress_wheel)
            rl_datewise_activity_main = findViewById(R.id.rl_datewise_activity_main)
        }

        progress_wheel.stopSpinning()
        rv_activity_list.layoutManager = LinearLayoutManager(mContext)

        tv_pick_date.text = AppUtils.getFormattedDate(myCalendar.time) + " To " + AppUtils.getFormattedDate(myCalendar.time)
        specifiedDate = AppUtils.getCurrentDateForShopActi()
        specifiedToDate = AppUtils.getCurrentDateForShopActi()

        tv_pick_date.setOnClickListener {
//            val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
//                    myCalendar.get(Calendar.MONTH),
//                    myCalendar.get(Calendar.DAY_OF_MONTH))
//            /*datePicker.datePicker.maxDate = Calendar.getInstance().timeInMillis
//            val cal = Calendar.getInstance()
//            cal.add(Calendar.DATE, -7)
//            datePicker.datePicker.minDate = cal.timeInMillis*/
//            datePicker.show()


            val now = Calendar.getInstance(Locale.ENGLISH)
            val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.isAutoHighlight = false
            dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
        }
        rl_datewise_activity_main.setOnClickListener(null)
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        tv_pick_date.text = AppUtils.getFormattedDate(myCalendar.time)
        specifiedDate = AppUtils.getFormattedDateForApi(myCalendar.time)

        initAdapter()
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
        tv_pick_date.text = date

        specifiedDate = AppUtils.convertFromRightToReverseFormat(fronString)
        specifiedToDate = AppUtils.convertFromRightToReverseFormat(endString)

        initAdapter()
    }

    private fun getActivityList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            tv_no_data_available.visibility = View.VISIBLE
            return
        }

        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.activityList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ActivityListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.activity_list != null && response.activity_list!!.isNotEmpty()) {

                                    doAsync {
                                        response.activity_list!!.forEach {
                                            val activity = ActivityEntity()
                                            AppDatabase.getDBInstance()?.activDao()?.insertAll(activity.apply {
                                                activity_id = it.id
                                                party_id = it.party_id
                                                date = it.date
                                                time = it.time
                                                name = it.name
                                                activity_dropdown_id = it.activity_id
                                                type_id = it.type_id
                                                product_id = it.product_id
                                                subject = it.subject
                                                details = it.details
                                                duration = it.duration
                                                priority_id = it.priority_id
                                                due_date = it.due_date
                                                due_time = it.due_time
                                                attachments = it.attachments
                                                image = it.image
                                                isUploaded = true
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            initAdapter()
                                        }
                                    }
                                }
                                else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    tv_no_data_available.visibility = View.VISIBLE
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                tv_no_data_available.visibility = View.VISIBLE
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            tv_no_data_available.visibility = View.VISIBLE
                        })
        )
    }

    private fun initAdapter() {
        val list = AppDatabase.getDBInstance()?.activDao()?.getDueDateRangeWise(specifiedDate, specifiedToDate)
        if (list != null && list.isNotEmpty()) {
            tv_no_data_available.visibility = View.GONE
            tv_count_no.text = "Total count: " + list.size
            rv_activity_list.visibility = View.VISIBLE

            rv_activity_list.adapter = DateWiseActivityAdapter(mContext, list as ArrayList<ActivityEntity>?, {
                syncActivity(it)
            }, {
                (mContext as DashboardActivity).loadFragment(FragType.EditActivityFragment, true, it)
            })
        }
        else {
            tv_count_no.text = "Total count: 0"
            tv_no_data_available.visibility = View.VISIBLE
            rv_activity_list.visibility = View.GONE
        }
    }

    private fun syncActivity(activity: ActivityEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val activityInput = AddActivityInputModel(Pref.session_token!!, Pref.user_id!!, activity.activity_id!!, activity.party_id!!,
                activity.date!!, activity.time!!, activity.name!!, activity.activity_dropdown_id!!, activity.type_id!!,
                activity.product_id!!, activity.subject!!, activity.details!!, activity.duration!!, activity.priority_id!!,
                activity.due_date!!, activity.due_time!!)

        XLog.d("==============Sync Activity Input Params (Datewise Activity List)====================")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("id=======> " + activity.activity_id)
        XLog.d("party_id=======> " + activity.party_id)
        XLog.d("details=======> " + activity.details)
        XLog.d("date=======> " + activity.date)
        XLog.d("time=======> " + activity.time)
        XLog.d("name=======> " + activity.name)
        XLog.d("time=======> " + activity.time)
        XLog.d("activity_id=======> " + activity.activity_dropdown_id)
        XLog.d("type_id=======> " + activity.type_id)
        XLog.d("product_id=======> " + activity.product_id)
        XLog.d("subject=======> " + activity.subject)
        XLog.d("details=======> " + activity.details)
        XLog.d("duration=======> " + activity.duration)
        XLog.d("priority_id=======> " + activity.priority_id)
        XLog.d("due_date=======> " + activity.due_date)
        XLog.d("due_time=======> " + activity.due_time)
        XLog.d("attachments=======> " + activity.attachments)
        XLog.d("image=======> " + activity.image)
        XLog.d("========================================================================")

        if (TextUtils.isEmpty(activity.attachments)) {
            val repository = ActivityRepoProvider.activityRepoProvider()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.addActivity(activityInput)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.activDao()?.updateIsUploaded(true, activity.activity_id!!)
                                    initAdapter()
                                }

                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
        else {

            val imgList = ArrayList<ActivityImage>()

            if (!TextUtils.isEmpty(activity.attachments)) {
                imgList.add(ActivityImage(activity.attachments!!, "attachment"))
            }

            if (!TextUtils.isEmpty(activity.image)) {
                imgList.add(ActivityImage(activity.image!!, "image"))
            }

            val repository = ActivityRepoProvider.activityImageRepoProvider()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.addActivityWithAttachment(activityInput, imgList, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.activDao()?.updateIsUploaded(true, activity.activity_id!!)
                                    initAdapter()
                                }

                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
    }

    fun updateList() {
        initAdapter()
    }
}
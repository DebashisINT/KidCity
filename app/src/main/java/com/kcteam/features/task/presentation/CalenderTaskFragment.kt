package com.kcteam.features.task.presentation

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.TaskEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomTextView
import java.util.*


class CalenderTaskFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var ll_calendar_main: LinearLayout
    private lateinit var calendarView: CalendarView
    private lateinit var rv_task_list: RecyclerView
    private lateinit var tv_no_task_available: AppCustomTextView

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_calender, container, false)

        initView(view)

        return view
    }

    private fun initView(view: View) {
        view.apply {
            ll_calendar_main = findViewById(R.id.ll_calendar_main)
            calendarView = findViewById(R.id.calendarView)
            rv_task_list = findViewById(R.id.rv_task_list)
            tv_no_task_available = findViewById(R.id.tv_no_task_available)
        }

        ll_calendar_main.setOnClickListener(null)
        rv_task_list.layoutManager = LinearLayoutManager(mContext)

        val calendarStart = Calendar.getInstance(Locale.ENGLISH)
        calendarStart[Calendar.YEAR] = Calendar.getInstance(Locale.ENGLISH)[Calendar.YEAR]
        calendarStart[Calendar.MONTH] = 0
        calendarStart[Calendar.DAY_OF_MONTH] = 1
        calendarView.minDate = calendarStart.timeInMillis

        val calendarEnd = Calendar.getInstance(Locale.ENGLISH)
        calendarEnd[Calendar.YEAR] = Calendar.getInstance(Locale.ENGLISH)[Calendar.YEAR]
        calendarEnd[Calendar.MONTH] = 11
        calendarEnd[Calendar.DAY_OF_MONTH] = 31
        calendarView.maxDate = calendarEnd.timeInMillis


        val list = AppDatabase.getDBInstance()?.taskDao()?.getTaskDateWise(AppUtils.getCurrentDateForShopActi())
        setAdapter(list)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            var day = ""
            day = if (dayOfMonth.toString().length == 1)
                "0$dayOfMonth"
            else
                dayOfMonth.toString()

            val date = year.toString() + "-" + (month + 1).toString() + "-" + day
            val list_ = AppDatabase.getDBInstance()?.taskDao()?.getTaskDateWise(date)
            setAdapter(list_)
        }
    }

    private fun setAdapter(list: List<TaskEntity>?) {
        if (list != null && list.isNotEmpty()) {
            rv_task_list.visibility = View.VISIBLE
            //tv_no_task_available.visibility = View.GONE
            rv_task_list.adapter = EventAdapter(mContext, list as ArrayList<TaskEntity>?)
        }
        else {
            rv_task_list.visibility = View.GONE
            /*tv_no_task_available.visibility = View.VISIBLE
            tv_no_task_available.text = ""*/
        }
    }
}
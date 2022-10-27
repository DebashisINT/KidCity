package com.kcteam.features.location

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomTextView
import java.util.*

/**
 * Created by Saikat on 07-01-2019.
 */
class LocationListFragment : BaseFragment()/*, View.OnClickListener*/ {

    private lateinit var mContext: Context
    private lateinit var rv_location_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var pick_a_date_TV: AppCustomTextView
    private var myCalendar = Calendar.getInstance(Locale.ENGLISH)
    //private var adapter: LocationListAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_location_list, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        rv_location_list = view.findViewById(R.id.rv_location_list)
        rv_location_list.layoutManager = LinearLayoutManager(mContext)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        pick_a_date_TV = view.findViewById(R.id.pick_a_date_TV)
        pick_a_date_TV.text = AppUtils.getFormattedDate(myCalendar.time)
        //pick_a_date_TV.setOnClickListener(this)

        /*val list = AppDatabase.getDBInstance()!!.locationDao().getAllValueDateWise(AppUtils.getFormattedDateString(myCalendar))

        if (list != null && list.isNotEmpty())
            initAdapter(list)
        else {
            rv_location_list.visibility = View.GONE
            tv_no_data_available.visibility = View.VISIBLE
        }*/
    }

    /*private fun initAdapter(list: List<LocationEntity>) {
        rv_location_list.visibility = View.VISIBLE
        tv_no_data_available.visibility = View.GONE

        adapter = LocationListAdapter(mContext, list)
        rv_location_list.adapter = adapter
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.pick_a_date_TV -> {
                val datePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.datePicker.maxDate = Calendar.getInstance().timeInMillis
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

        val list = AppDatabase.getDBInstance()!!.locationDao().getAllValueDateWise(AppUtils.getFormattedDateString(myCalendar))

        if (list != null && list.isNotEmpty())
            initAdapter(list)
        else {
            rv_location_list.visibility = View.GONE
            tv_no_data_available.visibility = View.VISIBLE
        }
    }*/
}
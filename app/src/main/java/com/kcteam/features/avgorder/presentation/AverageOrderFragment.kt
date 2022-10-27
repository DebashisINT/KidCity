package com.kcteam.features.avgorder.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.averageshop.presentation.AverageShopListClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import devs.mulham.horizontalcalendar.HorizontalCalendar
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener
import org.joda.time.DateTime
import java.util.*

/**
 * Created by Pratishruti on 15-11-2017.
 */
class AverageOrderFragment : BaseFragment(), DatePickerListener {
    private lateinit var horizontalCalendar: HorizontalCalendar
    private lateinit var averageShopListAdapter: AverageOrderListAdapter
    private lateinit var shopList: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var orderValue: AppCustomTextView
    private lateinit var avgShoplabel: AppCustomTextView
    private lateinit var noShopAvailable: AppCompatTextView
    private lateinit var list: List<AddShopDBModelEntity>
    private lateinit var no_of_shop_TV: AppCustomTextView
    private lateinit var total_shop_TV: AppCustomTextView
    private lateinit var picker: HorizontalPicker


    private lateinit var mContext: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_average_ordered_valued, container, false)
        initView(view)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(view: View) {

        /** end after 1 month from now */
        val endDate = Calendar.getInstance(Locale.ENGLISH)
        endDate.add(Calendar.MONTH, 1)

        /** start before 1 month from now */
        val startDate = Calendar.getInstance(Locale.ENGLISH)
        startDate.add(Calendar.MONTH, -1)







        horizontalCalendar = HorizontalCalendar.Builder(view, R.id.calendarView)
//                .startDate(startDate.time)
//                .endDate(endDate.time)
//                .datesNumberOnScreen(5)
//                .dayNameFormat("EEE")
//                .dayNumberFormat("dd")
//                .monthFormat("MMM")
//                .textSize(14f, 24f, 14f)
//                .showDayName(true)
//                .showMonthName(false)
////                .defaultSelectedDate(getMeYesterday())
//                .textColor(ContextCompat.getColor(mContext, R.color.login_txt_color), ContextCompat.getColor(mContext, R.color.date_selector_color))
                .build()
        horizontalCalendar.calendarListener = object : HorizontalCalendarListener() {
            override fun onDateSelected(date: Calendar?, position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                override fun onDateSelected(date: Date, position: Int) {
//                    getListFromDatabase(AppUtils.changeToCurrentDateFormat(date))
//                }
            }



        }
        /*NEW CALENDER*/
        picker = view.findViewById<HorizontalPicker>(R.id.datePicker)
        picker.setListener(this)
                .setDays(120)
                .setOffset(7)
                .setDateSelectedColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//box color
                .setDateSelectedTextColor(ContextCompat.getColor(mContext, R.color.white))
                .setMonthAndYearTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//month color
                .setTodayButtonTextColor(ContextCompat.getColor(mContext, R.color.date_selector_color))
                .setTodayDateTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setTodayDateBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent))//
                .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setDayOfWeekTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .showTodayButton(false)
                .init()
        picker.backgroundColor = Color.WHITE
        picker.setDate(DateTime())


        /*NEW CALENDER*/

        shopList = view.findViewById(R.id.shop_list_RCV)
        orderValue = view.findViewById(R.id.no_of_shop_TV)
        orderValue.text = mContext.getString(R.string.zero_order_in_value)
        avgShoplabel = view.findViewById(R.id.avg_shop_tv)
        avgShoplabel.text = mContext.getString(R.string.avg_order_on_each_shop)
        noShopAvailable = view.findViewById(R.id.no_shop_tv)
        noShopAvailable = view.findViewById(R.id.no_shop_tv)
        no_of_shop_TV = view.findViewById(R.id.no_of_shop_TV)
        no_of_shop_TV.text = "0"
        total_shop_TV = view.findViewById(R.id.total_shop_TV)
        total_shop_TV.text = "0"
    }

    override fun onDateSelected(dateSelected: DateTime) {
        var dateTime = dateSelected.toString()
        var dateFormat = dateTime.substring(0, dateTime.indexOf('T'))
        var convertedDate = AppUtils.convertToCommonFormat(dateFormat)
        list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsVisitedPerDay(convertedDate, true)
        initAdapter(list)
//        Toast.makeText(mContext,"Fecha seleccionada="+dateSelected.toString(),Toast.LENGTH_LONG).show()
    }

    private fun getMeYesterday(): Date {
        return Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
    }

    private fun getListFromDatabase(date: String) {
        list = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopsVisitedPerDay(date, true)
        if (list.isNotEmpty()) {
            noShopAvailable.visibility = View.GONE
            initAdapter(list)
        } else {
            noShopAvailable.visibility = View.VISIBLE
            shopList.visibility = View.GONE
        }
    }

    @SuppressLint("WrongConstant")
    private fun initAdapter(list: List<AddShopDBModelEntity>) {
//        getLocationList()
        averageShopListAdapter = AverageOrderListAdapter(mContext, list, object : AverageShopListClickListener {
            override fun onSyncClick(position: Int) {

            }

            override fun onQuestionnarieClick(shopId: String) {

            }

            override fun onReturnClick(position: Int) {

            }

            override fun onDamageClick(shop_id: String) {
                TODO("Not yet implemented")
            }


            override fun OnItemClick(position: Int) {
                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, list[position])
            }

            override fun OnMenuClick(position: Int, view: View) {
                initiatePopupWindow(view, position)
            }

            override fun onSurveyClick(shop_id: String) {

            }
        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        shopList.layoutManager = layoutManager
        shopList.adapter = averageShopListAdapter
    }


    private fun initiatePopupWindow(view: View, position: Int) {
        val popup = PopupWindow(context)
        val layout = layoutInflater.inflate(R.layout.popup_window_shop_item, null)

        popup.contentView = layout
        popup.isOutsideTouchable = true
        popup.isFocusable = true

        var call_ll: LinearLayout = layout.findViewById(R.id.call_ll)
        var direction_ll: LinearLayout = layout.findViewById(R.id.direction_ll)
        var add_order_ll: LinearLayout = layout.findViewById(R.id.add_order_ll)

        var call_iv: ImageView = layout.findViewById(R.id.call_iv)
        var call_tv: TextView = layout.findViewById(R.id.call_tv)
        var direction_iv: ImageView = layout.findViewById(R.id.direction_iv)
        var direction_tv: TextView = layout.findViewById(R.id.direction_tv)
        var order_iv: ImageView = layout.findViewById(R.id.order_iv)
        var order_tv: TextView = layout.findViewById(R.id.order_tv)


        call_ll.setOnClickListener(View.OnClickListener {
            call_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_call_select))

            order_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_add_order_deselect))
            direction_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_direction_deselect))
            order_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))
            direction_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))

            call_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            popup.dismiss()
            IntentActionable.initiatePhoneCall(mContext, list[position].ownerContactNumber)
        })

        direction_ll.setOnClickListener(View.OnClickListener {
            direction_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_direction_select))

            call_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_call_deselect))
            order_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_add_order_deselect))
            call_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))
            order_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))

            direction_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            popup.dismiss()
            (mContext as DashboardActivity).openLocationWithTrack()

        })

        add_order_ll.setOnClickListener(View.OnClickListener {
            order_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_add_order_select))

            call_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_call_deselect))
            direction_iv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_registered_shop_direction_deselect))
            call_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))
            direction_tv.setTextColor(ContextCompat.getColor(mContext, R.color.login_txt_color))

            order_tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
            popup.dismiss()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))

        })

        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(view)
        popup.update()

    }

}
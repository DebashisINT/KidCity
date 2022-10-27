package com.kcteam.features.report.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.averageshop.business.InfoWizard
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.report.api.GetMISRepositoryProvider
import com.kcteam.features.report.model.MISResponse
import com.kcteam.features.report.model.MISShopListCount
import com.kcteam.widgets.AppCustomTextView
import com.rackspira.kristiawan.rackmonthpicker.RackMonthPicker
import com.rackspira.kristiawan.rackmonthpicker.listener.DateMonthDialogListener
import com.rackspira.kristiawan.rackmonthpicker.listener.OnCancelMonthDialogListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Pratishruti on 07-11-2017.
 */
class ReportFragment : BaseFragment(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var reportList: RecyclerView
    private lateinit var adapter: ReportAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var mContext: Context
    private lateinit var nestedcsrollvire: NestedScrollView
    private lateinit var monthlyChkbox: AppCompatTextView
    private lateinit var selectDateRangeChkbox: AppCompatTextView
    private lateinit var selectedDate: AppCompatTextView
    private lateinit var avgTime: AppCustomTextView
    private lateinit var avgShop: AppCustomTextView
    private lateinit var avgOrder: AppCustomTextView
    private lateinit var shopsRL: RelativeLayout
    private lateinit var timeRL: RelativeLayout
    private lateinit var orderRL: RelativeLayout
    private lateinit var list: ArrayList<AddShopDBModelEntity>
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_report, container, false)
        addShopDBModel()
        initView(view)
        getMISDetail(AppUtils.getCurrentMonthInNum(), "", "")
        return view
    }


    private fun initView(view: View) {

        reportList = view.findViewById(R.id.report_RCV)
        nestedcsrollvire = view.findViewById(R.id.nestedcsrollvire_dashboard)
        monthlyChkbox = view.findViewById(R.id.monthly_chkbox_TV)
        selectDateRangeChkbox = view.findViewById(R.id.daterange_chkbox_TV)
        selectedDate = view.findViewById(R.id.date_select_TV)
        avgTime = view.findViewById(R.id.n_time_TV)
        avgShop = view.findViewById(R.id.n_shops_TV)
        avgOrder = view.findViewById(R.id.n_order_TV)
        shopsRL = view.findViewById(R.id.shops_RL)
        timeRL = view.findViewById(R.id.time_RL)
        orderRL = view.findViewById(R.id.order_value_RL)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        monthlyChkbox.isSelected = true
        selectedDate.text = getMonth(Calendar.getInstance(Locale.ENGLISH).get(Calendar.MONTH) + 1)

        monthlyChkbox.setOnClickListener(this)
        selectDateRangeChkbox.setOnClickListener(this)
        selectedDate.setOnClickListener(this)
        shopsRL.setOnClickListener(this)
        timeRL.setOnClickListener(this)
        orderRL.setOnClickListener(this)

        initAdapter()
    }

    private fun getMISDetail(currentMonthInNum: String, startDate: String, endDate: String) {
        if (!AppUtils.isOnline(mContext)){
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = GetMISRepositoryProvider.provideMISRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getMISDetail(Pref.user_id!!, Pref.session_token!!, currentMonthInNum, startDate, endDate, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            var misResponse = result as MISResponse
                            if (misResponse.status == NetworkConstant.SUCCESS) {
                                progress_wheel.stopSpinning()
                                updateValue(misResponse.shop_list_count)
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(misResponse.message!!)
                                //TODO SNACK MESSAGE
                                avgShop.text = "0"
                                avgTime.text = "0:0"
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            error.printStackTrace()
                            //TODO SNACK MESSAGE
                            avgShop.text = "0"
                            avgTime.text = "0:0"
                        })
        )
    }

    private fun updateValue(shop_list_count: MISShopListCount?) {
//        avgShop.text = InfoWizard.getAvergareShopVisitCount()
//        avgTime.text = InfoWizard.getAverageShopVisitTimeDuration()
        avgShop.text = InfoWizard.getAvgCountOfShopInMIS(shop_list_count!!.total_shop_visited!!, shop_list_count!!.total_attendance!!)
        avgTime.text = InfoWizard.getAvgTimeOfShopInMIS(shop_list_count!!.total_time_spent_at_shop!!, shop_list_count!!.total_shop_visited!!)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initAdapter() {
        adapter = ReportAdapter(mContext, list)
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        reportList.layoutManager = layoutManager
        reportList.adapter = adapter
        reportList.isNestedScrollingEnabled = false
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.monthly_chkbox_TV -> {
                monthlyChkbox.isSelected = true
                selectDateRangeChkbox.isSelected = false
                selectedDate.text = ""
                callMonthPicker()

            }
            R.id.daterange_chkbox_TV -> {
                monthlyChkbox.isSelected = false
                selectDateRangeChkbox.isSelected = true
                callDateRangePicker()
            }
            R.id.date_select_TV -> {
                if (monthlyChkbox.isSelected) {
                    callMonthPicker()
                } else {
                    callDateRangePicker()
                }
            }
            R.id.shops_RL -> {
                (mContext as DashboardActivity).loadFragment(FragType.AverageShopFragment, true, "")
            }
            R.id.time_RL -> {
                (mContext as DashboardActivity).loadFragment(FragType.AvgTimespentShopListFragment, true, "")
            }
            R.id.order_value_RL -> {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.functionality_disabled))
//                (mContext as DashboardActivity).loadFragment(FragType.AverageOrderFragment,true,"")
            }


        }

    }

    private fun callDateRangePicker() {
        val now = Calendar.getInstance(Locale.ENGLISH)
        val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.isAutoHighlight = true
        dpd.maxDate = Calendar.getInstance(Locale.ENGLISH)
        dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
    }

    override fun onDateSet(datePickerDialog: DatePickerDialog, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {

        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth
        if (dayOfMonthEnd < 10)
            dayEnd = "0" + dayOfMonthEnd
        var fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear + 1).toString() + "") + "-" + year
        var endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd + 1).toString() + "") + "-" + yearEnd
        if (AppUtils.getStrinTODate(endString).before(AppUtils.getStrinTODate(fronString))) {
            (mContext as DashboardActivity).showSnackMessage("Your end date is before start date.")
            return
        }
        val startMonth = getMonthAsThree((monthOfYear + 1))
        val endMonth = getMonthAsThree(monthOfYearEnd + 1)
        val dayStartSuffix = AppUtils.fetDateSuffix(dayOfMonth)
        val dayEndSuffix = AppUtils.fetDateSuffix(dayOfMonthEnd)
        val date = "$day$dayStartSuffix $startMonth $year  to  $dayEnd$dayEndSuffix $endMonth $yearEnd"
//        dateRangeTv.setVisibility(View.VISIBLE)
        selectedDate.text = date
        getMISDetail("", AppUtils.changeLocalDateFormatToAtt(fronString), AppUtils.changeLocalDateFormatToAtt(endString))
    }

    fun callMonthPicker() {
        RackMonthPicker(mContext)
                .setLocale(Locale.getDefault())
                .setColorTheme(R.color.colorPrimary)
                .setPositiveButton(DateMonthDialogListener { month, startDate, endDate, year, monthLabel ->
                    selectedDate.text = getMonth(month)
                    getMISDetail(month.toString(), "", "")

                })
                .setNegativeButton(object : OnCancelMonthDialogListener {
                    override fun onCancel(dialog: AlertDialog) {
                        dialog.dismiss()
                    }
                }).show()
    }

    fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month - 1]
    }

    fun getMonthAsThree(month: Int): String {
        val monthParse = SimpleDateFormat("MM")
        val monthDisplay = SimpleDateFormat("MMM")
        return monthDisplay.format(monthParse.parse(month.toString()))
    }

    fun addShopDBModel() {
        val mAddShopDBModelEntity1: AddShopDBModelEntity = AddShopDBModelEntity()
        list = ArrayList<AddShopDBModelEntity>()
        mAddShopDBModelEntity1.address = "SDF Module GP block Kol 700091"
        mAddShopDBModelEntity1.shopName = "The Tommy Hilfinger"
        mAddShopDBModelEntity1.orderValue = 200

        val mAddShopDBModelEntity2: AddShopDBModelEntity = AddShopDBModelEntity()

        mAddShopDBModelEntity2.address = "SDF Module GP block Kol 700091"
        mAddShopDBModelEntity2.shopName = "Addidus Store"
        mAddShopDBModelEntity2.orderValue = 300


        val mAddShopDBModelEntity3: AddShopDBModelEntity = AddShopDBModelEntity()

        mAddShopDBModelEntity3.address = "SDF Module GP block Kol 700091"
        mAddShopDBModelEntity3.shopName = "Turtle Outlet"
        mAddShopDBModelEntity3.orderValue = 400

        val mAddShopDBModelEntity4: AddShopDBModelEntity = AddShopDBModelEntity()

        mAddShopDBModelEntity4.address = "SDF Module GP block Kol 700091"
        mAddShopDBModelEntity4.shopName = "Levice International"
        mAddShopDBModelEntity4.orderValue = 500

        val mAddShopDBModelEntity5: AddShopDBModelEntity = AddShopDBModelEntity()

        mAddShopDBModelEntity5.address = "SDF Module GP block Kol 700091"
        mAddShopDBModelEntity5.shopName = "Image Kolkata Store"
        mAddShopDBModelEntity5.orderValue = 600


        list.add(mAddShopDBModelEntity1)
        list.add(mAddShopDBModelEntity2)
        list.add(mAddShopDBModelEntity3)
        list.add(mAddShopDBModelEntity4)
        list.add(mAddShopDBModelEntity5)


        Collections.sort(list, object : Comparator<AddShopDBModelEntity> {
            override fun compare(p0: AddShopDBModelEntity?, p1: AddShopDBModelEntity?): Int {
                return p0?.orderValue?.let { p1?.orderValue?.compareTo(it) }!!
            }
        })
    }


}
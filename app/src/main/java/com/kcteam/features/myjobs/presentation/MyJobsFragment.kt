package com.kcteam.features.myjobs.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.marketing.api.marketingresponse.GetMarketingDetailsRepoProvider
import com.kcteam.features.marketing.model.GetMarketingDetailsResponse
import com.kcteam.features.myjobs.api.MyJobRepoProvider
import com.kcteam.features.myjobs.model.CustomerDataModel
import com.kcteam.features.myjobs.model.CustomerListResponseModel
import com.kcteam.features.nearbyuserlist.model.NearbyUserDataModel
import com.kcteam.features.nearbyuserlist.presentation.NearbyUserListAdapter
import com.kcteam.features.stockCompetetorStock.CompetetorStockFragment
import com.kcteam.widgets.AppCustomTextView
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import java.lang.Exception


class MyJobsFragment: BaseFragment(), DatePickerListener {

    private lateinit var mContext: Context

    private lateinit var picker: HorizontalPicker
    private lateinit var rv_cust_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data_available: AppCustomTextView

    private lateinit var selectedDate: String

    companion object{
        var usr_id:String ? = ""
        fun setUserID(objects: Any):MyJobsFragment{
            val myJobsFragment = MyJobsFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                usr_id =objects.toString()
            }
            return myJobsFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext= context

        selectedDate = AppUtils.getCurrentDateForShopActi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_myjobs, container, false)

        initView(view)
        //getCustomerListApi()

        return  view
    }

    private fun initView(view: View) {
        picker = view.findViewById(R.id.datePicker)
        rv_cust_list = view.findViewById(R.id.rv_cust_list)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)

        progress_wheel.stopSpinning()
        rv_cust_list.layoutManager = LinearLayoutManager(mContext)

        picker.setListener(this)
                .setDays(8)
                .setOffset(4)
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
    }

    override fun onDateSelected(dateSelected: DateTime?) {
        val dateTime = dateSelected.toString()
        val dateFormat = dateTime.substring(0, dateTime.indexOf('T'))
        selectedDate = dateFormat

        getCustomerListApi()
    }


    fun getCustomerListApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        if(usr_id!!.length==0 || usr_id.equals("")){
            usr_id=Pref.user_id
        }

        progress_wheel.spin()
        val repository =  MyJobRepoProvider.jobRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getCustomerListDateWise(selectedDate,usr_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as CustomerListResponseModel
                            progress_wheel.stopSpinning()

                            when (response.status) {
                                NetworkConstant.SUCCESS -> initAdapter(response.job_list!!)
                                NetworkConstant.NO_DATA -> {
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    initAdapter(ArrayList<CustomerDataModel>())
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

    private fun initAdapter(customerList: ArrayList<CustomerDataModel>) {
        if (customerList.isNotEmpty())
            tv_no_data_available.visibility = View.GONE
        else
            tv_no_data_available.visibility = View.VISIBLE

        rv_cust_list.adapter = MyJobAdapter(mContext, customerList, {
            it.date = selectedDate
            (mContext as DashboardActivity).loadFragment(FragType.JobsCustomerFragment, true, it)
        }, {
            (mContext as DashboardActivity).openLocationMap(it, true)
        })
    }
}
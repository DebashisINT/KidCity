package com.kcteam.features.myjobs.presentation

import android.content.Context
import android.os.Bundle
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.SearchListener
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.myjobs.api.MyJobRepoProvider
import com.kcteam.features.myjobs.model.CustListResponseModel
import com.kcteam.features.myjobs.model.CustomerDataModel
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CustomerListFragment: BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_cust_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var date_CV: CardView
    private lateinit var tv_cust_no: AppCustomTextView

    private var customerList: ArrayList<CustomerDataModel>?= null
    private var adapter: CustomerListAdapter?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext= context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_myjobs, container, false)

        initView(view)
        getCustomerListApi()

        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    customerList?.let {
                        adapter?.refreshList(it)
                        tv_cust_no.text = "Total customer(s): " + it.size
                    }
                } else {
                    adapter?.filter?.filter(query)
                }
            }
        })

        return  view
    }

    private fun initView(view: View) {
        rv_cust_list = view.findViewById(R.id.rv_cust_list)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        date_CV = view.findViewById(R.id.date_CV)
        tv_cust_no = view.findViewById(R.id.tv_cust_no)

        date_CV.visibility = View.GONE
        tv_cust_no.visibility = View.VISIBLE
        progress_wheel.stopSpinning()

        rv_cust_list.layoutManager = LinearLayoutManager(mContext)
    }

    private fun getCustomerListApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository =  MyJobRepoProvider.jobRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getCustomerList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as CustListResponseModel
                            progress_wheel.stopSpinning()

                            when (response.status) {
                                NetworkConstant.SUCCESS -> {
                                    customerList = response.customer_list!!
                                    initAdapter()
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

    private fun initAdapter() {
        tv_no_data_available.visibility = View.GONE
        tv_cust_no.text = "Total customer(s): " + customerList?.size

        adapter = CustomerListAdapter(mContext, customerList!!, {
            tv_cust_no.text = "Total customer(s): " + it
        })

        rv_cust_list.adapter = adapter
    }
}
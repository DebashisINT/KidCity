package com.kcteam.features.know_your_state

import android.content.Context
import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.know_your_state.api.KnowStateListRepoProvider
import com.kcteam.features.know_your_state.model.KnowYourStateListDataModel
import com.kcteam.features.know_your_state.model.KnowYourStateListResponseModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Saikat on 27-11-2019.
 */
class KnowYourStateFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var rl_month_header: RelativeLayout
    private lateinit var tv_month: AppCustomTextView
    private lateinit var iv_month_dropdown: ImageView
    private lateinit var ll_month_list: LinearLayout
    private lateinit var et_month_search: AppCustomEditText
    private lateinit var rv_month_list: RecyclerView
    private lateinit var rl_year_header: RelativeLayout
    private lateinit var tv_year: AppCustomTextView
    private lateinit var iv_year_dropdown: ImageView
    private lateinit var ll_year_list: LinearLayout
    private lateinit var et_year_search: AppCustomEditText
    private lateinit var rv_year_list: RecyclerView
    private lateinit var rv_know_state_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_know_state_main: RelativeLayout
    private lateinit var scroll: NestedScrollView
    private lateinit var tv_no_data: AppCustomTextView

    private var yearAdapter: YearAdapter? = null
    private var monthAdapter: YearAdapter? = null
    private var monthVal = ""

    private var monthList = ArrayList<String>()
    private var yearList = ArrayList<String>()

    private var mKnowStateList: ArrayList<KnowYourStateListDataModel>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_know_your_state, container, false)

        initView(view)
        initClickListener()
        initTextChangeListener()

        return view
    }

    private fun initView(view: View) {
        rl_month_header = view.findViewById(R.id.rl_month_header)
        tv_month = view.findViewById(R.id.tv_month)
        iv_month_dropdown = view.findViewById(R.id.iv_month_dropdown)
        ll_month_list = view.findViewById(R.id.ll_month_list)
        et_month_search = view.findViewById(R.id.et_month_search)

        rv_month_list = view.findViewById(R.id.rv_month_list)
        rv_month_list.layoutManager = LinearLayoutManager(mContext)

        rl_year_header = view.findViewById(R.id.rl_year_header)
        tv_year = view.findViewById(R.id.tv_year)
        iv_year_dropdown = view.findViewById(R.id.iv_year_dropdown)
        ll_year_list = view.findViewById(R.id.ll_year_list)
        et_year_search = view.findViewById(R.id.et_year_search)

        rv_year_list = view.findViewById(R.id.rv_year_list)
        rv_year_list.layoutManager = LinearLayoutManager(mContext)

        rv_know_state_list = view.findViewById(R.id.rv_know_state_list)
        rv_know_state_list.layoutManager = LinearLayoutManager(mContext)
        //rv_know_state_list.adapter = KnowStateListAdapter(mContext, ArrayList<String>())

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        rl_know_state_main = view.findViewById(R.id.rl_know_state_main)
        scroll = view.findViewById(R.id.scroll)

        tv_no_data = view.findViewById(R.id.tv_no_data)

        monthList.add("January")
        monthList.add("February")
        monthList.add("March")
        monthList.add("April")
        monthList.add("May")
        monthList.add("June")
        monthList.add("July")
        monthList.add("August")
        monthList.add("September")
        monthList.add("October")
        monthList.add("November")
        monthList.add("December")

        yearList.add("2019")
        yearList.add("2020")
        yearList.add("2021")
        yearList.add("2022")
        yearList.add("2023")
        yearList.add("2024")
        yearList.add("2025")

        setMonthAdapter(monthList)
    }

    private fun initClickListener() {
        rl_know_state_main.setOnClickListener(null)
        rl_month_header.setOnClickListener(this)
        rl_year_header.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {

            R.id.rl_month_header -> {
                //if (!TextUtils.isEmpty(tv_month.text.toString().trim())) {
                if (iv_month_dropdown.isSelected) {
                    iv_month_dropdown.isSelected = false
                    ll_month_list.visibility = View.GONE

                    if ((mKnowStateList == null || mKnowStateList!!.size == 0) && (TextUtils.isEmpty(tv_month.text.toString().trim()) ||
                                    TextUtils.isEmpty(tv_year.text.toString().trim())))
                        tv_no_data.visibility = View.VISIBLE
                } else {
                    iv_month_dropdown.isSelected = true
                    ll_month_list.visibility = View.VISIBLE
                    tv_no_data.visibility = View.GONE
                }
                /*} else {
                    (mContext as DashboardActivity).showSnackMessage("Select Group first")
                }*/
            }


            R.id.rl_year_header -> {
                if (!TextUtils.isEmpty(tv_month.text.toString().trim())) {
                    if (iv_year_dropdown.isSelected) {
                        iv_year_dropdown.isSelected = false
                        ll_year_list.visibility = View.GONE

                        if ((mKnowStateList == null || mKnowStateList!!.size == 0) && (TextUtils.isEmpty(tv_month.text.toString().trim()) ||
                                        TextUtils.isEmpty(tv_year.text.toString().trim())))
                            tv_no_data.visibility = View.VISIBLE

                    } else {
                        iv_year_dropdown.isSelected = true
                        ll_year_list.visibility = View.VISIBLE
                        tv_no_data.visibility = View.GONE
                    }
                } else {
                    (mContext as DashboardActivity).showSnackMessage("Select Month first")
                }
            }
        }
    }

    private fun setYearAdapter(yearList: ArrayList<String>?) {
        rv_year_list.layoutManager = LinearLayoutManager(mContext)
        yearAdapter = YearAdapter(mContext, yearList, object : YearAdapter.OnItemClickListener {
            override fun onItemClick(year: String?, adapterPosition: Int) {
                tv_year.text = year
                ll_year_list.visibility = View.GONE
                iv_year_dropdown.isSelected = false

                scroll.visibility = View.GONE
                rv_know_state_list.visibility = View.VISIBLE

                callListApi()
            }
        })
        rv_year_list.adapter = yearAdapter
    }

    private fun setMonthAdapter(monthList: ArrayList<String>) {
        rv_month_list.layoutManager = LinearLayoutManager(mContext)

        monthAdapter = YearAdapter(mContext, monthList, object : YearAdapter.OnItemClickListener {
            override fun onItemClick(month: String?, adapterPosition: Int) {
                tv_month.text = month
                ll_month_list.visibility = View.GONE
                iv_month_dropdown.isSelected = false

                tv_year.text = ""
                et_year_search.setText("")

                monthVal = AppUtils.getFullMonthValue(month!!)

                rv_know_state_list.visibility = View.GONE
                scroll.visibility = View.VISIBLE
                //tv_no_data.visibility = View.VISIBLE

                setYearAdapter(yearList)

                if (!TextUtils.isEmpty(tv_year.text.toString().trim())) {
                    callListApi()
                }
            }
        })
        rv_month_list.adapter = monthAdapter
    }

    private fun initTextChangeListener() {
        et_year_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //if (!TextUtils.isEmpty(et_grp_search.text.toString().trim()) /*&& et_grp_search.text.toString().trim().length >= 2*/)
                yearAdapter?.filter?.filter(et_year_search.text.toString().trim())
            }
        })

        et_month_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //if (!TextUtils.isEmpty(et_category_search.text.toString().trim()) /*&& et_category_search.text.toString().trim().length >= 2*/)
                monthAdapter?.filter?.filter(et_month_search.text.toString().trim())
            }
        })
    }

    private fun callListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.e("=======INPUT FOR KNOW STATE LIST========")
        XLog.e("MONTH=====> $monthVal")
        XLog.e("YEAR======> " + tv_year.text.toString().trim())
        XLog.e("SESSION TOKEN======> " + Pref.session_token!!)
        XLog.e("USER ID======> " + Pref.user_id!!)
        XLog.e("=========================================")

        progress_wheel.spin()
        val repository = KnowStateListRepoProvider.knowStateListRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.knowStateList(monthVal, tv_year.text.toString().trim())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            progress_wheel.stopSpinning()

                            val response = result as KnowYourStateListResponseModel

                            XLog.e("RESPONSE CODE FOR KNOW STATE LIST=======> " + response.status)

                            if (response.status == "200") {

                                if (response.know_state_list != null && result.know_state_list!!.size > 0)
                                    initAdapter(response.know_state_list!!)
                                else {
                                    rv_know_state_list.visibility = View.GONE
                                    scroll.visibility = View.VISIBLE
                                    tv_no_data.visibility = View.VISIBLE
                                }

                            } else {
                                (mContext as DashboardActivity).showSnackMessage(result.message!!)

                                rv_know_state_list.visibility = View.GONE
                                scroll.visibility = View.VISIBLE
                                tv_no_data.visibility = View.VISIBLE
                            }


                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()

                            XLog.e("RESPONSE ERROR FOR KNOW STATE LIST=======> " + error.localizedMessage)

                            rv_know_state_list.visibility = View.GONE
                            scroll.visibility = View.VISIBLE
                            tv_no_data.visibility = View.VISIBLE

                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun initAdapter(knowStateList: ArrayList<KnowYourStateListDataModel>) {
        mKnowStateList = knowStateList
        rv_know_state_list.visibility = View.VISIBLE
        scroll.visibility = View.GONE
        tv_no_data.visibility = View.GONE

        rv_know_state_list.adapter = KnowStateListAdapter(mContext, knowStateList)
    }
}
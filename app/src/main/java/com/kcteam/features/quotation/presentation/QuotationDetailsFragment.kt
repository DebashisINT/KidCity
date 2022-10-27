package com.kcteam.features.quotation.presentation

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.QuotationEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import java.util.ArrayList

/**
 * Created by Saikat on 16-Jun-20.
 */
class QuotationDetailsFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var quot_no_EDT: AppCustomEditText
    private lateinit var date_EDT: AppCustomEditText
    private lateinit var hyp_EDT: AppCustomEditText
    private lateinit var account_EDT: AppCustomEditText
    private lateinit var model_EDT: AppCustomEditText
    private lateinit var bs_EDT: AppCustomEditText
    private lateinit var et_gearbox: AppCustomEditText
    private lateinit var et_nos1: AppCustomEditText
    private lateinit var et_value1: AppCustomEditText
    private lateinit var et_value2: AppCustomEditText
    private lateinit var et_tyres1: AppCustomEditText
    private lateinit var et_nos2: AppCustomEditText
    private lateinit var et_value3: AppCustomEditText
    private lateinit var et_value4: AppCustomEditText
    private lateinit var et_tyres2: AppCustomEditText
    private lateinit var amount_EDT: AppCustomEditText
    private lateinit var less_dis_EDT: AppCustomEditText
    private lateinit var cgst_EDT: AppCustomEditText
    private lateinit var sgst_EDT: AppCustomEditText
    private lateinit var tcs_EDT: AppCustomEditText
    private lateinit var insurance_EDT: AppCustomEditText
    private lateinit var net_amount_EDT: AppCustomEditText
    private lateinit var remakrs_EDT: AppCustomEditText
    private lateinit var tv_save_btn: AppCustomTextView
    private lateinit var rl_quot_details_main: RelativeLayout
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_cgst_percent: AppCustomTextView
    private lateinit var tv_sgst_percent: AppCustomTextView
    private lateinit var tv_tcs_percent: AppCustomTextView
    private lateinit var floating_fab: FloatingActionMenu

    private var programFab1: FloatingActionButton? = null

    private var quotId = ""
    private val preid: Int = 100

    private val getFloatingVal by lazy {
        ArrayList<String>()
    }

    companion object {

        fun getInstance(quo_id: Any?): QuotationDetailsFragment {
            val fragment = QuotationDetailsFragment()

            val bundle = Bundle()
            bundle.putString("quo_id", quo_id.toString())
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        if (!TextUtils.isEmpty(arguments?.getString("quo_id")))
            quotId = arguments?.getString("quo_id")!!
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_quot_details, container, false)

        initView(view)

        val quot = AppDatabase.getDBInstance()?.quotDao()?.getSingleQuotation(quotId)
        if (quot != null)
            setData(quot)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun initView(view: View) {
        view.apply {
            quot_no_EDT = findViewById(R.id.quot_no_EDT)
            date_EDT = findViewById(R.id.date_EDT)
            hyp_EDT = findViewById(R.id.hyp_EDT)
            account_EDT = findViewById(R.id.account_EDT)
            model_EDT = findViewById(R.id.model_EDT)
            bs_EDT = findViewById(R.id.bs_EDT)
            et_gearbox = findViewById(R.id.et_gearbox)
            et_nos1 = findViewById(R.id.et_nos1)
            et_value1 = findViewById(R.id.et_value1)
            et_value2 = findViewById(R.id.et_value2)
            et_tyres1 = findViewById(R.id.et_tyres1)
            et_nos2 = findViewById(R.id.et_nos2)
            et_value3 = findViewById(R.id.et_value3)
            et_value4 = findViewById(R.id.et_value4)
            et_tyres2 = findViewById(R.id.et_tyres2)
            amount_EDT = findViewById(R.id.amount_EDT)
            less_dis_EDT = findViewById(R.id.less_dis_EDT)
            cgst_EDT = findViewById(R.id.cgst_EDT)
            sgst_EDT = findViewById(R.id.sgst_EDT)
            tcs_EDT = findViewById(R.id.tcs_EDT)
            insurance_EDT = findViewById(R.id.insurance_EDT)
            net_amount_EDT = findViewById(R.id.net_amount_EDT)
            remakrs_EDT = findViewById(R.id.remakrs_EDT)
            tv_save_btn = findViewById(R.id.tv_save_btn)
            progress_wheel = findViewById(R.id.progress_wheel)
            tv_cgst_percent = findViewById(R.id.tv_cgst_percent)
            tv_sgst_percent = findViewById(R.id.tv_sgst_percent)
            tv_tcs_percent = findViewById(R.id.tv_tcs_percent)
            rl_quot_details_main = findViewById(R.id.rl_quot_details_main)
            floating_fab = findViewById(R.id.floating_fab)
        }

        progress_wheel.stopSpinning()

        tv_cgst_percent.text = Pref.cgstPercentage + "%"
        tv_sgst_percent.text = Pref.sgstPercentage + "%"
        tv_tcs_percent.text = Pref.tcsPercentage + "%"

        rl_quot_details_main.setOnClickListener(null)

        remakrs_EDT.showSoftInputOnFocus = false

        remakrs_EDT.setOnTouchListener(View.OnTouchListener { v, event ->
            if (remakrs_EDT.hasFocus()) {
                v?.parent?.requestDisallowInterceptTouchEvent(true)
                when (event?.action /*& MotionEvent.ACTION_MASK*/) {
                    MotionEvent.ACTION_SCROLL -> {
                        v?.parent?.requestDisallowInterceptTouchEvent(false)
                        return@OnTouchListener true
                    }
                }
            }
            false
        })

        floating_fab.apply {
            menuIconView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_add))
            menuButtonColorNormal = mContext.resources.getColor(R.color.colorAccent)
            menuButtonColorPressed = mContext.resources.getColor(R.color.colorPrimaryDark)
            menuButtonColorRipple = mContext.resources.getColor(R.color.colorPrimary)

            isIconAnimated = false
            setClosedOnTouchOutside(true)
        }

        getFloatingVal.add("Edit Quotation Details")

        getFloatingVal.forEachIndexed { i, value ->
            if (i == 0) {
                programFab1 = FloatingActionButton(activity)
                programFab1?.let {
                    it.buttonSize = FloatingActionButton.SIZE_MINI
                    it.id = preid + i
                    it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                    it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    it.labelText = getFloatingVal[0]
                    floating_fab.addMenuButton(it)
                    it.setOnClickListener(this)
                    it.setImageResource(R.drawable.ic_tick_float_icon)
                }
            }
        }
    }

    private fun setData(qout: QuotationEntity?) {
        qout?.apply {
            if (!TextUtils.isEmpty(quo_no))
                quot_no_EDT.setText(quo_no)

            if (!TextUtils.isEmpty(date))
                date_EDT.setText(AppUtils.changeAttendanceDateFormat(date!!))

            if (!TextUtils.isEmpty(hypothecation))
                hyp_EDT.setText(hypothecation)

            if (!TextUtils.isEmpty(account_no))
                account_EDT.setText(account_no)

            if (!TextUtils.isEmpty(model_id)) {
                val model = AppDatabase.getDBInstance()?.modelListDao()?.getSingleType(model_id!!)
                model_EDT.setText(model?.model_name)
            }

            if (!TextUtils.isEmpty(bs_id)) {
                val bs = AppDatabase.getDBInstance()?.bsListDao()?.getSingleType(bs_id!!)
                bs_EDT.setText(bs?.bs_name)
            }

            if (!TextUtils.isEmpty(gearbox))
                et_gearbox.setText(gearbox)

            if (!TextUtils.isEmpty(number1))
                et_nos1.setText(number1)

            if (!TextUtils.isEmpty(value1))
                et_value1.setText(value1)

            if (!TextUtils.isEmpty(value2))
                et_value2.setText(value2)

            if (!TextUtils.isEmpty(tyres1))
                et_tyres1.setText(tyres1)

            if (!TextUtils.isEmpty(number2))
                et_nos2.setText(number2)

            if (!TextUtils.isEmpty(value3))
                et_value3.setText(value3)

            if (!TextUtils.isEmpty(value4))
                et_value4.setText(value4)

            if (!TextUtils.isEmpty(tyres2))
                et_tyres2.setText(tyres2)

            if (!TextUtils.isEmpty(amount))
                amount_EDT.setText(String.format("%.2f", amount?.toFloat()))

            if (!TextUtils.isEmpty(discount))
                less_dis_EDT.setText(String.format("%.2f", discount?.toFloat()))

            if (!TextUtils.isEmpty(cgst))
                cgst_EDT.setText(String.format("%.2f", cgst?.toFloat()))

            if (!TextUtils.isEmpty(sgst))
                sgst_EDT.setText(String.format("%.2f", sgst?.toFloat()))

            if (!TextUtils.isEmpty(tcs))
                tcs_EDT.setText(String.format("%.2f", tcs?.toFloat()))

            if (!TextUtils.isEmpty(insurance))
                insurance_EDT.setText(String.format("%.2f", insurance?.toFloat()))

            if (!TextUtils.isEmpty(net_amount))
                net_amount_EDT.setText(String.format("%.2f", net_amount?.toFloat()))

            if (!TextUtils.isEmpty(remarks))
                remakrs_EDT.setText(remarks)

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            100 -> {
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab1?.setImageResource(R.drawable.ic_tick_float_icon)
                (mContext as DashboardActivity).onBackPressed()
                (mContext as DashboardActivity).loadFragment(FragType.EditQuotationFragment, true, quotId)
            }
        }
    }
}
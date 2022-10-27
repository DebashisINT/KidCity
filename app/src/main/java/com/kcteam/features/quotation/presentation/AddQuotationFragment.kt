package com.kcteam.features.quotation.presentation

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.BSListEntity
import com.kcteam.app.domain.ModelEntity
import com.kcteam.app.domain.QuotationEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.InputFilterDecimal
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.presentation.ModelListDialog
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.model.productlistmodel.ModelListResponse
import com.kcteam.features.nearbyshops.api.ShopListRepositoryProvider
import com.kcteam.features.nearbyshops.model.ModelListResponseModel
import com.kcteam.features.quotation.api.QuotationRepoProvider
import com.kcteam.features.quotation.model.AddQuotInputModel
import com.kcteam.features.quotation.model.BSListResponseModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by Saikat on 09-Jun-20.
 */
class AddQuotationFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var quot_no_EDT: AppCustomEditText
    private lateinit var date_EDT: AppCustomEditText
    private lateinit var hyp_EDT: AppCustomEditText
    private lateinit var account_EDT: AppCustomEditText
    private lateinit var tv_model: AppCustomTextView
    private lateinit var tv_bs: AppCustomTextView
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
    private lateinit var rl_model: RelativeLayout
    private lateinit var rl_bs: RelativeLayout
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_cgst_percent: AppCustomTextView
    private lateinit var tv_sgst_percent: AppCustomTextView
    private lateinit var tv_tcs_percent: AppCustomTextView
    private lateinit var rl_add_quot_main: RelativeLayout

    private var modelId = ""
    private var apiDate = ""
    private var bsId = ""
    private var shopId = ""

    private val myCalendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    companion object {

        fun getInstance(shopId: Any?): AddQuotationFragment {
            val fragment = AddQuotationFragment()

            val bundle = Bundle()
            bundle.putString("shop_id", shopId.toString())
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        if (!TextUtils.isEmpty(arguments?.getString("shop_id")))
            shopId = arguments?.getString("shop_id")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_quot, container, false)

        initView(view)
        initClickListener()
        initTextChangeListener()
        initTouchListener()

        return view
    }

    private fun initView(view: View) {

        view.apply {
            quot_no_EDT = findViewById(R.id.quot_no_EDT)
            date_EDT = findViewById(R.id.date_EDT)
            hyp_EDT = findViewById(R.id.hyp_EDT)
            account_EDT = findViewById(R.id.account_EDT)
            tv_model = findViewById(R.id.tv_model)
            tv_bs = findViewById(R.id.tv_bs)
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
            rl_model = findViewById(R.id.rl_model)
            rl_bs = findViewById(R.id.rl_bs)
            progress_wheel = findViewById(R.id.progress_wheel)
            tv_cgst_percent = findViewById(R.id.tv_cgst_percent)
            tv_sgst_percent = findViewById(R.id.tv_sgst_percent)
            tv_tcs_percent = findViewById(R.id.tv_tcs_percent)
            rl_add_quot_main = findViewById(R.id.rl_add_quot_main)
        }

        progress_wheel.stopSpinning()

        cgst_EDT.filters = arrayOf<InputFilter>(InputFilterDecimal(10, 2))
        sgst_EDT.filters = arrayOf<InputFilter>(InputFilterDecimal(10, 2))
        tcs_EDT.filters = arrayOf<InputFilter>(InputFilterDecimal(10, 2))
        insurance_EDT.filters = arrayOf<InputFilter>(InputFilterDecimal(10, 2))
        net_amount_EDT.filters = arrayOf<InputFilter>(InputFilterDecimal(10, 2))
        amount_EDT.filters = arrayOf<InputFilter>(InputFilterDecimal(10, 2))
        less_dis_EDT.filters = arrayOf<InputFilter>(InputFilterDecimal(10, 2))

        tv_cgst_percent.text = Pref.cgstPercentage + "%"
        tv_sgst_percent.text = Pref.sgstPercentage + "%"
        tv_tcs_percent.text = Pref.tcsPercentage + "%"
    }

    private fun initTouchListener() {
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

        amount_EDT.setOnTouchListener(View.OnTouchListener { v, event ->
            amount_EDT.requestFocus()
            cgst_EDT.clearFocus()
            sgst_EDT.clearFocus()
            tcs_EDT.clearFocus()
            less_dis_EDT.clearFocus()
            insurance_EDT.clearFocus()
            false
        })

        less_dis_EDT.setOnTouchListener(View.OnTouchListener { v, event ->
            less_dis_EDT.requestFocus()
            cgst_EDT.clearFocus()
            sgst_EDT.clearFocus()
            tcs_EDT.clearFocus()
            amount_EDT.clearFocus()
            insurance_EDT.clearFocus()
            false
        })

        cgst_EDT.setOnTouchListener(View.OnTouchListener { v, event ->
            cgst_EDT.requestFocus()
            less_dis_EDT.clearFocus()
            sgst_EDT.clearFocus()
            tcs_EDT.clearFocus()
            amount_EDT.clearFocus()
            insurance_EDT.clearFocus()
            false
        })

        sgst_EDT.setOnTouchListener(View.OnTouchListener { v, event ->
            sgst_EDT.requestFocus()
            less_dis_EDT.clearFocus()
            cgst_EDT.clearFocus()
            tcs_EDT.clearFocus()
            amount_EDT.clearFocus()
            insurance_EDT.clearFocus()
            false
        })

        tcs_EDT.setOnTouchListener(View.OnTouchListener { v, event ->
            tcs_EDT.requestFocus()
            less_dis_EDT.clearFocus()
            cgst_EDT.clearFocus()
            sgst_EDT.clearFocus()
            amount_EDT.clearFocus()
            insurance_EDT.clearFocus()
            false
        })

        insurance_EDT.setOnTouchListener(View.OnTouchListener { v, event ->
            insurance_EDT.requestFocus()
            less_dis_EDT.clearFocus()
            cgst_EDT.clearFocus()
            sgst_EDT.clearFocus()
            amount_EDT.clearFocus()
            tcs_EDT.clearFocus()
            false
        })
    }

    private fun initClickListener() {
        tv_save_btn.setOnClickListener(this)
        rl_bs.setOnClickListener(this)
        rl_model.setOnClickListener(this)
        date_EDT.setOnClickListener(this)
        rl_add_quot_main.setOnClickListener(null)
    }

    private fun initTextChangeListener() {
        amount_EDT.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (amount_EDT.hasFocus()) {
                    if (!TextUtils.isEmpty(less_dis_EDT.text.toString().trim())) {
                        cgst_EDT.setText(calculateCGST(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        sgst_EDT.setText(calculateSGST(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                    } else {
                        cgst_EDT.setText(calculateCGST(amount_EDT.text.toString().trim(), "0.00").toString())
                        sgst_EDT.setText(calculateSGST(amount_EDT.text.toString().trim(), "0.00").toString())
                        tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), "0.00").toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), "0.00").toString())
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                /*cgst_EDT.clearFocus()
                sgst_EDT.clearFocus()
                tcs_EDT.clearFocus()*/
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        less_dis_EDT.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (less_dis_EDT.hasFocus()) {
                    if (!TextUtils.isEmpty(amount_EDT.text.toString().trim())) {
                        cgst_EDT.setText(calculateCGST(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        sgst_EDT.setText(calculateSGST(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                    } else {
                        cgst_EDT.setText(calculateCGST("0.00", less_dis_EDT.text.toString().trim()).toString())
                        sgst_EDT.setText(calculateSGST("0.00", less_dis_EDT.text.toString().trim()).toString())
                        tcs_EDT.setText(calculateTCS("0.00", less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount("0.00", less_dis_EDT.text.toString().trim()).toString())
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                /*cgst_EDT.clearFocus()
                sgst_EDT.clearFocus()
                tcs_EDT.clearFocus()*/
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        cgst_EDT.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (cgst_EDT.hasFocus()) {
                    if (!TextUtils.isEmpty(amount_EDT.text.toString().trim()) && !TextUtils.isEmpty(less_dis_EDT.text.toString().trim())) {
                        tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                    } else if (!TextUtils.isEmpty(amount_EDT.text.toString().trim())) {
                        tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), "0.00").toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), "0.00").toString())
                    } else if (!TextUtils.isEmpty(less_dis_EDT.text.toString().trim())) {
                        tcs_EDT.setText(calculateTCS("0.00", less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount("0.00", less_dis_EDT.text.toString().trim()).toString())
                    } else {
                        tcs_EDT.setText(calculateTCS("0.00", "0.00").toString())
                        net_amount_EDT.setText(calculateNetAmount("0.00", "0.00").toString())
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                /*sgst_EDT.clearFocus()
                tcs_EDT.clearFocus()*/
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        sgst_EDT.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (sgst_EDT.hasFocus()) {
                    if (!TextUtils.isEmpty(amount_EDT.text.toString().trim()) && !TextUtils.isEmpty(less_dis_EDT.text.toString().trim())) {
                        tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                    } else if (!TextUtils.isEmpty(amount_EDT.text.toString().trim())) {
                        tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), "0.00").toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), "0.00").toString())
                    } else if (!TextUtils.isEmpty(less_dis_EDT.text.toString().trim())) {
                        tcs_EDT.setText(calculateTCS("0.00", less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount("0.00", less_dis_EDT.text.toString().trim()).toString())
                    } else {
                        tcs_EDT.setText(calculateTCS("0.00", "0.00").toString())
                        net_amount_EDT.setText(calculateNetAmount("0.00", "0.00").toString())
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                /*cgst_EDT.clearFocus()
                tcs_EDT.clearFocus()*/
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        tcs_EDT.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (tcs_EDT.hasFocus()) {
                    if (!TextUtils.isEmpty(amount_EDT.text.toString().trim()) && !TextUtils.isEmpty(less_dis_EDT.text.toString().trim())) {
                        //tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                    } else if (!TextUtils.isEmpty(amount_EDT.text.toString().trim())) {
                        //tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), "0.00").toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), "0.00").toString())
                    } else if (!TextUtils.isEmpty(less_dis_EDT.text.toString().trim())) {
                        //tcs_EDT.setText(calculateTCS("0.00", less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount("0.00", less_dis_EDT.text.toString().trim()).toString())
                    } else {
                        //tcs_EDT.setText(calculateTCS("0.00", "0.00").toString())
                        net_amount_EDT.setText(calculateNetAmount("0.00", "0.00").toString())
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                /*amount_EDT.clearFocus()
                sgst_EDT.clearFocus()*/
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        insurance_EDT.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (insurance_EDT.hasFocus()) {
                    if (!TextUtils.isEmpty(amount_EDT.text.toString().trim()) && !TextUtils.isEmpty(less_dis_EDT.text.toString().trim())) {
                        //tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), less_dis_EDT.text.toString().trim()).toString())
                    } else if (!TextUtils.isEmpty(amount_EDT.text.toString().trim())) {
                        //tcs_EDT.setText(calculateTCS(amount_EDT.text.toString().trim(), "0.00").toString())
                        net_amount_EDT.setText(calculateNetAmount(amount_EDT.text.toString().trim(), "0.00").toString())
                    } else if (!TextUtils.isEmpty(less_dis_EDT.text.toString().trim())) {
                        //tcs_EDT.setText(calculateTCS("0.00", less_dis_EDT.text.toString().trim()).toString())
                        net_amount_EDT.setText(calculateNetAmount("0.00", less_dis_EDT.text.toString().trim()).toString())
                    } else {
                        //tcs_EDT.setText(calculateTCS("0.00", "0.00").toString())
                        net_amount_EDT.setText(calculateNetAmount("0.00", "0.00").toString())
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun calculateCGST(amountString: String, discountString: String): Double {
        return try {

            var amount = 0.00
            var discount = 0.00

            if (!TextUtils.isEmpty(amountString))
                amount = amountString.toDouble()

            if (!TextUtils.isEmpty(discountString))
                discount = discountString.toDouble()

            val cgst = ((amount - discount) * Pref.cgstPercentage.toDouble()) / 100
            Log.e("Add Quot.", "CGST=====> " + cgst)

            val finalCGST = AppUtils.round(cgst, 2)
            Log.e("Add Quot.", "Final CGST=====> " + finalCGST)

            finalCGST
        } catch (e: Exception) {
            e.printStackTrace()
            0.00
        }
    }

    private fun calculateSGST(amountString: String, discountString: String): Double {
        return try {

            var amount = 0.00
            var discount = 0.00

            if (!TextUtils.isEmpty(amountString))
                amount = amountString.toDouble()

            if (!TextUtils.isEmpty(discountString))
                discount = discountString.toDouble()

            val sgst = ((amount - discount) * Pref.sgstPercentage.toDouble()) / 100
            Log.e("Add Quot.", "SGST=====> " + sgst)

            val finalSGST = AppUtils.round(sgst, 2)
            Log.e("Add Quot.", "Final SGST=====> " + finalSGST)

            finalSGST
        } catch (e: Exception) {
            e.printStackTrace()
            0.00
        }
    }

    private fun calculateTCS(amountString: String, discountString: String): Double {

        var cgstValue = "0.00"
        var sgstValue = "0.00"

        try {

            var amount = 0.00
            var discount = 0.00

            if (!TextUtils.isEmpty(amountString))
                amount = amountString.toDouble()

            if (!TextUtils.isEmpty(discountString))
                discount = discountString.toDouble()

            if (!TextUtils.isEmpty(cgst_EDT.text.toString().trim()))
                cgstValue = cgst_EDT.text.toString().trim()

            if (!TextUtils.isEmpty(sgst_EDT.text.toString().trim()))
                sgstValue = sgst_EDT.text.toString().trim()

            val tcs = (((amount - discount) + (cgstValue.toDouble() + sgstValue.toDouble())) *
                    Pref.tcsPercentage.toDouble()) / 100

            Log.e("Add Quot.", "TCS=====> " + tcs)

            val finalTCS = AppUtils.round(tcs, 2)
            Log.e("Add Quot.", "Final SGST=====> " + finalTCS)

            return finalTCS
        } catch (e: Exception) {
            e.printStackTrace()
            return 0.00
        }
    }

    private fun calculateNetAmount(amountString: String, discountString: String): Double {

        var cgstValue = "0.00"
        var sgstValue = "0.00"
        var tcsValue = "0.00"
        var insurance = "0.00"

        try {

            var amount = 0.00
            var discount = 0.00

            if (!TextUtils.isEmpty(amountString))
                amount = amountString.toDouble()

            if (!TextUtils.isEmpty(discountString))
                discount = discountString.toDouble()

            if (!TextUtils.isEmpty(cgst_EDT.text.toString().trim()))
                cgstValue = cgst_EDT.text.toString().trim()

            if (!TextUtils.isEmpty(sgst_EDT.text.toString().trim()))
                sgstValue = sgst_EDT.text.toString().trim()

            if (!TextUtils.isEmpty(tcs_EDT.text.toString().trim()))
                tcsValue = tcs_EDT.text.toString().trim()

            if (!TextUtils.isEmpty(insurance_EDT.text.toString().trim()))
                insurance = insurance_EDT.text.toString().trim()

            val netAmount = ((amount.toDouble() - discount.toDouble()) + (cgstValue.toDouble() + sgstValue.toDouble() +
                    tcsValue.toDouble() + insurance.toDouble()))

            Log.e("Add Quot.", "NET AMOUNT=====> " + netAmount)

            val finalNetAmount = AppUtils.round(netAmount, 2)
            Log.e("Add Quot.", "Final NET AMOUNT=====> " + finalNetAmount)

            return finalNetAmount
        } catch (e: Exception) {
            e.printStackTrace()
            return 0.00
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_save_btn -> {
                checkValidation()
            }

            R.id.rl_bs -> {
                val list = AppDatabase.getDBInstance()?.bsListDao()?.getAll() as ArrayList<BSListEntity>
                if (list == null || list.isEmpty())
                    getBsListApi()
                else
                    showBsDialog(list)
            }

            R.id.rl_model -> {
                val list = AppDatabase.getDBInstance()?.modelListDao()?.getAll() as ArrayList<ModelEntity>
                if (list == null || list.isEmpty())
                    getModelListApi()
                else
                    showModelDialog(list)
            }

            R.id.date_EDT -> {
                val aniDatePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                aniDatePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                aniDatePicker.show()
            }
        }
    }

    private fun checkValidation() {
        when {
            TextUtils.isEmpty(quot_no_EDT.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_quotation_no))
            TextUtils.isEmpty(date_EDT.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_date))
            TextUtils.isEmpty(modelId) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_model))
            TextUtils.isEmpty(amount_EDT.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.amount_error))
            else -> saveData()
        }
    }

    private fun saveData() {
        val quotEntity = QuotationEntity()
        AppDatabase.getDBInstance()?.quotDao()?.insert(quotEntity.apply {
            quo_id = Pref.user_id + "_quot_" + System.currentTimeMillis()
            quo_no = quot_no_EDT.text.toString().trim()

            date = apiDate

            hypothecation = if (!TextUtils.isEmpty(hyp_EDT.text.toString().trim()))
                hyp_EDT.text.toString().trim()
            else
                ""
            account_no = if (!TextUtils.isEmpty(account_EDT.text.toString().trim()))
                account_EDT.text.toString().trim()
            else
                ""

            model_id = modelId
            bs_id = bsId

            gearbox = if (!TextUtils.isEmpty(et_gearbox.text.toString().trim()))
                et_gearbox.text.toString().trim()
            else
                ""

            number1 = if (!TextUtils.isEmpty(et_nos1.text.toString().trim()))
                et_nos1.text.toString().trim()
            else
                ""

            value1 = if (!TextUtils.isEmpty(et_value1.text.toString().trim()))
                et_value1.text.toString().trim()
            else
                ""

            value2 = if (!TextUtils.isEmpty(et_value2.text.toString().trim()))
                et_value2.text.toString().trim()
            else
                ""

            tyres1 = if (!TextUtils.isEmpty(et_tyres1.text.toString().trim()))
                et_tyres1.text.toString().trim()
            else
                ""

            number2 = if (!TextUtils.isEmpty(et_nos2.text.toString().trim()))
                et_nos2.text.toString().trim()
            else
                ""

            value3 = if (!TextUtils.isEmpty(et_value3.text.toString().trim()))
                et_value3.text.toString().trim()
            else
                ""

            value4 = if (!TextUtils.isEmpty(et_value4.text.toString().trim()))
                et_value4.text.toString().trim()
            else
                ""

            tyres2 = if (!TextUtils.isEmpty(et_tyres2.text.toString().trim()))
                et_tyres2.text.toString().trim()
            else
                ""

            amount = if (!TextUtils.isEmpty(amount_EDT.text.toString().trim()))
                amount_EDT.text.toString().trim()
            else
                ""

            discount = if (!TextUtils.isEmpty(less_dis_EDT.text.toString().trim()))
                less_dis_EDT.text.toString().trim()
            else
                ""

            cgst = if (!TextUtils.isEmpty(cgst_EDT.text.toString().trim()))
                cgst_EDT.text.toString().trim()
            else
                ""

            sgst = if (!TextUtils.isEmpty(sgst_EDT.text.toString().trim()))
                sgst_EDT.text.toString().trim()
            else
                ""

            tcs = if (!TextUtils.isEmpty(tcs_EDT.text.toString().trim()))
                tcs_EDT.text.toString().trim()
            else
                ""

            insurance = if (!TextUtils.isEmpty(insurance_EDT.text.toString().trim()))
                insurance_EDT.text.toString().trim()
            else
                ""

            net_amount = if (!TextUtils.isEmpty(net_amount_EDT.text.toString().trim()))
                net_amount_EDT.text.toString().trim()
            else
                ""

            remarks = if (!TextUtils.isEmpty(remakrs_EDT.text.toString().trim()))
                remakrs_EDT.text.toString().trim()
            else
                ""

            shop_id = shopId
            isUploaded = false
        })

        if (AppUtils.isOnline(mContext))
            addQuotApi(quotEntity)
        else {
            (mContext as DashboardActivity).showSnackMessage("Quotation added successfully")
            (mContext as DashboardActivity).onBackPressed()
        }
    }

    private fun addQuotApi(quotEntity: QuotationEntity) {

        XLog.d("==============Add Quot. Input Params====================")
        XLog.d("shop id=======> " + quotEntity.shop_id)
        XLog.d("quot. date=======> " + quotEntity.date)
        XLog.d("quot. id=======> " + quotEntity.quo_id)
        XLog.d("quot. no=======> " + quotEntity.quo_no)
        XLog.d("hypothecation=======> " + quotEntity.hypothecation)
        XLog.d("account_no=======> " + quotEntity.account_no)
        XLog.d("model_id=======> " + quotEntity.model_id)
        XLog.d("bs_id=======> " + quotEntity.bs_id)
        XLog.d("gearbox=======> " + quotEntity.gearbox)
        XLog.d("number1=======> " + quotEntity.number1)
        XLog.d("value1=======> " + quotEntity.value1)
        XLog.d("value2=======> " + quotEntity.value2)
        XLog.d("tyres1=======> " + quotEntity.tyres1)
        XLog.d("number2=======> " + quotEntity.number2)
        XLog.d("value3=======> " + quotEntity.value3)
        XLog.d("value4=======> " + quotEntity.value4)
        XLog.d("tyres2=======> " + quotEntity.tyres2)
        XLog.d("amount=======> " + quotEntity.amount)
        XLog.d("discount=======> " + quotEntity.discount)
        XLog.d("cgst=======> " + quotEntity.cgst)
        XLog.d("sgst=======> " + quotEntity.sgst)
        XLog.d("tcs=======> " + quotEntity.tcs)
        XLog.d("insurance=======> " + quotEntity.insurance)
        XLog.d("net_amount=======> " + quotEntity.net_amount)
        XLog.d("remarks=======> " + quotEntity.remarks)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("============================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        val addQuot = AddQuotInputModel(Pref.session_token!!, Pref.user_id!!, quotEntity.shop_id!!, quotEntity.quo_id!!,
                quotEntity.quo_no!!, quotEntity.date!!, quotEntity.hypothecation!!, quotEntity.account_no!!, quotEntity.model_id!!,
                quotEntity.bs_id!!, quotEntity.gearbox!!, quotEntity.number1!!, quotEntity.value1!!, quotEntity.value2!!,
                quotEntity.tyres1!!, quotEntity.number2!!, quotEntity.value3!!, quotEntity.value4!!, quotEntity.tyres2!!, quotEntity.amount!!,
                quotEntity.discount!!, quotEntity.cgst!!, quotEntity.sgst!!, quotEntity.tcs!!, quotEntity.insurance!!, quotEntity.net_amount!!,
                quotEntity.remarks!!)

        BaseActivity.compositeDisposable.add(
                repository.addQuot(addQuot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("ADD QUOT. DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                doAsync {

                                    AppDatabase.getDBInstance()?.quotDao()?.updateIsUploaded(true, quotEntity.quo_id!!)

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                        (mContext as DashboardActivity).onBackPressed()
                                    }
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Quotation added successfully")
                                (mContext as DashboardActivity).onBackPressed()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("ADD QUOT. DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage("Quotation added successfully")
                            (mContext as DashboardActivity).onBackPressed()
                        })
        )

    }

    private fun getBsListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getBSList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BSListResponseModel
                            XLog.d("GET BS DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.bs_list != null && response.bs_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.bs_list?.forEach {
                                            val bsEntity = BSListEntity()
                                            AppDatabase.getDBInstance()?.bsListDao()?.insertAll(bsEntity.apply {
                                                bs_id = it.id
                                                bs_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            showBsDialog(AppDatabase.getDBInstance()?.bsListDao()?.getAll() as ArrayList<BSListEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET BS DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showBsDialog(bsList: ArrayList<BSListEntity>) {
        BSDialog.newInstance(bsList, { bs: BSListEntity ->
            tv_bs.text = bs.bs_name
            bsId = bs.bs_id!!
        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getModelListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = ShopListRepositoryProvider.provideShopListRepository()
        BaseActivity.compositeDisposable.add(
                //repository.getModelList()
                repository.getModelListNew()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            //val response = result as ModelListResponseModel
                            val response = result as ModelListResponse
                            XLog.d("GET MODEL DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.model_list != null && response.model_list!!.isNotEmpty()) {

                                    doAsync {

                                        AppDatabase.getDBInstance()?.modelListDao()?.insertAllLarge(response.model_list!!)

                                 /*       response.model_list?.forEach {
                                            val modelEntity = ModelEntity()
                                            AppDatabase.getDBInstance()?.modelListDao()?.insertAll(modelEntity.apply {
                                                model_id = it.id
                                                model_name = it.name
                                            })
                                        }*/

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            showModelDialog(AppDatabase.getDBInstance()?.modelListDao()?.getAll() as ArrayList<ModelEntity>)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }


                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET MODEL DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun showModelDialog(modelList: ArrayList<ModelEntity>) {
        ModelListDialog.newInstance(modelList, { model: ModelEntity ->
            tv_model.text = model.model_name
            modelId = model.model_id!!
        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        date_EDT.setText(AppUtils.changeAttendanceDateFormat(AppUtils.getDobFormattedDate(myCalendar.time)))
        apiDate = AppUtils.getFormattedDateForApi(myCalendar.time)
    }
}
package com.kcteam.features.dailyPlan.prsentation

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.InputFilter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.InputFilterDecimal
import com.kcteam.app.utils.Toaster
import com.kcteam.features.dailyPlan.model.GetPlanListDataModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import java.util.*

/**
 * Created by Saikat on 23-12-2019.
 */
class UpdatePlanDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var et_plan_value: AppCustomEditText
    private lateinit var et_plan_remark: AppCustomEditText
    private lateinit var tv_plan_update_btn: AppCustomTextView
    private lateinit var iv_close_icon: ImageView
    private lateinit var tv_plan_date: AppCustomTextView
    private var myCalendar = Calendar.getInstance(Locale.ENGLISH)

    private var plan: GetPlanListDataModel? = null

    companion object {

        private var listener: OnSubmitBtnClickListener? = null

        fun newInstance(planList: GetPlanListDataModel, mlistener: OnSubmitBtnClickListener): UpdatePlanDialog {
            val fragment = UpdatePlanDialog()

            listener = mlistener

            val bundle = Bundle()
            bundle.putSerializable("plan", planList)
            fragment.arguments = bundle

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        plan = arguments?.get("plan") as GetPlanListDataModel?
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_update_plan, container, false)

        isCancelable = false

        initView(v)
        initClickListener()

        return v
    }

    private fun initView(v: View) {
        et_plan_value = v.findViewById(R.id.et_plan_value)
        et_plan_remark = v.findViewById(R.id.et_plan_remark)
        tv_plan_update_btn = v.findViewById(R.id.tv_plan_update_btn)
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        tv_plan_date = v.findViewById(R.id.tv_plan_date)

        tv_plan_date.text = AppUtils.getBillFormattedDate(myCalendar.time)

        //et_plan_value.addTextChangedListener(CustomTextWatcher(et_plan_value, 8, 2))
        et_plan_value.filters = arrayOf<InputFilter>(InputFilterDecimal(8, 2))

        if (!AppUtils.isFromAttendance) {
            if (!TextUtils.isEmpty(plan?.last_plan_value))
                et_plan_value.setText(plan?.last_plan_value)

            if (!TextUtils.isEmpty(plan?.last_plan_feedback))
                et_plan_remark.setText(plan?.last_plan_feedback)

            if (!TextUtils.isEmpty(plan?.last_plan_date))
                tv_plan_date.text = AppUtils.convertToBillingFormat(plan?.last_plan_date!!)
        }
    }

    private fun initClickListener() {
        iv_close_icon.setOnClickListener(this)
        tv_plan_update_btn.setOnClickListener(this)

        if (AppUtils.isFromAttendance)
            tv_plan_date.setOnClickListener(null)
        else
            tv_plan_date.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_close_icon -> {
                dismiss()
            }

            R.id.tv_plan_update_btn -> {

                when {
                    TextUtils.isEmpty(et_plan_value.text.toString().trim()) -> Toaster.msgShort(mContext, getString(R.string.error_enter_primary_value))
                    et_plan_value.text.toString().trim() == "0" || et_plan_value.text.toString().trim() == "0.0" -> Toaster.msgShort(mContext, getString(R.string.error_enter_valid_primary_value))
                    else -> {
                        dismiss()
                        listener?.onSubmitClick(et_plan_value.text.toString().trim(), et_plan_remark.text.toString().trim(), tv_plan_date.text.toString().trim())
                    }
                }
            }

            R.id.tv_plan_date -> {
                val datePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))

                datePicker.datePicker.minDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis + (24 * 60 * 60 * 1000)
                datePicker.show()
            }
        }
    }

    val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        tv_plan_date.text = AppUtils.getBillFormattedDate(myCalendar.time)
    }

    interface OnSubmitBtnClickListener {
        fun onSubmitClick(planValue: String, planRemark: String, planDate: String)
    }
}
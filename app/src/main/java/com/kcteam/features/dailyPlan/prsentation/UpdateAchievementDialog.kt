package com.kcteam.features.dailyPlan.prsentation

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

/**
 * Created by Saikat on 23-12-2019.
 */
class UpdateAchievementDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var et_plan_value: AppCustomEditText
    private lateinit var et_achv_remark: AppCustomEditText
    private lateinit var iv_close_icon: ImageView
    private lateinit var et_achv_value: AppCustomEditText
    private lateinit var tv_achv_update_btn: AppCustomTextView
    private lateinit var tv_plan_date: AppCustomTextView

    private var plan: GetPlanListDataModel? = null

    companion object {

        private var listener: OnSubmitBtnClickListener? = null

        fun newInstance(planList: GetPlanListDataModel, mlistener: OnSubmitBtnClickListener): UpdateAchievementDialog {
            val fragment = UpdateAchievementDialog()
            listener = mlistener

            val bundle = Bundle()
            bundle.putSerializable("plan", planList)
            fragment.arguments = bundle

            return fragment
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        plan = arguments?.get("plan") as GetPlanListDataModel?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_update_achv, container, false)

        isCancelable = false

        initView(v)
        initClickListener()

        return v
    }

    private fun initView(v: View) {
        et_plan_value = v.findViewById(R.id.et_plan_value)
        et_achv_remark = v.findViewById(R.id.et_achv_remark)
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        et_achv_value = v.findViewById(R.id.et_achv_value)
        tv_achv_update_btn = v.findViewById(R.id.tv_achv_update_btn)
        tv_plan_date = v.findViewById(R.id.tv_plan_date)

        if (!TextUtils.isEmpty(plan?.last_plan_value))
            et_plan_value.setText(plan?.last_plan_value)

        if (!TextUtils.isEmpty(plan?.last_plan_date))
            tv_plan_date.text = AppUtils.convertToBillingFormat(plan?.last_plan_date!!)
        else
            tv_plan_date.text = AppUtils.convertToBillingFormat(AppUtils.getCurrentDateForShopActi())


        if (!AppUtils.isFromAttendance) {
            if (!TextUtils.isEmpty(plan?.last_achv_amount))
                et_achv_value.setText(plan?.last_achv_amount)

            if (!TextUtils.isEmpty(plan?.last_achv_feedback))
                et_achv_remark.setText(plan?.last_achv_feedback)
        }

        //et_achv_value.addTextChangedListener(CustomTextWatcher(et_achv_value, 8, 2))
        et_achv_value.filters = arrayOf<InputFilter>(InputFilterDecimal(8, 2))
    }

    private fun initClickListener() {
        iv_close_icon.setOnClickListener(this)
        tv_achv_update_btn.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_close_icon -> {
                dismiss()
            }

            R.id.tv_achv_update_btn -> {

                when {
                    TextUtils.isEmpty(et_achv_value.text.toString().trim()) -> Toaster.msgShort(mContext, getString(R.string.error_enter_achv_value))
                    et_achv_value.text.toString().trim() == "0" || et_achv_value.text.toString().trim() == "0.0" -> Toaster.msgShort(mContext, getString(R.string.error_enter_valid_achv_value))
                    else -> {
                        dismiss()
                        listener?.onSubmitClick(et_achv_value.text.toString().trim(), et_achv_remark.text.toString().trim())
                    }
                }
            }
        }
    }

    interface OnSubmitBtnClickListener {
        fun onSubmitClick(achvValue: String, achvRemark: String)
    }
}
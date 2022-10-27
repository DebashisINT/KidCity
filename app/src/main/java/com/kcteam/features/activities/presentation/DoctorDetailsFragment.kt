package com.kcteam.features.activities.presentation

import android.content.Context
import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.AddDoctorEntity
import com.kcteam.app.domain.AddDoctorProductListEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 09-01-2020.
 */
class DoctorDetailsFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_search_list: RecyclerView
    private lateinit var iv_yes: ImageView
    private lateinit var iv_no: ImageView
    private lateinit var iv_qty: ImageView
    private lateinit var iv_vol: ImageView
    private lateinit var ll_qty_product: LinearLayout
    private lateinit var rv_qty_search_list: RecyclerView
    private lateinit var til_qty_vol_txt: TextInputLayout
    private lateinit var et_qty_vol_txt: AppCustomEditText
    private lateinit var iv_sample_yes: ImageView
    private lateinit var iv_sample_no: ImageView
    private lateinit var ll_sample_product: LinearLayout
    private lateinit var rv_sample_search_list: RecyclerView
    private lateinit var iv_crm: ImageView
    private lateinit var iv_cme: ImageView
    private lateinit var ll_crm_cme_selection: LinearLayout
    private lateinit var iv_money: ImageView
    private lateinit var iv_kind: ImageView
    private lateinit var til_amount: TextInputLayout
    private lateinit var et_amount: AppCustomEditText
    private lateinit var til_what: TextInputLayout
    private lateinit var et_what: AppCustomEditText
    private lateinit var tv_from_date: AppCustomTextView
    private lateinit var tv_to_date: AppCustomTextView
    private lateinit var et_volume_crm_cme: AppCustomEditText
    private lateinit var iv_gift_yes: ImageView
    private lateinit var iv_gif_no: ImageView
    private lateinit var et_which_kind: AppCustomEditText
    private lateinit var tv_visit_date: AppCustomTextView
    private lateinit var et_remarks_mr: AppCustomEditText
    private lateinit var rl_doctor_details_main: RelativeLayout
    private lateinit var et_doc_remark: AppCustomEditText
    private lateinit var til_kind: TextInputLayout

    companion object {

        var doctorEntity: AddDoctorEntity? = null

        fun newInstance(objects: Any): DoctorDetailsFragment {
            val fragment = DoctorDetailsFragment()

            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is AddDoctorEntity) {
                    doctorEntity = objects
                }
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_doctor_details, container, false)

        initView(view)
        initAdapter()

        return view
    }

    private fun initView(view: View) {
        tv_visit_date = view.findViewById(R.id.tv_visit_date)
        et_remarks_mr = view.findViewById(R.id.et_remarks_mr)

        rv_search_list = view.findViewById(R.id.rv_search_list)
        rv_search_list.layoutManager = LinearLayoutManager(mContext)

        iv_yes = view.findViewById(R.id.iv_yes)
        iv_no = view.findViewById(R.id.iv_no)

        iv_qty = view.findViewById(R.id.iv_qty)
        iv_vol = view.findViewById(R.id.iv_vol)
        ll_qty_product = view.findViewById(R.id.ll_qty_product)

        rv_qty_search_list = view.findViewById(R.id.rv_qty_search_list)
        rv_qty_search_list.layoutManager = LinearLayoutManager(mContext)

        til_qty_vol_txt = view.findViewById(R.id.til_qty_vol_txt)
        et_qty_vol_txt = view.findViewById(R.id.et_qty_vol_txt)
        iv_sample_yes = view.findViewById(R.id.iv_sample_yes)
        iv_sample_no = view.findViewById(R.id.iv_sample_no)
        ll_sample_product = view.findViewById(R.id.ll_sample_product)

        rv_sample_search_list = view.findViewById(R.id.rv_sample_search_list)
        rv_sample_search_list.layoutManager = LinearLayoutManager(mContext)

        iv_crm = view.findViewById(R.id.iv_crm)
        iv_cme = view.findViewById(R.id.iv_cme)
        ll_crm_cme_selection = view.findViewById(R.id.ll_crm_cme_selection)
        iv_money = view.findViewById(R.id.iv_money)
        iv_kind = view.findViewById(R.id.iv_kind)
        til_amount = view.findViewById(R.id.til_amount)
        et_amount = view.findViewById(R.id.et_amount)
        til_what = view.findViewById(R.id.til_what)
        et_what = view.findViewById(R.id.et_what)
        tv_from_date = view.findViewById(R.id.tv_from_date)
        tv_to_date = view.findViewById(R.id.tv_to_date)
        et_volume_crm_cme = view.findViewById(R.id.et_volume_crm_cme)
        iv_gift_yes = view.findViewById(R.id.iv_gift_yes)
        iv_gif_no = view.findViewById(R.id.iv_gif_no)
        et_which_kind = view.findViewById(R.id.et_which_kind)
        et_doc_remark = view.findViewById(R.id.et_doc_remark)
        til_kind = view.findViewById(R.id.til_kind)

        rl_doctor_details_main = view.findViewById(R.id.rl_doctor_details_main)
        rl_doctor_details_main.setOnClickListener(null)

        if (!TextUtils.isEmpty(doctorEntity?.amount))
            et_amount.setText(doctorEntity?.amount)

        if (!TextUtils.isEmpty(doctorEntity?.doc_remark))
            et_doc_remark.setText(doctorEntity?.doc_remark)

        if (!TextUtils.isEmpty(doctorEntity?.qty_text))
            et_qty_vol_txt.setText(doctorEntity?.qty_text)

        if (!TextUtils.isEmpty(doctorEntity?.remarks_mr))
            et_remarks_mr.setText(doctorEntity?.remarks_mr)

        if (!TextUtils.isEmpty(doctorEntity?.volume))
            et_volume_crm_cme.setText(doctorEntity?.volume)

        if (!TextUtils.isEmpty(doctorEntity?.what))
            et_what.setText(doctorEntity?.what)

        if (!TextUtils.isEmpty(doctorEntity?.which_kind))
            et_which_kind.setText(doctorEntity?.which_kind)

        if (!TextUtils.isEmpty(doctorEntity?.visit_date))
            tv_visit_date.text = AppUtils.changeAttendanceDateFormat(doctorEntity?.visit_date!!)

        if (!TextUtils.isEmpty(doctorEntity?.crm_from_date))
            tv_from_date.text = AppUtils.changeAttendanceDateFormat(doctorEntity?.crm_from_date!!)

        if (!TextUtils.isEmpty(doctorEntity?.crm_to_date))
            tv_to_date.text = AppUtils.changeAttendanceDateFormat(doctorEntity?.crm_to_date!!)

        if (doctorEntity?.gift_status == 1) {
            iv_gift_yes.isSelected = true
            til_kind.visibility = View.VISIBLE
        } else if (doctorEntity?.gift_status == 0) {
            iv_gif_no.isSelected = true
            til_kind.visibility = View.GONE
        }

        if (doctorEntity?.money_status == 1) {
            iv_money.isSelected = true
            til_amount.visibility = View.VISIBLE
            til_what.visibility = View.GONE
        } else if (doctorEntity?.money_status == 0) {
            iv_kind.isSelected = true
            til_amount.visibility = View.GONE
            til_what.visibility = View.VISIBLE
        }

        if (doctorEntity?.crm_status == 1) {
            iv_crm.isSelected = true
            ll_crm_cme_selection.visibility = View.VISIBLE
        } else if (doctorEntity?.crm_status == 0) {
            iv_cme.isSelected = true
            ll_crm_cme_selection.visibility = View.VISIBLE
        }

        if (doctorEntity?.sample_status == 1)
            iv_sample_yes.isSelected = true
        else if (doctorEntity?.sample_status == 0)
            iv_sample_no.isSelected = true

        if (doctorEntity?.qty_status == 1) {
            iv_qty.isSelected = true
            til_qty_vol_txt.visibility = View.VISIBLE
        } else if (doctorEntity?.qty_status == 0) {
            iv_vol.isSelected = true
            til_qty_vol_txt.visibility = View.VISIBLE
        }

        if (doctorEntity?.prescribe_status == 1)
            iv_yes.isSelected = true
        else if (doctorEntity?.prescribe_status == 0)
            iv_no.isSelected = true


    }

    private fun initAdapter() {

        val productList = AppDatabase.getDBInstance()!!.addDocProductDao().getDataIdPodWise(doctorEntity?.doc_visit_id!!, 0) as ArrayList<AddDoctorProductListEntity>

        if (productList != null && productList.size > 0) {
            rv_search_list.visibility = View.VISIBLE
            rv_search_list.adapter = DoctorDetailsProductAdapter(mContext, productList)
        } else
            rv_search_list.visibility = View.GONE

        val qtyProductList = AppDatabase.getDBInstance()!!.addDocProductDao().getDataIdPodWise(doctorEntity?.doc_visit_id!!, 1) as ArrayList<AddDoctorProductListEntity>

        if (qtyProductList != null && qtyProductList.size > 0) {
            ll_qty_product.visibility = View.VISIBLE
            rv_qty_search_list.adapter = DoctorDetailsQtyProductAdapter(mContext, qtyProductList)
        } else
            ll_qty_product.visibility = View.GONE

        val sampleProductList = AppDatabase.getDBInstance()!!.addDocProductDao().getDataIdPodWise(doctorEntity?.doc_visit_id!!, 2) as ArrayList<AddDoctorProductListEntity>

        if (sampleProductList != null && sampleProductList.size > 0) {
            ll_sample_product.visibility = View.VISIBLE
            rv_sample_search_list.adapter = DoctorDetailsSampleProductAdapter(mContext, sampleProductList)
        } else
            ll_sample_product.visibility = View.GONE
    }
}
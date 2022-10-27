package com.kcteam.features.activities.presentation

import android.content.Context
import android.os.Bundle
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
import com.kcteam.app.domain.AddChemistEntity
import com.kcteam.app.domain.AddChemistProductListEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 08-01-2020.
 */
class ChemistDetailsFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_search_list: RecyclerView
    private lateinit var iv_yes: ImageView
    private lateinit var iv_no: ImageView
    private lateinit var ll_pob_product: LinearLayout
    private lateinit var rv_pob_search_list: RecyclerView
    private lateinit var et_volume: AppCustomEditText
    private lateinit var et_remarks: AppCustomEditText
    private lateinit var tv_visit_date: AppCustomTextView
    private lateinit var et_remarks_mr: AppCustomEditText
    private lateinit var rl_chemist_details_main: RelativeLayout

    companion object {

        var mAddShopDataObj: AddChemistEntity? = null

        fun newInstance(objects: Any): ChemistDetailsFragment {
            val fragment = ChemistDetailsFragment()

            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is AddChemistEntity) {
                    mAddShopDataObj = objects
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
        val view = inflater.inflate(R.layout.fragment_chemist_details, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        rv_search_list = view.findViewById(R.id.rv_search_list)
        rv_search_list.layoutManager = LinearLayoutManager(mContext)

        iv_yes = view.findViewById(R.id.iv_yes)
        iv_no = view.findViewById(R.id.iv_no)
        ll_pob_product = view.findViewById(R.id.ll_pob_product)

        rv_pob_search_list = view.findViewById(R.id.rv_pob_search_list)
        rv_pob_search_list.layoutManager = LinearLayoutManager(mContext)

        et_volume = view.findViewById(R.id.et_volume)
        et_remarks = view.findViewById(R.id.et_remarks)
        tv_visit_date = view.findViewById(R.id.tv_visit_date)
        et_remarks_mr = view.findViewById(R.id.et_remarks_mr)

        rl_chemist_details_main = view.findViewById(R.id.rl_chemist_details_main)
        rl_chemist_details_main.setOnClickListener(null)

        if (mAddShopDataObj?.pob == 1) {
            iv_yes.isSelected = true
            ll_pob_product.visibility = View.VISIBLE
        } else if (mAddShopDataObj?.pob == 0)
            iv_no.isSelected = true

        if (!TextUtils.isEmpty(mAddShopDataObj?.remarks))
            et_remarks.setText(mAddShopDataObj?.remarks)

        if (!TextUtils.isEmpty(mAddShopDataObj?.remarks_mr))
            et_remarks_mr.setText(mAddShopDataObj?.remarks_mr)

        if (!TextUtils.isEmpty(mAddShopDataObj?.volume))
            et_volume.setText(mAddShopDataObj?.volume)

        if (!TextUtils.isEmpty(mAddShopDataObj?.visit_date))
            tv_visit_date.text = AppUtils.changeAttendanceDateFormat(mAddShopDataObj?.visit_date!!)

        val podList = AppDatabase.getDBInstance()!!.addChemistProductDao().getDataIdPodWise(mAddShopDataObj?.chemist_visit_id!!, true) as ArrayList<AddChemistProductListEntity>

        if (podList != null && podList.isNotEmpty()) {
            rv_pob_search_list.adapter = ChemistDetailsPodProductAdapter(mContext, podList)
        } else
            ll_pob_product.visibility = View.GONE

        val list = AppDatabase.getDBInstance()!!.addChemistProductDao().getDataIdPodWise(mAddShopDataObj?.chemist_visit_id!!, false) as ArrayList<AddChemistProductListEntity>

        if (list != null && list.isNotEmpty()) {
            rv_search_list.visibility = View.VISIBLE
            rv_search_list.adapter = ChemistDetailsProductAdapter(mContext, list)
        } else
            rv_search_list.visibility = View.GONE
    }

}
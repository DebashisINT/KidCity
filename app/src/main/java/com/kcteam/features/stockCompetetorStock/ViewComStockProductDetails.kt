package com.kcteam.features.stockCompetetorStock

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.stockAddCurrentStock.ViewStockDetailsFragment
import com.kcteam.features.stockCompetetorStock.adapter.AdapterComStockProduct

class ViewComStockProductDetails: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var rvList: RecyclerView


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object{
        var stockID: String = ""
        fun getInstance(objects: Any): ViewComStockProductDetails {
            val viewStockFragment = ViewComStockProductDetails()
            if (!TextUtils.isEmpty(objects.toString())) {
                stockID=objects.toString()
            }
            return viewStockFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_view_com_stock_product_details, container, false)
        initView(view)
        return view
    }

    private fun initView(view:View){
        rvList=view?.findViewById(R.id.rv_view_competetor_stock_list)
        rvList.layoutManager=LinearLayoutManager(mContext)
    }

    override fun onResume() {
        super.onResume()
        getStockDetails()
    }

    private fun getStockDetails(){
        var stockList= AppDatabase.getDBInstance()?.competetorStockEntryProductDao()?.getComProductStockAllByStockID(stockID)
        rvList.adapter= AdapterComStockProduct(mContext, stockList!!)
    }

    override fun onClick(p0: View?) {

    }
}
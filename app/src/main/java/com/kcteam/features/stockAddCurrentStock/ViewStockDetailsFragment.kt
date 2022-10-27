package com.kcteam.features.stockAddCurrentStock

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
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.stockAddCurrentStock.adapter.AdapterStockProductDetails

class ViewStockDetailsFragment: BaseFragment(), View.OnClickListener  {

    private lateinit var mContext: Context
    private lateinit var mRv_productQty: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object{
        var stockID: String = ""
        fun getInstance(objects: Any): ViewStockDetailsFragment {
            val viewStockFragment = ViewStockDetailsFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                stockID=objects.toString()
            }
            return viewStockFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_view_stock_details, container, false)
        initView(view)
        return view
    }

    private fun initView(view:View){
        mRv_productQty=view!!.findViewById(R.id.rv_view_current_stock_list)
        mRv_productQty.layoutManager=LinearLayoutManager(mContext)
    }

    override fun onResume() {
        super.onResume()
        getStockDetails()

    }

    private fun getStockDetails(){
        var list = AppDatabase.getDBInstance()?.shopCurrentStockProductsEntryDao()!!.getShopProductsStockAllByStockID(stockID)
        mRv_productQty.adapter= AdapterStockProductDetails(mContext,list)

    }

    override fun onClick(p0: View?) {

    }
}
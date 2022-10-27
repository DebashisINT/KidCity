package com.kcteam.features.stockAddCurrentStock.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.CurrentStockEntryModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.stockAddCurrentStock.UpdateShopStockFragment
import com.kcteam.features.stockAddCurrentStock.`interface`.ShowStockOnClick
import kotlinx.android.synthetic.main.row_show_stock_list.view.*
import kotlinx.android.synthetic.main.row_show_stock_list.view.stock_date_tv
import kotlinx.android.synthetic.main.row_show_stock_list.view.stock_qty_tv
import kotlinx.android.synthetic.main.row_show_stock_list.view.sync_status_iv
import kotlinx.android.synthetic.main.row_show_stock_list.view.tv_stock_view
import kotlinx.android.synthetic.main.row_view_competetor_stock_list.view.*

class AdapterShowStockList(val context: Context, val stockList: List<CurrentStockEntryModelEntity>,val listner: ShowStockOnClick): RecyclerView.Adapter<AdapterShowStockList.ShowStockViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowStockViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_show_stock_list,parent,false)
        return ShowStockViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stockList!!.size
    }

    override fun onBindViewHolder(holder: ShowStockViewHolder, position: Int) {
        holder.tv_stock_date.text=stockList!!.get(position).visited_date
        holder.tv_stock_qty.text=stockList!!.get(position).total_product_stock_qty
        if(stockList.get(position).isUploaded!!){
            holder.iv_sync.setImageResource(R.drawable.ic_registered_shop_sync)
        }else{
            holder.iv_sync.setImageResource(R.drawable.ic_registered_shop_not_sync)
        }
        holder.tv_stock_view.setOnClickListener { listner.stockListOnClick(stockList!!.get(position).stock_id!!) }
    }

    inner class ShowStockViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_stock_date = itemView.stock_date_tv
        val tv_stock_qty = itemView.stock_qty_tv
        val tv_stock_view = itemView.tv_stock_view
        val iv_sync=itemView.sync_status_iv
    }

}
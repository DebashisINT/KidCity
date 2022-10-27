package com.kcteam.features.stockCompetetorStock.adapter

import android.content.Context
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.CcompetetorStockEntryModelEntity
import com.kcteam.app.domain.CurrentStockEntryModelEntity
import com.kcteam.features.stockAddCurrentStock.`interface`.ShowStockOnClick
import com.kcteam.features.stockCompetetorStock.`interface`.CompetetorStockOnClick
import kotlinx.android.synthetic.main.row_view_competetor_stock_list.view.*

class AdapterCompetetorStock(val context: Context, val stockList: List<CcompetetorStockEntryModelEntity>, val listner: CompetetorStockOnClick):
        RecyclerView.Adapter<AdapterCompetetorStock.CompetetorStockViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompetetorStockViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view_competetor_stock_list,parent,false)
        return CompetetorStockViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stockList?.size
    }

    override fun onBindViewHolder(holder: CompetetorStockViewHolder, position: Int) {
        holder.tv_date.text=stockList.get(position).visited_date
        holder.tv_qty.text=stockList.get(position).total_product_stock_qty
        if(stockList.get(position).isUploaded!!){
            holder.iv_sync.setImageResource(R.drawable.ic_registered_shop_sync)
        }else{
            holder.iv_sync.setImageResource(R.drawable.ic_registered_shop_not_sync)
        }
        holder.iv_date.setOnClickListener{listner.stockListOnClickView(stockList.get(holder.adapterPosition).competitor_stock_id.toString())}
    }

    inner class CompetetorStockViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_date=itemView.stock_date_tv
        val tv_qty=itemView.stock_qty_tv
        val iv_date=itemView.tv_stock_view
        val iv_sync=itemView.sync_status_iv
    }


}
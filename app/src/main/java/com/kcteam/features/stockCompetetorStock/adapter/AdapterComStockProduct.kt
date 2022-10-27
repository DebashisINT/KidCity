package com.kcteam.features.stockCompetetorStock.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.CompetetorStockEntryProductModelEntity
import kotlinx.android.synthetic.main.row_com_stock_product_list.view.*

class AdapterComStockProduct(val context: Context,val stockList:List<CompetetorStockEntryProductModelEntity>): RecyclerView.Adapter<AdapterComStockProduct.ComStockProductViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComStockProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_com_stock_product_list,parent,false)
        return ComStockProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stockList?.size
    }

    override fun onBindViewHolder(holder: ComStockProductViewHolder, position: Int) {
        holder.tv_brand.text=stockList?.get(position)?.brand_name
        holder.tv_product.text=stockList?.get(position)?.product_name
        holder.tv_qty.text=stockList?.get(position)?.qty
        holder.tv_mrp.text=stockList?.get(position)?.mrp
    }

    inner class ComStockProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_brand=itemView.tv_row_view_stock_products_brand
        val tv_product=itemView.tv_row_view_stock_products_product
        val tv_qty=itemView.tv_row_view_stock_products_qty
        val tv_mrp=itemView.tv_row_view_stock_products_mrp
    }

}
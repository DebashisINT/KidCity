package com.kcteam.features.stockAddCurrentStock.adapter

import android.content.Context
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.CurrentStockEntryModelEntity
import com.kcteam.app.domain.CurrentStockEntryProductModelEntity
import kotlinx.android.synthetic.main.row_view_stock_product_details.view.*

class AdapterStockProductDetails(val context: Context, val stockProductList: List<CurrentStockEntryProductModelEntity>): RecyclerView.Adapter<AdapterStockProductDetails.StockProductViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view_stock_product_details,parent,false)
        return StockProductViewHolder(view)
    }

    override fun getItemCount(): Int {
       return stockProductList.size
    }

    override fun onBindViewHolder(holder: StockProductViewHolder, position: Int) {
        var product = AppDatabase.getDBInstance()?.productListDao()?.getSingleProduct(stockProductList.get(position).product_id!!.toInt())
        product!!.product_name
        holder.tv_product.text= product!!.product_name
        holder.tv_product_brand.text= product!!.brand
        holder.tv_product_qty.text= stockProductList.get(position).product_stock_qty
    }

    inner class StockProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_product=itemView.tv_row_view_stock_products_product
        val tv_product_brand=itemView.tv_row_view_stock_products_brand
        val tv_product_qty=itemView.tv_row_view_stock_products_qty
    }

}
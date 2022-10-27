package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.features.viewAllOrder.interf.NewOrderSizeQtyDelOnClick
import com.kcteam.features.viewAllOrder.model.ProductOrder
import kotlinx.android.synthetic.main.row_new_order_size_qty_list.view.*

class OrderSizeQtyDetailsAdapter(var context: Context,var size_qty_list:ArrayList<ProductOrder>,val listner: NewOrderSizeQtyDelOnClick) :
    RecyclerView.Adapter<OrderSizeQtyDetailsAdapter.SizeQtyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeQtyViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.row_new_order_size_qty_list,parent,false)
        return SizeQtyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return size_qty_list!!.size
    }

    override fun onBindViewHolder(holder: SizeQtyViewHolder, position: Int) {
        holder.tv_size.text="Size : "+size_qty_list.get(position).size
        holder.tv_qty.text="Qty : "+size_qty_list.get(position).qty

//        holder.iv_del.setOnClickListener{listner.sizeQtyListOnClick(size_qty_list.get(holder.adapterPosition),holder.adapterPosition)}
    }

    inner class SizeQtyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val tv_size=itemView.tv_new_order_size
        val tv_qty=itemView.tv_new_order_qty
        val iv_del=itemView.iv_new_order_del
    }

}
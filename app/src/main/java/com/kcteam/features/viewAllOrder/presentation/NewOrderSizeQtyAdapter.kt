package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.features.viewAllOrder.model.ProductOrder
import kotlinx.android.synthetic.main.row_new_order_size_qty_list.view.*

class NewOrderSizeQtyAdapter(var context: Context, var size_qty_list:ArrayList<ProductOrder>) :
        RecyclerView.Adapter<NewOrderSizeQtyAdapter.NewOrderCartViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrderCartViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.row_new_order_size_qty_list,parent,false)
        return NewOrderCartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return size_qty_list!!.size
    }

    override fun onBindViewHolder(holder: NewOrderCartViewHolder, position: Int) {
        holder.tv_size.text=size_qty_list.get(position).size
        holder.tv_qty.text=size_qty_list.get(position).qty
        holder.iv_del.visibility=View.GONE
        holder.ch_flag.visibility=View.GONE
    }

    inner class NewOrderCartViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tv_size=itemView.tv_new_order_size
        val tv_qty=itemView.tv_new_order_qty
        val iv_del=itemView.iv_new_order_del
        val ch_flag = itemView.new_order_size_qty_checked

    }

}



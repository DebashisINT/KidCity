package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.utils.Toaster
import com.kcteam.features.viewAllOrder.interf.NewOrderSizeQtyDelOnClick
import com.kcteam.features.viewAllOrder.model.ProductOrder
import kotlinx.android.synthetic.main.row_new_order_size_qty_list.view.*

class OrderSizeQtyDetailsDelAdapter (var context: Context, var size_qty_list:ArrayList<ProductOrder>, val listner: NewOrderSizeQtyDelOnClick):
    RecyclerView.Adapter<OrderSizeQtyDetailsDelAdapter.SizeQtyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeQtyViewHolder {
        val view= LayoutInflater.from(context).inflate(R.layout.row_new_order_size_qty_list,parent,false)
        return SizeQtyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return size_qty_list!!.size
    }

    override fun onBindViewHolder(holder: SizeQtyViewHolder, position: Int) {
        holder.tv_size.text= size_qty_list.get(position).size
        holder.tv_qty.text= size_qty_list.get(position).qty

        holder.iv_del.visibility = View.INVISIBLE

        if(holder.ch_flag.isChecked){
            size_qty_list.get(position).isCheckedStatus=true
        }else{
            size_qty_list.get(position).isCheckedStatus=false
        }

        holder.ch_flag.setOnClickListener { v: View? ->
            if (holder.ch_flag.isChecked()) {
                size_qty_list.get(position).isCheckedStatus=true
                listner.sizeQtySelListOnClick(size_qty_list)
//                listner.sizeQtyListOnClick(size_qty_list.get(position),position)
//                holder.iv_del.setOnClickListener{listner.sizeQtyListOnClick(size_qty_list.get(position),position)}
            }
            else{
                size_qty_list.get(position).isCheckedStatus=false
//                holder.iv_del.setOnclic
//                Toaster.msgShort(context,"No Checkbox Selected For Deleted")
            }
        }

    }

    inner class SizeQtyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_size=itemView.tv_new_order_size
        val tv_qty=itemView.tv_new_order_qty
        val iv_del=itemView.iv_new_order_del
        val ch_flag = itemView.new_order_size_qty_checked
    }

}
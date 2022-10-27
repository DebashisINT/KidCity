package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.viewAllOrder.interf.NewOrdScrShowDetaisOnCLick
import com.kcteam.features.viewAllOrder.orderNew.NewOrderScrOrderDetailsFragment
import kotlinx.android.synthetic.main.row_new_ord_scr_list.view.*

class AdapterNewOrdScrOrdList(var context: Context,var view_list:ArrayList<NewOrderScrOrderDetailsFragment.OrderIDDateViewStatus>,var listner: NewOrdScrShowDetaisOnCLick) :
    RecyclerView.Adapter<AdapterNewOrdScrOrdList.NewOrdScrViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrdScrViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_new_ord_scr_list,parent,false)
        return NewOrdScrViewHolder(view)
    }

    override fun getItemCount(): Int {
        return view_list!!.size
    }

    override fun onBindViewHolder(holder: NewOrdScrViewHolder, position: Int) {
        holder.date.text= AppUtils.convertToCommonFormat(view_list.get(position).order_date)
        holder.order_id.text=view_list.get(position).order_id
        if(view_list.get(position).isUploaded!!){
            holder.sync.setImageResource(R.drawable.ic_registered_shop_sync)
        }else{
            holder.sync.setImageResource(R.drawable.ic_registered_shop_not_sync)
        }
        holder.view_details.setOnClickListener { listner.getOrderID(view_list.get(holder.adapterPosition).order_id!!,view_list.get(holder.adapterPosition).order_date) }
    }

    inner class NewOrdScrViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var date=itemView.tv_row_new_ord_scr_list_date
        var order_id=itemView.tv_row_new_ord_scr_list_order_id
        var sync=itemView.tv_row_new_ord_scr_list_sync
        var view_details=itemView.ll_row_new_ord_scr_view
    }

}
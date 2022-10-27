package com.kcteam.features.lead.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.features.lead.model.CustomerLeadList
import com.kcteam.features.lead.model.activity_dtls_list
import kotlinx.android.synthetic.main.row_view_activity_lead.view.*

class ViewActivityAdapter(var mContext:Context,var list:ArrayList<activity_dtls_list>,private val listener: OnViewActiClickListener):
    RecyclerView.Adapter<ViewActivityAdapter.ViewActivityViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewActivityViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.row_view_activity_lead, parent, false)
        return ViewActivityViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewActivityViewHolder, position: Int) {
        holder.tv_date.text=list.get(position).activity_date
        holder.tv_time.text=list.get(position).activity_time
        holder.tv_status.text=list.get(position).activity_status
        holder.tv_type.text=list.get(position).activity_type_name
        holder.tv_dtls.text=list.get(position).activity_details
        holder.tv_remark.text=list.get(position).other_remarks

        if(CustomStatic.IsViewLeadFromInProcess){
            holder.iv_edit.visibility=View.VISIBLE
        }else{
            holder.iv_edit.visibility=View.GONE
        }

        holder.iv_edit.setOnClickListener {
            listener.onEditClick(list.get(holder.adapterPosition),holder.adapterPosition)
        }
    }

    inner class ViewActivityViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var tv_date = itemView.tv_row_view_acti_lead_date
        var tv_time = itemView.tv_row_view_acti_lead_time
        var tv_status = itemView.tv_row_view_acti_lead_status
        var tv_type = itemView.tv_row_view_acti_lead_type
        var tv_dtls = itemView.tv_row_view_acti_lead_dtls
        var tv_remark = itemView.tv_row_view_acti_lead_remarks

        var iv_edit = itemView.iv_row_view_acti_lead_edit
    }

    interface OnViewActiClickListener {
        fun onEditClick(obj: activity_dtls_list,adapterPos:Int)
    }


}
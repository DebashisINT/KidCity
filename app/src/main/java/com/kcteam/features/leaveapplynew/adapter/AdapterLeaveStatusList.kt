package com.kcteam.features.leaveapplynew.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.features.addAttendence.model.Leave_list_Response
import kotlinx.android.synthetic.main.row_leave_status_list.view.*


class AdapterLeaveStatusList(var context: Context, var applied_leave_list:ArrayList<Leave_list_Response>) :
        RecyclerView.Adapter<AdapterLeaveStatusList.AdapterLeaveStatusHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterLeaveStatusHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_leave_status_list,parent,false)
        return AdapterLeaveStatusHolder(view)
    }

    override fun getItemCount(): Int {
        return applied_leave_list!!.size
    }

    override fun onBindViewHolder(holder: AdapterLeaveStatusHolder, position: Int) {
        holder.date_applied.text = applied_leave_list.get(position).applied_date
        holder.date_from.text = applied_leave_list.get(position).from_date
        holder.date_to.text = applied_leave_list.get(position).to_date
        holder.leave_type.text = applied_leave_list.get(position).leave_type
        holder.date_to.text = applied_leave_list.get(position).to_date
        holder.remarks.text = applied_leave_list.get(position).approver_remarks
        holder.leave_reason.text = applied_leave_list.get(position).leave_reason

        if(applied_leave_list.get(position).approve_status!!){
            holder.leave_status.text = "Approved"
        }else if(applied_leave_list.get(position).reject_status!!){
            holder.leave_status.text = "Rejected"
        }

    }

    inner class AdapterLeaveStatusHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val date_applied=itemView.pick_a_date_TV
        val date_from=itemView.from_date_show_tv
        val date_to=itemView.to_date_show_tv
        val leave_type=itemView.leave_type_show_tv
        val leave_status = itemView.leave_status_show_tv
        val remarks = itemView.remark_show_tv
        val leave_reason = itemView.tv_leave_reason

    }



}
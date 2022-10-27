package com.kcteam.features.leaveapplynew.adapter

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.features.addAttendence.model.Leave_list_Response
import com.kcteam.features.leaveapplynew.ClickonStatus
import kotlinx.android.synthetic.main.row_applied_leave_list.view.*


class AdapterAppliedLeaveList(var context: Context, var applied_leave_list:ArrayList<Leave_list_Response>, var clickedUserId:String,var listner: ClickonStatus) :
        RecyclerView.Adapter<AdapterAppliedLeaveList.AdapterAppliedLeaveHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterAppliedLeaveHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_applied_leave_list,parent,false)
        return AdapterAppliedLeaveHolder(view)
    }

    override fun getItemCount(): Int {
        return applied_leave_list!!.size
    }

    override fun onBindViewHolder(holder: AdapterAppliedLeaveHolder, position: Int) {
       holder.date_applied.text = applied_leave_list.get(position).applied_date
        holder.date_from.text = applied_leave_list.get(position).from_date
        holder.date_to.text = applied_leave_list.get(position).to_date
        holder.leave_type.text = applied_leave_list.get(position).leave_type
        holder.date_to.text = applied_leave_list.get(position).to_date

        holder.btn_approved.setOnClickListener {
            listner.OnApprovedclick(applied_leave_list.get(holder.adapterPosition))
        }
        holder.btn_reject.setOnClickListener {
            listner.OnRejectclick(applied_leave_list.get(holder.adapterPosition))
        }

        if(clickedUserId.equals(Pref.user_id)){
            holder.ll_appr_rej_root.visibility=View.GONE
        }

        holder.remark.text = applied_leave_list.get(position).leave_reason

    }

    inner class AdapterAppliedLeaveHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val date_applied=itemView.pick_a_date_TV
        val date_from=itemView.from_date_show_tv
        val date_to=itemView.to_date_show_tv
        val leave_type=itemView.leave_type_show_tv
        val btn_approved = itemView.tv_message_approved
        val btn_reject = itemView.tv_message_reject
        val remark = itemView.leave_remark_show_tv

        val ll_appr_rej_root = itemView.ll_row_applied_leave_appr_rej_root
    }



}
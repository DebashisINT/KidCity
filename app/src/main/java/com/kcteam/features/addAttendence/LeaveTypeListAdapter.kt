package com.kcteam.features.addAttendence

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.LeaveTypeEntity
import kotlinx.android.synthetic.main.inflate_vehicle_log_type.view.*

/**
 * Created by Saikat on 08-11-2018.
 */
class LeaveTypeListAdapter(private val context: Context, private val leaveTypeList: ArrayList<LeaveTypeEntity>, private val listener: OnLeaveTypeClickListener) :
        RecyclerView.Adapter<LeaveTypeListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_vehicle_log_type, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, leaveTypeList, listener)
    }

    override fun getItemCount(): Int {
        return leaveTypeList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, leaveTypeList: ArrayList<LeaveTypeEntity>?, listener: OnLeaveTypeClickListener) {

            itemView.tv_log_type.text = leaveTypeList?.get(adapterPosition)?.leave_type
            itemView.iv_check.visibility = View.GONE
            //itemView.iv_check.isSelected = workTypeList?.get(adapterPosition)?.isSelected!!
            itemView.setOnClickListener {
                listener.onLeaveTypeClick(leaveTypeList?.get(adapterPosition), adapterPosition)
            }
        }
    }

    interface OnLeaveTypeClickListener {
        fun onLeaveTypeClick(leaveTypeList: LeaveTypeEntity?, adapterPosition: Int)
    }
}
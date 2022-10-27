package com.kcteam.features.addAttendence

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.LeaveTypeEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.addAttendence.model.LeaveListDataModel
import kotlinx.android.synthetic.main.inflate_leave_list_item.view.*
import kotlinx.android.synthetic.main.inflate_vehicle_log_type.view.*

/**
 * Created by Saikat on 05-Aug-20.
 */
class LeaveAdapter(private val context: Context, private val leaveList: ArrayList<LeaveListDataModel>?) : RecyclerView.Adapter<LeaveAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_leave_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, leaveList)
    }

    override fun getItemCount(): Int {
        return leaveList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, leaveList: ArrayList<LeaveListDataModel>?) {

            itemView.apply {
               //tv_from_date.text = leaveList?.get(adapterPosition)?.from_date
                tv_from_date.text =  AppUtils.getFormatedDateNew(leaveList?.get(adapterPosition)?.from_date!!,"yyyy-mm-dd","dd-mm-yyyy")
                //tv_to_date.text = leaveList?.get(adapterPosition)?.to_date
                tv_to_date.text =  AppUtils.getFormatedDateNew(leaveList?.get(adapterPosition)?.to_date!!,"yyyy-mm-dd","dd-mm-yyyy")
                tv_leave_type.text = leaveList?.get(adapterPosition)?.leave_type

                if (!TextUtils.isEmpty(leaveList?.get(adapterPosition)?.desc))
                    tv_reason.text = leaveList?.get(adapterPosition)?.desc
                else
                    tv_reason.text = "N.A."

                tv_status.text = leaveList?.get(adapterPosition)?.status
            }
        }
    }
}
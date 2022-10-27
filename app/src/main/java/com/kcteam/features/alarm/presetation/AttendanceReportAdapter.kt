package com.kcteam.features.alarm.presetation

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.alarm.model.AttendanceReport
import kotlinx.android.synthetic.main.inflate_attendance_report_item.view.*

/**
 * Created by Saikat on 20-02-2019.
 */
class AttendanceReportAdapter(context: Context, val userLocationDataEntity: ArrayList<AttendanceReport>?, val listener: OnClickListener) :
        RecyclerView.Adapter<AttendanceReportAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    //var userLocationDataEntity: List<OrderDetailsListEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_attendance_report_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: ArrayList<AttendanceReport>?, listener: OnClickListener) {
            if (adapterPosition % 2 == 0)
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            itemView.tv_name.text = userLocationDataEntity?.get(adapterPosition)?.member_name

            if (!TextUtils.isEmpty(userLocationDataEntity?.get(adapterPosition)?.report_to)) {
                itemView.tv_report_to_name.visibility = View.VISIBLE
                itemView.tv_report_to_name.text = userLocationDataEntity?.get(adapterPosition)?.report_to
            } else
                itemView.tv_report_to_name.visibility = View.GONE

            if (!TextUtils.isEmpty(userLocationDataEntity?.get(adapterPosition)?.login_time))
                itemView.tv_login_time.text = userLocationDataEntity?.get(adapterPosition)?.login_time
            else
                itemView.tv_login_time.text = "--"

            itemView.tv_status.text = userLocationDataEntity?.get(adapterPosition)?.status
            if(userLocationDataEntity?.get(adapterPosition)?.status.equals("Not Login")){
                itemView.tv_status.text = "Absent"
            }
            else{
                itemView.tv_status.text = userLocationDataEntity?.get(adapterPosition)?.status
            }

            if (userLocationDataEntity?.get(adapterPosition)?.status.equals("leave", ignoreCase = true))
                itemView.tv_status.setTextColor(context.resources.getColor(R.color.bill_green))
            else if (userLocationDataEntity?.get(adapterPosition)?.status.equals("late", ignoreCase = true)) {
                itemView.tv_status.setTextColor(context.resources.getColor(R.color.red))
                itemView.tv_status.setTypeface(null, Typeface.BOLD)
            } else
                itemView.tv_status.setTextColor(context.resources.getColor(R.color.black))

            itemView.iv_call_icon.setOnClickListener({
                listener.onCallClick(adapterPosition)
            })
        }
    }

    interface OnClickListener {
        fun onCallClick(adapterPosition: Int)
    }
}
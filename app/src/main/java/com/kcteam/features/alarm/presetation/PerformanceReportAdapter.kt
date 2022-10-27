package com.kcteam.features.alarm.presetation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.alarm.model.PerformanceReportDataModel
import kotlinx.android.synthetic.main.inflate_performance_report_item.view.*


/**
 * Created by Saikat on 21-02-2019.
 */
class PerformanceReportAdapter(context: Context, val performanceData: ArrayList<PerformanceReportDataModel>?, val listener: OnClickListener) :
        RecyclerView.Adapter<PerformanceReportAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    //var performanceData: List<OrderDetailsListEntity> = performanceData

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, performanceData, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_performance_report_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return performanceData?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, performanceData: ArrayList<PerformanceReportDataModel>?, listener: OnClickListener) {

            itemView.tv_name.text = performanceData?.get(adapterPosition)?.member_name

            if (!TextUtils.isEmpty(performanceData?.get(adapterPosition)?.report_to))
                itemView.tv_report_to_value.text = performanceData?.get(adapterPosition)?.report_to
            else
                itemView.tv_report_to_value.text = "N.A."

            itemView.tv_visit_count_value.text = performanceData?.get(adapterPosition)?.total_shop_count
            itemView.tv_travel_value.text = performanceData?.get(adapterPosition)?.total_travel_distance
            itemView.tv_collection_value.text = context.getString(R.string.rupee_symbol) + " " + performanceData?.get(adapterPosition)?.collection_value
            itemView.tv_order_value.text = context.getString(R.string.rupee_symbol) + " " + performanceData?.get(adapterPosition)?.order_vale

            itemView.iv_call_icon.setOnClickListener {
                listener.onCallClick(adapterPosition)
            }
        }
    }

    interface OnClickListener {
        fun onCallClick(adapterPosition: Int)
    }
}
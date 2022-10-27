package com.kcteam.features.alarm.presetation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.alarm.model.VisitReportDataModel
import kotlinx.android.synthetic.main.inflate_visit_report_item.view.*

/**
 * Created by Saikat on 21-02-2019.
 */
class VisitReportAdapter(context: Context, val visitData: ArrayList<VisitReportDataModel>?, val listener: OnClickListener) :
        RecyclerView.Adapter<VisitReportAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    //var visitData: List<OrderDetailsListEntity> = visitData

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, visitData, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_visit_report_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return visitData?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, visitData: ArrayList<VisitReportDataModel>?, listener: OnClickListener) {

            if (adapterPosition % 2 == 0)
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            itemView.tv_name.text = visitData?.get(adapterPosition)?.member_name

            if (!TextUtils.isEmpty(visitData?.get(adapterPosition)?.report_to)) {
                itemView.tv_report_to_name.visibility = View.VISIBLE
                itemView.tv_report_to_name.text = visitData?.get(adapterPosition)?.report_to
            }
            else
                itemView.tv_report_to_name.visibility = View.GONE

            itemView.tv_shop_count.text = visitData?.get(adapterPosition)?.total_shop_count
            itemView.tv_distance.text = visitData?.get(adapterPosition)?.total_distance_travelled

            itemView.setOnClickListener({
                listener.onViewClick(adapterPosition)
            })
        }
    }

    interface OnClickListener {
        fun onViewClick(adapterPosition: Int)
    }
}
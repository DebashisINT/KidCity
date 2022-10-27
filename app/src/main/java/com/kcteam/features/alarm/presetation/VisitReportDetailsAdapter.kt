package com.kcteam.features.alarm.presetation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.alarm.model.VisitDetailsDataModel
import kotlinx.android.synthetic.main.inflate_visit_report_details_item.view.*

/**
 * Created by Saikat on 21-02-2019.
 */
class VisitReportDetailsAdapter(context: Context, val visitDetailsData: ArrayList<VisitDetailsDataModel>?) : RecyclerView.Adapter<VisitReportDetailsAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    //var visitDetailsData: List<OrderDetailsListEntity> = visitDetailsData

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, visitDetailsData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_visit_report_details_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return visitDetailsData?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, visitDetailsData: ArrayList<VisitDetailsDataModel>?) {

            if (adapterPosition % 2 == 0)
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            itemView.tv_shop_name.text = visitDetailsData?.get(adapterPosition)?.shop_name
            itemView.tv_visit_time.text = visitDetailsData?.get(adapterPosition)?.visit_time
            itemView.tv_duration.text = visitDetailsData?.get(adapterPosition)?.duration_spent
            itemView.tv_distance.text = visitDetailsData?.get(adapterPosition)?.distance

            itemView.tv_date_visit_report_dtls.text = visitDetailsData?.get(adapterPosition)?.date

        }
    }
}
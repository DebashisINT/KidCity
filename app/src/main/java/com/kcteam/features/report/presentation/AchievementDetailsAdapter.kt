package com.kcteam.features.report.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.report.model.AchievementDetailsModel
import kotlinx.android.synthetic.main.inflate_visit_report_details_item.view.*

/**
 * Created by Saikat on 22-Jul-20.
 */
class AchievementDetailsAdapter(context: Context, val achvDetailsData: ArrayList<AchievementDetailsModel>?) : RecyclerView.Adapter<AchievementDetailsAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, achvDetailsData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_visit_report_details_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return achvDetailsData?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, achvDetailsData: ArrayList<AchievementDetailsModel>?) {

            if (adapterPosition % 2 == 0)
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            achvDetailsData?.get(adapterPosition)?.apply {
                itemView.tv_shop_name.text = cust_name
                itemView.tv_visit_time.text = stage
                itemView.tv_duration.text = visit_date
                itemView.tv_distance.text = visit_time
            }
        }
    }
}
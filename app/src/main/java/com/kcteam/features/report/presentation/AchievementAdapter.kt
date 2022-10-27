package com.kcteam.features.report.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.report.model.AchievementDataModel
import kotlinx.android.synthetic.main.inflate_visit_report_item.view.*

/**
 * Created by Saikat on 22-Jul-20.
 */
class AchievementAdapter(context: Context, val achvData: ArrayList<AchievementDataModel>?, val listener: OnClickListener) :
        RecyclerView.Adapter<AchievementAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, achvData, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_visit_report_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return achvData?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, achvData: ArrayList<AchievementDataModel>?, listener: OnClickListener) {

            if (adapterPosition % 2 == 0)
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            itemView.tv_name.text = achvData?.get(adapterPosition)?.member_name

            if (!TextUtils.isEmpty(achvData?.get(adapterPosition)?.report_to)) {
                itemView.tv_report_to_name.visibility = View.VISIBLE
                itemView.tv_report_to_name.text = achvData?.get(adapterPosition)?.report_to
            }
            else
                itemView.tv_report_to_name.visibility = View.GONE

            itemView.tv_shop_count.text = achvData?.get(adapterPosition)?.stage_count
            itemView.tv_distance.visibility = View.GONE
            //itemView.rl_arrow.visibility = View.GONE

            itemView.setOnClickListener({
                listener.onViewClick(adapterPosition)
            })
        }
    }

    interface OnClickListener {
        fun onViewClick(adapterPosition: Int)
    }
}
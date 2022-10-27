package com.kcteam.features.dailyPlan.prsentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dailyPlan.model.GetPlanDetailsDataModel
import kotlinx.android.synthetic.main.inflate_plan_details_item.view.*

/**
 * Created by Saikat on 23-12-2019.
 */
class DailyPlanDetailsAdapter(private val context: Context, private val planDataDetails: ArrayList<GetPlanDetailsDataModel>) : RecyclerView.Adapter<DailyPlanDetailsAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_plan_details_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, planDataDetails)
    }

    override fun getItemCount(): Int {
        return planDataDetails.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, planDataDetails: ArrayList<GetPlanDetailsDataModel>) {
            /*if (adapterPosition % 2 == 0)
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))*/

            itemView.tv_plan_date.text = AppUtils.convertToCommonFormat(planDataDetails[adapterPosition].plan_date)
            if (!TextUtils.isEmpty(planDataDetails[adapterPosition].plan_value))
                itemView.tv_plan_value.text = context.getString(R.string.rupee_symbol_with_space) + planDataDetails[adapterPosition].plan_value
            else
                itemView.tv_plan_value.text = context.getString(R.string.rupee_symbol_with_space) + "0.00"

            if (!TextUtils.isEmpty(planDataDetails[adapterPosition].achievement_value))
                itemView.tv_achv_value.text = context.getString(R.string.rupee_symbol_with_space) + planDataDetails[adapterPosition].achievement_value
            else
                itemView.tv_achv_value.text = context.getString(R.string.rupee_symbol_with_space) + "0.00"

            itemView.tv_percent.text = planDataDetails[adapterPosition].percnt

            if (!TextUtils.isEmpty(planDataDetails[adapterPosition].achievement_remarks))
                itemView.tv_achv_remark.text = planDataDetails[adapterPosition].achievement_remarks
            else
                itemView.tv_achv_remark.text = "N.A."

            if (!TextUtils.isEmpty(planDataDetails[adapterPosition].plan_remarks))
                itemView.tv_plan_remark.text = planDataDetails[adapterPosition].plan_remarks
            else
                itemView.tv_plan_remark.text = "N.A."
        }
    }
}
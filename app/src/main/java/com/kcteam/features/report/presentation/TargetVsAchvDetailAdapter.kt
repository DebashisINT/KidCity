package com.kcteam.features.report.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.report.model.TargetVsAchvDetailsModel
import kotlinx.android.synthetic.main.inflate_targ_achv_item.view.*

/**
 * Created by Saikat on 22-Jul-20.
 */
class TargetVsAchvDetailAdapter(context: Context, val targVsAchvDetailsData: ArrayList<TargetVsAchvDetailsModel>?) : RecyclerView.Adapter<TargetVsAchvDetailAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, targVsAchvDetailsData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_targ_achv_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return targVsAchvDetailsData?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, targVsAchvDetailsData: ArrayList<TargetVsAchvDetailsModel>?) {

            itemView.apply {
                if (adapterPosition % 2 == 0)
                    setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
                else
                    setBackgroundColor(ContextCompat.getColor(context, R.color.white))

                tv_enquiry.text = targVsAchvDetailsData?.get(adapterPosition)?.enquiry
                tv_lead.text = targVsAchvDetailsData?.get(adapterPosition)?.lead
                tv_test_drive.text = targVsAchvDetailsData?.get(adapterPosition)?.test_drive
                tv_booking.text = targVsAchvDetailsData?.get(adapterPosition)?.booking
                tv_retail.text = targVsAchvDetailsData?.get(adapterPosition)?.retail
            }
        }
    }
}
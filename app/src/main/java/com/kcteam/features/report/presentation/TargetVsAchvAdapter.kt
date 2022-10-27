package com.kcteam.features.report.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.report.model.TargetVsAchvDataModel
import kotlinx.android.synthetic.main.inflate_visit_report_item.view.*

/**
 * Created by Saikat on 22-Jul-20.
 */
class TargetVsAchvAdapter(context: Context, val targ_vs_achv_list: ArrayList<TargetVsAchvDataModel>?, val listener: OnClickListener) :
        RecyclerView.Adapter<TargetVsAchvAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, targ_vs_achv_list, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_target_achv_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return targ_vs_achv_list?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, targ_vs_achv_list: ArrayList<TargetVsAchvDataModel>?, listener: OnClickListener) {

            itemView.tv_name.text = targ_vs_achv_list?.get(adapterPosition)?.member_name

            if (!TextUtils.isEmpty(targ_vs_achv_list?.get(adapterPosition)?.report_to)) {
                itemView.tv_report_to_name.visibility = View.VISIBLE
                itemView.tv_report_to_name.text = targ_vs_achv_list?.get(adapterPosition)?.report_to
            }
            else
                itemView.tv_report_to_name.visibility = View.GONE


            itemView.setOnClickListener({
                listener.onViewClick(adapterPosition)
            })
        }
    }

    interface OnClickListener {
        fun onViewClick(adapterPosition: Int)
    }
}
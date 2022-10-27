package com.kcteam.features.reimbursement.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.reimbursement.model.reimbursementlist.ReimbursementListDetailsModel
import kotlinx.android.synthetic.main.inflate_reimbursement_item.view.*

/**
 * Created by Saikat on 22-01-2019.
 */
class ReimbursmentListAdapter(context: Context, private val expense_list_details: ArrayList<ReimbursementListDetailsModel>, val listener: OnItemClickListener) : RecyclerView.Adapter<ReimbursmentListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, expense_list_details, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_reimbursement_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return expense_list_details.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: List<ReimbursementListDetailsModel>, listener: OnItemClickListener) {
            //Picasso.with(context).load(list[adapterPosition].shopImageLocalPath).into(itemView.shop_image_IV)
            try {

                /*if (adapterPosition == 2) {
                    itemView.tv_status.text = context.getString(R.string.pending)
                    itemView.tv_status.setTextColor(context.resources.getColor(R.color.reimbusement_yellow))
                    itemView.iv_edit_icon.visibility = View.VISIBLE
                }
                else {
                    itemView.tv_status.text = context.getString(R.string.approved)
                    itemView.tv_status.setTextColor(context.resources.getColor(R.color.approved_green))
                    itemView.iv_edit_icon.visibility = View.GONE
                }*/

                itemView.tv_status.text = list[adapterPosition].status
                //itemView.tv_amount.paintFlags = itemView.tv_amount.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

                if (list[adapterPosition].status.equals("pending", ignoreCase = true)) {
                    //itemView.tv_status.text = context.getString(R.string.pending)
                    itemView.tv_status.setTextColor(context.resources.getColor(R.color.reimbusement_yellow))
                    //itemView.iv_edit_icon.visibility = View.VISIBLE
                } else if (list[adapterPosition].status.equals("approved", ignoreCase = true)) {
                    //itemView.tv_status.text = context.getString(R.string.approved)
                    itemView.tv_status.setTextColor(context.resources.getColor(R.color.approved_green))
                    //itemView.iv_edit_icon.visibility = View.GONE
                } else {
                    itemView.tv_status.setTextColor(context.resources.getColor(R.color.reimbusement_yellow))
                    //itemView.iv_edit_icon.visibility = View.GONE
                }

                if (list[adapterPosition].isEditable.equals("false", ignoreCase = true)) {
                    itemView.iv_edit_icon.visibility = View.GONE
                    itemView.iv_view_icon.visibility = View.GONE
                }
                else {
                    itemView.iv_edit_icon.visibility = View.VISIBLE
                    itemView.iv_view_icon.visibility = View.VISIBLE
                }

                if (!TextUtils.isEmpty(list[adapterPosition].applied_date))
                    itemView.tv_applied_on_value.text = AppUtils.convertToSelectedDateReimbursement(list[adapterPosition].applied_date!!)
                else
                    itemView.tv_applied_on_value.text = "N/A"

                if (!TextUtils.isEmpty(list[adapterPosition].travel_mode)) {
                    itemView.tv_travel_mode.text = context.getString(R.string.travel_mode)
                    itemView.tv_travel_mode_value.text = list[adapterPosition].travel_mode
                } else if (!TextUtils.isEmpty(list[adapterPosition].food_type)) {
                    itemView.tv_travel_mode.text = context.getString(R.string.food_type)
                    itemView.tv_travel_mode_value.text = list[adapterPosition].food_type
                } else if (!TextUtils.isEmpty(list[adapterPosition].hotel_name)) {
                    itemView.tv_travel_mode.text = context.getString(R.string.hotel_name)
                    itemView.tv_travel_mode_value.text = list[adapterPosition].hotel_name
                } else if (!TextUtils.isEmpty(list[adapterPosition].remarks)) {
                    itemView.tv_travel_mode.text = context.getString(R.string.remarks)
                    itemView.tv_travel_mode_value.text = list[adapterPosition].remarks
                } else {
                    itemView.tv_travel_mode.visibility = View.GONE
                    itemView.tv_travel_mode_value.visibility = View.GONE
                }

                itemView.tv_amount.text = list[adapterPosition].amount

                itemView.tv_approved_amount.text = list[adapterPosition].approved_amount

                itemView.iv_edit_icon.setOnClickListener({
                    listener.onEditClick(adapterPosition)
                })

                itemView.iv_view_icon.setOnClickListener({
                    listener.onDeleteClick(adapterPosition)
                })

                itemView.setOnClickListener({
                    listener.onViewClick(adapterPosition)
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface OnItemClickListener {
        fun onEditClick(adapterPosition: Int)
        fun onViewClick(adapterPosition: Int)
        fun onDeleteClick(adapterPosition: Int)
    }
}
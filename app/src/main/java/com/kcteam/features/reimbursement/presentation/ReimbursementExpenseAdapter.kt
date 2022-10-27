package com.kcteam.features.reimbursement.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.R
import com.kcteam.features.reimbursement.model.reimbursementlist.ReimbursementListDataModel
import kotlinx.android.synthetic.main.inflate_reimbursement_expense_item.view.*

/**
 * Created by Saikat on 23-01-2019.
 */
class ReimbursementExpenseAdapter(context: Context, private val list: ArrayList<ReimbursementListDataModel>?, val listener: OnItemClickListener) : RecyclerView.Adapter<ReimbursementExpenseAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    //private var list = arrayOfNulls<String>(10)

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, list, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_reimbursement_expense_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: ArrayList<ReimbursementListDataModel>?, listener: OnItemClickListener) {
            //Picasso.with(context).load(list[adapterPosition].shopImageLocalPath).into(itemView.shop_image_IV)
            try {

                itemView.tv_food_amount.text = list?.get(adapterPosition)?.total_amount
                itemView.tv_expense_type.text = list?.get(adapterPosition)?.expense_type

                itemView.cv_food_price.isSelected = list?.get(adapterPosition)?.isSelected!!
                itemView.iv_food_rupee.isSelected = list[adapterPosition].isSelected
                itemView.tv_food_amount.isSelected = list[adapterPosition].isSelected

                if (!TextUtils.isEmpty(list[adapterPosition].expense_type_image)) {
                    Glide.with(context)
                            .load(list[adapterPosition].expense_type_image)
                            .apply(RequestOptions.placeholderOf(R.drawable.ic_food_icon).error(R.drawable.ic_food_icon))
                            .into(itemView.iv_expense_icon)
                }

                itemView.setOnClickListener({
                    //if (adapterPosition==list[adapterPosition])

                    /*itemView.cv_food_price.isSelected = true
                    itemView.iv_food_rupee.isSelected = true
                    itemView.tv_food_amount.isSelected = true*/

                    listener.onItemClick(adapterPosition)

                })


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(adapterPosition: Int)
    }
}
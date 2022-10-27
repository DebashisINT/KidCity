package com.kcteam.features.reimbursement.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.reimbursement.model.ApplyReimbursementInputModel
import com.kcteam.features.reimbursement.model.ReimbursementConfigModeOfTravelDataModel
import kotlinx.android.synthetic.main.row_price_layout.view.*

/**
 * Created by Saikat on 28-01-2019.
 */
class ReimbursementBottomLayoutAdapter(context: Context, private val list: ArrayList<ApplyReimbursementInputModel>, val modeOfTravelArrayList: ArrayList<ReimbursementConfigModeOfTravelDataModel>,
                                       val listener: OnItemClickListener) : RecyclerView.Adapter<ReimbursementBottomLayoutAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    //private var list = arrayOfNulls<String>(10)

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, list, modeOfTravelArrayList, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.row_price_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: ArrayList<ApplyReimbursementInputModel>, modeOfTravelArrayList: ArrayList<ReimbursementConfigModeOfTravelDataModel>,
                      listener: OnItemClickListener) {
            //Picasso.with(context).load(list[adapterPosition].shopImageLocalPath).into(itemView.shop_image_IV)
            try {

                itemView.tvPriceRow.text = context.getString(R.string.Rs) + " " + String.format("%.2f", list[adapterPosition].expense_details[0].reimbursement_details[0].amount.toDouble())


                if (list[adapterPosition].expense_details[0].expence_type_id == "1") {
                    itemView.tvPriceRowTitle.text = context.getString(R.string.travel_mode)
                    itemView.tvPriceDesc.visibility = View.VISIBLE

                    for (i in modeOfTravelArrayList.indices) {
                        if (modeOfTravelArrayList[i].travel_id == list[adapterPosition].expense_details[0].reimbursement_details[0].mode_of_travel) {
                            itemView.tvPriceDesc.text = modeOfTravelArrayList[i].travel_type
                            break
                        }
                    }
                } else {
                    itemView.tvPriceRowTitle.text = list[adapterPosition].expense_details[0].expence_type
                    itemView.tvPriceDesc.visibility = View.GONE
                }


                itemView.iv_ta_delete_icon.setOnClickListener({
                    listener.onDeleteClick(adapterPosition)
                })

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface OnItemClickListener {
        fun onDeleteClick(adapterPosition: Int)
    }
}
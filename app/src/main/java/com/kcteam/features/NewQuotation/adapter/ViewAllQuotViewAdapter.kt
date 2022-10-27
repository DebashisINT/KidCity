package com.kcteam.features.NewQuotation.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.ProductListEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.features.NewQuotation.model.shop_wise_quotation_list
import kotlinx.android.synthetic.main.inflater_quot_history_item.view.*
import kotlinx.android.synthetic.main.row_new_quot_added_prod.view.*
import kotlinx.android.synthetic.main.row_new_quot_added_prod.view.tv_row_new_quot_added_prod_name

class ViewAllQuotViewAdapter(private val context: Context, private val selectedProductList: ArrayList<shop_wise_quotation_list>?, private val listener: OnClickListener) :
        RecyclerView.Adapter<ViewAllQuotViewAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflater_quot_history_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, selectedProductList, listener)
    }

    override fun getItemCount(): Int {
        return selectedProductList!!.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<shop_wise_quotation_list>?, listener: OnClickListener) {
            itemView.quot_date_tv.text=AppUtils.convertToDateLikeOrderFormat(categoryList!!.get(adapterPosition).save_date_time!!.toString()).toString()
            itemView.quot_no_tv.text=categoryList!!.get(adapterPosition).quotation_number
//            Pending/Rejected/Approved
            if(categoryList!!.get(adapterPosition).quotation_status.equals("Approved")){
                itemView.tv_quot_del.visibility = View.VISIBLE
                itemView.share_iv.visibility = View.VISIBLE
                itemView.iv_quto_his_status_action.setImageResource(R.drawable.approve)
                itemView.quot_date_tv.setTextColor(context.resources.getColor(R.color.color_custom_green))
                itemView.quot_no_tv.setTextColor(context.resources.getColor(R.color.color_custom_green))
            }
            else{
                itemView.tv_quot_del.visibility = View.GONE
                itemView.share_iv.visibility = View.GONE
                if(categoryList!!.get(adapterPosition).quotation_status.equals("Rejected")){
                    itemView.iv_quto_his_status_action.setImageResource(R.drawable.rejected)
                    itemView.quot_date_tv.setTextColor(context.resources.getColor(R.color.color_custom_red))
                    itemView.quot_no_tv.setTextColor(context.resources.getColor(R.color.color_custom_red))
                }else{
                    itemView.iv_quto_his_status_action.setImageResource(R.drawable.pending)
                }
            }

            itemView.tv_quot_view.setOnClickListener {
                if(categoryList!!.get(adapterPosition).quotation_status.equals("Approved")) {
                    listener.onView(adapterPosition = adapterPosition,categoryList!!.get(adapterPosition).quotation_number!!, "" )
                }
                else{
                    if(categoryList!!.get(adapterPosition).document_number != null)
                        listener.onView(adapterPosition = adapterPosition, "",categoryList!!.get(adapterPosition).document_number!!)
                    else if(categoryList!!.get(adapterPosition).quotation_number !=null){
                        listener.onView(adapterPosition = adapterPosition,categoryList!!.get(adapterPosition).quotation_number!!, "" )
                    }else
                        listener.onShowMsg("Document Number Not Found")
                }
            }

//            itemView.tv_quot_view.setOnClickListener {
//                listener.onView(adapterPosition = adapterPosition,QuotId = categoryList!!.get(adapterPosition).quotation_number!!)
//            }
            itemView.share_iv.setOnClickListener {
                listener.onShare(adapterPosition = adapterPosition)
            }
            itemView.tv_quot_del.setOnClickListener {
                listener.onDelete(adapterPosition = adapterPosition,QuotId = categoryList!!.get(adapterPosition).quotation_number!!)
            }

        }
    }

    interface OnClickListener {
        fun onView(adapterPosition: Int,QuotId:String,DocId:String)
        fun onShare(adapterPosition: Int)
        fun onDelete(adapterPosition: Int,QuotId:String)
        fun onShowMsg(msg:String)
    }
}
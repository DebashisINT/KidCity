package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.NewOrderColorEntity
import com.kcteam.features.viewAllOrder.interf.ColorListNewOrderOnClick
import kotlinx.android.synthetic.main.row_dialog_new_order_color.view.*

class ColorListAdapter(var context: Context,var color_list:List<NewOrderColorEntity>, var listner: ColorListNewOrderOnClick):
RecyclerView.Adapter<ColorListAdapter.ColorListViewHolder>(), Filterable {

    private var arrayList_bean: ArrayList<NewOrderColorEntity>? = ArrayList()
    private var arrayList_color: ArrayList<NewOrderColorEntity>? = ArrayList()
    private var valueFilter: ValueFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorListViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.row_dialog_new_order_color,parent,false)
        return ColorListViewHolder(view)
    }

    init {
        arrayList_bean?.addAll(color_list)
        arrayList_color?.addAll(color_list)
    }

    override fun getItemCount(): Int {
        //return color_list!!.size
        return arrayList_color!!.size

    }



    override fun onBindViewHolder(holder: ColorListViewHolder, position: Int) {
        //holder.tv_color.text=color_list.get(position).color_name
        //holder.tv_color.setOnClickListener { listner?.productListOnClick(color_list.get(holder.adapterPosition!!)) }

        holder.tv_color.text=arrayList_color!!.get(position).color_name
        holder.tv_color.setOnClickListener { listner?.productListOnClick(arrayList_color!!.get(holder.adapterPosition!!)) }
    }

    inner class ColorListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tv_color = itemView!!.tv_row_dialog_new_order_color
    }

    override fun getFilter(): Filter {
        if (valueFilter == null) {
            valueFilter = ValueFilter()
        }
        return valueFilter as ValueFilter
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            if (constraint != null && constraint.length > 0) {
                val arrayList_filter: ArrayList<NewOrderColorEntity> = ArrayList()
                for (i in 0..color_list!!.size-1) {
                    if (color_list!!.get(i).color_name!!.contains(constraint.toString(),ignoreCase = true)) {
                        val setGetProductDetails = NewOrderColorEntity()
                        setGetProductDetails.id=color_list!!.get(i).id
                        setGetProductDetails.color_id=color_list!!.get(i).color_id
                        setGetProductDetails.color_name=color_list!!.get(i).color_name
                        setGetProductDetails.product_id=color_list!!.get(i).product_id
                        arrayList_filter.add(setGetProductDetails)
                    }
                }
                filterResults.count = arrayList_filter!!.size
                filterResults.values = arrayList_filter
            } else {
                filterResults.count = arrayList_bean!!.size
                filterResults.values = arrayList_bean
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            arrayList_color = results.values as ArrayList<NewOrderColorEntity>
            notifyDataSetChanged()
        }
    }

}
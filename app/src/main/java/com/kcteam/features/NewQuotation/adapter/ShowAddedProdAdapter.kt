package com.kcteam.features.NewQuotation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.features.NewQuotation.model.product_list
import kotlinx.android.synthetic.main.row_new_quot_added_prod.view.*

class ShowAddedProdAdapter(private var context:Context,private var prodList:ArrayList<product_list>):
 RecyclerView.Adapter<ShowAddedProdAdapter.ShowAddedProdViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowAddedProdViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_new_quot_added_prod,parent,false)
        return ShowAddedProdViewHolder(view)
    }

    override fun getItemCount(): Int {
        return prodList.size
    }

    override fun onBindViewHolder(holder: ShowAddedProdViewHolder, position: Int) {
        holder.prod_name.text=prodList.get(position).product_name
        holder.prod_color.text="Color : "+prodList.get(position).color_name
        holder.rate_sqft.text=prodList.get(position).rate_sqft
        holder.rate_sqmtr.text=prodList.get(position).rate_sqmtr
    }

    inner class ShowAddedProdViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var prod_name = itemView.tv_row_new_quot_added_prod_name
        var prod_color = itemView.tv_row_new_quot_added_prod_color
        var rate_sqft = itemView.tv_row_new_quot_added_prod_rate_sqft
        var rate_sqmtr = itemView.tv_row_new_quot_added_prod_rate_sqmtr
    }

}
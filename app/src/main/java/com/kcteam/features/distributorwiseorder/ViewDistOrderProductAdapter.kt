package com.kcteam.features.distributorwiseorder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.DistWiseOrderTblEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.damageProduct.model.Shop_wise_breakage_list
import kotlinx.android.synthetic.main.inflater_breakage_item.view.*
import kotlinx.android.synthetic.main.inflater_dist_order_view_item.view.*

class ViewDistOrderProductAdapter(private val context: Context, private val selectedProductList: ArrayList<DistWiseOrderTblEntity>?) :
    RecyclerView.Adapter<ViewDistOrderProductAdapter.MyViewHolder>(){

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflater_dist_order_view_item, parent, false)
        return MyViewHolder(v)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, selectedProductList)
    }

    override fun getItemCount(): Int {
        return selectedProductList!!.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<DistWiseOrderTblEntity>?) {
            itemView.from_date_dist_wise_order_view_tv.text= categoryList!!.get(adapterPosition).from_date!!
            itemView.to_date_dist_wise_order_view_tv.text=categoryList!!.get(adapterPosition).to_date
            itemView.select_dd_dist_wise_order_view_tv.text=categoryList!!.get(adapterPosition).selected_dd!!
            itemView.select_pp_dist_wise_order_view_tv.text=categoryList!!.get(adapterPosition).selected_pp

            itemView.generated_dt_dist_wise_order_view_tv.text=categoryList!!.get(adapterPosition).genereated_date_time


        }
    }


}
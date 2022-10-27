package com.kcteam.features.returnsOrder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.OrderProductListEntity
import com.kcteam.app.domain.ReturnProductListEntity
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_cart.view.*

class ViewReturnCartAdapter(private val context: Context, private val selectedProductList: List<ReturnProductListEntity>) :
        RecyclerView.Adapter<ViewReturnCartAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    init {
        (context as DashboardActivity).totalPrice.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_cart, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, selectedProductList)
    }

    override fun getItemCount(): Int {
        return selectedProductList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: List<ReturnProductListEntity>) {

            itemView.tv_particular_item.text = categoryList[adapterPosition].product_name


            itemView.tv_brand_item.text = "Rate: ₹ " + String.format("%.2f", categoryList[adapterPosition].rate?.toFloat())

            if (categoryList[adapterPosition].qty?.contains(".")!!)
                itemView.tv_category_item.text = "Quantity: " + categoryList[adapterPosition].qty?.toFloat()?.toInt()
            else
                itemView.tv_category_item.text = "Quantity: " + categoryList[adapterPosition].qty

            try {
                val totalPrice = String.format("%.2f", categoryList[adapterPosition].total_price?.toFloat())
                itemView.tv_watt_item.text = "Total Price: ₹ $totalPrice"
            } catch (e: Exception) {
                e.printStackTrace()
            }
            itemView.ll_edit_delete.visibility = View.GONE
        }
    }
}
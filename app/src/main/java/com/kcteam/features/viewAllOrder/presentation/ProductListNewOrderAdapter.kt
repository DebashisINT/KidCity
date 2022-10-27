package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.NewOrderProductEntity
import com.kcteam.features.viewAllOrder.interf.ProductListNewOrderOnClick
import kotlinx.android.synthetic.main.row_dialog_new_order_product.view.*
import java.util.*
import kotlin.collections.ArrayList

class ProductListNewOrderAdapter(var context: Context,var productList:ArrayList<NewOrderProductEntity>,val listner: ProductListNewOrderOnClick):
 RecyclerView.Adapter<ProductListNewOrderAdapter.ProductListNewOrderViewHolder>(),Filterable{

    private var arrayList_bean: ArrayList<NewOrderProductEntity>? = ArrayList()
    private var arrayList_product: ArrayList<NewOrderProductEntity>? = ArrayList()
    private var valueFilter: ValueFilter? = null

    init {
        arrayList_bean?.addAll(productList)
        arrayList_product?.addAll(productList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListNewOrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_dialog_new_order_product,parent,false)
        return ProductListNewOrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList_product!!.size
    }

    override fun onBindViewHolder(holder: ProductListNewOrderViewHolder, position: Int) {
        holder.tv_product.text=arrayList_product?.get(position)?.product_name

        holder.tv_product.setOnClickListener{listner?.productListOnClick(arrayList_product?.get(holder.adapterPosition)!!)}
    }

    inner class ProductListNewOrderViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tv_product = itemView.tv_row_product_new_order_product
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
                val arrayList_filter: ArrayList<NewOrderProductEntity> = ArrayList()
                for (i in 0..productList!!.size-1) {
                    if (productList!!.get(i).product_name!!.contains(constraint.toString(),ignoreCase = true)) {
                        val setGetProductDetails = NewOrderProductEntity()
                        setGetProductDetails.id=productList!!.get(i).id
                        setGetProductDetails.product_id=productList!!.get(i).product_id
                        setGetProductDetails.product_name=productList!!.get(i).product_name
                        setGetProductDetails.product_for_gender=productList!!.get(i).product_for_gender
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
            arrayList_product = results.values as ArrayList<NewOrderProductEntity>
            notifyDataSetChanged()
        }
    }
}
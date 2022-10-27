package com.kcteam.features.viewAllOrder.adapter

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.ProductListEntity
import com.kcteam.features.DecimalDigitsInputFilter
import com.kcteam.features.stockAddCurrentStock.AddShopStockFragment
import com.kcteam.features.stockAddCurrentStock.`interface`.ProductListOnClick
import kotlinx.android.synthetic.main.item_new_order_product_size.view.*
import kotlinx.android.synthetic.main.row_add_stock_item_binding.view.*
import kotlinx.android.synthetic.main.row_view_competetor_stock_list.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

class AdapterNewOrderProduct(val context: Context, val productList: ArrayList<ProductListEntity>?, val productListQty:ArrayList<String>?, private val listner: ProductListOnClick): RecyclerView.Adapter<AdapterNewOrderProduct.ProductListViewHolder>() {


   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
      val view = LayoutInflater.from(context).inflate(R.layout.item_new_order_product_size,parent,false)
      return ProductListViewHolder(view)
   }

   override fun getItemCount(): Int {
      return productList!!.size
   }


   @SuppressLint("RecyclerView")
   override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
      holder.tv_size.text=productList!!.get(position).brand

   }

   inner class ProductListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
      val tv_size=itemView.item_new_order_product_sizeTv
      val tv_qty=itemView.et_size_count
   }


}
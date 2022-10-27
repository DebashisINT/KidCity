package com.kcteam.features.stockAddCurrentStock.adapter

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
import kotlinx.android.synthetic.main.row_add_stock_item_binding.view.*
import java.math.RoundingMode
import java.text.DecimalFormat

class AdapterProductList(val context: Context, val productList: ArrayList<ProductListEntity>?, val productListQty:ArrayList<String>?, private val listner: ProductListOnClick): RecyclerView.Adapter<AdapterProductList.ProductListViewHolder>() {


   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
      val view = LayoutInflater.from(context).inflate(R.layout.row_add_stock_item_binding,parent,false)
      return ProductListViewHolder(view)
   }

   override fun getItemCount(): Int {
      return productList!!.size
   }


   @SuppressLint("RecyclerView")
   override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {

      try{
         holder.et_qty.filters=(arrayOf<InputFilter>(DecimalDigitsInputFilter(7, 2)))
      }catch (ex:Exception){

      }


      if (position % 2 == 0)
         holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
      else
         holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

      holder.tv_product_name.text=productList!!.get(position).product_name
      holder.tv_brand.text="Brand: "+productList.get(position).brand
      holder.tv_measure.text="Measurement: "+productList.get(position).watt

      if(productListQty!!.get(position).equals("0.0")){
         holder.et_qty.setText("")
      }else{
         holder.et_qty.setText(productListQty?.get(position).toString())
      }


      holder.et_qty.addTextChangedListener(object :TextWatcher{
         override fun afterTextChanged(p0: Editable?) {

         }

         override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

         }

         override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            try{
               var qty:Double
               if(holder.et_qty.text.toString().length>0){
                  qty=holder.et_qty.text.toString().toDouble()
                  productListQty?.set(holder.adapterPosition,qty.toString())
               }else{
                  qty=0.0
                  productListQty?.set(holder.adapterPosition,qty.toString())
               }
               listner?.productListOnClick(qty,productList.get(holder.adapterPosition).id)
            }catch (ex:Exception){

            }



           /* if(holder.et_qty.text.toString().length>0 && holder.adapterPosition == position){
               productListQty?.set(holder.adapterPosition,holder.et_qty.text.toString())
               //listner?.productListOnClick(productListQty!!,productList.get(position).id)
               var qty:Int=holder.et_qty.text.toString().toInt()
               listner?.productListOnClick(qty,productList.get(holder.adapterPosition).id)
            }else if (holder.adapterPosition == position){
               //productListQty?.set(holder.adapterPosition,"")
               listner?.productListOnClick(0,productList.get(position).id)
            }*/



          /*  if(holder.et_qty.text.toString().length>0){
               var qty:Int=holder.et_qty.text.toString().toInt()
               listner?.productListOnClick(qty,productList.get(position).id)
            }else{
               listner?.productListOnClick(0,productList.get(position).id)
            }*/
         }
      })

   }

   inner class ProductListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
      val tv_product_name = itemView.tv_row_add_stock_product_name
      val tv_brand = itemView.tv_row_add_stock_brand
      val tv_measure = itemView.tv_row_add_stock_measure
      val et_qty = itemView.et_row_add_stock_qty
   }


}
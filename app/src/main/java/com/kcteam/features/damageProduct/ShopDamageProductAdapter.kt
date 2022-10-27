package com.kcteam.features.damageProduct

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.damageProduct.model.Shop_wise_breakage_list
import kotlinx.android.synthetic.main.inflater_breakage_item.view.*

class ShopDamageProductAdapter(private val context: Context, private val selectedProductList: ArrayList<Shop_wise_breakage_list>?, private val listener: ShopDamageProductAdapter.OnClickListener) :
    RecyclerView.Adapter<ShopDamageProductAdapter.MyViewHolder>(){

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflater_breakage_item, parent, false)
        return MyViewHolder(v)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, selectedProductList, listener)
    }

    override fun getItemCount(): Int {
        return selectedProductList!!.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<Shop_wise_breakage_list>?, listener: ShopDamageProductAdapter.OnClickListener) {
         itemView.breakage_date_tv.text= AppUtils.changeLocalDateFormatToAtte(AppUtils.convertDateTimeToCommonFormat(categoryList!!.get(adapterPosition).date_time!!.subSequence(0,10).toString()).toString())
            itemView.breakage_no_tv.text=categoryList!!.get(adapterPosition).breakage_number
            itemView.breakage_product_tv.text=categoryList!!.get(adapterPosition).product_name
            itemView.breakage_type_tv.text=categoryList!!.get(adapterPosition).description_of_breakage

            itemView.tv_breakage_view.setOnClickListener {
                listener.onView(adapterPosition,categoryList!!.get(adapterPosition).breakage_number!!)
            }
            itemView.breakage_share_iv.setOnClickListener {
                listener.onShare(categoryList[adapterPosition])
            }
            itemView.breakage_del_iv.setOnClickListener {
                listener.onDelete(adapterPosition,categoryList!!.get(adapterPosition).breakage_number!!)
            }
        }
    }

    interface OnClickListener {
        fun onView(adapterPosition: Int,breakageId:String)
        fun onShare(obj: Shop_wise_breakage_list)
        fun onDelete(adapterPosition: Int,breakageId:String)
    }
}
package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.NewOrderSizeEntity
import com.kcteam.app.utils.Toaster
import com.kcteam.features.viewAllOrder.interf.SizeListNewOrderOnClick
import kotlinx.android.synthetic.main.item_new_order_product_size.view.*

class NewOrderSizeAdapter(var context: Context, var sizeList: List<NewOrderSizeEntity>, var listner: SizeListNewOrderOnClick):
    RecyclerView.Adapter<NewOrderSizeAdapter.NewOrderSizeViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrderSizeViewHolder {
        val view = layoutInflater.inflate(R.layout.item_new_order_product_size, parent, false)
        return NewOrderSizeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sizeList!!.size
    }

    override fun onBindViewHolder(holder: NewOrderSizeViewHolder, position: Int) {

        //holder.bindItems()

        holder.tv_size.text=sizeList.get(position).size

         holder.et_size.setOnFocusChangeListener({ v, hasFocus ->
            //holder.et_size.setFocusable(true)
            //holder.et_size.requestFocus()

                    if (hasFocus) {
                        //holder.et_size.getSelectionStart()
                        holder.et_size.setBackgroundResource(R.drawable.blue_line_custom_selected)
                        //holder.et_size.setFocusableInTouchMode(true)
                    } else {
                        holder.et_size.setBackgroundResource(R.drawable.blue_line_custom)
                    }
        })

        /* holder.et_size.setOnClickListener{
            for(i in 0..sizeList.size-1){
                if(i==holder.adapterPosition){
                    holder.et_size.setBackgroundDrawable(context.resources.getDrawable(R.drawable.blue_line_custom_selected))
                }else{
                    holder.et_size.setBackgroundDrawable(context.resources.getDrawable(R.drawable.blue_line_custom))
                }
            }
        }*/


        holder.ll_root.setOnClickListener(View.OnClickListener {
           Toaster.msgShort(context, sizeList.get(holder.adapterPosition).size)
       })


    }

    inner class NewOrderSizeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_size = itemView.item_new_order_product_sizeTv
        val et_size = itemView.et_size_count
        val ll_root = itemView.ll_item_new_ord_pr_size_root

        /*fun bindItems() {
            itemView.apply {
                item_new_order_product_sizeTv.text=sizeList.get(adapterPosition).size



                et_size_count.post({
                    et_size_count.requestFocus()
                })

                ll_item_new_ord_pr_size_root.setOnClickListener(View.OnClickListener {
                    Toaster.msgShort(context, sizeList.get(adapterPosition).size)
                })
            }}
    }*/

    }

}
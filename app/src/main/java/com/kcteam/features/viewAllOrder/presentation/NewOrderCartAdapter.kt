package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.features.viewAllOrder.interf.NewOrderorderCount
import com.kcteam.features.viewAllOrder.model.NewOrderCartModel
import com.kcteam.features.viewAllOrder.model.ProductOrder
import kotlinx.android.synthetic.main.row_new_order_cart_details.view.*

class NewOrderCartAdapter(var context: Context,var cartOrderList:List<NewOrderCartModel>,var listner: NewOrderorderCount) :
    RecyclerView.Adapter<NewOrderCartAdapter.NewOrderCartViewHolder>(){

    private var sizw_qty_list:ArrayList<ProductOrder> = ArrayList()

    private var sizeQtyAdapter: OrderSizeQtyDetailsAdapter?=null
    private var sizeQtyDialogAdapter: OrderSizeQtyDetailsDelAdapter?=null

    private lateinit var cartOrderList_Temp:MutableList<NewOrderCartModel>

    init {
        cartOrderList_Temp = cartOrderList as MutableList<NewOrderCartModel>
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrderCartViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.row_new_order_cart_details,parent,false)
        return NewOrderCartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartOrderList!!.size
    }

    override fun onBindViewHolder(holder: NewOrderCartViewHolder, position: Int) {
       /* holder.productName.text=cartOrderList.get(position).product_name.toString()
        holder.gender.text=cartOrderList.get(position).gender.toString()
        holder.color.text="Color : "+cartOrderList.get(position).color_name.toString()

       sizw_qty_list.clear()
        sizw_qty_list.addAll(cartOrderList.get(position).order_list)

        sizeQtyAdapter=OrderSizeQtyDetailsAdapter(context,sizw_qty_list,object : NewOrderSizeQtyDelOnClick {
            override fun sizeQtyListOnClick(product_size_qty: ProductOrder) {
                if(cartOrderList.get(holder.adapterPosition).order_list.size==1){
                    //cartOrderList.toMutableList().removeAt(holder.adapterPosition)
                    cartOrderList_Temp.removeAt(holder.adapterPosition)
                    notifyDataSetChanged()
                    listner.getOrderCount(cartOrderList.size)
                }else{
                    for(i in 0..cartOrderList.get(holder.adapterPosition).order_list.size-1){
                        if(cartOrderList.get(holder.adapterPosition).order_list.get(i) == product_size_qty){
                            cartOrderList.get(holder.adapterPosition).order_list.removeAt(i)
                            break
                        }else{

                        }
                    }
                    //sizeQtyAdapter!!.notifyDataSetChanged()
                    notifyDataSetChanged()
                }
            }
        })


        holder.rv_details.adapter=sizeQtyAdapter

        holder.ll_root_new_or.setOnClickListener(View.OnClickListener {
<<<<<<< HEAD
            ddialog(cartOrderList.get(holder.adapterPosition),holder.adapterPosition,cartOrderList.get(holder.adapterPosition).product_name)
        })

    }

    fun ddialog(cartData:NewOrderCartModel,position:Int,header:String){
        val simpleDialog = Dialog(context)
        simpleDialog.setCancelable(true)
=======
            ddialog(cartOrderList.get(holder.adapterPosition),holder.adapterPosition)
        })*/
    }

    fun ddialog(cartData:NewOrderCartModel,position:Int){
/*
        val simpleDialog = Dialog(context)
        simpleDialog.setCancelable(true)


        simpleDialog.getWindow()!!.setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

>>>>>>> 7c0e894399c7ea84dc22704723fbc8869d18f20d
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_new_order_size_qty)
        val rv_details = simpleDialog.findViewById(R.id.rv_dialog_order_details) as RecyclerView
        val dialog_headerTV =simpleDialog.findViewById(R.id.dialog_order_details_headerTV) as AppCustomTextView

        dialog_headerTV.text = header.toString()
        sizeQtyDialogAdapter= OrderSizeQtyDetailsDelAdapter(context,cartData.order_list,object : NewOrderSizeQtyDelOnClick {
            override fun sizeQtyListOnClick(product_size_qty: ProductOrder) {
                var ass="asdf"
            }
        })

        rv_details.adapter=sizeQtyDialogAdapter

        simpleDialog.show()*/
    }


    inner class NewOrderCartViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val productName=itemView.tv_row_new_order_product_name
        val gender=itemView.tv_row_new_order_gender
        val color=itemView.tv_row_new_order_color
        val rv_details=itemView.rv_order_details
        val ll_root_new_or=itemView.ll_root_new_or

    }

}

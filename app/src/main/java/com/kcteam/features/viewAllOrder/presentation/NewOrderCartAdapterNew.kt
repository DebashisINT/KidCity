package com.kcteam.features.viewAllOrder.presentation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.Toaster
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.viewAllOrder.interf.ColorListOnCLick
import com.kcteam.features.viewAllOrder.interf.EmptyProductOnClick
import com.kcteam.features.viewAllOrder.interf.NewOrderSizeQtyDelOnClick
import com.kcteam.features.viewAllOrder.interf.NewOrderorderCount
import com.kcteam.features.viewAllOrder.model.NewOrderCartModel
import com.kcteam.features.viewAllOrder.model.ProductOrder
import com.kcteam.mappackage.SendBrod
import com.kcteam.widgets.AppCustomTextView
import kotlinx.android.synthetic.main.row_new_order_cart_details_new.view.*
import kotlinx.android.synthetic.main.row_new_order_color_list.view.*
import java.util.logging.Handler

class NewOrderCartAdapterNew(var context: Context,var cartOrderList:List<NewOrderCartModel>,var listner: NewOrderorderCount) :
    RecyclerView.Adapter<NewOrderCartAdapterNew.NewOrderCartViewHolderNew>(){

    private var sizw_qty_list:ArrayList<ProductOrder> = ArrayList()

    private var sizeQtyAdapter: OrderSizeQtyDetailsAdapter?=null
    private var sizeQtyDialogAdapter: OrderSizeQtyDetailsDelAdapter?=null

    private var adapterColorListNewOrder: AdapterColorListNewOrder?=null

    private lateinit var cartOrderList_Temp:MutableList<NewOrderCartModel>

    var mProductName:String=""

    var mGender:String=""

    init {
        cartOrderList_Temp = cartOrderList as MutableList<NewOrderCartModel>
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewOrderCartViewHolderNew {
        val view=LayoutInflater.from(context).inflate(R.layout.row_new_order_cart_details_new,parent,false)
        return NewOrderCartViewHolderNew(view)
    }

    override fun getItemCount(): Int {
        return cartOrderList!!.size
    }

    override fun onBindViewHolder(holder: NewOrderCartViewHolderNew, position: Int) {

        //gender vs product type new order
        //holder.genderOrProductTypeTag.text = context.getString(R.string.GenderTextNewOrd)+" : "
        holder.genderOrProductTypeTag.text = context.getString(R.string.ProductTextNewOrd)+" : "

        holder.productName.text=cartOrderList.get(position).product_name.toString()
        holder.gender.text=cartOrderList.get(position).gender.toString()
        holder.rate.text=cartOrderList.get(position).rate.toString()

        var totalQty = 0
        var colorL = cartOrderList.get(position).color_list
        for(l in 0..colorL.size-1){
            var ordL = colorL.get(l).order_list
            for(m in 0..ordL.size-1){
                totalQty+=ordL.get(m).qty.toInt()
            }
        }
        if(!cartOrderList.get(position).rate.equals(""))
            holder.totalRate.text = (totalQty.toInt() * cartOrderList.get(position).rate.toDouble()).toString()
        else
            holder.totalRate.text = "0.0"


        if(cartOrderList.get(position).rate.toString().equals("0")){
            holder.ll_frag_new_ord_rate_dtls_root.visibility = View.GONE
        }else{
            holder.ll_frag_new_ord_rate_dtls_root.visibility = View.VISIBLE
        }

/*        adapterColorListNewOrder=AdapterColorListNewOrder(context,cartOrderList.get(position).color_list,object: ColorListOnCLick{
            override fun colorListOnCLick(size_qty_list: ArrayList<ProductOrder>, adpPosition: Int) {
                var art=size_qty_list
            }
        })*/
        mProductName = cartOrderList.get(position).product_name
        mGender = cartOrderList.get(position).gender

        adapterColorListNewOrder=AdapterColorListNewOrder(context,cartOrderList.get(position).color_list,mProductName,mGender,object: EmptyProductOnClick {
            override fun emptyProductOnCLick(emptyFound: Boolean) {
                for(i in 0..cartOrderList.size-1){
                    if(cartOrderList.get(i).color_list.size==0){
                        cartOrderList_Temp.removeAt(i)
                        break
                    }
                }
                notifyDataSetChanged()

                //05-11-2021
                   /* if(cartOrderList.size==0){
                        android.os.Handler(Looper.getMainLooper()).postDelayed({
                            (context as DashboardActivity).loadFragment(FragType.NewOrderScrActiFragment, false, "")
                        }, 8000)

                    }*/



            }

            override fun delProductOnCLick(isDel: Boolean) {
                notifyDataSetChanged()
            }
        })




        holder.rv_color_list.adapter=adapterColorListNewOrder

    }





    inner class NewOrderCartViewHolderNew(itemView: View):RecyclerView.ViewHolder(itemView){
        val productName=itemView.tv_row_new_order_product_name_new
        val gender=itemView.tv_row_new_order_gender_new
        val genderOrProductTypeTag=itemView.tv_rownew_ord_cart_dtls_gender_or_productType
        val rate=itemView.tv_row_new_order_rate_new
        val rv_color_list=itemView.rv_color_details_new
        val ll_frag_new_ord_rate_dtls_root=itemView.ll_frag_new_ord_rate_dtls_root
        val totalRate=itemView.tv_row_new_order_total_rate_new

    }


}
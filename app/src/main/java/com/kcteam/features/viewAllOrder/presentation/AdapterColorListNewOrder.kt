package com.kcteam.features.viewAllOrder.presentation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.viewAllOrder.interf.ColorListOnCLick
import com.kcteam.features.viewAllOrder.interf.EmptyProductOnClick
import com.kcteam.features.viewAllOrder.interf.NewOrderSizeQtyDelOnClick
import com.kcteam.features.viewAllOrder.model.ColorList
import com.kcteam.features.viewAllOrder.model.NewOrderCartModel
import com.kcteam.features.viewAllOrder.model.ProductOrder
import com.kcteam.mappackage.SendBrod
import com.kcteam.widgets.AppCustomTextView
import kotlinx.android.synthetic.main.row_new_order_color_list.view.*
import kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.DeclaredMemberIndex

//class AdapterColorListNewOrder(var context: Context,var color_list:ArrayList<ColorList>,var listner: ColorListOnCLick):
class AdapterColorListNewOrder(var context: Context, var color_list: ArrayList<ColorList>,var ProductName:String,var genderName:String,var listner: EmptyProductOnClick) :
        RecyclerView.Adapter<AdapterColorListNewOrder.ColorListNewOrderViewHolder>() {

    var newOrderSizeQtyAdapter: NewOrderSizeQtyAdapter? = null
    private var sizeQtyDialogAdapter: OrderSizeQtyDetailsDelAdapter? = null

    private var tempSizeQty: ArrayList<ProductOrder> = ArrayList()
    private lateinit var value: ProductOrder

    private lateinit var simpleDialog: Dialog




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorListNewOrderViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_new_order_color_list, parent, false)
        return ColorListNewOrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return color_list!!.size
    }

    override fun onBindViewHolder(holder: ColorListNewOrderViewHolder, position: Int) {
        holder.color_name.text = color_list.get(position).color_name.toString()
        newOrderSizeQtyAdapter = NewOrderSizeQtyAdapter(context, color_list.get(position).order_list)
        holder.rv_size_qty.adapter = newOrderSizeQtyAdapter
        if (CustomStatic.IsFromViewNewOdrScr == true) {
            holder.iv_del.visibility = View.GONE
        } else {
            holder.iv_del.visibility = View.VISIBLE
        }

        if (CustomStatic.IsFromViewNewOdrScr == false) {

        }
        holder.ll_color.setOnClickListener(View.OnClickListener { view ->
            if (CustomStatic.IsFromViewNewOdrScr == false) {
                var ast = color_list.get(holder.adapterPosition)
                ddialog(color_list.get(holder.adapterPosition).order_list, holder.adapterPosition, color_list[position].color_name.toString())
            }

        })

        //holder.ll_color.setOnClickListener{listner.colorListOnCLick(color_list.get(holder.adapterPosition).order_list!!,holder.adapterPosition!!)}
    }


    fun ddialog(size_qty_list: ArrayList<ProductOrder>, adpPosition: Int, colorName: String) {
         simpleDialog = Dialog(context)
        simpleDialog.setCancelable(true)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_new_order_size_qty)
        val rv_details = simpleDialog.findViewById(R.id.rv_dialog_order_details) as RecyclerView
        val genderTv = simpleDialog.findViewById(R.id.dialog_order_details_headerGenderTV) as AppCustomTextView
        val ProductTv = simpleDialog.findViewById(R.id.dialog_message_headerProductTV) as AppCustomTextView
        val colorTv = simpleDialog.findViewById(R.id.dialog_message_headerColorTV) as AppCustomTextView

        val genderProductTypeTag = simpleDialog.findViewById(R.id.tv_dialog_new_ord_size_qty_gender_productType) as AppCustomTextView
        //gender vs product type new order
        //holder.genderOrProductTypeTag.text = context.getString(R.string.GenderTextNewOrd)
        genderProductTypeTag.text = context.getString(R.string.ProductTextNewOrd)+" : "

        colorTv.text = colorName
        ProductTv.text = ProductName
        genderTv.text = genderName

        val delNo = simpleDialog.findViewById(R.id.btn_no) as Button
        val del = simpleDialog.findViewById(R.id.btn_delete) as Button
        //val dialog_headerTV =simpleDialog.findViewById(R.id.dialog_order_details_headerTV) as AppCustomTextView

        delNo.setOnClickListener { v: View? ->
            simpleDialog.dismiss()
        }

        sizeQtyDialogAdapter = OrderSizeQtyDetailsDelAdapter(context, size_qty_list, object : NewOrderSizeQtyDelOnClick {
            override fun sizeQtySelListOnClick(product_size_qty: ArrayList<ProductOrder>) {
                del.setOnClickListener { v: View? ->

                    checkConfirmBoxTodelete(product_size_qty,adpPosition)



                  /*  tempSizeQty = ArrayList()
                    for (i in 0..product_size_qty!!.size - 1) {
                        if (product_size_qty[i].isCheckedStatus == false) {
                            tempSizeQty.add(product_size_qty.get(i))
//                              value = color_list.get(adpPosition).order_list.add(product_size_qty[i])
//                             color_list.get(adpPosition).order_list.remove(product_size_qty[i])
                        }
                    }
                    color_list.get(adpPosition).order_list = tempSizeQty*/


//                    color_list.get(adpPosition).order_list.remove(value)
//                    color_list.get(adpPosition).order_list.removeAt(position)
                   /* if (color_list.get(adpPosition).order_list.size == 0) {
                        color_list.removeAt(adpPosition)
                        listner.emptyProductOnCLick(true)
                    }
                    notifyDataSetChanged()
                    simpleDialog.dismiss()*/

                }
            }


            override fun sizeQtyListOnClick(product_size_qty: ProductOrder, position: Int) {
//                del.setOnClickListener { v: View? ->
//                    color_list.get(adpPosition).order_list.remove(product_size_qty)
////                    color_list.get(adpPosition).order_list.removeAt(position)
//                    if(color_list.get(adpPosition).order_list.size==0){
//                        color_list.removeAt(adpPosition)
//                        listner.emptyProductOnCLick(true)
//                    }
//                    notifyDataSetChanged()
//                    simpleDialog.dismiss()
//                }


            }
        })

        rv_details.adapter = sizeQtyDialogAdapter

        simpleDialog.show()
    }

    private fun checkConfirmBoxTodelete(product_size_qty: ArrayList<ProductOrder>,adpPosition: Int) {
        val simpleDDialog = Dialog(context)
        simpleDDialog.setCancelable(false)
        simpleDDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDDialog.setContentView(R.layout.dialog_delete_new_order_confirm_popoup)
        val dialogHeader = simpleDDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
        val dialog_yes_no_headerTV = simpleDDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
        dialogHeader.text = "Are you sure?"
        val dialogYes = simpleDDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
        val dialogNo = simpleDDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView




        dialogYes.setOnClickListener({ view ->
            simpleDialog.dismiss()
            tempSizeQty = ArrayList()
            for (i in 0..product_size_qty!!.size - 1) {
                if (product_size_qty[i].isCheckedStatus == false) {
                    tempSizeQty.add(product_size_qty.get(i))
                }
            }
            color_list.get(adpPosition).order_list = tempSizeQty
            if (color_list.get(adpPosition).order_list.size == 0) {
                color_list.removeAt(adpPosition)
                listner.emptyProductOnCLick(true)
            }
            notifyDataSetChanged()
            simpleDDialog.dismiss()
            showPopup()
        })
        dialogNo.setOnClickListener({ view ->
            simpleDDialog.cancel()

        })
        simpleDDialog.show()

    }

    private fun showPopup() {
        val simpleDialog = Dialog(context)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_message)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
        dialogHeader.text = "Item Deleted Successfully."
        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            CustomStatic.IsNewOrdDeleteConfirmOkClick=true
            simpleDialog.cancel()
            listner.delProductOnCLick(true)
        })
        simpleDialog.show()
        voiceAttendanceMsg("Item Deleted Successfully")

        CustomStatic.IsNewOrdDeleteConfirmOkClick=false

//05-11-2021
        Handler(Looper.getMainLooper()).postDelayed({
            simpleDialog.dismiss()
            listner.delProductOnCLick(true)
        }, 8000)
    }

    inner class ColorListNewOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var color_name = itemView.tv_row_new_order_color_name
        var rv_size_qty = itemView.rv_color_details_size_qty
        var ll_color = itemView.ll_roow_new_order_color_list
        var iv_del = itemView.iv_row_new_order_color_del
    }

    private fun voiceAttendanceMsg(msg: String) {
        if (Pref.isVoiceEnabledForAttendanceSubmit) {
            val speechStatus = (context as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Item Deleted Successfully", "TTS error in converting Text to Speech!");
        }
    }

}
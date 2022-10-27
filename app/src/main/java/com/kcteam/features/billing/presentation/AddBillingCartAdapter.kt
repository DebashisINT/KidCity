package com.kcteam.features.billing.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.kcteam.R
import com.kcteam.app.domain.OrderProductListEntity
import com.kcteam.app.domain.ProductListEntity
import com.kcteam.app.utils.CustomSpecialTextWatcher
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.cart_adapter_body_layout.view.*
import kotlinx.android.synthetic.main.item_right_menu.view.*

/**
 * Created by Saikat on 24-10-2019.
 */
class AddBillingCartAdapter(private val context: Context, private val selectedProductList: List<OrderProductListEntity>?, private val listener: OnProductClickListener) :
        RecyclerView.Adapter<AddBillingCartAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    /*init {
        (context as DashboardActivity).totalPrice.clear()
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_billing_item_new, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, selectedProductList, listener)
    }

    override fun getItemCount(): Int {
        return selectedProductList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: List<OrderProductListEntity>?, listener: OnProductClickListener) {

            try {

                itemView.tv_post_list_item_delete.setOnClickListener {
                    listener.onDelete(adapterPosition)
                }

                itemView.tv_particular_item.text = categoryList?.get(adapterPosition)?.product_name

                if ((context as DashboardActivity).rateList[adapterPosition] >= "1")
                    itemView.et_rate.setText(((context as DashboardActivity).rateList[adapterPosition]/*.toFloat().toInt()*/).toString())
                else
                    itemView.et_rate.setText("")


                itemView.et_rate.addTextChangedListener(CustomSpecialTextWatcher(itemView.et_rate, 5, 2, object : CustomSpecialTextWatcher.GetCustomTextChangeListener {
                    override fun beforeTextChange(text: String) {
                    }

                    override fun customTextChange(text: String) {
                        val rate = text //itemView.et_rate.text.toString().trim()
                        //val finalRate = String.format("%.2f", rate.toFloat())
                        //if (!TextUtils.isEmpty(rate)) {
                            (context as DashboardActivity).rateList[adapterPosition] = rate
                            listener.onEdit(adapterPosition)
                        //}
                    }
                }))

                if ((context as DashboardActivity).qtyList[adapterPosition] >= "1")
                    itemView.et_qty.setText((context as DashboardActivity).qtyList[adapterPosition])
                else
                    itemView.et_qty.setText("")

                itemView.et_qty.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        var qty = itemView.et_qty.text.toString().trim()
                        /*if (qty < "1") {
                            qty = "1"
                            itemView.et_qty.setText(qty)
                        }*/

                        //if (!TextUtils.isEmpty(qty)) {
                            (context as DashboardActivity).qtyList[adapterPosition] = qty
                            listener.onEdit(adapterPosition)
                        //}
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })


                if (adapterPosition != categoryList?.size!! - 1) {
                    itemView.et_qty.imeOptions = EditorInfo.IME_ACTION_NEXT
                } else
                    itemView.et_qty.imeOptions = EditorInfo.IME_ACTION_DONE


                /*itemView.et_rate.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {

                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            itemView.et_qty.requestFocus()
                            itemView.et_rate.clearFocus()
                        }

                        return true
                    }
                })*/


                /*itemView.et_qty.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {

                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            itemView.et_qty.clearFocus()
                            itemView.et_rate.requestFocus()
                        }
                        else if (actionId == EditorInfo.IME_ACTION_DONE) {
                            itemView.et_qty.clearFocus()
                            AppUtils.hideSoftKeyboard(context)
                        }

                        return true
                    }
                })*/

                //(context as DashboardActivity).totalPrice.add(totalPrice.toDouble())

                //(context as DashboardActivity).totalPrice[adapterPosition] = 0.00

            } catch (e: Exception) {
                e.printStackTrace()
            }
            //itemView.iv_check.isSelected = workTypeList?.get(adapterPosition)?.isSelected!!
            //itemView.iv_check.visibility = View.GONE
            /*itemView.setOnClickListener {
                listener.onProductClick(categoryList?.get(adapterPosition), adapterPosition)
            }*/
        }
    }

    interface OnProductClickListener {
        fun onProductClick(brand: ProductListEntity?, adapterPosition: Int, isSelected: Boolean)

        fun onEdit(adapterPosition: Int)

        fun onDelete(adapterPosition: Int)
    }
}
package com.kcteam.features.returnsOrder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.ProductListEntity
import com.kcteam.app.domain.ProductRateEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.CustomSpecialTextWatcher
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.cart_adapter_body_layout.view.*
import kotlinx.android.synthetic.main.item_right_menu.view.*



class CartReturnAdapter(private val context: Context, private val selectedProductList: ArrayList<ProductListEntity>?, private val listener: OnProductClickListener) :
        RecyclerView.Adapter<CartReturnAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_cart_new, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, selectedProductList, listener)
    }

    override fun getItemCount(): Int {
        return selectedProductList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<ProductListEntity>?, listener: OnProductClickListener) {

            var previousQty = ""
            var previousRate = ""

            try {
                itemView.tv_post_list_item_delete.setOnClickListener {
                    listener.onDelete(adapterPosition)
                }
                itemView.tv_particular_item.text = categoryList?.get(adapterPosition)?.product_name
                if (Pref.isRateOnline) {
                    val list = AppUtils.loadSharedPreferencesProductRateList(context)

                    if (AppUtils.stockStatus == 0) {
                        if (Pref.isRateNotEditable && list != null && list.size > 0) {
                            itemView.et_rate.isEnabled = false
                            itemView.et_rate.setText(((context as DashboardActivity).rateList[adapterPosition]))
                        } else {
                            itemView.et_rate.isEnabled = true

                            if ((context as DashboardActivity).rateList[adapterPosition] > "0.00")
                                itemView.et_rate.setText(((context as DashboardActivity).rateList[adapterPosition]/*.toFloat().toInt()*/).toString())
                            else
                                itemView.et_rate.setText("")
                        }
                    } else if (AppUtils.stockStatus == 1) {
                        itemView.et_rate.isEnabled = true

                        if ((context as DashboardActivity).rateList[adapterPosition] > "0.00")
                            itemView.et_rate.setText(((context as DashboardActivity).rateList[adapterPosition]/*.toFloat().toInt()*/).toString())
                        else
                            itemView.et_rate.setText("")
                    }else if (AppUtils.stockStatus == 2){
                        if (Pref.isRateNotEditable && list != null && list.size > 0) {
                            itemView.et_rate.isEnabled = false
                            itemView.et_rate.setText(((context as DashboardActivity).rateList[adapterPosition]))
                        } else {
                            itemView.et_rate.isEnabled = true

                            if ((context as DashboardActivity).rateList[adapterPosition] > "0.00")
                                itemView.et_rate.setText(((context as DashboardActivity).rateList[adapterPosition]/*.toFloat().toInt()*/).toString())
                            else
                                itemView.et_rate.setText("")
                        }
                    }
                }
                else {
                    val list = AppDatabase.getDBInstance()?.productRateDao()?.getAll() as ArrayList<ProductRateEntity>?

                    if (AppUtils.stockStatus == 0) {
                        if (Pref.isRateNotEditable && list != null && list.size > 0) {
                            itemView.et_rate.isEnabled = false
                            itemView.et_rate.setText(((context as DashboardActivity).rateList[adapterPosition]))
                        } else {
                            itemView.et_rate.isEnabled = true

                            if ((context as DashboardActivity).rateList[adapterPosition] > "0.00")
                                itemView.et_rate.setText(((context as DashboardActivity).rateList[adapterPosition]/*.toFloat().toInt()*/).toString())
                            else
                                itemView.et_rate.setText("")
                        }
                    } else if (AppUtils.stockStatus == 1) {
                        itemView.et_rate.isEnabled = true

                        if ((context as DashboardActivity).rateList[adapterPosition] > "0.00")
                            itemView.et_rate.setText(((context as DashboardActivity).rateList[adapterPosition]/*.toFloat().toInt()*/).toString())
                        else
                            itemView.et_rate.setText("")
                    }
                }


                itemView.et_rate.addTextChangedListener(CustomSpecialTextWatcher(itemView.et_rate, 5, 2, object : CustomSpecialTextWatcher.GetCustomTextChangeListener {
                    override fun beforeTextChange(text: String) {
                        previousRate = text
                    }

                    override fun customTextChange(text: String) {
                        val rate = text //itemView.et_rate.text.toString().trim()
                        //val finalRate = String.format("%.2f", rate.toFloat())

                        if (!Pref.isShowAllProduct && AppUtils.stockStatus == 0) {
                            (context as DashboardActivity).rateList[adapterPosition] = rate
                            listener.onEdit(adapterPosition)
                        } else {
                            if (!TextUtils.isEmpty(rate)) {
                                try {
                                    (context as DashboardActivity).rateList[adapterPosition] = rate
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                listener.onEdit(adapterPosition)
                            } else {
                                try {
                                    if (!TextUtils.isEmpty((context as DashboardActivity).rateList[adapterPosition])) {
                                        (context as DashboardActivity).rateList[adapterPosition] = "0.00"
                                        listener.onEdit(adapterPosition)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()

                                    for (i in (context as DashboardActivity).rateList.indices) {
                                        if ((context as DashboardActivity).rateList[i] == previousRate) {
                                            (context as DashboardActivity).rateList[i] = "0.00"
                                            listener.onEdit(i)
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    }
                }))

                if ((context as DashboardActivity).qtyList[adapterPosition] >= "1")
                    itemView.et_qty.setText((context as DashboardActivity).qtyList[adapterPosition])
                else
                    itemView.et_qty.setText("")

                itemView.et_qty.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) {
                        val qty = itemView.et_qty.text.toString().trim()
                        if (!Pref.isShowAllProduct && AppUtils.stockStatus == 0) {
                            (context as DashboardActivity).qtyList[adapterPosition] = qty
                            listener.onEdit(adapterPosition)

                        } else {
                            if (!TextUtils.isEmpty(qty)) {
                                try {
                                    (context as DashboardActivity).qtyList[adapterPosition] = qty
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                listener.onEdit(adapterPosition)
                            } else {
                                try {
                                    if (!TextUtils.isEmpty((context as DashboardActivity).qtyList[adapterPosition])) {
                                        (context as DashboardActivity).qtyList[adapterPosition] = "0"
                                        listener.onEdit(adapterPosition)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    for (i in (context as DashboardActivity).qtyList.indices) {
                                        if ((context as DashboardActivity).qtyList[i] == previousQty) {
                                            (context as DashboardActivity).qtyList[i] = "0"
                                            listener.onEdit(i)
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        previousQty = p0.toString()
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })


                if (adapterPosition != categoryList?.size!! - 1) {
                    itemView.et_qty.imeOptions = EditorInfo.IME_ACTION_NEXT
                } else
                    itemView.et_qty.imeOptions = EditorInfo.IME_ACTION_DONE


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    interface OnProductClickListener {
        fun onProductClick(brand: ProductListEntity?, adapterPosition: Int, isSelected: Boolean)

        fun onEdit(adapterPosition: Int)

        fun onDelete(adapterPosition: Int)
    }
}
package com.kcteam.features.viewAllOrder

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.ProductListEntity
import com.kcteam.app.domain.ProductRateEntity
import com.kcteam.features.login.model.productlistmodel.ProductRateDataModel
import kotlinx.android.synthetic.main.inflate_product_list.view.*
import java.util.HashSet
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 08-11-2018.
 */
class ProductListAdapter(private val context: Context, private val workTypeList: ArrayList<ProductListEntity>?,
                         private val productRateList: ArrayList<ProductRateDataModel>?, private val productRateListDb: ArrayList<ProductRateEntity>?,
                         private val shopId: String, private val listener: OnProductClickListener) : RecyclerView.Adapter<ProductListAdapter.MyViewHolder>(), Filterable {

    private val layoutInflater: LayoutInflater

    private var productList: ArrayList<ProductListEntity>
    private var tempProductList: ArrayList<ProductListEntity>
    private var filteredProductList: ArrayList<ProductListEntity>

    init {
        layoutInflater = LayoutInflater.from(context)

        productList = ArrayList()
        tempProductList = ArrayList()
        filteredProductList = ArrayList()

        productList.addAll(workTypeList!!)
        tempProductList.addAll(workTypeList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_product_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, productList, listener, productRateList, productRateListDb, shopId)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<ProductListEntity>?, listener: OnProductClickListener,
                      productRateList: ArrayList<ProductRateDataModel>?, productRateListDb: ArrayList<ProductRateEntity>?,
                      shopId: String) {

            if (adapterPosition % 2 == 0)
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            itemView.tv_product_list_log_type.text = categoryList?.get(adapterPosition)?.product_name
            //itemView.tv_product_list_log_type.isSelected = true

            val builder = SpannableStringBuilder()
            val str1 = SpannableString("Brand: ")
            str1.setSpan(ForegroundColorSpan(Color.GRAY), 0, str1.length, 0)
            builder.append(str1)

            val str2 = SpannableString(categoryList?.get(adapterPosition)?.brand)
            builder.append(str2)

            itemView.tv_brand.setText(builder, TextView.BufferType.SPANNABLE)

            val builder1 = SpannableStringBuilder()
            val str1_ = SpannableString("Measurement: ")
            str1_.setSpan(ForegroundColorSpan(Color.GRAY), 0, str1_.length, 0)
            builder1.append(str1_)

            val str2_ = SpannableString(categoryList?.get(adapterPosition)?.watt)
            builder1.append(str2_)

            itemView.tv_watt.setText(builder1, TextView.BufferType.SPANNABLE)

            try {
                if (Pref.isRateOnline) {
                    if (productRateList != null) {
                        for (i in productRateList.indices) {

                            if (Pref.isRateNotEditable) {
                                if (productRateList[i].isRateShow)
                                    itemView.tv_rate.visibility = View.VISIBLE
                                else
                                    itemView.tv_rate.visibility = View.GONE
                            }

                            if (productRateList[i].isStockShow)
                                itemView.ll_stock.visibility = View.VISIBLE
                            else
                                itemView.ll_stock.visibility = View.GONE


                            if (productRateList[i].product_id.toInt() == categoryList?.get(adapterPosition)?.id) {
                                itemView.tv_rate.text = "Rate: \u20B9" + productRateList[i].rate

                                if (!TextUtils.isEmpty(productRateList[i].stock_amount)) {
                                    if (productRateList[i].stock_amount.toDouble() >= 0.00) {
                                        itemView.tv_stock.setTextColor(context.resources.getColor(android.R.color.black))
                                        itemView.tv_stock.text = productRateList[i].stock_amount + " (" + productRateList[i].stock_unit + ")"
                                    } else {
                                        if (productRateList[i].stock_amount.toDouble() < 0.00) {
                                            itemView.tv_stock.setTextColor(context.resources.getColor(android.R.color.holo_red_light))
                                            itemView.tv_stock.text = productRateList[i].stock_amount + " (" + productRateList[i].stock_unit + ")"
                                        } /*else
                                    itemView.tv_stock.text = "N.A."*/
                                    }
                                }

                                break
                            }
                        }
                    } else
                        itemView.tv_rate.text = "Rate: ₹0.00"
                } else {
                    if (productRateListDb != null) {
                        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
                        for (i in productRateListDb.indices) {

                            if (Pref.isRateNotEditable) {
                                if (productRateListDb[i].isRateShow!!)
                                    itemView.tv_rate.visibility = View.VISIBLE
                                else
                                    itemView.tv_rate.visibility = View.GONE
                            }

                            if (productRateList != null && productRateList.size > 0) {
                                if (productRateList[i].isStockShow)
                                    itemView.ll_stock.visibility = View.VISIBLE
                                else
                                    itemView.ll_stock.visibility = View.GONE
                            }


                            if (productRateListDb[i].product_id?.toInt() == categoryList?.get(adapterPosition)?.id) {

                                when {
                                    shop.type == "1" -> itemView.tv_rate.text = "Rate: \u20B9" + productRateListDb[i].rate1
                                    shop.type == "2" -> itemView.tv_rate.text = "Rate: \u20B9" + productRateListDb[i].rate2
                                    shop.type == "3" -> itemView.tv_rate.text = "Rate: \u20B9" + productRateListDb[i].rate3
                                    shop.type == "4" -> itemView.tv_rate.text = "Rate: \u20B9" + productRateListDb[i].rate4
                                    shop.type == "5" -> itemView.tv_rate.text = "Rate: \u20B9" + productRateListDb[i].rate5
                                }

//                                if (!TextUtils.isEmpty(productRateListDb[i].stock_amount)) {
//                                    if (productRateListDb[i].stock_amount?.toDouble()!! >= 0.00) {
//                                        itemView.tv_stock.setTextColor(context.resources.getColor(android.R.color.black))
//                                        itemView.tv_stock.text = productRateListDb[i].stock_amount + " (" + productRateListDb[i].stock_unit + ")"
//                                    } else {
//                                        if (productRateListDb[i].stock_amount?.toDouble()!! < 0.00) {
//                                            itemView.tv_stock.setTextColor(context.resources.getColor(android.R.color.holo_red_light))
//                                            itemView.tv_stock.text = productRateListDb[i].stock_amount + " (" + productRateListDb[i].stock_unit + ")"
//                                        } /*else
//                                    itemView.tv_stock.text = "N.A."*/
//                                    }
//                                }

                                if (productRateList != null && productRateList.size > 0) {
                                    if (!TextUtils.isEmpty(productRateList[i].stock_amount)) {
                                        if (productRateList[i].stock_amount.toDouble() >= 0.00) {
                                            itemView.tv_stock.setTextColor(context.resources.getColor(android.R.color.black))
                                            itemView.tv_stock.text = productRateList?.get(i)?.stock_amount + " (" + productRateList?.get(i)?.stock_unit + ")"
                                        } else {
                                            if (productRateList[i].stock_amount.toDouble() < 0.00) {
                                                itemView.tv_stock.setTextColor(context.resources.getColor(android.R.color.holo_red_light))
                                                itemView.tv_stock.text = productRateList?.get(i)?.stock_amount + " (" + productRateList?.get(i)?.stock_unit + ")"
                                            } /*else
                                    itemView.tv_stock.text = "N.A."*/
                                        }
                                    }
                                }

                                break
                            }
                        }
                    } else {
                        itemView.tv_rate.text = "Rate: ₹0.00"

                        if (productRateList != null) {
                            for (i in productRateList.indices) {

                                if (productRateList[i].isStockShow)
                                    itemView.ll_stock.visibility = View.VISIBLE
                                else
                                    itemView.ll_stock.visibility = View.GONE

                                if (productRateList[i].product_id.toInt() == categoryList?.get(adapterPosition)?.id) {

                                    if (!TextUtils.isEmpty(productRateList[i].stock_amount)) {
                                        if (productRateList[i].stock_amount.toDouble() >= 0.00) {
                                            itemView.tv_stock.setTextColor(context.resources.getColor(android.R.color.black))
                                            itemView.tv_stock.text = productRateList[i].stock_amount + " (" + productRateList[i].stock_unit + ")"
                                        } else {
                                            if (productRateList[i].stock_amount.toDouble() < 0.00) {
                                                itemView.tv_stock.setTextColor(context.resources.getColor(android.R.color.holo_red_light))
                                                itemView.tv_stock.text = productRateList[i].stock_amount + " (" + productRateList[i].stock_unit + ")"
                                            } /*else
                                    itemView.tv_stock.text = "N.A."*/
                                        }
                                    }
                                    break
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                itemView.tv_rate.text = "Rate: ₹0.00"
            }

            itemView./*tv_add_to_cart.*/setOnClickListener {
                listener.onProductClick(categoryList?.get(adapterPosition), adapterPosition)
            }
        }
    }

    interface OnProductClickListener {
        fun onProductClick(brand: ProductListEntity?, adapterPosition: Int)
    }

    fun updateList(mProductList: ArrayList<ProductListEntity>) {
        productList.clear()
        productList.addAll(mProductList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filteredProductList.clear()

            tempProductList.indices
                    .filter { tempProductList[it].product_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filteredProductList.add(tempProductList[it]) }

            results.values = filteredProductList
            results.count = filteredProductList.size

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filteredProductList = results?.values as ArrayList<ProductListEntity>
                productList.clear()
                val hashSet = HashSet<String>()
                if (filteredProductList != null) {

                    filteredProductList.indices
                            .filter { hashSet.add(filteredProductList[it].id.toString()) }
                            .forEach { productList.add(filteredProductList[it]) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
package com.kcteam.features.activities.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.features.activities.model.ProductListModel
import kotlinx.android.synthetic.main.inflate_product_item.view.*
import java.util.HashSet
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 09-01-2020.
 */
class AddChemistSampleAdapter(private val context: Context, productList: ArrayList<ProductListModel>?, private val listener: OnProductClickListener) :
        RecyclerView.Adapter<AddChemistSampleAdapter.MyViewHolder>(), Filterable {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    private var orginalProductList: ArrayList<ProductListModel>? = null
    private var tempProductList: ArrayList<ProductListModel>? = null
    private var filteredProductList: ArrayList<ProductListModel>? = null

    init {
        orginalProductList = ArrayList()
        orginalProductList?.addAll(productList!!)

        tempProductList = ArrayList()
        tempProductList?.addAll(productList!!)

        filteredProductList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_product_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, listener, orginalProductList)
    }

    override fun getItemCount(): Int {
        return orginalProductList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, listener: OnProductClickListener, orginalProductList: ArrayList<ProductListModel>?) {

            if (adapterPosition == orginalProductList?.size!! - 1)
                itemView.product_view.visibility = View.GONE
            else
                itemView.product_view.visibility = View.VISIBLE

            itemView.iv_check.setOnClickListener {
                if (orginalProductList[adapterPosition].isChecked)
                    orginalProductList[adapterPosition].isChecked = false
                else
                    orginalProductList[adapterPosition].isChecked = true

                listener.onCheckClick(orginalProductList[adapterPosition], orginalProductList[adapterPosition].isChecked)
            }

            itemView.iv_check.isSelected = orginalProductList[adapterPosition].isChecked

            itemView.tv_product_name.text = orginalProductList[adapterPosition].product_name
        }
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filteredProductList?.clear()

            tempProductList?.indices!!
                    .filter { tempProductList?.get(it)?.product_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filteredProductList?.add(tempProductList?.get(it)!!) }

            results.values = filteredProductList
            results.count = filteredProductList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filteredProductList = results?.values as ArrayList<ProductListModel>?
                orginalProductList?.clear()
                val hashSet = HashSet<String>()
                if (filteredProductList != null) {


                    filteredProductList?.indices!!
                            .filter { hashSet.add(filteredProductList?.get(it)?.id.toString()) }
                            .forEach { orginalProductList?.add(filteredProductList?.get(it)!!) }

                    if (orginalProductList!!.size > 0)
                        listener.showList(true)
                    else
                        listener.showList(false)

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    interface OnProductClickListener {
        fun onCheckClick(product: ProductListModel, isSelected: Boolean)

        fun showList(isShowList: Boolean)
    }
}
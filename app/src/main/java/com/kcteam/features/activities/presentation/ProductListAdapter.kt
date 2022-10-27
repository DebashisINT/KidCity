package com.kcteam.features.activities.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.ProductListEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

class ProductListAdapter(private val context: Context, productList: ArrayList<ProductListEntity>?,
                         private val onItemClick: (ProductListEntity) -> Unit?) : RecyclerView.Adapter<ProductListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mProductList: ArrayList<ProductListEntity>? = null
    private var tempProductList: ArrayList<ProductListEntity>? = null
    private var filterProductList: ArrayList<ProductListEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mProductList = ArrayList()
        mProductList?.addAll(productList!!)

        tempProductList = ArrayList()
        filterProductList = ArrayList()

        tempProductList?.addAll(productList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mProductList?.get(position)?.product_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mProductList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mProductList?.get(adapterPosition)!!)
            }
        }
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterProductList?.clear()

            tempProductList?.indices!!
                    .filter { tempProductList?.get(it)?.product_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterProductList?.add(tempProductList?.get(it)!!) }

            results.values = filterProductList
            results.count = filterProductList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterProductList = results?.values as ArrayList<ProductListEntity>?
                mProductList?.clear()
                val hashSet = HashSet<String>()
                if (filterProductList != null) {

                    filterProductList?.indices!!
                            .filter { hashSet.add(filterProductList?.get(it)?.id?.toString()!!) }
                            .forEach { mProductList?.add(filterProductList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
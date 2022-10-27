package com.kcteam.features.viewAllOrder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.ProductListEntity
import kotlinx.android.synthetic.main.inflate_vehicle_log_type.view.*
import java.util.HashSet
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 08-11-2018.
 */
class CategoryListAdapter(private val context: Context, private val workTypeList: ArrayList<ProductListEntity>?, private val listener: OnCategoryClickListener) :
        RecyclerView.Adapter<CategoryListAdapter.MyViewHolder>(), Filterable {

    private var layoutInflater: LayoutInflater? = null
    private var categoryList: ArrayList<ProductListEntity>? = null
    private var tempCategoryList: ArrayList<ProductListEntity>? = null
    private var filterCategoryList: ArrayList<ProductListEntity>? = null

    init {
        categoryList = ArrayList<ProductListEntity>()
        tempCategoryList = ArrayList()
        filterCategoryList = ArrayList()

        categoryList?.addAll(workTypeList!!)
        tempCategoryList?.addAll(workTypeList!!)

        layoutInflater = LayoutInflater.from(context)
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater?.inflate(R.layout.inflate_vehicle_log_type, parent, false)
        return MyViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, categoryList, listener)
    }

    override fun getItemCount(): Int {
        return categoryList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<ProductListEntity>?, listener: OnCategoryClickListener) {

            itemView.tv_log_type.text = categoryList?.get(adapterPosition)?.category
            //itemView.iv_check.isSelected = workTypeList?.get(adapterPosition)?.isSelected!!
            itemView.iv_check.visibility = View.GONE
            itemView.setOnClickListener {
                listener.onCategoryClick(categoryList?.get(adapterPosition), adapterPosition)
            }
        }
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(category: ProductListEntity?, adapterPosition: Int)
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterCategoryList?.clear()

            /*for (i in tempCategoryList?.indices!!){
                if (tempCategoryList?.get(i)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!) {
                    filterCategoryList?.add(tempCategoryList?.get(i)!!)
                }
            }

            tempCategoryList?.indices!!
                    .asSequence()
                    .filter { tempCategoryList?.get(it)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterCategoryList?.add(tempCategoryList?.get(it)!!) }*/

            tempCategoryList?.indices!!
                    .filter { tempCategoryList?.get(it)?.category?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterCategoryList?.add(tempCategoryList?.get(it)!!) }

            results.values = filterCategoryList
            results.count = filterCategoryList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterCategoryList = results?.values as ArrayList<ProductListEntity>?
                categoryList?.clear()
                val hashSet = HashSet<String>()
                if (filterCategoryList != null) {

                    /*for (i in filterCategoryList?.indices!!) {
                        if (hashSet.add(filterCategoryList?.get(i)!!))
                            categoryList?.add(filterCategoryList?.get(i)!!)
                    }

                    filterCategoryList?.indices!!
                            .asSequence()
                            .filter { hashSet.add(filterCategoryList?.get(it)!!) }
                            .forEach { categoryList?.add(filterCategoryList?.get(it)!!) }*/

                    filterCategoryList?.indices!!
                            .filter { hashSet.add(filterCategoryList?.get(it)?.category!!) }
                            .forEach { categoryList?.add(filterCategoryList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
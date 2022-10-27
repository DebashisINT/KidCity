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
class BrandListAdapter(private val context: Context, private val workTypeList: ArrayList<ProductListEntity>, private val listener: OnBrandClickListener) :
        RecyclerView.Adapter<BrandListAdapter.MyViewHolder>(), Filterable {

    private var layoutInflater: LayoutInflater? = null
    private var brandList: ArrayList<ProductListEntity>? = null
    private var tempBrandList: ArrayList<ProductListEntity>? = null
    private var filterBrandList: ArrayList<ProductListEntity>? = null

    init {
        brandList = ArrayList<ProductListEntity>()
        tempBrandList = ArrayList()
        filterBrandList = ArrayList()

        brandList?.addAll(workTypeList!!)
        tempBrandList?.addAll(workTypeList!!)

        layoutInflater = LayoutInflater.from(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater?.inflate(R.layout.inflate_vehicle_log_type, parent, false)
        return MyViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, brandList, listener)
    }

    override fun getItemCount(): Int {
        return brandList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<ProductListEntity>?, listener: OnBrandClickListener) {

            itemView.tv_log_type.text = categoryList?.get(adapterPosition)?.brand?.toUpperCase()
            itemView.tv_log_type
            //itemView.iv_check.isSelected = workTypeList?.get(adapterPosition)?.isSelected!!
            itemView.iv_check.visibility = View.GONE
            itemView.setOnClickListener {
                listener.onBrandClick(categoryList?.get(adapterPosition), adapterPosition)
            }
        }
    }

    interface OnBrandClickListener {
        fun onBrandClick(brand: ProductListEntity?, adapterPosition: Int)
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterBrandList?.clear()

            /*for (i in tempBrandList?.indices!!){
                if (tempBrandList?.get(i)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!) {
                    filterBrandList?.add(tempBrandList?.get(i)!!)
                }
            }

            tempBrandList?.indices!!
                    .asSequence()
                    .filter { tempBrandList?.get(it)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterBrandList?.add(tempBrandList?.get(it)!!) }*/

            tempBrandList?.indices!!
                    .filter { tempBrandList?.get(it)?.brand?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterBrandList?.add(tempBrandList?.get(it)!!) }

            results.values = filterBrandList
            results.count = filterBrandList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterBrandList = results?.values as ArrayList<ProductListEntity>?
                brandList?.clear()
                val hashSet = HashSet<String>()
                if (filterBrandList != null) {

                    /*for (i in filterBrandList?.indices!!) {
                        if (hashSet.add(filterBrandList?.get(i)!!))
                            brandList?.add(filterBrandList?.get(i)!!)
                    }

                    filterBrandList?.indices!!
                            .asSequence()
                            .filter { hashSet.add(filterBrandList?.get(it)!!) }
                            .forEach { brandList?.add(filterBrandList?.get(it)!!) }*/

                    filterBrandList?.indices!!
                            .filter { hashSet.add(filterBrandList?.get(it)?.brand!!) }
                            .forEach { brandList?.add(filterBrandList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    }
}
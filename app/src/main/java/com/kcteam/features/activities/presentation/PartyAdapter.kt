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
import com.kcteam.app.domain.TypeListEntity
import com.kcteam.features.addshop.presentation.TypeAdapter
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

class PartyAdapter(private val context: Context, shopList: ArrayList<AddShopDBModelEntity>?,
                   private val onItemClick: (AddShopDBModelEntity) -> Unit?) : RecyclerView.Adapter<PartyAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mShopList: ArrayList<AddShopDBModelEntity>? = null
    private var tempShopList: ArrayList<AddShopDBModelEntity>? = null
    private var filterShopList: ArrayList<AddShopDBModelEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mShopList = ArrayList()
        mShopList?.addAll(shopList!!)

        tempShopList = ArrayList()
        filterShopList = ArrayList()

        tempShopList?.addAll(shopList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mShopList?.get(position)?.shopName
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mShopList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mShopList?.get(adapterPosition)!!)
            }
        }
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterShopList?.clear()

            tempShopList?.indices!!
                    .filter { tempShopList?.get(it)?.shopName?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterShopList?.add(tempShopList?.get(it)!!) }

            results.values = filterShopList
            results.count = filterShopList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterShopList = results?.values as ArrayList<AddShopDBModelEntity>?
                mShopList?.clear()
                val hashSet = HashSet<String>()
                if (filterShopList != null) {

                    filterShopList?.indices!!
                            .filter { hashSet.add(filterShopList?.get(it)?.type_id!!) }
                            .forEach { mShopList?.add(filterShopList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
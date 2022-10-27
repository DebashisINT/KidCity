package com.kcteam.features.myjobs.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.features.myjobs.model.CustomerDataModel
import kotlinx.android.synthetic.main.inflate_customer_item.view.*

class CustomerListAdapter(private val mContext: Context, customerList: ArrayList<CustomerDataModel>, private val getSize: (Int) -> Unit) :
        RecyclerView.Adapter<CustomerListAdapter.MyViewHolder>(), Filterable {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    private var mList: ArrayList<CustomerDataModel>? = null
    private var tempList: ArrayList<CustomerDataModel>? = null
    private var filterList: ArrayList<CustomerDataModel>? = null
    
    init {
        mList = ArrayList()
        tempList = ArrayList()
        filterList = ArrayList()

        mList?.addAll(customerList)
        tempList?.addAll(customerList)
    }
    
    
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_customer_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList?.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                tv_cust_address.text = mList?.get(adapterPosition)?.address
                tv_shop_contact_no.text = mList?.get(adapterPosition)?.contact_no
                tv_cust_name.text = mList?.get(adapterPosition)?.name

                val drawable = TextDrawable.builder()
                        .buildRoundRect(mList?.get(adapterPosition)?.name?.trim()?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

                shop_IV.setImageDrawable(drawable)

                tv_job_code.text = mList?.get(adapterPosition)?.job_code
                tv_service_for.text = mList?.get(adapterPosition)?.service_for
                tv_total_service.text = mList?.get(adapterPosition)?.total_service
                tv_service_frequency.text = mList?.get(adapterPosition)?.service_frequency
                tv_total_service_committed.text = mList?.get(adapterPosition)?.total_service_commited
                tv_last_service_committed.text = mList?.get(adapterPosition)?.last_service_committed
                tv_total_service_pending.text = mList?.get(adapterPosition)?.total_service_pending
            }
        }
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterList?.clear()

            tempList?.indices!!
                    .filter { tempList?.get(it)?.name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterList?.add(tempList?.get(it)!!) }

            results.values = filterList
            results.count = filterList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterList = results?.values as ArrayList<CustomerDataModel>?
                mList?.clear()
                val hashSet = HashSet<String>()
                if (filterList != null) {

                    filterList?.indices!!
                            .filter { hashSet.add(filterList?.get(it)?.id!!) }
                            .forEach { mList?.add(filterList?.get(it)!!) }

                    getSize(mList?.size!!)

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshList(list: ArrayList<CustomerDataModel>) {
        mList?.clear()
        mList?.addAll(list)

        tempList?.clear()
        tempList?.addAll(list)

        if (filterList == null)
            filterList = ArrayList()
        filterList?.clear()

        notifyDataSetChanged()
    }
}
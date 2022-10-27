package com.kcteam.features.NewQuotation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.NewOrderGenderEntity
import com.kcteam.app.domain.NewOrderProductEntity
import com.kcteam.features.NewQuotation.interfaces.SalesmanOnClick
import com.kcteam.features.NewQuotation.interfaces.TaxOnclick
import com.kcteam.features.member.model.TeamListDataModel
import com.kcteam.features.viewAllOrder.interf.GenderListOnClick
import com.kcteam.features.viewAllOrder.presentation.ProductListNewOrderAdapter
import kotlinx.android.synthetic.main.row_dialog_new_order_gender.view.*
import kotlinx.android.synthetic.main.row_dialog_new_order_gender.view.tv_row_dialog_new_order_gender
import kotlinx.android.synthetic.main.row_dialog_tax.view.*

class MemberSalesmanListAdapter(private var context:Context, private var msalesmanList: ArrayList<TeamListDataModel>, private val listner: SalesmanOnClick):
  RecyclerView.Adapter<MemberSalesmanListAdapter.GenderListViewHolder>(), Filterable {
    private var arrayList_bean: ArrayList<TeamListDataModel>? = ArrayList()
    private var arrayList_product: ArrayList<TeamListDataModel>? = ArrayList()
    private var valueFilter: MemberSalesmanListAdapter.ValueFilter? = null

    init {
        arrayList_bean?.addAll(msalesmanList)
        arrayList_product?.addAll(msalesmanList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenderListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_dialog_tax,parent,false)
        return GenderListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList_product!!.size!!
    }

    override fun getFilter(): Filter {
        if (valueFilter == null) {
            valueFilter = ValueFilter()
        }
        return valueFilter as MemberSalesmanListAdapter.ValueFilter
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filterResults = FilterResults()
            if (constraint != null && constraint.length > 0) {
                val arrayList_filter: ArrayList<TeamListDataModel> = ArrayList()
                for (i in 0..msalesmanList!!.size-1) {
                    if (msalesmanList!!.get(i).user_name!!.contains(constraint.toString(),ignoreCase = true)) {
                        val setGetSalesUserDetails = TeamListDataModel()
                        setGetSalesUserDetails.user_name=msalesmanList!!.get(i).user_name
                        setGetSalesUserDetails.user_id=msalesmanList!!.get(i).user_id
                        arrayList_filter.add(setGetSalesUserDetails)
                    }
                }
                filterResults.count = arrayList_filter!!.size
                filterResults.values = arrayList_filter
            } else {
                filterResults.count = arrayList_bean!!.size
                filterResults.values = arrayList_bean
            }
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            arrayList_product = results.values as ArrayList<TeamListDataModel>
            notifyDataSetChanged()
        }
    }


    override fun onBindViewHolder(holder: GenderListViewHolder, position: Int) {
        holder.tv_tax.text=arrayList_product!!.get(position).user_name!!
        holder.cv_tax.setOnClickListener { listner?.OnClick(arrayList_product!!.get(holder.adapterPosition)) }
    }

    inner class GenderListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tv_tax = itemView.tv_row_dialog_tax
        val cv_tax = itemView.cv_tax
    }

}


package com.kcteam.features.lead.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.features.NewQuotation.adapter.MemberSalesmanListAdapter
import com.kcteam.features.lead.model.CustomerLeadList
import com.kcteam.features.member.model.TeamListDataModel
import com.kcteam.features.photoReg.model.UserListResponseModel
import kotlinx.android.synthetic.main.inflate_registered_shops.view.*
import kotlinx.android.synthetic.main.row_customer_lead_list.view.*
import kotlinx.android.synthetic.main.row_new_quot_added_prod.view.*

class CustomerLeadAdapter(var mContext:Context,var list:ArrayList<CustomerLeadList>,private val listener: OnPendingLeadClickListener,private val getSize: (Int) -> Unit) :
   RecyclerView.Adapter<CustomerLeadAdapter.CustomerLeadViewHolder>(), Filterable {
    private var mList: ArrayList<CustomerLeadList>? = null
    private var tempList: ArrayList<CustomerLeadList>? = null
    private var filterList: ArrayList<CustomerLeadList>? = null

    init {
        mList = ArrayList()
        tempList = ArrayList()
        filterList = ArrayList()

        mList?.addAll(list)
        tempList?.addAll(list)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerLeadViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.row_customer_lead_list, parent, false)
        return CustomerLeadViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    override fun onBindViewHolder(holder: CustomerLeadViewHolder, position: Int) {

        val drawable = TextDrawable.builder().buildRoundRect(mList!!.get(position).customer_name.trim().toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

        holder.imageShop.setImageDrawable(drawable)
        holder.shopName.text=mList!!.get(position).customer_name
        holder.shopAdd.text=mList!!.get(position).customer_addr
        holder.shopPhone.text=mList!!.get(position).mobile_no
        holder.shopSource.text=mList!!.get(position).source_vend_type
        holder.shopTime.text=mList!!.get(position).time
        holder.shopDate.text=mList!!.get(position).date
        holder.email.text=mList!!.get(position).email

        holder.productReq.text=mList!!.get(position).product_req
        holder.qty.text=mList!!.get(position).qty
        holder.uom.text=mList!!.get(position).UOM
        holder.order_values.text=mList!!.get(position).order_value
        holder.enquiry_dtls.text=mList!!.get(position).enquiry_details
        if(mList!!.get(position).status.toUpperCase().equals("PENDING")){
            holder.status.text="Assigned"
        }else{
            holder.status.text=mList!!.get(position).status
        }

        holder.iv_activity.setOnClickListener {listener.onActivityClick(mList!!.get(holder.adapterPosition))  }

        holder.shopPhone.setOnClickListener {listener.onPhoneClick(mList!!.get(holder.adapterPosition))  }

    }

    inner class CustomerLeadViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var imageShop = itemView.row_cutomer_lead_list_ivImage
        var updateLL = itemView.row_cutomer_lead_list_ll_update
        var shopName = itemView.row_cutomer_lead_list_ShopNameTV
        var shopAdd = itemView.row_cutomer_lead_list_Shopaddress_TV
        var shopPhone = itemView.row_cutomer_lead_list_Shopcontact_no
        var shopSource= itemView.row_cutomer_lead_list_tv_source
        var shopTime = itemView.row_cutomer_lead_list_tv_time
        var shopDate = itemView.row_cutomer_lead_list_tv_date
        var email = itemView.row_cutomer_lead_list_tv_email
        var productReq = itemView.row_cutomer_lead_list_tv_productReq
        var qty = itemView.row_cutomer_lead_list_tv_qty
        var uom = itemView.row_cutomer_lead_list_tv_uom
        var order_values = itemView.row_cutomer_lead_list_tv_order_values
        var enquiry_dtls = itemView.row_cutomer_lead_list_tv_enqury_dts
        var status = itemView.row_cutomer_lead_list_tv_status

        var iv_activity = itemView.row_cutomer_lead_list_iv_update



    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterList?.clear()

            tempList?.indices!!
                    .filter { tempList?.get(it)?.customer_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!
                            || tempList?.get(it)?.email?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! ||
                            tempList?.get(it)?.enquiry_details?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! ||
                            tempList?.get(it)?.mobile_no?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! ||
                            tempList?.get(it)?.enquiry_details?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! ||
                            tempList?.get(it)?.product_req?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! ||
                            tempList?.get(it)?.status?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! ||
                            tempList?.get(it)?.customer_addr?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!}

                    .forEach { filterList?.add(tempList?.get(it)!!) }

            results.values = filterList
            results.count = filterList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterList = results?.values as ArrayList<CustomerLeadList>?
                mList?.clear()
                val hashSet = HashSet<String>()
                if (filterList != null) {

                    filterList?.indices!!
                            .filter { hashSet.add(filterList?.get(it)?.customer_name!!) }
                            .forEach { mList?.add(filterList?.get(it)!!) }

                    getSize(mList?.size!!)

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshList(list: ArrayList<CustomerLeadList>) {
        mList?.clear()
        mList?.addAll(list)

        tempList?.clear()
        tempList?.addAll(list)

        if (filterList == null)
            filterList = ArrayList()
        filterList?.clear()

        notifyDataSetChanged()
    }


    interface OnPendingLeadClickListener {
        fun onActivityClick(obj:CustomerLeadList)
        fun onPhoneClick(obj:CustomerLeadList)
    }


}
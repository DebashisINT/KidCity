package com.kcteam.features.myjobs.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.myjobs.model.CustomerDataModel
import com.kcteam.features.nearbyuserlist.model.NearbyUserDataModel
import kotlinx.android.synthetic.main.inflate_job_cust_item.view.*

class MyJobAdapter(private val mContext: Context, private val customerList: ArrayList<CustomerDataModel>,
                   private val onDetailsClick: (CustomerDataModel) -> Unit,
                   private val onMapClick: (CustomerDataModel) -> Unit) : RecyclerView.Adapter<MyJobAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_job_cust_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return customerList.size
//        return 10
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                tv_cust_name.text=customerList[adapterPosition].name
                tv_cust_address.text=customerList[adapterPosition].address
                tv_shop_contact_no.text=customerList[adapterPosition].contact_person+"("+customerList[adapterPosition].contact_no+")"
                tv_status.text=customerList[adapterPosition].status
                tv_view_details.setOnClickListener {
                    onDetailsClick(customerList[adapterPosition])
                }

                iv_map_icon.setOnClickListener {
                    onMapClick(customerList[adapterPosition])
                }
            }
        }
    }
}
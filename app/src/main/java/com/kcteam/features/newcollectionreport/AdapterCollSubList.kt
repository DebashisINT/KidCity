package com.kcteam.features.newcollectionreport

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import kotlinx.android.synthetic.main.inflater_collect_pend_dtls_list_item.view.*
import kotlinx.android.synthetic.main.row_coll_sub_list.view.*

class AdapterCollSubList(mContext: Context, list: List<CollectionList>):RecyclerView.Adapter<AdapterCollSubList.MyViewHolder>() {

    private var mList: ArrayList<CollectionList>? = null

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    init {
        mList = ArrayList()
        mList!!.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.row_coll_sub_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun getItemCount(): Int {
        return mList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems() {
            itemView.apply {
                tv_row_coll_sub_coll_dt.text=mList!!.get(adapterPosition).coll_date
                tv_row_coll_sub_coll_amt.text=String.format("%.2f",mList!!.get(adapterPosition).coll_amt.toDouble())

            }

        }
    }

}
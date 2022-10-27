package com.kcteam.features.newcollectionreport

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.CollectionDetailsEntity
import com.kcteam.features.photoReg.adapter.PhotoAttendanceListner
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.*
import kotlinx.android.synthetic.main.inflater_collect_pend_list_item.view.*


class CollectionPendingListAdapter(mContext: Context, list: List<PendingCollData>,val listner: PendingCollListner) :
        RecyclerView.Adapter<CollectionPendingListAdapter.MyViewHolder>() {

    private var mList: ArrayList<PendingCollData>? = null

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    init {
        mList = ArrayList()
        mList!!.addAll(list)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflater_collect_pend_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList!!.size!!
    }

   inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems() {
            itemView.apply {
                tv_inf_coll_pendi_list_shop_name.text = mList!!.get(adapterPosition).shopName
                tv_inf_coll_pendi_list_due_amt.text = mList!!.get(adapterPosition).dueAmt
                iv_inf_coll_pendi_list_view.setOnClickListener { listner?.getUserInfoOnLick(mList!!.get(adapterPosition)) }
            }

        }
    }

}
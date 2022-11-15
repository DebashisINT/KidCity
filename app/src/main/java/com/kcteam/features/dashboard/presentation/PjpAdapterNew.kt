package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.PjpListEntity
import kotlinx.android.synthetic.main.inflate_user_pjp_item.view.*

class PjpAdapterNew(var mContext: Context,var list: ArrayList<PjpListEntity>,var listener: PJPClickListner):
RecyclerView.Adapter<PjpAdapterNew.MyViewHolder>(){

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    private var mList: ArrayList<PjpListEntity>? = null

    init {
        mList = ArrayList()
        mList?.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_user_pjp_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItems(){
            itemView.apply {
                tv_pjp_name.text = mList?.get(adapterPosition)?.customer_name
                println("pjp_tag inside adapter_pos -  $adapterPosition ");
            }}
    }

}
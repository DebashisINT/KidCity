package com.kcteam.features.orderhistory

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.orderhistory.model.ConsolidatedCount
import kotlinx.android.synthetic.main.inflate_consolidated_shop_count.view.*


/**
 * Created by sayantan.sarkar on 1/11/17.
 */
class ConsolidatedAdapter(context: Context,userLocationDataEntity: List<ConsolidatedCount>) : RecyclerView.Adapter<ConsolidatedAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<ConsolidatedCount>

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.userLocationDataEntity=userLocationDataEntity
    }

    override fun onBindViewHolder(holder: ConsolidatedAdapter.MyViewHolder, position: Int) {
        holder.bindItems(context,userLocationDataEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_consolidated_shop_count, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<ConsolidatedCount>) {
            itemView.total_shop_visited_TV.text=userLocationDataEntity[adapterPosition].displayName
            itemView.total_shop_TV.text=userLocationDataEntity[adapterPosition].displayValue
        }
    }
    open fun updateList(newlist: List<ConsolidatedCount>) {
        userLocationDataEntity = newlist
        notifyDataSetChanged()
    }
}

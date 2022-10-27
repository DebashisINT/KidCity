package com.kcteam.features.achievement

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.kcteam.R

class CustomRecyclerViewAdapter(private val context: Context, private val performanceType: String) : RecyclerView.Adapter<CustomRecyclerViewAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_content_layout, parent, false)
        return ItemViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        if (performanceType.equals("Visit", ignoreCase = true))
            holder.tv_order_taken.text = "2"
        else if (performanceType.equals("Revisit", ignoreCase = true))
            holder.tv_order_taken.text = "3"
        else
            holder.tv_order_taken.text = context.getString(R.string.rupee_symbol_with_space) + " 5,000"
    }


    override fun getItemCount(): Int {
        return 10
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_date: TextView
        var tv_order_taken: TextView

        init {
            tv_date = itemView.findViewById<View>(R.id.tv_date_rv_item) as TextView
            tv_order_taken = itemView.findViewById<View>(R.id.tv_order_taken_rv_item) as TextView
        }
    }
}



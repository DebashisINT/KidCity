package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 11-01-2019.
 */
class SuggestionShopListAdapter(private val context: Context, private val shopList: ArrayList<String>) : RecyclerView.Adapter<SuggestionShopListAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.inflate_suggestion_shop_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = shopList[position]

        if (position == shopList.size - 1) {
            holder.view.visibility = View.GONE
        } else
            holder.view.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView
        var view: View

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)
            tv_phn_no.visibility = View.GONE
            view = itemView.findViewById(R.id.view)
        }
    }

}
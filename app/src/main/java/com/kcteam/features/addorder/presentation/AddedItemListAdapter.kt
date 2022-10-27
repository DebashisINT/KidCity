package com.kcteam.features.addorder.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import kotlinx.android.synthetic.main.inflate_add_item.view.*

/**
 * Created by Pratishruti on 30-10-2017.
 */
class AddedItemListAdapter(context: Context, val listener: AddItemListClickListener): RecyclerView.Adapter<AddedItemListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var  context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context=context
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_add_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 5
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, listener: AddItemListClickListener) {
            itemView.setOnClickListener{
                itemView.item_chkbox_IV.isSelected=true
                listener.OnItemChkBoxClickListener(adapterPosition)
            }
        }

    }
}
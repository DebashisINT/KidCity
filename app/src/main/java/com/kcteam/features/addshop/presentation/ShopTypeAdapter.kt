package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.ShopTypeEntity
import kotlinx.android.synthetic.main.inflate_shop_type.view.*

/**
 * Created by Saikat on 01-Jun-20.
 */
class ShopTypeAdapter(private val context: Context, private val shopTypeList: List<ShopTypeEntity>?, private val onItemClick: (ShopTypeEntity) -> Unit) : RecyclerView.Adapter<ShopTypeAdapter.ViewHolder>() {

    private val inflater: LayoutInflater by lazy {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.inflate_shop_type, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun getItemCount(): Int {
        return shopTypeList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                tv_shop_type.text = shopTypeList?.get(adapterPosition)?.shoptype_name

                if (adapterPosition == shopTypeList?.size!! - 1)
                    view.visibility = View.GONE
                else
                    view.visibility = View.VISIBLE

                setOnClickListener {
                    onItemClick(shopTypeList[adapterPosition])
                }
            }
        }
    }
}
package com.kcteam.features.averageshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.AddShopDBModelEntity

/**
 * Created by Pratishruti on 15-11-2017.
 */
class AverageShopDateAdapter(context: Context, userLocationDataEntity: List<AddShopDBModelEntity>) : RecyclerView.Adapter<AverageShopDateAdapter.MyViewHolder>()  {

    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<AddShopDBModelEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_registered_shops, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 8
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<AddShopDBModelEntity>) {


        }

    }


}
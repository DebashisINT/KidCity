package com.kcteam.features.avgorder.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.features.averageshop.presentation.AverageShopListClickListener
import kotlinx.android.synthetic.main.inflate_avg_order.view.*


/**
 * Created by Pratishruti on 15-11-2017.
 */
class AverageOrderListAdapter(context: Context, userLocationDataEntity: List<AddShopDBModelEntity>, val listener: AverageShopListClickListener) : RecyclerView.Adapter<AverageOrderListAdapter.MyViewHolder>(){
    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<AddShopDBModelEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_avg_order, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<AddShopDBModelEntity>, listener: AverageShopListClickListener) {

//            Picasso.with(context).load(userLocationDataEntity[adapterPosition].shopImageLocalPath).into(itemView.shop_image_IV);
            itemView.myshop_name_TV.setText(userLocationDataEntity[adapterPosition].shopName)
            val address:String=userLocationDataEntity[adapterPosition].address+", "+userLocationDataEntity[adapterPosition].pinCode
            itemView.myshop_address_TV.setText(address)

            val drawable = TextDrawable.builder()
                    .buildRoundRect(userLocationDataEntity[adapterPosition].shopName.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor,120)
            itemView.shop_image_IV.findViewById<ImageView>(R.id.shop_image_IV).setImageDrawable(drawable)

            itemView.menu_IV.findViewById<ImageView>(R.id.menu_IV).setOnClickListener(View.OnClickListener {
                listener.OnMenuClick(adapterPosition,itemView.menu_IV)
            })
//
            itemView.setOnClickListener {
                listener.OnItemClick(adapterPosition)
            }

        }

    }
}
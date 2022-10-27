package com.kcteam.features.newcollection

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.CollectionDetailsEntity
import kotlinx.android.synthetic.main.inflate_new_collection_list_item.view.*

/**
 * Created by Saikat on 15-02-2019.
 */
class NewCollectionAdapter(context: Context, private val userLocationDataEntity: ArrayList<CollectionDetailsEntity>, private val listener: OnSyncClickListener) :
        RecyclerView.Adapter<NewCollectionAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_new_collection_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: ArrayList<CollectionDetailsEntity>, listener: OnSyncClickListener) {

            try {

                if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].date))
                    itemView.tv_collection_date.text = /*AppUtils.convertDateTimeToCommonFormat(*/userLocationDataEntity[adapterPosition].date!!//)

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(userLocationDataEntity[adapterPosition].shop_id)
                itemView.myshop_name_TV.text = shop?.shopName
                itemView.myshop_address_TV.text = shop?.address

                val totalPrice = String.format("%.2f", java.lang.Float.parseFloat(userLocationDataEntity[adapterPosition].collection!!))

                itemView.tv_collection_amount_value.text = context.getString(R.string.rupee_symbol) + " " + totalPrice


                if (shop.isUploaded) {

                    if (userLocationDataEntity[adapterPosition].isUploaded) {
                        itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                    } else {
                        itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                        itemView.sync_icon.setOnClickListener(View.OnClickListener {
                            listener.onSyncClick(adapterPosition)
                        })
                    }
                } else {
                    itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    itemView.sync_icon.setOnClickListener(View.OnClickListener {
                        listener.onSyncClick(adapterPosition)
                    })
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface OnSyncClickListener {
        fun onSyncClick(adapterPosition: Int)
    }
}
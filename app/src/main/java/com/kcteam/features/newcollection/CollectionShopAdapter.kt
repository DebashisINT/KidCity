package com.kcteam.features.newcollection

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.R
import com.kcteam.features.newcollection.model.CollectionShopListDataModel
import kotlinx.android.synthetic.main.inflate_collection_shop_item.view.*

class CollectionShopAdapter(context: Context, private val amountList: ArrayList<CollectionShopListDataModel>?,
                            private val onItemClick: (CollectionShopListDataModel) -> Unit) : RecyclerView.Adapter<CollectionShopAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_collection_shop_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return amountList?.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {

            try {

                itemView.apply {
                    myshop_name_TV.text = amountList?.get(adapterPosition)?.shop_name
                    tv_total_amount.text = context.getString(R.string.rupee_symbol_with_space) + amountList?.get(adapterPosition)?.total_amount
                    tv_total_collection.text = context.getString(R.string.rupee_symbol_with_space) + amountList?.get(adapterPosition)?.total_collection
                    tv_total_bal.text = context.getString(R.string.rupee_symbol_with_space) + amountList?.get(adapterPosition)?.total_bal

                    Glide.with(context)
                            .load(amountList?.get(adapterPosition)?.shop_image)
                            .apply(RequestOptions.placeholderOf(R.drawable.ic_logo).error(R.drawable.ic_logo))
                            .into(shop_image_IV)

                    setOnClickListener {
                        onItemClick(amountList?.get(adapterPosition)!!)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
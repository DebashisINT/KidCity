package com.kcteam.features.marketing.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.marketing.model.MarketingDetailImageData
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.inflate_marketing_detail_image.view.*


/**
 * Created by Pratishruti on 23-02-2018.
 */
class MarketingImageAdapter(context: Context, marketing_list: ArrayList<MarketingDetailImageData>, val listener:RecyclerViewClickListener) : RecyclerView.Adapter<MarketingImageAdapter.MyViewHolder>()   {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var context: Context = context
    private var marketing_list: List<MarketingDetailImageData> = marketing_list

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder!!.bindItems(context, marketing_list.get(position),listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_marketing_detail_image, parent, false)
        return MarketingImageAdapter.MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return marketing_list.size
    }

    class MyViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, marketingitem:MarketingDetailImageData,listener: RecyclerViewClickListener) {

            if (marketingitem.image_url!!.isBlank()){
                itemView.delete_img_IV.visibility=View.GONE
                itemView.item_img_IV.visibility=View.GONE
                itemView.img_upload_IV.visibility=View.VISIBLE
            }else{
               // Picasso.with(context).load(marketingitem.image_url).into(itemView.item_img_IV)
                Picasso.get()
                        .load(marketingitem.image_url)
                        .resize(100, 100)
                        .into(itemView.item_img_IV)
                itemView.delete_img_IV.visibility=View.VISIBLE
                itemView.item_img_IV.visibility=View.VISIBLE
                itemView.img_upload_IV.visibility=View.GONE
            }
            itemView.delete_img_IV.setOnClickListener(View.OnClickListener {
                listener.getDeleteItemPosition(adapterPosition)
            })
            itemView.setOnClickListener(View.OnClickListener {
                listener.getPosition(adapterPosition)
            })
        }
    }
}
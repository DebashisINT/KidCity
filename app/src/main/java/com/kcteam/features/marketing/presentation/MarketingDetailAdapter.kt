package com.kcteam.features.marketing.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.features.marketing.model.MarketingDetailData
import kotlinx.android.synthetic.main.inflate_marketing_brand_item.view.*

/**
 * Created by Pratishruti on 23-02-2018.
 */
class MarketingDetailAdapter(context: Context, marketing_list: ArrayList<MarketingDetailData>,val listener:RecyclerViewClickListener) : RecyclerView.Adapter<MarketingDetailAdapter.MyViewHolder>()  {
    private val layoutInflater: LayoutInflater
    private var context: Context
    var marketing_list: List<MarketingDetailData>


    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.marketing_list = marketing_list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_marketing_brand_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return marketing_list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, marketing_list.get(position),listener)
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, marketingitem:MarketingDetailData,listener: RecyclerViewClickListener) {
            var name= AppDatabase.getDBInstance()!!.marketingCategoryMasterDao().getMarketingCategoryNameFromId(marketingitem.material_id.toString())
            itemView.marketing_item_name_TV.text=name
            itemView.marketing_item_chkbox_IV.isSelected = marketingitem.isChecked
            if (marketingitem.date.isNullOrBlank()){
                itemView.marketing_item_date_TV.visibility=View.GONE
                itemView.marketing_item_chkbox_IV.visibility=View.VISIBLE
            }else{
                itemView.marketing_item_date_TV.setText(marketingitem.date!!)
                itemView.marketing_item_date_TV.visibility=View.VISIBLE
                itemView.marketing_item_chkbox_IV.visibility=View.GONE
            }

            itemView.item_RL.setOnClickListener(View.OnClickListener {
                if (marketingitem.date!=null && marketingitem.date!!.isNotBlank())
                    return@OnClickListener
                marketingitem.isChecked = !marketingitem.isChecked
                itemView.marketing_item_chkbox_IV.isSelected = marketingitem.isChecked

            })
            itemView.setOnClickListener(View.OnClickListener {
                listener.getPosition(adapterPosition)
            })
        }
    }
}
package com.kcteam.features.report.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.AddShopDBModelEntity
import kotlinx.android.synthetic.main.inflate_report_item.view.*

/**
 * Created by Kinsuk on 01-11-2017.
 */
class ReportAdapter(context: Context, list: ArrayList<AddShopDBModelEntity>) : RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    private lateinit var mList: ArrayList<AddShopDBModelEntity>


    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        mList = list
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, mList, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_report_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: List<AddShopDBModelEntity>, position: Int) {

            val addShopDBModelEntity: AddShopDBModelEntity = list[position]

            itemView.shop_name_TV.text = addShopDBModelEntity.shopName
            itemView.shop_address_TV.setText(addShopDBModelEntity.address)
            itemView.avg_order_val_TV.setText(context.getString(R.string.rupee_symbol) + " " + addShopDBModelEntity.orderValue)

            if (position > 4) {
                itemView.visibility = View.INVISIBLE
                itemView.shop_name_TV.setText("")
                itemView.shop_address_TV.setText("")
            } else {
                itemView.visibility = View.VISIBLE
            }

            /*if (list[adapterPosition].Descrpton?.equals("Field Work", ignoreCase = true)!!) {
                val routeList = AppDatabase.getDBInstance()?.selectedRouteListDao()?.getTodaysData(AppUtils.getCurrentDate())

                if (routeList != null && routeList.isNotEmpty()) {

                    for (i in routeList.indices) {

                        if (routeList[i].route_name.equals("other", ignoreCase = true)) {
                            itemView.shop_name_TV.text = "Other"
                            itemView.shop_address_TV.visibility = View.GONE
                            itemView.time_RL.visibility = View.GONE
                        } else {
                            val routeShopList = AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.getDataRouteIdDateWise(routeList[i].route_id!!, AppUtils.getCurrentDate())

                            if (routeShopList != null && routeShopList.isNotEmpty()) {
                                for (j in routeShopList.indices) {
                                }

                                itemView.rv_route_shop_list.visibility = View.VISIBLE
                            }

                        }
                    }

                }

            } */


            /*if(adapterPosition==0){
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.green_round))
            }else if(adapterPosition==7){
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.red_round))
            }else if (adapterPosition%2==0){
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.navy_blue_round))
            }else{
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.yellow_round))
            }*/

        }

    }


}
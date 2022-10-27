package com.kcteam.features.orderList

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.domain.OrderListEntity
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.types.FragType
import com.kcteam.features.averageshop.presentation.AverageShopListClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_registered_shops.view.*

/**
 * Created by Pratishruti on 15-11-2017.
 */
class OrderListAdapter(context: Context, userLocationDataEntity: ArrayList<OrderListEntity>?, val listener: AverageShopListClickListener) : RecyclerView.Adapter<OrderListAdapter.MyViewHolder>() {
    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<OrderListEntity> = userLocationDataEntity!!

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_registered_shops, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  /*10*/ userLocationDataEntity.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<OrderListEntity>, listener: AverageShopListClickListener) {

            itemView.iconWrapper_rl.visibility = View.GONE
            itemView.order_RL.visibility = View.GONE
            itemView.total_visited_RL.visibility = View.GONE
            itemView.last_visited_RL.visibility = View.GONE
            itemView.update_address_TV.visibility = View.GONE

//            Picasso.with(context).load(userLocationDataEntity[adapterPosition].shopImageLocalPath).into(itemView.shop_image_IV);
            itemView.myshop_name_TV.text = userLocationDataEntity[adapterPosition].shop_name
            itemView.myshop_address_TV.text = userLocationDataEntity[adapterPosition].address
            itemView.view_all_tv.visibility = View.VISIBLE
            itemView.view_all_tv.setOnClickListener(View.OnClickListener {
                (context as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, userLocationDataEntity[adapterPosition])
            })

            //itemView.order_amount_tv.text = "Total Order Amount : \u20B9 " + userLocationDataEntity[adapterPosition].order_amount


            /*if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].order_amount))
                itemView.order_amount_tv.visibility = View.VISIBLE
            else
                itemView.order_amount_tv.visibility = View.GONE*/

            /*if (userLocationDataEntity[adapterPosition].isUploaded && userLocationDataEntity[adapterPosition].isDurationCalculated) {
                itemView.sync_icon.visibility = View.VISIBLE
                itemView.sync_icon.setImageResource(R.drawable.ic_dashboard_green_tick_new)
            } else
                if (userLocationDataEntity[adapterPosition].isDurationCalculated && !userLocationDataEntity[adapterPosition].isUploaded) {
                    itemView.sync_icon.visibility = View.VISIBLE
                    itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    itemView.sync_icon.setOnClickListener(View.OnClickListener {
                        listener.onSyncClick(adapterPosition)
                    })
                } else
                    itemView.sync_icon.visibility = View.GONE*/

            itemView.sync_icon.visibility = View.GONE
//            if (userLocationDataEntity[adapterPosition].lastVisitedDate == "") {
//                var listnew = AppDatabase.getDBInstance()!!.addShopEntryDao().getVisitedShopListByName(userLocationDataEntity[adapterPosition].shop_name, true)
//                userLocationDataEntity[adapterPosition].totalVisitCount = listnew.size.toString()
//                userLocationDataEntity[adapterPosition].lastVisitedDate = listnew[listnew.size - 1].visitDate
//            }

//            itemView.total_visited_value_TV.setText(userLocationDataEntity[adapterPosition])
            //itemView.last_visited_date_TV.text = userLocationDataEntity[adapterPosition].visited_date

            val drawable = TextDrawable.builder()
                    .buildRoundRect(userLocationDataEntity[adapterPosition].shop_name!!.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

            itemView.shop_IV.setImageDrawable(drawable)

            itemView.menu_IV.findViewById<ImageView>(R.id.menu_IV).setOnClickListener(View.OnClickListener {
                listener.OnMenuClick(adapterPosition, itemView.menu_IV)
            })
//
            itemView.setOnClickListener {
                listener.OnItemClick(adapterPosition)
            }

            val viewAllOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(userLocationDataEntity[adapterPosition].shop_id!!) as ArrayList<OrderDetailsListEntity>

            try {
                if (viewAllOrderList != null && viewAllOrderList.size > 0) {
                    itemView.order_amount_tv.visibility = View.VISIBLE
                    var amount = 0.0
                    for (i in viewAllOrderList.indices) {
                        if (!TextUtils.isEmpty(viewAllOrderList[i].amount))
                            amount += viewAllOrderList[i].amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())
                    itemView.order_amount_tv.text = "Total Order Amount: ₹ " + finalAmount
                } else
                    itemView.order_amount_tv.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    open fun updateList(locationDataEntity: List<ShopActivityEntity>) {
        /*Collections.reverse(locationDataEntity)
        userLocationDataEntity = locationDataEntity
        notifyDataSetChanged()*/
    }
}
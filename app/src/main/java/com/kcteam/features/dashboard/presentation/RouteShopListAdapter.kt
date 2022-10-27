package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.SelectedRouteShopListEntity
import com.kcteam.app.utils.AppUtils
import kotlinx.android.synthetic.main.inflate_selected_route_shop_list_item.view.*

/**
 * Created by Saikat on 30-11-2018.
 */
class RouteShopListAdapter(context: Context, list: ArrayList<SelectedRouteShopListEntity>?) : RecyclerView.Adapter<RouteShopListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    private lateinit var mList: ArrayList<SelectedRouteShopListEntity>


    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        mList = list!!
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, mList, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_selected_route_shop_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: ArrayList<SelectedRouteShopListEntity>, position: Int) {
            if (!TextUtils.isEmpty(list[adapterPosition].route_id)) {
                itemView.shop_name_TV.text = list[adapterPosition].shop_name
                itemView.route_id_TV.text = "Route " + list[adapterPosition].route_id

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(list[adapterPosition].shop_id)

                if (!TextUtils.isEmpty(Pref.isFieldWorkVisible)) {
                    if (Pref.isFieldWorkVisible.equals("true", ignoreCase = true)) {
                        itemView.ll_route_name.visibility = View.VISIBLE
                        itemView.ll_sales_visit.visibility = View.GONE
                    } else {
                        itemView.ll_route_name.visibility = View.GONE
                        itemView.ll_sales_visit.visibility = View.VISIBLE
                    }
                } else {
                    itemView.ll_route_name.visibility = View.VISIBLE
                    itemView.ll_sales_visit.visibility = View.GONE
                }

                if (shop != null)
                    itemView.tv_shop_address.text = shop.address
                else
                    itemView.ll_sales_visit.visibility = View.GONE

                //if (!list[adapterPosition].shop_name.equals("other", ignoreCase = true)) {
                val visitedShop = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(list[adapterPosition].shop_id!!, AppUtils.getCurrentDateForShopActi())
                itemView.avg_order_val_TV.visibility = View.VISIBLE
                val bgShape = itemView.view.background as GradientDrawable
                if (visitedShop == null || visitedShop.isEmpty() || !visitedShop.get(0).isVisited) {
                    itemView.avg_order_val_TV.text = "Pending"
                    itemView.iv_task_icon.setImageResource(R.drawable.ic_pending_icon)
                    //itemView.view.setBackgroundColor(context.resources.getColor(R.color.yellow))
                    bgShape.setColor(context.resources.getColor(R.color.yellow))
                } else {
                    itemView.avg_order_val_TV.text = "Visited"
                    itemView.iv_task_icon.setImageResource(R.drawable.ic_visited_icon)
                    //itemView.view.setBackgroundColor(context.resources.getColor(R.color.green))
                    bgShape.setColor(context.resources.getColor(R.color.green))
                }
                /*}
                else
                    itemView.avg_order_val_TV.visibility = View.GONE*/
                //itemView.rv_route_shop_list.visibility = View.GONE

                itemView.iv_map.setOnClickListener {
                    if (shop != null) {
                        (context as DashboardActivity).openLocationMap(shop, false)
                    } else
                        (context as DashboardActivity).showSnackMessage(context.getString(R.string.shop_loc_unavailable))
                }
            }
        }
    }
}
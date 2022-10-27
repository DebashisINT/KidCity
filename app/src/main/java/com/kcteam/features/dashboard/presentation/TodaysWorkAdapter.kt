package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.SelectedRouteShopListEntity
import com.kcteam.app.domain.SelectedWorkTypeEntity
import com.kcteam.app.utils.AppUtils
import kotlinx.android.synthetic.main.inflate_work_plan_item.view.*


/**
 * Created by Saikat on 30-11-2018.
 */
class TodaysWorkAdapter(context: Context, list: ArrayList<SelectedWorkTypeEntity>) : RecyclerView.Adapter<TodaysWorkAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    private lateinit var mList: ArrayList<SelectedWorkTypeEntity>


    init {
        Log.e("works adapter", "========init block=============")
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        mList = list
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.e("works adapter", "mList.position---------> $position")
        holder.bindItems(context, mList, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_work_plan_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        Log.e("works adapter", "mList.size---------> " + mList.size)
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: ArrayList<SelectedWorkTypeEntity>, position: Int) {

            if (!TextUtils.isEmpty(list[adapterPosition].Descrpton)) {

                if (list[adapterPosition].Descrpton?.equals(context.getString(R.string.field_work), ignoreCase = true)!! /*||
                        list[adapterPosition].Descrpton?.equals(context.getString(R.string.sales_visit), ignoreCase = true)!!*/) {
                    val routeList = AppDatabase.getDBInstance()?.selectedRouteListDao()?.getTodaysData(AppUtils.getCurrentDate())

                    if (routeList != null && routeList.isNotEmpty()) {

                        val routeShopList = AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.getDataDateWise(AppUtils.getCurrentDate())
                                as ArrayList<SelectedRouteShopListEntity>
                        if (routeShopList != null && routeShopList.isNotEmpty()) {
                            itemView.rv_route_shop_list.visibility = View.VISIBLE
                            itemView.rv_route_shop_list.layoutManager = LinearLayoutManager(context)
                            itemView.rv_route_shop_list.adapter = RouteShopListAdapter(context, routeShopList)
                            itemView.rl_work_plan.visibility = View.GONE
                        } else {
                            itemView.rl_work_plan.visibility = View.VISIBLE
                            itemView.rv_route_shop_list.visibility = View.GONE
                        }

                        for (i in routeList.indices) {

                            if (routeList[i].route_name.equals("other", ignoreCase = true)) {
                                itemView.shop_name_TV.text = "Other"
                                itemView.shop_address_TV.visibility = View.VISIBLE
                                itemView.shop_address_TV.text = "Other"
                                itemView.avg_order_val_TV.text = "Route Task"
                                itemView.tv_route.visibility = View.VISIBLE
                                itemView.fl_dot.visibility = View.VISIBLE
                                itemView.iv_task_icon.setImageResource(R.drawable.ic_others_icon)
                                if (routeShopList != null && routeShopList.isNotEmpty())
                                    itemView.rv_route_shop_list.visibility = View.VISIBLE
                                else
                                    itemView.rv_route_shop_list.visibility = View.GONE

                                val bgShape = itemView.view.background as GradientDrawable
                                bgShape.setColor(context.resources.getColor(R.color.purple))

                                itemView.rl_work_plan.visibility = View.VISIBLE
                                break
                            } /*else {
                                val routeShopList = AppDatabase.getDBInstance()?.selectedRouteShopListDao()?.getDataRouteIdDateWise(routeList[i].route_id!!,
                                        AppUtils.getCurrentDate()) as ArrayList<SelectedRouteShopListEntity>
                                if (routeShopList != null && routeShopList.isNotEmpty()) {
                                    itemView.rv_route_shop_list.visibility = View.VISIBLE
                                    itemView.rv_route_shop_list.layoutManager = LinearLayoutManager(context)
                                    itemView.rv_route_shop_list.adapter = RouteShopListAdapter(context, routeShopList)
                                    itemView.cv_work_plan.visibility = View.GONE
                                } else {
                                    itemView.cv_work_plan.visibility = View.VISIBLE
                                    itemView.rv_route_shop_list.visibility = View.GONE
                                }

                            }*/
                        }
                    }
                } else {
                    val bgShape = itemView.view.background as GradientDrawable

                    if (list[adapterPosition].Descrpton.equals("meeting", ignoreCase = true)) {
                        itemView.iv_task_icon.setImageResource(R.drawable.ic_mettings_icon)
                        //itemView.view.setBackgroundColor(context.resources.getColor(R.color.purple))
                        bgShape.setColor(context.resources.getColor(R.color.light_red))
                    } else {
                        itemView.iv_task_icon.setImageResource(R.drawable.ic_others_icon)
                        //itemView.view.setBackgroundColor(context.resources.getColor(R.color.light_red))
                        bgShape.setColor(context.resources.getColor(R.color.purple))
                    }
                    itemView.avg_order_val_TV.text = ""
                    itemView.shop_name_TV.text = list[adapterPosition].Descrpton
                    itemView.shop_address_TV.visibility = View.GONE
                    itemView.rv_route_shop_list.visibility = View.GONE
                    itemView.rl_work_plan.visibility = View.VISIBLE
                    itemView.tv_route.visibility = View.GONE
                    itemView.fl_dot.visibility = View.GONE
                }

            }
        }

    }
}
package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.location.Location
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.PjpListEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.features.localshops.LocalShopListClickListener
import com.kcteam.features.localshops.LocalShopsListAdapter
import com.kcteam.features.location.LocationWizard
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.*
import kotlinx.android.synthetic.main.inflate_user_pjp_item.view.*
import java.lang.Exception

/**
 * Created by Saikat on 07-Apr-20.
 */
class PjpAdapter(private val context: Context, private val list: ArrayList<PjpListEntity>, val listener: PJPClickListner) : RecyclerView.Adapter<PjpAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, list, position, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_user_pjp_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: ArrayList<PjpListEntity>, position: Int, listener: PJPClickListner) {
            itemView.apply {

                rl_pjp_status.visibility = View.VISIBLE

                if (!TextUtils.isEmpty(list?.get(position)?.customer_name)) {
                    tv_pjp_name.text = list?.get(position)?.customer_name

                    var isVisited=false
                    val visitedShop = AppDatabase.getDBInstance()?.shopActivityDao()?.getShopForDay(list?.get(position)?.customer_id!!, AppUtils.getCurrentDateForShopActi())
                    if (visitedShop != null){
                        try{
                            if(visitedShop.get(0).isVisited){
                                isVisited=true
                            }else{
                                isVisited=false
                            }
                        }catch (e:Exception){
                            isVisited=false
                        }

                    }

                    val shopID = list[position].customer_id
                    val shopDetails = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(shopID)
                    val shopLocation = Location("")
                    shopLocation.latitude = shopDetails!!.shopLat
                    shopLocation.longitude = shopDetails!!.shopLong
                    val myLoc = Location("")
                    //myLoc.latitude = Pref!!.current_latitude.toDouble()
                    myLoc.latitude = Pref.latitude!!.toDouble()
                    myLoc.longitude = Pref.longitude!!.toDouble()
                    //myLoc.longitude = Pref!!.current_longitude.toDouble()

                    val isShopNearby = FTStorageUtils.checkShopPositionWithinRadious(myLoc, shopLocation, LocationWizard.NEARBY_RADIUS)

                   /* val distance = LocationWizard.getDistance(shopDetails!!.shopLat, shopDetails!!.shopLong,
                            Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())*/


                    //if ((distance.toInt()) <= Pref.gpsAccuracy.toInt() && !isVisited) {

                  /*  if (isShopNearby && !isVisited) {
                        tv_pjp_shop_revisit.visibility = View.VISIBLE
                    } else {
                        tv_pjp_shop_revisit.visibility = View.GONE
                    }*/

                    val bgShape = itemView.view.background as GradientDrawable
                    if (visitedShop == null || visitedShop.isEmpty() || !visitedShop[0].isVisited) {
                        itemView.tv_status.text = "Pending"
                        itemView.iv_task_icon.setImageResource(R.drawable.ic_pending_icon)
                        bgShape.setColor(context.resources.getColor(R.color.yellow))
                        if (isShopNearby && !isVisited) {
                            itemView.tv_status.text = "Revisit"
                            itemView.iv_task_icon.setImageResource(R.drawable.revisit)
                            bgShape.setColor(context.resources.getColor(R.color.yellow))
                        }
                    } else {
                        itemView.tv_status.text = "Visited"
                        itemView.iv_task_icon.setImageResource(R.drawable.ic_visited_icon)
                        bgShape.setColor(context.resources.getColor(R.color.green))
                    }

                    itemView.rl_pjp_status.setOnClickListener(View.OnClickListener {
                        if(itemView.tv_status.text.equals("Revisit")){
                            listener.visitShop(shopDetails)
                        }
                    })


                } else if (!TextUtils.isEmpty(list?.get(position)?.location)) {
                    tv_pjp_name.text = list?.get(position)?.location

                    itemView.iv_task_icon.setImageResource(R.drawable.ic_others_icon)
                    itemView.tv_status.text = ""
                    val bgShape = itemView.view.background as GradientDrawable
                    bgShape.setColor(context.resources.getColor(R.color.purple))
                } else {
                    tv_pjp_name.text = "Other PJP"

                    itemView.iv_task_icon.setImageResource(R.drawable.ic_others_icon)
                    itemView.tv_status.text = ""
                    val bgShape = itemView.view.background as GradientDrawable
                    bgShape.setColor(context.resources.getColor(R.color.purple))
                }
            }
        }

    }
}
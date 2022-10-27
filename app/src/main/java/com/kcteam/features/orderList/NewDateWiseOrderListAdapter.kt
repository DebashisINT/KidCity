package com.kcteam.features.orderList

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.domain.OrderProductListEntity
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.averageshop.presentation.AverageShopListClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_new_order_item.view.*
import kotlinx.android.synthetic.main.inflate_new_order_item.view.myshop_address_TV
import kotlinx.android.synthetic.main.inflate_new_order_item.view.myshop_name_TV
import kotlinx.android.synthetic.main.inflate_new_order_item.view.sync_icon
import kotlinx.android.synthetic.main.inflate_new_order_item.view.total_visited_value_TV

/**
 * Created by Saikat on 29-11-2018.
 */
class NewDateWiseOrderListAdapter(context: Context, userLocationDataEntity: ArrayList<OrderDetailsListEntity>,
                                  val listener: AverageShopListClickListener, private val onEmailClick: (String, String) -> Unit,
                                  private val onDownloadClick: (OrderDetailsListEntity) -> Unit,
                                  private val onCollectionClick: (OrderDetailsListEntity) -> Unit,
                                  private val onLocationClick: (OrderDetailsListEntity) -> Unit,
                                  private val onCreateQRClick: (OrderDetailsListEntity) -> Unit) :
        RecyclerView.Adapter<NewDateWiseOrderListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<OrderDetailsListEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_new_order_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<OrderDetailsListEntity>, listener: AverageShopListClickListener) {

            try {


                itemView.total_visited_value_TV.text = userLocationDataEntity[adapterPosition].order_id
                if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].date))
                    itemView.tv_order_date.text = AppUtils.convertDateTimeToCommonFormat(userLocationDataEntity[adapterPosition].date!!)
                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(userLocationDataEntity[adapterPosition].shop_id)
                itemView.myshop_name_TV.text = shop?.shopName
                itemView.myshop_address_TV.text = shop?.address
                val list = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToOrderId(userLocationDataEntity[adapterPosition].order_id!!) as ArrayList<OrderProductListEntity>
                itemView.tv_total_item.text = list.size.toString()

                if (shop != null) {
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
                }

                var totalAmount = 0.0

                for (i in list.indices) {
                    totalAmount += list[i].total_price?.toDouble()!!
                }
                //val totalPrice = DecimalFormat("##.##").format(totalAmount)
                val totalPrice = String.format("%.2f", totalAmount.toFloat())
                itemView.tv_total_amount.text = context.getString(R.string.rupee_symbol) + totalPrice

                if (Pref.isOrderMailVisible) {
                    if (shop.type != "1" && shop.type != "2" && shop.type != "3" && shop.type != "4" && shop.type != "5")
                        itemView.email_icon.visibility = View.GONE
                    else
                        itemView.email_icon.visibility = View.VISIBLE
                } else
                    itemView.email_icon.visibility = View.GONE

                /*if (Pref.isCollectioninMenuShow)
                    itemView.collection_icon.visibility = View.VISIBLE
                else
                    itemView.collection_icon.visibility = View.GONE*/

                if(Pref.isCollectioninMenuShow && Pref.ShowCollectionOnlywithInvoiceDetails ==false){
                    itemView.collection_icon.visibility = View.VISIBLE
                }else if(Pref.isCollectioninMenuShow && Pref.ShowCollectionOnlywithInvoiceDetails){
                    val list = AppDatabase.getDBInstance()!!.billingDao().getDataOrderIdWise(userLocationDataEntity[adapterPosition]?.order_id!!) as ArrayList
                    if(list.size>0){
                        itemView.collection_icon.visibility = View.VISIBLE
                    }else{
                        itemView.collection_icon.visibility = View.GONE
                    }
                }else{
                    itemView.collection_icon.visibility = View.GONE
                }


                itemView.setOnClickListener {
                    (context as DashboardActivity).loadFragment(FragType.ViewCartFragment, true, userLocationDataEntity.get(adapterPosition))
                }

                itemView.email_icon.setOnClickListener {
                    onEmailClick(userLocationDataEntity[adapterPosition].shop_id!!, userLocationDataEntity[adapterPosition].order_id!!)
                }


                val list_ = AppDatabase.getDBInstance()!!.billingDao().getDataOrderIdWise(userLocationDataEntity[adapterPosition].order_id!!)
                if (list_ != null && list_.isNotEmpty()) {

                    //itemView.tv_billing.setBackgroundResource(R.drawable.selector_view_bills_bg)
                    itemView.tv_billing.setBackgroundResource(R.drawable.shape_custom_border_blue_button)
                    itemView.tv_billing.setText(R.string.view_bill)

                    itemView.tv_billing.setOnClickListener {
                        (context as DashboardActivity).shop = shop
                        (context as DashboardActivity).loadFragment(FragType.BillingListFragment, true, userLocationDataEntity[adapterPosition])
                    }
                } else {

                    //itemView.tv_billing.setBackgroundResource(R.drawable.selector_update_bills_bg)
                    itemView.tv_billing.setBackgroundResource(R.drawable.shape_custom_border_blue_button)
                    itemView.tv_billing.text = Pref.updateBillingText

                    itemView.tv_billing.setOnClickListener {

                        (context as DashboardActivity).shop = shop

                        /*if (!shop.isUploaded)
                                        (context as DashboardActivity).showSnackMessage("Sync Shop first")
                                    else if (!userLocationDataEntity[adapterPosition].isUploaded)
                                        (context as DashboardActivity).showSnackMessage("Sync Order first")
                                    else*/
                        if (!Pref.isAddAttendence)
                            (context as DashboardActivity).checkToShowAddAttendanceAlert()
                        else
                            (context as DashboardActivity).loadFragment(FragType.AddBillingFragment, true, userLocationDataEntity[adapterPosition])
                    }
                }

                itemView.download_icon.setOnClickListener {
                    onDownloadClick(userLocationDataEntity[adapterPosition])
                }

                itemView.collection_icon.setOnClickListener {
                    onCollectionClick(userLocationDataEntity[adapterPosition])
                }

                itemView.location_icon.setOnClickListener {
                    onLocationClick(userLocationDataEntity[adapterPosition])
                }

                itemView.iv_create_qr.setOnClickListener {
                    onCreateQRClick(userLocationDataEntity[adapterPosition])
                }

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
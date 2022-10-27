package com.kcteam.features.activities.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.ActivityEntity
import com.kcteam.app.utils.AppUtils
import kotlinx.android.synthetic.main.inflate_datewise_activity_item.view.*
import java.util.HashSet

class DateWiseActivityAdapter(private val context: Context, private val list: ArrayList<ActivityEntity>?,
                              private val onSyncClick: (ActivityEntity) -> Unit?, private val onEditClick: (ActivityEntity) -> Unit?) : RecyclerView.Adapter<DateWiseActivityAdapter.ViewHolder>() {

    private val inflater: LayoutInflater by lazy {
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.inflate_datewise_activity_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.subject))
                    tv_subject.text = list?.get(adapterPosition)?.subject
                else
                    tv_subject.text = "N.A."

                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.details))
                    tv_details.text = list?.get(adapterPosition)?.details
                else
                    tv_details.text = "N.A."

                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.attachments))
                    tv_attachment.text = list?.get(adapterPosition)?.attachments
                else
                    tv_attachment.text = "N.A."

                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.image))
                    tv_image.text = list?.get(adapterPosition)?.image
                else
                    tv_image.text = "N.A."


                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.party_id)) {
                    val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(list?.get(adapterPosition)?.party_id)
                    if (!TextUtils.isEmpty(shop?.shopName))
                        tv_shop_name.text = shop?.shopName
                    else
                        tv_shop_name.text = "N.A."
                }
                else
                    tv_shop_name.text = "N.A."


                if(!TextUtils.isEmpty(list?.get(adapterPosition)?.activity_dropdown_id)) {
                    val activity = AppDatabase.getDBInstance()?.activityDropdownDao()?.getSingleItem(list?.get(adapterPosition)?.activity_dropdown_id!!)
                    if (!TextUtils.isEmpty(activity?.activity_name))
                        tv_activity.text = activity?.activity_name
                    else
                        tv_activity.text = "N.A."
                }
                else
                    tv_activity.text = "N.A."


                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.type_id)) {
                    val type = AppDatabase.getDBInstance()?.typeDao()?.getSingleType(list?.get(adapterPosition)?.type_id!!)
                    if (!TextUtils.isEmpty(type?.name))
                        tv_type.text = type?.name
                    else
                        tv_type.text = "N.A."
                }
                else
                    tv_type.text = "N.A."


                if(!TextUtils.isEmpty(list?.get(adapterPosition)?.product_id)) {
                    val product = AppDatabase.getDBInstance()?.productListDao()?.getSingleProduct(list?.get(adapterPosition)?.product_id?.toInt()!!)
                    if (!TextUtils.isEmpty(product?.product_name))
                        tv_product.text = product?.product_name
                    else
                        tv_product.text = "N.A."
                }
                else
                    tv_product.text = "N.A."


                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.priority_id)) {
                    val priority = AppDatabase.getDBInstance()?.priorityDao()?.getSingleType(list?.get(adapterPosition)?.priority_id!!)
                    if (!TextUtils.isEmpty(priority?.name))
                        tv_priority.text = priority?.name
                    else
                        tv_priority.text = "N.A."
                }
                else
                    tv_priority.text = "N.A."

                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.duration))
                    tv_duration.text = list?.get(adapterPosition)?.duration
                else
                    tv_duration.text = "N.A."


                tv_due_date_time.text = AppUtils.convertToBillingFormat(list?.get(adapterPosition)?.due_date!!) + " " + list?.get(adapterPosition)?.due_time
                tv_date_time.text = AppUtils.convertToBillingFormat(list?.get(adapterPosition)?.date!!) + " " + list?.get(adapterPosition)?.time


                if (list[adapterPosition].isUploaded)
                    sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                else {
                    sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    sync_icon.setOnClickListener {
                        onSyncClick(list[adapterPosition])
                    }
                }

                edit_icon.setOnClickListener {
                    onEditClick(list[adapterPosition])
                }
            }
        }
    }
}
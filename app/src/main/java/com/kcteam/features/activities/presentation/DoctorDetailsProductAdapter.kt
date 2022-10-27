package com.kcteam.features.activities.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.AddDoctorProductListEntity
import kotlinx.android.synthetic.main.inflate_product_item.view.*

/**
 * Created by Saikat on 09-01-2020.
 */
class DoctorDetailsProductAdapter(private val context: Context, private val productList: ArrayList<AddDoctorProductListEntity>) :
        RecyclerView.Adapter<DoctorDetailsProductAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_product_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, productList)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, productList: ArrayList<AddDoctorProductListEntity>) {

            itemView.iv_check.visibility = View.GONE

            if (adapterPosition == productList.size - 1)
                itemView.product_view.visibility = View.GONE
            else
                itemView.product_view.visibility = View.VISIBLE


            itemView.tv_product_name.text = productList[adapterPosition].product_name
        }
    }
}
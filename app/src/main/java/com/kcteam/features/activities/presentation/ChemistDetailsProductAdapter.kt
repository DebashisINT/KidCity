package com.kcteam.features.activities.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.AddChemistProductListEntity
import kotlinx.android.synthetic.main.inflate_product_item.view.*

/**
 * Created by Saikat on 08-01-2020.
 */
class ChemistDetailsProductAdapter(private val context: Context, private val productList: ArrayList<AddChemistProductListEntity>?) :
        RecyclerView.Adapter<ChemistDetailsProductAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_product_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, productList)
    }

    override fun getItemCount(): Int {
        return productList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, orginalProductList: ArrayList<AddChemistProductListEntity>?) {

            if (adapterPosition == orginalProductList?.size!! - 1)
                itemView.product_view.visibility = View.GONE
            else
                itemView.product_view.visibility = View.VISIBLE

            itemView.iv_check.visibility = View.GONE

            itemView.tv_product_name.text = orginalProductList[adapterPosition].product_name
        }
    }
}
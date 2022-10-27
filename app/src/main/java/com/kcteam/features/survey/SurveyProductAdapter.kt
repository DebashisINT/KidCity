package com.kcteam.features.survey

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.damageProduct.model.Shop_wise_breakage_list
import kotlinx.android.synthetic.main.inflater_breakage_item.view.*
import kotlinx.android.synthetic.main.inflater_breakage_item.view.breakage_type_tv
import kotlinx.android.synthetic.main.inflater_survey_item.view.*

class SurveyProductAdapter(private val context: Context, private val selectedProductList: ArrayList<survey_list>?, private val listener: SurveyProductAdapter.OnClickListener) :
    RecyclerView.Adapter<SurveyProductAdapter.MyViewHolder>(){

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflater_survey_item, parent, false)
        return MyViewHolder(v)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, selectedProductList, listener)
    }

    override fun getItemCount(): Int {
        return selectedProductList!!.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<survey_list>?, listener: SurveyProductAdapter.OnClickListener) {
            itemView.survey_date_tv.text= AppUtils.getFormatedDateNew(categoryList!!.get(position).saved_date_time!!.split("T").get(0),"yyyy-mm-dd","dd-mm-yyyy")
            itemView.survey_no_tv.text=categoryList!!.get(adapterPosition).survey_id

            itemView.tv_survey_view.setOnClickListener {
                listener.onView(categoryList!!.get(adapterPosition))
            }
            itemView.survey_share_iv.setOnClickListener {
                listener.onShare(categoryList!!)
            }
            itemView.survey_del_iv.setOnClickListener {
                listener.onDelete(categoryList!!.get(adapterPosition))
            }
        }
    }

    interface OnClickListener {
        fun onView(obj:survey_list)
        fun onShare(obj:ArrayList<survey_list>)
        fun onDelete(obj:survey_list)
    }
}
package com.kcteam.features.survey

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.squareup.picasso.Cache
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_survey_dtls_view.view.*

class AdapterSurveyDtlsView(var context: Context,var list:ArrayList<question_ans_list>,private val listener: AdapterSurveyDtlsView.OnClickListener):
    RecyclerView.Adapter<AdapterSurveyDtlsView.MyViewHolder>(){


    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.row_survey_dtls_view, parent, false)
        return MyViewHolder(v)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, list,listener)
    }

    override fun getItemCount(): Int {
        return list!!.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<question_ans_list>?,listener: AdapterSurveyDtlsView.OnClickListener) {
            itemView.tv_row_survey_dtls_view_q.text=categoryList!!.get(adapterPosition).question_desc
            itemView.tv_row_survey_dtls_view_ans.text=categoryList!!.get(adapterPosition).answer

            if(!categoryList!!.get(adapterPosition).image_link!!.equals("")){
                val picasso = Picasso.Builder(context)
                    .memoryCache(Cache.NONE)
                    .indicatorsEnabled(false)
                    .loggingEnabled(true)
                    .build()

                picasso.load(Uri.parse(categoryList!!.get(adapterPosition).image_link!!))
                    .centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(500, 500)
                    .into(itemView.iv_row_survey_dtls_view_ansimg)
            }
            else{
                itemView.iv_row_survey_dtls_view_ansimg.visibility = View.GONE
            }

            itemView.iv_row_survey_dtls_view_ansimg.setOnClickListener {
                listener.viewPicOnLick(categoryList?.get(adapterPosition))
            }



        }
    }

    interface OnClickListener {
        fun viewPicOnLick(obj:question_ans_list)

    }

}
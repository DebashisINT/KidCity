package com.kcteam.features.document.presentation

import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.domain.DocumentypeEntity
import com.kcteam.app.domain.TimesheetListEntity
import com.kcteam.features.timesheet.presentation.TimeSheetAdapter
import kotlinx.android.synthetic.main.inflate_document_type.view.*
import kotlinx.android.synthetic.main.inflate_timesheet_item.view.*

class DocumentTypeAdapter(private val mContext: Context, private val docTypeList:  ArrayList<DocumentypeEntity>,
                          private val onItemClick: (DocumentypeEntity) -> Unit) : RecyclerView.Adapter<DocumentTypeAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_document_type, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return docTypeList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {

            itemView.apply {
                tv_type_name.text = docTypeList[adapterPosition].type_name

               /* Glide.with(mContext)
                        .load(docTypeList[adapterPosition].image)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_doc).error(R.drawable.ic_doc))
                        .into(iv_doc_type)*/

                setOnClickListener {
                    onItemClick(docTypeList[adapterPosition])
                }
            }
        }
    }
}
package com.kcteam.features.document.presentation

import android.content.Context
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.domain.DocumentListEntity
import com.kcteam.app.utils.AppUtils
import kotlinx.android.synthetic.main.inflater_document_item.view.*
import java.io.File

class DocumentAdapter(private val mContext: Context, private val docList: ArrayList<DocumentListEntity>,
                      private val onEditClick: (DocumentListEntity) -> Unit, private val onDeleteClick: (DocumentListEntity) -> Unit,
                      private val onShareClick: (DocumentListEntity, String) -> Unit, private val onSyncClick: (DocumentListEntity) -> Unit,
                      private val onAttachmentClick: (DocumentListEntity) -> Unit,
                      private val onViewClick: (DocumentListEntity) -> Unit,
                      private val onOpenFileClick: (DocumentListEntity, String) -> Unit


        /*,
                      private val onAttchmentClick: (DocumentListEntity, String) -> Unit*/) : RecyclerView.Adapter<DocumentAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
           val v = layoutInflater.inflate(R.layout.inflater_document_item, parent, false)
           return MyViewHolder(v)

    }

    override fun getItemCount(): Int {
        return docList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {

            itemView.apply {

                if (adapterPosition % 2 == 0)
                    setBackgroundColor(mContext.resources.getColor(R.color.report_screen_bg))
                else
                    setBackgroundColor(mContext.resources.getColor(R.color.white))

                if (!TextUtils.isEmpty(docList[adapterPosition].date_time))
                    tv_doc_date_time.text = AppUtils.convertToNotificationDateTime(docList[adapterPosition].date_time!!)
                else
                    tv_doc_date_time.text = "N.A."

                if(!docList[adapterPosition].attachment?.startsWith("http")!!) {
                    val strFileName = SpannableString(File(docList[adapterPosition].attachment!!).name)
                    strFileName.setSpan(UnderlineSpan(), 0, strFileName.length, 0)
                    tv_attachment.text = strFileName
                }
                else {
                    val strFileName = SpannableString(docList[adapterPosition].attachment?.substring(docList[adapterPosition].attachment?.lastIndexOf("/")!! + 1))
                    strFileName.setSpan(UnderlineSpan(), 0, strFileName.length, 0)
                    tv_attachment.text = strFileName
                }

                if(docList[adapterPosition].isUploaded) {
                    sync_status_iv.visibility = View.VISIBLE
                    sync_status_iv.setImageResource(R.drawable.ic_registered_shop_sync)
                }
                else {
                    sync_status_iv.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    sync_status_iv.visibility = View.VISIBLE
                    sync_status_iv.setOnClickListener {
                        onSyncClick(docList[adapterPosition])
                    }
                }

                iv_edit.setOnClickListener {
                    onEditClick(docList[adapterPosition])
                }

                iv_delete.setOnClickListener {
                    onDeleteClick(docList[adapterPosition])
                }

                iv_share.setOnClickListener {
                    onShareClick(docList[adapterPosition], tv_attachment.text.toString().trim())
                }

                iv_download.setOnClickListener{
                    onAttachmentClick(docList[adapterPosition])
                }
                tv_down_text.setOnClickListener{
                    onViewClick(docList[adapterPosition])
                }

                if (!TextUtils.isEmpty(docList[adapterPosition].document_name))
//                    tv_doc_file_name.text = AppUtils.convertToNotificationDateTime(docList[adapterPosition].document_name!!)
                    tv_doc_file_name.text = docList[adapterPosition].document_name!!
                else
                    tv_doc_file_name.text = "N.A."

                tv_attachment.setOnClickListener {
                    onOpenFileClick(docList[adapterPosition], tv_attachment.text.toString().trim())
                }


                /*tv_attachment.setOnClickListener {
                    onAttchmentClick(docList[adapterPosition], tv_attachment.text.toString().trim())
                }*/
                if(CustomStatic.IsDocZero==true){
                  if(Pref.IsDocRepShareDownloadAllowed){
                      iv_share.visibility = View.VISIBLE
                      iv_download.visibility =  View.VISIBLE
                      tv_down_text.visibility =  View.VISIBLE
                      iv_edit.visibility = View.INVISIBLE
                      iv_delete.visibility = View.INVISIBLE
                      sync_status_iv.visibility = View.GONE
                      tv_doc_file_name.visibility = View.VISIBLE

                  }
                    else{
                      iv_share.visibility = View.GONE
                      iv_download.visibility =  View.GONE
                      tv_down_text.visibility =  View.GONE
                      iv_edit.visibility = View.INVISIBLE
                      iv_delete.visibility = View.INVISIBLE
                      sync_status_iv.visibility = View.GONE
                      tv_doc_file_name.visibility = View.VISIBLE
                  }
                }
                else{
                    iv_share.visibility = View.VISIBLE
                    iv_edit.visibility = View.VISIBLE
                    iv_delete.visibility = View.VISIBLE
                    sync_status_iv.visibility = View.VISIBLE
                    tv_doc_file_name.visibility = View.GONE
                }


            }
        }
    }
}
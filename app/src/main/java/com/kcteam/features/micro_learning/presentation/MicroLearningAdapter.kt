package com.kcteam.features.micro_learning.presentation

import android.content.Context
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.micro_learning.model.MicroLearningDataModel
import kotlinx.android.synthetic.main.inflate_micro_learning_item.view.*

class MicroLearningAdapter(private val mContext: Context, list: ArrayList<MicroLearningDataModel>?,
                           private val onItemClick: (MicroLearningDataModel) -> Unit?, private val onUpdateNoteClick: (MicroLearningDataModel) -> Unit?,
                           private val onDownloadClick: (MicroLearningDataModel) -> Unit?) :
        RecyclerView.Adapter<MicroLearningAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mList: ArrayList<MicroLearningDataModel>? = null
    private var tempList: ArrayList<MicroLearningDataModel>? = null
    private var filterList: ArrayList<MicroLearningDataModel>? = null

    init {
        inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mList = ArrayList()
        mList?.addAll(list!!)

        tempList = ArrayList()
        filterList = ArrayList()

        tempList?.addAll(list!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.inflate_micro_learning_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun getItemCount(): Int {
        return mList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                tv_category.text = mList?.get(adapterPosition)?.category_name
                tv_desc.text = mList?.get(adapterPosition)?.description
                tv_file_name.text = mList?.get(adapterPosition)?.file_name
                tv_size.text = mList?.get(adapterPosition)?.file_size
            /*    var selectedPathVideo = ""
                selectedPathVideo = AppUtils.getPhotoFilePath(mContext,mList?.get(adapterPosition)?.url.toString())
                try{
                    val thumb = ThumbnailUtils.createVideoThumbnail(selectedPathVideo, MediaStore.Video.Thumbnails.MICRO_KIND)
                    iv_thumbnail.setImageBitmap(thumb)

                }catch (e: Exception){
                    e.printStackTrace()
                    iv_thumbnail.setImageResource(R.drawable.ic_image)
                }*/
          if (!TextUtils.isEmpty(mList?.get(adapterPosition)?.thumbnail)) {
                    Glide.with(mContext)
                            .load(mList?.get(adapterPosition)?.thumbnail)
                            .apply(RequestOptions.placeholderOf(R.drawable.ic_image).error(R.drawable.ic_image))
                            .thumbnail(Glide.with(context).load(mList?.get(adapterPosition)?.url))
                            .into(iv_thumbnail)
                }
                else
                    iv_thumbnail.setImageResource(R.drawable.ic_image)

                if (mList?.get(adapterPosition)?.isVideo!!) {
                    //iv_download.visibility = View.GONE

                    if (!TextUtils.isEmpty(mList?.get(adapterPosition)?.video_duration)) {
                        tv_video_duration_header.visibility = View.VISIBLE
                        tv_video_duration.visibility = View.VISIBLE
                    }
                    else {
                        tv_video_duration_header.visibility = View.GONE
                        tv_video_duration.visibility = View.GONE
                    }
                }
                else {
                    //iv_download.visibility = View.VISIBLE
                    tv_video_duration_header.visibility = View.GONE
                    tv_video_duration.visibility = View.GONE
                }

                tv_video_duration.text = mList?.get(adapterPosition)?.video_duration

                if (mList?.get(adapterPosition)?.isDownloaded!!)
                    //iv_download.setImageResource(R.drawable.ic_micro_check_thik)
                    iv_download.setImageResource(R.drawable.ic_down_vote_arrow)
                else
                    iv_download.setImageResource(R.drawable.ic_down_vote_arrow)

                setOnClickListener {
                    onItemClick(mList?.get(adapterPosition)!!)

                    /*if (!mList?.get(adapterPosition)?.isVideo!!) {
                        mList?.get(adapterPosition)!!.isDownloaded = true
                        notifyDataSetChanged()
                    }*/
                }
                menu_microlearn_ll.isSelected = false
                menu_microlearn_ll.visibility = View.GONE

                iv_more.setOnClickListener {
                    if (!menu_microlearn_ll.isSelected) {
                        menu_microlearn_ll.isSelected = true
                        menu_microlearn_ll.visibility = View.VISIBLE
                    } else {
                        menu_microlearn_ll.isSelected = false
                        menu_microlearn_ll.visibility = View.GONE
                    }
                }

                iv_update_note.setOnClickListener {
                    onUpdateNoteClick(mList?.get(adapterPosition)!!)
                }

                iv_download.setOnClickListener {
                    onDownloadClick(mList?.get(adapterPosition)!!)

                    /*if (!mList?.get(adapterPosition)?.isVideo!!) {
                        mList?.get(adapterPosition)?.isDownloaded = true
                        notifyDataSetChanged()
                    }*/
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterList?.clear()

            tempList?.indices!!
                    .filter {
                        tempList?.get(it)?.category_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! ||
                                tempList?.get(it)?.description?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! ||
                                tempList?.get(it)?.file_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!
                    }
                    .forEach { filterList?.add(tempList?.get(it)!!) }

            results.values = filterList
            results.count = filterList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterList = results?.values as ArrayList<MicroLearningDataModel>?
                mList?.clear()
                val hashSet = HashSet<String>()
                if (filterList != null) {

                    filterList?.indices!!
                            .filter { hashSet.add(filterList?.get(it)?.id!!) }
                            .forEach { mList?.add(filterList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshList(list: ArrayList<MicroLearningDataModel>) {
        mList?.clear()
        mList?.addAll(list)

        tempList?.clear()
        tempList?.addAll(list)

        if (filterList == null)
            filterList = ArrayList()
        filterList?.clear()

        notifyDataSetChanged()
    }
}
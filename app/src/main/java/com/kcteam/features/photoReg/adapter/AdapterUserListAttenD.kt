package com.kcteam.features.photoReg.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.view.menu.ListMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.features.photoReg.model.UserListResponseModel
import com.squareup.picasso.Cache
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_user_list_face_attend.view.*
import kotlinx.android.synthetic.main.row_user_list_face_regis.view.*
import kotlinx.android.synthetic.main.row_user_list_face_regis.view.photo_reg_dd_name_tv
import kotlinx.android.synthetic.main.row_user_list_face_regis.view.photo_reg_user_name_tv

class AdapterUserListAttenD(var mContext: Context, var customerList:ArrayList<UserListResponseModel>,val listner:PhotoAttendanceListner,private val getSize: (Int) -> Unit):
        RecyclerView.Adapter<AdapterUserListAttenD.MyViewHolder>(), Filterable {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    private var mList: ArrayList<UserListResponseModel>? = null
    private var tempList: ArrayList<UserListResponseModel>? = null
    private var filterList: ArrayList<UserListResponseModel>? = null

    init {
        mList = ArrayList()
        tempList = ArrayList()
        filterList = ArrayList()

        mList?.addAll(customerList)
        tempList?.addAll(customerList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.row_user_list_face_attend, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList?.size!!
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bindItems(){
            itemView.apply {
                photo_reg_user_name_tv.text = mList?.get(adapterPosition)?.user_name
                photo_reg_dd_name_tv.text="Distributor : "+mList?.get(adapterPosition)?.ShowDDInFaceRegistration

                if(mList?.get(adapterPosition)?.RegisteredAadhaarNo!=null && mList?.get(adapterPosition)?.RegisteredAadhaarNo!!.isNotEmpty()){
                    photo_reg_user_adhaar_tv.text = "Aadhaar No : "+mList?.get(adapterPosition)?.RegisteredAadhaarNo
                }
                else{
                    photo_reg_user_adhaar_tv.text = "Aadhaar not Registered."
                }


                if(mList?.get(adapterPosition)!!.isFaceRegistered!!){
                    tv_row_user_list_face_attend_face_not.visibility=View.GONE
                    var picasso = Picasso.Builder(mContext)
                            .memoryCache(Cache.NONE)
                            .indicatorsEnabled(true)
                            .loggingEnabled(true)
                            .build()
                    picasso.load(Uri.parse(mList?.get(adapterPosition)?.face_image_link))
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .resize(500, 500)
                            .into(iv_row_face_attd_face)
                }else{
                    tv_row_user_list_face_attend_face_not.visibility=View.VISIBLE
                }

                click_for_photo_attd.setOnClickListener{listner?.getUserInfoOnLick(mList?.get(adapterPosition)!!)}
                click_for_photo_attd_report.setOnClickListener{listner?.getUserInfoAttendReportOnLick(mList?.get(adapterPosition)!!)}



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
                    .filter { tempList?.get(it)?.user_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!  ||
                            tempList?.get(it)?.user_login_id?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!}
                    .forEach { filterList?.add(tempList?.get(it)!!) }

            results.values = filterList
            results.count = filterList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterList = results?.values as ArrayList<UserListResponseModel>?
                mList?.clear()
                val hashSet = HashSet<String>()
                if (filterList != null) {

                    filterList?.indices!!
                            .filter { hashSet.add(filterList?.get(it)?.user_name!!) }
                            .forEach { mList?.add(filterList?.get(it)!!) }

                    getSize(mList?.size!!)

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshList(list: ArrayList<UserListResponseModel>) {
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
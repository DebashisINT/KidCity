package com.kcteam.features.photoReg.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.features.photoReg.model.UserListResponseModel
import kotlinx.android.synthetic.main.row_user_list_face_regis.view.*

class AdapterUserList (var mContext: Context,var customerList:ArrayList<UserListResponseModel>,val listner:PhotoRegUserListner,private val getSize: (Int) -> Unit):
        RecyclerView.Adapter<AdapterUserList.MyViewHolder>(), Filterable {

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
        val v = layoutInflater.inflate(R.layout.row_user_list_face_regis, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList?.size!!
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    inner class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bindItems(){
            itemView.apply {
                photo_reg_user_name_tv.text = mList?.get(adapterPosition)?.user_name
                photo_reg_user_ph_tv.text = " "+mList?.get(adapterPosition)?.user_login_id
                photo_reg_dd_name_tv.text="Distributor : "+mList?.get(adapterPosition)?.ShowDDInFaceRegistration
//                photo_reg_dd_name_tv.text="Distributor Surise Manali Himachal Limited "
                click_for_photo_reg_tv.setOnClickListener{listner?.getUserInfoOnLick(mList?.get(adapterPosition)!!)}
                click_for_update_type_tv.setOnClickListener{listner?.updateTypeOnClick(mList?.get(adapterPosition)!!)}
                sync_whatsapp_iv.setOnClickListener{listner?.getWhatsappOnLick(mList?.get(adapterPosition)?.user_login_id.toString())}
                photo_reg_user_ph_tv.setOnClickListener{listner?.getPhoneOnLick(mList?.get(adapterPosition)?.user_login_id.toString())}
                //sync_delete_iv.setOnClickListener{listner?.deletePicOnLick(mList?.get(adapterPosition)!!)}
                sync_delete_iv_red.setOnClickListener{listner?.deletePicOnLick(mList?.get(adapterPosition)!!)}

                iv_aadhaar_ion.setOnClickListener{listner?.getAadhaarOnLick(mList?.get(adapterPosition)!!)}
                if(mList?.get(adapterPosition)?.IsAadhaarRegistered!!){
                    iv_aadhaar_ion.setImageResource(R.drawable.ic_aadhaar_icon_done)
                }else{
                    iv_aadhaar_ion.setImageResource(R.drawable.ic_aadhaar_icon)
                }

                if(mList?.get(adapterPosition)?.isFaceRegistered!!){
                    sync_image_view.setOnClickListener{listner?.viewPicOnLick(mList?.get(adapterPosition)?.face_image_link!!,mList?.get(adapterPosition)?.user_name!!)}
                }


                if(mList?.get(adapterPosition)?.isFaceRegistered!!){
                    sync_status_failed_iv.visibility=View.GONE
                    sync_status_iv.visibility=View.VISIBLE
                    //sync_delete_iv.setImageResource(R.drawable.trash_red)

                    //sync_delete_iv_red.visibility=View.VISIBLE
                    //sync_delete_iv.visibility=View.GONE

                    //sync_delete_iv.isEnabled=true
                    click_for_photo_reg_tv.isEnabled=false
                    try{
                        click_for_photo_reg_tv.text="Registered on "+mList?.get(adapterPosition)?.registration_date_time!!
                    }
                    catch (ex:java.lang.Exception){

                    }
                    click_for_photo_reg_tv.setTextColor(resources.getColor(R.color.color_custom_green))
                }else{
                    sync_status_failed_iv.visibility=View.VISIBLE
                    sync_status_iv.visibility=View.GONE
                    //sync_delete_iv.setImageResource(R.drawable.ic_delete)

                    //sync_delete_iv_red.visibility=View.GONE
                    //sync_delete_iv.visibility=View.VISIBLE

                    //sync_delete_iv.isEnabled=false
                    click_for_photo_reg_tv.isEnabled=true
                    click_for_photo_reg_tv.text="Click for Registration"
                    click_for_photo_reg_tv.setTextColor(resources.getColor(R.color.color_custom_red))
                }


                //if(mList?.get(adapterPosition)?.IsPhotoDeleteShow!!){
                if(Pref.IsPhotoDeleteShow){
                    sync_delete_iv_red.visibility=View.VISIBLE
                    ll_row_user_list_face_regis_tagline.visibility=View.VISIBLE
                }else{
                    sync_delete_iv_red.visibility=View.GONE
                    ll_row_user_list_face_regis_tagline.visibility=View.GONE
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
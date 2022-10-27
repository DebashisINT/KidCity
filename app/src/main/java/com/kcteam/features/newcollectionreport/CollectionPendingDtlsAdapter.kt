package com.kcteam.features.newcollectionreport

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import kotlinx.android.synthetic.main.inflater_collect_pend_dtls_list_item.view.*
import kotlinx.android.synthetic.main.inflater_collect_pend_list_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class CollectionPendingDtlsAdapter(mContext: Context, list: List<PendingCollDtlsData>, val listner: PendingCollDtlsListner) :
        RecyclerView.Adapter<CollectionPendingDtlsAdapter.MyViewHolder>() {

    private var mList: ArrayList<PendingCollDtlsData>? = null
    private lateinit var adapter :AdapterCollSubList
    private lateinit var contxT:Context

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    init {
        contxT=mContext
        mList = ArrayList()
        mList!!.addAll(list)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflater_collect_pend_dtls_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList!!.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems() {
            itemView.apply {
                if(mList!!.get(adapterPosition).coll_list.size>0){
                    ll_root_inf_coll_pend_sub_coll_root.visibility=View.VISIBLE
                    adapter=AdapterCollSubList(contxT,mList!!.get(adapterPosition).coll_list)
                rv_inf_coll_pend_coll_list.adapter=adapter
                }else{
                    ll_root_inf_coll_pend_sub_coll_root.visibility=View.GONE
                }

                tv_inf_coll_pend_shop_name.text="Shop Name : "+mList!!.get(adapterPosition).shop_name

                tv_inf_coll_pend_ord_id.text=mList!!.get(adapterPosition).order_id
                tv_inf_coll_pend_ord_dt.text=mList!!.get(adapterPosition).order_date
                tv_inf_coll_pend_ord_amt.text=mList!!.get(adapterPosition).order_amt

                if(mList!!.get(adapterPosition).invoice_id.equals("0")){
                    tv_inf_coll_pend_inv_id.text="N/A"
                }else{
                    tv_inf_coll_pend_inv_id.text=mList!!.get(adapterPosition).invoice_id
                }

                if(mList!!.get(adapterPosition).invoice_date.equals("0")){
                    tv_inf_coll_pend_inv_dt.text="N/A"
                }else{
                    tv_inf_coll_pend_inv_dt.text=AppUtils.convertToDateLikeOrderFormat(mList!!.get(adapterPosition).invoice_date)
                }
                if(mList!!.get(adapterPosition).invoice_amt.equals("0")){
                    tv_inf_coll_pend_inv_amt.text="N/A"
                }else{
                    tv_inf_coll_pend_inv_amt.text=String.format("%.2f",mList!!.get(adapterPosition).invoice_amt.toDouble())
                }

                btn_inf_coll_pend_coll.text="Collect ( Pending Amt. "+mList!!.get(adapterPosition).pendingAmt+" )"
                btn_inf_coll_pend_coll.setOnClickListener { listner.getInfoDtlsOnLick(mList!!.get(adapterPosition)) }

                if(CustomStatic.IsCollectionViewFromTeam){
                    btn_inf_coll_pend_coll.visibility=View.GONE
                }else{
                    btn_inf_coll_pend_coll.visibility=View.VISIBLE
                }

            }

        }
    }
}

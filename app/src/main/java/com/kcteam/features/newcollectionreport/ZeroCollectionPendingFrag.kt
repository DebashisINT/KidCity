package com.kcteam.features.newcollectionreport

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.shopdetail.presentation.AddCollectionWithOrderDialog
import com.pnikosis.materialishprogress.ProgressWheel

class ZeroCollectionPendingFrag: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var tvNodata: TextView
    private lateinit var rv_CollectionList:RecyclerView
    private lateinit var adapter:CollectionPendingListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_zero_collection_pending, container, false)
        initView(view)

        return view
    }

    private fun initView(view: View){
        rv_CollectionList = view.findViewById(R.id.rv_frag_zero_coll_pending_list)
        tvNodata = view.findViewById(R.id.tv_coll_pend_dtls_list_noData)

        if(Pref.IsCollectionEntryConsiderOrderOrInvoice){
            //AppDatabase.getDBInstance()!!.billingDao().updateAmt("25","54679_bill_1653804212274")
            var pendingCollDataList:ArrayList<PendingCollData> = ArrayList()
            //val shopList = AppDatabase.getDBInstance()?.addShopEntryDao()?.all as ArrayList<AddShopDBModelEntity>
            val shopList = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopIdHasOrder() as ArrayList<AddShopDBModelEntity>
            if(shopList.size>0){
                for(i in 0..shopList.size-1){
                    var totalInvAmt ="0"
                    var totalCollectionAmt ="0"
                    var dueAmt ="0"


                    var isShopOrder = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopOrder.size!=0){
                        for(j in 0..isShopOrder.size-1){
                            var ob=AppDatabase.getDBInstance()!!.billingDao().getInvoiceSumAmt(isShopOrder.get(j).order_id.toString())
                            if(ob!=null)
                                totalInvAmt=String.format("%.2f",(totalInvAmt.toDouble()+ob.toDouble())).toString()
                        }
                    }


                    var isShopCollection = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopCollection.size!=0){
                        totalCollectionAmt = AppDatabase.getDBInstance()!!.collectionDetailsDao().getCollectSumAmt(shopList.get(i).shop_id.toString())
                    }

                    try{
                        dueAmt = String.format("%.2f",(totalInvAmt.toDouble()-totalCollectionAmt.toDouble()).toDouble()).toString()
                    }catch (ex:Exception){

                    }
                    if(dueAmt.contains("-") || dueAmt.equals("0.0") || dueAmt.equals("0.00")){
                        dueAmt="0"
                    }

                    if(totalInvAmt.toDouble() == dueAmt.toDouble())
                        pendingCollDataList.add(PendingCollData(shopList.get(i).shopName,dueAmt,shopList.get(i).shop_id.toString()))
                }
            }
            if(pendingCollDataList.size>0){
                tvNodata.visibility=View.GONE
                initAdapter(pendingCollDataList)
            }else{
                tvNodata.visibility=View.VISIBLE
            }
        }
        else{
            var pendingCollDataList:ArrayList<PendingCollData> = ArrayList()
            val shopList = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopIdHasOrder() as ArrayList<AddShopDBModelEntity>
            if(shopList.size>0){
                for(i in 0..shopList.size-1){
                    var totalOrderAmt ="0"
                    var totalCollectionAmt ="0"
                    var dueAmt ="0"

                    var isShopOrder = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopOrder.size!=0){
                        totalOrderAmt=AppDatabase.getDBInstance()!!.orderDetailsListDao().getOrderSumAmt(shopList.get(i).shop_id.toString()).toString()

                        var isShopCollection = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                        if(isShopCollection.size!=0){
                            totalCollectionAmt = AppDatabase.getDBInstance()!!.collectionDetailsDao().getCollectSumAmt(shopList.get(i).shop_id.toString())
                        }
                        try{
                            dueAmt = (totalOrderAmt.toDouble()-totalCollectionAmt.toDouble()).toString()
                        }catch (ex:Exception){

                        }
                        if(dueAmt.contains("-")){
                            dueAmt="0"
                        }
                        if(totalOrderAmt.toDouble() == dueAmt.toDouble())
                            pendingCollDataList.add(PendingCollData(shopList.get(i).shopName,dueAmt,shopList.get(i).shop_id.toString()))
                    }

                }
            }
            if(pendingCollDataList.size>0){
                tvNodata.visibility=View.GONE
                initAdapter(pendingCollDataList)
            }else{
                tvNodata.visibility=View.VISIBLE
            }
        }
    }


    private fun initAdapter(list :ArrayList<PendingCollData>){
        adapter=CollectionPendingListAdapter(mContext,list,object :PendingCollListner{
            override fun getUserInfoOnLick(obj: PendingCollData) {
                (mContext as DashboardActivity).loadFragment(FragType.CollectionPendingDtlsFrag, true, obj)
            }
        })
        rv_CollectionList.adapter=adapter
    }


    override fun onResume() {
        super.onResume()

    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

}
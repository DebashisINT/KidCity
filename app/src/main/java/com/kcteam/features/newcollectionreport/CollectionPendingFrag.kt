package com.kcteam.features.newcollectionreport

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.photoReg.ProtoRegistrationFragment
import com.kcteam.features.shopdetail.presentation.AddCollectionDialog
import java.util.*
import kotlin.collections.ArrayList

class CollectionPendingFrag : BaseFragment(),View.OnClickListener{

    private lateinit var mContext: Context

    private lateinit var rv_CollectionList:RecyclerView
    private lateinit var adapter:CollectionPendingListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_collection_pending, container, false)
        initView(view)

        return view
    }

    private fun initView(view: View){
        rv_CollectionList=view.findViewById(R.id.rv_frag_coll_pending_list)

        getData()
    }

    fun getData(){
        if(Pref.IsCollectionEntryConsiderOrderOrInvoice){
            //AppDatabase.getDBInstance()!!.billingDao().updateAmt("59","54679_bill_1653893428835")
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
                                totalInvAmt=(totalInvAmt.toDouble()+ob.toDouble()).toString()
                        }
                    }

                    var isShopCollection = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopCollection.size!=0){
                        totalCollectionAmt = AppDatabase.getDBInstance()!!.collectionDetailsDao().getCollectSumAmt(shopList.get(i).shop_id.toString())
                    }

                    try{
                        dueAmt = (totalInvAmt.toDouble()-totalCollectionAmt.toDouble()).toString()
                    }catch (ex:Exception){

                    }
                    if(dueAmt.contains("-") || dueAmt.equals("0.0")){
                        dueAmt="0"
                    }
                    dueAmt=String.format("%.2f", dueAmt.toDouble())

                    if(totalCollectionAmt.toDouble() != totalInvAmt.toDouble() || totalInvAmt.toDouble()==0.0)
                        pendingCollDataList.add(PendingCollData(shopList.get(i).shopName,dueAmt,shopList.get(i).shop_id.toString()))
                }
            }
            if(pendingCollDataList.size>0){
                initAdapter(pendingCollDataList)
            }
        }
        else{
            var pendingCollDataList:ArrayList<PendingCollData> = ArrayList()
            //val shopList = AppDatabase.getDBInstance()?.addShopEntryDao()?.all as ArrayList<AddShopDBModelEntity>
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
                        if(dueAmt.contains("-") || dueAmt.equals("0.0")){
                            dueAmt="0"
                        }
                        dueAmt=String.format("%.2f", dueAmt.toDouble())
                        if(!dueAmt.equals("0.00"))
                            pendingCollDataList.add(PendingCollData(shopList.get(i).shopName,dueAmt,shopList.get(i).shop_id.toString()))
                    }

                }
            }
            if(pendingCollDataList.size>0){
                initAdapter(pendingCollDataList)
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initAdapter(list :ArrayList<PendingCollData>){
        adapter=CollectionPendingListAdapter(mContext,list,object :PendingCollListner{
            override fun getUserInfoOnLick(obj: PendingCollData) {
                (mContext as DashboardActivity).loadFragment(FragType.CollectionPendingDtlsFrag, true, obj)
            }
        })
        rv_CollectionList.adapter=adapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    fun updateView(){
        getData()
    }



}
package com.kcteam.features.member.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.ShopDtlsTeamEntity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.member.model.TeamListDataModel
import com.kcteam.features.newcollectionreport.*
import com.pnikosis.materialishprogress.ProgressWheel

class CollectionPendingTeamFrag : BaseFragment(),View.OnClickListener{

    private lateinit var mContext: Context

    private lateinit var rv_CollectionList:RecyclerView
    private lateinit var adapter: CollectionPendingListAdapter
    private lateinit var adapterTeamDtls: CollectionPendingDtlsAdapter
    private lateinit var UserName: TextView
    var objPendingList: ArrayList<PendingCollDtlsData> = ArrayList()

    private lateinit var progress_wheel: ProgressWheel

    companion object {
        var mobj: TeamListDataModel? = null
        fun getInstance(objects: Any): CollectionPendingTeamFrag {
            val collectionPendingteamFrag = CollectionPendingTeamFrag()
            if (objects != null) {
                if (objects is TeamListDataModel)
                    this.mobj = objects
            }
            return collectionPendingteamFrag
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context


    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_collection_pending_dtls_team, container, false)
        initView(view)

        return view
    }

    private fun initView(view: View){
        rv_CollectionList=view.findViewById(R.id.rv_frag_coll_pending_dtle_team_list)
        UserName = view.findViewById(R.id.tv_frag_coll_pending_dtle_team_userName)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

            try{
                UserName.text = mobj?.user_name!!
            }catch (ex:Exception){
                UserName.text = ""
            }


        getData()
    }

    fun getData(){
        if(Pref.IsCollectionEntryConsiderOrderOrInvoice){
            //AppDatabase.getDBInstance()!!.billingDao().updateAmt("59","54679_bill_1653893428835")
            var pendingCollDataList:ArrayList<PendingCollData> = ArrayList()
            //val shopList = AppDatabase.getDBInstance()?.addShopEntryDao()?.all as ArrayList<AddShopDBModelEntity>
            val shopList = AppDatabase.getDBInstance()?.shopDtlsTeamDao()?.getShopIdHasOrder() as ArrayList<ShopDtlsTeamEntity>
            if(shopList.size>0){
                for(i in 0..shopList.size-1){
                    var totalInvAmt ="0"
                    var totalCollectionAmt ="0"
                    var dueAmt ="0"

                    var isShopOrder = AppDatabase.getDBInstance()!!.orderDtlsTeamDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopOrder.size!=0){
                        for(j in 0..isShopOrder.size-1){
                            var ob=AppDatabase.getDBInstance()!!.billDtlsTeamDao().getInvoiceSumAmt(isShopOrder.get(j).order_id.toString())
                            if(ob!=null)
                                totalInvAmt=(totalInvAmt.toDouble()+ob.toDouble()).toString()
                        }
                    }

                    var isShopCollection = AppDatabase.getDBInstance()!!.collDtlsTeamDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopCollection.size!=0){
                        totalCollectionAmt = AppDatabase.getDBInstance()!!.collDtlsTeamDao().getCollectSumAmt(shopList.get(i).shop_id.toString())
                    }

                    try{
                        dueAmt = (totalInvAmt.toDouble()-totalCollectionAmt.toDouble()).toString()
                    }catch (ex:Exception){

                    }
                    if(dueAmt.contains("-")){
                        dueAmt="0"
                    }
                    pendingCollDataList.add(PendingCollData(shopList.get(i).shop_name!!,dueAmt,shopList.get(i).shop_id.toString()))
                }
            }
            if(pendingCollDataList.size>0){
                //initAdapter(pendingCollDataList)

                objPendingList=ArrayList()
                for(l in 0..pendingCollDataList.size-1){
                    getDataDtls(pendingCollDataList.get(l).shop_id,pendingCollDataList.get(l).shopName)
                }
                initAdapter(objPendingList)
            }
        }
        else{
            var pendingCollDataList:ArrayList<PendingCollData> = ArrayList()
            //val shopList = AppDatabase.getDBInstance()?.addShopEntryDao()?.all as ArrayList<AddShopDBModelEntity>
            val shopList = AppDatabase.getDBInstance()?.shopDtlsTeamDao()?.getShopIdHasOrder() as ArrayList<ShopDtlsTeamEntity>
            if(shopList.size>0){
                for(i in 0..shopList.size-1){
                    var totalOrderAmt ="0"
                    var totalCollectionAmt ="0"
                    var dueAmt ="0"

                    var isShopOrder = AppDatabase.getDBInstance()!!.orderDtlsTeamDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                    if(isShopOrder.size!=0){
                        totalOrderAmt=AppDatabase.getDBInstance()!!.orderDtlsTeamDao().getOrderSumAmt(shopList.get(i).shop_id.toString()).toString()

                        var isShopCollection = AppDatabase.getDBInstance()!!.collDtlsTeamDao().getListAccordingToShopId(shopList.get(i).shop_id.toString())
                        if(isShopCollection.size!=0){
                            totalCollectionAmt = AppDatabase.getDBInstance()!!.collDtlsTeamDao().getCollectSumAmt(shopList.get(i).shop_id.toString())
                        }
                        try{
                            dueAmt = (totalOrderAmt.toDouble()-totalCollectionAmt.toDouble()).toString()
                        }catch (ex:Exception){

                        }
                        if(dueAmt.contains("-")){
                            dueAmt="0"
                        }
                        pendingCollDataList.add(PendingCollData(shopList.get(i).shop_name!!,dueAmt,shopList.get(i).shop_id.toString()))
                    }

                }
            }
            if(pendingCollDataList.size>0){
                //initAdapter(pendingCollDataList)

                objPendingList=ArrayList()
                for(l in 0..pendingCollDataList.size-1){
                    getDataDtls(pendingCollDataList.get(l).shop_id,pendingCollDataList.get(l).shopName)
                }
                initAdapter(objPendingList)
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    /*private fun initAdapter(list :ArrayList<PendingCollData>){
        adapter=CollectionPendingListAdapter(mContext,list,object : PendingCollListner {
            override fun getUserInfoOnLick(obj: PendingCollData) {
                (mContext as DashboardActivity).loadFragment(FragType.CollectionPendingTeamDtlsFrag, true, obj)
            }
        })
        rv_CollectionList.adapter=adapter
    }*/

    private fun initAdapter(list: ArrayList<PendingCollDtlsData>) {
        CustomStatic.IsCollectionViewFromTeam = true
        adapterTeamDtls = CollectionPendingDtlsAdapter(mContext, list, object : PendingCollDtlsListner {
            override fun getInfoDtlsOnLick(obj: PendingCollDtlsData) {

            }
        })
        rv_CollectionList.adapter = adapterTeamDtls
    }

    private fun getDataDtls(shop_ID:String,shop_name:String){
        var objList: ArrayList<PendingCollDtlsData> = ArrayList()
        var orderList = AppDatabase.getDBInstance()?.orderDtlsTeamDao()?.getListAccordingToShopId(shop_ID)
        if (orderList != null && orderList.size > 0) {
            for (i in 0..orderList.size - 1) {
                var objPending: PendingCollDtlsData = PendingCollDtlsData("0", "0", "0", "0", "0", "0",
                    "0", "0", ArrayList<CollectionList>(), "","")
                objPending.shop_id = shop_ID
                objPending.shop_name = shop_name
                objPending.order_id = orderList.get(i).order_id.toString()
                objPending.order_date = orderList.get(i).only_date.toString()
                objPending.order_amt = orderList.get(i).amount.toString()

                var bDtlList = AppDatabase.getDBInstance()!!.billDtlsTeamDao().getDataOrderIdWise(objPending.order_id.toString())
                if(bDtlList!= null && bDtlList.size>0){
                    objPending.bill_id=bDtlList.get(0).bill_id
                }


                var invList = AppDatabase.getDBInstance()!!.billDtlsTeamDao().getDataOrderIdWise(orderList.get(i).order_id.toString()!!)
                if (invList != null && invList.size > 0) {
                    objPending.invoice_id = invList.get(0).invoice_no
                    objPending.invoice_date = invList.get(0).invoice_date
                    objPending.invoice_amt = invList.get(0).invoice_amount
                }

                var totalCollAmt=0.0
                var totalPendingAmt=objPending.order_amt

                var collList = AppDatabase.getDBInstance()?.collDtlsTeamDao()?.getListOrderWise(orderList.get(i).order_id!!)
                if (collList != null && collList.size > 0) {
                    for (k in 0..collList.size - 1) {
                        var collectionObj: CollectionList = CollectionList("0", "0", "0")
                        collectionObj.coll_id = collList.get(k).collection_id.toString()
                        collectionObj.coll_amt = collList.get(k).collection.toString()
                        collectionObj.coll_date = collList.get(k).date.toString()
                        objPending.coll_list.add(collectionObj)
                        try{
                            totalCollAmt=totalCollAmt+collList.get(k).collection!!.toDouble()
                        }catch (ex:Exception){
                            totalCollAmt=totalCollAmt+0
                        }
                    }
                }

                totalPendingAmt=(objPending.order_amt.toDouble()-totalCollAmt.toDouble()).toString()

                var totalInvAmt ="0"
                if(Pref.IsCollectionEntryConsiderOrderOrInvoice){
                    var ob=AppDatabase.getDBInstance()!!.billDtlsTeamDao().getInvoiceSumAmt(objPending.order_id.toString())
                    if(ob!=null)
                        totalInvAmt=(totalInvAmt.toDouble()+ob.toDouble()).toString()
                    else{
                        totalInvAmt="0"
                    }

                    totalPendingAmt=(totalInvAmt.toDouble()-totalCollAmt.toDouble()).toString()
                    if(totalPendingAmt.contains("-")){
                        totalPendingAmt="0"
                    }
                }


                objPending.pendingAmt=totalPendingAmt
                if(totalPendingAmt.equals("0.0") || totalPendingAmt.equals("0")){

                }else{
                    objPendingList.add(objPending)
                }

            }
        }

        if (objPendingList.size > 0){
            var tt="asd"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }

    fun updateView(){
        getData()
    }



}
package com.kcteam.features.stockCompetetorStock

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.CcompetetorStockEntryModelEntity
import com.kcteam.app.domain.CompetetorStockEntryProductModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.orderList.model.NewOrderListResponseModel
import com.kcteam.features.stockAddCurrentStock.UpdateShopStockFragment
import com.kcteam.features.stockCompetetorStock.`interface`.CompetetorStockOnClick
import com.kcteam.features.stockCompetetorStock.adapter.AdapterCompetetorStock
import com.kcteam.features.stockCompetetorStock.api.AddCompStockProvider
import com.kcteam.features.stockCompetetorStock.model.CompetetorStockGetData
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class CompetetorStockFragment: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var mRv_competetorStock: RecyclerView
    private lateinit var ll_competetorAdd:LinearLayout

    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_addr_TV: AppCustomTextView
    private lateinit var myshop_contact_TV: AppCustomTextView


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var mAddShopDataObj: AddShopDBModelEntity? = null
        var shop_id:String = ""
        fun getInstance(objects: Any): CompetetorStockFragment {
            val competetorStockFragment = CompetetorStockFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                shop_id=objects.toString()
                mAddShopDataObj = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)
            }
            return competetorStockFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_competetor_stock, container, false)
        initView(view)
        return view
    }

    private fun initView(view:View){
        mRv_competetorStock=view!!.findViewById(R.id.rv_competetor_stock_list)
        mRv_competetorStock.layoutManager=LinearLayoutManager(mContext)
        ll_competetorAdd=view!!.findViewById(R.id.ll_frag_competetor_shop_stock_add)
        ll_competetorAdd.setOnClickListener(this)

        myshop_name_TV = view!!.findViewById(R.id.myshop_name_TV)
        myshop_addr_TV = view!!.findViewById(R.id.myshop_address_TV)
        myshop_contact_TV = view!!.findViewById(R.id.tv_contact_number)

        myshop_name_TV.text=mAddShopDataObj?.shopName
        myshop_addr_TV.text=mAddShopDataObj?.address
        myshop_contact_TV.text="Owner Contact Number: " + mAddShopDataObj?.ownerContactNumber.toString()


    }

    override fun onResume() {
        super.onResume()

        var comListAll= AppDatabase.getDBInstance()!!.competetorStockEntryDao().getCompetetorStockAll()
        if (comListAll != null && comListAll?.isNotEmpty()){
            getStockList()
        }else{
            if (AppUtils.isOnline(mContext)){
                getCompStockApi()
            }else{
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }
        }

    }

    private fun getStockList(){
        var list = AppDatabase.getDBInstance()?.competetorStockEntryDao()?.getCompetetorStockAllByShopID(shop_id)
        if(list?.size!! >0){
            mRv_competetorStock.adapter= AdapterCompetetorStock(mContext,list,object : CompetetorStockOnClick{
                override fun stockListOnClickView(stockID: String) {
                    (mContext as DashboardActivity).loadFragment(FragType.ViewComStockProductDetails, true, stockID)
                }

                override fun stockListOnClickSync(stockID: String) {

                }
            })
        }else{
            return
        }
    }

    override fun onClick(p0: View?) {
        if(p0!=null){
            when(p0.id){
                R.id.ll_frag_competetor_shop_stock_add -> {
                    if (Pref.isAddAttendence){
                        (mContext as DashboardActivity).loadFragment(FragType.AddCompetetorStockFragment, true, mAddShopDataObj!!)
                    }
                }
            }
        }
    }

    fun update() {
        getStockList()
    }

    private fun getCompStockApi(){
        try{
            val repository = AddCompStockProvider.provideCompStockRepositiry()
            BaseActivity.compositeDisposable.add(
                    repository.getCompStockList(Pref.session_token!!, Pref.user_id!!, "")
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                XLog.d("CompetitorStock/CompetitorStockList : RESPONSE " + result.status)
                                val response = result as CompetetorStockGetData
                                if (response.status == NetworkConstant.SUCCESS){

                                    if (response.competitor_stock_list!! != null && response.competitor_stock_list!!.isNotEmpty()){
                                        doAsync {

                                            for(i in response.competitor_stock_list?.indices!!){
                                                var obj= CcompetetorStockEntryModelEntity()
                                                obj.user_id=Pref?.user_id!!
                                                obj.competitor_stock_id=response.competitor_stock_list?.get(i)?.competitor_stock_id!!
                                                obj.shop_id=response.competitor_stock_list?.get(i)?.shop_id!!
                                                obj.visited_datetime=response.competitor_stock_list?.get(i)?.visited_datetime!!
                                                obj.visited_date=response.competitor_stock_list?.get(i)?.visited_datetime?.take(10)
                                                obj.total_product_stock_qty=response.competitor_stock_list?.get(i)?.total_qty!!
                                                obj.isUploaded=true
                                                AppDatabase.getDBInstance()?.competetorStockEntryDao()?.insert(obj)

                                                val proDuctList=response.competitor_stock_list?.get(i)?.product_list
                                                for(j in proDuctList?.indices!!){
                                                    var objjj= CompetetorStockEntryProductModelEntity()
                                                    objjj.user_id=Pref.user_id
                                                    objjj.competitor_stock_id=response.competitor_stock_list?.get(i)?.competitor_stock_id!!
                                                    objjj.shop_id= response.competitor_stock_list?.get(i)?.shop_id!!

                                                    objjj.brand_name=proDuctList?.get(j)?.brand_name
                                                    objjj.product_name=proDuctList?.get(j)?.product_name
                                                    objjj.qty=proDuctList?.get(j)?.qty
                                                    objjj.mrp=proDuctList?.get(j)?.mrp
                                                    objjj.isUploaded=true
                                                    AppDatabase.getDBInstance()?.competetorStockEntryProductDao()?.insert(objjj)
                                                }
                                            }

                                            uiThread {
                                                getStockList()
                                            }
                                        }

                                    }else{
                                        getStockList()
                                    }
                                }
                            },{error ->
                                if (error == null) {
                                    XLog.d("CompetitorStock/CompetitorStockList : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
                                } else {
                                    XLog.d("CompetitorStock/CompetitorStockList : ERROR " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                            })
            )
        }
        catch (ex:Exception){
            XLog.d("CompetitorStock/CompetitorStockList : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
        }
    }

}
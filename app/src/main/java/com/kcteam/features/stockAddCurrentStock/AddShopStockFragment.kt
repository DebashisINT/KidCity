package com.kcteam.features.stockAddCurrentStock

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseActivity.Companion.compositeDisposable
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dashboard.presentation.DashboardFragment
import com.kcteam.features.location.model.ShopRevisitStatusRequestData
import com.kcteam.features.location.shopRevisitStatus.ShopRevisitStatusRepositoryProvider
import com.kcteam.features.login.api.productlistapi.ProductListRepoProvider
import com.kcteam.features.login.model.productlistmodel.ProductRateDataModel
import com.kcteam.features.login.model.productlistmodel.ProductRateListResponseModel
import com.kcteam.features.shopdetail.presentation.ShopDetailFragment
import com.kcteam.features.stockAddCurrentStock.`interface`.ProductListOnClick
import com.kcteam.features.stockAddCurrentStock.adapter.AdapterProductList
import com.kcteam.features.stockAddCurrentStock.api.ShopAddStockProvider
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_add_shop_stock.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception
import java.math.RoundingMode
import java.text.DecimalFormat

class AddShopStockFragment: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var mRv_productList: RecyclerView
    private var productRateList: ArrayList<ProductRateDataModel>? = null
    private var productRateListDb: ArrayList<ProductRateEntity>? = null
    private var productList: ArrayList<ProductListEntity>? = null

    private var productListWithQTY: ArrayList<String>? = ArrayList()

    private var isForDb = false
    private lateinit var totalQty:TextView

    private  var qtyList:ArrayList<Double> = ArrayList()
    private  var idList:ArrayList<Int> = ArrayList()
    private var total:Double = 0.0
    private lateinit var saveStock:AppCustomTextView



    lateinit var ll_root:LinearLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object{
        var mAddShopDataObj: AddShopDBModelEntity? = null
        fun getInstance(objects: Any): AddShopStockFragment {
            val addStockFragment = AddShopStockFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is AddShopDBModelEntity) {
                    mAddShopDataObj = objects
                }
            }
            return addStockFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_shop_stock, container, false)
        initView(view)
        return view
    }

    private fun initView(view:View){
        progress_wheel = view.findViewById(R.id.progress_wheel)
        totalQty = view.findViewById(R.id.tv_row_add_stock_total_qty)
        saveStock = view.findViewById(R.id.tv_save_shop_stock)
        saveStock.setOnClickListener(this)
        totalQty.text=0.toString()
        progress_wheel.stopSpinning()
        mRv_productList=view!!.findViewById(R.id.rv_frag_add_shop_stock_product_list)

        ll_root=view!!.findViewById(R.id.ll_root)


        productList=AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
        if(productList!=null && productList!!.size>=1){
            setProductAdapter(productList!!)
        }else{
            getProductRateListApi(mAddShopDataObj!!.shop_id)
        }






    }

    private fun getProductRateListApi(shopId:String) {

        if (!AppUtils.isOnline(mContext)) {
            if (Pref.isRateOnline)
                (mContext as DashboardActivity).showSnackMessage("Internet not connected. Product Rates will show as ZERO(0). You may put manually.", 10000)
            return
        }

        BaseActivity.isApiInitiated = true
        val repository = ProductListRepoProvider.productListProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getProductRateList(shopId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ProductRateListResponseModel
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.product_rate_list != null && response.product_rate_list!!.size > 0) {
                                    if (!isForDb) {
                                        progress_wheel.stopSpinning()
                                        productRateList = response.product_rate_list
                                        AppUtils.saveSharedPreferencesProductRateList(mContext, productRateList!!)

                                        if (Pref.isShowAllProduct) {
                                            productList = AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
                                            setProductAdapter(productList!!)
                                        }

                                    } else {
                                        doAsync {

                                            response.product_rate_list!!.forEach {
                                                val productRate = ProductRateEntity()
                                                AppDatabase.getDBInstance()?.productRateDao()?.insert(productRate.apply {
                                                    product_id = it.product_id
                                                    //rate = it.rate
                                                    stock_amount = it.stock_amount
                                                    stock_unit = it.stock_unit
                                                    isStockShow = it.isStockShow
                                                    isRateShow = it.isRateShow
                                                })
                                            }

                                            uiThread {
                                                productRateListDb = AppDatabase.getDBInstance()?.productRateDao()?.getAll() as ArrayList<ProductRateEntity>?
                                                progress_wheel.stopSpinning()
                                                isForDb = false
                                            }
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                    if (!isForDb && Pref.isShowAllProduct) {
                                        productList = AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
                                        setProductAdapter(productList!!)
                                    }

                                    if (isForDb)
                                        isForDb = false
                                }
                            } else {
                                progress_wheel.stopSpinning()

                                if (/*!isForDb && */Pref.isShowAllProduct) {
                                    productList = AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
                                    setProductAdapter(productList!!)
                                }

                                if (isForDb)
                                    isForDb = false
                            }


                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()

                            if (/*!isForDb &&*/ Pref.isShowAllProduct) {
                                productList = AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
                                setProductAdapter(productList!!)
                            }

                            if (isForDb)
                                isForDb = false
                        })
        )
    }

    private fun setProductAdapter(mProductList: ArrayList<ProductListEntity>){
        for(i in 0..mProductList.size-1){
            productListWithQTY?.add("0.0")
        }
        mRv_productList.layoutManager=LinearLayoutManager(mContext)
        mRv_productList.adapter= AdapterProductList(mContext,mProductList,productListWithQTY,object: ProductListOnClick{
            override fun productListOnClick(qty: Double, id: Int) {
            //override fun productListOnClick(qtyList:ArrayList<String>, id: Int) {

                var tot:Double=0.0
                for(i in 0..productListWithQTY!!.size-1){
                    if(productListWithQTY!!.get(i).length>0){
                        tot+=productListWithQTY!!.get(i).toDouble()
                    }
                }
                totalQty.text=tot.toString()

                /*if(qty==0){
                    for(i in 0..idList.size-1){
                        if(idList[i]==id){
                            qtyList[i]=0
                        }
                    }
                }else{
                    qtyList.add(qty)
                    idList.add(id)
                }*/

               /* if(qtyList.size>0){
                    total=0
                    for(i in 0..qtyList.size-1){
                        total+=qtyList[i]
                    }
                    totalQty.text=total.toString()
                }else{
                    totalQty.text=0.toString()
                }*/



            }
        })
    }
    var vi:LayoutInflater ? = null
    var viewC: View? = null
    override fun onClick(p0: View?) {
        if(p0!=null){
            when(p0.id){
                R.id.tv_save_shop_stock ->{
                    for(i in 0..productListWithQTY!!.size-1){
                        if(roundOffDecimal(productListWithQTY!!.get(i).toDouble())!=0.0)
                        {
                            total+= roundOffDecimal(productListWithQTY!!.get(i).toDouble())
                        }
                    }
                    if(total == 0.0){
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_value))
                        return
                    }


                    val simpleDialogg = Dialog(mContext)
                    simpleDialogg.setCancelable(false)
                    simpleDialogg.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialogg.setContentView(R.layout.dialog_yes_no)
                    val dialogHeader = simpleDialogg.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                    dialogHeader.text="Do you want to submit ?"
                    val dialogYes = simpleDialogg.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                    val dialogNo = simpleDialogg.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView

                    dialogYes.setOnClickListener( { view ->
                        simpleDialogg.cancel()

                        qtyList.clear()
                        idList.clear()
                        total=0.0
                        for(i in 0..productListWithQTY!!.size-1){
                            if(roundOffDecimal(productListWithQTY!!.get(i).toDouble())!=0.0)
                            {
                                qtyList.add(roundOffDecimal(productListWithQTY!!.get(i).toDouble()))
                                idList.add(productList!!.get(i).id)
                                total+= roundOffDecimal(productListWithQTY!!.get(i).toDouble())
                            }
                        }
                        if(total == 0.0){
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_value))
                        }else{
                            var obj = CurrentStockEntryModelEntity()
                            var shopAll=AppDatabase.getDBInstance()!!.shopCurrentStockEntryDao().getShopStockAll()
                            if(shopAll==null || shopAll.isEmpty()){
                                obj.stock_id = Pref.user_id+AppUtils.getCurrentDateMonth()+"90001"
                            }else{
                                if(shopAll[shopAll.size-1].stock_id != null && shopAll[shopAll.size-1].stock_id!!.length >=1){

                                    val lastId = shopAll[shopAll.size - 1].stock_id?.toLong()
                                    val finalId = lastId!! + 1
                                    obj.stock_id=finalId.toString()
                                    //obj.stock_id=(shopAll[shopAll.size-1].stock_id!!.toLong()+1).toString()
                                }
                                else
                                    obj.stock_id = Pref.user_id+AppUtils.getCurrentDateMonth()+"90001"
                            }
                            obj.shop_id=mAddShopDataObj!!.shop_id
                            obj.visited_datetime=AppUtils.getCurrentDateTime()
                            //obj.visited_date=AppUtils.getCurrentDate()
                            obj.visited_date=obj?.visited_datetime?.take(10)
                            obj.user_id=Pref.user_id
                            obj.total_product_stock_qty=total.toString()
                            obj.isUploaded=false
                            AppDatabase.getDBInstance()?.shopCurrentStockEntryDao()!!.insert(obj)

                            for(i in 0..qtyList.size-1){
                                var objjj = CurrentStockEntryProductModelEntity()
                                objjj.stock_id=obj.stock_id
                                objjj.shop_id=mAddShopDataObj!!.shop_id
                                objjj.product_id=idList[i].toString()
                                objjj.product_stock_qty=qtyList[i].toString()
                                objjj.user_id=Pref.user_id
                                objjj.isUploaded=false
                                AppDatabase.getDBInstance()?.shopCurrentStockProductsEntryDao()!!.insert(objjj)
                            }
                            if(AppUtils.isOnline(mContext)){
                                apiCall(obj?.stock_id.toString())
                            }
                            else{
                                simpleDialog("Saved Successfully")
                            }
                        }

                    })
                    dialogNo.setOnClickListener( { view ->
                        simpleDialogg.cancel()
                    })
                    simpleDialogg.show()

                }

                // off 11-01-2022 ruby issues
              /*  R.id.tv_save_shop_stock ->{


                    for(i in 0..productListWithQTY!!.size-1){
                        if(roundOffDecimal(productListWithQTY!!.get(i).toDouble())!=0.0)
                        {
                            total+= roundOffDecimal(productListWithQTY!!.get(i).toDouble())
                        }
                    }
                    if(total == 0.0){
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_value))
                        return
                    }


                    val simpleDialogg = Dialog(mContext)
                    simpleDialogg.setCancelable(false)
                    simpleDialogg.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialogg.setContentView(R.layout.dialog_yes_no)
                    val dialogHeader = simpleDialogg.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                    dialogHeader.text="Do you want to submit ?"
                    val dialogYes = simpleDialogg.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
                    val dialogNo = simpleDialogg.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView

                    dialogYes.setOnClickListener( { view ->
                        simpleDialogg.cancel()

                        qtyList.clear()
                        idList.clear()
                        total=0.0
                        for(i in 0..productListWithQTY!!.size-1){
                            if(roundOffDecimal(productListWithQTY!!.get(i).toDouble())!=0.0)
                            {
                                qtyList.add(roundOffDecimal(productListWithQTY!!.get(i).toDouble()))
                                idList.add(productList!!.get(i).id)
                                total+= roundOffDecimal(productListWithQTY!!.get(i).toDouble())
                            }
                        }
                        if(total == 0.0){
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_value))
                        }
                        else{
                            var obj = CurrentStockEntryModelEntity()
                            var shopAll=AppDatabase.getDBInstance()!!.shopCurrentStockEntryDao().getShopStockAll()
                            if(shopAll==null || shopAll.isEmpty()){
                                obj.stock_id = Pref.user_id+AppUtils.getCurrentDateMonth()+"90001"
                            }else{
                                if(shopAll[shopAll.size-1].stock_id != null && shopAll[shopAll.size-1].stock_id!!.length >=1){

                                    val lastId = shopAll[shopAll.size - 1].stock_id?.toLong()
                                    val finalId = lastId!! + 1
                                    obj.stock_id=finalId.toString()
                                    //obj.stock_id=(shopAll[shopAll.size-1].stock_id!!.toLong()+1).toString()
                                }
                                else
                                    obj.stock_id = Pref.user_id+AppUtils.getCurrentDateMonth()+"90001"
                            }
                            obj.shop_id=mAddShopDataObj!!.shop_id
                            obj.visited_datetime=AppUtils.getCurrentDateTime()
                            //obj.visited_date=AppUtils.getCurrentDate()
                            obj.visited_date=obj?.visited_datetime?.take(10)
                            obj.user_id=Pref.user_id
                            obj.total_product_stock_qty=total.toString()
                            obj.isUploaded=false
                            AppDatabase.getDBInstance()?.shopCurrentStockEntryDao()!!.insert(obj)

                            for(i in 0..qtyList.size-1){
                                var objjj = CurrentStockEntryProductModelEntity()
                                objjj.stock_id=obj.stock_id
                                objjj.shop_id=mAddShopDataObj!!.shop_id
                                objjj.product_id=idList[i].toString()
                                objjj.product_stock_qty=qtyList[i].toString()
                                objjj.user_id=Pref.user_id
                                objjj.isUploaded=false
                                AppDatabase.getDBInstance()?.shopCurrentStockProductsEntryDao()!!.insert(objjj)
                            }
                            if(AppUtils.isOnline(mContext)){
                                apiCall(obj?.stock_id.toString())
                            }
                            else{
                                simpleDialog("Saved Successfully")
                            }
                        }

                    })
                    dialogNo.setOnClickListener( { view ->
                        simpleDialogg.cancel()
                    })
                    simpleDialogg.show()

                }*/
            }
        }
    }

    private fun apiCall(currentStockId:String){

        try{
            var currentStock :ShopAddCurrentStockRequest = ShopAddCurrentStockRequest()
            //var unsyncData= AppDatabase.getDBInstance()?.shopCurrentStockEntryDao()!!.getShopStockAllByShopIDUnsynced(mAddShopDataObj!!.shop_id.toString())
            var unsyncData= AppDatabase.getDBInstance()?.shopCurrentStockEntryDao()!!.getShopStockAllByStockIDUnsynced(currentStockId)
            currentStock.user_id=Pref.user_id
            currentStock.session_token=Pref.session_token
            currentStock.stock_id=unsyncData.stock_id
            currentStock.shop_id=mAddShopDataObj?.shop_id
            currentStock.visited_datetime=unsyncData.visited_datetime


            var currentStockProductList = AppDatabase.getDBInstance()?.shopCurrentStockProductsEntryDao()!!.getShopProductsStockAllByStockID(currentStock.stock_id.toString())
            var productList:MutableList<ShopAddCurrentStockList> = ArrayList()
            for(i in 0..currentStockProductList.size-1){
                var obj=ShopAddCurrentStockList()
                obj.product_id=currentStockProductList.get(i).product_id
                obj.product_stock_qty=currentStockProductList.get(i).product_stock_qty
                productList.add(obj)
            }
            currentStock.stock_product_list=productList

            val repository = ShopAddStockProvider.provideShopAddStockRepository()
            compositeDisposable.add(
                    repository.shopAddStock(currentStock)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                XLog.d("Stock/AddCurrentStock : RESPONSE " + result.status)
                                if (result.status == NetworkConstant.SUCCESS){
                                    AppDatabase.getDBInstance()?.shopCurrentStockEntryDao()!!.syncShopStocktable(currentStock.stock_id.toString())
                                    AppDatabase.getDBInstance()?.shopCurrentStockProductsEntryDao()!!.syncShopProductsStock(currentStock.stock_id.toString())
                                    simpleDialog("Uploaded Successfully")
                                }
                                else if(result.status == NetworkConstant.SESSION_MISMATCH){
                                    simpleDialog("Uploaded Unable"+currentStock.stock_id)
                                }
                            }
                                    ,{error ->
                                if (error == null) {
                                    XLog.d("Stock/AddCurrentStock : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
                                } else {
                                    XLog.d("Stock/AddCurrentStock : ERROR " + error.localizedMessage)
                                    error.printStackTrace()
                                }
                            })
            )
        }catch (ex:Exception){

        }

    }

    private fun simpleDialog(subject:String){
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_simple)

        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_simple_ok) as AppCustomTextView
        val heading = simpleDialog.findViewById(R.id.dialog_simple_header_TV) as AppCustomTextView
        heading.text=subject
        dialogYes.setOnClickListener( { view ->
            simpleDialog.dismiss()
            (mContext as DashboardActivity).onBackPressed()
            //(mContext as DashboardActivity).loadFragment(FragType.UpdateShopStockFragment, false, mAddShopDataObj?.shop_id.toString())
        })

        simpleDialog.show()
    }


    fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }

    fun checkValue(){
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_yes_no)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
        dialogHeader.text="Do you want to submit ?"
        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
        val dialogNo = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView

        dialogYes.setOnClickListener( { view ->
            simpleDialog.cancel()
            for(i in 0..productListWithQTY!!.size-1){
                if(roundOffDecimal(productListWithQTY!!.get(i).toDouble())!=0.0)
                {
                    total+= roundOffDecimal(productListWithQTY!!.get(i).toDouble())
                }
            }
        })
        dialogNo.setOnClickListener( { view ->
            simpleDialog.cancel()
        })
        simpleDialog.show()
    }


}
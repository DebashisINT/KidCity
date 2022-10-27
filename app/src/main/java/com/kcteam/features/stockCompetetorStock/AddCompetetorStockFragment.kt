package com.kcteam.features.stockCompetetorStock

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.CcompetetorStockEntryModelEntity
import com.kcteam.app.domain.CompetetorStockEntryProductModelEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.widgets.MovableFloatingActionButton
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.DecimalDigitsInputFilter
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.stockCompetetorStock.api.AddCompStockProvider
import com.kcteam.features.stockCompetetorStock.model.CompetetorStockData
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class AddCompetetorStockFragment: BaseFragment(), View.OnClickListener {

    var checkStatus=true

    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_addr_TV: AppCustomTextView
    private lateinit var myshop_contact_TV: AppCustomTextView

    private lateinit var mContext: Context
    private lateinit var addComtetetorStock:MovableFloatingActionButton
    private lateinit var removeComtetetorStock:MovableFloatingActionButton
    private lateinit var confirmStock: AppCustomTextView
    lateinit var ll_root: LinearLayout
    var vi:LayoutInflater ? = null
    var viewC: View? = null
    var competetorStockDataList: ArrayList<CompetetorStockData> ? = ArrayList()
    var total:Double = 0.0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object{
        var mAddShopDataObj: AddShopDBModelEntity? = null
        fun getInstance(objects: Any): AddCompetetorStockFragment {
            val addCompetetorStockFragment = AddCompetetorStockFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is AddShopDBModelEntity) {
                    mAddShopDataObj = objects
                }
            }
            return addCompetetorStockFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_competetor_stock, container, false)
        initView(view)
        return view
    }

    private fun initView(view:View){
        myshop_name_TV = view!!.findViewById(R.id.myshop_name_TV)
        myshop_addr_TV = view!!.findViewById(R.id.myshop_address_TV)
        myshop_contact_TV = view!!.findViewById(R.id.tv_contact_number)

        myshop_name_TV.text = mAddShopDataObj!!.shopName
        myshop_addr_TV.text = mAddShopDataObj!!.address
        myshop_contact_TV.text = "Owner Contact Number: " + mAddShopDataObj!!.ownerContactNumber

        var check:Boolean = true
        addComtetetorStock=view.findViewById(R.id.fab_add_competetor_stock)
        removeComtetetorStock=view.findViewById(R.id.fab_remove_competetor_stock)
        ll_root=view.findViewById(R.id.ll_root_add_competetor_stock) as LinearLayout
        addComtetetorStock.setCustomClickListener {

            if(ll_root.childCount>0){
                var v:View = ll_root.getChildAt(ll_root.childCount-1)
                var brand=(v.findViewById(R.id.et_comp_stock_brand) as EditText).text.toString()
                var product_name=(v.findViewById(R.id.et_comp_stock_product_name) as EditText).text.toString()
                var qty=(v.findViewById(R.id.et_comp_stock_product_qty) as EditText).text.toString()
                var product_mrp=(v.findViewById(R.id.et_comp_stock_product_mrp) as EditText).text.toString()

                if(product_name.length>0){
                    if(qty.length>0){
                        createDynaView()
                    }else{
                        (v.findViewById(R.id.et_comp_stock_product_qty) as EditText).setError("Please Enter QTY")
                        (v.findViewById(R.id.et_comp_stock_product_qty) as EditText).requestFocus()
                    }
                }else{
                    (v.findViewById(R.id.et_comp_stock_product_name) as EditText).setError("Please Enter Product name")
                    (v.findViewById(R.id.et_comp_stock_product_name) as EditText).requestFocus()
                }

            }else{
                createDynaView()
            }



        }

        removeComtetetorStock.setCustomClickListener {

            removeView()
        }

        confirmStock=view.findViewById(R.id.tv_save_competetor_stock)
        confirmStock.setOnClickListener(this)
    }



    private fun removeView(){
        var viewCountt=ll_root.childCount
        var k=0
        while (k<=viewCountt-1){

            var v:View = ll_root.getChildAt(k)
            var brand=(v.findViewById(R.id.et_comp_stock_brand) as EditText).text.toString()
            var product_name=(v.findViewById(R.id.et_comp_stock_product_name) as EditText).text.toString()
            var qty=(v.findViewById(R.id.et_comp_stock_product_qty) as EditText).text.toString()
            var product_mrp=(v.findViewById(R.id.et_comp_stock_product_mrp) as EditText).text.toString()

            if(brand.length==0 && product_name.length==0 && qty.length==0 && product_mrp.length==0){
                ll_root.removeView(v)
                k=0
            }
            viewCountt=ll_root.childCount
            k++
        }
    }


    override fun onClick(p0: View?) {
        if(p0!=null){
            when(p0.id){
                R.id.tv_save_competetor_stock ->{

                    if(ll_root.childCount==0){
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_value))
                        return
                    }
                    for(i in 0..ll_root.childCount-1){
                        var v:View = ll_root.getChildAt(i)
                        var brand=(v.findViewById(R.id.et_comp_stock_brand) as EditText).text.toString()
                        var product_name=(v.findViewById(R.id.et_comp_stock_product_name) as EditText).text.toString()
                        var qty=(v.findViewById(R.id.et_comp_stock_product_qty) as EditText).text.toString()
                        var product_mrp=(v.findViewById(R.id.et_comp_stock_product_mrp) as EditText).text.toString()
                        if(product_name.length>0){
                            if(qty.length>0 && (roundOffDecimal(qty.toDouble())!=0.0)){
                                if(product_name.length>0 && qty.length>0 ){

                                }else{

                                }
                            }else{
                                (v.findViewById(R.id.et_comp_stock_product_qty) as EditText).setError("Please Enter QTY")
                                (v.findViewById(R.id.et_comp_stock_product_qty) as EditText).requestFocus()
                                return
                            }
                        }else{
                            (v.findViewById(R.id.et_comp_stock_product_name) as EditText).setError("Please Enter Product name")
                            (v.findViewById(R.id.et_comp_stock_product_name) as EditText).requestFocus()
                            return
                        }
                    }


                    if(ll_root.childCount>=1){
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

                            var v:View = ll_root.getChildAt(0)
                            var brand=(v.findViewById(R.id.et_comp_stock_brand) as EditText).text.toString()
                            var product_name=(v.findViewById(R.id.et_comp_stock_product_name) as EditText).text.toString()
                            var qty=(v.findViewById(R.id.et_comp_stock_product_qty) as EditText).text.toString()
                            var product_mrp=(v.findViewById(R.id.et_comp_stock_product_mrp) as EditText).text.toString()


                            if(product_name.length>0){
                                if(qty.length>0){
                                    if(product_name.length>0 && qty.length>0 ){

                                    }else{

                                    }
                                }else{
                                    (v.findViewById(R.id.et_comp_stock_product_qty) as EditText).setError("Please Enter QTY")
                                    (v.findViewById(R.id.et_comp_stock_product_qty) as EditText).requestFocus()

                                }
                            }else{
                                (v.findViewById(R.id.et_comp_stock_product_name) as EditText).setError("Please Enter Product name")
                                (v.findViewById(R.id.et_comp_stock_product_name) as EditText).requestFocus()

                            }


                           if(product_name.length>0 && qty.length>0 ){
                               //getValues()
                               checkVali()
                           }else{
                               (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_value))
                           }
                        })
                        dialogNo.setOnClickListener( { view ->
                            simpleDialog.cancel()
                        })
                        simpleDialog.show()
                    }else {
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_value))
                    }

                }
            }
        }
    }


    private fun checkVali(){
        checkStatus=true
        var count = 0
        var viewCount=ll_root.childCount
        for(i in 0..viewCount-1){
            count++
            var v:View = ll_root.getChildAt(i)

            var brand=(v.findViewById(R.id.et_comp_stock_brand) as EditText).text.toString()
            var product_name=(v.findViewById(R.id.et_comp_stock_product_name) as EditText).text.toString()
            var qty=(v.findViewById(R.id.et_comp_stock_product_qty) as EditText).text.toString()
            var product_mrp=(v.findViewById(R.id.et_comp_stock_product_mrp) as EditText).text.toString()

            if(product_name.length>0){
                if(qty.length>0){
                    if(product_name.length>0 && qty.length>0 ){

                    }else{
                        checkStatus=false
                    }
                }else{
                    (v.findViewById(R.id.et_comp_stock_product_qty) as EditText).setError("Please Enter QTY")
                    (v.findViewById(R.id.et_comp_stock_product_qty) as EditText).requestFocus()
                    count=0
                }
            }else{
                (v.findViewById(R.id.et_comp_stock_product_name) as EditText).setError("Please Enter Product name")
                (v.findViewById(R.id.et_comp_stock_product_name) as EditText).requestFocus()
                count=0
            }

        }
        if(checkStatus && count==ll_root.childCount){
            getValues()
        }else{
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_value))
        }
    }


    private fun getValues(){
        var viewCount=ll_root.childCount
        if(viewCount<=0){
            return
        }else{
            total=0.0
            for(i in 0..viewCount-1){
                var v:View = ll_root.getChildAt(i)

                var brand=(v.findViewById(R.id.et_comp_stock_brand) as EditText).text.toString()
                var product_name=(v.findViewById(R.id.et_comp_stock_product_name) as EditText).text.toString()
                var qty=(v.findViewById(R.id.et_comp_stock_product_qty) as EditText).text.toString()
                var product_mrp=(v.findViewById(R.id.et_comp_stock_product_mrp) as EditText).text.toString()
                qty=roundOffDecimal(qty.toDouble()).toString()
                if(product_mrp.length>0){
                    product_mrp=roundOffDecimal(product_mrp.toDouble()).toString()
                }else{
                    product_mrp="0.0"
                }
                var objj:CompetetorStockData=CompetetorStockData(brand,product_name,qty,product_mrp)
                competetorStockDataList?.add(objj)
                total+=roundOffDecimal(qty.toDouble())
            }

            var obj= CcompetetorStockEntryModelEntity()
            var comListAll= AppDatabase.getDBInstance()!!.competetorStockEntryDao().getCompetetorStockAll()
            if(comListAll==null || comListAll.isEmpty()){
                //obj.competitor_stock_id = Pref.user_id+ AppUtils.getCurrentDateMonth()+System.currentTimeMillis()+"801"
                obj.competitor_stock_id = Pref.user_id+AppUtils.getCurrentDateMonth()+"80001"
            }else{
                if(comListAll[comListAll.size-1].competitor_stock_id != null && comListAll[comListAll.size-1].competitor_stock_id!!.length >=1){
                    val lastId = comListAll[comListAll.size - 1].competitor_stock_id?.toLong()
                    val finalId = lastId!! + 1
                    obj.competitor_stock_id=finalId.toString()
                    //obj.competitor_stock_id=(comListAll[comListAll.size-1].competitor_stock_id!!.toLong()+1).toString()
                }
                else{
                    obj.competitor_stock_id = Pref.user_id+AppUtils.getCurrentDateMonth()+"80001"
                }
            }
            obj.user_id=Pref.user_id
            obj.shop_id= mAddShopDataObj?.shop_id
            obj.visited_datetime=AppUtils.getCurrentDateTime()
            //obj.visited_date=AppUtils.getCurrentDate()
            obj.visited_date=obj?.visited_datetime?.take(10)
            obj.total_product_stock_qty=total.toString()
            obj.isUploaded=false

            AppDatabase.getDBInstance()?.competetorStockEntryDao()?.insert(obj)

           doAsync {
               for(i in 0..competetorStockDataList!!.size-1)
               {
                   var objjj= CompetetorStockEntryProductModelEntity()
                   objjj.user_id=Pref.user_id
                   objjj.competitor_stock_id=obj.competitor_stock_id
                   objjj.shop_id=mAddShopDataObj?.shop_id
                   objjj.brand_name=competetorStockDataList?.get(i)?.brand
                   objjj.product_name=competetorStockDataList?.get(i)?.productName
                   objjj.qty=competetorStockDataList?.get(i)?.qty
                   objjj.mrp=competetorStockDataList?.get(i)?.mrp
                   objjj.isUploaded=false
                   AppDatabase.getDBInstance()?.competetorStockEntryProductDao()?.insert(objjj)

               }
               uiThread {
                   if(AppUtils.isOnline(mContext)){
                       apiCall(obj.competitor_stock_id.toString())
                   }else{
                       simpleDialog("Successfully Saved")
                   }

               }
           }

        }

    }

    private fun apiCall(currentStockId:String){
        try{
            var currentStock : ShopAddCompetetorStockRequest = ShopAddCompetetorStockRequest()
            var unsyncData= AppDatabase.getDBInstance()?.competetorStockEntryDao()!!.getCompetetorStockByStockIDUnsynced(currentStockId)
            currentStock.user_id=Pref.user_id
            currentStock.session_token=Pref.session_token
            currentStock.shop_id=unsyncData?.shop_id
            currentStock.visited_datetime=unsyncData?.visited_datetime
            currentStock.competitor_stock_id=unsyncData?.competitor_stock_id

            var currentProductStockList= AppDatabase.getDBInstance()?.competetorStockEntryProductDao()?.getComProductStockByStockIDUnsynced(currentStockId)
            var productList:MutableList<ShopAddCompetetorStockProductList> = ArrayList()
            for(i in 0..currentProductStockList!!.size-1){
                var obj=ShopAddCompetetorStockProductList()
                obj.brand_name=currentProductStockList.get(i).brand_name
                obj.product_name=currentProductStockList.get(i).product_name
                obj.qty=currentProductStockList.get(i).qty
                obj.mrp=currentProductStockList.get(i).mrp
                productList.add(obj)
            }
            currentStock.competitor_stock_list=productList

            val repository = AddCompStockProvider.provideCompStockRepositiry()
            BaseActivity.compositeDisposable.add(
                    repository.addCompStock(currentStock)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                XLog.d("CompetitorStock/AddCompetitorStock : RESPONSE " + result.status)
                                if (result.status == NetworkConstant.SUCCESS){
                                    AppDatabase.getDBInstance()?.competetorStockEntryDao()?.syncShopCompStocktable(currentStock.competitor_stock_id.toString())
                                    AppDatabase.getDBInstance()?.competetorStockEntryProductDao()?.syncShopCompProductable(currentStock.competitor_stock_id.toString())
                                    simpleDialog("Uploaded Successfully")
                                }
                            },{error ->
                                if (error == null) {
                                    XLog.d("CompetitorStock/AddCompetitorStock : ERROR " + "UNEXPECTED ERROR IN Add Stock ACTIVITY API")
                                } else {
                                    XLog.d("CompetitorStock/AddCompetitorStock : ERROR " + error.localizedMessage)
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

    private fun createDynaView(){
        vi = mContext.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewC = vi!!.inflate(R.layout.dynamic_view_add_competetor_stock, null)

        ll_root.addView(viewC)

        var v:View = ll_root.getChildAt(ll_root.childCount-1)
        (v.findViewById(R.id.et_comp_stock_product_mrp) as EditText).filters=(arrayOf<InputFilter>(DecimalDigitsInputFilter(7, 2)))
        (v.findViewById(R.id.et_comp_stock_product_qty) as EditText).filters=(arrayOf<InputFilter>(DecimalDigitsInputFilter(7, 2)))
    }


    fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
    }



    fun createFourDigitNumber(): String {
        var r = Random()
        var randomNumber = String.format("%04d", Integer.valueOf(r.nextInt(1001)) as Any?)
        return randomNumber
    }


}
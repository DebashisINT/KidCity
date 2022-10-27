package com.kcteam.features.viewAllOrder.orderNew

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.CustomStatic.IsOrderFromTotalOrder
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.app.widgets.MovableFloatingActionButton
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.viewAllOrder.interf.ViewNewOrdScrDetailsOnCLick
import com.kcteam.features.viewAllOrder.model.ColorList
import com.kcteam.features.viewAllOrder.model.NewOrderCartModel
import com.kcteam.features.viewAllOrder.model.ProductOrder
import com.kcteam.features.viewAllOrder.presentation.NewOdrScrListAdapter
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import java.io.File
import java.lang.Exception

class NewOdrScrListFragment: BaseFragment(), DatePickerListener {
//NewOdrScrListAdapter
    private lateinit var mContext: Context
    private lateinit var rv_details:RecyclerView

    data class ViewDataNewOdrScr(var order_id:String,var shop_id:String,var order_date:String)
    data class ViewDataNewOdrScrDetails(var order_id:String,var shop_id:String,var order_date:String,var shop_name:String,var shop_addr:String)
    private var viewDataList:ArrayList<ViewDataNewOdrScrDetails> = ArrayList()
    private var viewDataListPDF: ArrayList<ViewDataNewOdrScrDetails> = ArrayList()
    private var newOdrScrListAdapter: NewOdrScrListAdapter? = null


    private lateinit var picker: HorizontalPicker
    private lateinit var selectedDate: String
    private lateinit var date_CV:CardView

    private lateinit var share: MovableFloatingActionButton


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var mAddShopDataObj: AddShopDBModelEntity? = null
        var shop_id:String = ""
        fun getInstance(objects: Any): NewOdrScrListFragment {
            val Fragment = NewOdrScrListFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                shop_id=objects.toString()
                mAddShopDataObj = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)
            }
            return Fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_new_odr_scr_list, container, false)
        selectedDate = AppUtils.getCurrentDateForShopActi()
        initView(view)
        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initView(view:View){

        share=view!!.findViewById(R.id.fab_frag_view_new_ord_scr_list_share)
        share.setCustomClickListener {
            sharePdf()
        }

        date_CV=view!!.findViewById(R.id.date_CV)
        if(CustomStatic.IsOrderFromTotalOrder){
            date_CV.visibility=View.VISIBLE
        }else{
            date_CV.visibility=View.GONE
        }

        /*NEW CALENDER*/
        picker = view.findViewById<HorizontalPicker>(R.id.datePicker)
        picker.setListener(this)
                .setDays(60)
                .setOffset(30)
                .setDateSelectedColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//box color
                .setDateSelectedTextColor(ContextCompat.getColor(mContext, R.color.white))
                .setMonthAndYearTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))//month color
                .setTodayButtonTextColor(ContextCompat.getColor(mContext, R.color.date_selector_color))
                .setTodayDateTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setTodayDateBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent))//
                .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setDayOfWeekTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .setUnselectedDayTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .showTodayButton(false)
                .init()
        picker.backgroundColor = Color.WHITE
        picker.setDate(DateTime())


        /*NEW CALENDER*/

        rv_details=view!!.findViewById(R.id.rv_frag_new_odr_scr_list)

        gatherData()
    }

    override fun onDateSelected(dateSelected: DateTime) {
        var dateTime = dateSelected.toString()
        var dateFormat = dateTime.substring(0, dateTime.indexOf('T'))
        selectedDate = dateFormat
        gatherData()
    }

    private fun gatherData(){

        var odr_shop_list:List<ViewDataNewOdrScr> = emptyList()
        if(CustomStatic.IsOrderFromTotalOrder){
            odr_shop_list= AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getDistinctOrderShopAllDateFiltered(selectedDate)!!
        }else{
            odr_shop_list= AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getDistinctOrderShopAll()!!
        }


        viewDataList.clear()

        doAsync {
            try{
                for(i in 0..odr_shop_list!!.size-1){
                    var shopName= AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(odr_shop_list.get(i).shop_id).shopName
                    var shopAddr= AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(odr_shop_list.get(i).shop_id).address
                    var obj= ViewDataNewOdrScrDetails(odr_shop_list.get(i).order_id,odr_shop_list.get(i).shop_id,odr_shop_list.get(i).order_date,shopName,shopAddr)
                    viewDataList.add(obj)
                }

            }catch (ex:Exception){

            }


            uiThread {
                newOdrScrListAdapter=NewOdrScrListAdapter(mContext,viewDataList!!,object : ViewNewOrdScrDetailsOnCLick {
                    override fun getOrderID(orderID: String, orderDate: String,shop_id:String) {
                        NewOrderScrOrderDetailsFragment.shop_id=shop_id
                        getOrderDetailsList(orderID,orderDate)
                    }
                })
                rv_details.adapter=newOdrScrListAdapter

            }

        }

        if(odr_shop_list.size==0){
            Toaster.msgShort(mContext,"No Order (s) found.")
        }

    }

    private  var final_order_list:ArrayList<NewOrderCartModel> = ArrayList()
    private  var newOrderCartModel1:NewOrderCartModel? = null
    private  var newOrderCartModel2:NewOrderCartModel? = null

    private fun getOrderDetailsList(orderID:String,orderDate:String){
        var orderAllList=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getShopOrderAll(NewOrderScrOrderDetailsFragment.shop_id)
        //var orderIdList=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getShopOrderDistinct(shop_id)
        var orderIdList:ArrayList<String> = ArrayList()
        orderIdList.add(orderID)
        final_order_list.clear()

        for(i in 0..orderIdList!!.size-1){
            newOrderCartModel1= NewOrderCartModel()
            newOrderCartModel2= NewOrderCartModel()
            //var uniqGender=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getUniqGenderForOrderID(orderIdList.get(i))

            var productIDList=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getProductCodeDistinctByOrderID(orderIdList.get(i))
            //


            for(j in 0..productIDList!!.size-1){


                if(j!=0){
                    if(newOrderCartModel1!=null){
                        if(newOrderCartModel1!!.color_list.size>0)
                            final_order_list.add(newOrderCartModel1!!)
                    }
                    if(newOrderCartModel2!=null){
                        if(newOrderCartModel2!!.color_list.size>0)
                            final_order_list.add(newOrderCartModel2!!)
                    }
                }

                newOrderCartModel1=NewOrderCartModel()
                newOrderCartModel2=NewOrderCartModel()

                //newOrderCartModel1!!.product_id=productIDList!!.get(j)
                //newOrderCartModel2!!.product_id=productIDList!!.get(j)
                //newOrderCartModel1!!.product_name=AppDatabase.getDBInstance()?.newOrderProductDao()?.getNewOrderProductName(productIDList!!.get(j))!!
                //newOrderCartModel2!!.product_name=AppDatabase.getDBInstance()?.newOrderProductDao()?.getNewOrderProductName(productIDList!!.get(j))!!

                var colorIDListForProduct=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getColorIDDistinctByOrderID(orderIdList.get(i),productIDList.get(j))
                for(k in 0..colorIDListForProduct!!.size-1){
                    var sizeQtyListMale = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getSizeQtyByProductColorIDMale(orderIdList!!.get(i), productIDList!!.get(j), colorIDListForProduct!!.get(k),
                            Pref.new_ord_gender_male)
                    var sizeQtyListFeMale = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getSizeQtyByProductColorIDFemale(orderIdList!!.get(i), productIDList!!.get(j), colorIDListForProduct!!.get(k),
                            Pref.new_ord_gender_female)
                    if(sizeQtyListMale!!.size>0){
                        newOrderCartModel1!!.product_id=productIDList!!.get(j)
                        newOrderCartModel1!!.product_name=AppDatabase.getDBInstance()?.newOrderProductDao()?.getNewOrderProductName(productIDList!!.get(j))!!

                        //newOrderCartModel1!!.gender="MALE"
                        newOrderCartModel1!!.gender = Pref.new_ord_gender_male

                        try{
                            var totalRate = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getNewOrderProductRateByOrdID(productIDList!!.get(j),orderIdList.get(i))!!.toDouble()
                            var qt = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getNewOrderProductQtyByOrdID(productIDList!!.get(j),orderIdList.get(i))!!.toDouble()
                            //newOrderCartModel1!!.rate=(totalRate/qt).toString()
                            newOrderCartModel1!!.rate=totalRate.toString()
                        }catch (ex:Exception){
                            newOrderCartModel1!!.rate="0.0"
                        }
                        //newOrderCartModel1!!.rate=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getNewOrderProductRateByOrdID(productIDList!!.get(j),orderIdList.get(i))!!

                        var colorSel= AppDatabase.getDBInstance()?.newOrderColorDao()?.getNewOrderColorName(colorIDListForProduct.get(k))
                        var colorList: ColorList = ColorList(colorSel!!,colorIDListForProduct.get(k), sizeQtyListMale as ArrayList<ProductOrder>)
                        newOrderCartModel1!!.color_list.add(colorList)
                    }

                    if(sizeQtyListFeMale!!.size>0){
                        newOrderCartModel2!!.product_id=productIDList!!.get(j)
                        newOrderCartModel2!!.product_name=AppDatabase.getDBInstance()?.newOrderProductDao()?.getNewOrderProductName(productIDList!!.get(j))!!

                        //newOrderCartModel2!!.gender="FEMALE"
                        newOrderCartModel2!!.gender = Pref.new_ord_gender_female

                        try{
                            var totalRate = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getNewOrderProductRateByOrdID(productIDList!!.get(j),orderIdList.get(i))!!.toDouble()
                            var qt = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getNewOrderProductQtyByOrdID(productIDList!!.get(j),orderIdList.get(i))!!.toDouble()
                            //newOrderCartModel1!!.rate=(totalRate/qt).toString()
                            newOrderCartModel1!!.rate=totalRate.toString()
                        }catch (ex:Exception){
                            newOrderCartModel1!!.rate="0.0"
                        }
                        //newOrderCartModel2!!.rate=AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getNewOrderProductRateByOrdID(productIDList!!.get(j),orderIdList.get(i))!!

                        var colorSel= AppDatabase.getDBInstance()?.newOrderColorDao()?.getNewOrderColorName(colorIDListForProduct.get(k))
                        var colorList: ColorList = ColorList(colorSel!!,colorIDListForProduct.get(k), sizeQtyListFeMale as ArrayList<ProductOrder>)
                        newOrderCartModel2!!.color_list.add(colorList)
                    }

                }

            }

            if(newOrderCartModel1!=null){
                if(newOrderCartModel1!!.color_list.size>0)
                    final_order_list.add(newOrderCartModel1!!)
            }
            if(newOrderCartModel2!=null){
                if(newOrderCartModel2!!.color_list.size>0)
                    final_order_list.add(newOrderCartModel2!!)
            }

        }

        CustomStatic.IsFromViewNewOdrScr=true
        CustomStatic.IsFromViewNewOdrScrOrderID=orderID
        CustomStatic.IsFromViewNewOdrScrOrderDate=orderDate
        (mContext as DashboardActivity).loadFragment(FragType.NeworderScrCartFragment, true, final_order_list)

    }


    fun sharePdf(){
        var odr_shop_list: List<ViewDataNewOdrScr> = emptyList()
        if (CustomStatic.IsOrderFromTotalOrder) {
            odr_shop_list = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getDistinctOrderShopAllDateFiltered(selectedDate)!!
        } else {
            odr_shop_list = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getDistinctOrderShopAll()!!
        }
        viewDataListPDF.clear()
        try {
            for (i in 0..odr_shop_list!!.size - 1) {
                var shopName = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(odr_shop_list.get(i).shop_id).shopName
                var shopAddr = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(odr_shop_list.get(i).shop_id).address
                var obj = ViewDataNewOdrScrDetails(odr_shop_list.get(i).order_id, odr_shop_list.get(i).shop_id, odr_shop_list.get(i).order_date, shopName, shopAddr)
                viewDataListPDF.add(obj)
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        var heading = "ORDER SUMMARY"
        var pdfBody: String = "\n\n"
        for (i in 0..viewDataListPDF!!.size - 1) {
            var qty_Order: Int = 0
            var qtty_list = AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.getShopOrderQtyOrderIDWise(viewDataListPDF!!.get(i).order_id)
            for (j in 0..qtty_list!!.size - 1) {
                qty_Order = qty_Order + qtty_list.get(j).toString().toInt()
            }
            var content= "\n\n"+"Order ID  : "+viewDataListPDF!!.get(i).order_id+"          Order Date : "+AppUtils.convertToCommonFormat(viewDataListPDF!!.get(i).order_date)+"\n"+"Qty           : "+qty_Order.toString()+"\n"+"Name      : "+viewDataListPDF!!.get(i).shop_name+"\n"+"Address  :"+viewDataListPDF!!.get(i).shop_addr+"\n"+
                    "\n____________________________________________________________________________________________________"
            pdfBody=pdfBody+content
        }


        val image = BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher)

        val path = FTStorageUtils.stringToPdf(pdfBody, mContext, "OrderDetalis" +
                "_" + Pref.user_id+AppUtils.getCurrentDateTime().toString().replace(" ","R").replace(":","_") + ".pdf", image, heading, 3.7f)

        if (!TextUtils.isEmpty(path)) {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                val fileUrl = Uri.parse(path)

                val file = File(fileUrl.path)
//                val uri = Uri.fromFile(file)
                val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
                shareIntent.type = "image/png"
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(shareIntent, "Share pdf using"));
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else
            (mContext as DashboardActivity).showSnackMessage("Pdf can not be sent.")
    }


}
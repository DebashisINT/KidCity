package com.kcteam.features.viewAllOrder.orderNew

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.NewOrderScrOrderEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.widgets.MovableFloatingActionButton
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.viewAllOrder.api.addorder.AddOrderRepoProvider
import com.kcteam.features.viewAllOrder.presentation.NewOrderCartAdapter
import com.kcteam.features.viewAllOrder.interf.NewOrderorderCount
import com.kcteam.features.viewAllOrder.model.AddOrderInputParamsModel
import com.kcteam.features.viewAllOrder.model.AddOrderInputProductList
import com.kcteam.features.viewAllOrder.model.NewOrderCartModel
import com.kcteam.features.viewAllOrder.model.NewOrderSaveApiModel
import com.kcteam.features.viewAllOrder.presentation.NewOrderCartAdapterNew
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File


class NeworderScrCartFragment : BaseFragment(), View.OnClickListener {

    private lateinit var rv_order: RecyclerView
    private var newOrderCartAdapter: NewOrderCartAdapter? = null
    private var newOrderCartAdapterNew: NewOrderCartAdapterNew? = null

    private lateinit var tv_name:TextView
    private lateinit var tv_orderID:TextView
    private lateinit var tv_orderDate:TextView
    private lateinit var iv_call:ImageView
    private lateinit var tv_phone:TextView
    private lateinit var ll_odrDtlsRoot:LinearLayout
    private var shop_phone:String=""
    private lateinit var share: MovableFloatingActionButton
    private lateinit var mContext: Context

    private lateinit var btnSaveDB: Button
    var ordID:String=""

//    var simpleDialog = Dialog(mContext)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var cartOrder: ArrayList<NewOrderCartModel>? = null
        fun getInstance(objects: Any): NeworderScrCartFragment {
            val Fragment = NeworderScrCartFragment()
            cartOrder = objects as ArrayList<NewOrderCartModel>
            return Fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_new_order_scr_cart, container, false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //fab_frag_new_order_share.setOnClickListener(this)
    }

    @SuppressLint("UseRequireInsteadOfGet", "RestrictedApi")
    private fun initView(view: View?) {
        share=view!!.findViewById(R.id.fab_frag_new_order_share)
        share.setCustomClickListener {
            sharePdf()
        }
        rv_order = view!!.findViewById(R.id.rv_frag_new_order_cart)

        tv_name = view!!.findViewById(R.id.tv_frag_new_order_scr_cart_name)
        tv_orderID = view!!.findViewById(R.id.tv_frag_new_order_scr_cart_order_id)
        tv_orderDate = view!!.findViewById(R.id.tv_frag_new_order_scr_cart_order_date)
        iv_call = view!!.findViewById(R.id.iv_frag_new_order_scr_cart_phone)
        tv_phone = view!!.findViewById(R.id.tv_frag_new_order_scr_cart_phone)
        ll_odrDtlsRoot = view!!.findViewById(R.id.ll_frag_new_order_scr_root)

        btnSaveDB = view!!.findViewById(R.id.btn_new_order_save_db)
        btnSaveDB.setOnClickListener(this)

        tv_orderID.text=CustomStatic.IsFromViewNewOdrScrOrderID

        tv_orderDate.text= AppUtils.convertToCommonFormat(CustomStatic.IsFromViewNewOdrScrOrderDate)
        iv_call.setOnClickListener(this)
        tv_phone.setOnClickListener(this)

        if( CustomStatic.IsFromViewNewOdrScr==true){
            share.visibility = View.VISIBLE
            ll_odrDtlsRoot.visibility=View.VISIBLE
            btnSaveDB.visibility=View.GONE
            tv_name.text=AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(NewOrderScrOrderDetailsFragment.shop_id).shopName!!
            shop_phone=AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(NewOrderScrOrderDetailsFragment.shop_id).ownerContactNumber!!
        }else{
            share.visibility = View.GONE
            ll_odrDtlsRoot.visibility=View.GONE
            btnSaveDB.visibility=View.VISIBLE
        }
        tv_phone.text=shop_phone

        showCartDetails()
    }


    /*    private fun showCartDetails(){
            newOrderCartAdapter=NewOrderCartAdapter(mContext,cartOrder!!,object: NewOrderorderCount{
                override fun getOrderCount(orderCount: Int) {
                    (mContext as DashboardActivity).tv_cart_count.text = orderCount.toString()
                    (mContext as DashboardActivity).tv_cart_count.visibility = View.VISIBLE
                    //(mContext as DashboardActivity).showSnackMessage(getString(R.string.add_product_cart))
                }
            })
            rv_order.adapter=newOrderCartAdapter
        }*/
    fun showCheckAlert() {
        var simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_new_order_confirmed)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_new_order_confirmed_header_TV) as AppCustomTextView
        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_new_order_confirmed_headerTV) as AppCustomTextView
        dialog_yes_no_headerTV.text = "Order Confirmation"
        //dialogHeader.text = "Do you want to recheck the order?"
        dialogHeader.text = "Would you like to confirm the order?"
        val dialogYes = simpleDialog.findViewById(R.id.tv_message_yes) as AppCustomTextView
        val dialogNo = simpleDialog.findViewById(R.id.tv_message_no) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
            if (cartOrder!!.size > 0)
                saveToDB()
        })
        dialogNo.setOnClickListener({ view ->
            simpleDialog.cancel()
            //if (cartOrder!!.size > 0)
                //saveToDB()
        })
        simpleDialog.show()
//        CommonDialog.getInstance(header, title, getString(R.string.no), getString(R.string.yes), false, object : CommonDialogClickListener {
//            override fun onLeftClick() {
//                if (cartOrder!!.size > 0)
//                    saveToDB()
//            }
//
//            override fun onRightClick(editableData: String) {
//                // Api called
//            }
//
//        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun showCartDetails() {

        newOrderCartAdapterNew = NewOrderCartAdapterNew(mContext, cartOrder!!, object : NewOrderorderCount {
            override fun getOrderCount(orderCount: Int) {
                (mContext as DashboardActivity).tv_cart_count.text = orderCount.toString()
                (mContext as DashboardActivity).tv_cart_count.visibility = View.VISIBLE
                //(mContext as DashboardActivity).showSnackMessage(getString(R.string.add_product_cart))
            }
        })
        rv_order.adapter = newOrderCartAdapterNew
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_new_order_save_db -> {
                    if (cartOrder!!.size > 0)
                        showCheckAlert()
                        //saveToDB()
                }
                R.id.iv_frag_new_order_scr_cart_phone -> {
                    IntentActionable.initiatePhoneCall(mContext, shop_phone)
                }
                R.id.tv_frag_new_order_scr_cart_phone -> {
                    IntentActionable.initiatePhoneCall(mContext, shop_phone)
                }
                R.id.fab_frag_new_order_share -> {
                    sharePdf()
                }
            }
        }
    }

    var newOrderRoomDataList:ArrayList<NewOrderRoomData> = ArrayList()

    fun saveToDB(){
        newOrderRoomDataList.clear()
        ordID= Pref.user_id + AppUtils.getCurrentDateMonth() + AppUtils.getRandomNumber(6)
        for(i in 0..cartOrder!!.size-1){
            for(j in 0..cartOrder!!.get(i).color_list.size-1){
                for(k in 0..cartOrder!!.get(i).color_list.get(j).order_list.size-1){
                    var newOrderRoomData=NewOrderRoomData(ordID,cartOrder!!.get(i).product_id.toString(),cartOrder!!.get(i).product_name.toString(),cartOrder!!.get(i).gender.toString(),
                            cartOrder!!.get(i).color_list.get(j).color_id,cartOrder!!.get(i).color_list.get(j).color_name,cartOrder!!.get(i).color_list.get(j).order_list.get(k).size, cartOrder!!.get(i).color_list.get(j).order_list.get(k).qty,
                        //(cartOrder!!.get(i).rate.toDouble()*cartOrder!!.get(i).color_list.get(j).order_list.get(k).qty.toInt()).toString())
                        (cartOrder!!.get(i).rate.toDouble()).toString())

                    newOrderRoomDataList.add(newOrderRoomData)

                    var obj:NewOrderScrOrderEntity= NewOrderScrOrderEntity()
                    obj.order_id=ordID
                    obj.product_id=newOrderRoomData.product_id
                    obj.product_name=newOrderRoomData.product_name
                    obj.gender=newOrderRoomData.gender
                    obj.size=newOrderRoomData.size
                    obj.qty=newOrderRoomData.qty
                    obj.order_date=AppUtils.getCurrentDateyymmdd()
                    obj.shop_id=NewOrderScrActiFragment.shop_id!!
                    obj.color_id=newOrderRoomData.color_id
                    obj.color_name=newOrderRoomData.color_name
                    obj.isUploaded=false
                    //obj.rate= (cartOrder!!.get(i).rate.toDouble()*newOrderRoomData.qty.toInt()).toString()
                    obj.rate= (cartOrder!!.get(i).rate.toDouble()).toString()
                    AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.insert(obj)

                    XLog.d("NeworderScrCartFragment ITEM : "  + AppUtils.getCurrentDateTime().toString()+"\n"+
                    "ordID:"+ordID+"~product_id:"+obj.product_id+"~gender:"+obj.gender+"~size:"+obj.size+"~qty:"+obj.qty+"~order_date:"+obj.order_date+"~shop_id:"+obj.shop_id+
                    "~color_id:"+obj.color_id+"~color_name:"+obj.color_name+"\n")
                }
            }
        }

        if(AppUtils.isOnline(mContext)){
            sendToApi(ordID)

            //showConfirmationDialog()

        }else{
            showConfirmationDialog()
        }

    }

    data class NewOrderRoomData(var order_id:String,var product_id:String,var product_name:String,var gender:String,var color_id:String,var color_name:String ,var size:String,var qty:String,var rate:String)


    private fun sendToApi(ordID: String) {
        var newOrderSaveApiModel: NewOrderSaveApiModel = NewOrderSaveApiModel()
        newOrderSaveApiModel.user_id = Pref.user_id
        newOrderSaveApiModel.session_token = Pref.session_token
        newOrderSaveApiModel.order_id = ordID
        newOrderSaveApiModel.shop_id = NewOrderScrActiFragment.shop_id!!  //// test needed
        newOrderSaveApiModel.order_date = AppUtils.getCurrentDateyymmdd()
        newOrderSaveApiModel.product_list = newOrderRoomDataList


        /////
        val addOrder = AddOrderInputParamsModel()
        addOrder.collection = ""
        addOrder.description = ""
        addOrder.order_amount = "0"
        addOrder.order_date = AppUtils.getCurrentISODateTime()
        addOrder.order_id = ordID
        addOrder.shop_id = NewOrderScrActiFragment.shop_id!!
        addOrder.session_token = Pref.session_token
        addOrder.user_id = Pref.user_id
        addOrder.latitude = Pref.latitude
        addOrder.longitude = Pref.longitude

        addOrder.patient_name = ""
        addOrder.patient_address = ""
        addOrder.patient_no = ""
        addOrder.remarks = ""
        addOrder.scheme_amount = "0"

        if (!TextUtils.isEmpty(Pref.latitude) && !TextUtils.isEmpty(Pref.longitude))
            addOrder.address = LocationWizard.getLocationName(mContext, Pref.latitude!!.toDouble(), Pref.longitude!!.toDouble())
        else
            addOrder.address = ""

        addOrder.Hospital = ""
        addOrder.Email_Address = ""

        val productList = ArrayList<AddOrderInputProductList>()
        for (i in 0..newOrderRoomDataList.size - 1) {
            val product = AddOrderInputProductList()
            product.id = newOrderRoomDataList.get(i).product_id!!
            product.product_name = newOrderRoomDataList.get(i).product_name!!
            product.qty = newOrderRoomDataList.get(i).qty!!
            product.rate = "0"
            product.total_price = "0"
            product.scheme_qty = "0"
            product.scheme_rate = "0"
            product.total_scheme_price = "0"
            product.MRP = "0"
            productList.add(product)
        }

        addOrder.product_list = productList

        ///////

        val repository = AddOrderRepoProvider.provideAddOrderRepository()
        BaseActivity.compositeDisposable.add(
                repository.addOrderNewOrderScr(newOrderSaveApiModel)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            XLog.d("NewOrderScrCartFrag OrderWithProductAttribute/OrderWithProductAttribute : RESPONSE " + result.status)
                            if (result.status == NetworkConstant.SUCCESS) {

                                doAsync {

                                    AppDatabase.getDBInstance()?.newOrderScrOrderDao()?.syncNewOrder(ordID)

                                    uiThread {
                                        updateSecondaryOrderApi(addOrder)

                                        //showConfirmationDialog()
                                    }
                                }


                            }
                        }, { error ->
                            if (error == null) {
                                XLog.d("NewOrderScrCartFrag OrderWithProductAttribute/OrderWithProductAttribute : ERROR ")
                            } else {
                                XLog.d("NewOrderScrCartFrag OrderWithProductAttribute/OrderWithProductAttribute : ERROR " + error.localizedMessage)
                                error.printStackTrace()
                            }
                        })
        )

    }

    private fun updateSecondaryOrderApi(addOrder: AddOrderInputParamsModel) {
        val repository = AddOrderRepoProvider.provideAddOrderRepository()
        BaseActivity.compositeDisposable.add(
                repository.addNewOrder(addOrder)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val orderList = result as BaseResponse
                            if (orderList.status == NetworkConstant.SUCCESS) {
                                showConfirmationDialog()
                            }
                            //(mContext as DashboardActivity).showSnackMessage("Order added successfully")

                        }, { error ->
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage("Something went wrong.")
                            //(mContext as DashboardActivity).showSnackMessage("Order added successfully")

                        })
        )
    }

    private fun showConfirmationDialog(){
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_message_new)

        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV_new) as AppCustomTextView
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV_new) as AppCustomTextView

        dialog_yes_no_headerTV.text = "Congrats!"
        dialogHeader.text = AppUtils.hiFirstNameText() + " , Your order has been placed successfully. Order No is "+ ordID.toString()+"."


        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok_new) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
            voiceAttendanceMsg( AppUtils.hiFirstNameText()+"!" + " , Your order has been placed successfully.")
        })
        simpleDialog.show()
    }

    private fun voiceAttendanceMsg(msg: String) {
        if (Pref.isVoiceEnabledForAttendanceSubmit) {
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Speeh Error", "NewOrderScrCartFragment");
        }

        (mContext as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, false, NewOrderScrActiFragment.shop_id!! )
    }


    @SuppressLint("UseRequireInsteadOfGet")
    fun sharePdf(){
        var heading = "ORDER DETAILS"
        var pdfBody: String = "\n\n"

        pdfBody=pdfBody+"Party      : "+AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(NewOrderScrOrderDetailsFragment.shop_id).shopName!!+ "                                  "+ "Phone : "+shop_phone.toString()+"\n\n"
        pdfBody=pdfBody+"Order ID : "+CustomStatic.IsFromViewNewOdrScrOrderID+ "     "+
                "                                             Date : "+AppUtils.convertToCommonFormat(CustomStatic.IsFromViewNewOdrScrOrderDate)+"\n\n\n"


        for(i in 0..cartOrder!!.size-1){
//            val currentPos: Int = cartOrder!!.size - i
            /* if(currentPos >= 0){

             }*/
            var rootObjj=cartOrder!!.get(i)

            //gender vs product type new order
            //var str = getString(R.string.GenderTextNewOrd)
            var str = getString(R.string.ProductTextNewOrd)
            //var contextHeader="\n"+"___________________________________________________________"+"\n\n"+"Product Name : "+rootObjj.product_name!!+"       "+" Gender : "+rootObjj.gender+"\n"
            var contextHeader="\n"+"___________________________________________________________"+"\n\n"+"Product Name : "+rootObjj.product_name!!+"       "+str+" : "+rootObjj.gender+"\n"

            //var contextHeader="Product Name : "+rootObjj.product_name!!+"       "+" Gender : " +rootObjj.gender+"\n"

            var colorObjj=cartOrder!!.get(i).color_list
            for(j in 0..colorObjj!!.size!!-1){
                var colorRoot="\nColor : "+colorObjj.get(j).color_name+"\n"
                contextHeader+=colorRoot
                var sizeQtyObjj=colorObjj.get(j).order_list
                for(k in 0..sizeQtyObjj!!.size-1){
                    var spaceCount=sizeQtyObjj.get(k).size.length
                    var spacee=" "
                    for(p in 0..(20-spaceCount)){
                        spacee+=" "
                    }
                    var sizeQtyRoot="       Size : "+sizeQtyObjj.get(k).size+"  "+spacee+"  "+"Qty : "+sizeQtyObjj.get(k).qty.repeat(1)+"\n"
                    contextHeader+=sizeQtyRoot
                }
            }
            pdfBody+=contextHeader+"\n"
        }

        val image = BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher)

        val path = FTStorageUtils.stringToPdf(pdfBody, mContext, "OrderSingleDetalis" +
                "_" + Pref.user_id + AppUtils.getCurrentDateTime().toString().replace(" ", "R").replace(":", "_") + ".pdf", image, heading, 3.7f)



        if (!TextUtils.isEmpty(path)) {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                val fileUrl = Uri.parse(path)
                val file = File(fileUrl.path)
                val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
//                val uri = Uri.fromFile(file)
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
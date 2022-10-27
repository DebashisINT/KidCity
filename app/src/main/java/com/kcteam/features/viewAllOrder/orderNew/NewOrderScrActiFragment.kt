package com.kcteam.features.viewAllOrder.orderNew

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.NewOrderColorEntity
import com.kcteam.app.domain.NewOrderGenderEntity
import com.kcteam.app.domain.NewOrderProductEntity
import com.kcteam.app.domain.NewOrderSizeEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.DecimalDigitsInputFilter
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.viewAllOrder.interf.SizeListNewOrderOnClick
import com.kcteam.features.viewAllOrder.model.ColorList
import com.kcteam.features.viewAllOrder.model.NewOrderCartModel
import com.kcteam.features.viewAllOrder.model.ProductOrder
import com.kcteam.features.viewAllOrder.presentation.ColorListDialog
import com.kcteam.features.viewAllOrder.presentation.GenderListDialog
import com.kcteam.features.viewAllOrder.presentation.NewOrderSizeAdapter
import com.kcteam.features.viewAllOrder.presentation.ProductListNewOrderDialog
import com.kcteam.widgets.AppCustomTextView
import io.fabric.sdk.android.services.common.CommonUtils.hideKeyboard

class NewOrderScrActiFragment : BaseFragment(), View.OnClickListener {

    lateinit var btn_add_cart: RelativeLayout
    lateinit var btn_next: Button

    private lateinit var mContext: Context

    private lateinit var genderSpinner: AppCustomTextView
    private lateinit var productSpinner: AppCustomTextView
    private lateinit var colorSpinner: AppCustomTextView
    private lateinit var ll_gender: LinearLayout
    private lateinit var ll_product: LinearLayout
    private lateinit var ll_color: LinearLayout
    private lateinit var horr_scroll_v: HorizontalScrollView

    private var gender_list: List<NewOrderGenderEntity> = listOf()
    private var product_list: List<NewOrderProductEntity> = listOf()
    private var color_list: List<NewOrderColorEntity> = listOf()
    private var size_list: List<NewOrderSizeEntity> = listOf()

    private lateinit var rv_size: RecyclerView
    private var adapterSize: NewOrderSizeAdapter? = null

    private var isGenderSel: Boolean = false
    private var isProductSel: Boolean = false

    private var productId: Int = 0
    private var colorId: Int = 0

    private var final_order_list: ArrayList<NewOrderCartModel> = ArrayList()

    private lateinit var ll_size_icon: LinearLayout

    private lateinit var ll_rate_ll: LinearLayout
    private lateinit var ll_stock_ll: LinearLayout
    private lateinit var et_rate_new_ord: EditText
    private lateinit var tv_stock_new_ord: TextView

    private lateinit var sizeText: TextView

    private lateinit var sizeIcon: ImageView

    private lateinit var ColorTv: TextView

    private lateinit var color_IV: ImageView

    private lateinit var genderTv: TextView



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var shop_id: String = ""
        fun getInstance(objects: Any): NewOrderScrActiFragment {
            val Fragment = NewOrderScrActiFragment()
            shop_id = objects.toString()
            return Fragment
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_new_order_screen_activity, container, false)
        initView(view)
        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initView(view: View?) {
        ll_root=view!!.findViewById(R.id.lll_root) as LinearLayout
        horr_scroll_v=view!!.findViewById(R.id.horr_scroll_v) as HorizontalScrollView
        btn_add_cart = view!!.findViewById(R.id.btn_frag_new_order_screen_add_cart)
        genderSpinner = view!!.findViewById(R.id.genderSpinner)
        productSpinner = view!!.findViewById(R.id.ProductSpinner)
        colorSpinner = view!!.findViewById(R.id.ColorSpinner)
        ll_gender = view!!.findViewById(R.id.ll_new_order_scr_gender)
        ll_product = view!!.findViewById(R.id.ll_new_order_scr_product)
        ll_color = view!!.findViewById(R.id.ll_new_order_scr_color)

        sizeIcon = view!!.findViewById(R.id.sizeIcon)
        sizeText = view!!.findViewById(R.id.sizeText)


        color_IV = view!!.findViewById(R.id.color_IV)
        ColorTv = view!!.findViewById(R.id.ColorTv)

        btn_next = view!!.findViewById(R.id.btn_nextttt)
        btn_next.setOnClickListener(this)

        rv_size = view!!.findViewById(R.id.rv_order_list_size)

        ll_size_icon = view!!.findViewById(R.id.ll_order_list_list_icon)
        ll_rate_ll = view!!.findViewById(R.id.ll_item_new_ord_rate_root)
        ll_stock_ll = view!!.findViewById(R.id.ll_item_new_ord_stock_root)
        et_rate_new_ord = view!!.findViewById(R.id.et_rate_new_ord)
        tv_stock_new_ord = view!!.findViewById(R.id.tv_stock_new_ord)

        genderTv = view!!.findViewById(R.id.genderTv)

        //gender vs product type new order
        //genderTv.text=getString(R.string.GenderTextNewOrd)
        //genderSpinner.text="Select "+getString(R.string.GenderTextNewOrd)
        genderTv.text=getString(R.string.ProductTextNewOrd)
        genderSpinner.text="Select "+getString(R.string.ProductTextNewOrd)


        var horizontalLayout = LinearLayoutManager(
                mContext,
                LinearLayoutManager.HORIZONTAL,
                false)
        rv_size.setLayoutManager(horizontalLayout)
        try {
            et_rate_new_ord.setFilters(arrayOf<InputFilter>(DecimalDigitsInputFilter(9, 2)))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        ll_gender.setOnClickListener(this)
        ll_product.setOnClickListener(this)
        ll_color.setOnClickListener(this)

        btn_add_cart.setOnClickListener(this)

        sizeIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray))
        sizeText.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray))


//        color_IV.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray))
        color_IV.setImageDrawable(getResources(). getDrawable(R.drawable.ic_colour_new_order_gray))
//        color_IV.background = getDrawable(mContext, R.drawable.ic_colour_new_order_gray)
        ColorTv.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray))



//        sizeIcon.setBackgroundColor(R.color.color_custom_red)
//        sizeText.setTextColor(R.color.default_text_color)
    }

    private fun loadGender() {
        sizeIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray))
        sizeText.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray))

        ll_size_icon.visibility=View.GONE
        ll_rate_ll.visibility=View.GONE
        ll_stock_ll.visibility=View.GONE
        var gender_list = AppDatabase.getDBInstance()?.newOrderGenderDao()?.getGenderList() as List<NewOrderGenderEntity>
        if (gender_list != null && gender_list.isNotEmpty()) {
            GenderListDialog.newInstance(gender_list as ArrayList<NewOrderGenderEntity>) {
                isGenderSel = true
                genderSpinner.text = it.gender
                productSpinner.text = "Select Product"
                product_list = emptyList()
                product_list = AppDatabase.getDBInstance()?.newOrderProductDao()?.getProductListGenderWise(it.gender.toString()) as List<NewOrderProductEntity>
            }.show((mContext as DashboardActivity).supportFragmentManager, "")
        } else {
            //Toaster.msgShort(mContext, "No Gender Found")
            //gender vs product type new order
            //Toaster.msgShort(mContext, "No "+ getString(R.string.GenderTextNewOrd) +"Found")
            Toaster.msgShort(mContext, "No "+ getString(R.string.ProductTextNewOrd) +"Found")
        }
    }

    private fun loadProduct() {
        color_IV.setImageDrawable(getResources(). getDrawable(R.drawable.ic_colour_new_order))
        ColorTv.setTextColor(ContextCompat.getColor(mContext, R.color.default_text_color))
        if (product_list != null && product_list.isNotEmpty()) {
            ProductListNewOrderDialog.newInstance(product_list as ArrayList<NewOrderProductEntity>) {
                isProductSel = true
                productId = it.product_id!!
                productSpinner.text = it.product_name
                color_list = emptyList()
                color_list = AppDatabase.getDBInstance()?.newOrderColorDao()?.getColorListProductWise(it.product_id!!) as List<NewOrderColorEntity>

                try{
                    if(Pref.isRateOnline){
                    et_rate_new_ord.setText(AppDatabase.getDBInstance()?.productRateDao()?.getProductRateByProductID(it.product_id!!.toString())?.rate1.toString())
                    }
                }catch (ex:Exception){

                }
                if (Pref.isRateNotEditable) {
                    et_rate_new_ord.isEnabled=false
                }else{
                    et_rate_new_ord.isEnabled=true
                }

                try{
                    tv_stock_new_ord.text = AppDatabase.getDBInstance()?.productRateDao()?.getProductRateByProductID(it.product_id!!.toString())?.stock_amount.toString()
                }catch (ex:Exception){

                }



            }.show((mContext as DashboardActivity).supportFragmentManager, "")
        } else {
            Toaster.msgShort(mContext, "No Product Found")
        }

    }


    private fun loadColor() {
        if (color_list != null && color_list.isNotEmpty()) {
            ColorListDialog.newInstance(color_list as ArrayList<NewOrderColorEntity>) {
                colorSpinner.text = it.color_name
                colorId = it.color_id!!
                size_list = emptyList()
                size_list = AppDatabase.getDBInstance()?.newOrderSizeDao()?.getSizeListProductWise(it.product_id!!) as List<NewOrderSizeEntity>

                try{
                    if(Pref.isRateOnline){
                        et_rate_new_ord.setText(AppDatabase.getDBInstance()?.productRateDao()?.getProductRateByProductID(it.product_id!!.toString())?.rate1.toString())
                    }
                }catch (ex:Exception){

                }
                if (Pref.isRateNotEditable) {
                    et_rate_new_ord.isEnabled=false
                }else{
                    et_rate_new_ord.isEnabled=true
                }
                try{
                    tv_stock_new_ord.text = AppDatabase.getDBInstance()?.productRateDao()?.getProductRateByProductID(it.product_id!!.toString())?.stock_amount.toString()
                }catch (ex:Exception){

                }

                loadSize()

            }.show((mContext as DashboardActivity).supportFragmentManager, "")
        } else {
            Toaster.msgShort(mContext, "No Color Found")
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun loadSize() {

//        sizeIcon.setBackgroundColor(R.color.color_custom_red)
        sizeIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.color_custom_red))
        sizeText.setTextColor(ContextCompat.getColor(mContext, R.color.default_text_color))

//        sizeText.setTextColor(R.color.default_text_color)

        ll_size_icon.visibility = View.VISIBLE
        ll_rate_ll.visibility = View.VISIBLE
        ll_stock_ll.visibility = View.VISIBLE

        if (size_list != null && size_list.isNotEmpty()) {




            ll_root.removeAllViews()
            for(i in 0..size_list.size-1){
                createDynaView(size_list.get(i).size.toString())
            }


            //var mLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            //rv_size.setLayoutManager(mLayoutManager)
            //rv_size.setNestedScrollingEnabled(true)
            //rv_size.setHasFixedSize(true)

            adapterSize = NewOrderSizeAdapter(mContext, size_list, object : SizeListNewOrderOnClick {
                override fun sizeListOnClick(size: NewOrderSizeEntity) {

                }
            })

            rv_size.adapter = adapterSize


        } else {
            Toaster.msgShort(mContext, "No Size Found")
        }
    }

    override fun onResume() {
        super.onResume()
        (mContext as DashboardActivity).tv_cart_count.text = final_order_list.size.toString()
        (mContext as DashboardActivity).tv_cart_count.visibility = View.VISIBLE
        CustomStatic.NewOrderTotalCartItem=final_order_list.size
        println("new_ord onresume");
    }

    fun updateCartNumber() {

    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        var asd = "asd"
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_frag_new_order_screen_add_cart -> {

                    var totalQty: Int = 0
                    for (x in 0..ll_root.childCount - 1) {
                        var vi: View = ll_root.getChildAt(x)
                        var et_qty = vi.findViewById(R.id.et_size_count) as EditText
                        var tv_size = vi.findViewById(R.id.item_new_order_product_sizeTv) as AppCustomTextView
                        if (et_qty.text.toString().length > 0) {
                            totalQty = totalQty + et_qty.text.toString().toInt()
                        }
                    }
                    if (totalQty == 0) {
                        Toaster.msgShort(mContext, "Please add Qty for following size.")
                        return
                    }


                    sizeIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_gray))
                    sizeText.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray))

//                    color_IV.background = getDrawable(mContext, R.drawable.ic_colour_new_order_gray)
                    color_IV.setImageDrawable(getResources(). getDrawable(R.drawable.ic_colour_new_order_gray))
                    ColorTv.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray))
                    ll_size_icon.visibility = View.GONE
                    ll_rate_ll.visibility = View.GONE
                    ll_stock_ll.visibility = View.GONE
                    var isSame: Boolean = false
                    if (isGenderSel) {
                        if (isProductSel) {

                            var order_list: ArrayList<ProductOrder> = ArrayList()

                            for (i in 0..final_order_list.size - 1) {
                                if (final_order_list.get(i).product_id == productId.toString()) {
                                    if (final_order_list.get(i).gender == genderSpinner.text.toString()) {

                                        var order_list: ArrayList<ProductOrder> = ArrayList()
                                        for (x in 0..ll_root.childCount - 1) {
                                            var vi: View = ll_root.getChildAt(x)
                                            var et_qty = vi.findViewById(R.id.et_size_count) as EditText
                                            var tv_size = vi.findViewById(R.id.item_new_order_product_sizeTv) as AppCustomTextView
                                            if (et_qty.text.toString().trim().length > 0) {
                                                var productOrder: ProductOrder = ProductOrder(tv_size.text.toString(), et_qty.text.toString())


                                                /*for(j in 0.. final_order_list.get(i).color_list.size-1){
                                                    for(k in 0..final_order_list.get(i).color_list.get(j).order_list.size-1){
                                                        if(final_order_list.get(i).color_list.get(j).order_list.get(k).size.equals(productOrder.size)){
                                                            var tt="asf"
                                                        }
                                                    }
                                                }*/

                                                order_list.add(productOrder)
                                            }
                                        }
                                        var colorList: ColorList = ColorList(colorSpinner.text.toString(), colorId.toString(), order_list)

                                        var isSameColorObj: Boolean = false

                                        for (p in 0..final_order_list.get(i).color_list.size - 1) {
                                            if (final_order_list.get(i).color_list.get(p).color_id.equals(colorList.color_id)) {
                                                ////////////
                                                var order_list1: ArrayList<ProductOrder> = ArrayList()
                                                for (x in 0..ll_root.childCount - 1) {
                                                    var vi: View = ll_root.getChildAt(x)
                                                    var et_qty = vi.findViewById(R.id.et_size_count) as EditText
                                                    var tv_size = vi.findViewById(R.id.item_new_order_product_sizeTv) as AppCustomTextView
                                                    var productOrder1: ProductOrder
                                                    if (et_qty.text.toString().length > 0) {
                                                        productOrder1 = ProductOrder(tv_size.text.toString(), et_qty.text.toString())
                                                    } else {
                                                        productOrder1 = ProductOrder(tv_size.text.toString(), "0")
                                                    }

                                                    order_list1.add(productOrder1)
                                                }
                                                var colorList1: ColorList = ColorList(colorSpinner.text.toString(), colorId.toString(), order_list1)
                                                ///////////////////////////////

                                                isSameColorObj = true
                                                var loopSize = 0
                                                var obj = final_order_list.get(i).color_list.get(p).order_list
                                                var objj = colorList.order_list

                                                var ob1 = order_list1
                                                var ob2 = final_order_list.get(i).color_list.get(p).order_list
                                                for (o in 0..ob1.size - 1) {
                                                    for (oo in 0..ob2.size - 1) {
                                                        if (ob1.get(o).size.equals(ob2.get(oo).size)) {
                                                            var qty = ob1.get(o).qty.toInt() + ob2.get(oo).qty.toInt()
                                                            ob1.get(o).qty = qty.toString()
                                                        }
                                                    }
                                                }

                                                var obFinal: ArrayList<ProductOrder> = ArrayList()
                                                for (o in 0..ob1.size - 1) {
                                                    if (ob1.get(o).qty.equals("0")) {

                                                    } else {
                                                        obFinal.add(ob1.get(o))
                                                    }
                                                }
                                                ob1 = obFinal

                                                /*                          for(y in 0..obj.size-1){
                                                                                  for(yy in 0..colorList1.order_list.size-1){
                                                                                      if(obj.get(y).size.equals(colorList1.order_list.get(yy).size)){
                                                                                          var qqty:Int=colorList1.order_list.get(yy).qty.toInt()+obj.get(y).qty.toInt()
                                                                                          obj.get(y).qty=qqty.toString()
                                                                                          var ty="asdadas"
                                                                                      }
                                                                                  }
                                                                              }

                                                                          if(obj.size==colorList.order_list.size){
                                                                              for(l in 0..obj.size-1){
                                                                                  var qqty:Int=obj.get(l).qty.toInt()+colorList.order_list.get(l).qty.toInt()
                                                                                  obj.get(l).qty=qqty.toString()
                                                                              }
                                                                          }
                                                                          else{
                                                                              for(p in 0..colorList1.order_list.size-1){
                                                                                  var tt="op"
                                                                              }
                                                                          }
                          */
                                                final_order_list.get(i).color_list.get(p).order_list = ob1
                                                if(et_rate_new_ord.text.toString().equals("")){
                                                    final_order_list.get(i).rate = "0"
                                                }else{
                                                final_order_list.get(i).rate = et_rate_new_ord.text.toString()
                                                }
                                            }
                                        }

                                        if (isSameColorObj == false){
                                            final_order_list.get(i).color_list.add(colorList)
                                            if(et_rate_new_ord.text.toString().equals("")){
                                                final_order_list.get(i).rate = "0"
                                            }else{
                                            final_order_list.get(i).rate = et_rate_new_ord.text.toString()
                                            }
                                        }
                                        isSame = true
                                    }
                                }
                            }


                            if (!isSame) {
                                var cartData: NewOrderCartModel = NewOrderCartModel()
                                cartData.product_id = productId.toString()
                                cartData.product_name = productSpinner.text.toString()
                                cartData.gender = genderSpinner.text.toString()

                                //for (i in 0..rv_size.childCount - 1) {
                                for (i in 0..ll_root.childCount - 1) {
                                    var vi: View = ll_root.getChildAt(i)
                                    var et_qty = vi.findViewById(R.id.et_size_count) as EditText
                                    var tv_size = vi.findViewById(R.id.item_new_order_product_sizeTv) as AppCustomTextView
                                    if (et_qty.text.toString().trim().length > 0) {
                                        var productOrder: ProductOrder = ProductOrder(tv_size.text.toString(), et_qty.text.toString())
                                        order_list.add(productOrder)
                                    }
                                }
                                var colorList: ColorList = ColorList(colorSpinner.text.toString(), colorId.toString(), order_list)
                                cartData.color_list.add(colorList)

                                if(et_rate_new_ord.text.toString().equals("")){
                                    cartData.rate = "0"
                                }else{
                                cartData.rate = et_rate_new_ord.text.toString()
                                }

                                final_order_list.add(cartData)
                            }


                            et_rate_new_ord.setText("")
                            et_rate_new_ord.setHint("Rate  ( \u20B9 )")

                            isSame = false
                            genderSpinner.text=genderSpinner.text.toString()
                            productSpinner.text = productSpinner.text.toString()
                            //genderSpinner.text = "Select Gender"
                            //productSpinner.text = "Select Product"
                            colorSpinner.text = "Select  Color"
                            rv_size.adapter = null

                            //isGenderSel = false
                            //isProductSel = false

                            (mContext as DashboardActivity).tv_cart_count.text = final_order_list.size.toString()
                            (mContext as DashboardActivity).tv_cart_count.visibility = View.VISIBLE

                            CustomStatic.NewOrderTotalCartItem=final_order_list.size

                            /*Added Product Voice 13-09-2021*/
                            val simpleDialog = Dialog(mContext)
                            simpleDialog.setCancelable(false)
                            simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            simpleDialog.setContentView(R.layout.dialog_message)
                            val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                            val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                            dialog_yes_no_headerTV.text = "Hi " + Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!) + "!"
                            dialogHeader.text = "Item Added Successfully."
                            val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                            dialogYes.setOnClickListener({ view ->
                                simpleDialog.cancel()
                            })
                            simpleDialog.show()
                            voiceAttendanceMsg("Item Added Successfully")
//                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.add_product_cart))
                            hideKeyboard(mContext, view)

                        } else {
                            Toaster.msgShort(mContext, "Please Select a Product")
                        }
                    } else {
                        //Toaster.msgShort(mContext, "Please Select Gender First")
                        //gender vs product type new order
                        //Toaster.msgShort(mContext, "Please Select "+ getString(R.string.GenderTextNewOrd) +" First")
                        Toaster.msgShort(mContext, "Please Select "+ getString(R.string.ProductTextNewOrd) +" First")
                    }

                }
                R.id.ll_new_order_scr_gender -> {
                    color_IV.setImageDrawable(getResources(). getDrawable(R.drawable.ic_colour_new_order_gray))
                    ColorTv.setTextColor(ContextCompat.getColor(mContext, R.color.dark_gray))
                    productSpinner.text = productSpinner.text.toString()
                    //productSpinner.text = "Select Product"
                    colorSpinner.text = "Select  Color"
                    rv_size.adapter = null
                    ll_root.removeAllViews()

                    loadGender()
                }
                R.id.ll_new_order_scr_product -> {
                    if (isGenderSel) {
                        colorSpinner.text = "Select  Color"
                        rv_size.adapter = null
                        ll_root.removeAllViews()
                        loadProduct()
                    } else {
                        //Toaster.msgShort(mContext, "Please Select Gender First")
                        //gender vs product type new order
                        //Toaster.msgShort(mContext, "Please Select "+ getString(R.string.GenderTextNewOrd) +"First")
                        Toaster.msgShort(mContext, "Please Select "+ getString(R.string.ProductTextNewOrd) +"First")
                    }
                }
                R.id.ll_new_order_scr_color -> {
                    if (isGenderSel) {
                        if (isProductSel) {
                            rv_size.adapter = null
                            ll_root.removeAllViews()
                            loadColor()

                        } else {
                            Toaster.msgShort(mContext, "Please Select a Product")
                        }
                    } else {
                        //Toaster.msgShort(mContext, "Please Select Gender First")
                        //gender vs product type new order
                        //Toaster.msgShort(mContext, "Please Select "+ getString(R.string.GenderTextNewOrd) +"First")
                        Toaster.msgShort(mContext, "Please Select "+ getString(R.string.ProductTextNewOrd) +"First")
                    }
                }
//                R.id.btn_nextttt ->{
//                    (mContext as DashboardActivity).loadFragment(FragType.NeworderScrCartFragment, true, final_order_list)
//                }

            }
        }
    }

    fun clickToCart() {
        CustomStatic.IsFromViewNewOdrScr = false
        if ((mContext as DashboardActivity).tv_cart_count.text != "0"){
            (mContext as DashboardActivity).loadFragment(FragType.NeworderScrCartFragment, true, final_order_list)
        }
        else
            (mContext as DashboardActivity).showSnackMessage("No item is available in cart")
    }


    fun updateCartQty() {
        (mContext as DashboardActivity).tv_cart_count.text = final_order_list.size.toString()
        (mContext as DashboardActivity).tv_cart_count.visibility = View.VISIBLE
        CustomStatic.NewOrderTotalCartItem=final_order_list.size
    }


    private fun voiceAttendanceMsg(msg: String) {
        if (Pref.isVoiceEnabledForAttendanceSubmit) {
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Item Added Successfully", "TTS error in converting Text to Speech!");
        }
    }



    //////////
    var vi:LayoutInflater ? = null
    var viewC: View? = null
    lateinit var ll_root: LinearLayout
    private fun createDynaView(sizeStr: String){
        vi = mContext.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        viewC = vi!!.inflate(R.layout.item_new_order_product_size, null)

        ll_root.addView(viewC)

        var v:View = ll_root.getChildAt(ll_root.childCount - 1)

        var sizeLst:AppCustomTextView=(v.findViewById(R.id.item_new_order_product_sizeTv) as AppCustomTextView)
        var qtyLst:EditText=(v.findViewById(R.id.et_size_count) as EditText)
        sizeLst.text=sizeStr


        qtyLst.setOnFocusChangeListener({ v, hasFocus ->
            if (hasFocus) {
                qtyLst.setBackgroundResource(R.drawable.blue_line_custom_selected)
            } else {
                qtyLst.setBackgroundResource(R.drawable.blue_line_custom)
            }
        })

        var x:Int
        var y:Int
        x = qtyLst.getLeft();
        y = qtyLst.getTop();
        horr_scroll_v.scrollTo(x, y);


    }






}
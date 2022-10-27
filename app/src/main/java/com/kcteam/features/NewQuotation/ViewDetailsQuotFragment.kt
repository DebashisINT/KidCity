package com.kcteam.features.NewQuotation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.NewQuotation.adapter.ShowAddedProductAdapter
import com.kcteam.features.NewQuotation.api.GetQuotRegProvider
import com.kcteam.features.NewQuotation.model.*
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.widgets.AppCustomTextView
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ViewDetailsQuotFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mContext: Context
    lateinit var shopName: TextView
    lateinit var phone: ImageView
    lateinit var quotNumber: TextView
    lateinit var time: TextView
    lateinit var progress_wheel: ProgressWheel
    var addedProdList:ArrayList<quotation_product_details_list> = ArrayList()
    var showAddedProdAdapter: ShowAddedProductAdapter? = null
    private lateinit var rv_addedProduct: RecyclerView

    private lateinit var floating_fab: FloatingActionMenu
    private var getFloatingVal:ArrayList<String> = ArrayList()
    private var programFab1: FloatingActionButton? = null

    private lateinit var tv_updates: AppCustomTextView

    lateinit var addQuotEditResult: ViewDetailsQuotResponse

    var addQuotData = EditQuotRequestData()

    companion object {
         var QuotID:String  = ""
         var DocID : String = ""
        var obj = shop_wise_quotation_list()
        fun getInstance(ob: Any?): ViewDetailsQuotFragment {
            val fragment = ViewDetailsQuotFragment()

            if (!TextUtils.isEmpty(ob.toString())) {
                QuotID = ob.toString().split(",").get(0).toString()
                DocID = ob.toString().split(",").get(1).toString()
                if(QuotID.equals("x"))
                    QuotID = ""
                if(DocID.equals("x"))
                    DocID = ""
            }


            /*val bundle = Bundle()
            bundle.putString("QuotId", QuotID as String?)
            bundle.putString("DocId", DocID as String?)
            fragment.arguments = bundle*/

            return fragment
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        //QuotID = arguments?.getString("QuotId").toString()
        //DocID = arguments?.getString("DocId").toString()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_view_details_quot_list, container, false)
        initView(view)
        if(AppUtils.isOnline(mContext)){
            Handler().postDelayed(Runnable {
                if(QuotID.equals(""))
                    docwiseQuotViewListCall(DocID)
                if(DocID.equals(""))
                    quotViewListCall(QuotID)
            }, 400)
        }
        else{
            Toaster.msgShort(mContext, "No Internet connection")
        }
        return view
    }

    private fun initView(view: View) {
        shopName = view.findViewById(R.id.tv_frag_view_details_quot_list_shopName)
        phone = view.findViewById(R.id.iv_frag_view_details_quot_list_phone)
        quotNumber = view.findViewById(R.id.tv_frag_view_details_quot_list_quotId)
        time = view.findViewById(R.id.tv_frag_view_details_quot_list_date)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        rv_addedProduct = view.findViewById(R.id.quot_view_list_rv)
        floating_fab = view.findViewById(R.id.floating_fab_frag_view_dtls)
        tv_updates = view.findViewById(R.id.update_TV_frag_view_details_quot_list)

        tv_updates.setOnClickListener(this)


        floating_fab.apply {
            menuIconView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_add))
            menuButtonColorNormal = mContext.resources.getColor(R.color.colorAccent)
            menuButtonColorPressed = mContext.resources.getColor(R.color.colorPrimaryDark)
            menuButtonColorRipple = mContext.resources.getColor(R.color.colorPrimary)

            isIconAnimated = false
            setClosedOnTouchOutside(true)
        }

        getFloatingVal.add("Update")

        getFloatingVal.forEachIndexed { i, value ->
            if (i == 0) {
                programFab1 = FloatingActionButton(activity)
                programFab1?.let {
                    it.buttonSize = FloatingActionButton.SIZE_MINI
                    it.id = 100 + i
                    it.colorNormal = mContext.resources.getColor(R.color.colorPrimaryDark)
                    it.colorPressed = mContext.resources.getColor(R.color.delivery_status_green)
                    it.colorRipple = mContext.resources.getColor(R.color.delivery_status_green)
                    it.labelText = getFloatingVal[0]
                    floating_fab.addMenuButton(it)
                    it.setOnClickListener(this)
                    it.setImageResource(R.drawable.ic_tick_float_icon)
                }
            }
        }
    }

    private fun quotViewListCall(quotId: String) {
        try{
            progress_wheel.spin()
            val repository = GetQuotRegProvider.provideSaveButton()
            BaseActivity.compositeDisposable.add(
                    repository.viewDetailsQuot(quotId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addQuotResult = result as ViewDetailsQuotResponse

                                addQuotEditResult=addQuotResult

                                progress_wheel.stopSpinning()
                                if (addQuotResult!!.status == NetworkConstant.SUCCESS) {
                                    setData(addQuotResult)
                                    if(addQuotResult!!.quotation_product_details_list!!.size>0){
                                        addedProdList.clear()
                                        addedProdList.addAll(addQuotResult!!.quotation_product_details_list!!)
                                        setAdapter()
                                    }

                                }else {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                progress_wheel.stopSpinning()
                                BaseActivity.isApiInitiated = false
                                if (error != null) {
                                }
                            })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
            progress_wheel.stopSpinning()
        }

    }

    private fun docwiseQuotViewListCall(docId: String) {
        try{
            progress_wheel.spin()
            val repository = GetQuotRegProvider.provideSaveButton()
            BaseActivity.compositeDisposable.add(
                    repository.viewDetailsDoc(docId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addQuotResult = result as ViewDetailsQuotResponse

                                addQuotEditResult=addQuotResult

                                progress_wheel.stopSpinning()
                                if (addQuotResult!!.status == NetworkConstant.SUCCESS) {
                                    setData(addQuotResult)
                                    if(addQuotResult!!.quotation_product_details_list!!.size>0){
                                        addedProdList.clear()
                                        addedProdList.addAll(addQuotResult!!.quotation_product_details_list!!)
                                        setAdapter()
                                    }

                                }else {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                progress_wheel.stopSpinning()
                                BaseActivity.isApiInitiated = false
                                if (error != null) {
                                }
                            })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
            progress_wheel.stopSpinning()
        }

    }

    private fun setData(addQuotResult: ViewDetailsQuotResponse) {
        shopName.setText(addQuotResult.shop_name)
        quotNumber.setText("Quot Number:  "+ addQuotResult.quotation_number)
        phone.setOnClickListener {
            IntentActionable.initiatePhoneCall(mContext, addQuotResult.shop_phone_no)
        }
        time.setText(AppUtils.convertDateTimeToCommonFormat(addQuotResult.quotation_date_selection!!.subSequence(0,10).toString()).toString())
    }
    private fun setAdapter() {
        showAddedProdAdapter=ShowAddedProductAdapter(mContext,addedProdList,object :ShowAddedProductAdapter.OnClickListener{
            override fun onEditCLick(obj: quotation_product_details_list) {
                editRate(obj)
            }
        })
        rv_addedProduct.adapter=showAddedProdAdapter
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            100 -> {
                floating_fab.close(true)
                programFab1?.colorNormal = mContext.resources.getColor(R.color.delivery_status_green)
                programFab1?.setImageResource(R.drawable.ic_tick_float_icon)
                CustomStatic.IsNewQuotEdit=true
                showAddedProdAdapter!!.notifyDataSetChanged()
            }
            R.id.update_TV_frag_view_details_quot_list ->{
                editQuot()
            }
        }
    }

    private fun editQuot() {
        addQuotData.updated_by_user_id = Pref.user_id
        addQuotData.updated_date_time = AppUtils.getCurrentDateTime()
        addQuotData.quotation_number = addQuotEditResult.quotation_number
        addQuotData.quotation_date_selection = addQuotEditResult.quotation_date_selection
        addQuotData.project_name = addQuotEditResult.project_name
        addQuotData.shop_id = addQuotEditResult.shop_id
        addQuotData.taxes = addQuotEditResult.taxes
        addQuotData.Freight = addQuotEditResult.Freight
        addQuotData.delivery_time = addQuotEditResult.delivery_time
        addQuotData.payment = addQuotEditResult.payment
        addQuotData.validity = addQuotEditResult.validity
        addQuotData.billing = addQuotEditResult.billing
        addQuotData.product_tolerance_of_thickness = addQuotEditResult.product_tolerance_of_thickness
        addQuotData.tolerance_of_coating_thicknes = addQuotEditResult.tolerance_of_coating_thickness
        addQuotData.salesman_user_id = addQuotEditResult.salesman_user_id
        addQuotData.quotation_updated_lat = Pref.latitude.toString()
        addQuotData.quotation_updated_long = Pref.longitude.toString()
        addQuotData.quotation_updated_address = LocationWizard.getAdressFromLatlng(mContext, Pref.latitude!!.toDouble(), Pref.longitude!!.toDouble())

        addQuotData.product_list = ArrayList()
        addQuotData.product_list = addQuotEditResult.quotation_product_details_list!!

        editQuotButtoncall(addQuotData)

    }

    private fun editQuotButtoncall(addQuot: EditQuotRequestData) {
        try{
            BaseActivity.isApiInitiated = true
            progress_wheel.spin()
            val repository = GetQuotRegProvider.provideSaveButton()
            BaseActivity.compositeDisposable.add(
                    repository.editQuot(addQuot)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as BaseResponse
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    (mContext as DashboardActivity).showSnackMessage("Quotation updated successfully.")

                                    Handler().postDelayed(Runnable {
                                        (mContext as DashboardActivity).onBackPressed()
                                    }, 1200)

                                } else if (addShopResult.status == "205") {
                                    (mContext as DashboardActivity).showSnackMessage("Duplicate Quotation Number.")
                                } else {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                }
                            }, { error ->
                                progress_wheel.stopSpinning()
                                BaseActivity.isApiInitiated = false
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                if (error != null) {
                                }
                            })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
            BaseActivity.isApiInitiated = false
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
            progress_wheel.stopSpinning()
        }
    }




    private fun editRate(obj: quotation_product_details_list){
        var simpleDialog1 = Dialog(mContext)
        simpleDialog1.setCancelable(true)
        simpleDialog1.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog1.setContentView(R.layout.dialog_new_quot_rate_edit)

        val productName = simpleDialog1.findViewById(R.id.tv_row_new_quot_added_prod_name) as TextView
        val colorName = simpleDialog1.findViewById(R.id.tv_row_new_quot_added_prod_color) as TextView
        val rate_sqft = simpleDialog1.findViewById(R.id.et_row_new_quot_added_prod_rate_sqft) as EditText
        val rate_sqmtr = simpleDialog1.findViewById(R.id.et_row_new_quot_added_prod_rate_sqmtr) as EditText
        val tv_update = simpleDialog1.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView
        val tv_cancel = simpleDialog1.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView

        tv_cancel.setOnClickListener({ view ->
            simpleDialog1.cancel()
        })
        tv_update.setOnClickListener({ view ->
            for(i in 0..addedProdList.size-1){
                if(obj.product_id == addedProdList.get(i).product_id && obj.color_id == addedProdList.get(i).color_id){
                    addedProdList.get(i).rate_sqft=rate_sqft.text.toString()
                    addedProdList.get(i).rate_sqmtr=rate_sqmtr.text.toString()

                    addedProdList.get(i).qty = 0
                    addedProdList.get(i).amount = 0.0

                    addQuotEditResult.quotation_product_details_list=addedProdList!!
                    showAddedProdAdapter!!.notifyDataSetChanged()
                    simpleDialog1.cancel()
                    break
                }
            }
        })

        productName.text = obj.product_name
        colorName.text = "Color : "+obj.color_name

        if(CustomStatic.IsNewQuotEdit){
            tv_updates.visibility=View.VISIBLE
        }else{
            tv_updates.visibility=View.GONE
        }

        simpleDialog1.show()



        rate_sqft.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!=null && s!="" && s.toString().length>0){
                    var value=s.toString().toDouble()* Pref.SqMtrRateCalculationforQuotEuro.toDouble()
                    rate_sqmtr.setText(value.toString())
                }else{
                    rate_sqmtr.setText("")
                }
            }
        })

    }

}
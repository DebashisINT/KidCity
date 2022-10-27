package com.kcteam.features.shopFeedbackHistory

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.ShopFeedbackEntity
import com.kcteam.features.login.ShopFeedbackTempEntity
import com.kcteam.features.login.model.ShopFeedbackResponseModel
import com.kcteam.features.orderList.api.neworderlistapi.NewOrderListRepoProvider
import com.kcteam.features.shopFeedbackHistory.adapter.FeedBackListAdapter
import com.kcteam.features.shopFeedbackHistory.model.FeedBackListReq
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

class ShopFeedbackHisFrag: BaseFragment(),View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_addr_TV: AppCustomTextView
    private lateinit var myshop_contact_TV: AppCustomTextView
    private lateinit var tv_shop_type: AppCustomTextView
    private lateinit var shopImage: ImageView
    private lateinit var fromDate: AppCompatRadioButton
    private lateinit var toDate: AppCompatRadioButton
    private lateinit var noData: AppCustomTextView
    private lateinit var progresWheel: ProgressWheel
    lateinit var feedBackListViewAdapter: FeedBackListAdapter
    private lateinit var rvFeedbackList: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var submitFeedbackresultshow: ImageView
    private lateinit var headerView: View
    private lateinit var progress_wheel: ProgressWheel

    private var fromDateSel :String = ""
    private var toDateSel :String = ""

    var reqData = FeedBackListReq()

    var userIdForApi = Pref.user_id

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var shopDtls: AddShopDBModelEntity? = null
        fun newInstance(objects: Any): ShopFeedbackHisFrag {
            val fragment = ShopFeedbackHisFrag()
            shopDtls = objects as AddShopDBModelEntity
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_shop_feedback_his, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
        myshop_addr_TV = view.findViewById(R.id.myshop_address_TV)
        myshop_contact_TV = view.findViewById(R.id.tv_contact_number)
        shopImage = view.findViewById(R.id.shop_IV)
        fromDate = view.findViewById(R.id.frag_shop_feedback_dtls_from_date_range)
        rvFeedbackList = view.findViewById(R.id.rv_frag_shop_feedback_dtls)
        toDate = view.findViewById(R.id.fragshop_feedback_dtls_to_date_range)
        noData = view.findViewById(R.id.tv_no_data)
        progresWheel = view.findViewById(R.id.progress_wheel)
        submitFeedbackresultshow = view.findViewById(R.id.frag_shop_feedback_submit)
        headerView = view.findViewById(R.id.inflate_item_header_view)
        tv_shop_type = view.findViewById(R.id.tv_shop_type)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        var shopNameByID=""
        try{
            shopNameByID = AppDatabase.getDBInstance()!!.shopTypeDao().getShopNameById(shopDtls!!.type)
        }catch (ex:Exception){
            shopNameByID = "N.A"
        }
        tv_shop_type.text="Type : "+shopNameByID


        myshop_name_TV.text=shopDtls?.shopName
        myshop_addr_TV.text= shopDtls?.address
        myshop_contact_TV.text="Owner Contact Number: " + shopDtls?.ownerContactNumber.toString()

        val drawable = TextDrawable.builder()
                .buildRoundRect(shopDtls!!.shopName.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

        shopImage.setImageDrawable(drawable)
        fromDate.setOnClickListener(this)
        toDate.setOnClickListener(this)
        submitFeedbackresultshow.setOnClickListener(this)

        initAdapter()
    }

    val FromCalender = Calendar.getInstance(Locale.ENGLISH)
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.frag_shop_feedback_dtls_from_date_range -> {
                fromDate.error=null
                toDateSel=""
                toDate.text="To Date"
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.getDatePicker().maxDate = FromCalender.timeInMillis

                datePicker.show()
            }

            R.id.fragshop_feedback_dtls_to_date_range -> {
                if(fromDateSel.equals("") && fromDateSel.length==0){
                    Toaster.msgShort(mContext,"Please select From Date")
                    return

                }

                toDate.error=null
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date1, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.getDatePicker().maxDate =FromCalender.timeInMillis //+ (60*60*1000*24*90)
                datePicker.getDatePicker().minDate = selectFromDate.timeInMillis //+ (60*60*1000*24*80)
                datePicker.show()
            }
            R.id.frag_shop_feedback_submit -> {
                if (!AppUtils.isOnline(mContext)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                } else {
                    headerView.visibility = View.VISIBLE
                    rvFeedbackList.visibility = View.VISIBLE
                    if (fromDateSel.length > 0 && toDateSel.length > 0)
                        getShopFeedback()
                    else
                        Toaster.msgShort(mContext, "Please select date.")
                }
            }

        }
    }

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

var selectFromDate=Calendar.getInstance()

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        selectFromDate.set(year,monthOfYear,dayOfMonth)
        //tv_date_dialog.text=  AppUtils.getFormatedDateNew(AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time)),"dd-mm-yyyy","yyyy-mm-dd")
        fromDate.text=  AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time))
        fromDateSel = AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time))
    }

    val date1 = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        //tv_date_dialog.text=  AppUtils.getFormatedDateNew(AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time)),"dd-mm-yyyy","yyyy-mm-dd")
        toDate.text=  AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time))
        toDateSel=  AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time))
    }

    private fun getShopFeedback(){
        try{
            var fromDateStr=  AppUtils.getFormatedDateNew(fromDateSel,"dd-mm-yyyy","yyyy-mm-dd")
            var toDateStr=  AppUtils.getFormatedDateNew(toDateSel,"dd-mm-yyyy","yyyy-mm-dd")
            val repository = NewOrderListRepoProvider.provideOrderListRepository()
            BaseActivity.compositeDisposable.add(
                repository.getShopFeedback(userIdForApi!!, fromDateStr!!,toDateStr!!,"0")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val response = result as ShopFeedbackResponseModel
                        if (response.status == NetworkConstant.SUCCESS) {
                            if(response.shop_list!=null && response.shop_list!!.size>0){
                                val objList = response.shop_list!!
                                doAsync {
                                    println("xTag_ start")
                                    AppDatabase.getDBInstance()?.shopFeedbackTempDao()?.deleteAll()
                                    for(i in 0..objList!!.size-1){
                                        var feedList=objList.get(i).feedback_remark_list!!
                                        if(shopDtls!!.shop_id.equals(objList.get(i).shop_id)){
                                            for(j in 0..feedList.size-1){
                                                var ob: ShopFeedbackTempEntity = ShopFeedbackTempEntity()
                                                ob.apply {
                                                    shop_id=objList.get(i).shop_id
                                                    feedback=feedList.get(j).feedback
                                                    date_time=feedList.get(j).date_time
                                                    if(feedback.equals(""))
                                                        feedback="N/A"
                                                }

                                                AppDatabase.getDBInstance()?.shopFeedbackTempDao()?.insert(ob)
                                            }
                                        }
                                    }
                                    uiThread {
                                        println("xTag_ finish")
                                        initAdapterForOnline()
                                    }
                                }
                            }
                        } else {
                            noData.visibility = View.VISIBLE
                            rvFeedbackList.visibility = View.GONE
                        }

                    }, { error ->
                        error.localizedMessage
                    })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

    private fun initAdapter(){
        var feedList= AppDatabase.getDBInstance()?.shopFeedbackDao()?.getAllByShopID(shopDtls!!.shop_id)
        if(feedList!!.size>0){
            noData.visibility=View.GONE
            feedBackListViewAdapter=FeedBackListAdapter(mContext,feedList!! as ArrayList<ShopFeedbackEntity>)
            rvFeedbackList.adapter=feedBackListViewAdapter
        }else{
            var shopIsInRoom= AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopIdFromDtls(shopDtls!!.shop_id)
            if(shopIsInRoom!!.size>0){
                noData.visibility = View.VISIBLE
                rvFeedbackList.visibility = View.GONE
            }
            else {
                userIdForApi=CustomStatic.ShopFeedBachHisUserId
                getShopFeedbackOtherUsers()
            }
        }
    }

    private fun initAdapterForOnline( ){
        var feedList= AppDatabase.getDBInstance()?.shopFeedbackTempDao()?.getAllByShopID(shopDtls!!.shop_id)
        if(feedList!!.size>0){
            var filteredList:ArrayList<ShopFeedbackEntity> = ArrayList()
            for(i in 0..feedList.size-1){
                var obj:ShopFeedbackEntity = ShopFeedbackEntity()
                obj.apply {
                    shop_id=feedList.get(i).shop_id
                    feedback=feedList.get(i).feedback
                    date_time=feedList.get(i).date_time
                }
                filteredList.add(obj)
            }

            noData.visibility=View.GONE
            feedBackListViewAdapter=FeedBackListAdapter(mContext,filteredList!! as ArrayList<ShopFeedbackEntity>)
            rvFeedbackList.adapter=feedBackListViewAdapter
        }else{
                noData.visibility = View.VISIBLE
                rvFeedbackList.visibility = View.GONE
        }
    }

    private fun getShopFeedbackOtherUsers(){
        try{
//            var fromDateStr=  AppUtils.getFormatedDateNew(fromDateSel,"dd-mm-yyyy","yyyy-mm-dd")
//            var toDateStr=  AppUtils.getFormatedDateNew(toDateSel,"dd-mm-yyyy","yyyy-mm-dd")
            val repository = NewOrderListRepoProvider.provideOrderListRepository()
            BaseActivity.compositeDisposable.add(
                    repository.getShopFeedback(CustomStatic.ShopFeedBachHisUserId, "","","90")
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as ShopFeedbackResponseModel
                                if (response.status == NetworkConstant.SUCCESS) {
                                    if(response.shop_list!=null && response.shop_list!!.size>0){
                                        val objList = response.shop_list!!
                                        doAsync {
                                            println("xTag_ start")
                                            AppDatabase.getDBInstance()?.shopFeedbackTempDao()?.deleteAll()
                                            for(i in 0..objList!!.size-1){
                                                var feedList=objList.get(i).feedback_remark_list!!
                                                if(shopDtls!!.shop_id.equals(objList.get(i).shop_id)){
                                                    for(j in 0..feedList.size-1){
                                                        var ob: ShopFeedbackTempEntity = ShopFeedbackTempEntity()
                                                        ob.apply {
                                                            shop_id=objList.get(i).shop_id
                                                            feedback=feedList.get(j).feedback
                                                            date_time=feedList.get(j).date_time
                                                            if(feedback.equals(""))
                                                                feedback="N/A"
                                                        }
                                                        AppDatabase.getDBInstance()?.shopFeedbackTempDao()?.insert(ob)
                                                    }
                                                }
                                            }
                                            uiThread {
                                                println("xTag_ finish")
                                                initAdapterForOnline()
                                            }
                                        }
                                    }
                                } else {
                                    noData.visibility = View.VISIBLE
                                    rvFeedbackList.visibility = View.GONE
                                }

                            }, { error ->
                                error.localizedMessage
                            })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }

}
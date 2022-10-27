package com.kcteam.features.newcollectionreport

import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.nearbyshops.presentation.NearByShopsListClickListener
import com.kcteam.features.photoReg.ProtoRegistrationFragment
import com.kcteam.features.shopdetail.presentation.AddCollectionDialog
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.collections.ArrayList

class RepeatOrderFrag : BaseFragment(),View.OnClickListener{

    private lateinit var mContext: Context

    private lateinit var rv_CusList:RecyclerView
    private lateinit var customerRepeatOrderItemListAdapter:CustomerRepeatOrderItemListAdapter
    private lateinit var shopList:ArrayList<AddShopDBModelEntity>
    private lateinit var seekBar:SeekBar
    private lateinit var tvDays:AppCustomTextView
    private lateinit var ivView:ImageView
    private lateinit var tv_noData: TextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var etSearch: AppCustomEditText

    var dateList:ArrayList<String> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_repeat_order, container, false)
        initView(view)

        return view
    }

    private fun initView(view: View){
        rv_CusList=view.findViewById(R.id.rv_frag_customer_repeat_order_list)
        seekBar=view.findViewById(R.id.sb_frag_repeat_order_days)
        tvDays=view.findViewById(R.id.tv_frag_repeat_order_datedrop)
        ivView=view.findViewById(R.id.iv_frag_repeat_ord_view)
        progress_wheel=view.findViewById(R.id.progress_wheel)
        tv_noData=view.findViewById(R.id.tv_frag_repeat_ord_noData)
        etSearch=view.findViewById(R.id.et_frag_repeat_order_search)
        progress_wheel.stopSpinning()
        ivView.setOnClickListener(this)

        if(Pref.ZeroOrderInterval.toInt() !=0){
            seekBar.setProgress(Pref.ZeroOrderInterval.toInt())
            tvDays.text = Pref.ZeroOrderInterval.toString()+" Days back"
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tvDays.text = progress.toString()+" Days back"
                Pref.ZeroOrderInterval=progress.toString()
                seekBar.getProgressDrawable().setColorFilter(mContext.getColor(R.color.maroon), PorterDuff.Mode.MULTIPLY)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        //  getData()
    }



    override fun onResume() {
        super.onResume()
        initTextChangeListener()
    }

    private fun initTextChangeListener() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!TextUtils.isEmpty(etSearch.text.toString().trim()))
                    customerRepeatOrderItemListAdapter!!.getFilter().filter(etSearch.text.toString().trim())
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_frag_repeat_ord_view -> {
                if(Pref.ZeroOrderInterval.toInt()!=0){
                    dateList= ArrayList()
                    shopList= ArrayList()
                    progress_wheel.spin()
                    var currentDStr = AppUtils.getCurrentDateyymmdd()
                    var currentD= LocalDate.parse(currentDStr)
                    var stDate=currentD.minusDays(Pref.ZeroOrderInterval.toInt().toLong())
                    var endDate=currentD.minusDays(1)
                    val diff = Period.between(stDate,endDate)

                    dateList.add(stDate.toString())
                    var countDate=stDate
                    for(i in 0..diff.days-1){
                        countDate=countDate.plusDays(1)
                        dateList.add(countDate.toString())
                    }

                    var todayDateFormat = AppUtils.convertDateTimeToCommonFormat(AppUtils.getCurrentDateTime())
                    var shopListfromOrder = AppDatabase.getDBInstance()!!.orderDetailsListDao().getDistinctShopIDExceptCurrDate(todayDateFormat) as ArrayList<String>

                    if(shopListfromOrder!=null){
                        for(i in 0..shopListfromOrder.size-1){
                            var isshopOrderTaken=false
                            for(j in 0..dateList.size-1){
                                var convDate=AppUtils.convertDateTimeToCommonFormat(dateList.get(j)+"T00:00:00")
                                var shopCount = AppDatabase.getDBInstance()!!.orderDetailsListDao().getAllByOnlyDate(convDate,shopListfromOrder.get(i))
                                if(shopCount.size>0){
                                    isshopOrderTaken=true
                                    break
                                }
                            }
                            if(isshopOrderTaken==false){
                                shopList.add(AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopListfromOrder.get(i)))
                            }
                        }
                    }
                    progress_wheel.stopSpinning()
                    if(shopList.size>0){
                        tv_noData.visibility=View.GONE
                        rv_CusList.visibility=View.VISIBLE
                        etSearch.visibility=View.VISIBLE
                        initAdapter()
                    }else{
                        tv_noData.visibility=View.VISIBLE
                        rv_CusList.visibility=View.GONE
                        etSearch.visibility=View.GONE
                    }


                }


                //var t="2022-06-01 09:45:29".replace(" ","T")
                //var tt = t.split("T").get(0);



            }
        }
    }

    private fun initAdapter(){
        customerRepeatOrderItemListAdapter=CustomerRepeatOrderItemListAdapter(mContext,shopList,object : RepeatOrderShopsListClickListener {
            override fun OnNearByShopsListClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun mapClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun orderClick(obj: AddShopDBModelEntity) {
                (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, obj)
            }

            override fun callClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun syncClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun updateLocClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onStockClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onUpdateStageClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onQuotationClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onActivityClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onShareClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onCollectionClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onWhatsAppClick(no: String) {
                TODO("Not yet implemented")
            }

            override fun onSmsClick(no: String) {
                TODO("Not yet implemented")
            }

            override fun onCreateQrClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onUpdatePartyStatusClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onUpdateBankDetailsClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onQuestionnarieClick(shopId: String) {
                TODO("Not yet implemented")
            }

            override fun onReturnClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onHistoryClick(shop: Any) {
                TODO("Not yet implemented")
            }
        },{
            it
        })
        rv_CusList.adapter=customerRepeatOrderItemListAdapter
    }

    fun updateView(){

    }



}
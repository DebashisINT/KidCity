package com.kcteam.features.newcollectionreport

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.domain.TeamAllShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.assignToPPList.AssignToPPListApi
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.model.TeamListDataModel
import com.kcteam.features.nearbyshops.presentation.NearByShopsListClickListener
import com.kcteam.features.photoReg.PhotoAttendanceFragment
import com.kcteam.features.photoReg.ProtoRegistrationFragment
import com.kcteam.features.shopdetail.presentation.AddCollectionDialog
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.collections.ArrayList

class TeamRepeatOrderFrag : BaseFragment(),View.OnClickListener{

    private lateinit var mContext: Context

    private lateinit var rv_CusList:RecyclerView
    private lateinit var customerRepeatOrderItemListAdapter:TeamCustomerRepeatOrderItemListAdapter
    private lateinit var shopList:ArrayList<TeamAllShopDBModelEntity>
    private lateinit var seekBar:SeekBar
    private lateinit var tvDays:AppCustomTextView
    private lateinit var ivView:ImageView
    private lateinit var tv_noData: TextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var etSearch: AppCustomEditText
    private lateinit var tvName: TextView

    var dateList:ArrayList<String> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object{
        var user_Obj: TeamListDataModel = TeamListDataModel()
        var userName : String = ""
        fun getInstance(objects: Any): TeamRepeatOrderFrag {
            val teamRepeatOrderFrag = TeamRepeatOrderFrag()
            if (!TextUtils.isEmpty(objects.toString())) {
                if(objects is TeamListDataModel){
                    user_Obj=objects
                    userName=objects.user_name
                }
            }
            return teamRepeatOrderFrag
        }
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
        tvName=view.findViewById(R.id.tv_frag_repeat_order_userName)

        progress_wheel.stopSpinning()
        ivView.setOnClickListener(this)

        tvName.visibility=View.VISIBLE
        tvName.setText(userName)

        if(Pref.ZeroOrderInterval.toInt() !=0){
            seekBar.setProgress(Pref.ZeroOrderInterval.toInt())
            tvDays.text = Pref.ZeroOrderInterval.toString()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tvDays.text = progress.toString()
                Pref.ZeroOrderInterval=progress.toString()

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

    @RequiresApi(Build.VERSION_CODES.O)
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

                    var shopListfromOrder = AppDatabase.getDBInstance()!!.orderDtlsTeamDao().getDistinctShopIDTeam() as ArrayList<String>

                    if(shopListfromOrder!=null){
                        for(i in 0..shopListfromOrder.size-1){
                            var isshopOrderTaken=false
                            for(j in 0..dateList.size-1){
                                var convDate=AppUtils.convertDateTimeToCommonFormat(dateList.get(j)+"T00:00:00")
                                var shopCount = AppDatabase.getDBInstance()!!.orderDtlsTeamDao().getAllByOnlyDateTeam(convDate,shopListfromOrder.get(i))
                                if(shopCount.size>0){
                                    isshopOrderTaken=true
                                    break
                                }
                            }
                            if(isshopOrderTaken==false){
                                shopList.add(AppDatabase.getDBInstance()!!.teamAllShopDBModelDao().getShopByIdN(shopListfromOrder.get(i)))
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
        customerRepeatOrderItemListAdapter=TeamCustomerRepeatOrderItemListAdapter(mContext,shopList,object : TeamRepeatOrderShopsListClickListener {
            override fun OnNearByShopsListClick(position: Int) {

            }

            override fun mapClick(position: Int) {

            }

            override fun orderClick(obj: TeamAllShopDBModelEntity) {
                (mContext as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, obj)
            }

            override fun callClick(position: Int) {

            }

            override fun syncClick(position: Int) {

            }

            override fun updateLocClick(position: Int) {

            }

            override fun onStockClick(position: Int) {

            }

            override fun onUpdateStageClick(position: Int) {

            }

            override fun onQuotationClick(position: Int) {

            }

            override fun onActivityClick(position: Int) {

            }

            override fun onShareClick(position: Int) {

            }

            override fun onCollectionClick(position: Int) {

            }

            override fun onWhatsAppClick(no: String) {

            }

            override fun onSmsClick(no: String) {

            }

            override fun onCreateQrClick(position: Int) {

            }

            override fun onUpdatePartyStatusClick(position: Int) {

            }

            override fun onUpdateBankDetailsClick(position: Int) {

            }

            override fun onQuestionnarieClick(shopId: String) {

            }

            override fun onReturnClick(position: Int) {

            }

            override fun onHistoryClick(shop: Any) {

            }
        },{
            it
        })
        rv_CusList.adapter=customerRepeatOrderItemListAdapter
    }

    fun updateView(){

    }



}
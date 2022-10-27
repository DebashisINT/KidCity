package com.kcteam.features.lead

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.lead.adapter.CustomerLeadAdapter
import com.kcteam.features.lead.api.GetLeadRegProvider
import com.kcteam.features.lead.dialog.EnqListDialog
import com.kcteam.features.lead.model.CustomerLeadList
import com.kcteam.features.lead.model.CustomerLeadResponse
import com.kcteam.features.lead.model.CustomerListReq
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class LeadInProcessFrag : BaseFragment(),  DatePickerDialog.OnDateSetListener,View.OnClickListener{

    private lateinit var mContext: Context
    private  var adapter: CustomerLeadAdapter? = null
    private var tempList:ArrayList<CustomerLeadList> = ArrayList()

    private lateinit var rv_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var date_range: AppCompatRadioButton
    private lateinit var radioList: ArrayList<RadioButton>
    private var isChkChanged: Boolean = false
    private val mAutoHighlight: Boolean = false
    private lateinit var date_rangeDisplay: AppCustomTextView
    private lateinit var enquiryTypeSelectSpinner: AppCustomTextView
    private lateinit var showButton: ImageView
    private var fromDate:String = ""
    private var toDate:String = ""
    private lateinit var leadSearch: AppCustomEditText

    var reqData = CustomerListReq()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_lead_pending, container, false)
        initView(view)

        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    tempList?.let {
                        adapter?.refreshList(it)
                    }
                } else {
                    adapter?.filter?.filter(query)
                }
            }
        })

        return view
    }

    private fun initView(view: View){
        progress_wheel=view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        tv_no_data=view.findViewById(R.id.tv_no_data)
        rv_list=view.findViewById(R.id.rv_frag_lead_pending_dtls)
        radioList = ArrayList()
        date_range=view.findViewById(R.id.frag_lead_pending_date_range)
        date_rangeDisplay = view.findViewById(R.id.frag_lead_pending_date_range_display)
        enquiryTypeSelectSpinner = view.findViewById(R.id.frag_lead_pending_spinnerType)
        radioList.add(date_range)
        showButton =  view.findViewById(R.id.frag_lead_pending_show)
        leadSearch = view.findViewById(R.id.et_frag_lead_pending_search)

        date_range.setOnClickListener(this)
        enquiryTypeSelectSpinner.setOnClickListener(this)
        showButton.setOnClickListener(this)
    }

    override fun onResume() {

        initTextChangeListener()

        super.onResume()
        if(CustomStatic.IsViewLeadAddUpdate){
            isChkChanged = false
            date_range.isChecked = false
        }
        if(LeadFrag.reqData_inProcess_LeadFrag.user_id!=null){
            isChkChanged = true
            date_range.isChecked = true
            enquiryTypeSelectSpinner.text=LeadFrag.reqData_inProcess_LeadFrag.enquiry_from
            var strt_date=LeadFrag.reqData_inProcess_LeadFrag.from_date
            var end_date=LeadFrag.reqData_inProcess_LeadFrag.to_date
            onDateSetCustom(strt_date!!.substring(0,4).toInt(),strt_date!!.substring(5,7).replace("0","").toInt()-1, strt_date!!.substring(8,10).replace("0","").toInt(),
                    end_date!!.substring(0,4).toInt(),end_date!!.substring(5,7).replace("0","").toInt()-1, end_date!!.substring(8,10).replace("0","").toInt())
        }
    }

    private fun initTextChangeListener() {
        leadSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter!!.getFilter().filter(leadSearch.text.toString().trim())
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.frag_lead_pending_date_range -> {
                if (!isChkChanged) {
                    date_range.isChecked = true
                    val now = Calendar.getInstance(Locale.ENGLISH)
                    val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                            this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.isAutoHighlight = mAutoHighlight
                    dpd.maxDate = Calendar.getInstance(Locale.ENGLISH)
                    dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
                } else {
                    isChkChanged = false
                }
            }
            R.id.frag_lead_pending_spinnerType->{
                var List:ArrayList<String> = ArrayList()
                List.add("IndiaMart")
                List.add("IndiaMart (ARCHER)")
                List.add("MccoyMart")
                List.add("Website")
                List.add("Direct Call")
                List.add("Exhibition")
                List.add("Twak")
                EnqListDialog.newInstance(List,"Select Enquiry"){
                    enquiryTypeSelectSpinner.text=it
                }.show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            R.id.frag_lead_pending_show->{
                if( enquiryTypeSelectSpinner.text.toString().equals("Select Enquiry")){
                    (mContext as DashboardActivity).showSnackMessage("Please select enquiry")
                    return
                }
                getLeadFetch()
            }
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        val date = "From " + dayOfMonth + AppUtils.getDayNumberSuffix(dayOfMonth) + " " + FTStorageUtils.formatMonth((++monthOfYear).toString()) + " " + year + " To " + dayOfMonthEnd + AppUtils.getDayNumberSuffix(dayOfMonthEnd) + " " + FTStorageUtils.formatMonth((++monthOfYearEnd).toString()) + " " + yearEnd
        date_rangeDisplay.text = date
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd
        if (dayOfMonth < 10)
            day = "0$dayOfMonth"
        if (dayOfMonthEnd < 10)
            dayEnd = "0$dayOfMonthEnd"
        var fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear /*+ 1*/).toString() + "") + "-" + year
        var endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd /*+ 1*/).toString() + "") + "-" + yearEnd
        fromDate = AppUtils.changeLocalDateFormatToAtt(fronString).replace("/","-")
        toDate = AppUtils.changeLocalDateFormatToAtt(endString).replace("/","-")
    }

     fun onDateSetCustom(year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        val date = "From " + dayOfMonth + AppUtils.getDayNumberSuffix(dayOfMonth) + " " + FTStorageUtils.formatMonth((++monthOfYear).toString()) + " " + year + " To " + dayOfMonthEnd + AppUtils.getDayNumberSuffix(dayOfMonthEnd) + " " + FTStorageUtils.formatMonth((++monthOfYearEnd).toString()) + " " + yearEnd
        date_rangeDisplay.text = date
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd
        if (dayOfMonth < 10)
            day = "0$dayOfMonth"
        if (dayOfMonthEnd < 10)
            dayEnd = "0$dayOfMonthEnd"
        var fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear /*+ 1*/).toString() + "") + "-" + year
        var endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd /*+ 1*/).toString() + "") + "-" + yearEnd
        fromDate = AppUtils.changeLocalDateFormatToAtt(fronString).replace("/","-")
        toDate = AppUtils.changeLocalDateFormatToAtt(endString).replace("/","-")

         getLeadFetch()
    }

    private fun getLeadFetch() {
        reqData.from_date =  fromDate
        reqData.to_date = toDate
        reqData.enquiry_from = enquiryTypeSelectSpinner.text.toString()
        reqData.user_id = Pref.user_id

        try {
            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }
            BaseActivity.isApiInitiated = true
            progress_wheel.spin()
            val repository = GetLeadRegProvider.provideList()
            BaseActivity.compositeDisposable.add(
                    repository.CustomerList(reqData)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as CustomerLeadResponse
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    if(addShopResult.customer_dtls_list.size>0){
                                        rv_list.visibility=View.VISIBLE
                                        leadSearch.visibility=View.VISIBLE
                                        LeadFrag.reqData_inProcess_LeadFrag=reqData

                                        setAdapter(addShopResult.customer_dtls_list)
                                    }
                                } else if(addShopResult.status == NetworkConstant.NO_DATA) {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                                    tv_no_data.visibility=View.VISIBLE
                                    rv_list.visibility=View.GONE
                                    leadSearch.visibility=View.GONE
                                }
                            }, { error ->
                                progress_wheel.stopSpinning()
                                BaseActivity.isApiInitiated = false
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                if (error != null) {
                                }
                            })
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            BaseActivity.isApiInitiated = false
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
            progress_wheel.stopSpinning()
        }
    }

    private fun setAdapter(list:ArrayList<CustomerLeadList>){

        tv_no_data.visibility=View.GONE
        tempList = ArrayList()
        for(i in 0..list.size-1){
            if(!list.get(i).status.toUpperCase().equals("PENDING") && !list.get(i).status.toUpperCase().equals("NOT INTERESTED")){
                tempList.add(list.get(i))
            }
        }
        if(tempList.size==0){
            rv_list.visibility=View.GONE
            tv_no_data.visibility=View.VISIBLE
            leadSearch.visibility=View.GONE
            return
        }

        adapter = CustomerLeadAdapter(mContext,tempList,object : CustomerLeadAdapter.OnPendingLeadClickListener{
            override fun onActivityClick(obj: CustomerLeadList) {
                doActivity(obj)
            }
            override fun onPhoneClick(obj: CustomerLeadList) {
                if(obj.mobile_no.length>1)
                {
                    var phoneNo=obj.mobile_no
                    IntentActionable.initiatePhoneCall(mContext, phoneNo)
                }
            }
        }, {
            it
        })
        rv_list.adapter=adapter

    }

    private fun doActivity(obj:CustomerLeadList){
        CustomStatic.IsViewLeadFromInProcess=true
        (mContext as DashboardActivity).loadFragment(FragType.ViewLeadFrag, true, obj)
    }

}
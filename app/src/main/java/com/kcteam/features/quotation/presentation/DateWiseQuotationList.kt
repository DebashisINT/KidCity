package com.kcteam.features.quotation.presentation

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.elvishew.xlog.XLog
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.api.assignToPPList.AssignToPPListRepoProvider
import com.kcteam.features.addshop.api.assignedToDDList.AssignToDDListRepoProvider
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.addshop.model.AssignedToShopListResponseModel
import com.kcteam.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import com.kcteam.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.quotation.api.QuotationRepoProvider
import com.kcteam.features.quotation.model.AddQuotInputModel
import com.kcteam.features.quotation.model.QuotationListResponseModel
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 16-Jun-20.
 */
class DateWiseQuotationList : BaseFragment(), DatePickerListener {

    private lateinit var mContext: Context

    private lateinit var rv_quot_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var fab: FloatingActionButton
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var rl_quot_main: RelativeLayout
    private lateinit var tv_quot_count: AppCustomTextView
    private lateinit var date_CV: CardView
    private lateinit var picker: HorizontalPicker
    private lateinit var selectedDate: String
    private lateinit var sync_all_tv: AppCustomTextView
    private var i: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        selectedDate = AppUtils.getCurrentDateForShopActi()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_quot, container, false)

        initView(view)

        return view
    }

    private fun initView(view: View) {
        view.apply {
            rv_quot_list = findViewById(R.id.rv_quot_list)
            progress_wheel = findViewById(R.id.progress_wheel)
            fab = findViewById(R.id.fab)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
            rl_quot_main = findViewById(R.id.rl_quot_main)
            tv_quot_count = findViewById(R.id.tv_quot_count)
            date_CV = findViewById(R.id.date_CV)
            picker = findViewById(R.id.datePicker)
            sync_all_tv = findViewById(R.id.sync_all_tv)
        }

        date_CV.visibility = View.VISIBLE
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

        rv_quot_list.layoutManager = LinearLayoutManager(mContext)
        progress_wheel.stopSpinning()
        fab.visibility = View.GONE

        val list = AppDatabase.getDBInstance()?.quotDao()?.getAll()
        if (list == null || list.isEmpty())
            geQuotApi()
        else
            initAdapter()


        rl_quot_main.setOnClickListener(null)
        sync_all_tv.setOnClickListener {
            i = 0

            val unSyncQuotList = AppDatabase.getDBInstance()?.quotDao()?.getQuotDateSyncWise(selectedDate, false)

            if (unSyncQuotList != null && unSyncQuotList.isNotEmpty()) {
                val unSyncedList = ArrayList<QuotationEntity>()

                unSyncQuotList.forEach {
                    val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(it.shop_id)

                    if (shop != null && shop.isUploaded) {
                        unSyncedList.add(it)
                    }
                }

                if (unSyncedList.size > 0)
                    syncAllQuot(unSyncedList)
            } else {
                val unEditSyncQuotList = AppDatabase.getDBInstance()?.quotDao()?.getQuotDateEditSyncWise(selectedDate, 0)

                if (unEditSyncQuotList != null && unEditSyncQuotList.isNotEmpty()) {
                    val unSyncedList = ArrayList<QuotationEntity>()

                    unEditSyncQuotList.forEach {
                        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(it.shop_id)

                        if (shop != null && shop.isUploaded) {
                            unSyncedList.add(it)
                        }
                    }

                    if (unSyncedList.size > 0)
                        syncAllEditQuot(unSyncedList)
                }
            }
        }
    }

    private fun geQuotApi() {

        if (!AppUtils.isOnline(mContext)) {
            tv_no_data_available.visibility = View.VISIBLE
            return
        }

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()
        BaseActivity.compositeDisposable.add(
                repository.getQuotList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as QuotationListResponseModel
                            XLog.d("GET QUOT DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.quot_list != null && response.quot_list!!.isNotEmpty()) {

                                    doAsync {

                                        response.quot_list?.forEach {
                                            val quotEntity = QuotationEntity()
                                            AppDatabase.getDBInstance()?.quotDao()?.insert(quotEntity.apply {
                                                quo_id = it.quo_id
                                                quo_no = it.quo_no

                                                date = it.date

                                                hypothecation = if (!TextUtils.isEmpty(it.hypothecation))
                                                    it.hypothecation
                                                else
                                                    ""
                                                account_no = if (!TextUtils.isEmpty(it.account_no))
                                                    it.account_no
                                                else
                                                    ""

                                                model_id = it.model_id
                                                bs_id = it.bs_id

                                                gearbox = if (!TextUtils.isEmpty(it.gearbox))
                                                    it.gearbox
                                                else
                                                    ""

                                                number1 = if (!TextUtils.isEmpty(it.number1))
                                                    it.number1
                                                else
                                                    ""

                                                value1 = if (!TextUtils.isEmpty(it.value1))
                                                    it.value1
                                                else
                                                    ""

                                                value2 = if (!TextUtils.isEmpty(it.value2))
                                                    it.value2
                                                else
                                                    ""

                                                tyres1 = if (!TextUtils.isEmpty(it.tyres1))
                                                    it.tyres1
                                                else
                                                    ""

                                                number2 = if (!TextUtils.isEmpty(it.number2))
                                                    it.number2
                                                else
                                                    ""

                                                value3 = if (!TextUtils.isEmpty(it.value3))
                                                    it.value3
                                                else
                                                    ""

                                                value4 = if (!TextUtils.isEmpty(it.value4))
                                                    it.value4
                                                else
                                                    ""

                                                tyres2 = if (!TextUtils.isEmpty(it.tyres2))
                                                    it.tyres2
                                                else
                                                    ""

                                                amount = if (!TextUtils.isEmpty(it.amount))
                                                    it.amount
                                                else
                                                    ""

                                                discount = if (!TextUtils.isEmpty(it.discount))
                                                    it.discount
                                                else
                                                    ""

                                                cgst = if (!TextUtils.isEmpty(it.cgst))
                                                    it.cgst
                                                else
                                                    ""

                                                sgst = if (!TextUtils.isEmpty(it.sgst))
                                                    it.sgst
                                                else
                                                    ""

                                                tcs = if (!TextUtils.isEmpty(it.tcs))
                                                    it.tcs
                                                else
                                                    ""

                                                insurance = if (!TextUtils.isEmpty(it.insurance))
                                                    it.insurance
                                                else
                                                    ""

                                                net_amount = if (!TextUtils.isEmpty(it.net_amount))
                                                    it.net_amount
                                                else
                                                    ""

                                                remarks = if (!TextUtils.isEmpty(it.remarks))
                                                    it.remarks
                                                else
                                                    ""

                                                shop_id = it.shop_id
                                                isUploaded = true
                                                isEditUpdated = -1
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            initAdapter()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                tv_no_data_available.visibility = View.VISIBLE
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET QUOT DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            tv_no_data_available.visibility = View.VISIBLE
                        })
        )
    }

    private fun initAdapter() {
        val list = AppDatabase.getDBInstance()?.quotDao()?.getQuotDateWise(selectedDate)
        if (list != null && list.isNotEmpty()) {
            tv_no_data_available.visibility = View.GONE
            rv_quot_list.visibility = View.VISIBLE
            rv_quot_list.adapter = QuotationAdapter(mContext, list as ArrayList<QuotationEntity>, { quot: QuotationEntity ->

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(quot.shop_id)

                if (shop != null) {
                    if (shop.isUploaded) {

                        if (!quot.isUploaded)
                            addQuotApi(quot)
                        else if (quot.isEditUpdated == 0)
                            editQuotApi(quot)

                    } else {
                        if (!quot.isUploaded)
                            syncShop(shop, quot, false)
                        else if (quot.isEditUpdated == 0)
                            syncShop(shop, quot, true)
                    }
                }


            }, { quot: QuotationEntity ->
                (mContext as DashboardActivity).loadFragment(FragType.QuotationDetailsFragment, true, quot.quo_id!!)
            }, { phoneNo: String ->
                IntentActionable.initiatePhoneCall(mContext, phoneNo)
            }, { shop: AddShopDBModelEntity? ->
                shop?.let {
                    (mContext as DashboardActivity).openLocationMap(it, false)
                }
            }, { quot: QuotationEntity ->
                sendMailSms(quot, true)
            }, { quot: QuotationEntity ->
                sendMailSms(quot, false)
            })
        } else {
            tv_no_data_available.visibility = View.VISIBLE
            rv_quot_list.visibility = View.GONE
        }
    }

    private fun sendMailSms(quot: QuotationEntity, isSms: Boolean) {

        XLog.d("==============Send Mail Sms Input Params(Quot.List)====================")
        XLog.d("shop id=======> " + quot.shop_id)
        XLog.d("quot. id=======> " + quot.quo_id)
        XLog.d("isSms=======> " + isSms)
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("======================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        BaseActivity.compositeDisposable.add(
                repository.sendQuoSmsMail(quot.quo_id, quot.shop_id, isSms)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("Send Mail Sms DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            if (response.status == NetworkConstant.SUCCESS) {

                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("Send Mail Sms DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    override fun onDateSelected(dateSelected: DateTime) {
        val dateTime = dateSelected.toString()
        val dateFormat = dateTime.substring(0, dateTime.indexOf('T'))
        selectedDate = dateFormat

        initAdapter()
    }

    private fun syncAllQuot(unSyncedList: ArrayList<QuotationEntity>) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("==============Sync All Add Quot. Input Params(Date Wise Quot.List)====================")
        XLog.d("shop id=======> " + unSyncedList[i].shop_id)
        XLog.d("quot. date=======> " + unSyncedList[i].date)
        XLog.d("quot. id=======> " + unSyncedList[i].quo_id)
        XLog.d("quot. no=======> " + unSyncedList[i].quo_no)
        XLog.d("hypothecation=======> " + unSyncedList[i].hypothecation)
        XLog.d("account_no=======> " + unSyncedList[i].account_no)
        XLog.d("model_id=======> " + unSyncedList[i].model_id)
        XLog.d("bs_id=======> " + unSyncedList[i].bs_id)
        XLog.d("gearbox=======> " + unSyncedList[i].gearbox)
        XLog.d("number1=======> " + unSyncedList[i].number1)
        XLog.d("value1=======> " + unSyncedList[i].value1)
        XLog.d("value2=======> " + unSyncedList[i].value2)
        XLog.d("tyres1=======> " + unSyncedList[i].tyres1)
        XLog.d("number2=======> " + unSyncedList[i].number2)
        XLog.d("value3=======> " + unSyncedList[i].value3)
        XLog.d("value4=======> " + unSyncedList[i].value4)
        XLog.d("tyres2=======> " + unSyncedList[i].tyres2)
        XLog.d("amount=======> " + unSyncedList[i].amount)
        XLog.d("discount=======> " + unSyncedList[i].discount)
        XLog.d("cgst=======> " + unSyncedList[i].cgst)
        XLog.d("sgst=======> " + unSyncedList[i].sgst)
        XLog.d("tcs=======> " + unSyncedList[i].tcs)
        XLog.d("insurance=======> " + unSyncedList[i].insurance)
        XLog.d("net_amount=======> " + unSyncedList[i].net_amount)
        XLog.d("remarks=======> " + unSyncedList[i].remarks)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("========================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        val addQuot = AddQuotInputModel(Pref.session_token!!, Pref.user_id!!, unSyncedList[i].shop_id!!, unSyncedList[i].quo_id!!,
                unSyncedList[i].quo_no!!, unSyncedList[i].date!!, unSyncedList[i].hypothecation!!, unSyncedList[i].account_no!!, unSyncedList[i].model_id!!,
                unSyncedList[i].bs_id!!, unSyncedList[i].gearbox!!, unSyncedList[i].number1!!, unSyncedList[i].value1!!, unSyncedList[i].value2!!,
                unSyncedList[i].tyres1!!, unSyncedList[i].number2!!, unSyncedList[i].value3!!, unSyncedList[i].value4!!, unSyncedList[i].tyres2!!,
                unSyncedList[i].amount!!, unSyncedList[i].discount!!, unSyncedList[i].cgst!!, unSyncedList[i].sgst!!, unSyncedList[i].tcs!!,
                unSyncedList[i].insurance!!, unSyncedList[i].net_amount!!, unSyncedList[i].remarks!!)

        BaseActivity.compositeDisposable.add(
                repository.addQuot(addQuot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("ADD QUOT. DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                doAsync {

                                    AppDatabase.getDBInstance()?.quotDao()?.updateIsUploaded(true, unSyncedList[i].quo_id!!)

                                    uiThread {
                                        //progress_wheel.stopSpinning()

                                        i++
                                        if (i < unSyncedList.size) {
                                            syncAllQuot(unSyncedList)
                                        } else {
                                            progress_wheel.stopSpinning()
                                            i = 0
                                            initAdapter()
                                        }
                                    }
                                }

                            } else {
                                i++
                                if (i < unSyncedList.size) {
                                    syncAllQuot(unSyncedList)
                                } else {
                                    progress_wheel.stopSpinning()
                                    i = 0
                                    initAdapter()
                                }
                            }

                        }, { error ->
                            XLog.d("ADD QUOT. DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            i++
                            if (i < unSyncedList.size) {
                                syncAllQuot(unSyncedList)
                            } else {
                                progress_wheel.stopSpinning()
                                i = 0
                                initAdapter()
                            }
                        })
        )
    }


    private fun syncAllEditQuot(unSyncedList: ArrayList<QuotationEntity>) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("==============Sync All Edit Quot. Input Params(Date Wise Quot.List)====================")
        XLog.d("shop id=======> " + unSyncedList[i].shop_id)
        XLog.d("quot. date=======> " + unSyncedList[i].date)
        XLog.d("quot. id=======> " + unSyncedList[i].quo_id)
        XLog.d("quot. no=======> " + unSyncedList[i].quo_no)
        XLog.d("hypothecation=======> " + unSyncedList[i].hypothecation)
        XLog.d("account_no=======> " + unSyncedList[i].account_no)
        XLog.d("model_id=======> " + unSyncedList[i].model_id)
        XLog.d("bs_id=======> " + unSyncedList[i].bs_id)
        XLog.d("gearbox=======> " + unSyncedList[i].gearbox)
        XLog.d("number1=======> " + unSyncedList[i].number1)
        XLog.d("value1=======> " + unSyncedList[i].value1)
        XLog.d("value2=======> " + unSyncedList[i].value2)
        XLog.d("tyres1=======> " + unSyncedList[i].tyres1)
        XLog.d("number2=======> " + unSyncedList[i].number2)
        XLog.d("value3=======> " + unSyncedList[i].value3)
        XLog.d("value4=======> " + unSyncedList[i].value4)
        XLog.d("tyres2=======> " + unSyncedList[i].tyres2)
        XLog.d("amount=======> " + unSyncedList[i].amount)
        XLog.d("discount=======> " + unSyncedList[i].discount)
        XLog.d("cgst=======> " + unSyncedList[i].cgst)
        XLog.d("sgst=======> " + unSyncedList[i].sgst)
        XLog.d("tcs=======> " + unSyncedList[i].tcs)
        XLog.d("insurance=======> " + unSyncedList[i].insurance)
        XLog.d("net_amount=======> " + unSyncedList[i].net_amount)
        XLog.d("remarks=======> " + unSyncedList[i].remarks)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("========================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        val addQuot = AddQuotInputModel(Pref.session_token!!, Pref.user_id!!, unSyncedList[i].shop_id!!, unSyncedList[i].quo_id!!,
                unSyncedList[i].quo_no!!, unSyncedList[i].date!!, unSyncedList[i].hypothecation!!, unSyncedList[i].account_no!!, unSyncedList[i].model_id!!,
                unSyncedList[i].bs_id!!, unSyncedList[i].gearbox!!, unSyncedList[i].number1!!, unSyncedList[i].value1!!, unSyncedList[i].value2!!,
                unSyncedList[i].tyres1!!, unSyncedList[i].number2!!, unSyncedList[i].value3!!, unSyncedList[i].value4!!, unSyncedList[i].tyres2!!,
                unSyncedList[i].amount!!, unSyncedList[i].discount!!, unSyncedList[i].cgst!!, unSyncedList[i].sgst!!, unSyncedList[i].tcs!!,
                unSyncedList[i].insurance!!, unSyncedList[i].net_amount!!, unSyncedList[i].remarks!!)

        BaseActivity.compositeDisposable.add(
                repository.addQuot(addQuot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("EDIT QUOT. DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                doAsync {

                                    AppDatabase.getDBInstance()?.quotDao()?.updateIsEdit(1, unSyncedList[i].quo_id!!)

                                    uiThread {
                                        //progress_wheel.stopSpinning()

                                        i++
                                        if (i < unSyncedList.size) {
                                            syncAllEditQuot(unSyncedList)
                                        } else {
                                            progress_wheel.stopSpinning()
                                            i = 0
                                            initAdapter()
                                        }
                                    }
                                }

                            } else {
                                i++
                                if (i < unSyncedList.size) {
                                    syncAllEditQuot(unSyncedList)
                                } else {
                                    progress_wheel.stopSpinning()
                                    i = 0
                                    initAdapter()
                                }
                            }

                        }, { error ->
                            XLog.d("EDIT QUOT. DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            i++
                            if (i < unSyncedList.size) {
                                syncAllEditQuot(unSyncedList)
                            } else {
                                progress_wheel.stopSpinning()
                                i = 0
                                initAdapter()
                            }
                        })
        )
    }

    private fun addQuotApi(quotEntity: QuotationEntity) {

        XLog.d("==============Sync Add Quot. Input Params(Date Wise Quot.List)====================")
        XLog.d("shop id=======> " + quotEntity.shop_id)
        XLog.d("quot. date=======> " + quotEntity.date)
        XLog.d("quot. id=======> " + quotEntity.quo_id)
        XLog.d("quot. no=======> " + quotEntity.quo_no)
        XLog.d("hypothecation=======> " + quotEntity.hypothecation)
        XLog.d("account_no=======> " + quotEntity.account_no)
        XLog.d("model_id=======> " + quotEntity.model_id)
        XLog.d("bs_id=======> " + quotEntity.bs_id)
        XLog.d("gearbox=======> " + quotEntity.gearbox)
        XLog.d("number1=======> " + quotEntity.number1)
        XLog.d("value1=======> " + quotEntity.value1)
        XLog.d("value2=======> " + quotEntity.value2)
        XLog.d("tyres1=======> " + quotEntity.tyres1)
        XLog.d("number2=======> " + quotEntity.number2)
        XLog.d("value3=======> " + quotEntity.value3)
        XLog.d("value4=======> " + quotEntity.value4)
        XLog.d("tyres2=======> " + quotEntity.tyres2)
        XLog.d("amount=======> " + quotEntity.amount)
        XLog.d("discount=======> " + quotEntity.discount)
        XLog.d("cgst=======> " + quotEntity.cgst)
        XLog.d("sgst=======> " + quotEntity.sgst)
        XLog.d("tcs=======> " + quotEntity.tcs)
        XLog.d("insurance=======> " + quotEntity.insurance)
        XLog.d("net_amount=======> " + quotEntity.net_amount)
        XLog.d("remarks=======> " + quotEntity.remarks)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("========================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        val addQuot = AddQuotInputModel(Pref.session_token!!, Pref.user_id!!, quotEntity.shop_id!!, quotEntity.quo_id!!,
                quotEntity.quo_no!!, quotEntity.date!!, quotEntity.hypothecation!!, quotEntity.account_no!!, quotEntity.model_id!!,
                quotEntity.bs_id!!, quotEntity.gearbox!!, quotEntity.number1!!, quotEntity.value1!!, quotEntity.value2!!,
                quotEntity.tyres1!!, quotEntity.number2!!, quotEntity.value3!!, quotEntity.value4!!, quotEntity.tyres2!!, quotEntity.amount!!,
                quotEntity.discount!!, quotEntity.cgst!!, quotEntity.sgst!!, quotEntity.tcs!!, quotEntity.insurance!!, quotEntity.net_amount!!,
                quotEntity.remarks!!)

        BaseActivity.compositeDisposable.add(
                repository.addQuot(addQuot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("ADD QUOT. DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                doAsync {

                                    AppDatabase.getDBInstance()?.quotDao()?.updateIsUploaded(true, quotEntity.quo_id!!)

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Quotation synced successfully")
                                        initAdapter()
                                    }
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_quot))
                                initAdapter()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("ADD QUOT. DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_quot))
                            initAdapter()
                        })
        )
    }

    private fun editQuotApi(quotEntity: QuotationEntity) {
        XLog.d("==============Sync Edit Quot. Input Params(Date Wise Quot.List)====================")
        XLog.d("shop id=======> " + quotEntity.shop_id)
        XLog.d("quot. date=======> " + quotEntity.date)
        XLog.d("quot. id=======> " + quotEntity.quo_id)
        XLog.d("quot. no=======> " + quotEntity.quo_no)
        XLog.d("hypothecation=======> " + quotEntity.hypothecation)
        XLog.d("account_no=======> " + quotEntity.account_no)
        XLog.d("model_id=======> " + quotEntity.model_id)
        XLog.d("bs_id=======> " + quotEntity.bs_id)
        XLog.d("gearbox=======> " + quotEntity.gearbox)
        XLog.d("number1=======> " + quotEntity.number1)
        XLog.d("value1=======> " + quotEntity.value1)
        XLog.d("value2=======> " + quotEntity.value2)
        XLog.d("tyres1=======> " + quotEntity.tyres1)
        XLog.d("number2=======> " + quotEntity.number2)
        XLog.d("value3=======> " + quotEntity.value3)
        XLog.d("value4=======> " + quotEntity.value4)
        XLog.d("tyres2=======> " + quotEntity.tyres2)
        XLog.d("amount=======> " + quotEntity.amount)
        XLog.d("discount=======> " + quotEntity.discount)
        XLog.d("cgst=======> " + quotEntity.cgst)
        XLog.d("sgst=======> " + quotEntity.sgst)
        XLog.d("tcs=======> " + quotEntity.tcs)
        XLog.d("insurance=======> " + quotEntity.insurance)
        XLog.d("net_amount=======> " + quotEntity.net_amount)
        XLog.d("remarks=======> " + quotEntity.remarks)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("========================================================================")

        progress_wheel.spin()
        val repository = QuotationRepoProvider.provideBSListRepository()

        val addQuot = AddQuotInputModel(Pref.session_token!!, Pref.user_id!!, quotEntity.shop_id!!, quotEntity.quo_id!!,
                quotEntity.quo_no!!, quotEntity.date!!, quotEntity.hypothecation!!, quotEntity.account_no!!, quotEntity.model_id!!,
                quotEntity.bs_id!!, quotEntity.gearbox!!, quotEntity.number1!!, quotEntity.value1!!, quotEntity.value2!!,
                quotEntity.tyres1!!, quotEntity.number2!!, quotEntity.value3!!, quotEntity.value4!!, quotEntity.tyres2!!, quotEntity.amount!!,
                quotEntity.discount!!, quotEntity.cgst!!, quotEntity.sgst!!, quotEntity.tcs!!, quotEntity.insurance!!, quotEntity.net_amount!!,
                quotEntity.remarks!!)

        BaseActivity.compositeDisposable.add(
                repository.addQuot(addQuot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("EDIT QUOT. DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                doAsync {

                                    AppDatabase.getDBInstance()?.quotDao()?.updateIsEdit(1, quotEntity.quo_id!!)

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Quotation synced successfully")
                                        initAdapter()
                                    }
                                }

                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_quot))
                                initAdapter()
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("EDIT QUOT. DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_quot))
                            initAdapter()
                        })
        )
    }

    private fun syncShop(shop: AddShopDBModelEntity?, quot: QuotationEntity, isEdit: Boolean) {
        val addShopData = AddShopRequestData()

        shop?.apply {
            addShopData.session_token = Pref.session_token
            addShopData.address = address
            addShopData.owner_contact_no = ownerContactNumber
            addShopData.owner_email = ownerEmailId
            addShopData.owner_name = ownerName
            addShopData.pin_code = pinCode
            addShopData.shop_lat = shopLat.toString()
            addShopData.shop_long = shopLong.toString()
            addShopData.shop_name = shopName.toString()
            addShopData.type = type.toString()
            addShopData.shop_id = shop_id
            addShopData.user_id = Pref.user_id

            if (!TextUtils.isEmpty(dateOfBirth))
                addShopData.dob = AppUtils.changeAttendanceDateFormatToCurrent(dateOfBirth)

            if (!TextUtils.isEmpty(dateOfAniversary))
                addShopData.date_aniversary = AppUtils.changeAttendanceDateFormatToCurrent(dateOfAniversary)

            addShopData.assigned_to_dd_id = assigned_to_dd_id
            addShopData.assigned_to_pp_id = assigned_to_pp_id
            addShopData.added_date = added_date
            addShopData.amount = amount
            addShopData.area_id = area_id
            addShopData.model_id = model_id
            addShopData.primary_app_id = primary_app_id
            addShopData.secondary_app_id = secondary_app_id
            addShopData.lead_id = lead_id
            addShopData.stage_id = stage_id
            addShopData.funnel_stage_id = funnel_stage_id
            addShopData.booking_amount = booking_amount
            addShopData.type_id = type_id

            addShopData.director_name = director_name
            addShopData.key_person_name = person_name
            addShopData.phone_no = person_no

            if (!TextUtils.isEmpty(family_member_dob))
                addShopData.family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(family_member_dob)

            if (!TextUtils.isEmpty(add_dob))
                addShopData.addtional_dob = AppUtils.changeAttendanceDateFormatToCurrent(add_dob)

            if (!TextUtils.isEmpty(add_doa))
                addShopData.addtional_doa = AppUtils.changeAttendanceDateFormatToCurrent(add_doa)

            addShopData.specialization = specialization
            addShopData.category = category
            addShopData.doc_address = doc_address
            addShopData.doc_pincode = doc_pincode
            addShopData.is_chamber_same_headquarter = chamber_status.toString()
            addShopData.is_chamber_same_headquarter_remarks = remarks
            addShopData.chemist_name = chemist_name
            addShopData.chemist_address = chemist_address
            addShopData.chemist_pincode = chemist_pincode
            addShopData.assistant_contact_no = assistant_no
            addShopData.average_patient_per_day = patient_count
            addShopData.assistant_name = assistant_name

            if (!TextUtils.isEmpty(doc_family_dob))
                addShopData.doc_family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(doc_family_dob)

            if (!TextUtils.isEmpty(assistant_dob))
                addShopData.assistant_dob = AppUtils.changeAttendanceDateFormatToCurrent(assistant_dob)

            if (!TextUtils.isEmpty(assistant_doa))
                addShopData.assistant_doa = AppUtils.changeAttendanceDateFormatToCurrent(assistant_doa)

            if (!TextUtils.isEmpty(assistant_family_dob))
                addShopData.assistant_family_dob = AppUtils.changeAttendanceDateFormatToCurrent(assistant_family_dob)

            addShopData.entity_id = entity_id
            addShopData.party_status_id = party_status_id
            addShopData.retailer_id = retailer_id
            addShopData.dealer_id = dealer_id
            addShopData.beat_id = beat_id
            addShopData.assigned_to_shop_id = assigned_to_shop_id
            addShopData.actual_address = actual_address



            addShopData.project_name = project_name
            addShopData.landline_number = landline_number
            addShopData.agency_name = agency_name

            addShopData.alternateNoForCustomer = alternateNoForCustomer
            addShopData.whatsappNoForCustomer = whatsappNoForCustomer

            // duplicate shop api call
            addShopData.isShopDuplicate=isShopDuplicate
            addShopData.purpose=purpose
        }
        callAddShopApi(addShopData, shop?.shopImageLocalPath!!, quot, isEdit, shop.doc_degree!!)
    }

    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, quot: QuotationEntity, isEdit: Boolean,
                               doc_degree: String?) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()


        XLog.d("==============SyncShop Input Params (Date Wise Quot.List)====================")
        XLog.d("shop id=======> " + addShop.shop_id)
        val index = addShop.shop_id!!.indexOf("_")
        XLog.d("decoded shop id=======> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
        XLog.d("shop added date=======> " + addShop.added_date)
        XLog.d("shop address=======> " + addShop.address)
        XLog.d("assigned to dd id=======> " + addShop.assigned_to_dd_id)
        XLog.d("assigned to pp id=======> " + addShop.assigned_to_pp_id)
        XLog.d("date aniversery=======> " + addShop.date_aniversary)
        XLog.d("dob=======> " + addShop.dob)
        XLog.d("shop owner phn no=======> " + addShop.owner_contact_no)
        XLog.d("shop owner email=======> " + addShop.owner_email)
        XLog.d("shop owner name=======> " + addShop.owner_name)
        XLog.d("shop pincode=======> " + addShop.pin_code)
        XLog.d("session token=======> " + addShop.session_token)
        XLog.d("shop lat=======> " + addShop.shop_lat)
        XLog.d("shop long=======> " + addShop.shop_long)
        XLog.d("shop name=======> " + addShop.shop_name)
        XLog.d("shop type=======> " + addShop.type)
        XLog.d("user id=======> " + addShop.user_id)
        XLog.d("amount=======> " + addShop.amount)
        XLog.d("area id=======> " + addShop.area_id)
        XLog.d("model id=======> " + addShop.model_id)
        XLog.d("primary app id=======> " + addShop.primary_app_id)
        XLog.d("secondary app id=======> " + addShop.secondary_app_id)
        XLog.d("lead id=======> " + addShop.lead_id)
        XLog.d("stage id=======> " + addShop.stage_id)
        XLog.d("funnel stage id=======> " + addShop.funnel_stage_id)
        XLog.d("booking amount=======> " + addShop.booking_amount)
        XLog.d("type id=======> " + addShop.type_id)

        if (shop_imgPath != null)
            XLog.d("shop image path=======> $shop_imgPath")

        XLog.d("director name=======> " + addShop.director_name)
        XLog.d("family member dob=======> " + addShop.family_member_dob)
        XLog.d("key person's name=======> " + addShop.key_person_name)
        XLog.d("phone no=======> " + addShop.phone_no)
        XLog.d("additional dob=======> " + addShop.addtional_dob)
        XLog.d("additional doa=======> " + addShop.addtional_doa)
        XLog.d("family member dob=======> " + addShop.family_member_dob)
        XLog.d("key person's name=======> " + addShop.key_person_name)
        XLog.d("phone no=======> " + addShop.phone_no)
        XLog.d("additional dob=======> " + addShop.addtional_dob)
        XLog.d("additional doa=======> " + addShop.addtional_doa)
        XLog.d("doctor family member dob=======> " + addShop.doc_family_member_dob)
        XLog.d("specialization=======> " + addShop.specialization)
        XLog.d("average patient count per day=======> " + addShop.average_patient_per_day)
        XLog.d("category=======> " + addShop.category)
        XLog.d("doctor address=======> " + addShop.doc_address)
        XLog.d("doctor pincode=======> " + addShop.doc_pincode)
        XLog.d("chambers or hospital under same headquarter=======> " + addShop.is_chamber_same_headquarter)
        XLog.d("chamber related remarks=======> " + addShop.is_chamber_same_headquarter_remarks)
        XLog.d("chemist name=======> " + addShop.chemist_name)
        XLog.d("chemist name=======> " + addShop.chemist_address)
        XLog.d("chemist pincode=======> " + addShop.chemist_pincode)
        XLog.d("assistant name=======> " + addShop.assistant_name)
        XLog.d("assistant contact no=======> " + addShop.assistant_contact_no)
        XLog.d("assistant dob=======> " + addShop.assistant_dob)
        XLog.d("assistant date of anniversary=======> " + addShop.assistant_doa)
        XLog.d("assistant family dob=======> " + addShop.assistant_family_dob)
        XLog.d("entity id=======> " + addShop.entity_id)
        XLog.d("party status id=======> " + addShop.party_status_id)
        XLog.d("retailer id=======> " + addShop.retailer_id)
        XLog.d("dealer id=======> " + addShop.dealer_id)
        XLog.d("beat id=======> " + addShop.beat_id)
        XLog.d("assigned to shop id=======> " + addShop.assigned_to_shop_id)
        XLog.d("actual address=======> " + addShop.actual_address)

        if (doc_degree != null)
            XLog.d("doctor degree image path=======> $doc_degree")
        XLog.d("======================================================================")


        if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(doc_degree)) {
            val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShop(addShop)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    progress_wheel.stopSpinning()
                                    getAssignedPPListApi(addShop.shop_id, quot, isEdit)

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    getAssignedPPListApi(addShop.shop_id, quot, isEdit)
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                }


                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
        else {
            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShop, shop_imgPath, doc_degree, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                if (addShopResult.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)

                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    progress_wheel.stopSpinning()
                                    getAssignedPPListApi(addShop.shop_id, quot, isEdit)

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()
                                    //(mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {

                                            }

                                        }
                                    }
                                    getAssignedPPListApi(addShop.shop_id, quot, isEdit)
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                }


                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
    }

    private fun runLongTask(shop_id: String?): Any {
        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(shop_id!!, true, false)
        if (shopActivity != null)
            callShopActivitySubmit(shop_id)
        return true
    }

    private var shop_duration = ""
    private fun callShopActivitySubmit(shopId: String) {
        var list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())
        if (list.isEmpty())
            return

        var shopDataList: MutableList<ShopDurationRequestData> = java.util.ArrayList()
        var shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token

        if (!Pref.isMultipleVisitEnable) {
            var shopActivity = list[0]

            var shopDurationData = ShopDurationRequestData()
            shopDurationData.shop_id = shopActivity.shopid
            if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi())

                shopDurationData.spent_duration = duration
            } else {
                shopDurationData.spent_duration = shopActivity.duration_spent
            }
            shopDurationData.visited_date = shopActivity.visited_date
            shopDurationData.visited_time = shopActivity.visited_date
            if (TextUtils.isEmpty(shopActivity.distance_travelled))
                shopActivity.distance_travelled = "0.0"
            shopDurationData.distance_travelled = shopActivity.distance_travelled
            var sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
            if (sList != null && sList.isNotEmpty())
                shopDurationData.total_visit_count = sList[0].totalVisitCount

            if (!TextUtils.isEmpty(shopActivity.feedback))
                shopDurationData.feedback = shopActivity.feedback
            else
                shopDurationData.feedback = ""

            shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
            shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc
            shopDurationData.next_visit_date = shopActivity.next_visit_date

            if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
            else
                shopDurationData.early_revisit_reason = ""

            shopDurationData.device_model = shopActivity.device_model
            shopDurationData.android_version = shopActivity.android_version
            shopDurationData.battery = shopActivity.battery
            shopDurationData.net_status = shopActivity.net_status
            shopDurationData.net_type = shopActivity.net_type
            shopDurationData.in_time = shopActivity.in_time
            shopDurationData.out_time = shopActivity.out_time
            shopDurationData.start_timestamp = shopActivity.startTimeStamp
            shopDurationData.in_location = shopActivity.in_loc
            shopDurationData.out_location = shopActivity.out_loc

            shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!


            /*10-12-2021*/
            shopDurationData.updated_by = Pref.user_id
            try {
                shopDurationData.updated_on = shopActivity.updated_on!!
            }catch (ex:Exception){
                shopDurationData.updated_on = ""
            }

            if (!TextUtils.isEmpty(shopActivity.pros_id!!))
                shopDurationData.pros_id = shopActivity.pros_id!!
            else
                shopDurationData.pros_id = ""

            if (!TextUtils.isEmpty(shopActivity.agency_name!!))
                shopDurationData.agency_name =shopActivity.agency_name!!
            else
                shopDurationData.agency_name = ""

            if (!TextUtils.isEmpty(shopActivity.approximate_1st_billing_value))
                shopDurationData.approximate_1st_billing_value = shopActivity.approximate_1st_billing_value!!
            else
                shopDurationData.approximate_1st_billing_value = ""
            //duration garbage fix
            try{
                if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                {
                    shopDurationData.spent_duration="00:00:10"
                }
            }catch (ex:Exception){
                shopDurationData.spent_duration="00:00:10"
            }
            shopDataList.add(shopDurationData)
        }
        else {
            for (i in list.indices) {
                var shopActivity = list[i]

                var shopDurationData = ShopDurationRequestData()
                shopDurationData.shop_id = shopActivity.shopid
                if (shopActivity.startTimeStamp != "0" && !shopActivity.isDurationCalculated) {
                    val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivity.startTimeStamp, System.currentTimeMillis().toString())
                    val duration = AppUtils.getTimeFromTimeSpan(shopActivity.startTimeStamp, System.currentTimeMillis().toString())

                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivity.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivity.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivity.startTimeStamp)

                    shopDurationData.spent_duration = duration
                } else {
                    shopDurationData.spent_duration = shopActivity.duration_spent
                }
                shopDurationData.visited_date = shopActivity.visited_date
                shopDurationData.visited_time = shopActivity.visited_date

                if (TextUtils.isEmpty(shopActivity.distance_travelled))
                    shopActivity.distance_travelled = "0.0"

                shopDurationData.distance_travelled = shopActivity.distance_travelled

                var sList = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdList(shopDurationData.shop_id)
                if (sList != null && sList.isNotEmpty())
                    shopDurationData.total_visit_count = sList[0].totalVisitCount

                if (!TextUtils.isEmpty(shopActivity.feedback))
                    shopDurationData.feedback = shopActivity.feedback
                else
                    shopDurationData.feedback = ""

                shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
                shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc
                shopDurationData.next_visit_date = shopActivity.next_visit_date

                if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                    shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
                else
                    shopDurationData.early_revisit_reason = ""

                shopDurationData.device_model = shopActivity.device_model
                shopDurationData.android_version = shopActivity.android_version
                shopDurationData.battery = shopActivity.battery
                shopDurationData.net_status = shopActivity.net_status
                shopDurationData.net_type = shopActivity.net_type
                shopDurationData.in_time = shopActivity.in_time
                shopDurationData.out_time = shopActivity.out_time
                shopDurationData.start_timestamp = shopActivity.startTimeStamp
                shopDurationData.in_location = shopActivity.in_loc
                shopDurationData.out_location = shopActivity.out_loc

                shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!


                /*10-12-2021*/
                shopDurationData.updated_by = Pref.user_id
                try {
                    shopDurationData.updated_on = shopActivity.updated_on!!
                }catch (ex:Exception){
                    shopDurationData.updated_on = ""
                }

                if (!TextUtils.isEmpty(shopActivity.pros_id!!))
                    shopDurationData.pros_id = shopActivity.pros_id!!
                else
                    shopDurationData.pros_id = ""

                if (!TextUtils.isEmpty(shopActivity.agency_name!!))
                    shopDurationData.agency_name =shopActivity.agency_name!!
                else
                    shopDurationData.agency_name = ""

                if (!TextUtils.isEmpty(shopActivity.approximate_1st_billing_value))
                    shopDurationData.approximate_1st_billing_value = shopActivity.approximate_1st_billing_value!!
                else
                    shopDurationData.approximate_1st_billing_value = ""
                //duration garbage fix
                try{
                    if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                    {
                        shopDurationData.spent_duration="00:00:10"
                    }
                }catch (ex:Exception){
                    shopDurationData.spent_duration="00:00:10"
                }
                shopDataList.add(shopDurationData)
            }
        }

        if (shopDataList.isEmpty()) {
            return
        }

        shopDurationApiReq.shop_list = shopDataList
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

        BaseActivity.compositeDisposable.add(
                repository.shopDuration(shopDurationApiReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            XLog.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + ", RESPONSE:" + result.message)
                            if (result.status == NetworkConstant.SUCCESS) {

                            }

                        }, { error ->
                            error.printStackTrace()
                            if (error != null)
                                XLog.d("syncShopActivityFromShopList : " + ", SHOP: " + list[0].shop_name + error.localizedMessage)
//                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )

    }

    private fun getAssignedPPListApi(shop_id: String?, quot: QuotationEntity, isEdit: Boolean) {

        val shopActivityList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shop_id!!, AppUtils.getCurrentDateForShopActi())

        if (!Pref.isMultipleVisitEnable) {
            if (shopActivityList[0].isVisited && shopActivityList[0].isDurationCalculated) {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id, AppUtils.getCurrentDateForShopActi())
                XLog.d("================sync locally shop visited (date wise quot.list)===============")
            }
        }
        else {
            shopActivityList.forEach {
                if (it.isVisited && it.isDurationCalculated) {
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shop_id, AppUtils.getCurrentDateForShopActi(), it.startTimeStamp)
                    XLog.d("================sync locally shop visited (date wise quot.list)===============")
                }
            }
        }

        val repository = AssignToPPListRepoProvider.provideAssignPPListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToPPList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignToPPListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.assigned_to_pp_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        val assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                                        if (assignPPList != null)
                                            AppDatabase.getDBInstance()?.ppListDao()?.delete()

                                        for (i in list.indices) {
                                            val assignToPP = AssignToPPEntity()
                                            assignToPP.pp_id = list[i].assigned_to_pp_id
                                            assignToPP.pp_name = list[i].assigned_to_pp_authorizer_name
                                            assignToPP.pp_phn_no = list[i].phn_no
                                            AppDatabase.getDBInstance()?.ppListDao()?.insert(assignToPP)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getAssignedDDListApi(shop_id, quot, isEdit)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedDDListApi(shop_id, quot, isEdit)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getAssignedDDListApi(shop_id, quot, isEdit)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            getAssignedDDListApi(shop_id, quot, isEdit)
                        })
        )
    }

    private fun getAssignedDDListApi(shop_id: String?, quot: QuotationEntity, isEdit: Boolean) {
        val repository = AssignToDDListRepoProvider.provideAssignDDListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToDDList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignToDDListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.assigned_to_dd_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAll()
                                        if (assignDDList != null)
                                            AppDatabase.getDBInstance()?.ddListDao()?.delete()

                                        for (i in list.indices) {
                                            val assignToDD = AssignToDDEntity()
                                            assignToDD.dd_id = list[i].assigned_to_dd_id
                                            assignToDD.dd_name = list[i].assigned_to_dd_authorizer_name
                                            assignToDD.dd_phn_no = list[i].phn_no
                                            assignToDD.pp_id = list[i].assigned_to_pp_id
                                            assignToDD.type_id = list[i].type_id
                                            assignToDD.dd_latitude = list[i].dd_latitude
                                            assignToDD.dd_longitude = list[i].dd_longitude
                                            AppDatabase.getDBInstance()?.ddListDao()?.insert(assignToDD)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            getAssignedToShopApi(shop_id, quot, isEdit)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    getAssignedToShopApi(shop_id, quot, isEdit)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                getAssignedToShopApi(shop_id, quot, isEdit)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            getAssignedToShopApi(shop_id, quot, isEdit)
                        })
        )
    }

    private fun getAssignedToShopApi(shop_id: String?, quot: QuotationEntity, isEdit: Boolean) {
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.assignToShopList(Pref.profile_state)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as AssignedToShopListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.shop_list

                                AppDatabase.getDBInstance()?.assignToShopDao()?.delete()

                                doAsync {
                                    list?.forEach {
                                        val shop = AssignToShopEntity()
                                        AppDatabase.getDBInstance()?.assignToShopDao()?.insert(shop.apply {
                                            assigned_to_shop_id = it.assigned_to_shop_id
                                            name = it.name
                                            phn_no = it.phn_no
                                            type_id = it.type_id
                                        })
                                    }

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        if (isEdit)
                                            editQuotApi(quot)
                                        else
                                            addQuotApi(quot)
                                    }
                                }
                            }
                            else {
                                progress_wheel.stopSpinning()
                                if (isEdit)
                                    editQuotApi(quot)
                                else
                                    addQuotApi(quot)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            if (isEdit)
                                editQuotApi(quot)
                            else
                                addQuotApi(quot)
                        })
        )
    }
}
package com.kcteam.features.viewPPDDStock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.OutstandingListEntity
import com.kcteam.app.domain.StockListEntity
import com.kcteam.app.domain.UpdateStockEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.viewPPDDStock.api.UpdateStockRepoProvider
import com.kcteam.features.viewPPDDStock.api.stocklist.StockListRepoProvider
import com.kcteam.features.viewPPDDStock.model.UpdateStockInputParamsModel
import com.kcteam.features.viewPPDDStock.model.stocklist.StockListDataModel
import com.kcteam.features.viewPPDDStock.model.stocklist.StockListResponseModel
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 14-12-2018.
 */
class ViewOutstandingFragment: BaseFragment() {

    private lateinit var mContext: Context
    private lateinit var tv_co: AppCustomTextView
    private lateinit var rv_stock_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var ll_view_stock_main: LinearLayout
    private lateinit var tv_po: AppCustomTextView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var tv_stock_list_shop_name: AppCustomTextView
    private lateinit var fab: FloatingActionButton
    private var shopId = ""
    private lateinit var tv_opening_stock: AppCustomTextView
    private lateinit var tv_mo: AppCustomTextView


    companion object {

        private val ARG_SHOP_ID = "shop_id"

        fun newInstance(objects: Any): ViewOutstandingFragment {
            val viewStockFragment = ViewOutstandingFragment()
            val bundle = Bundle()
            bundle.putString(ARG_SHOP_ID, objects.toString())
            viewStockFragment.arguments = bundle
            return viewStockFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        shopId = arguments?.getString(ARG_SHOP_ID)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_stock_listing, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        tv_co = view.findViewById(R.id.tv_co)
        tv_po = view.findViewById(R.id.tv_po)
        rv_stock_list = view.findViewById(R.id.rv_stock_list)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        ll_view_stock_main = view.findViewById(R.id.ll_view_stock_main)
        ll_view_stock_main.setOnClickListener(null)
        tv_stock_list_shop_name = view.findViewById(R.id.tv_stock_list_shop_name)
        fab = view.findViewById(R.id.fab)
        tv_mo = view.findViewById(R.id.tv_mo)
        tv_mo.visibility = View.VISIBLE
        tv_opening_stock = view.findViewById(R.id.tv_opening_stock)
        tv_opening_stock.visibility = View.GONE

        fab.setOnClickListener({
            if (!Pref.isAddAttendence)
                (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
            else {
                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)

                if (!shop.isUploaded) {
                    (mContext as DashboardActivity).showSnackMessage("Please sync the shop first")

                    Handler().postDelayed(Runnable {
                        (mContext as DashboardActivity).onBackPressed()
                    }, 500)
                } else {
                    if (!TextUtils.isEmpty((mContext as DashboardActivity).shop_type))
                        openAddressUpdateDialog(shop)
                }
            }
        })

        val list = AppDatabase.getDBInstance()!!.updateOutstandingDao().getAll()

        /*if (list == null || list.isEmpty()) {
            if (AppUtils.isOnline(mContext)) {
                callStockListApi()
            } else
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        } else*/
            setData()
    }

    private fun openAddressUpdateDialog(addShopModelEntity: AddShopDBModelEntity) {
        try {
            UpdateOutstandingDialog.getInstance(addShopModelEntity.shop_id, (mContext as DashboardActivity).shop_type, object : UpdateOutstandingDialog.UpdateOutstandingListener {
                override fun updateOutstanding(mo: String, co: String, po: String) {
                    val updateStock = AppDatabase.getDBInstance()!!.updateOutstandingDao().getOutstandingAccordingToSyncStateId(false/*AppUtils.getCurrentDateForShopActi()*/, addShopModelEntity.shop_id)

                    val currentDateTime = AppUtils.getCurrentISODateTime()

                    if (updateStock == null || updateStock.isEmpty()) {

                        doAsync {

                            saveToDatabase(addShopModelEntity, mo, co, po)

                            uiThread {
                                /*if (AppUtils.isOnline(mContext)) {
                                    callUpdateStockApiForNewStock(addShopModelEntity.shop_id, amount, closingMonth, closingYear, openingMonth,
                                            openingYear, description, mo, co, po, currentDateTime)
                                }
                                else {*/
                                val stockList = AppDatabase.getDBInstance()!!.updateOutstandingDao().getOutstandingAccordingToShopId(shopId)
                                initAdapter(stockList)
                                (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.outstanding_update_success))
                                //}
                            }
                        }

                    } else {
                        /*if (AppUtils.isOnline(mContext))
                            callUpdateStockApiForSyncOneitem(updateStock, amount, mo, co, po, description)
                        else*/
                        (mContext as DashboardActivity).showSnackMessage("Please sync your previous outstanding first")
                        Handler().postDelayed(Runnable {
                            (mContext as DashboardActivity).onBackPressed()
                        }, 500)
                    }
                }
            }).show((mContext as DashboardActivity).supportFragmentManager, "UpdatePPDDOutstandingDialog")
        } catch (e: Exception) {
            //openAddressUpdateDialog(addShopModelEntity)
            e.printStackTrace()
        }
    }

    private fun saveToDatabase(addShopModelEntity: AddShopDBModelEntity, mo: String, co: String, po: String) {
        val updateOutstandingObj = OutstandingListEntity()
        updateOutstandingObj.shop_id = addShopModelEntity.shop_id
        updateOutstandingObj.current_date = AppUtils.getCurrentDateForShopActi()

        if (TextUtils.isEmpty(mo))
            updateOutstandingObj.mo = "0.00"
        else {
            val totalPrice = String.format("%.2f", mo.toFloat())
            updateOutstandingObj.mo = totalPrice
        }
        if (TextUtils.isEmpty(co))
            updateOutstandingObj.co = "0.00"
        else {
            val totalPrice = String.format("%.2f", co.toFloat())
            updateOutstandingObj.co = totalPrice
        }
        if (TextUtils.isEmpty(po))
            updateOutstandingObj.po = "0.00"
        else {
            val totalPrice = String.format("%.2f", po.toFloat())
            updateOutstandingObj.po = totalPrice
        }
        AppDatabase.getDBInstance()!!.updateOutstandingDao().insert(updateOutstandingObj)
    }

    private fun addDataToListDb(amount: String, mo: String, po: String, co: String, shop_id: String) {
        val updateStockList = StockListEntity()
        updateStockList.current_date = AppUtils.getCurrentDateForShopActi()
        updateStockList.mo = mo + ".00"
        updateStockList.stock_value = amount + ".00"

        if (TextUtils.isEmpty(po))
            updateStockList.po = "0.00"
        else
            updateStockList.po = po + ".00"

        if (TextUtils.isEmpty(co))
            updateStockList.co = "0.00"
        else
            updateStockList.co = co + ".00"

        updateStockList.shop_id = shop_id
        AppDatabase.getDBInstance()!!.stockListDao().insert(updateStockList)
    }

    private fun callUpdateStockApiForNewStock(shop_id: String, amount: String, closingMonth: String, closingYear: String, openingMonth: String,
                                              openingYear: String, description: String, mo: String, co: String, po: String, currentDateTime: String) {
        val repository = UpdateStockRepoProvider.provideOrderDetailsListRepository()
        progress_wheel.spin()
        val addressUpdateReq = UpdateStockInputParamsModel()
        addressUpdateReq.user_id = Pref.user_id
        addressUpdateReq.shop_id = shop_id
        addressUpdateReq.closing_stock_amount = amount
        addressUpdateReq.closing_stock_month = closingMonth + ", " + closingYear
        addressUpdateReq.closing_stock_month_val = AppUtils.getMonthValue(closingMonth)
        addressUpdateReq.closing_stock_year_val = closingYear
        addressUpdateReq.opening_stock_amount = amount
        addressUpdateReq.opening_stock_month = openingMonth + ", " + openingYear
        addressUpdateReq.opening_stock_month_val = AppUtils.getMonthValue(openingMonth)
        addressUpdateReq.opening_stock_year_val = openingYear
        addressUpdateReq.description = description
        addressUpdateReq.stock_date = currentDateTime

        if (TextUtils.isEmpty(mo))
            addressUpdateReq.m_o = "0.00"
        else
            addressUpdateReq.m_o = mo

        if (TextUtils.isEmpty(po))
            addressUpdateReq.p_o = "0.00"
        else
            addressUpdateReq.p_o = po

        if (TextUtils.isEmpty(co))
            addressUpdateReq.c_o = "0.00"
        else
            addressUpdateReq.c_o = co

        BaseActivity.compositeDisposable.add(
                repository.updateStock(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {

                                AppDatabase.getDBInstance()?.updateStockDao()?.updateIsUploadedForOneItem(true, shop_id, AppUtils.getMonthValue(closingMonth),
                                        closingYear, AppUtils.getMonthValue(openingMonth), openingYear)

                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            } else
                                (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))

                            val stockList = AppDatabase.getDBInstance()!!.stockListDao().getStockAccordingToShopId(shopId)
                            //initAdapter(stockList)

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))

                            val stockList = AppDatabase.getDBInstance()!!.stockListDao().getStockAccordingToShopId(shopId)
                            //initAdapter(stockList)
                        })
        )
    }

    private fun callUpdateStockApiForSyncOneitem(updateStock: UpdateStockEntity, amount: String, mo: String, co: String, po: String, description: String) {
        val repository = UpdateStockRepoProvider.provideOrderDetailsListRepository()
        progress_wheel.spin()
        val addressUpdateReq = UpdateStockInputParamsModel()
        addressUpdateReq.user_id = Pref.user_id
        addressUpdateReq.shop_id = updateStock.shop_id
        addressUpdateReq.closing_stock_amount = updateStock.closing_stock_amount
        addressUpdateReq.closing_stock_month = updateStock.closing_stock_month
        addressUpdateReq.closing_stock_month_val = updateStock.closing_stock_month_val
        addressUpdateReq.closing_stock_year_val = updateStock.closing_stock_year_val
        addressUpdateReq.opening_stock_amount = updateStock.opening_stock_amount
        addressUpdateReq.opening_stock_month = updateStock.opening_stock_month
        addressUpdateReq.opening_stock_month_val = updateStock.opening_stock_month_val
        addressUpdateReq.opening_stock_year_val = updateStock.opening_stock_year_val
        addressUpdateReq.description = updateStock.description
        addressUpdateReq.p_o = updateStock.po
        addressUpdateReq.m_o = updateStock.mo
        addressUpdateReq.c_o = updateStock.co

        BaseActivity.compositeDisposable.add(
                repository.updateStock(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                addDataToListDb(amount, mo, po, co, updateStock.shop_id!!)
                                AppDatabase.getDBInstance()!!.updateStockDao().updateClosingAmount(amount, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!)
                                AppDatabase.getDBInstance()!!.updateStockDao().updateOpeningAmount(amount, updateStock.shop_id!!, updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                if (TextUtils.isEmpty(mo))
                                    AppDatabase.getDBInstance()!!.updateStockDao().updateMO("0", updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)
                                else
                                    AppDatabase.getDBInstance()!!.updateStockDao().updateMO(mo, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                if (TextUtils.isEmpty(po))
                                    AppDatabase.getDBInstance()!!.updateStockDao().updatePO("0", updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)
                                else
                                    AppDatabase.getDBInstance()!!.updateStockDao().updatePO(po, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                if (TextUtils.isEmpty(co))
                                    AppDatabase.getDBInstance()!!.updateStockDao().updateCO("0", updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)
                                else
                                    AppDatabase.getDBInstance()!!.updateStockDao().updateCO(co, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                            updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                AppDatabase.getDBInstance()!!.updateStockDao().updateDescription(description, updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                        updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)


                                AppDatabase.getDBInstance()!!.updateStockDao().updateCurrentDate(AppUtils.getCurrentDate(), updateStock.shop_id!!, updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                        updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                val updateStockNew = AppDatabase.getDBInstance()!!.updateStockDao().getStockAccordingToShopIdStockMonth(updateStock.shop_id!!,
                                        updateStock.closing_stock_month_val!!, updateStock.closing_stock_year_val!!,
                                        updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)

                                callUpdateStockApiForOneitem(updateStockNew, amount)

                                //(mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                        })
        )
    }

    private fun callUpdateStockApiForOneitem(updateStock: UpdateStockEntity, amount: String) {
        val repository = UpdateStockRepoProvider.provideOrderDetailsListRepository()
        progress_wheel.spin()
        val addressUpdateReq = UpdateStockInputParamsModel()
        addressUpdateReq.user_id = Pref.user_id
        addressUpdateReq.shop_id = updateStock.shop_id
        addressUpdateReq.closing_stock_amount = amount
        addressUpdateReq.closing_stock_month = updateStock.closing_stock_month
        addressUpdateReq.closing_stock_month_val = updateStock.closing_stock_month_val
        addressUpdateReq.closing_stock_year_val = updateStock.closing_stock_year_val
        addressUpdateReq.opening_stock_amount = amount
        addressUpdateReq.opening_stock_month = updateStock.opening_stock_month
        addressUpdateReq.opening_stock_month_val = updateStock.opening_stock_month_val
        addressUpdateReq.opening_stock_year_val = updateStock.opening_stock_year_val
        addressUpdateReq.description = updateStock.description
        addressUpdateReq.p_o = updateStock.po
        addressUpdateReq.m_o = updateStock.mo
        addressUpdateReq.c_o = updateStock.co

        BaseActivity.compositeDisposable.add(
                repository.updateStock(addressUpdateReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                AppDatabase.getDBInstance()?.updateStockDao()?.updateIsUploadedForOneItem(true, updateStock.shop_id!!, updateStock.closing_stock_month_val!!,
                                        updateStock.closing_stock_year_val!!, updateStock.opening_stock_month_val!!, updateStock.opening_stock_year_val!!)
                            } else
                                (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))

                            val stockList = AppDatabase.getDBInstance()!!.stockListDao().getStockAccordingToShopId(shopId)
                            //initAdapter(stockList)

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.stock_update_success))

                            val stockList = AppDatabase.getDBInstance()!!.stockListDao().getStockAccordingToShopId(shopId)
                            //initAdapter(stockList)
                        })
        )
    }

    private fun callStockListApi() {

        if (Pref.user_id == null || Pref.session_token == null) {
            (mContext as DashboardActivity).clearData()
            startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
            (mContext as DashboardActivity).overridePendingTransition(0, 0)
            (mContext as DashboardActivity).finish()
            return
        }

        val repository = StockListRepoProvider.stockListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.stockList(Pref.session_token!!, Pref.user_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val stockList = result as StockListResponseModel
                            if (stockList.status == NetworkConstant.SUCCESS) {

                                doAsync {
                                    saveValueToDb(stockList.stock_list)

                                    uiThread {
                                        progress_wheel.stopSpinning()
                                        setData()
                                    }
                                }
                            } else if (stockList.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                tv_no_data_available.visibility = View.VISIBLE
                            } else if (stockList.status == NetworkConstant.SESSION_MISMATCH) {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(stockList.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }

    private fun saveValueToDb(stockListData: ArrayList<StockListDataModel>?) {

        for (i in stockListData?.indices!!) {
            val updateStockList = StockListEntity()
            updateStockList.current_date = stockListData[i].stock_date
            updateStockList.mo = stockListData[i].m_o
            updateStockList.po = stockListData[i].p_o
            updateStockList.co = stockListData[i].c_o
            updateStockList.shop_id = stockListData[i].shop_id
            updateStockList.stock_value = stockListData[i].stock_value
            AppDatabase.getDBInstance()!!.stockListDao().insert(updateStockList)
        }
    }

    private fun setData() {
        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)

        tv_stock_list_shop_name.text = shop.shopName

        if (shop.type == "2") {
            tv_po.visibility = View.GONE
            tv_co.visibility = View.VISIBLE
        } else if (shop.type == "4") {
            tv_co.visibility = View.GONE
            tv_po.visibility = View.VISIBLE
        }

        val stockList = AppDatabase.getDBInstance()!!.updateOutstandingDao().getOutstandingAccordingToShopId(shopId)
        if (stockList != null && stockList.isNotEmpty())
            initAdapter(stockList)
        else
            tv_no_data_available.visibility = View.VISIBLE
    }

    private fun initAdapter(stockList: List<OutstandingListEntity>) {
        tv_no_data_available.visibility = View.GONE
        rv_stock_list.layoutManager = LinearLayoutManager(mContext)
        rv_stock_list.adapter = ViewOutstandingAdapter(mContext, stockList)
    }
}
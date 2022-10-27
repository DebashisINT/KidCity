package com.kcteam.features.activities.presentation

import android.content.Context
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddDoctorEntity
import com.kcteam.app.domain.AddDoctorProductListEntity
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.activities.api.ActivityRepoProvider
import com.kcteam.features.activities.model.AddChemistProductModel
import com.kcteam.features.activities.model.AddDoctorVisitInputModel
import com.kcteam.features.activities.model.DoctorListResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 09-01-2020.
 */
class DoctorActivityListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var shop_IV: ImageView
    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_address_TV: AppCustomTextView
    private lateinit var tv_contact_number: AppCustomTextView
    private lateinit var rv_activity_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var fab_add_activity: FloatingActionButton
    private lateinit var shop_list_LL: LinearLayout

    companion object {

        var mAddShopDataObj: AddShopDBModelEntity? = null

        fun newInstance(objects: Any): DoctorActivityListFragment {
            val fragment = DoctorActivityListFragment()

            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is AddShopDBModelEntity) {
                    mAddShopDataObj = objects
                }
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_activity_list, container, false)

        initView(view)

        val list = AppDatabase.getDBInstance()!!.addDocDao().getAll()
        if (list != null && list.isNotEmpty())
            initAdapter()
        else
            getDoctorVisitListApi()

        return view
    }

    private fun initView(view: View) {
        shop_IV = view.findViewById(R.id.shop_IV)
        myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
        myshop_address_TV = view.findViewById(R.id.myshop_address_TV)
        tv_contact_number = view.findViewById(R.id.tv_contact_number)

        rv_activity_list = view.findViewById(R.id.rv_activity_list)
        rv_activity_list.layoutManager = LinearLayoutManager(mContext)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        tv_no_data = view.findViewById(R.id.tv_no_data)
        fab_add_activity = view.findViewById(R.id.fab_add_activity)
        shop_list_LL = view.findViewById(R.id.shop_list_LL)

        fab_add_activity.setOnClickListener(this)
        shop_list_LL.setOnClickListener(this)

        myshop_name_TV.text = mAddShopDataObj?.shopName
        myshop_address_TV.text = mAddShopDataObj?.address
        tv_contact_number.text = "Owner Contact Number: " + mAddShopDataObj?.ownerContactNumber

        val drawable = TextDrawable.builder().buildRoundRect(mAddShopDataObj?.shopName?.toString()?.trim()?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)
        shop_IV.setImageDrawable(drawable)

        if ((mContext as DashboardActivity).isFromShop)
            fab_add_activity.visibility = View.VISIBLE
        else
            fab_add_activity.visibility = View.GONE
    }


    private fun getDoctorVisitListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            tv_no_data.visibility = View.VISIBLE
            return
        }

        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getDoctortVisit()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as DoctorListResponseModel
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {
                                val docVisitList = response.doc_visit_list

                                if (docVisitList != null && docVisitList.isNotEmpty()) {

                                    doAsync {

                                        for (i in docVisitList.indices) {

                                            val docEntity = AddDoctorEntity()
                                            docEntity.shop_id = docVisitList[i].shop_id
                                            docEntity.remarks_mr = docVisitList[i].remarks_mr
                                            docEntity.visit_date = docVisitList[i].next_visit_date
                                            docEntity.doc_visit_id = docVisitList[i].doc_visit_id
                                            docEntity.crm_from_date = docVisitList[i].from_cme_date
                                            docEntity.crm_to_date = docVisitList[i].to_crm_date
                                            docEntity.volume = docVisitList[i].crm_volume
                                            docEntity.which_kind = docVisitList[i].which_kind
                                            docEntity.doc_remark = docVisitList[i].doc_remarks
                                            docEntity.qty_text = docVisitList[i].qty_vol_text
                                            docEntity.prescribe_status = docVisitList[i].is_prescriber
                                            docEntity.qty_status = docVisitList[i].is_qty
                                            docEntity.sample_status = docVisitList[i].is_sample
                                            docEntity.crm_status = docVisitList[i].is_crm
                                            docEntity.money_status = docVisitList[i].is_money
                                            docEntity.gift_status = docVisitList[i].is_gift
                                            docEntity.amount = docVisitList[i].amount
                                            docEntity.what = docVisitList[i].what
                                            docEntity.isUploaded = true

                                            for (j in docVisitList[i].product_list.indices) {
                                                val pobChemEntity = AddDoctorProductListEntity()
                                                pobChemEntity.doc_visit_id = docEntity.doc_visit_id
                                                pobChemEntity.shop_id = docEntity.shop_id
                                                pobChemEntity.product_status = 0
                                                pobChemEntity.product_id = docVisitList[i].product_list[j].product_id
                                                pobChemEntity.product_name = docVisitList[i].product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addDocProductDao().insertAll(pobChemEntity)
                                            }

                                            for (j in docVisitList[i].qty_product_list.indices) {
                                                val pobChemEntity = AddDoctorProductListEntity()
                                                pobChemEntity.doc_visit_id = docEntity.doc_visit_id
                                                pobChemEntity.shop_id = docEntity.shop_id
                                                pobChemEntity.product_status = 1
                                                pobChemEntity.product_id = docVisitList[i].qty_product_list[j].product_id
                                                pobChemEntity.product_name = docVisitList[i].qty_product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addDocProductDao().insertAll(pobChemEntity)
                                            }

                                            for (j in docVisitList[i].sample_product_list.indices) {
                                                val pobChemEntity = AddDoctorProductListEntity()
                                                pobChemEntity.doc_visit_id = docEntity.doc_visit_id
                                                pobChemEntity.shop_id = docEntity.shop_id
                                                pobChemEntity.product_status = 2
                                                pobChemEntity.product_id = docVisitList[i].sample_product_list[j].product_id
                                                pobChemEntity.product_name = docVisitList[i].sample_product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addDocProductDao().insertAll(pobChemEntity)
                                            }

                                            AppDatabase.getDBInstance()!!.addDocDao().insertAll(docEntity)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            initAdapter()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    tv_no_data.visibility = View.VISIBLE
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                tv_no_data.visibility = View.VISIBLE
                            }

                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            tv_no_data.visibility = View.VISIBLE
                        })
        )
    }


    private fun initAdapter() {
        val list = AppDatabase.getDBInstance()!!.addDocDao().getDataShopIdWise(mAddShopDataObj?.shop_id!!) as ArrayList<AddDoctorEntity>

        if (list != null && list.isNotEmpty()) {
            tv_no_data.visibility = View.GONE
            rv_activity_list.adapter = DoctorActivityAdapter(mContext, list, object : DoctorActivityAdapter.OnItemClickListener {
                override fun onSyncClick(adapterPosition: Int) {

                    val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(mAddShopDataObj?.shop_id)
                    if (!shop.isUploaded) {
                        (mContext as DashboardActivity).showSnackMessage("Please sync party first")
                        return
                    }

                    val docEntity = list[adapterPosition]

                    val docVisit = AddDoctorVisitInputModel()

                    if (!TextUtils.isEmpty(docEntity.doc_visit_id))
                        docVisit.doc_visit_id = docEntity.doc_visit_id!!

                    if (!TextUtils.isEmpty(docEntity.amount))
                        docVisit.amount = docEntity.amount!!

                    if (!TextUtils.isEmpty(docEntity.visit_date))
                        docVisit.next_visit_date = docEntity.visit_date!!

                    if (!TextUtils.isEmpty(docEntity.volume))
                        docVisit.crm_volume = docEntity.volume!!

                    if (!TextUtils.isEmpty(docEntity.remarks_mr))
                        docVisit.remarks_mr = docEntity.remarks_mr!!

                    if (!TextUtils.isEmpty(docEntity.doc_remark))
                        docVisit.doc_remarks = docEntity.doc_remark!!

                    if (!TextUtils.isEmpty(docEntity.shop_id))
                        docVisit.shop_id = docEntity.shop_id!!

                    docVisit.user_id = Pref.user_id!!
                    docVisit.session_token = Pref.session_token!!

                    if (!TextUtils.isEmpty(docEntity.crm_from_date))
                        docVisit.from_cme_date = docEntity.crm_from_date!!

                    if (!TextUtils.isEmpty(docEntity.crm_to_date))
                        docVisit.to_crm_date = docEntity.crm_to_date!!

                    docVisit.is_crm = docEntity.crm_status
                    docVisit.is_gift = docEntity.gift_status
                    docVisit.is_money = docEntity.money_status
                    docVisit.is_prescriber = docEntity.prescribe_status
                    docVisit.is_qty = docEntity.qty_status
                    docVisit.is_sample = docEntity.sample_status

                    if (!TextUtils.isEmpty(docEntity.qty_text))
                        docVisit.qty_vol_text = docEntity.qty_text!!

                    if (!TextUtils.isEmpty(docEntity.what))
                        docVisit.what = docEntity.what!!

                    if (!TextUtils.isEmpty(docEntity.which_kind))
                        docVisit.which_kind = docEntity.which_kind!!

                    val mList = AppDatabase.getDBInstance()!!.addDocProductDao().getDataIdPodWise(docEntity.doc_visit_id!!, 0) as ArrayList
                    val productList = ArrayList<AddChemistProductModel>()
                    if (mList != null) {
                        for (i in mList.indices) {
                            val product = AddChemistProductModel()
                            product.product_id = mList[i].product_id!!
                            product.product_name = mList.get(i).product_name!!
                            productList.add(product)
                        }
                    }
                    docVisit.product_list = productList

                    val qtyProductList = AppDatabase.getDBInstance()!!.addDocProductDao().getDataIdPodWise(docEntity.doc_visit_id!!, 1) as ArrayList
                    val podProductList = ArrayList<AddChemistProductModel>()
                    if (qtyProductList != null) {
                        for (i in qtyProductList.indices) {
                            val product = AddChemistProductModel()
                            product.product_id = qtyProductList[i].product_id!!
                            product.product_name = qtyProductList[i].product_name!!
                            podProductList.add(product)
                        }
                    }
                    docVisit.qty_product_list = podProductList

                    val sampleProductDbList = AppDatabase.getDBInstance()!!.addDocProductDao().getDataIdPodWise(docEntity.doc_visit_id!!, 2) as ArrayList
                    val sampleProductList = ArrayList<AddChemistProductModel>()
                    if (sampleProductDbList != null) {
                        for (i in sampleProductDbList.indices) {
                            val product = AddChemistProductModel()
                            product.product_id = sampleProductDbList[i].product_id!!
                            product.product_name = sampleProductDbList[i].product_name!!
                            sampleProductList.add(product)
                        }
                    }
                    docVisit.sample_product_list = sampleProductList

                    callUploadDocVisitApi(docVisit)
                }

                override fun onEditClick(adapterPosition: Int) {
                    (mContext as DashboardActivity).loadFragment(FragType.EditDoctorActivityFragment, true, list[adapterPosition])
                }

                override fun onViewClick(adapterPosition: Int) {
                    if (mAddShopDataObj?.type == "7")
                        (mContext as DashboardActivity).loadFragment(FragType.ChemistDetailsFragment, true, list[adapterPosition])
                    else if (mAddShopDataObj?.type == "8")
                        (mContext as DashboardActivity).loadFragment(FragType.DoctorDetailsFragment, true, list[adapterPosition])
                }

            })
        } else
            tv_no_data.visibility = View.VISIBLE
    }

    private fun callUploadDocVisitApi(docVisit: AddDoctorVisitInputModel) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("======SYNC DOCTOR VISIT INPUT PARAMS (ACTIVITY LIST)======")
        XLog.d("USER ID===> " + docVisit.user_id)
        XLog.d("SESSION ID====> " + docVisit.session_token)
        XLog.d("DOCTOR VISIT ID====> " + docVisit.doc_visit_id)
        XLog.d("SHOP_ID====> " + docVisit.shop_id)
        XLog.d("AMOUNT====> " + docVisit.amount)
        XLog.d("NEXT VISIT DATE====> " + docVisit.next_visit_date)
        XLog.d("VOLUME====> " + docVisit.crm_volume)
        XLog.d("DOCTOR REMARKS====> " + docVisit.doc_remarks)
        XLog.d("REMARKS MR====> " + docVisit.remarks_mr)
        XLog.d("FROM DATE====> " + docVisit.from_cme_date)
        XLog.d("TO DATE====> " + docVisit.to_crm_date)
        XLog.d("IS GIFT====> " + docVisit.is_gift)
        XLog.d("IS CRM====> " + docVisit.is_crm)
        XLog.d("IS MONEY====> " + docVisit.is_money)
        XLog.d("IS PRESCRIBER====> " + docVisit.is_prescriber)
        XLog.d("IS QTY====> " + docVisit.is_qty)
        XLog.d("IS SAMPLE====> " + docVisit.is_sample)
        XLog.d("QTY VOL TEXT====> " + docVisit.qty_vol_text)
        XLog.d("WHAT====> " + docVisit.what)
        XLog.d("WHICH====> " + docVisit.which_kind)
        XLog.d("PRODUCT LIST SIZE====> " + docVisit.product_list.size)
        XLog.d("QTY PRODUCT LIST SIZE====> " + docVisit.qty_product_list.size)
        XLog.d("SAMPLE PRODUCT LIST SIZE====> " + docVisit.sample_product_list.size)
        XLog.d("=========================================================")


        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.uploadDoctorVisit(docVisit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse

                            XLog.d("SYNC DOCTOR VISIT DETAILS : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + response.message)

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.addDocDao().updateIsUploaded(true, docVisit.doc_visit_id)
                                initAdapter()
                            }

                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("SYNC DOCTOR VISIT DETAILS : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_doc))
                            error.printStackTrace()
                        })
        )
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fab_add_activity -> {
                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else {
                    if (mAddShopDataObj?.type == "7")
                        (mContext as DashboardActivity).loadFragment(FragType.AddChemistFragment, true, mAddShopDataObj!!)
                    else if (mAddShopDataObj?.type == "8")
                        (mContext as DashboardActivity).loadFragment(FragType.AddDoctorFragment, true, mAddShopDataObj!!)
                }
            }

            R.id.shop_list_LL -> {
                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, mAddShopDataObj?.shop_id!!)
            }
        }
    }

    fun updateItem() {
        val phoneNo = AppDatabase.getDBInstance()!!.addShopEntryDao().getContactNumber(mAddShopDataObj?.shop_id)
        tv_contact_number.text = "Owner Contact Number: $phoneNo"

        initAdapter()
    }
}
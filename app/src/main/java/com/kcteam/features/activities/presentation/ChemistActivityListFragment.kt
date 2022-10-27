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
import com.kcteam.app.domain.AddChemistEntity
import com.kcteam.app.domain.AddChemistProductListEntity
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.activities.api.ActivityRepoProvider
import com.kcteam.features.activities.model.AddChemistProductModel
import com.kcteam.features.activities.model.AddChemistVisitInputModel
import com.kcteam.features.activities.model.ChemistVisitResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 07-01-2020.
 */
class ChemistActivityListFragment : BaseFragment(), View.OnClickListener {

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

        fun newInstance(objects: Any): ChemistActivityListFragment {
            val fragment = ChemistActivityListFragment()

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

        val list = AppDatabase.getDBInstance()!!.addChemistDao().getAll()
        if (list != null && list.isNotEmpty())
            initAdapter()
        else
            getChemistVisitListApi()

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


    private fun getChemistVisitListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            tv_no_data.visibility = View.VISIBLE
            return
        }

        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getChemistVisit()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ChemistVisitResponseModel
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {
                                val chemistVisitList = response.chemist_visit_list

                                if (chemistVisitList != null && chemistVisitList.isNotEmpty()) {

                                    doAsync {

                                        for (i in chemistVisitList.indices) {
                                            val chemistEntity = AddChemistEntity()
                                            chemistEntity.shop_id = chemistVisitList[i].shop_id
                                            chemistEntity.pob = chemistVisitList[i].isPob

                                            chemistEntity.remarks = chemistVisitList[i].remarks
                                            chemistEntity.remarks_mr = chemistVisitList[i].remarks_mr
                                            chemistEntity.visit_date = chemistVisitList[i].next_visit_date
                                            chemistEntity.volume = chemistVisitList[i].volume
                                            chemistEntity.chemist_visit_id = chemistVisitList[i].chemist_visit_id
                                            chemistEntity.isUploaded = true

                                            for (j in chemistVisitList[i].product_list.indices) {
                                                val pobChemEntity = AddChemistProductListEntity()
                                                pobChemEntity.chemist_visit_id = chemistEntity.chemist_visit_id
                                                pobChemEntity.shop_id = chemistEntity.shop_id
                                                pobChemEntity.isPob = false
                                                pobChemEntity.product_id = chemistVisitList[i].product_list[j].product_id
                                                pobChemEntity.product_name = chemistVisitList[i].product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addChemistProductDao().insertAll(pobChemEntity)
                                            }

                                            for (j in chemistVisitList[i].pob_product_list.indices) {
                                                val pobChemEntity = AddChemistProductListEntity()
                                                pobChemEntity.chemist_visit_id = chemistEntity.chemist_visit_id
                                                pobChemEntity.shop_id = chemistEntity.shop_id
                                                pobChemEntity.isPob = true
                                                pobChemEntity.product_id = chemistVisitList[i].pob_product_list[j].product_id
                                                pobChemEntity.product_name = chemistVisitList[i].pob_product_list[j].product_name

                                                AppDatabase.getDBInstance()!!.addChemistProductDao().insertAll(pobChemEntity)
                                            }


                                            AppDatabase.getDBInstance()!!.addChemistDao().insertAll(chemistEntity)
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
        val list = AppDatabase.getDBInstance()!!.addChemistDao().getDataShopIdWise(mAddShopDataObj?.shop_id!!) as ArrayList<AddChemistEntity>

        if (list != null && list.isNotEmpty()) {
            tv_no_data.visibility = View.GONE
            rv_activity_list.adapter = ChemistActivityAdapter(mContext, list, object : ChemistActivityAdapter.OnItemClickListener {
                override fun onSyncClick(adapterPosition: Int) {

                    val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(mAddShopDataObj?.shop_id)
                    if (!shop.isUploaded) {
                        (mContext as DashboardActivity).showSnackMessage("Please sync party first")
                        return
                    }

                    val chemistVisit = AddChemistVisitInputModel()

                    if (!TextUtils.isEmpty(list[adapterPosition].chemist_visit_id))
                        chemistVisit.chemist_visit_id = list[adapterPosition].chemist_visit_id!!

                    chemistVisit.isPob = list[adapterPosition].pob

                    if (!TextUtils.isEmpty(list[adapterPosition].visit_date))
                        chemistVisit.next_visit_date = list[adapterPosition].visit_date

                    if (!TextUtils.isEmpty(list[adapterPosition].remarks))
                        chemistVisit.remarks = list[adapterPosition].remarks!!

                    if (!TextUtils.isEmpty(list[adapterPosition].remarks_mr))
                        chemistVisit.remarks_mr = list[adapterPosition].remarks_mr!!

                    if (!TextUtils.isEmpty(list[adapterPosition].volume))
                        chemistVisit.volume = list[adapterPosition].volume!!

                    if (!TextUtils.isEmpty(list[adapterPosition].shop_id))
                        chemistVisit.shop_id = list[adapterPosition].shop_id!!

                    chemistVisit.user_id = Pref.user_id!!
                    chemistVisit.session_token = Pref.session_token!!

                    val mlist = AppDatabase.getDBInstance()!!.addChemistProductDao().getDataIdPodWise(chemistVisit.chemist_visit_id, false) as ArrayList
                    val productList = ArrayList<AddChemistProductModel>()
                    if (mlist != null) {
                        for (i in mlist.indices) {
                            val product = AddChemistProductModel()
                            product.product_id = mlist[i].id.toString()
                            product.product_name = mlist[i].product_name!!
                            productList.add(product)
                        }
                    }
                    chemistVisit.product_list = productList

                    val podList = AppDatabase.getDBInstance()!!.addChemistProductDao().getDataIdPodWise(chemistVisit.chemist_visit_id, true) as ArrayList
                    val podProductList = ArrayList<AddChemistProductModel>()
                    if (podList != null) {
                        for (i in podList.indices) {
                            val product = AddChemistProductModel()
                            product.product_id = podList[i].id.toString()
                            product.product_name = podList[i].product_name!!
                            productList.add(product)
                        }
                    }
                    chemistVisit.pob_product_list = podProductList

                    callUploadChemistVisitApi(chemistVisit)
                }

                override fun onEditClick(adapterPosition: Int) {
                    (mContext as DashboardActivity).loadFragment(FragType.EditChemistActivityFragment, true, list[adapterPosition])
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

    private fun callUploadChemistVisitApi(chemistVisit: AddChemistVisitInputModel) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("======SYNC CHEMIST VISIT INPUT PARAMS (ACTIVITY LIST)======")
        XLog.d("USER ID===> " + chemistVisit.user_id)
        XLog.d("SESSION ID====> " + chemistVisit.session_token)
        XLog.d("CHEMIST VISIT ID====> " + chemistVisit.chemist_visit_id)
        XLog.d("SHOP_ID====> " + chemistVisit.shop_id)
        XLog.d("IS POB====> " + chemistVisit.isPob)
        XLog.d("NEXT VISIT DATE====> " + chemistVisit.next_visit_date)
        XLog.d("VOLUME====> " + chemistVisit.volume)
        XLog.d("REMARKS====> " + chemistVisit.remarks)
        XLog.d("REMARKS MR====> " + chemistVisit.remarks_mr)
        XLog.d("PRODUCT LIST SIZE====> " + chemistVisit.product_list.size)
        XLog.d("POB PRODUCT LIST SIZE====> " + chemistVisit.pob_product_list.size)
        XLog.d("=========================================================")

        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.uploadChemistVisit(chemistVisit)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse

                            XLog.d("SYNC CHEMIST VISIT DETAILS : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + response.message)

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.addChemistDao().updateIsUploaded(true, chemistVisit.chemist_visit_id)
                                initAdapter()
                            }
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                        }, { error ->
                            XLog.d("SYNC CHEMIST VISIT DETAILS : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ", MESSAGE : " + error.localizedMessage)
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_chemist))
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
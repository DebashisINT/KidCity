package com.kcteam.features.newcollection

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.CollectionDetailsEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.newcollection.model.NewCollectionListResponseModel
import com.kcteam.features.newcollection.newcollectionlistapi.NewCollectionListRepoProvider
import com.kcteam.features.shopdetail.presentation.api.addcollection.AddCollectionRepoProvider
import com.kcteam.features.shopdetail.presentation.model.addcollection.AddCollectionInputParamsModel
import com.kcteam.features.shopdetail.presentation.model.collectionlist.CollectionListDataModel
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception
import java.util.*

/**
 * Created by Saikat on 15-02-2019.
 */
class NewCollectionListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var rv_collection_list: RecyclerView
    private lateinit var no_shop_tv: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var pick_a_date_TV: AppCustomTextView

    private var myCalendar = Calendar.getInstance(Locale.ENGLISH)
    private var isShopRegistrationInProcess = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_collection_list, container, false)

        initView(view)

        val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getAll() as ArrayList

        if (list != null && list.isNotEmpty()) {
            /*val dateWiseList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getDateWiseCollection(AppUtils.getFormattedDate(myCalendar)) as ArrayList<CollectionDetailsEntity>

            if (dateWiseList != null && dateWiseList.size > 0)
                initAdapter(dateWiseList)
            else {
                rv_collection_list.visibility = View.GONE
                no_shop_tv.visibility = View.VISIBLE
            }*/

            initAdapter(list)

        } else {
            getCollectionListApi()
        }

        return view
    }

    private fun initView(view: View) {
        rv_collection_list = view.findViewById(R.id.rv_collection_list)
        rv_collection_list.layoutManager = LinearLayoutManager(mContext)
        no_shop_tv = view.findViewById(R.id.no_shop_tv)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        pick_a_date_TV = view.findViewById(R.id.pick_a_date_TV)
        pick_a_date_TV.text = AppUtils.getFormattedDate(myCalendar.time)

        pick_a_date_TV.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getCollectionListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = NewCollectionListRepoProvider.newCollectionListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.collectionList(Pref.session_token!!, Pref.user_id!!, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val collection = result as NewCollectionListResponseModel
                            if (collection.status == NetworkConstant.SUCCESS) {
                                if (collection.collection_list == null || collection.collection_list?.size!! == 0) {
                                    progress_wheel.stopSpinning()
                                    no_shop_tv.visibility = View.VISIBLE
                                } else
                                    saveToDatabase(collection.collection_list!!)

                            } else if (collection.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                no_shop_tv.visibility = View.VISIBLE
                            } else if (collection.status == NetworkConstant.SESSION_MISMATCH) {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(collection.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            no_shop_tv.visibility = View.VISIBLE
                        })
        )
    }

    //private fun saveToDatabase(collection_details_list: ArrayList<CollectionListDataModel>) {
    private fun saveToDatabase(collection_details_list: ArrayList<CollectionDetailsEntity>) {
        doAsync {

        /*    for (i in collection_details_list.indices) {
                val collectionList = CollectionDetailsEntity()
                collectionList.date = AppUtils.convertDateTimeToCommonFormat(collection_details_list[i].collection_date!!) *//*AppUtils.convertToCommonFormat(collection_details_list[i].collection_date!!)*//*
                collectionList.shop_id = collection_details_list[i].shop_id
                collectionList.isUploaded = true
                collectionList.collection_id = collection_details_list[i].collection_id
                collectionList.collection = collection_details_list[i].collection
                collectionList.only_time = AppUtils.convertDateTimeToTime(collection_details_list[i].collection_date!!)
                collectionList.payment_id = collection_details_list[i].payment_id
                collectionList.instrument_no = collection_details_list[i].instrument_no
                collectionList.feedback = collection_details_list[i].remarks
                collectionList.bank = collection_details_list[i].bank
                collectionList.file_path = collection_details_list[i].doc
                collectionList.bill_id = collection_details_list[i].bill_id
                collectionList.order_id = collection_details_list[i].order_id
                collectionList.patient_no = collection_details_list[i].patient_no
                collectionList.patient_name = collection_details_list[i].patient_name
                collectionList.patient_address = collection_details_list[i].patient_address

                AppDatabase.getDBInstance()!!.collectionDetailsDao().insert(collectionList)
            }*/

            AppDatabase.getDBInstance()!!.collectionDetailsDao().insertAll(collection_details_list!!)

            uiThread {
                progress_wheel.stopSpinning()
                //val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getDateWiseCollection(AppUtils.getFormattedDate(myCalendar)) as ArrayList<CollectionDetailsEntity>

                val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getAll() as ArrayList<CollectionDetailsEntity>
                initAdapter(list)

            }
        }
    }

    private fun initAdapter(list: ArrayList<CollectionDetailsEntity>) {

        rv_collection_list.visibility = View.VISIBLE
        no_shop_tv.visibility = View.GONE

        rv_collection_list.adapter = NewCollectionAdapter(mContext, list, object : NewCollectionAdapter.OnSyncClickListener {
            override fun onSyncClick(adapterPosition: Int) {

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(list[adapterPosition].shop_id)

                if (shop != null) {

                    if (shop.isUploaded) {

                        if (!list[adapterPosition].isUploaded) {

                            if (AppUtils.isOnline(mContext))
                                syncAddCollectionApi(list, adapterPosition)
                            else
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                        }

                    } else {
                        syncShopFromSyncOption(shop, list, adapterPosition)
                    }
                }
            }
        })
    }

    private fun syncShopFromSyncOption(addShop: AddShopDBModelEntity, list: ArrayList<CollectionDetailsEntity>, adapterPosition: Int) {
        val addShopData = AddShopRequestData()
        val mAddShopDBModelEntity = addShop
        addShopData.session_token = Pref.session_token
        addShopData.address = mAddShopDBModelEntity.address
        addShopData.owner_contact_no = mAddShopDBModelEntity.ownerContactNumber
        addShopData.owner_email = mAddShopDBModelEntity.ownerEmailId
        addShopData.owner_name = mAddShopDBModelEntity.ownerName
        addShopData.pin_code = mAddShopDBModelEntity.pinCode
        addShopData.shop_lat = mAddShopDBModelEntity.shopLat.toString()
        addShopData.shop_long = mAddShopDBModelEntity.shopLong.toString()
        addShopData.shop_name = mAddShopDBModelEntity.shopName.toString()
        addShopData.type = mAddShopDBModelEntity.type.toString()
        addShopData.shop_id = mAddShopDBModelEntity.shop_id
        addShopData.user_id = Pref.user_id
        addShopData.amount = mAddShopDBModelEntity.amount
        addShopData.area_id = mAddShopDBModelEntity.area_id
        addShopData.model_id = mAddShopDBModelEntity.model_id
        addShopData.primary_app_id = mAddShopDBModelEntity.primary_app_id
        addShopData.secondary_app_id = mAddShopDBModelEntity.secondary_app_id
        addShopData.lead_id = mAddShopDBModelEntity.lead_id
        addShopData.stage_id = mAddShopDBModelEntity.stage_id
        addShopData.funnel_stage_id = mAddShopDBModelEntity.funnel_stage_id
        addShopData.booking_amount = mAddShopDBModelEntity.booking_amount
        addShopData.type_id = mAddShopDBModelEntity.type_id

        addShopData.director_name = mAddShopDBModelEntity.director_name
        addShopData.key_person_name = mAddShopDBModelEntity.person_name
        addShopData.phone_no = mAddShopDBModelEntity.person_no

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.family_member_dob))
            addShopData.family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.family_member_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.add_dob))
            addShopData.addtional_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.add_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.add_doa))
            addShopData.addtional_doa = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.add_doa)

        addShopData.specialization = mAddShopDBModelEntity.specialization
        addShopData.category = mAddShopDBModelEntity.category
        addShopData.doc_address = mAddShopDBModelEntity.doc_address
        addShopData.doc_pincode = mAddShopDBModelEntity.doc_pincode
        addShopData.is_chamber_same_headquarter = mAddShopDBModelEntity.chamber_status.toString()
        addShopData.is_chamber_same_headquarter_remarks = mAddShopDBModelEntity.remarks
        addShopData.chemist_name = mAddShopDBModelEntity.chemist_name
        addShopData.chemist_address = mAddShopDBModelEntity.chemist_address
        addShopData.chemist_pincode = mAddShopDBModelEntity.chemist_pincode
        addShopData.assistant_contact_no = mAddShopDBModelEntity.assistant_no
        addShopData.average_patient_per_day = mAddShopDBModelEntity.patient_count
        addShopData.assistant_name = mAddShopDBModelEntity.assistant_name

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.doc_family_dob))
            addShopData.doc_family_member_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.doc_family_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_dob))
            addShopData.assistant_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_dob)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_doa))
            addShopData.assistant_doa = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_doa)

        if (!TextUtils.isEmpty(mAddShopDBModelEntity.assistant_family_dob))
            addShopData.assistant_family_dob = AppUtils.changeAttendanceDateFormatToCurrent(mAddShopDBModelEntity.assistant_family_dob)

        addShopData.entity_id = mAddShopDBModelEntity.entity_id
        addShopData.party_status_id = mAddShopDBModelEntity.party_status_id
        addShopData.retailer_id = mAddShopDBModelEntity.retailer_id
        addShopData.dealer_id = mAddShopDBModelEntity.dealer_id
        addShopData.beat_id = mAddShopDBModelEntity.beat_id
        addShopData.assigned_to_shop_id = mAddShopDBModelEntity.assigned_to_shop_id
        addShopData.actual_address = mAddShopDBModelEntity.actual_address

        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id!!,false)
        addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!

        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name

        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate
        addShopData.purpose=mAddShopDBModelEntity.purpose


        callAddShopApiForSync(addShopData, mAddShopDBModelEntity.shopImageLocalPath, list, adapterPosition, mAddShopDBModelEntity.doc_degree)
    }

    private fun callAddShopApiForSync(addShop: AddShopRequestData, shop_imgPath: String?, list: ArrayList<CollectionDetailsEntity>,
                                      adapterPosition: Int, degree_imgPath: String?) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }



        if (isShopRegistrationInProcess)
            return


        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("=======SyncShop Input Params(New Collection)=========")
        XLog.d("shop id=======> " + addShop.shop_id)
        val index = addShop.shop_id!!.indexOf("_")
        XLog.d("decoded shop id=======> " + addShop.user_id + "_" + AppUtils.getDate(addShop.shop_id!!.substring(index + 1, addShop.shop_id!!.length).toLong()))
        XLog.d("shop added date=======> " + addShop.added_date)
        XLog.d("shop address=======> " + addShop.address)
        XLog.d("assigned to dd id=======> " + addShop.assigned_to_dd_id)
        XLog.d("assigned to pp id=======> " + addShop.assigned_to_pp_id)
        XLog.d("date aniversery=======> " + addShop.date_aniversary)
        XLog.d("dob=====> " + addShop.dob)
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

        if (degree_imgPath != null)
            XLog.d("doctor degree image path=======> $degree_imgPath")
        XLog.d("=======================================================")

        if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(degree_imgPath)) {
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
                                                syncAddCollectionApi(list, adapterPosition)
                                            }
                                        }
                                    }
                                    progress_wheel.stopSpinning()
                                    isShopRegistrationInProcess = false

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {
                                                syncAddCollectionApi(list, adapterPosition)
                                            }
                                        }
                                    }
                                    isShopRegistrationInProcess = false

                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)

                                    isShopRegistrationInProcess = false
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                isShopRegistrationInProcess = false
                                if (error != null)
                                    XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + error.localizedMessage)
                            })
            )
        }
        else {
            val repository = AddShopRepositoryProvider.provideAddShopRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShopWithImage(addShop, shop_imgPath, degree_imgPath, mContext)
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
                                                syncAddCollectionApi(list, adapterPosition)
                                            }
                                        }
                                    }
                                    progress_wheel.stopSpinning()
                                    isShopRegistrationInProcess = false

                                } else if (addShopResult.status == NetworkConstant.DUPLICATE_SHOP_ID) {
                                    XLog.d("DuplicateShop : " + ", SHOP: " + addShop.shop_name)
                                    AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)
                                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getDuplicateShopData(addShop.owner_contact_no).size > 0) {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().deleteShopById(addShop.shop_id)
                                        AppDatabase.getDBInstance()!!.shopActivityDao().deleteShopByIdAndDate(addShop.shop_id!!, AppUtils.getCurrentDateForShopActi())
                                    }
                                    doAsync {
                                        val resultAs = runLongTask(addShop.shop_id)
                                        uiThread {
                                            if (resultAs == true) {
                                                syncAddCollectionApi(list, adapterPosition)
                                            }
                                        }
                                    }
                                    isShopRegistrationInProcess = false

                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)

                                    isShopRegistrationInProcess = false
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync))
                                isShopRegistrationInProcess = false
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

    private fun callShopActivitySubmit(shopId: String) {
        val list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())
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
            }catch(ex:Exception){
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
                }
                catch(ex:Exception){
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
                        })
        )
    }


    private fun syncAddCollectionApi(list: ArrayList<CollectionDetailsEntity>, adapterPosition: Int) {

        if (isShopRegistrationInProcess)
            return

        isShopRegistrationInProcess = true

        val addCollection = AddCollectionInputParamsModel()
        addCollection.collection = list[adapterPosition].collection
        addCollection.collection_date = AppUtils.getCurrentDateFormatInTa(list[adapterPosition].date!!) + "T" + list[adapterPosition].only_time
        addCollection.collection_id = list[adapterPosition].collection_id
        addCollection.session_token = Pref.session_token
        addCollection.user_id = Pref.user_id
        addCollection.shop_id = list[adapterPosition].shop_id
        addCollection.bill_id = if (TextUtils.isEmpty(list[adapterPosition].bill_id)) "" else list[adapterPosition].bill_id!!
        addCollection.order_id = if (TextUtils.isEmpty(list[adapterPosition].order_id)) "" else list[adapterPosition].order_id!!
        addCollection.payment_id = if (TextUtils.isEmpty(list[adapterPosition].payment_id)) "" else list[adapterPosition].payment_id!!
        addCollection.instrument_no = if (TextUtils.isEmpty(list[adapterPosition].instrument_no)) "" else list[adapterPosition].instrument_no!!
        addCollection.bank = if (TextUtils.isEmpty(list[adapterPosition].bank)) "" else list[adapterPosition].bank!!
        addCollection.remarks = if (TextUtils.isEmpty(list[adapterPosition].feedback)) "" else list[adapterPosition].feedback!!
        addCollection.patient_name = if (TextUtils.isEmpty(list[adapterPosition].patient_name)) "" else list[adapterPosition].patient_name!!
        addCollection.patient_address = if (TextUtils.isEmpty(list[adapterPosition].patient_address)) "" else list[adapterPosition].patient_address!!
        addCollection.patient_no = if (TextUtils.isEmpty(list[adapterPosition].patient_no)) "" else list[adapterPosition].patient_no!!
        /*06-01-2022*/
        addCollection.Hospital = if (TextUtils.isEmpty(list[adapterPosition].Hospital)) "" else list[adapterPosition].Hospital!!
        addCollection.Email_Address = if (TextUtils.isEmpty(list[adapterPosition].Email_Address)) "" else list[adapterPosition].Email_Address!!

        progress_wheel.spin()

        if (TextUtils.isEmpty(list[adapterPosition].file_path)) {
            val repository = AddCollectionRepoProvider.addCollectionRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addCollection(addCollection)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                isShopRegistrationInProcess = false
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, list[adapterPosition].collection_id!!)

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")

                                    //val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getDateWiseCollection(AppUtils.getFormattedDate(myCalendar)) as ArrayList<CollectionDetailsEntity>

                                    val list_ = AppDatabase.getDBInstance()!!.collectionDetailsDao().getAll() as ArrayList<CollectionDetailsEntity>
                                    initAdapter(list_)
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                isShopRegistrationInProcess = false
                            })
            )
        }
        else {
            val repository = AddCollectionRepoProvider.addCollectionMultipartRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addCollection(addCollection, list[adapterPosition].file_path, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                isShopRegistrationInProcess = false
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, list[adapterPosition].collection_id!!)

                                    (mContext as DashboardActivity).showSnackMessage("Synced successfully")

                                    //val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getDateWiseCollection(AppUtils.getFormattedDate(myCalendar)) as ArrayList<CollectionDetailsEntity>

                                    val list_ = AppDatabase.getDBInstance()!!.collectionDetailsDao().getAll() as ArrayList<CollectionDetailsEntity>
                                    initAdapter(list_)
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                isShopRegistrationInProcess = false
                            })
            )
        }
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.pick_a_date_TV -> {
                val datePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datePicker.show()
            }
        }
    }

    val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        pick_a_date_TV.text = AppUtils.getFormattedDate(myCalendar.time)

        //val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getDateWiseCollection(AppUtils.getFormattedDate(myCalendar)) as ArrayList<CollectionDetailsEntity>

        val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getAll() as ArrayList<CollectionDetailsEntity>
        initAdapter(list)
    }
}
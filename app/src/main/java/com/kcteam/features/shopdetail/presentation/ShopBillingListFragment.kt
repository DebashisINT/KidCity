package com.kcteam.features.shopdetail.presentation

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
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
import com.kcteam.features.billing.model.BillingListDataModel
import com.kcteam.features.billing.model.BillingListResponseModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.shopdetail.presentation.api.addcollection.AddCollectionRepoProvider
import com.kcteam.features.shopdetail.presentation.api.collectionlist.CollectionListRepoProvider
import com.kcteam.features.shopdetail.presentation.model.addcollection.AddCollectionInputParamsModel
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ShopBillingListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_billing_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_address_TV: AppCustomTextView
    private lateinit var shop_IV: ImageView
    private lateinit var order_amount_tv: AppCustomTextView
    private lateinit var tv_contact_number: AppCustomTextView
    private lateinit var rl_shop_bill_list_main: RelativeLayout

    private var collectionDialog: AddCollectionDialog?= null

    companion object {
        private var mAddShopDataObj: AddShopDBModelEntity? = null

        fun getInstance(objects: Any): ShopBillingListFragment {
            val mViewAllOrderListFragment = ShopBillingListFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is AddShopDBModelEntity)
                    mAddShopDataObj = objects
            }
            return mViewAllOrderListFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_shop_billing_list, container, false)

        initView(view)
        setData()
        getBillListApi()

        return view
    }

    private fun initView(view: View) {
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        rv_billing_list = view.findViewById(R.id.rv_billing_list)
        rv_billing_list.layoutManager = LinearLayoutManager(mContext)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
        myshop_address_TV = view.findViewById(R.id.myshop_address_TV)
        shop_IV = view.findViewById(R.id.shop_IV)
        order_amount_tv = view.findViewById(R.id.order_amount_tv)
        tv_contact_number = view.findViewById(R.id.tv_contact_number)
        rl_shop_bill_list_main = view.findViewById(R.id.rl_shop_bill_list_main)

        rl_shop_bill_list_main.setOnClickListener(null)
    }

    private fun setData() {
        try {
            if (mAddShopDataObj != null) {
                val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(mAddShopDataObj?.shop_id!!)

                if (list != null && list.isNotEmpty()) {
                    order_amount_tv.visibility = View.VISIBLE
                    var amount = 0.0
                    for (i in list.indices) {
                        if (!TextUtils.isEmpty(list[i].collection))
                            amount += list[i].collection?.toDouble()!!
                    }
                    val totalPrice = String.format("%.2f", amount.toFloat())

                    val builder = SpannableStringBuilder()

                    val str1 = SpannableString("Total Collection: ")
                    builder.append(str1)

                    val str2 = SpannableString("₹ $totalPrice")
                    str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                    builder.append(str2)

                    order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
                } else
                    order_amount_tv.visibility = View.GONE

                myshop_address_TV.text = mAddShopDataObj?.address

                val builder = SpannableStringBuilder()

                val str1 = SpannableString("Owner Contact Number: ")
                builder.append(str1)

                val str2 = SpannableString(mAddShopDataObj?.ownerContactNumber)
                str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                builder.append(str2)

                tv_contact_number.setText(builder, TextView.BufferType.SPANNABLE)
            }

            myshop_name_TV.text = mAddShopDataObj?.shopName!!

            val drawable = TextDrawable.builder()
                    .buildRoundRect(mAddShopDataObj?.shopName?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

            shop_IV.setImageDrawable(drawable)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getBillListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = CollectionListRepoProvider.collectionListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.billingList(mAddShopDataObj?.shop_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BillingListResponseModel
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                val billing_list = response.billing_list

                                if (billing_list != null && billing_list.isNotEmpty())
                                    initAdapter(billing_list)
                                else {
                                    rv_billing_list.visibility = View.GONE
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }

                            } else {
                                rv_billing_list.visibility = View.GONE
                                tv_no_data_available.visibility = View.VISIBLE
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }


                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun initAdapter(billingList: ArrayList<BillingListDataModel>) {
        tv_no_data_available.visibility = View.GONE
        rv_billing_list.visibility = View.VISIBLE
        rv_billing_list.adapter = ShopBillingListAdapter(mContext, billingList) {
            try {

                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else {
                    collectionDialog = AddCollectionDialog.getInstance(mAddShopDataObj, true, mAddShopDataObj?.shopName!!, it.invoice_date!!, it.total_amount!!, it.order_id!!,  object : AddCollectionDialog.AddCollectionClickLisneter {
                        override fun onClick(collection: String, date: String, paymentId: String, instrument: String, bank: String, filePath: String, feedback: String, patientName: String, patientAddress: String, patinetNo: String,
                                             hospital:String,emailAddress:String,order_id:String) {


                            val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(mAddShopDataObj?.shop_id)
                            if (addShop != null) {

                                //if (addShop.isUploaded) {

                                doAsync {

                                    val collectionDetails = CollectionDetailsEntity()
                                    collectionDetails.collection = collection/*.substring(1)*/

                                    val random = Random()
                                    val m = random.nextInt(9999 - 1000) + 1000

                                    //collectionDetails.collection_id = Pref.user_id + "_" + m /*+ "_" + System.currentTimeMillis().toString()*/
                                    collectionDetails.collection_id = Pref.user_id + "c" + m
                                    collectionDetails.shop_id = mAddShopDataObj?.shop_id
                                    collectionDetails.date = date //AppUtils.getCurrentDate()
                                    collectionDetails.only_time = AppUtils.getCurrentTime()  //AppUtils.getCurrentDate()
                                    collectionDetails.bill_id = it.bill_id
                                    collectionDetails.order_id = it.order_id
                                    collectionDetails.payment_id = paymentId
                                    collectionDetails.bank = bank
                                    collectionDetails.instrument_no = instrument
                                    collectionDetails.file_path = filePath
                                    collectionDetails.feedback = feedback
                                    collectionDetails.patient_name = patientName
                                    collectionDetails.patient_address = patientAddress
                                    collectionDetails.patient_no = patinetNo

                                    /*06-01-2022*/
                                    collectionDetails.Hospital = hospital
                                    collectionDetails.Email_Address = emailAddress

                                    collectionDetails.order_id = order_id
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().insert(collectionDetails)

                                    val collectionDate = AppUtils.getCurrentDateForShopActi() + "T" + collectionDetails.only_time

                                    uiThread {

                                        if (AppUtils.isOnline(mContext)) {
                                            if (addShop.isUploaded) {
                                                addCollectionApi(collectionDetails.shop_id, collectionDetails.collection_id, "",
                                                        "", collection, collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                            } else {
                                                syncShop(addShop, collectionDetails.shop_id, collectionDetails.collection_id, "", "", collection,
                                                        collectionDate, collectionDetails.bill_id, collectionDetails.order_id, collectionDetails)
                                            }


                                        } else {
                                            (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
                                            voiceCollectionMsg()

                                            val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(mAddShopDataObj?.shop_id!!) as java.util.ArrayList<CollectionDetailsEntity>

                                            if (list != null && list?.size!! > 0) {
                                                order_amount_tv.visibility = View.VISIBLE
                                                var amount = 0.0
                                                for (i in list?.indices!!) {
                                                    if (!TextUtils.isEmpty(list?.get(i)?.collection))
                                                        amount += list?.get(i)?.collection?.toDouble()!!
                                                }

                                                val totalPrice = String.format("%.2f", amount.toFloat())
                                                order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                                            } else
                                                order_amount_tv.visibility = View.GONE
                                        }
                                    }
                                }
                            }

                        }
                    })
                    collectionDialog?.show((mContext as DashboardActivity).supportFragmentManager, "AddCollectionDialog")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun voiceCollectionMsg() {
        if (Pref.isVoiceEnabledForCollectionSaved) {
            val msg = "Hi, Collection saved successfully."
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Add Collection", "TTS error in converting Text to Speech!");

        }
    }

    private fun syncShop(addShop: AddShopDBModelEntity, shop_id: String?, collection_id: String?, amount: String, desc: String, collection: String,
                         currentDateForShopActi: String, billId: String?, orderId: String?, collectionDetails: CollectionDetailsEntity) {
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
        addShopData.assigned_to_pp_id = mAddShopDBModelEntity.assigned_to_pp_id
        addShopData.assigned_to_dd_id = mAddShopDBModelEntity.assigned_to_dd_id
        addShopData.added_date = mAddShopDBModelEntity.added_date
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

        var uniqKeyObj=AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id,false)
        addShopData.shop_revisit_uniqKey=uniqKeyObj?.shop_revisit_uniqKey!!


        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name

        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate

        addShopData.purpose=mAddShopDBModelEntity.purpose

        callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shop_id, collection_id, amount, collection,
                currentDateForShopActi, desc, billId, mAddShopDBModelEntity.doc_degree, orderId, collectionDetails)
    }

    var isShopRegistrationInProcess = false
    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, shop_id: String?, collection_id: String?, amount: String, collection: String,
                               currentDateForShopActi: String, desc: String, billId: String?, degree_imgPath: String?, orderId: String?,
                               collectionDetails: CollectionDetailsEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        if (isShopRegistrationInProcess)
            return

        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("==============================SyncShop Input Params(Collection)==============================")
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

        if(shop_imgPath != null)
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
        XLog.d("=================================================================================")

        if (TextUtils.isEmpty(shop_imgPath) && TextUtils.isEmpty(degree_imgPath)) {
            val repository = AddShopRepositoryProvider.provideAddShopWithoutImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addShop(addShop)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addShopResult = result as AddShopResponse
                                XLog.d("syncShopFromShopList : " + ", SHOP: " + addShop.shop_name + ", RESPONSE:" + result.message)
                                when (addShopResult.status) {
                                    NetworkConstant.SUCCESS -> {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                        //(mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                        doAsync {
                                            val resultAs = runLongTask(addShop.shop_id)
                                            uiThread {
                                                if (resultAs == true) {
                                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetails)
                                                }
                                            }
                                        }
                                        progress_wheel.stopSpinning()
                                        isShopRegistrationInProcess = false

                                    }
                                    NetworkConstant.DUPLICATE_SHOP_ID -> {
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
                                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetails)
                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                                        isShopRegistrationInProcess = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
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
                                when (addShopResult.status) {
                                    NetworkConstant.SUCCESS -> {
                                        AppDatabase.getDBInstance()!!.addShopEntryDao().updateIsUploaded(true, addShop.shop_id)
                                        //(mContext as DashboardActivity).showSnackMessage("Synced successfully")
                                        doAsync {
                                            val resultAs = runLongTask(addShop.shop_id)
                                            uiThread {
                                                if (resultAs == true) {
                                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetails)
                                                }
                                            }
                                        }
                                        progress_wheel.stopSpinning()
                                        isShopRegistrationInProcess = false

                                    }
                                    NetworkConstant.DUPLICATE_SHOP_ID -> {
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
                                                    addCollectionApi(shop_id, collection_id, amount, desc, collection, currentDateForShopActi,
                                                            billId, orderId, collectionDetails)
                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                                        isShopRegistrationInProcess = false
                                    }
                                }

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")
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

    private fun addCollectionApi(shop_id: String?, collection_id: String?, amount: String, desc: String, collection: String, date: String?,
                                 billId: String?, orderId: String?, collectionDetails: CollectionDetailsEntity) {


        val addCollection = AddCollectionInputParamsModel()
        addCollection.collection = collection
        addCollection.collection_date = date
        addCollection.collection_id = collection_id
        addCollection.session_token = Pref.session_token
        addCollection.user_id = Pref.user_id
        addCollection.shop_id = shop_id
        addCollection.bill_id = billId!!
        addCollection.order_id = orderId!!

        addCollection.payment_id = collectionDetails.payment_id!!

        if (collectionDetails.instrument_no != null)
            addCollection.instrument_no = collectionDetails.instrument_no!!

        if(collectionDetails.bank != null)
            addCollection.bank = collectionDetails.bank!!

        if (collectionDetails.feedback != null)
            addCollection.remarks = collectionDetails.feedback!!

        if (collectionDetails.patient_name != null)
            addCollection.patient_name = collectionDetails.patient_name!!

        if (collectionDetails.patient_address != null)
            addCollection.patient_address = collectionDetails.patient_address!!

        if (collectionDetails.patient_no != null)
            addCollection.patient_no = collectionDetails.patient_no!!

        progress_wheel.spin()

        if (TextUtils.isEmpty(collectionDetails.file_path)) {
            val repository = AddCollectionRepoProvider.addCollectionRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addCollection(addCollection)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, collection_id!!)
                                }

                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                                val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(mAddShopDataObj?.shop_id!!) as java.util.ArrayList<CollectionDetailsEntity>

                                if (list != null && list?.size!! > 0) {
                                    order_amount_tv.visibility = View.VISIBLE
                                    var amount = 0.0
                                    for (i in list?.indices!!) {
                                        if (!TextUtils.isEmpty(list?.get(i)?.collection))
                                            amount += list?.get(i)?.collection?.toDouble()!!
                                    }

                                    val totalPrice = String.format("%.2f", amount.toFloat())
                                    order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                                } else
                                    order_amount_tv.visibility = View.GONE

                                getBillListApi()
                                voiceCollectionMsg()

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")

                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                                val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(mAddShopDataObj?.shop_id!!) as java.util.ArrayList<CollectionDetailsEntity>

                                if (list != null && list?.size!! > 0) {
                                    order_amount_tv.visibility = View.VISIBLE
                                    var amount = 0.0
                                    for (i in list?.indices!!) {
                                        if (!TextUtils.isEmpty(list?.get(i)?.collection))
                                            amount += list?.get(i)?.collection?.toDouble()!!
                                    }

                                    val totalPrice = String.format("%.2f", amount.toFloat())
                                    order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                                } else
                                    order_amount_tv.visibility = View.GONE

                                getBillListApi()
                                voiceCollectionMsg()
                            })
            )
        }
        else {
            val repository = AddCollectionRepoProvider.addCollectionMultipartRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addCollection(addCollection, collectionDetails.file_path, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.collectionDetailsDao().updateIsUploaded(true, collection_id!!)
                                }

                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                                val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(mAddShopDataObj?.shop_id!!) as java.util.ArrayList<CollectionDetailsEntity>

                                if (list != null && list?.size!! > 0) {
                                    order_amount_tv.visibility = View.VISIBLE
                                    var amount = 0.0
                                    for (i in list?.indices!!) {
                                        if (!TextUtils.isEmpty(list?.get(i)?.collection))
                                            amount += list?.get(i)?.collection?.toDouble()!!
                                    }

                                    val totalPrice = String.format("%.2f", amount.toFloat())
                                    order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                                } else
                                    order_amount_tv.visibility = View.GONE

                                getBillListApi()

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")

                                (mContext as DashboardActivity).showSnackMessage("Collection added successfully")

                                val list = AppDatabase.getDBInstance()!!.collectionDetailsDao().getListAccordingToShopId(mAddShopDataObj?.shop_id!!) as java.util.ArrayList<CollectionDetailsEntity>

                                if (list != null && list?.size!! > 0) {
                                    order_amount_tv.visibility = View.VISIBLE
                                    var amount = 0.0
                                    for (i in list?.indices!!) {
                                        if (!TextUtils.isEmpty(list?.get(i)?.collection))
                                            amount += list?.get(i)?.collection?.toDouble()!!
                                    }

                                    val totalPrice = String.format("%.2f", amount.toFloat())
                                    order_amount_tv.text = "Total Collection: ₹ $totalPrice"
                                } else
                                    order_amount_tv.visibility = View.GONE

                                getBillListApi()
                            })
            )
        }
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        collectionDialog?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setImage(file: File) {
        collectionDialog?.setImage(file)
    }

}
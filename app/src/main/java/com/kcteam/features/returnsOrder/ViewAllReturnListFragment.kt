package com.kcteam.features.returnsOrder

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.domain.ReturnDetailsEntity
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.viewAllOrder.api.addorder.AddOrderRepoProvider
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class ViewAllReturnListFragment: BaseFragment(), View.OnClickListener {
    private lateinit var mContext: Context

    lateinit var ViewAllReturnListRecyclerViewAdapter: ViewAllReturnListRecyclerViewAdapter
    private lateinit var return_list_rv: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager

    private var viewAllReturnList: ArrayList<ReturnDetailsEntity>? = null
    private var shopName = ""
    private var shopContact = ""
    private lateinit var add_return_tv: FloatingActionButton
    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_address_TV: AppCustomTextView
    private lateinit var order_amount_tv: AppCustomTextView
    private lateinit var shop_IV: ImageView
    private lateinit var no_shop_tv: AppCustomTextView
    private lateinit var shop_detail_RL: RelativeLayout
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_contact_number: AppCustomTextView

    var i: Int = 0
    private var shopId = ""



    companion object {
        var mShopActivityEntity: ShopActivityEntity? = null
        var maddShopDataObj: AddShopDBModelEntity? = null
//          var orderListObj: OrderListEntity? = null
        var mSShopID_Str:String? = null
        fun getInstance(objects: Any): ViewAllReturnListFragment {
            val mViewAllReturnListFragment = ViewAllReturnListFragment()
            if (!TextUtils.isEmpty(objects.toString())) {
                if (objects is ShopActivityEntity) {
                    mShopActivityEntity = objects
                    maddShopDataObj = null
//                    orderListObj = null
                    mSShopID_Str = mShopActivityEntity?.shopid!!
                } else if (objects is AddShopDBModelEntity) {
                    maddShopDataObj = objects
//                    orderListObj = null
                    mShopActivityEntity = null
                    mSShopID_Str = maddShopDataObj?.shop_id
                }
            }
                return mViewAllReturnListFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_view_all_return_list, container, false)
        initView(view)
        AppUtils.stockStatus = 2
        setData()

        return view
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initView(view: View) {
        return_list_rv = view.findViewById(R.id.return_list_rv)
        add_return_tv = view.findViewById(R.id.add_return_tv)
        myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
        myshop_address_TV =  view.findViewById(R.id.myshop_address_TV)
        order_amount_tv = view.findViewById(R.id.order_amount_tv)
        no_shop_tv = view.findViewById(R.id.no_shop_tv)
        shop_IV = view.findViewById(R.id.shop_IV)
        shop_detail_RL = view.findViewById(R.id.shop_detail_RL)
        tv_contact_number = view.findViewById(R.id.tv_contact_number)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        shop_detail_RL.setOnClickListener(this)
        add_return_tv.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        i = 0
        when (p0?.id) {
            R.id.add_return_tv -> {
                try {
                    if (!Pref.isAddAttendence)
                        (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                    else
                        (mContext as DashboardActivity).loadFragment(FragType.ReturnTypeListFragment, true, shopId)


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            R.id.shop_detail_RL->{
                (mContext as DashboardActivity).loadFragment(FragType.ShopDetailFragment, true, shopId)
            }
        }
    }

    private fun setData() {
        try {
            if (mShopActivityEntity != null) {
                viewAllReturnList = AppDatabase.getDBInstance()!!.returnDetailsDao().getListAccordingToShopId(mShopActivityEntity?.shopid!!) as ArrayList<ReturnDetailsEntity>
                shopId = mShopActivityEntity?.shopid!!
                shopName = mShopActivityEntity?.shop_name!!
                var shopObj=AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(mShopActivityEntity?.shopid!!)
                shopContact = shopObj.ownerContactNumber!!

                if (viewAllReturnList != null && viewAllReturnList?.size!! > 0) {

                    var amount = 0.0
                    for (i in viewAllReturnList?.indices!!) {
                        if (!TextUtils.isEmpty(viewAllReturnList?.get(i)?.amount))
                            amount += viewAllReturnList?.get(i)?.amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())

                    val builder = SpannableStringBuilder()

                    val str1 = SpannableString("Total Return Amount: ")
                    builder.append(str1)

                    val str2 = SpannableString("₹ $finalAmount")
                    str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                    builder.append(str2)
                    order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
                }
                myshop_address_TV.text = mShopActivityEntity?.shop_address
                tv_contact_number.text = "Owner Contact Number: "+shopContact

            }
            else if (maddShopDataObj != null) {
                viewAllReturnList = AppDatabase.getDBInstance()!!.returnDetailsDao().getListAccordingToShopId(maddShopDataObj?.shop_id!!) as ArrayList<ReturnDetailsEntity>
                shopId = maddShopDataObj?.shop_id!!
                shopName = maddShopDataObj?.shopName!!
                shopContact = maddShopDataObj!!.ownerContactNumber!!

                if (viewAllReturnList != null && viewAllReturnList?.size!! > 0) {
                    order_amount_tv.visibility = View.VISIBLE
                    var amount = 0.0
                    for (i in viewAllReturnList?.indices!!) {
                        if (!TextUtils.isEmpty(viewAllReturnList?.get(i)?.amount))
                            amount += viewAllReturnList?.get(i)?.amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())

                    val builder = SpannableStringBuilder()

                    val str1 = SpannableString("Total Return Amount: ")
                    builder.append(str1)

                    val str2 = SpannableString("₹ $finalAmount")
                    str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                    builder.append(str2)

                    order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
                } else
                    order_amount_tv.visibility = View.GONE

                myshop_address_TV.text = maddShopDataObj?.address

                val builder = SpannableStringBuilder()

                val str1 = SpannableString("Owner Contact Number: ")
                builder.append(str1)

                val str2 = SpannableString(maddShopDataObj?.ownerContactNumber)
                str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                builder.append(str2)



                tv_contact_number.setText(builder, TextView.BufferType.SPANNABLE)

            }
           /* else {
                viewAllReturnList = AppDatabase.getDBInstance()!!.returnDetailsDao().getListAccordingToShopId(maddShopDataObj?.shop_id!!) as ArrayList<ReturnDetailsEntity>
                shopId = orderListObj?.shop_id!!
                shopName = orderListObj?.shop_name!!

                myshop_address_TV.text = orderListObj?.address

                val builder = SpannableStringBuilder()

                val str1 = SpannableString("Owner Contact Number: ")
                builder.append(str1)

                val str2 = SpannableString(orderListObj?.owner_contact_no)
                str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                builder.append(str2)

                tv_contact_number.setText(builder, TextView.BufferType.SPANNABLE)




                if (viewAllReturnList != null && viewAllReturnList?.size!! > 0) {
                    var amount = 0.0
                    for (i in viewAllReturnList?.indices!!) {
                        if (!TextUtils.isEmpty(viewAllReturnList?.get(i)?.amount))
                            amount += viewAllReturnList?.get(i)?.amount?.toDouble()!!
                    }

                    order_amount_tv.visibility = View.VISIBLE
                    val finalAmount = String.format("%.2f", amount.toFloat())

                    val builder_ = SpannableStringBuilder()

                    val str1_ = SpannableString("Total Order Amount: ")
                    builder_.append(str1_)

                    val str2_ = SpannableString("₹ $finalAmount")
                    str2_.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2_.length, 0)
                    builder_.append(str2_)

                    order_amount_tv.setText(builder_, TextView.BufferType.SPANNABLE)
                } else
                    order_amount_tv.visibility = View.GONE
            }*/

            myshop_name_TV.text = shopName
            tv_contact_number.text = "Owner Contact Number: "+shopContact

            val drawable = TextDrawable.builder()
                    .buildRoundRect(shopName.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

            shop_IV.setImageDrawable(drawable)


            if (viewAllReturnList != null && viewAllReturnList?.size!! > 0) {
                initAdapter(viewAllReturnList)
                no_shop_tv.visibility = View.GONE
            } else {
                no_shop_tv.visibility = View.VISIBLE
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initAdapter(viewAllReturnList: ArrayList<ReturnDetailsEntity>?) {

        ViewAllReturnListRecyclerViewAdapter = ViewAllReturnListRecyclerViewAdapter(mContext, viewAllReturnList,
                object : ViewAllReturnListRecyclerViewAdapter.onScrollEndListener {
                    override fun onScrollEnd() {
                    }

                }, object : ViewAllReturnListRecyclerViewAdapter.OnItemClickListener {


            override fun onLocationClick(position: Int) {
                if (!TextUtils.isEmpty(viewAllReturnList?.get(position)?.return_lat) && !TextUtils.isEmpty(viewAllReturnList?.get(position)?.return_long))
                    (mContext as DashboardActivity).openLocationMap(viewAllReturnList?.get(position)!!, false)
                else
                    (mContext as DashboardActivity).showSnackMessage("No Return location available")
            }

            override fun onSyncClick(position: Int) {
                if (AppUtils.isOnline(mContext)) {
                    val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
                    if (addShop.isUploaded)
                        syncAddReturnApi()
//                    else {
//                        syncShopFromSyncOption(addShop, viewAllOrderList?.get(position)?.shop_id, viewAllOrderList?.get(position)?.order_id, viewAllOrderList?.get(position)?.amount!!/*.substring(1)*/,
//                                viewAllOrderList[position].description!!, viewAllOrderList[position].collection!!/*.substring(1)*/,
//                                viewAllOrderList[position].date!!, viewAllOrderList[position].remarks, viewAllOrderList[position].signature,
//                                viewAllOrderList[position].order_lat, viewAllOrderList[position].order_long, viewAllOrderList[position])
//                    }
                } else
                    (mContext as DashboardActivity).showSnackMessage("Please check your internet connection")
            }

            override fun onViewClick(position: Int) {
                (mContext as DashboardActivity).loadFragment(FragType.ViewCartReturnFragment, true, viewAllReturnList?.get(position)!!)
            }
        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        return_list_rv.layoutManager = layoutManager
        return_list_rv.adapter = ViewAllReturnListRecyclerViewAdapter
    }

    fun syncAddReturnApi() {
        try {
            var returnList: ReturnRequest = ReturnRequest()
            var unsyncData = AppDatabase.getDBInstance()?.returnDetailsDao()!!.getAllUnsynced()
            if (unsyncData != null && unsyncData.isNotEmpty() && unsyncData.size != 0) {
                var i = 0
                returnList.user_id = Pref.user_id
                returnList.session_token = Pref.session_token
                returnList.shop_id = unsyncData?.get(i).shop_id
                returnList.return_id = unsyncData?.get(i).return_id
                returnList.latitude = unsyncData?.get(i).return_lat
                returnList.longitude = unsyncData?.get(i).return_long
                returnList.description = unsyncData?.get(i).description
                returnList.return_date_time = unsyncData?.get(i).date


                var returnProductList = AppDatabase.getDBInstance()?.returnProductListDao()?.getIDUnsynced(returnList?.return_id.toString())
                var reproductList: MutableList<ReturnProductList> = ArrayList()
                for (j in 0..returnProductList!!.size - 1) {
                    var obj = ReturnProductList()
                    obj.id = returnProductList.get(j).product_id.toString()
                    obj.product_name = returnProductList.get(j).product_name
                    obj.qty = returnProductList.get(j).qty
                    obj.rate = returnProductList.get(j).rate
                    obj.total_price = returnProductList.get(j).total_price
                    reproductList.add(obj)
                }
                returnList.return_list = reproductList

                val repository = AddOrderRepoProvider.provideAddOrderRepository()
                BaseActivity.compositeDisposable.add(
                        repository.addReturn(returnList)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    XLog.d("Return : RESPONSE " + result.status)
                                    if (result.status == NetworkConstant.SUCCESS) {
                                        AppDatabase.getDBInstance()?.returnDetailsDao()?.updateIsUploaded(true, returnList.return_id!!)
                                        syncAddReturnApi()
                                    }
                                }, { error ->
                                    if (error == null) {
                                        XLog.d("Return : ERROR " + "UNEXPECTED ERROR IN Add Return API")
                                    } else {
                                        XLog.d("Return : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
                                })
                )

            } else {

            }
        } catch (ex: Exception) {


        }


    }


    fun updateList() {
        viewAllReturnList = AppDatabase.getDBInstance()!!.returnDetailsDao().getListAccordingToShopId(shopId) as ArrayList<ReturnDetailsEntity>


        if (viewAllReturnList != null && viewAllReturnList?.size!! > 0) {
            initAdapter(viewAllReturnList)
            no_shop_tv.visibility = View.GONE
        } else {
            no_shop_tv.visibility = View.VISIBLE
        }

        try {
            if (viewAllReturnList != null && viewAllReturnList?.size!! > 0) {
                order_amount_tv.visibility = View.VISIBLE
                var amount = 0.0
                for (i in viewAllReturnList?.indices!!) {
                    if (!TextUtils.isEmpty(viewAllReturnList?.get(i)?.amount))
                        amount += viewAllReturnList?.get(i)?.amount?.toDouble()!!
                }
                val finalAmount = String.format("%.2f", amount.toFloat())

                val builder = SpannableStringBuilder()

                val str1 = SpannableString("Total Return Amount: ")
                builder.append(str1)

                val str2 = SpannableString("₹ $finalAmount")
                str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                builder.append(str2)

                order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)
            } else
                order_amount_tv.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
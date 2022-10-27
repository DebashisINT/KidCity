package com.kcteam.features.TA

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.domain.TaListDBModelEntity
import com.kcteam.app.domain.ViewAllOrderListEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.ImagePickerManager
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.TA.model.TaList
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.nearbyshops.presentation.ShopAddressUpdateListener
import com.kcteam.widgets.AppCustomTextView
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker
import com.pnikosis.materialishprogress.ProgressWheel
import java.io.File
import java.util.*


/**
 * Created by Pratishruti on 15-11-2017.
 */
class ViewAllTAListFragment : BaseFragment(), View.OnClickListener {


    private var ViewAllTAListRecyclerViewAdapter: ViewAllTAListRecyclerViewAdapter? = null
    private lateinit var order_list_rv: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_address_TV: AppCustomTextView
    private lateinit var order_amount_tv: AppCustomTextView
    private lateinit var no_data_available_tv: AppCustomTextView

    private lateinit var picker: HorizontalPicker
    private lateinit var ViewAllOrderListEntityList: ArrayList<ViewAllOrderListEntity>
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var add_ta_fb: FloatingActionButton
    private lateinit var mTaDialog: Dialog
    private lateinit var mAddTADialog: AddTADialog
    private var dialogFragment: AddTADialog = AddTADialog(true)
    var i: Int = 0
    private var mTalist: ArrayList<TaList> = ArrayList()

    private lateinit var mContext: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_view_all_ta_list, container, false)
        initView(view)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    companion object {
        var mShopActivityEntity: ShopActivityEntity? = null
        fun getInstance(objects: Any): ViewAllTAListFragment {
            val mViewAllOrderListFragment = ViewAllTAListFragment()
            if (objects is ShopActivityEntity) {
                mShopActivityEntity = objects
            }
            return mViewAllOrderListFragment
        }
    }

    private fun initView(view: View) {
        add_ta_fb = view.findViewById(R.id.add_ta_fb)
        order_list_rv = view.findViewById(R.id.order_list_rv)
        ViewAllOrderListEntityList = ArrayList()
        myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
        myshop_address_TV = view.findViewById(R.id.myshop_address_TV)
        order_amount_tv = view.findViewById(R.id.order_amount_tv)
        no_data_available_tv = view.findViewById(R.id.no_data_available_tv)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        val mTaList: TaList = TaList("2018-08-09", "2018-08-29", "10000", "na", "Approved")
        mTalist?.add(mTaList)
        val mTaList1: TaList = TaList("2018-08-09", "2018-08-29", "12000", "na", "Pending")
        mTalist?.add(mTaList1)
        val mTaList2: TaList = TaList("2018-08-09", "2018-08-29", "13000", "na", "Rejected")
        mTalist?.add(mTaList2)

        insertDataToDatabase(mTalist)

        if (mShopActivityEntity != null) {
            myshop_name_TV.setText(mShopActivityEntity?.shop_name)
            myshop_address_TV.setText(mShopActivityEntity?.shop_address)
            order_amount_tv.text = "Order Amount: ₹10,000"
        }
        //generateOrderListDate()
        //initAdapter(ta_list)
        add_ta_fb.setOnClickListener(this)
    }

    private fun generateOrderListDate() {
        for (i in 0..9) {
            var mViewAllOrderListEntity: ViewAllOrderListEntity = ViewAllOrderListEntity()
            mViewAllOrderListEntity.amount = "1000"
            mViewAllOrderListEntity.itemId = i
            mViewAllOrderListEntity.date = "03-Sep-18"
            ViewAllOrderListEntityList.add(mViewAllOrderListEntity)
        }
    }


    private fun insertDataToDatabase(ta_list: ArrayList<TaList>) {
        AppDatabase.getDBInstance()!!.taListDao().deleteAll()
        var list: MutableList<TaListDBModelEntity> = ArrayList()
        var shopObj = TaListDBModelEntity()
        for (i in 0 until ta_list.size) {
            shopObj.from_date = ta_list[i].fromDate
            shopObj.to_date = ta_list[i].toDate
            shopObj.amount = ta_list[i].amount
            shopObj.description = ta_list[i].description
            shopObj.status = ta_list[i].status
            list.add(shopObj)
            AppDatabase.getDBInstance()!!.taListDao().insert(shopObj)
        }

        initAdapter()
    }

    override fun onClick(p0: View?) {
        i = 0
        when (p0?.id) {
            R.id.add_ta_fb -> {
                AddTADialog(true)
            }
        }
    }


    private fun initAdapter() {
        var mTaList: ArrayList<TaListDBModelEntity> = AppDatabase.getDBInstance()!!.taListDao().getAll() as ArrayList<TaListDBModelEntity>

        Collections.sort(mTaList, byDate);

        if (ViewAllTAListRecyclerViewAdapter == null) {
            if (mTaList.size > 0)
                ViewAllTAListRecyclerViewAdapter = ViewAllTAListRecyclerViewAdapter(mContext, mTaList, object : ViewAllTAListRecyclerViewAdapter.onScrollEndListener {
                    override fun onScrollEnd() {
                    }

                }, object : ViewAllTAListRecyclerViewAdapter.onItemClickListener {
                    override fun onActionItemClick() {
                        AddTADialog(false)
                    }

                })
            else
                no_data_available_tv.visibility = View.GONE
        } else {
            if (mTaList.size > 0)
                ViewAllTAListRecyclerViewAdapter?.notifyAdapter(mTaList)
            else
                no_data_available_tv.visibility = View.GONE
        }
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        order_list_rv.layoutManager = layoutManager
        order_list_rv.adapter = ViewAllTAListRecyclerViewAdapter
    }


    val byDate: Comparator<TaListDBModelEntity> = object : Comparator<TaListDBModelEntity> {
        //        internal var sdf = TaListDBModelEntity("yyyy,MM,dd")
        override fun compare(p0: TaListDBModelEntity?, p1: TaListDBModelEntity?): Int {
            return if (AppUtils.getDateFormat(p0?.from_date!!).getTime() > AppUtils.getDateFormat(p1?.from_date!!).getTime()) 1 else -1
        }
    }

    private fun AddTADialog(action: Boolean): AddTADialog {
        try {
            dialogFragment = AddTADialog.getInstance(action, object : ShopAddressUpdateListener {
                override fun onAddedDataSuccess() {
                    initAdapter()
                }

                override fun getDialogInstance(mdialog: Dialog?) {
                    mTaDialog = mdialog!!
                }

                override fun onUpdateClick(address: AddShopDBModelEntity?) {
                    (mContext as DashboardActivity).showSnackMessage("Order added successfully")
                }

            })
            dialogFragment.show((mContext as DashboardActivity).supportFragmentManager, "AddOrderDialog")


        } catch (e: Exception) {
        }
        return dialogFragment
    }

    fun showPickedFileFromGalleryFetch(data: Intent?) {
        val filePath = ImagePickerManager.getImagePathFromData(data, mContext)
        val file = File(filePath)
        val strFileName: String = file.name
        if (mTaDialog != null && mTaDialog.isShowing)
            dialogFragment.showPickedFile(strFileName, filePath)
    }

    fun getCaptureImage(imG_URI: Uri) {
        val file = File(imG_URI.path)
        val strFileName: String = file.name
        if (mTaDialog != null && mTaDialog.isShowing)
            dialogFragment.showPickedFile(strFileName, imG_URI.path!!)
    }
}
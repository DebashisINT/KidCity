package com.kcteam.features.returnsOrder

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.domain.*
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.AppUtils.Companion.isAllSelect
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.AddShopRepositoryProvider
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.commondialogsinglebtn.CommonDialogSingleBtn
import com.kcteam.features.commondialogsinglebtn.OnDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.login.api.productlistapi.ProductListRepoProvider
import com.kcteam.features.login.model.productlistmodel.ProductListOfflineResponseModelNew
import com.kcteam.features.login.model.productlistmodel.ProductListResponseModel
import com.kcteam.features.login.model.productlistmodel.ProductRateDataModel
import com.kcteam.features.login.model.productlistmodel.ProductRateListResponseModel
import com.kcteam.features.stock.api.StockRepositoryProvider
import com.kcteam.features.stock.model.AddStockInputParamsModel
import com.kcteam.features.viewAllOrder.BrandListAdapter
import com.kcteam.features.viewAllOrder.CategoryListAdapter
import com.kcteam.features.viewAllOrder.ProductListAdapter
import com.kcteam.features.viewAllOrder.WattListAdapter
import com.kcteam.features.viewAllOrder.api.addorder.AddOrderRepoProvider
import com.kcteam.features.viewAllOrder.model.AddOrderInputParamsModel
import com.kcteam.features.viewAllOrder.model.AddOrderInputProductList
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ReturnTypeListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var rl_category_type_header: RelativeLayout
    private lateinit var tv_category_type: AppCustomTextView
    private lateinit var iv_category_type_dropdown: ImageView
    private lateinit var ll_category_type_list: LinearLayout
    private lateinit var rv_category_type_list: RecyclerView
    private lateinit var rl_brand_type_header: RelativeLayout
    private lateinit var tv_brand_type: AppCustomTextView
    private lateinit var iv_brand_type_dropdown: ImageView
    private lateinit var ll_brand_type_list: LinearLayout
    private lateinit var rv_brand_type_list: RecyclerView
    private lateinit var rv_product_type_list: RecyclerView
    private lateinit var rl_order_type_main: RelativeLayout
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var tv_shop_name: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rl_watt_type_header: RelativeLayout
    private lateinit var tv_watt_type: AppCustomTextView
    private lateinit var ll_watt_type_list: LinearLayout
    private lateinit var rv_watt_type_list: RecyclerView
    private lateinit var iv_watt_type_dropdown: ImageView
    private lateinit var et_grp_search: AppCustomEditText
    private lateinit var et_category_search: AppCustomEditText
    private lateinit var et_watt_search: AppCustomEditText
    private lateinit var tv_select_all: AppCompatTextView

    private var productEntity: ProductListEntity? = null
    private var selectedProductList = ArrayList<ProductListEntity>()
    private var shopId = ""
    private var productList: ArrayList<ProductListEntity>? = null
    private var brandAdapter: BrandListAdapter? = null
    private var categoryAdapter: CategoryListAdapter? = null
    private var wattAdapter: WattListAdapter? = null
    private lateinit var scroll: NestedScrollView
    private var isShopRegistrationInProcess = false

    private var productRateList: ArrayList<ProductRateDataModel>? = null
    private var productRateListDb: ArrayList<ProductRateEntity>? = null
    private var productAdapter: ProductListAdapter? = null
    private var isForDb = false

    companion object {

        private var ARG_SHOP_ID = "shop_id"

        fun newInstance(objects: Any): ReturnTypeListFragment {
            val fragment = ReturnTypeListFragment()
            val bundle = Bundle()
            bundle.putString(ARG_SHOP_ID, objects.toString())
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        shopId = arguments?.getString(ARG_SHOP_ID)!!
        (mContext as DashboardActivity).tv_cart_count.text = "0"
        (mContext as DashboardActivity).tv_cart_count.visibility = View.GONE

        val list = AppDatabase.getDBInstance()?.productRateDao()?.getAll() as ArrayList<ProductRateEntity>?
        if (list == null || list.isEmpty()) {
            getProductRateListOfflineApi(true)
        } else
            productRateListDb = list
    }

    private fun getProductRateListOfflineApi(isFromOnAttach: Boolean) {
        val repository = ProductListRepoProvider.productListProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getProductRateOfflineListNew()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ProductListOfflineResponseModelNew
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {
                                val productRateList = response.product_rate_list
                                if (productRateList != null && productRateList.size > 0) {
                                    if (!isFromOnAttach)
                                        AppDatabase.getDBInstance()?.productRateDao()?.deleteAll()
                                    doAsync {
                                        AppDatabase.getDBInstance()?.productRateDao()?.insertAll(productRateList)
                                        uiThread {
                                            productRateListDb = AppDatabase.getDBInstance()?.productRateDao()?.getAll() as ArrayList<ProductRateEntity>?
                                            progress_wheel.stopSpinning()
                                            if (!isFromOnAttach)
                                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.success_msg), 1000)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                    if (!isFromOnAttach)
                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                                }
                            } else {
                                progress_wheel.stopSpinning()

                                if (!isFromOnAttach)
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                            }

                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()

                            if (!isFromOnAttach)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                        })
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_order_type_list_new, container, false)
        initView(view)
        initClickListener()
        initTextChangeListener()



        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    if (productList != null && productList!!.size > 0) {
                        productAdapter?.updateList(productList!!)
                    }
                } else {
                    if (productList != null && productList!!.size > 0)
                        productAdapter?.filter?.filter(query)
                }
            }
        })


        return view
    }


    private fun getProductRateListApi() {

        if (!AppUtils.isOnline(mContext)) {
            if (Pref.isRateOnline)
                (mContext as DashboardActivity).showSnackMessage("Internet not connected. Product Rates will show as ZERO(0). You may put manually.", 10000)
            return
        }

        BaseActivity.isApiInitiated = true
        val repository = ProductListRepoProvider.productListProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getProductRateList(shopId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ProductRateListResponseModel
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.product_rate_list != null && response.product_rate_list!!.size > 0) {
                                    if (!isForDb) {
                                        progress_wheel.stopSpinning()
                                        productRateList = response.product_rate_list
                                        AppUtils.saveSharedPreferencesProductRateList(mContext, productRateList!!)

                                        if (Pref.isShowAllProduct) {
                                            productList = AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
                                            setProductAdapter(productList!!)
                                        }

                                    } else {
                                        doAsync {

                                            response.product_rate_list!!.forEach {
                                                val productRate = ProductRateEntity()
                                                AppDatabase.getDBInstance()?.productRateDao()?.insert(productRate.apply {
                                                    product_id = it.product_id
                                                    stock_amount = it.stock_amount
                                                    stock_unit = it.stock_unit
                                                    isStockShow = it.isStockShow
                                                    isRateShow = it.isRateShow
                                                })
                                            }

                                            uiThread {
                                                productRateListDb = AppDatabase.getDBInstance()?.productRateDao()?.getAll() as ArrayList<ProductRateEntity>?
                                                progress_wheel.stopSpinning()
                                                isForDb = false
                                            }
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                    if (!isForDb && Pref.isShowAllProduct) {
                                        productList = AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
                                        setProductAdapter(productList!!)
                                    }

                                    if (isForDb)
                                        isForDb = false
                                }
                            } else {
                                progress_wheel.stopSpinning()

                                if (Pref.isShowAllProduct) {
                                    productList = AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
                                    setProductAdapter(productList!!)
                                }

                                if (isForDb)
                                    isForDb = false
                            }


                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()

                            if (Pref.isShowAllProduct) {
                                productList = AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
                                setProductAdapter(productList!!)
                            }

                            if (isForDb)
                                isForDb = false
                        })
        )
    }

    private fun initView(view: View) {
        rl_category_type_header = view.findViewById(R.id.rl_category_type_header)
        tv_category_type = view.findViewById(R.id.tv_category_type)
        iv_category_type_dropdown = view.findViewById(R.id.iv_category_type_dropdown)
        ll_category_type_list = view.findViewById(R.id.ll_category_type_list)
        rv_category_type_list = view.findViewById(R.id.rv_category_type_list)
        rl_brand_type_header = view.findViewById(R.id.rl_brand_type_header)
        tv_brand_type = view.findViewById(R.id.tv_brand_type)
        iv_brand_type_dropdown = view.findViewById(R.id.iv_brand_type_dropdown)
        ll_brand_type_list = view.findViewById(R.id.ll_brand_type_list)
        rv_brand_type_list = view.findViewById(R.id.rv_brand_type_list)
        rv_product_type_list = view.findViewById(R.id.rv_product_type_list)
        rl_order_type_main = view.findViewById(R.id.rl_order_type_main)
        tv_no_data = view.findViewById(R.id.tv_no_data)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        tv_shop_name = view.findViewById(R.id.tv_shop_name)
        rl_watt_type_header = view.findViewById(R.id.rl_watt_type_header)
        tv_watt_type = view.findViewById(R.id.tv_watt_type)
        ll_watt_type_list = view.findViewById(R.id.ll_watt_type_list)
        rv_watt_type_list = view.findViewById(R.id.rv_watt_type_list)
        iv_watt_type_dropdown = view.findViewById(R.id.iv_watt_type_dropdown)
        et_grp_search = view.findViewById(R.id.et_grp_search)
        et_category_search = view.findViewById(R.id.et_category_search)
        et_watt_search = view.findViewById(R.id.et_watt_search)
        scroll = view.findViewById(R.id.scroll)
        tv_select_all = view.findViewById(R.id.tv_select_all)

        val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
        if (addShop != null && !TextUtils.isEmpty(addShop.shopName))
            tv_shop_name.text = addShop.shopName


        val list = AppDatabase.getDBInstance()?.productListDao()?.getUniqueBrandList() as ArrayList<ProductListEntity>

        val hashSet = HashSet<ProductListEntity>()
        hashSet.addAll(list)
        list.clear()
        list.addAll(hashSet)

        if (list != null && list.size > 0) {
            checkToShowAllProducts()
            setBrandAdapter(list)
        } else {
            if (AppUtils.isOnline(mContext))
                getProductList("", true)
            else
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }


    }

    private fun checkToShowAllProducts() {

        val list = AppUtils.loadSharedPreferencesProductRateList(mContext)

        if (Pref.isShowAllProduct) {
            tv_select_all.visibility = View.VISIBLE

            if (list == null || list.size == 0)
                getProductRateListApi()
            else {
                productList = AppDatabase.getDBInstance()?.productListDao()?.getAll() as ArrayList<ProductListEntity>?
                setProductAdapter(productList!!)
            }


        } else {
            tv_select_all.visibility = View.GONE


            if (list == null || list.size == 0)
                getProductRateListApi()

        }
    }

    private fun getProductList(date: String?, isFromInitView: Boolean) {

        if (!isFromInitView)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.wait_msg), 1000)

        val repository = ProductListRepoProvider.productListProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getProductList(Pref.session_token!!, Pref.user_id!!, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ProductListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.product_list

                                if (list != null && list.isNotEmpty()) {

                                    if (!isFromInitView)
                                        AppDatabase.getDBInstance()?.productListDao()?.deleteAllProduct()
                                    doAsync {

                                        AppDatabase.getDBInstance()?.productListDao()?.insertAll(list!!)
                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            val list_ = AppDatabase.getDBInstance()?.productListDao()?.getUniqueBrandList() as ArrayList<ProductListEntity>

                                            val hashSet = HashSet<ProductListEntity>()
                                            hashSet.addAll(list_)
                                            list_.clear()
                                            list_.addAll(hashSet)

                                            if (!isFromInitView)
                                                getProductRateListOfflineApi(false)

                                            checkToShowAllProducts()
                                            setBrandAdapter(list_)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()

                                    if (isFromInitView)
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    else
                                        getProductRateListOfflineApi(false)
                                }
                            } else if (response.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()

                                if (isFromInitView)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    getProductRateListOfflineApi(false)
                            } else {
                                progress_wheel.stopSpinning()
                                if (isFromInitView)
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()

                            if (isFromInitView)
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            else
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_msg), 1000)
                        })
        )
    }

    private fun initTextChangeListener() {
        et_grp_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                brandAdapter?.filter?.filter(et_grp_search.text.toString().trim())
            }
        })

        et_category_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                categoryAdapter?.filter?.filter(et_category_search.text.toString().trim())
            }
        })

        et_watt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                wattAdapter?.filter?.filter(et_watt_search.text.toString().trim())
            }
        })
    }

    private fun initClickListener() {
        rl_order_type_main.setOnClickListener(null)
        rl_category_type_header.setOnClickListener(this)
        rl_brand_type_header.setOnClickListener(this)
        rl_watt_type_header.setOnClickListener(this)
        tv_select_all.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {

            R.id.rl_category_type_header -> {
                if (!TextUtils.isEmpty(tv_brand_type.text.toString().trim())) {
                    if (iv_category_type_dropdown.isSelected) {
                        iv_category_type_dropdown.isSelected = false
                        ll_category_type_list.visibility = View.GONE
                    } else {
                        iv_category_type_dropdown.isSelected = true
                        ll_category_type_list.visibility = View.VISIBLE
                    }
                } else {
                    (mContext as DashboardActivity).showSnackMessage("Select Group first")
                }
            }


            R.id.rl_brand_type_header -> {
                if (iv_brand_type_dropdown.isSelected) {
                    iv_brand_type_dropdown.isSelected = false
                    ll_brand_type_list.visibility = View.GONE
                } else {
                    iv_brand_type_dropdown.isSelected = true
                    ll_brand_type_list.visibility = View.VISIBLE
                }

            }

            R.id.rl_watt_type_header -> {
                if (!TextUtils.isEmpty(tv_brand_type.text.toString().trim())) {
                    if (iv_watt_type_dropdown.isSelected) {
                        iv_watt_type_dropdown.isSelected = false
                        ll_watt_type_list.visibility = View.GONE
                    } else {
                        iv_watt_type_dropdown.isSelected = true
                        ll_watt_type_list.visibility = View.VISIBLE
                    }
                } else {
                    (mContext as DashboardActivity).showSnackMessage("Select Group first")
                }
            }

            R.id.tv_select_all -> {

                if (isAllSelect) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_added_all_items))
                    return
                }

                selectedProductList.clear()
                (mContext as DashboardActivity).qtyList.clear()
                (mContext as DashboardActivity).rateList.clear()
                (mContext as DashboardActivity).totalPrice.clear()

                for (j in productList?.indices!!) {
                    selectedProductList.add(productList?.get(j)!!)
                    (mContext as DashboardActivity).qtyList.add("0")

                    if (!Pref.isRateNotEditable)
                        (mContext as DashboardActivity).rateList.add("0.00")
                    else {
                        if (Pref.isRateOnline) {
                            if (productRateList != null && productRateList!!.size > 0)
                                (mContext as DashboardActivity).rateList.add(productRateList?.get(j)?.rate!!)
                            else
                                (mContext as DashboardActivity).rateList.add("0.00")
                        } else {
                            val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
                            if (productRateListDb != null && productRateListDb?.size!! > 0) {
                                when (shop.type) {
                                    "1" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(j)?.rate1!!)
                                    "2" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(j)?.rate2!!)
                                    "3" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(j)?.rate3!!)
                                    "4" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(j)?.rate4!!)
                                    "5" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(j)?.rate5!!)
                                    else -> {
                                        (mContext as DashboardActivity).rateList.add("0.00")
                                    }
                                }
                            } else
                                (mContext as DashboardActivity).rateList.add("0.00")
                        }
                    }
                    (mContext as DashboardActivity).totalPrice.add(0.00)

                    (mContext as DashboardActivity).tv_cart_count.text = selectedProductList.size.toString()
                    (mContext as DashboardActivity).tv_cart_count.visibility = View.VISIBLE
                }

                (mContext as DashboardActivity).showSnackMessage(getString(R.string.add_all_product_cart))

                if (!isAllSelect)
                    isAllSelect = true
            }
        }
    }


    private var productName = ""
    private fun setProductAdapter(mProductList: ArrayList<ProductListEntity>) {

        (mContext as DashboardActivity).searchView.closeSearch()

        if (mProductList != null && mProductList.size > 0) {
            tv_no_data.visibility = View.GONE
            scroll.visibility = View.GONE
        } else {
            tv_no_data.visibility = View.VISIBLE
        }

        rv_product_type_list.visibility = View.VISIBLE
        rv_product_type_list.layoutManager = LinearLayoutManager(mContext)
        productAdapter = ProductListAdapter(mContext, mProductList, productRateList, productRateListDb, shopId, object : ProductListAdapter.OnProductClickListener {
            override fun onProductClick(product: ProductListEntity?, adapterPosition: Int) {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                println("p_id "+product!!.id);
                for (i in selectedProductList.indices) {
                    if (selectedProductList[i].id == product?.id) {
                        (mContext as DashboardActivity).showSnackMessage("This product has already added to cart")
                        return
                    }
                }

                selectedProductList.add(product!!)
                (mContext as DashboardActivity).qtyList.add("0")

                if (!Pref.isRateNotEditable)
                    (mContext as DashboardActivity).rateList.add("0.00")
                else {
                    if (Pref.isRateOnline) {
                        if (productRateList != null && productRateList!!.size > 0) {
                            for (i in productRateList!!.indices) {
                                Log.e("Select Product", "Product Rate id========> " + productRateList!![i].product_id)
                                Log.e("Select Product", "Product id========> " + product.id)
                                if (productRateList!![i].product_id.toInt() == product.id) {
                                    (mContext as DashboardActivity).rateList.add(productRateList?.get(i)?.rate!!)
                                    break
                                }
                            }
                        } else
                            (mContext as DashboardActivity).rateList.add("0.00")
                    } else {
                        if (productRateListDb != null && productRateListDb!!.size > 0) {
                            val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
                            for (i in productRateListDb!!.indices) {
                                Log.e("Select Product", "Product Rate id Offline========> " + productRateListDb!![i].product_id)
                                Log.e("Select Product", "Product id Offline========> " + product.id)
                                if (productRateListDb!![i].product_id?.toInt() == product.id) {
                                    if (productRateListDb != null && productRateListDb?.size!! > 0) {
                                        when (shop.type) {
                                            "1" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(i)?.rate1!!)
                                            "2" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(i)?.rate2!!)
                                            "3" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(i)?.rate3!!)
                                            "4" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(i)?.rate4!!)
                                            "5" -> (mContext as DashboardActivity).rateList.add(productRateListDb?.get(i)?.rate5!!)
                                            else -> {
                                                (mContext as DashboardActivity).rateList.add("0.00")
                                            }
                                        }
                                    } else
                                        (mContext as DashboardActivity).rateList.add("0.00")
                                    break
                                }
                            }
                        } else
                            (mContext as DashboardActivity).rateList.add("0.00")
                    }
                }
                (mContext as DashboardActivity).totalPrice.add(0.00)

                (mContext as DashboardActivity).tv_cart_count.text = selectedProductList.size.toString()
                (mContext as DashboardActivity).tv_cart_count.visibility = View.VISIBLE
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.add_product_cart))

            }
        })

        rv_product_type_list.adapter = productAdapter
    }

    private fun setCategoryAdapter(categoryList: ArrayList<ProductListEntity>?) {
        rv_category_type_list.layoutManager = LinearLayoutManager(mContext)
        categoryAdapter = CategoryListAdapter(mContext, categoryList, object : CategoryListAdapter.OnCategoryClickListener {
            override fun onCategoryClick(category: ProductListEntity?, adapterPosition: Int) {
                tv_category_type.text = category?.category
                ll_category_type_list.visibility = View.GONE
                iv_category_type_dropdown.isSelected = false

                tv_watt_type.text = ""
                et_watt_search.setText("")


                val list = (AppDatabase.getDBInstance()?.productListDao()?.getAllValueAccordingToCategory(tv_category_type.text.toString().trim())
                        as ArrayList<ProductListEntity>?)!!

                var finalList = null

                val wattList = AppDatabase.getDBInstance()?.productListDao()?.getWattListBrandCategoryIdWise(category?.brand_id!!, category.category_id!!) as ArrayList<ProductListEntity>?

                if (wattList != null) {
                    val hashSet = HashSet<ProductListEntity>()
                    hashSet.addAll(wattList)
                    wattList.clear()
                    wattList.addAll(hashSet)

                    setWattAdapter(true, wattList)
                }

                productList = (AppDatabase.getDBInstance()?.productListDao()?.getAllValueAccordingToCategoryBrandId(category?.brand_id!!,
                        category.category_id!!) as ArrayList<ProductListEntity>?)!!

                setProductAdapter(productList!!)
            }
        })
        rv_category_type_list.adapter = categoryAdapter
    }

    private fun setBrandAdapter(brandList: ArrayList<ProductListEntity>) {
        rv_brand_type_list.layoutManager = LinearLayoutManager(mContext)

        brandAdapter = BrandListAdapter(mContext, brandList, object : BrandListAdapter.OnBrandClickListener {
            override fun onBrandClick(brand: ProductListEntity?, adapterPosition: Int) {
                tv_brand_type.text = brand?.brand
                ll_brand_type_list.visibility = View.GONE
                iv_brand_type_dropdown.isSelected = false

                tv_category_type.text = ""
                tv_watt_type.text = ""
                et_category_search.setText("")
                et_watt_search.setText("")


                val wattList = AppDatabase.getDBInstance()?.productListDao()?.getWattListBrandIdWise(brand?.brand_id!!) as ArrayList<ProductListEntity>?

                if (wattList != null) {
                    val hashSet = HashSet<ProductListEntity>()
                    hashSet.addAll(wattList)
                    wattList.clear()
                    wattList.addAll(hashSet)

                    setWattAdapter(false, wattList)
                }


                val categoryList = AppDatabase.getDBInstance()?.productListDao()?.getCategoryListAccordingToBrandId(brand?.brand_id!!) as ArrayList<ProductListEntity>?

                if (categoryList != null) {
                    val hashSet = HashSet<ProductListEntity>()
                    hashSet.addAll(categoryList)
                    categoryList.clear()
                    categoryList.addAll(hashSet)

                    setCategoryAdapter(categoryList)
                }

                productList = (AppDatabase.getDBInstance()?.productListDao()?.getAllValueAccordingToBrandId(brand?.brand_id!!) as ArrayList<ProductListEntity>)
                setProductAdapter(productList!!)
            }
        })
        rv_brand_type_list.adapter = brandAdapter
    }

    private fun setWattAdapter(isCategorySelected: Boolean, arrayList: ArrayList<ProductListEntity>?) {
        rv_watt_type_list.layoutManager = LinearLayoutManager(mContext)
        wattAdapter = WattListAdapter(mContext, arrayList, object : WattListAdapter.OnCategoryClickListener {
            override fun onCategoryClick(category: ProductListEntity?, adapterPosition: Int) {
                tv_watt_type.text = category?.watt
                ll_watt_type_list.visibility = View.GONE
                iv_watt_type_dropdown.isSelected = false


                productList = if (isCategorySelected) {
                    (AppDatabase.getDBInstance()?.productListDao()?.getAllValueAccordingToCategoryBrandFilteredByWattId(category?.brand_id!!,
                            category.category_id!!, category.watt_id!!) as ArrayList<ProductListEntity>?)!!
                } else {
                    (AppDatabase.getDBInstance()?.productListDao()?.getAllValueAccordingToBrandWattIdWise(category?.brand_id!!,
                            category.watt_id!!) as ArrayList<ProductListEntity>?)!!
                }

                setProductAdapter(productList!!)
            }
        })
        rv_watt_type_list.adapter = wattAdapter
    }


    private fun voiceOrderMsg() {
        if (Pref.isVoiceEnabledForOrderSaved) {
            val msg = "Hi, Return saved successfully."
            val speechStatus = (mContext as DashboardActivity).textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null)
            if (speechStatus == TextToSpeech.ERROR)
                Log.e("Add Return", "TTS error in converting Text to Speech!")

        }
    }
    /*Modified save order*/
    fun saveOrder(totalOrderValue: String, selectedProductList: ArrayList<ProductListEntity>?, totalPrice: java.util.ArrayList<Double>) {

        try {
            val addShop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
            if (addShop != null) {

                //if (addShop.isUploaded) {
                (mContext as DashboardActivity).isShowAlert = false
                doAsync {

                    val returnListDetails = ReturnDetailsEntity()
                    returnListDetails.amount = totalOrderValue
                    returnListDetails.description = ""


                    val random = Random()
                    val m = random.nextInt(9999 - 1000) + 1000


                    val list = AppDatabase.getDBInstance()!!.returnDetailsDao().getListAccordingDate(AppUtils.getCurrentDate())
                    if (list == null || list.isEmpty()) {
                        returnListDetails.return_id = Pref.user_id + AppUtils.getCurrentDateMonth() + "000001"
                    } else {
                        val lastId = list[/*list.size - 1*/0].return_id?.toLong()
                        val finalId = lastId!! + 1
                        returnListDetails.return_id = finalId.toString()
                    }
                    returnListDetails.shop_id = shopId
                    returnListDetails.date = AppUtils.getCurrentISODateTime()
                    returnListDetails.only_date = AppUtils.getCurrentDate()


                    val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shopId)

                    if (shopActivity != null) {
                        if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                            val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
                            returnListDetails.return_lat = shopDetail.shopLat.toString()
                            returnListDetails.return_long = shopDetail.shopLong.toString()
                        } else {
                            returnListDetails.return_lat  = Pref.current_latitude
                            returnListDetails.return_long= Pref.current_longitude
                        }
                    } else {
                        returnListDetails.return_lat = Pref.current_latitude
                        returnListDetails.return_long = Pref.current_longitude
                    }

                    AppDatabase.getDBInstance()!!.returnDetailsDao().insert(returnListDetails)

                    if (selectedProductList != null) {
                        for (i in (mContext as DashboardActivity).qtyList.indices) {
                            if ((mContext as DashboardActivity).qtyList[i].toInt() != 0) {

                                if (Pref.isRateNotEditable) {
                                    if (Pref.isRateOnline) {
                                        if (productRateList != null && productRateList!!.size > 0)
                                            insertOrderProductList(selectedProductList[i], returnListDetails, i, totalPrice[i])
                                        else {
                                            if ((mContext as DashboardActivity).rateList[i].toDouble() != 0.00)
                                                insertOrderProductList(selectedProductList[i], returnListDetails, i, totalPrice[i])
                                        }
                                    } else {
                                        if (productRateListDb != null && productRateListDb!!.size > 0)
                                            insertOrderProductList(selectedProductList[i], returnListDetails, i, totalPrice[i])
                                        else {
                                            if ((mContext as DashboardActivity).rateList[i].toDouble() != 0.00)
                                                insertOrderProductList(selectedProductList[i], returnListDetails, i, totalPrice[i])
                                        }
                                    }
                                } else {
                                    insertOrderProductList(selectedProductList[i], returnListDetails, i, totalPrice[i])
                                }
                            }
                        }
                    }

             /*       val orderList = AppDatabase.getDBInstance()!!.orderListDao().getListAccordingToShopID(shopId)
                    if (orderList == null || orderList.isEmpty()) {

                        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)

                        if (shop != null) {
                            val orderListEntity = OrderListEntity()

                            orderListEntity.address = shop.address
                            orderListEntity.order_amount = totalOrderValue
                            orderListEntity.owner_contact_no = shop.ownerContactNumber
                            orderListEntity.owner_name = shop.ownerName
                            orderListEntity.owner_email = shop.ownerEmailId
                            orderListEntity.pin_code = shop.pinCode
                            orderListEntity.shop_id = shop.shop_id
                            orderListEntity.shop_image_link = shop.shopImageUrl
                            orderListEntity.shop_lat = shop.shopLat.toString()
                            orderListEntity.shop_long = shop.shopLong.toString()
                            orderListEntity.shop_name = shop.shopName
                            orderListEntity.date = AppUtils.getCurrentDateForShopActi()
                            orderListEntity.date_long = AppUtils.convertDateStringToLong(AppUtils.getCurrentDateForShopActi())

                            AppDatabase.getDBInstance()!!.orderListDao().insert(orderListEntity)
                        }
                    } else {
                        AppDatabase.getDBInstance()!!.orderListDao().updateDate(AppUtils.getCurrentDateForShopActi(), shopId)
                        AppDatabase.getDBInstance()!!.orderListDao().updateDateLong(AppUtils.convertDateStringToLong(
                                AppUtils.getCurrentDateForShopActi()), shopId)
                    }

                    if (true) {
                        val obj = OrderStatusRemarksModelEntity()
                        obj.shop_id = shopId
                        obj.user_id = Pref.user_id
                        obj.order_status = "Success"
                        obj.order_remarks = "Successful Order"
                        obj.visited_date_time = AppUtils.getCurrentDateTime()
                        obj.visited_date = AppUtils.getCurrentDateForShopActi()
                        obj.isUploaded = false

                        var shopAll = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityAll()
                        if (shopAll.size == 1) {
                            obj.shop_revisit_uniqKey = shopAll.get(0).shop_revisit_uniqKey
                        } else if (shopAll.size != 0) {
                            obj.shop_revisit_uniqKey = shopAll.get(shopAll.size - 1).shop_revisit_uniqKey
                        }
                        if (shopAll.size != 0)
                            AppDatabase.getDBInstance()?.shopVisitOrderStatusRemarksDao()!!.insert(obj)
                    }*/


                    uiThread {
                        var lat = ""
                        var long = ""
                        AppUtils.isAllSelect = false
                        if (AppUtils.isOnline(mContext)) {

                            lat = if (TextUtils.isEmpty(returnListDetails.return_lat))
                                "0.0"
                            else
                                returnListDetails.return_lat!!

                            long = if (TextUtils.isEmpty(returnListDetails.return_long))
                                "0.0"
                            else
                                returnListDetails.return_long!!

                            if (addShop.isUploaded) {
                               callReturnApi()

                            } else {
//                                syncShop(addShop, orderListDetails.shop_id, orderListDetails.order_id, totalOrderValue,
//                                        "", "", orderListDetails.date!!, lat, long, "", orderListDetails.remarks,
//                                        orderListDetails.signature, orderListDetails)
                            }
                        } else {
                            (mContext as DashboardActivity).showSnackMessage("Return added successfully")
                            showCongratsAlert(returnListDetails.shop_id!!,returnListDetails.return_id!!)
                            voiceOrderMsg()
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun insertOrderProductList(objSelectedProductList: ProductListEntity, returnListDetails: ReturnDetailsEntity, i: Int, totalPrice: Double) {
        val returnOrderList = ReturnProductListEntity()
        returnOrderList.brand = objSelectedProductList.brand
        returnOrderList.brand_id = objSelectedProductList.brand_id
        returnOrderList.category_id = objSelectedProductList.category_id
        returnOrderList.watt = objSelectedProductList.watt
        returnOrderList.watt_id = objSelectedProductList.watt_id
        returnOrderList.product_id = objSelectedProductList.id.toString()
        returnOrderList.category = objSelectedProductList.category
        returnOrderList.return_id = returnListDetails.return_id
        returnOrderList.product_name = objSelectedProductList.product_name
        returnOrderList.qty = (mContext as DashboardActivity).qtyList[i]
        returnOrderList.rate = (mContext as DashboardActivity).rateList[i].toDouble().toString()
        returnOrderList.total_price = totalPrice.toString()
        returnOrderList.shop_id = shopId

        AppDatabase.getDBInstance()!!.returnProductListDao().insert(returnOrderList)
    }

    private fun addOrderApi(shop_id: String?, order_id: String?, amount: String, desc: String, collection: String, date: String?, order_lat: String?,
                            order_long: String?, remarks: String?, signature: String?, orderListDetails: OrderDetailsListEntity?) {

        val addOrder = AddOrderInputParamsModel()
        addOrder.collection = ""
        addOrder.description = ""
        addOrder.order_amount = amount
        addOrder.order_date = date
        addOrder.order_id = order_id
        addOrder.shop_id = shop_id
        addOrder.session_token = Pref.session_token
        addOrder.user_id = Pref.user_id
        addOrder.latitude = order_lat
        addOrder.longitude = order_long

        if (orderListDetails!!.scheme_amount != null)
            addOrder.scheme_amount = orderListDetails!!.scheme_amount
        else
            addOrder.scheme_amount = ""

        if (remarks != null)
            addOrder.remarks = remarks
        else
            addOrder.remarks = ""

        if (orderListDetails?.patient_name != null)
            addOrder.patient_name = orderListDetails.patient_name
        else
            addOrder.patient_name = ""

        if (orderListDetails?.patient_address != null)
            addOrder.patient_address = orderListDetails.patient_address
        else
            addOrder.patient_address = ""

        if (orderListDetails?.patient_no != null)
            addOrder.patient_no = orderListDetails.patient_no
        else
            addOrder.patient_no = ""

        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shopId)
        if (shopActivity != null) {
            if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)

                if (!TextUtils.isEmpty(shopDetail.address))
                    addOrder.address = shopDetail.address
                else
                    addOrder.address = ""
            } else {
                if (!TextUtils.isEmpty(order_lat) && !TextUtils.isEmpty(order_long))
                    addOrder.address = LocationWizard.getLocationName(mContext, order_lat!!.toDouble(), order_long!!.toDouble())
                else
                    addOrder.address = ""
            }
        } else {
            if (!TextUtils.isEmpty(order_lat) && !TextUtils.isEmpty(order_long))
                addOrder.address = LocationWizard.getLocationName(mContext, order_lat!!.toDouble(), order_long!!.toDouble())
            else
                addOrder.address = ""
        }

        /*06-01-2022*/
        if (orderListDetails?.Hospital != null)
            addOrder.Hospital = orderListDetails?.Hospital
        else
            addOrder.Hospital = ""

        if (orderListDetails?.Email_Address != null)
            addOrder.Email_Address = orderListDetails?.Email_Address
        else
            addOrder.Email_Address = ""

        val list = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToShopAndOrderId(order_id!!, shop_id!!)
        val productList = ArrayList<AddOrderInputProductList>()

        for (i in list.indices) {
            val product = AddOrderInputProductList()
            product.id = list[i].product_id
            product.qty = list[i].qty
            product.rate = list[i].rate
            product.total_price = list[i].total_price
            product.product_name = list[i].product_name
            product.scheme_qty = list[i].scheme_qty
            product.scheme_rate = list[i].scheme_rate
            product.total_scheme_price = list[i].total_scheme_price
            product.MRP = list[i].MRP
            productList.add(product)
        }

        addOrder.product_list = productList

        progress_wheel.spin()

        if (TextUtils.isEmpty(signature)) {
            val repository = AddOrderRepoProvider.provideAddOrderRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addNewOrder(addOrder)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, order_id)
                                }

                                (mContext as DashboardActivity).showSnackMessage("Order added successfully")
                                showCongratsAlert(shop_id, order_id)
                                voiceOrderMsg()
                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Order added successfully")
                                showCongratsAlert(shop_id, order_id)
                                voiceOrderMsg()
                            })
            )
        } else {
            val repository = AddOrderRepoProvider.provideAddOrderImageRepository()
            BaseActivity.compositeDisposable.add(
                    repository.addNewOrder(addOrder, signature!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val orderList = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (orderList.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()!!.orderDetailsListDao().updateIsUploaded(true, order_id)
                                }

                                (mContext as DashboardActivity).showSnackMessage("Order added successfully")
                                showCongratsAlert(shop_id, order_id)

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Order added successfully")
                                showCongratsAlert(shop_id, order_id)
                            })
            )
        }
    }

    private fun showCongratsAlert(shopId: String, orderId: String) {
        val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(shopId)
        val body = "${AppUtils.hiFirstNameText()}!. Your Return for " + shop?.shopName + " has been placed successfully. Return No. is $orderId"
        CommonDialogSingleBtn.getInstance("Congrats!", body, "OK", object : OnDialogClickListener {
            override fun onOkClick() {
                (mContext as DashboardActivity).onBackPressed()
            }
        }).show((mContext as DashboardActivity).supportFragmentManager, "CommonDialogSingleBtn")
    }


    private fun addStockApi(shopType: String, stock_id: String?, amount: String, date: String?, stock_lat: String?, stock_long: String?) {

        val addStock = AddStockInputParamsModel()
        addStock.stock_amount = amount
        addStock.stock_date_time = date
        addStock.stock_id = stock_id
        addStock.shop_id = shopId
        addStock.session_token = Pref.session_token
        addStock.user_id = Pref.user_id
        addStock.latitude = stock_lat
        addStock.longitude = stock_long
        addStock.shop_type = shopType

        val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().getShopActivityForId(shopId)

        if (shopActivity != null) {
            if (shopActivity.isVisited && !shopActivity.isDurationCalculated && shopActivity.date == AppUtils.getCurrentDateForShopActi()) {
                val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)

                if (!TextUtils.isEmpty(shopDetail.address))
                    addStock.address = shopDetail.address
                else
                    addStock.address = ""
            } else {
                if (!TextUtils.isEmpty(stock_lat) && !TextUtils.isEmpty(stock_long))
                    addStock.address = LocationWizard.getLocationName(mContext, stock_lat!!.toDouble(), stock_long!!.toDouble())
                else
                    addStock.address = ""
            }
        } else {
            if (!TextUtils.isEmpty(stock_lat) && !TextUtils.isEmpty(stock_long))
                addStock.address = LocationWizard.getLocationName(mContext, stock_lat!!.toDouble(), stock_long!!.toDouble())
            else
                addStock.address = ""
        }

        val list = AppDatabase.getDBInstance()!!.stockProductDao().getDataAccordingToShopAndStockId(stock_id!!, shopId)
        val productList = ArrayList<AddOrderInputProductList>()

        for (i in list.indices) {
            val product = AddOrderInputProductList()
            product.id = list[i].product_id
            product.qty = list[i].qty
            product.rate = list[i].rate
            product.total_price = list[i].total_price
            product.product_name = list[i].product_name
            productList.add(product)
        }

        addStock.product_list = productList

        val repository = StockRepositoryProvider.provideStockRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.addStock(addStock)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val orderList = result as BaseResponse
                            progress_wheel.stopSpinning()
                            if (orderList.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.stockDetailsListDao().updateIsUploaded(true, stock_id)
                            }

                            (mContext as DashboardActivity).showSnackMessage("Stock added successfully")
                            (mContext as DashboardActivity).onBackPressed()

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()

                            (mContext as DashboardActivity).showSnackMessage("Stock added successfully")
                            (mContext as DashboardActivity).onBackPressed()
                        })
        )
    }

    private fun syncShop(addShop: AddShopDBModelEntity, shop_id: String?, order_id: String?, amount: String, desc: String, collection: String,
                         currentDateForShopActi: String, order_lat: String?, order_long: String?, stock_id: String?,
                         remarks: String?, signature: String?, orderListDetails: OrderDetailsListEntity?) {
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
        addShopData.added_date = mAddShopDBModelEntity.added_date
        addShopData.assigned_to_pp_id = mAddShopDBModelEntity.assigned_to_pp_id
        addShopData.assigned_to_dd_id = mAddShopDBModelEntity.assigned_to_dd_id
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

        var uniqKeyObj = AppDatabase.getDBInstance()!!.shopActivityDao().getNewShopActivityKey(mAddShopDBModelEntity.shop_id, false)
        addShopData.shop_revisit_uniqKey = uniqKeyObj?.shop_revisit_uniqKey!!

        addShopData.project_name = mAddShopDBModelEntity.project_name
        addShopData.landline_number = mAddShopDBModelEntity.landline_number
        addShopData.agency_name = mAddShopDBModelEntity.agency_name

        addShopData.alternateNoForCustomer = mAddShopDBModelEntity.alternateNoForCustomer
        addShopData.whatsappNoForCustomer = mAddShopDBModelEntity.whatsappNoForCustomer

        // duplicate shop api call
        addShopData.isShopDuplicate=mAddShopDBModelEntity.isShopDuplicate

        addShopData.purpose=mAddShopDBModelEntity.purpose

        addShopData.GSTN_Number=mAddShopDBModelEntity.gstN_Number
        addShopData.ShopOwner_PAN=mAddShopDBModelEntity.shopOwner_PAN


        callAddShopApi(addShopData, mAddShopDBModelEntity.shopImageLocalPath, shop_id, order_id, amount, collection, currentDateForShopActi, desc, order_lat,
                order_long, stock_id, mAddShopDBModelEntity.doc_degree, remarks, signature, orderListDetails)
        //callAddShopApi(addShopData, "")
    }

    private fun callAddShopApi(addShop: AddShopRequestData, shop_imgPath: String?, shop_id: String?, order_id: String?, amount: String, collection: String,
                               currentDateForShopActi: String, desc: String, order_lat: String?, order_long: String?,
                               stock_id: String?, degree_imgPath: String?, remarks: String?, signature: String?,
                               orderListDetails: OrderDetailsListEntity?) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        if (isShopRegistrationInProcess)
            return

        progress_wheel.spin()

        isShopRegistrationInProcess = true

        XLog.d("=================SyncShop Input Params (Order)=====================")
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
        XLog.d("==================================================================")

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
                                        doAsync {
                                            val resultAs = runLongTask(addShop.shop_id)
                                            uiThread {
                                                if (resultAs == true) {
                                                    if (AppUtils.stockStatus == 0)
                                                        addOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi,
                                                                order_lat, order_long, remarks, signature, orderListDetails)
                                                    else if (AppUtils.stockStatus == 1)
                                                        addStockApi(addShop.type!!, stock_id, amount, currentDateForShopActi, order_lat, order_long)
                                                    else if (AppUtils.stockStatus == 2)
                                                        addReturnApi()
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
                                                    if (AppUtils.stockStatus == 0)
                                                        addOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi,
                                                                order_lat, order_long, remarks, signature, orderListDetails)
                                                    else if (AppUtils.stockStatus == 1)
                                                        addStockApi(addShop.type!!, stock_id, amount, currentDateForShopActi, order_lat, order_long)
                                                    else if (AppUtils.stockStatus == 2)
                                                    // add return api
                                                        addReturnApi()

                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)

                                        isShopRegistrationInProcess = false
                                    }
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
        } else {
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
                                                    if (AppUtils.stockStatus == 0)
                                                        addOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi,
                                                                order_lat, order_long, remarks, signature, orderListDetails)
                                                    else if (AppUtils.stockStatus == 1)
                                                        addStockApi(addShop.type!!, stock_id, amount, currentDateForShopActi, order_lat, order_long)
                                                    else if (AppUtils.stockStatus == 2)
                                                        addReturnApi()
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
                                                    if (AppUtils.stockStatus == 0)
                                                        addOrderApi(shop_id, order_id, amount, desc, collection, currentDateForShopActi,
                                                                order_lat, order_long, remarks, signature, orderListDetails)
                                                    else if (AppUtils.stockStatus == 1)
                                                        addStockApi(addShop.type!!, stock_id, amount, currentDateForShopActi, order_lat, order_long)
                                                    else if (AppUtils.stockStatus == 2)
                                                        addReturnApi()
                                                }
                                            }
                                        }
                                        isShopRegistrationInProcess = false

                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(addShopResult.message!!)

                                        isShopRegistrationInProcess = false
                                    }
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
            }
            catch(ex:Exception){
                shopDurationData.updated_on = ""
            }

            if (!TextUtils.isEmpty(shopActivity.pros_id!!))
                shopDurationData.pros_id = shopActivity.pros_id!!
            else
                shopDurationData.pros_id = ""

            if (!TextUtils.isEmpty(shopActivity.agency_name!!))
                shopDurationData.agency_name = shopActivity.agency_name!!
            else
                shopDurationData.agency_name = ""

            if (!TextUtils.isEmpty(shopActivity.approximate_1st_billing_value))
                shopDurationData.approximate_1st_billing_value = shopActivity.approximate_1st_billing_value!!
            else
                shopDurationData.approximate_1st_billing_value = ""

            shopDataList.add(shopDurationData)
        } else {
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


                shopDurationData.updated_by = Pref.user_id
                try{
                    shopDurationData.updated_on = shopActivity.updated_on!!
                }
                catch(ex:java.lang.Exception){
                    shopDurationData.updated_on = ""
                }


                if (!TextUtils.isEmpty(shopActivity.pros_id!!))
                    shopDurationData.pros_id = shopActivity.pros_id!!
                else
                    shopDurationData.pros_id = ""

                if (!TextUtils.isEmpty(shopActivity.agency_name!!))
                    shopDurationData.agency_name = shopActivity.agency_name!!
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

    fun goToNextScreen() {
        if ((mContext as DashboardActivity).tv_cart_count.text != "0")
            (mContext as DashboardActivity).loadFragment(FragType.CartReturnFragment, true, selectedProductList)
        else
            (mContext as DashboardActivity).showSnackMessage("No item is available in cart")
    }


    fun refreshProductList() {
        if (AppUtils.isOnline(mContext))
            getProductList("", false)
        else
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
    }


    fun addReturnApi() {
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
                                        AppDatabase.getDBInstance()?.returnDetailsDao()?.updateIsUploaded(true,returnList.return_id!!)
                                        addReturnApi()
                                    }
                                }, { error ->
                                    if (error == null) {
                                        XLog.d("Return : ERROR " + "UNEXPECTED ERROR IN Add Return API")
                                    } else {
                                        XLog.d("Return : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }
                                    //
                                })
                )

            } else {
            }
        } catch (ex: Exception) {

        }

    }
    private fun callReturnApi(){
        try{
            var returnList : ReturnRequest = ReturnRequest()
            var unsyncData= AppDatabase.getDBInstance()?.returnDetailsDao()!!.getAllUnsynced()
            if(unsyncData != null && unsyncData.isNotEmpty() && unsyncData.size!=0){
                var i=0
                returnList.user_id=Pref.user_id
                returnList.session_token=Pref.session_token
                returnList.shop_id=unsyncData?.get(i).shop_id
                returnList.return_id=unsyncData?.get(i).return_id
                returnList.latitude=unsyncData?.get(i).return_lat
                returnList.longitude=unsyncData?.get(i).return_long
                returnList.description=unsyncData?.get(i).description
                returnList.return_date_time=unsyncData?.get(i).date
                returnList.address=""
                returnList.return_amount=unsyncData?.get(i).amount


                var returnProductList= AppDatabase.getDBInstance()?.returnProductListDao()?.getIDUnsynced(returnList?.return_id.toString())
                var reproductList:MutableList<ReturnProductList> = ArrayList()
                for(j in 0..returnProductList!!.size-1){
                    var obj= ReturnProductList()
                    obj.id=returnProductList.get(j).product_id.toString()
                    obj.product_name=returnProductList.get(j).product_name
                    obj.qty=returnProductList.get(j).qty
                    obj.rate=returnProductList.get(j).rate
                    obj.total_price=returnProductList.get(j).total_price
                    reproductList.add(obj)
                }
                returnList.return_list=reproductList

                val repository = AddOrderRepoProvider.provideAddOrderRepository()
                BaseActivity.compositeDisposable.add(
                        repository.addReturn(returnList)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({ result ->
                                    XLog.d("Return : RESPONSE " + result.status)
                                    if (result.status == NetworkConstant.SUCCESS){
                                        AppDatabase.getDBInstance()?.returnDetailsDao()?.updateIsUploaded(true,returnList.return_id!!)
                                        callReturnApi()
                                    }
                                    else if(result.status == NetworkConstant.SESSION_MISMATCH) {
                                        (mContext as DashboardActivity).showSnackMessage(result.message!!)
                                    }
                                    (mContext as DashboardActivity).showSnackMessage("Return added successfully")
                                    showCongratsAlert(returnList.shop_id!!,returnList.return_id!!)
                                    voiceOrderMsg()
                                },{error ->
                                    if (error == null) {
                                        XLog.d("Return : ERROR " + "UNEXPECTED ERROR IN Add Return API")
                                    } else {
                                        XLog.d("Return : ERROR " + error.localizedMessage)
                                        error.printStackTrace()
                                    }

                                })
                )

            }else{

            }
        }catch (ex:Exception){


        }
    }
}
package com.kcteam.features.marketing.presentation

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kcteam.app.AppDatabase
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.themechangeapp.pickimage.PermissionHelper
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.MarketingDetailEntity
import com.kcteam.app.domain.MarketingDetailImageEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.marketing.api.deletemarketingimage.DeleteMarketingImageRepoProvider
import com.kcteam.features.marketing.api.marketingrequest.MarketingDetailSubmitRepoProvider
import com.kcteam.features.marketing.api.marketingresponse.GetMarketingDetailsRepoProvider
import com.kcteam.features.marketing.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Pratishruti on 23-02-2018.
 */
class MarketingPagerFragment : BaseFragment(), View.OnClickListener {


    private lateinit var marketing_viewpager: ViewPager
    private var pager_adapter: MarketingPagerAdapter? = null
    private var imageAdapter: MarketingImageAdapter? = null
    private lateinit var marketing_img_HRCV: RecyclerView
    private var retail_branding_list = ArrayList<MarketingDetailData>()
    private var pop_material = ArrayList<MarketingDetailData>()
    private var marketing_img_list = ArrayList<MarketingDetailImageData>()
    private lateinit var retail_branding_material_TV: AppCustomTextView
    private lateinit var pop_material_TV: AppCustomTextView
    private lateinit var save_TV: AppCustomTextView
    private lateinit var mContext: Context
    private var imgPos = 0
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel

    companion object {
        private lateinit var shopId: String
        fun getInstance(shopId: Any): MarketingPagerFragment {
            val marketingDetailFrag = MarketingPagerFragment()
            this.shopId = shopId as String
            return marketingDetailFrag
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_pager_marketing, container, false)
        initView(view)

        if (AppDatabase.getDBInstance()!!.marketingDetailDao().getMarketingDetailForShop(shopId).isEmpty() || !Pref.isMarketingImgSynched)
            getMarketingDetailsApi(shopId, Pref.user_id!!)
        else {
            var marketinglist = AppDatabase.getDBInstance()!!.marketingDetailDao().getMarketingDetailForShop(shopId)
            var imarketing_img_list = AppDatabase.getDBInstance()!!.marketingDetailImageDao().getImageDetailForShop(shopId)
            for (i in 0 until marketinglist.size) {
                var marketingDetailsData = MarketingDetailData()
                marketingDetailsData.material_name = marketinglist[i].material_name
                marketingDetailsData.material_id = marketinglist[i].material_id!!.toInt()
                if (marketinglist[i].date.isNullOrBlank())
                    marketingDetailsData.date = ""
                else
                    marketingDetailsData.date = marketinglist[i].date
                marketingDetailsData.shop_id = shopId
                marketingDetailsData.typeid = marketinglist[i].typeid
                if (marketinglist[i].typeid == "1")
                    retail_branding_list.add(marketingDetailsData)
                else
                    pop_material.add(marketingDetailsData)
            }

            for (i in 0 until imarketing_img_list.size) {
                var marketingImg = MarketingDetailImageData()
                marketingImg.shop_id = shopId
                marketingImg.image_id = imarketing_img_list[i].image_id
                marketingImg.image_url = imarketing_img_list[i].marketing_img
                marketing_img_list.add(i, marketingImg)
            }
            initAdapter()
        }

        return view
    }


    private fun getMarketingDetailsApi(shop_id: String, user_id: String) {
        progress_wheel.spin()
        var repository =  GetMarketingDetailsRepoProvider.provideMarketingDetail()
        BaseActivity.compositeDisposable.add(
                repository.getMarketingDetails(shop_id, user_id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            var marketingDetails = result as GetMarketingDetailsResponse
                            if (marketingDetails.status == NetworkConstant.SUCCESS) {
                                Pref.isMarketingImgSynched = true
                                convertDataAndSaveInDB(marketingDetails)
                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")
                            } else if (marketingDetails.status == NetworkConstant.NO_DATA) {
                                showAllCategoryList()
                            } else {
                                (mContext as DashboardActivity).showSnackMessage(result.message!!)
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }

    private fun showAllCategoryList() {
        var marketinglist = AppDatabase.getDBInstance()!!.marketingCategoryMasterDao().getAll()
        for (i in 0 until marketinglist.size) {
            var marketingDetailsData = MarketingDetailData()
            marketingDetailsData.material_name = marketinglist[i].material_name
            marketingDetailsData.material_id = marketinglist[i].material_id!!.toInt()
            marketingDetailsData.date = ""
            marketingDetailsData.typeid = marketinglist[i].type_id
            marketingDetailsData.shop_id = shopId
            if (marketinglist[i].type_id == "1")
                retail_branding_list.add(marketingDetailsData)
            else
                pop_material.add(marketingDetailsData)
            insertIntoTable(marketingDetailsData)
        }
//        Pref.isMarketingImgSynched = false
        initAdapter()
    }

    private fun insertIntoTable(marketingDetailsData: MarketingDetailData) {
        var marketingCatEntity = MarketingDetailEntity()
        marketingCatEntity.shop_id = shopId
        marketingCatEntity.material_id = marketingDetailsData.material_id.toString()
        marketingCatEntity.material_name =marketingDetailsData.material_name
        marketingCatEntity.date =marketingDetailsData.date
        marketingCatEntity.typeid = marketingDetailsData.typeid
        AppDatabase.getDBInstance()!!.marketingDetailDao().insertAll(marketingCatEntity)
    }


    private fun convertDataAndSaveInDB(marketingDetails: GetMarketingDetailsResponse) {
//        var material_details:List<GetMarketingDetailsData>
//        var marketing_img:List<GetMarketingDetailImageData>
        if(AppDatabase.getDBInstance()!!.marketingDetailDao().getMarketingDetailForShop(shopId).isEmpty()){
            var material_details = marketingDetails.material_details
            for (i in 0 until material_details.size) {
                var marketingCatEntity = MarketingDetailEntity()
                marketingCatEntity.shop_id = shopId
                marketingCatEntity.material_id = material_details[i].material_id.toString()
                marketingCatEntity.material_name = AppDatabase.getDBInstance()!!.marketingCategoryMasterDao().getMarketingCategoryNameFromId(material_details[i].material_id.toString())
                if (material_details[i].date == null || material_details[i].date!!.isBlank())
                    marketingCatEntity.date = ""
                else{
                    marketingCatEntity.date = AppUtils.changeAttendanceDateFormat((material_details[i].date!!))
                    material_details[i].date = AppUtils.changeAttendanceDateFormat((material_details[i].date!!))
                }
                marketingCatEntity.typeid = material_details[i].typeid
                if (material_details[i].typeid == "1")
                    retail_branding_list.add(material_details[i])
                else
                    pop_material.add(material_details[i])
                AppDatabase.getDBInstance()!!.marketingDetailDao().insertAll(marketingCatEntity)
            }
        }else{
            var marketinglist = AppDatabase.getDBInstance()!!.marketingDetailDao().getMarketingDetailForShop(shopId)
            for (i in 0 until marketinglist.size) {
                var marketingDetailsData = MarketingDetailData()
                marketingDetailsData.material_name = marketinglist[i].material_name
                marketingDetailsData.material_id = marketinglist[i].material_id!!.toInt()
                if (marketinglist[i].date.isNullOrBlank())
                    marketingDetailsData.date = ""
                else
                    marketingDetailsData.date = marketinglist[i].date
                marketingDetailsData.shop_id = shopId
                marketingDetailsData.typeid = marketinglist[i].typeid
                if (marketinglist[i].typeid == "1")
                    retail_branding_list.add(marketingDetailsData)
                else
                    pop_material.add(marketingDetailsData)
            }
        }

        var marketing_img = marketingDetails.marketing_img
        for (i in 0 until marketing_img.size) {
            marketing_img[i].shop_id = shopId
            var marketingImg = MarketingDetailImageEntity()
            marketingImg.shop_id = shopId
            marketingImg.marketing_img = marketing_img[i].image_url
            marketingImg.image_id = marketing_img[i].image_id
            if (AppDatabase.getDBInstance()!!.marketingDetailImageDao().getAllImageForId(shopId, marketing_img[i].image_id!!).isEmpty())
            AppDatabase.getDBInstance()!!.marketingDetailImageDao().insertAll(marketingImg)
            marketing_img_list.add(i, marketing_img[i])
        }
        initAdapter()
    }


    fun tabSelection(position: Int) {
        if (position == 0) {
            retail_branding_material_TV.isSelected = true
            pop_material_TV.isSelected = false
            marketing_viewpager.currentItem = 0
        } else {
            retail_branding_material_TV.isSelected = false
            pop_material_TV.isSelected = true
            marketing_viewpager.currentItem = 1
        }
    }


    private fun initView(view: View) {
        marketing_viewpager = view.findViewById(R.id.marketing_viewpager)
        marketing_img_HRCV = view.findViewById(R.id.marketing_img_HRCV)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        retail_branding_material_TV = view.findViewById(R.id.retail_branding_material_TV)
        save_TV = view.findViewById(R.id.save_TV)
        pop_material_TV = view.findViewById(R.id.pop_material_TV)
        retail_branding_material_TV.isSelected = true
        pop_material_TV.isSelected = false
        marketing_viewpager.currentItem = 0
        marketing_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    retail_branding_material_TV.isSelected = true
                    pop_material_TV.isSelected = false
                } else {
                    retail_branding_material_TV.isSelected = false
                    pop_material_TV.isSelected = true
                }
            }

        })
//        pager_adapter = MarketingPagerAdapter(fragmentManager, retail_branding_list, pop_material)
//        marketing_viewpager.adapter = pager_adapter
        var marketingImg = MarketingDetailImageData()
        marketingImg.image_url = ""
        marketingImg.shop_id = shopId
        marketing_img_list.add(marketingImg)

        val horizontalLayoutManagaer = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        marketing_img_HRCV.layoutManager = horizontalLayoutManagaer
//        imageAdapter = MarketingImageAdapter(mContext, marketing_img_list, object : RecyclerViewClickListener {
//            override fun getDeleteItemPosition(position: Int) {
//
//            }
//
//            override fun getPosition(position: Int) {
//                imgPos = position
//                showPictureDialog()
//            }
//        })
//        marketing_img_HRCV.adapter = imageAdapter
        marketing_img_HRCV.smoothScrollToPosition(marketing_img_list.size)

        retail_branding_material_TV.setOnClickListener(this)
        pop_material_TV.setOnClickListener(this)
        save_TV.setOnClickListener(this)


    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.retail_branding_material_TV -> {
                tabSelection(0)
            }
            R.id.pop_material_TV -> {
                tabSelection(1)
            }
            R.id.save_TV -> {
                submitMarketingDetail()
            }
        }
    }

    fun initAdapter() {
        pager_adapter = MarketingPagerAdapter(fragmentManager, retail_branding_list, pop_material)
        marketing_viewpager.adapter = pager_adapter

        imageAdapter = MarketingImageAdapter(mContext, marketing_img_list, object : RecyclerViewClickListener {
            override fun getDeleteItemPosition(position: Int) {
                openDeleteConfirmationDialog(position)

            }

            override fun getPosition(position: Int) {
                imgPos = position
                showPictureDialog()
            }
        })
        marketing_img_HRCV.adapter = imageAdapter


    }

    private fun openDeleteConfirmationDialog(position: Int) {
        CommonDialog.getInstance(getString(R.string.app_name), getString(R.string.alert_delete_marketing_img), getString(R.string.no), getString(R.string.yes), object : CommonDialogClickListener {
            override fun onLeftClick() {

            }

            override fun onRightClick(editableData: String) {
                if (AppUtils.isOnline(mContext)) {
                    if (marketing_img_list[position].image_url!!.contains("http")) {
                        callDeleteMarketingImageApi(marketing_img_list[position], position)
                    } else {
                        marketing_img_list.removeAt(position)
                        imageAdapter!!.notifyDataSetChanged()
                    }
                } else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))


            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "CommonDialog")

    }

    private fun callDeleteMarketingImageApi(marketingDetailImageData: MarketingDetailImageData, position: Int) {
        progress_wheel.spin()
        var repository = DeleteMarketingImageRepoProvider.provideDeleteMarketingImage()
        BaseActivity.compositeDisposable.add(
                repository.getMarketingCategoryList(Pref.user_id!!, marketingDetailImageData.shop_id!!, marketingDetailImageData.image_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            var marketingPagerResult = result as BaseResponse
                            if (marketingPagerResult.status == NetworkConstant.SUCCESS) {
//                                insertDataIntoDB(retail_branding_list, pop_material, marketing_img_list)
                                AppDatabase.getDBInstance()!!.marketingDetailImageDao().deleteMarketingImage(shopId, marketingDetailImageData.image_id!!)
                                marketing_img_list.removeAt(position)
                                imageAdapter!!.notifyDataSetChanged()
                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")
                            }else if(marketingPagerResult.status == NetworkConstant.NO_DATA) {
                                AppDatabase.getDBInstance()!!.marketingDetailImageDao().deleteMarketingImage(shopId, marketingDetailImageData.image_id!!)
                                marketing_img_list.removeAt(position)
                                imageAdapter!!.notifyDataSetChanged()
                                (mContext as DashboardActivity).showSnackMessage("NO DATA")
                            }else {
                                (mContext as DashboardActivity).showSnackMessage("NOT SUCCESS")
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )

    }

    fun setImage(imgRealPath: Uri) {
        var marketingImg = MarketingDetailImageData()
        marketingImg.image_url = imgRealPath.toString()
        marketing_img_list.add(imgPos, marketingImg)
        if (imageAdapter != null)
            imageAdapter!!.notifyDataSetChanged()
        marketing_img_HRCV.smoothScrollToPosition(marketing_img_list.size)
    }


    fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> selectImageInAlbum()
                        1 -> launchCamera()
                    }
                })
        pictureDialog.show()
    }

    fun launchCamera() {
        if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, (mContext as DashboardActivity).getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)
        }
    }

    fun selectImageInAlbum() {
        if (PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_STORAGE)

        }

    }

    fun submitMarketingDetail() {
        if (setDateOfMarketing()) {
            var marketingDetailSubmitReq = MarketingDetailSubmitRequest()
            marketingDetailSubmitReq.shop_id = shopId
            marketingDetailSubmitReq.user_id = Pref.user_id
            var marketing_details_list = AppDatabase.getDBInstance()!!.marketingDetailDao().getMarketingDetailForShop(shopId)
            var marketingDetailList: MutableList<MarketingDetailsSubmitData> = arrayListOf()
            for (i in 0 until marketing_details_list.size) {
                var marketingDetailSubmit = MarketingDetailsSubmitData()
                marketingDetailSubmit.material_id = marketing_details_list[i].material_id
                marketingDetailSubmit.date = marketing_details_list[i].date
                marketingDetailSubmit.typeid = marketing_details_list[i].typeid
                marketingDetailList.add(marketingDetailSubmit)
            }
            marketingDetailSubmitReq.marketing_detail = marketingDetailList

            var marketing_img: MutableList<MarketingDetailImageData> = arrayListOf()
            for (i in 0 until marketing_img_list.size) {
                if (marketing_img_list[i].image_url!!.contains("http") || marketing_img_list[i].image_url!!.isBlank())
                    continue
                var marketingImg = MarketingDetailImageData()
                marketingImg.image_url = marketing_img_list[i].image_url
                marketingImg.shop_id = shopId
                marketing_img.add(marketingImg)
            }
            callMarketingDetailsSubmitApi(marketingDetailSubmitReq, marketing_img)
        }

    }

    private fun callMarketingDetailsSubmitApi(marketingDetailSubmitReq: MarketingDetailSubmitRequest, marketing_img: MutableList<MarketingDetailImageData>) {

        if (marketing_img.isNotEmpty())
            Pref.isMarketingImgSynched = false
        progress_wheel.spin()
        var repository = MarketingDetailSubmitRepoProvider.providesMarketingDetailsSubmit()
        BaseActivity.compositeDisposable.add(
                repository.submitMarketingDetails(marketingDetailSubmitReq, marketing_img, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            var marketingPagerResult = result as BaseResponse
                            if (marketingPagerResult.status == NetworkConstant.SUCCESS) {
//                                insertDataIntoDB(retail_branding_list, pop_material, marketing_img_list)
                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")
                                (mContext as DashboardActivity).onBackPressed()
                            } else {
                                setDateToBlank()
                                (mContext as DashboardActivity).showSnackMessage(marketingPagerResult.message!!)
                            }
                        }, { error ->
                            setDateToBlank()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )

    }

    private fun insertDataIntoDB(retail_branding_list: ArrayList<MarketingDetailData>, pop_material: ArrayList<MarketingDetailData>, marketing_img_list: ArrayList<MarketingDetailImageData>) {
        for (i in 0 until retail_branding_list.size) {
            var marketingCatEntity = MarketingDetailEntity()
            marketingCatEntity.shop_id = shopId
            marketingCatEntity.material_id = retail_branding_list[i].material_id.toString()
            marketingCatEntity.material_name = retail_branding_list[i].material_name
            marketingCatEntity.date = retail_branding_list[i].date
            marketingCatEntity.typeid = "1"
            AppDatabase.getDBInstance()!!.marketingDetailDao().insertAll(marketingCatEntity)
        }
        for (i in 0 until pop_material.size) {
            var marketingCatEntity = MarketingDetailEntity()
            marketingCatEntity.shop_id = shopId
            marketingCatEntity.material_id = pop_material[i].material_id.toString()
            marketingCatEntity.material_name = pop_material[i].material_name
            marketingCatEntity.date = pop_material[i].date
            marketingCatEntity.typeid = "2"
            AppDatabase.getDBInstance()!!.marketingDetailDao().insertAll(marketingCatEntity)
        }

        for (i in 0 until marketing_img_list.size) {
            var marketingImg = MarketingDetailImageEntity()
            marketingImg.shop_id = shopId
            marketingImg.marketing_img = marketing_img_list[i].image_url
            marketingImg.image_id = marketing_img_list[i].image_id
            AppDatabase.getDBInstance()!!.marketingDetailImageDao().insertAll(marketingImg)
        }

    }



    private fun setDateOfMarketing(): Boolean {

        for (i in 0 until retail_branding_list.size) {
            if (retail_branding_list[i].isChecked) {
                var marketing = retail_branding_list[i]
                var count=AppDatabase.getDBInstance()!!.marketingDetailDao().setMarketingDetailDate(AppUtils.getCurrentDateChanged(), retail_branding_list[i].material_id!!.toString(), shopId)
            }

        }
        for (i in 0 until pop_material.size) {
            if (pop_material[i].isChecked) {
                var marketing = pop_material[i]
                var count=AppDatabase.getDBInstance()!!.marketingDetailDao().setMarketingDetailDate(AppUtils.getCurrentDateChanged(), pop_material[i].material_id!!.toString(), shopId)
            }

        }

//        for (i in 0 until marketing_img_list.size) {
//            if (!marketing_img_list[i].image_url!!.contains("http")){
//                var marketingImg = MarketingDetailImageEntity()
//                marketingImg.shop_id = shopId
//                marketingImg.marketing_img = marketing_img_list[i].image_url
//                marketingImg.image_id= marketing_img_list[i].image_id
//                AppDatabase.getDBInstance()!!.marketingDetailImageDao().insertAll(marketingImg)
//            }
//        }

        return true
    }

    private fun setDateToBlank(): Boolean {

        for (i in 0 until retail_branding_list.size) {
            if (retail_branding_list[i].isChecked) {
                AppDatabase.getDBInstance()!!.marketingDetailDao().setMarketingDetailDate("", retail_branding_list[i].material_id!!.toString(), shopId)
            }

        }
        for (i in 0 until pop_material.size) {
            if (pop_material[i].isChecked) {
                AppDatabase.getDBInstance()!!.marketingDetailDao().setMarketingDetailDate("", pop_material[i].material_id!!.toString(), shopId)
            }

        }
        return true
    }

}
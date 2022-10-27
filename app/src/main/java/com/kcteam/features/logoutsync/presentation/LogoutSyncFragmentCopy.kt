package com.kcteam.features.logoutsync.presentation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.xmlmodel.UserLocationList
import com.kcteam.app.xmlmodel.UserRevisitList
import com.kcteam.app.xmlmodel.XMLRootData
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.location.UserLocationDataEntity
import com.kcteam.features.location.model.ShopDurationRequestData
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.util.*

/**
 * Created by Kinsuk on 14-01-2019.
 */
class LogoutSyncFragmentCopy : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var addShopTickImg: AppCompatImageView
    private lateinit var addShopSyncImg: AppCompatImageView
    //private lateinit var addShopRetryImg: AppCompatImageView

    private lateinit var addOrderTickImg: AppCompatImageView
    private lateinit var addOrderSyncImg: AppCompatImageView
    //private lateinit var addOrderRetryImg: AppCompatImageView

    private lateinit var collectionTickImg: AppCompatImageView
    private lateinit var collectionSyncImg: AppCompatImageView
    //private lateinit var collectionRetryImg: AppCompatImageView

    private lateinit var gpsTickImg: AppCompatImageView
    private lateinit var gpsSyncImg: AppCompatImageView
    //private lateinit var gpsRetryImg: AppCompatImageView

    private lateinit var revisitTickImg: AppCompatImageView
    private lateinit var revisitSyncImg: AppCompatImageView
    //private lateinit var revisitRetryImg: AppCompatImageView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel

    private var i = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater!!.inflate(R.layout.fragment_logout_sync, container, false)

        initView(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun initView(view: View) {

        addShopTickImg = view.findViewById(R.id.add_shop_tick_img)
        addShopSyncImg = view.findViewById(R.id.add_shop_sync_img)
        //addShopRetryImg = view.findViewById(R.id.retry_add_shop_img)

        addOrderTickImg = view.findViewById(R.id.add_order_tick_img)
        addOrderSyncImg = view.findViewById(R.id.add_order_sync_img)
       // addOrderRetryImg = view.findViewById(R.id.retry_add_order_img)

        collectionTickImg = view.findViewById(R.id.collection_tick_img)
        collectionSyncImg = view.findViewById(R.id.collection_sync_img)
        //collectionRetryImg = view.findViewById(R.id.retry_collection_img)

        gpsTickImg = view.findViewById(R.id.gps_tick_img)
        gpsSyncImg = view.findViewById(R.id.gps_sync_img)
        //gpsRetryImg = view.findViewById(R.id.retry_gps_img)

        revisitTickImg = view.findViewById(R.id.revisit_tick_img)
        revisitSyncImg = view.findViewById(R.id.revisit_sync_img)
       // revisitRetryImg = view.findViewById(R.id.retry_revisit_img)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()


        animateSyncImage(addShopSyncImg)
        animateSyncImage(addOrderSyncImg)
        animateSyncImage(collectionSyncImg)
        animateSyncImage(gpsSyncImg)
        animateSyncImage(revisitSyncImg)


        //val xmlMapper = XmlMapper()
        // val strObject = xmlMapper.writeValueAsString(getLocationListData()[0])
        // val strObject1 = xmlMapper.writeValueAsString(checkToCallVisitShopApi()[0])
        val xmlRootData = XMLRootData()
        val userLocationList = UserLocationList()
        val userRevisitList = UserRevisitList()

        userLocationList.userLocationDataEntities = getLocationListData()
        userRevisitList.userRevisitDataEntities = checkToCallVisitShopApi()

        xmlRootData.userLocationList = userLocationList
        xmlRootData.userRevisitList = userRevisitList

        //val strObject1 = xmlMapper.writeValueAsString(xmlRootData)
        //Log.d("WASIMMMM", "WASIMMMM" + strObject1)
        // Log.d("WASIMMMM", "WASIMMMM::"+checkToCallVisitShopApi().size)

        //
    }

    private fun getLocationListData(): List<UserLocationDataEntity> {
        return AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationNotUploaded(false)
    }

    private fun checkToCallVisitShopApi(): List<ShopDurationRequestData> {
        /* Get all the shop list that has been synched successfully*/

        val syncedShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(true)
        val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()

        if (syncedShopList != null && syncedShopList.size > 0) {

            for (k in 0 until syncedShopList.size) {

                /* Get shop activity that has completed time duration calculation*/
                val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(syncedShopList[k].shop_id, true, false)
                if (shopActivity != null) {
                    val shopDurationData = ShopDurationRequestData()
                    shopDurationData.shop_id = shopActivity?.shopid
                    shopDurationData.spent_duration = shopActivity?.duration_spent
                    shopDurationData.visited_date = shopActivity?.visited_date
                    shopDurationData.visited_time = shopActivity?.visited_date

                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity?.shopid) != null) {
                        shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity?.shopid).totalVisitCount
                    } else {
                        shopDurationData.total_visit_count = "1"
                    }
                    val unSyncImage = AppDatabase.getDBInstance()!!.shopVisitImageDao().getUnSyncedData(false, shopActivity?.shopid!!)

                    //shopDurationData.base_64_image = encoder(unSyncImage.shop_image!!)
                    shopDataList.add(shopDurationData)
                }
            }

        }

        return shopDataList;
    }


    private fun writeDataToFile(list: List<UserLocationDataEntity>) {
        val company = JSONArray()

        for (i in 0 until list.size) {
            if (list[i].latitude == null || list[i].longitude == null)
                continue
            val jsonObject = JSONObject()
            jsonObject.put("date", list[i].updateDateTime)
            jsonObject.put("distance_covered", list[i].distance)
            jsonObject.put("last_update_time", list[i].time + " " + list[i].meridiem)
            jsonObject.put("latitude", list[i].latitude)
            jsonObject.put("longitude", list[i].longitude)
            jsonObject.put("locationId", list[i].locationId)
            jsonObject.put("location_name", list[i].locationName)
            jsonObject.put("shops_covered", list[i].shops)
            company.put(jsonObject)
        }

        val parentObject = JSONObject()
        parentObject.put("location_details", company)

        try {
            var output: Writer? = null
            val folderPath = FTStorageUtils.getFolderPath(mContext)
            val file = File(folderPath + "/FTS_Location_" + System.currentTimeMillis() + ".txt")
            output = BufferedWriter(FileWriter(file))
            output.write(parentObject.toString())
            output.close()
            Log.e("location", "Value saved")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun animateSyncImage(icon: AppCompatImageView) {
        icon.animation = AnimationUtils.loadAnimation(mContext, R.anim.rotation_sync)
        icon.startAnimation(icon.animation)
    }

    fun encoder(filePath: String): String {
        val bytes = File(filePath).readBytes()
        val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
        return base64
    }
}
package com.kcteam.features.location

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.text.TextUtils
import android.util.Log
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.NotificationUtils
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by aranya on 15/1/18.
 */
class GeofenceTransitionsJobIntentService : JobIntentService() {


    private val TAG = "GeofenceTransitionsIS"

    private val CHANNEL_ID = "channel_01"


    companion object {
        private const val JOB_ID = 573
        public fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, GeofenceTransitionsJobIntentService::class.java, JOB_ID, intent)
        }
    }

    /**
     * Convenience method for enqueuing work in to this service.
     */


    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    override fun onHandleWork(intent: Intent) {
        XLog.d("Geofence: GeofenceTransitionsJobIntentService : ENTRY")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        try {
            if (geofencingEvent!!.hasError()) {
//            val errorMessage = GeofenceErrorMessages.getErrorString(this,
//                    geofencingEvent.errorCode)
                Log.e(TAG, "${geofencingEvent!!.errorCode}")
                return
            }
        }catch (ex:Exception){
            return
        }


        // Get the transition type.
        val geofenceTransition = geofencingEvent!!.geofenceTransition

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
            val geofenceTransitionDetails = getGeofenceTransitionDetails(geofenceTransition,
                    triggeringGeofences!!)
            triggeringGeofences?.forEach {
                //                it.requestId
                // Send notification and log the transition details.
                XLog.d("=====================Geofence=======================")
                when (geofenceTransition) {
                    Geofence.GEOFENCE_TRANSITION_ENTER -> {
                        XLog.d("Geofence: GeofenceTransitionsJobIntentService : ENTER")
                        if (!TextUtils.isEmpty(Pref.user_id) && !Pref.isAutoLogout)
                            sendNotification(it.requestId)
                    }
                    Geofence.GEOFENCE_TRANSITION_EXIT -> {
                        XLog.d("Geofence: GeofenceTransitionsJobIntentService : EXIT")
                        cancelNotification(it.requestId)
                        endShopDuration(it.requestId)
                    }
                    Geofence.GEOFENCE_TRANSITION_DWELL -> {
                        XLog.d("Geofence: GeofenceTransitionsJobIntentService : DWELL")
                        if (!TextUtils.isEmpty(Pref.user_id) && !Pref.isAutoLogout)
                            sendNotification(it.requestId)
//                        calculateShopDuration(it.requestId)
                    }
                }
            }


            XLog.e(TAG, geofenceTransitionDetails)
        } else {
            // Log the error.
            XLog.e(TAG, "Invalid Type $geofenceTransition")
        }
        XLog.d("Geofence: GeofenceTransitionsJobIntentService : EXIT")
    }


    fun cancelNotification(shopId: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(shopId.hashCode())
    }

    private fun endShopDuration(requestId: String?) {
        val shopActiList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(requestId!!, AppUtils.getCurrentDateForShopActi())
        if (shopActiList.isEmpty())
            return
        XLog.d("Geofence: FarFromShop : " + "ShopName : " + shopActiList[0].shop_name!!)

        if (!Pref.isMultipleVisitEnable) {
            if (!shopActiList[0].isDurationCalculated && !shopActiList[0].isUploaded) {
                Pref.durationCompletedShopId = shopActiList[0].shopid!!
                val endTimeStamp = System.currentTimeMillis().toString()
                val startTimestamp = shopActiList[0].startTimeStamp

                val duration = AppUtils.getTimeFromTimeSpan(startTimestamp, endTimeStamp)
                val totalMinute = AppUtils.getMinuteFromTimeStamp(startTimestamp, endTimeStamp)

                AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, shopActiList[0].shopid!!, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, shopActiList[0].shopid!!, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActiList[0].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActiList[0].shopid!!, duration, AppUtils.getCurrentDateForShopActi())
                AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopActiList[0].shopid!!, AppUtils.getCurrentDateForShopActi())
//            callShopDurationApi(requestId)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), shopActiList[0].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActiList[0].startTimeStamp)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(LocationWizard.getNewLocationName(this, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()), shopActiList[0].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActiList[0].startTimeStamp)

                val netStatus = if (AppUtils.isOnline(this))
                    "Online"
                else
                    "Offline"

                val netType = if (AppUtils.getNetworkType(this).equals("wifi", ignoreCase = true))
                    AppUtils.getNetworkType(this)
                else
                    "Mobile ${AppUtils.mobNetType(this)}"

                AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                        AppUtils.getBatteryPercentage(this).toString(), netStatus, netType.toString(), shopActiList[0].shopid!!, AppUtils.getCurrentDateForShopActi())

//                AppUtils.isShopVisited = false
                Pref.isShopVisited=false

                if (Pref.willShowShopVisitReason && totalMinute.toInt() <= Pref.minVisitDurationSpentTime.toInt()) {
                    Pref.isShowShopVisitReason = true

                    val intent = Intent()
                    intent.action = "REVISIT_REASON_BROADCAST"
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                }
            }
        }
        else {
            shopActiList.forEach {
                if (!it.isDurationCalculated && !it.isUploaded) {
                    Pref.durationCompletedShopId = it.shopid!!
                    val endTimeStamp = System.currentTimeMillis().toString()
                    val startTimestamp = it.startTimeStamp

                    val duration = AppUtils.getTimeFromTimeSpan(startTimestamp, endTimeStamp)
                    val totalMinute = AppUtils.getMinuteFromTimeStamp(startTimestamp, endTimeStamp)

                    AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, it.shopid!!, AppUtils.getCurrentDateForShopActi(), startTimestamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, it.shopid!!, AppUtils.getCurrentDateForShopActi(), startTimestamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(it.shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), startTimestamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(it.shopid!!, duration, AppUtils.getCurrentDateForShopActi(), startTimestamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, it.shopid!!, AppUtils.getCurrentDateForShopActi(), startTimestamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), it.shopid!!, AppUtils.getCurrentDateForShopActi(), it.startTimeStamp)
                    AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(LocationWizard.getNewLocationName(this, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()), it.shopid!!, AppUtils.getCurrentDateForShopActi(), it.startTimeStamp)

                    val netStatus = if (AppUtils.isOnline(this))
                        "Online"
                    else
                        "Offline"

                    val netType = if (AppUtils.getNetworkType(this).equals("wifi", ignoreCase = true))
                        AppUtils.getNetworkType(this)
                    else
                        "Mobile ${AppUtils.mobNetType(this)}"

                    AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                            AppUtils.getBatteryPercentage(this).toString(), netStatus, netType.toString(), it.shopid!!, AppUtils.getCurrentDateForShopActi(), startTimestamp)

//                    AppUtils.isShopVisited = false

                    Pref.isShopVisited=false
                    if (Pref.willShowShopVisitReason && totalMinute.toInt() <= Pref.minVisitDurationSpentTime.toInt()) {
                        Pref.isShowShopVisitReason = true

                        val intent = Intent()
                        intent.action = "REVISIT_REASON_BROADCAST"
                        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                    }
                }
            }
        }

    }

    private fun calculateShopDuration(requestId: String?) {
        var shopActiList = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(requestId!!, AppUtils.getCurrentDateForShopActi())
        if (shopActiList.isEmpty())
            return
        if (shopActiList[0].isDurationCalculated)
            return
        for (i in 0 until shopActiList.size) {
            var totalMinute = AppUtils.getMinuteFromTimeStamp(shopActiList[i].startTimeStamp, System.currentTimeMillis().toString())
            //If duration is greater than 20 hour then stop incrementing
            if (totalMinute.toInt() > 20 * 60) {
                AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopActiList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                return
            }
            XLog.d("Geofence: ShopDurationIncrement : " + "ShopName : " + shopActiList[i].shop_name + "," + shopActiList[i].duration_spent)
            AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActiList[i].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
            var duration = AppUtils.getTimeFromTimeSpan(shopActiList[i].startTimeStamp, System.currentTimeMillis().toString())
            AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActiList[i].shopid!!, duration, AppUtils.getCurrentDateForShopActi())
        }

    }

    private fun callShopDurationApi(shopId: String) {

        if (Pref.user_id.isNullOrEmpty())
            return
        /* Get all the shop list that has been synched successfully*/
        var shopObj = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)
        if (!shopObj.isUploaded)
            return

        var shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
        /* Get shop activity that has completed time duration calculation*/
        var shopActivity: ShopActivityEntity? = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(shopId, true, false)
                ?: return
        var shopDurationData = ShopDurationRequestData()
        shopDurationData.shop_id = shopActivity!!.shopid
        shopDurationData.spent_duration = shopActivity!!.duration_spent
        shopDurationData.visited_date = shopActivity!!.visited_date
        shopDurationData.visited_time = shopActivity!!.visited_date
        shopDurationData.distance_travelled = shopActivity.distance_travelled
        if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity!!.shopid) != null)
            shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity!!.shopid).totalVisitCount
        else
            shopDurationData.total_visit_count = "1"

        if (!TextUtils.isEmpty(shopActivity.feedback))
            shopDurationData.feedback = shopActivity.feedback
        else
            shopDurationData.feedback = ""

        shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
        shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc
        shopDurationData.next_visit_date = shopActivity.next_visit_date
        shopDataList.add(shopDurationData)

        if (shopDataList.isEmpty()) {
            return
        }

        var shopDurationApiReq = ShopDurationRequest()
        shopDurationApiReq.user_id = Pref.user_id
        shopDurationApiReq.session_token = Pref.session_token
        shopDurationApiReq.shop_list = shopDataList

        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()
        repository.shopDuration(shopDurationApiReq)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    if (result.status == NetworkConstant.SUCCESS) {
                        for (i in 0 until shopDataList.size) {
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.getCurrentDateForShopActi())
                        }
                    }

                }, { error ->
                    error.printStackTrace()
                })


    }


    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private fun getGeofenceTransitionDetails(
            geofenceTransition: Int,
            triggeringGeofences: List<Geofence>): String {

        val geofenceTransitionString = getTransitionString(geofenceTransition)

        // Get the Ids of each geofence that was triggered.
        val triggeringGeofencesIdsList = arrayListOf<String>()
        for (geofence in triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)

        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private fun sendNotification(shopId: String) {
        val list = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(shopId, AppUtils.getCurrentDateForShopActi())
        var isDurationCalculated = false
        var isVisited = false
        var shopName = ""
        if (list.isEmpty()) {
            if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId) == null)
                return
            shopName = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId).shopName
        } else {
            isDurationCalculated = list[0].isDurationCalculated
            isVisited = list[0].isVisited
            shopName = list[0].shop_name!!
        }
        XLog.d("Geofence: ENTER : ShopName : $shopName,IS_DURATION_CALCULATED : $isDurationCalculated")

        if (!Pref.isMultipleVisitEnable) {
            if (isDurationCalculated || isVisited)
                return
        }
        else {
           if (list.isNotEmpty()) {
               for (i in list.indices) {
                   if (!list[i].isDurationCalculated)
                       return
               }
           }
        }

        XLog.d("Geofence: NearToShop : ShopName : $shopName")
        // Get an instance of the Notification manager
        val notification = NotificationUtils(getString(R.string.app_name), shopName, shopId, "")
        notification.CreateNotification(this, shopId)
//        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private fun getTransitionString(transitionType: Int): String {
        return when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "Enter"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "Exit"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "Dwell"
            else -> "Invalid"
        }
    }
}
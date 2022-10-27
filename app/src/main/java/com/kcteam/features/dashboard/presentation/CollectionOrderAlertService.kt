package com.kcteam.features.dashboard.presentation

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.text.TextUtils
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import java.util.*


/**
 * Created by Saikat on 05-03-2019.
 */
class CollectionOrderAlertService : Service() {

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                if (!Pref.isAutoLogout) {

                    try {
                        val currentTime = System.currentTimeMillis()

                        var isOrderAdded = false
                        var isCollectionAdded = false

                        val todaysOrderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingDate(AppUtils.getCurrentDate())

                        if (todaysOrderList != null && todaysOrderList.isNotEmpty()) {
                            val lastOrderTime = AppUtils.getTimeStampFromValidDetTime(todaysOrderList[0].date!!)

                            val diffInSec = (currentTime - lastOrderTime) / 1000

                            if (diffInSec <= 120) {
                                isOrderAdded = true
                            }
                        }

                        val todaysCollectionList = AppDatabase.getDBInstance()!!.collectionDetailsDao().getDateWiseCollection(AppUtils.getCurrentDate())

                        if (todaysCollectionList != null && todaysCollectionList.isNotEmpty()) {
                            if (!TextUtils.isEmpty(todaysCollectionList[0].only_time)) {
                                val lastCollectionTime = AppUtils.getTimeStampFromValidDetTime(AppUtils.getCurrentDateFormatInTa(todaysCollectionList[0].date!!) +
                                        "T" + todaysCollectionList[0].only_time)

                                val diffInSec = (currentTime - lastCollectionTime) / 1000

                                if (diffInSec <= 120) {
                                    isCollectionAdded = true
                                }
                            }
                        }

                        if (!TextUtils.isEmpty(Pref.user_id) && Pref.isAddAttendence && (!isOrderAdded || !isCollectionAdded)) {
                            //showOrderCollectionAlert(isOrderAdded, isCollectionAdded)

                            val inten = Intent()
                            inten.action = "ALERT_RECIEVER_BROADCAST"
                            inten.putExtra("isOrderAdded", isOrderAdded)
                            inten.putExtra("isCollectionAdded", isCollectionAdded)

                            LocalBroadcastManager.getInstance(this@CollectionOrderAlertService).sendBroadcast(inten)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }, 0, 120 * 1000)

        return START_STICKY
    }
}
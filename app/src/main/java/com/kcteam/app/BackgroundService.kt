package com.kcteam.app

import android.app.IntentService
import android.content.Intent
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import java.util.*

/**
 * Created by Pratishruti on 27-11-2017.
 */
class BackgroundService : IntentService("MyService") {
    override fun onHandleIntent(p0: Intent?) {
        // Gets data from the incoming Intent
        val loginTime: Date = FTStorageUtils.getStringToDate(AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginTime(Pref.user_id!!,AppUtils.getCurrentDateForShopActi()))
        val logoutTime: Date = FTStorageUtils.getStringToDate(AppUtils.getCurrentDateTime())
        val result = AppUtils.substractDates(logoutTime, loginTime)
        AppDatabase.getDBInstance()!!.userAttendanceDataDao().updateDuration(result,Pref.user_id!!, AppUtils.getCurrentDateForShopActi())
        AppDatabase.getDBInstance()!!.userAttendanceDataDao().updateLogoutTimeN(AppUtils.getCurrentDateTime(),Pref.user_id!!, AppUtils.getCurrentDateForShopActi())
    }
}
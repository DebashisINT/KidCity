package com.kcteam.app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.kcteam.R
import com.kcteam.app.AppConstant
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.EntityTypeEntity
import com.kcteam.app.domain.PartyStatusEntity
import com.kcteam.features.alarm.model.AlarmData
import com.kcteam.features.broadcastreceiver.ActionReceiver
import com.kcteam.features.chat.model.ChatListDataModel
import com.kcteam.features.chat.model.ChatUserDataModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.UserLoginDataEntity
import com.elvishew.xlog.XLog
import com.google.firebase.messaging.RemoteMessage
import java.util.*


/**
 * Created by sandip on 10-11-2017.
 */
class NotificationUtils(headerText: String, bodyText: String, shopId: String, localShopId: String) {

    var headerText: String = headerText
    var bodyText: String = bodyText
    var shopId: String = shopId
    var localShopId: String = localShopId
//    lateinit var notificationmanager :NotificationManager

    companion object {
        fun cancelNotification(id: Int, tag: String, mContext: Context) {
            //you can get notificationManager like this:
            val notificationmanager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationmanager.cancel(tag, id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun CreateNotification(mContext: Context, shopID: String = "") {

        if (Pref.isAttendanceFeatureOnly)
            return

        if (Pref.isOnLeave.equals("true", ignoreCase = true))
            return
        //19-08-21 revisit visit stop untill daystart
        if (Pref.IsShowDayStart) {
            if (!Pref.DayStartMarked)
                return
        }


        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopId)
        val contactNumber = shop?.ownerContactNumber
        val remoteView = RemoteViews(mContext.packageName, R.layout.customnotificationsmall)

        val remoteViewsLarge = RemoteViews(mContext.packageName, R.layout.customnotification)

        val yesIntent = Intent(mContext, ActionReceiver::class.java)
        yesIntent.action = "actionYes"
        yesIntent.putExtra("action", "actionYes")
        yesIntent.putExtra("NAME", bodyText)
        yesIntent.putExtra("ID", shopID)
        yesIntent.putExtra("LOCAL_ID", localShopId)
        //val pendingIntentYes = PendingIntent.getBroadcast(mContext, shopID.hashCode(), yesIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntentYes = PendingIntent.getBroadcast(mContext, shopID.hashCode(), yesIntent, PendingIntent.FLAG_IMMUTABLE)

        val noIntent = Intent(mContext, ActionReceiver::class.java)
        noIntent.action = "actionNo"
        noIntent.putExtra("action", "actionNo")
        noIntent.putExtra("NAME", bodyText)
        noIntent.putExtra("ID", shopID)
        noIntent.putExtra("LOCAL_ID", localShopId)
        //val pendingIntentno = PendingIntent.getBroadcast(mContext, shopID.hashCode(), noIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntentno = PendingIntent.getBroadcast(mContext, shopID.hashCode(), noIntent, PendingIntent.FLAG_IMMUTABLE)

        val shopIntent = Intent(mContext, DashboardActivity::class.java)
        shopIntent.putExtra("NAME", bodyText)
        shopIntent.putExtra("ID", shopID)
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pendingIntent: PendingIntent = PendingIntent.getActivity(mContext, shopID.hashCode(), shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntent: PendingIntent = PendingIntent.getActivity(mContext, shopID.hashCode(), shopIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationmanager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        var notificationChannelId = ""

        //Android 8.0 checking
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannelId = AppUtils.notificationChannelId
            val notificationChannel = NotificationChannel(notificationChannelId, AppUtils.notificationChannelName, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = mContext.getColor(R.color.colorPrimary)
            notificationChannel.enableVibration(true)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationmanager.createNotificationChannel(notificationChannel)
        }


        val builder = NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_notifications_icon)
                .setStyle(NotificationCompat.InboxStyle())
//                .setPriority(Notification.PRIORITY_MAX)
//                .addAction(android.R.drawable.ic_delete, "Yes", pendingIntentYes)
//                .addAction(android.R.drawable.ic_delete, "No", pendingIntentno)
//                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
        builder.setStyle(NotificationCompat.BigTextStyle().bigText(headerText + "\n" + bodyText + "\n\n\n"))  // Expand collapse notification
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        //builder.setGroup("FTS Group")
        //builder.setGroupSummary(true)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(notificationChannelId)
        }

        builder.setContent(remoteView)
        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)

        /*if (Pref.isReplaceShopText)
            remoteView.setTextViewText(R.id.title_small, "You are near $bodyText($contactNumber) customer")
        else
            remoteView.setTextViewText(R.id.title_small, "You are near $bodyText($contactNumber) shop")*/

        var notificationBody = ""

        var partyStatus: PartyStatusEntity? = null
        var entity: EntityTypeEntity? = null

        try {
            partyStatus = AppDatabase.getDBInstance()?.partyStatusDao()?.getSingleItem(shop?.party_status_id!!)
            entity = AppDatabase.getDBInstance()?.entityDao()?.getSingleItem(shop?.entity_id!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (Pref.willShowPartyStatus && Pref.willShowEntityTypeforShop) {
            if (shop?.type == "1") {
                if (partyStatus != null && entity != null)
                    notificationBody = "You are at nearby location of $bodyText(Entity Type: ${entity?.name}, Party Status: ${partyStatus?.name}), $contactNumber. Wish to Revisit now?"
                else if (partyStatus != null)
                    notificationBody = "You are at nearby location of $bodyText(Party Status: ${partyStatus?.name}), $contactNumber. Wish to Revisit now?"
                else if (entity != null)
                    notificationBody = "You are at nearby location of $bodyText(Entity Type: ${entity?.name}), $contactNumber. Wish to Revisit now?"
                else
                    notificationBody = "You are at nearby location of $bodyText, $contactNumber. Wish to Revisit now?"
            } else {
                if (partyStatus != null)
                    notificationBody = "You are at nearby location of $bodyText(Party Status: ${partyStatus?.name}), $contactNumber. Wish to Revisit now?"
                else
                    notificationBody = "You are at nearby location of $bodyText, $contactNumber. Wish to Revisit now?"
            }
        } else if (Pref.willShowPartyStatus) {
            if (partyStatus != null)
                notificationBody = "You are at nearby location of $bodyText(Party Status: ${partyStatus?.name}), $contactNumber. Wish to Revisit now?"
            else
                notificationBody = "You are at nearby location of $bodyText, $contactNumber. Wish to Revisit now?"
        } else if (Pref.willShowEntityTypeforShop && shop?.type == "1" && entity != null)
            notificationBody = "You are at nearby location of $bodyText(Entity Type: ${entity?.name}), $contactNumber. Wish to Revisit now?"
        else
            notificationBody = "You are at nearby location of $bodyText, $contactNumber. Wish to Revisit now?"

        remoteView.setTextViewText(R.id.title_small, notificationBody)

        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        builder.setCustomBigContentView(remoteViewsLarge)
        remoteViewsLarge.setImageViewResource(R.id.imagenotileft, R.drawable.ic_logo)
        remoteViewsLarge.setTextViewText(R.id.title, headerText)
        remoteViewsLarge.setTextViewText(R.id.text, notificationBody.substring(0, notificationBody.indexOf(".") + 1))
        remoteViewsLarge.setOnClickPendingIntent(R.id.no, pendingIntentno)
        remoteViewsLarge.setOnClickPendingIntent(R.id.yes, pendingIntentYes)

        builder.setContentIntent(pendingIntent)
        builder.setOngoing(true)

        // Create Notification Manager

        //var notificationmanager = mContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationmanager.notify(if (shopID.isBlank()) 0 else shopID.hashCode(), builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendFCMNotificaiton(applicationContext: Context, remoteMessage: RemoteMessage?) {

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)

        //val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnoti)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationmanager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            applicationContext.startActivity(intent)
        }*/

        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, remoteMessage?.data?.get("body"))
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationmanager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            applicationContext.startActivity(intent)
        }*/

        if (!TextUtils.isEmpty(remoteMessage?.data?.get("type")) && remoteMessage?.data?.get("type") == "leaveApprove") {
            saveData(remoteMessage.data)
        }

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        if (!TextUtils.isEmpty(remoteMessage?.data?.get("type")) && remoteMessage?.data?.get("type") == "timesheet")
            shopIntent.putExtra("TYPE", "TIMESHEET")
        else if (!TextUtils.isEmpty(remoteMessage?.data?.get("type")) && remoteMessage?.data?.get("type") == "reimbursement")
            shopIntent.putExtra("TYPE", "REIMBURSEMENT")
        else if (!TextUtils.isEmpty(remoteMessage?.data?.get("type")) && remoteMessage?.data?.get("type") == "video_upload")
            shopIntent.putExtra("TYPE", "VIDEO")
        else
            shopIntent.putExtra("TYPE", "PUSH")
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_IMMUTABLE)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            XLog.e("========Notification Channel enabled (FirebaseMesagingService)=========")

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(
                    applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }

    private fun saveData(data: MutableMap<String, String>) {
        val list = AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!, AppUtils.getCurrentDateChanged())
        if (list != null && list.isNotEmpty()) {
            AppDatabase.getDBInstance()!!.userAttendanceDataDao().deleteTodaysData(AppUtils.getCurrentDateChanged())
        }

        if (AppDatabase.getDBInstance()!!.userAttendanceDataDao().getLoginDate(Pref.user_id!!, AppUtils.getCurrentDateChanged()).isEmpty()) {
            val userLoginDataEntity = UserLoginDataEntity()
            userLoginDataEntity.logindate = AppUtils.getCurrentDateChanged()
            userLoginDataEntity.logindate_number = AppUtils.getTimeStampFromDateOnly(AppUtils.getCurrentDateForShopActi())
            userLoginDataEntity.Isonleave = "true"
            userLoginDataEntity.userId = Pref.user_id!!
            AppDatabase.getDBInstance()!!.userAttendanceDataDao().insertAll(userLoginDataEntity)

            if (!TextUtils.isEmpty(data["leaveFromDate"]) && data["leaveFromDate"] == AppUtils.getCurrentDateForShopActi()) {
                Pref.add_attendence_time = AppUtils.getCurrentTimeWithMeredian()
                Pref.isOnLeave = "true"
                Pref.isAddAttendence = true
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun sendIdealNotificaiton(applicationContext: Context, body: String) {

        if (Pref.isAttendanceFeatureOnly)
            return

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationmanager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            applicationContext.startActivity(intent)
        }*/

        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, body + "\n" + AppUtils.getCurrentTimeWithMeredian())
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    //.setContentIntent(pending)
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    //.setContentIntent(pending)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun sendForceLogoutNotification(applicationContext: Context, body: String) {

        if (Pref.isAttendanceFeatureOnly)
            return

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationmanager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            applicationContext.startActivity(intent)
        }*/

        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, body /*+ "\n" + AppUtils.getCurrentTimeWithMeredian()*/)
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "force_logout")
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pending: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pending: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_IMMUTABLE)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(false)
                    .setChannelId(channelId)
                    .setContentIntent(pending)
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    .setContentIntent(pending)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(false)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendAlarmNotification(applicationContext: Context, body: String, alarmData: AlarmData?) {

        if (Pref.isAttendanceFeatureOnly)
            return

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationmanager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            applicationContext.startActivity(intent)
        }*/

        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, body + "\n" + AppUtils.getCurrentTimeWithMeredian())
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("ALARM_DATA", alarmData)
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pending: PendingIntent = PendingIntent.getActivity(applicationContext, alarmData?.id?.toInt()?.hashCode()!!, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pending: PendingIntent = PendingIntent.getActivity(applicationContext, alarmData?.id?.toInt()?.hashCode()!!, shopIntent, PendingIntent.FLAG_IMMUTABLE)

//        val shopIntent = Intent("android.intent.category.LAUNCHER")
//        shopIntent.setClassName("com.kcteam.features.dashboard.presentation", "com.kcteam.features.dashboard.presentation.DashboardActivity")
//        shopIntent.putExtra("ALARM_DATA", alarmData)
//        shopIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//        val pending: PendingIntent = PendingIntent.getActivity(applicationContext, alarmData?.id?.toInt()?.hashCode()!!, shopIntent,
//                /*PendingIntent.FLAG_UPDATE_CURRENT*/ 0)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    //.setContentIntent(pending)
                    .setFullScreenIntent(pending, true)
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    //.setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    //.setContentIntent(pending)
                    .setFullScreenIntent(pending, true)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build()

            notificationmanager.notify(m, notification)
        }


        XLog.e("=================Show alarm notification (Notification)=================")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendRevisitDueNotification(applicationContext: Context, body: String) {

        if (Pref.isAttendanceFeatureOnly)
            return

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, body)
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "DUE")
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_IMMUTABLE)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setCustomBigContentView(remoteView)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(
                    applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setCustomBigContentView(remoteView)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun sendLocNotification(applicationContext: Context, body: String) {

        if (Pref.isAttendanceFeatureOnly)
            return

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, body)
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        val notificationIntent = Intent(applicationContext, DashboardActivity::class.java)
        notificationIntent.apply {
            action = AppConstant.MAIN_ACTION
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }


//        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)
        // FLAG_IMMUTABLE update
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName


            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    //.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(body)
                    //.setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    //.setContent(remoteView)
                    .setCustomBigContentView(remoteView)
                    .setPriority(NotificationManager.IMPORTANCE_MAX)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(applicationContext)
                    //.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(body)
                    //.setStyle(NotificationCompat.BigTextStyle().bigText(body))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    //.setContent(remoteView)
                    .setCustomBigContentView(remoteView)
                    .setPriority(NotificationManager.IMPORTANCE_MAX)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendClearDataNotification(applicationContext: Context, body: String) {

        if (Pref.isAttendanceFeatureOnly)
            return

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationmanager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            applicationContext.startActivity(intent)
        }*/

        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, body + "\n" + AppUtils.getCurrentTimeWithMeredian())
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "clearData")
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pending: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pending: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_IMMUTABLE)

//        val shopIntent = Intent("android.intent.category.LAUNCHER")
//        shopIntent.setClassName("com.kcteam.features.dashboard.presentation", "com.kcteam.features.dashboard.presentation.DashboardActivity")
//        shopIntent.putExtra("ALARM_DATA", alarmData)
//        shopIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//        val pending: PendingIntent = PendingIntent.getActivity(applicationContext, alarmData?.id?.toInt()?.hashCode()!!, shopIntent,
//                /*PendingIntent.FLAG_UPDATE_CURRENT*/ 0)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    //.setContentIntent(pending)
                    .setFullScreenIntent(pending, true)
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    //.setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    //.setContentIntent(pending)
                    .setFullScreenIntent(pending, true)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build()

            notificationmanager.notify(m, notification)
        }


        XLog.e("=================Show clear data notification (Notification)=================")

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendTaskDueNotification(applicationContext: Context, body: String) {

        if (Pref.isAttendanceFeatureOnly)
            return

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, body)
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "TASK")
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_IMMUTABLE)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(
                    applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun msgNotification(applicationContext: Context, body: String, chatListDataModel: ChatListDataModel?, chatUserDataModel: ChatUserDataModel?) {
        Log.e("Notification", "==================Show Chat Notification===============")

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall_new)

        val notifiBody = body/*.substring(0, body.indexOf("says"))*/ + " " + AppUtils.decodeEmojiAndText(chatListDataModel?.msg!!)

        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, notifiBody)
        remoteView.setTextViewText(R.id.text_small, AppUtils.getCurrentTimeWithMeredian())


        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "Msg")
        shopIntent.putExtra("chatData", chatListDataModel)
        shopIntent.putExtra("chatUser", chatUserDataModel)
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pending: PendingIntent = PendingIntent.getActivity(applicationContext, (chatUserDataModel?.id + "#" + chatListDataModel.id).hashCode(), shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pending: PendingIntent = PendingIntent.getActivity(applicationContext, (chatUserDataModel?.id + "#" + chatListDataModel.id).hashCode(), shopIntent, PendingIntent.FLAG_IMMUTABLE)

//        val shopIntent = Intent("android.intent.category.LAUNCHER")
//        shopIntent.setClassName("com.kcteam.features.dashboard.presentation", "com.kcteam.features.dashboard.presentation.DashboardActivity")
//        shopIntent.putExtra("ALARM_DATA", alarmData)
//        shopIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//
//        val pending: PendingIntent = PendingIntent.getActivity(applicationContext, alarmData?.id?.toInt()?.hashCode()!!, shopIntent,
//                /*PendingIntent.FLAG_UPDATE_CURRENT*/ 0)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pending)
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    .setCustomBigContentView(remoteView)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(headerText + "\n" + bodyText + "\n\n\n"))
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build()

            notificationmanager.notify((chatUserDataModel?.id + "#" + chatListDataModel.id).hashCode(), notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(applicationContext)
                    /* .setContentTitle(applicationContext.getString(R.string.app_name))
                     .setContentText(body)*/
                    .setContentIntent(pending)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContent(remoteView)
                    .setCustomBigContentView(remoteView)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(headerText + "\n" + bodyText + "\n\n\n"))
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .build()

            notificationmanager.notify((chatUserDataModel?.id + "#" + chatListDataModel.id).hashCode(), notification)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun sendLogoutNotificaiton(applicationContext: Context, remoteMessage: RemoteMessage?) {

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.custom_logout_fcm_notification)


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationmanager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            applicationContext.startActivity(intent)
        }*/

        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, remoteMessage?.data?.get("body"))
        remoteView.setTextViewText(R.id.text_small, "")

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationmanager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            applicationContext.startActivity(intent)
        }*/

        if (!TextUtils.isEmpty(remoteMessage?.data?.get("type")) && remoteMessage?.data?.get("type") == "leaveApprove") {
            saveData(remoteMessage.data)
        }

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        if (!TextUtils.isEmpty(remoteMessage?.data?.get("type")) && remoteMessage?.data?.get("type") == "timesheet")
            shopIntent.putExtra("TYPE", "TIMESHEET")
        else if (!TextUtils.isEmpty(remoteMessage?.data?.get("type")) && remoteMessage?.data?.get("type") == "reimbursement")
            shopIntent.putExtra("TYPE", "REIMBURSEMENT")
        else if (!TextUtils.isEmpty(remoteMessage?.data?.get("type")) && remoteMessage?.data?.get("type") == "video_upload")
            shopIntent.putExtra("TYPE", "VIDEO")
        else
            shopIntent.putExtra("TYPE", "PUSH")
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_IMMUTABLE)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            XLog.e("========Notification Channel enabled (FirebaseMesagingService)=========")

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(
                    applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun sendFCMNotificaitonCustom(applicationContext: Context, remoteMessage: RemoteMessage?) {

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationmanager.isNotificationPolicyAccessGranted) {
            val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            applicationContext.startActivity(intent)
        }*/

        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, remoteMessage?.data?.get("body") +
                "\n From : " + AppUtils.getFormatedDateNew(remoteMessage?.data?.get("leave_from_date"), "yyyy-mm-dd", "dd-mm-yyyy") +
                " To : " + AppUtils.getFormatedDateNew(remoteMessage?.data?.get("leave_to_date"), "yyyy-mm-dd", "dd-mm-yyyy"))
        remoteView.setTextViewText(R.id.text_small, "Nordusk")


        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "LEAVE_APPLY")
        shopIntent.putExtra("USER_ID", remoteMessage?.data?.get("applied_user_id"))
        shopIntent.putExtra("LEAVE_FROM_DATE", remoteMessage?.data?.get("leave_from_date"))
        shopIntent.putExtra("LEAVE_TO_DATE", remoteMessage?.data?.get("leave_to_date"))
        shopIntent.putExtra("LEAVE_REASON", remoteMessage?.data?.get("leave_reason"))
        shopIntent.putExtra("LEAVE_TYPE", remoteMessage?.data?.get("leave_type"))
        shopIntent.putExtra("LEAVE_TYPE_ID", remoteMessage?.data?.get("leave_type_id"))
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 1, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 1, shopIntent, PendingIntent.FLAG_IMMUTABLE)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            XLog.e("========Notification Channel enabled (FirebaseMesagingService)=========")

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(
                    applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun sendFCMNotificaitonByUCustom(applicationContext: Context, remoteMessage: RemoteMessage?) {

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, remoteMessage?.data?.get("body"))
        remoteView.setTextViewText(R.id.text_small, "Nordusk")


        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "LEAVE_STATUS")
        shopIntent.putExtra("USER_ID", remoteMessage?.data?.get("applied_user_id"))
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 1, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 1, shopIntent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            XLog.e("========Notification Channel enabled (FirebaseMesagingService)=========")

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(
                    applicationContext)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendLeadActivityNotification(applicationContext: Context, body: String) {

        if (Pref.isAttendanceFeatureOnly)
            return

        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)


        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, body)
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "ACTIVITYDUE")
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        //val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        // FLAG_IMMUTABLE update
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, shopIntent, PendingIntent.FLAG_IMMUTABLE)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            //notificationChannel.setLightColor(getResources().getColor(R.color.material_progress_color));
            notificationChannel.enableVibration(true)
            //notificationChannel.setVibrationPattern(new Long[100, 200, 300, 400, 500, 400, 300, 200, 400]);
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setChannelId(channelId)
                    .setContentIntent(pendingIntent)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setCustomBigContentView(remoteView)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(
                    applicationContext)
                    /*.setContentTitle(applicationContext.getString(R.string.app_name))
                    .setContentText(remoteMessage?.data?.get("body"))*/
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    // .setStyle(new
                    // NotificationCompat.BigPictureStyle()
                    // .bigPicture(bmp))
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setCustomBigContentView(remoteView)
                    .setContent(remoteView)
                    .build()

            notificationmanager.notify(m, notification)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendFCMNotificaitonQuotationapprova(applicationContext: Context, remoteMessage: RemoteMessage?) {
        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnotificationsmall)
        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnoti)

        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, remoteMessage?.data?.get("body"))
//        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "quotation_approval")
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 1, shopIntent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId

            val channelName = AppUtils.notificationChannelName

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lockscreenVisibility = Notification.DEFAULT_ALL
            notificationChannel.setShowBadge(false)
            notificationmanager.createNotificationChannel(notificationChannel)

            val notificationBuilder = NotificationCompat.Builder(applicationContext)
                .setSmallIcon(R.drawable.ic_notifications_icon)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setChannelId(channelId)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                //.setGroup("FTS Group")
                .setGroupSummary(true)
                .setContent(remoteView)
                .setPriority(Notification.PRIORITY_HIGH)
                .setOngoing(false)
                .setFullScreenIntent(pendingIntent,true)
                .build()

            notificationmanager.notify(m, notificationBuilder)
        } else {
            val notification = NotificationCompat.Builder(
                applicationContext)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notifications_icon)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setGroup("FTS Group")
                .setGroupSummary(true)
                .setContent(remoteView)
                .build()

            notificationmanager.notify(m, notification)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendFCMNotificaitonQuotationapprova1(applicationContext: Context, remoteMessage: RemoteMessage?) {
        val random = Random()
        val m = random.nextInt(9999 - 1000) + 1000

        val notificationmanager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val remoteView = RemoteViews(applicationContext.packageName, R.layout.customnoti)
        remoteView.setImageViewResource(R.id.imagenotileft_small, R.drawable.ic_logo)
        remoteView.setTextViewText(R.id.title_small, remoteMessage?.data?.get("body"))
        remoteView.setTextViewText(R.id.text_small, "Nordusk")

        val shopIntent = Intent(applicationContext, DashboardActivity::class.java)
        shopIntent.putExtra("TYPE", "quotation_approval")
        shopIntent.action = Intent.ACTION_MAIN
        shopIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        shopIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        shopIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 1, shopIntent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = AppUtils.notificationChannelId
            val channelName = AppUtils.notificationChannelName
            val importance = NotificationManager.IMPORTANCE_HIGH

            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lockscreenVisibility = Notification.DEFAULT_ALL
            notificationmanager.createNotificationChannel(notificationChannel)

            var notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(applicationContext, channelId)
                    .setSmallIcon(R.drawable.ic_notifications_icon)
                    //.setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.drawable.ic_logo))
                    //.setContent(remoteView)
                    .setContentText(remoteMessage?.data?.get("body").toString())
                    .setChannelId(channelId)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setGroup("FTS Group")
                    .setGroupSummary(true)
                    .setOngoing(false)

            notificationBuilder.setContentIntent(pendingIntent)

            notificationmanager.notify(m,notificationBuilder.build())
        }
            else {
            val notification = NotificationCompat.Builder(
                applicationContext)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notifications_icon)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setGroup("FTS Group")
                .setGroupSummary(true)
                .setContent(remoteView)
                .build()

            notificationmanager.notify(m, notification)
        }

    }


}
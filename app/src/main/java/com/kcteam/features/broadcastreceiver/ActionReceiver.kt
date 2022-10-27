package com.kcteam.features.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.kcteam.features.dashboard.presentation.DashboardActivity

/**
 * Created by Dhiraj on 14-11-2017.
 */

class ActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();

        val action = intent.getStringExtra("action")
        val shopName = intent.getStringExtra("NAME")
        val shopId = intent.getStringExtra("ID")
        val localShopId = intent.getStringExtra("LOCAL_ID")
        val action_new=intent.action

        if (action_new == "actionNo") {
            val i = Intent(context, DashboardActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("NAME", shopName)
            i.putExtra("ID", shopId)
            i.putExtra("ACTION", "CANCEL")
            context.startActivity(i)
        } else if (action_new == "actionYes") {
            // Toast.makeText(context, "Yes !!", Toast.LENGTH_SHORT).show();
            //            if(!FTStorageUtils.isAppInForeground(context))
            val i = Intent(context, DashboardActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("NAME", shopName)
            i.putExtra("ID", shopId)
            i.putExtra("ACTION", "OK")
            context.startActivity(i)

            //            }
        }

        //        Intent i = new Intent(context, DashboardActivity.class);
        //        context.startActivity(i);
        //This is used to close the notification tray
        val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        context.sendBroadcast(it)
    }

    fun performAction1() {

    }

    fun performAction2() {

    }

    //TODO

    //    fun shoulIBotherToNotify(shopId: String): Boolean {
    //        var shouldUpdate = AppDatabase.getDBInstance()!!.shopActivityDao().getIsVisitedOfShop(shopId,AppUtils.getCurrentDateForShopActi())
    //        return shouldUpdate
    //    }

}
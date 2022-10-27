package com.kcteam.mappackage

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import com.kcteam.MonitorBroadcast
import com.kcteam.MonitorCollPending
import com.kcteam.app.Pref


class SendBrod {

    companion object{
        var monitorNotiID:Int = 201
        var monitorNotiIDColl:Int = 202
        var monitorNotiIDZeroOrder:Int = 203
        var monitorNotiIDDoaDob:Int = 204
        fun sendBrod(context: Context){
            if(Pref.user_id.toString().length > 0){
                //var notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
                //notificationManager.cancel(monitorNotiID)
                MonitorBroadcast.isSound=Pref.GPSAlertwithSound
                MonitorBroadcast.isVibrator = Pref.GPSAlertwithVibration
                val intent: Intent = Intent(context, MonitorBroadcast::class.java)
                intent.putExtra("notiId", monitorNotiID)
                intent.putExtra("fuzedLoc", "Fuzed Stop")
                context.sendBroadcast(intent)
            }
        }

        fun stopBrod(context: Context){
            if (monitorNotiID != 0){
                if(MonitorBroadcast.player!=null){
                    MonitorBroadcast.player.stop()
                    MonitorBroadcast.player=null
                    MonitorBroadcast.vibrator.cancel()
                    MonitorBroadcast.vibrator=null
                }
                var notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(monitorNotiID)
            }
        }


        fun sendBrodColl(context: Context){
            if(Pref.user_id.toString().length > 0){
                val intent: Intent = Intent(context, MonitorCollPending::class.java)
                intent.putExtra("notiId", monitorNotiIDColl)
                intent.putExtra("coll", "Pending Collection")
                context.sendBroadcast(intent)
            }
        }

        fun stopBrodColl(context: Context){
            if (monitorNotiIDColl != 0){
                if(MonitorCollPending.player!=null){
                    MonitorCollPending.player.stop()
                    MonitorCollPending.player=null
                    MonitorCollPending.vibrator.cancel()
                    MonitorCollPending.vibrator=null
                }
                var notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(monitorNotiIDColl)
            }
        }

        fun sendBrodZeroOrder(context: Context){
            if(Pref.user_id.toString().length > 0){
                val intent: Intent = Intent(context, MonitorCollPending::class.java)
                intent.putExtra("notiId", monitorNotiIDZeroOrder)
                intent.putExtra("coll", "Zero Order")
                context.sendBroadcast(intent)
            }
        }

        fun stopBrodZeroOrder(context: Context){
            if (monitorNotiIDZeroOrder != 0){
                if(MonitorCollPending.player!=null){
                    MonitorCollPending.player.stop()
                    MonitorCollPending.player=null
                    MonitorCollPending.vibrator.cancel()
                    MonitorCollPending.vibrator=null
                }
                var notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(monitorNotiIDZeroOrder)
            }
        }

        fun sendBrodDOBDOA(context: Context){
            if(Pref.user_id.toString().length > 0){
                val intent: Intent = Intent(context, MonitorCollPending::class.java)
                intent.putExtra("notiId", monitorNotiIDDoaDob)
                intent.putExtra("coll", "Doa Dob")
                context.sendBroadcast(intent)
            }
        }

        fun stopBrodDOBDOA(context: Context){
            if (monitorNotiIDDoaDob != 0){
                if(MonitorCollPending.player!=null){
                    MonitorCollPending.player.stop()
                    MonitorCollPending.player=null
                    MonitorCollPending.vibrator.cancel()
                    MonitorCollPending.vibrator=null
                }
                var notificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(monitorNotiIDDoaDob)
            }
        }

    }

}
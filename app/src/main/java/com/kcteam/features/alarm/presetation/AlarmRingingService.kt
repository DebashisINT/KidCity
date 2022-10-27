package com.kcteam.features.alarm.presetation

import android.app.Service
import android.content.Intent

import android.media.RingtoneManager
import android.net.Uri

import android.os.IBinder
import com.kcteam.app.utils.AlarmRingtonePlayer
import com.kcteam.app.utils.AlarmVibrator
import java.io.File


/**
 * Created by Kinsuk on 15-02-2019.
 */
class AlarmRingingService : Service() {

    lateinit var alarmPlayer: AlarmRingtonePlayer
    lateinit var vibratore: AlarmVibrator

    override fun onCreate() {
        super.onCreate()

        alarmPlayer = AlarmRingtonePlayer(applicationContext)
        vibratore = AlarmVibrator(applicationContext)
        alarmPlayer.initialize()
        vibratore.initialize()

        try {
            vibratore.vibrate()

            var alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL)
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }
            }

            if (alarmUri === null) {
                alarmPlayer.play()
            } else {
                if (File(alarmUri.path).exists())
                    alarmPlayer.play(alarmUri)
                else
                    alarmPlayer.play()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            alarmPlayer.play()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        vibratore.stop()
        vibratore.cleanup()
        alarmPlayer.stop()
        alarmPlayer.cleanup()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }


}
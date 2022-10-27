package com.kcteam;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import androidx.core.app.NotificationCompat;

import com.kcteam.app.Pref;
import com.kcteam.app.utils.AppUtils;
import com.kcteam.features.splash.presentation.SplashActivity;
import com.elvishew.xlog.XLog;

public class MonitorBroadcast extends BroadcastReceiver {

    public static MediaPlayer player = null;
    public static Vibrator vibrator = null;
    public static Boolean isSound = false;
    public static Boolean isVibrator = false;

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {


        XLog.e("MONITOR BROADCAST GPS_EVENT_STOPPED: " + "Time : " + AppUtils.Companion.getCurrentDateTime());

        int notiID=intent.getIntExtra("notiId",0);
        String subject=intent.getStringExtra("fuzedLoc");

        Intent mainIntent = new Intent(context, SplashActivity.class);
        mainIntent.putExtra("Subject",subject);
        //PendingIntent pendingIntent =PendingIntent.getActivity(context, notiID, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // FLAG_IMMUTABLE update
        PendingIntent pendingIntent =PendingIntent.getActivity(context, notiID, mainIntent, PendingIntent.FLAG_IMMUTABLE);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context,notiID,mainIntent,0);

        long[] pattern = {500,500,500,500,500,500,500,500,500,500,500,500,500};
        //Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getPackageName() + "/" + R.raw.alaram_sound);
        //Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/raw/alaram_sound.mp3");
        Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                //.setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        //String CHANNEL_ID = "Monitor";
        String CHANNEL_ID = "201";
        Notification notification =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(subject)
                        .setContentText(" :: Loc Stopped \n Turn ON GPS : Turn Off Battery Saver")
                        .setAutoCancel(false)
                        //.setSound(soundUri)
                        .setChannelId(CHANNEL_ID)
                        .setWhen(System.currentTimeMillis())
                        //.setVibrate(pattern)
                        .build();

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,subject,NotificationManager.IMPORTANCE_HIGH);
            //channel.enableVibration(true);
            //channel.setVibrationPattern(pattern);
            //channel.setSound(soundUri,audioAttributes);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(notiID,notification);


        /*Uri soundUriAlarm= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(soundUriAlarm == null){
            soundUriAlarm= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }*/
        //Ringtone ringtone=RingtoneManager.getRingtone(context,soundUriAlarm);
        //ringtone.play();


       /* VolumeShaper.Configuration config =
                new VolumeShaper.Configuration.Builder()
                        .setDuration(3000)
                        .setCurve(new float[] {0.f, 1.f}, new float[] {0.f, 1.f})
                        .setInterpolatorType(VolumeShaper.Configuration.INTERPOLATOR_TYPE_LINEAR)
                        .build();*/


     /*   player=null;
        player = MediaPlayer.create(context, soundUriAlarm);
        if(!player.isPlaying()){
            //player.setLooping(true);
            player.createVolumeShaper(config);
            player.start();
        }*/

        if(player==null){
            funcc(context);
        }

    }

    private void funcc(Context context){

        //Uri soundUriAlarm= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Uri soundUriAlarm = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getPackageName() + "/" + R.raw.beethoven);
        if(soundUriAlarm == null){
            //soundUriAlarm= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            soundUriAlarm= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        }

        if(player!=null){
            player.stop();
        }
        player = MediaPlayer.create(context, soundUriAlarm);
        player.setLooping(true);
        player.start();

        if(!isSound)
            player.stop();
//        if(Pref.GPSAlertwithVibration)
        if(isVibrator) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {0, 5, 10, 20, 40, 80, 120, 100, 600, 700, 500, 500, 500};
            vibrator.vibrate(pattern, 1);
        }
        else{

        }
        //vibrator.vibrate(10*60*1000);

    }

}

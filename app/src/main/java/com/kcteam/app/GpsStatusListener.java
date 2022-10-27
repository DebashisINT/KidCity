package com.kcteam.app;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.provider.Settings;

import com.kcteam.features.location.LocationFuzedService;

public class GpsStatusListener extends BroadcastReceiver {

    private Context mContext;
    /**
     * Callbacks for service binding, passed to bindService()
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
//			LocalBinder binder = (LocalBinder) service;
//			binder.getService().setCallbacks(); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        try {
            String provider = Settings.Secure.getString(
                    mContext.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (provider.contains("gps")) {
                startLocationService(mContext);

            } else {

                stopLocationService();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startLocationService(Context context) {
        mContext = context;

        try {
//			mContext.startService(new Intent(mContext, LocationService.class));
//			mContext.bindService(new Intent(mContext, LocationService.class),
//					serviceConnection, Context.BIND_AUTO_CREATE);

            Intent service = new Intent(context, LocationFuzedService.class);
//            startLocationService(context, service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isLocationServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void stopLocationService() {
        try {
            if (isLocationServiceRunning(LocationFuzedService.class)) {
                mContext.stopService(new Intent(mContext, LocationFuzedService.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

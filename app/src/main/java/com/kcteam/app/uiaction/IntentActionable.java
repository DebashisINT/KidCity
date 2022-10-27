package com.kcteam.app.uiaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by rp : 17-08-2017:16:44
 */

public class IntentActionable {

    public static void initiatePhoneCall(Context mContext, String phoneNum) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Uri.encode(phoneNum.trim())));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(callIntent);
    }

    public static void showGoogleMap(Context mContext, String label, String lat, String lng) {
        String strUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lng + " (" + label + ")";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        mContext.startActivity(intent);
    }

    public static void showOnWeb(Context mContext, String url) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }
        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(openUrlIntent);
    }


    public static void sendMail(Context mContext, String email, String message) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: " + email));
        mContext.startActivity(Intent.createChooser(emailIntent, message));
    }
}

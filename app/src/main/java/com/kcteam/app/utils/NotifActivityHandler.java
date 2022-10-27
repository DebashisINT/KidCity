package com.kcteam.app.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kcteam.features.dashboard.presentation.DashboardActivity;

public class NotifActivityHandler extends Activity {

    private NotifActivityHandler ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx=this;
        String action= (String)getIntent().getExtras().get("DO");
        Log.i("LOG", "lauching action: " + action);
        if(action.equals("1")){
        } else if(action.equals("2")){
        } else if(action.equals("config")){
            Intent i = new Intent(NotifActivityHandler.this, DashboardActivity.class);
            startActivity(i);
        }
    }   
}
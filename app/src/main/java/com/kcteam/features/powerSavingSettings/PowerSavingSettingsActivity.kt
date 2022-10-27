package com.kcteam.features.powerSavingSettings

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import net.alexandroid.gps.GpsStatusDetector


class PowerSavingSettingsActivity : BaseActivity(){

    private lateinit var mContext: Context
    private lateinit var tv_power_gps: AppCustomTextView
    private lateinit var rl_power_main: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_power_savings_screen)
        rl_power_main = findViewById(R.id.rl_power_main)
        rl_power_main.setOnClickListener(null)
        tv_power_gps =findViewById(R.id.tv_power_gps)
        tv_power_gps.setOnClickListener({
            startActivity(Intent(Settings.ACTION_SETTINGS))
        })
    }








}
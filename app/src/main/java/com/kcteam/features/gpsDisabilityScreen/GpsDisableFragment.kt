package com.kcteam.features.gpsDisabilityScreen

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import net.alexandroid.gps.GpsStatusDetector


/**
 * Created by Saikat on 13-11-2018.
 */
class GpsDisableFragment : BaseFragment(), GpsStatusDetector.GpsStatusDetectorCallBack {

    private lateinit var mContext: Context
    private lateinit var tv_enable_gps: AppCustomTextView
    private var mGpsStatusDetector: GpsStatusDetector? = null
    private lateinit var rl_gps_main: RelativeLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_gps_disable_screen, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        mGpsStatusDetector = GpsStatusDetector(this)
        rl_gps_main = view.findViewById(R.id.rl_gps_main)
        rl_gps_main.setOnClickListener(null)
        tv_enable_gps = view.findViewById(R.id.tv_enable_gps)
        tv_enable_gps.setOnClickListener({
            mGpsStatusDetector?.checkGpsStatus()
        })
    }

    fun onFragmentActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mGpsStatusDetector?.checkOnActivityResult(requestCode, resultCode)
    }

    override fun onResume() {
        super.onResume()

        val manager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Handler().postDelayed(Runnable {
                (mContext as DashboardActivity).showSnackMessage("GPS enabled")
                (mContext as DashboardActivity).isGpsDisabled = false
                (mContext as DashboardActivity).onBackPressed()

            }, 500)
        }
        /*else
            Toaster.msgShort(mContext,"Gps Disable On Resume")*/
    }

    // GpsStatusDetectorCallBack
    override fun onGpsSettingStatus(enabled: Boolean) {

        if (enabled) {
            (mContext as DashboardActivity).showSnackMessage("GPS enabled")
            (mContext as DashboardActivity).isGpsDisabled = false
            (mContext as DashboardActivity).onBackPressed()
        } else
            (mContext as DashboardActivity).showSnackMessage("GPS disabled")
    }

    override fun onGpsAlertCanceledByUser() {
    }

}
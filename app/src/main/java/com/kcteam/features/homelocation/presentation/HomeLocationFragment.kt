package com.kcteam.features.homelocation.presentation

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.widget.AppCompatImageView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.widgets.AppCustomTextView
import java.io.File

class HomeLocationFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var tv_lat: AppCustomTextView
    private lateinit var tv_long: AppCustomTextView
    private lateinit var tv_address: AppCustomTextView
    private lateinit var tv_city: AppCustomTextView
    private lateinit var tv_state: AppCustomTextView
    private lateinit var tv_country: AppCustomTextView
    private lateinit var tv_pincode: AppCustomTextView
    private lateinit var iv_location_icon: AppCompatImageView
    private lateinit var iv_share_icon: AppCompatImageView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_home_location, container, false)

        initView(view)
        initClickListener()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            tv_lat = findViewById(R.id.tv_lat)
            tv_long = findViewById(R.id.tv_long)
            tv_address = findViewById(R.id.tv_address)
            tv_city = findViewById(R.id.tv_city)
            tv_state = findViewById(R.id.tv_state)
            tv_country = findViewById(R.id.tv_country)
            tv_pincode = findViewById(R.id.tv_pincode)
            iv_location_icon = findViewById(R.id.iv_location_icon)
            iv_share_icon = findViewById(R.id.iv_share_icon)
        }

        tv_lat.text = Pref.home_latitude
        tv_long.text = Pref.home_longitude
        tv_address.text = LocationWizard.getLocationName(mContext, Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble())
        tv_city.text = LocationWizard.getCity(mContext, Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble())
        tv_state.text = LocationWizard.getState(mContext, Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble())
        tv_country.text = LocationWizard.getCountry(mContext, Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble())
        tv_pincode.text = LocationWizard.getPostalCode(mContext, Pref.home_latitude.toDouble(), Pref.home_longitude.toDouble())
    }

    private fun initClickListener() {
        iv_location_icon.setOnClickListener(this)
        iv_share_icon.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.iv_location_icon -> {
                (mContext as DashboardActivity).loadFragment(FragType.HomeLocationMapFragment, true, "")
            }

            R.id.iv_share_icon -> {
                val heading = "${Pref.user_name?.toUpperCase()} HOME LOCATION"
                val pdfBody = "\n\n\n\n${getString(R.string.latitude)}  ${Pref.home_latitude}\n\n${getString(R.string.longitude)}  " +
                        "${Pref.home_longitude}\n\n${getString(R.string.address_colon)}  ${tv_address.text.toString().trim()}\n\n" +
                        "${getString(R.string.city_colon)}  ${tv_city.text.toString().trim()}\n\n${getString(R.string.state_colon)}  " +
                        "${tv_state.text.toString().trim()}\n\n${getString(R.string.country_colon)}  ${tv_country.text.toString().trim()}" +
                        "\n\n${getString(R.string.pincode_colon)}  ${tv_pincode.text.toString().trim()}"

                val image = BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher)
                val path = FTStorageUtils.stringToPdf(pdfBody, mContext, "FTS_${Pref.user_id}_${Pref.user_name}.pdf",
                        image, heading, 2.7f)

                if (!TextUtils.isEmpty(path)) {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        val fileUrl = Uri.parse(path)

                        val file = File(fileUrl.path)
                        //val uri = Uri.fromFile(file)
                        //27-09-2021
                        val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
                        shareIntent.type = "image/png"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(Intent.createChooser(shareIntent, "Share pdf using"));
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else
                    (mContext as DashboardActivity).showSnackMessage("Pdf can not be sent.")
            }
        }
    }
}
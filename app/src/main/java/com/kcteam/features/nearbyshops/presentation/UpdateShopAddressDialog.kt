package com.kcteam.features.nearbyshops.presentation

import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog

/**
 * Created by Pratishruti on 02-02-2018.
 */
class UpdateShopAddressDialog : DialogFragment() {
    private lateinit var shop_name_TV: AppCustomTextView
    private lateinit var address_EDT: AppCustomEditText
    private lateinit var update_TV: AppCustomTextView
    private lateinit var new_address_EDT: AppCustomEditText
    var addShopData = AddShopDBModelEntity()
    private lateinit var mContext: Context


    companion object {
        private lateinit var shopId: String
        private lateinit var addressUpdateClickListener: ShopAddressUpdateListener
        private lateinit var loc: Location

        fun getInstance(shopId: String, location: Location, listener: ShopAddressUpdateListener): UpdateShopAddressDialog {
            val mUpdateShopAddressDialog = UpdateShopAddressDialog()
            this.shopId = shopId
            this.loc = location
            addressUpdateClickListener = listener
            return mUpdateShopAddressDialog
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater?.inflate(R.layout.dialog_update_shop_address, container, false)
        addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)
        initView(v)
        return v
    }

    private fun initView(v: View?) {
        shop_name_TV = v!!.findViewById(R.id.shop_name_TV)
        address_EDT = v!!.findViewById(R.id.address_EDT)
        update_TV = v!!.findViewById(R.id.update_TV)
        new_address_EDT = v.findViewById(R.id.new_address_EDT)
        /*SingleShotLocationProvider.requestSingleUpdate(mContext,
                object : SingleShotLocationProvider.LocationCallback {
                    override fun onStatusChanged(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(status: String) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onNewLocationAvailable(location: Location) {

                        //location.accuracy = 50.0f

                        if (location.accuracy > 30) {
                            new_address_EDT.setText(addShopData.address)
                            return
                        }
                        try {
                            addShopData.shopLat = location.latitude
                            addShopData.shopLong = location.longitude
                            addShopData.pinCode = LocationWizard.getPostalCode(mContext, Pref.latitude!!.toDouble(), Pref.longitude!!.toDouble())
                            new_address_EDT.setText(LocationWizard.getLocationName(mContext, location.latitude, location.longitude))
                        } catch (e: Exception) {
                            new_address_EDT.setText(addShopData.address)
                        }
                    }
                })*/


        /*if (loc.accuracy > 30) {
            new_address_EDT.setText(addShopData.address)
            return
        }*/

        try {
            addShopData.shopLat = loc.latitude
            addShopData.shopLong = loc.longitude
            addShopData.pinCode = LocationWizard.getPostalCode(mContext, loc.latitude, loc.longitude)
            new_address_EDT.setText(LocationWizard.getLocationName(mContext, loc.latitude, loc.longitude))
        } catch (e: Exception) {
            e.printStackTrace()
            XLog.e("Update Shop Address", "Address calculation error(From shop list)=========> " + e.localizedMessage + " for shop " + addShopData.shopName)
            new_address_EDT.setText("Unknown")
        }


        update_TV.setOnClickListener(View.OnClickListener {

            if (TextUtils.isEmpty(address_EDT.text.toString().trim()))
                (mContext as DashboardActivity).showSnackMessage("Please enter current address")
            else if (TextUtils.isEmpty(new_address_EDT.text.toString().trim()))
                (mContext as DashboardActivity).showSnackMessage("Please enter new address")
            else {
                addShopData.address = new_address_EDT.text.toString()
                addressUpdateClickListener.onUpdateClick(addShopData)
                dialog?.dismiss()
            }
        })
        shop_name_TV.text = addShopData.shopName
        address_EDT.setText(addShopData.address)

    }


}
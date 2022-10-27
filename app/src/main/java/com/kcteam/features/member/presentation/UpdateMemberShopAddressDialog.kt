package com.kcteam.features.member.presentation

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
import com.kcteam.app.domain.MemberShopEntity
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.member.model.TeamShopListDataModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog

/**
 * Created by Saikat on 09-Jun-20.
 */
class UpdateMemberShopAddressDialog : DialogFragment() {

    private lateinit var shop_name_TV: AppCustomTextView
    private lateinit var address_EDT: AppCustomEditText
    private lateinit var update_TV: AppCustomTextView
    private lateinit var new_address_EDT: AppCustomEditText
    private lateinit var mContext: Context

    companion object {
        private lateinit var addressUpdateClickListener: (Any) -> Unit
        private lateinit var loc: Location
        private var mTeam: TeamShopListDataModel? = null
        private var mLocalTeam: MemberShopEntity? = null

        fun getInstance(team: Any, location: Location, listener: (Any) -> Unit): UpdateMemberShopAddressDialog {
            val mUpdateShopAddressDialog = UpdateMemberShopAddressDialog()

            if (team is TeamShopListDataModel)
                mTeam = team
            else if (team is MemberShopEntity)
                mLocalTeam = team

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
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_update_shop_address, container, false)

        initView(v)
        return v
    }

    private fun initView(v: View) {
        shop_name_TV = v.findViewById(R.id.shop_name_TV)
        address_EDT = v.findViewById(R.id.address_EDT)
        update_TV = v.findViewById(R.id.update_TV)
        new_address_EDT = v.findViewById(R.id.new_address_EDT)

        if (mTeam != null) {
            mTeam!!.apply {
                try {
                    shop_lat = loc.latitude.toString()
                    shop_long = loc.longitude.toString()
                    shop_pincode = LocationWizard.getPostalCode(mContext, loc.latitude, loc.longitude)
                    new_address_EDT.setText(LocationWizard.getLocationName(mContext, loc.latitude, loc.longitude))
                } catch (e: Exception) {
                    e.printStackTrace()
                    XLog.e("Update Shop Address", "Address calculation error(From team shop list)=========> " + e.localizedMessage + " for shop " + shop_name)
                    new_address_EDT.setText("Unknown")
                }
            }
        } else if (mLocalTeam != null) {
            mLocalTeam!!.apply {
                try {
                    shop_lat = loc.latitude.toString()
                    shop_long = loc.longitude.toString()
                    shop_pincode = LocationWizard.getPostalCode(mContext, loc.latitude, loc.longitude)
                    new_address_EDT.setText(LocationWizard.getLocationName(mContext, loc.latitude, loc.longitude))
                } catch (e: Exception) {
                    e.printStackTrace()
                    XLog.e("Update Shop Address", "Address calculation error(From team shop list)=========> " + e.localizedMessage + " for shop " + shop_name)
                    new_address_EDT.setText("Unknown")
                }
            }
        }


        update_TV.setOnClickListener {

            when {
                TextUtils.isEmpty(address_EDT.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage("Please enter current address")
                TextUtils.isEmpty(new_address_EDT.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage("Please enter new address")
                else -> {
                    if (mTeam!=null) {
                        mTeam?.shop_address = new_address_EDT.text.toString()
                        addressUpdateClickListener(mTeam!!)
                    }
                    else if (mLocalTeam != null) {
                        mLocalTeam?.shop_address = new_address_EDT.text.toString()
                        addressUpdateClickListener(mLocalTeam!!)
                    }
                    dialog?.dismiss()
                }
            }
        }

        if (mTeam != null) {
            shop_name_TV.text = mTeam?.shop_name
            address_EDT.setText(mTeam?.shop_address)
        }
        else if (mLocalTeam != null) {
            shop_name_TV.text = mLocalTeam?.shop_name
            address_EDT.setText(mLocalTeam?.shop_address)
        }
    }
}
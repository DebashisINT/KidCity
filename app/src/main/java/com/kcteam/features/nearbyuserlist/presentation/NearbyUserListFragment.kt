package com.kcteam.features.nearbyuserlist.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.nearbyuserlist.api.NearbyUserRepoProvider
import com.kcteam.features.nearbyuserlist.model.NearbyUserDataModel
import com.kcteam.features.nearbyuserlist.model.NearbyUserResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NearbyUserListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_user_list: RecyclerView
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_nearby_user_list, container, false)

        initView(view)
        getUserList()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            rv_user_list = findViewById(R.id.rv_user_list)
            tv_no_data = findViewById(R.id.tv_no_data)
            progress_wheel = findViewById(R.id.progress_wheel)
        }

        progress_wheel.stopSpinning()
        rv_user_list.layoutManager = LinearLayoutManager(mContext)
    }

    private fun getUserList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = NearbyUserRepoProvider.getNearbyUserListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.nearbyUserList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as NearbyUserResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val newUserList = ArrayList<NearbyUserDataModel>()

                                response.user_list?.forEach {
                                    if (!TextUtils.isEmpty(it.latitude) && !TextUtils.isEmpty(it.longitude)) {
                                        val distance = LocationWizard.getDistance(it.latitude.toDouble(), it.longitude.toDouble(), Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())

                                        if(distance * 1000 <= Pref.gpsAccuracy.toDouble())
                                            newUserList.add(it)
                                    }
                                }

                                progress_wheel.stopSpinning()

                                if (newUserList.isNotEmpty())
                                    initAdapter(newUserList)
                                else
                                    (mContext as DashboardActivity).showSnackMessage("No nearby user available")
                            }
                            else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun initAdapter(newUserList: ArrayList<NearbyUserDataModel>) {
        tv_no_data.visibility = View.GONE
        rv_user_list.adapter = NearbyUserListAdapter(mContext, newUserList, {
            IntentActionable.initiatePhoneCall(mContext, newUserList[it].phone_no)
        }, {
            val url = "https://api.whatsapp.com/send?phone=+91${newUserList[it].phone_no}"

            try {
                val pm = mContext.packageManager
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            } catch (e: PackageManager.NameNotFoundException ) {
                e.printStackTrace()
                (mContext as DashboardActivity).showSnackMessage("Whatsapp app not installed in your phone.")
            }
            catch (e: java.lang.Exception) {
                e.printStackTrace()
                (mContext as DashboardActivity).showSnackMessage("This is not whatsApp no.")
            }
        },{
            val uri = Uri.parse("smsto:${newUserList[it].phone_no}")
            val intent = Intent(Intent.ACTION_SENDTO, uri)
            //intent.putExtra("sms_body", "The SMS text")
            startActivity(intent)
        })
    }
}
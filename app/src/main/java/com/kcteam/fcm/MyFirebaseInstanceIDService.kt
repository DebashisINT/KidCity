package com.kcteam.fcm

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.fcm.api.UpdateDeviceTokenRepoProvider
import com.elvishew.xlog.XLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 20-09-2018.
 */

class MyFirebaseInstanceIDService{ //: FirebaseInstanceIdService() {

    /*@SuppressLint("WrongThread")
    override fun onTokenRefresh() {
        super.onTokenRefresh()

        Log.e("FCMInstanceIDService","=======Calling========")


//        object : AsyncTask<Void, Void, String>() {
//
//            override fun doInBackground(vararg voids: Void): String {
//
//                var refreshedToken = FirebaseInstanceId.getInstance().token
//
//                while (refreshedToken == null) {
//                    refreshedToken = FirebaseInstanceId.getInstance().token
//                }
//
//                Log.e(TAG, "Device Token====> $refreshedToken")
//
//
//                return refreshedToken
//            }
//
//            override fun onPostExecute(refreshedToken: String) {
//                super.onPostExecute(refreshedToken)
//
//                if (!TextUtils.isEmpty(Pref.user_id)) {
//
//                    /*new AsyncUpdateDeviceToken (getApplicationContext(), SomaxApplication.getPreference().getDeviceToken(), refreshedToken,
//                    SomaxApplication.getPreference().getLoginSessionID()).execute();*/
//                }
//
//                Pref.deviceToken = refreshedToken
//            }
//        }.execute()


        doAsync {

            var refreshedToken = FirebaseInstanceId.getInstance().token

            while (refreshedToken == null) {
                refreshedToken = FirebaseInstanceId.getInstance().token
            }

            //Log.e(TAG, "Device Token====> $refreshedToken")
            XLog.e("MyFirebaseInstanceIDService : \nDevice Token=====> $refreshedToken")

            uiThread {

                if (!TextUtils.isEmpty(Pref.user_id)) {

                    /*new AsyncUpdateDeviceToken (getApplicationContext(), SomaxApplication.getPreference().getDeviceToken(), refreshedToken,
                    SomaxApplication.getPreference().getLoginSessionID()).execute();*/


                    doAsync {

                        callUpdateDeviceTokenApi(refreshedToken)

                        uiThread {

                        }
                    }
                }

                Pref.deviceToken = refreshedToken
            }
        }
    }

    private fun callUpdateDeviceTokenApi(refreshedToken: String?) {

        if (!AppUtils.isOnline(applicationContext))
            return

        val repository = UpdateDeviceTokenRepoProvider.updateDeviceTokenRepoProvider()

        BaseActivity.compositeDisposable.add(
                repository.updateDeviceToken(refreshedToken!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("UpdateDeviceTokenResponse : " + "\n" + "Status====> " + response.status + ", Message===> " + response.message)

                        }, { error ->
                            error.printStackTrace()
                            XLog.d("UpdateDeviceTokenResponse ERROR: " + error.localizedMessage + "\n" + "Username :" + Pref.user_name + ", Time :" + AppUtils.getCurrentDateTime())
                        })
        )
    }

    companion object {
        private val TAG = MyFirebaseInstanceIDService::class.java.simpleName
    }*/
}

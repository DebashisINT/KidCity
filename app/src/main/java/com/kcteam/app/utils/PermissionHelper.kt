package com.themechangeapp.pickimage

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by Pratishruti on 15-09-2017.
 */
class PermissionHelper {

    companion object {
        var REQUEST_CODE_CAMERA = 100
        var REQUEST_CODE_STORAGE = 102
        var REQUEST_CODE_PHONE_STATE = 104

        val TAG_LOCATION_RESULTCODE = 1001

        var REQUEST_CODE_DOCUMENT = 105
        var REQUEST_CODE_AUDIO = 123
        var REQUEST_CODE_GET_FILE = 111
        var REQUEST_CODE_EXO_PLAYER = 99

        internal var PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        internal var PERMISSIONS_LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        fun checkCameraPermission(activity: Activity): Boolean {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, arrayOf<String>(Manifest.permission.CAMERA), PermissionHelper.REQUEST_CODE_CAMERA)
                return false
            } else {
                return true
            }
        }


        fun checkPhoneStatePermisssion(activity: Activity): Boolean {
            return if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(activity, arrayOf<String>(Manifest.permission.READ_PHONE_STATE), PermissionHelper.REQUEST_CODE_PHONE_STATE)
                false
            } else {
                true
            }
        }


        fun checkStoragePermission(mActivity: Activity): Boolean {

            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    ActivityCompat.requestPermissions(mActivity, PERMISSIONS_STORAGE, REQUEST_CODE_STORAGE)
                } else {
                    ActivityCompat.requestPermissions(mActivity, PERMISSIONS_STORAGE, REQUEST_CODE_STORAGE)
                }
                return false
            } else {
                return true
            }
        }

        fun checkLocationPermission(mActivity: Activity, mRequestCode: Int): Boolean {
            var requestCode: Int
            if (mRequestCode == 0)
                requestCode = TAG_LOCATION_RESULTCODE
            else
                requestCode = mRequestCode

            if (ContextCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(mActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(mActivity, PERMISSIONS_LOCATION, requestCode)
                } else {
                    ActivityCompat.requestPermissions(mActivity, PERMISSIONS_LOCATION, requestCode)
                }
                return false
            } else {
                return true
            }
        }


    }
}
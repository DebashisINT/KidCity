package com.kcteam.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by Saikat on 14-11-2018.
 */
class PermissionUtils(private val activity: Activity, private val listener: OnPermissionListener,
                      private val requestedPermissions: Array<String>) {

    private val REQUEST_CODE = 20

    init {
        requestPermission()
    }

    /*companion object {

        //private var permissionObject: PermissionUtils? = null

        fun getInstance(): PermissionUtils {

            return PermissionUtils(activity)
        }
    }*/

    private fun requestPermission() {
        var permissionCheck = PackageManager.PERMISSION_GRANTED
        var shouldShowRequestPermissionRationale = false
        for (permission in requestedPermissions) {
            permissionCheck += ContextCompat.checkSelfPermission(activity, permission)
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale) {
                ActivityCompat.requestPermissions(activity, requestedPermissions, REQUEST_CODE)
            } else {
                ActivityCompat.requestPermissions(activity, requestedPermissions, REQUEST_CODE)
            }

        } else {
            listener.onPermissionGranted()
        }
    }

    @SuppressLint("MissingPermission")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            val permissionCheck = PackageManager.PERMISSION_GRANTED + grantResults.sum()
            if (grantResults.isNotEmpty() && permissionCheck == PackageManager.PERMISSION_GRANTED) {
                listener.onPermissionGranted()
            } else {
                for (i in grantResults.indices) {
                    if (grantResults[i] == -1) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE)
                            break
                        } else {
                            /*Toaster.msgShort(activity, "Please accept permissions from Settings.")
                            Handler().postDelayed(Runnable { activity.finish() }, 1500)*/
                            listener.onPermissionNotGranted()
                            break
                        }
                    }
                }
            }
        }
    }

    interface OnPermissionListener {
        fun onPermissionGranted()

        fun onPermissionNotGranted()
    }
}
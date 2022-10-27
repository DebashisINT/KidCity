package com.kcteam.features.addshop.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import org.jetbrains.anko.runOnUiThread


class ScanImageFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var surface_view: SurfaceView
    private lateinit var mCameraSource: CameraSource
    private lateinit var rl_scan_image_main: RelativeLayout
    private lateinit var tv_scan: AppCustomTextView
    private lateinit var tv_copy_scan: AppCustomTextView

    private var permissionUtils: PermissionUtils? = null
    private var isScanFuelCard = false
    var isCopy = false

    val stringArrays: ArrayList<String> by lazy {
        ArrayList<String>()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_scan_image, container, false)

        initView(view)
        initClickListener()

        return view
    }

    private fun initView(view: View) {
        tv_scan = view.findViewById(R.id.tv_scan)
        surface_view = view.findViewById(R.id.surface_view)
        rl_scan_image_main = view.findViewById(R.id.rl_scan_image_main)
        tv_copy_scan = view.findViewById(R.id.tv_copy_scan)

        val textRecognizer = TextRecognizer.Builder(mContext).build()

        if (!textRecognizer.isOperational) {
            Log.e("Scan Image", "Detector dependencies are not available!")
        } else {
            mCameraSource = CameraSource.Builder(mContext, textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build()
        }

        surface_view.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mCameraSource.stop()
            }

            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheck()
                    else
                        mCameraSource.start(surface_view.holder)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        textRecognizer.setProcessor(object : Detector.Processor<TextBlock?> {
            override fun release() {}
            override fun receiveDetections(detections: Detections<TextBlock?>) {

                if (!isScanFuelCard)
                    return

                val items: SparseArray<TextBlock?> = detections.detectedItems

                if (items == null || items.size() == 0) {
                    Log.e("Scan Image", "===========Item is empty============")
                    return
                }

                mContext.runOnUiThread {
                    isScanFuelCard = false
                    stringArrays.clear()
                    for (i in 0 until items.size()) {
                        if (items.get(i)?.components != null) {
                            for (j in items.get(i)?.components?.indices!!) {
                                stringArrays.add(items.get(i)?.components?.get(j)?.value!!)
                            }
                        }
                    }

                    (mContext as DashboardActivity).onBackPressed()
                }
            }
        })

    }

    private fun initClickListener() {
        tv_scan.setOnClickListener(this)
        rl_scan_image_main.setOnClickListener(null)
        tv_copy_scan.setOnClickListener(this)
    }

    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                mCameraSource.start(surface_view.holder)
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_scan -> {
                isScanFuelCard = true
                isCopy = false
            }

            R.id.tv_copy_scan -> {
                isScanFuelCard = true
                isCopy = true
            }
        }
    }
}
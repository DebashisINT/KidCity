package com.kcteam.features.device_info.presentation

import android.content.Context
import android.hardware.Camera
import android.os.Build
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.BatteryNetStatusEntity
import com.kcteam.app.utils.AppUtils
import kotlinx.android.synthetic.main.inflate_device_info_item.view.*
import kotlin.math.roundToInt

class DeviceInfoAdapter(private val mContext: Context, private val list: ArrayList<BatteryNetStatusEntity>?,
                        private val onSyncClick: (BatteryNetStatusEntity) -> Unit) : RecyclerView.Adapter<DeviceInfoAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_device_info_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.date_time))
                    tv_date.text = AppUtils.convertToNotificationDateTime(list?.get(adapterPosition)?.date_time!!)
                else
                    tv_date.text = "N.A."

                tv_battery.text = list?.get(adapterPosition)?.bat_level + "%"

                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.net_type)) {
                    tv_network.text = "Online"

                    if (list?.get(adapterPosition)?.net_type?.equals("Wifi", ignoreCase = true)!!)
                        tv_net_type.text = list[adapterPosition].net_type
                    else
                        tv_net_type.text = list[adapterPosition].net_type + " " + list[adapterPosition].mob_net_type
                }
                else {
                    tv_network.text = "Offline"
                    tv_net_type.text = "N.A."
                }

                if (list?.get(adapterPosition)?.isUploaded!!)
                    sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                else {
                    sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    sync_icon.setOnClickListener {
                        onSyncClick(list?.get(adapterPosition))
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                    tv_back_camera.text = "Unable to detected"
                    tv_front_camera.text =  "Unable to detected"
                }
                else{
                    tv_back_camera.text = getBackCameraResolutionInMp().roundToInt().toString() + " Megapixels "
                    tv_front_camera.text = getFrontCameraResolutionInMp().roundToInt().toString()+ " Megapixels "
                }

            }
        }
    }

    fun getBackCameraResolutionInMp(): Float {
        val noOfCameras: Int = Camera.getNumberOfCameras()
        var maxResolution = -1f
        var pixelCount: Long = -1
        for (i in 0 until noOfCameras) {
            val cameraInfo: Camera.CameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing === Camera.CameraInfo.CAMERA_FACING_BACK) {
                val camera: Camera = Camera.open(i)
                val cameraParams: Camera.Parameters = camera.getParameters()
                for (j in 0 until cameraParams.getSupportedPictureSizes().size) {
                    val pixelCountTemp: Int = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height // Just changed i to j in this loop
                    if (pixelCountTemp > pixelCount) {
                        pixelCount = pixelCountTemp.toLong()
                        maxResolution = pixelCountTemp.toFloat() / 1024000.0f

                    }
                }
                camera.release()
            }
        }
        return maxResolution
    }

    fun getFrontCameraResolutionInMp(): Float {
        val noOfCameras: Int = Camera.getNumberOfCameras()
        var maxResolution = -1f
        var pixelCount: Long = -1
        for (i in 0 until noOfCameras) {
            val cameraInfo: Camera.CameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(i, cameraInfo)
            if (cameraInfo.facing === Camera.CameraInfo.CAMERA_FACING_FRONT) {
                val camera: Camera = Camera.open(i)
                val cameraParams: Camera.Parameters = camera.getParameters()
                for (j in 0 until cameraParams.getSupportedPictureSizes().size) {
                    val pixelCountTemp: Int = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height // Just changed i to j in this loop
                    if (pixelCountTemp > pixelCount) {
                        pixelCount = pixelCountTemp.toLong()
                        maxResolution = pixelCountTemp.toFloat() / 1024000.0f

                    }
                }
                camera.release()
            }
        }
        return maxResolution
    }
}
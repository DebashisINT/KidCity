package com.kcteam.features.location.model

import com.kcteam.app.domain.NewGpsStatusEntity
import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 17-Aug-20.
 */
data class AppInfoInputModel(var session_token: String = "",
                             var user_id: String = "",
                             var app_info_list: ArrayList<AppInfoDataModel>?= null,
                             var total_visit_revisit_count: String = "",
                             var total_visit_revisit_count_synced:String ="",
                             var total_visit_revisit_count_unsynced:String = "",
                             var power_saver_status:String = ""): Serializable

data class AppInfoDataModel(var id: String = "",
                            var date_time: String = "",
                            var battery_status: String = "",
                            var battery_percentage: String = "",
                            var network_type: String = "",
                            var mobile_network_type: String = "",
                            var device_model: String = "",
                            var android_version: String = "",
                            var Available_Storage: String = "",
                            var Total_Storage:String = "",
                            var power_saver_status:String = ""): Serializable

data class AppInfoResponseModel(var app_info_list: ArrayList<AppInfoDataModel>?= null): BaseResponse(), Serializable

data class VisitRemarksResponseModel(var remarks_list: ArrayList<VisitRemarksDataModel>?= null) : BaseResponse(), Serializable

data class VisitRemarksDataModel(var id: String = "",
                                 var name: String = "") : Serializable

data class GpsNetInputModel(var session_token: String = "",
                            var user_id: String = "",
                            var gps_net_status_list: ArrayList<NewGpsStatusEntity>? = null): Serializable
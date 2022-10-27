package com.kcteam.features.addAttendence.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 05-Aug-20.
 */
data class LeaveListResponseModel(var leave_list: ArrayList<LeaveListDataModel>? = null) : BaseResponse(), Serializable

data class LeaveListDataModel(var id: String = "",
                              var from_date: String = "",
                              var to_date: String = "",
                              var leave_type: String = "",
                              var desc: String = "",
                              var status: String = "") : Serializable

data class LocationListResponseModel(var loc_list: ArrayList<LocationDataModel>?= null): BaseResponse(), Serializable

data class LocationDataModel(var id: String = "",
                             var location: String = "",
                             var lattitude: String = "",
                             var longitude: String = ""): Serializable

data class DistanceResponseModel(var distance: String = ""): Serializable, BaseResponse()
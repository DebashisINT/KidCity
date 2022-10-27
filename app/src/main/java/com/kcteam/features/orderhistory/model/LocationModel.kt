package com.kcteam.features.orderhistory.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

data class UnknownReponseModel(var location_list: ArrayList<LocationDataModel>?= null): BaseResponse(), Serializable

data class LocationDataModel(var id: String = "",
                             var location_name: String = "",
                             var latitude: String = "",
                             var longitude: String = ""): Serializable

data class SubmitLocationInputModel(var session_token: String = "",
                                    var user_id: String = "",
                                    var location_list: ArrayList<LocationDataModel>?= null): Serializable
package com.kcteam.features.orderhistory.model

/**
 * Created by Pratishruti on 23-11-2017.
 */
class LocationUpdateRequest {
    var session_token:String?=null
    var user_id:String?=null
    var location_details:List<LocationData>?= null
}
package com.kcteam.features.location.model

class ShopRevisitStatusRequest {
    var user_id: String? = null
    var session_token: String? = null
    var ordernottaken_list: List<ShopRevisitStatusRequestData>? = null
}
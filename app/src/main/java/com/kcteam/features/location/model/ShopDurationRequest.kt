package com.kcteam.features.location.model

/**
 * Created by Pratishruti on 28-11-2017.
 */
class ShopDurationRequest {
    var session_token: String? = null
    var user_id: String? = null
    var shop_list: List<ShopDurationRequestData>? = null
    var status: String? = null
    var message: String? = null
}
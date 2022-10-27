package com.kcteam.features.averageshop.model

/**
 * Created by Pratishruti on 07-12-2017.
 */
class ShopActivityResponseShopList {
    var shopid: String? = null
    var duration_spent: String? = null
    var date: String? = null
    var shop_name: String? = null
    var shop_address: String? = null
    var visited_date: String? = null

    var device_model: String? = null
    var android_version: String? = null
    var battery: String? = null
    var net_status: String? = null
    var net_type: String? = null

    var in_time: String? = null
    var out_time: String? = null
    var start_timestamp: String? = null
    var in_location: String? = null
    var out_location: String? = null

    var Key: String? = null

    var agency_name: String? = null
    var pros_id: String? = null
    var approximate_1st_billing_value: Double? = 0.00

//    "date": "2017-11-12",--------------yyyy-MM-dd
//    "shopid": "378_1512478115504",
//    "duration_spent": "17:00:08.0000000",
//    "shop_name": "sdfg",
//    "shop_address": "L2, GP Block, Sector V, Salt Lake City, Kolkata, West Bengal 700091, India",
//    "visited_date": "2017-11-12T00:00:00"  "yyyy-MM-dd'T'HH:mm:ss"

}
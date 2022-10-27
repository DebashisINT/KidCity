package com.kcteam.features.location.model


/**
 * Created by Pratishruti on 28-11-2017.
 */
class ShopDurationRequestData {

    var shop_id: String? = null
    var visited_date: String? = null
    var visited_time: String? = null
    var spent_duration: String? = null
    var total_visit_count: String? = null
    var distance_travelled: String? = null
    var feedback: String? = null
    var isFirstShopVisited: Boolean? = null
    var distanceFromHomeLoc: String? = null
    var next_visit_date: String? = null
    var early_revisit_reason: String? = null
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
    var shop_revisit_uniqKey: String? = null

    var agency_name: String? = null
    var approximate_1st_billing_value: String?= null
    var updated_by: String?= null
    var updated_on: String?= null
    var pros_id: String?= null


//    "visited_date": "yyyy-MM-dd",
//    "visited_time": "yyyy-MM-dd HH:mm:ss",
//    "spent_duration": "HH:mm:ss"

    override fun equals(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj !is ShopDurationRequestData)
            return false
        if (obj === this)
            return true
        if (this.shop_id === (obj as ShopDurationRequestData).shop_id && this.visited_date === (obj as ShopDurationRequestData).visited_date)
            return true
        return false
    }

    override fun hashCode(): Int {
        /* var result: Int
         var temp: Long
         temp = shop_id?.toLong()!!
         result = (temp xor temp.ushr(32)).toInt()
         temp = visited_date?.toLong()!!
         result = 31 * result + (temp xor temp.ushr(32)).toInt()
         return result*/

        var result = 17
        result = 31 * result + shop_id?.hashCode()!!
        result = 31 * result + visited_date?.hashCode()!!
        return result

        //return 1
    }
}
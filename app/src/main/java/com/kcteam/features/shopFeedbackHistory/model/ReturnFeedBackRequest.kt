package com.kcteam.features.shopFeedbackHistory.model


class ReturnFeedBackRequest {
    var status: String? = null
    var message: String? = null
    var user_id: String? = null
    var shop_list: List<shop_list>? = null
}
 class shop_list {
    var feedback: String? = null
    var date_time: String? = null
}
package com.kcteam.features.orderList.model


class ReturnDataModel {
    var shop_id: String? = null
    var shop_name: String? = null
    var address: String? = null
    var pin_code: String? = null
    var shop_lat: String? = null
    var shop_long: String? = null
    var return_id: String? = null
    var return_date_time: String? = null
    var return_amount: String? = null
    var return_lat: String? = null
    var return_long: String? = null
    var product_list: ArrayList<NewProductListDataModel>? = null
}

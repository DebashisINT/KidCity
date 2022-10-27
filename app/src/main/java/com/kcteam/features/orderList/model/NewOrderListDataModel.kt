package com.kcteam.features.orderList.model

/**
 * Created by Saikat on 03-12-2018.
 */
class NewOrderListDataModel {
    var shop_id: String? = null
    var shop_name: String? = null
    var address: String? = null
    var pin_code: String? = null
    var shop_lat: String? = null
    var shop_long: String? = null
    var order_id: String? = null
    var order_date_time: String? = null
    var order_amount: String? = null
    var order_lat: String? = null
    var order_long: String? = null
    var patient_no: String?= null
    var patient_name: String?= null
    var patient_address: String?= null
    var scheme_amount: String? = null
    var Hospital: String?= null
    var Email_Address: String?= null
    var product_list: ArrayList<NewProductListDataModel>? = null
}
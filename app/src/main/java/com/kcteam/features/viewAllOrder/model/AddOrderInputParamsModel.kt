package com.kcteam.features.viewAllOrder.model

/**
 * Created by Saikat on 20-11-2018.
 */
class AddOrderInputParamsModel {
    var session_token: String? = null
    var user_id: String? = null
    var order_amount: String? = null
    var shop_id: String? = null
    var order_id: String? = null
    var description: String? = null
    var collection: String? = null
    var order_date: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var address: String? = null
    var remarks: String? = null
    var patient_no: String? = null
    var patient_name: String? = null
    var patient_address: String? = null
    var scheme_amount: String? = null
    var Hospital: String? = null
    var Email_Address: String? = null
    var product_list: ArrayList<AddOrderInputProductList>? = null
}
package com.kcteam.features.billing.model

import com.kcteam.features.viewAllOrder.model.AddOrderInputProductList

/**
 * Created by Saikat on 20-02-2019.
 */
class AddBillingInputParamsModel {
    var session_token = ""
    var user_id = ""
    var bill_id = ""
    var invoice_no = ""
    var invoice_date = ""
    var invoice_amount = ""
    var remarks = ""
    var order_id = ""
    var patient_no = ""
    var patient_name = ""
    var patient_address = ""
    var product_list: ArrayList<AddOrderInputProductList>?=null
}
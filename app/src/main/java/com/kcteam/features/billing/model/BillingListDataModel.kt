package com.kcteam.features.billing.model

import com.kcteam.features.orderList.model.NewProductListDataModel

/**
 * Created by Saikat on 20-02-2019.
 */
class BillingListDataModel {
    var bill_id: String? = null
    var invoice_no: String? = null
    var invoice_date: String? = null
    var invoice_amount: String? = null
    var remarks: String? = null
    var order_id: String? = null
    var billing_image: String? = null
    var total_amount: String? = null
    var paid_amount: String? = null
    var bal_amount: String? = null
    var patient_no: String? = null
    var patient_name: String? = null
    var patient_address: String? = null
    var product_list: ArrayList<NewProductListDataModel>? = null
}
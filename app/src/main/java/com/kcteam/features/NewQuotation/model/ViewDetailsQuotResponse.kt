package com.kcteam.features.NewQuotation.model


data class ViewDetailsQuotResponse(var status: String? = null,
                                   var message: String? = null,
                                   var quotation_number: String? = null,
                                   var save_date_time: String? = null,
                                   var quotation_date_selection: String? = null,
                                   var project_name: String? = null,
                                   var taxes: String? = null,
                                   var Freight: String? = null,
                                   var delivery_time: String? = null,
                                   var payment: String? = null,
                                   var validity: String? = null,
                                   var billing: String? = null,
                                   var product_tolerance_of_thickness: String? = null,
                                   var tolerance_of_coating_thickness: String? = null,
                                   var salesman_user_id: String? = null,
                                   var shop_id: String? = null,
                                   var shop_name: String? = null,
                                   var shop_phone_no: String? = null,
                                   var quotation_created_lat: String? = null,
                                   var quotation_created_long: String? = null,
                                   var quotation_created_address: String? = null,
                                   var shop_addr: String? = null,
                                   var shop_email: String? = null,
                                   var shop_owner_name: String? = null,
                                   var salesman_name: String? = null,
                                   var salesman_designation: String? = null,
                                   var salesman_login_id: String? = null,
                                   var salesman_email: String? = null,
                                   var salesman_phone_no: String? = null,
                                   var Remarks: String? = null,
                                   var document_number: String? = null,
                                   var shop_address_pincode: String? = null,
                                   var quotation_product_details_list: ArrayList<quotation_product_details_list>? = null

)

data class quotation_product_details_list(
        var product_id: Int? = null,
        var product_name: String? = null,
        var color_id: String? = null,
        var color_name: String? = null,
        var rate_sqft: String? = null,
        var rate_sqmtr: String? = null,
        var qty: Int? = null,
        var amount: Double? = null
        )
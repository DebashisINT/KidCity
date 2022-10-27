package com.kcteam.features.NewQuotation.model


class AddQuotRequestData {
    var quotation_number:String?=null
    var user_id: String?=null
    var save_date_time: String?=null
    var quotation_date_selection: String?=null
    var project_name: String?=null
    var taxes: String?=null
    var Freight: String?=null
    var delivery_time: String?=null
    var payment: String?=null
    var validity: String?=null
    var billing: String?=null
    var product_tolerance_of_thickness: String?=null
    var tolerance_of_coating_thickness:String?=null
    var salesman_user_id:String?=null
    var shop_id:String?=null
    var quotation_created_lat:String?=null
    var quotation_created_long:String?=null
    var quotation_created_address:String?=null
    var product_list: ArrayList<product_list>? = null
    var Remarks:String?=null
    var document_number:String?=null
    var quotation_status:String?=null

}
data class product_list (var product_name:String?=null,
                         var product_id:String?=null,
                         var color_id:String?=null,
                         var color_name:String?=null,
                         var rate_sqft:String?=null,
                         var rate_sqmtr:String?=null,
                         var qty:String?=null,
                         var amount:String?=null)




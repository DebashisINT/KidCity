package com.kcteam.features.NewQuotation.model


class EditQuotRequestData {
        var updated_by_user_id:String?=null
        var updated_date_time: String?=null
        var quotation_number: String?=null
        var quotation_date_selection: String?=null
        var project_name: String?=null
        var taxes: String?=null
        var Freight: String?=null
        var delivery_time: String?=null
        var payment: String?=null
        var validity: String?=null
        var billing: String?=null
        var product_tolerance_of_thickness: String?=null
        var tolerance_of_coating_thicknes:String?=null
        var salesman_user_id:String?=null
        var shop_id:String?=null
        var quotation_updated_lat:String?=null
        var quotation_updated_long:String?=null
        var quotation_updated_address:String?=null
        var product_list: ArrayList<quotation_product_details_list>? = null

    }

    data class product_lists (var product_id:String?=null,
                             var color_id:String?=null,
                             var rate_sqft:String?=null,
                             var rate_sqmtr:String?=null,
                             var qty:String?=null,
                             var amount:String?=null)


package com.kcteam.features.damageProduct.model


data class DamageProductResponseModel(var status:String?=null,
                            var message:String?=null,
                            var user_id:String?=null,
                            var user_name:String?=null,
                            var shop_id:String?=null,
                            var shop_name:String?=null,
                            var breakage_list: ArrayList<Shop_wise_breakage_list>? = null

)
data class Shop_wise_breakage_list(
    var date_time :String?=null,
    var product_id :String?=null,
    var product_name :String?=null,
    var breakage_number :String?=null,
    var description_of_breakage :String?=null,
    var customer_feedback :String?=null,
    var remarks :String?=null,
    var image_link :String?=null,
)
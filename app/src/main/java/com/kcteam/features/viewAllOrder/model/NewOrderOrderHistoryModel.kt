package com.kcteam.features.viewAllOrder.model

class NewOrderOrderHistoryModel {

    var status:String ? = null
    var message:String ? = null
    var Shop_list:ArrayList<data_list>?=null
}

data class data_list(var shop_name:String,var shop_id:String,var owner_name:String,var PhoneNumber:String,var OrderList:ArrayList<order_list>)

data class order_list(var order_id:String,var order_date:String,var product_list:ArrayList<product_list>)

data class product_list(var product_id:Int,var product_name:String,var gender:String,var color_list:ArrayList<color_list>)

data class color_list(var size:String,var qty:Int,var color_id:String)


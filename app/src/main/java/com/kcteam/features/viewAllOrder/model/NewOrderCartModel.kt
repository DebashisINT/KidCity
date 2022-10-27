package com.kcteam.features.viewAllOrder.model

class NewOrderCartModel {
    var product_id: String = ""
    var product_name: String = ""
    var gender: String = ""
    var color_list: ArrayList<ColorList> = ArrayList()
    var rate:String = "0"
    //var color_name:String= ""
    //var order_list:ArrayList<ProductOrder> = ArrayList()
}

data class ColorList(var color_name: String, var color_id: String, var order_list: ArrayList<ProductOrder> = ArrayList())

//data class ProductOrder(var size:String,var qty:String)

data class ProductOrder(var size: String, var qty: String,var isCheckedStatus: Boolean?=false)

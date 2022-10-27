package com.kcteam.features.stock.model

import com.kcteam.features.viewAllOrder.model.AddOrderInputProductList


/**
 * Created by Saikat on 17-09-2019.
 */
class AddStockInputParamsModel {
    var session_token: String? = null
    var user_id: String? = null
    var stock_amount: String? = null
    var shop_id: String? = null
    var stock_id: String? = null
    var stock_date_time: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var address: String? = null
    var shop_type: String? = null
    var product_list: ArrayList<AddOrderInputProductList>? = null
}
package com.kcteam.features.returnsOrder

import com.kcteam.features.stockCompetetorStock.ShopAddCompetetorStockProductList


class ReturnRequest {
    var user_id: String? = null
    var session_token: String? = null
    var return_amount: String? = null
    var shop_id: String? = null
    var return_id: String? = null
    var description: String? = null
    var return_date_time: String? = null
    var latitude: String? = null
    var longitude: String? = null
    var address: String? = null
    var return_list: List<ReturnProductList>? = null

}
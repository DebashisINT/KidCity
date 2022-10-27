package com.kcteam.features.viewAllOrder.model

import com.kcteam.features.stockCompetetorStock.ShopAddCompetetorStockProductList
import com.kcteam.features.viewAllOrder.orderNew.NeworderScrCartFragment

class NewOrderSaveApiModel {
    var user_id: String? = null
    var session_token: String? = null
    var order_id: String? = null
    var shop_id: String? = null
    var order_date: String? = null
    var product_list: List<NeworderScrCartFragment.NewOrderRoomData>? = null
}
package com.kcteam.features.stock.model

import com.kcteam.features.orderList.model.NewProductListDataModel


/**
 * Created by Saikat on 17-09-2019.
 */
class StockListDataModel {
    var shop_id: String? = null
    var shop_name: String? = null
    var address: String? = null
    var pin_code: String? = null
    var shop_lat: String? = null
    var shop_long: String? = null
    var stock_id: String? = null
    var stock_date_time: String? = null
    var stock_amount: String? = null
    var stock_qty: String? = null
    var product_list: ArrayList<NewProductListDataModel>? = null
}
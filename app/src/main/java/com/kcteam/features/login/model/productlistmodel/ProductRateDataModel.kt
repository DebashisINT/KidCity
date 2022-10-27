package com.kcteam.features.login.model.productlistmodel

import java.io.Serializable

/**
 * Created by Saikat on 15-01-2020.
 */
class ProductRateDataModel : Serializable {
    var product_id = ""
    var rate = ""
    var stock_amount = ""
    var stock_unit = ""
    var isStockShow = false
    var isRateShow = false
}
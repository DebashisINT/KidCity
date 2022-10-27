package com.kcteam.features.viewAllOrder.model

import com.kcteam.app.domain.NewOrderColorEntity
import com.kcteam.app.domain.NewOrderGenderEntity
import com.kcteam.app.domain.NewOrderProductEntity
import com.kcteam.app.domain.NewOrderSizeEntity
import com.kcteam.features.stockCompetetorStock.model.CompetetorStockGetDataDtls

class NewOrderDataModel {
    var status:String ? = null
    var message:String ? = null
    var Gender_list :ArrayList<NewOrderGenderEntity>? = null
    var Product_list :ArrayList<NewOrderProductEntity>? = null
    var Color_list :ArrayList<NewOrderColorEntity>? = null
    var size_list :ArrayList<NewOrderSizeEntity>? = null
}


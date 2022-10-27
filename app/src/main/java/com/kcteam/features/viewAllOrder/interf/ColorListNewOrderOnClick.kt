package com.kcteam.features.viewAllOrder.interf

import com.kcteam.app.domain.NewOrderColorEntity
import com.kcteam.app.domain.NewOrderProductEntity

interface ColorListNewOrderOnClick {
    fun productListOnClick(color: NewOrderColorEntity)
}
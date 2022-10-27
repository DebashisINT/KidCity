package com.kcteam.features.viewAllOrder.interf

import com.kcteam.app.domain.NewOrderGenderEntity
import com.kcteam.app.domain.NewOrderProductEntity

interface ProductListNewOrderOnClick {
    fun productListOnClick(product: NewOrderProductEntity)
}
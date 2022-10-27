package com.kcteam.features.viewAllOrder.interf

import com.kcteam.app.domain.NewOrderProductEntity
import com.kcteam.app.domain.NewOrderSizeEntity

interface SizeListNewOrderOnClick {
    fun sizeListOnClick(size: NewOrderSizeEntity)
}
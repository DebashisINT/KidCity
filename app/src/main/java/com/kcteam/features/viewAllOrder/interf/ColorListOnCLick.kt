package com.kcteam.features.viewAllOrder.interf

import com.kcteam.app.domain.NewOrderGenderEntity
import com.kcteam.features.viewAllOrder.model.ProductOrder

interface ColorListOnCLick {
    fun colorListOnCLick(size_qty_list: ArrayList<ProductOrder>, adpPosition:Int)
}
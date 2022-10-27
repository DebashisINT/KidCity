package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.SHOP_TYPE_STOCK_VIEW_STATUS)
class ShopTypeStockViewStatus {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "shoptype_id")
    var shoptype_id: String? = null

    @ColumnInfo(name = "shoptype_name")
    var shoptype_name: String? = null

    @ColumnInfo(name = "CurrentStockEnable")
    var CurrentStockEnable: Int? = 0

    @ColumnInfo(name = "CompetitorStockEnable")
    var CompetitorStockEnable: Int? = 0

}
package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.SHOP_COMTETETOR_STOCK_TABLE)
class CcompetetorStockEntryModelEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "user_id")
    var user_id: String? = null

    @ColumnInfo(name = "competitor_stock_id")
    var competitor_stock_id: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "visited_datetime")
    var visited_datetime: String? = null

    @ColumnInfo(name = "visited_date")
    var visited_date: String? = null

    @ColumnInfo(name = "total_product_stock_qty")
    var total_product_stock_qty: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

}
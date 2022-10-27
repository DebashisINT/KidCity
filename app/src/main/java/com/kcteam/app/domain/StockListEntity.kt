package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 13-11-2018.
 */
@Entity(tableName = AppConstant.STOCK_LIST_TABLE)
class StockListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "stock_value")
    var stock_value: String? = null

    @ColumnInfo(name = "mo")
    var mo: String? = null

    @ColumnInfo(name = "co")
    var co: String? = null

    @ColumnInfo(name = "po")
    var po: String? = null

    @ColumnInfo(name = "current_date")
    var current_date: String? = null
}
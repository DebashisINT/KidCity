package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.NEW_ORDER_ENTRY)
class NewOrderScrOrderEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "order_id")
    var order_id: String? = null

    @ColumnInfo(name = "product_id")
    var product_id: String? = null

    @ColumnInfo(name = "product_name")
    var product_name: String? = null

    @ColumnInfo(name = "gender")
    var gender: String? = null

    @ColumnInfo(name = "size")
    var size: String? = null

    @ColumnInfo(name = "qty")
    var qty: String? = null

    @ColumnInfo(name = "order_date")
    var order_date: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null


    @ColumnInfo(name = "color_id")
    var color_id: String? = null

    @ColumnInfo(name = "color_name")
    var color_name: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "rate")
    var rate: String = "0"

}
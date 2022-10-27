package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.RETURN_PRODUCT_LIST_TABLE)

class ReturnProductListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "product_id")
    var product_id: String? = null

    @ColumnInfo(name = "product_name")
    var product_name: String? = null

    @ColumnInfo(name = "brand_id")
    var brand_id: String? = null

    @ColumnInfo(name = "brand")
    var brand: String? = null

    @ColumnInfo(name = "category_id")
    var category_id: String? = null

    @ColumnInfo(name = "category")
    var category: String? = null

    @ColumnInfo(name = "watt_id")
    var watt_id: String? = null

    @ColumnInfo(name = "watt")
    var watt: String? = null

    @ColumnInfo(name = "qty")
    var qty: String? = null

    @ColumnInfo(name = "rate")
    var rate: String? = null

    @ColumnInfo(name = "total_price")
    var total_price: String? = null

    @ColumnInfo(name = "return_id")
    var return_id: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null
}
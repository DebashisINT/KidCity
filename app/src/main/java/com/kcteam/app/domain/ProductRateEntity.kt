package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 11-May-20.
 */
@Entity(tableName = AppConstant.PRODUCT_RATE_TABLE)
class ProductRateEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "product_id")
    var product_id: String? = null

    @ColumnInfo(name = "rate1")
    var rate1: String? = null

    @ColumnInfo(name = "rate2")
    var rate2: String? = null

    @ColumnInfo(name = "rate3")
    var rate3: String? = null

    @ColumnInfo(name = "rate4")
    var rate4: String? = null

    @ColumnInfo(name = "rate5")
    var rate5: String? = null

    @ColumnInfo(name = "stock_amount")
    var stock_amount: String? = null

    @ColumnInfo(name = "stock_unit")
    var stock_unit: String? = null

    @ColumnInfo(name = "ideal_duration")
    var ideal_duration: String? = null

    @ColumnInfo(name = "isStockShow")
    var isStockShow: Boolean? = null

    @ColumnInfo(name = "isRateShow")
    var isRateShow: Boolean? = null
}
package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-10-2018.
 */
@Entity(tableName = AppConstant.UPDATE_STOCK_TABLE)
class UpdateStockEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "opening_stock_amount")
    var opening_stock_amount: String? = null

    @ColumnInfo(name = "closing_stock_amount")
    var closing_stock_amount: String? = null

    @ColumnInfo(name = "opening_stock_month")
    var opening_stock_month: String? = null

    @ColumnInfo(name = "closing_stock_month")
    var closing_stock_month: String? = null

    @ColumnInfo(name = "opening_stock_month_val")
    var opening_stock_month_val: String? = null

    @ColumnInfo(name = "opening_stock_year_val")
    var opening_stock_year_val: String? = null

    @ColumnInfo(name = "closing_stock_month_val")
    var closing_stock_month_val: String? = null

    @ColumnInfo(name = "closing_stock_year_val")
    var closing_stock_year_val: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "mo")
    var mo: String? = null

    @ColumnInfo(name = "co")
    var co: String? = null

    @ColumnInfo(name = "po")
    var po: String? = null

    @ColumnInfo(name = "current_date")
    var current_date: String? = null
}
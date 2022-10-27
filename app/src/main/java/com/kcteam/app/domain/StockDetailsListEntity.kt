package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant


/**
 * Created by Saikat on 11-09-2019.
 */
@Entity(tableName = AppConstant.STOCK_DETAILS_LIST)
class StockDetailsListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "amount")
    var amount: String? = null

    @ColumnInfo(name = "qty")
    var qty: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "stock_id")
    var stock_id: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "only_date")
    var only_date: String? = null

    @ColumnInfo(name = "stock_lat")
    var stock_lat: String? = null

    @ColumnInfo(name = "stock_long")
    var stock_long: String? = null
}
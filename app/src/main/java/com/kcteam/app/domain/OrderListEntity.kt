package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 23-Sep-18.
 */
@Entity(tableName = AppConstant.ORDER_LIST_TABLE)
class OrderListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "shop_name")
    var shop_name: String? = null

    @ColumnInfo(name = "address")
    var address: String? = null

    @ColumnInfo(name = "pin_code")
    var pin_code: String? = null

    @ColumnInfo(name = "shop_lat")
    var shop_lat: String? = null

    @ColumnInfo(name = "shop_long")
    var shop_long: String? = null

    @ColumnInfo(name = "owner_name")
    var owner_name: String? = null

    @ColumnInfo(name = "owner_contact_no")
    var owner_contact_no: String? = null

    @ColumnInfo(name = "owner_email")
    var owner_email: String? = null

    @ColumnInfo(name = "shop_performance")
    var shop_performance: String? = null

    @ColumnInfo(name = "order_amount")
    var order_amount: String? = null

    @ColumnInfo(name = "order_id")
    var order_id: String? = null

    @ColumnInfo(name = "shop_image_link")
    var shop_image_link: String? = null

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "date_long")
    var date_long: Long? = null
}
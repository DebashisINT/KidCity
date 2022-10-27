package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.SHOP_ORDER_STATUS_REMARKS_TABLE)
class OrderStatusRemarksModelEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "user_id")
    var user_id: String? = null

    @ColumnInfo(name = "order_status")
    var order_status: String? = null

    @ColumnInfo(name = "order_remarks")
    var order_remarks: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "visited_date_time")
    var visited_date_time: String? = null

    @ColumnInfo(name = "visited_date")
    var visited_date: String? = null

    @ColumnInfo(name = "shop_revisit_uniqKey")
    var shop_revisit_uniqKey: String? = null

}
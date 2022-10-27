package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.NEW_ORDER_COLOR)
class NewOrderColorEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "color_id")
    var color_id: Int? = 0

    @ColumnInfo(name = "color_name")
    var color_name: String? = null

    @ColumnInfo(name = "product_id")
    var product_id: Int? = 0
}
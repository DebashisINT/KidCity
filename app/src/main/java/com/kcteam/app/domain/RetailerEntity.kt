package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.RETAILER_TABLE)
class RetailerEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "retailer_id")
    var retailer_id: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "type_id")
    var type_id: String? = null
}
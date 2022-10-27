package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 02-Jun-20.
 */
@Entity(tableName = AppConstant.PJP_LIST_TABLE)
class PjpListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "pjp_id")
    var pjp_id: String? = null

    @ColumnInfo(name = "from_time")
    var from_time: String? = null

    @ColumnInfo(name = "to_time")
    var to_time: String? = null

    @ColumnInfo(name = "customer_name")
    var customer_name: String? = null

    @ColumnInfo(name = "customer_id")
    var customer_id: String? = null

    @ColumnInfo(name = "location")
    var location: String? = null

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "remarks")
    var remarks: String? = null
}
package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 14-12-2018.
 */
@Entity(tableName = AppConstant.UPDATE_OUTSTANDING_TABLE)
class OutstandingListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "mo")
    var mo: String? = null

    @ColumnInfo(name = "co")
    var co: String? = null

    @ColumnInfo(name = "po")
    var po: String? = null

    @ColumnInfo(name = "current_date")
    var current_date: String? = null
}
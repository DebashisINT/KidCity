package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 24-10-2018.
 */
@Entity(tableName = AppConstant.PERFORMANCE_TABLE)
class PerformanceEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "total_shop_visited")
    var total_shop_visited: String? = null

    @ColumnInfo(name = "total_duration_spent")
    var total_duration_spent: String? = null

    @ColumnInfo(name = "gps_off_duration")
    var gps_off_duration: String? = null

    @ColumnInfo(name = "ideal_duration")
    var ideal_duration: String? = null

    @ColumnInfo(name = "total_distance")
    var total_distance: String? = null
}
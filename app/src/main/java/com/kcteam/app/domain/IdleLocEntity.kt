package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 04-02-2019.
 */
@Entity(tableName = AppConstant.IDEAL_LOCATION_TABLE)
class IdleLocEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "ideal_id")
    var ideal_id: String? = null

    @ColumnInfo(name = "start_date_time")
    var start_date_time: String? = null

    @ColumnInfo(name = "end_date_time")
    var end_date_time: String? = null

    @ColumnInfo(name = "start_lat")
    var start_lat: String? = null

    @ColumnInfo(name = "start_long")
    var start_long: String? = null

    @ColumnInfo(name = "end_lat")
    var end_lat: String? = null

    @ColumnInfo(name = "end_long")
    var end_long: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false
}
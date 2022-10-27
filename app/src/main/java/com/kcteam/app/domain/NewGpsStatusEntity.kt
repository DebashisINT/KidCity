package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.NEW_GPS_STATUS)
class NewGpsStatusEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "date_time")
    var date_time: String? = null

    @ColumnInfo(name = "gps_service_status")
    var gps_service_status: String? = null

    @ColumnInfo(name = "network_status")
    var network_status: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false
}
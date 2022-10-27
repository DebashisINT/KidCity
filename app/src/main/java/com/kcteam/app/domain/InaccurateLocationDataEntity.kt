package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 02-11-2018.
 */
@Entity(tableName = AppConstant.INACCURATE_LOCATION_TABLE)
class InaccurateLocationDataEntity {

    @PrimaryKey(autoGenerate = true)
    var locationId: Int = 0

    @ColumnInfo(name = "latitude")
    var latitude: String = "0.0"

    @ColumnInfo(name = "longitude")
    var longitude: String = "0.0"

    @ColumnInfo(name = "accuracy")
    var accuracy: String = "0.0"

    @ColumnInfo(name = "location_name")
    var locationName: String? = null

    @ColumnInfo(name = "timestamp")
    var timestamp: String? = null

    @ColumnInfo(name = "time")
    var time: String? = null

    @ColumnInfo(name = "hour")
    var hour: String = "0"

    @ColumnInfo(name = "minutes")
    var minutes: String = "0"

    @ColumnInfo(name = "shops")
    var shops: String? = null

    @ColumnInfo(name = "meridiem")
    var meridiem: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "updateDate")
    var updateDate: String? = null

    @ColumnInfo(name = "updateDateTime")
    var updateDateTime: String? = null

}
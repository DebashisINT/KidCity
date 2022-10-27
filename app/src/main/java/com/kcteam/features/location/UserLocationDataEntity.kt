package com.kcteam.features.location

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant


/**
 * Created by riddhi on 7/11/17.
 */

@Entity(tableName = AppConstant.LOCATION_TABLE)
//@Entity(tableName = AppConstant.LOCATION_TABLE, indices = arrayOf(Index("latitude", "longitude", "timestamp",unique = true)))
class UserLocationDataEntity {

    @PrimaryKey(autoGenerate = true)
    var locationId: Int = 0

    @ColumnInfo(name = "latitude")
    var latitude: String = "0.0"

    @ColumnInfo(name = "longitude")
    var longitude: String = "0.0"

    @ColumnInfo(name = "distance")
    var distance: String = "0.0"

    /*@ColumnInfo(name = "accuracy")
    var accuracy: String = "0.0"*/

    @ColumnInfo(name = "location_name")
    var locationName: String = ""

    @ColumnInfo(name = "timestamp")
    var timestamp: String = ""

    @ColumnInfo(name = "time")
    var time: String = ""

    @ColumnInfo(name = "hour")
    var hour: String = "0"

    @ColumnInfo(name = "minutes")
    var minutes: String = "0"

    @ColumnInfo(name = "shops")
    var shops: String = ""

    @ColumnInfo(name = "meridiem")
    var meridiem: String = ""

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "updateDate")
    var updateDate: String = ""

    @ColumnInfo(name = "updateDateTime")
    var updateDateTime: String = ""

    @ColumnInfo(name = "meeting")
    var meeting: String? = null

    @ColumnInfo(name = "visit_distance")
    var visit_distance: String? = null

    @ColumnInfo(name = "home_distance")
    var home_distance: String? = null

    @ColumnInfo(name = "network_status")
    var network_status: String? = null

    @ColumnInfo(name = "battery_percentage")
    var battery_percentage: String? = null

    @ColumnInfo(name = "home_duration")
    var home_duration: String? = null

    /*@ColumnInfo(name = "unique_id")
    var unique_id: String? = null*/

//    @ColumnInfo(name = "prev_latitude")
//    var prev_latitude: String = "0.0"
//
//    @ColumnInfo(name = "prev_longitude")
//    var prev_longitude: String = "0.0"


}
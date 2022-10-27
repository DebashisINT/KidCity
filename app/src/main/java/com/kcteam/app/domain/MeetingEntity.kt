package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 15-01-2020.
 */
@Entity(tableName = AppConstant.MEETING)
class MeetingEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "lattitude")
    var lattitude: String? = null

    @ColumnInfo(name = "longitude")
    var longitude: String? = null

    @ColumnInfo(name = "remakrs")
    var remakrs: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded = false

    @ColumnInfo(name = "duration_spent")
    var duration_spent: String? = null

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "isDurationCalculated")
    var isDurationCalculated = false

    @ColumnInfo(name = "startTimeStamp")
    var startTimeStamp: String? = null

    @ColumnInfo(name = "endTimeStamp")
    var endTimeStamp: String? = null

    @ColumnInfo(name = "meetingTypeId")
    var meetingTypeId: String? = null

    @ColumnInfo(name = "address")
    var address: String? = null

    @ColumnInfo(name = "pincode")
    var pincode: String? = null

    @ColumnInfo(name = "distance_travelled")
    var distance_travelled: String? = null

    @ColumnInfo(name = "date_time")
    var date_time: String? = null
}
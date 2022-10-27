package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 10-Jul-20.
 */
@Entity(tableName = AppConstant.TIMESHEET_LIST)
class TimesheetListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "timesheet_id")
    var timesheet_id: String? = null

    @ColumnInfo(name = "time")
    var time: String? = null

    @ColumnInfo(name = "client_id")
    var client_id: String? = null

    @ColumnInfo(name = "client_name")
    var client_name: String? = null

    @ColumnInfo(name = "product_id")
    var product_id: String? = null

    @ColumnInfo(name = "product_name")
    var product_name: String? = null

    @ColumnInfo(name = "project_id")
    var project_id: String? = null

    @ColumnInfo(name = "project_name")
    var project_name: String? = null

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "activity_id")
    var activity_id: String? = null

    @ColumnInfo(name = "activity_name")
    var activity_name: String? = null

    @ColumnInfo(name = "comments")
    var comments: String? = null

    @ColumnInfo(name = "isUpdateable")
    var isUpdateable: Boolean = false

    @ColumnInfo(name = "status")
    var status: String? = null

    @ColumnInfo(name = "image")
    var image: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false
}
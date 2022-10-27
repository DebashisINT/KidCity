package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.Activity)
class ActivityEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "activity_id")
    var activity_id: String? = null

    @ColumnInfo(name = "party_id")
    var party_id: String? = null

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "time")
    var time: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "activity_dropdown_id")
    var activity_dropdown_id: String? = null

    @ColumnInfo(name = "type_id")
    var type_id: String? = null

    @ColumnInfo(name = "product_id")
    var product_id: String? = null

    @ColumnInfo(name = "subject")
    var subject: String? = null

    @ColumnInfo(name = "details")
    var details: String? = null

    @ColumnInfo(name = "duration")
    var duration: String? = null

    @ColumnInfo(name = "priority_id")
    var priority_id: String? = null

    @ColumnInfo(name = "due_date")
    var due_date: String? = null

    @ColumnInfo(name = "due_time")
    var due_time: String? = null

    @ColumnInfo(name = "attachments")
    var attachments: String? = null

    @ColumnInfo(name = "image")
    var image: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false
}
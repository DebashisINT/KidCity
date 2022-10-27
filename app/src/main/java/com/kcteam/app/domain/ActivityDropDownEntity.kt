package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.ACTIVITY_DROPDOWN_TABLE)
class ActivityDropDownEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "activity_id")
    var activity_id: String? = null

    @ColumnInfo(name = "activity_name")
    var activity_name: String? = null
}
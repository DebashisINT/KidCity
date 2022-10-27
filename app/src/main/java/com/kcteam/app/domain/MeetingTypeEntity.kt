package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 17-01-2020.
 */
@Entity(tableName = AppConstant.MEETING_TYPE)
class MeetingTypeEntity {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "typeId")
    var typeId: Int = 0

    @ColumnInfo(name = "typeText")
    var typeText: String? = null
}
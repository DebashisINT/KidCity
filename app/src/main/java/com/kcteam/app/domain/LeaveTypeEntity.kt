package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 08-11-2018.
 */
@Entity(tableName = AppConstant.LEAVE_TYPE_TABLE)
class LeaveTypeEntity {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "leave_type")
    var leave_type: String? = null
}
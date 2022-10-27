package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 10-Jul-20.
 */
@Entity(tableName = AppConstant.ACTIVITY_LIST)
class ActivityListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "activity_id")
    var activity_id: String? = null

    @ColumnInfo(name = "activity_name")
    var activity_name: String? = null

}
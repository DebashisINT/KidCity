package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-Jun-20.
 */
@Entity(tableName = AppConstant.SECONDARY_APPLICATION_TABLE)
class SecondaryAppEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "secondary_app_id")
    var secondary_app_id: String? = null

    @ColumnInfo(name = "secondary_app_name")
    var secondary_app_name: String? = null
}
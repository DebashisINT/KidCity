package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 03-Jul-20.
 */
@Entity(tableName = AppConstant.MEMBER_AREA_TABLE)
class TeamAreaEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "user_id")
    var user_id: String? = null

    @ColumnInfo(name = "area_id")
    var area_id: String? = null

    @ColumnInfo(name = "area_name")
    var area_name: String? = null
}
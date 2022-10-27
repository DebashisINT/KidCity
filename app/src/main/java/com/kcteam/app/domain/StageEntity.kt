package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-Jun-20.
 */
@Entity(tableName = AppConstant.STAGE_TABLE)
class StageEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "stage_id")
    var stage_id: String? = null

    @ColumnInfo(name = "stage_name")
    var stage_name: String? = null
}
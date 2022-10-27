package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 31-08-2018.
 */
@Entity(tableName = AppConstant.WORK_TYPE_TABLE)
class WorkTypeEntity {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "ID")
    var ID: Int = 0

    @ColumnInfo(name = "Descrpton")
    var Descrpton: String? = null

    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean = false
}
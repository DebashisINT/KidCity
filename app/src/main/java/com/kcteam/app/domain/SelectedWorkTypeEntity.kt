package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 30-11-2018.
 */
@Entity(tableName = AppConstant.SELECTED_WORK_TYPE_TABLE)
class SelectedWorkTypeEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "work_type_id")
    var work_type_id: Int = 0

    @ColumnInfo(name = "ID")
    var ID: Int = 0

    @ColumnInfo(name = "Descrpton")
    var Descrpton: String? = null

    @ColumnInfo(name = "date")
    var date: String? = null
}
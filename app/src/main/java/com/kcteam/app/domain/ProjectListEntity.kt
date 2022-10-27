package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 10-Jul-20.
 */
@Entity(tableName = AppConstant.PROJECT_LIST)
class ProjectListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "project_id")
    var project_id: String? = null

    @ColumnInfo(name = "project_name")
    var project_name: String? = null
}
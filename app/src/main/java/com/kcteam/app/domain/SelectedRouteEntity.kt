package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 30-11-2018.
 */
@Entity(tableName = AppConstant.SELECTED_ROUTE_LIST_TABLE)
class SelectedRouteEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "route_id")
    var route_id: String? = null

    @ColumnInfo(name = "route_name")
    var route_name: String? = null

    @ColumnInfo(name = "date")
    var date: String? = null
}
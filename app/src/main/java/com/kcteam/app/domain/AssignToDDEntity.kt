package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 18-09-2018.
 */
@Entity(tableName = AppConstant.ASSIGNED_TO_DD_TABLE)
class AssignToDDEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "dd_id")
    var dd_id: String? = null

    @ColumnInfo(name = "dd_name")
    var dd_name: String? = null

    @ColumnInfo(name = "dd_phn_no")
    var dd_phn_no: String? = null

    @ColumnInfo(name = "pp_id")
    var pp_id: String? = null

    @ColumnInfo(name = "type_id")
    var type_id: String? = null


    @ColumnInfo(name = "dd_latitude")
    var dd_latitude: String? = null

    @ColumnInfo(name = "dd_longitude")
    var dd_longitude: String? = null

}
package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 18-09-2018.
 */
@Entity(tableName = AppConstant.ASSIGNED_TO_SHOP_TABLE)
class AssignToShopEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "assigned_to_shop_id")
    var assigned_to_shop_id: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "phn_no")
    var phn_no: String? = null

    @ColumnInfo(name = "type_id")
    var type_id: String? = null
}
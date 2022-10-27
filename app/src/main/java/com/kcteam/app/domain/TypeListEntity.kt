package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 22-Jun-20.
 */
@Entity(tableName = AppConstant.TYPE_TABLE)
class TypeListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "type_id")
    var type_id: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null
}
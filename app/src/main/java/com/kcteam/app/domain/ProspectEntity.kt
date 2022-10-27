package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant


@Entity(tableName = AppConstant.PROSPECT_TABLE_MASTER)
class ProspectEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "pros_id")
    var pros_id: String? = null

    @ColumnInfo(name = "pros_name")
    var pros_name: String? = null
}
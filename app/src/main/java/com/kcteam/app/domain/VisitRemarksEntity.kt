package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.VISIT_REMARKS_TABLE)
class VisitRemarksEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "remarks_id")
    var remarks_id: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null
}
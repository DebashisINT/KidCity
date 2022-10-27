package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.TBL_DIST_WISE_ORD_REPORT)
class DistWiseOrderTblEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "from_date")
    var from_date: String? = null

    @ColumnInfo(name = "to_date")
    var to_date: String? = null

    @ColumnInfo(name = "selected_dd")
    var selected_dd: String? = null

    @ColumnInfo(name = "selected_pp")
    var selected_pp: String? = null

    @ColumnInfo(name = "genereated_date_time")
    var genereated_date_time: String? = null

    @ColumnInfo(name = "only_date")
    var only_date: String? = null

}
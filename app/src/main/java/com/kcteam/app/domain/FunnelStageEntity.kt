package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-Jun-20.
 */
@Entity(tableName = AppConstant.FUNNEL_STAGE_TABLE)
class FunnelStageEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "funnel_stage_id")
    var funnel_stage_id: String? = null

    @ColumnInfo(name = "funnel_stage_name")
    var funnel_stage_name: String? = null
}
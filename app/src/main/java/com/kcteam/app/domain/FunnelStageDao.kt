package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-Jun-20.
 */
@Dao
interface FunnelStageDao {

    @Query("SELECT * FROM " + AppConstant.FUNNEL_STAGE_TABLE)
    fun getAll(): List<FunnelStageEntity>

    @Insert
    fun insertAll(vararg funnel: FunnelStageEntity)

    @Query("DELETE FROM " + AppConstant.FUNNEL_STAGE_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.FUNNEL_STAGE_TABLE + " where funnel_stage_id=:funnel_stage_id")
    fun getSingleType(funnel_stage_id: String): FunnelStageEntity
}
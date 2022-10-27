package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 14-May-20.
 */
@Dao
interface AreaListDao {

    @Query("SELECT * FROM " + AppConstant.AREA_LIST_TABLE)
    fun getAll(): List<AreaListEntity>

    @Query("SELECT * FROM " + AppConstant.AREA_LIST_TABLE + " where area_id=:area_id")
    fun getSingleArea(area_id: String): AreaListEntity

    @Insert
    fun insert(vararg area: AreaListEntity)

    @Query("DELETE FROM " + AppConstant.AREA_LIST_TABLE)
    fun deleteAll()
}
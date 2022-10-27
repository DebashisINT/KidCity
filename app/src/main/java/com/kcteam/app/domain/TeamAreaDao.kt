package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 03-Jul-20.
 */
@Dao
interface TeamAreaDao {

    @Query("SELECT * FROM " + AppConstant.MEMBER_AREA_TABLE)
    fun getAll(): List<TeamAreaEntity>

    @Insert
    fun insertAll(vararg area: TeamAreaEntity)

    @Query("DELETE FROM " + AppConstant.MEMBER_AREA_TABLE)
    fun deleteAll()
}
package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface PriorityDao {

    @Query("SELECT * FROM " + AppConstant.PRIORITY_TABLE)
    fun getAll(): List<PriorityListEntity>

    @Insert
    fun insertAll(vararg priority: PriorityListEntity)

    @Query("DELETE FROM " + AppConstant.PRIORITY_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.PRIORITY_TABLE + " where priority_id=:priority_id")
    fun getSingleType(priority_id: String): PriorityListEntity
}
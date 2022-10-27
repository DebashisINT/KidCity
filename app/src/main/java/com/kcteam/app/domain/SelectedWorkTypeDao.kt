package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 30-11-2018.
 */
@Dao
interface SelectedWorkTypeDao {

    @Query("SELECT * FROM " + AppConstant.SELECTED_WORK_TYPE_TABLE)
    fun getAll(): List<SelectedWorkTypeEntity>

    @Query("SELECT * FROM " + AppConstant.SELECTED_WORK_TYPE_TABLE + " where date=:date")
    fun getTodaysData(date: String): List<SelectedWorkTypeEntity>

    @Query("DELETE FROM " + AppConstant.SELECTED_WORK_TYPE_TABLE)
    fun delete()

    @Insert
    fun insertAll(vararg workType: SelectedWorkTypeEntity)
}
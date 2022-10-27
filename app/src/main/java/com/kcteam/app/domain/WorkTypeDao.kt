package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant


/**
 * Created by Saikat on 31-08-2018.
 */
@Dao
interface WorkTypeDao {

    @Query("SELECT * FROM " + AppConstant.WORK_TYPE_TABLE)
    fun getAll(): List<WorkTypeEntity>

    @Query("SELECT * FROM " + AppConstant.WORK_TYPE_TABLE + " where isSelected=:isSelected")
    fun getSelectedWork(isSelected: Boolean): List<WorkTypeEntity>

    @Query("update " + AppConstant.WORK_TYPE_TABLE + " set isSelected=:isSelected where ID=:ID")
    fun updateIsSelected(isSelected: Boolean, ID: Int)

    @Insert
    fun insertAll(vararg workType: WorkTypeEntity)

    @Query("DELETE FROM " + AppConstant.WORK_TYPE_TABLE)
    fun delete()
}
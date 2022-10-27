package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 08-11-2018.
 */
@Dao
interface LeaveTypeDao {

    @Query("SELECT * FROM " + AppConstant.LEAVE_TYPE_TABLE)
    fun getAll(): List<LeaveTypeEntity>

    @Insert
    fun insert(vararg leaveType: LeaveTypeEntity)

    @Query("DELETE FROM " + AppConstant.LEAVE_TYPE_TABLE)
    fun delete()
}
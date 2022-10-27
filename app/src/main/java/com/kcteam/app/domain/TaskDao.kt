package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 12-Aug-20.
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM " + AppConstant.TASK_TABLE)
    fun getAll(): List<TaskEntity>

    @Insert
    fun insertAll(vararg task: TaskEntity)

    @Query("DELETE FROM " + AppConstant.TASK_TABLE)
    fun deleteAll()

    @Query("DELETE FROM " + AppConstant.TASK_TABLE + " where task_id=:task_id")
    fun deleteSingleItem(task_id: String)

    @Query("update " + AppConstant.TASK_TABLE + " set isUploaded=:isUploaded where task_id=:task_id")
    fun updateIsUploaded(isUploaded: Boolean, task_id: String)

    @Update
    fun updateTask(task: TaskEntity)

    @Query("SELECT * FROM " + AppConstant.TASK_TABLE + " where date=:date and isCompleted=:isCompleted")
    fun getTaskStatusDateWise(date: String, isCompleted: Boolean): List<TaskEntity>

    @Query("SELECT * FROM " + AppConstant.TASK_TABLE + " where date=:date")
    fun getTaskDateWise(date: String): List<TaskEntity>

    @Query("update " + AppConstant.TASK_TABLE + " set isStatusUpdated=:isStatusUpdated where task_id=:task_id")
    fun updateIsStatus(isStatusUpdated: Int, task_id: String)

    @Query("update " + AppConstant.TASK_TABLE + " set isCompleted=:isCompleted where task_id=:task_id")
    fun updateIsCompleted(isCompleted: Boolean, task_id: String)

    @Query("SELECT * FROM " + AppConstant.TASK_TABLE + " where isUploaded=:isUploaded")
    fun getTaskSyncWise(isUploaded: Boolean): List<TaskEntity>

    @Query("SELECT * FROM " + AppConstant.TASK_TABLE + " where isStatusUpdated=:isStatusUpdated")
    fun getTaskStatusWise(isStatusUpdated: Int): List<TaskEntity>

    @Query("SELECT date FROM " + AppConstant.TASK_TABLE + " group by date")
    fun getGroupedDateList(): List<String>
}
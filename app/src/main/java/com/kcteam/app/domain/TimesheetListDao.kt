package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 10-Jul-20.
 */
@Dao
interface TimesheetListDao {

    @Query("SELECT * FROM " + AppConstant.TIMESHEET_LIST)
    fun getAll(): List<TimesheetListEntity>

    @Insert
    fun insertAll(vararg timesheet: TimesheetListEntity)

    @Query("DELETE FROM " + AppConstant.TIMESHEET_LIST)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.TIMESHEET_LIST + " where timesheet_id=:timesheet_id")
    fun getSingleTimesheet(timesheet_id: String): TimesheetListEntity

    @Query("SELECT * FROM " + AppConstant.TIMESHEET_LIST + " where date=:date")
    fun getTimesheetDateWise(date: String): List<TimesheetListEntity>

    @Query("SELECT * FROM " + AppConstant.TIMESHEET_LIST + " where isUploaded=:isUploaded")
    fun getTimesheetSyncWise(isUploaded: Boolean): List<TimesheetListEntity>

    @Query("DELETE FROM " + AppConstant.TIMESHEET_LIST + " where timesheet_id=:timesheet_id")
    fun deleteSingleTimesheet(timesheet_id: String)

    @Query("update " + AppConstant.TIMESHEET_LIST + " set isUploaded=:isUploaded where timesheet_id=:timesheet_id")
    fun updateIsUploaded(isUploaded: Boolean, timesheet_id: String)

    @Query("update " + AppConstant.TIMESHEET_LIST + " set status=:status where timesheet_id=:timesheet_id")
    fun updateStatus(status: String, timesheet_id: String)

    @Update
    fun updateTimesheet(timesheet: TimesheetListEntity)
}
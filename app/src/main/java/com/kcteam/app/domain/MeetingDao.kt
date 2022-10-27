package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 15-01-2020.
 */
@Dao
interface MeetingDao {

    @Query("SELECT * FROM " + AppConstant.MEETING + " order by date_time DESC")
    fun getAll(): List<MeetingEntity>

    @Insert
    fun insertAll(vararg meeting: MeetingEntity)

    @Query("DELETE FROM " + AppConstant.MEETING)
    fun deleteAll()

    @Query("update meeting_list set isUploaded=:isUploaded where id=:id and date=:date")
    fun updateIsUploadedDateWise(isUploaded: Boolean, id: Int, date: String)

    @Query("update meeting_list set isUploaded=:isUploaded where id=:id")
    fun updateIsUploaded(isUploaded: Boolean, id: Int)

    @Query("Select * from meeting_list where isUploaded=:isUploaded")
    fun getSyncedShopActivity(isUploaded: Boolean): List<MeetingEntity>

    @Query("Select * from meeting_list where date=:date")
    fun getMeetingDateWise(date: String): List<MeetingEntity>

    @Query("Select * from meeting_list where isDurationCalculated=:isDurationCalculated")
    fun durationAvailable(isDurationCalculated: Boolean): List<MeetingEntity>

    @Query("Select * from meeting_list where isDurationCalculated=:isDurationCalculated and isUploaded=:isUploaded")
    fun durationAvailableSyncWise(isDurationCalculated: Boolean, isUploaded: Boolean): List<MeetingEntity>

    @Query("Select * from meeting_list where id=:id and isDurationCalculated=:isDurationCalculated and isUploaded=:isUploaded")
    fun durationAvailableForSingleMeeting(id: Int, isDurationCalculated: Boolean, isUploaded: Boolean): MeetingEntity?

    @Query("Select * from meeting_list where id=:id and isDurationCalculated=:isDurationCalculated and isUploaded=:isUploaded and date=:date")
    fun durationAvailableForTodayMeeting(id: Int, isDurationCalculated: Boolean, isUploaded: Boolean, date: String): MeetingEntity?

    @Query("update meeting_list set isDurationCalculated=:isDurationCalculated where id=:id and date=:date")
    fun updateDurationAvailable(isDurationCalculated: Boolean, id: Int, date: String)

    @Query("update meeting_list set duration_spent=:duration where id=:id and date=:date")
    fun updateTimeDurationForDayOfMeeting(id: Int, duration: String, date: String)

    @Query("update meeting_list set endTimeStamp=:endTime where id=:id and date=:date")
    fun updateEndTimeOfMeeting(endTime: String, id: Int, date: String): Int
}
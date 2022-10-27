package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 17-01-2020.
 */
@Dao
interface MeetingTypeDao {

    @Query("SELECT * FROM " + AppConstant.MEETING_TYPE)
    fun getAll(): List<MeetingTypeEntity>

    @Insert
    fun insertAll(vararg meetingType: MeetingTypeEntity)

    @Query("DELETE FROM " + AppConstant.MEETING_TYPE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.MEETING_TYPE + " where typeId=:typeId")
    fun getSingleType(typeId: Int): MeetingTypeEntity
}
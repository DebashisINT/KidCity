package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Pratishruti on 18-01-2018.
 */
@Dao
interface GpsStatusDao {

    @Query("SELECT * FROM " + AppConstant.GPS_STATUS_TABLE)
    fun getAll(): List<GpsStatusEntity>

    @Query("SELECT * FROM " + AppConstant.GPS_STATUS_TABLE + " where date=:date")
    fun getDataDateWise(date: String): List<GpsStatusEntity>

    @Query("SELECT * FROM " + AppConstant.GPS_STATUS_TABLE + " where isUploaded=:isUploaded")
    fun getDataSyncStateWise(isUploaded: Boolean): List<GpsStatusEntity>

    @Query("update " + AppConstant.GPS_STATUS_TABLE + " set isUploaded=:isUploaded where id=:id")
    fun updateIsUploadedAccordingToId(isUploaded: Boolean, id: Int)

    @Insert
    fun insert(vararg gpsStatus: GpsStatusEntity)

    @Query("DELETE FROM " + AppConstant.GPS_STATUS_TABLE)
    fun delete()
}
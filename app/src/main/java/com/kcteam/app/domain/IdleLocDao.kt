package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 04-02-2019.
 */
@Dao
interface IdleLocDao {
    @Query("SELECT * FROM " + AppConstant.IDEAL_LOCATION_TABLE)
    fun getAll(): List<IdleLocEntity>

    @Query("SELECT * FROM " + AppConstant.IDEAL_LOCATION_TABLE + " where isUploaded=:isUploaded")
    fun getDataSyncStateWise(isUploaded: Boolean): List<IdleLocEntity>

    @Query("update " + AppConstant.IDEAL_LOCATION_TABLE + " set isUploaded=:isUploaded where id=:id")
    fun updateIsUploadedAccordingToId(isUploaded: Boolean, id: Int)

    @Insert
    fun insert(vararg ideal: IdleLocEntity)

    @Query("DELETE FROM " + AppConstant.IDEAL_LOCATION_TABLE)
    fun delete()
}
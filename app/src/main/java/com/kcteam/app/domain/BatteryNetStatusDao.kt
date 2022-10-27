package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 17-Aug-20.
 */
@Dao
interface BatteryNetStatusDao {

    @Query("SELECT * FROM " + AppConstant.BATTERY_NET_TABLE)
    fun getAll(): List<BatteryNetStatusEntity>

    @Query("SELECT * FROM " + AppConstant.BATTERY_NET_TABLE + " where date=:date order by date_time")
    fun getDataDateWise(date: String): List<BatteryNetStatusEntity>

    @Query("SELECT * FROM " + AppConstant.BATTERY_NET_TABLE + " where isUploaded=:isUploaded")
    fun getDataSyncStateWise(isUploaded: Boolean): List<BatteryNetStatusEntity>

    @Query("update " + AppConstant.BATTERY_NET_TABLE + " set isUploaded=:isUploaded where id=:id")
    fun updateIsUploadedAccordingToId(isUploaded: Boolean, id: Int)

    @Insert
    fun insert(vararg batNet: BatteryNetStatusEntity)

    @Query("DELETE FROM " + AppConstant.BATTERY_NET_TABLE)
    fun delete()
}
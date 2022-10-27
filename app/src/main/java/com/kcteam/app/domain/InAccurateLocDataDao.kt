package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 02-11-2018.
 */
@Dao
interface InAccurateLocDataDao {

    @get:Query("SELECT * FROM " + AppConstant.INACCURATE_LOCATION_TABLE)
    val all: MutableList<InaccurateLocationDataEntity>

    //    @Query("SELECT * FROM " + DBNAME + " where first_name LIKE  :shopName AND last_name LIKE :lastName")
    //    AddShopDBModelEntity findByName(String firstName, String lastName);

    @Query("SELECT COUNT(*) from " + AppConstant.INACCURATE_LOCATION_TABLE)
    fun countUsers(): Int

    @Insert
    fun insertAll(vararg locationdata: InaccurateLocationDataEntity)

    @Insert
    fun insert(location: InaccurateLocationDataEntity)

    @Delete
    fun delete(locationdata: InaccurateLocationDataEntity)

    @Query("Select * from inaccurate_location_db where updateDate=:date and isUploaded=:isUploaded")
    fun getLocationUpdatePerDay(date: String,isUploaded:Boolean): List<InaccurateLocationDataEntity>

    @Query("Select * from inaccurate_location_db where isUploaded=:isUploaded")
    fun getLocationNotUploaded(isUploaded:Boolean): List<InaccurateLocationDataEntity>


    @Query("Select * from inaccurate_location_db where updateDate=:date")
    fun getLocationUpdateForADay(date: String): List<InaccurateLocationDataEntity>

    @Query("Select * from inaccurate_location_db where updateDate=:date and isUploaded=:isUploaded")
    fun getLocationUpdateForADayNotSyn(date: String,isUploaded:Boolean): List<InaccurateLocationDataEntity>

    @Query("update inaccurate_location_db set isUploaded=:isUploaded where locationId=:id")
    fun updateIsUploaded(isUploaded:Boolean,id:Int): Int

    @Query("DELETE FROM " + AppConstant.INACCURATE_LOCATION_TABLE)
    fun deleteAll()
}
package com.kcteam.features.location

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by riddhi on 7/11/17.
 */
@Dao
interface UserLocationDataDao {

    @get:Query("SELECT * FROM " + AppConstant.LOCATION_TABLE)
    val all: MutableList<UserLocationDataEntity>

    //    @Query("SELECT * FROM " + DBNAME + " where first_name LIKE  :shopName AND last_name LIKE :lastName")
    //    AddShopDBModelEntity findByName(String firstName, String lastName);

    @Query("SELECT COUNT(*) from " + AppConstant.LOCATION_TABLE)
    fun countUsers(): Int

    @Insert
    fun insertAll(vararg locationdata: UserLocationDataEntity)

    @Insert
    fun insert(location: UserLocationDataEntity)

    @Delete
    fun delete(locationdata: UserLocationDataEntity)

    @Query("Select * from location_db where updateDate=:date and isUploaded=:isUploaded")
    fun getLocationUpdatePerDay(date: String, isUploaded: Boolean): List<UserLocationDataEntity>

    @Query("Select * from location_db where isUploaded=:isUploaded")
    fun getLocationNotUploaded(isUploaded: Boolean): List<UserLocationDataEntity>


    @Query("Select * from location_db where updateDate=:date")
    fun getLocationUpdateForADay(date: String): List<UserLocationDataEntity>

    @Query("Select * from location_db where updateDate=:date and isUploaded=:isUploaded")
    fun getLocationUpdateForADayNotSyn(date: String, isUploaded: Boolean): List<UserLocationDataEntity>

    @Query("Select SUM(CAST(distance as DOUBLE)) from location_db where updateDate=:date")
    fun getTotalDistanceForADay(date: String): Double

    @Query("update location_db set isUploaded=:isUploaded where locationId=:id")
    fun updateIsUploaded(isUploaded: Boolean, id: Int): Int

    @Query("update location_db set isUploaded=:isUploaded where locationId >=:id_1 and locationId <=:id_2")
    fun updateIsUploadedFor5Items(isUploaded: Boolean, id_1: Int, id_2: Int): Int

    /*@Query("update location_db set unique_id=:unique_id where locationId=:id")
    fun updateUniqueId(unique_id: String, id: Int): Int*/

    @Query("DELETE FROM " + AppConstant.LOCATION_TABLE)
    fun deleteAll()


    @Query("Select * from location_db ORDER BY locationId DESC LIMIT 1")
    fun getLastRecord(): UserLocationDataEntity

    @Query("Select * from location_db where updateDate=:date and location_name=:location_name and isUploaded=:isUploaded")
    fun getUnknownLocation(date: String,location_name:String,isUploaded:Boolean): List<UserLocationDataEntity>

    @Query("update " + AppConstant.LOCATION_TABLE+" set location_name=:location_name where locationId=:locationId")
    fun updateUnknownLocation(locationId:String,location_name:String)

    @Query("update " + AppConstant.LOCATION_TABLE+" set location_name=:location_name where updateDate=:date and isUploaded=:isUploaded")
    fun updateUnknownLocationTest(date: String,location_name:String,isUploaded:Boolean)
}
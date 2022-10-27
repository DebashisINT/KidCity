package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Created by Pratishruti on 18-01-2018.
 */
@Dao
interface CityListDao {

    @Query("SELECT * FROM city_list")
    fun getAll(): List<CityListEntity>

    @Insert
    fun insertAll(vararg city: CityListEntity)

    @Query("Select city_name from city_list")
    fun getAllCities(): List<String>

    @Query("Select city_id from city_list where city_name=:name and state_id=:state_id")
    fun getIdFromName(name:String,state_id:String): Int

    @Query("Select city_name from city_list where state_id=:state_id")
    fun getCityListFromState(state_id:String): List<String>

    @Query("Select city_name from city_list where city_id=:id")
    fun getNameFromId(id:Int): String

    @Query("Delete from city_list")
    fun deleteAll()
}
package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Created by Pratishruti on 18-01-2018.
 */
@Dao
interface StateListDao {

    @Query("SELECT * FROM state_list")
    fun getAll(): List<StateListEntity>

    @Insert
    fun insertAll(vararg state: StateListEntity)

    @Query("Select state_name from state_list")
    fun getAllState(): List<String>

    @Query("Select state_id from state_list where state_name=:name")
    fun getIdFromName(name:String): Int

    @Query("Select state_name from state_list where state_id=:id")
    fun getNameFromId(id:Int): String

    @Query("Delete from state_list")
    fun deleteAll()
}
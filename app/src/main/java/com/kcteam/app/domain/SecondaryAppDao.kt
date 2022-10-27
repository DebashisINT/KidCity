package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-Jun-20.
 */
@Dao
interface SecondaryAppDao {
    @Query("SELECT * FROM " + AppConstant.SECONDARY_APPLICATION_TABLE)
    fun getAll(): List<SecondaryAppEntity>

    @Insert
    fun insertAll(vararg secondary: SecondaryAppEntity)

    @Query("DELETE FROM " + AppConstant.SECONDARY_APPLICATION_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.SECONDARY_APPLICATION_TABLE + " where secondary_app_id=:secondary_app_id")
    fun getSingleType(secondary_app_id: String): SecondaryAppEntity
}
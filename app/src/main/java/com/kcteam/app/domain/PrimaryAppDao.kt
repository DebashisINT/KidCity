package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-Jun-20.
 */
@Dao
interface PrimaryAppDao {

    @Query("SELECT * FROM " + AppConstant.PRIMARY_APPLICATION_TABLE)
    fun getAll(): List<PrimaryAppEntity>

    @Insert
    fun insertAll(vararg primary: PrimaryAppEntity)

    @Query("DELETE FROM " + AppConstant.PRIMARY_APPLICATION_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.PRIMARY_APPLICATION_TABLE + " where primary_app_id=:primary_app_id")
    fun getSingleType(primary_app_id: String): PrimaryAppEntity
}
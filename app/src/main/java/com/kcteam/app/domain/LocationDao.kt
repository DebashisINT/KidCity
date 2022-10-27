/*
package com.fieldtrackingsystem.app.domain

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.fieldtrackingsystem.app.AppConstant

*/
/**
 * Created by Saikat on 07-01-2019.
 *//*

@Dao
interface LocationDao {

    @get:Query("SELECT * FROM " + AppConstant.ALL_LOCATION_TABLE)
    val all: MutableList<LocationEntity>

    @Query("SELECT * FROM " + AppConstant.ALL_LOCATION_TABLE + " where date=:date")
    fun getAllValueDateWise(date: String): List<LocationEntity>

    @Insert
    fun insert(location: LocationEntity)
}*/

package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 30-11-2018.
 */
@Dao
interface SelectedRouteDao {

    @Query("SELECT * FROM " + AppConstant.SELECTED_ROUTE_LIST_TABLE)
    fun getAll(): List<SelectedRouteEntity>

    @Query("SELECT * FROM " + AppConstant.SELECTED_ROUTE_LIST_TABLE + " where date=:date")
    fun getTodaysData(date: String): List<SelectedRouteEntity>

    @Query("DELETE FROM " + AppConstant.SELECTED_ROUTE_LIST_TABLE)
    fun deleteRoute()

    @Insert
    fun insert(vararg route: SelectedRouteEntity)
}
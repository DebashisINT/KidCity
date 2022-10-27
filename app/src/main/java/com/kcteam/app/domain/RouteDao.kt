package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 08-11-2018.
 */
@Dao
interface RouteDao {

    @Query("SELECT * FROM " + AppConstant.ROUTE_TABLE)
    fun getAll(): List<RouteEntity>

    @Query("SELECT * FROM " + AppConstant.ROUTE_TABLE + " where isSelected=:isSelected")
    fun getSelectedRoute(isSelected: Boolean): List<RouteEntity>

    @Query("update " + AppConstant.ROUTE_TABLE + " set isSelected=:isSelected where route_id=:route_id")
    fun updateIsSelectedAccordingToRouteId(isSelected: Boolean, route_id: String)

    @Query("DELETE FROM " + AppConstant.ROUTE_TABLE)
    fun deleteRoute()

    @Insert
    fun insert(vararg route: RouteEntity)

}
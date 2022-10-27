package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 22-11-2018.
 */
@Dao
interface RouteShopListDao {

    @Query("SELECT * FROM " + AppConstant.ROUTE_SHOP_LIST_TABLE)
    fun getAll(): List<RouteShopListEntity>

    @Query("SELECT * FROM " + AppConstant.ROUTE_SHOP_LIST_TABLE + " where route_id=:route_id")
    fun getDataRouteIdWise(route_id: String): List<RouteShopListEntity>

    @Query("SELECT * FROM " + AppConstant.ROUTE_SHOP_LIST_TABLE + " where isSelected=:isSelected")
    fun getSelectedData(isSelected: Boolean): List<RouteShopListEntity>

    @Query("SELECT * FROM " + AppConstant.ROUTE_SHOP_LIST_TABLE + " where isSelected=:isSelected and route_id=:route_id")
    fun getSelectedDataRouteIdWise(isSelected: Boolean, route_id: String): List<RouteShopListEntity>

    @Query("update " + AppConstant.ROUTE_SHOP_LIST_TABLE + " set isSelected=:isSelected where route_id=:route_id")
    fun updateIsUploadedAccordingToRouteId(isSelected: Boolean, route_id: String)

    @Query("update " + AppConstant.ROUTE_SHOP_LIST_TABLE + " set isSelected=:isSelected where route_id=:route_id and shop_id=:shop_id")
    fun updateIsUploadedAccordingToRouteAndShopId(isSelected: Boolean, route_id: String, shop_id: String)

    @Query("DELETE FROM " + AppConstant.ROUTE_SHOP_LIST_TABLE)
    fun deleteData()

    @Insert
    fun insert(vararg routeShopList: RouteShopListEntity)
}
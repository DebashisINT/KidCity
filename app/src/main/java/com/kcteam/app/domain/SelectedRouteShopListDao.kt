package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 30-11-2018.
 */
@Dao
interface SelectedRouteShopListDao {
    @Query("SELECT * FROM " + AppConstant.SELECTED_ROUTE_TYPE_SHOP_LIST_TABLE)
    fun getAll(): List<SelectedRouteShopListEntity>

    @Query("SELECT * FROM " + AppConstant.SELECTED_ROUTE_TYPE_SHOP_LIST_TABLE + " where route_id=:route_id")
    fun getDataRouteIdWise(route_id: String): List<SelectedRouteShopListEntity>

    @Query("SELECT * FROM " + AppConstant.SELECTED_ROUTE_TYPE_SHOP_LIST_TABLE + " where route_id=:route_id and date=:date")
    fun getDataRouteIdDateWise(route_id: String, date: String): List<SelectedRouteShopListEntity>

    @Query("SELECT * FROM " + AppConstant.SELECTED_ROUTE_TYPE_SHOP_LIST_TABLE + " where date=:date")
    fun getDataDateWise(date: String): List<SelectedRouteShopListEntity>

    @Query("DELETE FROM " + AppConstant.SELECTED_ROUTE_TYPE_SHOP_LIST_TABLE)
    fun deleteData()

    @Insert
    fun insert(vararg routeShopList: SelectedRouteShopListEntity)
}
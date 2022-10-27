package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 18-09-2018.
 */
@Dao
interface AssignToShopDao {

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_SHOP_TABLE)
    fun getAll(): List<AssignToShopEntity>

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_SHOP_TABLE + " where assigned_to_shop_id=:assigned_to_shop_id")
    fun getSingleValue(assigned_to_shop_id: String): AssignToShopEntity

    @Insert
    fun insert(vararg assignToShop: AssignToShopEntity)

    @Query("DELETE FROM " + AppConstant.ASSIGNED_TO_SHOP_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_SHOP_TABLE + " where type_id=:type_id")
    fun getValueTypeWise(type_id: String): List<AssignToShopEntity>

    @Query("update " + AppConstant.ASSIGNED_TO_SHOP_TABLE + " set type_id=:type_id where assigned_to_shop_id=:assigned_to_shop_id")
    fun updateTypeId(assigned_to_shop_id: String, type_id: String)
}
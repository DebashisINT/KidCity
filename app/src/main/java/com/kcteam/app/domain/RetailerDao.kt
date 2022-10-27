package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface RetailerDao {
    @Query("SELECT * FROM " + AppConstant.RETAILER_TABLE)
    fun getAll(): List<RetailerEntity>

    @Insert
    fun insert(vararg retailer: RetailerEntity)

    @Query("DELETE FROM " + AppConstant.RETAILER_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.RETAILER_TABLE + " where retailer_id=:retailer_id")
    fun getSingleItem(retailer_id: String): RetailerEntity

    @Query("SELECT * FROM " + AppConstant.RETAILER_TABLE + " where type_id=:type_id")
    fun getItemTypeWise(type_id: String): List<RetailerEntity>
}
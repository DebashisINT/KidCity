package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Created by Pratishruti on 05-03-2018.
 */
@Dao
interface MarketingCategoryMasterDao {
    @Query("SELECT * FROM marketing_category_master_table")
    fun getAll(): List<MarketingCategoryMasterEntity>

    @Insert
    fun insertAll(vararg marketingdetail: MarketingCategoryMasterEntity)

    @Query("Select material_id from marketing_category_master_table where material_name=:cat_name")
    fun getMarketingCategoryIdFromName(cat_name:String):String

    @Query("Select material_name from marketing_category_master_table where material_id=:material_id")
    fun getMarketingCategoryNameFromId(material_id:String):String

    @Query("Delete from marketing_category_master_table")
    fun deleteAll()
}
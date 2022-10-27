package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Created by Pratishruti on 28-02-2018.
 */
@Dao
interface MarketingDetailDao {
    @Query("SELECT * FROM marketing_category")
    fun getAll(): List<MarketingDetailEntity>

    @Insert
    fun insertAll(vararg marketingdetail: MarketingDetailEntity)

    @Query("Select * from marketing_category where typeid=:typeid")
    fun getMarketingCategoryFromType(typeid:String):List<MarketingDetailEntity>

    @Query("update marketing_category set date=:date where material_id=:material_id and shop_id=:shop_id")
    fun setMarketingDetailDate(date:String,material_id:String,shop_id:String):Int

    @Query("Select material_id from marketing_category where material_name=:cat_name")
    fun getMarketingCategoryIdFromName(cat_name:String):String


    @Query("Select * from marketing_category where shop_id=:shopId")
    fun getMarketingDetailForShop(shopId:String):List<MarketingDetailEntity>

    @Query("Delete from marketing_category")
    fun deleteAll()

}
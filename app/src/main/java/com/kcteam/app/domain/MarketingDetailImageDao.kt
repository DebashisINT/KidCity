package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Created by Pratishruti on 05-03-2018.
 */
@Dao
interface MarketingDetailImageDao {
    @Query("SELECT * FROM marketing_image")
    fun getAll(): List<MarketingDetailImageEntity>

    @Insert
    fun insertAll(vararg marketingdetail: MarketingDetailImageEntity)

    @Query("Select * from marketing_image where shop_id=:shop_id")
    fun getImageDetailForShop(shop_id:String):List<MarketingDetailImageEntity>

    @Query("DELETE FROM marketing_image WHERE shop_id = :shop_id AND image_id = :image_id")
    fun deleteMarketingImage(shop_id:String,image_id:String):Int

    @Query("Select * from marketing_image where shop_id = :shop_id AND image_id = :image_id")
    fun getAllImageForId(shop_id:String,image_id:String):List<MarketingDetailImageEntity>

    @Query("Delete from marketing_image")
    fun deleteAll()
}
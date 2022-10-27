package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 27-09-2018.
 */
@Dao
interface ShopVisitImageDao {

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_IMAGE_TABLE)
    fun getAll(): List<ShopVisitImageModelEntity>

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_IMAGE_TABLE + " where isUploaded=:isUploaded")
    fun getUnSyncedList(isUploaded: Boolean): List<ShopVisitImageModelEntity>

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_IMAGE_TABLE + " where isUploaded=:isUploaded and shop_id=:shop_id")
    fun getUnSyncedListAccordingToShopId(isUploaded: Boolean, shop_id: String): List<ShopVisitImageModelEntity>

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_IMAGE_TABLE + " where isUploaded=:isUploaded and shop_id=:shop_id")
    fun getUnSyncedData(isUploaded: Boolean, shop_id: String): ShopVisitImageModelEntity

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_IMAGE_TABLE + " where isUploaded=:isUploaded and shop_id=:shop_id and visit_datetime=:visit_datetime")
    fun getTodaysUnSyncedListAccordingToShopId(isUploaded: Boolean, shop_id: String, visit_datetime: String): List<ShopVisitImageModelEntity>

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_IMAGE_TABLE + " where shop_id=:shop_id and visit_datetime=:visit_datetime")
    fun getTodaysListAccordingToShopId(shop_id: String, visit_datetime: String): List<ShopVisitImageModelEntity>

    @Insert
    fun insert(vararg shopVisit: ShopVisitImageModelEntity)

    @Query("update " + AppConstant.SHOP_VISIT_IMAGE_TABLE + " set isUploaded=:isUploaded where shop_id=:shop_id")
    fun updateisUploaded(isUploaded: Boolean, shop_id: String)

    /*@Query("update " + AppConstant.SHOP_VISIT_IMAGE_TABLE + " set isUploaded=:isUploaded where id=:id")
    fun updateisUploadedAccordingToShopId(isUploaded: Boolean, id: Int)*/

    @Query("DELETE FROM " + AppConstant.SHOP_VISIT_IMAGE_TABLE)
    fun delete()
}
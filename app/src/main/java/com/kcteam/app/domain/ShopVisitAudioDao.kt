package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 21-Jul-20.
 */
@Dao
interface ShopVisitAudioDao {

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_AUDIO_TABLE)
    fun getAll(): List<ShopVisitAudioEntity>

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_AUDIO_TABLE + " where isUploaded=:isUploaded")
    fun getUnSyncedList(isUploaded: Boolean): List<ShopVisitAudioEntity>

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_AUDIO_TABLE + " where isUploaded=:isUploaded and shop_id=:shop_id")
    fun getUnSyncedListAccordingToShopId(isUploaded: Boolean, shop_id: String): List<ShopVisitAudioEntity>

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_AUDIO_TABLE + " where isUploaded=:isUploaded and shop_id=:shop_id")
    fun getUnSyncedData(isUploaded: Boolean, shop_id: String): ShopVisitAudioEntity

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_AUDIO_TABLE + " where isUploaded=:isUploaded and shop_id=:shop_id and visit_datetime=:visit_datetime")
    fun getTodaysUnSyncedListAccordingToShopId(isUploaded: Boolean, shop_id: String, visit_datetime: String): List<ShopVisitAudioEntity>

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_AUDIO_TABLE + " where shop_id=:shop_id and visit_datetime=:visit_datetime")
    fun getTodaysListAccordingToShopId(shop_id: String, visit_datetime: String): List<ShopVisitAudioEntity>

    @Insert
    fun insert(vararg shopVisit: ShopVisitAudioEntity)

    @Query("update " + AppConstant.SHOP_VISIT_AUDIO_TABLE + " set isUploaded=:isUploaded where shop_id=:shop_id")
    fun updateisUploaded(isUploaded: Boolean, shop_id: String)

    @Query("DELETE FROM " + AppConstant.SHOP_VISIT_AUDIO_TABLE)
    fun delete()
    
}
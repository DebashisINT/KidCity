package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 14-12-2018.
 */
@Dao
interface OutstandingListDao {
    
    @Query("SELECT * FROM " + AppConstant.UPDATE_OUTSTANDING_TABLE)
    fun getAll(): List<OutstandingListEntity>

    @Query("SELECT * FROM " + AppConstant.UPDATE_OUTSTANDING_TABLE + " where isUploaded=:isUploaded")
    fun getOutstandingAccordingToSyncState(isUploaded: Boolean): List<OutstandingListEntity>

    @Query("SELECT * FROM " + AppConstant.UPDATE_OUTSTANDING_TABLE + " where shop_id=:shop_id")
    fun getOutstandingAccordingToShopId(shop_id: String): List<OutstandingListEntity>

    @Query("SELECT * FROM " + AppConstant.UPDATE_OUTSTANDING_TABLE + " where isUploaded=:isUploaded and shop_id=:shop_id")
    fun getOutstandingAccordingToSyncStateId(isUploaded: Boolean, shop_id: String): List<OutstandingListEntity>

    @Query("SELECT * FROM " + AppConstant.UPDATE_OUTSTANDING_TABLE + " where current_date=:current_date")
    fun getOutstandingDateWise(current_date: String): List<OutstandingListEntity>

    @Query("update " + AppConstant.UPDATE_OUTSTANDING_TABLE + " set isUploaded=:isUploaded where shop_id=:shop_id")
    fun updateIsUploaded(isUploaded: Boolean, shop_id: String)

    @Query("update " + AppConstant.UPDATE_OUTSTANDING_TABLE + " set isUploaded=:isUploaded where id=:id")
    fun updateIsUploadedAccordingToId(isUploaded: Boolean, id: Int)
    
    @Insert
    fun insert(vararg orderProductList: OutstandingListEntity)

    @Query("DELETE FROM " + AppConstant.UPDATE_OUTSTANDING_TABLE)
    fun delete()
}
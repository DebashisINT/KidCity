package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface ReturnDetailsDao {
    @Query("SELECT * FROM " + AppConstant.RETURN_DETAILS_TABLE)
    fun getAll(): List<ReturnDetailsEntity>

    @Query("SELECT * FROM " + AppConstant.RETURN_DETAILS_TABLE + " where shop_id=:shop_id order by id desc")
    fun getListAccordingToShopId(shop_id: String): List<ReturnDetailsEntity>

    @Insert
    fun insert(vararg returnDetailsEntity: ReturnDetailsEntity)

    @Query("update " + AppConstant.RETURN_DETAILS_TABLE + " set isUploaded=:isUploaded where return_id=:return_id")
    fun updateIsUploaded(isUploaded: Boolean,return_id: String)

    @Query("Select * from "+ AppConstant.RETURN_DETAILS_TABLE+ " where isUploaded = 0 order by id asc" )
    fun getAllUnsynced(): List<ReturnDetailsEntity>


    @Query("SELECT * FROM " + AppConstant.RETURN_DETAILS_TABLE + " where only_date=:date order by id desc")
    fun getListAccordingDate(date: String): List<ReturnDetailsEntity>





}
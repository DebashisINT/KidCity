package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant
import com.kcteam.features.logoutsync.presentation.LogoutSyncFragment

@Dao
interface AddShopSecondaryImgDao {

    @Query("SELECT * FROM " + AppConstant.ADDSHOP_SECONDARY_IMG_TABLE)
    fun getAll(): List<AddShopSecondaryImgEntity>

    @Query("update " + AppConstant.ADDSHOP_SECONDARY_IMG_TABLE + " set rubylead_image1=:rubylead_image1 " +
            ",  rubylead_image2=:rubylead_image2" +
            " where lead_shop_id=:lead_shop_id")
    fun updateListOfSecImages(lead_shop_id: String,rubylead_image1: String,rubylead_image2: String)


    @Insert
    fun insert(vararg shopVisitCompetetor: AddShopSecondaryImgEntity)

    @Query("update " + AppConstant.ADDSHOP_SECONDARY_IMG_TABLE + " set isUploaded_image1=:isUploaded_image1 where lead_shop_id=:lead_shop_id")
    fun updateisUploaded1(isUploaded_image1: Boolean, lead_shop_id: String)

    @Query("update " + AppConstant.ADDSHOP_SECONDARY_IMG_TABLE + " set isUploaded_image2=:isUploaded_image2 where lead_shop_id=:lead_shop_id")
    fun updateisUploaded2(isUploaded_image2: Boolean, lead_shop_id: String)


    @Query("SELECT  lead_shop_id,rubylead_image1 FROM " + AppConstant.ADDSHOP_SECONDARY_IMG_TABLE + " where isUploaded_image1=:isUploaded_image1")
    fun getUnsnycShopImage1(isUploaded_image1: Boolean):List<LogoutSyncFragment.SecondaryShopImg1>

    @Query("SELECT  lead_shop_id,rubylead_image2 FROM " + AppConstant.ADDSHOP_SECONDARY_IMG_TABLE + " where isUploaded_image2=:isUploaded_image2")
    fun getUnsnycShopImage2(isUploaded_image2: Boolean):List<LogoutSyncFragment.SecondaryShopImg2>
}
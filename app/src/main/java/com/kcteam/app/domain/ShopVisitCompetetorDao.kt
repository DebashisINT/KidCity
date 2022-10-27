package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface ShopVisitCompetetorDao {

    @Insert
    fun insert(vararg shopVisitCompetetor: ShopVisitCompetetorModelEntity)

    @Query("update " + AppConstant.SHOP_VISIT_COMPETETOR_IMAGE_TABLE + " set isUploaded=:isUploaded where shop_id=:shop_id")
    fun updateisUploaded(isUploaded: Boolean, shop_id: String)

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_COMPETETOR_IMAGE_TABLE +" where user_id =:user_id and isUploaded = 0")
    fun getUnSyncedCopetetorImg(user_id:String): List<ShopVisitCompetetorModelEntity>

    @Query("SELECT * FROM " + AppConstant.SHOP_VISIT_COMPETETOR_IMAGE_TABLE +" where shop_id =:shop_id and isUploaded = 0")
    fun getUnSyncedCopetetorImgByShopID(shop_id:String): List<ShopVisitCompetetorModelEntity>

    @Query("DELETE FROM " + AppConstant.SHOP_VISIT_COMPETETOR_IMAGE_TABLE )
    fun deleteUnSyncedCopetetorImg()

}
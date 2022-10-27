package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 03-Jul-20.
 */
@Dao
interface MemberShopDao {

    @Query("SELECT * FROM " + AppConstant.MEMBER_SHOP_TABLE)
    fun getAll(): List<MemberShopEntity>

    @Query("SELECT * FROM " + AppConstant.MEMBER_SHOP_TABLE + " where user_id=:user_id")
    fun getSingleUserShop(user_id: String): List<MemberShopEntity>

    @Query("SELECT * FROM " + AppConstant.MEMBER_SHOP_TABLE + " where user_id=:user_id and area_id=:area_id")
    fun getSingleUserShopAreaWise(user_id: String, area_id: String): List<MemberShopEntity>

    @Insert
    fun insertAll(vararg memberShop: MemberShopEntity)

    @Query("DELETE FROM " + AppConstant.MEMBER_SHOP_TABLE)
    fun deleteAll()

    @Query("update " + AppConstant.MEMBER_SHOP_TABLE + " set isUploaded=:isUploaded where shop_id=:shop_id")
    fun updateIsUploaded(isUploaded: Boolean, shop_id: String)

    @Update
    fun updateShop(member: MemberShopEntity)

    @Query("SELECT * FROM " + AppConstant.MEMBER_SHOP_TABLE + " where isUploaded=:isUploaded")
    fun getShopSyncWise(isUploaded: Boolean): List<MemberShopEntity>

    @Query("SELECT * FROM " + AppConstant.MEMBER_SHOP_TABLE + " where shop_id=:shop_id")
    fun getSingleShop(shop_id: String): MemberShopEntity
}
package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface ShopTypeStockViewStatusDao {

    @Insert
    fun insertAll(vararg shopTypeStockViewStatus: ShopTypeStockViewStatus)

    @Query("Select CurrentStockEnable from "+ AppConstant.SHOP_TYPE_STOCK_VIEW_STATUS+ " where shoptype_id =:shoptype_id " )
    fun getShopCurrentStockViewStatus(shoptype_id:String) : Int

    @Query("Select CompetitorStockEnable from "+ AppConstant.SHOP_TYPE_STOCK_VIEW_STATUS+ " where shoptype_id =:shoptype_id " )
    fun getShopCompetitorStockViewStatus(shoptype_id:String) : Int

    @Query("DELETE FROM " + AppConstant.SHOP_TYPE_STOCK_VIEW_STATUS)
    fun deleteAll()

}
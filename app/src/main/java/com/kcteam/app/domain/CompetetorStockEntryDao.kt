package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface CompetetorStockEntryDao {
    @Insert
    fun insert(vararg competetorStockEntry: CcompetetorStockEntryModelEntity)

    @Query("Select * from "+ AppConstant.SHOP_COMTETETOR_STOCK_TABLE)
    fun getCompetetorStockAll(): List<CcompetetorStockEntryModelEntity>

    @Query("Select * from "+ AppConstant.SHOP_COMTETETOR_STOCK_TABLE+ " where shop_id=:shop_id")
    fun getCompetetorStockAllByShopID(shop_id:String): List<CcompetetorStockEntryModelEntity>

    @Query("Select * from "+ AppConstant.SHOP_COMTETETOR_STOCK_TABLE +" where competitor_stock_id=:competitor_stock_id and isUploaded = 0 ")
    fun getCompetetorStockByStockIDUnsynced(competitor_stock_id:String): CcompetetorStockEntryModelEntity

    @Query("Select * from "+ AppConstant.SHOP_COMTETETOR_STOCK_TABLE+ " where isUploaded = 0 order by id asc" )
    fun getShopCompetetorStockAllUnsynced(): List<CcompetetorStockEntryModelEntity>


    @Query("update "+ AppConstant.SHOP_COMTETETOR_STOCK_TABLE+ " set isUploaded = 1 where competitor_stock_id=:competitor_stock_id" )
    fun syncShopCompStocktable(competitor_stock_id:String)

}
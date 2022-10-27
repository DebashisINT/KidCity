package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface CurrentStockEntryDao {

    @Insert
    fun insert(vararg currentStockEntry: CurrentStockEntryModelEntity)

    @Query("Select * from "+ AppConstant.SHOP_CURRENT_STOCK_TABLE)
    fun getShopStockAll(): List<CurrentStockEntryModelEntity>

    @Query("Select * from "+ AppConstant.SHOP_CURRENT_STOCK_TABLE+ " where shop_id=:shop_id" )
    fun getShopStockAllByShopID(shop_id:String): List<CurrentStockEntryModelEntity>

    @Query("Select * from "+ AppConstant.SHOP_CURRENT_STOCK_TABLE+ " where shop_id=:shop_id and isUploaded = 0" )
    fun getShopStockAllByShopIDUnsynced(shop_id:String): List<CurrentStockEntryModelEntity>

    @Query("Select * from "+ AppConstant.SHOP_CURRENT_STOCK_TABLE+ " where stock_id=:stock_id ")
    fun getShopStockAllByStockIDUnsynced(stock_id:String): CurrentStockEntryModelEntity

    @Query("Select * from "+ AppConstant.SHOP_CURRENT_STOCK_TABLE+ " where isUploaded = 0  order by id asc " )
    fun getShopStockAllUnsynced(): List<CurrentStockEntryModelEntity>

    @Query("update "+ AppConstant.SHOP_CURRENT_STOCK_TABLE+ " set isUploaded = 1 where stock_id=:stock_id" )
    fun syncShopStocktable(stock_id:String)

}
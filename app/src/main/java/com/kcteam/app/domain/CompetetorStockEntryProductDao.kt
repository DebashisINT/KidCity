package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface CompetetorStockEntryProductDao {
    @Insert
    fun insert(vararg competetorStockProductEntry: CompetetorStockEntryProductModelEntity)

    @Query("Select * from "+ AppConstant.SHOP_COMTETETOR_STOCK_PRODUCTS_TABLE +" where competitor_stock_id=:competitor_stock_id and isUploaded = 0 ")
    fun getComProductStockByStockIDUnsynced(competitor_stock_id:String): List<CompetetorStockEntryProductModelEntity>

    @Query("Select * from "+ AppConstant.SHOP_COMTETETOR_STOCK_PRODUCTS_TABLE+" where shop_id=:shop_id" )
    fun getComProductStockAllByShopID(shop_id:String): List<CompetetorStockEntryProductModelEntity>

    @Query("Select * from "+ AppConstant.SHOP_COMTETETOR_STOCK_PRODUCTS_TABLE+" where competitor_stock_id=:competitor_stock_id" )
    fun getComProductStockAllByStockID(competitor_stock_id:String): List<CompetetorStockEntryProductModelEntity>

    @Query("update "+ AppConstant.SHOP_COMTETETOR_STOCK_PRODUCTS_TABLE+ " set isUploaded = 1 where competitor_stock_id=:competitor_stock_id" )
    fun syncShopCompProductable(competitor_stock_id:String)

}
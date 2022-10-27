package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface ReturnProductListDao {
    @Query("SELECT * FROM " + AppConstant.RETURN_PRODUCT_LIST_TABLE)
    fun getAll(): List<ReturnProductListEntity>

    @Query("SELECT * FROM " + AppConstant.RETURN_PRODUCT_LIST_TABLE + " where return_id=:return_id")
    fun getDataAccordingToOrderId(return_id: String): List<ReturnProductListEntity>


    @Insert
    fun insert(vararg returnProductList: ReturnProductListEntity)

    @Query("Select * from "+ AppConstant.RETURN_PRODUCT_LIST_TABLE +" where return_id=:return_id")
    fun getIDUnsynced(return_id:String): List<ReturnProductListEntity>

}
package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface NewOrderProductDao {
    @Insert
    fun insert(vararg newOrderProductEntity: NewOrderProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<NewOrderProductEntity>)

    @Query("SELECT * FROM " + AppConstant.NEW_ORDER_PRODUCT + " where product_for_gender=:product_for_gender ")
    fun getProductListGenderWise(product_for_gender:String): List<NewOrderProductEntity>

    @Query("DELETE FROM " + AppConstant.NEW_ORDER_PRODUCT)
    fun deleteAll()

    @Query("SELECT product_name FROM " + AppConstant.NEW_ORDER_PRODUCT + " where product_id=:product_id ")
    fun getNewOrderProductName(product_id:String): String

    @Query("update " + AppConstant.NEW_ORDER_PRODUCT+" set product_name = UPPER(product_name), product_for_gender = UPPER(product_for_gender)")
    fun updateProducttoUpperCase()

    @Query("SELECT * FROM " + AppConstant.NEW_ORDER_PRODUCT )
    fun getAllProduct(): List<NewOrderProductEntity>


}
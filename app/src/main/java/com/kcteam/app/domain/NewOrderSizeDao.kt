package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface NewOrderSizeDao {
    @Insert
    fun insert(vararg newOrderSizeEntity: NewOrderSizeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<NewOrderSizeEntity>)

    @Query("DELETE FROM " + AppConstant.NEW_ORDER_SIZE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.NEW_ORDER_SIZE + " where product_id=:product_id ")
    fun getSizeListProductWise(product_id:Int): List<NewOrderSizeEntity>

    @Query("update " + AppConstant.NEW_ORDER_SIZE+" set size = UPPER(size) ")
    fun updateSizeNametoUpperCase()
}
package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant


@Dao
interface NewOrderColorDao {
    @Insert
    fun insert(vararg newOrderColorEntity: NewOrderColorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<NewOrderColorEntity>)

    @Query("DELETE FROM " + AppConstant.NEW_ORDER_COLOR)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.NEW_ORDER_COLOR + " where product_id=:product_id ")
    fun getColorListProductWise(product_id:Int): List<NewOrderColorEntity>

    @Query("SELECT color_name FROM " + AppConstant.NEW_ORDER_COLOR + " where color_id=:color_id ")
    fun getNewOrderColorName(color_id:String): String

    @Query("update " + AppConstant.NEW_ORDER_COLOR+" set color_name = UPPER(color_name) ")
    fun updateColorNametoUpperCase()
}
package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface PaymentModeDao {
    @Query("SELECT * FROM " + AppConstant.PAYMENT_MODE_TABLE)
    fun getAll(): List<PaymentModeEntity>

    @Insert
    fun insert(vararg performance: PaymentModeEntity)

    @Query("DELETE FROM " + AppConstant.PAYMENT_MODE_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.PAYMENT_MODE_TABLE + " where payment_id=:payment_id")
    fun getSingleData(payment_id: String): PaymentModeEntity
}
package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface NewOrderGenderDao {

    @Insert
    fun insert(vararg newOrderGenderEntity: NewOrderGenderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<NewOrderGenderEntity>)

    @Query("DELETE FROM " + AppConstant.NEW_ORDER_GENDER)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.NEW_ORDER_GENDER)
    fun getGenderList(): List<NewOrderGenderEntity>

    @Query("update " + AppConstant.NEW_ORDER_GENDER+" set gender = UPPER(gender) ")
    fun updateGendertoUpperCase()
}
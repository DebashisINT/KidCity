package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface DealerDao {
    @Query("SELECT * FROM " + AppConstant.DEALER_TABLE)
    fun getAll(): List<DealerEntity>

    @Insert
    fun insert(vararg dealer: DealerEntity)

    @Query("DELETE FROM " + AppConstant.DEALER_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.DEALER_TABLE + " where dealer_id=:dealer_id")
    fun getSingleItem(dealer_id: String): DealerEntity
}
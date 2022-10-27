package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface PartyStatusDao {
    @Query("SELECT * FROM " + AppConstant.PARTY_STATUS_TABLE)
    fun getAll(): List<PartyStatusEntity>

    @Insert
    fun insert(vararg partyStatus: PartyStatusEntity)

    @Query("DELETE FROM " + AppConstant.PARTY_STATUS_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.PARTY_STATUS_TABLE + " where party_status_id=:party_status_id")
    fun getSingleItem(party_status_id: String): PartyStatusEntity
}
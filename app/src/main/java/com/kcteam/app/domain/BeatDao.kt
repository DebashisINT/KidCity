package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface BeatDao {
    @Query("SELECT * FROM " + AppConstant.BEAT_TABLE + " order by name")
    fun getAll(): List<BeatEntity>

    @Insert
    fun insert(vararg beat: BeatEntity)

    @Query("DELETE FROM " + AppConstant.BEAT_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.BEAT_TABLE + " where beat_id=:beat_id")
    fun getSingleItem(beat_id: String): BeatEntity

    @Query("Select * from " + AppConstant.BEAT_TABLE + " where name LIKE '%' || :name  || '%' ")
    fun getBeatBySearchData(name: String): List<BeatEntity>

}
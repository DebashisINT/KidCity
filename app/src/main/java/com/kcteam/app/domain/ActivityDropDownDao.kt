package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface ActivityDropDownDao {

    @Query("SELECT * FROM " + AppConstant.ACTIVITY_DROPDOWN_TABLE)
    fun getAll(): List<ActivityDropDownEntity>

    @Insert
    fun insertAll(vararg activity: ActivityDropDownEntity)

    @Query("DELETE FROM " + AppConstant.ACTIVITY_DROPDOWN_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.ACTIVITY_DROPDOWN_TABLE + " where activity_id=:activity_id")
    fun getSingleItem(activity_id: String): ActivityDropDownEntity
}
package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 10-Jul-20.
 */
@Dao
interface ActivityListDao {

    @Query("SELECT * FROM " + AppConstant.ACTIVITY_LIST)
    fun getAll(): List<ActivityListEntity>

    @Insert
    fun insertAll(vararg activity: ActivityListEntity)

    @Query("DELETE FROM " + AppConstant.ACTIVITY_LIST)
    fun deleteAll()

}
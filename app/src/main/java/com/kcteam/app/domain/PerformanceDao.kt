package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 24-10-2018.
 */
@Dao
interface PerformanceDao {

    @Query("SELECT * FROM " + AppConstant.PERFORMANCE_TABLE)
    fun getAll(): List<PerformanceEntity>

    @Query("SELECT * FROM " + AppConstant.PERFORMANCE_TABLE + " where date=:date")
    fun getTodaysData(date: String): PerformanceEntity

    @Insert
    fun insert(vararg performance: PerformanceEntity)

    @Query("update " + AppConstant.PERFORMANCE_TABLE + " set gps_off_duration=:gps_off_duration where date=:date")
    fun updateGPSoffDuration(gps_off_duration: String, date: String)

    @Query("update " + AppConstant.PERFORMANCE_TABLE + " set ideal_duration=:ideal_duration where date=:date")
    fun updateIdealDuration(ideal_duration: String, date: String)

    @Query("update " + AppConstant.PERFORMANCE_TABLE + " set total_duration_spent=:total_duration_spent where date=:date")
    fun updateTotalDuration(total_duration_spent: String, date: String)

    @Query("update " + AppConstant.PERFORMANCE_TABLE + " set total_shop_visited=:total_shop_visited where date=:date")
    fun updateTotalShopVisited(total_shop_visited: String, date: String)

    @Query("update " + AppConstant.PERFORMANCE_TABLE + " set total_distance=:total_distance where date=:date")
    fun updateTotalDistance(total_distance: String, date: String)

    @Query("DELETE FROM " + AppConstant.PERFORMANCE_TABLE)
    fun delete()
}
package com.kcteam.features.login

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kcteam.app.AppConstant

/**
 * Created by Kinsuk on 16-11-2017.
 */

@Dao
interface UserAttendanceDataDao {

    @get:Query("SELECT * FROM " + AppConstant.ATTENDANCE_TABLE)
    val all: MutableList<UserLoginDataEntity>

    @Insert
    fun insertAll(vararg attandancedata: UserLoginDataEntity)

    @Update
    fun update(vararg attandancedata: UserLoginDataEntity)

    @Update
    fun updateLogoutTime(mUserLoginDataEntity: UserLoginDataEntity): Int

    @Query("update attendance set logintime=:loginTime where userId=:user_id and logindate=:loginDate")
    fun updateLoginTime(loginTime: String, user_id: String, loginDate: String)

    @Query("update attendance set Isonleave=:Isonleave where userId=:user_id and logindate=:loginDate")
    fun updateIsLeave(Isonleave: String, user_id: String, loginDate: String)

    @Query("update attendance set logouttime=:logoutTime where userId=:user_id and logindate=:loginDate")
    fun updateLogoutTimeN(logoutTime: String, user_id: String, loginDate: String)

    @Query("update attendance set duration=:duration where userId=:user_id and logindate=:loginDate")
    fun updateDuration(duration: String, user_id: String, loginDate: String)

    @Query("Select * from attendance where userId=:user_id and logindate=:logindate")
    fun getLoginDate(user_id: String, logindate: String): List<UserLoginDataEntity>

    @Query("Select logintime from attendance where userId=:user_id and logindate=:logindate")
    fun getLoginTime(user_id: String, logindate: String): String

    @Query("Select Isonleave from attendance where userId=:user_id and logindate=:logindate")
    fun getIsOnLeave(user_id: String, logindate: String): String

    @Query("DELETE FROM attendance where logindate!=:logindate")
    fun deleteAll(logindate: String): Int

    @Query("DELETE FROM attendance where logindate=:logindate")
    fun deleteTodaysData(logindate: String)

    @Query("DELETE FROM attendance")
    fun delete()

    @Query("SELECT * FROM " + AppConstant.ATTENDANCE_TABLE + " ORDER BY logindate_number desc")
    fun getAllSortedList(): MutableList<UserLoginDataEntity>
}
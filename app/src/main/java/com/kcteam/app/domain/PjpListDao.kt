package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 02-Jun-20.
 */
@Dao
interface PjpListDao {

    @Insert
    fun insert(vararg pjpList: PjpListEntity)

    @Query("DELETE FROM " + AppConstant.PJP_LIST_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.PJP_LIST_TABLE)
    fun getAll(): List<PjpListEntity>

    @Query("update " + AppConstant.PJP_LIST_TABLE+" set date=:date where pjp_id=:pjp_id")
    fun updatePjp(date:String,pjp_id:String)


    @Query("SELECT * FROM " + AppConstant.PJP_LIST_TABLE + " where date=:date")
    fun getAllByDate(date:String): List<PjpListEntity>
}
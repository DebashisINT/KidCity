package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 10-Jul-20.
 */
@Dao
interface TimesheetProductListDao {

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST)
    fun getAll(): List<TimesheetProductListEntity>

    @Insert
    fun insertAll(vararg client: TimesheetProductListEntity)

    @Query("DELETE FROM " + AppConstant.PRODUCT_LIST)
    fun deleteAll()

}
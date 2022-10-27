package com.kcteam.features.login

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface ShopFeedbackTepDao {
    @Insert
    fun insert(vararg obj: ShopFeedbackTempEntity)

    @Query("DELETE FROM " + AppConstant.TBL_SHOP_FEEDBACK_TEMP)
    fun deleteAll()

    @Query("select * FROM " + AppConstant.TBL_SHOP_FEEDBACK_TEMP +" where shop_id=:shop_id order by date_time desc")
    fun getAllByShopID(shop_id:String):List<ShopFeedbackTempEntity>


    @Query("select * FROM " + AppConstant.TBL_SHOP_FEEDBACK_TEMP )
    fun getAll():List<ShopFeedbackTempEntity>
}
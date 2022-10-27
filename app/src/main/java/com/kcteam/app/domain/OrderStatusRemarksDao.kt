package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant
import com.kcteam.features.myprofile.model.citylist.CityListApiResponse
import io.reactivex.Observable
import retrofit2.http.GET

@Dao
interface OrderStatusRemarksDao {

    @Insert
    fun insert(vararg orderStatusRemarks: OrderStatusRemarksModelEntity)

    @Query("SELECT * FROM shop_order_status_remarks  where shop_revisit_uniqKey=:shop_revisit_uniqKey")
    fun getSingleItem(shop_revisit_uniqKey: String): OrderStatusRemarksModelEntity

    @Query("SELECT * FROM shop_order_status_remarks  where isUploaded = 0 ORDER BY id ASC")
    fun getUnsyncedList(): List<OrderStatusRemarksModelEntity>

    @Query("update shop_order_status_remarks  set isUploaded = 1 where  shop_revisit_uniqKey=:shop_revisit_uniqKey")
    fun updateOrderStatus(shop_revisit_uniqKey: String)
}
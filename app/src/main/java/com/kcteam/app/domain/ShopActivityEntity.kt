package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant.SHOP_ACTIVITY

/**
 * Created by Pratishruti on 07-12-2017.
 */
//@Entity(tableName = SHOP_ACTIVITY)
@Entity(tableName = SHOP_ACTIVITY, indices = [Index(name="ACTIVITYID",value = ["shopActivityId","shopid","visited_date"]),Index(name="ACTIVITY_ID_DATE",value = ["shopid","visited_date"])] )
class ShopActivityEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "shopActivityId")
    var shopActivityId: Int = 0

    @ColumnInfo(name = "shopid")
    var shopid: String? = null

    @ColumnInfo(name = "duration_spent")
    var duration_spent: String = "0"

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "shop_name")
    var shop_name: String? = null

    @ColumnInfo(name = "shop_address")
    var shop_address: String? = null

    @ColumnInfo(name = "visited_date")
    var visited_date: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded = false

    @ColumnInfo(name = "isVisited")
    var isVisited = false

    @ColumnInfo(name = "isDurationCalculated")
    var isDurationCalculated = false

    @ColumnInfo(name = "startTimeStamp")
    var startTimeStamp = "0"

    @ColumnInfo(name = "endTimeStamp")
    var endTimeStamp = "0"

    @ColumnInfo(name = "totalMinute")
    var totalMinute = "0"

    @ColumnInfo(name = "distance_travelled")
    var distance_travelled: String? = null

    @ColumnInfo(name = "feedback")
    var feedback: String? = null

    @ColumnInfo(name = "isFirstShopVisited")
    var isFirstShopVisited = false

    @ColumnInfo(name = "distance_from_home_loc")
    var distance_from_home_loc: String? = null

    @ColumnInfo(name = "next_visit_date")
    var next_visit_date: String? = null

    @ColumnInfo(name = "early_revisit_reason")
    var early_revisit_reason: String? = null

    @ColumnInfo(name = "device_model")
    var device_model: String? = null

    @ColumnInfo(name = "android_version")
    var android_version: String? = null

    @ColumnInfo(name = "battery")
    var battery: String? = null

    @ColumnInfo(name = "net_status")
    var net_status: String? = null

    @ColumnInfo(name = "net_type")
    var net_type: String? = null

    @ColumnInfo(name = "in_time")
    var in_time: String? = null

    @ColumnInfo(name = "out_time")
    var out_time: String? = null

    @ColumnInfo(name = "in_loc")
    var in_loc: String? = null

    @ColumnInfo(name = "out_loc")
    var out_loc: String? = null

    @ColumnInfo(name = "shop_revisit_uniqKey")
    var shop_revisit_uniqKey: String? = null

    @ColumnInfo(name = "updated_by")
    var updated_by: String? = null

    @ColumnInfo(name = "updated_on")
    var updated_on: String? = null

    @ColumnInfo(name = "approximate_1st_billing_value")
    var approximate_1st_billing_value: String? = null

    @ColumnInfo(name = "agency_name")
    var agency_name: String? = null

    @ColumnInfo(name = "pros_id") // pros_id used as stage_id in pref.IsnewleadtypeforRuby and type of shop = 16
    var pros_id: String? = null

}
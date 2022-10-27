package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 03-Jul-20.
 */
@Entity(tableName = AppConstant.MEMBER_SHOP_TABLE)
class MemberShopEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "user_id")
    var user_id: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "shop_name")
    var shop_name: String? = null

    @ColumnInfo(name = "shop_lat")
    var shop_lat: String? = null

    @ColumnInfo(name = "shop_long")
    var shop_long: String? = null

    @ColumnInfo(name = "shop_address")
    var shop_address: String? = null

    @ColumnInfo(name = "shop_pincode")
    var shop_pincode: String? = null

    @ColumnInfo(name = "shop_contact")
    var shop_contact: String? = null

    @ColumnInfo(name = "total_visited")
    var total_visited: String? = null

    @ColumnInfo(name = "last_visit_date")
    var last_visit_date: String? = null

    @ColumnInfo(name = "shop_type")
    var shop_type: String? = null

    @ColumnInfo(name = "dd_name")
    var dd_name: String? = null

    @ColumnInfo(name = "entity_code")
    var entity_code: String? = null

    @ColumnInfo(name = "model_id")
    var model_id: String? = null

    @ColumnInfo(name = "primary_app_id")
    var primary_app_id: String? = null

    @ColumnInfo(name = "secondary_app_id")
    var secondary_app_id: String? = null

    @ColumnInfo(name = "lead_id")
    var lead_id: String? = null

    @ColumnInfo(name = "funnel_stage_id")
    var funnel_stage_id: String? = null

    @ColumnInfo(name = "stage_id")
    var stage_id: String? = null

    @ColumnInfo(name = "booking_amount")
    var booking_amount: String? = null

    @ColumnInfo(name = "type_id")
    var type_id: String? = null

    @ColumnInfo(name = "area_id")
    var area_id: String? = null

    @ColumnInfo(name = "assign_to_pp_id")
    var assign_to_pp_id: String? = null

    @ColumnInfo(name = "assign_to_dd_id")
    var assign_to_dd_id: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "date_time")
    var date_time: String? = null
}
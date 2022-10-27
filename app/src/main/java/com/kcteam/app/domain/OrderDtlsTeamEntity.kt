package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant


@Entity(tableName = AppConstant.ORDER_DTLS_TEAM)
class OrderDtlsTeamEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "amount")
    var amount: String? = null

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "order_id")
    var order_id: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "collection")
    var collection: String? = null

    @ColumnInfo(name = "only_date")
    var only_date: String? = null

    @ColumnInfo(name = "order_lat")
    var order_lat: String? = null

    @ColumnInfo(name = "order_long")
    var order_long: String? = null

    @ColumnInfo(name = "remarks")
    var remarks: String? = null

    @ColumnInfo(name = "signature")
    var signature: String? = null

    @ColumnInfo(name = "patient_no")
    var patient_no: String? = null

    @ColumnInfo(name = "patient_name")
    var patient_name: String? = null

    @ColumnInfo(name = "patient_address")
    var patient_address: String? = null

    @ColumnInfo(name = "scheme_amount")
    var scheme_amount: String? = null

    @ColumnInfo(name = "Hospital")
    var Hospital: String? = null

    @ColumnInfo(name = "Email_Address")
    var Email_Address: String? = null


}
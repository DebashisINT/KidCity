package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 26-10-2018.
 */
@Entity(tableName = AppConstant.COLL_DTLS_TEAM)
class CollDtlsTeamEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "collection_id")
    var collection_id: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "collection")
    var collection: String? = null

    @ColumnInfo(name = "only_time")
    var only_time: String? = null

    @ColumnInfo(name = "bill_id")
    var bill_id: String? = null

    @ColumnInfo(name = "order_id")
    var order_id: String? = null

    @ColumnInfo(name = "payment_id")
    var payment_id: String? = null

    @ColumnInfo(name = "instrument_no")
    var instrument_no: String? = null

    @ColumnInfo(name = "bank")
    var bank: String? = null

    @ColumnInfo(name = "file_path")
    var file_path: String? = null

    @ColumnInfo(name = "feedback")
    var feedback: String? = null

    @ColumnInfo(name = "patient_no")
    var patient_no: String? = null

    @ColumnInfo(name = "patient_name")
    var patient_name: String? = null

    @ColumnInfo(name = "patient_address")
    var patient_address: String? = null

    @ColumnInfo(name = "Hospital")
    var Hospital: String? = null

    @ColumnInfo(name = "Email_Address")
    var Email_Address: String? = null



}
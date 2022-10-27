package com.kcteam.features.alarm.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kinsuk on 18-02-2019.
 */
data class AlarmData(
        var requestCode: Int = 0,
        var id: String? = null,
        var alarm_time_hours: String = "",
        var alarm_time_mins: String = "",
        var report_id: String = "",
        var report_title: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(requestCode)
        parcel.writeString(id)
        parcel.writeString(alarm_time_hours)
        parcel.writeString(alarm_time_mins)
        parcel.writeString(report_id)
        parcel.writeString(report_title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlarmData> {
        override fun createFromParcel(parcel: Parcel): AlarmData {
            return AlarmData(parcel)
        }

        override fun newArray(size: Int): Array<AlarmData?> {
            return arrayOfNulls(size)
        }
    }
}



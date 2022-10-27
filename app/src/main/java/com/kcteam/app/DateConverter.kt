package com.kcteam.app

import androidx.room.TypeConverter
import java.util.*

/**
 * Created by Pratishruti on 24-11-2017.
 */

class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return (if (date == null) null else date.time)!!.toLong()
    }
}
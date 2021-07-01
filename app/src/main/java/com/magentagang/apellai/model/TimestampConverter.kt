package com.magentagang.apellai.model

import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimestampConverter {
    companion object{
        var df : DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        @TypeConverter
        fun fromTimestamp(value: String): Date?
        {
            if (value != null) {
                try {
                    return df.parse(value)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                return null
            } else {
                return null
            }
        }
    }
}
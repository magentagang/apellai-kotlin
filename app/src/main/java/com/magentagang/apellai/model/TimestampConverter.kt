package com.magentagang.apellai.model

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TimestampConverter {
    companion object {
        @SuppressLint("SimpleDateFormat")
        private var df: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")

        @TypeConverter
        fun fromTimestamp(value: String): Date? {
            try {
                return df.parse(value)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return null
        }
    }
}
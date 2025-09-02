package com.example.criminalintent.database

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class CrimeTypeConvertor {

    @TypeConverter
    fun fromUUID(id: UUID?): String? {
        return id.toString()
    }

    @TypeConverter
    fun toUUID(id: String?): UUID? {
        return UUID.fromString(id)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(millisSinceEpoch)
        }
    }
}
package com.example.criminalintent.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.criminalintent.database.convertor.CrimeTypeConvertor
import com.example.criminalintent.database.dao.CrimeDao
import com.example.criminalintent.database.entity.Crime

@Database(
    version = 1,
    entities = [Crime::class],
    exportSchema = true
)
@TypeConverters(CrimeTypeConvertor::class)
abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao

}
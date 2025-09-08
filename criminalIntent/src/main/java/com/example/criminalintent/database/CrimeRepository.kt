package com.example.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.criminalintent.CrimeApplication
import com.example.criminalintent.CrimeApplication.Companion.context
import com.example.criminalintent.database.entity.Crime
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors

object CrimeRepository {
    private const val DATABASE_NAME = "crime-database"
    private val executor = Executors.newSingleThreadExecutor()
    private val filesDir = context.filesDir
    private val database: CrimeDatabase =
        Room.databaseBuilder(
            CrimeApplication.context,
            klass = CrimeDatabase::class.java,
            name = DATABASE_NAME
        ).addMigrations(object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''")
            }
        })
            .build()

    private val dao = database.crimeDao()

    fun getAllCrimes(): LiveData<List<Crime>> {
        return dao.getAllCrimes()
    }

    fun getCrime(id: UUID): LiveData<Crime?> {
        return dao.getCrime(id)
    }

    fun update(crime: Crime) {
        executor.execute {
            dao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute { dao.addCrime(crime) }
    }

    fun getPhotoFile(crime: Crime) = File(filesDir, crime.getPhotoFileName())

}
package com.example.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.criminalintent.CrimeApplication
import com.example.criminalintent.database.entity.Crime
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

object CrimeRepository {
    private const val DATABASE_NAME = "crime-database"
    private val executor = Executors.newSingleThreadExecutor()

    private val database: CrimeDatabase =
        Room.databaseBuilder(
            CrimeApplication.context,
            klass = CrimeDatabase::class.java,
            name = DATABASE_NAME
        ).build()

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

}
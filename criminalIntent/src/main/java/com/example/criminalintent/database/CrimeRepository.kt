package com.example.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.criminalintent.CrimeApplication
import java.util.UUID

object CrimeRepository {
    private const val DATABASE_NAME = "crime-database"
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

}
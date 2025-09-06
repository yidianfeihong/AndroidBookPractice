package com.example.criminalintent.viewmodel

import androidx.lifecycle.ViewModel
import com.example.criminalintent.database.CrimeRepository
import com.example.criminalintent.database.entity.Crime

class CrimeListViewmodel : ViewModel() {

    val crimeListLiveData by lazy { CrimeRepository.getAllCrimes() }

    fun addCrime(crime: Crime) {
        CrimeRepository.addCrime(crime)
    }

}
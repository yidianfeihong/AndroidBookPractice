package com.example.criminalintent.viewmodel

import androidx.lifecycle.ViewModel
import com.example.criminalintent.database.CrimeRepository

class CrimeListViewmodel : ViewModel() {
    val crimeListLiveData by lazy { CrimeRepository.getAllCrimes() }

}
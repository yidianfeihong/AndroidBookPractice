package com.example.criminalintent.viewmodel

import androidx.lifecycle.ViewModel
import com.example.criminalintent.database.Crime
import com.example.criminalintent.database.CrimeRepository

class CrimeListViewmodel : ViewModel() {
    val crimeList by lazy { CrimeRepository.getAllCrimes() }

}
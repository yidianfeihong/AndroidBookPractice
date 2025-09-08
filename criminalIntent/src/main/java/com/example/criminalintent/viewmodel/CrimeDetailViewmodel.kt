package com.example.criminalintent.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.criminalintent.database.CrimeRepository
import com.example.criminalintent.database.entity.Crime
import java.io.File
import java.util.UUID

class CrimeDetailViewmodel : ViewModel() {

    private var crimeIdLiveData = MutableLiveData<UUID>()

    var crimeLiveData: LiveData<Crime?> = crimeIdLiveData.switchMap { crimeId ->
        CrimeRepository.getCrime(crimeId).apply {
            Log.d(TAG, "getCrime crimeId = $crimeId crime = $this")
        }
    }

    fun loadCrime(id: UUID?) {
        if (id == null) {
            return
        }
        crimeIdLiveData.value = id
    }

    fun saveCrime(crime: Crime) {
        CrimeRepository.update(crime)
    }

    fun getCrimePhotoFile(crime: Crime): File {
        return CrimeRepository.getPhotoFile(crime)
    }

    companion object {
        private const val TAG = "CrimeDetailViewmodel"
    }
}
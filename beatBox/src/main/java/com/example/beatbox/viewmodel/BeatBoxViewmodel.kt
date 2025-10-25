package com.example.beatbox.viewmodel

import androidx.lifecycle.ViewModel
import com.example.beatbox.BeatBox

class BeatBoxViewmodel : ViewModel() {

    lateinit var beatBox: BeatBox

    fun isInitBeatBox(): Boolean {
        return ::beatBox.isInitialized
    }

    override fun onCleared() {
        super.onCleared()
        beatBox.release()
    }
}
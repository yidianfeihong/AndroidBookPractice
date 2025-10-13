package com.example.beatbox

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.beatbox.bean.Sound

class SoundViewModel : BaseObservable() {
    var sound: Sound? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    @get:Bindable
    val title: String?
        get() = sound?.name


//    val title : MutableLiveData<String?> = MutableLiveData()

}
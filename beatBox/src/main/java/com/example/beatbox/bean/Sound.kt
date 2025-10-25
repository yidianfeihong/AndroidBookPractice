package com.example.beatbox.bean

const val WAV = ".wav"

class Sound(filePath: String, val soundId: Int? = null) {
    val name = filePath.split("/").last().removeSuffix(WAV)
}
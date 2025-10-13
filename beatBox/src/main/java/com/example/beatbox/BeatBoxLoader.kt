package com.example.beatbox

import android.content.res.AssetManager
import android.util.Log
import com.example.beatbox.bean.Sound
import com.example.beatbox.bean.WAV

const val TAG = "BeatBoxLoader"
const val SOUND_FOLDER = "sample_sounds"


class BeatBoxLoader(val assetManager: AssetManager) {

    var sounds: List<Sound>

    init {
        sounds = loadSounds()
    }

    fun loadSounds(): List<Sound> {
        var soundNames: Array<String>? = null
        val sounds = mutableListOf<Sound>()
        runCatching {
            soundNames = assetManager.list(SOUND_FOLDER)
            if (soundNames.isNullOrEmpty()) {
                return emptyList()
            }
        }.onFailure {
            Log.e(TAG, "asset list fail for ${it.message}")
        }
        soundNames?.forEach { soundName ->
            val simpleName = soundName.removeSuffix(WAV)
            sounds.add(Sound("${SOUND_FOLDER}/${soundName}", simpleName))
        }
        return sounds
    }

}
package com.example.beatbox

import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log
import com.example.beatbox.bean.Sound

const val TAG = "BeatBox"
const val SOUND_FOLDER = "sample_sounds"
const val MAX_SOUNDS = 5

open class BeatBox(val assetManager: AssetManager) {

    var playRate: Float = 1f
    var sounds: List<Sound>
    var soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(MAX_SOUNDS)
        .build()

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
            val soundId = load("$SOUND_FOLDER/$soundName")
            sounds.add(Sound("${SOUND_FOLDER}/${soundName}", soundId))
        }
        return sounds
    }

    private fun load(soundName: String): Int? {
        var soundId: Int? = null
        runCatching {
            val fileDescriptor = assetManager.openFd(soundName)
            soundId = soundPool.load(fileDescriptor, 1)
        }.onFailure {
            Log.e(TAG, "soundPool load fail:${it}")
        }.onSuccess {
            Log.d(TAG, "soundPool load success:${soundName}")
        }
        return soundId
    }

    fun play(sound: Sound) {
        sound.soundId?.let {
            soundPool.play(it, 1.0F, 1.0F, 0, 0, playRate)
        }
    }

    fun release() {
        soundPool.release()
    }
}
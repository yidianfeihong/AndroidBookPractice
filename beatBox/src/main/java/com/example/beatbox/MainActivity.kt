package com.example.beatbox

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beatbox.bean.Sound
import com.example.beatbox.databinding.ActivityMainBinding
import com.example.beatbox.databinding.ItemSoundBinding
import com.example.beatbox.viewmodel.BeatBoxViewmodel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: BeatBoxViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[BeatBoxViewmodel::class.java]
        if (!viewModel.isInitBeatBox()) {
            viewModel.beatBox = BeatBox(assets)
        }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = SoundAdapter(viewModel.beatBox.sounds)
        }
        setPlaySpeed(1f)
        binding.playSpeed.text = getString(R.string.playback_speed, 0)
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                Log.d(TAG, "onProgressChanged progress = $progress")
                binding.playSpeed.text = getString(R.string.playback_speed, progress)
                val playSpeed = (1 + progress / 100.0).toFloat()
                setPlaySpeed(playSpeed)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    fun setPlaySpeed(playSpeed: Float) {
        viewModel.beatBox.playRate = playSpeed
    }

    inner class SoundViewHolder(val itemSoundBinding: ItemSoundBinding) :
        RecyclerView.ViewHolder(itemSoundBinding.root) {

        init {
            itemSoundBinding.viewModel = SoundViewModel(viewModel.beatBox)
            itemSoundBinding.root.setOnClickListener {
                itemSoundBinding.viewModel?.onButtonClicked()
            }
        }

        fun bind(sound: Sound) {
            itemSoundBinding.apply {
                viewModel?.sound = sound
                executePendingBindings()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        viewModel.beatBox.release()
    }

    inner class SoundAdapter(val sounds: List<Sound>) : RecyclerView.Adapter<SoundViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): SoundViewHolder {
            return SoundViewHolder(
                DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.item_sound,
                    parent, false
                )
            )
        }

        override fun onBindViewHolder(
            holder: SoundViewHolder,
            position: Int
        ) {
            holder.bind(sounds[position])
        }

        override fun getItemCount(): Int {
            return sounds.size
        }
    }
}
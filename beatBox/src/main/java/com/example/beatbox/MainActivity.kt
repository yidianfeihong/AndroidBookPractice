package com.example.beatbox

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beatbox.bean.Sound
import com.example.beatbox.databinding.ActivityMainBinding
import com.example.beatbox.databinding.ItemSoundBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = SoundAdapter(BeatBoxLoader(assets).sounds)
        }
    }

    class SoundViewHolder(val itemSoundBinding: ItemSoundBinding) :
        RecyclerView.ViewHolder(itemSoundBinding.root) {

        init {
            itemSoundBinding.viewModel = SoundViewModel()
        }

        fun bind(sound: Sound) {
            itemSoundBinding.apply {
                viewModel?.sound = sound
                executePendingBindings()
            }
        }
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
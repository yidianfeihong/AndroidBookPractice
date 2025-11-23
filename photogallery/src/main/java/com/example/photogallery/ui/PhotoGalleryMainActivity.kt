package com.example.photogallery.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.photogallery.R
import com.example.photogallery.databinding.ActivityMainBinding
import com.example.photogallery.fragment.PhotoGalleryMainFragment

class PhotoGalleryMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "PhotoGalleryActivity"
        fun newIntent(context: Context): Intent {
            return Intent(context, PhotoGalleryMainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Log.d(TAG, "onCreate")
        supportFragmentManager.beginTransaction()
            .replace(binding.main.id, PhotoGalleryMainFragment.newInstance())
            .commit()
    }
}
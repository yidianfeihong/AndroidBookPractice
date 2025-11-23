package com.example.photogallery.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.photogallery.R
import com.example.photogallery.databinding.ActivityPhotoGalleryWebBinding
import com.example.photogallery.fragment.PhotoGalleryWebFragment
import com.example.photogallery.model.PhotoDetail


class PhotoGalleryWebActivity : AppCompatActivity() {
    private lateinit var fragment: PhotoGalleryWebFragment
    private lateinit var binding: ActivityPhotoGalleryWebBinding

    companion object {
        const val TAG = "PhotoGalleryWebActivity"
        fun newIntent(context: Context, photo: PhotoDetail): Intent {
            return Intent(context, PhotoGalleryWebActivity::class.java).apply {
                putExtra(PhotoGalleryWebFragment.ARG_PHOTO, photo)
            }
        }
    }

    val callback = object : OnBackPressedCallback(true /* 默认启用 */) {
        override fun handleOnBackPressed() {
            Log.d(TAG, "handleOnBackPressed")
            if (!fragment.goBack()) {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery_web)
        enableEdgeToEdge()
        binding = ActivityPhotoGalleryWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val photo: PhotoDetail? =
            intent.getSerializableExtra(PhotoGalleryWebFragment.ARG_PHOTO) as? PhotoDetail
        fragment = PhotoGalleryWebFragment.newInstance(photo)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.main.id, fragment)
                .commitNow()
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }
}
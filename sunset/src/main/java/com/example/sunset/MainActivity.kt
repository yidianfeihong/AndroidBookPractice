package com.example.sunset

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var sceneView: View
    private lateinit var sunView: View
    private lateinit var skyView: View

    private val blueSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.blue_sky)
    }
    private val sunsetSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.sunset_sky)
    }
    private val nightSkyColor: Int by lazy {
        ContextCompat.getColor(this, R.color.night_sky)
    }

    var heightAnimator: ObjectAnimator? = null
    var sunsetSkyAnimator: ObjectAnimator? = null
    var nightSkyAnimator: ObjectAnimator? = null

    var isAnimatingForward: Boolean = false
    var animatorSet: AnimatorSet? = null

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scene)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sceneView = findViewById(R.id.scene)
        sunView = findViewById(R.id.sun)
        skyView = findViewById(R.id.sky)
        sceneView.setOnClickListener {
            if (animatorSet == null) {
                initAnimation()
            }
            if (animatorSet?.isRunning == true) {
                return@setOnClickListener
            }
            if (isAnimatingForward) {
                Log.d(TAG, "do reverse")
                animatorSet?.reverse()
            } else {
                Log.d(TAG, "do start")
                animatorSet?.start()
            }
            isAnimatingForward = !isAnimatingForward
        }
    }

    private fun initAnimation() {
        val sunYStart = sunView.top.toFloat()
        val sunYEnd = skyView.height.toFloat()
        heightAnimator = ObjectAnimator
            .ofFloat(sunView, "y", sunYStart, sunYEnd)
            .setDuration(3000)
        heightAnimator?.interpolator = AccelerateInterpolator()
        sunsetSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", blueSkyColor, sunsetSkyColor)
            .setDuration(3000)
        sunsetSkyAnimator?.setEvaluator(ArgbEvaluator())
        nightSkyAnimator = ObjectAnimator
            .ofInt(skyView, "backgroundColor", sunsetSkyColor, nightSkyColor)
            .setDuration(1500)
        nightSkyAnimator?.setEvaluator(ArgbEvaluator())

        animatorSet = AnimatorSet().apply {
            play(heightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator)
        }
    }
}
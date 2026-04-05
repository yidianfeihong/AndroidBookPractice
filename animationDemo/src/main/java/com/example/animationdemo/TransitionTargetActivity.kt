package com.example.animationdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.animationdemo.databinding.ActivityTransitionTargetBinding

/**
 * 转场动画的目标页
 *
 * 注意：共享元素动画中，这个页面的 sharedElement View 的 transitionName
 * 必须和源页面的一致（都是 "shared_box"）
 */
class TransitionTargetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTransitionTargetBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun finish() {
        super.finish()
        // 返回时的转场动画
        @Suppress("DEPRECATION")
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

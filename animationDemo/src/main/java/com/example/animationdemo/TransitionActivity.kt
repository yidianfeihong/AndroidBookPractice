package com.example.animationdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.example.animationdemo.databinding.ActivityTransitionBinding

/**
 * 转场动画（Activity Transition）
 *
 * 【面试核心知识点】
 *
 * 1. overridePendingTransition(enterAnim, exitAnim)
 *    - 最基础的 Activity 切换动画
 *    - 必须在 startActivity() 或 finish() 之后立即调用
 *    - enterAnim：新页面进入的动画
 *    - exitAnim：旧页面退出的动画
 *
 * 2. 共享元素动画（Shared Element Transition）—— API 21+
 *    - 两个页面的某个 View "无缝过渡"
 *    - 关键：两个页面的 View 必须设置相同的 transitionName
 *    - 通过 ActivityOptionsCompat.makeSceneTransitionAnimation() 启动
 *
 * 3. Android 5.0+ 的 Transition 框架：
 *    - window.enterTransition / exitTransition
 *    - Slide、Fade、Explode 等内置转场效果
 *    - 可在 theme 中全局配置
 */
class TransitionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTransitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ========== 滑动转场 ==========
        binding.btnSlide.setOnClickListener {
            val intent = Intent(this, TransitionTargetActivity::class.java)
            startActivity(intent)
            // 必须紧跟 startActivity 之后调用
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // ========== 共享元素转场 ==========
        binding.btnSharedElement.setOnClickListener {
            val intent = Intent(this, TransitionTargetActivity::class.java)
            // 创建共享元素选项
            // 参数：当前页面的 View, transitionName（必须和目标页面一致）
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                binding.sharedElement,   // 源 View
                "shared_box"             // transitionName，和目标页面 XML 中一致
            )
            startActivity(intent, options.toBundle())
        }
    }
}

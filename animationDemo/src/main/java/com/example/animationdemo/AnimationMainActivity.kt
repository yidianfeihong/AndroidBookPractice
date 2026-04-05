package com.example.animationdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.animationdemo.databinding.ActivityAnimationMainBinding

/**
 * 动画体系教学 - 主导航页
 *
 * Android View 体系的动画分为三大类：
 *
 * 1. 帧动画（Frame Animation）
 *    - AnimationDrawable，逐帧播放图片，类似 GIF
 *
 * 2. 补间动画（Tween/View Animation）
 *    - alpha、scale、rotate、translate
 *    - 只改变绘制效果，不改变 View 实际属性（面试重点！）
 *
 * 3. 属性动画（Property Animation）—— Android 3.0 引入
 *    - ValueAnimator：只产生值的变化，需要手动应用
 *    - ObjectAnimator：自动将值应用到目标对象的属性
 *    - AnimatorSet：组合多个动画
 *    - ViewPropertyAnimator：view.animate() 的简便方式
 *
 * 辅助概念：
 *    - Interpolator（插值器）：控制动画速度曲线
 *    - TypeEvaluator（估值器）：控制值的计算方式
 *    - LayoutTransition：布局变化动画
 *    - Activity Transition：页面转场动画
 */
class AnimationMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAnimationMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnTween.setOnClickListener {
            startActivity(Intent(this, TweenAnimationActivity::class.java))
        }
        binding.btnProperty.setOnClickListener {
            startActivity(Intent(this, PropertyAnimationActivity::class.java))
        }
        binding.btnInterpolator.setOnClickListener {
            startActivity(Intent(this, InterpolatorActivity::class.java))
        }
        binding.btnViewPropertyAnimator.setOnClickListener {
            startActivity(Intent(this, ViewPropertyAnimatorActivity::class.java))
        }
        binding.btnFrame.setOnClickListener {
            startActivity(Intent(this, FrameAnimationActivity::class.java))
        }
        binding.btnLayoutTransition.setOnClickListener {
            startActivity(Intent(this, LayoutTransitionActivity::class.java))
        }
        binding.btnTransition.setOnClickListener {
            startActivity(Intent(this, TransitionActivity::class.java))
        }
    }
}

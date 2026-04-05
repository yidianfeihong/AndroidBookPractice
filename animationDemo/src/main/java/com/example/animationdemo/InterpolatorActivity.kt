package com.example.animationdemo

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.animationdemo.databinding.ActivityInterpolatorBinding

/**
 * 插值器（Interpolator）
 *
 * 【面试核心知识点】
 *
 * 插值器决定动画的"速度曲线"：同样 1 秒从 A 到 B，是匀速？先快后慢？弹跳？
 *
 * 原理：
 *   插值器接收一个 input（0~1 的时间进度），返回一个 output（实际的进度值）
 *   output = interpolator.getInterpolation(input)
 *
 *   - LinearInterpolator：output = input（匀速）
 *   - AccelerateInterpolator：output = input²（加速）
 *   - DecelerateInterpolator：output = 1-(1-input)²（减速）
 *   - BounceInterpolator：模拟弹跳效果
 *   - OvershootInterpolator：超过目标值后回弹
 *   - AnticipateInterpolator：先向反方向蓄力再出发
 *
 * 补间动画和属性动画都可以设置插值器。
 */
class InterpolatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityInterpolatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val target = binding.targetView

        fun playWith(interpolator: TimeInterpolator) {
            target.translationX = 0f
            ObjectAnimator.ofFloat(target, "translationX", 0f, 600f).apply {
                duration = 1500
                this.interpolator = interpolator
                start()
            }
        }

        binding.btnLinear.setOnClickListener {
            playWith(LinearInterpolator())
        }

        binding.btnAccelerate.setOnClickListener {
            playWith(AccelerateInterpolator())
        }

        binding.btnDecelerate.setOnClickListener {
            playWith(DecelerateInterpolator())
        }

        binding.btnAccelerateDecelerate.setOnClickListener {
            playWith(AccelerateDecelerateInterpolator())
        }

        binding.btnOvershoot.setOnClickListener {
            // 超过目标后回弹——常用于弹出菜单、FAB 出现等
            playWith(OvershootInterpolator())
        }

        binding.btnAnticipate.setOnClickListener {
            // 先蓄力（反方向）再出发——像扔东西前先往后拉
            playWith(AnticipateInterpolator())
        }

        binding.btnBounce.setOnClickListener {
            // 弹跳效果——像球落地后弹跳
            playWith(BounceInterpolator())
        }
    }
}

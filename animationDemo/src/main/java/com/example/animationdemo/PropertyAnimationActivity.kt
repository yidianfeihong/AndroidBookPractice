package com.example.animationdemo

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.animationdemo.databinding.ActivityPropertyAnimationBinding

/**
 * 属性动画（Property Animation）—— Android 3.0（API 11）引入
 *
 * 【面试核心知识点】
 *
 * 1. 和补间动画的根本区别：
 *    属性动画真正改变了 View 的属性值（translationX、scaleX 等）
 *    平移后点击事件跟着走！
 *
 * 2. 三个核心类：
 *    - ValueAnimator：只负责产生值的变化（从 A 到 B），需要自己在回调里应用到 View
 *    - ObjectAnimator：继承 ValueAnimator，自动通过反射调用 setXxx() 方法设置属性
 *    - AnimatorSet：组合多个动画，支持 playTogether / playSequentially / 链式 before/after
 *
 * 3. ObjectAnimator 的原理（面试常问）：
 *    ObjectAnimator.ofFloat(view, "translationX", 0f, 300f)
 *    内部通过反射找到 view.setTranslationX() 方法来设值
 *    所以：目标对象必须有对应的 setter 方法！
 *
 * 4. TypeEvaluator（估值器）：
 *    决定如何从起始值计算到结束值
 *    - IntEvaluator / FloatEvaluator：线性计算
 *    - ArgbEvaluator：颜色渐变
 *    - 自定义 TypeEvaluator：实现任意复杂的值变化
 */
class PropertyAnimationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityPropertyAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val target = binding.targetView

        // 验证属性动画确实改变了属性——平移后点击事件跟着走
        target.setOnClickListener {
            Toast.makeText(this, "属性动画：点到我了！位置跟着变", Toast.LENGTH_SHORT).show()
        }

        // ========== ValueAnimator ==========
        // 只产生值，不直接操作 View，需要自己在监听器里应用
        binding.btnValueAnimator.setOnClickListener {
            val animator = ValueAnimator.ofFloat(0f, 300f)
            animator.interpolator = AnticipateInterpolator()
            animator.duration = 1000
            animator.addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Float
                binding.tvValue.text = "ValueAnimator 当前值：${value.toInt()}"
                // 手动设置 View 属性
                target.translationX = value
            }
            animator.start()
        }

        // ========== ObjectAnimator ==========
        // 自动通过反射调用 setTranslationX()，不需要手动设值
        binding.btnObjectAnimator.setOnClickListener {
            // "translationX" → 内部反射调用 view.setTranslationX(value)
            ObjectAnimator.ofFloat(target, View.TRANSLATION_X, 0f, 300f)
                .setDuration(1000)
                .start()
        }

        // ========== AnimatorSet ==========
        binding.btnAnimatorSet.setOnClickListener {
            val translateX = ObjectAnimator.ofFloat(target, "translationX", 0f, 200f)
            val scaleX = ObjectAnimator.ofFloat(target, "scaleX", 1f, 1.5f)
            val scaleY = ObjectAnimator.ofFloat(target, "scaleY", 1f, 1.5f)
            val rotate = ObjectAnimator.ofFloat(target, "rotation", 0f, 360f)

            AnimatorSet().apply {
                // playTogether：同时执行
                // playSequentially：顺序执行
                // play(a).with(b).before(c).after(d)：灵活组合
                play(translateX).with(scaleX).with(scaleY)
                play(rotate).after(translateX) // 旋转在平移之后
                duration = 1000
                start()
            }
        }

        // ========== 颜色渐变动画（ArgbEvaluator）==========
        binding.btnColorAnimator.setOnClickListener {
            val colorAnim = ValueAnimator.ofObject(
                ArgbEvaluator(),
                Color.parseColor("#2196F3"),  // 蓝
                Color.parseColor("#F44336")   // 红
            )
            colorAnim.duration = 2000
            colorAnim.addUpdateListener {
                target.setBackgroundColor(it.animatedValue as Int)
            }
            colorAnim.start()
        }

        // ========== 重置 ==========
        binding.btnReset.setOnClickListener {
            target.translationX = 0f
            target.translationY = 0f
            target.scaleX = 1f
            target.scaleY = 1f
            target.rotation = 0f
            target.alpha = 1f
            target.setBackgroundResource(R.drawable.shape_target_box)
            binding.tvValue.text = "ValueAnimator 当前值：0"
        }
    }
}

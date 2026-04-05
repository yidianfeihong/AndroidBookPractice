package com.example.animationdemo

import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.animationdemo.databinding.ActivityTweenAnimationBinding

/**
 * 补间动画（Tween Animation / View Animation）
 *
 * 【面试核心知识点】
 *
 * 1. 四种基本类型：
 *    - AlphaAnimation：透明度
 *    - ScaleAnimation：缩放
 *    - RotateAnimation：旋转
 *    - TranslateAnimation：平移
 *    - AnimationSet：组合以上多种
 *
 * 2. 两种创建方式：
 *    - XML 定义在 res/anim/ 目录下，用 AnimationUtils.loadAnimation() 加载
 *    - 纯代码 new AlphaAnimation() 等
 *
 * 3. ⚠️ 最大缺陷（面试必问）：
 *    补间动画只改变 View 的【绘制位置】，不改变【实际属性】！
 *    例如：View 平移到右边后，点击事件仍然在原来的位置。
 *    这就是后来引入属性动画（Property Animation）的根本原因。
 *
 * 4. fillAfter = true：动画结束后保持最终状态（否则回到起始位置）
 *    fillBefore = true：动画开始前应用起始状态（默认 true）
 */
class TweenAnimationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityTweenAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val target = binding.targetView

        // 给目标 View 加点击事件，验证"补间动画不改变实际位置"
        target.setOnClickListener {
            Toast.makeText(this, "点到我了！", Toast.LENGTH_SHORT).show()
        }

        // ========== XML 方式加载动画 ==========

        binding.btnAlpha.setOnClickListener {
            target.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_alpha))
        }

        binding.btnScale.setOnClickListener {
            target.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_scale))
        }

        binding.btnRotate.setOnClickListener {
            target.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_rotate))
        }

        binding.btnTranslate.setOnClickListener {
            // 平移后试试点击方块——点击事件还在原位！这就是补间动画的缺陷
            target.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_translate))
        }

        binding.btnSet.setOnClickListener {
            target.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_set))
        }

        // ========== 纯代码方式 ==========

        binding.btnCodeAnim.setOnClickListener {
            // 代码创建和 XML 效果完全一样，面试时两种都要会
            val anim = AnimationSet(true).apply {
                addAnimation(AlphaAnimation(0f, 1f))
                addAnimation(ScaleAnimation(
                    0f, 1f, 0f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f,  // pivotX = 50%
                    Animation.RELATIVE_TO_SELF, 0.5f   // pivotY = 50%
                ))
                duration = 1000
                fillAfter = true
                // 可以设置插值器
                interpolator = OvershootInterpolator()
            }
            target.startAnimation(anim)
        }

        // ========== 重置 ==========

        binding.btnReset.setOnClickListener {
            target.clearAnimation()
        }
    }
}

package com.example.animationdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.animationdemo.databinding.ActivityViewPropertyAnimatorBinding

/**
 * ViewPropertyAnimator —— view.animate() 链式调用
 *
 * 【面试核心知识点】
 *
 * 1. 本质：view.animate() 返回 ViewPropertyAnimator 对象
 *    内部封装了 ObjectAnimator，但更简洁
 *
 * 2. 和 ObjectAnimator 的区别：
 *    - ObjectAnimator：可以操作任意属性（通过反射），功能更强
 *    - ViewPropertyAnimator：只能操作 View 的固定属性（translationX/Y、alpha、scaleX/Y 等）
 *      但写法简洁，性能更好（内部做了优化，多个属性合并在一帧里更新）
 *
 * 3. 两类方法：
 *    - translationX(200f)：设置到绝对值 200
 *    - translationXBy(200f)：在当前值基础上加 200（相对值）
 *
 * 4. 不需要手动调 start()！设置完属性后自动在下一帧开始。
 */
class ViewPropertyAnimatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityViewPropertyAnimatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val target = binding.targetView

        // ========== 简单平移 ==========
        binding.btnSimple.setOnClickListener {
            target.animate()
                .translationX(300f)     // 平移到 x=300 的位置
                .setDuration(1000)
            // 不需要 start()！
        }

        // ========== 链式组合 ==========
        binding.btnChain.setOnClickListener {
            target.animate()
                .translationX(200f)     // 平移
                .rotation(360f)         // 旋转
                .alpha(0.5f)            // 半透明
                .scaleX(1.5f)           // 放大
                .scaleY(1.5f)
                .setDuration(1000)
        }

        // ========== 带监听器 ==========
        binding.btnWithListener.setOnClickListener {
            target.animate()
                .translationY(200f)
                .setDuration(1000)
                .withStartAction {
                    // 动画开始时执行（主线程）
                    Log.d("AnimDemo", "动画开始")
                }
                .withEndAction {
                    // 动画结束时执行（主线程）
                    Toast.makeText(this, "动画结束！", Toast.LENGTH_SHORT).show()
                }
        }

        // ========== By 方法（相对值）==========
        // 每次点击都在当前位置基础上再移动 100
        binding.btnBy.setOnClickListener {
            target.animate()
                .translationXBy(100f)   // 相对当前位置再右移 100
                .rotationBy(90f)        // 相对当前角度再转 90 度
                .setDuration(500)
        }

        // ========== 重置 ==========
        binding.btnReset.setOnClickListener {
            target.animate()
                .translationX(0f)
                .translationY(0f)
                .rotation(0f)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
        }
    }
}

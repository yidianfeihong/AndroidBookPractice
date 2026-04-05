package com.example.animationdemo

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.animationdemo.databinding.ActivityLayoutTransitionBinding

/**
 * 布局动画（LayoutTransition）
 *
 * 【面试核心知识点】
 *
 * 1. 最简单的方式：在 XML 中给容器加 android:animateLayoutChanges="true"
 *    这样容器内子 View 的添加/移除自动带动画
 *
 * 2. LayoutTransition 有四种动画类型：
 *    - APPEARING：新 View 自身出现的动画
 *    - DISAPPEARING：被移除的 View 自身消失的动画
 *    - CHANGE_APPEARING：因为新 View 出现，其他 View 需要腾位置的动画
 *    - CHANGE_DISAPPEARING：因为 View 消失，其他 View 需要补位的动画
 *
 * 3. 可以通过代码自定义每种类型的动画效果
 *
 * 4. 和 RecyclerView.ItemAnimator 的区别：
 *    - LayoutTransition 适用于普通 ViewGroup（LinearLayout 等）
 *    - RecyclerView 有自己的 ItemAnimator 体系
 */
class LayoutTransitionActivity : AppCompatActivity() {

    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLayoutTransitionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val container = binding.container

        // 布局已在 XML 中设置 animateLayoutChanges="true"
        // 也可以代码设置更细致的 LayoutTransition：
        val transition = LayoutTransition()
        transition.setDuration(300)
        // 可以自定义每种动画：
        // transition.setAnimator(LayoutTransition.APPEARING, customAnimator)
        container.layoutTransition = transition

        // 添加 View
        binding.btnAdd.setOnClickListener {
            count++
            val textView = TextView(this).apply {
                text = "Item $count（点击删除）"
                textSize = 16f
                gravity = Gravity.CENTER
                setPadding(16, 24, 16, 24)
                setBackgroundColor(
                    arrayOf(0xFF2196F3, 0xFF4CAF50, 0xFFF44336, 0xFFFF9800, 0xFF9C27B0)
                        .let { it[count % it.size].toInt() }
                )
                setTextColor(0xFFFFFFFF.toInt())
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 8 }

                // 点击自身删除
                setOnClickListener { container.removeView(this) }
            }
            container.addView(textView, 0) // 插到最前面，效果更明显
        }

        // 移除最后一个
        binding.btnRemove.setOnClickListener {
            if (container.childCount > 0) {
                container.removeViewAt(container.childCount - 1)
            }
        }
    }
}

package com.example.scrollconflict.scenes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scrollconflict.PageAdapter
import com.example.scrollconflict.R
import com.example.scrollconflict.databinding.ActivityOppositeDirectionBinding

/**
 * 反向滑动冲突演示场景
 *
 * 演示垂直 ScrollView 嵌套水平 ViewPager2 的滑动冲突及解决方案。
 *
 * 【场景结构】
 * - 外层：垂直滑动的 ScrollView（[OppositeDirectionScrollView]）
 * - 内层：水平滑动的 ViewPager2
 *
 * 【解决方案】
 * 在父容器的 onInterceptTouchEvent 中比较 |dx| 和 |dy|：
 * - |dx| > |dy| → 水平滑动 → 不拦截 → ViewPager2 处理
 * - |dy| > |dx| → 垂直滑动 → 拦截 → ScrollView 处理
 */
class OppositeDirectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOppositeDirectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOppositeDirectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeViewPager()
        setupSolutionToggle()
    }

    private fun initializeViewPager() {
        binding.viewPager.adapter = PageAdapter(
            fragmentActivity = this,
            pageCount = 5
        )
    }

    private fun setupSolutionToggle() {
        binding.switchSolution.setOnCheckedChangeListener { _, isChecked ->
            binding.scrollView.isConflictResolutionEnabled = isChecked

            if (isChecked) {
                binding.textSolutionStatus.text = getString(R.string.toggle_solution_enabled)
                binding.textSceneDescription.text =
                    getString(R.string.instruction_opposite_direction_resolved)
            } else {
                binding.textSolutionStatus.text = getString(R.string.toggle_solution_disabled)
                binding.textSceneDescription.text =
                    getString(R.string.instruction_opposite_direction_conflict)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

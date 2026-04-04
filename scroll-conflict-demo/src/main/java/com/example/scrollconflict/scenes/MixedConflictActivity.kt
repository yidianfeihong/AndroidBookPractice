package com.example.scrollconflict.scenes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scrollconflict.ItemAdapter
import com.example.scrollconflict.R
import com.example.scrollconflict.databinding.ActivityMixedConflictBinding

/**
 * 混杂滑动冲突演示场景
 *
 * 演示三层嵌套的复杂滑动冲突及解决方案。
 *
 * 【场景结构（三层嵌套）】
 * - 第一层：垂直 ScrollView（[MixedOuterScrollView]）
 * - 第二层：水平 HorizontalScrollView（[MixedHorizontalScrollView]）
 * - 第三层：垂直 RecyclerView × 3
 *
 * 【冲突类型】
 * 1. 第一层（垂直）↔ 第二层（水平）：反向冲突
 * 2. 第一层（垂直）↔ 第三层（垂直）：同向冲突（跨层）
 * 3. 第二层（水平）↔ 第三层（垂直）：反向冲突
 *
 * 【解决方案】
 * 每层只处理自己方向的滑动，通过方向判断 + 子视图状态检查实现多层协调。
 */
class MixedConflictActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMixedConflictBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMixedConflictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeRecyclerViews()
        setupSolutionToggle()
    }

    private fun initializeRecyclerViews() {
        val recyclerViews = listOf(
            binding.recyclerView1,
            binding.recyclerView2,
            binding.recyclerView3
        )

        recyclerViews.forEach { rv ->
            rv.apply {
                layoutManager = LinearLayoutManager(this@MixedConflictActivity)
                adapter = ItemAdapter(itemCount = 20)
                isNestedScrollingEnabled = false
            }
        }
    }

    /**
     * 设置解决方案开关
     *
     * 同时控制外层 ScrollView 和中间层 HorizontalScrollView 的冲突解决状态，
     * 确保多层级的协调逻辑同步开启或关闭。
     */
    private fun setupSolutionToggle() {
        binding.switchSolution.setOnCheckedChangeListener { _, isChecked ->
            binding.outerScrollView.isConflictResolutionEnabled = isChecked
            binding.horizontalScrollView.isConflictResolutionEnabled = isChecked

            if (isChecked) {
                binding.textSolutionStatus.text = getString(R.string.toggle_solution_enabled)
                binding.textSceneDescription.text =
                    getString(R.string.instruction_mixed_conflict_resolved)
            } else {
                binding.textSolutionStatus.text = getString(R.string.toggle_solution_disabled)
                binding.textSceneDescription.text =
                    getString(R.string.instruction_mixed_conflict_conflict)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

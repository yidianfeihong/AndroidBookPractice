package com.example.scrollconflict.scenes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scrollconflict.ItemAdapter
import com.example.scrollconflict.R
import com.example.scrollconflict.databinding.ActivitySameDirectionBinding

/**
 * 同向滑动冲突演示场景
 *
 * 演示垂直 ScrollView 嵌套垂直 RecyclerView 的滑动冲突及两种解决方案。
 *
 * 【场景结构】
 * - 外层：垂直滑动的 ScrollView（[SameDirectionScrollView]）
 * - 内层：垂直滑动的 RecyclerView（[SmartRecyclerView]）
 *
 * 【三种模式】
 * 1. 冲突状态：ScrollView 默认拦截所有垂直事件 → RecyclerView 无法滑动
 * 2. 外部拦截法：父容器在 onInterceptTouchEvent 中智能判断 → 根据子视图状态决定拦截
 * 3. 内部拦截法：子视图通过 requestDisallowInterceptTouchEvent 控制父容器 → 子视图主导
 */
class SameDirectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySameDirectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySameDirectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeRecyclerView()
        setupModeSelector()
    }

    private fun initializeRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SameDirectionActivity)
            adapter = ItemAdapter(itemCount = 30)
            // 禁用 RecyclerView 的默认嵌套滑动行为
            // 这样触摸事件完全由我们的自定义拦截逻辑控制，便于教学演示
            isNestedScrollingEnabled = false
        }
    }

    /**
     * 设置三种模式的选择逻辑
     *
     * 根据用户选择的 Chip，配置不同的冲突解决方式：
     * - 冲突状态：两个自定义视图的解决方案都关闭
     * - 外部拦截法：SameDirectionScrollView 启用拦截逻辑
     * - 内部拦截法：SmartRecyclerView 启用内部拦截逻辑
     */
    private fun setupModeSelector() {
        binding.chipGroupMode.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull()

            // 先重置所有状态
            binding.scrollView.isConflictResolutionEnabled = false
            binding.recyclerView.isInternalInterceptEnabled = false

            when (checkedId) {
                R.id.chipConflict -> {
                    // 冲突状态：都不启用 → ScrollView 默认拦截 → RecyclerView 无法滑动
                    binding.textSceneDescription.text =
                        getString(R.string.instruction_same_direction_conflict)
                }

                R.id.chipExternal -> {
                    // 外部拦截法：父容器（ScrollView）决定是否拦截
                    binding.scrollView.isConflictResolutionEnabled = true
                    binding.textSceneDescription.text =
                        getString(R.string.instruction_same_direction_external)
                }

                R.id.chipInternal -> {
                    // 内部拦截法：子视图（RecyclerView）控制父容器拦截
                    binding.recyclerView.isInternalInterceptEnabled = true
                    binding.textSceneDescription.text =
                        getString(R.string.instruction_same_direction_internal)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

package com.example.scrollconflict.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * 内部拦截法解决同向滑动冲突的自定义 RecyclerView
 *
 * 【内部拦截法原理】
 * 与外部拦截法（由父容器决定是否拦截）不同，内部拦截法的事件分发决策权在子视图手中。
 * 子视图通过调用 parent.requestDisallowInterceptTouchEvent() 来控制父容器能否拦截事件。
 *
 * 【核心流程】
 * 1. ACTION_DOWN 时：告诉父容器「不要拦截」→ 保证后续事件能到达子视图
 * 2. ACTION_MOVE 时：判断自己是否还能滑动
 *    - 可以继续滑动 → 保持「不要拦截」→ 自己处理滑动
 *    - 到达边界 → 告诉父容器「可以拦截了」→ 父容器接管滑动
 * 3. ACTION_UP/CANCEL 时：恢复父容器的拦截能力
 *
 * 【与外部拦截法的对比】
 * ┌─────────────┬──────────────────────────────────┬──────────────────────────────────┐
 * │             │ 外部拦截法                        │ 内部拦截法                        │
 * ├─────────────┼──────────────────────────────────┼──────────────────────────────────┤
 * │ 决策者       │ 父容器 (onInterceptTouchEvent)    │ 子视图 (dispatchTouchEvent)       │
 * │ 关键 API     │ onInterceptTouchEvent 返回值      │ requestDisallowInterceptTouchEvent│
 * │ 适用场景     │ 父容器了解子视图的滑动状态          │ 子视图自己最清楚能否继续滑动        │
 * │ 代码位置     │ 自定义父容器类                     │ 自定义子视图类                     │
 * └─────────────┴──────────────────────────────────┴──────────────────────────────────┘
 *
 * 【使用条件】
 * 父容器（ScrollView）的 onInterceptTouchEvent 在 ACTION_DOWN 时必须返回 false，
 * 否则子视图收不到任何事件。标准 ScrollView 默认在 ACTION_DOWN 时返回 false。
 */
class SmartRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    /** 是否启用内部拦截法 */
    var isInternalInterceptEnabled: Boolean = false

    /** 上一次 dispatchTouchEvent 中记录的 Y 坐标 */
    private var lastDispatchY = 0f

    private val scaledTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    /**
     * 事件分发的核心方法 —— 内部拦截法的关键实现
     *
     * dispatchTouchEvent 是事件到达 View 后最先被调用的方法，在 onInterceptTouchEvent
     * 和 onTouchEvent 之前。在此方法中调用 requestDisallowInterceptTouchEvent
     * 可以在父容器决策之前就设置好拦截控制标志。
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!isInternalInterceptEnabled) {
            return super.dispatchTouchEvent(ev)
        }

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastDispatchY = ev.y
                // 【关键】告诉父容器：不要拦截我的事件！
                // 设置此标志后，父容器的 ViewGroup.dispatchTouchEvent() 在后续事件中
                // 会跳过 onInterceptTouchEvent() 的调用，确保事件能到达子视图
                parent?.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.y - lastDispatchY
                lastDispatchY = ev.y

                if (abs(deltaY) > scaledTouchSlop) {
                    val scrollDirection = if (deltaY > 0) -1 else 1

                    if (!canScrollVertically(scrollDirection)) {
                        // 已到达滑动边界，无法继续滑动
                        // 【关键】告诉父容器：你可以拦截了！
                        // 清除此标志后，父容器的下一个 MOVE 事件会调用 onInterceptTouchEvent()，
                        // 父容器（ScrollView）检测到垂直滑动 > touchSlop → 拦截 → 接管滑动
                        parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}

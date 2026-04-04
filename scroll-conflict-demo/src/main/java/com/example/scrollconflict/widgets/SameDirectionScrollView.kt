package com.example.scrollconflict.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.scrollconflict.ConflictResolvableLayout
import kotlin.math.abs

/**
 * 外部拦截法解决同向滑动冲突的自定义 ScrollView
 *
 * 【同向滑动冲突】是指父子视图的滑动方向相同（都是垂直滑动），
 * 典型场景：垂直 ScrollView 内嵌套垂直 RecyclerView。
 *
 * 【冲突原因】
 * ScrollView 的默认 onInterceptTouchEvent() 在检测到垂直滑动时会拦截事件，
 * 导致内层 RecyclerView 无法接收触摸事件，从而无法正常滑动。
 *
 * 【外部拦截法原理】
 * 在父容器（本类）的 onInterceptTouchEvent() 中，根据子视图的滑动状态决定是否拦截：
 * - 如果子视图（RecyclerView）在当前滑动方向上还可以继续滑动 → 不拦截，让子视图处理
 * - 如果子视图已经滑到边界，无法继续滑动 → 拦截，由父容器（ScrollView）处理
 *
 * 【关键 API】
 * - [onInterceptTouchEvent]: 事件拦截的核心决策点
 * - [View.canScrollVertically]: 判断子视图是否可以在指定方向上继续滑动
 * - [requestDisallowInterceptTouchEvent]: 需要重写以忽略子视图的请求，保证拦截逻辑可控
 */
class SameDirectionScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr), ConflictResolvableLayout {

    override var isConflictResolutionEnabled: Boolean = false

    /** ACTION_DOWN 时记录的初始 Y 坐标，用于后续计算滑动方向 */
    private var interceptStartY = 0f

    /** 系统定义的最小滑动距离阈值，小于此值的移动不被认为是滑动 */
    private val scaledTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    override fun enableConflictResolution() {
        isConflictResolutionEnabled = true
    }

    override fun disableConflictResolution() {
        isConflictResolutionEnabled = false
    }

    /**
     * 触摸事件拦截的核心方法 —— 外部拦截法的关键实现
     *
     * Android 触摸事件分发链：
     * Activity.dispatchTouchEvent → ViewGroup.dispatchTouchEvent → ViewGroup.onInterceptTouchEvent
     *   ↓ (若不拦截)
     * 子 View.dispatchTouchEvent → 子 View.onTouchEvent
     *
     * 本方法在事件传递给子视图之前被调用，返回 true 表示拦截（事件转到本视图的 onTouchEvent），
     * 返回 false 表示不拦截（事件继续传递给子视图）。
     *
     * 【外部拦截法流程】
     * 1. ACTION_DOWN: 记录初始触摸位置，不拦截（子视图需要接收 DOWN 才能响应后续事件）
     * 2. ACTION_MOVE: 判断滑动方向和距离
     *    - 子视图可以继续滑动 → 不拦截
     *    - 子视图到达边界 → 拦截，父容器接管
     * 3. ACTION_UP/CANCEL: 交给默认处理
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isConflictResolutionEnabled) {
            // 【冲突状态】使用 ScrollView 默认行为：拦截所有垂直滑动 → RecyclerView 无法滑动
            return super.onInterceptTouchEvent(ev)
        }

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                interceptStartY = ev.y
                // 调用 super 让 ScrollView 初始化内部状态（速度跟踪器、mLastMotionY 等）
                // ScrollView 在 ACTION_DOWN 时默认返回 false（除非正在 fling）
                return super.onInterceptTouchEvent(ev)
            }

            MotionEvent.ACTION_MOVE -> {
                val deltaY = ev.y - interceptStartY
                if (abs(deltaY) <= scaledTouchSlop) {
                    return false
                }

                // 判断用户的滑动方向：
                // deltaY > 0 → 手指向下滑 → 内容向上滚 → canScrollVertically(-1)
                // deltaY < 0 → 手指向上滑 → 内容向下滚 → canScrollVertically(1)
                val scrollDirection = if (deltaY > 0) -1 else 1
                val recyclerView = findRecyclerViewInHierarchy(this)

                if (recyclerView != null && recyclerView.canScrollVertically(scrollDirection)) {
                    // 子视图还可以继续滑动 → 不拦截，让 RecyclerView 处理
                    return false
                }

                // 子视图已到达边界（或不存在可滑动子视图）→ 拦截，ScrollView 接管
                // 注意：ScrollView 内部在此时会更新 mLastMotionY 到当前位置，避免滚动跳跃
                return super.onInterceptTouchEvent(ev)
            }

            else -> return super.onInterceptTouchEvent(ev)
        }
    }

    /**
     * 重写以在冲突解决启用时忽略子视图的拦截禁止请求
     *
     * 【为什么需要重写这个方法？】
     * RecyclerView 在检测到滑动时，会自动调用 parent.requestDisallowInterceptTouchEvent(true)
     * 来阻止父容器拦截。如果不重写，一旦 RecyclerView 设置了此标志，
     * ViewGroup.dispatchTouchEvent() 就不会再调用我们的 onInterceptTouchEvent()，
     * 导致我们无法在 RecyclerView 到达边界时接管事件。
     */
    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (isConflictResolutionEnabled) {
            return
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    /** 在视图层级中递归查找第一个 RecyclerView 子视图 */
    private fun findRecyclerViewInHierarchy(viewGroup: ViewGroup): RecyclerView? {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is RecyclerView) return child
            if (child is ViewGroup) {
                val result = findRecyclerViewInHierarchy(child)
                if (result != null) return result
            }
        }
        return null
    }
}

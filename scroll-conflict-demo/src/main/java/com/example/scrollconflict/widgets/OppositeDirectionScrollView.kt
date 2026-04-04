package com.example.scrollconflict.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.ScrollView
import com.example.scrollconflict.ConflictResolvableLayout
import kotlin.math.abs

/**
 * 方向感知的自定义 ScrollView，用于解决反向滑动冲突
 *
 * 【反向滑动冲突】是指父子视图的滑动方向垂直（一个垂直，一个水平），
 * 典型场景：垂直 ScrollView 内嵌套水平 ViewPager2。
 *
 * 【冲突原因】
 * 当用户在 ViewPager2 区域做斜向滑动时，ScrollView 可能会在垂直方向上的微小偏移
 * 就触发拦截，导致 ViewPager2 无法正常接收水平滑动事件。
 *
 * 【解决策略（外部拦截法 + 方向判断）】
 * 通过比较水平和垂直滑动距离来判断用户的主要滑动意图：
 * - |dy| > |dx| → 用户主要是上下滑动 → 拦截事件（ScrollView 处理垂直滚动）
 * - |dx| > |dy| → 用户主要是左右滑动 → 不拦截（ViewPager2 处理页面切换）
 *
 * 这种策略确保了滑动方向的正确路由，用户体验流畅。
 */
class OppositeDirectionScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr), ConflictResolvableLayout {

    override var isConflictResolutionEnabled: Boolean = false

    private var interceptStartX = 0f
    private var interceptStartY = 0f
    private val scaledTouchSlop: Int = ViewConfiguration.get(context).scaledTouchSlop

    override fun enableConflictResolution() {
        isConflictResolutionEnabled = true
    }

    override fun disableConflictResolution() {
        isConflictResolutionEnabled = false
    }

    /**
     * 触摸事件拦截 —— 方向判断的核心实现
     *
     * 【方向判断逻辑】
     * 1. 在 ACTION_DOWN 时记录初始触摸的 (x, y) 坐标
     * 2. 在 ACTION_MOVE 时计算水平偏移 dx 和垂直偏移 dy
     * 3. 比较 |dx| 和 |dy| 的大小：
     *    - |dx| > |dy|: 水平滑动为主 → return false（不拦截）→ ViewPager2 处理
     *    - |dy| > |dx|: 垂直滑动为主 → return super（让 ScrollView 决定）→ ScrollView 处理
     *
     * 这是最经典的反向滑动冲突解决方案，被广泛应用于各种 App 中。
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isConflictResolutionEnabled) {
            return super.onInterceptTouchEvent(ev)
        }

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                interceptStartX = ev.x
                interceptStartY = ev.y
                return super.onInterceptTouchEvent(ev)
            }

            MotionEvent.ACTION_MOVE -> {
                val absDx = abs(ev.x - interceptStartX)
                val absDy = abs(ev.y - interceptStartY)

                if (absDx <= scaledTouchSlop && absDy <= scaledTouchSlop) {
                    return false
                }

                if (absDx > absDy) {
                    // 水平滑动为主 → 不拦截，让 ViewPager2 处理页面切换
                    return false
                }

                // 垂直滑动为主 → 让 ScrollView 的默认逻辑处理垂直滚动
                return super.onInterceptTouchEvent(ev)
            }

            else -> return super.onInterceptTouchEvent(ev)
        }
    }

    /**
     * 忽略子视图的拦截禁止请求（原因同 SameDirectionScrollView）
     *
     * ViewPager2 内部使用 RecyclerView 实现，它在滑动时也会调用
     * requestDisallowInterceptTouchEvent(true)。需要忽略此请求，
     * 保证我们的方向判断逻辑始终有效。
     */
    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (isConflictResolutionEnabled) {
            return
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }
}

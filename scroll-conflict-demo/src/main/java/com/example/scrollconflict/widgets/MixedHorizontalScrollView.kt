package com.example.scrollconflict.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.HorizontalScrollView
import com.example.scrollconflict.ConflictResolvableLayout
import kotlin.math.abs

/**
 * 混杂冲突场景的中间层自定义 HorizontalScrollView（水平滑动）
 *
 * 在三层嵌套结构中，此视图位于中间层，负责水平滚动以切换不同的 RecyclerView。
 *
 * 【本层的解决策略】
 * 纯粹的方向判断：
 * 1. 用户垂直滑动 → 不拦截（可能由外层 ScrollView 或内层 RecyclerView 处理）
 * 2. 用户水平滑动 → 拦截，自己处理水平滚动
 *
 * 【协调机制】
 * 三层视图各自只处理自己方向的滑动，形成清晰的职责分工：
 * ┌────────────────────────────────────────────────┐
 * │ 第一层 MixedOuterScrollView   → 处理垂直滑动    │
 * │   ┌────────────────────────────────────────────┤
 * │   │ 第二层 MixedHorizontalScrollView → 处理水平 │
 * │   │   ┌────────────────────────────────────────┤
 * │   │   │ 第三层 RecyclerView    → 处理垂直滑动   │
 * │   │   └────────────────────────────────────────┤
 * │   └────────────────────────────────────────────┤
 * └────────────────────────────────────────────────┘
 */
class MixedHorizontalScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr), ConflictResolvableLayout {

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

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isConflictResolutionEnabled) {
            return super.onInterceptTouchEvent(ev)
        }

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                interceptStartX = ev.x
                interceptStartY = ev.y
                super.onInterceptTouchEvent(ev)
                return false
            }

            MotionEvent.ACTION_MOVE -> {
                val absDx = abs(ev.x - interceptStartX)
                val absDy = abs(ev.y - interceptStartY)

                if (absDx <= scaledTouchSlop && absDy <= scaledTouchSlop) {
                    return false
                }

                if (absDy > absDx) {
                    // 垂直滑动为主 → 不拦截，交给外层 ScrollView 或内层 RecyclerView
                    return false
                }

                // 水平滑动为主 → 拦截，处理水平滚动
                return super.onInterceptTouchEvent(ev)
            }

            else -> return super.onInterceptTouchEvent(ev)
        }
    }

    /**
     * 忽略子视图的拦截禁止请求
     *
     * 内层 RecyclerView 滑动时会请求禁止拦截。如果不忽略，
     * 用户从垂直滑动切换到水平滑动时，本层无法及时拦截。
     */
    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (isConflictResolutionEnabled) return
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }
}

package com.example.scrollconflict.widgets

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.scrollconflict.ConflictResolvableLayout
import kotlin.math.abs

/**
 * 混杂冲突场景的外层自定义 ScrollView（垂直滑动）
 *
 * 【混杂滑动冲突】是最复杂的冲突场景，包含三层嵌套：
 * - 第一层（本类）：垂直 ScrollView
 * - 第二层：水平 HorizontalScrollView（[MixedHorizontalScrollView]）
 * - 第三层：垂直 RecyclerView（多个）
 *
 * 【冲突分析】
 * 三层嵌套中同时存在同向冲突和反向冲突：
 * 1. 第一层（垂直）↔ 第二层（水平）：反向冲突
 * 2. 第一层（垂直）↔ 第三层（垂直）：同向冲突（跨层）
 * 3. 第二层（水平）↔ 第三层（垂直）：反向冲突
 *
 * 【本层（最外层）的解决策略】
 * 综合运用方向判断和子视图滑动状态检查：
 * 1. 用户水平滑动 → 不拦截，交给第二层 HorizontalScrollView
 * 2. 用户垂直滑动 → 检查第三层 RecyclerView 是否可以滑动
 *    - 内层可以滑动 → 不拦截，让事件穿透到内层
 *    - 内层不能滑动 → 拦截，自己处理垂直滚动
 *
 * 这种策略结合了反向冲突的方向判断和同向冲突的边界检测，是两种基础方案的综合应用。
 */
class MixedOuterScrollView @JvmOverloads constructor(
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

                if (absDx > absDy) {
                    // 水平滑动为主 → 交给中间层 HorizontalScrollView 处理
                    return false
                }

                // 垂直滑动为主 → 检查触摸点所在的 RecyclerView 是否可以继续滑动
                val direction = if (ev.y - interceptStartY > 0) -1 else 1
                val targetRv = findRecyclerViewAtTouchPoint(ev)

                if (targetRv != null && targetRv.canScrollVertically(direction)) {
                    return false
                }

                return super.onInterceptTouchEvent(ev)
            }

            else -> return super.onInterceptTouchEvent(ev)
        }
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (isConflictResolutionEnabled) return
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    /**
     * 根据触摸事件的屏幕坐标，精确找到用户正在操作的 RecyclerView。
     *
     * 混杂冲突场景有多个 RecyclerView 水平排列在 HorizontalScrollView 内，
     * 必须定位到用户实际触摸的那一个，才能正确判断其滑动状态。
     * 使用 rawX/rawY（屏幕绝对坐标）和 getGlobalVisibleRect() 进行匹配。
     */
    private fun findRecyclerViewAtTouchPoint(ev: MotionEvent): RecyclerView? {
        val screenX = ev.rawX.toInt()
        val screenY = ev.rawY.toInt()
        return findRecyclerViewAtScreenPoint(this, screenX, screenY)
    }

    private fun findRecyclerViewAtScreenPoint(
        viewGroup: ViewGroup,
        screenX: Int,
        screenY: Int
    ): RecyclerView? {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is RecyclerView) {
                val rect = Rect()
                if (child.getGlobalVisibleRect(rect) && rect.contains(screenX, screenY)) {
                    return child
                }
            }
            if (child is ViewGroup) {
                val result = findRecyclerViewAtScreenPoint(child, screenX, screenY)
                if (result != null) return result
            }
        }
        return null
    }
}

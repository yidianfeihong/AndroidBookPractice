package com.example.draganddraw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

private const val TAG = "BoxDrawingView"

class BoxDrawingView(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private var currentBox: Box? = null
    private var boxen = mutableListOf<Box>()
    private val boxPaint = Paint().apply {
        color = 0x22ff0000.toInt()
    }
    private val backgroundPaint = Paint().apply {
        color = 0xfff8efe0.toInt()
    }

    class SavedState(
        superState: Parcelable?,
        val boxen: MutableList<Box>
    ) : BaseSavedState(superState)

    val gestureDetector = object : GestureDetector(context, object : OnGestureListener {
        override fun onDown(event: MotionEvent): Boolean {
            return false
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent) {

        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent) {

        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return false
        }
    }) {

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val current = PointF(event.x, event.y)
        var action = ""
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                action = "ACTION_DOWN"
                // Reset drawing state
                currentBox = Box(current).also {
                    boxen.add(it)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                action = "ACTION_MOVE"
                updateCurrentBox(current)
            }

            MotionEvent.ACTION_UP -> {
                action = "ACTION_UP"
                updateCurrentBox(current)
                currentBox = null
            }

            MotionEvent.ACTION_CANCEL -> {
                action = "ACTION_CANCEL"
                currentBox = null
            }
        }

        Log.i(TAG, "$action at x=${current.x}, y=${current.y}")

        return true
    }

    override fun onDraw(canvas: Canvas) {
        // Fill the background
        canvas.drawPaint(backgroundPaint)

        boxen.forEach { box ->
            canvas.drawRect(box.left, box.top, box.right, box.bottom, boxPaint)
        }
    }

    private fun updateCurrentBox(current: PointF) {
        currentBox?.let {
            it.end = current
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        // 1. 保存父类可能已经存在的状态
        val superState = super.onSaveInstanceState()
        return SavedState(superState, boxen)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SavedState -> {
                // 先恢复父类状态
                super.onRestoreInstanceState(state.superState)
                // 再恢复自定义状态
                this.boxen = state.boxen
                // 状态恢复后，可能需要更新UI
                invalidate()
            }

            else -> {
                // 如果不是我们的SavedState类型，交给父类处理
                super.onRestoreInstanceState(state)
            }
        }
    }
}
package com.example.scrollconflict.models

/**
 * 滑动方向枚举
 * 用于标识触摸事件的主要滑动方向
 */
enum class ScrollDirection {
    /**
     * 无滑动或滑动距离未超过阈值
     */
    NONE,

    /**
     * 水平滑动
     */
    HORIZONTAL,

    /**
     * 垂直滑动
     */
    VERTICAL
}

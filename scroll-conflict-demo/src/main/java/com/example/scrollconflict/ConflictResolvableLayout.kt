package com.example.scrollconflict

/**
 * 可解决冲突的布局接口
 * 定义了支持滑动冲突解决功能的布局容器的通用接口
 *
 * 实现此接口的自定义 ViewGroup 可以通过开关控制是否启用滑动冲突解决逻辑，
 * 用于演示和对比冲突状态与解决状态的行为差异。
 */
interface ConflictResolvableLayout {
    /**
     * 冲突解决功能是否启用
     * true 表示启用冲突解决逻辑，false 表示禁用（展示冲突状态）
     */
    var isConflictResolutionEnabled: Boolean

    /**
     * 启用冲突解决功能
     * 调用此方法后，布局将使用触摸事件拦截逻辑来解决滑动冲突
     */
    fun enableConflictResolution()

    /**
     * 禁用冲突解决功能
     * 调用此方法后，布局将表现出滑动冲突的问题行为
     */
    fun disableConflictResolution()
}

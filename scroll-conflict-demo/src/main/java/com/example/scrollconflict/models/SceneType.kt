package com.example.scrollconflict.models

import android.app.Activity
import com.example.scrollconflict.scenes.SameDirectionActivity
import com.example.scrollconflict.scenes.OppositeDirectionActivity
import com.example.scrollconflict.scenes.MixedConflictActivity

/**
 * 场景类型枚举，定义三种滑动冲突演示场景
 *
 * @property title 场景标题
 * @property description 场景描述
 * @property activityClass 对应的 Activity 类
 */
enum class SceneType(
    val title: String,
    val description: String,
    val activityClass: Class<out Activity>
) {
    /**
     * 同向滑动冲突场景
     * 垂直 ScrollView 嵌套垂直 RecyclerView
     */
    SAME_DIRECTION(
        title = "同向滑动冲突",
        description = "垂直 ScrollView 嵌套垂直 RecyclerView",
        activityClass = SameDirectionActivity::class.java
    ),

    /**
     * 反向滑动冲突场景
     * 垂直 ScrollView 嵌套水平 ViewPager2
     */
    OPPOSITE_DIRECTION(
        title = "反向滑动冲突",
        description = "垂直 ScrollView 嵌套水平 ViewPager2",
        activityClass = OppositeDirectionActivity::class.java
    ),

    /**
     * 混杂滑动冲突场景
     * 多层嵌套的复杂场景
     */
    MIXED_CONFLICT(
        title = "混杂滑动冲突",
        description = "多层嵌套的复杂场景",
        activityClass = MixedConflictActivity::class.java
    )
}

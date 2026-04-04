package com.example.scrollconflict

import com.example.scrollconflict.models.SceneType
import com.example.scrollconflict.scenes.MixedConflictActivity
import com.example.scrollconflict.scenes.OppositeDirectionActivity
import com.example.scrollconflict.scenes.SameDirectionActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * SceneType 枚举的纯 JUnit 单元测试
 */
class MainActivityTest {

    @Test
    fun testSceneTypeEnumContainsAllScenes() {
        val sceneTypes = SceneType.values()
        assertEquals("Should have exactly 3 scene types", 3, sceneTypes.size)

        val sameDirection = SceneType.SAME_DIRECTION
        assertEquals("同向滑动冲突", sameDirection.title)
        assertEquals("垂直 ScrollView 嵌套垂直 RecyclerView", sameDirection.description)
        assertEquals(SameDirectionActivity::class.java, sameDirection.activityClass)

        val oppositeDirection = SceneType.OPPOSITE_DIRECTION
        assertEquals("反向滑动冲突", oppositeDirection.title)
        assertEquals("垂直 ScrollView 嵌套水平 ViewPager2", oppositeDirection.description)
        assertEquals(OppositeDirectionActivity::class.java, oppositeDirection.activityClass)

        val mixedConflict = SceneType.MIXED_CONFLICT
        assertEquals("混杂滑动冲突", mixedConflict.title)
        assertEquals("多层嵌套的复杂场景", mixedConflict.description)
        assertEquals(MixedConflictActivity::class.java, mixedConflict.activityClass)
    }

    @Test
    fun testAllSceneTypesHaveDescriptiveTitles() {
        SceneType.values().forEach { sceneType ->
            assertNotNull("Scene type should have a title", sceneType.title)
            assertTrue("Scene title should not be empty", sceneType.title.isNotEmpty())
            assertNotNull("Scene type should have a description", sceneType.description)
            assertTrue("Scene description should not be empty", sceneType.description.isNotEmpty())
        }
    }

    @Test
    fun testSceneTypeActivityClassMapping() {
        assertEquals(SameDirectionActivity::class.java, SceneType.SAME_DIRECTION.activityClass)
        assertEquals(OppositeDirectionActivity::class.java, SceneType.OPPOSITE_DIRECTION.activityClass)
        assertEquals(MixedConflictActivity::class.java, SceneType.MIXED_CONFLICT.activityClass)
    }
}

package com.example.scrollconflict

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.scrollconflict.databinding.ActivityMainBinding
import com.example.scrollconflict.models.SceneType

/**
 * MainActivity - 滑动冲突演示应用的入口界面
 *
 * 展示三种滑动冲突场景的导航卡片，用户点击卡片可以进入对应的演示场景。
 * 
 * 场景包括：
 * 1. 同向滑动冲突 (垂直嵌套垂直)
 * 2. 反向滑动冲突 (垂直嵌套水平)
 * 3. 混杂滑动冲突 (多层嵌套)
 *
 * **Validates: Requirements 5.1, 5.2, 5.3**
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 使用 View Binding 绑定布局
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 设置 Toolbar
        setSupportActionBar(binding.toolbar)
        
        // 设置场景卡片的点击事件
        setupSceneNavigation()
    }

    /**
     * 设置场景导航逻辑
     * 
     * 为每个场景卡片添加点击事件监听器，点击后导航到对应的场景 Activity。
     * 使用 SceneType 枚举来管理场景信息和对应的 Activity 类。
     */
    private fun setupSceneNavigation() {
        // 同向滑动冲突场景
        binding.cardSameDirection.setOnClickListener {
            navigateToScene(SceneType.SAME_DIRECTION)
        }
        
        // 反向滑动冲突场景
        binding.cardOppositeDirection.setOnClickListener {
            navigateToScene(SceneType.OPPOSITE_DIRECTION)
        }
        
        // 混杂滑动冲突场景
        binding.cardMixedConflict.setOnClickListener {
            navigateToScene(SceneType.MIXED_CONFLICT)
        }
    }

    /**
     * 导航到指定的场景 Activity
     *
     * @param sceneType 场景类型枚举，包含场景的标题、描述和对应的 Activity 类
     */
    private fun navigateToScene(sceneType: SceneType) {
        val intent = Intent(this, sceneType.activityClass)
        startActivity(intent)
    }
}

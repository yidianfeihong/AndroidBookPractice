package com.example.animationdemo

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.animationdemo.databinding.ActivityFrameAnimationBinding

/**
 * 帧动画（Frame Animation）
 *
 * 【面试核心知识点】
 *
 * 1. 原理：逐帧切换 Drawable，类似 GIF
 *    通过 AnimationDrawable 实现，定义在 res/drawable/ 下的 <animation-list>
 *
 * 2. 使用方式：
 *    - XML：定义 <animation-list>，每个 <item> 是一帧
 *    - 代码：new AnimationDrawable()，addFrame() 添加帧
 *
 * 3. ⚠️ 面试常问的坑：
 *    - 不能在 onCreate() 中直接调 start()！因为此时 View 还没完成测量和绘制
 *      需要用 view.post {} 或在 onWindowFocusChanged() 中调用
 *    - 图片帧数多或尺寸大时容易 OOM！所有帧一次性加载到内存
 *    - 实际开发中更推荐用 Lottie、GIF 库等方案
 *
 * 4. oneshot 属性：
 *    - true：播放一次后停在最后一帧
 *    - false：循环播放
 */
class FrameAnimationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFrameAnimationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageView = binding.ivFrame
        // 获取 AnimationDrawable（已在 XML 中设置了 src）
        val frameAnim = imageView.drawable as AnimationDrawable

        // 循环播放
        binding.btnStart.setOnClickListener {
            frameAnim.isOneShot = false
            frameAnim.stop()   // 先停止，防止重复启动
            frameAnim.start()
        }

        // 停止
        binding.btnStop.setOnClickListener {
            frameAnim.stop()
        }

        // 播放一次
        binding.btnOneShot.setOnClickListener {
            frameAnim.isOneShot = true
            frameAnim.stop()
            frameAnim.start()
        }

        // ========== 代码方式创建帧动画 ==========
        var codeAnim: AnimationDrawable? = null

        binding.btnCodeStart.setOnClickListener {
            val anim = AnimationDrawable().apply {
                addFrame(ContextCompat.getDrawable(this@FrameAnimationActivity, R.drawable.shape_circle_red)!!, 200)
                addFrame(ContextCompat.getDrawable(this@FrameAnimationActivity, R.drawable.shape_circle_blue)!!, 200)
                addFrame(ContextCompat.getDrawable(this@FrameAnimationActivity, R.drawable.shape_circle_green)!!, 200)
                addFrame(ContextCompat.getDrawable(this@FrameAnimationActivity, R.drawable.shape_circle_yellow)!!, 200)
                isOneShot = false
            }
            binding.ivFrameCode.setImageDrawable(anim)
            anim.start()
            codeAnim = anim
        }

        binding.btnCodeStop.setOnClickListener {
            codeAnim?.stop()
        }
    }
}

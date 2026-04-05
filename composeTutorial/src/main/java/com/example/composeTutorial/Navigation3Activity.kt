package com.example.composeTutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable

/**
 * Navigation 3 回退栈示例
 *
 * 核心思想：
 * 1. backStack 就是一个普通的 List，你自己管理
 *    - 入栈：backStack.add(Screen)
 *    - 出栈：backStack.removeLastOrNull()
 * 2. NavDisplay 把栈顶的 key 渲染成对应的 Composable
 * 3. 路由用 @Serializable + NavKey 定义（类型安全，替代字符串路由）
 */
class Navigation3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) { Nav3Demo() }
            }
        }
    }
}

// ===== 路由定义（必须实现 NavKey + @Serializable） =====
@Serializable data object Home : NavKey
@Serializable data class Detail(val id: Int, val title: String) : NavKey
@Serializable data object Settings : NavKey

// ===== 主入口 =====
@Composable
fun Nav3Demo() {
    // rememberNavBackStack 支持配置变更 + 进程死亡后自动恢复
    val backStack = rememberNavBackStack(Home)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(
                    onGoDetail = { id, title -> backStack.add(Detail(id, title)) },
                    onGoSettings = { backStack.add(Settings) }
                )
            }
            entry<Detail> { key ->
                DetailScreen(key.id, key.title) { backStack.removeLastOrNull() }
            }
            entry<Settings> {
                SettingsScreen { backStack.removeLastOrNull() }
            }
        }
    )
}

// ===== 首页 =====
@Composable
fun HomeScreen(onGoDetail: (Int, String) -> Unit, onGoSettings: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🏠 首页", fontSize = 28.sp)
        Spacer(Modifier.height(32.dp))
        Button(onClick = { onGoDetail(1, "Kotlin 协程") }) {
            Text("→ 详情：Kotlin 协程")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = { onGoDetail(2, "Compose 动画") }) {
            Text("→ 详情：Compose 动画")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onGoSettings) { Text("⚙ 设置页") }
    }
}

// ===== 详情页 =====
@Composable
fun DetailScreen(id: Int, title: String, onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("📄 详情页", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))
        Text("ID = $id", fontSize = 18.sp)
        Text("标题 = $title", fontSize = 18.sp)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack) { Text("← 返回") }
    }
}

// ===== 设置页 =====
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚙ 设置页", fontSize = 28.sp)
        Spacer(Modifier.height(16.dp))
        Text("这里是设置内容", fontSize = 16.sp)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBack) { Text("← 返回") }
    }
}

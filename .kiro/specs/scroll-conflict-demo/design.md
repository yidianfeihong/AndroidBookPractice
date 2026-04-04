# Design Document: scroll-conflict-demo

## Overview

scroll-conflict-demo 是一个 Android library module，用于演示和教学三种经典的滑动冲突场景及其解决方案。该模块通过自定义 ViewGroup 实现触摸事件拦截机制，为每个场景提供可切换的冲突/解决状态，帮助开发者深入理解 Android 的触摸事件分发体系。

### 核心目标

- 提供三种滑动冲突场景的可交互演示（同向、反向、混杂）
- 通过开关控制对比冲突状态和解决状态
- 使用自定义 ViewGroup 展示触摸事件拦截的实现方式
- 提供清晰的代码注释和教学说明文本

### 技术栈

- Kotlin
- Android SDK (minSdk 21, targetSdk 36)
- RecyclerView (同向冲突场景)
- ViewPager2 (反向冲突场景)
- Material Design Components
- View Binding

## Architecture

### 高层架构

该模块采用经典的 Android Activity-View 架构，结合自定义 ViewGroup 来实现触摸事件处理：

```
┌─────────────────────────────────────────┐
│         MainActivity                     │
│  (场景导航入口)                          │
└────────────┬────────────────────────────┘
             │
             ├──────────────┬──────────────┬──────────────┐
             │              │              │              │
             ▼              ▼              ▼              ▼
    ┌────────────┐  ┌────────────┐  ┌────────────┐  ┌────────────┐
    │  Scene1    │  │  Scene2    │  │  Scene3    │  │  About     │
    │  Activity  │  │  Activity  │  │  Activity  │  │  Activity  │
    └─────┬──────┘  └─────┬──────┘  └─────┬──────┘  └────────────┘
          │               │               │
          ▼               ▼               ▼
    ┌────────────┐  ┌────────────┐  ┌────────────┐
    │  Custom    │  │  Custom    │  │  Custom    │
    │  ViewGroup │  │  ViewGroup │  │  ViewGroup │
    │  (同向)    │  │  (反向)    │  │  (混杂)    │
    └────────────┘  └────────────┘  └────────────┘
```

### 模块结构

```
scroll-conflict-demo/
├── src/main/
│   ├── java/com/example/scrollconflict/
│   │   ├── MainActivity.kt                    # 场景导航主界面
│   │   ├── scenes/
│   │   │   ├── SameDirectionActivity.kt       # 同向冲突场景
│   │   │   ├── OppositeDirectionActivity.kt   # 反向冲突场景
│   │   │   └── MixedConflictActivity.kt       # 混杂冲突场景
│   │   ├── widgets/
│   │   │   ├── SameDirectionLayout.kt         # 同向冲突自定义容器
│   │   │   ├── OppositeDirectionLayout.kt     # 反向冲突自定义容器
│   │   │   └── MixedConflictLayout.kt         # 混杂冲突自定义容器
│   │   └── adapters/
│   │       ├── ItemAdapter.kt                 # RecyclerView 适配器
│   │       └── PageAdapter.kt                 # ViewPager2 适配器
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_main.xml              # 主导航界面
│   │   │   ├── activity_same_direction.xml    # 同向冲突布局
│   │   │   ├── activity_opposite_direction.xml # 反向冲突布局
│   │   │   ├── activity_mixed_conflict.xml    # 混杂冲突布局
│   │   │   ├── item_recycler.xml              # RecyclerView 项布局
│   │   │   └── item_page.xml                  # ViewPager2 页面布局
│   │   ├── values/
│   │   │   ├── strings.xml                    # 字符串资源
│   │   │   ├── colors.xml                     # 颜色资源
│   │   │   └── dimens.xml                     # 尺寸资源
│   │   └── drawable/                          # 图形资源
│   └── AndroidManifest.xml
└── build.gradle                               # 模块构建配置
```

### 触摸事件分发机制

Android 的触摸事件分发遵循以下流程：

```
Activity.dispatchTouchEvent()
    ↓
ViewGroup.dispatchTouchEvent()
    ↓
ViewGroup.onInterceptTouchEvent()  ← 关键拦截点
    ↓ (如果不拦截)
Child View.dispatchTouchEvent()
    ↓
View.onTouchEvent()
```

滑动冲突的本质是多个嵌套的可滑动视图都想响应同一个触摸事件序列。解决方案是在父容器的 `onInterceptTouchEvent()` 中根据滑动方向判断是否拦截事件。

## Components and Interfaces

### 1. MainActivity

主导航界面，提供三个场景的入口。

**职责：**
- 展示场景列表
- 处理场景选择和导航
- 提供关于页面入口

**关键方法：**
```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?)
    private fun setupSceneList()
    private fun navigateToScene(sceneType: SceneType)
}
```

### 2. Scene Activities

三个场景 Activity，每个对应一种冲突类型。

**SameDirectionActivity**
- 展示垂直 ScrollView 嵌套垂直 RecyclerView
- 提供解决方案开关
- 显示场景说明文本

**OppositeDirectionActivity**
- 展示垂直 ScrollView 嵌套水平 ViewPager2
- 提供解决方案开关
- 显示场景说明文本

**MixedConflictActivity**
- 展示多层嵌套的复杂场景
- 提供解决方案开关
- 显示场景说明文本

**通用接口：**
```kotlin
abstract class BaseSceneActivity : AppCompatActivity() {
    protected abstract val binding: ViewBinding
    protected abstract val customLayout: ConflictResolvableLayout
    
    protected fun setupToggleSwitch()
    protected fun updateDescription(isResolved: Boolean)
}
```

### 3. Custom ViewGroup Components

自定义容器组件，实现触摸事件拦截逻辑。

**ConflictResolvableLayout 接口**
```kotlin
interface ConflictResolvableLayout {
    var isConflictResolutionEnabled: Boolean
    fun enableConflictResolution()
    fun disableConflictResolution()
}
```

**SameDirectionLayout**

解决同向滑动冲突（垂直嵌套垂直）。

```kotlin
class SameDirectionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ConflictResolvableLayout {
    
    override var isConflictResolutionEnabled: Boolean = false
    
    private var lastX = 0f
    private var lastY = 0f
    private var initialX = 0f
    private var initialY = 0f
    
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean
    override fun onTouchEvent(event: MotionEvent): Boolean
}
```

**关键逻辑：**
- 在 `ACTION_DOWN` 记录初始触摸位置
- 在 `ACTION_MOVE` 计算滑动距离和方向
- 根据垂直滑动距离判断是否拦截事件
- 当解决方案启用时，父容器优先处理外层滑动

**OppositeDirectionLayout**

解决反向滑动冲突（垂直嵌套水平）。

```kotlin
class OppositeDirectionLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ConflictResolvableLayout {
    
    override var isConflictResolutionEnabled: Boolean = false
    
    private var lastX = 0f
    private var lastY = 0f
    private var initialX = 0f
    private var initialY = 0f
    
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean
    override fun onTouchEvent(event: MotionEvent): Boolean
}
```

**关键逻辑：**
- 计算水平和垂直滑动距离
- 比较 `abs(dx)` 和 `abs(dy)` 判断主要滑动方向
- 当水平滑动为主时，不拦截事件（交给 ViewPager2）
- 当垂直滑动为主时，拦截事件（父容器处理）

**MixedConflictLayout**

解决混杂滑动冲突（多层嵌套）。

```kotlin
class MixedConflictLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), ConflictResolvableLayout {
    
    override var isConflictResolutionEnabled: Boolean = false
    
    private var lastX = 0f
    private var lastY = 0f
    private var initialX = 0f
    private var initialY = 0f
    private var interceptLevel = 0  // 当前拦截层级
    
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean
    override fun onTouchEvent(event: MotionEvent): Boolean
}
```

**关键逻辑：**
- 结合同向和反向冲突的解决策略
- 根据嵌套层级和滑动方向协调事件分发
- 使用状态机跟踪当前处理的滑动层级

### 4. Adapters

**ItemAdapter (RecyclerView)**

```kotlin
class ItemAdapter(private val itemCount: Int) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    
    class ViewHolder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    override fun getItemCount(): Int = itemCount
}
```

**PageAdapter (ViewPager2)**

```kotlin
class PageAdapter(fragment: Fragment, private val pageCount: Int) : FragmentStateAdapter(fragment) {
    
    override fun getItemCount(): Int = pageCount
    override fun createFragment(position: Int): Fragment
}
```

## Data Models

### SceneType

场景类型枚举。

```kotlin
enum class SceneType(
    val title: String,
    val description: String,
    val activityClass: Class<out Activity>
) {
    SAME_DIRECTION(
        title = "同向滑动冲突",
        description = "垂直 ScrollView 嵌套垂直 RecyclerView",
        activityClass = SameDirectionActivity::class.java
    ),
    OPPOSITE_DIRECTION(
        title = "反向滑动冲突",
        description = "垂直 ScrollView 嵌套水平 ViewPager2",
        activityClass = OppositeDirectionActivity::class.java
    ),
    MIXED_CONFLICT(
        title = "混杂滑动冲突",
        description = "多层嵌套的复杂场景",
        activityClass = MixedConflictActivity::class.java
    )
}
```

### TouchEventState

触摸事件状态，用于跟踪滑动方向判断。

```kotlin
data class TouchEventState(
    var initialX: Float = 0f,
    var initialY: Float = 0f,
    var lastX: Float = 0f,
    var lastY: Float = 0f,
    var isScrolling: Boolean = false,
    var scrollDirection: ScrollDirection = ScrollDirection.NONE
) {
    fun reset() {
        initialX = 0f
        initialY = 0f
        lastX = 0f
        lastY = 0f
        isScrolling = false
        scrollDirection = ScrollDirection.NONE
    }
    
    fun calculateDelta(): Pair<Float, Float> {
        return Pair(lastX - initialX, lastY - initialY)
    }
}
```

### ScrollDirection

滑动方向枚举。

```kotlin
enum class ScrollDirection {
    NONE,
    HORIZONTAL,
    VERTICAL
}
```

### ConflictResolutionStrategy

冲突解决策略配置。

```kotlin
data class ConflictResolutionStrategy(
    val touchSlop: Int,              // 最小滑动距离阈值
    val directionThreshold: Float,   // 方向判断阈值（dx/dy 比值）
    val enableLogging: Boolean = false  // 是否启用调试日志
) {
    companion object {
        fun default(context: Context): ConflictResolutionStrategy {
            val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
            return ConflictResolutionStrategy(
                touchSlop = touchSlop,
                directionThreshold = 0.5f
            )
        }
    }
}
```

### LayoutConfiguration

布局配置数据类。

```kotlin
data class LayoutConfiguration(
    val recyclerViewItemCount: Int = 20,
    val viewPagerPageCount: Int = 5,
    val nestedLevels: Int = 3,
    val showDebugBorders: Boolean = false
)
```


## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system—essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Scroll Direction Calculation Accuracy

*For any* sequence of MotionEvent coordinates, the calculated scroll direction (HORIZONTAL, VERTICAL, or NONE) should accurately reflect the primary axis of movement based on the delta between initial and current positions.

**Validates: Requirements 7.3**

### Property 2: Touch Event State Machine Integrity

*For any* sequence of touch events containing ACTION_DOWN, ACTION_MOVE, and ACTION_UP actions, the touch event handler should process each event without throwing exceptions or corrupting internal state, maintaining valid TouchEventState throughout the sequence.

**Validates: Requirements 7.4**

### Property 3: Toggle State Synchronization

*For any* toggle state change (enabled/disabled), the isConflictResolutionEnabled flag in the corresponding ConflictResolvableLayout should immediately reflect the new state.

**Validates: Requirements 7.5**

### Property 4: Same Direction Conflict Interception

*For any* vertical scroll gesture within a SameDirectionLayout when conflict resolution is enabled, the layout's onInterceptTouchEvent should return true when the vertical scroll distance exceeds the touch slop threshold, preventing the nested RecyclerView from receiving the event.

**Validates: Requirements 2.4**

### Property 5: Opposite Direction Conflict Interception

*For any* touch gesture within an OppositeDirectionLayout when conflict resolution is enabled, the layout should intercept the event if and only if the vertical scroll distance exceeds the horizontal scroll distance by the direction threshold ratio, allowing horizontal gestures to pass through to the ViewPager2.

**Validates: Requirements 3.4**

### Property 6: Mixed Conflict Multi-Level Coordination

*For any* touch gesture within a MixedConflictLayout when conflict resolution is enabled, the layout should correctly route the event to the appropriate nesting level based on the scroll direction, ensuring that each level only handles events matching its scroll orientation.

**Validates: Requirements 4.4**

### Property 7: Scene Navigation Correctness

*For any* scene selection in MainActivity, the navigation action should launch the activity corresponding to the selected SceneType, with the correct activity class being started.

**Validates: Requirements 5.3**

## Error Handling

### Touch Event Processing Errors

**Scenario:** Invalid or malformed MotionEvent data
- **Handling:** Validate event coordinates before processing; use safe defaults if coordinates are NaN or infinite
- **Recovery:** Reset TouchEventState and log warning; do not crash

**Scenario:** Rapid toggle state changes during active touch sequence
- **Handling:** Complete the current touch sequence with the initial toggle state; apply new state only to subsequent sequences
- **Recovery:** Maintain consistent behavior within a single gesture

### View Hierarchy Errors

**Scenario:** Custom ViewGroup has no scrollable children
- **Handling:** Disable conflict resolution automatically; log warning
- **Recovery:** Behave as standard FrameLayout

**Scenario:** Unexpected view hierarchy structure
- **Handling:** Attempt to find scrollable children recursively; fall back to no-op if none found
- **Recovery:** Graceful degradation without crash

### Adapter Errors

**Scenario:** RecyclerView adapter returns item count < 20
- **Handling:** Accept the provided count; log warning that scrolling may not be demonstrable
- **Recovery:** Continue with available items

**Scenario:** ViewPager2 adapter returns page count < 5
- **Handling:** Accept the provided count; log warning
- **Recovery:** Continue with available pages

### Configuration Errors

**Scenario:** Invalid ConflictResolutionStrategy parameters (negative touchSlop, invalid threshold)
- **Handling:** Use default values from ViewConfiguration
- **Recovery:** Log warning and continue with safe defaults

### Navigation Errors

**Scenario:** Scene activity class not found or cannot be instantiated
- **Handling:** Show error toast to user; remain on MainActivity
- **Recovery:** Allow user to try other scenes

## Testing Strategy

### Unit Testing

Unit tests will focus on specific examples, edge cases, and component integration:

**Custom ViewGroup Tests:**
- Test that SameDirectionLayout correctly identifies vertical scrolls exceeding touch slop
- Test that OppositeDirectionLayout correctly distinguishes horizontal vs vertical gestures
- Test that MixedConflictLayout handles edge cases (diagonal scrolls, very small movements)
- Test toggle state changes update isConflictResolutionEnabled flag
- Test that disabled conflict resolution allows all events to pass through

**Adapter Tests:**
- Test ItemAdapter creates correct number of view holders
- Test ItemAdapter binds correct data to each position
- Test PageAdapter creates correct number of fragments

**Navigation Tests:**
- Test MainActivity launches correct activity for each SceneType
- Test back navigation returns to MainActivity

**Edge Cases:**
- Test touch events at exact touch slop boundary
- Test simultaneous horizontal and vertical movement (45-degree diagonal)
- Test very rapid touch sequences
- Test touch events outside view bounds

### Property-Based Testing

Property-based tests will verify universal properties across randomized inputs using Kotest property testing framework. Each test will run a minimum of 100 iterations.

**Library:** Kotest Property Testing (io.kotest:kotest-property)

**Property Test 1: Scroll Direction Calculation**
```kotlin
// Feature: scroll-conflict-demo, Property 1: Scroll direction calculation accuracy
class ScrollDirectionPropertyTest : StringSpec({
    "scroll direction should match primary movement axis" {
        checkAll(100, Arb.float(-1000f..1000f), Arb.float(-1000f..1000f)) { dx, dy ->
            val state = TouchEventState()
            state.initialX = 0f
            state.initialY = 0f
            state.lastX = dx
            state.lastY = dy
            
            val direction = calculateScrollDirection(state, touchSlop = 10)
            
            when {
                abs(dx) < 10 && abs(dy) < 10 -> direction shouldBe ScrollDirection.NONE
                abs(dx) > abs(dy) -> direction shouldBe ScrollDirection.HORIZONTAL
                else -> direction shouldBe ScrollDirection.VERTICAL
            }
        }
    }
})
```

**Property Test 2: Touch Event State Machine**
```kotlin
// Feature: scroll-conflict-demo, Property 2: Touch event state machine integrity
class TouchEventStateMachinePropertyTest : StringSpec({
    "touch event handler should not crash on any valid event sequence" {
        checkAll(100, Arb.list(Arb.motionEvent(), 1..50)) { events ->
            val layout = SameDirectionLayout(context)
            
            shouldNotThrowAny {
                events.forEach { event ->
                    layout.onInterceptTouchEvent(event)
                }
            }
            
            // State should be valid after sequence
            layout.touchState.initialX shouldBe gte(0f)
            layout.touchState.initialY shouldBe gte(0f)
        }
    }
})
```

**Property Test 3: Toggle State Synchronization**
```kotlin
// Feature: scroll-conflict-demo, Property 3: Toggle state synchronization
class ToggleStateSyncPropertyTest : StringSpec({
    "toggle state changes should immediately update conflict resolution flag" {
        checkAll(100, Arb.bool()) { newState ->
            val layout = SameDirectionLayout(context)
            
            layout.isConflictResolutionEnabled = newState
            
            layout.isConflictResolutionEnabled shouldBe newState
        }
    }
})
```

**Property Test 4: Same Direction Interception**
```kotlin
// Feature: scroll-conflict-demo, Property 4: Same direction conflict interception
class SameDirectionInterceptionPropertyTest : StringSpec({
    "vertical scrolls exceeding touch slop should be intercepted when enabled" {
        checkAll(100, Arb.float(0f..500f), Arb.float(-500f..500f)) { dy, dx ->
            val layout = SameDirectionLayout(context)
            layout.isConflictResolutionEnabled = true
            val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
            
            val downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 100f, 0)
            val moveEvent = MotionEvent.obtain(0, 100, MotionEvent.ACTION_MOVE, 100f + dx, 100f + dy, 0)
            
            layout.onInterceptTouchEvent(downEvent)
            val intercepted = layout.onInterceptTouchEvent(moveEvent)
            
            if (abs(dy) > touchSlop) {
                intercepted shouldBe true
            }
            
            downEvent.recycle()
            moveEvent.recycle()
        }
    }
})
```

**Property Test 5: Opposite Direction Interception**
```kotlin
// Feature: scroll-conflict-demo, Property 5: Opposite direction conflict interception
class OppositeDirectionInterceptionPropertyTest : StringSpec({
    "should intercept vertical scrolls but allow horizontal scrolls" {
        checkAll(100, Arb.float(-500f..500f), Arb.float(-500f..500f)) { dx, dy ->
            val layout = OppositeDirectionLayout(context)
            layout.isConflictResolutionEnabled = true
            val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
            
            val downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 100f, 0)
            val moveEvent = MotionEvent.obtain(0, 100, MotionEvent.ACTION_MOVE, 100f + dx, 100f + dy, 0)
            
            layout.onInterceptTouchEvent(downEvent)
            val intercepted = layout.onInterceptTouchEvent(moveEvent)
            
            if (abs(dy) > touchSlop && abs(dy) > abs(dx)) {
                intercepted shouldBe true
            } else if (abs(dx) > touchSlop && abs(dx) > abs(dy)) {
                intercepted shouldBe false
            }
            
            downEvent.recycle()
            moveEvent.recycle()
        }
    }
})
```

**Property Test 6: Mixed Conflict Coordination**
```kotlin
// Feature: scroll-conflict-demo, Property 6: Mixed conflict multi-level coordination
class MixedConflictCoordinationPropertyTest : StringSpec({
    "should route events to appropriate nesting level based on scroll direction" {
        checkAll(100, Arb.float(-500f..500f), Arb.float(-500f..500f), Arb.int(0..2)) { dx, dy, level ->
            val layout = MixedConflictLayout(context)
            layout.isConflictResolutionEnabled = true
            layout.currentLevel = level
            
            val downEvent = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 100f, 100f, 0)
            val moveEvent = MotionEvent.obtain(0, 100, MotionEvent.ACTION_MOVE, 100f + dx, 100f + dy, 0)
            
            layout.onInterceptTouchEvent(downEvent)
            val intercepted = layout.onInterceptTouchEvent(moveEvent)
            
            val direction = if (abs(dx) > abs(dy)) ScrollDirection.HORIZONTAL else ScrollDirection.VERTICAL
            val shouldIntercept = layout.shouldInterceptAtLevel(level, direction)
            
            intercepted shouldBe shouldIntercept
            
            downEvent.recycle()
            moveEvent.recycle()
        }
    }
})
```

**Property Test 7: Scene Navigation**
```kotlin
// Feature: scroll-conflict-demo, Property 7: Scene navigation correctness
class SceneNavigationPropertyTest : StringSpec({
    "selecting any scene should launch the correct activity" {
        checkAll(100, Arb.enum<SceneType>()) { sceneType ->
            val scenario = ActivityScenario.launch(MainActivity::class.java)
            
            scenario.onActivity { activity ->
                val intent = activity.createSceneIntent(sceneType)
                intent.component?.className shouldBe sceneType.activityClass.name
            }
            
            scenario.close()
        }
    }
})
```

### Test Configuration

**Unit Tests:**
- Framework: JUnit 4
- Mocking: Mockito
- UI Testing: Espresso
- Location: `src/test/` and `src/androidTest/`

**Property Tests:**
- Framework: Kotest Property Testing
- Minimum iterations: 100 per property
- Location: `src/test/`
- Dependencies:
  ```gradle
  testImplementation "io.kotest:kotest-runner-junit5:5.5.5"
  testImplementation "io.kotest:kotest-assertions-core:5.5.5"
  testImplementation "io.kotest:kotest-property:5.5.5"
  ```

### Test Coverage Goals

- Custom ViewGroup classes: 90%+ line coverage
- Touch event handling logic: 100% branch coverage
- Adapters: 80%+ line coverage
- Navigation logic: 100% branch coverage
- Property tests: All 7 properties implemented and passing


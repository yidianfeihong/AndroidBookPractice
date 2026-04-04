# Implementation Plan: scroll-conflict-demo

## Overview

本实现计划将 scroll-conflict-demo 设计转化为可执行的编码任务。该模块演示三种 Android 滑动冲突场景（同向、反向、混杂）及其解决方案，通过自定义 ViewGroup 实现触摸事件拦截机制。实现将使用 Kotlin 语言，基于 Android SDK，采用增量开发方式，确保每个步骤都能验证核心功能。

## Tasks

- [x] 1. 配置模块和项目结构
  - 创建 scroll-conflict-demo 模块的 build.gradle 配置文件
  - 配置 Kotlin 支持、minSdk 21、targetSdk 36
  - 添加必要依赖：RecyclerView、ViewPager2、Material Components、View Binding
  - 配置 AndroidManifest.xml，声明所有 Activity
  - 在 settings.gradle 中注册模块
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 10.1, 10.2, 10.3, 10.4, 10.5_

- [x] 2. 实现数据模型和工具类
  - [x] 2.1 创建 SceneType 枚举类
    - 定义三种场景类型：SAME_DIRECTION、OPPOSITE_DIRECTION、MIXED_CONFLICT
    - 为每个场景添加 title、description、activityClass 属性
    - _Requirements: 5.2_
  
  - [ ] 2.2 创建 ScrollDirection 枚举类
    - 定义滑动方向：NONE、HORIZONTAL、VERTICAL
    - _Requirements: 7.3_
  
  - [x] 2.3 创建 TouchEventState 数据类
    - 实现 initialX、initialY、lastX、lastY 属性
    - 实现 isScrolling、scrollDirection 属性
    - 实现 reset() 和 calculateDelta() 方法
    - _Requirements: 7.3, 7.4_
  
  - [x] 2.4 创建 ConflictResolutionStrategy 数据类
    - 实现 touchSlop、directionThreshold、enableLogging 属性
    - 实现 default() 工厂方法，使用 ViewConfiguration 获取系统 touchSlop
    - _Requirements: 7.3_
  
  - [x] 2.5 创建 LayoutConfiguration 数据类
    - 定义 recyclerViewItemCount、viewPagerPageCount、nestedLevels、showDebugBorders 属性
    - _Requirements: 2.2, 3.2_

- [x] 3. 实现 ConflictResolvableLayout 接口和基础自定义 ViewGroup
  - [x] 3.1 创建 ConflictResolvableLayout 接口
    - 定义 isConflictResolutionEnabled 属性
    - 定义 enableConflictResolution() 和 disableConflictResolution() 方法
    - _Requirements: 7.5_
  
  - [x] 3.2 实现 SameDirectionLayout 自定义 ViewGroup
    - 继承 FrameLayout，实现 ConflictResolvableLayout 接口
    - 实现 onInterceptTouchEvent() 方法，处理 ACTION_DOWN、ACTION_MOVE、ACTION_UP
    - 在 ACTION_DOWN 记录初始触摸位置
    - 在 ACTION_MOVE 计算垂直滑动距离，判断是否超过 touchSlop
    - 当 isConflictResolutionEnabled 为 true 且垂直滑动超过阈值时，拦截事件
    - 添加详细的代码注释，解释触摸事件拦截逻辑
    - _Requirements: 2.3, 2.4, 7.1, 7.2, 7.3, 7.4, 7.5, 8.1, 8.2, 8.4_
  
  - [ ] 3.3 为 SameDirectionLayout 编写属性测试
    - **Property 4: Same Direction Conflict Interception**
    - **Validates: Requirements 2.4**
    - 使用 Kotest Property Testing 验证垂直滑动超过 touchSlop 时正确拦截事件
  
  - [x] 3.4 实现 OppositeDirectionLayout 自定义 ViewGroup
    - 继承 FrameLayout，实现 ConflictResolvableLayout 接口
    - 实现 onInterceptTouchEvent() 方法
    - 计算水平和垂直滑动距离，比较 abs(dx) 和 abs(dy)
    - 当垂直滑动为主时拦截事件，水平滑动为主时不拦截
    - 添加详细的代码注释，解释方向判断逻辑
    - _Requirements: 3.3, 3.4, 7.1, 7.2, 7.3, 7.4, 7.5, 8.1, 8.2, 8.4_
  
  - [ ] 3.5 为 OppositeDirectionLayout 编写属性测试
    - **Property 5: Opposite Direction Conflict Interception**
    - **Validates: Requirements 3.4**
    - 使用 Kotest Property Testing 验证根据滑动方向正确拦截或放行事件
  
  - [x] 3.6 实现 MixedConflictLayout 自定义 ViewGroup
    - 继承 FrameLayout，实现 ConflictResolvableLayout 接口
    - 实现 onInterceptTouchEvent() 方法，结合同向和反向冲突解决策略
    - 使用 interceptLevel 状态变量跟踪当前处理的嵌套层级
    - 根据嵌套层级和滑动方向协调事件分发
    - 添加详细的代码注释，解释多层级协调逻辑
    - _Requirements: 4.3, 4.4, 7.1, 7.2, 7.3, 7.4, 7.5, 8.1, 8.2, 8.4_
  
  - [ ] 3.7 为 MixedConflictLayout 编写属性测试
    - **Property 6: Mixed Conflict Multi-Level Coordination**
    - **Validates: Requirements 4.4**
    - 使用 Kotest Property Testing 验证多层级事件路由的正确性

- [ ] 4. Checkpoint - 验证自定义 ViewGroup 核心逻辑
  - 确保所有自定义 ViewGroup 类编译通过
  - 确保触摸事件处理逻辑符合设计文档
  - 如有疑问，询问用户

- [x] 5. 实现适配器
  - [x] 5.1 创建 ItemAdapter (RecyclerView 适配器)
    - 实现 RecyclerView.Adapter<ViewHolder>
    - 创建 ViewHolder 内部类，使用 View Binding
    - 实现 onCreateViewHolder()、onBindViewHolder()、getItemCount() 方法
    - 支持配置项数量（默认 20 项）
    - _Requirements: 2.2_
  
  - [ ] 5.2 为 ItemAdapter 编写单元测试
    - 测试 getItemCount() 返回正确数量
    - 测试 onBindViewHolder() 绑定正确数据
    - _Requirements: 2.2_
  
  - [x] 5.3 创建 PageAdapter (ViewPager2 适配器)
    - 实现 FragmentStateAdapter
    - 实现 getItemCount() 和 createFragment() 方法
    - 支持配置页面数量（默认 5 页）
    - 为每个页面创建简单的 Fragment，显示页码
    - _Requirements: 3.2_
  
  - [ ] 5.4 为 PageAdapter 编写单元测试
    - 测试 getItemCount() 返回正确数量
    - 测试 createFragment() 创建正确的 Fragment
    - _Requirements: 3.2_

- [x] 6. 创建布局资源文件
  - [x] 6.1 创建 activity_main.xml
    - 使用 RecyclerView 或 LinearLayout 展示场景列表
    - 为每个场景添加标题和描述文本
    - 使用 Material Design 组件（CardView、Button）
    - _Requirements: 5.1, 5.2, 5.4_
  
  - [x] 6.2 创建 activity_same_direction.xml
    - 使用 SameDirectionLayout 作为根容器
    - 嵌套 ScrollView 和 RecyclerView
    - 添加 Switch 控件用于切换解决方案
    - 添加 TextView 显示场景说明文本
    - 使用不同背景色区分嵌套视图
    - _Requirements: 2.1, 2.3, 2.5, 6.1, 6.2, 6.3, 6.4, 9.1, 9.2, 9.3, 9.4_
  
  - [x] 6.3 创建 activity_opposite_direction.xml
    - 使用 OppositeDirectionLayout 作为根容器
    - 嵌套 ScrollView 和 ViewPager2
    - 添加 Switch 控件用于切换解决方案
    - 添加 TextView 显示场景说明文本
    - 使用不同背景色区分嵌套视图
    - _Requirements: 3.1, 3.3, 3.5, 6.1, 6.2, 6.3, 6.4, 9.1, 9.2, 9.3, 9.4_
  
  - [x] 6.4 创建 activity_mixed_conflict.xml
    - 使用 MixedConflictLayout 作为根容器
    - 创建至少三层嵌套的滑动视图结构
    - 添加 Switch 控件用于切换解决方案
    - 添加 TextView 显示场景说明文本
    - 使用不同背景色区分嵌套视图
    - _Requirements: 4.1, 4.2, 4.3, 4.5, 6.1, 6.2, 6.3, 6.4, 9.1, 9.2, 9.3, 9.4_
  
  - [x] 6.5 创建 item_recycler.xml
    - 设计 RecyclerView 项布局
    - 显示项位置编号
    - 使用合适的高度确保可滑动
    - _Requirements: 2.2, 9.4_
  
  - [x] 6.6 创建 item_page.xml
    - 设计 ViewPager2 页面布局
    - 显示页码
    - 使用不同背景色区分页面
    - _Requirements: 3.2, 9.4_

- [x] 7. 创建资源文件（strings、colors、dimens）
  - [x] 7.1 创建 strings.xml
    - 定义所有场景标题和描述文本
    - 定义冲突状态和解决状态的说明文本
    - 定义 Switch 控件的标签文本
    - _Requirements: 6.1, 6.2, 6.3, 6.4_
  
  - [x] 7.2 创建 colors.xml
    - 定义嵌套视图的背景色
    - 定义边框和分隔线颜色
    - 使用 Material Design 色彩方案
    - _Requirements: 9.1, 9.2, 9.3_
  
  - [x] 7.3 创建 dimens.xml
    - 定义文本大小、间距、边距
    - 定义视图高度和宽度
    - _Requirements: 6.4_

- [x] 8. 实现 MainActivity
  - [x] 8.1 创建 MainActivity 类
    - 继承 AppCompatActivity
    - 使用 View Binding 绑定布局
    - 实现 onCreate() 方法
    - _Requirements: 5.1_
  
  - [x] 8.2 实现场景列表展示
    - 使用 SceneType 枚举填充场景列表
    - 为每个场景显示标题和描述
    - _Requirements: 5.2_
  
  - [x] 8.3 实现场景导航逻辑
    - 为每个场景添加点击事件监听器
    - 使用 Intent 启动对应的 Activity
    - _Requirements: 5.3_
  
  - [ ] 8.4 为 MainActivity 编写单元测试
    - 测试场景列表正确显示
    - 测试点击场景启动正确的 Activity
    - _Requirements: 5.3_
  
  - [ ] 8.5 为场景导航编写属性测试
    - **Property 7: Scene Navigation Correctness**
    - **Validates: Requirements 5.3**
    - 使用 Kotest Property Testing 验证任意场景选择都启动正确的 Activity

- [x] 9. 实现 SameDirectionActivity
  - [x] 9.1 创建 SameDirectionActivity 类
    - 继承 AppCompatActivity
    - 使用 View Binding 绑定布局
    - 实现 onCreate() 方法
    - _Requirements: 2.1_
  
  - [x] 9.2 初始化 RecyclerView
    - 设置 LayoutManager
    - 创建 ItemAdapter 实例，传入 20 项数据
    - 绑定适配器到 RecyclerView
    - _Requirements: 2.2_
  
  - [x] 9.3 实现 Switch 控件逻辑
    - 监听 Switch 状态变化
    - 更新 SameDirectionLayout 的 isConflictResolutionEnabled 属性
    - 更新场景说明文本
    - _Requirements: 2.3, 2.5, 6.2, 6.3, 7.5_
  
  - [x] 9.4 添加 KDoc 注释
    - 为类和公共方法添加 KDoc 注释
    - _Requirements: 8.3_
  
  - [ ] 9.5 为 SameDirectionActivity 编写单元测试
    - 测试 Switch 切换更新 isConflictResolutionEnabled
    - 测试 RecyclerView 正确初始化
    - _Requirements: 2.3, 2.5_

- [x] 10. 实现 OppositeDirectionActivity
  - [x] 10.1 创建 OppositeDirectionActivity 类
    - 继承 AppCompatActivity
    - 使用 View Binding 绑定布局
    - 实现 onCreate() 方法
    - _Requirements: 3.1_
  
  - [x] 10.2 初始化 ViewPager2
    - 创建 PageAdapter 实例，传入 5 页数据
    - 绑定适配器到 ViewPager2
    - _Requirements: 3.2_
  
  - [x] 10.3 实现 Switch 控件逻辑
    - 监听 Switch 状态变化
    - 更新 OppositeDirectionLayout 的 isConflictResolutionEnabled 属性
    - 更新场景说明文本
    - _Requirements: 3.3, 3.5, 6.2, 6.3, 7.5_
  
  - [x] 10.4 添加 KDoc 注释
    - 为类和公共方法添加 KDoc 注释
    - _Requirements: 8.3_
  
  - [ ] 10.5 为 OppositeDirectionActivity 编写单元测试
    - 测试 Switch 切换更新 isConflictResolutionEnabled
    - 测试 ViewPager2 正确初始化
    - _Requirements: 3.3, 3.5_

- [x] 11. 实现 MixedConflictActivity
  - [x] 11.1 创建 MixedConflictActivity 类
    - 继承 AppCompatActivity
    - 使用 View Binding 绑定布局
    - 实现 onCreate() 方法
    - _Requirements: 4.1_
  
  - [x] 11.2 初始化嵌套滑动视图
    - 初始化多层嵌套的 RecyclerView 和 ViewPager2
    - 为每层设置适配器
    - _Requirements: 4.2_
  
  - [x] 11.3 实现 Switch 控件逻辑
    - 监听 Switch 状态变化
    - 更新 MixedConflictLayout 的 isConflictResolutionEnabled 属性
    - 更新场景说明文本
    - _Requirements: 4.3, 4.5, 6.2, 6.3, 7.5_
  
  - [x] 11.4 添加 KDoc 注释
    - 为类和公共方法添加 KDoc 注释
    - _Requirements: 8.3_
  
  - [ ] 11.5 为 MixedConflictActivity 编写单元测试
    - 测试 Switch 切换更新 isConflictResolutionEnabled
    - 测试嵌套视图正确初始化
    - _Requirements: 4.3, 4.5_

- [x] 12. 实现通用属性测试
  - [ ] 12.1 编写滑动方向计算属性测试
    - **Property 1: Scroll Direction Calculation Accuracy**
    - **Validates: Requirements 7.3**
    - 使用 Kotest Property Testing 验证滑动方向计算的准确性
  
  - [ ] 12.2 编写触摸事件状态机属性测试
    - **Property 2: Touch Event State Machine Integrity**
    - **Validates: Requirements 7.4**
    - 使用 Kotest Property Testing 验证触摸事件处理不会崩溃或损坏状态
  
  - [ ] 12.3 编写开关状态同步属性测试
    - **Property 3: Toggle State Synchronization**
    - **Validates: Requirements 7.5**
    - 使用 Kotest Property Testing 验证开关状态立即反映到 isConflictResolutionEnabled

- [ ] 13. Checkpoint - 集成测试和最终验证
  - 在 Android 模拟器或真机上运行所有三个场景
  - 验证每个场景的冲突状态和解决状态都能正常工作
  - 验证 Switch 切换功能正常
  - 验证场景导航功能正常
  - 确保所有测试通过
  - 如有疑问，询问用户

## Notes

- 任务标记 `*` 为可选任务，可跳过以加快 MVP 开发
- 每个任务都引用了具体的需求编号，确保可追溯性
- Checkpoint 任务确保增量验证
- 属性测试验证通用正确性属性
- 单元测试验证具体示例和边界情况
- 所有自定义 ViewGroup 都需要详细的代码注释，用于教学目的

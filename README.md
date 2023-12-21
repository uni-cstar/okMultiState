# 说明
StateLayout用于提供界面不同状态视图的统一管理。
- 1、提供常用缺省状态视图：empty、loading、error、no network
- 2、支持自定义状态视图：支持添加任意状态的自定义视图
- 3、支持全局统一配置和局部自定义配置
- 4、支持activity和View场景动态使用（几乎无感接入、侵入性低）
- 5、扩展LoaderUiState，用于符合绝大多数界面的状态使用，再配合droid-arch库提供的ViewModel和Activity等封装，几乎可以达到一行代码实现多状态视图与UiState的联动
- 6、强大的动画策略支持：
 - 支持切换动画
 - 支持loading动画显示设置最少时间（即loading切换其他状态时，至少显示一个动画或者指定的时间）
 - 支持设置智能显示（即在短时间内不显示loading，如果显示了loading则至少显示一个动画周期） 
 - 支持Lottie动画

# 工程配置：

在module的build.gradle中添加依赖
```
dependencies {
    implementation 'io.github.uni-cstar:okMultiState:0.0.3'
} 
```

# 具体使用
1、在xml中直接使用
参考demo

2、在Activity中使用
通过MultiState.bindMultiState(Activity)得到对应对象进行方法调用即可。
注意次方法是在android.R.id.content结点下添加一个容器实现，因此多增加了一个层级。

Kotlin方法：Activity.bindMultiState()
说明：重复调用也没问题，重复调用会返回之前添加的容器（或者以前的容器本身就支持的情况下就直接返回该容器）

3、为已存在的view添加功能支持
MultiState.bindMultiState(View)得到对应对象进行方法调用即可。
注意次方法是在指定view的位置创建了一个容器，并把之前的view添加进来。

Kotlin方法：View.bindMultiState()
说明：重复调用也没问题，重复调用会返回之前添加的容器（或者以前的容器本身就支持的情况下就直接返回该容器）

4、MultiState的具体功能，请查看上述对象提供的功能即可

5、MultiStateManager提供了默认的几种视图，如需自定义，通过其提供的方法设置即可。

6、对于某些场景需要自定义（不是使用全局配置）的情况，只需通过在xml中定义或者通过bindMultiState方法得到的对象设置即可。

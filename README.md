# StateLayout 说明
StateLayout用于提供界面不同状态视图的统一管理。
- 1、提供常用缺省状态视图：empty、loading、error、no network
- 2、支持自定义状态视图：支持添加任意状态的自定义视图
- 3、支持全局统一配置和局部自定义配置
- 4、支持activity和View场景动态使用（几乎无感接入、侵入性低）
- 5、扩展LoaderUiState，用于符合绝大多数界面的状态使用，再配合droid-arch库提供的ViewModel和Activity等封装，几乎可以达到一行代码实现多状态视图与UiState的联动
- 6、支持状态间切换动画、支持loading动画显示设置最少时间（即loading切换其他状态时，至少显示一个动画或者指定的时间）
- 7、支持Lottie动画

# 2.1.6
StateLayout.setLoaderUiState逻辑调整：如果UiState是Lazy，则不抛异常，不做什么逻辑（之前是抛异常）

# 2.1.5
修正StateLayout.onFinishInflate逻辑

# 2.1.4
StateLayout显示/隐藏StatusView，支持两种模式：
1、add/remove StatusView对应的视图（之前的模式）
2、visible/gone StatusView对应的视图（新增模式）
目前content view由原来的add/remove模式更改为visible/gone模式，避免将content view从layout中移除之后，
用户在非content状态时，通过find view 查找content内的view为null的问题。

部分api同步改名优化;


# 2.1.3
1、新增LoaderUiState.NotLoading将data转换成列表的方法
2、优化StateLayout.tryShowStateView逻辑，减少一次逻辑判断

# 2.1.2
修正StateLayout.setLoading的逻辑问题

# 2.1.1
调整工程结构，去掉ViewHolder的概念；优化动态设置视图支持（理论上极端的使用方式都支持了）



# 工程配置：
1、在工程的build.gradle中添加如下配置
```
allprojects {
    repositories {
        maven {
            url "http://nexus.ucuxin.com/repository/droid-app-release/"
        }
    }
}
```

2、在module的build.gradle中添加如下配置
```
dependencies {
    implementation bas.droid:droid-multistate:2.1.6
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

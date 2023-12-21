package unics.okmultistate

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import unics.okmultistate.handler.StateChangeHandler
import unics.okmultistate.status.EmptyStatusView
import unics.okmultistate.status.ErrorStatusView
import unics.okmultistate.status.LoadingStatusView
import unics.okmultistate.status.NoNetworkStatusView

/**
 * Created by Lucio on 2021/5/15.
 * 状态布局管理器
 *
 * 如果要修改按钮在tv上使用的模式，请通过提供xml中的变量 ： <bool name="television_ui_mode">true</bool>
 * @see Activity.bindStateLayout
 * @see View.bindStateLayout
 */
@SuppressLint("StaticFieldLeak")
object OkMultiState {

    /**
     * 状态切换处理器
     */
    @JvmStatic
    var stateChangeHandler: StateChangeHandler? = null

    /**
     * status view 的工厂集合
     */
    @JvmStatic
    private val factories: MutableMap<Int, StatusFactory<out StatusView>> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    var loadingFactory: StatusFactory<LoadingStatusView>
        get() = factories[Status.LOADING] as StatusFactory<LoadingStatusView>
        set(value) {
            factories[Status.LOADING] = value
        }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    var errorFactory: StatusFactory<ErrorStatusView>
        get() = factories[Status.ERROR] as StatusFactory<ErrorStatusView>
        set(value) {
            factories[Status.ERROR] = value
        }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    var emptyFactory: StatusFactory<EmptyStatusView>
        get() = factories[Status.EMPTY] as StatusFactory<EmptyStatusView>
        set(value) {
            factories[Status.EMPTY] = value
        }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    var noNetworkFactory: StatusFactory<NoNetworkStatusView>
        get() = factories[Status.NO_NETWORK] as StatusFactory<NoNetworkStatusView>
        set(value) {
            factories[Status.NO_NETWORK] = value
        }

    /**
     * 添加自定义工厂
     */
    @JvmStatic
    fun addCustomFactory(status: Int, factory: StatusFactory<StatusView>) {
        require(
            status != Status.LOADING
                    && status != Status.EMPTY
                    && status != Status.ERROR
                    && status != Status.NO_NETWORK
                    && status != Status.UNKNOWN
        ) {
            "自定义工厂的类型不能与默认的工厂类型相同，如需修改默认工厂，请使用对应的工厂设置方法。"
        }
        factories[status] = factory
    }

    /**
     * 是否是内置类型
     */
    @JvmStatic
    fun isInnerStatus(status: Int): Boolean {
        return status == Status.CONTENT
                || status == Status.LOADING
                || status == Status.EMPTY
                || status == Status.ERROR
                || status == Status.NO_NETWORK
                || status == Status.UNKNOWN
    }

    /**
     * 状态快照：用于获取当前全局设置的状态
     */
    @JvmStatic
    fun statusSnapshot(): MutableMap<Int, StatusView> {
        val snapshot = mutableMapOf<Int, StatusView>()
        for ((key, value) in factories) {
            snapshot[key] = value.createStatusView()
        }
        return snapshot
    }

    /**
     * 为Activity增加MultiState功能
     * 该方法最好在调用[Activity.setContentView]之后调用
     */
    @JvmStatic
    fun bind(activity: Activity, ignoreParentType: Boolean = false): StateLayout {
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
//            ?: activity.findViewById<ViewGroup>(R.id.content)
        //如果还未添加content view，则直接返回一个StateLayout
        val userView = contentView.getChildAt(0)

        //如果用户的视图已经支持MultiState，则直接返回
        if (!ignoreParentType && userView != null && userView is StateLayout)
            return userView

        if (userView == null) {
            val multiStateLayout = StateLayout(activity).also {
                it.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            //用户的视图还没设置，则此时直接添加一个容器并返回
            contentView.addView(multiStateLayout)
            return multiStateLayout
        }
        return bind(userView)
    }

    /**
     * 为指定的View增加MultiState功能
     * @param ignoreParentType 是否忽略目标视图的父级视图类型：默认不忽略，则会判断父级视图是否已经支持MultiState，如果支持则直接返回父级对象，
     * 设置为true则不管父级视图是否支持MultiState，都会新添加一个容器返回
     */
    @JvmStatic
    fun bind(
        targetView: View,
        ignoreParentType: Boolean = false
    ): StateLayout {
        val parent = targetView.parent as ViewGroup?

        //目标视图的父级视图已经支持MultiState，则直接返回
        if (!ignoreParentType && parent != null && parent is StateLayout)
            return parent

        val multiStateLayout = StateLayout(targetView.context)
        parent?.let { targetViewParent ->
            val targetViewIndex = targetViewParent.indexOfChild(targetView)
            targetViewParent.removeView(targetView)
            targetViewParent.addView(multiStateLayout, targetViewIndex, targetView.layoutParams)
        }
        multiStateLayout.addView(
            targetView,
            0,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        multiStateLayout.setContentView(targetView)
        return multiStateLayout
    }

    init {//设置默认的几种界面工厂
        factories[Status.LOADING] = StatusFactory.defaultLoading()
        factories[Status.ERROR] = StatusFactory.defaultError()
        factories[Status.EMPTY] = StatusFactory.defaultEmpty()
        factories[Status.NO_NETWORK] = StatusFactory.defaultNoNetwork()
    }

}



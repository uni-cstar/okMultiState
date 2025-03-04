package unics.okmultistate

import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import unics.okmultistate.handler.LeastLoadingStateChangeHandler
import unics.okmultistate.handler.LeastLoadingStatusView
import unics.okmultistate.handler.SmartStateChangeHandler
import unics.okmultistate.handler.StateChangeHandler
import unics.okmultistate.status.ContentStatusView
import unics.okmultistate.status.EmptyStatusView
import unics.okmultistate.status.ErrorStatusView
import unics.okmultistate.status.LoadingStatusView
import unics.okmultistate.status.NoNetworkStatusView
import unics.okmultistate.uistate.EmptyUiState
import unics.okmultistate.uistate.ErrorUiState
import unics.okmultistate.uistate.LoaderUiState
import unics.okmultistate.uistate.LoadingUiState
import unics.okmultistate.uistate.NonetworkUiState

/**
 * UiState绑定器
 */
internal typealias UiStateBinder<T> = ((T) -> Unit)

/**
 * Created by Lucio on 2023/2/17.
 * 状态布局：管理所有的StatusView
 * 设计规则：（一个StateLayout管理多个StatusView），只能包含一个内容结点，即会将该view视为content view
 * 原因：之前是支持直接包含多个child view作为 content view，但是这种设计不好管理child view的状态。当多个直接的child view 作为content view时，用户可能手动改变了child view 的可见性；
 * 因此在content与其他状态之间切换时，由于用户已经更改了child view，这样可能造成状态不一致的问题；虽然可以设计成让其他状态的view 覆盖content view，而不去更改content view的状态，但这种设计终归有点霸道;
 * 如果其他状态的view背景是半透明的，就会造成问题（虽然一般用户这么处理）
 */
open class StateLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), StateAction {

    /**
     * 状态变化监听
     */
    interface OnStatusChangeListener {
        fun onStatusChanged(old: Int, new: Int)
    }

    //布局加载器
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * attach集合：当前关联的所有状态
     */
    internal val allStatusView: MutableMap<Int, StatusView> = OkMultiState.statusSnapshot()

    private val statusBinders: MutableMap<Int, UiStateBinder<in StatusView>> = mutableMapOf()

    private var _stateChangeHandler: StateChangeHandler? = null

    /**
     * 状态改变帮助类：默认就是普通的添加和移除
     */
    var stateChangedHandler: StateChangeHandler
        get() = _stateChangeHandler ?: OkMultiState.stateChangeHandler ?: StateChangeHandler
        set(value) {
            _stateChangeHandler = value
        }

    private var statusChangeListener: OnStatusChangeListener? = null

    /**
     * 当前State类型
     */
    var currentState: Int = Status.UNKNOWN
        private set(value) {
            if (field != value) {
                val old = field
                field = value
                statusChangeListener?.onStatusChanged(old, value)
            }
        }

    /**
     * 是否已自动注册内容视图
     */
    private var hasAutoInjectContentView: Boolean = false

    /**
     * 是否已设置内容视图
     */
    private var hasSetContentView: Boolean = false

    private val contentStatusView: ContentStatusView? get() = allStatusView[Status.CONTENT] as? ContentStatusView

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (hasSetContentView)
            return

        if (childCount == 1 && contentStatusView == null) {
            val view = getChildAt(0)
            smartSetContentView(view)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        child?.let {
            log("[onViewAdded]:执行smartSetContentView")
            smartSetContentView(it)
        }
    }

    /**
     * 智能设置ContentView
     */
    private fun smartSetContentView(view: View) {
        if (hasSetContentView || hasAutoInjectContentView) {
            log("[smartSetContentView]:不处理  hasSetContentView=$hasSetContentView hasAutoInjectContentView=$hasAutoInjectContentView")
            return
        }

        allStatusView.forEach2 { _, statusView ->
            if (statusView.view == view) {
                //已关联的view中找到了该view，说明不是content view
                log("[smartSetContentView]:在 all view info中找到了该view，所以不是contentView")
                return
            }
        }
        setContentView(view)
        hasAutoInjectContentView = true
        log("[smartSetContentView]:自动注册contentview成功")
    }

    /**
     * 设置布局中的ContentView
     * 一般不需要开发者自己调用
     */
    fun setContentView(view: View) {
        val contentStatus = allStatusView.getOrPut(Status.CONTENT) {
            ContentStatusView()
        } as ContentStatusView
        contentStatus.view = view
        hasSetContentView = true
    }

    /**
     * 当前是否是内容视图
     */
    val isContent
        get() = currentState == Status.CONTENT

    /**
     * 当前是否是加载中视图
     */
    val isLoading
        get() = currentState == Status.LOADING

    /**
     * 当前是否是空数据视图
     */
    val isEmpty: Boolean
        get() = currentState == Status.EMPTY

    /**
     * 当前是否是错误视图
     */
    val isError: Boolean
        get() = currentState == Status.ERROR

    /**
     * 当前是否是断网视图
     */
    val isNoNetwork: Boolean
        get() = currentState == Status.NO_NETWORK

    /**
     * 设置状态监听器
     */
    fun setOnStateChangedListener(listener: OnStatusChangeListener?) {
        statusChangeListener = listener
    }

    /**
     * 使用默认状态切换处理器：view切换采用直接的add和remove方式
     */
    fun useDefaultStateChangeHandler() {
        stateChangedHandler = StateChangeHandler
    }

    /**
     * 使用渐变动画状态切换处理器：view切换的时候使用渐变动画
     */
    fun useFadeAnimStateChangeHandler(
        duration: Long = 400,
        useAnimOnFirstLoading: Boolean = false
    ) {
        stateChangedHandler =
            StateChangeHandler.fadeAnimStateChangeHandler(duration, useAnimOnFirstLoading)
    }

    /**
     * 最少时间Loading显示状态处理器：loading status 状态至少显示[leastDuration]时间之后才切换其他状态,
     * notice:使用该方法，必须保证内部保存的loading status view 必须实现了[LeastLoadingStatusView]
     * @param leastDuration 最少执行的动画时间，如果为null，则使用[LeastLoadingStatusView.duration]作为最小动画时间
     * @param transformTypeIfNotMatched 如果loading status view 不是[LeastLoadingStatusView]，是否自动转换类型
     * @param transformedLeastDuration 转换的动画持续时间
     * @param transformedRefreshInterval 转换的动画刷新间隔
     */
    fun useLeastLoadingStateChangeHandler(
        leastDuration: Long?,
        transformTypeIfNotMatched: Boolean = false,
        transformedLeastDuration: Long = 1000,
        transformedRefreshInterval: Long = 100
    ) {
        checkLoadingStatusView(
            transformTypeIfNotMatched,
            transformedLeastDuration,
            transformedRefreshInterval
        )
        stateChangedHandler = StateChangeHandler.leastLoadingAnimStateChangeHandler(leastDuration)
    }

    /**
     * 最少时间Loading显示状态处理器：loading status 状态至少显示[leastDuration] （如果未null，则使用[LeastLoadingStatusView.duration]）时间之后才切换其他状态
     * @param view loading status view，用于提供自定义的Loading status view，比如可以提供基于Lottie实现的动画视图
     * @param leastDuration 至少显示动画多长时间, 如果为null则至少显示动画完整播放一次的时间(即[LeastLoadingStatusView.duration]返回的时间])
     */
    fun useLeastLoadingStateChangeHandler(
        view: LeastLoadingStatusView,
        leastDuration: Long? = null
    ) {
        require(view.status == Status.LOADING) {
            "the status of the param must be ${Status.LOADING}"
        }
        setLoading(view)
        stateChangedHandler = StateChangeHandler.leastLoadingAnimStateChangeHandler(leastDuration)
    }

    /**
     * 最少时间Loading显示状态处理器：loading status 状态至少显示[leastDuration]时间之后才切换其他状态,
     * @param delayMills 延迟时间,具体查看[SmartStateChangeHandler.delayMills]
     * @param leastDuration 最少执行的动画时间，如果为null，则使用[LeastLoadingStatusView.duration]作为最小动画时间
     * @param transformTypeIfNotMatched 如果loading status view 不是[LeastLoadingStatusView]，是否自动转换类型
     * @param transformedLeastDuration 转换的动画持续时间
     * @param transformedRefreshInterval 转换的动画刷新间隔
     * @see StateChangeHandler.smartStateChangeHandler
     */
    fun useSmartStateChangeHandler(
        delayMills: Long,
        leastDuration: Long?,
        transformTypeIfNotMatched: Boolean = false,
        transformedLeastDuration: Long = 1000,
        transformedRefreshInterval: Long = 100
    ) {
        checkLoadingStatusView(
            transformTypeIfNotMatched,
            transformedLeastDuration,
            transformedRefreshInterval
        )
        stateChangedHandler = StateChangeHandler.smartStateChangeHandler(delayMills, leastDuration)
    }

    /**
     * @param transformedLeastDuration 转换的动画持续时间
     * @param transformedRefreshInterval 转换的动画刷新间隔
     * @see StateChangeHandler.smartStateChangeHandler
     */
    private fun checkLoadingStatusView(
        transformTypeIfNotMatched: Boolean = false,
        transformedLeastDuration: Long = 1000,
        transformedRefreshInterval: Long = 100
    ) {
        val loadingStatusView = allStatusView[Status.LOADING] as? LoadingStatusView
        if (loadingStatusView != null && loadingStatusView !is LeastLoadingStatusView) {
            if (transformTypeIfNotMatched) {
                setLoading(
                    loadingStatusView.toLeastLoadingStatusView(
                        transformedLeastDuration,
                        transformedRefreshInterval
                    )
                )
            } else {
                throw RuntimeException("使用${SmartStateChangeHandler::class.simpleName},loading status view 必须实现${LeastLoadingStatusView::class.simpleName}")
            }
        }
    }

    fun useSmartStateChangeHandler(
        view: LeastLoadingStatusView,
        delayMills: Long,
        leastDuration: Long? = null
    ) {
        require(view.status == Status.LOADING) {
            "the status of the param must be ${Status.LOADING}"
        }
        setLoading(view)
        stateChangedHandler = StateChangeHandler.smartStateChangeHandler(delayMills, leastDuration)
    }

    /**
     * 设置自定义LoadingView
     */
    fun setLoading(state: LoadingStatusView) {
        if (state.status != Status.LOADING)
            throw IllegalStateException("the state type of loading state must be #MultiState.LOADING(${Status.LOADING})")
        if (stateChangedHandler is LeastLoadingStateChangeHandler) {
            require(state is LeastLoadingStatusView) {
                "the stateChangedHandler is type of ${LeastLoadingStateChangeHandler::class.simpleName},require the loading status must be type of ${LeastLoadingStatusView::class.simpleName}"
            }
        }
        storeStateView<LoadingStatusView>(state)
    }

    /**
     * 设置自定义空状态
     */
    fun setEmpty(state: EmptyStatusView) {
        if (state.status != Status.EMPTY)
            throw IllegalStateException("the state type of empty state must be #MultiState.EMPTY(${Status.EMPTY})")
        storeStateView<EmptyStatusView>(state)
    }

    /**
     * 设置自定义错误状态
     */
    fun setError(state: ErrorStatusView) {
        if (state.status != Status.ERROR)
            throw IllegalStateException("the state type of error state must be #MultiState.ERROR(${Status.ERROR})")
        storeStateView<ErrorStatusView>(state)
    }

    /**
     * 设置自定义无网络状态
     */
    fun setNoNetwork(state: NoNetworkStatusView) {
        if (state.status != Status.NO_NETWORK)
            throw IllegalStateException("the state type of no network state must be #MultiState.NO_NETWORK(${Status.NO_NETWORK})")
        storeStateView<NoNetworkStatusView>(state)
    }

    /**
     * 添加自定义状态
     */
    fun addCustom(state: StatusView) {
        require(!OkMultiState.isInnerStatus(state.status)) {
            "The type value of the custom type cannot be the same as the built-in,any of MultiStateType "
        }
        storeStateView<StatusView>(state)
    }

    /**
     * 根据加载器状态处理State逻辑
     */
    override fun setLoaderUiState(state: LoaderUiState) {
        if (state is LoaderUiState.Loading) {
            val extra = state.extra as? LoadingUiState
            if (extra != null) {
                showLoading {
                    it.setData(extra)
                }
            } else {
                showLoading()
            }
        } else if (state.isContentState) {
            if (state.isEmptyState) {
                val extra = state.extra as? EmptyUiState
                if (extra != null) {
                    showEmpty {
                        it.setData(extra)
                    }
                } else {
                    showEmpty()
                }
            } else {
                showContent()
            }
        } else if (state is LoaderUiState.Error) {
            if (state.isNoNetworkExtra) {
                val extra = state.extra as? NonetworkUiState
                if (extra != null) {
                    showNoNetwork {
                        it.setData(extra)
                    }
                } else {
                    showNoNetwork()
                }
            } else {
                val extra = state.extra as? ErrorUiState
                if (extra != null) {
                    showError {
                        it.setData(extra)
                    }
                } else {
                    showError()
                }
            }
        } else if (state is LoaderUiState.LAZY) {
            //do nothing for lazy.
        } else {
            throw IllegalStateException("无法处理的LoaderUiState：${state}")
        }
    }

    /**
     * 显示内容布局
     * 显示content的逻辑不同于显示其他内容的逻辑：将在container中包含的所有子View，如果子view的类型不包含在此容器中，则视为是内容布局：也就是相当于没有添加tag，或者tag为STATE_CONTENT的为内容布局
     */
    override fun showContent() {
        allStatusView.forEach2 { _, statusView ->
            if (statusView.status == Status.CONTENT) {
                performStatusViewGainState(statusView)
            } else {
                performStatusViewLostState(statusView)
            }
        }
        currentState = Status.CONTENT
    }

    override fun showLoading(setup: ((LoadingStatusView) -> Unit)?) {
        tryShowStateView(Status.LOADING, setup)
    }

    override fun showEmpty(setup: ((EmptyStatusView) -> Unit)?) {
        tryShowStateView(Status.EMPTY, setup)
    }

    override fun showError(setup: ((ErrorStatusView) -> Unit)?) {
        tryShowStateView(Status.ERROR, setup)
    }

    override fun showNoNetwork(setup: ((NoNetworkStatusView) -> Unit)?) {
        tryShowStateView(Status.NO_NETWORK, setup)
    }

    /**
     * 显示自定义状态:自定义状态需要先调用[addCustom]方法添加，否则会导致报错
     */
    @JvmOverloads
    fun showCustom(state: Int, setup: ((StatusView) -> Unit)? = null) {
        tryShowStateView(state, setup)
    }

    /**
     * 存储状态
     * @return true：存储成功；false：存储失败（存储的状态已存在）
     */
    private fun <T : StatusView> storeStateView(statusView: T): Boolean {
        val type = statusView.status
        val previous = allStatusView[type]
        if (previous == statusView) {
            //已存在，忽略
            log("[storeStateView]:$type is found in current,ignore store state.")
            return false
        }
        allStatusView[type] = statusView

        previous?.let {
            stateChangedHandler.onReplaceStatusView(this, it, statusView)
            if (it.hasGainStateFocus)
                it.onLostStateFocus(this)
            it.onDetach(this)
        }

        if (currentState == type) {
            handler.postAtFrontOfQueue {
                showStateViewImpl<T>(statusView, statusBinders[statusView.status])
            }
        }
        return true
    }

    /**
     * 在持有的状态中显示对应类型的状态
     */
    private fun <T : StatusView> tryShowStateView(
        status: Int,
        setup: UiStateBinder<T>?
    ) {
        if (status == currentState) {
            setup?.let { binder ->
                getStatusView<T>(status)?.let { statusView ->
                    mainInvoke {
                        binder.invoke(statusView)
                    }
                }
            }
            return
        }
        getStatusView<T>(status)?.let { statusView ->
            showStateViewImpl(statusView = statusView, setup = setup)
        }
    }

    private fun <T : StatusView> getStatusView(status: Int): T? {
        val statusView = allStatusView[status] as? T
        if (statusView == null) {
            log("[showSpecStateView]: the state of type $statusView is not found. This operation cannot be performed.")
            return null
        }
        return statusView
    }

    private fun <T : StatusView> showStateViewImpl(
        statusView: T,
        setup: UiStateBinder<T>?
    ) {
        currentState = statusView.status
        mainInvoke {
            if (statusView.view == null) {
                statusView.onCreateView(context, inflater, this)
            }
            allStatusView.forEach2 { t, u ->
                if (t != statusView.status) {
                    performStatusViewLostState(u)
                }
            }
            setup?.let {
                it.invoke(statusView)
                statusBinders[statusView.status] = it as UiStateBinder<in StatusView>
            }
            performStatusViewGainState(statusView)
        }
    }

    /**
     * 状态视图失去状态：即当前显示的不是该视图
     */
    private fun performStatusViewLostState(statusView: StatusView) {
        if (statusView.hasGainStateFocus) {//已经为空的就不再执行移除动作，避免handler额外执行动画行为
            stateChangedHandler.onStatusViewLostStateFocus(this, statusView)
        }
    }

    /**
     * 状态视图获取状态：即当前显示的是该视图
     */
    private fun performStatusViewGainState(statusView: StatusView) {
        stateChangedHandler.onStatusViewGainStateFocus(this, statusView)
    }

    private fun mainInvoke(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            this.post(block)
        }
    }

//    private inline fun <E> SparseArray<E>.forEach(block: (E) -> Unit) {
//        for (index in 0 until size()) {
//            block.invoke(valueAt(index))
//        }
//    }

    private inline fun <K, V> Map<K, V>.forEach2(action: (K, V) -> Unit) {
        for (entry in this) {
            action.invoke(entry.key, entry.value)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun log(message: String) {
        Log.d("MultiState", message)
    }
}

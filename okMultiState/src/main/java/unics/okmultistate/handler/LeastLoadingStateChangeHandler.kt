package unics.okmultistate.handler

import android.animation.ValueAnimator
import android.util.Log
import unics.okmultistate.StateLayout
import unics.okmultistate.Status
import unics.okmultistate.StatusView

/**
 * loading动画至少显示指定（[leastDuration]或者[android.animation.Animator.getDuration]）时间控制器,避免loading动画果断引起界面切换闪烁
 * （比如先showLoading，但是很快时间调用了show其他状态，由于loading显示时间过短，因此会造成很快的显示了loading后显示其他视图，导致视图切换闪一下）
 * @param leastDuration 至少显示动画多长时间, 如果为null则至少显示动画完整播放一次的时间([LeastLoadingAnimation.duration]])
 */
open class LeastLoadingStateChangeHandler(
    private val leastDuration: Long? = null
) : StateChangeHandler {

    //其他状态切换处理器
    private val otherStateHandler: StateChangeHandler = StateChangeHandler

    /** 加载状态开始时间 */
    private var loadingStartTime = System.currentTimeMillis()

    /**
     * Loading状态之后的下一个状态视图（不会设置成loading status view）
     */
    private var nextStatusView: StatusView? = null

    /**
     * 当前显示的loading status view
     */
    private var loadingStatusView: LeastLoadingStatusView? = null

    private var animationPlaying = false

    private lateinit var container: StateLayout

    private fun log(msg: String) {
        Log.d("StateChange", msg)
    }

    private val animationUpdateCallback = ValueAnimator.AnimatorUpdateListener {
        checkNext()
    }

    override fun onStatusViewGainStateFocus(container: StateLayout, statusView: StatusView) {
        log("[onAddView] container=${container} info=${statusView}")
        this.container = container
        nextStatusView = if (statusView.status == Status.LOADING) {
            null
        } else {
            statusView
        }
        if (animationPlaying) {
            log("[onAddView] 正在播放动画")
            checkNext()
            return
        }

        if (statusView.status == Status.LOADING) {
            log("[onAddView] 开始播放动画")
            //保存当前的加载动画
            require(statusView is LeastLoadingStatusView){
                "使用${this::class.java.name} ,loading status view 必须实现 ${LeastLoadingStatusView::class.java.name} 接口"
            }
            statusView.onGainStateFocus(container)
            loadingStatusView = statusView
            //添加监听检测动画时间
            statusView.removeAnimatorUpdateListener(animationUpdateCallback)
            statusView.addAnimatorUpdateListener(animationUpdateCallback)
            statusView.startAnimation()
            loadingStartTime = System.currentTimeMillis()
            animationPlaying = true
        } else {
            //重置loading
            resetLoadingStatusState()
            otherStateHandler.onStatusViewGainStateFocus(container, statusView)
            log("[onAddView] 执行普通切换")
        }
    }

    override fun onStatusViewLostStateFocus(container: StateLayout, statusView: StatusView) {
        this.container = container
        if (statusView.status == Status.LOADING) {//如果要移除的是loading，先检测是否到时间移除
            val handled = checkNext()
            log("[onRemoveView] checkNext=$handled")
//            val next = this.nextStatusView
//            //下一个状态不为空，则尝试切换下一页
//            if (next == null || !checkNext(container, next)) {
//                log("[onRemoveView] 未切换，等待回调切换")
//                //未到状态切换下一页，则增加监听，根据动画刷新来切换下一页
//                loadingStatusView?.removeAnimatorUpdateListener(animationUpdateCallback)
//                loadingStatusView?.addAnimatorUpdateListener(animationUpdateCallback)
//            }
        } else {
            log("[onRemoveView] 采用其他方式切换")
            otherStateHandler.onStatusViewLostStateFocus(container, statusView)
        }
    }

    /**
     * 检查切换到下一个类型
     * @return true 执行了切换，false 未到切换条件
     */
    @Synchronized
    private fun checkNext(): Boolean {
        val next = nextStatusView ?: return false
        return if (isLoadingDurationEnough()) {
            log("[checkNext] 执行切换")
            //添加下一个视图:loading切换其他视图不采用其他动画处理
            resetLoadingStatusState()
            next.onGainStateFocus(container)
            true
        } else {
            log("[checkNext] 未到切换条件")
            false
        }
    }

    private fun isLoadingDurationEnough(): Boolean {
        //动画执行时间
        val playTime = System.currentTimeMillis() - loadingStartTime
        val duration = leastDuration ?: this.loadingStatusView?.duration ?: 1
        require(duration > 0) {
            "the duration of the animation must greater than 0."
        }
        log("[isLoadingDurationEnough] playTime=$playTime duration=$duration")
        return playTime >= duration
    }

    /**
     * 重置加载状态
     */
    private fun resetLoadingStatusState() {
        log("[reset] 重置")
        //动画时间已经超过单次或者指定的最小动画时间，并且下一个展示的控件不是loading类型的，则切换下一个
        detachLoadingStatusView(container, loadingStatusView)
        animationPlaying = false
        this.nextStatusView = null
        this.loadingStatusView = null
    }

    /**
     * 解除加载状态
     */
    private fun detachLoadingStatusView(
        container: StateLayout,
        loadingStatusView: LeastLoadingStatusView?,
        removeAllOther: Boolean = true
    ) {
        loadingStatusView?.let {
            it.removeAnimatorUpdateListener(animationUpdateCallback)
            it.cancelAnimation()
        }
        //此时可以先移除其他所有类型的
        if (removeAllOther) {
            container.allStatusView.values.forEach {
                it.onLostStateFocus(container)
            }
        }
    }

    override fun onReplaceStatusView(container: StateLayout, old: StatusView, new: StatusView) {
        this.container = container
        log("[onSwitchView]")
        if (nextStatusView == old) {//下一个view是被替换的view，更新即可
            nextStatusView = new
            log("[onSwitchView] 更换下一个view")
            return
        }

        if (old.status == Status.LOADING) {//替换loading视图
            log("[onSwitchView] 更换loading")
            require(old is LeastLoadingStatusView){
                "使用${this::class.java.name} ,loading status view 必须实现 ${LeastLoadingStatusView::class.java.name} 接口"
            }
            resetLoadingStatusState()
            loadingStatusView = old
        }
    }


}
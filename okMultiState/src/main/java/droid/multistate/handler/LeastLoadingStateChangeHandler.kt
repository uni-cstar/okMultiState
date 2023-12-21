package droid.multistate.handler

import android.animation.ValueAnimator
import android.util.Log
import droid.multistate.StateLayout
import droid.multistate.Status
import droid.multistate.StatusView

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
     * 下一个展示的Status View:不会是 status view
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
        nextStatusView?.let {
            checkNext(container, it)
        }
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
            checkNext(container, statusView)
            return
        }

        if (statusView.status == Status.LOADING) {
            log("[onAddView] 开始播放动画")
            //保存当前的加载动画
            if (statusView is LeastLoadingStatusView) {
                loadingStatusView = statusView
            } else {
                throw RuntimeException("使用${this::class.java.name} ,loading status view 必须实现 ${LeastLoadingStatusView::class.java.name} 接口")
            }
            statusView.onGainStateFocus(container)
            loadingStartTime = System.currentTimeMillis()
            animationPlaying = true
            statusView.startAnimation()
        } else {
            reset(container)
            otherStateHandler.onStatusViewGainStateFocus(container, statusView)
            log("[onAddView] 执行普通切换")
        }
    }

    override fun onStatusViewLostStateFocus(container: StateLayout, statusView: StatusView) {
        this.container = container
        if (statusView.status == Status.LOADING) {//如果要移除的是loading，先检测是否到时间移除
            log("[onRemoveView]")
            val next = this.nextStatusView
            //下一个状态不为空，则尝试切换下一页
            if (next == null || !checkNext(container, next)) {
                log("[onRemoveView] 未切换，等待回调切换")
                //未到状态切换下一页，则增加监听，根据动画刷新来切换下一页
                loadingStatusView?.removeAnimatorUpdateListener(animationUpdateCallback)
                loadingStatusView?.addAnimatorUpdateListener(animationUpdateCallback)
            }
        } else {
            log("[onRemoveView] 采用其他方式切换")
            otherStateHandler.onStatusViewLostStateFocus(container, statusView)
        }
    }

    /**
     * 检查切换下一个类型
     * @return true 执行了切换，false 未到切换条件
     */
    @Synchronized
    private fun checkNext(
        container: StateLayout,
        next: StatusView
    ): Boolean {
        //动画执行时间
        val loadingStatusView = this.loadingStatusView
        val playTime = System.currentTimeMillis() - loadingStartTime
        val duration = leastDuration ?: loadingStatusView?.duration ?: 1
        require(duration > 0) {
            "the duration of the animation must greater than 0."
        }
        log("[checkNext] playTime=$playTime duration=$duration")
        return if (playTime >= duration && next.status != Status.LOADING) {
            log("[checkNext] 执行切换")
            //添加下一个视图:loading切换其他视图不采用其他动画处理
            reset(container)
            next.onGainStateFocus(container)
            true
        } else {
            log("[checkNext] 未到切换条件")
            false
        }
    }

    private fun reset(container: StateLayout) {
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
        loadingStatusView?.removeAnimatorUpdateListener(animationUpdateCallback)
        loadingStatusView?.cancelAnimation()
        //此时可以先移除其他所有类型的
        if (removeAllOther){
//            container.removeAllViews()
            //移除所有view改为调用所有视图的失去焦点方法
            //todo 以前采用直接移除所有view的写法也略微粗暴，但是只是图方便又不容易出错的解决办法
            container.allStatusView.values.forEach {
                it.onLostStateFocus(container)
            }
        }
    }

    override fun onSwitchView(container: StateLayout, old: StatusView, new: StatusView) {
        super.onSwitchView(container, old, new)
        log("[onSwitchView]")
        if (old == nextStatusView) {
            nextStatusView = new
            log("[onSwitchView] 更换下一个view")
        }

        if (old.status == Status.LOADING) {
            log("[onSwitchView] 更换loading")
            if (old is LeastLoadingStatusView) {
                reset(container)
                loadingStatusView = old
            } else {
                throw RuntimeException("使用${this::class.java.name} ,loading status view 必须实现 ${LeastLoadingStatusView::class.java.name} 接口")
            }
        } else {
            log("[onSwitchView] 怪异")
        }
    }


}
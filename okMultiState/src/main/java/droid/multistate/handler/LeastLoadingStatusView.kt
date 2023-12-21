package droid.multistate.handler

import droid.multistate.Status
import droid.multistate.status.LoadingStatusView

/**
 * 至少完成一个动画周期的loading status view
 */
interface LeastLoadingStatusView : LoadingStatusView, LeastLoadingAnimation {

    override val status: Int
        get() = Status.LOADING

    /**
     * 将普通的[LoadingStatusView] 转换成 [LeastLoadingStatusView]以便使用[LeastLoadingStateChangeHandler]
     */
    class Wrapper(
        private val statusView: LoadingStatusView,
        private val animation: LeastLoadingAnimation
    ) : LeastLoadingStatusView,
        LoadingStatusView by statusView, LeastLoadingAnimation by animation {

        constructor(
            statusView: LoadingStatusView,
            leastDuration: Long,
            interval: Long = 300
        ) : this(statusView, LeastLoadingAnimation.DurationAnimation(leastDuration, interval))

        override val status: Int
            get() = statusView.status
    }
}
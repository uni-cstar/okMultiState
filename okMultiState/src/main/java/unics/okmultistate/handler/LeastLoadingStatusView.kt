package unics.okmultistate.handler

import unics.okmultistate.Status
import unics.okmultistate.status.LoadingStatusView

/**
 * 至少完成一个动画周期的loading status view
 */
interface LeastLoadingStatusView : LoadingStatusView, LeastLoadingAnimation {

    override val status: Int
        get() = Status.LOADING

    /**
     * 将普通的[LoadingStatusView] 转换成 [LeastLoadingStatusView]以便使用[LeastLoadingStateChangeHandler]
     * @param statusView 原视图
     * @param animation 原始图使用的动画
     */
    class Wrapper(
        private val statusView: LoadingStatusView, private val animation: LeastLoadingAnimation
    ) : LeastLoadingStatusView, LoadingStatusView by statusView,
        LeastLoadingAnimation by animation {

        /**
         * 创建模拟动画将普通的[LoadingStatusView]转换成[LeastLoadingStatusView]以便使用[LeastLoadingStateChangeHandler]
         * @param leastDuration 动画至少持续时间
         * @param interval 动画刷新间隔
         */
        constructor(
            statusView: LoadingStatusView, leastDuration: Long, interval: Long = 300
        ) : this(statusView, LeastLoadingAnimation.DurationAnimation(leastDuration, interval))

        override val status: Int
            get() = statusView.status
    }
}
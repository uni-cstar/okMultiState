package unics.okmultistate

import android.app.Activity
import android.view.View
import unics.okmultistate.handler.LeastLoadingAnimation
import unics.okmultistate.handler.LeastLoadingStatusView
import unics.okmultistate.status.LoadingStatusView

/**
 * 为某个界面应用多状态视图功能
 */
@Suppress("NOTHING_TO_INLINE")
inline fun Activity.bindStateLayout(): StateLayout {
    return OkMultiState.bind(this)
}

/**
 * 为某个view使用多状态视图功能
 */
@Suppress("NOTHING_TO_INLINE")
inline fun View.bindStateLayout(): StateLayout = OkMultiState.bind(this)


/**
 * 转换成支持至少动画时间的状态视图
 * @param animation 原视图使用的动画
 */
@Suppress("NOTHING_TO_INLINE")
inline fun LoadingStatusView.toLeastLoadingStatusView(animation: LeastLoadingAnimation): LeastLoadingStatusView {
    return LeastLoadingStatusView.Wrapper(this, animation)
}

/**
 * 转换成支持至少动画时间的状态视图（通过模拟动画实现：动画持续时间+动画刷新间隔）
 * @param duration 动画持续时间
 * @param interval 动画刷新间隔
 */
@Suppress("NOTHING_TO_INLINE")
inline fun LoadingStatusView.toLeastLoadingStatusView(
    duration: Long,
    interval: Long = 300
): LeastLoadingStatusView {
    return LeastLoadingStatusView.Wrapper(this, duration, interval)
}
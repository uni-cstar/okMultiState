package droid.multistate.handler

import droid.multistate.StateLayout
import droid.multistate.StatusView

interface StateChangeHandler {

    /**
     * 交换同一类型的view info
     * @param old 之前的view info，可能为null(当该状态没有被显示的情况下为空)
     * @param new 新的view info
     */
    fun onSwitchView(container: StateLayout, old: StatusView, new: StatusView) {}

    fun onStatusViewGainStateFocus(container: StateLayout, statusView: StatusView) {
        statusView.onGainStateFocus(container)
    }

    fun onStatusViewLostStateFocus(container: StateLayout, statusView: StatusView) {
        statusView.onLostStateFocus(container)
    }

    companion object : StateChangeHandler {

        /**
         * 状态改变的时候，使用渐变切换动画处理器
         */
        @JvmOverloads
        @JvmStatic
        fun fadeAnimStateChangeHandler(
            duration: Long = 400,
            useAnimOnFirstLoading: Boolean = false
        ): StateChangeHandler {
            return FadeAnimStateChangeHandler(duration, useAnimOnFirstLoading)
        }

        /**
         * 执行最小的加载动画时间
         * @param leastDuration 最少执行的动画时间，如果为null，则使用[LeastLoadingStatusView.duration]作为最小动画时间
         */
        @JvmOverloads
        @JvmStatic
        fun leastLoadingAnimStateChangeHandler(
            leastDuration: Long? = null
        ): StateChangeHandler {
            return LeastLoadingStateChangeHandler(leastDuration)
        }

        /**
         * 持续时间
         */
        @JvmStatic
        fun leastDurationAnimation(
            leastDuration: Long,
            interval: Long = 300
        ): LeastLoadingAnimation {
            return LeastLoadingAnimation.DurationAnimation(leastDuration, interval)
        }
    }


}
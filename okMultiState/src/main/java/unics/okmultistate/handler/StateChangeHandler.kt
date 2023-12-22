package unics.okmultistate.handler

import unics.okmultistate.StateLayout
import unics.okmultistate.StatusView

/**
 * 状态改变控制器：该控制器用于控制不同状态间切换时的额外处理规则，比如增加淡入淡出动画、比如控制最小显示时间等
 */
interface StateChangeHandler {

    /**
     * 替换状态视图；比如之前已经设置了一个emptyStatusView，后来又重新设置了一个不同的emptyStatusView，则会回调该方法
     * @param old 之前的view info
     * @param new 新的view info
     */
    fun onReplaceStatusView(container: StateLayout, old: StatusView, new: StatusView) {}

    /**
     * 状态视图获取了用户焦点：即当前用户期望显示的是该状态的视图时回调本方法（但是否会真正显示取决于设置的[StateChangeHandler]的处理规则）
     */
    fun onStatusViewGainStateFocus(container: StateLayout, statusView: StatusView) {
        statusView.onGainStateFocus(container)
    }

    /**
     * 状态视图失去了用户焦点：即当前用户期望显示的不再是该状态视图时回调本方法（但是否会真正移除取决于设置的[StateChangeHandler]的处理规则）
     */
    fun onStatusViewLostStateFocus(container: StateLayout, statusView: StatusView) {
        statusView.onLostStateFocus(container)
    }

    /**
     * 你可以在这个方法内做内存释放，在StateLayout不再使用时回调该方法
     */
    fun onCleared() {}

    companion object DEFAULT : StateChangeHandler {

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
         * @see SmartStateChangeHandler
         */
        @JvmStatic
        fun smartStateChangeHandler(
            delayMills: Long = 250,
            leastDuration: Long? = null
        ): StateChangeHandler {
            return SmartStateChangeHandler(delayMills, leastDuration)
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
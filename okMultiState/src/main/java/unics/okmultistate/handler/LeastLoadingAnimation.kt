package unics.okmultistate.handler

import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper

/**
 * 显示至少动画时间的动画接口，其实最主要的就是[duration]的定义
 * 主要是为了支持各种类型的动画，比如补间动画、属性动画、lottie动画、pag动画、模拟动画（）等
 */
interface LeastLoadingAnimation {

    /**
     * 单次动画的时间：用于控制该状态至少需要显示的时间
     */
    val duration: Long

    /**
     * 取消动画：切换其他类型控件时，取消之前的loading动画
     * animation.cancelAnimation()
     * animation.removeAllUpdateListeners()
     */
    fun cancelAnimation()

    /**
     * 开始执行动画
     * 为达到至少执行指定时间的动画，需要将动画设置为无限循环执行,参考[ValueAnimator.setRepeatCount]
     * @see ValueAnimator.setRepeatCount
     * @see ValueAnimator.INFINITE
     */
    fun startAnimation()

    fun addAnimatorUpdateListener(listener: ValueAnimator.AnimatorUpdateListener)

    fun removeAnimatorUpdateListener(listener: ValueAnimator.AnimatorUpdateListener)

    /**
     * 根据指定的持续时间模拟动画的执行
     * @param duration 持续时间
     * @param interval 更新回调调用的间隔时间
     */
    class DurationAnimation(override val duration: Long, private val interval: Long = 300) :
        LeastLoadingAnimation {

        private var isStart: Boolean = false

        private val WHAT_INTERVAL = 2

        private var listener: ValueAnimator.AnimatorUpdateListener? = null

        private val handler: Handler = Handler(Looper.getMainLooper()) {
            if (it.what == WHAT_INTERVAL) {
                listener?.let {
                    it.onAnimationUpdate(null)
                    sendInterval()
                }
            }
            true
        }

        private fun sendInterval() {
            if (isStart && listener != null) {
                handler.sendMessageDelayed(handler.obtainMessage(WHAT_INTERVAL), interval)
            } else {
                removeInterval()
            }
        }

        private fun removeInterval() {
            handler.removeMessages(WHAT_INTERVAL)
        }

        override fun cancelAnimation() {
            isStart = false
            handler.removeCallbacksAndMessages(null)
            listener = null
        }

        override fun startAnimation() {
            isStart = true
            handler.removeCallbacksAndMessages(null)
            sendInterval()
        }

        override fun addAnimatorUpdateListener(listener: ValueAnimator.AnimatorUpdateListener) {
            this.listener = listener
            sendInterval()
        }

        override fun removeAnimatorUpdateListener(listener: ValueAnimator.AnimatorUpdateListener) {
            this.listener = null
            removeInterval()
        }
    }
}
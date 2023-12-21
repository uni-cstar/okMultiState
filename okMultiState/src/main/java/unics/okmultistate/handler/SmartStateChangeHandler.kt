package unics.okmultistate.handler

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.os.Message
import unics.okmultistate.StateLayout
import unics.okmultistate.Status
import unics.okmultistate.StatusView

/**
 * Create by luochao
 * on 2023/12/21
 * 该Handler在显示loading status时会按照[delayMills]执行显示规则
 *
 * @param delayMills 延迟展示loading的时间。如果在[delayMills]内切换了其他status，则会立即处理，并不会实际显示loading status；如果超过[delayMills]则至少显示一个动画时间才会切换其他status。
 * @param leastDuration 至少显示动画多长时间, 如果为null则至少显示动画完整播放一次的时间([LeastLoadingAnimation.duration]])
 */
class SmartStateChangeHandler(
    private val delayMills: Long = 250,
    private val leastDuration: Long? = null
) : StateChangeHandler {

    companion object {
        private const val WHAT_DELAY_MSG = 1
    }

    private val otherStateHandler: StateChangeHandler = LeastLoadingStateChangeHandler()

    /**
     * 是否等待处理中
     */
    private var isWaiting: Boolean = false

    private val handler = object : Handler(Looper.getMainLooper()) {
        @Suppress("UNCHECKED_CAST")
        override fun handleMessage(msg: Message) {
            if (msg.what == WHAT_DELAY_MSG) {
                val (container, statusView) = msg.obj as Pair<StateLayout, StatusView>
                gainStateFocusInternal(container, statusView)
            }
        }
    }

    override fun onStatusViewGainStateFocus(container: StateLayout, statusView: StatusView) {
        if (statusView.status == Status.LOADING) {
            require(statusView is LeastLoadingStatusView) {
                "使用${this::class.java.name} ,loading status view 必须实现 ${LeastLoadingStatusView::class.java.name} 接口"
            }
            if (isDelaying()) {
                return
            }
            isWaiting = true
            handler.removeMessages(WHAT_DELAY_MSG)
            handler.sendMessageDelayed(
                handler.obtainMessage(
                    WHAT_DELAY_MSG,
                    Pair(container, statusView)
                ), delayMills
            )
        } else {
            gainStateFocusInternal(container, statusView)
        }
    }

    override fun onStatusViewLostStateFocus(container: StateLayout, statusView: StatusView) {
        otherStateHandler.onStatusViewLostStateFocus(container, statusView)
    }

    override fun onReplaceStatusView(container: StateLayout, old: StatusView, new: StatusView) {
        otherStateHandler.onReplaceStatusView(container, old, new)
    }

    /**
     * 执行statusView
     */
    private fun gainStateFocusInternal(container: StateLayout, statusView: StatusView) {
        reset()
        otherStateHandler.onStatusViewGainStateFocus(container, statusView)
    }

    private fun isDelaying(): Boolean = isWaiting && handler.hasMessages(WHAT_DELAY_MSG)

    override fun onCleared() {
        super.onCleared()
        reset()
    }

    private fun reset() {
        handler.removeCallbacksAndMessages(null)
        isWaiting = false
    }

}
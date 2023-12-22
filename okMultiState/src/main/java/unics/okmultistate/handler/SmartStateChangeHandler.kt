package unics.okmultistate.handler

import unics.okmultistate.StateLayout
import unics.okmultistate.StatusView

/**
 * Create by luochao
 * on 2023/12/21
 */
class SmartStateChangeHandler : StateChangeHandler {

    private val otherStateHandler: StateChangeHandler = LeastLoadingStateChangeHandler()

    /** 加载状态开始时间 */
    private var loadingStartTime = System.currentTimeMillis()

    /**
     * Loading状态之后的下一个状态视图（不会设置成loading status view）
     */
    private var nextStatusView: StatusView? = null

    override fun onReplaceStatusView(container: StateLayout, old: StatusView, new: StatusView) {
        otherStateHandler.onReplaceStatusView(container, old, new)
    }

    override fun onStatusViewGainStateFocus(container: StateLayout, statusView: StatusView) {
        super.onStatusViewGainStateFocus(container, statusView)
    }

    override fun onStatusViewLostStateFocus(container: StateLayout, statusView: StatusView) {
        super.onStatusViewLostStateFocus(container, statusView)
    }

}
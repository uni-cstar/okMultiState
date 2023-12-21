package unics.okmultistate.status

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import unics.okmultistate.StateLayout
import unics.okmultistate.Status
import unics.okmultistate.StatusView

/**
 * content view 的状态获取/失去，只做隐藏处理：如果采用move/add处理，可能会造成一些不好处理的问题
 * 比如：在[StateLayout]显示非content view的情况下，其他地方在诸如activity的场景中find content 中的view做操作的话，
 * 由于此时content 并没有add到content view 中，，找到的结果则为null，虽然可以通过其他办法解决，但终归不是很好把控
 */
internal class ContentStatusView(override var view: View? = null) : StatusView {

    override val status: Int = Status.CONTENT

    /**
     * 可见即视为拥有状态焦点
     */
    override val hasGainStateFocus: Boolean
        get() = view?.parent != null && view?.visibility == View.VISIBLE

    /**
     * 失去状态焦点时将视图设置为不可见即可
     */
    override fun onLostStateFocus(container: StateLayout) {
        view?.visibility = View.GONE
    }

    /**
     * 获得状态视图焦点时，将视图设置为可见即可
     */
    override fun onGainStateFocus(container: StateLayout) {
        view?.visibility = View.VISIBLE
    }

    override fun onCreateView(
        context: Context,
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View? {
        return view
    }

}
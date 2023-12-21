package unics.okmultistate

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import unics.okmultistate.status.EmptyStatusView
import unics.okmultistate.status.ErrorStatusView
import unics.okmultistate.status.LoadingStatusView
import unics.okmultistate.status.NoNetworkStatusView
import unics.okmultistate.status.internal.EmptyStatusViewImpl
import unics.okmultistate.status.internal.ErrorStatusViewImpl
import unics.okmultistate.status.internal.LoadingStatusViewImpl
import unics.okmultistate.status.internal.NoNetworkStatusViewImpl

/**
 * 类似[androidx.fragment.app.Fragment]的设计
 */
interface StatusView {

    /**
     * 当前控件对应的状态类型，一个类型只能存在一个
     */
    @Status
    val status: Int

    /**
     * 对应的View,如果为空，则会调用一次[onCreateView]
     */
    val view: View?

    /**
     * 是否拥有状态焦点，默认根据是否添加到容器中进行判断
     */
    val hasGainStateFocus: Boolean get() = view?.parent != null

    /**
     * 失去焦点：失去焦点的时候，可以是从container中移除自己，也可以是设置visibility不可见，默认是从parent中移除自己
     * 默认实现从容器中移除自己
     */
    fun onLostStateFocus(container: StateLayout) {
        view?.let {
            container.removeView(it)
        }
    }

    /**
     * 获取焦点
     * 默认将自己添加到容器
     */
    fun onGainStateFocus(container: StateLayout) {
        view?.let { view ->
            if (view.parent != container) {
                (view.parent as? ViewGroup)?.removeView(view)
                container.addView(view)
            } else {
                if (container.childCount > 1)
                    view.bringToFront()
            }
        }
    }

//    /**
//     * 附带的数据：即当前执行的函数表达式
//     */
//    var uiStateBinder: UiStateBinder<in StatusView>? = null

    /**
     * 附加到布局中：只在第一次附加到布局中的时候调用该方法
     * 游离控件可以返回null
     */
    fun onCreateView(
        context: Context,
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View?

    /**
     * 从容器中解除绑定:如果不是游离的控件，记得从view的parent中移除自己
     * 在此方法中清理内存
     * @param container 当前多状态容器
     */
    fun onDetach(container: ViewGroup) {
        view?.let { //非游离组件，解绑的时候从parent中移除自己
            container.removeView(it)
        }
    }

    companion object {

        @JvmStatic
        fun defaultLoading(@LayoutRes layoutId: Int = R.layout.ml_loading_view): LoadingStatusView {
            return LoadingStatusViewImpl(layoutId)
        }

        @JvmStatic
        fun defaultEmpty(@LayoutRes layoutId: Int = R.layout.ml_empty_view): EmptyStatusView {
            return EmptyStatusViewImpl(layoutId)
        }

        @JvmStatic
        fun defaultError(@LayoutRes layoutId: Int = R.layout.ml_error_view): ErrorStatusView {
            return ErrorStatusViewImpl(layoutId)
        }

        @JvmStatic
        fun defaultNoNetwork(@LayoutRes layoutId: Int = R.layout.ml_nonetwork_view): NoNetworkStatusView {
            return NoNetworkStatusViewImpl(layoutId)
        }

    }
}

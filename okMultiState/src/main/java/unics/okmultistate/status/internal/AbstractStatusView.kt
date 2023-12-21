package unics.okmultistate.status.internal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import unics.okmultistate.StatusView

abstract class AbstractStatusView(final override val status: Int, @LayoutRes val layoutId: Int) :
    StatusView {

    protected var contentView: View? = null

    protected abstract fun onViewCreated(view: View)

    override val view: View?
        get() = contentView

    override fun onCreateView(
        context: Context,
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View? {
        if (contentView == null) {
            contentView = inflater.inflate(layoutId, container, false)?.also {
                onViewCreated(it)
            }
        }
        return contentView
    }


    protected fun getString(@StringRes resId: Int): String? {
        // FIXME: 当view未初始化时怎么办？
        return contentView?.context?.resources?.getString(resId)
    }

}
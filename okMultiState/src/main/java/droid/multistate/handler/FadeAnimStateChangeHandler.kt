package droid.multistate.handler

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import droid.multistate.StateLayout
import droid.multistate.Status
import droid.multistate.StatusView
import java.lang.ref.WeakReference

/**
 * 切换状态时使用渐变透明动画过渡
 * @param duration 动画执行时间
 * @param useAnimOnFirstLoading 第一次添加view时是否使用动画，默认不使用
 */
open class FadeAnimStateChangeHandler(
    @JvmField var duration: Long = 400,
    var useAnimOnFirstLoading: Boolean = false
) : StateChangeHandler {

    private var layoutRef: WeakReference<StateLayout> = WeakReference(null)

    override fun onStatusViewGainStateFocus(container: StateLayout, statusView: StatusView) {
        statusView.view?.animate()?.cancel()
        if (layoutRef.get() != container) {
            layoutRef = WeakReference(container)
            if (!useAnimOnFirstLoading && container.currentState == Status.LOADING) {
                return attachToLayout(container, statusView)
            }
        }
        startInAnim(container, statusView)
    }

    override fun onStatusViewLostStateFocus(container: StateLayout, statusView: StatusView) {
        val view = statusView.view ?: return
        view.animate().cancel()
        if (container != layoutRef.get() && container.currentState == Status.LOADING) {
            return detachFromLayout(container, statusView)
        }
        startOutAnim(container, statusView)
    }

    protected open fun startInAnim(container: StateLayout, info: StatusView) {
        val view = info.view ?: return
        view.alpha = 0f
        attachToLayout(container, info)
        view.animate().setDuration(duration).alpha(1f).setListener(null).start()
    }

    protected open fun startOutAnim(container: StateLayout, info: StatusView) {
        val view = info.view ?: return
        view.animate().setDuration(duration).alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    return detachFromLayout(container, info).also {
                        //末端状态的时候都讲view的alpha还原，避免state change handler切换的时候造成干扰
                        view.alpha = 1f
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                    //末端状态的时候都讲view的alpha还原，避免state change handler切换的时候造成干扰
                    view.alpha = 1f
                }
            }).start()
    }

    protected fun attachToLayout(container: StateLayout, info: StatusView) {
        info.onGainStateFocus(container)
    }

    protected fun detachFromLayout(container: StateLayout, info: StatusView) {
        info.onLostStateFocus(container)
    }

}
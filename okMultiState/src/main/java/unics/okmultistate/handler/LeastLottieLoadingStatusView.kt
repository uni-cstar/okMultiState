package unics.okmultistate.handler

import android.animation.ValueAnimator
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import unics.okmultistate.uistate.LoadingUiState

/**
 * lottie动画实现的view <br />
 *
 * 注意：本库没有引入Lottie库，使用本类，需要在自己module(build.gradle)中引入lottie依赖
 */
abstract class LeastLottieLoadingStatusView(private val defaultDuration: Long = 300) :
    LeastLoadingStatusView {

    protected abstract fun getLottieView(): LottieAnimationView?

    protected open fun requireLottieView(): LottieAnimationView = getLottieView()!!

    override fun setData(data: LoadingUiState) {
    }

    override fun onDetach(container: ViewGroup) {
        //停止动画
        getLottieView()?.let {
            it.removeAllUpdateListeners()
            it.cancelAnimation()
        }
        super.onDetach(container)
    }

    override val duration: Long
        get() {
            val time = requireLottieView().duration
            return if (time > 0) {
                time
            } else {
                defaultDuration
            }
        }

    override fun cancelAnimation() {
        getLottieView()?.cancelAnimation()
    }

    override fun startAnimation() {
        getLottieView()?.playAnimation()
    }

    override fun addAnimatorUpdateListener(listener: ValueAnimator.AnimatorUpdateListener) {
        requireLottieView().addAnimatorUpdateListener(listener)
    }

    override fun removeAnimatorUpdateListener(listener: ValueAnimator.AnimatorUpdateListener) {
        requireLottieView().removeUpdateListener(listener)
    }

}
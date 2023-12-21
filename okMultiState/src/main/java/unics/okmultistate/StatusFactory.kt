package unics.okmultistate

import unics.okmultistate.status.EmptyStatusView
import unics.okmultistate.status.ErrorStatusView
import unics.okmultistate.status.LoadingStatusView
import unics.okmultistate.status.NoNetworkStatusView

/**
 * [StatusView]工厂
 */
fun interface StatusFactory<T : StatusView> {

    fun createStatusView(): T

    companion object {

        @JvmStatic
        fun defaultLoading(): StatusFactory<LoadingStatusView> {
            return StatusFactory { StatusView.defaultLoading() }
        }

        @JvmStatic
        fun defaultEmpty(): StatusFactory<EmptyStatusView> {
            return StatusFactory { StatusView.defaultEmpty() }
        }

        @JvmStatic
        fun defaultError(): StatusFactory<ErrorStatusView> {
            return StatusFactory { StatusView.defaultError()}
        }

        @JvmStatic
        fun defaultNoNetwork(): StatusFactory<NoNetworkStatusView> {
            return StatusFactory { StatusView.defaultNoNetwork() }
        }
    }
}


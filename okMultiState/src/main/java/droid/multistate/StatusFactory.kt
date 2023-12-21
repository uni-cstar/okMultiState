package droid.multistate

import droid.multistate.status.EmptyStatusView
import droid.multistate.status.ErrorStatusView
import droid.multistate.status.LoadingStatusView
import droid.multistate.status.NoNetworkStatusView
import droid.multistate.status.internal.EmptyStatusViewImpl
import droid.multistate.status.internal.ErrorStatusViewImpl
import droid.multistate.status.internal.LoadingStatusViewImpl
import droid.multistate.status.internal.NoNetworkStatusViewImpl

/**
 * [StatusView]工厂
 */
fun interface StatusFactory<T : StatusView> {

    fun createStatusView(): T

    companion object {

        @JvmStatic
        fun defaultLoading(): StatusFactory<LoadingStatusView> {
            return StatusFactory { StatusView.defaultLoading()  }
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


package droid.multistate

import droid.multistate.status.EmptyStatusView
import droid.multistate.status.ErrorStatusView
import droid.multistate.status.LoadingStatusView
import droid.multistate.status.NoNetworkStatusView
import droid.multistate.uistate.*

interface StateAction {

    /**
     * 通过loader uistate 确定显示的状态视图
     * 与LoaderState的联合使用
     */
    fun setLoaderUiState(state: LoaderUiState)

    /**
     * 显示内容布局
     */
    fun showContent()

    fun showLoading() = showLoading(null)

    fun showLoading(data: LoadingUiState) = showLoading {
        it.setData(data)
    }

    fun showLoading(setup: ((LoadingStatusView) -> Unit)?)

    fun showEmpty() = showEmpty(null)

    fun showEmpty(message:String) = showEmpty(EmptyUiState.create(message))

    fun showEmpty(data: EmptyUiState) = showEmpty {
        it.setData(data)
    }

    fun showEmpty(setup: ((EmptyStatusView) -> Unit)?)

    fun showError() = showError(null)

    fun showError(data: ErrorUiState) = showError {
        it.setData(data)
    }

    fun showError(setup: ((ErrorStatusView) -> Unit)?)

    fun showNoNetwork() = showNoNetwork(null)

    fun showNoNetwork(data: NonetworkUiState) = showNoNetwork {
        it.setData(data)
    }

    fun showNoNetwork(setup: ((NoNetworkStatusView) -> Unit)?)
}
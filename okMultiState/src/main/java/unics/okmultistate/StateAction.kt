package unics.okmultistate

import unics.okmultistate.status.EmptyStatusView
import unics.okmultistate.status.ErrorStatusView
import unics.okmultistate.status.LoadingStatusView
import unics.okmultistate.status.NoNetworkStatusView
import unics.okmultistate.uistate.EmptyUiState
import unics.okmultistate.uistate.ErrorUiState
import unics.okmultistate.uistate.LoaderUiState
import unics.okmultistate.uistate.LoadingUiState
import unics.okmultistate.uistate.NonetworkUiState

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
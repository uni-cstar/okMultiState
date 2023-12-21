package unics.okmultistate.status

import unics.okmultistate.StatusView
import unics.okmultistate.uistate.LoadingUiState

interface LoadingStatusView : StatusView {

    fun setData(data: LoadingUiState)

}
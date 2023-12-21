package unics.okmultistate.status

import unics.okmultistate.StatusView
import unics.okmultistate.uistate.ErrorUiState

interface ErrorStatusView : StatusView {

    fun setData(data: ErrorUiState)

}
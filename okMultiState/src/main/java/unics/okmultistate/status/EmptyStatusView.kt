package unics.okmultistate.status

import unics.okmultistate.StatusView
import unics.okmultistate.uistate.EmptyUiState

interface EmptyStatusView : StatusView {

    fun setData(data: EmptyUiState)

}
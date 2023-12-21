package unics.okmultistate.status

import unics.okmultistate.StatusView
import unics.okmultistate.uistate.NonetworkUiState

interface NoNetworkStatusView : StatusView {

    fun setData(data: NonetworkUiState)

}
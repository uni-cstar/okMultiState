package droid.multistate.status

import droid.multistate.StatusView
import droid.multistate.uistate.NonetworkUiState

interface NoNetworkStatusView : StatusView {

    fun setData(data: NonetworkUiState)

}
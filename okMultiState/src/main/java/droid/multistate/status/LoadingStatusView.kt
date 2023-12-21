package droid.multistate.status

import droid.multistate.StatusView
import droid.multistate.uistate.LoadingUiState

interface LoadingStatusView : StatusView {

    fun setData(data: LoadingUiState)

}
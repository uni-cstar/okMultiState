package droid.multistate.status

import droid.multistate.StatusView
import droid.multistate.uistate.ErrorUiState

interface ErrorStatusView : StatusView {

    fun setData(data: ErrorUiState)

}
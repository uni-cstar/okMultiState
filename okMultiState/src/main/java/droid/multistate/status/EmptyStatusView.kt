package droid.multistate.status

import droid.multistate.StatusView
import droid.multistate.uistate.EmptyUiState

interface EmptyStatusView : StatusView {

    fun setData(data: EmptyUiState)

}
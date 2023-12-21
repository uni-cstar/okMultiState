package droid.multistate.uistate

import androidx.annotation.StringRes

/**
 * @param showIndicator 是否显示指示器：进度条
 */
data class LoadingUiState constructor(
    val message: CharSequence = "",
    @StringRes
    val messageId: Int = 0,
    val showIndicator: Boolean = true
) {
    companion object {

        @JvmStatic
        @JvmOverloads
        fun create(message: CharSequence = "", showIndicator: Boolean = true): LoadingUiState {
            return LoadingUiState(message = message, showIndicator = showIndicator)
        }

        @JvmStatic
        @JvmOverloads
        fun create(messageId: Int, showIndicator: Boolean = true): LoadingUiState {
            return LoadingUiState(messageId = messageId, showIndicator = showIndicator)
        }
    }
}
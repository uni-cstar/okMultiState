package droid.multistate.uistate

import android.view.View
import androidx.annotation.StringRes

/**
 * 错误视图默认附带的消息
 * [message]和[messageId]：如果设置了messageId，则messageId生效，反之则message生效，如果message也为空，则不显示文本
 * @param showButton 是否显示按钮
 * @param requestFocus 按钮是否请求焦点，默认不请求：用于在tv场景显示错误视图时，按钮是否请求焦点之类的处理
 */
data class ErrorUiState(
    @StringRes
    val messageId: Int = 0,
    val message: CharSequence = "",
    val showButton: Boolean = false,
    @StringRes
    val buttonTextId: Int = 0,
    val buttonText: CharSequence = "",
    val requestFocus: Boolean = false,
    val buttonClick: View.OnClickListener? = null,
) {
    companion object {

        @JvmStatic
        fun create(@StringRes messageId: Int): ErrorUiState {
            return ErrorUiState(messageId = messageId, showButton = false)
        }

        @JvmStatic
        fun create(message: CharSequence): ErrorUiState {
            return ErrorUiState(message = message, showButton = false)
        }

        @JvmStatic
        @JvmOverloads
        fun createWithButton(
            message: CharSequence,
            buttonText: CharSequence = "重试",
            requestFocus: Boolean = false,
            buttonClick: View.OnClickListener
        ): ErrorUiState {
            return ErrorUiState(
                message = message,
                showButton = true,
                buttonText = buttonText,
                buttonClick = buttonClick,
                requestFocus = requestFocus
            )
        }

        @JvmStatic
        @JvmOverloads
        fun createWithButton(
            @StringRes
            messageId: Int,
            @StringRes
            buttonTextId: Int,
            requestFocus: Boolean = false,
            buttonClick: View.OnClickListener
        ): ErrorUiState {
            return ErrorUiState(
                messageId = messageId,
                showButton = true,
                buttonTextId = buttonTextId,
                buttonClick = buttonClick,
                requestFocus = requestFocus
            )
        }
    }
}
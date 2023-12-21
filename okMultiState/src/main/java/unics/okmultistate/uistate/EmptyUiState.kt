package unics.okmultistate.uistate

import android.view.View
import androidx.annotation.StringRes

/**
 * 空视图附带的内容信息
 * [message]和[messageId]：如果设置了messageId，则messageId生效，反之则message生效，如果message也为空，则不显示文本
 * @param messageId 消息对应的字符串资源id
 * @param message 自定义的消息内容
 * @param showButton 是否显示按钮
 * @param requestFocus 按钮是否请求焦点，默认不请求：用于在tv场景显示错误视图时，按钮是否请求焦点之类的处理
 * @param buttonClick 按钮点击事件
 */
data class EmptyUiState(
    @StringRes
    val messageId: Int = 0,
    val message: CharSequence = "",
    val showButton: Boolean = false,
    @StringRes
    val buttonTextId: Int = 0,
    val buttonText: CharSequence = "",
    val requestFocus: Boolean = false,
    val buttonClick: View.OnClickListener? = null
) {
    companion object {

        @JvmOverloads
        @JvmStatic
        fun create(@StringRes messageId: Int = 0): EmptyUiState {
            return EmptyUiState(messageId = messageId, showButton = false)
        }

        @JvmStatic
        fun create(message: CharSequence): EmptyUiState {
            return EmptyUiState(message = message, showButton = false)
        }

        @JvmStatic
        @JvmOverloads
        fun createWithButton(
            message: CharSequence,
            buttonText: CharSequence = "重试",
            requestFocus: Boolean = false,
            buttonClick: View.OnClickListener
        ): EmptyUiState {
            return EmptyUiState(
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
        ): EmptyUiState {
            return EmptyUiState(
                messageId = messageId,
                showButton = true,
                buttonTextId = buttonTextId,
                buttonClick = buttonClick,
                requestFocus = requestFocus
            )
        }
    }
}
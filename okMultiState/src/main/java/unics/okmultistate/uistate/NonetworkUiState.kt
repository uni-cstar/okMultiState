package unics.okmultistate.uistate

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
data class NonetworkUiState(
    @StringRes
    val messageId: Int = 0,
    val message: CharSequence = "",
    val retryButton: ButtonUiState? = null,
    val settingButton: ButtonUiState? = ButtonUiState.defaultNetworkSettingButtonUiState()
) {
    companion object {

        @JvmStatic
        fun create(@StringRes messageId: Int): NonetworkUiState {
            return NonetworkUiState(messageId = messageId, retryButton = null, settingButton = null)
        }

        @JvmStatic
        fun create(message: CharSequence): NonetworkUiState {
            return NonetworkUiState(message = message, retryButton = null, settingButton = null)
        }

        @JvmStatic
        fun createWithButton(
            message: CharSequence,
            retryButton: ButtonUiState?,
            settingButton: ButtonUiState? = ButtonUiState.defaultNetworkSettingButtonUiState()
        ): NonetworkUiState {
            return NonetworkUiState(
                message = message,
                retryButton = retryButton,
                settingButton = settingButton
            )
        }

        @JvmStatic
        fun createWithButton(
            @StringRes
            messageId: Int,
            retryButton: ButtonUiState?,
            settingButton: ButtonUiState? = ButtonUiState.defaultNetworkSettingButtonUiState()
        ): NonetworkUiState {
            return NonetworkUiState(
                messageId = messageId,
                retryButton = retryButton,
                settingButton = settingButton
            )
        }
    }
}
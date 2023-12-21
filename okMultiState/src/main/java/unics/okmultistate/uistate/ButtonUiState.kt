package unics.okmultistate.uistate

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast

data class ButtonUiState(
    val visible: Boolean = false,
    val buttonText: CharSequence = "",
    val requestFocus: Boolean = false,
    val buttonClick: View.OnClickListener? = null,
) {

    companion object {
        /**
         * 默认的网络设置按钮状态
         */
        @JvmStatic
        fun defaultNetworkSettingButtonUiState(
            requestFocus: Boolean = false
        ): ButtonUiState {
            return ButtonUiState(
                visible = true,
                buttonText = "网络设置",
                requestFocus = requestFocus,
                buttonClick = {
                    val intent = preferredNetworkSettingIntent(it.context)
                    if (intent == null) {
                        Toast.makeText(it.context, "无法打开网络设置界面", Toast.LENGTH_SHORT).show()
                        return@ButtonUiState
                    }
                    it.context.startActivity(intent)
                })
        }

        @JvmStatic
        fun preferredNetworkSettingIntent(context: Context): Intent? {
            var intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            if (intent.resolveActivity(context.packageManager) != null) {
                return intent
            } else {
                Log.d("MultiState", "Settings.ACTION_WIRELESS_SETTINGS Intent 不存在")
            }
            intent = Intent(Settings.ACTION_SETTINGS)
            if (intent.resolveActivity(context.packageManager) != null)
                return intent
            return null
        }
    }
}
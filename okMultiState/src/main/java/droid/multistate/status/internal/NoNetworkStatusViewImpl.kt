package droid.multistate.status.internal

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import droid.multistate.R
import droid.multistate.Status
import droid.multistate.status.NoNetworkStatusView
import droid.multistate.uistate.ButtonUiState
import droid.multistate.uistate.NonetworkUiState

class NoNetworkStatusViewImpl(@LayoutRes layoutId: Int = R.layout.ml_nonetwork_view) :
    AbstractStatusView(Status.NO_NETWORK, layoutId), NoNetworkStatusView {

    var imageView: ImageView? = null
        private set

    var textView: TextView? = null
        private set

    var buttonView: TextView? = null
        private set

    var settingView: TextView? = null
        private set

    override fun setData(data: NonetworkUiState) {
        var message: CharSequence? = data.message
        try {
            if (data.messageId > 0) {
                message = getString(data.messageId)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        setMessage(message)
        buttonView?.let {
            setButton(it, data.retryButton)
        }

        settingView?.let {
            setButton(it, data.settingButton)
        }
    }

    /**
     * 设置提示语
     */
    fun setMessage(message: CharSequence?) {
        textView?.let { textView ->
            if (message.isNullOrEmpty()) {
                textView.visibility = View.GONE
                textView.text = ""
            } else {
                textView.text = message
                textView.visibility = View.VISIBLE
            }
        }
    }

    private fun setButton(button: TextView, uiState: ButtonUiState?) {
        if (uiState == null) {
            button.visibility = View.GONE
            return
        }
        button.visibility = if (uiState.visible) View.VISIBLE else View.GONE
        button.text = uiState.buttonText
        button.setOnClickListener(uiState.buttonClick)
        if (uiState.requestFocus && uiState.visible) {
            button.requestFocus()
        }
    }

    override fun onViewCreated(view: View) {
        imageView = view.findViewById(R.id.ml_id_nonetwork_image)
        textView = view.findViewById(R.id.ml_id_nonetwork_text)
        buttonView = view.findViewById(R.id.ml_id_nonetwork_retry_button)
        settingView = view.findViewById(R.id.ml_id_nonetwork_setting_button)
    }

}
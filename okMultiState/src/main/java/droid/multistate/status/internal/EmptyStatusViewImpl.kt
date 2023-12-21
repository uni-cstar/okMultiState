package droid.multistate.status.internal

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import droid.multistate.R
import droid.multistate.Status
import droid.multistate.status.EmptyStatusView
import droid.multistate.uistate.EmptyUiState

class EmptyStatusViewImpl(@LayoutRes layoutId: Int = R.layout.ml_empty_view) :
    AbstractStatusView(Status.EMPTY, layoutId), EmptyStatusView {

    var imageView: ImageView? = null
        private set

    var textView: TextView? = null
        private set

    var buttonView: TextView? = null
        private set

    override fun onViewCreated(view: View) {
        imageView = view.findViewById(R.id.ml_id_empty_image)
        textView = view.findViewById(R.id.ml_id_empty_text)
        buttonView = view.findViewById(R.id.ml_id_empty_button)
    }

    override fun setData(data: EmptyUiState) {
        var message: CharSequence? = data.message
        try {
            if (data.messageId > 0) {
                message = getString(data.messageId)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        setMessage(message)
        if (data.showButton) {
            var buttonText: CharSequence? = data.buttonText
            try {
                if (data.buttonTextId > 0) {
                    buttonText = getString(data.buttonTextId)
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            setButton(
                isVisible = true,
                text = buttonText,
                requestFocus = data.requestFocus,
                onClick = data.buttonClick
            )
        } else {
            setButton(isVisible = false, text = "", requestFocus = false, onClick = null)
        }
    }

    /**
     * 设置提示语
     */
    fun setMessage(message: CharSequence?) {
        textView?.let { text ->
            if (message.isNullOrEmpty()) {
                text.visibility = View.GONE
                text.text = ""
            } else {
                text.text = message
                text.visibility = View.VISIBLE
            }
        }
    }

    fun setButton(
        isVisible: Boolean,
        text: CharSequence?,
        onClick: View.OnClickListener?
    ) = setButton(isVisible, text, false, onClick)

    /**
     * 设置按钮
     */
    fun setButton(
        isVisible: Boolean,
        text: CharSequence?,
        requestFocus: Boolean,
        onClick: View.OnClickListener?
    ) {
        buttonView?.let { button ->
            button.visibility = if (isVisible) View.VISIBLE else View.GONE
            button.text = text
            button.setOnClickListener(onClick)
            if (requestFocus && isVisible) {
                button.requestFocus()
            }
        }
    }

}
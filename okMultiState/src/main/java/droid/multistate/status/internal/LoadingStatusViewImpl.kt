package droid.multistate.status.internal

import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import droid.multistate.R
import droid.multistate.Status
import droid.multistate.status.LoadingStatusView
import droid.multistate.uistate.LoadingUiState

class LoadingStatusViewImpl(@LayoutRes layoutId: Int = R.layout.ml_loading_view) :
    AbstractStatusView(Status.LOADING, layoutId), LoadingStatusView {

    var indicatorView: View? = null
        private set

    var textView: TextView? = null
        private set

    override fun setData(data: LoadingUiState) {
        var message: CharSequence? = data.message
        try {
            if (data.messageId > 0) {
                message = getString(data.messageId)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        setMessage(message)
        setIndicatorVisible(data.showIndicator)
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

    /**
     * 设置指示器的可见性
     */
    fun setIndicatorVisible(isVisible: Boolean) {
        indicatorView?.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun onViewCreated(view: View) {
        indicatorView = view.findViewById(R.id.ml_id_loading_indicator)
        textView = view.findViewById(R.id.ml_id_loading_text)
    }

}
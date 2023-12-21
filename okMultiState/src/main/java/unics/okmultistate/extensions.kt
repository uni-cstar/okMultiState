package unics.okmultistate

import android.app.Activity
import android.view.View

/**
 * 为某个界面应用多状态视图功能
 */
inline fun Activity.bindStateLayout(): StateLayout {
    return OkMultiState.bind(this)
}

/**
 * 为某个view使用多状态视图功能
 */
inline fun View.bindStateLayout(): StateLayout = OkMultiState.bind(this)
internal typealias UiStateBinder<T> = ((T) -> Unit)
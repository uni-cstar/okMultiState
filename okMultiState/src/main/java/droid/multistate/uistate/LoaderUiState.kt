package droid.multistate.uistate

import android.view.View
import androidx.annotation.StringRes
import droid.multistate.NoNetworkException

/**
 * 加载器的状态,比较类似
 * @see LoaderUiState.LAZY
 * @see LoaderUiState.Loading
 * @see LoaderUiState.NotLoading
 * @see LoaderUiState.Error
 */
sealed class LoaderUiState private constructor(
    val endOfPaginationReached: Boolean,
    val extra: Any? = null
) {

    companion object {

        /**
         * 加载中
         */
        @JvmField
        val LOADING = Loading()

        @JvmStatic
        @JvmOverloads
        fun loading(messageId: Int = 0, showIndicator: Boolean = true) =
            loading(LoadingUiState.create(messageId, showIndicator))

        @JvmStatic
        @JvmOverloads
        fun loading(message: CharSequence, showIndicator: Boolean = true) =
            loading(LoadingUiState.create(message, showIndicator = showIndicator))

        @JvmStatic
        fun loading(uiState: LoadingUiState) = Loading(extra = uiState)

        /**
         * 用于内容视图为空场景的快速使用
         */
        @JvmField
        val EMPTY = empty()

        @JvmStatic
        fun empty(extra: Any? = null) = NotLoading(null, true, extra)

        @JvmStatic
        fun empty(@StringRes messageId: Int) = empty(EmptyUiState.create(messageId))

        @JvmStatic
        fun empty(message: CharSequence) = empty(EmptyUiState.create(message = message))

        @JvmStatic
        @JvmOverloads
        fun emptyWithButton(
            message: CharSequence,
            buttonText: CharSequence = "重试",
            requestFocus: Boolean = false,
            buttonClick: View.OnClickListener
        ) = empty(
            EmptyUiState.createWithButton(
                message = message,
                buttonText = buttonText,
                buttonClick = buttonClick,
                requestFocus = requestFocus
            )
        )

        @JvmStatic
        @JvmOverloads
        fun emptyWithButton(
            @StringRes
            messageId: Int,
            @StringRes
            buttonTextId: Int,
            requestFocus: Boolean = false,
            buttonClick: View.OnClickListener
        ) = empty(
            EmptyUiState.createWithButton(
                messageId = messageId,
                buttonTextId = buttonTextId,
                buttonClick = buttonClick,
                requestFocus = requestFocus
            )
        )

        @JvmStatic
        fun empty(uiState: EmptyUiState) =
            NotLoading(data = null, endOfPaginationReached = true, extra = uiState)

        /**
         * 用于内容视图但不关心数据场景的快速使用
         */
        @Deprecated("建议明确到底是否还有数据")
        @JvmField
        val CONTENT = data(Unit)

        fun <T> data(data: T?, extra: Any? = null): NotLoading<T?> {
            return NotLoading(data, endOfPaginationReached = true, extra)
        }

        fun <T> data(
            data: T?,
            endOfPaginationReached: Boolean,
            extra: Any? = null
        ): NotLoading<T?> {
            return NotLoading(data, endOfPaginationReached, extra)
        }


        @JvmStatic
        @JvmOverloads
        fun error(throwable: Throwable, message: String = throwable.message.orEmpty()) =
            Error(throwable, extra = ErrorUiState.create(message))

        fun error(throwable: Throwable, @StringRes messageId: Int) =
            Error(throwable, ErrorUiState.create(messageId))

        @JvmStatic
        @JvmOverloads
        fun errorWithButton(
            throwable: Throwable,
            message: CharSequence = throwable.message.orEmpty(),
            buttonText: CharSequence = "重试",
            requestFocus: Boolean = false,
            buttonClick: View.OnClickListener
        ) = Error(
            throwable, ErrorUiState.createWithButton(
                message = message,
                buttonText = buttonText,
                buttonClick = buttonClick,
                requestFocus = requestFocus
            )
        )

        @JvmStatic
        @JvmOverloads
        fun errorWithButton(
            throwable: Throwable,
            @StringRes
            messageId: Int,
            @StringRes
            buttonTextId: Int,
            requestFocus: Boolean = false,
            buttonClick: View.OnClickListener
        ) = Error(
            throwable, ErrorUiState.createWithButton(
                messageId = messageId,
                buttonTextId = buttonTextId,
                buttonClick = buttonClick,
                requestFocus = requestFocus
            )
        )

        @JvmStatic
        @JvmOverloads
        fun noNetwork(message: String, cause: Throwable = NoNetworkException(message)) =
            noNetwork(cause, NonetworkUiState.create(message))

        fun noNetwork(@StringRes messageId: Int, cause: Throwable) =
            noNetwork(cause, NonetworkUiState.create(messageId))

        fun noNetworkWithButton(
            message: String,
            cause: Throwable = NoNetworkException(message),
            retryButton: ButtonUiState?,
            settingButton: ButtonUiState?
        ) = noNetwork(cause, NonetworkUiState.createWithButton(message, retryButton, settingButton))

        fun noNetworkWithButton(
            @StringRes messageId: Int,
            cause: Throwable,
            retryButton: ButtonUiState?,
            settingButton: ButtonUiState?
        ): Error = noNetwork(
            cause,
            NonetworkUiState.createWithButton(messageId, retryButton, settingButton)
        )

        fun noNetwork(cause: Throwable, uiState: NonetworkUiState) = Error(cause, uiState)

    }

    /**
     * 是否为内容视图:内容视图又分为数据内容视图和空数据内容视图
     */
    val isContentState: Boolean get() = this is NotLoading<*>

    /**
     * 是否为数据内容视图
     */
    val isDataState: Boolean get() = this is NotLoading<*> && this.data != null

    /**
     * 是否为空内容视图
     */
    val isEmptyState: Boolean get() = this is NotLoading<*> && this.endOfPaginationReached && this.data == null

    /**
     * 是否为错误视图
     */
    val isErrorState: Boolean get() = this is Error

    /**
     * 懒惰状态:不知道该干嘛，，保持现状？
     */
    object LAZY : LoaderUiState(false, null)

    /**
     * loading状态
     */
    class Loading @JvmOverloads constructor(extra: Any? = null) : LoaderUiState(false, extra) {
        val message: CharSequence
            get() = (extra as? LoadingUiState)?.message?.toString() ?: extra?.toString().orEmpty()


        override fun toString(): String {
            return "Loading(extra=$extra)"
        }

        override fun equals(other: Any?): Boolean {
            return other is Loading && extra == other.extra
        }

        override fun hashCode(): Int {
            return extra.hashCode()
        }
    }

    /**
     * 数据状态:由分为有数据和 empty数据状态
     * @param data 对应的数据
     * @param endOfPaginationReached 是否没有更多数据了
     */
    class NotLoading<T> internal @JvmOverloads constructor(
        val data: T,
        endOfPaginationReached: Boolean,
        extra: Any? = null
    ) : LoaderUiState(endOfPaginationReached, extra) {
        override fun toString(): String {
            return "NotLoading(data=${data},endOfPaginationReached=$endOfPaginationReached,extra=$extra)"
        }

        override fun equals(other: Any?): Boolean {
            return other is NotLoading<*>
                    && data == other.data
                    && endOfPaginationReached == other.endOfPaginationReached
                    && extra == other.extra
        }

        override fun hashCode(): Int {
            return data.hashCode() + endOfPaginationReached.hashCode() + extra.hashCode()
        }

        /**
         * 将data转换成列表数据
         */
        fun toModelList(): List<Any> {
            return when (val data = this.data) {
                null -> emptyList<Any>()
                is Array<*> -> data.toList() as List<Any>
                is Collection<*> -> data.toList() as List<Any>
                else -> listOf(data)
            }
        }
    }

//    /**
//     * 数据状态：但不附加数据的情况，充当标志的情况
//     */
//    object CONTENT : LoaderUiState {
//        override val extra: Any? = null
//    }

    /**
     * 错误状态
     */
    class Error @JvmOverloads constructor(
        val error: Throwable,
        extra: Any? = null
    ) : LoaderUiState(false, extra) {
        val message: String
            get() {
                return if (isNoNetworkExtra) {
                    (extra as? NonetworkUiState)?.message?.toString() ?: extra?.toString().orEmpty()
                } else {
                    (extra as? ErrorUiState)?.message?.toString() ?: extra?.toString().orEmpty()
                }
            }

        val isNoNetworkExtra: Boolean get() = (extra as? NonetworkUiState) != null


        override fun equals(other: Any?): Boolean {
            return other is Error && error == other.error
        }

        override fun hashCode(): Int {
            return error.hashCode()
        }

        override fun toString(): String {
            return "Error(error=$error)"
        }
    }

}


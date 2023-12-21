package unics.okmultistate.sample.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import droid.multistate.StateLayout
import droid.multistate.StatusView
import droid.multistate.bindStateLayout
import droid.multistate.uistate.*
import kotlinx.coroutines.*
import unics.okmultistate.sample.R
import kotlin.random.Random


class MultiState2Activity() : AppCompatActivity() {

    private lateinit var multiStateLayout: StateLayout

    //    private val mGifLoadingView: GifLoadingView = GifLoadingView()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        multiStateLayout = bindStateLayout()
        multiStateLayout.setLoading(StatusView.defaultLoading(R.layout.skeleton_loading_layout))
        val actions = mutableListOf<() -> Unit>(
            {
                multiStateLayout.showLoading()
            },

            { multiStateLayout.showLoading(LoadingUiState.create("这是loading界面")) },

            { multiStateLayout.showLoading(LoadingUiState.create("这是没有指示器的loading界面", false)) },

            { multiStateLayout.showEmpty() },

            { multiStateLayout.showEmpty(EmptyUiState.Companion.create("这是Empty界面")) },

            {
                multiStateLayout.showEmpty(
                    EmptyUiState.createWithButton(
                        message = "这是有按钮的empty界面",
                        buttonClick = {
                            toast("点击了按钮")
                        })
                )
            },

            { multiStateLayout.showError(ErrorUiState.create("这是错误界面")) },

            {
                multiStateLayout.showError(
                    ErrorUiState.createWithButton(
                        "这也是错误界面",
                        buttonClick = {
                            toast("点击了按钮")
                        })
                )
            },

            {
                multiStateLayout.showNoNetwork {
                    it.setData(NonetworkUiState.Companion.create("这是网络错误"))
                }
            },

            {
                multiStateLayout.showNoNetwork {
                    it.setData(
                        NonetworkUiState.Companion.createWithButton(
                            "这是网络错误(有按钮)",
                            retryButton = ButtonUiState(true, "重试", true) {
                                toast("点击了按钮")
                            })
                    )
                }
            },

            {
                multiStateLayout.showNoNetwork {
                    it.setData(
                        NonetworkUiState.Companion.createWithButton(
                            "这是网络错误(有按钮)",
                            retryButton = ButtonUiState(true, "重试", true) {
                                toast("点击了按钮")
                            },
                            settingButton = ButtonUiState.defaultNetworkSettingButtonUiState()
                        )
                    )
                }
            }
        )

        lifecycleScope.launch {
            while (isActive) {
                withContext(Dispatchers.IO) {
                    delay(3000)
                }
                val index = Random.nextInt(0, actions.size - 1)
                actions[index].invoke()
            }
        }
    }

    private fun toast(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

}
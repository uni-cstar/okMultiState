package unics.okmultistate.sample.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import unics.okmultistate.StateLayout
import unics.okmultistate.sample.R
import unics.okmultistate.uistate.ErrorUiState
import unics.okmultistate.uistate.LoaderUiState

/**
 * Create by luochao
 * on 2023/12/21
 */
class SmartHandlerActivity : AppCompatActivity(R.layout.activity_smart_handler) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stateLayout = findViewById<StateLayout>(R.id.stateLayout)
        stateLayout.useSmartStateChangeHandler(250, null, true, 2000)
        findViewById<View>(R.id.shortTask).setOnClickListener {

            lifecycleScope.launch {
                stateLayout.showLoading()
                withContext(Dispatchers.Default) {
                    delay(200)
                }
                stateLayout.showContent()
            }
        }

        findViewById<View>(R.id.longTask).setOnClickListener {
            lifecycleScope.launch {
                stateLayout.showLoading()
                withContext(Dispatchers.Default) {
                    delay(3000)
                }
                stateLayout.showError(ErrorUiState.create("出错了"))
            }
        }
    }
}
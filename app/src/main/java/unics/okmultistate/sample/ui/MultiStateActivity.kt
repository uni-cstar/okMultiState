package unics.okmultistate.sample.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import droid.multistate.StateLayout
import droid.multistate.handler.LeastLottieLoadingStatusView
import droid.multistate.uistate.ButtonUiState
import droid.multistate.uistate.LoaderUiState
import unics.okmultistate.sample.R
import java.text.SimpleDateFormat
import java.util.Date

class MultiStateActivity() : AppCompatActivity(R.layout.multi_state_test_activity) {

    private lateinit var multiStateLayout: StateLayout

    //    private val mGifLoadingView: GifLoadingView = GifLoadingView()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        multiStateLayout = findViewById<StateLayout>(R.id.multi_state_layout)
//        multiStateLayout.useFadeAnimStateChangeHandler()
//        multiStateLayout.setLoading(object :
//            LeastLottieLoadingStatusView() {
//            override var view: View? = null
//
//            override fun getLottieView(): LottieAnimationView? {
//                return this.view?.findViewById(R.id.lottie)
//            }
//
//
//            override fun onCreateView(
//                context: Context,
//                inflater: LayoutInflater,
//                container: ViewGroup?
//            ): View? {
//                this.view =
//                    inflater.inflate(R.layout.bear_heart_loading_layout, container, false)
//                return this.view
//            }
//        })
        multiStateLayout.setOnStateChangedListener(object :StateLayout.OnStatusChangeListener{
            override fun onStatusChanged(old: Int, new: Int) {
                val view = findViewById<TextView>(R.id.content_right_tv)
                val time =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                view.text = time
                Log.d("MultiState","$time view = $view")

            }

        })
        multiStateLayout.setLoaderUiState(LoaderUiState.loading("开始加载"))
    }

    fun onViewClick(view: View) {
        when (view.id) {
            R.id.default_btn -> {
                multiStateLayout.useDefaultStateChangeHandler()
                multiStateLayout.showLoading()
            }
            R.id.fade_btn -> {
                multiStateLayout.useFadeAnimStateChangeHandler()
                multiStateLayout.showLoading()
            }
            R.id.latest_btn -> {
                multiStateLayout.useLeastLoadingStateChangeHandler(5000)
                multiStateLayout.showLoading()
            }
            R.id.skeleton_btn -> {
                multiStateLayout.useLeastLoadingStateChangeHandler(object :
                    LeastLottieLoadingStatusView() {

                    override var view: View? = null

                    override fun getLottieView(): LottieAnimationView? {
                        return this.view?.findViewById(R.id.lottie)
                    }


                    override fun onCreateView(
                        context: Context,
                        inflater: LayoutInflater,
                        container: ViewGroup?
                    ): View? {
                        this.view =
                            inflater.inflate(R.layout.skeleton_loading_layout, container, false)
                        return this.view
                    }

                }, null)
                multiStateLayout.showLoading()
            }
            R.id.loding_btn -> {
                multiStateLayout.setLoaderUiState(LoaderUiState.loading("开始加载"))
            }
            R.id.loding2_btn -> {
                multiStateLayout.setLoaderUiState(LoaderUiState.loading("努力加载中"))
            }
            R.id.loding_btn3 -> {
                multiStateLayout.setLoading(object :
                    LeastLottieLoadingStatusView() {


                    override var view: View? = null

                    override fun getLottieView(): LottieAnimationView? {
                        return this.view?.findViewById(R.id.lottie)
                    }


                    override fun onCreateView(
                        context: Context,
                        inflater: LayoutInflater,
                        container: ViewGroup?
                    ): View? {
                        this.view =
                            inflater.inflate(R.layout.bear_heart_loading_layout, container, false)
                        return this.view
                    }
                })
                multiStateLayout.showLoading()
            }
            R.id.empty_btn -> {
                multiStateLayout.setLoaderUiState(LoaderUiState.empty("空空如也"))
            }
            R.id.empty_btn2 -> {
                multiStateLayout.setLoaderUiState(
                    LoaderUiState.emptyWithButton(
                        "空空如也",
                        buttonClick = {
                            toast("点击了按钮")
                        })
                )
            }
            R.id.error_btn -> {
                multiStateLayout.setLoaderUiState(LoaderUiState.error(RuntimeException(), "空空如也"))
            }
            R.id.error_btn2 -> {
                multiStateLayout.setLoaderUiState(
                    LoaderUiState.errorWithButton(
                        RuntimeException(),
                        "空空如也",
                        buttonClick = {
                            toast("点击了按钮")
                        })
                )
            }
            R.id.nonetwork_btn -> {
                multiStateLayout.setLoaderUiState(LoaderUiState.noNetwork("网络不通"))
            }
            R.id.nonetwork_btn2 -> {
                multiStateLayout.setLoaderUiState(
                    LoaderUiState.noNetworkWithButton(
                        "网络故障",
                        retryButton = ButtonUiState(
                            visible = true,
                            buttonText = "重试",
                            false,
                            buttonClick = {
                                toast("点击了重试")
                            }),
                        settingButton = null
                    )
                )
            }
            R.id.nonetwork_btn3 -> {
                multiStateLayout.setLoaderUiState(
                    LoaderUiState.noNetworkWithButton(
                        R.string.no_network_tip,
                        RuntimeException(),
                        retryButton = ButtonUiState(
                            visible = true,
                            buttonText = "重试",
                            false,
                            buttonClick = {
                                toast("点击了重试")
                            }),
                        settingButton = ButtonUiState.defaultNetworkSettingButtonUiState(
                            requestFocus = true
                        )
                    )
                )
            }
            R.id.content_btn -> {
                multiStateLayout.showContent()
            }
        }
    }


    private fun toast(msg:String){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }
}
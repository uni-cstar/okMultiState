package unics.okmultistate.sample.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import unics.okmultistate.sample.R

/**
 * Create by luochao
 * on 2023/12/21
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<View>(R.id.testBtn1).setOnClickListener {
            startActivity(Intent(this, MultiStateActivity::class.java))
        }

        findViewById<View>(R.id.testBtn2).setOnClickListener {
            startActivity(Intent(this, MultiState2Activity::class.java))
        }
        findViewById<View>(R.id.testBtn3).setOnClickListener {
            startActivity(Intent(this, SmartHandlerActivity::class.java))
        }

    }
}
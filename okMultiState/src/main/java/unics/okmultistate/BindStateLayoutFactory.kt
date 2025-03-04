package unics.okmultistate

import android.app.Activity
import android.view.View

/**
 * 用于[View.bindStateLayout]、[Activity.bindStateLayout]方法创建默认的StateLayout
 */
interface BindStateLayoutFactory {
    fun createStateLayout(view: View): StateLayout
    fun createStateLayout(activity: Activity): StateLayout

    companion object DEFAULT : BindStateLayoutFactory {

        override fun createStateLayout(view: View): StateLayout {
            return StateLayout(view.context)
        }

        override fun createStateLayout(activity: Activity): StateLayout {
            return StateLayout(activity)
        }

    }
}
package liang.lollipop.electronicclock.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import liang.lollipop.widget.utils.Utils

/**
 * @author lollipop
 * @date 2019-09-20 22:27
 * 广播辅助类
 */
class BroadcastHelper(private val receive: (String) -> Unit): BroadcastReceiver() {

    private val filter = IntentFilter()

    private val logger = Utils.loggerI("BroadcastHelper")

    override fun onReceive(context: Context?, intent: Intent?) {
        logger("onReceive")
        intent?.action?.let {
            receive(it)
        }
    }

    companion object {

        const val ACTION_WIDGET_INFO_CHANGE = "ACTION_WIDGET_INFO_CHANGE"

        fun create(receive: (String) -> Unit): BroadcastHelper {
            return BroadcastHelper(receive)
        }

        fun sendEmptyBroadcast(context: Context, action: String, init: (Intent.() -> Unit)? = null) {
            val intent = Intent()
            intent.action = action
            init?.invoke(intent)
            context.sendBroadcast(intent)
        }
    }

    fun addActions(vararg actions: String) {
        for (action in actions) {
            filter.addAction(action)
        }
    }

    fun register(context: Context) {
        context.registerReceiver(this, filter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }

}
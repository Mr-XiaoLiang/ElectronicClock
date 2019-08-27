package liang.lollipop.widget.utils

import android.content.res.Resources
import android.util.Log
import android.util.TypedValue

/**
 * @author lollipop
 * @date 2019-08-01 17:48
 * 一些常规的工具类
 */
object Utils {

    const val isDebug = false

    fun loggerI(tag: String): ((String) -> Unit) {
        return { value ->
            Log.i("Lollipop", "$tag -> $value")
        }
    }

    fun loggerE(tag: String): ((String) -> Unit) {
        return { value ->
            Log.e("Lollipop", "$tag -> $value")
        }
    }

    fun logger(tag: String): LogHelper {
        return LogHelper(tag)
    }

    class LogHelper(private val tag: String) {

        val E = Utils.LogLevel.ERROR
        val D = Utils.LogLevel.DEBUG
        val I = Utils.LogLevel.INFO
        val V = Utils.LogLevel.VERBOSE
        val W = Utils.LogLevel.WARN

        private val LOG_TAG = "Lollipop-$tag"

        fun logger(value: String, level: LogLevel = I) {
            when(level) {
                Utils.LogLevel.ERROR -> Log.e(LOG_TAG, value)
                Utils.LogLevel.DEBUG -> Log.d(LOG_TAG, value)
                Utils.LogLevel.INFO -> Log.i(LOG_TAG, value)
                Utils.LogLevel.VERBOSE -> Log.v(LOG_TAG, value)
                Utils.LogLevel.WARN -> Log.w(LOG_TAG, value)
            }
        }

    }

    enum class LogLevel {
        ERROR,
        DEBUG,
        INFO,
        VERBOSE,
        WARN
    }

}
fun Resources.dp(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, this.displayMetrics)
}
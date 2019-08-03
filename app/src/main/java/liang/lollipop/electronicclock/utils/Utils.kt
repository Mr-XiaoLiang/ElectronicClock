package liang.lollipop.electronicclock.utils

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
            Log.i("Lollipop-$tag", value)
        }
    }

    fun logger(tag: String): LogHelper {
        return LogHelper(tag)
    }

    class LogHelper(private val tag: String) {

        val E = LogLevel.ERROR
        val D = LogLevel.DEBUG
        val I = LogLevel.INFO
        val V = LogLevel.VERBOSE
        val W = LogLevel.WARN

        private val LOG_TAG = "Lollipop-$tag"

        fun logger(value: String, level: LogLevel = I) {
            when(level) {
                LogLevel.ERROR -> Log.e(LOG_TAG, value)
                LogLevel.DEBUG -> Log.d(LOG_TAG, value)
                LogLevel.INFO -> Log.i(LOG_TAG, value)
                LogLevel.VERBOSE -> Log.v(LOG_TAG, value)
                LogLevel.WARN -> Log.w(LOG_TAG, value)
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
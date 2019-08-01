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

}
fun Resources.dp(value: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, this.displayMetrics)
}
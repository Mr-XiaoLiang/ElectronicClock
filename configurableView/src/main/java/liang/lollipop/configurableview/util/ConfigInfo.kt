package liang.lollipop.configurableview.util

import android.view.View
import android.view.ViewGroup
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

/**
 * @author lollipop
 * @date 2019-10-20 00:36
 * 配置信息
 */
open class ConfigInfo (protected val info: JSONObject) {

    companion object {
        private fun createJsonObject(json: String): JSONObject {
            if (json.isEmpty()) {
                return JSONObject()
            }
            return try {
                JSONObject(json)
            } catch (e: Exception) {
                JSONObject()
            }
        }

        const val KEY_CLASS = "class"
        const val KEY_WIDTH = "width"
        const val KEY_HEIGHT = "height"


    }

    constructor(json: String): this(createJsonObject(json))

    fun optConfig(name: String): ConfigInfo {
        return ConfigInfo(info.optJSONObject(name)?:JSONObject())
    }

    fun optConfigArray(name: String): Array<ConfigInfo> {
        val objArray = info.optJSONArray(name)?: JSONArray()
        return Array(objArray.length()) {index ->
            ConfigInfo(objArray.optJSONObject(index)?:JSONObject())
        }
    }

    val clazz: String
        get() {
            return opt(KEY_CLASS, "")
        }

    val width: Int
        get() {
            return opt(KEY_WIDTH, 0)
        }

    val height: Int
        get() {
            return opt(KEY_HEIGHT, 0)
        }

    protected inline fun <reified T> opt(name: String, def: T): T {
        return when (def) {
            is String -> {
                info.optString(name, def) as T
            }
            is Int -> {
                info.optInt(name, def) as T
            }
            is Double -> {
                info.optDouble(name, def) as T
            }
            is Float -> {
                info.optDouble(name, def.toDouble()).toFloat() as T
            }
            is Boolean -> {
                info.optBoolean(name, def) as T
            }
            is Long -> {
                info.optLong(name, def) as T
            }
            else -> def
        }
    }

    enum class LayoutSize(val tag: String, val value: Int) {
        Wrap("wrap", ViewGroup.LayoutParams.WRAP_CONTENT),
        Match("match", ViewGroup.LayoutParams.MATCH_PARENT)
    }

}
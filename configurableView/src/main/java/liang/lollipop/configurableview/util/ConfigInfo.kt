package liang.lollipop.configurableview.util

import org.json.JSONArray
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-10-20 00:36
 * 配置信息
 */
open class ConfigInfo (protected val parent: ConfigInfo? = null) {

    protected val info: JSONObject = parent?.info?: JSONObject()

    companion object {
        fun createJsonObject(json: String): JSONObject {
            if (json.isEmpty()) {
                return JSONObject()
            }
            return try {
                JSONObject(json)
            } catch (e: Exception) {
                JSONObject()
            }
        }

        const val KEY_CLASS =  "class"
        const val KEY_WIDTH =  "width"
        const val KEY_HEIGHT = "height"
        const val KEY_PADDING_LEFT =   "paddingLeft"
        const val KEY_PADDING_TOP =    "paddingTop"
        const val KEY_PADDING_RIGHT =  "paddingRight"
        const val KEY_PADDING_BOTTOM = "paddingBottom"

        const val KEY_MARGIN_LEFT =   "marginLeft"
        const val KEY_MARGIN_TOP =    "marginTop"
        const val KEY_MARGIN_RIGHT =  "marginRight"
        const val KEY_MARGIN_BOTTOM = "marginBottom"

        const val KEY_CHILDREN = "children"
    }

    val children = ArrayList<ConfigInfo>()

    fun parse(info: ConfigInfo) {
        parse(info.info)
    }

    fun parse(obj: JSONObject) {
        val keys = obj.keys()
        for (key in keys) {
            info.put(key, obj.opt(key))
        }

        children.clear()
        children.addAll(optConfigArray(KEY_CHILDREN))
    }

    fun serializat(): JSONObject {
        val jsonArray = JSONArray()
        children.forEach {
            jsonArray.put(it)
        }
        put(KEY_CHILDREN, jsonArray)

        return info
    }

    fun optConfig(name: String): ConfigInfo {
        return ConfigInfo().apply {
            info.optJSONObject(name)?.let {
                parse(it)
            }
        }
    }

    fun optConfigArray(name: String): Array<ConfigInfo> {
        val objArray = info.optJSONArray(name)?: JSONArray()
        return Array(objArray.length()) {index ->
            ConfigInfo().apply {
                objArray.optJSONObject(index)?.let {
                    parse(it)
                }
            }
        }
    }

    var clazz: String
        get() {
            return opt(KEY_CLASS, "")
        }
        set(value) {
            put(KEY_CLASS, value)
        }

    var width: Int
        get() {
            return opt(KEY_WIDTH, 0)
        }
        set(value) {
            put(KEY_WIDTH, value)
        }

    var height: Int
        get() {
            return opt(KEY_HEIGHT, 0)
        }
        set(value) {
            put(KEY_HEIGHT, value)
        }

    var paddingLeft: Int
        get() {
            return opt(KEY_PADDING_LEFT, 0)
        }
        set(value) {
            put(KEY_PADDING_LEFT, value)
        }

    var paddingRight: Int
        get() {
            return opt(KEY_PADDING_RIGHT, 0)
        }
        set(value) {
            put(KEY_PADDING_RIGHT, value)
        }

    var paddingTop: Int
        get() {
            return opt(KEY_PADDING_TOP, 0)
        }
        set(value) {
            put(KEY_PADDING_TOP, value)
        }

    var paddingBottom: Int
        get() {
            return opt(KEY_PADDING_BOTTOM, 0)
        }
        set(value) {
            put(KEY_PADDING_BOTTOM, value)
        }

    var marginLeft: Int
        get() {
            return opt(KEY_MARGIN_LEFT, 0)
        }
        set(value) {
            put(KEY_MARGIN_LEFT, value)
        }

    var marginRight: Int
        get() {
            return opt(KEY_MARGIN_RIGHT, 0)
        }
        set(value) {
            put(KEY_MARGIN_RIGHT, value)
        }

    var marginTop: Int
        get() {
            return opt(KEY_MARGIN_TOP, 0)
        }
        set(value) {
            put(KEY_MARGIN_TOP, value)
        }

    var marginBottom: Int
        get() {
            return opt(KEY_MARGIN_BOTTOM, 0)
        }
        set(value) {
            put(KEY_MARGIN_BOTTOM, value)
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

    protected fun put(name: String, value: Any) {
        info.put(name, value)
    }

}
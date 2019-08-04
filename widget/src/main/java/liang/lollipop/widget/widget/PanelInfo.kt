package liang.lollipop.widget.widget

import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-07-30 21:40
 * 面板信息
 * 这是面板的基础信息，
 */
open class PanelInfo {

    companion object {
        const val DEF_SPAN_X = 1
        const val DEF_SPAN_Y = 1
        const val DEF_X = -1
        const val DEF_Y = -1
        const val NO_ID = -1

        const val KEY_SPAN_X = "KEY_SPAN_X"
        const val KEY_SPAN_Y = "KEY_SPAN_Y"
        const val KEY_X = "KEY_X"
        const val KEY_Y = "KEY_Y"
        const val KEY_ID = "KEY_ID"

    }

    var spanX = DEF_SPAN_X
        protected set

    var spanY = DEF_SPAN_Y
        protected set

    var x = DEF_X
        protected set

    var y = DEF_Y
        protected set

    var id = NO_ID
        private set

    fun putId(value: Int) {
        id = value
    }

    fun offset(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    fun offsetBy(x: Int, y: Int) {
        this.x += x
        this.y += y
    }

    fun sizeChange(sx: Int, sy: Int) {
        this.spanX = sx
        this.spanY = sy
    }

    open fun parse(jsonObj: JSONObject) {
        spanX = jsonObj.optInt(KEY_SPAN_X, DEF_SPAN_X)
        spanY = jsonObj.optInt(KEY_SPAN_Y, DEF_SPAN_Y)
        x = jsonObj.optInt(KEY_X, DEF_X)
        y = jsonObj.optInt(KEY_Y, DEF_Y)
        id = jsonObj.optInt(KEY_ID, NO_ID)
    }

    open fun serialize(jsonObj: JSONObject) {
        jsonObj.put(KEY_SPAN_X, spanX)
        jsonObj.put(KEY_SPAN_Y, spanY)
        jsonObj.put(KEY_X, x)
        jsonObj.put(KEY_Y, y)
        jsonObj.put(KEY_ID, id)
    }

    override fun toString(): String {
        val obj = JSONObject()
        serialize(obj)
        return obj.toString()
    }

}
package liang.lollipop.electronicclock.widget

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
        const val EMPTY_WIDTH = 0
        const val EMPTY_HEIGHT = 0
        const val DEF_X = 0
        const val DEF_Y = 0
        const val NO_ID = -1

        const val KEY_SPAN_X = "KEY_SPAN_X"
        const val KEY_SPAN_Y = "KEY_SPAN_Y"
        const val KEY_WIDTH = "KEY_WIDTH"
        const val KEY_HEIGHT = "KEY_HEIGHT"
        const val KEY_X = "KEY_X"
        const val KEY_Y = "KEY_Y"
        const val KEY_ID = "KEY_ID"

    }

    var spanX = DEF_SPAN_X
        protected set

    var spanY = DEF_SPAN_Y
        protected set

    var width = EMPTY_WIDTH
        protected set

    var height = EMPTY_HEIGHT
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

    fun sizeChange(w: Int, h: Int) {
        width = w
        height = h
    }

    open fun parse(jsonObj: JSONObject) {
        spanX = jsonObj.optInt(KEY_SPAN_X, DEF_SPAN_X)
        spanY = jsonObj.optInt(KEY_SPAN_Y, DEF_SPAN_Y)
        width = jsonObj.optInt(KEY_WIDTH, EMPTY_WIDTH)
        height = jsonObj.optInt(KEY_HEIGHT, EMPTY_HEIGHT)
        x = jsonObj.optInt(KEY_X, DEF_X)
        y = jsonObj.optInt(KEY_Y, DEF_Y)
        id = jsonObj.optInt(KEY_ID, NO_ID)
    }

    open fun serialize(jsonObj: JSONObject) {
        jsonObj.put(KEY_SPAN_X, spanX)
        jsonObj.put(KEY_SPAN_Y, spanY)
        jsonObj.put(KEY_WIDTH, width)
        jsonObj.put(KEY_HEIGHT, height)
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
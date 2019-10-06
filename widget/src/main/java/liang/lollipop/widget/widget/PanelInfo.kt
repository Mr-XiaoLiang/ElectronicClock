package liang.lollipop.widget.widget

import android.content.Intent
import android.graphics.Color
import android.view.View
import org.json.JSONArray
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
        const val KEY_COLOR = "KEY_COLOR"

        const val PADDING = "PADDING"

        fun updatePadding(view: View, padding: FloatArray) {
            val width = view.width
            val height = view.height
            val left = width * padding[0]
            val top = height * padding[1]
            val right = width * padding[2]
            val bottom = height * padding[3]
            view.setPadding(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
        }
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

    var color = Color.BLACK

    /**
     * 四个方向的内缩进
     * 缩进的尺寸是相应维度的比例值
     */
    val padding = FloatArray(4)

    /**
     * 用于做初始化的intent
     * 类似于AppWidget的初始化Activity一样
     */
    var initIntent: Intent? = null

    /**
     * 用于根据数据值初始化数据的方法
     */
    open fun initData(data: Intent) {}

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
        color = jsonObj.optInt(KEY_COLOR, Color.BLACK)

        val paddingJsonArray = jsonObj.optJSONArray(PADDING)
        if (paddingJsonArray != null && paddingJsonArray.length() == 4) {
            for (i in padding.indices) {
                padding[i] = paddingJsonArray.optDouble(i, 0.0).toFloat()
            }
        } else {
            for (i in padding.indices) {
                padding[i] = 0F
            }
        }
    }

    open fun serialize(jsonObj: JSONObject) {
        jsonObj.put(KEY_SPAN_X, spanX)
        jsonObj.put(KEY_SPAN_Y, spanY)
        jsonObj.put(KEY_X, x)
        jsonObj.put(KEY_Y, y)
        jsonObj.put(KEY_COLOR, color)

        val paddingJsonArray = JSONArray()
        for (p in padding) {
            paddingJsonArray.put(p)
        }
        jsonObj.put(PADDING, paddingJsonArray)
    }

    fun copy(info: PanelInfo) {
        val value = JSONObject()
        info.serialize(value)
        parse(value)
    }

    override fun toString(): String {
        val obj = JSONObject()
        serialize(obj)
        return obj.toString()
    }

}
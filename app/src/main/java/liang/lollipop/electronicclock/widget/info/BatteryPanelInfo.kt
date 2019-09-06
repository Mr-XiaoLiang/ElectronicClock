package liang.lollipop.electronicclock.widget.info

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import liang.lollipop.electronicclock.activity.PanelInfoAdjustmentActivity
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONArray
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-08-19 22:14
 * 电池的描述信息
 */
class BatteryPanelInfo: PanelInfo() {

    companion object {
        const val IS_SHOW_BG = "IS_SHOW_BG"
        const val IS_SHOW_BORDER = "IS_SHOW_BORDER"
        const val CORNER = "CORNER"
        const val COLOR_ARRAY = "COLOR_ARRAY"
        const val PADDING = "PADDING"
        const val IS_VERTICAL = "IS_VERTICAL"
        const val BORDER_WIDTH = "BORDER_WIDTH"
        const val BORDER_COLOR = "BORDER_COLOR"
        const val IS_ANIMATION = "IS_ANIMATION"
        const val IS_ARC = "IS_ARC"
        const val ARC_WIDTH = "ARC_WIDTH"
    }

    init {
        initIntent = PanelInfoAdjustmentActivity.getIntent(this)
    }

    /**
     * 是否显示边界
     */
    var isShowBg = false

    /**
     * 是否展示边界
     */
    var isShowBorder = true

    /**
     * 圆角尺寸
     */
    var corner = 0.1F

    /**
     * 颜色的集合
     */
    val colorArray = ArrayList<Int>()

    /**
     * 四个方向的内缩进
     * 缩进的尺寸是相应维度的比例值
     */
    val padding = FloatArray(4)

    /**
     * 是否是垂直放置
     */
    var isVertical = false

    /**
     * 边框的宽度
     * 这是一个权重，比例值，
     * 它的值是相对窄边的长度而言
     */
    var borderWidth = 0.05F

    /**
     * 边框的颜色
     */
    var borderColor = Color.BLACK

    /**
     * 是否开启动画
     */
    var isAnimation = true

    /**
     * 是否是环形的
     */
    var isArc = true

    /**
     * 扇形的宽度，相对于半径而言
     */
    var arcWidth = 0.3F

    override fun parse(jsonObj: JSONObject) {
        super.parse(jsonObj)

        isShowBg = jsonObj.optBoolean(IS_SHOW_BG, false)
        isShowBorder = jsonObj.optBoolean(IS_SHOW_BORDER, true)
        corner = jsonObj.optDouble(CORNER, 0.1).toFloat()
        val colorJsonArray = jsonObj.optJSONArray(COLOR_ARRAY)
        colorArray.clear()
        if (colorJsonArray != null && colorJsonArray.length() > 0) {
            for (i in 0 until colorArray.size) {
                colorArray.add(colorJsonArray.optInt(i, Color.BLACK))
            }
        }

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

        isVertical = jsonObj.optBoolean(IS_VERTICAL, false)
        borderWidth = jsonObj.optDouble(BORDER_WIDTH, 0.05).toFloat()
        borderColor = jsonObj.optInt(BORDER_COLOR, Color.BLACK)
        isAnimation = jsonObj.optBoolean(IS_ANIMATION, true)
        isArc = jsonObj.optBoolean(IS_ARC, false)
        arcWidth = jsonObj.optDouble(ARC_WIDTH, 0.3).toFloat()

        // 如果初始化的intent没有被置空， 并且ID是有效ID，
        // 那么就重新生成一个带有新数据的intent
        if (initIntent != null && id != NO_ID) {
            initIntent = PanelInfoAdjustmentActivity.getIntent(this)
        }
    }

    override fun serialize(jsonObj: JSONObject) {
        super.serialize(jsonObj)
        jsonObj.apply {
            put(IS_SHOW_BG, isShowBg)
            put(IS_SHOW_BORDER, isShowBorder)
            put(CORNER, corner)
            val colorJsonArray = JSONArray()
            for (color in colorArray) {
                colorJsonArray.put(color)
            }
            put(COLOR_ARRAY, colorJsonArray)

            val paddingJsonArray = JSONArray()
            for (p in padding) {
                paddingJsonArray.put(p)
            }
            put(PADDING, paddingJsonArray)

            put(IS_VERTICAL, isVertical)
            put(BORDER_WIDTH, borderWidth)
            put(BORDER_COLOR, borderColor)
            put(IS_ANIMATION, isAnimation)
            put(IS_ARC, isArc)
            put(ARC_WIDTH, arcWidth)
        }
    }

    override fun initData(data: Intent) {
        super.initData(data)
        val info = PanelInfoAdjustmentActivity.getInfo(data)
        if (!TextUtils.isEmpty(info)) {
            parse(JSONObject(info))
        }
    }

}
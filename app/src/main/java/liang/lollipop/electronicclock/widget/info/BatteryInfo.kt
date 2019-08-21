package liang.lollipop.electronicclock.widget.info

import android.graphics.Color
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-08-19 22:14
 * 电池的描述信息
 */
class BatteryInfo: PanelInfo() {

    companion object {
        private val EMPTY_COLOR_ARRAY = IntArray(0)
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
    var corner = 1F

    /**
     * 颜色的集合
     */
    var colorArray = EMPTY_COLOR_ARRAY

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
    var isArc = false

    /**
     * 扇形的宽度，相对于半径而言
     */
    var arcWidth = 0.3F

}
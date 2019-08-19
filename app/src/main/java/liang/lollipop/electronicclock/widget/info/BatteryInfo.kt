package liang.lollipop.electronicclock.widget.info

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
    var corner = 0F

    /**
     * 颜色的集合
     */
    var colorArray = EMPTY_COLOR_ARRAY

    /**
     * 四个方向的内缩进
     * 缩进的尺寸是相应纬度的比例值
     */
    val padding = IntArray(4)

    /**
     * 是否是垂直放置
     */
    var isVertical = false



}
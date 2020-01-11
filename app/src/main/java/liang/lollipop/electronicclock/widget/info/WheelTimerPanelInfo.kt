package liang.lollipop.electronicclock.widget.info

import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-12-08 17:55
 * 滚轮时间的面板信息
 */
class WheelTimerPanelInfo: PanelInfo() {

    companion object {
        const val MONTH = "MONTH"
        const val DAY = "DAY"
        const val WEEK = "WEEK"
        const val HOUR = "HOUR"
        const val MINUTE = "MINUTE"
        const val SECOND = "SECOND"
        const val ANIMATION = "ANIMATION"
    }

    val monthValues = ArrayList<Int>()
    val dayValues = ArrayList<Int>()
    val weekValues = ArrayList<Int>()
    val hourValues = ArrayList<Int>()
    val minuteValues = ArrayList<Int>()
    val secondValues = ArrayList<Int>()

    var animation = true

}
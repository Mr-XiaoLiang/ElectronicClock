package liang.lollipop.electronicclock.widget.info

import android.graphics.Color
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-09-23 23:03
 * 日历面板的描述信息
 */
class CalendarPanelInfo: PanelInfo() {

    /**
     * 只展示月份模式
     */
    var onlyMonth = false

    /**
     * 今天的日期前景色
     */
    var todayColor = Color.WHITE

    /**
     * 今天的日期背景色
     */
    var todayBgColor = Color.BLUE

}
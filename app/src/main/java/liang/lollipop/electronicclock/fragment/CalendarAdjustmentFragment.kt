package liang.lollipop.electronicclock.fragment

import android.os.Bundle
import android.view.View
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.widget.info.CalendarPanelInfo
import liang.lollipop.electronicclock.widget.panel.CalendarPanel
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-10-05 01:05
 * 日历面板的参数调整页面
 */
class CalendarAdjustmentFragment: PanelInfoAdjustmentFragment() {

    companion object {
        fun getInstance(id: Int): BatteryAdjustmentFragment {
            return BatteryAdjustmentFragment().apply {
                arguments = Bundle().apply {
                    if (id != PanelInfo.NO_ID) {
                        putString(ARG_INFO_ID, "$id")
                    }
                }
            }
        }
    }

    private val calendarPanelInfo = CalendarPanelInfo()
    private val calendarPanel = CalendarPanel(calendarPanelInfo)

    override fun getPanelView(): View {
        return calendarPanel.createView(context!!)
    }

    override fun getPanelInfo(): PanelInfo {
        return calendarPanelInfo
    }

    override fun initInfoByValue(info: String) {
        calendarPanelInfo.parse(JSONObject(info))
    }

    override fun onInfoFoundById(info: PanelInfo?) {
        info?.let { calendarPanelInfo.copy(it) }
        putAdjustmentInfo()
        panelInitComplete()
    }

    private fun putAdjustmentInfo() {
        /*
        const val IS_SHOW_FESTIVAL            = "IS_SHOW_FESTIVAL"
        const val IS_SHOW_SOLAR_TERMS         = "IS_SHOW_SOLAR_TERMS"
        const val IS_SHOW_AUSPICIOUS          = "IS_SHOW_AUSPICIOUS"
        const val IS_OVAL_BG                  = "IS_OVAL_BG"
        const val TODAY_TEXT_COLOR            = "TODAY_TEXT_COLOR"
        const val TODAY_BG_COLOR              = "TODAY_BG_COLOR"
        const val OTHER_TEXT_COLOR            = "OTHER_TEXT_COLOR"
        const val OTHER_BG_COLOR              = "OTHER_BG_COLOR"
        const val IS_STARTING_ON_SUNDAY       = "IS_STARTING_ON_SUNDAY "
        const val IS_SHOW_SCHEDULE            = "IS_SHOW_SCHEDULE"
        const val SOLAR_FESTIVAL_POINT_COLOR  = "SOLAR_FESTIVAL_POINT_COLOR"
        const val LUNAR_FESTIVAL_POINT_COLOR  = "LUNAR_FESTIVAL_POINT_COLOR"
        const val AUSPICIOUS_POINT_COLOR      = "AUSPICIOUS_POINT_COLOR"
         */
        addAdjustmentInfo(
            switch {
                key = CalendarPanelInfo.IS_SHOW_WEEK
                title = "显示星期"
                summary = "周模式、月模式下生效"
                value = calendarPanelInfo.calendarOptions.isShowWeek
            },
            switch {
                key = CalendarPanelInfo.IS_SHOW_LUNAR
                title = "显示农历"
                summary = "显示农历的日期"
                value = calendarPanelInfo.calendarOptions.isShowLunar
            },
            switch {
                key = CalendarPanelInfo.IS_SHOW_FESTIVAL
                title = "显示节日"
                summary = "将会显示一些基本的节日"
                value = calendarPanelInfo.calendarOptions.isShowFestival
            }
        )
    }

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {

    }

}
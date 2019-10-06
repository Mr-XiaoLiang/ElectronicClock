package liang.lollipop.electronicclock.fragment

import android.os.Bundle
import android.view.View
import liang.lollipop.electronicclock.R
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
        addAdjustmentInfo(
            switch {
                key = CalendarPanelInfo.IS_SHOW_WEEK
                title = getString(R.string.title_show_week)
                summary = getString(R.string.summary_show_week)
                value = calendarPanelInfo.calendarOptions.isShowWeek
            },
            switch {
                key = CalendarPanelInfo.IS_SHOW_LUNAR
                title = getString(R.string.title_show_lunar)
                summary = getString(R.string.summary_show_lunar)
                value = calendarPanelInfo.calendarOptions.isShowLunar
            },
            switch {
                key = CalendarPanelInfo.IS_SHOW_FESTIVAL
                title = getString(R.string.title_show_festival)
                summary = getString(R.string.summary_show_festival)
                value = calendarPanelInfo.calendarOptions.isShowFestival
            },
            switch {
                key = CalendarPanelInfo.IS_SHOW_SOLAR_TERMS
                title = getString(R.string.title_show_solar_terms)
                summary = getString(R.string.summary_show_solar_terms)
                value = calendarPanelInfo.calendarOptions.isShowSolarTerms
            },
            switch {
                key = CalendarPanelInfo.IS_SHOW_AUSPICIOUS
                title = getString(R.string.title_show_auspicious)
                summary = getString(R.string.summary_show_auspicious)
                value = calendarPanelInfo.calendarOptions.isShowAuspicious
            },
            switch {
                key = CalendarPanelInfo.IS_STARTING_ON_SUNDAY
                title = getString(R.string.title_starting_on_sunday)
                summary = getString(R.string.summary_starting_on_sunday)
                value = calendarPanelInfo.calendarOptions.isStartingOnSunday
            },
            switch {
                key = CalendarPanelInfo.IS_OVAL_BG
                title = getString(R.string.title_oval_bg)
                summary = getString(R.string.summary_oval_bg)
                value = calendarPanelInfo.calendarOptions.isOvalBg
            },
            switch {
                key = CalendarPanelInfo.IS_AUTO_TEXT_COLOR
                title = getString(R.string.title_auto_text_color)
                summary = getString(R.string.summary_auto_text_color)
                value = calendarPanelInfo.isAutoTextColor
            },
            colors {
                key = CalendarPanelInfo.TODAY_TEXT_COLOR
                relevantKey = CalendarPanelInfo.IS_AUTO_TEXT_COLOR
                title = getString(R.string.title_today_text_color)
                summary = getString(R.string.summary_today_text_color)
                reset(calendarPanelInfo.calendarOptions.todayTextColor)
            },
            colors {
                key = CalendarPanelInfo.TODAY_BG_COLOR
                relevantKey = CalendarPanelInfo.IS_AUTO_TEXT_COLOR
                title = getString(R.string.title_today_bg_color)
                summary = getString(R.string.summary_today_bg_color)
                reset(calendarPanelInfo.calendarOptions.todayBgColor)
            },
            colors {
                key = CalendarPanelInfo.OTHER_TEXT_COLOR
                relevantKey = CalendarPanelInfo.IS_AUTO_TEXT_COLOR
                title = getString(R.string.title_other_text_color)
                summary = getString(R.string.summary_other_text_color)
                reset(calendarPanelInfo.calendarOptions.otherTextColor)
            },
            colors {
                key = CalendarPanelInfo.OTHER_BG_COLOR
                relevantKey = CalendarPanelInfo.IS_AUTO_TEXT_COLOR
                title = getString(R.string.title_other_bg_color)
                summary = getString(R.string.summary_other_bg_color)
                reset(calendarPanelInfo.calendarOptions.otherBgColor)
            },
            colors {
                key = CalendarPanelInfo.SOLAR_FESTIVAL_POINT_COLOR
                title = getString(R.string.title_solar_festival_point_color)
                summary = getString(R.string.summary_solar_festival_point_color)
                reset(calendarPanelInfo.calendarOptions.solarFestivalPointColor)
            },
            colors {
                key = CalendarPanelInfo.LUNAR_FESTIVAL_POINT_COLOR
                title = getString(R.string.title_lunar_festival_point_color)
                summary = getString(R.string.summary_lunar_festival_point_color)
                reset(calendarPanelInfo.calendarOptions.lunarFestivalPointColor)
            },
            colors {
                key = CalendarPanelInfo.AUSPICIOUS_POINT_COLOR
                title = getString(R.string.title_auspicious_point_color)
                summary = getString(R.string.summary_auspicious_point_color)
                reset(calendarPanelInfo.calendarOptions.auspiciousPointColor)
            },
            paddings {
                key = PanelInfo.PADDING
                title = getString(R.string.title_paddings)
                reset(calendarPanelInfo.padding)
            }
        )
    }

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {

    }

}
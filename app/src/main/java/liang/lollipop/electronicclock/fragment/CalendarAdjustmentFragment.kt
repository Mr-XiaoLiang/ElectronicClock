package liang.lollipop.electronicclock.fragment

import android.view.View
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.AdjustmentInfo
import liang.lollipop.electronicclock.view.CalendarView
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

    private val calendarPanelInfo = CalendarPanelInfo()
    private val calendarPanel = CalendarPanel(calendarPanelInfo)
    private var backgroundColor = 0

    companion object {
        private val typeValues = intArrayOf(
            R.string.calendar_type_month,
            R.string.calendar_type_week,
            R.string.calendar_type_day)
    }

    override fun getPanelView(): View {
        return calendarPanel.getView(context!!)
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
        calendarPanel.onInfoChange()
    }

    override fun onBackgroundColorChange(color: Int) {
        super.onBackgroundColorChange(color)
        backgroundColor = color
        calendarPanel.onColorChange(color, 1F)
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
                relevantEnable = false
                title = getString(R.string.title_today_text_color)
                summary = getString(R.string.summary_today_text_color)
                reset(calendarPanelInfo.calendarOptions.todayTextColor)
            },
            colors {
                key = CalendarPanelInfo.TODAY_BG_COLOR
                relevantKey = CalendarPanelInfo.IS_AUTO_TEXT_COLOR
                relevantEnable = false
                title = getString(R.string.title_today_bg_color)
                summary = getString(R.string.summary_today_bg_color)
                reset(calendarPanelInfo.calendarOptions.todayBgColor)
            },
            colors {
                key = CalendarPanelInfo.OTHER_TEXT_COLOR
                relevantKey = CalendarPanelInfo.IS_AUTO_TEXT_COLOR
                relevantEnable = false
                title = getString(R.string.title_other_text_color)
                summary = getString(R.string.summary_other_text_color)
                reset(calendarPanelInfo.calendarOptions.otherTextColor)
            },
            colors {
                key = CalendarPanelInfo.OTHER_BG_COLOR
                relevantKey = CalendarPanelInfo.IS_AUTO_TEXT_COLOR
                relevantEnable = false
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
            },
            select {
                key = CalendarPanelInfo.CALENDAR_TYPE
                title = getString(R.string.title_calendar_type)
                for (strId in typeValues) {
                    addItem(getString(strId))
                }
                selectBy(calendarPanelInfo.calendarType.typeToString())
            }
        )
    }

    private fun CalendarView.Type.typeToString(): String {
        return when (this) {
            CalendarView.Type.Month -> getString(R.string.calendar_type_month)
            CalendarView.Type.Week -> getString(R.string.calendar_type_week)
            CalendarView.Type.Day -> getString(R.string.calendar_type_day)
        }
    }

    private fun Int.indexToType() : CalendarView.Type {
        if (this < 0 || this >= typeValues.size) {
            return CalendarView.Type.Month
        }
        return when (typeValues[this]) {
            R.string.calendar_type_month -> CalendarView.Type.Month
            R.string.calendar_type_week -> CalendarView.Type.Week
            R.string.calendar_type_day -> CalendarView.Type.Day
            else -> CalendarView.Type.Month
        }
    }

    override fun onInfoChange(info: AdjustmentInfo, newValue: Any) {
        val option = calendarPanelInfo.calendarOptions
        when (info.key) {
            PanelInfo.PADDING                            -> {
                if (newValue is FloatArray) {
                    calendarPanelInfo.padding[0] = newValue[0]
                    calendarPanelInfo.padding[1] = newValue[1]
                    calendarPanelInfo.padding[2] = newValue[2]
                    calendarPanelInfo.padding[3] = newValue[3]
                }
            }
            CalendarPanelInfo.IS_SHOW_WEEK               -> {
                option.isShowWeek = newValue.optBoolean(option.isShowWeek)
            }
            CalendarPanelInfo.IS_SHOW_LUNAR              -> {
                option.isShowLunar = newValue.optBoolean(option.isShowLunar)
            }
            CalendarPanelInfo.IS_SHOW_FESTIVAL           -> {
                option.isShowFestival = newValue.optBoolean(option.isShowFestival)
            }
            CalendarPanelInfo.IS_SHOW_SOLAR_TERMS        -> {
                option.isShowSolarTerms = newValue.optBoolean(option.isShowSolarTerms)
            }
            CalendarPanelInfo.IS_SHOW_AUSPICIOUS         -> {
                option.isShowAuspicious = newValue.optBoolean(option.isShowAuspicious)
            }
            CalendarPanelInfo.IS_OVAL_BG                 -> {
                option.isOvalBg = newValue.optBoolean(option.isOvalBg)
            }
            CalendarPanelInfo.IS_AUTO_TEXT_COLOR         -> {
                calendarPanelInfo.isAutoTextColor = newValue.optBoolean(calendarPanelInfo.isAutoTextColor)
                if (calendarPanelInfo.isAutoTextColor) {
                    onBackgroundColorChange(backgroundColor)
                    return
                }
            }
            CalendarPanelInfo.TODAY_TEXT_COLOR           -> {
                if (newValue is IntArray && newValue.size > 0) {
                    option.todayTextColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    option.todayTextColor = newValue[0] as? Int ?: option.todayTextColor
                }
            }
            CalendarPanelInfo.TODAY_BG_COLOR             -> {
                if (newValue is IntArray && newValue.size > 0) {
                    option.todayBgColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    option.todayBgColor = newValue[0] as? Int ?: option.todayBgColor
                }
            }
            CalendarPanelInfo.OTHER_TEXT_COLOR           -> {
                if (newValue is IntArray && newValue.size > 0) {
                    option.otherTextColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    option.otherTextColor = newValue[0] as? Int ?: option.otherTextColor
                }
            }
            CalendarPanelInfo.OTHER_BG_COLOR             -> {
                if (newValue is IntArray && newValue.size > 0) {
                    option.otherBgColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    option.otherBgColor = newValue[0] as? Int ?: option.otherBgColor
                }
            }
            CalendarPanelInfo.IS_STARTING_ON_SUNDAY      -> {
                option.isStartingOnSunday = newValue.optBoolean(option.isStartingOnSunday)
            }
            CalendarPanelInfo.IS_SHOW_SCHEDULE           -> {
                option.isShowSchedule = newValue.optBoolean(option.isShowSchedule)
            }
            CalendarPanelInfo.SOLAR_FESTIVAL_POINT_COLOR -> {
                if (newValue is IntArray && newValue.size > 0) {
                    option.solarFestivalPointColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    option.solarFestivalPointColor = newValue[0] as? Int ?: option.solarFestivalPointColor
                }
            }
            CalendarPanelInfo.LUNAR_FESTIVAL_POINT_COLOR -> {
                if (newValue is IntArray && newValue.size > 0) {
                    option.lunarFestivalPointColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    option.lunarFestivalPointColor = newValue[0] as? Int ?: option.lunarFestivalPointColor
                }
            }
            CalendarPanelInfo.AUSPICIOUS_POINT_COLOR     -> {
                if (newValue is IntArray && newValue.size > 0) {
                    option.auspiciousPointColor = newValue[0]
                } else if (newValue is ArrayList<*> && newValue.size > 0) {
                    option.auspiciousPointColor = newValue[0] as? Int ?: option.auspiciousPointColor
                }
            }
            CalendarPanelInfo.CALENDAR_TYPE             -> {
                val index = newValue.optInt(0)
                val newType = index.indexToType()
                if (newType == calendarPanelInfo.calendarType) {
                    return
                }
                calendarPanelInfo.calendarType = newType
            }
        }
        calendarPanel.onInfoChange()
    }

}
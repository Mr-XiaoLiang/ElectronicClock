package liang.lollipop.electronicclock.widget.info

import android.graphics.Color
import liang.lollipop.electronicclock.view.CalendarView
import liang.lollipop.widget.widget.PanelInfo
import org.json.JSONObject

/**
 * @author lollipop
 * @date 2019-09-23 23:03
 * 日历面板的描述信息
 */
class CalendarPanelInfo: PanelInfo() {

    companion object {
        const val IS_SHOW_WEEK                = "IS_SHOW_WEEK"
        const val IS_SHOW_LUNAR               = "IS_SHOW_LUNAR"
        const val IS_SHOW_FESTIVAL            = "IS_SHOW_FESTIVAL"
        const val IS_SHOW_SOLAR_TERMS         = "IS_SHOW_SOLAR_TERMS"
        const val IS_SHOW_AUSPICIOUS          = "IS_SHOW_AUSPICIOUS"
        const val IS_OVAL_BG                  = "IS_OVAL_BG"
        const val IS_AUTO_TEXT_COLOR          = "IS_AUTO_TEXT_COLOR"
        const val TODAY_TEXT_COLOR            = "TODAY_TEXT_COLOR"
        const val TODAY_BG_COLOR              = "TODAY_BG_COLOR"
        const val OTHER_TEXT_COLOR            = "OTHER_TEXT_COLOR"
        const val OTHER_BG_COLOR              = "OTHER_BG_COLOR"
        const val IS_STARTING_ON_SUNDAY       = "IS_STARTING_ON_SUNDAY "
        const val IS_SHOW_SCHEDULE            = "IS_SHOW_SCHEDULE"
        const val SOLAR_FESTIVAL_POINT_COLOR  = "SOLAR_FESTIVAL_POINT_COLOR"
        const val LUNAR_FESTIVAL_POINT_COLOR  = "LUNAR_FESTIVAL_POINT_COLOR"
        const val AUSPICIOUS_POINT_COLOR      = "AUSPICIOUS_POINT_COLOR"

    }

    val calendarOptions = CalendarView.Options()

    var isAutoTextColor = true

//    /**
//     * 四个方向的内缩进
//     * 缩进的尺寸是相应维度的比例值
//     */
//    val padding = FloatArray(4)

    override fun parse(jsonObj: JSONObject) {
        super.parse(jsonObj)
        calendarOptions.isShowWeek               = jsonObj.optBoolean(IS_SHOW_WEEK, true)
        calendarOptions.isShowLunar              = jsonObj.optBoolean(IS_SHOW_LUNAR, true)
        calendarOptions.isShowFestival           = jsonObj.optBoolean(IS_SHOW_FESTIVAL, true)
        calendarOptions.isShowSolarTerms         = jsonObj.optBoolean(IS_SHOW_SOLAR_TERMS, true)
        calendarOptions.isShowAuspicious         = jsonObj.optBoolean(IS_SHOW_AUSPICIOUS, true)
        calendarOptions.isOvalBg                 = jsonObj.optBoolean(IS_OVAL_BG, true)
        calendarOptions.todayTextColor           = jsonObj.optInt(TODAY_TEXT_COLOR, Color.WHITE)
        calendarOptions.todayBgColor             = jsonObj.optInt(TODAY_BG_COLOR, Color.BLUE)
        calendarOptions.otherTextColor           = jsonObj.optInt(OTHER_TEXT_COLOR, Color.BLACK)
        calendarOptions.otherBgColor             = jsonObj.optInt(OTHER_BG_COLOR, Color.TRANSPARENT)
        calendarOptions.isStartingOnSunday       = jsonObj.optBoolean(IS_STARTING_ON_SUNDAY, true)
        calendarOptions.isShowSchedule           = jsonObj.optBoolean(IS_SHOW_SCHEDULE, true)
        calendarOptions.solarFestivalPointColor  = jsonObj.optInt(SOLAR_FESTIVAL_POINT_COLOR, Color.CYAN)
        calendarOptions.lunarFestivalPointColor  = jsonObj.optInt(LUNAR_FESTIVAL_POINT_COLOR, Color.MAGENTA)
        calendarOptions.auspiciousPointColor     = jsonObj.optInt(AUSPICIOUS_POINT_COLOR, Color.RED)
        isAutoTextColor                          = jsonObj.optBoolean(IS_AUTO_TEXT_COLOR, true)
    }

    override fun serialize(jsonObj: JSONObject) {
        super.serialize(jsonObj)
        jsonObj.put(IS_SHOW_WEEK               , calendarOptions.isShowWeek             )
        jsonObj.put(IS_SHOW_LUNAR              , calendarOptions.isShowLunar            )
        jsonObj.put(IS_SHOW_FESTIVAL           , calendarOptions.isShowFestival         )
        jsonObj.put(IS_SHOW_SOLAR_TERMS        , calendarOptions.isShowSolarTerms       )
        jsonObj.put(IS_SHOW_AUSPICIOUS         , calendarOptions.isShowAuspicious       )
        jsonObj.put(IS_OVAL_BG                 , calendarOptions.isOvalBg               )
        jsonObj.put(TODAY_TEXT_COLOR           , calendarOptions.todayTextColor         )
        jsonObj.put(TODAY_BG_COLOR             , calendarOptions.todayBgColor           )
        jsonObj.put(OTHER_TEXT_COLOR           , calendarOptions.otherTextColor         )
        jsonObj.put(OTHER_BG_COLOR             , calendarOptions.otherBgColor           )
        jsonObj.put(IS_STARTING_ON_SUNDAY      , calendarOptions.isStartingOnSunday     )
        jsonObj.put(IS_SHOW_SCHEDULE           , calendarOptions.isShowSchedule         )
        jsonObj.put(SOLAR_FESTIVAL_POINT_COLOR , calendarOptions.solarFestivalPointColor)
        jsonObj.put(LUNAR_FESTIVAL_POINT_COLOR , calendarOptions.lunarFestivalPointColor)
        jsonObj.put(AUSPICIOUS_POINT_COLOR     , calendarOptions.auspiciousPointColor   )
        jsonObj.put(IS_AUTO_TEXT_COLOR         , isAutoTextColor                        )
    }

}
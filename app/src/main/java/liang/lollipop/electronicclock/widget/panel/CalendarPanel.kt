package liang.lollipop.electronicclock.widget.panel

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.activity.LunarActivity
import liang.lollipop.electronicclock.activity.PanelInfoAdjustmentActivity
import liang.lollipop.electronicclock.utils.LunarCalendar
import liang.lollipop.electronicclock.view.CalendarView
import liang.lollipop.electronicclock.widget.info.CalendarPanelInfo
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelInfo
import kotlin.math.max
import kotlin.math.min

/**
 * @author lollipop
 * @date 2019-09-23 23:09
 * 日历面板的对象
 */
class CalendarPanel(info: CalendarPanelInfo): Panel<CalendarPanelInfo>(info) {

    override fun createView(context: Context): View {
        val view = CalendarView(context)
        if (!isInEditMode) {
            view.onDayViewClick { year, month, day ->
                LunarActivity.startByTime(context,
                    LunarCalendar.timeInMillis(year, month - 1, day))
            }
        }
        return view
    }

    override fun onInfoChange() {
        tryMyView<CalendarView> {
            it.changeOptions(panelInfo.calendarOptions, !panelInfo.isAutoTextColor)
            it.calendarType = panelInfo.calendarType
            val paddingArray = panelInfo.padding
            it.setWeightPadding(paddingArray[0], paddingArray[1], paddingArray[2], paddingArray[3])
        }
        onUpdate()
    }

    override fun onUpdate() {
        super.onUpdate()
        tryMyView<CalendarView> {
            LunarCalendar.getMonth(System.currentTimeMillis()) { year, month ->
                it.dateChange(year, month)
                it.notifyDataChange()
            }
        }
    }

    override fun onColorChange(color: Int, light: Float) {
        super.onColorChange(color, light)
        if (panelInfo.isAutoTextColor) {
            tryMyView<CalendarView> {
                it.options.apply {
                    val fullColor = color or 0xFF000000.toInt()
                    val alpha = Color.alpha(color)
                    val inverted = if (fullColor == Color.WHITE) {
                        Color.BLACK
                    } else  {
                        Color.WHITE
                    }.changeAlpha(alpha)
                    val lightInt = max(min((light * 255).toInt(), 255), 0)
                    todayTextColor = inverted
                    todayBgColor = color
                    otherTextColor = color
                    otherBgColor = Color.TRANSPARENT
                    auspiciousPointColor = auspiciousPointColor.changeAlpha(lightInt)
                    lunarFestivalPointColor = lunarFestivalPointColor.changeAlpha(lightInt)
                    solarFestivalPointColor = solarFestivalPointColor.changeAlpha(lightInt)
                }
                it.notifyDataChange()
            }
        }
    }
}
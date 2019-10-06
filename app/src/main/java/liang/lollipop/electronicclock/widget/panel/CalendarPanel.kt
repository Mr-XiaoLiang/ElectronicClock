package liang.lollipop.electronicclock.widget.panel

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.activity.PanelInfoAdjustmentActivity
import liang.lollipop.electronicclock.utils.LunarCalendar
import liang.lollipop.electronicclock.view.CalendarView
import liang.lollipop.electronicclock.widget.info.CalendarPanelInfo
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-09-23 23:09
 * 日历面板的对象
 */
class CalendarPanel(info: CalendarPanelInfo): Panel<CalendarPanelInfo>(info) {

    override fun onCreateView(layoutInflater: LayoutInflater, parent: ViewGroup): View {
        return createView(layoutInflater.context)
    }

    fun createView(context: Context): View {
        return CalendarView(context)
    }

    override fun onInfoChange() {
        tryMyView<CalendarView> {
            it.options = panelInfo.calendarOptions
        }
        onUpdate()
    }

    override fun onUpdate() {
        super.onUpdate()
        tryMyView<CalendarView> {
            LunarCalendar.getMonth(System.currentTimeMillis()) { year, month ->
                it.dateChange(year, month)
            }
        }
    }

    override fun onColorChange(color: Int, light: Float) {
        super.onColorChange(color, light)
        if (panelInfo.isAutoTextColor) {
            tryMyView<CalendarView> {
                it.options.apply {
                    val inverted = if (color == Color.WHITE) { Color.BLACK } else  { Color.WHITE }
                    todayTextColor = inverted
                    todayBgColor = color
                    otherTextColor = color
                    otherBgColor = Color.TRANSPARENT
                }
                it.notifyDataChange()
            }
        }
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        v?:return
        val context = v.context
        if (panelInfo.id == PanelInfo.NO_ID) {
            Snackbar.make(v, R.string.alert_save_first, Snackbar.LENGTH_LONG).show()
            return
        }
        context?.startActivity(PanelInfoAdjustmentActivity.getIntent(panelInfo))
    }

}
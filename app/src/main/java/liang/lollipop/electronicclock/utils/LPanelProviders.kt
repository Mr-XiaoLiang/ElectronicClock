package liang.lollipop.electronicclock.utils

import liang.lollipop.electronicclock.R
import liang.lollipop.electronicclock.bean.WidgetInfo
import liang.lollipop.electronicclock.widget.info.*
import liang.lollipop.electronicclock.widget.panel.*
import liang.lollipop.widget.info.ClockPanelInfo
import liang.lollipop.widget.utils.PanelProviders
import liang.lollipop.widget.widget.Panel
import liang.lollipop.widget.widget.PanelInfo

/**
 * @author lollipop
 * @date 2019-10-05 00:52
 * 面板提供器
 */
class LPanelProviders: PanelProviders {
    override fun createPanelByInfo(info: PanelInfo): Panel<*>? {
        return when (info) {
            is BatteryPanelInfo -> BatteryPanel(info)
            is CalendarPanelInfo -> CalendarPanel(info)
            is PhotoFramePanelInfo -> PhotoFramePanel(info)
            is LauncherPanelInfo -> LauncherPanel(info)
            is WheelTimerPanelInfo -> WheelTimerPanel(info)
            else -> null
        }
    }

    override fun createInfoByName(name: String): PanelInfo? {
        return when (name) {
            BatteryPanelInfo::class.java.name -> BatteryPanelInfo()
            CalendarPanelInfo::class.java.name -> CalendarPanelInfo()
            PhotoFramePanelInfo::class.java.name -> PhotoFramePanelInfo()
            LauncherPanelInfo::class.java.name -> LauncherPanelInfo()
            WheelTimerPanelInfo::class.java.name -> WheelTimerPanelInfo()
            else -> null
        }
    }

    companion object {

        fun getWidgetInfoList(): Array<WidgetInfo> {
            return arrayOf(
                WidgetInfo(
                    R.drawable.ic_access_time_black_24dp,
                    R.string.widget_clock,
                    ClockPanelInfo::class.java.name
                ),
                WidgetInfo(
                    R.drawable.ic_battery_60_white_24dp,
                    R.string.widget_battery,
                    BatteryPanelInfo::class.java.name
                ),
                WidgetInfo(
                    R.drawable.ic_event_white_24dp,
                    R.string.calendar,
                    CalendarPanelInfo::class.java.name
                ),
                WidgetInfo(
                    R.drawable.ic_wallpaper_white_24dp,
                    R.string.photo,
                    PhotoFramePanelInfo::class.java.name
                ),
                WidgetInfo(
                    R.drawable.ic_apps_white_24dp,
                    R.string.launcher,
                    LauncherPanelInfo::class.java.name
                ),
                WidgetInfo(
                    R.drawable.ic_filter_tilt_shift_white_24dp,
                    R.string.wheel_time,
                    WheelTimerPanelInfo::class.java.name
                )
            )
        }

    }

}